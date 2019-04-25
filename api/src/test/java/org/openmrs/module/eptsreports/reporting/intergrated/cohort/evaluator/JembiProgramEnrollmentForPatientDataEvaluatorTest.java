package org.openmrs.module.eptsreports.reporting.intergrated.cohort.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiProgramEnrollmentForPatientDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class JembiProgramEnrollmentForPatientDataEvaluatorTest
    extends BaseModuleContextSensitiveTest {

  @Before
  public void setUp() throws Exception {
    executeDataSet("evaluatorTest.xml");
  }

  @Test
  public void evaluateShouldReturnByDefaultTheFirstProgramEnrollmentForAPatientAtALocation()
      throws EvaluationException {
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new Cohort("1,2"));
    JembiProgramEnrollmentForPatientDefinition def =
        new JembiProgramEnrollmentForPatientDefinition();
    def.setProgram(new Program(1));
    def.setLocation(new Location(1));
    EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
    Assert.assertEquals(2, pd.getData().size());
    PatientProgram pp1 = (PatientProgram) pd.getData().get(1);
    Assert.assertEquals("2008-08-01", DateUtil.formatDate(pp1.getDateEnrolled(), "yyyy-MM-dd"));
    PatientProgram pp2 = (PatientProgram) pd.getData().get(2);
    Assert.assertEquals("2008-08-07", DateUtil.formatDate(pp2.getDateEnrolled(), "yyyy-MM-dd"));
  }

  @Test
  public void evaluateShouldReturnTheLastProgramEnrollmentForAPatientAtALocation()
      throws EvaluationException {
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new Cohort("1,2"));
    JembiProgramEnrollmentForPatientDefinition def =
        new JembiProgramEnrollmentForPatientDefinition();
    def.setProgram(new Program(1));
    def.setLocation(new Location(1));
    def.setWhichEnrollment(TimeQualifier.LAST);
    EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
    Assert.assertEquals(2, pd.getData().size());
    PatientProgram pp1 = (PatientProgram) pd.getData().get(1);
    Assert.assertEquals("2008-08-02", DateUtil.formatDate(pp1.getDateEnrolled(), "yyyy-MM-dd"));
    PatientProgram pp2 = (PatientProgram) pd.getData().get(2);
    Assert.assertEquals("2008-08-07", DateUtil.formatDate(pp2.getDateEnrolled(), "yyyy-MM-dd"));
  }

  @Test
  public void evaluateShouldIgnoreProgramEnrollmentsAtADifferentLocation()
      throws EvaluationException {
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new Cohort("3"));
    JembiProgramEnrollmentForPatientDefinition def =
        new JembiProgramEnrollmentForPatientDefinition();
    def.setProgram(new Program(1));
    def.setLocation(new Location(1));
    def.setWhichEnrollment(TimeQualifier.LAST);
    EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
    Assert.assertEquals(1, pd.getData().size());
    PatientProgram pp = (PatientProgram) pd.getData().get(3);
    Assert.assertEquals("2008-08-07", DateUtil.formatDate(pp.getDateEnrolled(), "yyyy-MM-dd"));
  }

  @Test
  public void evaluateShouldIgnoreVoidedProgramEnrollmentForAPatient() throws EvaluationException {
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new Cohort("4"));
    JembiProgramEnrollmentForPatientDefinition def =
        new JembiProgramEnrollmentForPatientDefinition();
    def.setProgram(new Program(1));
    def.setLocation(new Location(1));
    def.setWhichEnrollment(TimeQualifier.LAST);
    EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def, context);
    Assert.assertEquals(1, pd.getData().size());
    PatientProgram pp = (PatientProgram) pd.getData().get(4);
    Assert.assertEquals("2008-09-08", DateUtil.formatDate(pp.getDateEnrolled(), "yyyy-MM-dd"));
  }
}
