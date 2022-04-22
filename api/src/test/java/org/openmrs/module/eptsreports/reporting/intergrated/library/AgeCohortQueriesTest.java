package org.openmrs.module.eptsreports.reporting.intergrated.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.*;
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

  private Date now = DateUtil.getDateTime(2019, 4, 8);

  @Before
  public void init() {
    parameters.put(new Parameter("effectiveDate", "Effective Date", Date.class), now);
    patientService = Context.getPatientService();
  }

  @Test
  public void createXtoYAgeCohortShouldReturnRightlyAgedPatients() throws EvaluationException {
    assertEquals(44, patientService.getPatient(2).getAge(now).intValue());
    assertEquals(43, patientService.getPatient(6).getAge(now).intValue());
    assertEquals(42, patientService.getPatient(7).getAge(now).intValue());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7)),
        evaluateCohortDefinition(
                ageCohortQueries.createXtoYAgeCohort("allNonVoidedWithAge", null, null), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList()),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("<12", null, 11), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(7)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("0-42", 0, 42), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("43-44", 43, 44), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("43+", 43, null), parameters)
            .getMemberIds());
    assertEquals(
        new HashSet<>(Arrays.asList(2, 6, 7)),
        evaluateCohortDefinition(ageCohortQueries.createXtoYAgeCohort("11-44", 11, 44), parameters)
            .getMemberIds());
  }

  @Test
  public void createUnknownAgeCohortShouldReturnPatientsWithoutAge() throws EvaluationException {
    assertNull(patientService.getPatient(432).getAge(now));
    assertNull(patientService.getPatient(8).getAge(now));
    assertNull(patientService.getPatient(999).getAge(now));
    // unknown includes voided patients
    assertEquals(
        new HashSet<>(Arrays.asList(432, 8, 999)),
        evaluateCohortDefinition(ageCohortQueries.createUnknownAgeCohort(), parameters)
            .getMemberIds());
  }
}
