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
import org.openmrs.module.eptsreports.reporting.library.queries.PrepCtQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** DataSet for PREP CT */
@Component
public class PrepCtCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  /**
   * Build PrepCt composition cohort definition
   *
   * @param cohortName Cohort name
   * @return CompositionQuery
   */
  public CohortDefinition getClientsNewlyEnrolledInPrep() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("PREP CT");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsNewlyEnrolledInPrep",
                PrepCtQueries.QUERY.findClientsNewlyEnrolledInPrep),
            mappings));

    prepCtCompositionCohort.addSearch(
        "ATLEAST-ONE-FOLLOWUP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithAtLeastOneFollowUpVisitInFichaSeguimento",
                PrepCtQueries.QUERY.findClientsWithAtLeastOneFollowUpVisitInFichaSeguimento),
            mappings));

    prepCtCompositionCohort.addSearch(
        "TRANSFERED-IN-BEFORE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWhoWhereTransferredInBeforeReportingPeriod",
                PrepCtQueries.QUERY.findClientsWhoWhereTransferredInBeforeReportingPeriod),
            mappings));

    prepCtCompositionCohort.addSearch(
        "TRANSFERED-IN-DURING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWhoWhereTransferredInDuringReportingPeriod",
                PrepCtQueries.QUERY.findClientsWhoWhereTransferredInDuringReportingPeriod),
            mappings));

    prepCtCompositionCohort.addSearch(
        "REINITIATED-PREP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWhoReinitiatedPrep", PrepCtQueries.QUERY.findClientsWhoReinitiatedPrep),
            mappings));

    prepCtCompositionCohort.addSearch(
        "CONTINUE-PREP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWhoReinitiatedPrep", PrepCtQueries.QUERY.findClientsWhoContinuePrep),
            mappings));

    prepCtCompositionCohort.setCompositionString(
        "((START-PREP OR TRANSFERED-IN-BEFORE) AND ATLEAST-ONE-FOLLOWUP) OR TRANSFERED-IN-DURING OR REINITIATED-PREP OR CONTINUE-PREP");

    return prepCtCompositionCohort;
  }

  public CohortDefinition getClientsWithPositiveTestResult() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("POSITIVE TEST RESULT");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    prepCtCompositionCohort.addSearch(
        "POSITIVE-TEST",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithPositiveTestResult",
                PrepCtQueries.QUERY.findClientsWithPositiveTestResult),
            mappings));

    prepCtCompositionCohort.setCompositionString("START-PREP AND POSITIVE-TEST");

    return prepCtCompositionCohort;
  }

  public CohortDefinition getClientsWithNegativeTestResult() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("POSITIVE TEST RESULT");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    prepCtCompositionCohort.addSearch(
        "NEGATIVE-TEST",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithPositiveTestResult",
                PrepCtQueries.QUERY.findClientsWithNegativeTestResult),
            mappings));

    prepCtCompositionCohort.setCompositionString("START-PREP AND NEGATIVE-TEST");

    return prepCtCompositionCohort;
  }

  public CohortDefinition getClientsWithIndeterminateTestResult() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("INDETERMINATE TEST RESULT");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    prepCtCompositionCohort.addSearch(
        "INDETERMINATE-TEST",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithIndeterminateTestResult",
                PrepCtQueries.QUERY.findClientsWithIndeterminateTestResult),
            mappings));

    prepCtCompositionCohort.setCompositionString("START-PREP AND INDETERMINATE-TEST");

    return prepCtCompositionCohort;
  }

  public CohortDefinition getClientsWithPregnancyStatusDuringReportingPeriod() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("PREGNANT");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    prepCtCompositionCohort.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithPregnancyStatusDuringReportingPeriod",
                PrepCtQueries.QUERY.findClientsWithPregnancyStatusDuringReportingPeriod),
            mappings));

    prepCtCompositionCohort.setCompositionString("START-PREP AND PREGNANT");

    return prepCtCompositionCohort;
  }

  public CohortDefinition getClientsWithBreastfeedingStatusDuringReportingPeriod() {
    final CompositionCohortDefinition prepCtCompositionCohort = new CompositionCohortDefinition();

    prepCtCompositionCohort.setName("BREASTFEEDING");
    prepCtCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    prepCtCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    prepCtCompositionCohort.addSearch(
        "START-PREP", EptsReportUtils.map(this.getClientsNewlyEnrolledInPrep(), mappings));

    prepCtCompositionCohort.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findClientsWithBreastfeedingStatusDuringReportingPeriod",
                PrepCtQueries.QUERY.findClientsWithBreastfeedingStatusDuringReportingPeriod),
            mappings));

    prepCtCompositionCohort.setCompositionString("START-PREP AND BREASTFEEDING");

    return prepCtCompositionCohort;
  }
}
