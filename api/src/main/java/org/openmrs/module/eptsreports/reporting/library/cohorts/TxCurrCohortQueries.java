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
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispense6OrMoreMonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispenseLessThan3MonthCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** Defines all of the TxCurrCohortQueries we want to expose for EPTS */
@Component
public class TxCurrCohortQueries {

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findPatientsWhoAreActiveOnART() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxCurrQueries.QUERY.findPatientsWhoAreCurrentlyEnrolledOnART);

    return definition;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseForLessThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation less than 3 months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";
    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));
    cd.addSearch(
        "arvDispenseForLessThan3Months",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseForLessThan3MonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseForLessThan3Months");
    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseBetween3And5Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation Between 3 and 5 Months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";

    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));

    cd.addSearch(
        "arvDispenseBetween3And5Months",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseBetween3And5MonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseBetween3And5Months");
    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseFor6OrMoreMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation For 6 Or More Months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";

    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));

    cd.addSearch(
        "arvDispenseFor6OrMoreMonths",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseFor6OrMoreMonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseFor6OrMoreMonths");
    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseForLessThan3Months")
  private CohortDefinition getPatientsOnArtOnArvDispenseForLessThan3MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseForLessThan3Months",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispenseLessThan3MonthCalculation.class)
                .get(0));

    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseBetween3And5Months")
  private CohortDefinition getPatientsOnArtOnArvDispenseBetween3And5MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseBetween3And5Months",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation.class)
                .get(0));

    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseFor6OrMoreMonths")
  private CohortDefinition getPatientsOnArtOnArvDispenseFor6OrMoreMonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseFor6OrMoreMonths",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispense6OrMoreMonthsCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
