package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class UsMonthlySummaryHivCohortDefinitionsFGHLiveTest extends DefinitionsFGHLiveTest {

  @Autowired private TXTBCohortQueries txTbCohortQueries;
  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private HivMetadata hivMetadata;
  @Autowired private HivCohortQueries hivCohortQueries;

  @Test
  public void anyTimeARVTreatmentFinalPeriodShouldMatch() throws EvaluationException {
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition newDefinition = txTbCohortQueries.anyTimeARVTreatmentFinalPeriod();
    CohortDefinition oldDefinition =
        service.getDefinition(
            "a9043c4d-47f7-4e8d-96c7-4ff8389999e3", CompositionCohortDefinition.class);

    compareCohorts(newDefinition, oldDefinition);
  }

  @Test
  public void inARTProgramAtEndDateShouldMatch() throws EvaluationException {
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition newDefinition =
        genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
    CohortDefinition oldDefinition =
        service.getDefinition("9f7e8b72-7109-4253-9eec-f670965737f4", SqlCohortDefinition.class);

    compareCohorts(newDefinition, oldDefinition);
  }

  @Test
  public void CONCEITODATAShouldMatch() throws EvaluationException {
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition newDefinition =
        hivCohortQueries.getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
    CohortDefinition oldDefinition =
        service.getDefinition(
            "0db2683c-4cb0-4851-b9ed-4bf89626aae2", DateObsCohortDefinition.class);

    EvaluatedCohort oldCohort = evaluateCohortDefinition(oldDefinition);
    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("onOrBefore", "", Date.class), getEndDate());
    parameters.put(new Parameter("location", "", Location.class), getLocation());
    EvaluatedCohort newCohort = evaluateCohortDefinition(newDefinition, parameters);
    assertEquals(oldCohort.getSize(), newCohort.getSize());
  }

  @Test
  public void FRIDAFILAShouldMatch() throws EvaluationException {
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition newDefinition = txTbCohortQueries.arTTreatmentFromPharmacy();
    CohortDefinition oldDefinition =
        service.getDefinition(
            "b425b1de-240c-4672-8e22-5c64c1d5b910", EncounterCohortDefinition.class);

    compareCohorts(newDefinition, oldDefinition);
  }

  @Test
  public void CONCEITO1255ShouldMatch() throws EvaluationException {
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition newDefinition = txTbCohortQueries.arTTreatmentFromPharmacy();
    CohortDefinition oldDefinition =
        service.getDefinition(
            "bfab8cf5-77e4-4afc-bc0d-2212efe58586", CodedObsCohortDefinition.class);

    compareCohorts(newDefinition, oldDefinition);
  }

  private void compareCohorts(CohortDefinition newDefinition, CohortDefinition oldDefinition)
      throws EvaluationException {
    EvaluatedCohort oldCohort = evaluateCohortDefinition(oldDefinition);
    EvaluatedCohort newCohort = evaluateCohortDefinition(newDefinition);
    assertEquals(oldCohort.getSize(), newCohort.getSize());
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2018, 6, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2012, 3, 20);
  }

  protected Location getLocation() {
    return Context.getLocationService().getLocation(6);
  }
}
