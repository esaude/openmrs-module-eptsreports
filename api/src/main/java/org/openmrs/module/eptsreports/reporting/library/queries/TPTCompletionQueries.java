package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.*;

public class TPTCompletionQueries {
  /**
   * <b> Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida+Piridoxina”)
   * and “Seguimento de Tratamento TPT” with values “Continua” or no value marked on a drug pick-up
   * on Ficha de Levantamento de TPT (FILT) as “FILT Start Date” and:
   *
   * <ul>
   *   <li>No other INH values [Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *       ‘Isoniazid + piridoxina’ (concept id in [656, 23982])] marked on FILT in the 7 months
   *       prior to ‘INH Start Date’; and
   *   <li>No other INH Start Dates marked on Ficha resumo (Encounter Type ID 53) Última profilaxia
   *       TPT with value “Isoniazida (INH)” (concept id 23985 value 656) and Data Inicio (obs
   *       datetime for concept 165308 value 1256) selected in the 7 months prior to this FILT INH
   *       Start Date and
   *   <li>No other INH Start Dates marked on Ficha Clinica (Encounter Type ID 6) (Profilaxia TPT
   *       with the value “Isoniazida (INH)” (concept id 23985 value 656) and Estado da Profilaxia
   *       with the value “Inicio (I)” (concept 165308 value 1256) in the 7 months prior to this
   *       FILT INH Start Date and; and
   *   <li>No other INH Start Dates marked on Ficha de Seguimento (Encounter Type ID 9) Profilaxia
   *       TPT with the value “Isoniazida (INH)” (concept id 23985 value 656) and Data Início (obs
   *       datetime for concept 165308 value 1256) in the 7 months prior to this FILT INH Start Date
   * </ul>
   *
   * @return {@link String}
   */
  public static String getInhStartOnFilt() {
    return " SELECT     p.patient_id, filt.start_date AS start_date "
        + "FROM       patient p "
        + "INNER JOIN encounter e "
        + "ON         e.patient_id = p.patient_id "
        + "INNER JOIN obs o "
        + "ON         o.encounter_id = e.encounter_id "
        + "INNER JOIN "
        + "           ( "
        + "                      SELECT     p.patient_id, "
        + "                                 o2.obs_datetime start_date "
        + "                      FROM       patient p "
        + "                      INNER JOIN encounter e "
        + "                      ON         e.patient_id = p.patient_id "
        + "                      INNER JOIN obs o "
        + "                      ON         o.encounter_id = e.encounter_id "
        + "                      INNER JOIN obs o2 "
        + "                      ON         o2.encounter_id = e.encounter_id "
        + "                      WHERE      p.voided = 0 "
        + "                      AND        e.voided = 0 "
        + "                      AND        o.voided = 0 "
        + "                      AND        e.location_id = :location "
        + "                      AND        e.encounter_type = ${60} "
        + "                      AND (o.concept_id = ${23985} AND o.value_coded IN (${656}, ${23982})) "
        + "                      AND (o2.concept_id = ${23987} AND ( o2.value_coded = ${1257} OR o2.value_coded IS NULL )  "
        + "                        AND o2.obs_datetime <= :endDate )"
        + "                       ) AS filt "
        + "ON         filt.patient_id = p.patient_id "
        + "WHERE      p.patient_id NOT IN "
        + "           ( "
        + "                      SELECT     pp.patient_id "
        + "                      FROM       patient pp "
        + "                      INNER JOIN encounter ee "
        + "                      ON         ee.patient_id = pp.patient_id "
        + "                      INNER JOIN obs oo "
        + "                      ON         oo.encounter_id = ee.encounter_id "
        + "                      WHERE      p.patient_id = pp.patient_id "
        + "                      AND        pp.voided = 0 "
        + "                      AND        ee.voided = 0 "
        + "                      AND        oo.voided = 0 "
        + "                      AND        ee.location_id = :location "
        + "                      AND        ee.encounter_type = ${60} "
        + "                      AND        oo.concept_id = ${23985} "
        + "                      AND        oo.value_coded IN ( ${656} , "
        + "                                                    ${23982} ) "
        + "                      AND        ee.encounter_datetime >= date_sub(filt.start_date, INTERVAL 7 month) "
        + "                      AND        ee.encounter_datetime < filt.start_date "
        + "                      GROUP BY   p.patient_id "
        + "                      UNION "
        + "                      SELECT p.patient_id "
        + "                      FROM   patient p "
        + "                      JOIN   encounter e "
        + "                      ON     e.patient_id = p.patient_id "
        + "                      JOIN   obs o "
        + "                      ON     o.encounter_id = e.encounter_id "
        + "                      JOIN   obs o2 "
        + "                      ON     o2.encounter_id = e.encounter_id "
        + "                      WHERE  o.voided = 0 "
        + "                      AND    e.voided = 0 "
        + "                      AND    p.voided = 0 "
        + "                      AND    e.location_id = :location "
        + "                      AND    e.encounter_type IN (${53}, "
        + "                                                  ${6}, "
        + "                                                  ${9}) "
        + "                      AND    ( ( "
        + "                                           o.concept_id = ${23985} "
        + "                                    AND    o.value_coded = ${656} ) "
        + "                             AND    ( "
        + "                                           o2.concept_id = ${165308} "
        + "                                    AND    o2.value_coded = ${1256} "
        + "                                    AND    o2.obs_datetime >= date_sub(filt.start_date, INTERVAL 7 month) "
        + "                                    AND    o2.obs_datetime < filt.start_date ) ) ) "
        + "GROUP BY   p.patient_id";
  }
}
