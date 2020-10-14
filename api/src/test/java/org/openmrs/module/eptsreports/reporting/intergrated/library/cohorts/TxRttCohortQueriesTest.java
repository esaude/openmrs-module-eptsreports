package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

public class TxRttCohortQueriesTest extends DefinitionsTest {

  @Autowired public TxRttCohortQueries txRttCohortQueries;

  @Before
  public void setUp() throws Exception {
    executeDataSet("metadata.xml");
    executeDataSet("txRttCohortQueriesTest.xml");
    setStartDate(DateUtil.getDateTime(2019, 6, 21));
    setEndDate(DateUtil.getDateTime(2020, 1, 20));
    setLocation(new Location(1));
  }

  @Test
  @Ignore("Query using IF function not available in H2")
  public void getAllPatientsWhoMissedAppointmentBy28Or30DaysButLaterHadVisitShouldPass()
      throws EvaluationException {
    CohortDefinition cd =
        txRttCohortQueries.getAllPatientsWhoMissedAppointmentBy28Or30DaysButLaterHadVisit();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd);
    assertThat(evaluatedCohort.size(), is(1));
    assertThat(evaluatedCohort.getMemberIds(), contains(10101));
  }
}
