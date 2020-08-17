package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class SixMonthsAndAboveOnArvDispensationCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> map, PatientCalculationContext context) {

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");

    EncounterType fila = hivMetadata.getARVPharmaciaEncounterType();
    EncounterType ficha = hivMetadata.getAdultoSeguimentoEncounterType();

    Concept returnVisitDateForArvDrugs = hivMetadata.getReturnVisitDateForArvDrugConcept();
    Concept typeOfDispensation = hivMetadata.getTypeOfDispensationConcept();
    Concept completedConcept = hivMetadata.getCompletedConcept();
    Concept quaterly = hivMetadata.getQuarterlyConcept();
    Concept dispensaSemestra = hivMetadata.getSemiannualDispensation();
    Concept startDrugs = hivMetadata.getStartDrugs();
    Concept continueRegimen = hivMetadata.getContinueRegimenConcept();
    Concept monthly = hivMetadata.getMonthlyConcept();

    CalculationResultMap getLastFilaWithReturnVisitForDrugFilledMap =
        ePTSCalculationService.getObs(
            returnVisitDateForArvDrugs,
            Arrays.asList(fila),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // get all the next pick up date
    CalculationResultMap getAllFilaWithReturnVisitForDrugFilledMap =
        ePTSCalculationService.getObs(
            returnVisitDateForArvDrugs,
            Arrays.asList(fila),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            onOrBefore,
            context);
    CalculationResultMap getLastFichaWithSemestaral =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // get all the list of typeOfDispensation
    CalculationResultMap getAllFichaWithSemestaral =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            onOrBefore,
            context);
    CalculationResultMap getLastFichaWithoutSemestralMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    CalculationResultMap lastDispensaTrimestralWithCompletedMap =
        ePTSCalculationService.getObs(
            dispensaSemestra,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(completedConcept),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    CalculationResultMap lastDispensaSemestraWithStartOrContinueDrugsMap =
        ePTSCalculationService.getObs(
            dispensaSemestra,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);

    CalculationResultMap lastDispensaSemestraWithoutStartOrContinueDrugsMap =
        ePTSCalculationService.getObs(
            dispensaSemestra,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // all list of dispensaSemestra with start or continue regime
    CalculationResultMap allDispensaSemestraWithoutStartOrContinueDrugsMap =
        ePTSCalculationService.getObs(
            dispensaSemestra,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            onOrBefore,
            context);
    // find map that has monthly as an obs for type of desposition
    CalculationResultMap getLastFichaWithMonthlyObsMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(monthly),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // find map that has quartely response
    CalculationResultMap getLastFichaWithQuartelyObsMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(quaterly),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // find map that has semestarl as last obs
    CalculationResultMap getLastFichaWithSemestaralObsMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(dispensaSemestra),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    CalculationResultMap lastFichaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(ficha), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    CalculationResultMap lastFilaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(fila), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    for (Integer pId : cohort) {
      boolean found = false;
      // get last encounters
      Encounter lastFichaEncounter =
          EptsCalculationUtils.resultForPatient(lastFichaEncounterMap, pId);
      Encounter lastFilaEncounter =
          EptsCalculationUtils.resultForPatient(lastFilaEncounterMap, pId);

      Obs lastFilaWithReturnForDrugsObs =
          EptsCalculationUtils.obsResultForPatient(getLastFilaWithReturnVisitForDrugFilledMap, pId);
      Obs lastFichaObsWithSemestarlValueCoded =
          EptsCalculationUtils.obsResultForPatient(getLastFichaWithSemestaral, pId);
      Obs lastDispensaTrimestralWithCompltedObs =
          EptsCalculationUtils.obsResultForPatient(lastDispensaTrimestralWithCompletedMap, pId);
      Obs lastDispensaSemestraWithStartOrContinueDrugsObs =
          EptsCalculationUtils.obsResultForPatient(
              lastDispensaSemestraWithStartOrContinueDrugsMap, pId);

      Obs lastDispensaTrimestralWithoutSemestralObs =
          EptsCalculationUtils.obsResultForPatient(getLastFichaWithoutSemestralMap, pId);
      Obs lastDispensaSemestraWithoutStartOrContinueDrugsObs =
          EptsCalculationUtils.obsResultForPatient(
              lastDispensaSemestraWithoutStartOrContinueDrugsMap, pId);
      // get latest ficha with monthly obs collected
      Obs getLastFichaWithMonthlyObs =
          EptsCalculationUtils.obsResultForPatient(getLastFichaWithMonthlyObsMap, pId);
      // get lates ficha with quatertly collected
      Obs getLastFichaWithQuartelyObs =
          EptsCalculationUtils.obsResultForPatient(getLastFichaWithQuartelyObsMap, pId);
      // get latest semestarl obs collected
      Obs getLastFichaWithSemestaralObs =
          EptsCalculationUtils.obsResultForPatient(getLastFichaWithSemestaralObsMap, pId);

      // get all fila list of date obs
      ListResult listResultAllNextDateOfAppointment =
          (ListResult) getAllFilaWithReturnVisitForDrugFilledMap.get(pId);
      List<Obs> allFilaObsList =
          EptsCalculationUtils.extractResultValues(listResultAllNextDateOfAppointment);
      // get all ficha with type of disposition
      ListResult listResultDisposition = (ListResult) getAllFichaWithSemestaral.get(pId);
      List<Obs> listResultDispositionObs =
          EptsCalculationUtils.extractResultValues(listResultDisposition);
      // get all ficha obs with allDispensaSemestraWithoutStartOrContinueDrugsMap
      ListResult allListResultDispensaSemestry =
          (ListResult) allDispensaSemestraWithoutStartOrContinueDrugsMap.get(pId);
      List<Obs> allListResultDispensaSemestryObs =
          EptsCalculationUtils.extractResultValues(allListResultDispensaSemestry);

      // case 1 fila filled is after ficha filled with semestral concept id
      if (lastDispensaTrimestralWithoutSemestralObs != null
          && lastFilaEncounter != null
          && lastFichaEncounter != null
          && lastDispensaTrimestralWithoutSemestralObs.getEncounter() != null
          && lastFichaEncounter.equals(lastDispensaTrimestralWithoutSemestralObs.getEncounter())
          && lastFilaWithReturnForDrugsObs != null
          && lastFilaWithReturnForDrugsObs.getEncounter() != null
          && lastFilaEncounter.equals(lastFilaWithReturnForDrugsObs.getEncounter())
          && lastFilaWithReturnForDrugsObs.getValueDatetime() != null
          && lastFilaWithReturnForDrugsObs
              .getEncounter()
              .getEncounterDatetime()
              .after(
                  lastDispensaTrimestralWithoutSemestralObs.getEncounter().getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime(),
                  lastFilaWithReturnForDrugsObs.getValueDatetime())
              > 173) {
        found = true;

      }
      // case 2 ficha filled is after fila filled with semestral concept id reverse of 1
      else if (lastFilaEncounter != null
          && lastFilaWithReturnForDrugsObs != null
          && lastFichaEncounter != null
          && lastFichaObsWithSemestarlValueCoded != null
          && lastFichaObsWithSemestarlValueCoded.getEncounter() != null
          && lastFichaEncounter.equals(lastFichaObsWithSemestarlValueCoded.getEncounter())
          && lastFilaEncounter.equals(lastFilaWithReturnForDrugsObs.getEncounter())
          && lastFichaObsWithSemestarlValueCoded.getValueCoded().equals(dispensaSemestra)
          && lastFichaEncounter
              .getEncounterDatetime()
              .after(lastFilaEncounter.getEncounterDatetime())) {
        found = true;
      }
      // case 3 ficha filled is after fila filled with start or continue regimen concept id
      else if (lastFilaWithReturnForDrugsObs != null
          && lastFichaEncounter != null
          && lastFilaEncounter != null
          && lastFilaWithReturnForDrugsObs.getEncounter() != null
          && lastFilaEncounter.equals(lastFilaWithReturnForDrugsObs.getEncounter())
          && lastDispensaSemestraWithStartOrContinueDrugsObs != null
          && lastFichaEncounter.equals(
              lastDispensaSemestraWithStartOrContinueDrugsObs.getEncounter())
          && (lastDispensaSemestraWithStartOrContinueDrugsObs.getValueCoded().equals(startDrugs)
              || lastDispensaSemestraWithStartOrContinueDrugsObs
                  .getValueCoded()
                  .equals(continueRegimen))
          && lastFichaEncounter
              .getEncounterDatetime()
              .after(lastFilaEncounter.getEncounterDatetime())) {
        found = true;

      }
      // case 4 if there are multiple fila filled/only fila available for the same date, pick the
      // latest that has
      // information filled
      else if (lastFilaWithReturnForDrugsObs != null
          && lastFilaEncounter != null
          && lastFilaWithReturnForDrugsObs.getEncounter() != null
          && lastFilaEncounter.equals(lastFilaWithReturnForDrugsObs.getEncounter())
          && lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime() != null
          && lastDispensaTrimestralWithoutSemestralObs == null
          && lastDispensaSemestraWithoutStartOrContinueDrugsObs == null
          && lastFilaWithReturnForDrugsObs.getValueDatetime() != null
          && EptsCalculationUtils.daysSince(
                  lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime(),
                  lastFilaWithReturnForDrugsObs.getValueDatetime())
              > 173) {
        found = true;
      }
      // case 5 if ficha filled
      else if ((lastFichaObsWithSemestarlValueCoded != null
              && lastFichaEncounter != null
              && lastFichaObsWithSemestarlValueCoded.getEncounter() != null
              && lastFichaEncounter.equals(lastFichaObsWithSemestarlValueCoded.getEncounter())
              && lastFichaObsWithSemestarlValueCoded.getValueCoded().equals(dispensaSemestra)
              && lastFilaWithReturnForDrugsObs == null)
          || (lastDispensaSemestraWithStartOrContinueDrugsObs != null
                  && lastFichaEncounter != null
                  && lastDispensaSemestraWithStartOrContinueDrugsObs.getEncounter() != null
                  && lastFichaEncounter.equals(
                      lastDispensaSemestraWithStartOrContinueDrugsObs.getEncounter())
                  && (lastDispensaSemestraWithStartOrContinueDrugsObs
                          .getValueCoded()
                          .equals(startDrugs)
                      || lastDispensaSemestraWithStartOrContinueDrugsObs
                          .getValueCoded()
                          .equals(continueRegimen)))
              && lastFilaWithReturnForDrugsObs == null) {
        found = true;
      }
      // case 6 if there is a fila filled with ficha filled with semestral concept filled on the
      // same date
      // we will end up picking the fila
      else if (lastFichaEncounter != null
          && lastFilaEncounter != null
          && lastFichaEncounter.getEncounterDatetime() != null
          && lastFilaWithReturnForDrugsObs != null
          && lastFilaWithReturnForDrugsObs.getEncounter() != null
          && lastFilaEncounter.equals(lastFilaWithReturnForDrugsObs.getEncounter())
          && lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime() != null
          && lastFilaWithReturnForDrugsObs.getValueDatetime() != null
          && lastFichaEncounter
              .getEncounterDatetime()
              .equals(lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  lastFilaWithReturnForDrugsObs.getEncounter().getEncounterDatetime(),
                  lastFilaWithReturnForDrugsObs.getValueDatetime())
              > 173) {
        found = true;
      }
      // find all the fila, compare with the last encounter, if it has >173 days, pick it here
      else if (lastFilaEncounter != null && allFilaObsList.size() > 0) {
        for (Obs obs : allFilaObsList) {
          if (lastFilaEncounter.equals(obs.getEncounter())
              && obs.getValueDatetime() != null
              && EptsCalculationUtils.daysSince(
                      lastFilaEncounter.getEncounterDatetime(), obs.getValueDatetime())
                  > 173) {
            found = true;
            break;
          }
        }
      }
      // find all obs compared per the encounter based on
      else if (lastFichaEncounter != null && listResultDispositionObs.size() > 0) {
        for (Obs obs : listResultDispositionObs) {
          if (lastFichaEncounter.equals(obs.getEncounter())
              && obs.getValueCoded() != null
              && obs.getValueCoded().equals(dispensaSemestra)) {
            found = true;
            break;
          }
        }
      }
      // find all obs compared with last encounter with start and continue regimen
      else if (lastFichaEncounter != null && allListResultDispensaSemestryObs.size() > 0) {
        for (Obs obs : allListResultDispensaSemestryObs) {
          if (lastFichaEncounter.equals(obs.getEncounter())
              && obs.getValueCoded() != null
              && (obs.getValueCoded().equals(startDrugs)
                  || obs.getValueCoded().equals(continueRegimen))) {
            found = true;
            break;
          }
        }
      }
      // what if there is 3 fichas on the same date that has a criteria for <3 months, 3-5 months
      // and > 6 months
      // we will have to pick that criteria here as well
      else if (getLastFichaWithMonthlyObs != null
          && getLastFichaWithSemestaralObs != null
          && getLastFichaWithMonthlyObs.getObsDatetime() != null
          && getLastFichaWithSemestaralObs.getObsDatetime() != null
          && getLastFichaWithSemestaralObs
                  .getObsDatetime()
                  .compareTo(getLastFichaWithMonthlyObs.getObsDatetime())
              >= 0) {
        found = true;
      } else if (getLastFichaWithQuartelyObs != null
          && getLastFichaWithSemestaralObs != null
          && getLastFichaWithQuartelyObs.getObsDatetime() != null
          && getLastFichaWithSemestaralObs.getObsDatetime() != null
          && getLastFichaWithSemestaralObs
                  .getObsDatetime()
                  .compareTo(getLastFichaWithQuartelyObs.getObsDatetime())
              >= 0) {
        found = true;
      }

      // case 8:
      if (lastDispensaTrimestralWithCompltedObs != null) {
        found = false;
      }
      resultMap.put(pId, new BooleanResult(found, this));
    }

    return resultMap;
  }
}
