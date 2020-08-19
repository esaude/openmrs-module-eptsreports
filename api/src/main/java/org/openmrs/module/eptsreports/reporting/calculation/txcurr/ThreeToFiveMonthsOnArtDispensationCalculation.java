package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

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
public class ThreeToFiveMonthsOnArtDispensationCalculation extends AbstractPatientCalculation {

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
    Concept quaterlyDispensation = hivMetadata.getQuarterlyConcept();
    Concept quaterlyDispensationDT = hivMetadata.getQuarterlyDispensation();
    Concept startDrugs = hivMetadata.getStartDrugs();
    Concept continueRegimen = hivMetadata.getContinueRegimenConcept();
    Concept monthly = hivMetadata.getMonthlyConcept();
    // get the last fila with next drug pick up date captured, only the last one
    CalculationResultMap getLastFila =
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
    // get all the fila obs having next pickup concept collected
    CalculationResultMap getAllFila =
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
    // get exactly last typeOfDispensation
    CalculationResultMap getLastTypeOfDispensationWithoutQuartelyAsValueCoded =
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
    // get all obs with typeOfDispensation and and quartely value coded
    CalculationResultMap getAllLastTypeOfDispensationWithQuartelyAsValueCoded =
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
    // get only specific obs as the last encounter
    // find for question 23739 and specific answer of 23720
    CalculationResultMap getLastTypeOfDispensationWithQuartelyAsValueCodedAdded =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(quaterlyDispensation),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);

    // get last DT
    CalculationResultMap getLastQuartelyDispensationWithStartOrContinueRegimen =
        ePTSCalculationService.getObs(
            quaterlyDispensationDT,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // get last DT having either start or contiue regimen
    CalculationResultMap getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedMap =
        ePTSCalculationService.getObs(
            quaterlyDispensationDT,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(startDrugs, continueRegimen),
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    // get DT with ANY list
    CalculationResultMap getAllDtQuartelyDispensationWithStartOrContinueRegimen =
        ePTSCalculationService.getObs(
            quaterlyDispensationDT,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            onOrBefore,
            context);
    CalculationResultMap lastFichaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(ficha), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    CalculationResultMap lastFilaEncounterMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(fila), TimeQualifier.LAST, cohort, location, onOrBefore, context);
    CalculationResultMap allFilaEncountersMap =
        ePTSCalculationService.getEncounter(
            Arrays.asList(fila), TimeQualifier.ANY, cohort, location, onOrBefore, context);
    CalculationResultMap semestaralquartelyMap =
        ePTSCalculationService.getObs(
            quaterlyDispensationDT,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            onOrBefore,
            context);
    CalculationResultMap getLastEncounterWithDepositionAndMonthlyAsCodedValueMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.LAST,
            null,
            context);
    // provide one that has value coded of monthly
    CalculationResultMap getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedMap =
        ePTSCalculationService.getObs(
            typeOfDispensation,
            Arrays.asList(ficha),
            cohort,
            Arrays.asList(location),
            Arrays.asList(monthly),
            TimeQualifier.LAST,
            null,
            context);

    for (Integer pId : cohort) {
      boolean found = false;

      Obs lastFilaObs = EptsCalculationUtils.obsResultForPatient(getLastFila, pId);
      Obs getLastTypeOfDispensationObsWithoutQuartelyValueCoded =
          EptsCalculationUtils.obsResultForPatient(
              getLastTypeOfDispensationWithoutQuartelyAsValueCoded, pId);
      Obs getLastQuartelyDispensationObsWithStartOrContinueRegimenObs =
          EptsCalculationUtils.obsResultForPatient(
              getLastQuartelyDispensationWithStartOrContinueRegimen, pId);
      Encounter lastFichaEncounter =
          EptsCalculationUtils.resultForPatient(lastFichaEncounterMap, pId);
      Obs lastQuartelyObsWithCompleted =
          EptsCalculationUtils.obsResultForPatient(semestaralquartelyMap, pId);

      Encounter lastFilaEncounter =
          EptsCalculationUtils.resultForPatient(lastFilaEncounterMap, pId);

      Obs getObsWithDepositionAndMonthlyAsCodedValue =
          EptsCalculationUtils.obsResultForPatient(
              getLastEncounterWithDepositionAndMonthlyAsCodedValueMap, pId);

      // get t he last obs for concept 23739 and answer 23720
      Obs getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs =
          EptsCalculationUtils.obsResultForPatient(
              getLastTypeOfDispensationWithQuartelyAsValueCodedAdded, pId);
      // get obs that has monthly value coded
      Obs getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs =
          EptsCalculationUtils.obsResultForPatient(
              getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedMap, pId);
      Obs getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs =
          EptsCalculationUtils.obsResultForPatient(
              getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedMap, pId);

      // get all the list result for the fila
      ListResult listResultForAllFila = (ListResult) getAllFila.get(pId);
      List<Obs> obsListForAllFila = EptsCalculationUtils.extractResultValues(listResultForAllFila);
      // get all the list results for ficha with quaterlyDispensation
      ListResult listResultAddQuartelyDispensation =
          (ListResult) getAllLastTypeOfDispensationWithQuartelyAsValueCoded.get(pId);
      List<Obs> listObsForQuartely =
          EptsCalculationUtils.extractResultValues(listResultAddQuartelyDispensation);
      // get all the list with the patients DT and
      ListResult listResultDtAll =
          (ListResult) getAllDtQuartelyDispensationWithStartOrContinueRegimen.get(pId);
      List<Obs> listresultsDtAll = EptsCalculationUtils.extractResultValues(listResultDtAll);

      ListResult listResultAllFilaEncounters = (ListResult) allFilaEncountersMap.get(pId);
      List<Encounter> allFilaEncounters =
          EptsCalculationUtils.extractResultValues(listResultAllFilaEncounters);
      Encounter lastFilaPickedEncounter = null;
      Encounter secondLastEncounter = null;
      List<Obs> filaObsOnTheSameEncounterDate = new ArrayList<Obs>();
      if (allFilaEncounters.size() >= 2) {
        sortEncountersByEncounterId(allFilaEncounters);
        lastFilaPickedEncounter = allFilaEncounters.get(allFilaEncounters.size() - 1);
        secondLastEncounter = allFilaEncounters.get(allFilaEncounters.size() - 2);
      }

      // case 1: fila as last encounter and has return visit date for drugs filled
      // this is compared to ficha, if fila > ficha and the ficha filled should be the one with
      // typeOfDispensation(23739)
      if (lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && lastFilaObs.getEncounter().getEncounterDatetime() != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded.getEncounter() != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded
                  .getEncounter()
                  .getEncounterDatetime()
              != null
          && lastFilaObs.getValueDatetime() != null
          && lastFilaObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(
                      getLastTypeOfDispensationObsWithoutQuartelyValueCoded
                          .getEncounter()
                          .getEncounterDatetime())
              > 0
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              >= 83
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              <= 173) {
        found = true;
      }

      // case 2: fila as last encounter and has return visit date for drugs filled
      // this is compared to ficha, if fila > ficha and the ficha filled should be the one with
      // QUARTERLY DISPENSATION (DT) - 23730
      else if (lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && lastFilaObs.getEncounter().getEncounterDatetime() != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs.getEncounter() != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getEncounter()
                  .getEncounterDatetime()
              != null
          && lastFilaObs.getValueDatetime() != null
          && lastFilaObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(
                      getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                          .getEncounter()
                          .getEncounterDatetime())
              > 0
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              >= 83
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              <= 173) {
        found = true;
      }
      // case 3: fila as the last encounter and has return visit date for drugs filled
      // This is comapred to the 6 with 23739 concept collected and value coded of 23720

      else if (lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && lastFilaObs.getEncounter().getEncounterDatetime() != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded == null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs == null
          && lastFilaObs.getValueDatetime() != null
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              >= 83
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              <= 173) {
        found = true;
      }
      // case 4: ficha  as last encounter(ficha > fila) reverse of case1
      // this is compared to the date of Encounter Type Id = 6 Last TYPE OF DISPENSATION
      // (id=23739)Value.code = QUARTERLY (id=23720)
      else if (lastFilaObs != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded
              .getValueCoded()
              .equals(quaterlyDispensation)
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(lastFilaObs.getEncounter().getEncounterDatetime())
              > 0) {
        found = true;
      }
      // case 5: ficha as last encounter, ficha >fila opposite of 2
      // this is compared to the date of Encounter Type Id = 6 Last QUARTERLY DISPENSATION (DT)
      // (id=23730)Value.coded= START DRUGS (id=1256) OR Value.coded= (CONTINUE REGIMEN id=1257)
      else if (lastFilaObs != null
          && lastFichaEncounter != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs != null
          && (getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getValueCoded()
                  .equals(startDrugs)
              || getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getValueCoded()
                  .equals(continueRegimen))
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(lastFilaObs.getEncounter().getEncounterDatetime())
              > 0) {
        found = true;
      }
      // case 6: If the most recent have more than one source FILA and FICHA registered on the same
      // most recent date, then consider the information from FILA compare with quartely 23739
      else if (lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && lastFilaObs.getValueDatetime() != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded.getEncounter() != null
          && lastFilaObs
              .getEncounter()
              .getEncounterDatetime()
              .equals(
                  getLastTypeOfDispensationObsWithoutQuartelyValueCoded
                      .getEncounter()
                      .getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              >= 83
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              <= 173) {
        found = true;
      }
      // case 7: here fila is the latest/only  encounter with the values for the observations when
      // DT is null
      // collected getLastTypeOfDispensationObsWithoutQuartelyValueCoded
      else if (lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && lastFilaObs.getEncounter().getEncounterDatetime() != null
          && lastFilaObs.getValueDatetime() != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs != null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs.getEncounter() != null
          && lastFilaObs
              .getEncounter()
              .getEncounterDatetime()
              .equals(
                  getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                      .getEncounter()
                      .getEncounterDatetime())
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              >= 83
          && EptsCalculationUtils.daysSince(
                  lastFilaObs.getEncounter().getEncounterDatetime(), lastFilaObs.getValueDatetime())
              <= 173) {
        found = true;
      }
      // case 10: ficha has last/only encounter  Last TYPE OF DISPENSATION (id=23739) Value.code =
      // QUARTERLY (id=23720)
      // included also is the start and continue regimen
      else if (getLastTypeOfDispensationObsWithoutQuartelyValueCoded != null
          && lastFilaObs == null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded.getValueCoded() != null
          && getLastTypeOfDispensationObsWithoutQuartelyValueCoded
              .getValueCoded()
              .equals(quaterlyDispensation)) {
        found = true;
      }

      // case 11: ficha has last/only encounter
      // included also is the start and continue regimen
      else if (getLastQuartelyDispensationObsWithStartOrContinueRegimenObs != null
          && lastFilaObs == null
          && getLastQuartelyDispensationObsWithStartOrContinueRegimenObs.getValueCoded() != null
          && (getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getValueCoded()
                  .equals(startDrugs)
              || getLastQuartelyDispensationObsWithStartOrContinueRegimenObs
                  .getValueCoded()
                  .equals(continueRegimen))) {
        found = true;
      } else if (getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs != null
          && lastFilaObs == null
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs != null
          && getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs
                  .getObsDatetime()
                  .compareTo(
                      getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs.getObsDatetime())
              >= 0) {
        found = true;
      } else if (getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs != null
          && lastFilaObs == null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs != null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs
                  .getObsDatetime()
                  .compareTo(
                      getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs.getObsDatetime())
              >= 0) {
        found = true;
      } else if (lastFilaObs != null
          && getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs != null
          && getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(lastFilaObs.getEncounter().getEncounterDatetime())
              > 0) {
        found = true;
      } else if (lastFilaObs != null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs != null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(lastFilaObs.getEncounter().getEncounterDatetime())
              > 0) {
        found = true;
      } else if (lastFilaEncounter != null
          && lastFichaEncounter == null
          && obsListForAllFila.size() > 0) {
        for (Obs obs : obsListForAllFila) {
          if (lastFilaEncounter.equals(obs.getEncounter())
              && obs.getValueDatetime() != null
              && EptsCalculationUtils.daysSince(
                      obs.getEncounter().getEncounterDatetime(), obs.getValueDatetime())
                  >= 83
              && EptsCalculationUtils.daysSince(
                      obs.getEncounter().getEncounterDatetime(), obs.getValueDatetime())
                  <= 173) {
            found = true;
            break;
          }
        }
      }
      // fila and ficha available, but fila> fila
      else if (lastFilaPickedEncounter != null
          && secondLastEncounter != null
          && obsListForAllFila.size() > 0) {
        if (lastFilaPickedEncounter
            .getEncounterDatetime()
            .equals(secondLastEncounter.getEncounterDatetime())) {
          // loop through the obs and pick those that match those 2 encounter
          for (Obs obs : obsListForAllFila) {
            if (obs.getValueDatetime() != null
                && (lastFilaPickedEncounter.equals(obs.getEncounter())
                    || secondLastEncounter.equals(obs.getEncounter()))) {
              filaObsOnTheSameEncounterDate.add(obs);
            }
          }
          Date requiredDate = null;
          if (filaObsOnTheSameEncounterDate.size() == 2) {
            requiredDate = filaObsOnTheSameEncounterDate.get(0).getValueDatetime();
            if (filaObsOnTheSameEncounterDate.get(1).getValueDatetime().compareTo(requiredDate)
                > 0) {
              requiredDate = filaObsOnTheSameEncounterDate.get(1).getValueDatetime();
            }
          }
          // no that you have the right value datetime and the encounter date, do the logic for >=83
          // days and <=173 days
          if (requiredDate != null
              && EptsCalculationUtils.daysSince(
                      lastFilaPickedEncounter.getEncounterDatetime(), requiredDate)
                  >= 83
              && EptsCalculationUtils.daysSince(
                      lastFilaPickedEncounter.getEncounterDatetime(), requiredDate)
                  <= 173) {
            found = true;
          }
        }
      }

      // exclude   patients   who   have   the   last   SEMESTRAL   QUARTERLY (concept   id=23730
      // with value_coded as value_coded=1267)
      if (lastFichaEncounter != null
          && lastFilaEncounter != null
          && lastQuartelyObsWithCompleted != null
          && lastQuartelyObsWithCompleted.getEncounter() != null
          && lastQuartelyObsWithCompleted.getValueCoded() != null
          && lastFichaEncounter.equals(lastQuartelyObsWithCompleted.getEncounter())
          && lastQuartelyObsWithCompleted.getValueCoded().equals(completedConcept)
          && lastFichaEncounter
                  .getEncounterDatetime()
                  .compareTo(lastFilaEncounter.getEncounterDatetime())
              > 0) {
        found = false;
      }
      // exclude all patients who have ficha with 1098 which is after recent fila and ficha of
      // start/continue regimen
      if (getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs != null
          && lastFilaObs != null
          && lastFilaObs.getEncounter() != null
          && getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs.getEncounter() != null
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs != null
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs.getEncounter() != null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs != null
          && getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs.getEncounter()
              != null
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(lastFilaObs.getEncounter().getEncounterDatetime())
              > 0
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(
                      getLastTypeOfDispensationWithQuartelyAsValueCodedAddedObs
                          .getEncounter()
                          .getEncounterDatetime())
              > 0
          && getLastEncounterWithDepositionAndMonthlyAsCodedValueAddedObs
                  .getEncounter()
                  .getEncounterDatetime()
                  .compareTo(
                      getLastQuartelyDispensationWithStartOrContinueRegimenValueCodedObs
                          .getEncounter()
                          .getEncounterDatetime())
              > 0) {
        found = false;
      }

      resultMap.put(pId, new BooleanResult(found, this));
    }
    return resultMap;
  }

  private void sortEncountersByEncounterId(List<Encounter> encounters) {
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
