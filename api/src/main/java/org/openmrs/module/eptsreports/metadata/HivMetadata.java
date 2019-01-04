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
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.springframework.stereotype.Component;

@Component("hivMetadata")
public class HivMetadata extends CommonMetadata {
	
	// Concepts
	public Concept getHivViralLoadConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_HIV_VIRAL_LOAD_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getCriteriaForArtStart() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_CRITERIA_FOR_ART_START_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getReturnVisitDateForArvDrugConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_RETURN_VISIT_DATE_FOR_ARV_DRUG_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getARVPlanConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ARV_PLAN_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	// concept_id=21148
	public Concept getChangeToArtSecondLine() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_CHANGE_TO_ART_SECOND_LINE_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	// concept_id=1088
	public Concept getRegimeConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_REGIME_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Concept getRestartConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_RESTART_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	// Encounter types
	// encounterType_id = 6
	public EncounterType getAdultoSeguimentoEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_S_TARV_ADULTO_SEGUIMENTO_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	// encounterType_id = 9
	public EncounterType getARVPediatriaSeguimentoEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_S_TARV_PEDIATRIA_SEGUIMENTO_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	// encounterType_id = 5
	public EncounterType getARVAdultInitialEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_S_TARV_ADULTO_INITIAL_A_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	// encounterType_id = 7
	public EncounterType getARVPediatriaInitialEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_S_TARV_PEDIATRIA_INITIAL_A_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	public EncounterType getARVPharmaciaEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_S_TARV_FARMACIA_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	public EncounterType getMisauLaboratorioEncounterType() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	// Programs
	// program_id=2
	public Program getARTProgram() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgram(uuid);
	}
	
	public Program getPtvEtvProgram() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PTV_ETV_PROGRAM_UUID);
		return getProgram(uuid);
	}
	
	// program_id=1
	public Program getHIVCareProgram() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_HIV_CARE_PROGRAM_UUID);
		return getProgram(uuid);
	}
	
	// Program Workflow States
	public ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", "TRANSFERRED OUT TO ANOTHER FACILITY");
	}
	
	public ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		String transferFromOtherUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_TRANSFER_FROM_OTHER_FACILITY_CONCEPT_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", transferFromOtherUuid);
	}
	
	public ProgramWorkflowState getSuspendedTreatmentWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", "SUSPEND TREATMENT");
	}
	
	public ProgramWorkflowState getAbandonedWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", "ABANDONED");
	}
	
	public ProgramWorkflowState getPatientHasDiedWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(
		    EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", "PATIENT HAS DIED");
	}
	
}
