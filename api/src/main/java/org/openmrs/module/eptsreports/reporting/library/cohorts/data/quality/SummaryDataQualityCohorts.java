package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.DqQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityCohorts {

  private GenericCohortQueries genericCohortQueries;

  private TxNewCohortQueries txNewCohortQueries;

  private GenderCohortQueries genderCohortQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public SummaryDataQualityCohorts(
      GenericCohortQueries genericCohortQueries,
      TxNewCohortQueries txNewCohortQueries,
      GenderCohortQueries genderCohortQueries,
      HivMetadata hivMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.txNewCohortQueries = txNewCohortQueries;
    this.genderCohortQueries = genderCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /**
   * Get pregnant male patients
   *
   * @return Cohort Definition
   */
  public CohortDefinition getPregnantMalePatients() {

    SqlCohortDefinition pCd = new SqlCohortDefinition();
    pCd.setName("Patients patients ");
    pCd.addParameter(new Parameter("location", "Location", Location.class));
    pCd.setQuery(
        DqQueries.getPregnantPatients(
            hivMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getGestationConcept().getConceptId(),
            hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPtvEtvProgram().getProgramId()));

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant patients recorded in the system and are male");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("male", EptsReportUtils.map(genderCohortQueries.maleCohort(), ""));
    cd.addSearch("pregnant", EptsReportUtils.map(pCd, "location=${location}"));

    cd.setCompositionString("pregnant AND male");
    return cd;
  }

  /**
   * Get Breastfeeding male patients
   *
   * @return Cohort Definition
   */
  public CohortDefinition getBreastfeedingMalePatients() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding patients recorded in the system");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("male", EptsReportUtils.map(genderCohortQueries.maleCohort(), ""));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(getBreastfeedingPatientsComposition(), "location=${location}"));

    cd.setCompositionString("breastfeeding AND male");
    return cd;
  }

  public CohortDefinition getBreastfeedingPatientsComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setDescription("breastfeeding Patients Composition");
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "DATAPARTO",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsWithUpdatedDepartureInART(), "locationList=${location}"));
    cd.addSearch(
        "INICIOLACTANTE",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                hivMetadata.getCriteriaForArtStart(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(hivMetadata.getBreastfeeding())),
            "locationList=${location}"));
    cd.addSearch(
        "LACTANTEPROGRAMA",
        EptsReportUtils.map(
            genericCohortQueries.generalSql(
                "LACTANTEPROGRAMA",
                DqQueries.getPatientsWhoGaveBirth(
                    hivMetadata.getPtvEtvProgram().getProgramId(), 27)),
            "location=${location}"));
    cd.addSearch(
        "LACTANTE",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                hivMetadata.getBreastfeeding(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(hivMetadata.getYesConcept())),
            "locationList=${location}"));

    String compositionString = "(DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA OR LACTANTE)";

    cd.setCompositionString(compositionString);
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
    cd.addParameter(new Parameter("state", "States", ProgramWorkflowState.class, List.class, null));
    cd.setQuery(BaseQueries.getBaseQueryForDataQuality());
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
        DqQueries.getPatientsWithStateThatIsBeforeAnEncounter(programId, stateId, encounterList));
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
    cd.setQuery(DqQueries.getPatientsWhoseYearOfBirthIsBeforeYear(year));
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
    cd.setQuery(DqQueries.getPatientsWithNegativeBirthDates());
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
    cd.setQuery(DqQueries.getPatientsWithMoreThanXyears(years));
    return cd;
  }

  /**
   * The patient’s date of birth is after any drug pick up date
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoseBirthDatesAreAfterDrugPickUp(
      List<Integer> encounterList) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("The patient’s date of birth is after any drug pick up date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setQuery(DqQueries.getPatientsWhoseBirthdateIsAfterDrugPickup(encounterList));
    return cd;
  }
}
