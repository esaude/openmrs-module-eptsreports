package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsMonthlySummaryHivCohortQueriesTest extends DefinitionsTest {

  @Autowired private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  public UsMonthlySummaryHivCohortQueriesTest() {
    setStartDate(DateUtil.getDateTime(2014, 4, 21));
    setEndDate(DateUtil.getDateTime(2014, 12, 20));
  }

  @Before
  public void setUp() throws Exception {
    executeDataSet("usMonthlySummaryHivCohortQueriesTest.xml");
  }

  @Test
  public void getRegisteredInPreArtByEndOfPreviousMonthShouldReturnRegistered()
      throws EvaluationException {

    CohortDefinition cohort =
        usMonthlySummaryHivCohortQueries.getRegisteredInPreArtByEndOfPreviousMonth();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    System.out.println("Members: " + evaluatedCohort.getMemberIds());
    assertEquals(2, evaluatedCohort.getMemberIds().size());

    // INSCRITOS
    assertTrue(evaluatedCohort.getMemberIds().contains(2));

    // (INSCRITOFINAL NOT ALGUMAVEZTARV) AND CONSULTA
    assertTrue(evaluatedCohort.getMemberIds().contains(6));
  }

  @Test
  public void getEnrolledInArtShouldReturnEnrolledInArt() throws EvaluationException {

    EvaluatedCohort evaluatedCohort =
        evaluateCohortDefinition(usMonthlySummaryHivCohortQueries.getEnrolledInArt());

    assertEquals(4, evaluatedCohort.getMemberIds().size());
    // Has S.TARV: ADULTO SEGUIMENTO encounter type
    assertTrue(evaluatedCohort.getMemberIds().contains(1009));

    // Is enrolled in ART Program
    assertTrue(evaluatedCohort.getMemberIds().contains(1004));

    // Has S.TARV: FARMACIA
    assertTrue(evaluatedCohort.getMemberIds().contains(1001));

    assertTrue(evaluatedCohort.getMemberIds().contains(9999));
  }

  @Test
  public void getInArtCareEnrolledByTransferShouldReturnInArtCareTransferredFromOtherFacilities()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtCareEnrolledByTransfer();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Test
  public void getInArtEnrolledByTransferShouldReturnInArtTransferredFromOtherFacilities()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtEnrolledByTransfer();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    assertTrue(evaluatedCohort.getMemberIds().contains(9999));
  }

  @Test
  public void getInPreArtWhoScreenedForTbShouldReturnInArtCareWhoWereScreenedForTb()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForTb();
    HashMap<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "", Date.class), getStartDate());
    parameters.put(new Parameter("endDate", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), new Location(2));
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    assertTrue(evaluatedCohort.getMemberIds().contains(1005));
  }

  @Test
  public void getInPreArtWhoScreenedForStiShouldReturnPatientsInArtCareWhoWereScreenedForSti()
      throws EvaluationException {

    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForSti();
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "", Date.class), getStartDate());
    parameters.put(new Parameter("endDate", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), new Location(4));
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, parameters);

    assertEquals(1, evaluatedCohort.getMemberIds().size());

    // NO for STI screening
    assertTrue(evaluatedCohort.getMemberIds().contains(1008));
  }
}
