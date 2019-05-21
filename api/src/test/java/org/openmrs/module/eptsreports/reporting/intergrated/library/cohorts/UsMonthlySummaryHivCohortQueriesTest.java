package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsMonthlySummaryHivCohortQueriesTest extends DefinitionsTest {

  @Autowired private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  @Before
  public void setUp() throws Exception {
    executeDataSet("usMonthlySummaryHivCohortQueriesTest.xml");
  }

  @Test
  public void getRegisteredInPreArtBooksShouldReturnRegisteredInPreArtBooks()
      throws EvaluationException {

    EvaluatedCohort evaluatedCohort =
        evaluateCohortDefinition(usMonthlySummaryHivCohortQueries.getRegisteredInPreArtBooks());

    assertEquals(3, evaluatedCohort.getMemberIds().size());

    // Registered in PRE-ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(2));

    // Registered in PRE-ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(6));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(7));
  }

  @Test
  public void getRegisteredInArtBooksShouldReturnRegisteredInArtBooks() throws EvaluationException {

    EvaluatedCohort evaluatedCohort =
        evaluateCohortDefinition(usMonthlySummaryHivCohortQueries.getRegisteredInArtBooks());

    assertEquals(3, evaluatedCohort.getMemberIds().size());

    // Registered in ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(8));

    // Registered in ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(1001));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(1002));
  }
}
