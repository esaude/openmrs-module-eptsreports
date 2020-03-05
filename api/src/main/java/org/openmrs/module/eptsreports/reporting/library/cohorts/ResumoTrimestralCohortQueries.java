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
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation.QUARTERLIES;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralCohortQueries {

  public CohortDefinition getPatientsForMonthlyCohort(
      Month month, ResumoTrimestralMonthPeriodCalculation calculator) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("A - Get patients who have initiated ART Treatment on month " + month);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "patientsWhoHaveInitiatedArtTreatment",
        EptsReportUtils.map(
            this.getPatientsWhoHaveInitiatedArtTreatmentCalculation(month, calculator), mapping));

    cd.setCompositionString("patientsWhoHaveInitiatedArtTreatment");
    return cd;
  }

  // public CohortDefinition getPatientsWhoWereTransferredIn_B(
  // Month month,
  // ResumoTrimestralMonthPeriodCalculation calculatorB,
  // ResumoTrimestralMonthPeriodCalculation calculatorA) {
  //
  // CompositionCohortDefinition cd = new CompositionCohortDefinition();
  // cd.setName("B - Patient who were transferred in during month " + month);
  // cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
  // cd.addParameter(new Parameter("endDate", "End Date", Date.class));
  // cd.addParameter(new Parameter("location", "Location", Location.class));
  //
  // String mapping =
  // "startDate=${startDate-1y},endDate=${endDate-1y},location=${location}";
  //
  // cd.addSearch(
  // "transferredIn",
  // EptsReportUtils.map(
  // this.getPatientsWhoHaveInitiatedArtTreatmentCalculation(month, calculatorB),
  // mapping));
  //
  // cd.addSearch(
  // "patientsWhoHaveInitiatedArtTreatment",
  // EptsReportUtils.map(
  // this.getPatientsWhoHaveInitiatedArtTreatmentCalculation(month, calculatorA),
  // mapping));
  //
  // cd.setCompositionString("transferredIn AND
  // patientsWhoHaveInitiatedArtTreatment");
  // return cd;
  // }

  public CohortDefinition getTotalPatientsQuarterly(
      QUARTERLIES quarterly, ResumoTrimestralQuarterlyTotalCalculation calculator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total Quarterly " + quarterly);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "totalQuarterly",
        EptsReportUtils.map(
            this.getTotalPatientsQuarterlyCalculation(quarterly, calculator), mapping));

    cd.setCompositionString("totalQuarterly");
    return cd;
  }

  @DocumentedDefinition(value = "patient Calculation")
  private CohortDefinition getPatientsWhoHaveInitiatedArtTreatmentCalculation(
      Month month, ResumoTrimestralMonthPeriodCalculation calculator) {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition("Patient Calculation - " + month, calculator);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "totalQuarterlyCalculation")
  private CohortDefinition getTotalPatientsQuarterlyCalculation(
      QUARTERLIES quarterly, ResumoTrimestralQuarterlyTotalCalculation calculator) {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition("Total Quarterly - " + quarterly, calculator);
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
