package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class ConfigQueries {

  public static String getConfigurations(int programId) {
    String query =
        "SELECT pws.concept_id  FROM program_workflow_state pws WHERE pws.program_workflow_state_id IN(:state)";
    return String.format(query, programId);
  }
}
