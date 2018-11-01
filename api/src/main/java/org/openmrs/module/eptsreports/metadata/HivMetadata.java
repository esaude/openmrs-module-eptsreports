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
	
	//ECOUNTER TYPES
	public static final String S_TARV_ADULTO_SEGUIMENTO = "e278f956-1d5f-11e0-b929-000c29ad1d07";
	
	public static final String S_TARV_PEDIATRIA_SEGUIMENTO = "e278fce4-1d5f-11e0-b929-000c29ad1d07";
	
	public EncounterType getAdultoSeguimentoEncounterType() {
		return getEncounterType(S_TARV_ADULTO_SEGUIMENTO);
	}
	
	public EncounterType getARVPediatriaSeguimentoEncounterType() {
		return getEncounterType(S_TARV_PEDIATRIA_SEGUIMENTO);
	}
	
	// CONCEPTS
	
	public static final String ARVPlan = "e1d9ee10-1d5f-11e0-b929-000c29ad1d07";
	
	public Concept getARVPlanConcept() {
		return getConcept(ARVPlan);
	}
	
	//PROGRAMS
	public static final String ARTProgram = "efe2481f-9e75-4515-8d5a-86bfde2b5ad3";
	
	public Program getARTProgram() {
		return getProgram(ARTProgram);
	}
	
	// ProgramWorkflowState
	
	public ProgramWorkflowState gettransferredFromOtherHealthFacilityWorkflwoState() {
		return getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", "e1da7d3a-1d5f-11e0-b929-000c29ad1d07");
	}
	
}
