package org.openmrs.module.eptsreports.reporting.calculation.trfin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.TRFINQueryProcessor;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class TRFINPatientsWhoAreTransferedInCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    TRFINQueryProcessor trfInQueryProcessor =
        Context.getRegisteredComponents(TRFINQueryProcessor.class).get(0);

    Map<Integer, Date> allTransferredIn =
        trfInQueryProcessor.findPatientsWhoAreTransferredInWithinReportingPeriod(context);

    CalculationResultMap silentTransfersIn =
        Context.getRegisteredComponents(TrfInPatientsWhoAreSilentTransferedInCalculation.class)
            .get(0)
            .evaluate(parameterValues, context);

    for (Entry<Integer, CalculationResult> entry : silentTransfersIn.entrySet()) {

      CalculationResult calculationResult = entry.getValue();
      allTransferredIn.put(entry.getKey(), (Date) calculationResult.getValue());
    }

    List<Integer> consultationsOrDrugPickUpToExcluse =
        trfInQueryProcessor.findPatientsWithoutConsultationOrDrugPickUpWithinReportingPeriod(
            context);

    Map<Integer, Date> result =
        excludeTransferredInWithoutClinicalConsultationOrDrugPickUps(
            allTransferredIn, consultationsOrDrugPickUpToExcluse);

    for (Integer patientId : result.keySet()) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }
    return resultMap;
  }

  private Map<Integer, Date> excludeTransferredInWithoutClinicalConsultationOrDrugPickUps(
      Map<Integer, Date> allTransferredIn, List<Integer> consultationsOrDrugPickUpToExcluse) {

    Map<Integer, Date> result = new HashMap<>();
    for (Entry<Integer, Date> numeratorSet : allTransferredIn.entrySet()) {
      if (!consultationsOrDrugPickUpToExcluse.contains(numeratorSet.getKey())) {
        result.put(numeratorSet.getKey(), numeratorSet.getValue());
      }
    }
    return result;
  }
}
