package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

public class HivCohortQueriesTest extends DefinitionsTest {

  @Autowired HivCohortQueries hivCohortQueries;

  @Before
  public void setUp() throws Exception {
    executeDataSet("metadata.xml");
    executeDataSet("hivCohortQueriesTest.xml");
  }

  @Test
  public void getHomosexualKeyPopCohortShouldReturn() throws EvaluationException {
    CohortDefinition cohort = hivCohortQueries.getHomosexualKeyPopCohort();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(10000));
  }

  @Test
  public void getDrugUserKeyPopCohortShouldReturn() throws EvaluationException {
    CohortDefinition cohort = hivCohortQueries.getDrugUserKeyPopCohort();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(10001));
  }

  @Test
  public void getSexWorkerKeyPopCohortShouldReturn() throws EvaluationException {
    CohortDefinition cohort = hivCohortQueries.getSexWorkerKeyPopCohort();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(10002));
  }

  @Test
  public void getImprisonmentKeyPopCohortShouldReturn() throws EvaluationException {
    CohortDefinition cohort = hivCohortQueries.getImprisonmentKeyPopCohort();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.size());
    assertTrue(evaluatedCohort.contains(10003));
  }
}
