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
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodedObsOnFirstOrSecondEncounterCalculation extends AbstractPatientCalculation {

  private EPTSCalculationService eptsCalculationService;

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;

  @Autowired
  public CodedObsOnFirstOrSecondEncounterCalculation(
      EPTSCalculationService eptsCalculationService,
      HivMetadata hivMetadata,
      TbMetadata tbMetadata) {
    this.eptsCalculationService = eptsCalculationService;
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
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

    Concept tptRegimeConcept = tbMetadata.getRegimeTPTConcept();
    Concept isoniazidConcept = tbMetadata.getIsoniazidConcept();
    Concept threehpConcept = tbMetadata.get3HPConcept();

    Concept profilaxyStateConcept = tbMetadata.getDataEstadoDaProfilaxiaConcept();
    Concept startConcept = hivMetadata.getStartDrugs();

    CalculationResultMap adultSegEncounters =
        eptsCalculationService.allEncounters(
            encounterTypes, cohort, location, onOrAfter, onOrBefore, context);

    CalculationResultMap isoniazidResult =
        eptsCalculationService.getObs(
            tptRegimeConcept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            Arrays.asList(isoniazidConcept),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap threehpResult =
        eptsCalculationService.getObs(
            tptRegimeConcept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            Arrays.asList(threehpConcept),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap profilaxyStateResult =
        eptsCalculationService.getObs(
            profilaxyStateConcept,
            encounterTypes,
            cohort,
            Arrays.asList(location),
            Arrays.asList(startConcept),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap map = new CalculationResultMap();
    for (Integer pId : cohort) {
      List<Encounter> encounters = getFirstTwoEncounters(adultSegEncounters, pId);

      List<Obs> isoniazidObservations =
          EptsCalculationUtils.extractResultValues((ListResult) isoniazidResult.get(pId));

      List<Obs> threehpObservations =
          EptsCalculationUtils.extractResultValues((ListResult) threehpResult.get(pId));

      List<Obs> startObservations =
          EptsCalculationUtils.extractResultValues((ListResult) profilaxyStateResult.get(pId));

      boolean hasIsoniazid = false;
      boolean hasThreehp = false;
      boolean isStarting = false;
      for (Encounter e : encounters) {

        for (Obs o : isoniazidObservations) {
          if (e.getEncounterId().equals(o.getEncounter().getEncounterId())) {
            hasIsoniazid = true;
            break;
          }
        }

        for (Obs o : threehpObservations) {
          if (e.getEncounterId().equals(o.getEncounter().getEncounterId())) {
            hasThreehp = true;
            break;
          }
        }

        for (Obs o : startObservations) {
          if (e.getEncounterId().equals(o.getEncounter().getEncounterId())) {
            isStarting = true;
            break;
          }
        }
      }
      map.put(pId, new BooleanResult((hasIsoniazid || hasThreehp) && isStarting, this));
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
