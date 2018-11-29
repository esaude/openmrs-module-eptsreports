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
	private String S_TARV_ADULTO_SEGUIMENTO = "e278f956-1d5f-11e0-b929-000c29ad1d07"; // encounterType_id=6
	
	private String S_TARV_PEDIATRIA_SEGUIMENTO = "e278fce4-1d5f-11e0-b929-000c29ad1d07"; // encounterType_id = 9
	
	private String MISAU_LABORATORIO = "e2790f68-1d5f-11e0-b929-000c29ad1d07"; // encounterType_id = 13
	
	private String S_TARV_ADULTO_INICIAL_A = "e278f820-1d5f-11e0-b929-000c29ad1d07"; // 5
	
	// CONCEPTS
	private String ARVPlan = "e1d9ee10-1d5f-11e0-b929-000c29ad1d07";
	
	private String HIV_VIRAL_LOAD = "e1d6247e-1d5f-11e0-b929-000c29ad1d07";
	
	private String DATE_OF_DELIVERY = "e1e765c2-1d5f-11e0-b929-000c29ad1d07";
	
	private String CRITERIA_FOR_ART_START = "607315ab-2f52-4d9f-b28a-6383b9a5f9c4";
	
	private String RETURN_VISIT_DATE_FOR_ARV_DRUG = "e1e2efd8-1d5f-11e0-b929-000c29ad1d07";
	
	public Concept getARVPlanConcept() {
		return getConcept(ARVPlan);
	}
	
	public Concept getReturnVisitDateForArvDrugConcept() {
		return getConcept(RETURN_VISIT_DATE_FOR_ARV_DRUG);
	}
	
	// PROGRAMS
	private String ARTProgram = "efe2481f-9e75-4515-8d5a-86bfde2b5ad3";
	
	private String PTV_ETV = "06057245-ca21-43ab-a02f-e861d7e54593";// 8
	
	public EncounterType getAdultoSeguimentoEncounterType() {
		return getEncounterType(S_TARV_ADULTO_SEGUIMENTO);
	}
	
	public EncounterType getARVPediatriaSeguimentoEncounterType() {
		return getEncounterType(S_TARV_PEDIATRIA_SEGUIMENTO);
	}
	
	public EncounterType getARVAdultInitialEncounterType() {
		return getEncounterType(S_TARV_ADULTO_INICIAL_A);
	}
	
	private String S_TARV_FARMACIA = "e279133c-1d5f-11e0-b929-000c29ad1d07";
	
	public EncounterType getARVPharmaciaEncounterType() {
		return getEncounterType(S_TARV_FARMACIA);
	}
	
	public EncounterType getMisauLaboratorioEncounterType() {
		return getEncounterType(MISAU_LABORATORIO);
	}
	
	public Concept getDateOfDelivery() {
		return getConcept(DATE_OF_DELIVERY);
	}
	
	public Concept getCriteriaForArtStart() {
		return getConcept(CRITERIA_FOR_ART_START);
	}
	
	public Concept getHivViralLoadConcept() {
		return getConcept(HIV_VIRAL_LOAD);
	}
	
	public Program getARTProgram() {
		return getProgram(ARTProgram);
	}
	
	public Program getPtvEtvProgram() {
		return getProgram(PTV_ETV);
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
