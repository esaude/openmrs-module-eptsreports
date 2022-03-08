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
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBMontlyCascadeReportQueries.QUERY.EnrollmentPeriod;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBMontlyCascadeReporCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired
  private TXTBDenominatorForTBMontlyCascadeQueries txtbDenominatorForTBMontlyCascadeQueries;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @DocumentedDefinition(value = "patientsWhoAreCurrentyEnrolledOnArtInTheLastSixMonths")
  public CohortDefinition getPatientsEnrollendOnARTForTheLastSixMonths() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("Patient On ART In The Last 6 Months");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TXTBMontlyCascadeReportQueries.QUERY.findPatientsWhoAreCurrentlyEnrolledOnARTByPeriod(
            EnrollmentPeriod.NEWLY));

    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreCurrentlyEnrolledOnARTInForMoreThanSixMonths")
  public CohortDefinition getPatientsEnrolledOnArtForMoreThanSixMonths() {
    final CompositionCohortDefinition composiiton = new CompositionCohortDefinition();

    composiiton.setName("Patient On ART For More Than 6 Months");
    composiiton.addParameter(new Parameter("endDate", "End Date", Date.class));
    composiiton.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    composiiton.addSearch(
        "previouslyOnAT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PatientsWithClinicalConsultationIntheLastSixMonths",
                TXTBMontlyCascadeReportQueries.QUERY
                    .findPatientsWhoAreCurrentlyEnrolledOnARTByPeriod(EnrollmentPeriod.PREVIOUSLY)),
            mappings));

    composiiton.addSearch(
        "newlyOnArt",
        EptsReportUtils.map(this.getPatientsEnrollendOnARTForTheLastSixMonths(), mappings));

    composiiton.setCompositionString("previouslyOnAT NOT newlyOnArt");

    return composiiton;
  }

  @DocumentedDefinition(value = "patientsWithClinicalConsultationsInTheLastSixMonths")
  public CohortDefinition getClinicalConsultationsInLastSixMonths() {
    final CompositionCohortDefinition composiiton = new CompositionCohortDefinition();

    composiiton.setName("Patients with Clinical Consultations");
    composiiton.addParameter(new Parameter("endDate", "End Date", Date.class));
    composiiton.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    composiiton.addSearch(
        "consultationsLast6Months",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PatientsWithClinicalConsultationIntheLastSixMonths",
                TXTBMontlyCascadeReportQueries.QUERY
                    .findPatientsWithClinicalConsultationIntheLastSixMonths),
            mappings));

    composiiton.setCompositionString("consultationsLast6Months");

    return composiiton;
  }

  @DocumentedDefinition(value = "PatientsWithClinicalConsultationsForMoreThanSixMonths")
  public CohortDefinition gePatientsWithClinicalConsultationsForMoreThanSixMonths() {
    final CompositionCohortDefinition composiiton = new CompositionCohortDefinition();

    composiiton.setName("Patients With Clinical Consulations For more than Six Months");
    composiiton.addParameter(new Parameter("endDate", "End Date", Date.class));
    composiiton.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    composiiton.addSearch(
        "consultationsMoreThan6Months",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PatientsWithClinicalConsultationsForMoreThanSixMonths",
                TXTBMontlyCascadeReportQueries.QUERY
                    .findPatientsWithClinicalConsultationsForMoreThanSixMonths),
            mappings));

    composiiton.setCompositionString("consultationsMoreThan6Months ");

    return composiiton;
  }

  @DocumentedDefinition(value = "TBDenominatorAndTxCurr")
  public CohortDefinition getTxBTDenominatorAndTxCurr() {
    final CompositionCohortDefinition composiiton = new CompositionCohortDefinition();

    composiiton.setName("TX_TB denominator in the last 6 months and TX_CURR ");
    composiiton.addParameter(new Parameter("endDate", "End Date", Date.class));
    composiiton.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "endDate=${endDate},location=${location}";

    composiiton.addSearch(
        "TX-CURR",
        EptsReportUtils.map(this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappings));

    composiiton.addSearch(
        "TX-TB-DENOMINATOR",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascadeQueries.getTxTBDenominator(), mappings));

    composiiton.setCompositionString("TX-CURR and TX-TB-DENOMINATOR ");

    return composiiton;
  }
}
