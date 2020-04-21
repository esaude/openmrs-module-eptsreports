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

import static org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation.KeyPop.DRUG_USER;
import static org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation.KeyPop.HOMOSEXUAL;
import static org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation.KeyPop.PRISONER;
import static org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation.KeyPop.SEX_WORKER;
import static org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation.TYPE;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.KeyPopulationCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TbQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
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

  @Autowired private CommonMetadata commonMetadata;

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
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getFsrEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));
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
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getFsrEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));
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

    Map<String, Integer> map = new HashMap<>();

    map.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "aRVPediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "historicalDrugStartDateConcept",
        hivMetadata.getHistoricalDrugStartDateConcept().getConceptId());
    String query =
        "SELECT p.patient_id  "
            + "FROM patient p  "
            + "    INNER JOIN encounter e  "
            + "        ON p.patient_id=e.patient_id  "
            + "    INNER JOIN obs o  "
            + "        ON e.encounter_id=o.encounter_id  "
            + "WHERE p.voided=0 and e.voided= 0  "
            + "    AND o.voided=0  "
            + "    AND e.encounter_type IN (${aRVPharmaciaEncounterType}, ${adultoSeguimentoEncounterType}, ${aRVPediatriaSeguimentoEncounterType},${masterCardEncounterType})  "
            + "    AND o.concept_id= ${historicalDrugStartDateConcept}  "
            + "    AND o.value_datetime IS NOT NULL  "
            + "    AND o.value_datetime <= :onOrBefore  "
            + "    AND e.location_id= :location "
            + "GROUP BY p.patient_id";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    patientWithHistoricalDrugStartDateObs.setQuery(stringSubstitutor.replace(query));

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
    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setCalculation(Context.getRegisteredComponents(KeyPopulationCalculation.class).get(0));
    cd.setName("Men who have sex with men");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(TYPE, HOMOSEXUAL);
    return cd;
  }

  public CohortDefinition getDrugUserKeyPopCohort() {
    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setCalculation(Context.getRegisteredComponents(KeyPopulationCalculation.class).get(0));
    cd.setName("People who inject drugs");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(TYPE, DRUG_USER);
    return cd;
  }

  public CohortDefinition getImprisonmentKeyPopCohort() {
    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setCalculation(Context.getRegisteredComponents(KeyPopulationCalculation.class).get(0));
    cd.setName("People in prison and other closed settings");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(TYPE, PRISONER);
    return cd;
  }

  public CohortDefinition getSexWorkerKeyPopCohort() {
    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setCalculation(Context.getRegisteredComponents(KeyPopulationCalculation.class).get(0));
    cd.setName("Female sex workers");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(TYPE, SEX_WORKER);
    return cd;
  }

  public CohortDefinition getPatientsTransferredOut() {
    Integer transferOut = hivMetadata.getTransferredOutConcept().getConceptId();
    Integer transferOutState =
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId();
    return getPatientsTransferredOutOrSuspended(transferOut, transferOutState);
  }

  public CohortDefinition getPatientsWhoStoppedTreatment() {
    Integer suspended = hivMetadata.getSuspendedTreatmentConcept().getConceptId();
    Integer suspendedState =
        hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId();
    return getPatientsTransferredOutOrSuspended(suspended, suspendedState);
  }

  /**
   * Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred
   * from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY) or
   * marked as transferred in via mastercard between start date and end date
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "transferredFromOtherHealthFacility")
  public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("transferredFromOtherHealthFacility");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition transferredInViaProgram = getTransferredInViaProgram(true);
    CohortDefinition mastercard = getTransferredInViaMastercard();

    cd.addSearch("program", mapStraightThrough(transferredInViaProgram));
    cd.addSearch("mastercard", mapStraightThrough(mastercard));

    cd.setCompositionString("program or mastercard");

    return cd;
  }

  public CohortDefinition getTransferredInViaProgram(boolean hasStartDate) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("transferredFromOtherHealthFacility");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    String query =
        "select p.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=${artProgram}"
            + " and ps.state=${transferStateId}"
            + " and ps.start_date=pg.date_enrolled ";
    if (hasStartDate == true) {
      query = query + "and ps.start_date between :onOrAfter and :onOrBefore ";
    } else {
      query = query + "and ps.start_date <= :onOrBefore ";
    }
    query = query + "and location_id= :location group by p.patient_id";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "transferStateId",
        hivMetadata
            .getTransferredFromOtherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    cd.setQuery(sub.replace(query));

    return cd;
  }

  public CohortDefinition getTransferredInViaMastercard() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    return cd;
  }

  private CohortDefinition getPatientsTransferredOutOrSuspended(
      int transferedOutOrSuspendedConcept, int patientStateId) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("get Patients With Most Recent Date Have Fila or Consultation ");
    sqlCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pharmaciaEncounterType", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "stateOfStayOfPreArtPatient", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("transferedOutOrSuspendedConcept", transferedOutOrSuspendedConcept);
    map.put("autoTransferConcept", hivMetadata.getAutoTransferConcept().getConceptId());
    map.put("stateOfStayOfArtPatient", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put("transferredOutToAnotherHealthFacilityWorkflowState", patientStateId);

    String query =
        "  SELECT mostrecent.patient_id "
            + "FROM ("
            + " SELECT lastest.patient_id ,Max(lastest.last_date) as  last_date "
            + " FROM (  "
            + "    SELECT p.patient_id ,ps.start_date AS last_date  "
            + "    FROM patient p   "
            + "        INNER JOIN patient_program pg   "
            + "            ON p.patient_id=pg.patient_id   "
            + "        INNER JOIN patient_state ps   "
            + "            ON pg.patient_program_id=ps.patient_program_id   "
            + "    WHERE pg.voided=0   "
            + "        AND ps.voided=0   "
            + "        AND p.voided=0   "
            + "        AND pg.program_id= ${artProgram}  "
            + "        AND ps.state = ${transferredOutToAnotherHealthFacilityWorkflowState}   "
            + "        AND ps.end_date is null   "
            + "        AND ps.start_date <= :onOrBefore   "
            + "        AND pg.location_id= :location   "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT  p.patient_id,  o.obs_datetime AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${masterCardEncounterType}   "
            + "        AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "        AND o.value_coded =  ${transferedOutOrSuspendedConcept}   "
            + "        AND o.obs_datetime <= :onOrBefore   "
            + "        AND e.location_id =  :location   "
            + "    UNION   "
            + "    SELECT  p.patient_id , e.encounter_datetime AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "        AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "        AND o.value_coded = ${transferedOutOrSuspendedConcept}   "
            + "        AND e.encounter_datetime <= :onOrBefore   "
            + "        AND e.location_id =  :location  "
            + ") lastest   "
            + "WHERE lastest.patient_id NOT  IN("
            + " "
            + "  			     SELECT  p.patient_id    "
            + "	                 FROM patient p      "
            + "	                     INNER JOIN encounter e     "
            + "	                         ON e.patient_id=p.patient_id     "
            + "	                 WHERE  p.voided = 0     "
            + "	                     AND e.voided = 0     "
            + "	                     AND e.encounter_type IN (${adultoSeguimentoEncounterType},"
            + "${pediatriaSeguimentoEncounterType},"
            + "${pharmaciaEncounterType})    "
            + "	                     AND e.encounter_datetime > lastest.last_date "
            + " AND e.encounter_datetime <=  :onOrBefore    "
            + "	                     AND e.location_id =  :location    "
            + "	                 GROUP BY p.patient_id "
            + " UNION "
            + "        			 SELECT  p.patient_id    "
            + "	                 FROM patient p       "
            + "	                      INNER JOIN encounter e      "
            + "	                          ON e.patient_id=p.patient_id      "
            + "	                      INNER JOIN obs o      "
            + "	                          ON o.encounter_id=e.encounter_id      "
            + "	                  WHERE  p.voided = 0      "
            + "	                      AND e.voided = 0      "
            + "	                      AND o.voided = 0      "
            + "	                      AND e.encounter_type = ${masterCardDrugPickupEncounterType}     "
            + "	                      AND o.concept_id = ${artDatePickup}     "
            + "	                      AND o.value_datetime > lastest.last_date  "
            + " AND o.value_datetime <= :onOrBefore      "
            + "	                      AND e.location_id =  :location     "
            + "	                  GROUP BY p.patient_id   "
            + ")  "
            + " GROUP BY lastest.patient_id"
            + " )mostrecent "
            + " GROUP BY mostrecent.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * Get All Patients On TB Treatment
   *
   * @return
   */
  public CohortDefinition getPatientsOnTbTreatment() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsOnTbTreatment");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        TbQueries.getPatientsOnTbTreatmentQuery(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getTBDrugStartDateConcept().getConceptId(),
            hivMetadata.getTBDrugEndDateConcept().getConceptId(),
            hivMetadata.getTBProgram().getProgramId(),
            hivMetadata.getPatientActiveOnTBProgramWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getActiveTBConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTBTreatmentPlanConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId()));

    return cd;
  }
}
