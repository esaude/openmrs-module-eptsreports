package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.MaxLastDateFromFilaSeguimentoRecepcaoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.QueryDisaggregationProcessor;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TxMLPatientDisagregationProcessor;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TxMLPatientsWhoAreDeadCalculation extends BaseFghCalculation {

  @SuppressWarnings("unchecked")
  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    QueryDisaggregationProcessor queryDisaggregation =
        Context.getRegisteredComponents(QueryDisaggregationProcessor.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    Map<Integer, Date> patientsDeadInArtProgram =
        queryDisaggregation.findMapMaxPatientStateDateByProgramAndPatientStateAndEndDate(
            context, hivMetadata.getARTProgram(), hivMetadata.getPatientHasDiedWorkflowState());

    Map<Integer, Date> deadInHomeVisitForm =
        queryDisaggregation.findMapMaxObsDatetimeByEncounterAndQuestionsAndAnswersInPeriod(
            context,
            hivMetadata.getBuscaActivaEncounterType(),
            Arrays.asList(
                hivMetadata.getReasonPatientNotFound().getConceptId(),
                hivMetadata.getReasonPatientNotFoundByActivistSecondVisit().getConceptId(),
                hivMetadata.getReasonPatientNotFoundByActivistThirdVisit().getConceptId()),
            Arrays.asList(hivMetadata.getPatientHasDiedConcept().getConceptId()));

    Map<Integer, Date> deadInDemographicModule =
        queryDisaggregation.findPatientAndDateInDemographicModule(context);

    Map<Integer, Date> deadFichaClinicaAndFichaResumo =
        queryDisaggregation.findMapMaxObsDatetimeByEncountersAndQuestionsAndAnswerAndEndDate(
            context,
            Arrays.asList(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Arrays.asList(
                hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                hivMetadata.getStateOfStayOfArtPatient().getConceptId()),
            hivMetadata.getPatientHasDiedConcept());

    CalculationResultMap numerator =
        Context.getRegisteredComponents(TxMLPatientsWhoMissedNextApointmentCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    deadInHomeVisitForm =
        TxMLPatientCalculation.excludeEarlyHomeVisitDatesFromNextExpectedDateNumerator(
            numerator, deadInHomeVisitForm);

    Map<Integer, Date> maxResultFromAllSources =
        CalculationProcessorUtils.getMaxMapDateByPatient(
            patientsDeadInArtProgram,
            deadInHomeVisitForm,
            deadInDemographicModule,
            deadFichaClinicaAndFichaResumo);

    // Excluir pacientes q fizeram consulta/levantamento apos serem marcados como
    // mortos
    CalculationResultMap possiblePatientsToExclude =
        Context.getRegisteredComponents(MaxLastDateFromFilaSeguimentoRecepcaoCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    for (Integer patientId : maxResultFromAllSources.keySet()) {

      Date candidateDate = maxResultFromAllSources.get(patientId);
      if (!TxMLPatientDisagregationProcessor.hasDatesGreatherThanEvaluatedDateToExclude(
          patientId, candidateDate, possiblePatientsToExclude)) {
        resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
      }
    }
    return resultMap;
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> parameterValues, EvaluationContext context) {
    return this.evaluate(parameterValues, context);
  }
}
