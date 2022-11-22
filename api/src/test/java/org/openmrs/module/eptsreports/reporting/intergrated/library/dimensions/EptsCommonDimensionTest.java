package org.openmrs.module.eptsreports.reporting.intergrated.library.dimensions;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EptsCommonDimensionTest extends DefinitionsTest {
  private @Autowired EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Test
  public void genderShouldReturnRightCountsPerGender() throws EvaluationException {
    Map<String, Cohort> genderOptions =
        evaluateCohortDefinitionDimension(eptsCommonDimension.gender(), null).getOptionCohorts();
    assertEquals(new HashSet<>(Arrays.asList(2, 6)), genderOptions.get("M").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList(7, 8)), genderOptions.get("F").getMemberIds());
  }

  /**
   * @see org.openmrs.module.eptsreports.reporting.intergrated.library.AgeCohortQueriesTest
   * @throws EvaluationException
   */
  @Test
  public void ageShouldReturnRightAgeCountsForStandardPatients() throws EvaluationException {
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("effectiveDate", "Effective Date", Date.class),
        DateUtil.getDateTime(2019, 4, 8));
    Map<String, Cohort> ageOptions =
        evaluateCohortDefinitionDimension(eptsCommonDimension.age(ageDimensionCohort), parameters)
            .getOptionCohorts();

    assertEquals(new HashSet<>(Arrays.asList(432, 8, 999)), ageOptions.get("UK").getMemberIds());
    assertEquals(new HashSet<>(), ageOptions.get("<1").getMemberIds());
    assertEquals(new HashSet<>(), ageOptions.get("1-4").getMemberIds());
    assertEquals(new HashSet<>(), ageOptions.get("5-9").getMemberIds());
    assertEquals(new HashSet<>(), ageOptions.get("<15").getMemberIds());
    assertEquals(new HashSet<>(), ageOptions.get("10-14").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList(2, 6, 7)), ageOptions.get("15+").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("15-19").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("20-24").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("25-29").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("30-34").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("35-39").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList(2, 6, 7)), ageOptions.get("40-44").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("45-49").getMemberIds());
    assertEquals(new HashSet<>(Arrays.asList()), ageOptions.get("50+").getMemberIds());
  }
}
