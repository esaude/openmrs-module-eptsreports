package org.openmrs.module.eptsreports.reporting.calculation.trfin;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.MaxLastDateFromFilaSeguimentoRecepcaoCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.QueryDisaggregationProcessor;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TxMLPatientDisagregationProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TrfInPatientsWhoAreSilentTransferedInCalculation extends BaseFghCalculation {

  @SuppressWarnings("unchecked")
  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getParameterValues().get("location");
    Date startDate = (Date) context.getParameterValues().get("startDate");
    Map<String, Object> parameters = new HashMap<>();

    parameters.put("startDate", DateUtil.adjustDate(startDate, -1, DurationUnit.DAYS));
    parameters.put("endDate", DateUtil.adjustDate(startDate, -1, DurationUnit.DAYS));
    parameters.put("location", location);

    EvaluationContext newEvaluationContext = getNewEvaluationContext(parameters);

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    QueryDisaggregationProcessor queryDisaggregation =
        Context.getRegisteredComponents(QueryDisaggregationProcessor.class).get(0);

    Map<Integer, Date> transferedOutByProgram =
        queryDisaggregation.findMapMaxPatientStateDateByProgramAndPatientStateAndEndDate(
            newEvaluationContext,
            hivMetadata.getARTProgram(),
            hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState());

    Map<Integer, Date> transferrdOutInFichaClinica =
        queryDisaggregation
            .findMapMaxEncounterDatetimeByEncountersAndQuestionsAndAnswerAndEndOfReportingPeriod(
                newEvaluationContext,
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                    hivMetadata.getStateOfStayOfArtPatient().getConceptId()),
                Arrays.asList(hivMetadata.getTransferOutToAnotherFacilityConcept().getConceptId()));

    Map<Integer, Date> transferredOutInFichaResumo =
        queryDisaggregation
            .findMapMaxObsDatetimeByEncountersAndQuestionsAndAnswerAndEndOfReportingPeriod(
                newEvaluationContext,
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                    hivMetadata.getStateOfStayOfArtPatient().getConceptId()),
                hivMetadata.getTransferOutToAnotherFacilityConcept());

    Map<Integer, Date> maxResultFromAllSources =
        CalculationProcessorUtils.getMaxMapDateByPatient(
            transferedOutByProgram, transferrdOutInFichaClinica, transferredOutInFichaResumo);

    // Excluir todos pacientes com consulta ou levantamento apos terem sido marcados
    // como transferidos para
    CalculationResultMap possiblePatientsToExclude =
        Context.getRegisteredComponents(MaxLastDateFromFilaSeguimentoRecepcaoCalculation.class)
            .get(0)
            .evaluate(parameterValues, newEvaluationContext);

    for (Integer patientId : maxResultFromAllSources.keySet()) {
      Date candidateDate = maxResultFromAllSources.get(patientId);

      if (!TxMLPatientDisagregationProcessor.hasDatesGreatherThanEvaluatedDateToExclude(
          patientId, candidateDate, possiblePatientsToExclude)) {
        resultMap.put(patientId, new SimpleResult(candidateDate, this));
      }
    }
    return resultMap;
  }

  private EvaluationContext getNewEvaluationContext(Map<String, Object> parameters) {
    EvaluationContext context = new EvaluationContext();
    for (Entry<String, Object> entry : parameters.entrySet()) {
      context.addParameterValue(entry.getKey(), entry.getValue());
    }
    return context;
  }
}
