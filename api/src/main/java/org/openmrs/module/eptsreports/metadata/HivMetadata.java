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
	
	public EncounterType getAdultoSeguimentoEncounterType() {
		String uuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_S_TARV_ADULTO_SEGUIMENTO_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	public EncounterType getARVPediatriaSeguimentoEncounterType() {
		String uuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_S_TARV_PEDIATRIA_SEGUIMENTO_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	public EncounterType getARVPharmaciaEncounterType() {
		String uuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_S_TARV_FARMACIA_ENCOUNTER_TYPE_UUID);
		return getEncounterType(uuid);
	}
	
	public Concept getARVPlanConcept() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_ARV_PLAN_CONCEPT_UUID);
		return getConcept(uuid);
	}
	
	public Program getARTProgram() {
		String uuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		return getProgram(uuid);
	}
	
	// ProgramWorkflowState
	public ProgramWorkflowState gettransferredFromOtherHealthFacilityWorkflowState() {
		String artProgramUuid = Context.getAdministrationService().getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_ART_PROGRAM_UUID);
		String transferFromOtherUuid = Context.getAdministrationService()
		        .getGlobalProperty(EptsReportConstants.GLOBAL_PROPERTY_TRANSFER_FROM_OTHER_FACILITY_CONCEPT_UUID);
		return getProgramWorkflowState(artProgramUuid, "2", transferFromOtherUuid);
	}
	
}
