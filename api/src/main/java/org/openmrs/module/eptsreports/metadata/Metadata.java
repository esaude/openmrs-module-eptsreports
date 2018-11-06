/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;

public class Metadata {
	
	protected final static Log log = LogFactory.getLog(Metadata.class);
	
	/**
	 * @return the Program that matches the passed uuid, concept name, name, or primary key id
	 */
	public static Program getProgram(String lookup) {
		Program program = Context.getProgramWorkflowService().getProgramByUuid(lookup);
		if (program == null) {
			program = Context.getProgramWorkflowService().getProgramByName(lookup);
		}
		if (program == null) {
			for (Program p : Context.getProgramWorkflowService().getAllPrograms()) {
				if (p.getName().equalsIgnoreCase(lookup)) {
					program = p;
				}
			}
		}
		if (program == null) {
			try {
				program = Context.getProgramWorkflowService().getProgram(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (program == null) {
			throw new IllegalArgumentException("Unable to find program using key: " + lookup);
		}
		
		return program;
	}
	
	/**
	 * @return the ProgramWorkflow matching the given programLookup and workflowLookup
	 */
	public static ProgramWorkflow getProgramWorkflow(String programLookup, String workflowLookup) {
		Program p = getProgram(programLookup);
		ProgramWorkflow wf = p.getWorkflowByName(workflowLookup);
		
		if (wf == null) {
			for (ProgramWorkflow programWorkflow : p.getAllWorkflows()) {
				if (workflowLookup.equalsIgnoreCase(programWorkflow.getConcept().getName().toString())) {
					wf = programWorkflow;
				} else if (workflowLookup.equalsIgnoreCase(programWorkflow.getUuid())) {
					wf = programWorkflow;
				} else if (workflowLookup.equalsIgnoreCase(programWorkflow.getId().toString())) {
					wf = programWorkflow;
				}
			}
		}
		if (wf == null) {
			throw new IllegalArgumentException("Unable to find workflow using " + programLookup + " - " + workflowLookup);
		}
		return wf;
	}
	
	/**
	 * @return the ProgramWorkflowState matching the given programLookup and workflowLookup and
	 *         stateLookup
	 */
	public static ProgramWorkflowState getProgramWorkflowState(String programLookup, String workflowLookup,
	                                                           String stateLookup) {
		ProgramWorkflow wf = getProgramWorkflow(programLookup, workflowLookup);
		ProgramWorkflowState s = wf.getStateByName(stateLookup);
		if (s == null) {
			for (ProgramWorkflowState state : wf.getStates()) {
				if (stateLookup.equalsIgnoreCase(state.getConcept().getName().toString())) {
					s = state;
				} else if (stateLookup.equalsIgnoreCase(state.getConcept().getUuid())) {
					s = state;
				} else if (stateLookup.equalsIgnoreCase(state.getUuid())) {
					s = state;
				} else if (stateLookup.equalsIgnoreCase(state.getId().toString())) {
					s = state;
				} else if (stateLookup.equalsIgnoreCase(state.getConcept().getId().toString())) {
					s = state;
				}
			}
		}
		if (s == null) {
			throw new IllegalArgumentException(
			        "Unable to find state using " + programLookup + " - " + workflowLookup + " - " + stateLookup);
		}
		return s;
	}
	
	/**
	 * @return the PatientIdentifier that matches the passed uuid, name, or primary key id
	 */
	public static PatientIdentifierType getPatientIdentifierType(String lookup) {
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByUuid(lookup);
		if (pit == null) {
			pit = Context.getPatientService().getPatientIdentifierTypeByName(lookup);
		}
		if (pit == null) {
			try {
				pit = Context.getPatientService().getPatientIdentifierType(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (pit == null) {
			throw new RuntimeException("Unable to find Patient Identifier using key: " + lookup);
		}
		return pit;
	}
	
	/**
	 * @return the Concept that matches the passed uuid, name, source:code mapping, or primary key
	 *         id
	 */
	public static Concept getConcept(String lookup) {
		Concept c = Context.getConceptService().getConceptByUuid(lookup);
		if (c == null) {
			c = Context.getConceptService().getConceptByName(lookup);
		}
		if (c == null) {
			try {
				String[] split = lookup.split("\\:");
				if (split.length == 2) {
					c = Context.getConceptService().getConceptByMapping(split[1], split[0]);
				}
			}
			catch (Exception e) {}
		}
		if (c == null) {
			try {
				c = Context.getConceptService().getConcept(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (c == null) {
			throw new IllegalArgumentException("Unable to find Concept using key: " + lookup);
		}
		return c;
	}
	
	/**
	 * @return the List of Concepts that matches the passed comma-separated list of concept lookups
	 * @see Metadata#getConcept(String)
	 */
	public static List<Concept> getConceptList(String lookup) {
		List<Concept> l = new ArrayList<Concept>();
		if (ObjectUtil.notNull(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(Metadata.getConcept(s));
			}
		}
		return l;
	}
	
	/**
	 * @return the List of Concepts that matches the passed any separated list of concept lookups
	 * @see Metadata#getConcept(String)
	 */
	public static List<Concept> getConceptList(String lookup, String separator) {
		List<Concept> l = new ArrayList<Concept>();
		if (ObjectUtil.notNull(lookup)) {
			if (ObjectUtil.notNull(separator)) {
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(Metadata.getConcept(s));
				}
			} else {
				l.add(Metadata.getConcept(lookup));
			}
		}
		return l;
	}
	
	/**
	 * @return the Form that matches the passed uuid, name, or primary key id
	 */
	public static Form getForm(String lookup) {
		Form form = Context.getFormService().getFormByUuid(lookup);
		if (form == null) {
			form = Context.getFormService().getForm(lookup);
		}
		if (form == null) {
			try {
				form = Context.getFormService().getForm(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (form == null) {
			throw new IllegalArgumentException("Unable to find Form using key: " + lookup);
		}
		return form;
	}
	
	/**
	 * @return the List of Forms that matches the passed comma-separated list of Form lookups
	 * @see Metadata#getForm(String)
	 */
	public static List<Form> getFormList(String lookup) {
		List<Form> l = new ArrayList<Form>();
		if (ObjectUtil.notNull(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(Metadata.getForm(s));
			}
		}
		return l;
	}
	
	/**
	 * @return the List of Forms that matches the passed any separated list of Form lookups
	 * @see Metadata#getForm(String)
	 */
	public static List<Form> getFormList(String lookup, String separator) {
		List<Form> l = new ArrayList<Form>();
		if (ObjectUtil.notNull(lookup)) {
			if (ObjectUtil.notNull(separator)) {
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(Metadata.getForm(s));
				}
			} else {
				l.add(Metadata.getForm(lookup));
			}
		}
		return l;
	}
	
	/**
	 * @return the EncounterType that matches the passed uuid, name, or primary key id
	 */
	public static EncounterType getEncounterType(String lookup) {
		EncounterType et = Context.getEncounterService().getEncounterTypeByUuid(lookup);
		if (et == null) {
			et = Context.getEncounterService().getEncounterType(lookup);
		}
		if (et == null) {
			try {
				et = Context.getEncounterService().getEncounterType(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find EncounterType using key: " + lookup);
		}
		
		return et;
	}
	
	/**
	 * @return the List of EncounterTypes that matches the passed comma-separated list of Encounter
	 *         lookups
	 * @see Metadata#getEncounterType(String)
	 */
	public static List<EncounterType> getEncounterTypeList(String lookup) {
		List<EncounterType> l = new ArrayList<EncounterType>();
		if (ObjectUtil.notNull(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(Metadata.getEncounterType(s));
				
			}
		}
		return l;
	}
	
	/**
	 * @return the List of EncounterTypes that matches the passed any separated list of Encounter
	 *         lookups
	 * @see Metadata#getEncounterType(String)
	 */
	public static List<EncounterType> getEncounterTypeList(String lookup, String separator) {
		List<EncounterType> l = new ArrayList<EncounterType>();
		if (ObjectUtil.notNull(lookup)) {
			if (ObjectUtil.notNull(separator)) {
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(Metadata.getEncounterType(s));
				}
			} else {
				l.add(Metadata.getEncounterType(lookup));
			}
		}
		return l;
	}
	
	/**
	 * @return the RelationshipType that matches the passed uuid, name, or primary key id
	 */
	public static RelationshipType getRelationshipType(String lookup) {
		RelationshipType rt = Context.getPersonService().getRelationshipTypeByUuid(lookup);
		if (rt == null) {
			rt = Context.getPersonService().getRelationshipTypeByName(lookup);
		}
		if (rt == null) {
			try {
				rt = Context.getPersonService().getRelationshipType(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (rt == null) {
			throw new IllegalArgumentException("Unable to find RelationshipType using key: " + lookup);
		}
		return rt;
	}
	
	/**
	 * @return the Location that matches the passed uuid, name, or primary key id
	 */
	public static Location getLocation(String lookup) {
		Location et = Context.getLocationService().getLocationByUuid(lookup);
		if (et == null) {
			et = Context.getLocationService().getLocation(lookup);
		}
		if (et == null) {
			try {
				et = Context.getLocationService().getLocation(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find Location using key: " + lookup);
		}
		
		return et;
	}
	
	/**
	 * @return the PersonAttributeType that matches the passed uuid, name, or primary key id
	 */
	public static PersonAttributeType getPersonAttributeType(String lookup) {
		PersonAttributeType et = Context.getPersonService().getPersonAttributeTypeByUuid(lookup);
		if (et == null) {
			et = Context.getPersonService().getPersonAttributeTypeByName(lookup);
		}
		if (et == null) {
			try {
				et = Context.getPersonService().getPersonAttributeType(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find PersonAttributeType using key: " + lookup);
		}
		
		return et;
	}
}
