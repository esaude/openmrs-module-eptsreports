package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class DatimExtractCalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    String datimCode = this.getDatimCode(context);

    resultMap.put(1, new SimpleResult(datimCode, this));
    return resultMap;
  }

  public String getDatimCode(EvaluationContext context) {

    Location location = (Location) context.getParameterValues().get("location");

    String query =
        "select value_reference from location_attribute inner join location on location_attribute.location_id = location.location_id "
            + " where location_attribute.attribute_type_id =2  and location.uuid = '%s' ";

    final SqlQueryBuilder queryBuilder =
        new SqlQueryBuilder(String.format(query, location.getUuid()), context.getParameterValues());

    List<Object[]> queryResult =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToList(queryBuilder, context);

    if (queryResult.isEmpty() || queryResult.size() > 1) {
      return StringUtils.EMPTY;
    }
    return (String) queryResult.get(0)[0];
  }
}
