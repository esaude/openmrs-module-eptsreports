package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Set;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class CustomConfigurationsDataDefinition extends BaseDataSetDefinition {

  public static final long serialVersionUID = 1L;

  public Set<ProgramWorkflowState> getWorkflowStates() {
    return workflowStates;
  }

  public void setWorkflowStates(Set<ProgramWorkflowState> workflowStates) {
    this.workflowStates = workflowStates;
  }

  @ConfigurationProperty(group = "what")
  private Set<ProgramWorkflowState> workflowStates;
}
