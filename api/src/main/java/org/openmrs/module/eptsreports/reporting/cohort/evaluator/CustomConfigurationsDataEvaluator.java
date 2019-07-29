package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.Date;
import java.util.List;
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

    SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

    DataSetRow row = new DataSetRow();
    row.addColumnValue(
        new DataSetColumn("report_time", "Report time", String.class),
        EptsReportUtils.formatDateWithTime(new Date()));
    row.addColumnValue(
        new DataSetColumn("states", "States", String.class), getWorkflowStates(context));
    dataSet.addRow(row);
    return dataSet;
  }

  private String getWorkflowStates(EvaluationContext context) {
    List<ProgramWorkflowState> workflowStateList =
        (List<ProgramWorkflowState>) context.getParameterValue("state");
    StringBuilder statesAsString = new StringBuilder();
    String value = "";
    for (ProgramWorkflowState workflowState : workflowStateList) {
      value = workflowState.getConcept().getDisplayString();
      statesAsString =
          statesAsString.length() > 0
              ? statesAsString.append(",").append(value)
              : statesAsString.append(value);
    }
    return statesAsString.toString();
  }
}
