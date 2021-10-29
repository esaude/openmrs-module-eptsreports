package org.openmrs.module.eptsreports.reporting.intergrated.calculation.cxcascrn;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNBBCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;

import java.util.*;

public class CXCASCRNCalculationBBTest extends BasePatientCalculationTest {

  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(CXCASCRNBBCalculation.class).get(0);
  }

  @Override
  public Collection<Integer> getCohort() {
    return Arrays.asList(new Integer[] {});
  }

  @Override
  public CalculationResultMap getResult() {
    return new CalculationResultMap();
  }

  @Before
  public void setup() throws Exception {
    executeDataSet("cxcascrnBBDataset.xml");
  }

  @Ignore
  /** ignored because H2 database is not capable to test AA4 query */
  @Test
  public void evaluate_ShouldGetPatientBetweenAA3AndBB() {

    PatientCalculationContext context = getEvaluationContext();

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);

    final int patientId = 1001;

    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    Assert.assertNotNull(result);
  }

  @Test
  public void evaluate_ShouldGetPatientsWithCryotherapyDateAndCXCANResultPositive() {

    PatientCalculationContext context = getEvaluationContext();
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);

    final int patientId = 1002;

    CalculationResultMap results =
            service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    System.out.println(result);

    Assert.assertNotNull(result);
    Obs obs = (Obs) result.getValue();

    Concept suspectedCancerConcept = hivMetadata.getSuspectedCancerConcept();

    Assert.assertEquals(hivMetadata.getCryotherapyDateConcept(), obs.getConcept());
  }


  @Test
  public void evaluate_ShouldGetPatientsWithCryotherapyPerformedOnTheSameDayAsTheViaAndCXCANResultNegative() {

    PatientCalculationContext context = getEvaluationContext();
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.NEGATIVE);

    final int patientId = 1003;

    CalculationResultMap results =
            service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);


    Assert.assertNotNull(result);
    Obs obs = (Obs) result.getValue();
    Assert.assertEquals(hivMetadata.getCryotherapyDateConcept(), obs.getConcept());
  }

  @Test
  public void evaluate_ShouldGetPatientsWithViaResultOnTheReferenceAndSuspectedResult() {

    PatientCalculationContext context = getEvaluationContext();
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.SUSPECTED);

    final int patientId = 1004;

    CalculationResultMap results =
            service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);


    Assert.assertNotNull(result);
    Obs obs = (Obs) result.getValue();
    Assert.assertEquals(hivMetadata.getCryotherapyDateConcept(), obs.getConcept());
  }
}
