package org.openmrs.module.eptsreports.reporting.unit.library.indicators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;

public class EptsGeneralIndicatorTest {
  private EptsGeneralIndicator eptsGeneralIndicator = new EptsGeneralIndicator();

  @Test
  public void getIndicatorShouldCreateAndReturnNewIndicator() {
    Mapped<CohortDefinition> cd =
        EptsReportUtils.map((CohortDefinition) new AgeCohortDefinition(), "");
    CohortIndicator ci = eptsGeneralIndicator.getIndicator("test", cd);

    assertEquals(3, ci.getParameters().size());
    for (Parameter parameter : ci.getParameters()) {
      assertTrue(parameter.getName().matches("startDate|endDate|location"));
    }
    assertEquals("test", ci.getName());
    assertEquals(cd, ci.getCohortDefinition());
  }
}
