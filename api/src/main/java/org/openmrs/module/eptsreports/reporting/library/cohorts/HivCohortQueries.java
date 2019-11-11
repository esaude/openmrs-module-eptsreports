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
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
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

  public CohortDefinition getPatientsInArtCareTransferredFromOtherHealthFacility() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState transferredFrom =
        hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStates(
        hivCareProgram.getProgramId(), transferredFrom.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtCareTransferredOutToAnotherHealthFacility() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState state =
        hivMetadata.getArtCareTransferredOutToAnotherHealthFacilityWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        hivCareProgram.getProgramId(), state.getProgramWorkflowStateId());
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
            + "WHERE p.voided=0 and e.voided=0 AND o.voided=0 AND e.encounter_type IN (%d, %d, %d,%d) "
            + "AND o.concept_id=%d "
            + "AND o.value_datetime IS NOT NULL AND o.value_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithHistoricalDrugStartDateObs.setQuery(
        String.format(
            query,
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
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
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        hivCareProgram.getProgramId(), abandoned.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtCareWhoDied() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState dead = hivMetadata.getArtCareDeadWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        hivCareProgram.getProgramId(), dead.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtCareWhoInitiatedArt() {
    Program hivCareProgram = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState initiatedArt = hivMetadata.getArtCareInitiatedWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        hivCareProgram.getProgramId(), initiatedArt.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtWhoSuspendedTreatment() {
    ProgramWorkflowState suspendedTreatment = hivMetadata.getArtSuspendedTreatmentWorkflowState();
    Program artProgram = hivMetadata.getARTProgram();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        artProgram.getProgramId(), suspendedTreatment.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtTransferredOutToAnotherHealthFacility() {
    Program artProgram = hivMetadata.getARTProgram();
    ProgramWorkflowState state =
        hivMetadata.getArtTransferredOutToAnotherHealthFacilityWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        artProgram.getProgramId(), state.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtWhoDied() {
    Program artProgram = hivMetadata.getARTProgram();
    ProgramWorkflowState dead = hivMetadata.getArtDeadWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        artProgram.getProgramId(), dead.getProgramWorkflowStateId());
  }

  public CohortDefinition getPatientsInArtWhoAbandoned() {
    Program artProgram = hivMetadata.getARTProgram();
    ProgramWorkflowState state = hivMetadata.getArtAbandonedWorkflowState();
    return genericCohortQueires.getPatientsBasedOnPatientStatesBeforeDate(
        artProgram.getProgramId(), state.getProgramWorkflowStateId());
  }

  public CohortDefinition getHomosexualKeyPopCohort() {
    CodedObsCohortDefinition cd = (CodedObsCohortDefinition) getKeyPopulationCohort();
    cd.setName("Men who have sex with men");
    cd.setValueList(Collections.singletonList(hivMetadata.getHomosexualConcept()));
    return cd;
  }

  public CohortDefinition getDrugUserKeyPopCohort() {
    CodedObsCohortDefinition cd = (CodedObsCohortDefinition) getKeyPopulationCohort();
    cd.setName("People who inject drugs");
    cd.setValueList(Collections.singletonList(hivMetadata.getDrugUseConcept()));
    return cd;
  }

  public CohortDefinition getImprisonmentKeyPopCohort() {
    CodedObsCohortDefinition cd = (CodedObsCohortDefinition) getKeyPopulationCohort();
    cd.setName("People in prison and other closed settings");
    cd.setValueList(Collections.singletonList(hivMetadata.getImprisonmentConcept()));
    return cd;
  }

  public CohortDefinition getSexWorkerKeyPopCohort() {
    CodedObsCohortDefinition cd = (CodedObsCohortDefinition) getKeyPopulationCohort();
    cd.setName("Female sex workers");
    cd.setValueList(Collections.singletonList(hivMetadata.getSexWorkerConcept()));
    return cd;
  }

  private CohortDefinition getKeyPopulationCohort() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("Key population");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("locationList", "location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setQuestion(hivMetadata.getKeyPopulationConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getHomosexualConcept());
    cd.addValue(hivMetadata.getDrugUseConcept());
    cd.addValue(hivMetadata.getImprisonmentConcept());
    cd.addValue(hivMetadata.getSexWorkerConcept());
    return cd;
  }
}
