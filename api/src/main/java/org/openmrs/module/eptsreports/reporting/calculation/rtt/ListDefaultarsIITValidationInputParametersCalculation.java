package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.APIException;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class ListDefaultarsIITValidationInputParametersCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    Integer minDelayDays = (Integer) context.getParameterValues().get("minDelayDays");

    Integer maxDelayDays = (Integer) context.getParameterValues().get("maxDelayDays");

    Date endDate = (Date) context.getParameterValues().get("endDate");

    if (minDelayDays < 3) {
      throw new APIException(
          String.format(
              "Valor do parâmetro 'Minimum Days of Delay' invalido ('%s'). O mínimo de dias de Atraso deve ser maior ou igual a 3",
              minDelayDays));
    }

    if (minDelayDays >= maxDelayDays) {
      throw new APIException(
          String.format(
              "O mínimo de dias de Atraso '%s' deve ser menor que máximo de dias de atraso '%s' ",
              minDelayDays, maxDelayDays));
    }

    if (endDate.compareTo(Calendar.getInstance().getTime()) > 0) {
      throw new APIException(
          String.format(
              "Valor do parâmetro 'End date' ('%s') não deve ser maior que a data actual",
              endDate));
    }
    return new CalculationResultMap();
  }
}
