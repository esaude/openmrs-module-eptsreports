package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Arrays;
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
public class TxMLPatientsWhoRefusedOrStoppedTreatmentCalculation extends BaseFghCalculation {

  @SuppressWarnings("unchecked")
  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    QueryDisaggregationProcessor queryDisaggregation =
        Context.getRegisteredComponents(QueryDisaggregationProcessor.class).get(0);

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CalculationResultMap numerator =
        Context.getRegisteredComponents(TxMLPatientsWhoMissedNextApointmentCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    Map<Integer, Date> deadInHomeVisitForm =
        queryDisaggregation.findMapMaxObsDatetimeByEncounterAndQuestionsAndAnswersInPeriod(
            context,
            hivMetadata.getBuscaActivaEncounterType(),
            Arrays.asList(hivMetadata.getDefaultingMotiveConcept().getConceptId()),
            Arrays.asList(
                hivMetadata.getPatientForgotVisitDate().getConceptId(),
                hivMetadata.getPatientIsBedriddenAtHome().getConceptId(),
                hivMetadata.getDistanceOrMoneyForTransportIsToMuchForPatient().getConceptId(),
                hivMetadata.getPatientIsDissatisfiedWithDayHospitalServices().getConceptId(),
                hivMetadata.getFearOfTheProvider().getConceptId(),
                hivMetadata.getAbsenceOfHealthProviderInHealthUnit().getConceptId(),
                hivMetadata.getAdverseReaction().getConceptId(),
                hivMetadata.getPatientIsTreatingHivWithTradittionalMedicine().getConceptId(),
                hivMetadata.getOtherReasonWhyPatientMissedVisit().getConceptId()));

    deadInHomeVisitForm =
        TxMLPatientCalculation.excludeEarlyHomeVisitDatesFromNextExpectedDateNumerator(
            numerator, deadInHomeVisitForm);

    Map<Integer, Date> maxResultFromAllSources =
        CalculationProcessorUtils.getMaxMapDateByPatient(deadInHomeVisitForm);

    // Excluir todos pacientes que fizeram consulta/levantamento apos serem marcados
    // como Stopped/Transfered out
    CalculationResultMap possiblePatientsToExclude =
        Context.getRegisteredComponents(MaxLastDateFromFilaSeguimentoRecepcaoCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    CalculationResultMap deadExclusion =
        Context.getRegisteredComponents(TxMLPatientsWhoAreDeadCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);
    CalculationResultMap transferedOutExclusion =
        Context.getRegisteredComponents(TxMLPatientsWhoAreTransferedOutCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    for (Integer patientId : maxResultFromAllSources.keySet()) {

      Date candidateDate = maxResultFromAllSources.get(patientId);

      if (!(TxMLPatientDisagregationProcessor.hasPatientsFromOtherDisaggregationToExclude(
              patientId, deadExclusion, transferedOutExclusion)
          || TxMLPatientDisagregationProcessor.hasDatesGreatherThanEvaluatedDateToExclude(
              patientId, candidateDate, possiblePatientsToExclude))) {
        resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
      }
    }
    return resultMap;
  }
}
