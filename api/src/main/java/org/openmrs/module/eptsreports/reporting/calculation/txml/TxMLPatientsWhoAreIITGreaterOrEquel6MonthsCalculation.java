package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.LastRecepcaoLevantamentoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.MaxLastDateFromFilaSeguimentoRecepcaoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.QueryDisaggregationProcessor;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TxMLPatientDisagregationProcessor;
import org.openmrs.module.eptsreports.reporting.utils.EptsDateUtil;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxMLPatientsWhoAreIITGreaterOrEquel6MonthsCalculation extends TxMLPatientCalculation {

  private static int GREATHER_THAN_6_MONTHS = 180;
  private static int DAYS_TO_LTFU = 28;

  @Autowired private QueryDisaggregationProcessor disaggregationProcessor;

  @Autowired private HivMetadata hivMetadata;

  @Override
  protected CalculationResultMap evaluateUsingCalculationRules(
      Map<String, Object> parameterValues,
      EvaluationContext context,
      Set<Integer> cohort,
      Date startDate,
      Date endDate,
      CalculationResultMap resultMap,
      CalculationResultMap inicioRealResult,
      CalculationResultMap lastFilaCalculationResult,
      CalculationResultMap lastSeguimentoCalculationResult,
      CalculationResultMap nextFilaResult,
      CalculationResultMap nextSeguimentoResult,
      CalculationResultMap lastRecepcaoLevantamentoResult,
      LastRecepcaoLevantamentoCalculation lastRecepcaoLevantamentoCalculation) {

    for (Integer patientId : cohort) {
      Date inicioRealDate = (Date) inicioRealResult.get(patientId).getValue();
      Date maxNextDate =
          CalculationProcessorUtils.getMaxDate(
              patientId,
              nextFilaResult,
              nextSeguimentoResult,
              TxMLPatientsWhoMissedNextApointmentCalculation.getLastRecepcaoLevantamentoPlus30(
                  patientId, lastRecepcaoLevantamentoResult, lastRecepcaoLevantamentoCalculation));

      if (maxNextDate != null) {

        Date nextDatePlus28 = CalculationProcessorUtils.adjustDaysInDate(maxNextDate, DAYS_TO_LTFU);

        if (nextDatePlus28.compareTo(CalculationProcessorUtils.adjustDaysInDate(startDate, -1)) >= 0
            && nextDatePlus28.compareTo(endDate) < 0
            && EptsDateUtil.getDaysBetween(inicioRealDate, nextDatePlus28)
                >= GREATHER_THAN_6_MONTHS) {
          resultMap.put(patientId, new SimpleResult(maxNextDate, this));
        }
      } else {
        super.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, endDate, lastFilaCalculationResult, nextFilaResult);
        super.checkConsultationsOrFilaWithoutNextConsultationDate(
            patientId, resultMap, endDate, lastSeguimentoCalculationResult, nextSeguimentoResult);
      }
    }

    Location location = (Location) context.getParameterValues().get("location");
    Map<String, Object> parameters = new HashMap<>();

    parameters.put("startDate", DateUtil.adjustDate(startDate, -3, DurationUnit.MONTHS));
    parameters.put("endDate", DateUtil.adjustDate(endDate, -3, DurationUnit.MONTHS));
    parameters.put("location", location);

    EvaluationContext newContext = this.getNewEvaluationContext(parameters);

    this.excludeDeadFromPreviousReportingPeriod(newContext, resultMap);

    return resultMap;
  }

  private void excludeDeadFromPreviousReportingPeriod(
      EvaluationContext context, CalculationResultMap numerator) {

    Map<Integer, Date> patientsDeadInArtProgram =
        this.disaggregationProcessor
            .findMapMaxPatientStateDateByProgramAndPatientStateAndPatientStateEndDateNullAndEndDate(
                context,
                this.hivMetadata.getARTProgram(),
                hivMetadata.getPatientHasDiedWorkflowState());

    Map<Integer, Date> deadInHomeVisitForm =
        this.disaggregationProcessor.findMapMaxObsDatetimeByEncounterAndQuestionsAndAnswersInPeriod(
            context,
            this.hivMetadata.getBuscaActivaEncounterType(),
            Arrays.asList(
                this.hivMetadata.getReasonPatientNotFound().getConceptId(),
                this.hivMetadata.getReasonPatientNotFoundByActivistSecondVisit().getConceptId(),
                this.hivMetadata.getReasonPatientNotFoundByActivistThirdVisit().getConceptId()),
            Arrays.asList(this.hivMetadata.getPatientHasDiedConcept().getConceptId()));

    Map<Integer, Date> deadInDemographicModule =
        this.disaggregationProcessor.findPatientAndDateInDemographicModule(context);

    Map<Integer, Date> deadFichaClinica =
        this.disaggregationProcessor
            .findMapMaxEncounterDatetimeByEncountersAndQuestionsAndAnswerAndEndOfReportingPeriod(
                context,
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                    this.hivMetadata.getStateOfStayOfArtPatient().getConceptId()),
                Arrays.asList(this.hivMetadata.getPatientHasDiedConcept().getConceptId()));

    Map<Integer, Date> deadFichaResumo =
        this.disaggregationProcessor
            .findMapMaxObsDatetimeByEncountersAndQuestionsAndAnswerAndEndOfReportingPeriod(
                context,
                this.hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                    this.hivMetadata.getStateOfStayOfArtPatient().getConceptId()),
                this.hivMetadata.getPatientHasDiedConcept());

    deadInHomeVisitForm =
        TxMLPatientCalculation.excludeEarlyHomeVisitDatesFromNextExpectedDateNumerator(
            numerator, deadInHomeVisitForm);

    @SuppressWarnings("unchecked")
    Map<Integer, Date> maxResultFromAllSources =
        CalculationProcessorUtils.getMaxMapDateByPatient(
            patientsDeadInArtProgram,
            deadInHomeVisitForm,
            deadInDemographicModule,
            deadFichaClinica,
            deadFichaResumo);

    // Excluir pacientes q fizeram consulta/levantamento apos serem marcados como
    // mortos
    CalculationResultMap possiblePatientsToExclude =
        Context.getRegisteredComponents(MaxLastDateFromFilaSeguimentoRecepcaoCalculation.class)
            .get(0)
            .evaluate(context.getParameterValues(), context);

    CalculationResultMap allExclusions = new CalculationResultMap();

    for (Integer patientId : maxResultFromAllSources.keySet()) {
      allExclusions.put(patientId, new BooleanResult(Boolean.TRUE, this));

      Date candidateDate = maxResultFromAllSources.get(patientId);
      if (TxMLPatientDisagregationProcessor.hasDatesGreatherThanEvaluatedDateToExclude(
          patientId, candidateDate, possiblePatientsToExclude)) {
        allExclusions.remove(patientId);
      }
    }

    for (Entry<Integer, CalculationResult> entry : allExclusions.entrySet()) {
      numerator.remove(entry.getKey());
    }
  }

  private EvaluationContext getNewEvaluationContext(Map<String, Object> parameters) {
    EvaluationContext context = new EvaluationContext();
    for (Entry<String, Object> entry : parameters.entrySet()) {
      context.addParameterValue(entry.getKey(), entry.getValue());
    }
    return context;
  }
}
