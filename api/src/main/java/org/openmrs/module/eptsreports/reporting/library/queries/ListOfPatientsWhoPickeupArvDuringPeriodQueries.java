package org.openmrs.module.eptsreports.reporting.library.queries;

public class ListOfPatientsWhoPickeupArvDuringPeriodQueries {

  /**
   * Estado marked in “Mudança no Estado de Permanencia TARV” in Ficha Resumo with date of Mudança
   * by report generation date
   *
   * @return {@link String}
   */
  public static String getLastStateOfStayOnFichaResumo() {
    return "  SELECT     p.patient_id, "
        + "              max(o.obs_datetime) AS most_recent "
        + "   FROM       patient p "
        + "   INNER JOIN encounter e "
        + "   ON         p.patient_id = e.patient_id "
        + "   INNER JOIN obs o "
        + "   ON         e.encounter_id = o.encounter_id "
        + "   WHERE      p.voided = 0 "
        + "   AND        e.voided = 0 "
        + "   AND        o.voided = 0 "
        + "   AND        e.encounter_type = ${53} "
        + "   AND        e.location_id = :location "
        + "   AND        o.concept_id = ${6272} "
        + "   and        o.obs_datetime <= :endDate "
        + "   GROUP BY   p.patient_id ";
  }

  /**
   * Estado marked in “Mudança no Estado de Permanencia TARV” in Ficha Clínica by report generation
   * date
   *
   * @return {@link String}
   */
  public static String getLastStateOfStayOnFichaClinica() {
    return "          SELECT p.patient_id, Max(e.encounter_datetime) AS most_recent "
        + "      FROM patient p "
        + "               INNER JOIN encounter e ON p.patient_id = e.patient_id "
        + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
        + "      WHERE p.voided = 0 "
        + "        AND e.voided = 0 "
        + "        AND o.voided = 0 "
        + "        AND e.location_id = :location"
        + "        AND e.encounter_type = ${6} "
        + "        AND e.encounter_datetime <= :endDate "
        + "        AND o.concept_id = ${6273} "
        + "      GROUP BY p.patient_id ";
  }

  /**
   * Patient State informed on most recent active ART Program (Service TARV - Tratamento) where
   * patient is enrolled by report generation date
   *
   * @return {@link String}
   */
  public static String getLastStateOfStayOnArtProgram() {
    return " SELECT p.patient_id, "
        + "         Max(pg.date_enrolled) AS most_recent"
        + "  FROM   patient p"
        + "         INNER JOIN patient_program pg"
        + "                 ON p.patient_id = pg.patient_id"
        + "         INNER JOIN patient_state ps"
        + "                 ON pg.patient_program_id ="
        + "                    ps.patient_program_id"
        + "  WHERE  pg.program_id = ${2} AND pg.voided = 0 AND ps.voided = 0 "
        + "         AND pg.date_enrolled <= :endDate"
        + "     AND pg.location_id= :location "
        + "  GROUP  BY p.patient_id ";
  }
}
