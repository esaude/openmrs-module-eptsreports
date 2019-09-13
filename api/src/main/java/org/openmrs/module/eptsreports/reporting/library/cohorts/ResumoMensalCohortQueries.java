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
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
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
            hivMetadata.getArtStatus().getConceptId()));

    return cd;
  }

  /**
   * B.3 Number of patients who restarted the treatment during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoRestartedTreatmentDuringCurrentMonthB3() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.setName("Number of patients who restarted the treatment during the current month");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    cd.addIncludeCodedValue(hivMetadata.getStartDrugs());
    return cd;
  }

  /** @return B.5 Number of patients transferred out during the current month */
  public CohortDefinition getNumberOfPatientsTransferredOutDuringCurrentMonthB5() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    cd.addIncludeCodedValue(hivMetadata.getTransferredOutConcept());
    return cd;
  }

  /** @return B6: Number of patients with ART suspension during the current month */
  public CohortDefinition getNumberOfPatientsWithArtSuspensionDuringCurrentMonthB6() {
    EncounterWithCodedObsCohortDefinition cd = new EncounterWithCodedObsCohortDefinition();
    cd.setName("Number of patients transferred out during the current month");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    cd.addIncludeCodedValue(hivMetadata.getSuspendedTreatmentConcept());
    return cd;
  }

  /** @return B7: Number of patients who abandoned the ART during the current month */
  public CohortDefinition getNumberOfPatientsWhoAbandonedArtDuringCurrentMonthB7() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("Number of patientes who Abandoned the ART during the current month");
    cd.addParameter(new Parameter("value1", "Value 1", Date.class));
    cd.addParameter(new Parameter("value2", "Value 1", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardDrugPickupEncounterType());
    cd.setQuestion(hivMetadata.getArtDatePickup());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);
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

  /** @return Number of cumulative patients who started ART by end of previous month */
  public CohortDefinition getPatientsWhoStartedArtByEndOfPreviousMonthB10() {
    SqlCohortDefinition patientsWithArtStartDate = new SqlCohortDefinition();
    patientsWithArtStartDate.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientsWithArtStartDate.addParameter(new Parameter("location", "location", Location.class));
    patientsWithArtStartDate.setQuery(
        ResumoMensalQueries.getAllPatientsWithArtStartDateBeforeDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getARVStartDate().getConceptId()));

    SqlCohortDefinition patientsWithDrugPickup = new SqlCohortDefinition();
    patientsWithDrugPickup.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientsWithDrugPickup.addParameter(new Parameter("location", "location", Location.class));
    patientsWithDrugPickup.setQuery(
        ResumoMensalQueries.getPatientsWhoHadDrugPickUpBeforeDate(
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId()));

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of cumulative patients who started ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "artStartDate",
        map(patientsWithArtStartDate, "onOrBefore=${startDate},location=${location}"));
    cd.addSearch(
        "drugPickup", map(patientsWithDrugPickup, "onOrBefore=${startDate},location=${location}"));
    cd.addSearch(
        "transferredIn", mapStraightThrough(getPatientsTransferredFromOtherHealthFacilities()));

    cd.setCompositionString("artStartDate AND drugPickup NOT transferredIn");

    return cd;
  }

  /** @return Number of active patients in ART by end of previous month */
  public CohortDefinition getPatientsWhoWereActiveByEndOfPreviousMonthB12() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of active patients in ART by end of previous month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    SqlCohortDefinition transferredIn = new SqlCohortDefinition();
    transferredIn.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    transferredIn.addParameter(new Parameter("location", "location", Location.class));
    transferredIn.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId()));

    EncounterWithCodedObsCohortDefinition startDrugs = new EncounterWithCodedObsCohortDefinition();
    startDrugs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    startDrugs.addParameter(new Parameter("locationList", "location", Location.class));
    startDrugs.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    startDrugs.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    startDrugs.addIncludeCodedValue(hivMetadata.getStartDrugsConcept());

    EncounterWithCodedObsCohortDefinition transferredOut =
        new EncounterWithCodedObsCohortDefinition();
    transferredOut.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    transferredOut.addParameter(new Parameter("locationList", "location", Location.class));
    transferredOut.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    transferredOut.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    transferredOut.addIncludeCodedValue(hivMetadata.getTransferredOutConcept());

    EncounterWithCodedObsCohortDefinition suspended = new EncounterWithCodedObsCohortDefinition();
    suspended.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    suspended.addParameter(new Parameter("locationList", "location", Location.class));
    suspended.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    suspended.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    suspended.addIncludeCodedValue(hivMetadata.getSuspendedTreatmentConcept());

    DateObsCohortDefinition missedDrugPickup = new DateObsCohortDefinition();
    missedDrugPickup.addParameter(new Parameter("value1", "Value 1", Date.class));
    missedDrugPickup.addParameter(new Parameter("value2", "Value 1", Date.class));
    missedDrugPickup.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    missedDrugPickup.addParameter(new Parameter("locationList", "Location", Location.class));
    missedDrugPickup.addEncounterType(hivMetadata.getMasterCardDrugPickupEncounterType());
    missedDrugPickup.setQuestion(hivMetadata.getArtDatePickup());
    missedDrugPickup.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    missedDrugPickup.setOperator1(RangeComparator.GREATER_EQUAL);
    missedDrugPickup.setOperator2(RangeComparator.LESS_EQUAL);

    EncounterWithCodedObsCohortDefinition died = new EncounterWithCodedObsCohortDefinition();
    died.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    died.addParameter(new Parameter("locationList", "location", Location.class));
    died.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    died.setConcept(hivMetadata.getStateOfStayOfArtPatient());
    died.addIncludeCodedValue(hivMetadata.getPatientHasDiedConcept());

    String encounterWithCodedObsMappings = "onOrBefore=${startDate},locationList=${location}";
    String drugPickupMappings =
        "value1=${endDate-90d},value2=${endDate},onOrBefore=${startDate},locationList=${location}";

    cd.addSearch("B10", mapStraightThrough(getPatientsWhoStartedArtByEndOfPreviousMonthB10()));
    cd.addSearch("B2A", map(transferredIn, "onOrBefore=${startDate},location=${location}"));
    cd.addSearch("B3A", map(startDrugs, encounterWithCodedObsMappings));
    cd.addSearch("B5A", map(transferredOut, encounterWithCodedObsMappings));
    cd.addSearch("B6A", map(suspended, encounterWithCodedObsMappings));
    cd.addSearch("B7A", map(missedDrugPickup, drugPickupMappings));
    cd.addSearch("B8A", map(died, encounterWithCodedObsMappings));

    cd.setCompositionString("B10 OR B2A OR B3A AND NOT (B5A OR B6A OR B7A OR B8A)");

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
