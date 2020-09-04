package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TxNewCohortQueriesTest extends DefinitionsTest {

  @Autowired public TxNewCohortQueries txNewCohortQueries;

  @Before
  public void setUp() throws Exception {
    executeDataSet("metadata.xml");
    executeDataSet("txNewCohortQueriesTest.xml");
    setStartDate(DateUtil.getDateTime(2019, 6, 21));
    setEndDate(DateUtil.getDateTime(2019, 9, 20));
    setLocation(new Location(1));
  }

  @Test
  public void getTxNewCompositionCohortShouldExcludeTransferredInViaMastercard()
      throws EvaluationException {
    CohortDefinition cohort = txNewCohortQueries.getTxNewCompositionCohort("test");
    Map<Parameter, Object> params = new HashMap<>();
    params.put(new Parameter("onOrAfter", "onOrAfter", Date.class), getStartDate());
    params.put(new Parameter("onOrBefore", "onOrBefore", Date.class), getEndDate());
    params.put(new Parameter("location", "location", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohort, params);

    assertFalse(evaluatedCohort.contains(10000));
    assertEquals(0, evaluatedCohort.size());
  }

  @Test
  public void getTxNewBreastfeedingCompositionShouldReturnBreastfeedingPatients()
      throws EvaluationException {
    CohortDefinition cd = txNewCohortQueries.getTxNewBreastfeedingComposition(false);
    Map<Parameter, Object> params = new HashMap<>();
    params.put(new Parameter("onOrAfter", "onOrAfter", Date.class), getStartDate());
    params.put(new Parameter("onOrBefore", "onOrBefore", Date.class), getEndDate());
    params.put(new Parameter("location", "location", Location.class), getLocation());
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, params);
    assertThat(evaluatedCohort.size(), is(1));
    assertThat(evaluatedCohort.getMemberIds(), contains(1933));
  }

  @Test
  public void getPatientsPregnantEnrolledOnARTShouldReturnPregnantPatients()
      throws EvaluationException {
    CohortDefinition cd = txNewCohortQueries.getPatientsPregnantEnrolledOnART(false);
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertThat(evaluatedCohort.size(), is(1));
    assertThat(evaluatedCohort.getMemberIds(), contains(574));
  }
}
