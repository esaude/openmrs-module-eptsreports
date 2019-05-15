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

import java.util.Collections;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the HivCohortDefinition instances we want to expose for EPTS */
@Component
public class HivCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueires;

  /**
   * Adult and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml)
   * documented in the medical records and /or supporting laboratory results within the past 12
   * months
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "suppressedViralLoadWithin12Months")
  public CohortDefinition getPatientsWithSuppressedViralLoadWithin12Months() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("suppressedViralLoadWithin12Months");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ViralLoadQueries.getPatientsWithViralLoadSuppression(
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId()));
    return sql;
  }

  /**
   * Number of adult and pediatric ART patients with a viral load result documented in the patient
   * medical record and/ or laboratory records in the past 12 months.
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "viralLoadWithin12Months")
  public CohortDefinition getPatientsViralLoadWithin12Months() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("viralLoadWithin12Months");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ViralLoadQueries.getPatientsHavingViralLoadInLast12Months(
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId()));
    return sql;
  }

  /**
   * Adult and pediatric patients on ART who have re-initiated the treatment.
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "restartedTreatment")
  public CohortDefinition getPatientsWhoRestartedTreatment() {
    return genericCohortQueires.hasCodedObs(
        hivMetadata.getARVPlanConcept(),
        Collections.singletonList(hivMetadata.getRestartConcept()));
  }

  /**
   * Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred
   * from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY) between
   * start date and end date
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "transferredFromOtherHealthFacility")
  public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
    // TODO refactor this method, use #getPatientsInProgramWithStateDuringPeriod(Program,
    // ProgramWorkflowState)
    SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
    transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
    String query =
        "select p.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d"
            + " and ps.state=%d"
            + " and ps.start_date=pg.date_enrolled"
            + " and ps.start_date between :onOrAfter and :onOrBefore and location_id=:location "
            + "group by p.patient_id";
    transferredFromOtherHealthFacility.setQuery(
        String.format(
            query,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    transferredFromOtherHealthFacility.addParameter(
        new Parameter("onOrAfter", "onOrAfter", Date.class));
    transferredFromOtherHealthFacility.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    transferredFromOtherHealthFacility.addParameter(
        new Parameter("location", "location", Location.class));
    return transferredFromOtherHealthFacility;
  }

  public CohortDefinition getPatientsInArtCareTransferredFromOtherHealthFacility() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState transferredFrom =
        hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState();
    return getPatientsInProgramWithStateDuringPeriod(
        "transferredFromOtherHealthFacility", transferredFrom, hivCareProgram);
  }

  public CohortDefinition getPatientsInArtCareTransferredOutToAnotherHealthFacility() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState state =
        hivMetadata.getArtCareTransferredOutToAnotherHealthFacilityWorkflowState();
    return getPatientsInProgramWithStateByEndOfPeriod(
        "transferredOutToAnotherHealthFacility", hivCareProgram, state);
  }

  /**
   * @return Cohort of patients with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
   *     drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and
   *     children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO)
   *     where START DATE is before or equal end date
   */
  @DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
  public CohortDefinition getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate() {
    SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
    patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 and e.voided=0 AND o.voided=0 AND e.encounter_type IN (%d, %d, %d) "
            + "AND o.concept_id=%d "
            + "AND o.value_datetime IS NOT NULL AND o.value_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithHistoricalDrugStartDateObs.setQuery(
        String.format(
            query,
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId()));
    patientWithHistoricalDrugStartDateObs.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithHistoricalDrugStartDateObs.addParameter(
        new Parameter("location", "location", Location.class));
    return patientWithHistoricalDrugStartDateObs;
  }

  public CohortDefinition getPatientsInArtCareWhoAbandoned() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState abandoned = hivMetadata.getArtCareAbandonedWorkflowState();
    return getPatientsInProgramWithStateByEndOfPeriod(
        "patientsInArtCareWhoAbandoned", hivCareProgram, abandoned);
  }

  private CohortDefinition getPatientsInProgramWithStateDuringPeriod(
      String name, ProgramWorkflowState state, Program program) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(name);
    String query =
        "select p.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d"
            + " and ps.state=%d"
            + " and ps.start_date=pg.date_enrolled"
            + " and ps.start_date between :onOrAfter and :onOrBefore and location_id=:location "
            + "group by p.patient_id";
    cd.setQuery(String.format(query, program.getProgramId(), state.getProgramWorkflowStateId()));
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    return cd;
  }

  private CohortDefinition getPatientsInProgramWithStateByEndOfPeriod(
      String name, Program hivCareProgram, ProgramWorkflowState abandoned) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(name);
    String query =
        "select  pg.patient_id "
            + "from    patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where   pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "pg.program_id=%d and ps.state=%d and ps.end_date is null and "
            + "ps.start_date<=:onOrBefore and location_id=:location";
    cd.setQuery(
        String.format(query, hivCareProgram.getProgramId(), abandoned.getProgramWorkflowStateId()));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    return cd;
  }
}
