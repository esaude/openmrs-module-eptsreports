package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoTrimestralCustomConfigurationsDataDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = ResumoTrimestralCustomConfigurationsDataDefinition.class)
public class ResumoTrimestralCustomConfigurationsDataEvaluator implements DataSetEvaluator {

  @Override
  public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context)
      throws EvaluationException {

    ResumoTrimestralCustomConfigurationsDataDefinition dsd =
        (ResumoTrimestralCustomConfigurationsDataDefinition) dataSetDefinition;

    SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

    DataSetRow row = new DataSetRow();
    row.addColumnValue(
        new DataSetColumn("report_time", "Report time", String.class),
        EptsReportUtils.formatDateWithTime(new Date()));
    row.addColumnValue(new DataSetColumn("year", "Year", String.class), getYear(context));
    row.addColumnValue(
        new DataSetColumn("quarter", "Quarter", String.class), getReportingPeriodRange(context));
    dataSet.addRow(row);
    return dataSet;
  }

  private String getYear(EvaluationContext context) {
    String year = (String) context.getParameterValue("year");
    return year;
  }

  private String getReportingPeriodRange(EvaluationContext context) {
    String quarter = (String) context.getParameterValue("quarter");
    return quarter;
  }
}
