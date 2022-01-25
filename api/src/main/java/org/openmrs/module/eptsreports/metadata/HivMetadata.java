/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component("hivMetadata")
public class HivMetadata extends ProgramsMetadata {

  private String gpArtProgramUuid = "eptsreports.artProgramUuid";

  private String gpPtvEtvProgramUuid = "eptsreports.ptvEtvProgramUuid";

  /**
   * <b>concept_id = 307</b>
   *
   * <p><b>Name:</b> SPUTUM FOR ACID FAST BACILLI
   *
   * <p><b>Description:</b> Ziehl Nielsen stain for tuberculosis, performed on a deep sputum sample
   * from the respiratory tract.
   *
   * @return {@link Concept}
   */
  public Concept getResultForBasiloscopia() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.basiloscopiaUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21187</b>
   *
   * <p><b>Name:</b>ARV REGIMEN 2nd LINE
   *
   * <p><b>Description:</b> from the respiratory tract.
   *
   * @return {@link Concept}
   */
  public Concept getRegArvSecondLine() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.regArvSecondLineUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1792</b>
   *
   * <p><b>Name:</b>JUSTIFICATION TO CHANGE ARV TREATMENT
   *
   * <p><b>Description:</b> from the respiratory tract.
   *
   * @return {@link Concept}
   */
  public Concept getJustificativeToChangeArvTreatment() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.justificativeToChangeArvTreatmentUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 856</b>
   *
   * <p><b>Name:</b> CARGA VIRAL DE HIV
   *
   * <p><b>Description:</b> This is a measure of the number of copies/ml of DNA/RNA in patients with
   * HIV.
   *
   * @return {@link Concept}
   */
  public Concept getHivViralLoadConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivViralLoadConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6334</b>
   *
   * <p><b>Name:</b> CRITERIA FOR ART START
   *
   * <p><b>Description:</b> Are criteria that makes a patient starts antiretroviral treatment
   *
   * @return {@link Concept}
   */
  public Concept getCriteriaForArtStart() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.criteriaForArtStartUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5096</b>
   *
   * <p><b>Name:</b> RETURN VISIT DATE FOR ARV DRUG
   *
   * <p><b>Description:</b> Date set for patient re-raise ARV drugs. If the patient does not appear
   * on this date begins the countdown of days to mark Abandoned after 60 days.
   *
   * @return {@link Concept}
   */
  public Concept getReturnVisitDateForArvDrugConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateForArvDrugConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6123</b>
   *
   * <p><b>Name:</b> DATE OF HIV DIAGNOSIS
   *
   * <p><b>Description:</b> DATE OF HIV+ DIAGNOSIS concept
   *
   * @return {@link Concept}
   */
  public Concept getDateOfHivDiagnosisConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.dateOfHIVDiagnosis");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1190</b>
   *
   * <p><b>Name:</b> HISTORICAL DRUG START DATE
   *
   * <p><b>Description:</b> Information gathered on encounter forms which describes the date of a
   * particular drug's institution. Most often "globbed" with another concept which describes the
   * drug.
   *
   * @return {@link Concept}
   */
  public Concept getARVStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1255</b>
   *
   * <p><b>Name:</b> ANTIRETROVIRAL PLAN
   *
   * <p><b>Description:</b> Question on encounter form. Collects information related to
   * antiretroviral drug therapy plans.
   *
   * @return {@link Concept}
   */
  public Concept getARVPlanConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.arvPlanConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1088</b>
   *
   * <p><b>Name:</b> CURRENT ANTIRETROVIRAL DRUGS USED FOR TREATMENT
   *
   * <p><b>Description:</b> Question on encounter forms: Is the patient currently taking, or has the
   * patient ever taken, any of the following retroviral medications? or Current HIV Medications:.
   * This particular concept stores a history of active use of the associated antiretroviral
   * medications for treatment.
   *
   * @return {@link Concept}
   */
  public Concept getRegimeConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.regimeUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1705</b>
   *
   * <p><b>Name:</b> RESTART
   *
   * <p><b>Description:</b> Resumption of therapy or treatment or relapse after discontinuation
   *
   * @return {@link Concept}
   */
  public Concept getRestartConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.restartConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1707</b>
   *
   * <p><b>Name:</b> DROPPED FROM TREATMENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAbandonedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abandonedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6328</b>
   *
   * <p><b>Name:</b> AZT_3TC_ABC_EFV SECOND REGIMEN
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAzt3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6327</b>
   *
   * <p><b>Name:</b> D4T+3TC+ABC+EFV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getD4t3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6326</b>
   *
   * <p><b>Name:</b> AZT+3TC+ABC+LPV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAzt3tcAbcLpvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6325</b>
   *
   * <p><b>Name:</b> D4T+3TC+ABC+LPV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getD4t3tcAbcLpvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6109</b>
   *
   * <p><b>Name:</b> AZT_DDI_LPV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAztDdiLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_DDI_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1315</b>
   *
   * <p><b>Name:</b> TDF_3TC_EFV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTdf3tcEfvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1314</b>
   *
   * <p><b>Name:</b> AZT_3TC_LPV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAzt3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1313</b>
   *
   * <p><b>Name:</b> ABC_3TC_EFV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAbc3tcEfvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1312</b>
   *
   * <p><b>Name:</b> ABC_3TC_NVP (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAbc3tcNvpConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_NVP_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1311</b>
   *
   * <p><b>Name:</b> ABC_3TC_LPV (SECOND REGIMEN)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAbc3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1310</b>
   *
   * <p><b>Name:</b> TDF_3TC_LPV
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTdf3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6306</b>
   *
   * <p><b>Name:</b> ACEPT CONTACT
   *
   * <p><b>Description:</b> A question in a form that determines if the patient agrees to be contact
   * by the health unit in case of need
   *
   * @return {@link Concept}
   */
  public Concept getAcceptContactConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.acceptContactConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1066</b>
   *
   * <p><b>Name:</b> NO CONCEPT
   *
   * <p><b>Description:</b> Generic answer to a question.
   *
   * @return {@link Concept}
   */
  @Override
  public Concept getNoConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6128</b>
   *
   * <p><b>Name:</b> ISONIAZID PROPHYLAXIS START DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDataInicioProfilaxiaIsoniazidaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataInicioProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6129</b>
   *
   * <p><b>Name:</b> ISONIAZID PROPHYLAXIS END DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDataFinalizacaoProfilaxiaIsoniazidaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataFimProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6122</b>
   *
   * <p><b>Name:</b> ISONIAZID USE CONCEPT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getIsoniazidUsageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.isoniazidUseConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1695</b>
   *
   * <p><b>Name:</b> CD4 ABSOLUTE OBS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCD4AbsoluteOBSConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsoluteOBSUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6314</b>
   *
   * <p><b>Name:</b> COUNCELING ACTIVITY TYPE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCoucelingActivityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coucelingActivityTypeUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21150</b>
   *
   * <p><b>Name:</b> FIRST LINE CONCEPT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getFirstLineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.firstLineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5356</b>
   *
   * <p><b>Name:</b> CURRENT WHO HIV STAGE
   *
   * <p><b>Description:</b> Question asked on encounter form. Expects a numeric answer defining the
   * HIV stage at a particular visit.
   *
   * @return {@link Concept}
   */
  public Concept getcurrentWhoHivStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.currentWhoHivStageUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1205</b>
   *
   * <p><b>Name:</b> WHO STAGE 2 ADULT
   *
   * <p><b>Description:</b> Convenience set.
   *
   * @return {@link Concept}
   */
  public Concept getWho2AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who2AdultStageUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1206</b>
   *
   * <p><b>Name:</b> WHO STAGE 3 ADULT
   *
   * <p><b>Description:</b> Convenience set.
   *
   * @return {@link Concept}
   */
  public Concept getWho3AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who3AdultStageUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1207</b>
   *
   * <p><b>Name:</b> WHO STAGE 4 ADULT
   *
   * <p><b>Description:</b> Convenience set.
   *
   * @return {@link Concept}
   */
  public Concept getWho4AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who4AdultStageUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1366</b>
   *
   * <p><b>Name:</b> PATIENT HAS DIED
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientHasDiedConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientHasDiedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1981</b>
   *
   * <p><b>Name:</b> TYPE OF VISIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTypeOfVisitConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.typeOfVisit");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2003</b>
   *
   * <p><b>Name:</b> ACTIVIST LOCATED MISSING PATIENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.patientFound");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1065</b>
   *
   * <p><b>Name:</b> YES CONCEPT
   *
   * <p><b>Description:</b> Generic answer to a question.
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundYesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2160</b>
   *
   * <p><b>Name:</b> MISSED VISIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getBuscaConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.busca");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6254</b>
   *
   * <p><b>Name:</b> SECOND ATTEMPT
   *
   * <p><b>Description:</b> Date for the second time the activist tried to make the active search
   * for patients
   *
   * @return {@link Concept}
   */
  public Concept getSecondAttemptConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.secondAttempt");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6255</b>
   *
   * <p><b>Name:</b> THIRD ATTEMPTv
   *
   * <p><b>Description:</b> Date for the third time the activist tried to make the active search for
   * patients
   *
   * @return {@link Concept}
   */
  public Concept getThirdAttemptConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.thirdAttempt");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2016</b>
   *
   * <p><b>Name:</b> REASON PATIENT MISSED VISIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDefaultingMotiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.defaultingMotive");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6201</b>
   *
   * <p><b>Name:</b> REASON ARV STOPPED
   *
   * <p><b>Description:</b> Reason for stopped taking ARV drugs during last 7 Days
   *
   * @return {@link Concept}
   */
  public Concept getReasonForStoppedTakingArvDrugsDuringLast7DaysConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForStoppedTakingArvDrugsDuringLast7Days");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6202</b>
   *
   * <p><b>Name:</b> RREASON ARV STOPPED
   *
   * <p><b>Description:</b> Reason for stopped taking ARV drugs during last month
   *
   * @return {@link Concept}
   */
  public Concept getReasonForStoppedTakingArvDrugsDuringLastMonthConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForStoppedTakingArvDrugsDuringLastMonth");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6217</b>
   *
   * <p><b>Name:</b> MAIN REASON FOR DELAY IN TAKING ARV
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getMainReasonForDelayInTakingArvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.mainReasonForDelayInTakingArv");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2024</b>
   *
   * <p><b>Name:</b> PATIENT RECORD HAS WRONG ADDRESS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientRecordHasWrongAddressConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientRecordHasWrongAddress");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2026</b>
   *
   * <p><b>Name:</b> PATIENT MOVED HOUSES
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientMovedHousesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientMovedHouses");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2011</b>
   *
   * <p><b>Name:</b> PACIENTE TOOK A TRIP
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientTookATripConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientTookATrip");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2032</b>
   *
   * <p><b>Name:</b> OTHER REASONS WHY PATIENT WAS NOT LOCATED BY ACTIVIST
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getOtherReasonsWhyPatientWasNotLocatedByActivistConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.otherReasonsWhyPatientWasNotLocatedByActivist");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2158</b>
   *
   * <p><b>Name:</b> REPORT OF VISIT SUPPORT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReportOfVisitSupportConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reportOfVisitSupport");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2157</b>
   *
   * <p><b>Name:</b> PATIENT HAD DIFICULTY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientHadDifficultyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientHadDifficulty");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1272</b>
   *
   * <p><b>Name:</b> REFERRALS ORDERED
   *
   * <p><b>Description:</b> Question on encounter form. Answers are referrals made during a
   * particular patient visit.
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundForwardedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientFoundForwarded");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2037</b>
   *
   * <p><b>Name:</b> REASON PATIENT NOT FOUND PROVIDED BY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getWhoGaveInformationConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.whoGaveInformation");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2180</b>
   *
   * <p><b>Name:</b> DELIVERY DATE OF THE CARD HOME VISIT THE HEALTH UNIT <b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCardDeliveryDateConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cardDeliveryDate");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1709</b>
   *
   * <p><b>Name:</b> SUSPEND TREATMENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSuspendedTreatmentConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspendedTreatmentConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6269</b>
   *
   * <p><b>Name:</b> ACTIVE ON PROGRAM CONCEPT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getActiveOnProgramConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeOnProgramConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1126</b>
   *
   * <p><b>Name:</b> UROGENITAL EXAM FINDINGS
   *
   * <p><b>Description:</b> Findings noted on examination of the urogenital region.
   *
   * @return {@link Concept}
   */
  public Concept getUrogenitalExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.urogenitalExamFindingsConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1115</b>
   *
   * <p><b>Name:</b> NORMAL
   *
   * <p><b>Description:</b> General descriptive answer.
   *
   * @return {@link Concept}
   */
  public Concept getNormalConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.normalConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1116</b>
   *
   * <p><b>Name:</b> ABNORMAL
   *
   * <p><b>Description:</b> General descriptive answer
   *
   * @return {@link Concept}
   */
  public Concept getAbnormalConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abnormalConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1399</b>
   *
   * <p><b>Name:</b> SECRETIONS
   *
   * <p><b>Description:</b> SECRETIONS [Q HDD PROCESSO ADULTO: Genital examination]
   *
   * @return {@link Concept}
   */
  public Concept getSecretionsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.secretionsConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1400</b>
   *
   * <p><b>Name:</b> CONDYLOMAS
   *
   * <p><b>Description:</b> [Q HDD PROCESSO ADULTO: Genital examination]
   *
   * @return {@link Concept}
   */
  public Concept getCondylomasConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.condylomasConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1602</b>
   *
   * <p><b>Name:</b> ULCERS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getUlcersConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.ulcersConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5488</b>
   *
   * <p><b>Name:</b> ADHERENCE COUNSELING
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAdherenceCoucelingConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adherenceCounselingUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5497</b>
   *
   * <p><b>Name:</b> CD4 COUNT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCD4AbsoluteConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsolutoUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 730</b>
   *
   * <p><b>Name:</b> CD4 %
   *
   * <p><b>Description:</b> Percentage of T-helper lymphocytes.
   *
   * @return {@link Concept}
   */
  public Concept getCD4PercentConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4PercentUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1714</b>
   *
   * <p><b>Name:</b> ADHERENCE
   *
   * <p><b>Description:</b> Generic inquiry about treatment adherance since last farmacy visit
   *
   * @return {@link Concept}
   */
  public Concept getAdherence() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.poorAdherenceUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2015</b>
   *
   * <p><b>Name:</b> PATIENT DOES NOT LIKE ARV TREATMENT SIDE EFFECTS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAdverseReaction() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adverseReactionUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6292</b>
   *
   * <p><b>Name:</b> NEUTROPENIA
   *
   * <p><b>Description:</b> Neutropenia e uma doenca hematologica caracterizada por um numero
   * anormalmente baixo de neutrofilos, o mais importante tipo de célula branca do sangue, no sangue
   *
   * @return {@link Concept}
   */
  public Concept getNeutropenia() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.neutropenia");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6293</b>
   *
   * <p><b>Name:</b> PANCREATITIS
   *
   * <p><b>Description:</b> Pancreatite e a inflamacao do pancreas. O pancreas e um orgao situado na
   * parte superior do abdomen, aproximadamente atras do estomago
   *
   * @return {@link Concept}
   */
  public Concept getPancreatitis() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.pancreatitis");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6294</b>
   *
   * <p><b>Name:</b> HEPATOTOXICITY
   *
   * <p><b>Description:</b> Hepatotoxicity (liver toxicity) is a liver damage caused by chemicals
   * called hepatotoxins. It may be a side effect of certain medications
   *
   * @return {@link Concept}
   */
  public Concept getHepatotoxicity() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hepatotoxicity");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6295</b>
   *
   * <p><b>Name:</b> PSYCHOLOGICAL CHANGES
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPsychologicalChanges() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.psychologicalChanges");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6296</b>
   *
   * <p><b>Name:</b> MYOPATHY
   *
   * <p><b>Description:</b> Myopathy is the generic name of diseases and muscle diseases in which
   * muscle fibers do not work many times, resulting in muscle weakness.
   *
   * @return {@link Concept}
   */
  public Concept getMyopathy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.myopathy");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6927</b>
   *
   * <p><b>Name:</b> SKIN ALLERGY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSkinAllergy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.skinAllergy");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6298</b>
   *
   * <p><b>Name:</b> LIPODYSTROPHY
   *
   * <p><b>Description:</b> Lipodystrophy is the designation of a set of bodily changes, related to
   * fat distribution, which are more frequently observed in people being treated with
   * antiretrovirals.
   *
   * @return {@link Concept}
   */
  public Concept getLipodystrophy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.lipodystrophy");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6299</b>
   *
   * <p><b>Name:</b> LACTIC ACIDOSIS
   *
   * <p><b>Description:</b> It is an acute metabolic disease caused by eating sudden grain and other
   * food highly fermentable in bulk, which is characterized by loss of appetite, depression and
   * death
   *
   * @return {@link Concept}
   */
  public Concept getLacticAcidosis() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lacticAcidosis");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 821</b>
   *
   * <p><b>Name:</b> PERIPHERAL NEUROPATHY
   *
   * <p><b>Description:</b> A disease or degenerative state (as polyneuropathy) of the peripheral
   * nerves in which motor, sensory, or vasomotor nerve fibers may be affected and which is marked
   * by muscle weakness and atrophy, pain, and numbness.
   *
   * @return {@link Concept}
   */
  public Concept getPeripheralNeuropathy() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.peripheralNeuropathy");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 16</b>
   *
   * <p><b>Name:</b> DIARRHEA
   *
   * <p><b>Description:</b> Diarrhea due to an unspecified cause.
   *
   * @return {@link Concept}
   */
  public Concept getDiarrhea() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.diarrhea");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1406</b>
   *
   * <p><b>Name:</b> OTHER DIAGNOSIS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getOtherDiagnosis() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.otherDiagnosis");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23724</b>
   *
   * <p><b>Name:</b> GAAC
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getGaac() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.gaac");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23725</b>
   *
   * <p><b>Name:</b> AF
   *
   * <p><b>Description:</b> Family Approach
   *
   * @return {@link Concept}
   */
  public Concept getFamilyApproach() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.familyApproach");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23726</b>
   *
   * <p><b>Name:</b> CA
   *
   * <p><b>Description:</b> Accession Clubs
   *
   * @return {@link Concept}
   */
  public Concept getAccessionClubs() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.accessionClubs");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23727</b>
   *
   * <p><b>Name:</b> PU
   *
   * <p><b>Description:</b> Single Stop
   *
   * @return {@link Concept}
   */
  public Concept getSingleStop() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.singleStop");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23729</b>
   *
   * <p><b>Name:</b> FR
   *
   * <p><b>Description:</b> Rapid Flow
   *
   * @return {@link Concept}
   */
  public Concept getRapidFlow() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.rapidFlow");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23730</b>
   *
   * <p><b>Name:</b> DT
   *
   * <p><b>Description:</b> Quartely Dispensation
   *
   * @return {@link Concept}
   */
  public Concept getQuarterlyDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.quarterlyDispensation");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23731</b>
   *
   * <p><b>Name:</b> DC
   *
   * <p><b>Description:</b> Community Dispensation
   *
   * @return {@link Concept}
   */
  public Concept getCommunityDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.communityDispensation");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23732</b>
   *
   * <p><b>Name:</b> Another Model
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAnotherModel() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.anotherModel");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1256</b>
   *
   * <p><b>Name:</b> START DRUGS
   *
   * <p><b>Description:</b> Answer on encounter form. Implies that a patient will be started on
   * drugs for that particular encounter.
   *
   * @return {@link Concept}
   */
  public Concept getStartDrugs() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugsConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1257</b>
   *
   * <p><b>Name:</b> CONTINUE REGIMEN
   *
   * <p><b>Description:</b> Answer on encounter form. Implies that a patient will continue on the
   * same drug regimen as previously described.
   *
   * @return {@link Concept}
   */
  public Concept getContinueRegimenConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.continueRegimen");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23808</b>
   *
   * <p><b>Name:</b> PRE ART Start Date
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPreArtStartDate() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtStartDate");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23866</b>
   *
   * <p><b>Name:</b> ART PICKUP DATE
   *
   * <p><b>Description:</b> Date of ART Pickup
   *
   * @return {@link Concept}
   */
  public Concept getArtDatePickupMasterCard() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtPickupDate");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6300</b>
   *
   * <p><b>Name:</b> TYPE OF PATIENT TRANSFERRED FROM
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTypeOfPatientTransferredFrom() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeOfPatientTransferredFrom");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6272</b>
   *
   * <p><b>Name:</b> STATED OF STAY PRE ART PATIENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStateOfStayOfPreArtPatient() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayOfPreArtPatient");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6273</b>
   *
   * <p><b>Name:</b> STATED OF STAY ART PATIENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStateOfStayOfArtPatient() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.stateOfStayArtPatient");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2031</b>
   *
   * <p><b>Name:</b> REASON PATIENT NOT FOUND BY ACTIVIST
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReasonPatientNotFound() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reasonPatientNotFound");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1383</b>
   *
   * <p><b>Name:</b> PATIENT IS DEAD - Incorrect
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientIsDead() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientIsdeadIncorrect");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2027</b>
   *
   * <p><b>Name:</b> PATIENT IS DEAD
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientIsDeadCorrect() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.patientIsdead");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6276</b>
   *
   * <p><b>Name:</b> Date of ART Pickup
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getArtStatus() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.art");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1706</b>
   *
   * <p><b>Name:</b> TRANSFERRED OUT TO ANOTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTransferredOutConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferOutToAnotherFacilityConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23863</b>
   *
   * <p><b>Name:</b> AUTO TRANSFER
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAutoTransferConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.autoTransferConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6272</b>
   *
   * <p><b>Name:</b> STATE OF STAY PRIOR ART PATIENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStateOfStayPriorArtPatientConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayPriorArtPatientConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23722</b>
   *
   * <p><b>Name:</b> APPLICATION FOR LABORATORY RESEARCH
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getApplicationForLaboratoryResearch() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.applicationForLaboratoryResearch");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1305</b>
   *
   * <p><b>Name:</b> HIV VIRAL LOAD, QUALITATIVE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getHivViralLoadQualitative() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.viralLoadQualitativeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23703</b>
   *
   * <p><b>Name:</b> KEY POPULATION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getKeyPopulationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.keyPopulationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1377</b>
   *
   * <p><b>Name:</b> HOMOSEXUAL
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getHomosexualConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.homosexualConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 20454</b>
   *
   * <p><b>Name:</b> DRUG USE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDrugUseConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.drugUseConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 20426</b>
   *
   * <p><b>Name:</b> IMPRISONMENT AND OTHER INCARCERATION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getImprisonmentConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.imprisonmentConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1901</b>
   *
   * <p><b>Name:</b> SEX WORKER
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSexWorkerConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.sexWorkerConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23865</b>
   *
   * <p><b>Name:</b> ART PICKUP
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getArtPickupConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artPickupConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1465</b>
   *
   * <p><b>Name:</b> DATE OF LAST MENSTRUATION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDateOfLastMenstruationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfLastMenstruationConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23739</b>
   *
   * <p><b>Name:</b> TYPE OF DISPENSATION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTypeOfDispensationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeOfDispensationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23720</b>
   *
   * <p><b>Name:</b> QUARTELY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getQuarterlyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.quarterlyConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1267</b>
   *
   * <p><b>Name:</b> COMPLETED
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCompletedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.completedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23748</b>
   *
   * <p><b>Name:</b> CYTOPENIA
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCytopeniaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cytopeniaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23749</b>
   *
   * <p><b>Name:</b> NEPHROTOXICITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getNephrotoxicityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nephrotoxicityConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 29</b>
   *
   * <p><b>Name:</b> HEPATITIS
   *
   * <p><b>Description:</b> Liver infection with an unknown organism or unspecified non-infectious
   * liver inflammation.
   *
   * @return {@link Concept}
   */
  public Concept getHepatitisConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hepatitisConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23750</b>
   *
   * <p><b>Name:</b> STEVENS-JOHNSON SYNDROME
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStevensJonhsonSyndromeConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stevensJonhsonSyndromeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23751</b>
   *
   * <p><b>Name:</b> HYPERSENSITIVITY TO ABC/RAL
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getHypersensitivityToAbcOrRailConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hypersensitivityToAbcOrRailConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23752</b>
   *
   * <p><b>Name:</b> HEPATIC STEATOSIS WITH HYPERLACTATAEMIA
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getHepaticSteatosisWithHyperlactataemiaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hepaticSteatosisWithHyperlactataemiaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1113</b>
   *
   * <p><b>Name:</b> TUBERCULOSIS DRUG TREATMENT START DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTBDrugStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6120</b>
   *
   * <p><b>Name:</b> TB DRUG END DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTBDrugEndDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbDgrusTreatmentEndDateUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23761</b>
   *
   * <p><b>Name:</b> ACTIVE TB
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getActiveTBConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1268</b>
   *
   * <p><b>Name:</b> TUBERCULOSIS TREATMENT PLAN
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTBTreatmentPlanConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentPlanConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1306</b>
   *
   * <p><b>Name:</b> BEYOND DETECTABLE LIMIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getBeyondDetectableLimitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.beyondDtectableLimitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23814</b>
   *
   * <p><b>Name:</b> BEYOND DETECTABLE LIMIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getUndetectableViralLoadConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.undetectableViralLoadConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23904</b>
   *
   * <p><b>Name:</b> LESS THAN 839 COPIES/ML
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLessThan839CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan839CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23905</b>
   *
   * <p><b>Name:</b> LESS THAN 10 COPIES/ML
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLessThan10CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan10CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23906</b>
   *
   * <p><b>Name:</b> LESS THAN 20 COPIES/ML
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLessThan20CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan20CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23907</b>
   *
   * <p><b>Name:</b> LESS THAN 40 COPIES/ML
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLessThan40CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan40CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23908</b>
   *
   * <p><b>Name:</b> LESS THAN 400 COPIES/ML
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLessThan400CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan400CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1067</b>
   *
   * <p><b>Name:</b> UNKNOWN
   *
   * <p><b>Description:</b> Generic answer to a question
   *
   * @return {@link Concept}
   */
  public Concept getUnkownConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.unknownConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1098</b>
   *
   * <p><b>Name:</b> MONTHLY
   *
   * <p><b>Description:</b> Records whether the patient is receiving ARVs by quarterly dispensation
   *
   * @return {@link Concept}
   */
  public Concept getMonthlyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.monthlyConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23888</b>
   *
   * <p><b>Name:</b> SEMIANNUAL DISPENSATION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSemiannualDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.semiannualConceptUuid");

    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2005</b>
   *
   * <p><b>Name:</b> PATIENT FORGOT VISIT DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientForgotVisitDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientForgotVisitDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2006</b>
   *
   * <p><b>Name:</b> PATIENT IS BEDRIDDEN AT HOME
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientIsBedriddenAtHomeConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientIsBedriddenAtHomeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2007</b>
   *
   * <p><b>Name:</b> DISTANCE OR MONEY FOR TRANSPORT IS TO MUCH FOR PATIENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDistanceOrMoneyForTransportIsTooMuchForPatientConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.distanceOrMoneyForTransportIsTooMuchForPatientConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2010</b>
   *
   * <p><b>Name:</b> PATIENT IS DISSATISFIED WITH DAY HOSPITAL SERVICES
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientIsDissatisfiedWithDayHospitalServicesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.patientIsDissatisfiedWithDayHospitalServicesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2013</b>
   *
   * <p><b>Name:</b> PATIENT IS TREATING HIV WITH TRADITIONAL MEDICINE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientIsTreatingHivWithTraditionalMedicineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.patientIsTreatingHivWithTraditionalMedicineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2017</b>
   *
   * <p><b>Name:</b> OTHER REASON WHY PATIENT MISSED VISIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getOtherReasonWhyPatientMissedVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.otherReasonWhyPatientMissedVisitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23915</b>
   *
   * <p><b>Name:</b> FEAR OF THE PROVIDER
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getFearOfTheProviderConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.fearOfTheProviderConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23946</b>
   *
   * <p><b>Name:</b> ABSENCE OF HEALTH PROVIDER IN HEALTH UNIT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAbsenceOfHealthProviderInHealthUnitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.absenceOfHealthProviderInHealthUnitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6331</b>
   *
   * <p><b>Name:</b> B PLUS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getBpostiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.bPlusConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23944</b>
   *
   * <p><b>Name:</b> REASON PATIENT NOT FOUND BY ACTIVIST (2and Visit)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReasonPatientNotFoundByActivist2ndVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivist2ndVisitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23945</b>
   *
   * <p><b>Name:</b> REASON PATIENT NOT FOUND BY ACTIVIST (3rd Visit)
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReasonPatientNotFoundByActivist3rdVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivist3rdVisitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>encounterType_id = 6</b>
   *
   * <p><b>Name:</b> S.TARV: ADULTO SEGUIMENTO
   *
   * <p><b>Description:</b> Seguimento visita do paciente adulto
   *
   * @return {@link EncounterType}
   */
  public EncounterType getAdultoSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 9</b>
   *
   * <p><b>Name:</b> S.TARV: PEDIATRIA SEGUIMENTO
   *
   * <p><b>Description:</b> Seguimento visita do paciente pediatria
   *
   * @return {@link EncounterType}
   */
  public EncounterType getPediatriaSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 21</b>
   *
   * <p><b>Name:</b> S.TARV: BUSCA ACTIVA
   *
   * <p><b>Description:</b> Busca Activa
   *
   * @return {@link EncounterType}
   */
  public EncounterType getBuscaActivaEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.buscaActivaEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 5</b>
   *
   * <p><b>Name:</b> S.TARV: ADULTO INICIAL A
   *
   * <p><b>Description:</b> Primeira visita do paciente adulto
   *
   * @return {@link EncounterType}
   */
  public EncounterType getARVAdultInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoInitialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 7</b>
   *
   * <p><b>Name:</b> S.TARV: PEDIATRIA INICIAL A
   *
   * <p><b>Description:</b> Primeira visita do paciente pediatria
   *
   * @return {@link EncounterType}
   */
  public EncounterType getARVPediatriaInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaInicialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 18</b>
   *
   * <p><b>Name:</b> S.TARV: FARMACIA
   *
   * <p><b>Description:</b> Farmacia
   *
   * @return {@link EncounterType}
   */
  public EncounterType getARVPharmaciaEncounterType() {
    String gpSTarvFarmaciaEncounterTypeUuid = "eptsreports.sTarvFarmaciaEncounterTypeUuid";
    String uuid =
        Context.getAdministrationService().getGlobalProperty(gpSTarvFarmaciaEncounterTypeUuid);
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 29</b>
   *
   * <p><b>Name:</b> S.TARV: AVALIACAO E PREPARACAO DO CANDIDATO TARV
   *
   * <p><b>Description:</b> Avalicao e preparacao psicologica do candidato ao TARV
   *
   * @return {@link EncounterType}
   */
  public EncounterType getEvaluationAndPrepForARTEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.evaluationAndPrepForARTEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 13</b>
   *
   * <p><b>Name:</b> MISAU: LABORATORIO
   *
   * <p><b>Description:</b> Laboratorio
   *
   * @return {@link EncounterType}
   */
  public EncounterType getMisauLaboratorioEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.misauLaboratorioEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 34</b>
   *
   * <p><b>Name:</b> APSS: PREVENÇÃO POSITIVA - INICIAL
   *
   * <p><b>Description:</b> Avaliação Psico-Social e Prevenção Positiva
   *
   * @return {@link EncounterType}
   */
  public EncounterType getPrevencaoPositivaInicialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.apssPrevencaoPositivaInicialInicialEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 35</b>
   *
   * <p><b>Name:</b> APSS: PREVENÇÃO POSITIVA - SEGUIMENTO
   *
   * <p><b>Description:</b> Ficha de apoio psicossocial e prevenção positiva - Seguimento
   *
   * @return {@link EncounterType}
   */
  public EncounterType getPrevencaoPositivaSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.apssPrevencaoPositivaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 36 </b> Coming from ICAP
   *
   * <p><b>Name:</b> S.TARV: VISITA DE APOIO E REINTEGRACAO - PARTE A
   *
   * <p><b>Description:</b> Consulta de dados de visita ao domicilio para apoio e reintegração do
   * paciente
   *
   * @return {@link EncounterType}
   */
  public EncounterType getVisitaApoioReintegracaoParteAEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.visitaApoioReintegracaoParteA");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 37 </b> Coming from ICAP
   *
   * <p><b>Name:</b> S.TARV: VISITA DE APOIO E REINTEGRACAO - PARTE B
   *
   * <p><b>Description:</b> Consulta de dados de visita ao domicilio para apoio e reintegração do
   * paciente
   *
   * @return {@link EncounterType}
   */
  public EncounterType getVisitaApoioReintegracaoParteBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.visitaApoioReintegracaoParteB");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 1</b>
   *
   * <p><b>Name:</b> S.TARV: ADULTO INICIAL B
   *
   * <p><b>Description:</b> Outpatient Adult Initial Visit part 2
   *
   * @return {@link EncounterType}
   */
  public EncounterType getARVAdultInitialBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adultInitialBEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 3</b>
   *
   * <p><b>Name:</b> S.TARV: PEDIATRIA INICIAL B
   *
   * <p><b>Description:</b> Primeira visita de paciente pediatrico, parte B. As duas ultimas paginas
   *
   * @return {@link EncounterType}
   */
  public EncounterType getARVPediatriaInitialBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pediatriaInitialBEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 19</b>
   *
   * <p><b>Name:</b> S.TARV: ACONSELHAMENTO
   *
   * <p><b>Description:</b> MISAU/HDD TARV Aconselhamento
   *
   * @return {@link EncounterType}
   */
  public EncounterType getArtAconselhamentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 24</b>
   *
   * <p><b>Name:</b> S.TARV: ACONSELHAMENTO SEGUIMENTO
   *
   * <p><b>Description:</b> Seguimento Aconselhamento
   *
   * @return {@link EncounterType}
   */
  public EncounterType getArtAconselhamentoSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoSeguimentoEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 52</b>
   *
   * <p><b>Name:</b> Levantamento de ARV Master Card
   *
   * <p><b>Description:</b> Registo de levantamento de ARV no Master Card
   *
   * @return {@link EncounterType}
   */
  public EncounterType getMasterCardDrugPickupEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.masterCardDrugPickupEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 53</b>
   *
   * <p><b>Name:</b> Master Card - Ficha Resumo
   *
   * <p><b>Description:</b> Ficha resumo no master card
   *
   * @return {@link EncounterType}
   */
  public EncounterType getMasterCardEncounterType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.masterCardEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 51</b>
   *
   * <p><b>Name:</b> FSR
   *
   * <p><b>Description:</b> Carga Viral
   *
   * @return {@link EncounterType}
   */
  public EncounterType getFsrEncounterType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.fsrEncounterType");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 61</b>
   *
   * <p><b>Name:</b> LIVRO DE REGISTO DIÁRIO DE CHAMADAS E VISITAS DOMICILIARES
   *
   * <p><b>Description:</b>
   *
   * @return {@link EncounterType}
   */
  public EncounterType getLivroRegistoChamadasVisistasDomiciliaresEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.livroRegistoDiarioChamadasVisitasTypeUuid");
    return getEncounterType(uuid);
  }

  // Programs

  /**
   * <b>program_id = 2</b>
   *
   * <p><b>Name:</b> SERVICO TARV - TRATAMENTO
   *
   * <p><b>Description:</b> Programa de seguimento e tratamento aos pacientes HIV+
   *
   * @return {@link Program}
   */
  public Program getARTProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgram(uuid);
  }

