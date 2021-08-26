package org.openmrs.module.eptsreports.reporting.library.queries.data.quality.duplicate;

public class Ec1Queries {

  /**
   * The patient has more than one Ficha Resumo registered in OpenMRS EPTS at the same Health
   * Facility
   *
   * @return String
   */
  public static String getEc1FichaResumoDuplicates(int encounter_type) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " WHERE e.encounter_type=%d AND e.encounter_datetime <= :endDate";
    return String.format(query, encounter_type);
  }
}
