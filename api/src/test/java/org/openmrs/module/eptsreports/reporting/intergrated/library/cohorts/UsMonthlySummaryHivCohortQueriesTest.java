package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

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

    assertEquals(4, evaluatedCohort.getMemberIds().size());

    // Registered in PRE-ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(2));

    // Registered in PRE-ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(6));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(7));

    // No value coded but transferred from
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
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

  @Test
  public void getNewlyEnrolledInPreArtBooks1and2ShouldReturnEnrolledInArtBooksExcludingTransfers()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInPreArtBooks();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(3, evaluatedCohort.getMemberIds().size());

    // Registered in PRE-ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(2));

    // Registered in PRE-ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(6));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(7));

    // No value coded but transferred from
    assertFalse(evaluatedCohort.getMemberIds().contains(1003));
  }
}
