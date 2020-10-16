package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.SummaryQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityCohorts {

  private HivMetadata hivMetadata;

  @Autowired
  public SummaryDataQualityCohorts(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  /**
   * Get pregnant male patients
   *
   * @return Cohort Definition
   */
  public CohortDefinition getPregnantMalePatients() {

    SqlCohortDefinition pCd = new SqlCohortDefinition();
    pCd.setName("Pregnant patients recorded in the system and are male ");
    pCd.addParameter(new Parameter("location", "Location", Location.class));
    pCd.setQuery(
        SummaryQueries.getPregnantPatients(
            hivMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPtvEtvProgram().getProgramId()));
    return pCd;
  }

  /**
   * Get Breastfeeding male patients
   *
   * @return Cohort Definition
   */
  public CohortDefinition getBreastfeedingMalePatients() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get male breastfeeding patients recorded in the system");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        SummaryQueries.getBreastfeedingMalePatients(
            hivMetadata.getPriorDeliveryDateConcept().getConceptId(),
            hivMetadata.getCriteriaForArtStart().getConceptId(),
            hivMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));

    return cd;
  }

  /**
   * Note:<all, active&transferred in, abandoned, transferred out>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getQualityDataReportBaseCohort() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient States");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Facilities", Location.class, List.class, null));
    cd.addParameter(EptsReportUtils.getProgramConfigurableParameter(hivMetadata.getARTProgram()));
    cd.setQuery(BaseQueries.getBaseQueryForDataQuality(hivMetadata.getARTProgram().getProgramId()));
    return cd;
  }

  /**
   * Get patients with states and a list of encounters
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithStatesAndEncounters(
      int programId, int stateId, List<Integer> encounterList) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients who have state that is before an encounter");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        SummaryQueries.getPatientsWithStateThatIsBeforeAnEncounter(
            programId, stateId, encounterList));
    return cd;
  }

  /**
   * Patients with a birth date that is before 1920 The patient’s date of birth, estimated date of
   * birth or entered age indicate the patient was born before 1920
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoseBirthdateIsBeforeYear(int year) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with a birth date that is before " + year);
    cd.setQuery(SummaryQueries.getPatientsWhoseYearOfBirthIsBeforeYear(year));
    return cd;
  }

  /**
   * The patients date of birth, estimated date of birth or age is negative
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithNegativeAge() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("The patients date of birth, estimated date of birth or age is negative");
    cd.setQuery(SummaryQueries.getPatientsWithNegativeBirthDates());
    return cd;
  }

  /**
   * The patients birth, estimated date of birth or age indicates they are > years of age
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithAgeHigherThanXyears(int years) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("The patients birth, estimated date of birth or age indicates they are > " + years);
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setQuery(SummaryQueries.getPatientsWithMoreThanXyears(years));
    return cd;
  }

  /**
   * The patient’s date of birth is after any drug pick up date
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoseBirthDatesAreAfterEncounterDate(
      List<Integer> encounterList) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("The patient’s date of birth is after any drug pick up date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setQuery(SummaryQueries.getPatientsWhoseBirthdateIsAfterDrugPickup(encounterList));
    return cd;
  }

  /**
   * The patients who are marked as dead in the patient states or those marked as deceased in the
   * person object
   *
   * @return CohortDefinition
   */
  public CohortDefinition getDeadOrDeceasedPatientsHavingEncountersAfter(
      int programId, int stateId, List<Integer> encounterList) {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Deceased and have encounters after deceased date");
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(SummaryQueries.getPatientsMarkedAsDeceasedAndHaveAnEncounter(encounterList));

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Dead or deceased patients");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            getPatientsWithStatesAndEncounters(programId, stateId, encounterList),
            "location=${location}"));
    cd.addSearch("deceased", EptsReportUtils.map(sql, "location=${location}"));
    cd.setCompositionString("dead OR deceased");
    return cd;
  }

  /**
   * According with the encounterList The patients whose date of drug pick up is before 1985 The
   * date of clinical consultation is before 1985
   *
   * @param encounterList - list of encounters
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoseEncounterIsBefore1985(List<Integer> encounterList) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patients whose date of drug pick up is before 1985");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.setQuery(
        SummaryQueries.getPatientsWhoseEncounterIsBefore1985(encounterList));

    return sqlCohortDefinition;
  }
}
