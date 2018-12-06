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

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
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
 * Defines all of the TxNew Cohort Definition instances we want to expose for EPTS
 */
@Component
public class TxNewCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TbMetadata tbMetadata;
	
	// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
	// TRATAMENTO) before or on end date
	@DocumentedDefinition(value = "inARTProgramDuringTimePeriod")
	public CohortDefinition getPatientsinARTProgramDuringTimePeriod() {
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod
		        .setQuery("select p.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
		                + "where pg.voided=0 and p.voided=0 and pg.program_id= " + hivMetadata.getARTProgram().getProgramId()
		                + " and pg.date_enrolled <= :onOrBefore and pg.location_id=:location");
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("location", "location", Location.class));
		return inARTProgramDuringTimePeriod;
	}
	
	// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
	// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
	// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
	// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
	// SEGUIMENTO) before or on end date
	@DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
	public CohortDefinition getPatientWithSTARTDRUGSObs() {
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs
		        .setQuery("select p.patient_id " + "from patient p inner join encounter e on p.patient_id=e.patient_id "
		                + "inner join obs o on o.encounter_id=e.encounter_id "
		                + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		                + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		                + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		                + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ")" + " and o.concept_id="
		                + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded="
		                + hivMetadata.getstartDrugsConcept().getConceptId()
		                + " and e.encounter_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithSTARTDRUGSObs;
	}
	
	// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
	// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
	// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
	// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
	@DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
	public CohortDefinition getPatientWithHistoricalDrugStartDateObs() {
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery("select p.patient_id from patient p "
		        + "inner join encounter e on p.patient_id=e.patient_id inner join obs o on e.encounter_id=o.encounter_id "
		        + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
		        + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		        + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId() + " and o.value_datetime is not null "
		        + " and o.value_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Looks for patients with first drug pickup (encounter type 18=S.TARV:
	// FARMACIA) before or on end date
	@DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
	public CohortDefinition getPatientWithFirstDrugPickupEncounter() {
		SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
		patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");
		patientWithFirstDrugPickupEncounter
		        .setQuery("select e.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id "
		                + "where p.voided=0 and e.encounter_type= " + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()
		                + " and e.voided=0 and e.encounter_datetime <= :onOrBefore and e.location_id=:location "
		                + "group by p.patient_id");
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("location", "location", Location.class));
		return patientWithFirstDrugPickupEncounter;
	}
	
	// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
	// TRATAMENTO), transferred from other health facility (program workflow state
	// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery("select p.patient_id from patient p "
		        + "inner join patient_program pg on p.patient_id=pg.patient_id "
		        + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=" + hivMetadata.getARTProgram().getProgramId()
		        + " and ps.state=" + hivMetadata.gettransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()
		        + " and ps.start_date=pg.date_enrolled"
		        + " and ps.start_date >= :onOrAfter and ps.start_date <= :onOrBefore and location_id=:location "
		        + "group by p.patient_id");
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("location", "location", Location.class));
		return transferredFromOtherHealthFacility;
	}
	
	// Obtain patients notified to be on TB treatment
	@DocumentedDefinition(value = "notifiedToBeOnTbTreatment")
	public CohortDefinition getPatientsNotifiedToBeOnTbTreatment() {
		SqlCohortDefinition notifiedToBeOnTbTreatment = new SqlCohortDefinition();
		notifiedToBeOnTbTreatment.setName("notifiedToBeOnTbTreatment");
		notifiedToBeOnTbTreatment.setQuery(
		    "select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ")" + " and o.concept_id="
		            + tbMetadata.getTUBERCULOSIS_TREATMENT_PLANConcept().getConceptId() + " and o.value_coded="
		            + hivMetadata.getYesConcept().getConceptId()
		            + " and e.encounter_datetime >= :onOrAfter and e.encounter_datetime <=:onOrBefore and e.location_id=:location group by p.patient_id");
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("location", "location", Location.class));
		return notifiedToBeOnTbTreatment;
	}
	
	// Obtain patients from TxNew Union Query
	// @DocumentedDefinition(value = "tTxNewUnionNumerator")
	public CohortDefinition getTxNewUnionNumerator() {
		SqlCohortDefinition txNewUnionNumerator = new SqlCohortDefinition();
		txNewUnionNumerator.setName("TxNewUnionNumerator");
		txNewUnionNumerator.setQuery(TxNewQueries.getTxNewUnionQueries(hivMetadata.getARTProgram().getProgramId(),
		    hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
		    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(), hivMetadata.getARVPlanConcept().getConceptId(),
		    hivMetadata.getstartDrugsConcept().getConceptId(), hivMetadata.gethistoricalDrugStartDateConcept().getConceptId()));
		
		txNewUnionNumerator.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("location", "location", Location.class));
		return txNewUnionNumerator;
	}
	
	/**
	 * Build TxNew composition cohort definition
	 * 
	 * @param cohortName
	 * @param inARTProgramDuringTimePeriod
	 * @param patientWithSTARTDRUGSObs
	 * @param patientWithHistoricalDrugStartDateObs
	 * @param patientsWithDrugPickUpEncounters
	 * @param transferredFromOtherHealthFacility
	 * @param AgeCohort
	 * @param GenderCohort
	 * @return CompositionQuery
	 */
	@DocumentedDefinition(value = "getTxNewCompositionCohort")
	public CohortDefinition getTxNewCompositionCohort(String cohortName, CohortDefinition inARTProgramDuringTimePeriod,
	        CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs,
	        CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility,
	        CohortDefinition AgeCohort, CohortDefinition GenderCohort) {
		CompositionCohortDefinition TxNewComposition = new CompositionCohortDefinition();
		TxNewComposition.setName(cohortName);
		TxNewComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		TxNewComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		TxNewComposition.addParameter(new Parameter("location", "location", Location.class));
		TxNewComposition.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		// TxNewComposition.getSearches().put("1", new
		// Mapped<CohortDefinition>(inARTProgramDuringTimePeriod,
		// ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		// TxNewComposition.getSearches().put("2", new
		// Mapped<CohortDefinition>(patientWithSTARTDRUGSObs,
		// ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		// TxNewComposition.getSearches().put("3", new
		// Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs,
		// ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		// TxNewComposition.getSearches().put("4", new
		// Mapped<CohortDefinition>(patientsWithDrugPickUpEncounters,
		// ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		TxNewComposition.getSearches().put("1", new Mapped<CohortDefinition>(getTxNewUnionNumerator(),
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		TxNewComposition.getSearches().put("2", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility,
		        ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}")));
		
		// String compositionString = "((1 OR 2 OR 3 OR 4) AND (NOT 5))";
		String compositionString = "(1 AND (NOT 2))";
		
		if (AgeCohort != null) {
			TxNewComposition.getSearches().put("3", new Mapped<CohortDefinition>(AgeCohort,
			        ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
			
			compositionString = compositionString + " AND 3";
		}
		
		if (GenderCohort != null) {
			TxNewComposition.getSearches().put("4", new Mapped<CohortDefinition>(GenderCohort, null));
			
			compositionString = compositionString + " AND 4";
		}
		
		TxNewComposition.setCompositionString(compositionString);
		return TxNewComposition;
	}
}
