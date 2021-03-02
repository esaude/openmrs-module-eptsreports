package org.openmrs.module.eptsreports.reporting.calculation.cxcascrn;

import java.util.*;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class TXCXCACalculation extends AbstractPatientCalculation {

  private final String ON_OR_AFTER = "onOrAfter";
  private final String ON_OR_BEFORE = "onOrBefore";
  private final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Date startDate = (Date) context.getFromCache(ON_OR_AFTER);
    Date endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    Location location = (Location) context.getFromCache(LOCATION);

    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    EncounterType rastreioDoCancroDoColoUterinoEncounterType =
        hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType();
    Concept cryotherapyPerformedOnTheSameDayASViaConcept =
        hivMetadata.getCryotherapyPerformedOnTheSameDayASViaConcept();
    Concept yesConcept = hivMetadata.getPatientFoundYesConcept();
    Concept cryotherapyDateConcept = hivMetadata.getCryotherapyDateConcept();
    Concept viaResultOnTheReferenceConcept = hivMetadata.getViaResultOnTheReferenceConcept();
    Concept pediatricNursingConcept = hivMetadata.getPediatricNursingConcept();
    Concept thermocoagulationConcept = hivMetadata.getThermocoagulationConcept();
    Concept leepConcept = hivMetadata.getLeepConcept();
    Concept conizationConcept = hivMetadata.getconizationConcept();

    CalculationResultMap aaResultMap =
        calculate(
            Context.getRegisteredComponents(CXCASCRNAACalculation.class).get(0),
            cohort,
            parameterValues,
            context);

    CalculationResultMap criotherapyResulMap =
        eptsCalculationService.getObs(
            cryotherapyPerformedOnTheSameDayASViaConcept,
            rastreioDoCancroDoColoUterinoEncounterType,
            cohort,
            location,
            Arrays.asList(yesConcept),
            TimeQualifier.ANY,
            null,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap criotherapyDateResulMap =
        eptsCalculationService.getObs(
            cryotherapyDateConcept,
            rastreioDoCancroDoColoUterinoEncounterType,
            cohort,
            location,
            null,
            TimeQualifier.ANY,
            null,
            endDate,
            EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
            context);

    CalculationResultMap viaResultRefenceResulMap =
        eptsCalculationService.getObs(
            viaResultOnTheReferenceConcept,
            rastreioDoCancroDoColoUterinoEncounterType,
            cohort,
            location,
            Arrays.asList(
                pediatricNursingConcept, thermocoagulationConcept, leepConcept, conizationConcept),
            TimeQualifier.ANY,
            null,
            endDate,
            EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
            context);

    for (Integer pId : cohort) {

      Obs obs = EptsCalculationUtils.resultForPatient(aaResultMap, pId);

      if (endDate != null && obs != null) {
        List<Obs> criotherapies =
            EptsCalculationUtils.extractResultValues((ListResult) criotherapyResulMap.get(pId));
        List<Obs> criotherapyDates =
            EptsCalculationUtils.extractResultValues((ListResult) criotherapyDateResulMap.get(pId));
        List<Obs> viaResultRefences =
            EptsCalculationUtils.extractResultValues(
                (ListResult) viaResultRefenceResulMap.get(pId));

        for (Obs criotherapy : criotherapies) {

          if (criotherapy
                      .getEncounter()
                      .getEncounterDatetime()
                      .compareTo(obs.getEncounter().getEncounterDatetime())
                  >= 0
              && criotherapy.getEncounter().getEncounterDatetime().compareTo(endDate) <= 0) {
            map.put(pId, new SimpleResult(criotherapy, this));
            break;
          }
        }

        for (Obs criotherapyDate : criotherapyDates) {
          if (criotherapyDate
                      .getValueDatetime()
                      .compareTo(obs.getEncounter().getEncounterDatetime())
                  >= 0
              && criotherapyDate.getValueDatetime().compareTo(endDate) <= 0) {
            map.put(pId, new SimpleResult(criotherapyDate, this));
            break;
          }
        }

        for (Obs viaResultRefence : viaResultRefences) {

          if (viaResultRefence
                      .getValueDatetime()
                      .compareTo(obs.getEncounter().getEncounterDatetime())
                  >= 0
              && viaResultRefence.getValueDatetime().compareTo(endDate) <= 0) {
            map.put(pId, new SimpleResult(viaResultRefence, this));
            break;
          }
        }
      }
    }

    return map;
  }
}
