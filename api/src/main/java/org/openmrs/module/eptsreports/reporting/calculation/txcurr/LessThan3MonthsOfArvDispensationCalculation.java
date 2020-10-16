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
public class LessThan3MonthsOfArvDispensationCalculation extends AbstractPatientCalculation {
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
    Concept monthly = hivMetadata.getMonthlyConcept();

    CalculationResultMap getFichaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(ficha), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    CalculationResultMap getFilaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(fila), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    // get the last fila with date of next pick
    CalculationResultMap getLastEncounterWithReturnDateForArvMap =
        ePTSCalculationService.getObs(
            returnVisitDateForArvDrugs,
            Arrays.asList(fila),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            context);
    // get all fila obs with drug pick up date
    CalculationResultMap getAllEncounterWithReturnDateForArvMap =
        ePTSCalculationService.getObs(
            returnVisitDateForArvDrugs,
            Arrays.asList(fila),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap getLastEncounterWithDepositionAndMonthlyAsCodedValueMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(monthly),
            TimeQualifier.LAST,
            null,
            context);

    CalculationResultMap getLastEncounterWithoutDepositionAndMonthlyAsCodedValueMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            context);
    // get any all obs with typeOfDispensation and monthly
    CalculationResultMap getAllEncounterWithoutDepositionAndMonthlyAsCodedValueMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    for (Integer pId : cohort) {
      boolean found = false;

      Encounter lastFichaEncounter =
          EptsCalculationUtils.resultForPatient(getFichaEncounterMap, pId);
      Encounter lastFilaEncounter = EptsCalculationUtils.resultForPatient(getFilaEncounterMap, pId);

      Obs getObsWithReturnVisitDateFilled =
          EptsCalculationUtils.obsResultForPatient(getLastEncounterWithReturnDateForArvMap, pId);

      Obs getObsWithDepositionAndMonthlyAsCodedValue =
          EptsCalculationUtils.obsResultForPatient(
              getLastEncounterWithDepositionAndMonthlyAsCodedValueMap, pId);
      Obs getObsWithoutDepositionAndMonthlyAsCodedValue =
          EptsCalculationUtils.obsResultForPatient(
              getLastEncounterWithoutDepositionAndMonthlyAsCodedValueMap, pId);

      // get all the drug pick up date
      ListResult listResultNextDrugPickup =
          (ListResult) getAllEncounterWithReturnDateForArvMap.get(pId);
      List<Obs> obsListNextDrugPickup =
          EptsCalculationUtils.extractResultValues(listResultNextDrugPickup);
      // get all desposition and monthly
      ListResult listResultDespositionMonthlyObs =
          (ListResult) getAllEncounterWithoutDepositionAndMonthlyAsCodedValueMap.get(pId);
      List<Obs> obsListDespositionMonthlyObs =
          EptsCalculationUtils.extractResultValues(listResultDespositionMonthlyObs);

      // case 1: fila as last encounter and has return visit date for drugs filled
      // Both 2 encounter are filled with relevant obseravtions
      // We consider the fila
      if (getObsWithoutDepositionAndMonthlyAsCodedValue != null
          && lastFilaEncounter != null
          && getObsWithoutDepositionAndMonthlyAsCodedValue.getEncounter() != null
          && getObsWithoutDepositionAndMonthlyAsCodedValue.getEncounter().getEncounterDatetime()
              != null
          && getObsWithReturnVisitDateFilled != null
          && lastFilaEncounter.equals(getObsWithReturnVisitDateFilled.getEncounter())
          && getObsWithReturnVisitDateFilled.getEncounter() != null
          && getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime() != null
          && getObsWithReturnVisitDateFilled.getValueDatetime() != null
          && getObsWithReturnVisitDateFilled
              .getEncounter()
              .getEncounterDatetime()
              .after(
                  getObsWithoutDepositionAndMonthlyAsCodedValue
                      .getEncounter()
                      .getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime(),
                  getObsWithReturnVisitDateFilled.getValueDatetime())
              < 83) {
        found = true;
      }
      // case 2: ficha as the last encounter and has Last TYPE OF DISPENSATION and value coded as
      // monthly, make sure the last encounter has required obs collected on them
      else if (lastFilaEncounter != null
          && getObsWithReturnVisitDateFilled != null
          && lastFichaEncounter != null
          && getObsWithReturnVisitDateFilled.getEncounter() != null
          && lastFilaEncounter.equals(getObsWithReturnVisitDateFilled.getEncounter())
          && getObsWithoutDepositionAndMonthlyAsCodedValue != null
          && lastFichaEncounter.equals(getObsWithoutDepositionAndMonthlyAsCodedValue.getEncounter())
          && getObsWithoutDepositionAndMonthlyAsCodedValue.getValueCoded().equals(monthly)
          && lastFichaEncounter
                  .getEncounterDatetime()
                  .compareTo(lastFilaEncounter.getEncounterDatetime())
              > 0) {
        found = true;
      }
      // case 3: if both fila and ficha are available taken on the same date, we pick fila first if
      // it has the next drug pick up date
      // otherwise we consider ficha if fila is null, but ficha has to contain the required obs to
      // pass
      else if (lastFichaEncounter != null
          && lastFilaEncounter != null
          && lastFichaEncounter.getEncounterDatetime() != null
          && getObsWithReturnVisitDateFilled != null
          && lastFilaEncounter.equals(getObsWithReturnVisitDateFilled.getEncounter())
          && getObsWithReturnVisitDateFilled.getEncounter() != null
          && getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime() != null
          && getObsWithReturnVisitDateFilled.getValueDatetime() != null
          && getObsWithReturnVisitDateFilled
              .getEncounter()
              .getEncounterDatetime()
              .equals(lastFichaEncounter.getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime(),
                  getObsWithReturnVisitDateFilled.getValueDatetime())
              < 83) {
        found = true;
      }

      // case 4: Only fila available and has value datetime collected for the next drug pick up

      else if (getObsWithReturnVisitDateFilled != null
          && lastFilaEncounter != null
          && getObsWithReturnVisitDateFilled.getEncounter() != null
          && lastFilaEncounter.equals(getObsWithReturnVisitDateFilled.getEncounter())
          && getObsWithDepositionAndMonthlyAsCodedValue == null
          && getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime() != null
          && getObsWithReturnVisitDateFilled.getValueDatetime() != null
          && EptsCalculationUtils.daysSince(
                  getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime(),
                  getObsWithReturnVisitDateFilled.getValueDatetime())
              < 83) {
        found = true;
      }
      // case 5: if only ficha is available and has Last TYPE OF DISPENSATION and value coded as
      // monthly
      else if (getObsWithDepositionAndMonthlyAsCodedValue != null
          && getObsWithReturnVisitDateFilled == null) {
        found = true;
      }
      // case 6 check if there is date for drug pick up and there is a ficha without this concept
      // 23739
      // collected
      else if (getObsWithReturnVisitDateFilled != null
          && lastFilaEncounter != null
          && getObsWithReturnVisitDateFilled.getEncounter() != null
          && lastFilaEncounter.equals(getObsWithReturnVisitDateFilled.getEncounter())
          && getObsWithoutDepositionAndMonthlyAsCodedValue == null
          && getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime() != null
          && getObsWithReturnVisitDateFilled.getValueDatetime() != null
          && EptsCalculationUtils.daysSince(
                  getObsWithReturnVisitDateFilled.getEncounter().getEncounterDatetime(),
                  getObsWithReturnVisitDateFilled.getValueDatetime())
              < 83) {
        found = true;
      }
      // get all the fila obs collected and compare with last value date
      else if (lastFilaEncounter != null && obsListNextDrugPickup.size() > 0) {
        for (Obs obs : obsListNextDrugPickup) {
          if (lastFilaEncounter.equals(obs.getEncounter())
              && obs.getValueDatetime() != null
              && EptsCalculationUtils.daysSince(
                      lastFilaEncounter.getEncounterDatetime(), obs.getValueDatetime())
                  < 83) {
            found = true;
            break;
          }
        }
      }
      // get all ficha with 1098 values and match last encounter
      else if (lastFichaEncounter != null && obsListDespositionMonthlyObs.size() > 0) {
        for (Obs obs : obsListDespositionMonthlyObs) {
          if (lastFichaEncounter.equals(obs.getEncounter())
              && obs.getValueCoded() != null
              && obs.getValueCoded().equals(monthly)) {
            found = true;
            break;
          }
        }
      }
      resultMap.put(pId, new BooleanResult(found, this));
    }
    return resultMap;
  }
}
