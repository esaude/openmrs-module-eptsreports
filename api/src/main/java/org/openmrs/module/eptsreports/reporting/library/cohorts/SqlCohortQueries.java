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

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the SQL Cohort Definition instances we want to expose for EPTS
 */
@Component
public class SqlCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TbMetadata tbMetadata;
	
	// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
	// TRATAMENTO) before or on end date
	@DocumentedDefinition(value = "inARTProgramDuringTimePeriod")
	public SqlCohortDefinition getPatientsinARTProgramDuringTimePeriod() {
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod.setQuery("select pp.patient_id from patient_program pp where pp.program_id=" + hivMetadata.getARTProgram().getProgramId() + " and pp.voided=0   and pp.date_enrolled <= :onOrBefore and " + "(pp.date_completed >= :onOrAfter or pp.date_completed is null)");
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return inARTProgramDuringTimePeriod;
	}
	
	// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
	// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
	// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
	// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
	// SEGUIMENTO) before or on end date
	@DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
	public SqlCohortDefinition getPatientWithSTARTDRUGSObs() {
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + "," + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + "," + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id=" + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded=" + hivMetadata.getstartDrugsConcept().getConceptId() + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientWithSTARTDRUGSObs;
	}
	
	// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
	// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
	// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
	// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
	@DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
	public SqlCohortDefinition getPatientWithHistoricalDrugStartDateObs() {
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + "," + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + "," + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id=" + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId() + " and o.value_datetime is not null and o.value_datetime<=:onOrBefore group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
	// TRATAMENTO), transferred from other health facility (program workflow state
	// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public SqlCohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery("select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=" + hivMetadata.getARTProgram().getProgramId() + " and ps.state=" + hivMetadata.gettransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId() + " and ps.start_date=pg.date_enrolled and ps.start_date<=:onOrBefore and  ps.start_date>=:onOrAfter");
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return transferredFromOtherHealthFacility;
	}
	
	// Obtain patients notified to be on TB treatment
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public SqlCohortDefinition getPatientsNotifiedToBeOnTbTreatment() {
		SqlCohortDefinition notifiedToBeOnTbTreatment = new SqlCohortDefinition();
		notifiedToBeOnTbTreatment.setName("notifiedToBeOnTbTreatment");
		notifiedToBeOnTbTreatment.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + "," + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id=" + tbMetadata.getTUBERCULOSIS_TREATMENT_PLANConcept().getConceptId() + " and o.value_coded=" + hivMetadata.getYesConcept().getConceptId() + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return notifiedToBeOnTbTreatment;
	}
	
	// Patients who left ART program before or on end date(4). Includes: dead,
	// transferred to, stopped and abandoned (patient state 10, 7, 8 or 9)
	@DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
	public SqlCohortDefinition getPatientsWhoLeftARTProgramBeforeOrOnEndDate() {
		SqlCohortDefinition leftARTProgramBeforeOrOnEndDate = new SqlCohortDefinition();
		leftARTProgramBeforeOrOnEndDate.setName("leftARTProgramBeforeOrOnEndDate");
		leftARTProgramBeforeOrOnEndDate.setQuery("select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=" + hivMetadata.getARTProgram().getProgramId() + " and ps.state in (" + hivMetadata.gettransferredFromOtherHealthFacilityWorkflowState() + ", " + hivMetadata.getSuspendedTreatmentWorkflowState() + "," + hivMetadata.getAbandonedWorkflowState() + "," + hivMetadata.getPatientHasDiedWorkflowState() + ") and ps.end_date is null and ps.start_date<=:endDate");
		leftARTProgramBeforeOrOnEndDate.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		leftARTProgramBeforeOrOnEndDate.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return leftARTProgramBeforeOrOnEndDate;
	}
	
	// Looks for patients that from the date scheduled for next drug pickup (concept
	// 5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 60 days
	// and have not returned
	@DocumentedDefinition(value = "patientsWhoHaveNotReturned")
	public SqlCohortDefinition getPatientsWhoHaveNotReturned() {
		SqlCohortDefinition patientsWhoHaveNotReturned = new SqlCohortDefinition();
		patientsWhoHaveNotReturned.setName("patientsWhoHaveNotReturned");
		patientsWhoHaveNotReturned.setQuery("select patient_id from ( Select p.patient_id,max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_type=" + hivMetadata.getARVPharmaciaEncounterType() + " and e.encounter_datetime<=:endDate group by p.patient_id ) max_frida inner join obs o on o.person_id=max_frida.patient_id where max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id= " + hivMetadata.getReturnVisitDateForArvDrugConcept() + " and datediff(:endDate,o.value_datetime)>=60");
		patientsWhoHaveNotReturned.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWhoHaveNotReturned.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientsWhoHaveNotReturned;
	}
	
	// Looks for patients that from the date scheduled for next follow up
	// consultation (concept 1410=RETURN VISIT DATE) until the end date have not
	// completed 60 days
	@DocumentedDefinition(value = "patientsWhoHaveNotCompleted60Days")
	public SqlCohortDefinition getPatientsWhoHaveNotCompleted60Days() {
		SqlCohortDefinition patientsWhoHaveNotCompleted60Days = new SqlCohortDefinition();
		patientsWhoHaveNotCompleted60Days.setName("patientsWhoHaveNotCompleted60Days");
		patientsWhoHaveNotCompleted60Days.setQuery("select patient_id from ( Select p.patient_id,max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_type in (" + hivMetadata.getAdultoSeguimentoEncounterType() + ", " + hivMetadata.getARVPediatriaSeguimentoEncounterType() + ") and e.encounter_datetime<=:endDate group by p.patient_id ) max_mov inner join obs o on o.person_id=max_mov.patient_id where max_mov.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id=" + hivMetadata.getReturnVisitDateConcept() + " and datediff(:endDate,o.value_datetime)<60");
		patientsWhoHaveNotCompleted60Days.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientsWhoHaveNotCompleted60Days.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientsWhoHaveNotCompleted60Days;
	}
	
	// Looks for patients that were registered as abandonment (program workflow
	// state is 9=ABANDONED) but from the date scheduled for next drug pick up
	// (concept 5096=RETURN VISIT DATE FOR ARV DRUG) until the end date have not
	// completed 60 days
	@DocumentedDefinition(value = "getAbandonedButHaveNotcompleted60Days")
	public SqlCohortDefinition getAbandonedButHaveNotcompleted60Days() {
		SqlCohortDefinition abandonedButHaveNotcompleted60Days = new SqlCohortDefinition();
		abandonedButHaveNotcompleted60Days
		        .setName("select abandono.patient_id from ( select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id= " + hivMetadata.getARTProgram().getProgramId() + " and ps.state=" + hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId() + " and ps.end_date is null and ps.start_date<=:endDate )abandono inner join ( select max_frida.patient_id,max_frida.encounter_datetime,o.value_datetime from ( Select p.patient_id,max(encounter_datetime) encounter_datetime from patient p inner join encounter e on e.patient_id=p.patient_id where p.voided=0 and e.voided=0 and e.encounter_type=" + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + " and e.encounter_datetime<=:endDate group by p.patient_id ) max_frida inner join obs o on o.person_id=max_frida.patient_id where max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id=" + hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId() + " ) ultimo_fila on abandono.patient_id=ultimo_fila.patient_id where datediff(:endDate,ultimo_fila.value_datetime)<60");
		abandonedButHaveNotcompleted60Days.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		abandonedButHaveNotcompleted60Days.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return abandonedButHaveNotcompleted60Days;
	}
	
}
