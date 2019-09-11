/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalCohortQueries {

  private HivMetadata hivMetadata;

  @Autowired
  public ResumoMensalCohortQueries(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  /** A1 Number of patients who initiated Pre-TARV at this HF by end of previous month */
  public CohortDefinition getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A1i");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        ResumoMensalQueries.getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));

    cd.addSearch("A1I", map(sqlCohortDefinition, "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "A1II", map(getPatientsTransferredFromOtherHealthFacilities(), "location=${location}"));

    cd.setCompositionString("A1I AND NOT A1II");

    return cd;
  }

  /**
   * A2 Number of patients who initiated Pre-TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated Pre-TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("A2i");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.setQuery(
        ResumoMensalQueries.getAllPatientsWithPreArtStartDateWithBoundaries(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()));

    cd.addSearch(
        "A2I",
        map(sqlCohortDefinition, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A2II", map(getPatientsTransferredFromOtherHealthFacilities(), "location=${location}"));

    cd.setCompositionString("A2I AND NOT A2II");

    return cd;
  }
  /**
   * A3 = A.1 + A.2
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSumOfA1AndA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Sum of A1 and A2");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "A1",
        map(
            getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
            "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "A2",
        map(
            getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("A1 OR A2");
    return cd;
  }

  // Start of the B queries
  /**
   * B1 Number of patientes who initiated TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patientes who initiated TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1i",
        map(
            getPatientsWhoInitiatedTarvAtAfacilityDuringCurrentMonth(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1ii",
        map(
            getPatientsWithMasterCardDrugPickUpDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1iii", map(getPatientsTransferredFromOtherHealthFacilities(), "location=${location}"));
    cd.addSearch("B1iv", map(getTypeOfPatientTransferredFrom(), "location=${location}"));

    cd.setCompositionString("(B1i AND B1ii) AND NOT (B1iii OR B1iv)");
    return cd;
  }

  /**
   * B.2: Number of patients transferred-in from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients transferred-in from another HFs during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId()));

    return cd;
  }

  /**
   * B.3 Number of patients who restarted the treatment during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoRestartedTreatmentDuringCurrentMonthB3() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients who restarted the treatment during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWhoRestartedTreatmentDuringCurrentMonth(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId()));

    return cd;
  }

  /** @return B.5 Number of patients transferred out during the current month */
  public CohortDefinition getNumberOfPatientsTransferredOutDuringCurrentMonthB5() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsTransferredOutDuringCurrentMonth(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId()));

    return cd;
  }

  /** @return B6: Number of patients with ART suspension during the current month */
  public CohortDefinition getNumberOfPatientsWithArtSuspensionDuringCurrentMonthB6() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWithArtSuspensionDuringCurrentMonth(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId()));

    return cd;
  }

  /** @return B7: Number of patients who abandoned the ART during the current month */
  public CohortDefinition getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthB7() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWhoAbandonedArtDuringCurrentMonth(
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));

    return cd;
  }

  /** @return B8: Number of died patients during the current month */
  public CohortDefinition getNumberOfPatientsWhoDiedDuringCurrentMonthB8() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsWhoDiedDuringCurrentMonth(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    return cd;
  }

  /**
   * Patients with master card drug pickup date
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsWithMasterCardDrugPickUpDate() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients with master card drug pickup date");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ResumoMensalQueries.getPatientsWhoHadDrugPickUpInMasterCard(
            hivMetadata.getArtDatePickup().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));
    return sql;
  }

  /**
   * Get number of patients transferred from other health facility marked in master card
   *
   * @return CohortDefinition
   */
  private CohortDefinition getTypeOfPatientTransferredFrom() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients transferred from other health facility marked in master card");
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ResumoMensalQueries.getTypeOfPatientTransferredFrom(
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));
    return sql;
  }

  /**
   * Patients who have been transferred from other health facility to this one
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsTransferredFromOtherHealthFacilities() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients with master card and have been marked as transfer ins");
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromOtherHealthFacility(
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));
    return sql;
  }

  private CohortDefinition getPatientsWhoInitiatedTarvAtAfacilityDuringCurrentMonth() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Number of patients who initiated TARV at this HF during the current month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getAllPatientsWithArtStartDateWithBoundaries(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getARVStartDate().getConceptId()));
    return cd;
  }
}
