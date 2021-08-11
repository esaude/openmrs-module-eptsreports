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
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgeCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * @param name filter name
   * @param minAge minimum age of a patient based on reporting end date
   * @param maxAge maximum age of a patient based on reporting end date
   * @return CohortDefinition
   */
  public CohortDefinition createXtoYAgeCohort(String name, Integer minAge, Integer maxAge) {
    AgeCohortDefinition xToYCohort = new AgeCohortDefinition();
    xToYCohort.setName(name);
    if (minAge != null) {
      xToYCohort.setMinAge(minAge);
    }
    if (maxAge != null) {
      xToYCohort.setMaxAge(maxAge);
    }
    xToYCohort.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
    return xToYCohort;
  }

  /**
   * Person with Unknown age, the birthdate column is null
   *
   * @return CohortDefinition
   */
  public CohortDefinition createUnknownAgeCohort() {
    return genericCohortQueries.generalSql(
        "unknownAge",
        "SELECT p.patient_id FROM patient p JOIN person pr ON p.patient_id = pr.person_id WHERE pr.birthdate IS NULL");
  }
}
