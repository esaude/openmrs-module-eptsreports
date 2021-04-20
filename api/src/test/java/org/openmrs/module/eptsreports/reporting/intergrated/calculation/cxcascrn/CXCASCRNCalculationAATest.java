package org.openmrs.module.eptsreports.reporting.intergrated.calculation.cxcascrn;

import java.util.*;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNAACalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;

public class CXCASCRNCalculationAATest extends BasePatientCalculationTest {
  @Override
  public PatientCalculation getCalculation() {
    return Context.getRegisteredComponents(CXCASCRNAACalculation.class).get(0);
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
    executeDataSet("cxcascrnAADataset.xml");
  }

  @Test
  public void evaluate_ShouldGetPatientWithFichaCCU() {

    PatientCalculationContext context = getEvaluationContext();

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.SUSPECTED);

    final int patientId = 1001;

    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    Assert.assertNotNull(result);

    Obs obs = (Obs) result.getValue();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    Concept resultadoViaConcept = hivMetadata.getResultadoViaConcept();
    Concept suspectedCancerConcept = hivMetadata.getSuspectedCancerConcept();

    Assert.assertEquals(resultadoViaConcept, obs.getConcept());
    Assert.assertEquals(suspectedCancerConcept, obs.getValueCoded());
  }

  @Ignore(" FichaClinica And FichaResumo and resumo are no longer evaluated   ")
  @Test
  public void evaluateFichaClinicaAndFichaResumo_ShouldPickFichaResumo() {

    PatientCalculationContext context = getEvaluationContext();

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.NEGATIVE);

    final int patientId = 1002;

    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    Assert.assertNotNull(result);

    Obs obs = (Obs) result.getValue();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EncounterType masterCardEncounterType = hivMetadata.getMasterCardEncounterType();

    Assert.assertEquals(masterCardEncounterType, obs.getEncounter().getEncounterType());
  }

  @Test
  public void evaluate_shouldGetEarliestFichaCCU() {

    PatientCalculationContext context = getEvaluationContext();

    Calendar endDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
    Calendar startDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);

    endDate.set(2020, Calendar.JANUARY, 20);
    startDate.set(2019, Calendar.OCTOBER, 21);

    context.addToCache("onOrBefore", endDate.getTime());
    context.addToCache("onOrAfter", startDate.getTime());

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("result", CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);

    final int patientId = 1003;

    CalculationResultMap results =
        service.evaluate(Arrays.asList(patientId), getCalculation(), parameterValues, context);

    SimpleResult result = (SimpleResult) results.get(patientId);

    Assert.assertNotNull(result);

    Obs obs = (Obs) result.getValue();

    Assert.assertEquals("c7794986-6b0a-11eb-b8b2-c7c88a5c0e93", obs.getUuid());
  }
}
