package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;

public class ProgramsMetadata extends CommonMetadata {
  /** @return the Program that matches the passed uuid, concept name, name, or primary key id */
  public static Program getProgram(String lookup) {
    Program program = Context.getProgramWorkflowService().getProgramByUuid(lookup);
    if (program == null) {
      program = Context.getProgramWorkflowService().getProgramByName(lookup);
    }
    if (program == null) {
      for (Program p : Context.getProgramWorkflowService().getAllPrograms()) {
        if (p.getName().equalsIgnoreCase(lookup)) {
          program = p;
        }
      }
    }
    if (program == null) {
      try {
        program = Context.getProgramWorkflowService().getProgram(Integer.parseInt(lookup));
      } catch (Exception e) {
        // DO NOTHING
      }
    }
    if (program == null) {
      throw new ConfigurableMetadataLookupException("Unable to find program using key: " + lookup);
    }

    return program;
  }

  /** @return the ProgramWorkflow matching the given programLookup and workflowLookup */
  public static ProgramWorkflow getProgramWorkflow(String programLookup, String workflowLookup) {
    Program p = getProgram(programLookup);
    ProgramWorkflow wf = p.getWorkflowByName(workflowLookup);

    if (wf == null) {
      for (ProgramWorkflow programWorkflow : p.getAllWorkflows()) {
        if (workflowLookup.equalsIgnoreCase(programWorkflow.getConcept().getName().toString())) {
          wf = programWorkflow;
        } else if (workflowLookup.equalsIgnoreCase(programWorkflow.getUuid())) {
          wf = programWorkflow;
        } else if (workflowLookup.equalsIgnoreCase(programWorkflow.getId().toString())) {
          wf = programWorkflow;
        }
      }
    }
    if (wf == null) {
      throw new MetadataLookupException(
          "Unable to find workflow using " + programLookup + " - " + workflowLookup);
    }
    return wf;
  }

  /**
   * @return the ProgramWorkflowState matching the given programLookup and workflowLookup and
   *     stateLookup
   */
  public static ProgramWorkflowState getProgramWorkflowState(
      String programLookup, String workflowLookup, String stateLookup) {
    ProgramWorkflow wf = getProgramWorkflow(programLookup, workflowLookup);
    ProgramWorkflowState s = wf.getStateByName(stateLookup);
    if (s == null) {
      for (ProgramWorkflowState state : wf.getStates()) {
        if (stateLookup.equalsIgnoreCase(state.getConcept().getName().toString())) {
          s = state;
        } else if (stateLookup.equalsIgnoreCase(state.getConcept().getUuid())) {
          s = state;
        } else if (stateLookup.equalsIgnoreCase(state.getUuid())) {
          s = state;
        } else if (stateLookup.equalsIgnoreCase(state.getId().toString())) {
          s = state;
        } else if (stateLookup.equalsIgnoreCase(state.getConcept().getId().toString())) {
          s = state;
        }
      }
    }
    if (s == null) {
      throw new MetadataLookupException(
          "Unable to find state using "
              + programLookup
              + " - "
              + workflowLookup
              + " - "
              + stateLookup);
    }
    return s;
  }
}
