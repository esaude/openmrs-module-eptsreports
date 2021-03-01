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
import org.openmrs.module.eptsreports.reporting.library.queries.IMR1Queries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class IMR1CohortQueries {

  @DocumentedDefinition(value = "PatientsNewlyEnrolledOnArtCare")
  public CohortDefinition getPatientsNewlyEnrolledOnArtCare() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("Patients newly enrolled on ART Care");
    final String mappings = "endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "NEWLY-ENROLLED",
        EptsReportUtils.map(this.getAllPatientsEnrolledInArtCareDuringReportingPeriod(), mappings));

    compsitionDefinition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(this.getAllPatientsTransferredInByEndReportingDate(), mappings));

    compsitionDefinition.setCompositionString("NEWLY-ENROLLED NOT TRANSFERRED-IN");

    return compsitionDefinition;
  }

  @DocumentedDefinition(
      value = "PatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastFeedingDenominator")
  public CohortDefinition
      getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastfeedingDenominator() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("Patients newly enrolled on ART Care excluding Pregnants");
    final String mappings = "endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "DENOMINATOR", EptsReportUtils.map(this.getPatientsNewlyEnrolledOnArtCare(), mappings));

    compsitionDefinition.addSearch(
        "PREGNANT", EptsReportUtils.map(this.getAllPatientsWhoArePregnantInAPeriod(), mappings));

    compsitionDefinition.addSearch(
        "BREASTFEEDING", EptsReportUtils.map(this.getAllPatientsWhoAreBreastfeeding(), mappings));

    compsitionDefinition.setCompositionString("DENOMINATOR NOT (PREGNANT OR BREASTFEEDING)");

    return compsitionDefinition;
  }

  @DocumentedDefinition(
      value = "PatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastFeedingNumerator")
  public CohortDefinition
      getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreasFeedingNumerator() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("Patients newly enrolled on ART Care excluding Pregnants");
    final String mappings = "endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(this.getPatientsNewlyEnrolledOnArtCareNumerator(), mappings));

    compsitionDefinition.addSearch(
        "PREGNANT", EptsReportUtils.map(this.getAllPatientsWhoArePregnantInAPeriod(), mappings));

    compsitionDefinition.addSearch(
        "BREASTFEEDING", EptsReportUtils.map(this.getAllPatientsWhoAreBreastfeeding(), mappings));

    compsitionDefinition.setCompositionString("NUMERATOR NOT (PREGNANT OR BREASTFEEDING)");

    return compsitionDefinition;
  }

  @DocumentedDefinition(value = "PatientsNewlyEnrolledOnArtCareNumerator")
  public CohortDefinition getPatientsNewlyEnrolledOnArtCareNumerator() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("Patients newly enrolled on ART Care");
    final String mappings = "endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "NEWLY-ENROLLED",
        EptsReportUtils.map(this.getAllPatientsEnrolledInArtCareDuringReportingPeriod(), mappings));

    compsitionDefinition.addSearch(
        "CONSULTATION-SAME-DIAGNOSIS-DAY",
        EptsReportUtils.map(
            this
                .getAllPatientsWithClinicianConsultationInTheSameDiagnosisDayByBeforeEndOfReportingPeriod(),
            "endDate=${endDate-1m},location=${location}"));

    compsitionDefinition.addSearch(
        "TRANSFERRED-IN",
        EptsReportUtils.map(this.getAllPatientsTransferredInByEndReportingDate(), mappings));

    compsitionDefinition.setCompositionString(
        "(NEWLY-ENROLLED AND CONSULTATION-SAME-DIAGNOSIS-DAY) NOT TRANSFERRED-IN");

    return compsitionDefinition;
  }

  @DocumentedDefinition(value = "PatientsEnrolledInArtCareDuringReportingPeriod")
  public CohortDefinition getAllPatientsEnrolledInArtCareDuringReportingPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("PatientsEnrolledInArtCareDuringReportingPeriod Cohort");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(IMR1Queries.QUERY.findPatientsEnrolledInArtCareDuringReportingPeriod);

    return definition;
  }

  @DocumentedDefinition(value = "PatientsWhoAreBreastfeeding")
  public CohortDefinition getAllPatientsWhoAreBreastfeeding() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("PatientsWhoAreBreastfeeding Cohort");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(IMR1Queries.QUERY.findPatientsWhoAreBreastfeeding);

    return definition;
  }

  @DocumentedDefinition(value = "PatientsWhoArePregnantInAPeriod")
  public CohortDefinition getAllPatientsWhoArePregnantInAPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("PatientsWhoArePregnantInAPeriod Cohort");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(IMR1Queries.QUERY.findPatientsWhoArePregnantInAPeriod);

    return definition;
  }

  @DocumentedDefinition(value = "PatientsTransferredInByEndReportingDate")
  public CohortDefinition getAllPatientsTransferredInByEndReportingDate() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("PatientsTransferredInByEndReportingDate Cohort");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(IMR1Queries.QUERY.findPatiensTransferredInByEndDate);

    return definition;
  }

  @DocumentedDefinition(value = "PatientsWithClinicianConsultationInTheSameDiagnosisDay")
  private CohortDefinition
      getAllPatientsWithClinicianConsultationInTheSameDiagnosisDayByBeforeEndOfReportingPeriod() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("PatientsWithClinicianConsultationInTheSameDiagnosisDay Cohort");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(
        IMR1Queries.QUERY.findPatientsWithClinicianConsultationInTheSameDiagnosisDay);

    return definition;
  }
}
