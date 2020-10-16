package org.openmrs.module.eptsreports.reporting.unit.cohort.definition;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.StaticCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class EptsQuarterlyCohortDefinitionTest {

  private EptsQuarterlyCohortDefinition eptsQuarterlyCohortDefinition;

  @Before
  public void setUp() {
    eptsQuarterlyCohortDefinition = new EptsQuarterlyCohortDefinition();
  }

  @Test(expected = IllegalArgumentException.class)
  public void setCohortDefinitionShouldNotAcceptWithoutStartAndEndParameters() {
    eptsQuarterlyCohortDefinition.setCohortDefinition(new StaticCohortDefinition());
  }

  @Test
  public void setCohortDefinitionShouldAcceptWithStartAndEndParameters() {
    StaticCohortDefinition cohortDefinition = new StaticCohortDefinition();
    cohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    eptsQuarterlyCohortDefinition.setCohortDefinition(cohortDefinition);
  }

  @Test
  public void setCohortDefinitionShouldAcceptWithAfterAndBeforeParameters() {
    StaticCohortDefinition cohortDefinition = new StaticCohortDefinition();
    cohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    eptsQuarterlyCohortDefinition.setCohortDefinition(cohortDefinition);
  }
}
