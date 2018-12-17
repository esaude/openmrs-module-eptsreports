/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the TxCurrCohortQueries we want to expose for EPTS
 */
@Component
public class TxCurrCohortQueries {
	
	private static final int OLD_SPEC_ABANDONMENT_DAYS = 60;
	
	private static final int CURRENT_SPEC_ABANDONMENT_DAYS = 29;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	// Looks for patients with first drug pickup (encounter type 18=S.TARV:
	// FARMACIA) before or on end date
	@DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
	public CohortDefinition getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate() {
		SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
		patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");
		patientWithFirstDrugPickupEncounter.setQuery(
		    "select p.patient_id FROM patient p " + "inner join encounter e on p.patient_id=e.patient_id "
		            + "WHERE p.voided=0 and e.encounter_type= " + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()
		            + "  AND e.voided=0 and e.encounter_datetime <= :onOrBefore and e.location_id=:location GROUP BY p.patient_id");
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("location", "location", Location.class));
		return patientWithFirstDrugPickupEncounter;
	}
	
	// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
	// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
	// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
	// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
	// SEGUIMENTO) before or on end date
	@DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
	public CohortDefinition getPatientWithSTARTDRUGSObsBeforeOrOnEndDate() {
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs.setQuery("select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id "
		        + "inner join obs o on o.encounter_id=e.encounter_id "
		        + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		        + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ")" + " and o.concept_id="
		        + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded in ("
		        + hivMetadata.getstartDrugsConcept().getConceptId() + ", "
		        + hivMetadata.getTransferFromOtherFacilityConcept().getConceptId()
		        + ") and e.encounter_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithSTARTDRUGSObs;
	}
	
	// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
	// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
	// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
	// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
	@DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
	public CohortDefinition getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate() {
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery(
		    "select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id "
		            + "inner join obs o on e.encounter_id=o.encounter_id "
		            + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId()
		            + " and o.value_datetime is not null and o.value_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Patients who left ART program before or on end date(4). Includes: dead,
	// transferred to, stopped and abandoned (patient state 10, 7, 8 or 9)
	@DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
	public SqlCohortDefinition getPatientsWhoLeftARTProgramBeforeOrOnEndDate() {
		SqlCohortDefinition leftARTProgramBeforeOrOnEndDate = new SqlCohortDefinition();
		leftARTProgramBeforeOrOnEndDate.setName("leftARTProgramBeforeOrOnEndDate");
		leftARTProgramBeforeOrOnEndDate.setQuery(
		    "select p.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
		            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
		            + hivMetadata.getARTProgram().getProgramId() + " and ps.state in ("
		            + hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getProgramWorkflowStateId() + ", "
		            + hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId() + ","
		            + hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId() + ","
		            + hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()
		            + ") and ps.end_date is null and ps.start_date<=:onOrBefore and pg.location_id=:location group by p.patient_id");
		leftARTProgramBeforeOrOnEndDate.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		leftARTProgramBeforeOrOnEndDate.addParameter(new Parameter("location", "location", Location.class));
		return leftARTProgramBeforeOrOnEndDate;
	}
	
	// Looks for patients that from the date scheduled for next drug pickup (concept
	// 5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 28 days
	// and have not returned
	@DocumentedDefinition(value = "patientsThatMissedNexPickup")
	public SqlCohortDefinition getPatientsThatMissedNexPickup() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("patientsThatMissedNexPickup");
		String query = "select patient_id from ( Select p.patient_id,max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_type=%s"
		        + " and e.location_id=:location and e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida inner join obs o on o.person_id=max_frida.patient_id where max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id=%s"
		        + " and o.location_id=:location and datediff(:onOrBefore,o.value_datetime)>=:abandonmentDays";
		definition.setQuery(String.format(query, hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
		    hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
		definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));
		definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
		return definition;
	}
	
	// Looks for patients that from the date scheduled for next follow up
	// consultation (concept 1410=RETURN VISIT DATE) until the end date have not
	// completed 28 days
	@DocumentedDefinition(value = "patientsThatDidNotMissNextConsultation")
	public SqlCohortDefinition getPatientsThatDidNotMissNextConsultation() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("patientsThatDidNotMissNextConsultation");
		definition.setQuery("select patient_id from " + "( Select p.patient_id,max(encounter_datetime) encounter_datetime "
		        + "from patient p inner join encounter e on e.patient_id=p.patient_id "
		        + "where p.voided=0 and e.voided=0 and e.encounter_type in ("
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ", "
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") "
		        + "and e.location_id=:location and e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_mov "
		        + "inner join obs o on o.person_id=max_mov.patient_id "
		        + "where max_mov.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id="
		        + hivMetadata.getReturnVisitDateConcept().getConceptId()
		        + " and o.location_id=:location AND DATEDIFF(:onOrBefore,o.value_datetime)<:abandonmentDays");
		definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));
		definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
		return definition;
	}
	
	// Looks for patients that were registered as abandonment (program workflow
	// state is 9=ABANDONED) but from the date scheduled for next drug pick up
	// (concept 5096=RETURN VISIT DATE FOR ARV DRUG) until the end date have not
	// completed 28 days
	@DocumentedDefinition(value = "patientsReportedAsAbandonmentButStillInPeriod")
	public SqlCohortDefinition getPatientsReportedAsAbandonmentButStillInPeriod() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("patientsReportedAsAbandonmentButStillInPeriod");
		definition.setQuery(
		    "select abandono.patient_id from ( select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id= "
		            + hivMetadata.getARTProgram().getProgramId() + " and ps.state="
		            + hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId()
		            + " and ps.end_date is null and ps.start_date<=:onOrBefore and location_id=:location )abandono inner join ( select max_frida.patient_id,max_frida.encounter_datetime,o.value_datetime from ( Select p.patient_id,max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_type="
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()
		            + " and e.location_id=:location and e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida inner join obs o on o.person_id=max_frida.patient_id where max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id="
		            + hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()
		            + " and o.location_id=:location ) ultimo_fila on abandono.patient_id=ultimo_fila.patient_id where datediff(:onOrBefore,ultimo_fila.value_datetime)<:abandonmentDays");
		definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));
		definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
		return definition;
	}
	
	/**
	 * Patients that don't have next pickup date set on their most recent encounter
	 * 
	 * @return
	 */
	@DocumentedDefinition(value = "patientsWithoutNextPickupDate")
	public SqlCohortDefinition getPatientsWithoutNextPickupDate() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("patientsWithoutNextPickupDate");
		String query = "select patient_id from ( "
		        + "select p.patient_id, max(encounter_datetime) encounter_datetime from patient p join encounter e on e.patient_id=p.patient_id  "
		        + "where p.voided=0 and e.voided=0 and e.encounter_type=%s and e.location_id=:location and e.encounter_datetime<=:onOrBefore group by p.patient_id) most_recent_encounter_datetimes "
		        + "left join obs on obs.person_id = most_recent_encounter_datetimes.patient_id and obs.obs_datetime = most_recent_encounter_datetimes.encounter_datetime and obs.concept_id=%s and obs.voided = false "
		        + "where obs.obs_id is null ";
		definition.setQuery(String.format(query, hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
		    hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
		definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));
		return definition;
	}
	
	/**
	 * Patients that don't have next consultation date set on their most recent encounter
	 * 
	 * @return
	 */
	@DocumentedDefinition(value = "patientsWithoutNextConsultationDate")
	public SqlCohortDefinition getPatientsWithoutNextConsultationDate() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("patientsWithoutNextConsultationDate");
		String query = "select patient_id from ( "
		        + "select p.patient_id, max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id "
		        + "where p.voided=0 and e.voided=0 and e.encounter_type in (%s) and e.location_id=:location and e.encounter_datetime<=:onOrBefore group by p.patient_id) most_recent_encounter_datetimes "
		        + "left join obs on obs.person_id = most_recent_encounter_datetimes.patient_id and obs.obs_datetime = most_recent_encounter_datetimes.encounter_datetime and obs.concept_id=%s and obs.voided = false "
		        + "where obs.obs_id is null ";
		String encounterTypes = StringUtils.join(Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()), ',');
		definition.setQuery(String.format(query, encounterTypes, hivMetadata.getReturnVisitDateConcept().getConceptId()));
		definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));
		return definition;
	}
	
	/**
	 * Build TxCurr composition cohort definition
	 * 
	 * @param cohortName
	 * @param inARTProgramAtEndDate
	 * @param patientWithSTARTDRUGSObs
	 * @param patientWithHistoricalDrugStartDateObs
	 * @param patientsWithDrugPickUpEncounters
	 * @param patientsWhoLeftARTProgramBeforeOrOnEndDate
	 * @param patientsThatMissedNexPickup
	 * @param patientsThatDidNotMissNextConsultation
	 * @param patientsReportedAsAbandonmentButStillInPeriod
	 * @param ageCohort
	 * @param patientsWithoutNextPickupDate
	 * @param patientsWithoutNextConsultationDate
	 * @param currentSpec
	 * @return CompositionQuery
	 */
	@DocumentedDefinition(value = "getTxCurrCompositionCohort")
	public CohortDefinition getTxCurrCompositionCohort(String cohortName, CohortDefinition inARTProgramAtEndDate,
	        CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs,
	        CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition patientsWhoLeftARTProgramBeforeOrOnEndDate,
	        CohortDefinition patientsThatMissedNexPickup, CohortDefinition patientsThatDidNotMissNextConsultation,
	        CohortDefinition patientsReportedAsAbandonmentButStillInPeriod, CohortDefinition ageCohort, CohortDefinition genderCohort,
	        CohortDefinition patientsWithoutNextPickupDate, CohortDefinition patientsWithoutNextConsultationDate,
	        boolean currentSpec) {
		
		final int abandonmentDays = currentSpec ? CURRENT_SPEC_ABANDONMENT_DAYS : OLD_SPEC_ABANDONMENT_DAYS;
		CompositionCohortDefinition TxCurrComposition = new CompositionCohortDefinition();
		TxCurrComposition.setName(cohortName);
		
		TxCurrComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		TxCurrComposition.addParameter(new Parameter("location", "location", Location.class));
		TxCurrComposition.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		TxCurrComposition.addParameter(new Parameter("locations", "location", Location.class));
		TxCurrComposition.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramAtEndDate,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},locations=${location}")));
		TxCurrComposition.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		TxCurrComposition.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		TxCurrComposition.getSearches().put("4", new Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		TxCurrComposition.getSearches().put("5", new Mapped<CohortDefinition>(patientsWhoLeftARTProgramBeforeOrOnEndDate,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		TxCurrComposition.getSearches().put("6",
		    new Mapped<CohortDefinition>(patientsThatMissedNexPickup, ParameterizableUtil.createParameterMappings(
		        String.format("onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s", abandonmentDays))));
		TxCurrComposition.getSearches().put("7",
		    new Mapped<CohortDefinition>(patientsThatDidNotMissNextConsultation, ParameterizableUtil.createParameterMappings(
		        String.format("onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s", abandonmentDays))));
		TxCurrComposition.getSearches().put("8",
		    new Mapped<CohortDefinition>(patientsReportedAsAbandonmentButStillInPeriod, ParameterizableUtil.createParameterMappings(
		        String.format("onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s", abandonmentDays))));
		TxCurrComposition.getSearches().put("11", new Mapped<CohortDefinition>(patientsWithoutNextPickupDate,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		TxCurrComposition.getSearches().put("12", new Mapped<CohortDefinition>(patientsWithoutNextConsultationDate,
		        ParameterizableUtil.createParameterMappings("onOrBefore=${onOrBefore},location=${location}")));
		
		String compositionString;
		if (currentSpec) {
			compositionString = "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR (6 AND (NOT (5 OR 7 OR 8)))) AND (NOT (11 AND 12)))";
		} else {
			compositionString = "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR (6 AND (NOT (5 OR 7 OR 8)))))";
		}
		
		if (ageCohort != null) {
			TxCurrComposition.getSearches().put("9", new Mapped<CohortDefinition>(ageCohort,
			        ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
			compositionString = compositionString + " AND 9";
		}
		if (genderCohort != null) {
			TxCurrComposition.getSearches().put("10", new Mapped<CohortDefinition>(genderCohort, null));
			compositionString = compositionString + " AND 10";
		}
		
		TxCurrComposition.setCompositionString(compositionString);
		return TxCurrComposition;
	}
}
