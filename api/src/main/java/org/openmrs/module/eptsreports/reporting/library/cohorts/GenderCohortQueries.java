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

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TXCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenderCohortQueries {
	
	@Autowired private TxCurrCohortQueries txCurrCohortQueries;

  /**
   * Patients who are female
   *
   * @return the cohort definition
   */
  public CohortDefinition femaleCohort() {
    GenderCohortDefinition cohort = new GenderCohortDefinition();
    cohort.setName("femaleCohort");
    cohort.setFemaleIncluded(true);
    cohort.setMaleIncluded(false);
    return cohort;
  }

  /**
   * Patients who are male
   *
   * @return the cohort definition
   */
  public CohortDefinition maleCohort() {
    GenderCohortDefinition cohort = new GenderCohortDefinition();
    cohort.setName("maleCohort");
    cohort.setMaleIncluded(true);
    cohort.setFemaleIncluded(false);
    return cohort;
  }
  
  public CohortDefinition getPatinetWhoToLostToFollowUp() {
	  CompositionCohortDefinition  definition  = new CompositionCohortDefinition();
	  
	  
	    String mappings = "onOrBefore=${onOrBefore},location=${location}";
	    
	    definition.addSearch(
	            "31",
	            EptsReportUtils.map(txCurrCohortQueries.
	            		getPatientHavingLastScheduledDrugPickupDate(),
	                mappings));
	    
	    definition.addSearch(
	            "32",
	            EptsReportUtils.map(txCurrCohortQueries.
	            		getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(),
	                mappings));

	  
	  definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
	  definition.addParameter(new Parameter("location", "location", Location.class));
	  definition.setCompositionString(
		        "31 OR  32");
	  
	  return definition;
  }
}
