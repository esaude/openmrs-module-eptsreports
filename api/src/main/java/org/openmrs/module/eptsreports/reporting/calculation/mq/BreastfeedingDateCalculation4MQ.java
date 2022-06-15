package org.openmrs.module.eptsreports.reporting.calculation.mq;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientState;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <b>Description</b>Calculates for patient eligibility to be breastfeeding Based on ART start date,
 * the date is obtained by 18 months from last viral date
 *
 * @return CalculationResultMap
 */
@Component
public class BreastfeedingDateCalculation4MQ extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    // External Dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");

    Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType fichaResumoEncounterType = hivMetadata.getMasterCardEncounterType();
    EncounterType fsrEncounterType = hivMetadata.getFsrEncounterType();

    List<EncounterType> encounterTypeList =
        (List<EncounterType>) parameterValues.get("encounterList");

    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    Concept yes = hivMetadata.getYesConcept();
    // Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
    // Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();
    Concept hivViraloadQualitative = hivMetadata.getHivViralLoadQualitative();
    // Concept criteriaForArtStart = hivMetadata.getCriteriaForArtStart();
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Date oneYearBefore = EptsCalculationUtils.addMonths(onOrBefore, -12);
    Concept historicalArtStartDate = hivMetadata.getARVStartDateConcept();
    Concept sampleCollectionDateAndTime = hivMetadata.getSampleCollectionDateAndTime();

    CalculationResultMap lactatingMap =
        ePTSCalculationService.getObs(
            breastfeedingConcept,
            Arrays.asList(adultFollowup),
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);
    CalculationResultMap breastfeedingRegistrationBasedOnValueDateAndEncounter53Map =
        ePTSCalculationService.getObs(
            historicalArtStartDate,
            Arrays.asList(fichaResumoEncounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context.getNow(),
            context);

    CalculationResultMap patientStateMap =
        ePTSCalculationService.allPatientStates(
            cohort, location, hivMetadata.getPatientGaveBirthWorkflowState(), context);

    CalculationResultMap lastVl =
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

    // get calculation map based on breastfeeding on encounter type 53
    CalculationResultMap lactatingBasedOnEncounter53Map =
        ePTSCalculationService.getObs(
            breastfeedingConcept,
            Arrays.asList(fichaResumoEncounterType),
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context.getNow(),
            context);

    CalculationResultMap breastfeedingMap =
        ePTSCalculationService.getObs(
            breastfeedingConcept,
            Arrays.asList(fsrEncounterType),
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap breastfeedingSampleDatetimeMap =
        ePTSCalculationService.getObs(
            sampleCollectionDateAndTime,
            Arrays.asList(fsrEncounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    for (Integer pId : cohort) {
      Obs lastVlObs = EptsCalculationUtils.resultForPatient(lastVl, pId);
      Obs lastVlQualitativeObs = EptsCalculationUtils.resultForPatient(lastHivVlQualitative, pId);
      Date resultantDate =
          getResultantDate(
              lactatingMap,
              patientStateMap,
              pId,
              lastVlObs,
              lastVlQualitativeObs,
              breastfeedingRegistrationBasedOnValueDateAndEncounter53Map,
              lactatingBasedOnEncounter53Map,
              breastfeedingMap,
              breastfeedingSampleDatetimeMap,
              yes,
              onOrBefore);
      resultMap.put(pId, new SimpleResult(resultantDate, this));
    }
    return resultMap;
  }

  private Date getResultantDate(
      CalculationResultMap lactatingMap,
      CalculationResultMap patientStateMap,
      Integer pId,
      Obs lastVlObs,
      Obs lastVlQualitative,
      CalculationResultMap registeredBreastfeedingValueDate53,
      CalculationResultMap registeredBreastfeedingEncounterType53,
      CalculationResultMap breatfeedingMap,
      CalculationResultMap breatfeedingSampleDatetimeMap,
      Concept yes,
      Date endDate) {
    Date resultantDate = null;
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

      ListResult patientResult = (ListResult) patientStateMap.get(pId);

      ListResult lactatingResults = (ListResult) lactatingMap.get(pId);
      List<Obs> lactatingObs = EptsCalculationUtils.extractResultValues(lactatingResults);

      List<PatientState> patientStateList = EptsCalculationUtils.extractResultValues(patientResult);

      ListResult registeredBreastfeedingListResultsBasedOnValueDate53 =
          (ListResult) registeredBreastfeedingValueDate53.get(pId);
      List<Obs> registeredBreastfeedingListResultsListBasedOnValueDate53 =
          EptsCalculationUtils.extractResultValues(
              registeredBreastfeedingListResultsBasedOnValueDate53);
      ListResult getRegisteredBreastFeedingBasedOnEncounter53ListResults =
          (ListResult) registeredBreastfeedingEncounterType53.get(pId);
      List<Obs> getRegisteredBreastFeedingBasedOnEncounter53ObsList =
          EptsCalculationUtils.extractResultValues(
              getRegisteredBreastFeedingBasedOnEncounter53ListResults);

      ListResult breastfeedingFsrResults = (ListResult) breatfeedingMap.get(pId);
      List<Obs> breastfeedingFsrResultsObs =
          EptsCalculationUtils.extractResultValues(breastfeedingFsrResults);

      ListResult breastfeedingFSampleDatetimesrResults =
          (ListResult) breatfeedingSampleDatetimeMap.get(pId);
      List<Obs> breastfeedingFsrResultsSampleDateTimeObs =
          EptsCalculationUtils.extractResultValues(breastfeedingFSampleDatetimesrResults);

      // get a list of all eligible dates
      List<Date> allEligibleDates =
          Arrays.asList(
              this.isLactating(lastVlDate, lactatingObs),
              this.isInBreastFeedingInProgram(lastVlDate, patientStateList),
              this.getBreastFeedingRegistrationDate(
                  lastVlDate,
                  registeredBreastfeedingListResultsListBasedOnValueDate53,
                  getRegisteredBreastFeedingBasedOnEncounter53ObsList),
              this.isBreastFeedingWithFsr(
                  lastVlDate,
                  breastfeedingFsrResultsObs,
                  yes,
                  breastfeedingFsrResultsSampleDateTimeObs,
                  endDate));

      // have a resultant list of dates
      List<Date> resultantList = new ArrayList<>();
      if (allEligibleDates.size() > 0) {
        for (Date breastfeedingDate : allEligibleDates) {
          if (breastfeedingDate != null) {
            resultantList.add(breastfeedingDate);
          }
        }
      }
      if (resultantList.size() > 0) {
        Collections.sort(resultantList);
        // then pick the most recent entry, which is the last one
        resultantDate = resultantList.get(resultantList.size() - 1);
      }
    }
    return resultantDate;
  }

  private Date hasDeliveryDate(Date lastVlDate, List<Obs> deliveryDateObsList) {
    Date deliveryDate = null;
    for (Obs deliverDateObs : deliveryDateObsList) {
      if (deliverDateObs.getValueDatetime() != null
          && this.isInBreastFeedingViralLoadRange(lastVlDate, deliverDateObs.getValueDatetime())) {
        deliveryDate = deliverDateObs.getValueDatetime();
      }
    }
    return deliveryDate;
  }

  private Date isLactating(Date lastVlDate, List<Obs> lactantObsList) {
    Date lactatingDate = null;
    for (Obs lactantObs : lactantObsList) {
      if (lactantObs.getObsDatetime() != null
          && this.isInBreastFeedingViralLoadRange(
              lastVlDate, lactantObs.getEncounter().getEncounterDatetime())) {
        lactatingDate = lactantObs.getEncounter().getEncounterDatetime();
      }
    }
    return lactatingDate;
  }

  private Date hasHIVStartDate(Date lastVlDate, Obs hivStartDateObs) {
    Date hivStartDate = null;
    if (hivStartDateObs != null
        && this.isInBreastFeedingViralLoadRange(
            lastVlDate, hivStartDateObs.getEncounter().getEncounterDatetime())) {
      hivStartDate = hivStartDateObs.getEncounter().getEncounterDatetime();
    }
    return hivStartDate;
  }

  private Date isInBreastFeedingInProgram(Date lastVlDate, List<PatientState> patientStateList) {
    Date inProgramDate = null;
    if (!patientStateList.isEmpty()) {
      for (PatientState patientState : patientStateList) {
        if (this.isInBreastFeedingViralLoadRange(lastVlDate, patientState.getStartDate())) {
          inProgramDate = patientState.getStartDate();
        }
      }
    }
    return inProgramDate;
  }

  private boolean isInBreastFeedingViralLoadRange(Date viralLoadDate, Date breastFeedingDate) {

    Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -18);
    return breastFeedingDate.compareTo(startDate) >= 0
        && breastFeedingDate.compareTo(viralLoadDate) <= 0;
  }

  private Date getWhenOnARTWhileBpostive(Date lastVlDate, List<Obs> pregnantWithLastDateObsList) {
    Date requiredDate = null;
    for (Obs obs : pregnantWithLastDateObsList) {
      if (this.isInBreastFeedingViralLoadRange(
          lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        requiredDate = obs.getEncounter().getEncounterDatetime();
      }
    }

    return requiredDate;
  }

  private Date getBreastFeedingRegistrationDate(
      Date lastVlDate,
      List<Obs> breastfeedingRegistrationValueDateList,
      List<Obs> registeredBreastfeeding) {
    Date requiredDate = null;
    for (Obs breastFeedingRegistered : registeredBreastfeeding) {
      for (Obs breastfeedingValueDate : breastfeedingRegistrationValueDateList) {
        if (breastFeedingRegistered.getPerson().equals(breastfeedingValueDate.getPerson())
            && breastFeedingRegistered.getEncounter().equals(breastfeedingValueDate.getEncounter())
            && isInBreastFeedingViralLoadRange(
                lastVlDate, breastfeedingValueDate.getValueDatetime())) {
          requiredDate = breastfeedingValueDate.getValueDatetime();
          break;
        }
      }
    }
    return requiredDate;
  }

  private Date isBreastFeedingWithFsr(
      Date lastVlDate,
      List<Obs> breastfeeding,
      Concept yes,
      List<Obs> sampleCollectionDate,
      Date endDate) {
    Date isBreastfeedingWihFsrDate = null;
    for (Obs obsYes : breastfeeding) {
      for (Obs date : sampleCollectionDate) {
        if (obsYes.getEncounter().equals(date.getEncounter())
            && obsYes.getValueCoded().equals(yes)
            && date.getValueDatetime().compareTo(endDate) <= 0
            && (this.isInBreastFeedingViralLoadRange(lastVlDate, date.getValueDatetime()))) {
          isBreastfeedingWihFsrDate = date.getValueDatetime();
          break;
        }
      }
    }
    return isBreastfeedingWihFsrDate;
  }
}
