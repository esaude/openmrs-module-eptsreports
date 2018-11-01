/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class DataFactory {
	
	public Log log = LogFactory.getLog(getClass());
	
	public AgeCohortDefinition patientWithAgeBelow(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age - 1);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}
	
	public AgeCohortDefinition patientWithAgeAbove(int age) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}
	
	public AgeCohortDefinition createXtoYAgeCohort(String name, int minAge, int maxAge) {
		AgeCohortDefinition xToYCohort = new AgeCohortDefinition();
		xToYCohort.setName(name);
		xToYCohort.setMaxAge(new Integer(maxAge));
		xToYCohort.setMinAge(new Integer(minAge));
		xToYCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return xToYCohort;
	}
	
	public AgeCohortDefinition createOverXAgeCohort(String name, int minAge) {
		AgeCohortDefinition overXCohort = new AgeCohortDefinition();
		overXCohort.setName(name);
		overXCohort.setMinAge(new Integer(minAge));
		overXCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return overXCohort;
	}
	
}
