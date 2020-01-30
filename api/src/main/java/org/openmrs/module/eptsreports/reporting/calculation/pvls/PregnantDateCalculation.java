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
 * Calculates for patient eligibility to be pregnant
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
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Date oneYearBefore = EptsCalculationUtils.addMonths(onOrBefore, -12);

    EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType pediatriaFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();

    Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    Concept pregnant = hivMetadata.getPregnantConcept();
    Concept pregnantBasedOnWeeks = hivMetadata.getNumberOfWeeksPregnant();
    Concept pregnancyDueDate = hivMetadata.getPregnancyDueDate();
    Program ptv = hivMetadata.getPtvEtvProgram();
    Concept yes = hivMetadata.getYesConcept();

    CalculationResultMap pregnantMap =
        ePTSCalculationService.getObs(
            pregnant,
            null,
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

    CalculationResultMap markedPregnantInProgram =
        ePTSCalculationService.allProgramEnrollment(ptv, cohort, context);

    CalculationResultMap lastVl =
        ePTSCalculationService.lastObs(
            Arrays.asList(labEncounterType, adultFollowup, pediatriaFollowup),
            viralLoadConcept,
            location,
            oneYearBefore,
            onOrBefore,
            cohort,
            context);

    for (Integer pId : cohort) {
      Obs lastVlObs = EptsCalculationUtils.resultForPatient(lastVl, pId);
      Date requiredDate =
          getRequiredDate(
              location,
              pregnantMap,
              markedPregnantByWeeks,
              markedPregnantDueDate,
              markedPregnantInProgram,
              pId,
              lastVlObs);
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
      Obs lastVlObs) {
    Date requiredDate = null;
    if (lastVlObs != null && lastVlObs.getObsDatetime() != null) {
      Date lastVlDate = lastVlObs.getObsDatetime();

      ListResult pregnantResult = (ListResult) pregnantMap.get(pId);
      ListResult pregnantByWeeksResullt = (ListResult) markedPregnantByWeeks.get(pId);
      ListResult pregnantDueDateResult = (ListResult) markedPregnantDueDate.get(pId);
      ListResult pregnantsInProgramResults = (ListResult) markedPregnantInProgram.get(pId);

      List<Obs> pregnantObsList = EptsCalculationUtils.extractResultValues(pregnantResult);
      List<Obs> pregnantByWeeksObsList =
          EptsCalculationUtils.extractResultValues(pregnantByWeeksResullt);
      List<Obs> pregnantDueDateObsList =
          EptsCalculationUtils.extractResultValues(pregnantDueDateResult);
      List<PatientProgram> patientProgams =
          EptsCalculationUtils.extractResultValues(pregnantsInProgramResults);

      // add a list to contains all the dates that can be sorted and pick the most
      // recent one
      List<Date> allPregnancyDates =
          Arrays.asList(
              isPregnantDate(lastVlDate, pregnantObsList),
              isPregnantByWeeks(lastVlDate, pregnantByWeeksObsList),
              isPregnantDueDate(lastVlDate, pregnantDueDateObsList),
              isPregnantInProgram(lastVlDate, patientProgams, location));
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

  private boolean isInPregnantViralLoadRange(Date viralLoadDate, Date pregnancyDate) {

    Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -9);
    return pregnancyDate.compareTo(startDate) >= 0 && pregnancyDate.compareTo(viralLoadDate) <= 0;
  }
}
