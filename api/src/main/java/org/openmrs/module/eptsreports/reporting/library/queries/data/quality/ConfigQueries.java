package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

public class ConfigQueries {

  public static String getConfigurations(int programId) {
    String query =
        "SELECT distinct(pws.concept_id) AS concept, cn.name as name, DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s') AS report_time FROM program_workflow_state pws INNER JOIN concept_name cn ON pws.concept_id=cn.concept_id WHERE pws.program_workflow_id="
            + programId
            + " AND pws.program_workflow_state_id IN(:state) AND cn.locale='en'";
    return query;
  }
}
