package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class TXNewCohortDefinitionsFGHLiveTest extends DefinitionsFGHLiveTest {
  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Test
  public void getTxNewCompositionCohort() throws EvaluationException {
    EvaluatedCohort result =
        evaluateCodedObsCohortDefinition(txNewCohortQueries.getTxNewCompositionCohort("txNew"));
    Assert.assertEquals(7929, result.size());
  }
}
