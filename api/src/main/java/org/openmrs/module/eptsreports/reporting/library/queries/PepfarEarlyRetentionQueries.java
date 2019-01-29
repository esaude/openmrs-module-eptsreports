package org.openmrs.module.eptsreports.reporting.library.queries;

public class PepfarEarlyRetentionQueries {

    /**
     *Select all patients (adults and children) who ever initiated the treatment by end of reporting period which
     * include All patients who have initiated the drugs (ARV PLAN = START DRUGS) at the pharmacy or clinical visits
     *(adults and children) by end of reporting period. All patients who have historical start drugs date set in
     * pharmacy (FILA) or in clinical forms (Ficha de Seguimento Adulto end Ficha de seguimento Crianca ) by end of
     * reporting period. All patients enrolled in ART Program by end of reporting period and All patients who have
     * picked up drugs (at least one pharmacy visit) by end of reporting period
     *@return a union of cohort
     */
    public static String getPatientsRetainedOnArt3MonthsAfterArtInitiation(int arvPharmaciaEncounter,
                                                                           int arvAdultoSeguimentoEncounter,
                                                                           int arvPediatriaSeguimentoEncounter,
                                                                           int arvPlanConcept, int startDrugsConcept,
                                                                           int historicalDrugsConcept,
                                                                           int artProgram) {

        return "SELECT p.patient_id, MIN(e.encounter_datetime) data_inicio FROM patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o on o.encounter_id=e.encounter_id "
                + "WHERE e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type IN ("
                + arvPharmaciaEncounter
                + ","
                + arvAdultoSeguimentoEncounter
                + ","
                + arvPediatriaSeguimentoEncounter
                + ") AND o.concept_id="
                + arvPlanConcept
                + " AND o.value_coded="
                + startDrugsConcept
                + " AND e.encounter_datetime <= :endDate and e.location_id=:location GROUP BY p.patient_id"
                + " UNION "
                + "SELECT p.patient_id, MIN(value_datetime) data_inicio FROM patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON e.encounter_id=o.encounter_id "
                + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN("
                + arvPharmaciaEncounter
                + ","
                + arvAdultoSeguimentoEncounter
                + ","
                + arvPediatriaSeguimentoEncounter
                + ") AND o.concept_id="
                + historicalDrugsConcept
                + " AND o.value_datetime is NOT null and o.value_datetime <= :endDate AND e.location_id=:location "
                + " GROUP BY p.patient_id"
                + " UNION "
                +"SELECT patient_id FROM (SELECT patient_id, MIN(data_inicio) data_inicio FROM "
                + "(SELECT p.patient_id, date_enrolled data_inicio FROM patient p "
                + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
                + "WHERE pg.voided=0 AND p.voided=0 AND pg.program_id= "
                + artProgram
                + " AND pg.date_enrolled <= :endDate AND pg.location_id=:location"
                + " UNION "
                +"SELECT e.patient_id, MIN(e.encounter_datetime) data_inicio FROM patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided=0 AND e.encounter_type= "
                + arvPharmaciaEncounter
                + " AND e.voided=0 and e.encounter_datetime <= :endDate AND e.location_id=:location "
                + "group by p.patient_id) temp1 group by patient_id) temp2 INNER JOIN person pe on temp2.patient_id=pe.person_id "
                + "WHERE (data_inicio between :startDate and :endDate)"
                ;

    }
}
