package org.openmrs.module.eptsreports.reporting.intergrated.library;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class GenderCohortQueriesTest extends DefinitionsTest {
  @Autowired private GenderCohortQueries genderCohortQueries;

  private PatientService patientService;

  @Before
  public void init() {
    patientService = Context.getPatientService();
  }

  @Test
  public void femaleCohortShouldReturnFemalePatients() throws EvaluationException {
    assertEquals("F", patientService.getPatient(7).getGender());
    assertEquals("F", patientService.getPatient(8).getGender());
    assertEquals(
        new HashSet<>(Arrays.asList(7, 8)),
        evaluateCohortDefinition(
                genderCohortQueries.femaleCohort(), Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
  }

  @Test
  public void maleCohortShouldReturnMalePatients() throws EvaluationException {
    assertEquals("M", patientService.getPatient(2).getGender());
    assertEquals("M", patientService.getPatient(6).getGender());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6)),
        evaluateCohortDefinition(
                genderCohortQueries.maleCohort(), Collections.<Parameter, Object>emptyMap())
            .getMemberIds());
  }
}
