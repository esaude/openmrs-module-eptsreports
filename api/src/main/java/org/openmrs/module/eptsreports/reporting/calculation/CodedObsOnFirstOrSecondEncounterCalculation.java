package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodedObsOnFirstOrSecondEncounterCalculation extends AbstractPatientCalculation {

  private EPTSCalculationService eptsCalculationService;

  private HivMetadata hivMetadata;

  @Autowired
  public CodedObsOnFirstOrSecondEncounterCalculation(
      EPTSCalculationService eptsCalculationService, HivMetadata hivMetadata) {
    this.eptsCalculationService = eptsCalculationService;
    this.hivMetadata = hivMetadata;
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    Location location = (Location) context.getFromCache("location");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Concept concept = (Concept) parameterValues.get("concept");
    Concept valueCoded = (Concept) parameterValues.get("valueCoded");

    CalculationResultMap adultSegEncounters =
        eptsCalculationService.allEncounters(
            encounterTypes, cohort, location, onOrAfter, onOrBefore, context);
    CalculationResultMap getObs =
        eptsCalculationService.getObs(
            concept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            Arrays.asList(valueCoded),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap map = new CalculationResultMap();
    for (Integer pId : cohort) {
      List<Encounter> encounters = getFirstTwoEncounters(adultSegEncounters, pId);
      List<Obs> obsFoundList =
          EptsCalculationUtils.extractResultValues((ListResult) getObs.get(pId));
      boolean pass = false;
      for (Encounter e : encounters) {
        for (Obs o : obsFoundList) {
          if (e.getEncounterId().equals(o.getEncounter().getEncounterId())) {
            pass = true;
            break;
          }
        }
      }
      map.put(pId, new BooleanResult(pass, this));
    }

    return map;
  }

  private List<Encounter> getFirstTwoEncounters(
      CalculationResultMap adultSegEncounters, Integer pId) {
    ListResult listResult = (ListResult) adultSegEncounters.get(pId);

    List<Encounter> encounterResults = EptsCalculationUtils.extractResultValues(listResult);

    sortByDatetime(encounterResults);
    return encounterResults.size() > 1 ? encounterResults.subList(0, 2) : encounterResults;
  }

  private void sortByDatetime(List<Encounter> encounters) {
    Collections.sort(
        encounters,
        new Comparator<Encounter>() {
          @Override
          public int compare(Encounter a, Encounter b) {
            return a.getEncounterDatetime().compareTo(b.getEncounterDatetime());
          }
        });
  }
}