  /**
   * <b>program_id = 8</b>
   *
   * <p><b>Name:</b> PTV/ETV
   *
   * <p><b>Description:</b> Programa de representa o estado de gravidez de uma mulher
   *
   * @return {@link Program}
   */
  public Program getPtvEtvProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgram(uuid);
  }

  /**
   * <b>program_id = 1 </b>
   *
   * <p><b>Name:</b> SERVICO TARV - CUIDADO
   *
   * <p><b>Description:</b> Programa de seguimento e cuidado aos pacientes HIV+
   *
   * @return {@link Program}
   */
  public Program getHIVCareProgram() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgram(uuid);
  }

  /**
   * <b>program_id = 5 </b>
   *
   * <p><b>Name:</b> TUBERCULOSE
   *
   * <p><b>Description:</b> Programa de Combate a Tuberculose
   *
   * @return {@link Program}
   */
  public Program getTBProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.tbProgramUuid");
    return getProgram(uuid);
  }

  // Identifier types
  /**
   * <b>program_id = 2 </b>
   *
   * <p><b>Name:</b> NID (SERVICO TARV)
   *
   * <p><b>Description:</b> Numero de Identificaçao de Doente, serviço TARV
   *
   * @return {@link PatientIdentifierType}
   */
  public PatientIdentifierType getNidServiceTarvIdentifierType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.nidServicoTarvUuid");
    return getPatientIdentifierType(uuid);
  }

  // Program Workflow States

  /**
   * <b>program_workflow_state_id = 7 </b>
   *
   * <p><b>Name:</b> TRANSFERRED OUT TO ANOTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFERRED OUT TO ANOTHER FACILITY");
  }

  /**
   * <b>program_workflow_state_id = 29 </b>
   *
   * <p><b>Name:</b> ART CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState() {
    // TODO Refactor this method, use
    // #getTransferredFromOtherHealthFacilityWorkflowState(Program,
    // ProgramWorkflow)
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    String transferFromOtherUuid =
        Context.getAdministrationService()
            .getGlobalProperty(gpTransferFromOtherFacilityConceptUuid);
    return getProgramWorkflowState(artProgramUuid, "2", transferFromOtherUuid);
  }

  /**
   * <b>program_workflow_state_id = 28 </b>
   *
   * <p><b>Name:</b> HIV CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareTransferredFromOtherHealthFacilityWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 29 </b>
   *
   * <p><b>Name:</b> ART CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtTransferredFromOtherHealthFacilityWorkflowState() {
    Program hivCareProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
  }

  /**
   * <b>program_workflow_state_id = 3 </b>
   *
   * <p><b>Name:</b> HIV CARE TRANSFERRED OUT TO ANOTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareTransferredOutToAnotherHealthFacilityWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getTransferredOutToAnotherHealthFacilityWorkflowState(hivCareProgram, workflow);
  }

  /**
   * <b>program_workflow_state_id = 1 </b>
   *
   * <p><b>Name:</b> HIV CARE ACTIVE ON PROGRAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareActiveOnProgramWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getActiveOnProgramWorkflowState(hivCareProgram, workflow);
  }

  /**
   * <b>program_workflow_state_id = 6 </b>
   *
   * <p><b>Name:</b> ART CARE ACTIVE ON PROGRAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtActiveOnProgramWorkflowState() {
    Program hivCareProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getActiveOnProgramWorkflowState(hivCareProgram, workflow);
  }

  /**
   * <b>program_workflow_state_id = 2 </b>
   *
   * <p><b>Name:</b> HIV CARE ABANDONED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareAbandonedWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getAbandonedWorkflowState(hivCareProgram, workflow);
  }

  /**
   * <b>program_workflow_state_id = 8 </b>
   *
   * <p><b>Name:</b> ART CARE SUSPEND TREATMENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getSuspendedTreatmentWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "SUSPEND TREATMENT");
  }

  /**
   * <b>program_workflow_state_id = 9 </b>
   *
   * <p><b>Name:</b> ART CARE ABANDONED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getAbandonedWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "ABANDONED");
  }

  /**
   * <b>program_workflow_state_id = 10 </b>
   *
   * <p><b>Name:</b> ART CARE PATIENT HAS DIED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPatientHasDiedWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "PATIENT HAS DIED");
  }

  /**
   * <b>program_workflow_state_id = 27 </b>
   *
   * <p><b>Name:</b> GAVE BIRTH
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPatientGaveBirthWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "GAVE BIRTH");
  }

  /**
   * <b>program_workflow_state_id = 5 </b>
   *
   * <p><b>Name:</b> HIV CARE PATIENT HAS DIED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareDeadWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getDeadWorkflowState(hivCareProgram, workflow);
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 6 </b>
   *
   * <p><b>Name:</b> ART CARE ACTIVE ON PROGRAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPateintActiveArtWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "c50d6bdc-8a79-43ae-ab45-abbaa6b45e7d");
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 29 </b>
   *
   * <p><b>Name:</b> ART CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPateintTransferedFromOtherFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFER FROM OTHER FACILITY");
  }

  // TODO: Duplicated and Missing program_workflow_id
  public ProgramWorkflowState getPateintPregnantWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "PREGNANT");
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 1 </b>
   *
   * <p><b>Name:</b> HIV CARE ACTIVE ON PROGRAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPateintActiveOnHIVCareProgramtWorkflowState() {
    String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "ACTIVE ON PROGRAM");
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 28 </b>
   *
   * <p><b>Name:</b> HIV CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPateintTransferedFromOtherFacilityHIVCareWorkflowState() {
    String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "TRANSFER FROM OTHER FACILITY");
  }

  /**
   * <b>program_workflow_state_id = 16 </b>
   *
   * <p><b>Name:</b> TUBERCULOSE ACTIVE ON PROGRAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getPatientActiveOnTBProgramWorkflowState() {
    String getTBProgramUuid = "eptsreports.tbProgramUuid";
    String tbProgramUuid = Context.getAdministrationService().getGlobalProperty(getTBProgramUuid);
    return getProgramWorkflowState(tbProgramUuid, "4", "ACTIVE ON PROGRAM");
  }

  /**
   * <b>concept_id = 5356</b>
   *
   * <p><b>Name:</b> CURRENT WHO HIV STAGE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCurrentWHOHIVStageConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStageConceptUuid"));
  }

  /**
   * <b>concept_id = 44</b>
   *
   * <p><b>Name:</b> PREGNANCY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPregnancyResponseConcept() {
    return getConcept(
        Context.getAdministrationService().getGlobalProperty("eptsreports.pregnancyConceptUuid"));
  }

  /**
   * <b>concept_id = 23818</b>
   *
   * <p><b>Name:</b> REASON FOR REQUESTING VIRAL LOAD
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReasonForRequestingViralLoadConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForRequestingViralLoadConceptUuid"));
  }

  /**
   * <b>concept_id = 23817</b>
   *
   * <p><b>Name:</b> ROUTINE FOR REQUESTING VIRAL LOAD
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getRoutineForRequestingViralLoadConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.routineForRequestingViralLoadConceptUuid"));
  }

  /**
   * <b>concept_id = 843</b>
   *
   * <p><b>Name:</b> REGIMEN FAILURE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getRegimenFailureConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.regimenFailureConceptUuid"));
  }

  /**
   * <b>concept_id = 23881</b>
   *
   * <p><b>Name:</b> SUSPECTED IMMUNE FAILURE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSuspectedImmuneFailureConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspectedImmuneFailureConceptUuid"));
  }

  /**
   * <b>concept_id = 23864</b>
   *
   * <p><b>Name:</b> REPEAT AFTER BREASTFEEDING
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getRepeatAfterBreastfeedingConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.repeatAfterBreastfeedingConceptUuid"));
  }

  /**
   * <b>concept_id = 23882</b>
   *
   * <p><b>Name:</b> CLINICAL SUSPICION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getClinicalSuspicionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.clinicalSuspicionConceptUuid"));
  }

  public ProgramWorkflow getPreArtWorkflow() {
    return getProgramWorkflow(getHIVCareProgram().getUuid(), "1");
  }

  private ProgramWorkflowState getActiveOnProgramWorkflowState(
      Program program, ProgramWorkflow programWorkflow) {
    Concept activeOnProgram = getActiveOnProgramConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), activeOnProgram.getUuid());
  }

  private ProgramWorkflowState getAbandonedWorkflowState(
      Program program, ProgramWorkflow programWorkflow) {
    Concept abandoned = getAbandonedConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), abandoned.getUuid());
  }

  private ProgramWorkflowState getDeadWorkflowState(
      Program program, ProgramWorkflow programWorkflow) {
    Concept dead = getPatientHasDiedConcept();
    return getProgramWorkflowState(program.getUuid(), programWorkflow.getUuid(), dead.getUuid());
  }

  private ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState(
      Program program, ProgramWorkflow programWorkflow) {
    Concept transferOutToAnotherFacility = getTransferOutToAnotherFacilityConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), transferOutToAnotherFacility.getUuid());
  }

  private ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState(
      Program program, ProgramWorkflow programWorkflow) {
    Concept transferFromOtherFacility = getTransferFromOtherFacilityConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), transferFromOtherFacility.getUuid());
  }

  /**
   * <b>program_workflow_state_id = 4 </b>
   *
   * <p><b>Name:</b> HIV CARE START DRUGS
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtCareInitiatedWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    Concept startDrugs = getStartDrugsConcept();
    return getProgramWorkflowState(
        hivCareProgram.getUuid(), workflow.getUuid(), startDrugs.getUuid());
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 8 </b>
   *
   * <p><b>Name:</b> ART CARE SUSPEND TREATMENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtSuspendedTreatmentWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    Concept suspendedTreatment = getSuspendedTreatmentConcept();
    return getProgramWorkflowState(
        artProgram.getUuid(), workflow.getUuid(), suspendedTreatment.getUuid());
  }

  private ProgramWorkflow getArtWorkflow() {
    return getProgramWorkflow(getARTProgram().getUuid(), "2");
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 29 </b>
   *
   * <p><b>Name:</b> ART CARE TRANSFERRED FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtTransferredOutToAnotherHealthFacilityWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getTransferredOutToAnotherHealthFacilityWorkflowState(artProgram, workflow);
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 10 </b>
   *
   * <p><b>Name:</b> ART CARE PATIENT HAS DIED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtDeadWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getDeadWorkflowState(artProgram, workflow);
  }

  // TODO: Duplicated method
  /**
   * <b>program_workflow_state_id = 9 </b>
   *
   * <p><b>Name:</b> ART CARE ABANDONED
   *
   * <p><b>Description:</b>
   *
   * @return {@link ProgramWorkflowState}
   */
  public ProgramWorkflowState getArtAbandonedWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getAbandonedWorkflowState(artProgram, workflow);
  }

  /**
   * <b>person_attribute_type_id = 24 </b>
   *
   * <p><b>Name:</b> Identificador definido localmente 01
   *
   * @return {@link PersonAttributeType}
   */
  public PersonAttributeType getIdentificadorDefinidoLocalmente01() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.identificadorDefinidoLocalment01Uuid");
    return getPersonAttributeType(uuid);
  }

  // TODO: Duplicated Method
  /**
   * <b>concept_id = 6331</b>
   *
   * <p><b>Name:</b> B PLUS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getBPlusConcept() {
    return getConcept(
        Context.getAdministrationService().getGlobalProperty("eptsreports.bPlusConceptUuid"));
  }

  /**
   * <b>concept_id = 6275</b>
   *
   * <p><b>Name:</b> PRE-TARV
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPreTarvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preTarvConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23891</b>
   *
   * <p><b>Name:</b> DATE OF MASTER CARD FILE OPENING
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getDateOfMasterCardFileOpeningConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfMasterCardFileOpeningConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21148</b>
   *
   * <p><b>Name:</b> THERAPEUTIC LINE - SECOND LINE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSecondLineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artSecondLineSwitchUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21151</b>
   *
   * <p><b>Name:</b> THERAPEUTIC LINE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTherapeuticLineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.therapeuticLineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23758</b>
   *
   * <p><b>Name:</b> Has TB symptoms
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTBSymptomsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hasTbSymptomsConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23886</b>
   *
   * <p><b>Name:</b> Pre-ART Counseling
   *
   * <p><b>Description: Aconselhamento Pré-TARV </b>
   *
   * @return {@link Concept}
   */
  public Concept getPreARTCounselingConcept() {

    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.preARTCounselingConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6317</b>
   *
   * <p><b>Name:</b> PP1 concept - MESSAGE OF SEXUAL BEHAVIOR AND SUPPLY OF CONDOMS
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP1Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp1ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6318</b>
   *
   * <p><b>Name:</b> PP2 concept - MESSAGE DISCLOSING THEIR SEROSTATUS AND KNOWLEDGE / CALL FOR
   * TESTING THE PARTNER
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP2Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp2ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6319</b>
   *
   * <p><b>Name:</b> PP3 concept - MESSAGE OF ADHERENCE OF CARE AND TREATMENT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP3Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp3ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6320 </b>
   *
   * <p><b>Name:</b> PP4 concept - MESSAGE OF SEXUALLY TRANSMITTED INFECTION
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP4Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp4ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5271</b>
   *
   * <p><b>Name:</b> FAMILY PLANNING
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getfamilyPlanningConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.familyPlanningConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6321</b>
   *
   * <p><b>Name:</b> PP6 concept - MESSAGE OF CONSUMPTION OF ALCOHOL AND OTHER DRUGS
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP6Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp6ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6322</b>
   *
   * <p><b>Name:</b> PP7 concept - MESSAGE OF THE NEED FOR COMMUNITY SUPPORT SERVICES
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPP7Concept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pp7ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6337</b>
   *
   * <p><b>Name:</b> REVEALED
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getRevealdConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.revealedConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6340</b>
   *
   * <p><b>Name:</b> DISCLOSURE OF HIV DIAGNOSIS TO CHILDREN / ADOLESCENTS
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.disclosureOfHIVDiagnosisToChildrenAdolescentsConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23716</b>
   *
   * <p><b>Name:</b> MEMBERSHIP PLAN / PLANO DE ADESÃO
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getMemberShipPlanConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.memberShipPlanConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23887</b>
   *
   * <p><b>Name:</b> COUNSELED ON SIDE EFFECTS OF ART / ACONSELHADO SOBRE EFEITOS SECUNDÁRIOS DE
   * TARV
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getCounseledOnSideEffectsOfArtConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.counseledOnSideEffectsOfArtConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6223</b>
   *
   * <p><b>Name:</b> ADHERENCE EVALUATION / AVALIACAO DE ADESAO
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getAdherenceEvaluationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adherenceEvaluationConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1749</b>
   *
   * <p><b>Name:</b> ARV ADHERENCE RISK / TEM ALGUM RISCO DE POBRE ADERÊNCIA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getArvAdherenceRiskConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.arvAdherenceRiskConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1385</b>
   *
   * <p><b>Name:</b> BAD / MAU
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getBadConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.badConcept");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 22772</b>
   *
   * <p><b>Name:</b> TYPE OF HIV TEST
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getTypeTestHIVConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.typeTestHIVConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23884</b>
   *
   * <p><b>Name:</b> HIV Testing Site
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getHivTestingSiteConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hivTestingSiteConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6245</b>
   *
   * <p><b>Name:</b> VOLUNTARY COUNCELING AND TESTING - COMMUNITY
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getVoluntaryCouncelingTestingCommunityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.voluntaryCouncelingTestingCommunityConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1030</b>
   *
   * <p><b>Name:</b> HIV DNA POLYMERASE CHAIN REACTION, QUALITATIVE
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getHivPCRQualitativeConceptUuid() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hivPCRQualitativeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1040</b>
   *
   * <p><b>Name:</b> HIV RAPID TEST 1, QUALITATIVE
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getHivRapidTest1QualitativeConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hivRapidTest1QualitativeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23807</b>
   *
   * <p><b>Name:</b> PRESUMPTIVE DIAGNOSIS IN CHILDREN LESS THAN 18 MONTHS
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPresumptiveDiagnosisInChildrenConcep() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.presumptiveDiagnosisInChildrenConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23933</b>
   *
   * <p><b>Name:</b> DATA DE RETORNO NA PRIMEIRA TENTATIVA (VISITA)
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getReturnDateOnFirstAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnDateOnFirstAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23934</b>
   *
   * <p><b>Name:</b> DATA DE RETORNO NA SEGUNDA TENTATIVA (VISITA)
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getReturnDateOnSecondAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnDateOnSecondAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23935</b>
   *
   * <p><b>Name:</b> DATA DE RETORNO NA TERCEIRA TENTATIVA (VISITA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getReturnDateOnThirdAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnDateOnThirdAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23996</b>
   *
   * <p><b>Name:</b> PACIENTE ELEGIVEL
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientElegibilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientElegibleForConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23987</b>
   *
   * <p><b>Name:</b>
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientTreatmentFollowUp() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.treatmentFollowUpUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23993</b>
   *
   * <p><b>Name:</b> REINTEGRATION
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getReintegrationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reintegrationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23997</b>
   *
   * <p><b>Name:</b> Elegibility Date
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getElegibilityDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.elegibitilyDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23998</b>
   *
   * <p><b>Name:</b> PACIENTE CONTACTADO NA PRIMEIRA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientContactedOnFirstAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.contactedOnFirstAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23999</b>
   *
   * <p><b>Name:</b> PACIENTE CONTACTADO NA SEGUNDA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientContactedOnSecondAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.contactedOnSecondAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24000</b>
   *
   * <p><b>Name:</b> PACIENTE CONTACTADO NA TERCEIRA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientContactedOnThirdAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.contactedOnThirdAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24001</b>
   *
   * <p><b>Name:</b> DATA COMBINADA PARA RETORNO NA PRIMEIRA CHAMADA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDateAgreedForReturnOnFirstCallConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateAgreedForReturnOnFirstCallConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24002</b>
   *
   * <p><b>Name:</b> DATA COMBINADA PARA RETORNO NA SEGUNDA CHAMADA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDateAgreedForReturnOnSecondCallConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateAgreedForReturnOnSecondCallConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24003</b>
   *
   * <p><b>Name:</b> DATA COMBINADA PARA RETORNO NA TERCEIRA CHAMADA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDateAgreedForReturnOnThirdCallConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateAgreedForReturnOnThirdCallConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24004</b>
   *
   * <p><b>Name:</b> PATIENT CONTACTED
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientContactedConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientContactedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24005</b>
   *
   * <p><b>Name:</b> PATIENT RETURNED TO HEALTH UNIT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientReturnedConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientReturnedtoHealthUnitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24008</b>
   *
   * <p><b>Name:</b> PACIENTE ENCONTRADO NA PRIMEIRA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundOnFirstAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientFoundOnFirstAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24009</b>
   *
   * <p><b>Name:</b> PACIENTE ENCONTRADO NA SEGUNDA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundOnSecondAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientFoundOnSecondAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24010</b>
   *
   * <p><b>Name:</b> PACIENTE ENCONTRADO NA TERCEIRA TENTATIVA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientFoundOnThirdAttemptConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientFoundOnThirdAttemptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24011</b>
   *
   * <p><b>Name:</b> PATIENT RETURNED TO HEALTH UNIT AFTER VISIT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getPatientReturnedAfterVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.ReturnedtoHealthUnitAfterVisitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 24012</b>
   *
   * <p><b>Name:</b> DATE PATIENT RETURNED TO HEALTH UNIT AFTER VISIT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDatePatientReturnedAfterVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DateReturnedtoHUAfterVisitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 28</b>
   *
   * <p><b>Name:</b> CCU: RASTREIO
   *
   * <p><b>Description: Rastreio do cancro do colo uterino</b>
   *
   * @return {@link Concept}
   */
  public EncounterType getRastreioDoCancroDoColoUterinoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.rastreioDoCancroDoColoUterinoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>enconter_type_id = 2094</b>
   *
   * <p><b>Name:</b> VIA RESULT
   *
   * <p><b>Description: RESULTADO DO VIA</b>
   *
   * @return {@link Concept}
   */
  public Concept getResultadoViaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.resultadoViaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 2093</b>
   *
   * <p><b>Name:</b> SUSPECTED CANCER
   *
   * <p><b>Description: SUSPEITO DE CANCRO</b>
   *
   * @return {@link Concept}
   */
  public Concept getSuspectedCancerConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspectedCancerConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 2117</b>
   *
   * <p><b>Name:</b> CRYOTHERAPY PERFORMED ON THE SAME DAY AS THE VIA
   *
   * <p><b>Description: CRIOTERAPIA REALIZADA NO MESMO DIA QUE A VIA</b>
   *
   * @return {@link Concept}
   */
  public Concept getCryotherapyPerformedOnTheSameDayASViaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cryotherapyPerformedOnTheSameDayASViaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 23967</b>
   *
   * <p><b>Name:</b> Cryotherapy date
   *
   * <p><b>Description: Data da realizacao da crioterapia</b>
   *
   * @return {@link Concept}
   */
  public Concept getCryotherapyDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cryotherapyDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 2149</b>
   *
   * <p><b>Name:</b> VIA RESULT ON THE REFERENCE
   *
   * <p><b>Description:RESULTADO DE VIA NA REFERENCIA</b>
   *
   * @return {@link Concept}
   */
  public Concept getViaResultOnTheReferenceConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.viaResultOnTheReferenceConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 23874</b>
   *
   * <p><b>Name:</b> Pediatric Nursing concept
   *
   * <p><b>Description:Enfermaria de Pediatria</b>
   *
   * @return {@link Concept}
   */
  public Concept getPediatricNursingConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pediatricNursingConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 23972</b>
   *
   * <p><b>Name:</b> Thermocoagulation concept
   *
   * <p><b>Description:Termocoagulação</b>
   *
   * @return {@link Concept}
   */
  public Concept getThermocoagulationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.thermocoagulationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 23970</b>
   *
   * <p><b>Name:</b> Loop Electrosurgical Excision Procedure - LEEP concept
   *
   * <p><b>Description:Cirurgia de Alta Frequência - CAF </b>
   *
   * @return {@link Concept}
   */
  public Concept getLeepConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.leepConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>enconter_type_id = 23973</b>
   *
   * <p><b>Name:</b>Conization concept
   *
   * <p><b>Description:Conização do útero </b>
   *
   * @return {@link Concept}
   */
  public Concept getconizationConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.conizationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1087</b>
   *
   * <p><b>Name:</b>PREVIOUS ANTIRETROVIRAL DRUGS USED FOR TREATMENT concept
   *
   * <p><b>Description:Question on encounter forms: Is the patient currently taking, or has the
   * patient ever taken, any of the following retroviral medications? Reason for use? This
   * particular concept stores a history of previous use of the associated antiretroviral
   * medications for HIV treatment. </b>
   *
   * @return
   */
  public Concept getPreviousARVUsedForTreatmentConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.previousARVUsedForTreatmentConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5089</b>
   *
   * <p><b>Name:</b>WEIGHT (KG) concept
   *
   * <p><b>Patient's weight in kilograms.</b>
   *
   * @return
   */
  public Concept getWeightConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.weightConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1410</b>
   *
   * <p><b>Name:</b>RETURN VISIT DATE concept
   *
   * <p><b>Patient is to return on this date</b>
   *
   * @return
   */
  public Concept getReturnVisitDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateConceptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165256</b>
   *
   * <p><b>Name:</b>ART drug Formulation
   *
   * @return
   */
  public Concept getArtDrugFormulationConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artDrugFormulation");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165252</b>
   *
   * <p><b>Name:</b> Drug and Quantity
   *
   * @return
   */
  public Concept getDrugAndQuantityConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.drugAndQuantity");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23784</b>
   *
   * <p><b>Name:</b>TDF+3TC+DTG concept
   *
   * @return
   */
  public Concept getTDFand3TCandDTGCConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.TDFand3TCandDTGConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21167</b>
   *
   * <p><b>Name:</b>LOPINAVIR + RITONAVIR concept
   *
   * @return
   */
  public Concept getLopinavirAndRitonavirConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lopinavirAndRitonavirConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21159</b>
   *
   * <p><b>Name:</b>ABACAVIR (ABC)+LAMIVUDINA (3TC) concept
   *
   * @return
   */
  public Concept getAbacavirAndLamivudinaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.abacavirAndLamivudinaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 630</b>
   *
   * <p><b>Name:</b>ZIDOVUDINE AND LAMIVUDINE concept
   *
   * @return
   */
  public Concept getZidovudineAndLamivudineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.zidovudineAndLamivudineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21179</b>
   *
   * <p><b>Name:</b>TENOFOVIR/LAMIVUDINA concept
   *
   * @return
   */
  public Concept getTenofovirOrLamivudinaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tenofovirOrLamivudinaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21161</b>
   *
   * <p><b>Name:</b>RALTEGRAVIR concept
   *
   * @return
   */
  public Concept getRaltegravirConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.raltegravirConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165245 </b>
   *
   * <p><b>Name:</b>PATIENT KNOWLEDGE OF EFFECT OF HIV concept
   *
   * @return
   */
  public Concept getPatientKnowledgeOfEffectOfHIVConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientKnowledgeOfEffectOfHIVConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165244 </b>
   *
   * <p><b>Name:</b>PATERNAL DATA concept
   *
   * @return
   */
  public Concept getPaternalDataConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.paternalDataConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =631 </b>
   *
   * <p><b>Name:</b>NEVIRAPINE concept
   *
   * @return
   */
  public Concept getNevirapineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.nevirapineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =797 </b>
   *
   * <p><b>Name:</b>ZIDOVUDINE concept
   *
   * @return
   */
  public Concept getZidovudineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.zidovudineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =814 </b>
   *
   * <p><b>Name:</b>ABACAVIR concept
   *
   * @return
   */
  public Concept getAbacavirConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abacavirConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =21184 </b>
   *
   * <p><b>Name:</b>EFAVIRENZ (EFV) concept
   *
   * @return
   */
  public Concept getEfavirenzConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.efavirenzConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =21178 </b>
   *
   * <p><b>Name:</b>LAMIVUDINA(3TC) concept
   *
   * @return
   */
  public Concept getLamivudinaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lamivudinaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =21175 </b>
   *
   * <p><b>Name:</b>TENOFOVIR (TDF) concept
   *
   * @return
   */
  public Concept getTenofovirConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tenofovirConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =6324 </b>
   *
   * <p><b>Name:</b>TDF+3TC+EFV concept
   *
   * @return
   */
  public Concept getTdfAnd3tcAndEfvConceptUuid() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tdfAnd3tcAndEfvConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =1651 </b>
   *
   * <p><b>Name:</b>AZT+3TC+NVP concept
   *
   * @return
   */
  public Concept getAztAnd3tcAndNvpConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.aztAnd3tcAndNvpConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =6116 </b>
   *
   * <p><b>Name:</b>AZT+3TC+ABC concept
   *
   * @return
   */
  public Concept getAztAnd3tcAndAbcConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.aztAnd3tcAndAbcConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165215 </b>
   *
   * <p><b>Name:</b>TDF/FTC concept/
   *
   * @return
   */
  public Concept getTdfAndFtcConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tdfAndFtcConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =6177 </b>
   *
   * <p><b>Name:</b>WE CAN CONTACT THE CONFIDANT IF YOU ARE UNABLE
   *
   * @return
   */
  public Concept getConfidentAcceptContact() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.confidentAcceptContactUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =23776 </b>
   *
   * <p><b>Name:</b>CONFIDENT CONSENT DATE
   *
   * @return
   */
  public Concept getConfidentConsentDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.confidentConsentDateUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165296 </b>
   *
   * <p><b>Name:</b>Initial Status of the PrEP User
   *
   * @return {@link Concept}
   */
  public Concept getInitialStatusPrepUserConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.initialStatusPrepUserUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165211 </b>
   *
   * <p><b>Name:</b>PREP START DATE Concept
   *
   * @return {@link Concept}
   */
  public Concept getPrepStartDateConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.prepStartDateUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =1594 </b>
   *
   * <p><b>Name:</b>ENTRY POINT INTO HIV CARE, REFERAL TYPE / PROVENIENCIA, TIPO
   *
   * @return {@link Concept}
   */
  public Concept getReferalTypeConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.referalTypeUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =1369 </b>
   *
   * <p><b>Name:</b>TRANSFER FROM OTHER FACILITY
   *
   * @return {@link Concept}
   */
  public Concept getTransferredFromOtherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferredFromOtherFacilityUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =76 </b>
   *
   * <p><b>Name:</b>State of Stay on PrEP Program
   *
   * @return {@link Concept}
   */
  public Concept getStateOfStayOnPrepProgram() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayOnPrepProgramUuid");
    return getConcept(uuid);
  }

  /**
   * <b>program_id = 25 </b>
   *
   * <p><b>Name:</b> PREP
   *
   * <p><b>Description:</b> Profilaxia Pre Exposicao
   *
   * @return {@link Program}
   */
  public Program getPrepProgram() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.prepProgramUuid");
    return getProgram(uuid);
  }

  /**
   * <b>encounterType_id = 80</b>
   *
   * <p><b>Name:</b> PREP: PROFILAXIA PRE-EXPOSICAO - INICIAL
   *
   * <p><b>Description:</b> REGISTO PREP: PROFLAXIA PRÉ-EXPOSIÇÃO
   *
   * @return {@link EncounterType}
   */
  public EncounterType getPrepInicialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.prepInicialEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 81</b>
   *
   * <p><b>Name:</b> PREP: PROFILAXIA PRE-EXPOSICAO - SEGUIMENTO
   *
   * <p><b>Description:</b> REGISTO PREP: PROFLAXIA PRÉ-EXPOSIÇÃO
   *
   * @return {@link EncounterType}
   */
  public EncounterType getPrepSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.prepSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>concept_id =165205 </b>
   *
   * <p><b>Name:</b>TRANSGENDER
   *
   * @return {@link Concept}
   */
  public Concept getTransGenderConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.transGenderUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165194 </b>
   *
   * <p><b>Name:</b>DATE OF INITIAL HIV TEST
   *
   * @return {@link Concept}
   */
  public Concept getDateOfInitialHivTestConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfInitialHivTestUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165223 </b>
   *
   * <p><b>Name:</b>CURRENT STATE OF THE WOMAN
   *
   * @return {@link Concept}
   */
  public Concept getCurrentStateOfTheWomanUuidConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentStateOfTheWomanUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id =165293 </b>
   *
   * <p><b>Name:</b>Date of HIV Test with Negative result (PrEP)
   *
   * @return {@link Concept}
   */
  public Concept getDateOfHivTestWithNegativeResultsPrepUuidConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfHivTestWithNegativeResultsPrepUuid");
    return getConcept(uuid);
  }
}
