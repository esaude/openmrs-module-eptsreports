package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientState;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

/**
 * Calculates for patient eligibility to be breastfeeding
 *
 * @return CalculationResultMap
 */
@Component
public class BreastfeedingCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");

    HivMetadata hivMetadata = getHivMetadata();

    Concept viralLoadConcept = hivMetadata.getHivViralLoadConcept();
    EncounterType labEncounterType = hivMetadata.getMisauLaboratorioEncounterType();
    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();

    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    Concept yes = hivMetadata.getYesConcept();
    Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
    Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();
    Date oneYearBefore = EptsCalculationUtils.addMonths(context.getNow(), -12);

    // get female patients only
    Set<Integer> femaleCohort = EptsCalculationUtils.female(cohort, context);

    CalculationResultMap lactatingMap =
        EptsCalculations.getObs(
            breastfeedingConcept,
            femaleCohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.LAST,
            null,
            context);

    CalculationResultMap criteriaHivStartMap =
        EptsCalculations.getObs(
            criteriaForHivStart,
            femaleCohort,
            Arrays.asList(location),
            Arrays.asList(breastfeedingConcept),
            TimeQualifier.FIRST,
            null,
            context);

    CalculationResultMap deliveryDateMap =
        EptsCalculations.getObs(
            priorDeliveryDate,
            femaleCohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap patientStateMap =
        EptsCalculations.allPatientStates(
            femaleCohort,
            location,
            this.getHivMetadata().getPatientIsBreastfeedingWorkflowState(),
            context);

    CalculationResultMap lastVl =
        EptsCalculations.lastObs(
            Arrays.asList(labEncounterType, adultFollowup, childFollowup),
            viralLoadConcept,
            location,
            oneYearBefore,
            context.getNow(),
            femaleCohort,
            context);

    for (Integer pId : femaleCohort) {

      Boolean isCandidate = false;

      Obs lastVlObs = EptsCalculationUtils.obsResultForPatient(lastVl, pId);

      if (lastVlObs != null && lastVlObs.getObsDatetime() != null) {
        Date lastVlDate = lastVlObs.getObsDatetime();

        ListResult patientResult = (ListResult) patientStateMap.get(pId);

        Obs lactattingObs = EptsCalculationUtils.obsResultForPatient(lactatingMap, pId);
        Obs criteriaHivObs = EptsCalculationUtils.obsResultForPatient(criteriaHivStartMap, pId);
        ListResult deliveryDateResult = (ListResult) deliveryDateMap.get(pId);
        List<Obs> deliveryDateObsList =
            EptsCalculationUtils.extractResultValues(deliveryDateResult);
        List<PatientState> patientStateList =
            EptsCalculationUtils.extractResultValues(patientResult);

        if (isLactating(lastVlDate, lactattingObs)
            || hasHIVStartDate(lastVlDate, criteriaHivObs)
            || (hasDeliveryDate(lastVlDate, deliveryDateObsList))
            || isBreastFeedingInProgram(lastVlDate, patientStateList)) {

          isCandidate = true;
        }
      }
      resultMap.put(pId, new BooleanResult(isCandidate, this));
    }
    return resultMap;
  }

  private boolean hasDeliveryDate(Date lastVlDate, List<Obs> deliveryDateObsList) {

    for (Obs deliverDateObs : deliveryDateObsList) {
      if (deliverDateObs.getValueDatetime() != null
          && isInBreastFeedingnViralLoadRange(lastVlDate, deliverDateObs.getValueDatetime())) {
        return true;
      }
    }
    return false;
  }

  private boolean isLactating(Date lastVlDate, Obs lactantObs) {

    if (lactantObs != null
        && isInBreastFeedingnViralLoadRange(
            lastVlDate, lactantObs.getEncounter().getEncounterDatetime())) {
      return true;
    }
    return false;
  }

  private boolean hasHIVStartDate(Date lastVlDate, Obs hivStartDateObs) {

    if (hivStartDateObs != null
        && isInBreastFeedingnViralLoadRange(
            lastVlDate, hivStartDateObs.getEncounter().getEncounterDatetime())) {
      return true;
    }
    return false;
  }

  private boolean isBreastFeedingInProgram(Date lastVlDate, List<PatientState> patientStateList) {

    if (!patientStateList.isEmpty()) {
      for (PatientState patientState : patientStateList) {
        if (isInBreastFeedingnViralLoadRange(lastVlDate, patientState.getStartDate())) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isInBreastFeedingnViralLoadRange(Date viralLoadDate, Date breastFeedingDate) {

    Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -18);
    return breastFeedingDate.compareTo(startDate) >= 0
        && breastFeedingDate.compareTo(viralLoadDate) <= 0;
  }
}
