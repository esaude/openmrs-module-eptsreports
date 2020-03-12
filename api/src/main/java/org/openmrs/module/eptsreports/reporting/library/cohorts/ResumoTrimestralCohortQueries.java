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

import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralUtil.QUARTERLIES;
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
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";
    cd.addSearch(
        "patientsWhoHaveInitiatedArtTreatment",
        EptsReportUtils.map(
            this.getPatientsWhoHaveInitiatedArtTreatmentCalculation(month, calculator), mapping));

    cd.setCompositionString("patientsWhoHaveInitiatedArtTreatment");
    return cd;
  }

  public CohortDefinition getPatientsForCurrentCohort(
      Month month,
      CohortDefinition cohortDefinitionA,
      CohortDefinition cohortDefinitionB,
      CohortDefinition cohortDefinitionC) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("D - Patients of Current Cohort month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("A", EptsReportUtils.map(cohortDefinitionA, mapping));
    cd.addSearch("B", EptsReportUtils.map(cohortDefinitionB, mapping));
    cd.addSearch("C", EptsReportUtils.map(cohortDefinitionC, mapping));

    cd.setCompositionString("(A OR B) NOT C");
    return cd;
  }

  public CohortDefinition getPatientsWhoAbandonedArtTreatment(
      Month month,
      ResumoTrimestralMonthPeriodCalculation calculatorJ,
      CohortDefinition cohortDefinitionC,
      CohortDefinition cohortDefinitionD,
      CohortDefinition cohortDefinitionI,
      CohortDefinition cohortDefinitionL) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("J - Patients Who Abandoned Art Treatment for month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch(
        "J", EptsReportUtils.map(this.getPatientsForMonthlyCohort(month, calculatorJ), mapping));
    cd.addSearch("C", EptsReportUtils.map(cohortDefinitionC, mapping));
    cd.addSearch("D", EptsReportUtils.map(cohortDefinitionD, mapping));
    cd.addSearch("I", EptsReportUtils.map(cohortDefinitionI, mapping));
    cd.addSearch("L", EptsReportUtils.map(cohortDefinitionL, mapping));

    cd.setCompositionString("(J AND D) NOT (C OR I OR L)");

    return cd;
  }

  public CohortDefinition getPatientsWhoStillInFirstTerapeuticLine(
      Month month,
      ResumoTrimestralMonthPeriodCalculation calculatorE,
      CohortDefinition cohortDefinitionD,
      CohortDefinition cohortDefinitionI,
      CohortDefinition cohortDefinitionJ,
      CohortDefinition cohortDefinitionL) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("E - Patients Who Still on First Line for month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("D", EptsReportUtils.map(cohortDefinitionD, mapping));
    cd.addSearch(
        "E", EptsReportUtils.map(this.getPatientsForMonthlyCohort(month, calculatorE), mapping));
    cd.addSearch("I", EptsReportUtils.map(cohortDefinitionI, mapping));
    cd.addSearch("J", EptsReportUtils.map(cohortDefinitionJ, mapping));
    cd.addSearch("L", EptsReportUtils.map(cohortDefinitionL, mapping));

    cd.setCompositionString("(D NOT (I OR J OR L)) AND E");

    return cd;
  }

  public CohortDefinition getPatientsWhoAreInSecondTerapeuticLine(
      Month month,
      ResumoTrimestralMonthPeriodCalculation calculatorG,
      CohortDefinition cohortDefinitionD,
      CohortDefinition cohortDefinitionI,
      CohortDefinition cohortDefinitionJ,
      CohortDefinition cohortDefinitionL) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("H - Patients Who are in Second Terapeutic Line for month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("D", EptsReportUtils.map(cohortDefinitionD, mapping));
    cd.addSearch(
        "G", EptsReportUtils.map(this.getPatientsForMonthlyCohort(month, calculatorG), mapping));
    cd.addSearch("I", EptsReportUtils.map(cohortDefinitionI, mapping));
    cd.addSearch("J", EptsReportUtils.map(cohortDefinitionJ, mapping));
    cd.addSearch("L", EptsReportUtils.map(cohortDefinitionL, mapping));

    cd.setCompositionString("(D NOT (I OR J OR L)) AND G");

    return cd;
  }

  public CohortDefinition getPatientsWhoStillInFirstTerapeuticLineWithViralLoadResultRegistered(
      Month month,
      ResumoTrimestralMonthPeriodCalculation calculatorF,
      CohortDefinition cohortDefinitionE) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "F - Patients Who Still on First Line With Registered Viral Load for month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("E", EptsReportUtils.map(cohortDefinitionE, mapping));
    cd.addSearch(
        "F", EptsReportUtils.map(this.getPatientsForMonthlyCohort(month, calculatorF), mapping));

    cd.setCompositionString("E AND F");

    return cd;
  }

  public CohortDefinition getPatientsWhoAreInSecondTerapeuticLineWithViralLoadResultRegistered(
      Month month,
      ResumoTrimestralMonthPeriodCalculation calculatorF,
      CohortDefinition cohortDefinitionG) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("F - Patients Who are in Second Line With Registered Viral Load for month " + month);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("G", EptsReportUtils.map(cohortDefinitionG, mapping));
    cd.addSearch(
        "F", EptsReportUtils.map(this.getPatientsForMonthlyCohort(month, calculatorF), mapping));

    cd.setCompositionString("G AND F");

    return cd;
  }

  public CohortDefinition getTotalPatientsQuarterly(
      QUARTERLIES quarterly, List<CohortDefinition> cohortDefinitions) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(" Total Quarterly " + quarterly);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mapping = "year=${year},quarter=${quarter},location=${location}";

    cd.addSearch("A", EptsReportUtils.map(cohortDefinitions.get(0), mapping));
    cd.addSearch("B", EptsReportUtils.map(cohortDefinitions.get(1), mapping));
    cd.addSearch("C", EptsReportUtils.map(cohortDefinitions.get(2), mapping));

    cd.setCompositionString("A OR B OR C");
    return cd;
  }

  @DocumentedDefinition(value = "patient Calculation")
  private CohortDefinition getPatientsWhoHaveInitiatedArtTreatmentCalculation(
      Month month, ResumoTrimestralMonthPeriodCalculation calculator) {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition("Patient Calculation - " + month, calculator);
    cd.addParameter(new Parameter("year", "Year", String.class));
    cd.addParameter(new Parameter("quarter", "Quarter", String.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
