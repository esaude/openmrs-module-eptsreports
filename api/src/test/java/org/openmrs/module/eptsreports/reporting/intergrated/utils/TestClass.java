package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

public class Test extends DefinitionsFGHLiveTest {
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @org.junit.Test
  public void test() throws EvaluationException {
    CohortDefinition cohort =
        resumoMensalCohortQueries.getPatientsWhoRestartedTreatmentDuringCurrentMonthB3();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }
}
