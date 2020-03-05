package org.openmrs.module.eptsreports.reporting.calculation.quarterly.indicator;

import java.util.List;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.query.ResumoTrimestralQueries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class ResumoTrimestralQuarterlyIndicatorCalculationB
    extends ResumoTrimestralQuarterlyTotalCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap evaluated = super.evaluate(parameterValues, context);
    CalculationResultMap resultMap = new CalculationResultMap();
    Map<QUARTERLIES, MonthlyDateRange> quarterToExecute = getQuarterToExecute(evaluated);

    QUARTERLIES quarter = quarterToExecute.keySet().iterator().next();
    MonthlyDateRange monthlyDateRange = quarterToExecute.get(quarter);

    if (monthlyDateRange != null
        && monthlyDateRange.getStartDate() != null
        && monthlyDateRange.getEndDate() != null) {

      List<Integer> patientIds =
          ResumoTrimestralQueries.findPatientsWhoAreNewlyEnrolledOnART(context, monthlyDateRange);

      List<Integer> inclusions =
          ResumoTrimestralQueries.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod(
              context, monthlyDateRange);
      inclusions.addAll(
          ResumoTrimestralQueries
              .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard(
                  context, monthlyDateRange));
      patientIds.retainAll(inclusions);
      List<Integer> baseCohortQuery =
          ResumoTrimestralQueries.getBaseCohortQuery(context, monthlyDateRange);
      inclusions.retainAll(baseCohortQuery);
      for (Integer patientId : patientIds) {
        resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
      }
    }
    return resultMap;
  }

  public abstract Map<QUARTERLIES, MonthlyDateRange> getQuarterToExecute(
      CalculationResultMap evaluated);
}
