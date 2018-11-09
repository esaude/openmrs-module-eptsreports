package org.openmrs.module.eptsreports.reporting.library.queries;

import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Re usable queries that can be used for finding patients who are pregnant
 */
public class PregnantQueries {
	
	@Autowired
	private CommonMetadata commonMetadata;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * Looks for patients indicated PREGNANT in the initial or follow-up consultation between start date
	 * and end date
	 */
	private String PREGNANT_ON_INITIAL_OR_FOLLOW_UP_CONSULATION = "SELECT p.patient_id" + " FROM patient p"
	        + " INNER JOIN encounter e ON p.patient_id=e.patient_id" + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
	        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
	        + commonMetadata.getPregnantConcept().getConceptId() + " AND value_coded="
	        + commonMetadata.getGestationConcept().getConceptId() + " AND e.encounter_type IN ("
	        + hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId() + ","
	        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ")"
	        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location";
	
	/**
	 * Looks for patients with Number of Weeks Pregnant registered in the initial or follow-up
	 * consultation
	 */
	private String WEEKS_PREGNANT_ON_INITIAL_OR_FOLLOW_UP_CONSULTATION = "SELECT p.patient_id"
	        + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
	        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
	        + " WHERE p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
	        + commonMetadata.getNumberOfWeeksPregnant().getConceptId() + " AND e.encounter_type IN ("
	        + hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId() + ","
	        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ")"
	        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND" + " e.location_id=:location";
	
	/**
	 * Looks for patients with PREGNANCY DUE DATE registered in the initial or follow-up consultation
	 * between start date and end date
	 */
	private String PREGNANCY_DUE_DATE_REGISTERED = "SELECT p.patient_id FROM patient p INNER JOIN encounter"
	        + " e ON p.patient_id=e.patient_id INNER JOIN obs o ON e.encounter_id=o.encounter_id"
	        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
	        + commonMetadata.getPregnancyDueDate().getConceptId() + " AND e.encounter_type IN ("
	        + hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId() + ","
	        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ")"
	        + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND" + " e.location_id=:location";
	
	/**
	 * Looks for patients enrolled on PTV/ETV program between start date and end date
	 */
	private String ENROLLED_IN_PTV_ETV = "SELECT pp.patient_id FROM patient_program pp WHERE pp.program_id="
	        + hivMetadata.getPtvEtvProgram().getProgramId() + " AND pp.voided=0 AND pp.date_enrolled BETWEEN "
	        + ":startDate AND :endDate AND pp.location_id=:location";
	
	// getters for the string variables
	public String getPregnantOnInitialOrFollowUpConsultation() {
		return PREGNANT_ON_INITIAL_OR_FOLLOW_UP_CONSULATION;
	}
	
	public String getWeeksPregnantOnInitialOrFollowUpConsultation() {
		return WEEKS_PREGNANT_ON_INITIAL_OR_FOLLOW_UP_CONSULTATION;
	}
	
	public String getPregnacyDueDateRegistered() {
		return PREGNANCY_DUE_DATE_REGISTERED;
	}
	
	public String getEnrolledInPTVorETV() {
		return ENROLLED_IN_PTV_ETV;
	}
}
