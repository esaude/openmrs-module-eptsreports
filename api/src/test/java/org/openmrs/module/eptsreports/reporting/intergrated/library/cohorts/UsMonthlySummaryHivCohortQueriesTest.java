package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
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

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInPreArtBooks();
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("locationList", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

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

    assertEquals(4, evaluatedCohort.getMemberIds().size());

    // Registered in ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(8));
    assertTrue(evaluatedCohort.getMemberIds().contains(1004));

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

  @Test
  public void getNewlyEnrolledInArtShouldReturnEnrolledInArtBook1ExcludingTransfers()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInArt();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    // Registered in ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(8));

    // Registered in ART BOOK 1 but transferred from
    assertFalse(evaluatedCohort.getMemberIds().contains(1004));
  }

  @Test
  public void getInArtCareEnrolledByTransferShouldReturnInArtCareTransferredFromOtherFacilities()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtCareEnrolledByTransfer();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    // No value coded and transferred from
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getInArtEnrolledByTransferShouldReturnInArtTransferredFromOtherFacilities()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtEnrolledByTransfer();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    // Registered in ART BOOK 1 and transferred from
    assertTrue(evaluatedCohort.getMemberIds().contains(1004));
  }

  @Test
  public void getRegisteredInPreArtOrArtBooksShouldReturnRegisteredInArtCareOrArt()
      throws EvaluationException {

    EvaluatedCohort evaluatedCohort =
        evaluateCohortDefinition(
            usMonthlySummaryHivCohortQueries.getRegisteredInPreArtOrArtBooks());

    assertEquals(8, evaluatedCohort.getMemberIds().size());

    // Registered in PRE-ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(2));

    // Registered in PRE-ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(6));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(7));

    // No value coded but transferred from
    assertTrue(evaluatedCohort.getMemberIds().contains(1003));

    // Registered in ART BOOK 1
    assertTrue(evaluatedCohort.getMemberIds().contains(8));
    assertTrue(evaluatedCohort.getMemberIds().contains(1004));

    // Registered in ART BOOK 2
    assertTrue(evaluatedCohort.getMemberIds().contains(1001));

    // No value coded
    assertTrue(evaluatedCohort.getMemberIds().contains(1002));
  }

  @Test
  public void getInPreArtWhoScreenedForTbShouldReturnInArtCareWhoWereScreenedForTb()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForTb();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrAfter", "", Date.class), getStartDate());
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), new Location(2));
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    assertTrue(evaluatedCohort.getMemberIds().contains(1005));
  }

  @Test
  @Ignore("DATEDIFF syntax used in TXTBQueries#abandonedWithNoNotification is not compatible with H2")
  public void getAbandonedArtShouldReturnPatientsWhoAbandonedArt() throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getAbandonedArt();
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), new Location(3));
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    // Notified abandonment
    assertTrue(evaluatedCohort.getMemberIds().contains(1006));

    // Abandonment with no notification
    assertTrue(evaluatedCohort.getMemberIds().contains(1007));
  }
}
