package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryA;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryB;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryC;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryI;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryJ;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryL;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class ResumoTrimestralCohortDefinitionTest extends DefinitionsFGHLiveTest {

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Test
  public void shouldFindPatientsWhoInitiatedArtTreatmentInReportingPeriod()
      throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(221);
    final Date startDate = DateUtil.getDateTime(2020, 1, 1);
    final Date endDate = DateUtil.getDateTime(2020, 1, 31);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorC =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryC.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorB =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryB.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA_Fev =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.FEBRUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA_MARC =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.MARCH);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorI =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryI.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorJ =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryJ.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorL =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryL.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.JANUARY);

    CohortDefinition cohortDefinitionA =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.JANUARY, resumoTrimestralCalculatorA);

    CohortDefinition cohortDefinitionA_Fev =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.FEBRUARY, resumoTrimestralCalculatorA_Fev);

    CohortDefinition cohortDefinitionA_MARC =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.MARCH, resumoTrimestralCalculatorA_MARC);

    //    CohortDefinition cohortDefinitionB =
    //        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
    //            Month.JANUARY, resumoTrimestralCalculatorB);
    //    CohortDefinition cohortDefinitionC =
    //        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
    //            Month.JANUARY, resumoTrimestralCalculatorC);
    //
    //    CohortDefinition cohortDefinitionI =
    //        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
    //            Month.JANUARY, resumoTrimestralCalculatorI);
    //
    //    CohortDefinition cohortDefinition =
    //        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
    //            Month.JANUARY, resumoTrimestralCalculatorL);

    // CohortDefinition cohortDefinition =
    // resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(Month.JANUARY,
    // resumoTrimestralCalculatorI);

    // CohortDefinition cohortDefinition =
    // resumoTrimestralCohortQueries.getPatientsForCurrentCohort(
    // Month.JANUARY, cohortDefinitionA, cohortDefinitionB, cohortDefinitionC);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("year", "Year", String.class), 2020 + "");
    parameters.put(new Parameter("quarter", "Quarter", String.class), "Trimestre 01");
    parameters.put(new Parameter("location", "Location", Location.class), location);

    final EvaluatedCohort evaluateCohortDefinition1 =
        this.evaluateCohortDefinition(cohortDefinitionA_MARC, parameters);

    System.out.println(evaluateCohortDefinition1.getMemberIds().size());
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "H!$fGH0Mr$";
  }
}
