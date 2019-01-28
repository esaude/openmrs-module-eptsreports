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
                                                                           int arvPlanConcept, int startDrugsConcept ) {

        return "SELECT p.patient_id, MIN(e.encounter_datetime) data_inicio from patient p "
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
                + " AND e.encounter_datetime <= :onOrBefore and e.location_id=:location GROUP BY p.patient_id";
    }
}
