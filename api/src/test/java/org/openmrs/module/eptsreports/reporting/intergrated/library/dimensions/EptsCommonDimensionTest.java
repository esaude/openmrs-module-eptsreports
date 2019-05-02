package org.openmrs.module.eptsreports.reporting.intergrated.library.dimensions;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EptsCommonDimensionTest extends DefinitionsTest {
  private @Autowired EptsCommonDimension eptsCommonDimension;

  @Test
  public void genderShouldReturnRightCountsPerGender() throws EvaluationException {
    Map<String, Cohort> genderOptions =
        evaluateCohortDefinitionDimension(eptsCommonDimension.gender(), null).getOptionCohorts();

    assertEquals(new HashSet<>(Arrays.asList(2, 6)), genderOptions.get("M").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList(7, 8)), genderOptions.get("F").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), genderOptions.get("?").getMemberIds());
  }
}
