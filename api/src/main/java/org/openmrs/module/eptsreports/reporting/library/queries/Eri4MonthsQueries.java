package org.openmrs.module.eptsreports.reporting.library.queries;

public class Eri4MonthsQueries {

  /**
   * Select all patients (adults and children) who ever initiated the treatment by end of reporting
   * period which include All patients who have initiated the drugs (ARV PLAN = START DRUGS) at the
   * pharmacy or clinical visits (adults and children) by end of reporting period. All patients who
   * have historical start drugs date set in pharmacy (FILA) or in clinical forms (Ficha de
   * Seguimento Adulto end Ficha de seguimento Crianca ) by end of reporting period. All patients
   * enrolled in ART Program by end of reporting period and All patients who have picked up drugs
   * (at least one pharmacy visit) by end of reporting period
   *
   * @return a union of cohort
   */
  public static String
      allPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(
          final int arvPharmaciaEncounter,
          final int arvAdultoSeguimentoEncounter,
          final int arvPediatriaSeguimentoEncounter,
          final int arvPlanConcept,
          final int startDrugsConcept,
          final int historicalDrugsConcept,
          final int artProgram,
          final int transferFromStates) {

    return "SELECT inicio_real.patient_id"
        + " FROM ("
        + " SELECT patient_id,data_inicio"
        + " FROM ("
        + "SELECT patient_id,min(data_inicio) data_inicio"
        + " FROM ("
        + " SELECT p.patient_id,MIN(e.encounter_datetime) data_inicio"
        + " FROM patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON o.encounter_id=e.encounter_id"
        + " WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND"
        + " e.encounter_type IN("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND o.concept_id="
        + arvPlanConcept
        + " AND o.value_coded="
        + startDrugsConcept
        + " AND e.encounter_datetime<=:endDate AND e.location_id=:location"
        + " GROUP BY p.patient_id"
        + " UNION "
        + " SELECT p.patient_id,MIN(value_datetime) data_inicio"
        + " FROM 	patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND o.concept_id="
        + historicalDrugsConcept
        + " AND o.value_datetime IS NOT NULL AND"
        + " o.value_datetime<=:endDate AND e.location_id=:location"
        + " GROUP BY p.patient_id"
        + " UNION "
        + " SELECT pg.patient_id,date_enrolled AS data_inicio"
        + " FROM patient p INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " WHERE pg.voided=0 AND p.voided=0 AND program_id="
        + artProgram
        + " AND date_enrolled<=:endDate AND location_id=:location"
        + " UNION "
        + " SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio"
        + " FROM patient p"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " WHERE	p.voided=0 AND e.encounter_type="
        + arvPharmaciaEncounter
        + " AND e.voided=0 AND e.encounter_datetime<=:endDate and e.location_id=:location"
        + " GROUP BY p.patient_id"
        + ") inicio"
        + " GROUP BY patient_id"
        + ") inicio1"
        + " WHERE data_inicio BETWEEN :startDate AND :endDate "
        + ") inicio_real"
        + " INNER JOIN encounter e ON e.patient_id=inicio_real.patient_id"
        + " WHERE e.voided=0 AND e.encounter_type IN("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND e.location_id=:location AND"
        + " e.encounter_datetime BETWEEN date_add(inicio_real.data_inicio, interval 61 day) AND date_add(inicio_real.data_inicio, interval 120 day) AND"
        + " inicio_real.patient_id NOT IN"
        + "("
        + "SELECT pg.patient_id"
        + " FROM patient p"
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
        + " pg.program_id="
        + artProgram
        + " AND ps.state="
        + transferFromStates
        + " AND ps.start_date=pg.date_enrolled AND"
        + " ps.start_date BETWEEN :startDate AND :endDate and location_id=:location"
        + ")"
        + " GROUP BY inicio_real.patient_id";
  }

  // TODO: harmonise with LTFU queries from TxCurr
  public static String getPatientsLostToFollowUpOnDrugPickup(
      final int arvFarmacyEncounterType,
      final int drugPickupReturnVisitDateConcept,
      final int daysThreshold) {
    final String query =
        "SELECT patient_id FROM "
            + "(SELECT patient_id,value_datetime FROM "
            + "( SELECT p.patient_id,MAX(encounter_datetime) AS encounter_datetime FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 "
            + "AND e.encounter_type in(%d) AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id ) max_frida "
            + "INNER JOIN obs o ON o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND "
            + "o.voided=0 AND o.concept_id=%d AND o.location_id=:location AND encounter_datetime BETWEEN "
            + ":startDate and :endDate "
            + ") final WHERE datediff(:endDate,final.value_datetime)>=%d";
    return String.format(
        query, arvFarmacyEncounterType, drugPickupReturnVisitDateConcept, daysThreshold);
  }

