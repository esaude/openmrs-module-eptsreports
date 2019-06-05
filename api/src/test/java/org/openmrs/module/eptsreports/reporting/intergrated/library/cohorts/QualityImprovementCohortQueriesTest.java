package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovementCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class QualityImprovementCohortQueriesTest extends DefinitionsTest {

  @Autowired private QualityImprovementCohortQueries qualityImprovementCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Override
  public Connection getConnection() {
    // TODO Auto-generated method stub
    return super.getConnection();
  }

  private void config() throws DatabaseUnitException {
    // database connection for dbunit
    IDatabaseConnection connection = new DatabaseConnection(getConnection());
  }

  @Before
  public void setup() throws Exception {
    // QueryDataSet initialDataSet = new QueryDataSet();
    // getConnection()
    executeDataSet("qualityImprovement1-patientDataTest.xml");
    executeDataSet("qualityImprovement2-globalPropertyDataTest.xml");
    executeDataSet("qualityImprovement3-conceptDataTest.xml");
    executeDataSet("qualityImprovement4-programDataTest.xml");
    executeDataSet("qualityImprovement5-encounterAndObsDataTest.xml");
    // executeDataSet("qualityImprovement6-GaacAndGaacMemberDataTest.xml");
  }

  @Ignore
  @Test
  public void getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter()
      throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("startDate", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 26));
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class),
        getDataFinalAvaliacao());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));

    System.out.println(evaluatedCohort.getMemberIds());
    assertNotNull(cohortDefinition);
  }

  @Ignore
  @Test
  public void getPacientsEnrolledInTBProgram() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPacientsEnrolledInTBProgram();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1002));
  }

  @Ignore
  @Test
  public void getPatientWithDeliveryDate2YearsAgoBreatFeeding() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWithDeliveryDate2YearsAgoBreatFeeding();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1003));
  }

  @Ignore
  @Test
  public void getPatientWithAtLeastOneEncounterInPeriod() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWithAtLeastOneEncounterInPeriod();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1001));
  }

  @Ignore
  @Test
  public void getPatientsNotifiedSarcomaKaposi() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientsNotifiedSarcomaKaposi();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1004));
  }

  @Ignore
  @Test
  public void getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months()
      throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1005));
  }

  @Ignore
  @Test
  public void getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter()
      throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1006));
  }

  @Ignore
  @Test
  public void getPatientWithEncontersWithinSevenDaysAfterDiagnostic() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWithEncontersWithinSevenDaysAfterDiagnostic();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1007));
  }

  @Ignore
  @Test
  public void getPatientWhoCameOutARTProgramFinalPeriod() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWhoCameOutARTProgramFinalPeriod();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1001));
  }

  @Ignore
  @Test
  public void getPatientEnrolledARTProgramFinalPeriod() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientEnrolledARTProgramFinalPeriod();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1001));
  }

  // @Test
  public void getPatientsEnrolledInGaacInAPeriod() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientsEnrolledInGaacInAPeriod();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertNotNull(evaluatedCohort);
    assertTrue(evaluatedCohort.getMemberIds().contains(18));
  }

  @Ignore
  @Test
  public void getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted()
      throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("startDate", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 10));
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class),
        getDataFinalAvaliacao());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1008));
  }

  @Ignore
  @Test
  public void getPatientWithTrackInEachTBEncounter() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWithTrackInEachTBEncounter();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("startDate", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 10));
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1009));
  }

  @Ignore
  @Test
  public void usesIsoniazida() throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.usesIsoniazida(
            hivMetadata.getIsoniazidUsageConcept(),
            BaseObsCohortDefinition.TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getARVPediatriaInitialBEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                tbMetadata.getTBRastreioEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(
        new Parameter("onOrAfter", "Start Date", Date.class), DateUtil.getDateTime(2019, 01, 10));
    parameters.put(new Parameter("onOrBefore", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    assertTrue(evaluatedCohort.getMemberIds().contains(1010));

    assertNotNull(cohortDefinition);
  }

  @Ignore
  @Test
  public void getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample()
      throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(cohortDefinition);
  }

  @Ignore
  @Test
  public void getPatientsWithEnconterInPeriodAndHadScreeningForSTI() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientsWithEnconterInPeriodAndHadScreeningForSTI();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class),
        getDataFinalAvaliacao());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  @Ignore
  @Test
  public void getPacientsWithCD4RegisteredIn33Days() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPacientsWithCD4RegisteredIn33Days();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    //  assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  @Ignore
  @Test
  public void getPatientsWhomStartedARVIn15Days() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientsWhomStartedARVIn15Days();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  @Ignore
  @Test
  public void getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining()
      throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  @Ignore
  @Test
  public void getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART()
      throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  @Ignore // because  H2 does not recognize  "having if" syntax
  // TODO will try with "case when"
  @Test
  public void getPatientWhoAtLeast3JoiningEvaluationWithin3MothsARIEL() throws EvaluationException {
    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries.getPatientWhoAtLeast3AdherenceEvaluationWithin3MothsARIEL();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), this.getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), this.getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(new Parameter("testStart", "testStart", Boolean.class), getTestStart());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);
    // assertTrue(evaluatedCohort.getMemberIds().contains(1001));
    assertNotNull(evaluatedCohort);
  }

  private Date getDataFinalAvaliacao() {
    return DateUtil.getDateTime(2019, 5, 26);
  }

  private Boolean getTestStart() {
    return new Boolean(false);
  }

  @Override
  public Date getStartDate() {
    return DateUtil.getDateTime(2010, 5, 10);
  }

  @Override
  public Date getEndDate() {
    return DateUtil.getDateTime(2019, 5, 26);
  }
}
