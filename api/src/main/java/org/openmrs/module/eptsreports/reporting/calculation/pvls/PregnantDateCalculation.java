package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

/**
 * <b>Description</b>Calculates for patient eligibility to be pregnant
 *
 * <blockquote>
 *
 * Based on ART start date, pregnancy date is calculated based on 9 months from the last viral load
 * date
 *
 * </blockquote>
 *
 * @return CalculationResultMap
 */
@Component
public class PregnantDateCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    // External dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");
    Date onOrAfter = (Date) context.getFromCache("onOrAfter");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Date oneYearBefore = EptsCalculationUtils.addMonths(onOrBefore, -12);

    List<EncounterType> encounterTypeList =
        (List<EncounterType>) parameterValues.get("encounterList");

    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType fichaResumoEncounterType = hivMetadata.getMasterCardEncounterType();
    EncounterType adultInitial = hivMetadata.getARVAdultInitialEncounterType();
    EncounterType fsrEncounterType = hivMetadata.getFsrEncounterType();

    Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    Concept pregnant = hivMetadata.getPregnantConcept();
    Concept pregnantBasedOnWeeks = hivMetadata.getNumberOfWeeksPregnant();
    Concept pregnancyDueDate = hivMetadata.getPregnancyDueDate();
    Program ptv = hivMetadata.getPtvEtvProgram();
    Concept yes = hivMetadata.getYesConcept();
    Concept sampleCollectionDateAndTime = hivMetadata.getSampleCollectionDateAndTime();
    Concept hivViraloadQualitative = hivMetadata.getHivViralLoadQualitative();
    Concept criteriaForArtStart = hivMetadata.getCriteriaForArtStart();
    Concept bPostive = hivMetadata.getBpostiveConcept();
    Concept historicalArtStartDate = hivMetadata.getARVStartDateConcept();

    CalculationResultMap pregnantMap =
        ePTSCalculationService.getObs(
            pregnant,
            Arrays.asList(adultFollowup, adultInitial),
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap pregnantRegistrationOnEncounter53Map =
        ePTSCalculationService.getObs(
            historicalArtStartDate,
            Arrays.asList(fichaResumoEncounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap pregnantOnEncounter53Map =
        ePTSCalculationService.getObs(
            pregnant,
            Arrays.asList(fichaResumoEncounterType),
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantByWeeks =
        ePTSCalculationService.getObs(
            pregnantBasedOnWeeks,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantDueDate =
        ePTSCalculationService.getObs(
            pregnancyDueDate,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);
    CalculationResultMap lastPregnantFsrMap =
        ePTSCalculationService.getObs(
            pregnant,
            Arrays.asList(fsrEncounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap sampleCollectionDateTimeFsrMap =
        ePTSCalculationService.getObs(
            sampleCollectionDateAndTime,
            Arrays.asList(fsrEncounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap startArtBeingBpostiveMap =
        ePTSCalculationService.getObs(
            criteriaForArtStart,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(bPostive),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantInProgram =
        ePTSCalculationService.allProgramEnrollment(ptv, cohort, context);

    CalculationResultMap lastHivVl =
        ePTSCalculationService.lastObs(
            encounterTypeList,
            viralLoadConcept,
            location,
            oneYearBefore,
            onOrBefore,
            cohort,
            context);
    CalculationResultMap lastHivVlQualitative =
        ePTSCalculationService.lastObs(
            encounterTypeList,
            hivViraloadQualitative,
            location,
            oneYearBefore,
            onOrBefore,
            cohort,
            context);

    for (Integer pId : cohort) {

      Obs lastHivVlObs = EptsCalculationUtils.resultForPatient(lastHivVl, pId);
      Obs lastVlQualitativeObs = EptsCalculationUtils.resultForPatient(lastHivVlQualitative, pId);

      ListResult pregnantFsrYesResult = (ListResult) lastPregnantFsrMap.get(pId);
      ListResult pregnantFsrSampleCollectionResult =
          (ListResult) sampleCollectionDateTimeFsrMap.get(pId);
      List<Obs> pregnantFsrYesObsList =
          EptsCalculationUtils.extractResultValues(pregnantFsrYesResult);
      List<Obs> pregnantFsrSampleCollectionObsList =
          EptsCalculationUtils.extractResultValues(pregnantFsrSampleCollectionResult);
      Date requiredDate =
          getRequiredDate(
              location,
              pregnantMap,
              markedPregnantByWeeks,
              markedPregnantDueDate,
              markedPregnantInProgram,
              pId,
              lastHivVlObs,
              lastVlQualitativeObs,
              startArtBeingBpostiveMap,
              pregnantRegistrationOnEncounter53Map,
              pregnantOnEncounter53Map,
              pregnantFsrYesObsList,
              hivMetadata.getYesConcept(),
              pregnantFsrSampleCollectionObsList,
              onOrAfter,
              onOrBefore);
      resultMap.put(pId, new SimpleResult(requiredDate, this));
    }

    return resultMap;
  }

  private Date getRequiredDate(
      Location location,
      CalculationResultMap pregnantMap,
      CalculationResultMap markedPregnantByWeeks,
      CalculationResultMap markedPregnantDueDate,
      CalculationResultMap markedPregnantInProgram,
      Integer pId,
      Obs lastVlObs,
      Obs lastVlQualitative,
      CalculationResultMap artStartWhileBposMap,
      CalculationResultMap registeredPregnantValueDateBasedOnEncounter53,
      CalculationResultMap getRegisteredPregnantBasedOnEncounter53,
      List<Obs> markedAsPregnatFsrYesMap,
      Concept yes,
      List<Obs> markedAsPregnatFsrDateCollectionMap,
      Date startDate,
      Date endDate) {
    Date requiredDate = null;
    // check of the 2 dates passed for viral load and pick the latest
    List<Date> dateListForVl = new ArrayList<>();
    if (lastVlObs != null && lastVlObs.getObsDatetime() != null) {
      dateListForVl.add(lastVlObs.getObsDatetime());
    }
    if (lastVlQualitative != null && lastVlQualitative.getObsDatetime() != null) {
      dateListForVl.add(lastVlQualitative.getObsDatetime());
    }
    if (dateListForVl.size() > 0) {
      Collections.sort(dateListForVl);
      Date lastVlDate = dateListForVl.get(dateListForVl.size() - 1);

      ListResult pregnantResult = (ListResult) pregnantMap.get(pId);
      ListResult pregnantByWeeksResullt = (ListResult) markedPregnantByWeeks.get(pId);
      ListResult pregnantDueDateResult = (ListResult) markedPregnantDueDate.get(pId);
      ListResult pregnantsInProgramResults = (ListResult) markedPregnantInProgram.get(pId);
      ListResult onArtWhileBpos = (ListResult) artStartWhileBposMap.get(pId);
      ListResult registeredPregnantListResultBasedOnEncounter53ValueDate =
          (ListResult) registeredPregnantValueDateBasedOnEncounter53.get(pId);
      ListResult getRegisteredPregnantBasedOnEncounter53ListResults =
          (ListResult) getRegisteredPregnantBasedOnEncounter53.get(pId);

      List<Obs> pregnantObsList = EptsCalculationUtils.extractResultValues(pregnantResult);
      List<Obs> pregnantByWeeksObsList =
          EptsCalculationUtils.extractResultValues(pregnantByWeeksResullt);
      List<Obs> pregnantDueDateObsList =
          EptsCalculationUtils.extractResultValues(pregnantDueDateResult);
      List<PatientProgram> patientProgams =
          EptsCalculationUtils.extractResultValues(pregnantsInProgramResults);
      List<Obs> artWhileBpos = EptsCalculationUtils.extractResultValues(onArtWhileBpos);
      List<Obs> pregRegListBasedOnValueDateAndEncounter53 =
          EptsCalculationUtils.extractResultValues(
              registeredPregnantListResultBasedOnEncounter53ValueDate);
      List<Obs> getRegisteredPregnantBasedOnEncounter53ListResultsObsList =
          EptsCalculationUtils.extractResultValues(
              getRegisteredPregnantBasedOnEncounter53ListResults);

      // add a list to contains all the dates that can be sorted and pick the most recent one
      List<Date> allPregnancyDates =
          Arrays.asList(
              isPregnantDate(lastVlDate, pregnantObsList),
              isPregnantByWeeks(lastVlDate, pregnantByWeeksObsList),
              isPregnantDueDate(lastVlDate, pregnantDueDateObsList),
              isPregnantInProgram(lastVlDate, patientProgams, location),
              isPregnantWithFsr(
                  lastVlDate,
                  markedAsPregnatFsrYesMap,
                  yes,
                  markedAsPregnatFsrDateCollectionMap,
                  startDate,
                  endDate),
              getWhenOnARTWhileBpostive(lastVlDate, artWhileBpos),
              getPregnantRegistrationDateBasedOnEncounter53(
                  lastVlDate,
                  pregRegListBasedOnValueDateAndEncounter53,
                  getRegisteredPregnantBasedOnEncounter53ListResultsObsList));
      // have a resultant list of dates
      List<Date> resultantList = new ArrayList<>();
      if (allPregnancyDates.size() > 0) {
        for (Date eventDate : allPregnancyDates) {
          if (eventDate != null) {
            resultantList.add(eventDate);
          }
        }
      }
      if (resultantList.size() > 0) {
        Collections.sort(resultantList);
        // then pick the most recent entry, which is the last one
        requiredDate = resultantList.get(resultantList.size() - 1);
      }
    }
    return requiredDate;
  }

  private Date isPregnantDate(Date lastVlDate, List<Obs> pregnantObsList) {
    Date pregnancyDate = null;
    for (Obs obs : pregnantObsList) {
      if (this.isInPregnantViralLoadRange(lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        pregnancyDate = obs.getEncounter().getEncounterDatetime();
      }
    }
    return pregnancyDate;
  }

  private Date isPregnantByWeeks(Date lastVlDate, List<Obs> pregnantByWeeksObsList) {
    Date inWeeksDate = null;
    for (Obs obs : pregnantByWeeksObsList) {
      if (obs.getValueNumeric() != null
          && this.isInPregnantViralLoadRange(
              lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        inWeeksDate = obs.getEncounter().getEncounterDatetime();
      }
    }
    return inWeeksDate;
  }

  private Date isPregnantDueDate(Date lastVlDate, List<Obs> pregnantDueDateObsList) {
    Date isPregnancyDueDate = null;
    for (Obs obs : pregnantDueDateObsList) {
      if (this.isInPregnantViralLoadRange(lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        isPregnancyDueDate = obs.getEncounter().getEncounterDatetime();
      }
    }
    return isPregnancyDueDate;
  }

  private Date isPregnantInProgram(
      Date lastVlDate, List<PatientProgram> patientPrograms, Location location) {
    Date inProgramDate = null;
    for (PatientProgram patientProgram : patientPrograms) {
      if (location.equals(patientProgram.getLocation())
          && patientProgram.getDateEnrolled() != null
          && this.isInPregnantViralLoadRange(lastVlDate, patientProgram.getDateEnrolled())) {
        inProgramDate = patientProgram.getDateEnrolled();
      }
    }
    return inProgramDate;
  }

  private Date isPregnantWithFsr(
      Date lastVlDate,
      List<Obs> pregnant,
      Concept yes,
      List<Obs> sampleCollectionDate,
      Date startDate,
      Date endDate) {
    Date isPregnancyWihFsrDate = null;
    for (Obs obsYes : pregnant) {
      for (Obs date : sampleCollectionDate) {
        if (obsYes.getEncounter().equals(date.getEncounter())
            && obsYes.getValueCoded().equals(yes)
            && date.getValueDatetime().compareTo(startDate) >= 0
            && date.getValueDatetime().compareTo(endDate) <= 0
            && (this.isInPregnantViralLoadRange(lastVlDate, date.getValueDatetime()))) {
          isPregnancyWihFsrDate = date.getValueDatetime();
          break;
        }
      }
    }
    return isPregnancyWihFsrDate;
  }

  private Date getWhenOnARTWhileBpostive(Date lastVlDate, List<Obs> pregnantWithLastDateObsList) {
    Date requiredDate = null;
    for (Obs obs : pregnantWithLastDateObsList) {
      if (this.isInPregnantViralLoadRange(lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        requiredDate = obs.getEncounter().getEncounterDatetime();
      }
    }

    return requiredDate;
  }

  private Date getPregnantRegistrationDateBasedOnEncounter53(
      Date lastVlDate,
      List<Obs> pregnantRegistrationValueDateListBasedOnEncounter53,
      List<Obs> registeredPregnancyBasedOnEncounter53) {
    Date requiredDate = null;

    for (Obs pregnantRegistered : registeredPregnancyBasedOnEncounter53) {
      for (Obs pregnantValueDate : pregnantRegistrationValueDateListBasedOnEncounter53) {
        if (pregnantRegistered.getPerson().equals(pregnantValueDate.getPerson())
            && pregnantRegistered.getEncounter().equals(pregnantValueDate.getEncounter())
            && isInPregnantViralLoadRange(lastVlDate, pregnantValueDate.getValueDatetime())) {
          requiredDate = pregnantValueDate.getValueDatetime();
          break;
        }
      }
    }

    return requiredDate;
  }

  private boolean isInPregnantViralLoadRange(Date viralLoadDate, Date pregnancyDate) {

    if (viralLoadDate != null && pregnancyDate != null) {

      Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -9);
      return pregnancyDate.compareTo(startDate) >= 0 && pregnancyDate.compareTo(viralLoadDate) <= 0;
    }
    return false;
  }
}
