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
import org.springframework.stereotype.Component;

@Component("hivMetadata")
public class HivMetadata extends CommonMetadata {
	
	// ECOUNTER TYPES
	private String S_TARV_ADULTO_SEGUIMENTO = "e278f956-1d5f-11e0-b929-000c29ad1d07";
	
	private String S_TARV_PEDIATRIA_SEGUIMENTO = "e278fce4-1d5f-11e0-b929-000c29ad1d07";
	
	// CONCEPTS
	private String ARVPlan = "e1d9ee10-1d5f-11e0-b929-000c29ad1d07";
	
	// PROGRAMS
	private String ARTProgram = "efe2481f-9e75-4515-8d5a-86bfde2b5ad3";
	
	public EncounterType getAdultoSeguimentoEncounterType() {
		return getEncounterType(S_TARV_ADULTO_SEGUIMENTO);
	}
	
	public EncounterType getARVPediatriaSeguimentoEncounterType() {
		return getEncounterType(S_TARV_PEDIATRIA_SEGUIMENTO);
	}
	
	private String S_TARV_FARMACIA = "e279133c-1d5f-11e0-b929-000c29ad1d07";
	
	public EncounterType getARVPharmaciaEncounterType() {
		return getEncounterType(S_TARV_FARMACIA);
	}
	
	public Concept getARVPlanConcept() {
		return getConcept(ARVPlan);
	}
	
	public Program getARTProgram() {
		return getProgram(ARTProgram);
	}
	
	// ProgramWorkflowState
	
	private String TRANSFERRED_OUT_TO_ANOTHER_FACILITY = "TRANSFERRED OUT TO ANOTHER FACILITY";
	
	private String SUSPEND_TREATMENT = "SUSPEND TREATMENT";
	
	private String ABANDONED = "ABANDONED";
	
	private String PATIENT_HAS_DIED = "PATIENT HAS DIED";
	
	public ProgramWorkflowState gettransferredFromOtherHealthFacilityWorkflowState() {
		return getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", TRANSFERRED_OUT_TO_ANOTHER_FACILITY);
	}
	
	public ProgramWorkflowState getSuspendedTreatmentWorkflowState() {
		return getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", SUSPEND_TREATMENT);
	}
	
	public ProgramWorkflowState getAbandonedWorkflowState() {
		return getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", ABANDONED);
	}
	
	public ProgramWorkflowState getPatientHasDiedWorkflowState() {
		return getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", PATIENT_HAS_DIED);
	}
	
}
