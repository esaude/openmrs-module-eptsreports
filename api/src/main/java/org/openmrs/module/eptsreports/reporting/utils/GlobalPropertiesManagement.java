/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.reporting.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.Metadata;

public class GlobalPropertiesManagement {
	
	protected final static Log log = LogFactory.getLog(GlobalPropertiesManagement.class);
	
	public Program getProgram(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getProgram(globalProperty);
	}
	
	public PatientIdentifierType getPatientIdentifier(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getPatientIdentifierType(globalProperty);
	}
	
	public Concept getConcept(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getConcept(globalProperty);
	}
	
	public List<Concept> getConceptList(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getConceptList(globalProperty);
	}
	
	public List<Concept> getConceptList(String globalPropertyName, String separator) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getConceptList(globalProperty, separator);
	}
	
	public List<Concept> getConceptsByConceptSet(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		Concept c = Metadata.getConcept(globalProperty);
		return Context.getConceptService().getConceptsByConceptSet(c);
	}
	
	public Form getForm(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getForm(globalProperty);
	}
	
	public EncounterType getEncounterType(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getEncounterType(globalProperty);
	}
	
	public List<EncounterType> getEncounterTypeList(String globalPropertyName, String separator) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getEncounterTypeList(globalProperty, separator);
	}
	
	public List<EncounterType> getEncounterTypeList(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getEncounterTypeList(globalProperty);
	}
	
	public List<Form> getFormList(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getFormList(globalProperty);
	}
	
	public List<Form> getFormList(String globalPropertyName, String separator) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getFormList(globalProperty, separator);
	}
	
	public RelationshipType getRelationshipType(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getRelationshipType(globalProperty);
	}
	
	public ProgramWorkflow getProgramWorkflow(String globalPropertyName, String programName) {
		String programGp = Context.getAdministrationService().getGlobalProperty(programName);
		String workflowGp = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getProgramWorkflow(programGp, workflowGp);
	}
	
	public ProgramWorkflowState getProgramWorkflowState(String globalPropertyName, String workflowName, String programName) {
		String programGp = Context.getAdministrationService().getGlobalProperty(programName);
		String workflowGp = Context.getAdministrationService().getGlobalProperty(workflowName);
		String stateGp = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getProgramWorkflowState(programGp, workflowGp, stateGp);
		
	}
	
	public List<ProgramWorkflowState> getProgramWorkflowStateList(String globalPropertyName) {
		String programGp = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Metadata.getProgramWorkflowstateList(programGp);
		
	}
	
	public Map<Concept, Double> getVialSizes() {
		Map<Concept, Double> vialSizes = new HashMap<Concept, Double>();
		String vialGp = Context.getAdministrationService().getGlobalProperty("reports.vialSizes");
		String[] vials = vialGp.split(",");
		for (String vial : vials) {
			String[] v = vial.split(":");
			try {
				Concept drugConcept = Metadata.getConcept(v[0]);
				Double size = Double.parseDouble(v[1]);
				vialSizes.put(drugConcept, size);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Unable to convert " + vial + " into a vial size Concept and Double", e);
			}
		}
		return vialSizes;
	}
	
	public Integer getGlobalPropertyAsInt(String globalPropertyName) {
		String globalProperty = Context.getAdministrationService().getGlobalProperty(globalPropertyName);
		return Integer.parseInt(globalProperty);
	}
	
}
