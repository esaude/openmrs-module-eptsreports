package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import java.util.Set;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CustomConfigurationsDataDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = CustomConfigurationsDataDefinition.class)
public class CustomConfigurationsDataEvaluator implements DataSetEvaluator {

  @Override
  public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context)
      throws EvaluationException {

    CustomConfigurationsDataDefinition dsd = (CustomConfigurationsDataDefinition) dataSetDefinition;

    Set<ProgramWorkflowState> programWorkflowStateSet = dsd.getWorkflowStates();

    SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

    DataSetRow row = new DataSetRow();
    row.addColumnValue(
        new DataSetColumn("report_time", "Report time", String.class),
        EptsReportUtils.formatDateWithTime(new Date()));
    dataSet.addRow(row);
    return dataSet;
  }
}
