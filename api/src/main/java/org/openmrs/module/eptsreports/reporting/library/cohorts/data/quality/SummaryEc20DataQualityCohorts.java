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
public class SummaryEc20DataQualityCohorts {
  private HivMetadata hivMetadata;

  @Autowired
  public SummaryEc20DataQualityCohorts(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  /**
   * Get all patient both enrolled in all the existing programs
   *
   * @return CohortDefinition
   */
  public CohortDefinition getEc20DataQualityReportBaseCohort() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patient States");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("location", "Facilities", Location.class, List.class, null));
    sqlCohortDefinition.setQuery(
        BaseQueries.getBaseQueryForEc20DataQuality(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    return sqlCohortDefinition;
  }

  /**
   * Get Patients who are not enrolled in TARV, program ID 2
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsNotEnrolledOnTARV(List<Integer> encounterList, int programId) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Patients not enrolled on TARV");
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addSearch(
        "enrolledOnTARV",
        EptsReportUtils.map(
            getPatientsEnrolledOnTARV(programId), "location=${location},endDate=${endDate}"));
    compositionCohortDefinition.addSearch(
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithGivenEncounterList(encounterList),
            "location=${location},endDate=${endDate}"));

    compositionCohortDefinition.setCompositionString("allPatients AND NOT enrolledOnTARV");

    return compositionCohortDefinition;
  }

  /**
   * Get patients with given encounter list
   *
   * @param encounterList - list encounters
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithGivenEncounterList(List<Integer> encounterList) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patients with given encounter list");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.setQuery(SummaryQueries.getPatientsWithGivenEncounterList(encounterList));

    return sqlCohortDefinition;
  }

  /**
   * Get patients enrolled on TARV, program ID 2
   *
   * @param programId - refers to the program ID
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsEnrolledOnTARV(int programId) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patients enrolled on TARV, program ID 2");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.setQuery(SummaryQueries.getPatientsEnrolledOnTARV(programId));

    return sqlCohortDefinition;
  }
}