  public static String getPatientsLostToFollowUpOnConsultation(
      final int adultEncounteyType,
      final int paedEncounterType,
      final int returnVisitConcept,
      final int daysThreshold) {
    final String query =
        "SELECT patient_id FROM "
            + "(SELECT patient_id, value_datetime FROM( SELECT p.patient_id,MAX(encounter_datetime)AS encounter_datetime "
            + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND "
            + "e.encounter_type in (%d, %d) AND e.location_id=:location AND e.encounter_datetime<=:endDate "
            + "GROUP BY p.patient_id ) max_mov INNER JOIN obs o ON o.person_id=max_mov.patient_id "
            + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d"
            + " AND o.location_id=:location "
            + "AND encounter_datetime BETWEEN :startDate and :endDate "
            + ") final WHERE datediff(:endDate,final.value_datetime)>=%d";
    return String.format(
        query, adultEncounteyType, paedEncounterType, returnVisitConcept, daysThreshold);
  }

  public static final String
      findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
          final Eri4mType eri4mType) {

    String query =
        "SELECT patient_id FROM (SELECT inicio_real.patient_id, "
            + "inicio_real.data_inicio, "
            + "max(lev_seg.encounter_datetime) last_encounter, "
            + "IF(saida.data_estado is not null and max(date(ultLevConsulta.dataUltimLevConsulta))>saida.data_estado,null,saida.state_id) final_state,"
            + "IF(saida.data_estado is not null and max(date(ultLevConsulta.dataUltimLevConsulta))>saida.data_estado,null,saida.data_estado) final_statedate,"
            + "IF(date_add(date(ultLevConsulta.dataProxLevConsulta), interval 60 day) < :endDate,'LTFU',null) ltfu "
            + "FROM( SELECT patient_id,data_inicio FROM(  "
            + "Select patient_id,min(data_inicio) data_inicio FROM ( SELECT p.patient_id,min(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 AND e.encounter_datetime<=:endDate and e.location_id=:location "
            + "GROUP BY p.patient_id  "
            + "UNION "
            + "SELECT p.patient_id,min(value_datetime) data_inicio FROM patient p  "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id  "
            + "INNER JOIN obs o on e.encounter_id=o.encounter_id  "
            + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) AND o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location "
            + "group by p.patient_id "
            + "UNION "
            + "SELECT pg.patient_id,min(date_enrolled) data_inicio FROM patient p  "
            + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id  "
            + "WHERE pg.voided=0 and p.voided=0 and program_id=2 AND date_enrolled<=:endDate and location_id=:location "
            + "GROUP BY pg.patient_id "
            + "UNION "
            + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location group by p.patient_id) inicio group by patient_id "
            + ")inicio1 "
            + "WHERE data_inicio between date_add(date_add(:endDate, interval -5 month), INTERVAL 1 DAY) and date_add(:endDate, interval -4 month) "
            + ") inicio_real "
            + "LEFT JOIN ( "
            + "select p.patient_id, encounter_datetime FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and  e.encounter_type in (6,9,18) AND e.encounter_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id,value_datetime as encounter_datetime FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 AND o.concept_id=23866 and o.value_datetime is not null and o.value_datetime between  (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location) lev_seg "
            + "ON lev_seg.patient_id = inicio_real.patient_id and lev_seg.encounter_datetime BETWEEN date_add(inicio_real.data_inicio, interval 61 day) and date_add(inicio_real.data_inicio, interval 120 day) "
            + "LEFT JOIN ( "
            + "SELECT patient_id,max(data_estado) data_estado,state_id FROM ( "
            + "select pg.patient_id, max(ps.start_date) data_estado, ps.state as state_id from patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 AND pg.program_id=2 AND ps.state in (7,8,10) AND ps.end_date is null AND ps.start_date between  (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and location_id=:location "
            + "group by pg.patient_id "
            + "UNION "
            + "select p.patient_id, max(o.obs_datetime) data_estado, IF(o.value_coded=1706,7,if(o.value_coded=1366,10,8)) as state_id FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id  "
            + "INNER JOIN obs  o on e.encounter_id=o.encounter_id  "
            + "WHERE e.voided=0 and o.voided=0 AND p.voided=0 AND e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) AND o.obs_datetime between  (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "select person_id as patient_id,death_date as data_estado,10 as state_id from person "
            + "where dead=1 and death_date is not null and death_date between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate "
            + "UNION "
            + "SELECT p.patient_id, max(obsObito.obs_datetime) data_estado,10 as state_id from 	patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs obsObito on e.encounter_id=obsObito.encounter_id "
            + "WHERE e.voided=0 and p.voided=0 and obsObito.voided=0 AND e.encounter_type in (21,36,37) AND e.encounter_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location AND obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366 "
            + "GROUP BY p.patient_id) allSaida group by patient_id "
            + ") saida ON inicio_real.patient_id=saida.patient_id and saida.data_estado BETWEEN inicio_real.data_inicio and :endDate "
            + "LEFT JOIN ( "
            + "select patient_id,max(encounter_datetime) dataUltimLevConsulta,max(value_datetime) dataProxLevConsulta from ( "
            + "Select ultimavisita.patient_id,ultimavisita.encounter_datetime,o.value_datetime from ( "
            + "select p.patient_id,max(encounter_datetime) as encounter_datetime from encounter e "
            + "inner join patient p on p.patient_id=e.patient_id "
            + "where e.voided=0 and p.voided=0 and e.encounter_type=18 and e.location_id=:location and "
            + "e.encounter_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate "
            + "group by p.patient_id "
            + ") ultimavisita "
            + "inner join encounter e on e.patient_id=ultimavisita.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where o.concept_id=5096 and o.voided=0 and e.encounter_datetime=ultimavisita.encounter_datetime and  e.encounter_type=18 and e.location_id=:location	"
            + "union "
            + "Select p.patient_id,max(value_datetime) encounter_datetime,date_add(max(value_datetime), interval 30 day) as value_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and  o.concept_id=23866 and o.value_datetime is not null and  o.value_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate and e.location_id=:location "
            + "group by p.patient_id "
            + "union "
            + "Select ultimavisita.patient_id,ultimavisita.encounter_datetime,o.value_datetime from ( "
            + "select p.patient_id,max(encounter_datetime) as encounter_datetime from encounter e  "
            + "inner join patient p on p.patient_id=e.patient_id "
            + "where e.voided=0 and p.voided=0 and e.encounter_type in (6,9) and e.location_id=:location and  e.encounter_datetime between (:endDate - INTERVAL 5 MONTH  + INTERVAL 1 DAY) and :endDate "
            + "group by p.patient_id "
            + ") ultimavisita "
            + "left join obs o on o.person_id=ultimavisita.patient_id and o.concept_id=1410 and o.voided=0 and ultimavisita.encounter_datetime=o.obs_datetime and o.location_id=:location "
            + ") lev "
            + "group by patient_id "
            + ") ultLevConsulta on ultLevConsulta.patient_id=inicio_real.patient_id "
            + "LEFT JOIN ( "
            + "select pg.patient_id FROM patient p "
            + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state=29 and  ps.start_date <=:endDate and pg.location_id=:location "
            + "UNION "
            + "SELECT p.patient_id from patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id  "
            + "INNER JOIN obs obsTrans ON e.encounter_id=obsTrans.encounter_id AND obsTrans.voided=0 AND obsTrans.concept_id=1369 AND obsTrans.value_coded=1065 "
            + "INNER JOIN obs obsTarv ON e.encounter_id=obsTarv.encounter_id AND obsTarv.voided=0 AND obsTarv.concept_id=6300 AND obsTarv.value_coded=6276 "
            + "INNER JOIN obs obsData ON e.encounter_id=obsData.encounter_id AND obsData.voided=0 AND obsData.concept_id=23891 "
            + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=53 AND obsData.value_datetime <=:endDate AND e.location_id=:location "
            + ") transferedIn on inicio_real.patient_id=transferedIn.patient_id WHERE transferedIn.patient_id is null group by inicio_real.patient_id  "
            + ") allPatient4Month ";

    switch (eri4mType) {
      case DEFAULTER:
        query = query + " WHERE last_encounter IS NULL AND final_state IS NULL";
        break;

      case IN_TREATMENT:
        query = query + " WHERE last_encounter IS NOT NULL AND final_state IS NULL";
        break;

      case DEAD:
        query = query + " WHERE final_state = 10";
        break;

      case TRANFERED_OUT:
        query = query + " WHERE final_state = 7";
        break;

      case LFTU:
        query = query + "Where last_encounter is null and final_state is null and ltfu is not null";
        break;

      case SPTOPPED_TREATMENT:
        query = query + " WHERE final_state = 8";
        break;
    }

    return query;
  }

  public static final String findPatientsWhoAreLostFollowUp() {

    final String query =
        "SELECT patient_id FROM ( "
            + "SELECT inicio_fila_seg_prox.*, "
            + "GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c,"
            + "GREATEST(COALESCE(data_proximo_lev,data_proximo_seguimento,data_recepcao_levantou30),COALESCE(data_proximo_seguimento,data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_seguimento,data_proximo_lev)) data_usar "
            + "FROM (SELECT inicio_fila_seg.*, MAX(obs_fila.value_datetime) data_proximo_lev, MAX(obs_seguimento.value_datetime) data_proximo_seguimento, date_add(data_recepcao_levantou, INTERVAL 30 day) data_recepcao_levantou30 FROM (select inicio.*, "
            + "saida.data_estado, max_fila.data_fila, max_consulta.data_seguimento, max_recepcao.data_recepcao_levantou FROM ( "
            + "SELECT patient_id,min(data_inicio) data_inicio FROM ( SELECT p.patient_id,MIN(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o on o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND e.encounter_type in (18,6,9) AND o.concept_id=1255 and o.value_coded=1256 AND e.encounter_datetime<=:endDate "
            + "AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 AND o.voided=0 AND e.encounter_type IN (18,6,9,53) AND o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id UNION "
            + "SELECT pg.patient_id,min(date_enrolled) data_inicio FROM patient p "
            + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id WHERE pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate AND location_id=:location "
            + "GROUP BY pg.patient_id UNION "
            + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime <= :endDate AND e.location_id= :location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and o.concept_id=23866 "
            + "and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location GROUP BY p.patient_id) "
            + "inicio_real GROUP BY patient_id )inicio "
            + "LEFT JOIN ( "
            + "SELECT patient_id,max(data_estado) data_estado FROM (SELECT 	pg.patient_id, max(ps.start_date) data_estado FROM patient p "
            + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
            + "INNER JOIN patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=2 and ps.state in (7,8,10) and ps.end_date is null and ps.start_date<=:endDate and location_id=:location GROUP BY pg.patient_id "
            + "UNION "
            + "SELECT 	p.patient_id, max(o.obs_datetime) data_estado FROM 	patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs  o on e.encounter_id=o.encounter_id "
            + "WHERE e.voided=0 and o.voided=0 and p.voided=0 and  e.encounter_type IN (53,6) AND o.concept_id IN (6272,6273) "
            + "AND o.value_coded IN (1706,1366,1709) and o.obs_datetime<=:endDate and e.location_id=:location group by p.patient_id "
            + "UNION "
            + "SELECT person_id as patient_id,death_date as data_estado FROM person "
            + "WHERE dead=1 and death_date is not null and death_date<=:endDate "
            + "UNION SELECT p.patient_id, MAX(obsObito.obs_datetime) data_estado FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs obsEncontrado on e.encounter_id=obsEncontrado.encounter_id "
            + "INNER JOIN obs obsObito on e.encounter_id=obsObito.encounter_id WHERE e.voided=0 and obsEncontrado.voided=0 AND p.voided=0 and obsObito.voided=0 AND e.encounter_type in (21,36,37) "
            + "AND e.encounter_datetime<=:endDate and  e.location_id=:location and obsEncontrado.concept_id in (2003, 6348) and obsEncontrado.value_coded=1066 AND obsObito.concept_id=2031 and obsObito.value_coded=1383 "
            + "GROUP BY p.patient_id) "
            + "allSaida "
            + "GROUP BY patient_id) "
            + "saida ON inicio.patient_id=saida.patient_id "
            + "LEFT JOIN (SELECT p.patient_id,max(encounter_datetime) data_fila FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "WHERE p.voided=0 and e.voided=0 and e.encounter_type=18 AND "
            + "e.location_id=:location and e.encounter_datetime<=:endDate GROUP BY p.patient_id) max_fila on inicio.patient_id=max_fila.patient_id "
            + "LEFT JOIN (SELECT p.patient_id,max(encounter_datetime) data_seguimento FROM patient p "
            + "INNER JOIN encounter e on e.patient_id=p.patient_id "
            + "WHERE p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and e.location_id=:location and e.encounter_datetime<=:endDate "
            + "GROUP BY p.patient_id) max_consulta on inicio.patient_id=max_consulta.patient_id "
            + "LEFT JOIN ("
            + "SELECT p.patient_id,max(value_datetime) data_recepcao_levantou FROM patient p "
            + "INNER JOIN encounter e on p.patient_id=e.patient_id "
            + "INNER JOIN obs o on e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 AND o.concept_id=23866 AND o.value_datetime IS NOT NULL AND o.value_datetime<=:endDate and e.location_id=:location "
            + "GROUP BY p.patient_id) "
            + "max_recepcao on inicio.patient_id=max_recepcao.patient_id GROUP BY inicio.patient_id) inicio_fila_seg "
            + "LEFT JOIN obs obs_fila ON obs_fila.person_id=inicio_fila_seg.patient_id and obs_fila.voided=0 and obs_fila.obs_datetime=inicio_fila_seg.data_fila and obs_fila.concept_id=5096 and obs_fila.location_id=:location "
            + "left join obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id and obs_seguimento.voided=0 and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento and obs_seguimento.concept_id=1410 and obs_seguimento.location_id=:location group by inicio_fila_seg.patient_id) inicio_fila_seg_prox "
            + "GROUP BY patient_id) coorte12meses_final WHERE (data_estado is null or(data_estado is not null and data_usar_c>data_estado)) AND date_add(data_usar, interval 60 day) < :endDate";

    return query;
  }
}
