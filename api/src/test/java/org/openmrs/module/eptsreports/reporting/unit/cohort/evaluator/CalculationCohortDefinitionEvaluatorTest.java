package org.openmrs.module.eptsreports.reporting.unit.cohort.evaluator;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.Location;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.patient.PatientCalculationServiceImpl;
import org.openmrs.calculation.patient.PatientIdCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.cohort.Cohort;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.evaluator.CalculationCohortDefinitionEvaluator;
import org.openmrs.module.eptsreports.reporting.helper.TestsHelper;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reportingcompatibility.service.ReportService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@Ignore
@PrepareForTest({EptsCalculationUtils.class, PatientService.class})
public class CalculationCohortDefinitionEvaluatorTest extends PowerMockBaseContextTest {

  @Spy
  private PatientCalculationService patientCalculationService = new PatientCalculationServiceImpl();

  @Mock private ReportService patientSetService;

  private CalculationCohortDefinition definition;

  private CalculationCohortDefinitionEvaluator evaluator;

  private Cohort patients;

  private CalculationResultMap resultMap;

  private TestsHelper testsHelper;

  @Before
  public void setUp() {
    PowerMockito.mockStatic(Context.class, EptsCalculationUtils.class, PatientService.class);
    when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
    when(Context.getService(ReportService.class)).thenReturn(patientSetService);
    PatientIdCalculation calculation = new PatientIdCalculation();
    definition = new CalculationCohortDefinition(calculation);
    evaluator = new CalculationCohortDefinitionEvaluator();
    patients = new Cohort("Test patients", "", new HashSet<>(Arrays.asList(1, 2, 3)));
    resultMap = getResultMap();
    testsHelper = new TestsHelper();
  }

  @Test
  public void evaluateShouldReturnAnEvaluatedCohort() throws EvaluationException {
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    EvaluationContext context = new EvaluationContext();
    when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
    when(patientSetService.getAllPatients()).thenReturn(patients);
    when(EptsCalculationUtils.patientsThatPass(
            resultMap, definition.getWithResult(), definition.getWithResultFinder(), context))
        .thenReturn(patients.getMemberIds());
    when(patientCalculationService.evaluate(
            patients.getMemberIds(),
            definition.getCalculation(),
            definition.getCalculationParameters(),
            calculationContext))
        .thenReturn(resultMap);
    EvaluatedCohort evaluatedCohort = evaluator.evaluate(definition, context);
    Assert.assertEquals(evaluatedCohort.getMemberIds(), patients.getMemberIds());
    Assert.assertEquals(evaluatedCohort.getDefinition(), definition);
    Assert.assertEquals(evaluatedCohort.getContext(), context);
  }

  @Test
  public void evaluateShouldAddLocationToContextCache() throws EvaluationException {
    Location location = new Location(1);
    definition.setLocation(location);
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new PatientIdSet());
    when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
    evaluator.evaluate(definition, context);
    Assert.assertEquals(calculationContext.getFromCache("location"), location);
  }

  @Test
  public void evaluateShouldAddOnOrAfterDateToContextCache() throws EvaluationException {
    Date date = testsHelper.getDate("2018-04-10 00:00:00.0");
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new PatientIdSet());
    definition.setOnOrAfter(date);
    when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
    evaluator.evaluate(definition, context);
    Assert.assertEquals(date, calculationContext.getFromCache("onOrAfter"));
  }

  @Test
  public void evaluateShouldAddOnOrBeforeDateToContextCache() throws EvaluationException {
    Date date = testsHelper.getDate("2018-04-10 00:00:00.0");
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    EvaluationContext context = new EvaluationContext();
    context.setBaseCohort(new PatientIdSet());
    definition.setOnOrBefore(date);
    when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
    evaluator.evaluate(definition, context);
    Assert.assertEquals(calculationContext.getFromCache("onOrBefore"), date);
  }

  @Test
  public void evaluateShouldEvaluateCalculationWithAllPatientsIfNoBaseCohortIsSet()
      throws EvaluationException {
    Date date = testsHelper.getDate("2018-04-10 00:00:00.0");
    PatientCalculationContext calculationContext =
        patientCalculationService.createCalculationContext();
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("onOrBefore", date);
    when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
    when(patientSetService.getAllPatients()).thenReturn(patients);
    evaluator.evaluate(definition, context);
    verify(patientCalculationService)
        .evaluate(
            patients.getMemberIds(),
            definition.getCalculation(),
            definition.getCalculationParameters(),
            calculationContext);
  }

  private CalculationResultMap getResultMap() {
    CalculationResultMap map = new CalculationResultMap();
    for (Integer id : patients.getMemberIds()) {
      map.put(id, new SimpleResult(id, definition.getCalculation()));
    }
    return map;
  }
}
