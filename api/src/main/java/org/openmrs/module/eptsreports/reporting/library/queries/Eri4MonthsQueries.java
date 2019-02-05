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
          int arvPharmaciaEncounter,
          int arvAdultoSeguimentoEncounter,
          int arvPediatriaSeguimentoEncounter,
          int arvPlanConcept,
          int startDrugsConcept,
          int historicalDrugsConcept,
          int artProgram,
          int transferFromStates) {

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
        + " WHERE data_inicio BETWEEN date_add(date_add(:endDate, interval -4 month), interval 1 day) AND date_add(:endDate, interval -3 month)"
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
        + " ps.start_date BETWEEN date_add(date_add(:endDate, interval -4 month), interval 1 day) AND date_add(:endDate, interval -3 month) and location_id=:location"
        + ")"
        + " GROUP BY inicio_real.patient_id";
  }

  public static String allPatientsWhoInitiatedTreatmentDuringReportingPeriod(
      int arvPharmaciaEncounter,
      int arvAdultoSeguimentoEncounter,
      int arvPediatriaSeguimentoEncounter,
      int arvPlanConcept,
      int startDrugsConcept,
      int historicalDrugsConcept,
      int artProgram) {
    return "SELECT inicio_real.patient_id "
        + "FROM "
        + "(SELECT patient_id,data_inicio "
        + "FROM "
        + "(SELECT patient_id,MIN(data_inicio) data_inicio "
        + "FROM "
        + "(SELECT p.patient_id,min(e.encounter_datetime) AS data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
        + "WHERE e.voided=0 and o.voided=0 and p.voided=0 AND "
        + "e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND o.concept_id="
        + arvPlanConcept
        + " AND o.value_coded="
        + startDrugsConcept
        + " AND "
        + "e.encounter_datetime<=:endDate AND e.location_id=:location "
        + "GROUP BY p.patient_id "
        + "UNION "
        + "SELECT p.patient_id,min(value_datetime) AS data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
        + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN ("
        + arvPharmaciaEncounter
        + ","
        + arvAdultoSeguimentoEncounter
        + ","
        + arvPediatriaSeguimentoEncounter
        + ") AND "
        + "o.concept_id="
        + historicalDrugsConcept
        + " AND o.value_datetime IS NOT NULL AND "
        + "o.value_datetime<=:endDate and e.location_id=:location "
        + "GROUP BY p.patient_id "
        + "UNION "
        + "SELECT pg.patient_id,date_enrolled AS data_inicio "
        + "FROM patient p INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
        + "WHERE pg.voided=0 AND p.voided=0 AND program_id="
        + artProgram
        + " AND date_enrolled<=:endDate AND location_id=:location "
        + "UNION "
        + "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio "
        + "FROM patient p "
        + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
        + "WHERE p.voided=0 AND e.encounter_type="
        + arvPharmaciaEncounter
        + " AND e.voided=0 AND e.encounter_datetime<=:endDate AND e.location_id=:location "
        + "GROUP BY p.patient_id "
        + ") inicio "
        + "GROUP BY patient_id "
        + ")inicio1 "
        + "WHERE data_inicio BETWEEN date_add(date_add(:endDate, interval -4 month), interval 1 day) and date_add(:endDate, interval -3 month) "
        + ") inicio_real "
        + "GROUP BY inicio_real.patient_id";
  }

  public static String getTransferInPatients(int artProgram, int transferFromState) {
    return "SELECT pg.patient_id "
        + "FROM patient p "
        + "INNER JOIN patient_program pg on p.patient_id=pg.patient_id "
        + "INNER JOIN patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "WHERE pg.voided=0 and ps.voided=0 AND p.voided=0 AND  "
        + "pg.program_id="
        + artProgram
        + " AND ps.state="
        + transferFromState
        + " AND ps.start_date=pg.date_enrolled AND "
        + "ps.start_date BETWEEN date_add(date_add(:endDate, interval -4 month), interval 1 day) AND date_add(:endDate, interval -3 month) AND location_id=:location";
  }
}
