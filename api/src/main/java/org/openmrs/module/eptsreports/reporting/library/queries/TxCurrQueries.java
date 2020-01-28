/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TxCurrQueries {

  public class QUERY {

    public static final String findPatientsWhoAreCurrentlyEnrolledOnART =
        "SELECT patient_id FROM ( "
            + "SELECT inicio_fila_seg_prox.*, "
            + "GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c,"
            + "GREATEST(COALESCE(data_proximo_lev,data_proximo_seguimento,data_recepcao_levantou30),COALESCE(data_proximo_seguimento,data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_seguimento,data_proximo_lev)) data_usar "
            + "FROM (SELECT inicio_fila_seg.*, MAX(obs_fila.value_datetime) data_proximo_lev, MAX(obs_seguimento.value_datetime) data_proximo_seguimento, date_add(data_recepcao_levantou, INTERVAL 30 day) data_recepcao_levantou30 FROM (select inicio.*, "
            + "saida.data_estado, max_fila.data_fila, max_consulta.data_seguimento, max_recepcao.data_recepcao_levantou FROM ( SELECT patient_id,min(data_inicio) data_inicio FROM ( SELECT p.patient_id,MIN(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o on o.encounter_id=e.encounter_id WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type in (18,6,9) AND o.concept_id=1255 and o.value_coded=1256 AND e.encounter_datetime<=:endDate "
            + "AND e.location_id=:location GROUP BY p.patient_id UNION "
            + "SELECT p.patient_id,min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 AND o.voided=0 AND e.encounter_type IN (18,6,9,53) AND o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id UNION "
            + "SELECT pg.patient_id,min(date_enrolled) data_inicio FROM patient p INNER JOIN patient_program pg on p.patient_id=pg.patient_id WHERE pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate AND location_id=:location "
            + "GROUP BY pg.patient_id UNION "
            + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided=0 AND e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime <= :endDate AND e.location_id= :location GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id, MIN(value_datetime) data_inicio FROM 	patient p INNER JOIN encounter e on p.patient_id=e.patient_id INNER JOIN obs o on e.encounter_id=o.encounter_id WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 "
            + "and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id) inicio_real GROUP BY patient_id )inicio LEFT JOIN ( "
            + "SELECT patient_id,max(data_estado) data_estado FROM (SELECT 	pg.patient_id, max(ps.start_date) data_estado FROM patient p INNER JOIN patient_program pg on p.patient_id=pg.patient_id INNER JOIN patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state in (7,8,10) and ps.end_date is null and ps.start_date<=:endDate and location_id=:location GROUP BY pg.patient_id UNION "
            + "SELECT 	p.patient_id, max(o.obs_datetime) data_estado FROM 	patient p INNER JOIN encounter e on p.patient_id=e.patient_id INNER JOIN obs  o on e.encounter_id=o.encounter_id WHERE e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type IN (53,6) AND o.concept_id IN (6272,6273) "
            + "AND o.value_coded IN (1706,1366,1709) and o.obs_datetime<=:endDate and e.location_id=:location group by p.patient_id UNION "
            + "SELECT person_id as patient_id,death_date as data_estado FROM person WHERE dead=1 and death_date is not null and death_date<=:endDate UNION SELECT p.patient_id, MAX(obsObito.obs_datetime) data_estado FROM patient p INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs obsObito on e.encounter_id=obsObito.encounter_id WHERE e.voided=0 AND p.voided=0 and obsObito.voided=0 AND e.encounter_type=21 "
            + "AND e.encounter_datetime<=:endDate and  e.location_id=:location AND obsObito.concept_id in (2031, 23944, 23945) and obsObito.value_coded=1366 GROUP BY p.patient_id) allSaida GROUP BY patient_id) "
            + "saida ON inicio.patient_id=saida.patient_id LEFT JOIN (SELECT p.patient_id,max(encounter_datetime) data_fila FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id WHERE p.voided=0 and e.voided=0 and e.encounter_type=18 AND "
            + "e.location_id=:location and e.encounter_datetime<=:endDate GROUP BY p.patient_id) max_fila on inicio.patient_id=max_fila.patient_id LEFT JOIN (SELECT 	p.patient_id,max(encounter_datetime) data_seguimento FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "WHERE p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and e.location_id=:location and e.encounter_datetime<=:endDate GROUP BY p.patient_id) max_consulta on inicio.patient_id=max_consulta.patient_id LEFT JOIN (SELECT p.patient_id,max(value_datetime) data_recepcao_levantou FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id INNER JOIN obs o on e.encounter_id=o.encounter_id WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 AND o.concept_id=23866 AND o.value_datetime IS NOT NULL AND o.value_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id) "
            + "max_recepcao on inicio.patient_id=max_recepcao.patient_id GROUP BY inicio.patient_id) inicio_fila_seg LEFT JOIN obs obs_fila ON obs_fila.person_id=inicio_fila_seg.patient_id and obs_fila.voided=0 and obs_fila.obs_datetime=inicio_fila_seg.data_fila and obs_fila.concept_id=5096 and obs_fila.location_id=:location "
            + "left join obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id and obs_seguimento.voided=0 and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento and obs_seguimento.concept_id=1410 and obs_seguimento.location_id=:location group by inicio_fila_seg.patient_id) inicio_fila_seg_prox "
            + "GROUP BY patient_id) coorte12meses_final WHERE (data_estado is null or(data_estado is not null and data_usar_c>data_estado)) AND date_add(data_usar, interval 28 day) >=:endDate";

    public static final String findPatientsByGenderAndRage =
        "SELECT patient_id FROM patient "
            + "INNER JOIN person ON patient_id = person_id WHERE patient.voided=0 AND person.voided=0 "
            + "AND TIMESTAMPDIFF(year,birthdate,:endDate) BETWEEN %d AND %d AND gender='%s' AND birthdate IS NOT NULL";
  }
}
