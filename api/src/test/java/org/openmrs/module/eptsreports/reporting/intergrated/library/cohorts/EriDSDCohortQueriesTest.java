package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

public class EriDSDCohortQueriesTest extends DefinitionsTest {

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;

  private Cohort baseCohort;

  @Before
  public void setUp() throws Exception {
    executeDataSet("metadata.xml");
    executeDataSet("eriDSDCohortQueriesTest.xml");
    setStartDate(DateUtil.getDateTime(2019, 12, 21));
    setEndDate(DateUtil.getDateTime(2020, 1, 20));
    setLocation(Context.getLocationService().getLocation(221));
    this.baseCohort = new Cohort("574, 1933");
  }

  @Test
  @Ignore("Using DATE_SUB on tb query")
  public void getPregnantAndBreastfeedingAndOnTBTreatmentShouldReturnBreastfeedingPatients()
      throws EvaluationException {
    CohortDefinition cd = eriDSDCohortQueries.getPregnantAndBreastfeedingAndOnTBTreatment();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, baseCohort);
    assertThat(evaluatedCohort.getMemberIds(), contains(1933));
  }

  @Test
  @Ignore("Using DATE_SUB on tb query")
  public void getPregnantAndBreastfeedingAndOnTBTreatmentShouldReturnPregnantPatients()
      throws EvaluationException {
    CohortDefinition cd = eriDSDCohortQueries.getPregnantAndBreastfeedingAndOnTBTreatment();
    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, baseCohort);
    assertThat(evaluatedCohort.getMemberIds(), contains(574));
  }
}
