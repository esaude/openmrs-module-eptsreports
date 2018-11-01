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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.data.ConvertedDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;

public class Cohorts { // See how to merge this with CommonQueries.java
	
	public Log log = LogFactory.getLog(getClass());
	
	public static SqlCohortDefinition createPatientsNotVoided() {
		SqlCohortDefinition patientsNotVoided = new SqlCohortDefinition("select distinct p.patient_id from patient p where p.voided=0");
		return patientsNotVoided;
	}
	
	public static AgeCohortDefinition patientWithAgeBelow(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age - 1);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}
	
	public static AgeCohortDefinition patientWithAgeAbove(int age) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}
	
	public static AgeCohortDefinition createXtoYAgeCohort(String name, int minAge, int maxAge) {
		AgeCohortDefinition xToYCohort = new AgeCohortDefinition();
		xToYCohort.setName(name);
		xToYCohort.setMaxAge(new Integer(maxAge));
		xToYCohort.setMinAge(new Integer(minAge));
		xToYCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return xToYCohort;
	}
	
	public static AgeCohortDefinition createOverXAgeCohort(String name, int minAge) {
		AgeCohortDefinition overXCohort = new AgeCohortDefinition();
		overXCohort.setName(name);
		overXCohort.setMinAge(new Integer(minAge));
		overXCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return overXCohort;
	}
	
	// Convenience methods
	
	public static PatientDataDefinition convert(PatientDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		ConvertedPatientDataDefinition convertedDefinition = new ConvertedPatientDataDefinition();
		addAndConvertMappings(pdd, convertedDefinition, renamedParameters, converter);
		return convertedDefinition;
	}
	
	public static PatientDataDefinition convert(PatientDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}
	
	public static PatientDataDefinition convert(PersonDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		return convert(new PersonToPatientDataDefinition(pdd), renamedParameters, converter);
	}
	
	public static PatientDataDefinition convert(PersonDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}
	
	public static EncounterDataDefinition convert(EncounterDataDefinition pdd, Map<String, String> renamedParameters, DataConverter converter) {
		ConvertedEncounterDataDefinition convertedDefinition = new ConvertedEncounterDataDefinition();
		addAndConvertMappings(pdd, convertedDefinition, renamedParameters, converter);
		return convertedDefinition;
	}
	
	public static EncounterDataDefinition convert(EncounterDataDefinition pdd, DataConverter converter) {
		return convert(pdd, null, converter);
	}
	
	public static EncounterQuery convert(EncounterQuery query, Map<String, String> renamedParameters) {
		return new MappedParametersEncounterQuery(query, renamedParameters);
	}
	
	public static CohortDefinition convert(CohortDefinition cd, Map<String, String> renamedParameters) {
		return new MappedParametersCohortDefinition(cd, renamedParameters);
	}
	
	protected static <T extends DataDefinition> void addAndConvertMappings(T copyFrom, ConvertedDataDefinition<T> copyTo, Map<String, String> renamedParameters, DataConverter converter) {
		copyTo.setDefinitionToConvert(ParameterizableUtil.copyAndMap(copyFrom, copyTo, renamedParameters));
		if (converter != null) {
			copyTo.setConverters(Arrays.asList(converter));
		}
	}
	
	public static InStateCohortDefinition patientInStateByEndDate(String name, Program p, String workflowName, Concept state) {
		InStateCohortDefinition inState = new InStateCohortDefinition();
		inState.addState(p.getWorkflowByName(workflowName).getState(state));
		inState.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inState.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		return inState;
	}
	
	public static SqlCohortDefinition patientsWithCodedObsByStartAndEndDates(String name, Concept question, Concept value) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		codedObsCohortDefinition.setQuery("select person_id from obs where concept_id=" + question.getConceptId() + " and value_coded=" + value.getConceptId() + " and voided=0 and obs_datetime >= :onOrAfter and obs_datetime <= :onOrBefore");
		codedObsCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		codedObsCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithCodedObsWithoutStartAndEndDates(String name, Concept question, Concept value) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		codedObsCohortDefinition.setQuery("select person_id from obs where concept_id=" + question.getConceptId() + " and value_coded=" + value.getConceptId() + " and voided=0");
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithCodedObsByStartAndEndDates(String name, List<Concept> questions, List<Concept> values) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		
		StringBuilder qtions = new StringBuilder();
		
		int i = 0;
		for (Concept c : questions) {
			if (i > 0) {
				qtions.append(",");
			}
			qtions.append(c.getId());
			i++;
		}
		
		StringBuilder vs = new StringBuilder();
		
		int j = 0;
		for (Concept c : values) {
			if (j > 0) {
				vs.append(",");
			}
			vs.append(c.getId());
			j++;
		}
		
		codedObsCohortDefinition.setQuery("select person_id from obs where concept_id in (" + qtions.toString() + ") and value_coded in (" + vs.toString() + ") and voided=0 and obs_datetime >= :onOrAfter and obs_datetime <= :onOrBefore");
		codedObsCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		codedObsCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithCodedObs(String name, List<Concept> questions, List<Concept> values) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		
		StringBuilder qtions = new StringBuilder();
		
		int i = 0;
		for (Concept c : questions) {
			if (i > 0) {
				qtions.append(",");
			}
			qtions.append(c.getId());
			i++;
		}
		
		StringBuilder vs = new StringBuilder();
		
		int j = 0;
		for (Concept c : values) {
			if (j > 0) {
				vs.append(",");
			}
			vs.append(c.getId());
			j++;
		}
		
		codedObsCohortDefinition.setQuery("select person_id from obs where concept_id in (" + qtions.toString() + ") and value_coded in (" + vs.toString() + ") and voided=0");
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithCodedObs(String name, Concept value) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		codedObsCohortDefinition.setQuery("select person_id from obs where value_coded=" + value.getConceptId() + " and voided=0");
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithFistCodedObsAndValueByStartAndEndDates(String name, Concept question, Concept value) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		codedObsCohortDefinition.setQuery("select gr.person_id from (select * from (select * from obs where concept_id=" + question.getConceptId() + " and value_coded=" + value.getConceptId() + " and voided=0  order by obs_datetime) o group by o.person_id) gr " + "where gr.obs_datetime>= :onOrAfter and gr.obs_datetime<= :onOrBefore ");
		codedObsCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		codedObsCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		
		return codedObsCohortDefinition;
	}
	
	public static SqlCohortDefinition patientsWithDateObs(String name, Concept concept) {
		SqlCohortDefinition codedObsCohortDefinition = new SqlCohortDefinition();
		codedObsCohortDefinition.setName(name);
		codedObsCohortDefinition.setQuery("select person_id from obs where concept_id=" + concept.getConceptId() + " and voided=0 and value_datetime >= :onOrAfter and value_datetime <= :onOrBefore");
		codedObsCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		codedObsCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		return codedObsCohortDefinition;
	}
	
}
