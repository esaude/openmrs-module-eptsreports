package org.openmrs.module.eptsreports.reporting.intergrated.utils;

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
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryE;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryI;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryJ;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory.ResumoTrimestralIndicatorFactoryL;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class ResumoTrimestralCohortDefinitionTest extends DefinitionsFGHLiveTest {

  @Autowired private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Test
  public void shouldFindPatientsWhoInitiatedArtTreatmentInReportingPeriod()
      throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(221);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorB =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryB.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorC =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryC.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorE =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryE.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);

    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorI =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryI.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);
    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorJ =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryJ.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);
    ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorL =
        Context.getRegisteredComponents(ResumoTrimestralIndicatorFactoryL.class)
            .get(0)
            .getResumoTrimestralCalculator(Month.OCTOBER);

    CohortDefinition cohortDefinitionA =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.OCTOBER, resumoTrimestralCalculatorA);
    CohortDefinition cohortDefinitionB =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.OCTOBER, resumoTrimestralCalculatorB);
    CohortDefinition cohortDefinitionC =
        resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(
            Month.OCTOBER, resumoTrimestralCalculatorC);

    CohortDefinition definitionForSectionD =
        resumoTrimestralCohortQueries.getPatientsForCurrentCohort(
            Month.OCTOBER, cohortDefinitionA, cohortDefinitionB, cohortDefinitionC);

    CohortDefinition definitionForSectionL =
        resumoTrimestralCohortQueries.getPatientsWhoWereRegisteredAsDead(
            Month.OCTOBER, resumoTrimestralCalculatorL, definitionForSectionD);

    CohortDefinition definitionForSectionI =
        resumoTrimestralCohortQueries.findPatientsWhoHaveSuspendedTreatment(
            Month.OCTOBER,
            resumoTrimestralCalculatorE,
            resumoTrimestralCalculatorI,
            definitionForSectionD,
            definitionForSectionL);

    CohortDefinition definitionForSectionJ =
        resumoTrimestralCohortQueries.getPatientsWhoAbandonedArtTreatment(
            Month.OCTOBER,
            resumoTrimestralCalculatorJ,
            definitionForSectionD,
            definitionForSectionI,
            definitionForSectionL);

    CohortDefinition definitionForSectionE =
        resumoTrimestralCohortQueries.getPatientsWhoStillInFirstTerapeuticLine(
            Month.OCTOBER,
            resumoTrimestralCalculatorE,
            definitionForSectionD,
            definitionForSectionI,
            definitionForSectionJ,
            definitionForSectionL);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("year", "Year", String.class), 2019 + "");
    parameters.put(new Parameter("quarter", "Quarter", String.class), "Trimestre 04");
    parameters.put(new Parameter("location", "Location", Location.class), location);

    final EvaluatedCohort evaluateResults =
        this.evaluateCohortDefinition(definitionForSectionJ, parameters);

    System.out.println(evaluateResults.getMemberIds().size());

    for (Integer pId : evaluateResults.getMemberIds()) {

      System.out.println(pId);
    }
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "H!$fGH0Mr$";
  }

  // ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA_Fev =
  // Context
  // .getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class).get(0)
  // .getResumoTrimestralCalculator(Month.FEBRUARY);
  //
  // ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorA_MARC =
  // Context
  // .getRegisteredComponents(ResumoTrimestralIndicatorFactoryA.class).get(0)
  // .getResumoTrimestralCalculator(Month.MARCH);
  //
  // ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorI = Context
  // .getRegisteredComponents(ResumoTrimestralIndicatorFactoryI.class).get(0)
  // .getResumoTrimestralCalculator(Month.JANUARY);
  //
  // ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorJ = Context
  // .getRegisteredComponents(ResumoTrimestralIndicatorFactoryJ.class).get(0)
  // .getResumoTrimestralCalculator(Month.JANUARY);
  //
  // ResumoTrimestralMonthPeriodCalculation resumoTrimestralCalculatorL = Context
  // .getRegisteredComponents(ResumoTrimestralIndicatorFactoryL.class).get(0)
  // .getResumoTrimestralCalculator(Month.JANUARY);
  //
  // CohortDefinition cohortDefinitionA_Fev = resumoTrimestralCohortQueries
  // .getPatientsForMonthlyCohort(Month.FEBRUARY,
  // resumoTrimestralCalculatorA_Fev);
  //
  // CohortDefinition cohortDefinitionA_MARC =
  // resumoTrimestralCohortQueries.getPatientsForMonthlyCohort(Month.MARCH,
  // resumoTrimestralCalculatorA_MARC);
  //
  //
  //
  // CohortDefinition cohortDefinitionA_Fev = resumoTrimestralCohortQueries
  // .getPatientsForMonthlyCohort(Month.NOVEMBER,
  // resumoTrimestralCalculatorA_Fev);
  //
  // resumoTrimestralCohortQueries.getPatientsWhoStillInFirstTerapeuticLine(Month.NOVEMBER,
  // resumoTrimestralCalculatorE, cohortDefinitionD, cohortDefinitionI,
  // cohortDefinitionJ,
  // cohortDefinitionL)

}
