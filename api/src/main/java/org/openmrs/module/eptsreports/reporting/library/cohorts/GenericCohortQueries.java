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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * Generic Encounter cohort
	 * 
	 * @param types the encounter types
	 * @return the cohort definition
	 */
	public CohortDefinition hasEncounter(EncounterType... types) {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setName("has encounter between dates");
		cd.setTimeQualifier(TimeQualifier.ANY);
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		if (types.length > 0) {
			cd.setEncounterTypeList(Arrays.asList(types));
		}
		return cd;
	}
	
	/**
	 * Generic ProgramEnrollement cohort
	 * 
	 * @param programs the programs
	 * @return the cohort definition
	 */
	public CohortDefinition enrolled(Program... programs) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setName("enrolled in program between dates");
		cd.addParameter(new Parameter("enrolledOnOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("enrolledOnOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		if (programs.length > 0) {
			cd.setPrograms(Arrays.asList(programs));
		}
		return cd;
	}
	
	/**
	 * Generic Coded Observation cohort
	 * 
	 * @param question the question concept
	 * @param values the answers to include
	 * @return the cohort definition
	 */
	public CohortDefinition hasCodedObs(Concept question, TimeModifier timeModifier, SetComparator operator,
	        List<EncounterType> encounterTypes, List<Concept> values) {
		CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
		cd.setName("has obs between dates");
		cd.setQuestion(question);
		cd.setOperator(operator);
		cd.setTimeModifier(timeModifier);
		cd.setEncounterTypeList(encounterTypes);
		cd.setValueList(values);
		
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		
		return cd;
	}
	
	/**
	 * Generic Coded Observation cohort with default parameters defined
	 * 
	 * @param question the question concept
	 * @param values the answers to include
	 * @return the cohort definition
	 */
	public CohortDefinition hasCodedObs(Concept question, List<Concept> values) {
		return hasCodedObs(question, BaseObsCohortDefinition.TimeModifier.ANY, SetComparator.IN, null, values);
	}
	
	/**
	 * Generic SQL cohort
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "generalSql")
	public CohortDefinition generalSql(String name, String query) {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName(name);
		sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
		sql.addParameter(new Parameter("endDate", "End Date", Date.class));
		sql.addParameter(new Parameter("location", "Facility", Location.class));
		sql.setQuery(query);
		return sql;
	}
	
	/**
	 * Generic InProgram Cohort
	 * 
	 * @param program the programs
	 * @return the cohort definition
	 */
	public CohortDefinition createInProgram(String name, Program program) {
		InProgramCohortDefinition inProgram = new InProgramCohortDefinition();
		inProgram.setName(name);
		
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		
		inProgram.setPrograms(programs);
		inProgram.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		inProgram.addParameter(new Parameter("locations", "Location", Location.class));
		return inProgram;
	}
	
	/**
	 * Base cohort for the pepfar report
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBaseCohort() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("arvAdultInitialEncounterTypeId",
		    String.valueOf(hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId()));
		parameters.put("arvPediatriaInitialEncounterTypeId",
		    String.valueOf(hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId()));
		parameters.put("hivCareProgramId", String.valueOf(hivMetadata.getHIVCareProgram().getProgramId()));
		parameters.put("artProgramId", String.valueOf(hivMetadata.getARTProgram().getProgramId()));
		return generalSql("baseCohort", BaseQueries.getBaseCohortQuery(parameters));
	}
	
	/**
	 * Finds patients that do not have birthdate defined
	 * 
	 * @return
	 */
	public CohortDefinition getUnknownAgeCohort() {
		SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("unknownAgeCohort");
		definition.setQuery("select patient.patient_id from patient join person on person.person_id = patient.patient_id "
		        + "where patient.voided = 0 and person.voided = 0 and person.birthdate is null");
		definition.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return definition;
	}
	
	/**
	 * Get patient status per program
	 * 
	 * @param state
	 * @param program
	 * @return
	 */
	public CohortDefinition getPatientsToExcludeBasedOnStates(int program, int state) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("The exclusion criteria by work flow states");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String query = "SELECT pg.patient_id"
		        + " FROM patient p"
		        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
		        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
		        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
		        + " pg.program_id="
		        + program
		        + " AND ps.state="
		        + state
		        + " AND ps.start_date=pg.date_enrolled AND"
		        + " ps.start_date BETWEEN date_add(date_add(:endDate, interval -4 month), interval 1 day) AND date_add(:endDate, interval -3 month) and location_id=:location";
		cd.setQuery(query);
		return cd;
	}
	
	/**
	 * Get LTFU patients
	 * 
	 * @return
	 */
	public CohortDefinition getLostToFollowUpPatients() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who are lost to follow up");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		// get patients who have next scheduled drugs pick up date and never returned 60
		// days later
		SqlCohortDefinition missedDrugPickUp = new SqlCohortDefinition();
		missedDrugPickUp.setName("patientsThatMissedNextDrugPickup");
		missedDrugPickUp.addParameter(new Parameter("endDate", "End Date", Date.class));
		missedDrugPickUp.addParameter(new Parameter("location", "location", Location.class));
		String query = "SELECT patient_id FROM ( SELECT p.patient_id,MAX(encounter_datetime) encounter_datetime FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=%s"
		        + " AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id ) max_frida INNER JOIN obs o ON o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%s"
		        + " AND o.location_id=:location AND datediff(:endDate,o.value_datetime)>=60";
		missedDrugPickUp.setQuery(String.format(query, hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
		    hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
		
		// get patients who have no drug pick up date but have next appointment date and
		// have NOT shown up 60 days later
		SqlCohortDefinition missedNextVisit = new SqlCohortDefinition();
		missedNextVisit.setName("patientsThatMissNextConsultation");
		missedNextVisit.addParameter(new Parameter("endDate", "End Date", Date.class));
		missedNextVisit.addParameter(new Parameter("location", "location", Location.class));
		missedNextVisit.setQuery("SELECT patient_id FROM "
		        + "( SELECT p.patient_id,MAX(encounter_datetime) encounter_datetime "
		        + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id "
		        + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type in ("
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ", "
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") "
		        + "AND e.location_id=:location AND e.encounter_datetime<=:endDate GROUP BY p.patient_id ) max_mov "
		        + "INNER JOIN obs o ON o.person_id=max_mov.patient_id "
		        + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id="
		        + hivMetadata.getReturnVisitDateConcept().getConceptId()
		        + " AND o.location_id=:location AND DATEDIFF(:endDate,o.value_datetime)>=60");
		cd.addSearch("missedPickUps", EptsReportUtils.map(missedDrugPickUp, "endDate=${endDate},location=${location}"));
		cd.addSearch("missedNextVisit", EptsReportUtils.map(missedNextVisit, "endDate=${endDate},location=${location}"));
		cd.setCompositionString("missedPickUps OR missedNextVisit");
		return cd;
	}
	
}
