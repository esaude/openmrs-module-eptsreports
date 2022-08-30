package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class TPTInitiationCohortQueriesTest extends DefinitionsTest {

  private TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Before
  public void setup() throws Exception {
    tptInitiationCohortQueries =
        new TPTInitiationCohortQueries(
            new HivMetadata(), new TbMetadata(), new GenericCohortQueries());
    executeDataSet("tptInitiation-DataTest.xml");
  }

  @Test
  public void getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoShoulPass()
      throws EvaluationException {
    TPTInitiationCohortQueries tpt =
        new TPTInitiationCohortQueries(
            new HivMetadata(), new TbMetadata(), new GenericCohortQueries());

    CohortDefinition cd = tpt.getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamento();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  @Ignore("Funtions are not supported by H2")
  public void getPatientsWithUltimaProfilaxia3hpShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hp();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  @Ignore("Funtions are not supported by H2")
  public void getPatientWithProfilaxiaTpt3hpShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hp();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  @Ignore("Funtions are not supported by H2")
  public void getPatientsWithOutrasPerscricoesDT3HPShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HP();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  @Ignore("Funtions are not supported by H2")
  public void getPatientsWithRegimeDeTPT3HPShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HP();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  public void getPatientsWithFichaResumoUltimaProfilaxiaShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxia();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  public void getPatientsWithFichaClinicaProfilaxiaINHShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINH();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  public void getPatientsIPTStart3WithFichaClinicaOrFichaPediatricaShouldPass()
      throws EvaluationException {

    CohortDefinition cd =
        tptInitiationCohortQueries.getPatientsIPTStart3WithFichaClinicaOrFichaPediatrica();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  @Ignore("Funtions are not supported by H2")
  public void getPatientsWithFirstFiltRegimeTptShouldPass() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTpt();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Test
  public void getpatientswithRegimeTPTIsoniazid() throws EvaluationException {

    CohortDefinition cd = tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazid();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cd, parameters);

    assertNotNull(evaluatedCohort.getMemberIds());
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2020, 5, 26);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2020, 6, 27);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(400);
  }

  @Override
  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {

    context.addParameterValue("startDate", startDate);
    context.addParameterValue("endDate", endDate);
    context.addParameterValue("location", location);
  }
}
