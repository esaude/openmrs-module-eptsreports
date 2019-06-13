package org.openmrs.module.eptsreports.reporting.intergrated.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class AgeCohortQueriesTest extends DefinitionsTest {
  @Autowired private AgeCohortQueries ageCohortQueries;

  private Map<Parameter, Object> parameters = new HashMap<>();

  private PatientService patientService;

  @Before
  public void init() {
    parameters.put(
        new Parameter("effectiveDate", "Effective Date", Date.class),
        DateUtil.getDateTime(2019, 4, 8));
    patientService = Context.getPatientService();
  }

  @Test
  public void createXtoYAgeCohortShouldReturnRightlyAgedPatients() throws EvaluationException {
    assertEquals(44, patientService.getPatient(2).getAge().intValue());
    assertEquals(11, patientService.getPatient(6).getAge().intValue());
    assertEquals(42, patientService.getPatient(7).getAge().intValue());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7)),
        evaluateCohortDefinition(
                ageCohortQueries.createXtoYAgeCohort("allNonVoidedWithAge", null, null), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(6)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("<12", null, 11), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(6, 7)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("0-42", 0, 42), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("43-44", 43, 44), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("43+", 43, null), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("11-44", 11, 44), parameters)
            .getMemberIds());
  }

  @Test
  public void createUnknownAgeCohortShouldReturnPatientsWithoutAge() throws EvaluationException {
    assertNull(patientService.getPatient(432).getAge());
    assertNull(patientService.getPatient(8).getAge());
    assertNull(patientService.getPatient(999).getAge());
    // unknown includes voided patients
    assertEquals(
        new HashSet<>(Arrays.asList(432, 8, 999)),
        evaluateCohortDefinition(ageCohortQueries.createUnknownAgeCohort(), parameters)
            .getMemberIds());
  }
}
