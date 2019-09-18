package org.openmrs.module.eptsreports.reporting.calculation;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils.resultForPatient;

import java.util.ArrayList;
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
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsoniazidProphylaxisOnFirstOrSecondEncounterCalculation
    extends AbstractPatientCalculation {

  private EPTSCalculationService eptsCalculationService;

  private HivMetadata hivMetadata;

  @Autowired
  public IsoniazidProphylaxisOnFirstOrSecondEncounterCalculation(
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

    CalculationResultMap adultSegEncounters =
        eptsCalculationService.allEncounters(
            encounterTypes, cohort, location, onOrAfter, onOrBefore, context);

    CalculationResultMap map = new CalculationResultMap();
    for (Integer pId : cohort) {
      List<Encounter> encounters = getFirstTwoEncounters(adultSegEncounters, pId);
      boolean prophylaxis = false;
      for (Encounter e : encounters) {
        if (hasIsoniazidProphylaxis(e)) {
          prophylaxis = true;
          break;
        }
      }
      map.put(pId, new BooleanResult(prophylaxis, this));
    }

    return map;
  }

  private List<Encounter> getFirstTwoEncounters(
      CalculationResultMap adultSegEncounters, Integer pId) {
    List<SimpleResult> results = resultForPatient(adultSegEncounters, pId);
    if (results == null) {
      return Collections.emptyList();
    }
    List<Encounter> encounters = new ArrayList<>();
    for (SimpleResult result : results) {
      encounters.add((Encounter) result.getValue());
    }
    sortByDatetime(encounters);
    return encounters.size() > 1 ? encounters.subList(0, 1) : encounters;
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

  private boolean hasIsoniazidProphylaxis(Encounter encounter) {
    Concept isoniazidUsageConcept = hivMetadata.getIsoniazidUsageConcept();
    Concept startDrugs = hivMetadata.getStartDrugs();
    for (Obs o : encounter.getObs()) {
      if (isoniazidUsageConcept.equals(o.getConcept())) {
        return startDrugs.equals(o.getValueCoded());
      }
    }
    return false;
  }
}
