/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class RoutineCalculation extends AbstractPatientCalculation {

  public static final int CRITERIA2_MONTHS_MIN = 12;

  public static final int CRITERIA2_MONTHS_MAX = 15;

  /**
   * Patients on ART for the last X months with one VL result registered in the 12 month period
   * Between Y to Z months after ART initiation
   *
   * @param cohort
   * @param params
   * @param context
   * @return CalculationResultMap
   */
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    // External Dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    InitialArtStartDateCalculation artStartDateCalculation =
        Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0);
    OnArtForMoreThanXmonthsCalcultion onArtForMoreThanXmonthsCalcultion =
        Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    CalculationResultMap map = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");
    Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    Concept regimeConcept = hivMetadata.getRegimeConcept();
    Date latestVlLowerDateLimit = EptsCalculationUtils.addMonths(context.getNow(), -12);
    Date allVlLowerDateLimit =
        EptsCalculationUtils.addMonths(latestVlLowerDateLimit, -CRITERIA2_MONTHS_MAX);
    EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
    PatientsOnRoutineEnum criteria = (PatientsOnRoutineEnum) params.get("criteria");
    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();

    // lookups
    CalculationResultMap patientHavingVL =
        ePTSCalculationService.getObs(
            viralLoadConcept,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            allVlLowerDateLimit,
            context);

    CalculationResultMap changingRegimenLines =
        ePTSCalculationService.getObs(
            regimeConcept,
            cohort,
            Arrays.asList(location),
            getSecondLineTreatmentArvs(hivMetadata),
            TimeQualifier.FIRST,
            null,
            context);

    // get the ART initiation date
    CalculationResultMap arvsInitiationDateMap =
        calculate(artStartDateCalculation, cohort, context);
    CalculationResultMap lastVl =
        ePTSCalculationService.lastObs(
            Arrays.asList(labEncounterType, adultFollowup, childFollowup),
            viralLoadConcept,
            location,
            latestVlLowerDateLimit,
            context.getNow(),
            cohort,
            context);

    // get patients who have been on ART for more than 3 months
    Set<Integer> onArtForMoreThan3Months =
        EptsCalculationUtils.patientsThatPass(
            calculate(onArtForMoreThanXmonthsCalcultion, cohort, context));

    for (Integer pId : cohort) {
      boolean isOnRoutine = false;
      Date artInitiationDate = null;
      SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(pId);
      Obs lastVlObs = EptsCalculationUtils.resultForPatient(lastVl, pId);

      if (artStartDateResult != null) {
        artInitiationDate = (Date) artStartDateResult.getValue();
      }
      // check that this patient should be on ART for more than six months
      if (artInitiationDate != null
          && lastVlObs != null
          && lastVlObs.getObsDatetime() != null
          && criteria != null
          && onArtForMoreThan3Months.contains(pId)) {

        // we do not consider if the patient's last VL obs is not within
        // window
        if (lastVlObs.getObsDatetime().after(latestVlLowerDateLimit)
            && lastVlObs.getObsDatetime().before(context.getNow())) {

          // get all the VL results for each patient in the last 12 months
          ListResult vlObsResult = (ListResult) patientHavingVL.get(pId);
          List<Obs> vLoadList = EptsCalculationUtils.extractResultValues(vlObsResult);
          List<Obs> viralLoadForPatientTakenWithin12Months =
              getViralLoadForPatientTakenWithin12Months(
                  context.getNow(), latestVlLowerDateLimit, vLoadList);

          // find out for criteria 1 a
          // the patients should be 6 to 9 months after ART initiation
          // get the obs date for this VL and compare that with the
          // provided dates
          if (isOnRoutineCriteria1(
              criteria, artInitiationDate, viralLoadForPatientTakenWithin12Months)) {
            isOnRoutine = true;
          }

          // find out criteria 2
          if (isOnRoutineCriteria2(
              criteria,
              viralLoadForPatientTakenWithin12Months,
              vLoadList,
              lastVlObs.getObsDatetime())) {
            isOnRoutine = true;
          }

          // find out criteria 3
          if (!isOnRoutine && !viralLoadForPatientTakenWithin12Months.isEmpty()) {
            // get when a patient switch between lines from first to
            // second
            // Date when started on second line will be considered
            // the changing date
            isOnRoutine =
                isOnRoutineCriteria3(changingRegimenLines, pId, vLoadList, latestVlLowerDateLimit);
          }
        }
      }
      map.put(pId, new BooleanResult(isOnRoutine, this));
    }

    return map;
  }

  private boolean isOnRoutineCriteria3(
      CalculationResultMap changingRegimenLines,
      Integer pId,
      List<Obs> allViralLoadForPatient,
      Date latestVlLowerDateLimit) {
    boolean isOnRoutine = false;

    Obs obs = EptsCalculationUtils.resultForPatient(changingRegimenLines, pId);

    if (obs != null && latestVlLowerDateLimit != null) {
      Date startRegimeDate = obs.getObsDatetime();

      if (startRegimeDate != null && startRegimeDate.before(latestVlLowerDateLimit)) {
        isOnRoutine = true;
        // check that there is no other VL registered between first
        // encounter_date and
        // vl_registered_date
        // loop through the vls and exclude the patient if they have an
        // obs falling
        // between the 2 dates
        for (Obs obs1 : allViralLoadForPatient) {
          if (obs1.getObsDatetime() != null
              && obs1.getObsDatetime().after(startRegimeDate)
              && obs1.getObsDatetime().before(latestVlLowerDateLimit)) {
            isOnRoutine = false;
          }
        }
      }
    }
    return isOnRoutine;
  }

  private boolean isOnRoutineCriteria2(
      PatientsOnRoutineEnum criteria,
      List<Obs> viralLoadForPatientTakenWithin12Months,
      List<Obs> allViralLoadForPatient,
      Date lastViralLoadDate) {

    boolean isOnRoutine = false;

    // There is only one Vl for this patient so current can only be the first one
    if (allViralLoadForPatient.size() == 1) {
      return false;
    }

    // Sorts list of VL obs by obsDatetime in reverse order
    Comparator<Obs> vlComparator =
        new Comparator<Obs>() {
          @Override
          public int compare(Obs obs1, Obs obs2) {
            return obs2.getObsDatetime().compareTo(obs1.getObsDatetime());
          }
        };
    Collections.sort(viralLoadForPatientTakenWithin12Months, vlComparator);
    Collections.sort(allViralLoadForPatient, vlComparator);

    Obs currentObs = viralLoadForPatientTakenWithin12Months.get(0);
    Obs previousObs = allViralLoadForPatient.get(1);

    if (currentObs != null
        && previousObs != null
        && previousObs.getValueNumeric() != null
        && previousObs.getObsDatetime() != null
        && previousObs.getValueNumeric() < 1000
        && currentObs.getObsDatetime() != null
        && previousObs.getObsDatetime().before(currentObs.getObsDatetime())) {

      float monthsSince =
          EptsCalculationUtils.monthsSinceIncludingDaysDiff(
              previousObs.getObsDatetime(), currentObs.getObsDatetime());

      if (criteria.equals(PatientsOnRoutineEnum.ADULTCHILDREN)) {
        isOnRoutine = monthsSince >= CRITERIA2_MONTHS_MIN && monthsSince <= CRITERIA2_MONTHS_MAX;
      } else if (criteria.equals(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT)) {
        isOnRoutine = true;
      }
    }
    if (criteria.equals(PatientsOnRoutineEnum.ADULTCHILDREN)) {
      for (Obs allVls : allViralLoadForPatient) {
        float monthsSince =
            EptsCalculationUtils.monthsSinceIncludingDaysDiff(
                allVls.getObsDatetime(), lastViralLoadDate);
        if (allVls.getValueNumeric() < 1000
            && monthsSince >= CRITERIA2_MONTHS_MIN
            && monthsSince <= CRITERIA2_MONTHS_MAX) {
          isOnRoutine = true;
          break;
        }
      }
    }

    return isOnRoutine;
  }

  private boolean isOnRoutineCriteria1(
      PatientsOnRoutineEnum criteria,
      Date artInitiationDate,
      List<Obs> viralLoadForPatientTakenWithin12Months) {

    for (Obs vlObs : viralLoadForPatientTakenWithin12Months) {
      if (vlObs != null && vlObs.getObsDatetime() != null) {
        Date vlDate = vlObs.getObsDatetime();
        Date threeMonths = EptsCalculationUtils.addMonths(artInitiationDate, 3);
        Date sixMonths = EptsCalculationUtils.addMonths(artInitiationDate, 6);
        Date nineMonths = EptsCalculationUtils.addMonths(artInitiationDate, 9);
        boolean withinAdultLimits =
            vlDate.compareTo(sixMonths) > 0 && vlDate.compareTo(nineMonths) <= 0;
        boolean withinBreastfeedingLimits =
            vlDate.compareTo(threeMonths) > 0 && vlDate.compareTo(sixMonths) <= 0;
        if (criteria.equals(PatientsOnRoutineEnum.ADULTCHILDREN) && withinAdultLimits) {
          return true;
        } else if (criteria.equals(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT)
            && withinBreastfeedingLimits) {
          return true;
        }
      }
    }
    return false;
  }

  private List<Obs> getViralLoadForPatientTakenWithin12Months(
      Date now, Date latestVlLowerDateLimit, List<Obs> vLoadList) {
    List<Obs> viralLoadForPatientTakenWithin12Months = new ArrayList<>();
    // populate viralLoadForPatientTakenWithin12Months with obs which fall within the 12month window
    for (Obs obs : vLoadList) {
      if (obs != null
          && obs.getObsDatetime().after(latestVlLowerDateLimit)
          && obs.getObsDatetime().before(now)) {
        viralLoadForPatientTakenWithin12Months.add(obs);
      }
    }
    return viralLoadForPatientTakenWithin12Months;
  }

  private List<Concept> getSecondLineTreatmentArvs(HivMetadata hivMetadata) {
    List<Concept> secondLineArvs = new ArrayList<Concept>();
    secondLineArvs.add(hivMetadata.getAzt3tcAbcEfvConcept());
    secondLineArvs.add(hivMetadata.getD4t3tcAbcEfvConcept());
    secondLineArvs.add(hivMetadata.getAzt3tcAbcLpvConcept());
    secondLineArvs.add(hivMetadata.getD4t3tcAbcLpvConcept());
    secondLineArvs.add(hivMetadata.getAztDdiLpvConcept());
    secondLineArvs.add(hivMetadata.getTdf3tcEfvConcept());
    secondLineArvs.add(hivMetadata.getAzt3tcLpvConcept());
    secondLineArvs.add(hivMetadata.getAbc3tcEfvConcept());
    secondLineArvs.add(hivMetadata.getAbc3tcNvpConcept());
    secondLineArvs.add(hivMetadata.getAbc3tcLpvConcept());
    secondLineArvs.add(hivMetadata.getTdf3tcLpvConcept());
    return secondLineArvs;
  }
}
