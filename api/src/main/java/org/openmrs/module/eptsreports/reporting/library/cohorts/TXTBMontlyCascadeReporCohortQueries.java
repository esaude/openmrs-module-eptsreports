/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBMontlyCascadeReportQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class TXTBMontlyCascadeReporCohortQueries {

  @DocumentedDefinition(value = "patientsWhoAreCurrentyEnrolledOnArtInTheLastSixMonths")
  public CohortDefinition getTxCurrForTheLastSixMonths() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsWhoAreCurrentyEnrolledOnArtInTheLastSixMonths");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TXTBMontlyCascadeReportQueries.QUERY
            .findPatientsWhoAreCurrentlyEnrolledOnARTInTheLastSixMonths);

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreCurrentlyEnrolledOnARTInForMoreThanSixMonths")
  public CohortDefinition getTxCurrForMoreThanSixMonths() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsWhoAreCurrentlyEnrolledOnARTInForMoreThanSixMonths");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TXTBMontlyCascadeReportQueries.QUERY
            .findPatientsWhoAreCurrentlyEnrolledOnARTInForMoreThanSixMonths);

    return definition;
  }
}
