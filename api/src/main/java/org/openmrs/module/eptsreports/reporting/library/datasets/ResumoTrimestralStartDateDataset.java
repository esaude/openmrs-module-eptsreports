package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.Map;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class ResumoTrimestralStartDateDataset extends EvaluatableDataSetDefinition {

  public ResumoTrimestralStartDateDataset() {
    addParameter(new Parameter("year", "Year", Integer.class));
    addParameter(new Parameter("quarter", "Quarter", EptsQuarterlyCohortDefinition.Quarter.class));
  }

  @Override
  public DataSet evaluate(EvaluationContext context) {
    Integer year = (Integer) context.getParameterValue("year");
    EptsQuarterlyCohortDefinition.Quarter quarter =
        (EptsQuarterlyCohortDefinition.Quarter) context.getParameterValue("quarter");
    MapDataSet dataSet = new MapDataSet(this, context);
    DataSetColumn dataSetColumn = new DataSetColumn("startDate", "startDate", Date.class);
    dataSet.addData(dataSetColumn, getStartDate(year, quarter));
    return dataSet;
  }

  private Date getStartDate(Integer year, EptsQuarterlyCohortDefinition.Quarter quarter) {
    Map<String, Date> periodDates = DateUtil.getPeriodDates(year, quarter.ordinal() + 1, null);
    return periodDates.get("startDate");
  }
}
