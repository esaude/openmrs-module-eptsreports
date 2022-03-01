package org.openmrs.module.eptsreports.reporting.unit.cohort.evaluator;

import org.junit.Ignore;

@Ignore
// @PrepareForTest({ EptsCalculationUtils.class, PatientService.class })
public class CalculationCohortDefinitionEvaluatorTest
/**
 * extends PowerMockBaseContextTest { @Spy private PatientCalculationService
 * patientCalculationService = new PatientCalculationServiceImpl(); @Mock private PatientSetService
 * patientSetService;
 *
 * <p>private CalculationCohortDefinition definition;
 *
 * <p>private CalculationCohortDefinitionEvaluator evaluator;
 *
 * <p>private Cohort patients;
 *
 * <p>private CalculationResultMap resultMap;
 *
 * <p>private TestsHelper testsHelper; @Before public void setUp() {
 * PowerMockito.mockStatic(Context.class, EptsCalculationUtils.class, PatientService.class);
 * when(Context.getService(PatientCalculationService.class)).thenReturn(patientCalculationService);
 * when(Context.getPatientSetService()).thenReturn(patientSetService); PatientIdCalculation
 * calculation = new PatientIdCalculation(); definition = new
 * CalculationCohortDefinition(calculation); evaluator = new CalculationCohortDefinitionEvaluator();
 * patients = new Cohort("Test patients", "", new HashSet<>(Arrays.asList(1, 2, 3))); resultMap =
 * getResultMap(); testsHelper = new TestsHelper(); } @Test public void
 * evaluateShouldReturnAnEvaluatedCohort() throws EvaluationException { PatientCalculationContext
 * calculationContext = patientCalculationService.createCalculationContext(); EvaluationContext
 * context = new EvaluationContext();
 * when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
 * when(patientSetService.getAllPatients()).thenReturn(patients);
 * when(EptsCalculationUtils.patientsThatPass(resultMap, definition.getWithResult(),
 * definition.getWithResultFinder(), context)).thenReturn(patients.getMemberIds());
 * when(patientCalculationService.evaluate(patients.getMemberIds(), definition.getCalculation(),
 * definition.getCalculationParameters(), calculationContext)).thenReturn(resultMap);
 * EvaluatedCohort evaluatedCohort = evaluator.evaluate(definition, context);
 * Assert.assertEquals(evaluatedCohort.getMemberIds(), patients.getMemberIds());
 * Assert.assertEquals(evaluatedCohort.getDefinition(), definition);
 * Assert.assertEquals(evaluatedCohort.getContext(), context); } @Test public void
 * evaluateShouldAddLocationToContextCache() throws EvaluationException { Location location = new
 * Location(1); definition.setLocation(location); PatientCalculationContext calculationContext =
 * patientCalculationService.createCalculationContext(); EvaluationContext context = new
 * EvaluationContext(); context.setBaseCohort(new PatientIdSet());
 * when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
 * evaluator.evaluate(definition, context);
 * Assert.assertEquals(calculationContext.getFromCache("location"), location); } @Test public void
 * evaluateShouldAddOnOrAfterDateToContextCache() throws EvaluationException { Date date =
 * testsHelper.getDate("2018-04-10 00:00:00.0"); PatientCalculationContext calculationContext =
 * patientCalculationService.createCalculationContext(); EvaluationContext context = new
 * EvaluationContext(); context.setBaseCohort(new PatientIdSet()); definition.setOnOrAfter(date);
 * when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
 * evaluator.evaluate(definition, context); Assert.assertEquals(date,
 * calculationContext.getFromCache("onOrAfter")); } @Test public void
 * evaluateShouldAddOnOrBeforeDateToContextCache() throws EvaluationException { Date date =
 * testsHelper.getDate("2018-04-10 00:00:00.0"); PatientCalculationContext calculationContext =
 * patientCalculationService.createCalculationContext(); EvaluationContext context = new
 * EvaluationContext(); context.setBaseCohort(new PatientIdSet()); definition.setOnOrBefore(date);
 * when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
 * evaluator.evaluate(definition, context);
 * Assert.assertEquals(calculationContext.getFromCache("onOrBefore"), date); } @Test public void
 * evaluateShouldEvaluateCalculationWithAllPatientsIfNoBaseCohortIsSet() throws EvaluationException
 * { Date date = testsHelper.getDate("2018-04-10 00:00:00.0"); PatientCalculationContext
 * calculationContext = patientCalculationService.createCalculationContext(); EvaluationContext
 * context = new EvaluationContext(); context.addParameterValue("onOrBefore", date);
 * when(patientCalculationService.createCalculationContext()).thenReturn(calculationContext);
 * when(patientSetService.getAllPatients()).thenReturn(patients); evaluator.evaluate(definition,
 * context); verify(patientCalculationService).evaluate(patients.getMemberIds(),
 * definition.getCalculation(), definition.getCalculationParameters(), calculationContext); }
 *
 * <p>private CalculationResultMap getResultMap() { CalculationResultMap map = new
 * CalculationResultMap(); for (Integer id : patients.getMemberIds()) { map.put(id, new
 * SimpleResult(id, definition.getCalculation())); } return map; } }
 */
{}
