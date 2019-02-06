package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;

public class AgeCohortQueriesTest {

  AgeCohortQueries ageCohortQueries = new AgeCohortQueries();

  @Test
  public void createXtoYAgeCohort_shouldAgeCohortDefinitionWithMinAndMaxAge() {
    AgeCohortDefinition ageCohort =
        (AgeCohortDefinition)
            ageCohortQueries.createXtoYAgeCohort("patients with age between 10 and 14", 10, 14);

    Assert.assertEquals(Integer.valueOf(10), ageCohort.getMinAge());
    Assert.assertEquals(Integer.valueOf(14), ageCohort.getMaxAge());
  }

  @Test
  public void createXtoYAgeCohort_shouldAgeCohortDefinitionWitOnlyMinAge() {
    AgeCohortDefinition ageCohort =
        (AgeCohortDefinition)
            ageCohortQueries.createXtoYAgeCohort("patients with age above 10", 10, null);

    Assert.assertEquals(Integer.valueOf(10), ageCohort.getMinAge());
    Assert.assertNull(ageCohort.getMaxAge());
  }

  @Test
  public void createXtoYAgeCohort_shouldAgeCohortDefinitionWitOnlyMaxAge() {
    AgeCohortDefinition ageCohort =
        (AgeCohortDefinition)
            ageCohortQueries.createXtoYAgeCohort("patients with age bellow 14", null, 14);

    Assert.assertNull(ageCohort.getMinAge());
    Assert.assertEquals(Integer.valueOf(13), ageCohort.getMaxAge());
  }
}
