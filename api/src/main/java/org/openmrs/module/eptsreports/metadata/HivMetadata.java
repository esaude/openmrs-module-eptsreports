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

  private String gpSTarvFarmaciaEncounterTypeUuid = "eptsreports.sTarvFarmaciaEncounterTypeUuid";

  private String gpArtProgramUuid = "eptsreports.artProgramUuid";

  private String gpPtvEtvProgramUuid = "eptsreports.ptvEtvProgramUuid";

  private String getTBProgramUuid = "eptsreports.tbProgramUuid";

  /**
   * concept_id = 307
   *
   * @return
   */
  public Concept getResultForBasiloscopia() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.basiloscopiaUuid");
    return getConcept(uuid);
  }
  /**
   * Concepts Id = 856
   *
   * @return
   */
  public Concept getHivViralLoadConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivViralLoadConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Criterio para inicio de tarv Concept Id = 6334
   *
   * @return
   */
  public Concept getCriteriaForArtStart() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.criteriaForArtStartUuid");
    return getConcept(uuid);
  }

  /**
   * Fila Next pick up concept concept_id=5096
   *
   * @return
   */
  public Concept getReturnVisitDateForArvDrugConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateForArvDrugConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id=6123
   *
   * @return
   */
  public Concept getDateOfHivDiagnosisConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.dateOfHIVDiagnosis");
    return getConcept(uuid);
  }
  /**
   * concept_id=1190
   *
   * <p>Information gathered on encounter forms which describes the date of a particular drug's
   * institution. Most often "globbed" with another concept which describes the drug.
   *
   * @return
   */
  public Concept getARVStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id=1255
   *
   * @return
   */
  public Concept getARVPlanConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.arvPlanConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id=1088
   *
   * @return
   */
  public Concept getRegimeConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.regimeUuid");
    return getConcept(uuid);
  }
  /**
   * Concept id = 1705 Reinicio de terapia ou tratamento apos abandono ou recaida
   *
   * @return
   */
  public Concept getRestartConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.restartConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 1707 DROPPED FROM TREATMENT Sinónimos DROPPED FROM ANTI-RETROVIRAL THERAPY DROPPED
   * FROM TUBERCULOSIS TREATMENT ABANDONED
   *
   * @return
   */
  public Concept getAbandonedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abandonedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * 6328 Second line ARV concepts
   *
   * @return
   */
  public Concept getAzt3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept 6327
   *
   * @return
   */
  public Concept getD4t3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  /**
   * 6326
   *
   * @return
   */
  public Concept getAzt3tcAbcLpvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6325
  public Concept getD4t3tcAbcLpvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6109
  public Concept getAztDdiLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_DDI_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1315
  public Concept getTdf3tcEfvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1314
  public Concept getAzt3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1313
  public Concept getAbc3tcEfvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1312
  public Concept getAbc3tcNvpConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_NVP_ConceptUuid");
    return getConcept(uuid);
  }

  // 1311
  public Concept getAbc3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  //
  public Concept getTdf3tcLpvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // concept id 6306
  public Concept getAcceptContactConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.acceptContactConceptUuid");
    return getConcept(uuid);
  }

  /** concept id 1066 Generic Answer for a question */
  @Override
  public Concept getNoConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  public Concept getDataInicioProfilaxiaIsoniazidaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataInicioProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  public Concept getDataFinalizacaoProfilaxiaIsoniazidaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataFimProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6122
  public Concept getIsoniazidUsageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.isoniazidUseConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id=1695 */
  public Concept getCD4AbsoluteOBSConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsoluteOBSUuid");
    return getConcept(uuid);
  }

  /** concept_id=6314 */
  public Concept getCoucelingActivityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coucelingActivityTypeUuid");
    return getConcept(uuid);
  }

  /** concept_id=21150 */
  public Concept getFirstLineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.firstLineConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id=5356 */
  public Concept getcurrentWhoHivStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.currentWhoHivStageUuid");
    return getConcept(uuid);
  }

  /** concept_id=1205 */
  public Concept getWho2AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who2AdultStageUuid");
    return getConcept(uuid);
  }

  /** concept_id=1206 */
  public Concept getWho3AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who3AdultStageUuid");
    return getConcept(uuid);
  }

  /** concept_id=1207 */
  public Concept getWho4AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who4AdultStageUuid");
    return getConcept(uuid);
  }

  /** concept_id = 1366 patient is dead */
  public Concept getPatientHasDiedConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientHasDiedConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 1981 */
  public Concept getTypeOfVisitConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.typeOfVisit");
    return getConcept(uuid);
  }

  /** concept_id = 2003 */
  public Concept getPatientFoundConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.patientFound");
    return getConcept(uuid);
  }

  /** concept_id = 1065 */
  public Concept getPatientFoundYesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 2160 */
  public Concept getBuscaConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.busca");
    return getConcept(uuid);
  }

  /** concept_id = 6254 */
  public Concept getSecondAttemptConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.secondAttempt");
    return getConcept(uuid);
  }

  /** concept_id = 6255 */
  public Concept getThirdAttemptConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.thirdAttempt");
    return getConcept(uuid);
  }

  /** Concept_id = 2016 */
  public Concept getDefaultingMotiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.defaultingMotive");
    return getConcept(uuid);
  }

  // concept_id = 6201
  public Concept getReasonForStoppedTakingArvDrugsDuringLast7DaysConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForStoppedTakingArvDrugsDuringLast7Days");
    return getConcept(uuid);
  }

  /** Concept_id = 6202 */
  public Concept getReasonForStoppedTakingArvDrugsDuringLastMonthConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForStoppedTakingArvDrugsDuringLastMonth");
    return getConcept(uuid);
  }

  /** Concept_id = 6217 */
  public Concept getMainReasonForDelayInTakingArvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.mainReasonForDelayInTakingArv");
    return getConcept(uuid);
  }

  // concept_id = 2024
  /** Concept_id = 2024 */
  public Concept getPatientRecordHasWrongAddressConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientRecordHasWrongAddress");
    return getConcept(uuid);
  }

  // concept_id = 2026
  /** Concept_id = 2026 */
  public Concept getPatientMovedHousesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientMovedHouses");
    return getConcept(uuid);
  }

  // concept_id = 2011
  /** Concept_id = 2011 */
  public Concept getPatientTookATripConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientTookATrip");
    return getConcept(uuid);
  }

  // concept_id = 2032
  /** Concept_id = 2032 */
  public Concept getOtherReasonsWhyPatientWasNotLocatedByActivistConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.otherReasonsWhyPatientWasNotLocatedByActivist");
    return getConcept(uuid);
  }

  // concept_id = 2032
  /** Concept_id = 2032 */
  public Concept getReportOfVisitSupportConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reportOfVisitSupport");
    return getConcept(uuid);
  }

  // concept_id = 2157
  /** Concept_id = 2157 */
  public Concept getPatientHadDifficultyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientHadDifficulty");
    return getConcept(uuid);
  }

  // concept_id = 1272
  /** Concept_id = 1272 */
  public Concept getPatientFoundForwardedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientFoundForwarded");
    return getConcept(uuid);
  }

  // concept_id = 2037
  public Concept getWhoGaveInformationConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.whoGaveInformation");
    return getConcept(uuid);
  }

  // concept_id = 2180
  public Concept getCardDeliveryDateConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cardDeliveryDate");
    return getConcept(uuid);
  }

  // concept_id = 1709
  public Concept getSuspendedTreatmentConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspendedTreatmentConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 6269
  public Concept getActiveOnProgramConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeOnProgramConcept");
    return getConcept(uuid);
  }

  // concept_id = 1126
  public Concept getUrogenitalExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.urogenitalExamFindingsConcept");
    return getConcept(uuid);
  }

  // concept_id = 1115
  public Concept getNormalConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.normalConcept");
    return getConcept(uuid);
  }

  // concept_id = 1116
  public Concept getAbnormalConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abnormalConcept");
    return getConcept(uuid);
  }

  // concept_id = 1399
  public Concept getSecretionsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.secretionsConcept");
    return getConcept(uuid);
  }

  // concept_id = 1400
  public Concept getCondylomasConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.condylomasConcept");
    return getConcept(uuid);
  }

  // concept_id = 1602
  public Concept getUlcersConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.ulcersConcept");
    return getConcept(uuid);
  }

  // concept_id=5488
  public Concept getAdherenceCoucelingConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adherenceCounselingUuid");
    return getConcept(uuid);
  }

  /** concept_id=5497 */
  public Concept getCD4AbsoluteConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsolutoUuid");
    return getConcept(uuid);
  }

  /** concept_id=730 */
  public Concept getCD4PercentConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4PercentUuid");
    return getConcept(uuid);
  }

  /** concept_id=1714 */
  public Concept getAdherence() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.poorAdherenceUuid");
    return getConcept(uuid);
  }


  /** concept_id=2015 */

  public Concept getAdverseReaction() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adverseReactionUuid");
    return getConcept(uuid);
  }

  /** Concept 6292 */
  public Concept getNeutropenia() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.neutropenia");
    return getConcept(uuid);
  }

  /** Concept 6293 */
  public Concept getPancreatitis() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.pancreatitis");
    return getConcept(uuid);
  }

  /** Concept 6294 */
  public Concept getHepatotoxicity() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hepatotoxicity");
    return getConcept(uuid);
  }

  /** Concept 6295 */
  public Concept getPsychologicalChanges() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.psychologicalChanges");
    return getConcept(uuid);
  }

  /** Concept 6296 */
  public Concept getMyopathy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.myopathy");
    return getConcept(uuid);
  }

  /** Concept 6297 */
  public Concept getSkinAllergy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.skinAllergy");
    return getConcept(uuid);
  }

  /** Concept 6298 */
  public Concept getLipodystrophy() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.lipodystrophy");
    return getConcept(uuid);
  }

  /** Concept 6299 */
  public Concept getLacticAcidosis() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lacticAcidosis");
    return getConcept(uuid);
  }

  /** Concept 821 */
  public Concept getPeripheralNeuropathy() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.peripheralNeuropathy");
    return getConcept(uuid);
  }

  /** Concept 16 */
  public Concept getDiarrhea() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.diarrhea");
    return getConcept(uuid);
  }

  /** Concept 1406 */
  public Concept getOtherDiagnosis() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.otherDiagnosis");
    return getConcept(uuid);
  }

  /** Concept 23724 GAAC */
  public Concept getGaac() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.gaac");
    return getConcept(uuid);
  }

  /** Concept 23725 AF */
  public Concept getFamilyApproach() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.familyApproach");
    return getConcept(uuid);
  }

  /** Concept 23726 CA */
  public Concept getAccessionClubs() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.accessionClubs");
    return getConcept(uuid);
  }

  /** Concept 23727 PU */
  public Concept getSingleStop() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.singleStop");
    return getConcept(uuid);
  }

  /** Concept 23729 FR */
  public Concept getRapidFlow() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.rapidFlow");
    return getConcept(uuid);
  }

  /** Concept 23730 DT */
  public Concept getQuarterlyDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.quarterlyDispensation");
    return getConcept(uuid);
  }

  /** Concept 23731 DC */
  public Concept getCommunityDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.communityDispensation");
    return getConcept(uuid);
  }

  /**
   * Concept 23732 Other Model
   *
   * @return
   */
  public Concept getAnotherModel() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.anotherModel");
    return getConcept(uuid);
  }

  /**
   * Concept 1256 Start Drugs
   *
   * @return
   */
  public Concept getStartDrugs() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugsConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 1257 Continue Regimen */
  public Concept getContinueRegimenConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.continueRegimen");
    return getConcept(uuid);
  }

  /** Concept 23808 Pre-ART Start Date */
  public Concept getPreArtStartDate() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtStartDate");
    return getConcept(uuid);
  }

  //
  /** Concept 23866 Date of ART Pickup Return date for ART pick up Master Card */
  public Concept getArtDatePickupMasterCard() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtPickupDate");
    return getConcept(uuid);
  }

  /** Concept 6300 Date of ART Pickup */
  public Concept getTypeOfPatientTransferredFrom() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeOfPatientTransferredFrom");
    return getConcept(uuid);
  }

  /** concept_id = 6272 */
  public Concept getStateOfStayOfPreArtPatient() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayOfPreArtPatient");
    return getConcept(uuid);
  }

  /** Concept 6273 patient state */
  public Concept getStateOfStayOfArtPatient() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.stateOfStayArtPatient");
    return getConcept(uuid);
  }

  /** Concept 2031 REASON PATIENT NOT FOUND BY ACTIVIST */
  public Concept getReasonPatientNotFound() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reasonPatientNotFound");
    return getConcept(uuid);
  }

  /** Concept 1383 PATIENT IS DEAD - Incorrect */
  public Concept getPatientIsDead() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientIsdeadIncorrect");
    return getConcept(uuid);
  }

  /** Concept 2027 PATIENT IS DEAD */
  public Concept getPatientIsDeadCorrect() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.patientIsdead");
    return getConcept(uuid);
  }

  /** Concept 6276 Date of ART Pickup */
  public Concept getArtStatus() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.art");
    return getConcept(uuid);
  }

  /** Concept 1706 TRANSFERRED OUT TO ANOTHER FACILITY */
  public Concept getTransferredOutConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferOutToAnotherFacilityConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept 23863 AUTO TRANSFER
   *
   * @return
   */
  public Concept getAutoTransferConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.autoTransferConceptUuid");
    return getConcept(uuid);
  }
  /**
   * Concept 6272 STATE OF STAY PRIOR ART PATIENT
   *
   * @return
   */
  public Concept getStateOfStayPriorArtPatientConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayPriorArtPatientConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept 23722 APPLICATION FOR LABORATORY RESEARCH
   *
   * @return
   */
  public Concept getApplicationForLaboratoryResearch() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.applicationForLaboratoryResearch");
    return getConcept(uuid);
  }
  /** Concept 1305 HIV VIRAL LOAD, QUALITATIVE */
  public Concept getHivViralLoadQualitative() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.viralLoadQualitativeConceptUuid");
    return getConcept(uuid);
  }

  public Concept getKeyPopulationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.keyPopulationConceptUuid");
    return getConcept(uuid);
  }

  public Concept getHomosexualConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.homosexualConceptUuid");
    return getConcept(uuid);
  }

  public Concept getDrugUseConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.drugUseConceptUuid");
    return getConcept(uuid);
  }

  public Concept getImprisonmentConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.imprisonmentConceptUuid");
    return getConcept(uuid);
  }

  public Concept getSexWorkerConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.sexWorkerConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23865
   *
   * @return
   */
  public Concept getArtPickupConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artPickupConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Last Menstruation Date Concept_id = 1465
   *
   * @return
   */
  public Concept getDateOfLastMenstruationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfLastMenstruationConcept");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23739
   *
   * @return
   */
  public Concept getTypeOfDispensationConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeOfDispensationConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23720
   *
   * @return
   */
  public Concept getQuarterlyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.quarterlyConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 1267
   *
   * @return
   */
  public Concept getCompletedConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.completedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept 23748
   *
   * @return
   */
  public Concept getCytopeniaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cytopeniaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept 23749
   *
   * @return
   */
  public Concept getNephrotoxicityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nephrotoxicityConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 29 */
  public Concept getHepatitisConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hepatitisConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 23750 STEVENS-JOHNSON SYNDROME */
  public Concept getStevensJonhsonSyndromeConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stevensJonhsonSyndromeConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 23751 HYPERSENSITIVITY TO ABC/RAL */
  public Concept getHypersensitivityToAbcOrRailConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hypersensitivityToAbcOrRailConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 23752 HEPATIC STEATOSIS WITH HYPERLACTATAEMIA */
  public Concept getHepaticSteatosisWithHyperlactataemiaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hepaticSteatosisWithHyperlactataemiaConceptUuid");
    return getConcept(uuid);
  }

  /** Concept Id 1113 */
  public Concept getTBDrugStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentStartDateConceptUuid");
    return getConcept(uuid);
  }

  /** Concept 6120 */
  public Concept getTBDrugEndDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbDgrusTreatmentEndDateUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23761
   *
   * @return
   */
  public Concept getActiveTBConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 1268
   *
   * @return
   */
  public Concept getTBTreatmentPlanConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentPlanConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 1306
   *
   * @return
   */
  public Concept getBeyondDetectableLimitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.beyondDtectableLimitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23814
   *
   * @return
   */
  public Concept getUndetectableViralLoadConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.undetectableViralLoadConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23904
   *
   * @return
   */
  public Concept getLessThan839CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan839CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23905
   *
   * @return
   */
  public Concept getLessThan10CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan10CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23906
   *
   * @return
   */
  public Concept getLessThan20CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan20CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23907
   *
   * @return
   */
  public Concept getLessThan40CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan40CopiesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept ID 23908
   *
   * @return
   */
  public Concept getLessThan400CopiesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.lessThan400CopiesConceptUuid");
    return getConcept(uuid);
  }
  /**
   * Concept ID 1067
   *
   * @return
   */
  public Concept getUnkownConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.unknownConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23720
   *
   * @return
   */
  public Concept getMonthlyConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.monthlyConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id 23888
   *
   * @return
   */
  public Concept getSemiannualDispensation() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.semiannualConceptUuid");

    return getConcept(uuid);
  }
  /** concept_id = 2005 */
  public Concept getPatientForgotVisitDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientForgotVisitDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 2006
   *
   * @return
   */
  public Concept getPatientIsBedriddenAtHomeConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientIsBedriddenAtHomeConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 2007 */
  public Concept getDistanceOrMoneyForTransportIsTooMuchForPatientConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.distanceOrMoneyForTransportIsTooMuchForPatientConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 2010 */
  public Concept getPatientIsDissatisfiedWithDayHospitalServicesConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.patientIsDissatisfiedWithDayHospitalServicesConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 2013 */
  public Concept getPatientIsTreatingHivWithTraditionalMedicineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(
                "eptsreports.patientIsTreatingHivWithTraditionalMedicineConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 2017 */
  public Concept getOtherReasonWhyPatientMissedVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.otherReasonWhyPatientMissedVisitConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 23915 */
  public Concept getFearOfTheProviderConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.fearOfTheProviderConceptUuid");
    return getConcept(uuid);
  }

  /** concept_id = 23946 */
  public Concept getAbsenceOfHealthProviderInHealthUnitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.absenceOfHealthProviderInHealthUnitConceptUuid");
    return getConcept(uuid);
  }

  /**
   * B+ concept = 6331
   *
   * @return
   */
  public Concept getBpostiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.bPlusConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 23944
  /** Concept_id = 23944 */
  public Concept getReasonPatientNotFoundByActivist2ndVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivist2ndVisitConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 23945
  /** Concept_id = 23945 */
  public Concept getReasonPatientNotFoundByActivist3rdVisitConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivist3rdVisitConceptUuid");
    return getConcept(uuid);
  }
  /** encounterType_id = 6 */
  public EncounterType getAdultoSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /** encounterType_id = 9 */
  public EncounterType getPediatriaSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }
  /** encounterType_id= 21 */
  public EncounterType getBuscaActivaEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.buscaActivaEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /** encounterType_id = 5 */
  public EncounterType getARVAdultInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoInitialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /** encounterType_id = 7 */
  public EncounterType getARVPediatriaInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaInicialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /** encounterType_id = 18 */
  public EncounterType getARVPharmaciaEncounterType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty(gpSTarvFarmaciaEncounterTypeUuid);
    return getEncounterType(uuid);
  }

  /** encounter_type_id = 29 */
  public EncounterType getEvaluationAndPrepForARTEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.evaluationAndPrepForARTEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * encounter_type 13
   *
   * @return
   */
  public EncounterType getMisauLaboratorioEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.misauLaboratorioEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter type 34
  public EncounterType getPrevencaoPositivaInicialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.apssPrevencaoPositivaInicialInicialEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter type 35
  public EncounterType getPrevencaoPositivaSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.apssPrevencaoPositivaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * encounter type 36 Coming from ICAP
   *
   * @return encounter type
   */
  public EncounterType getVisitaApoioReintegracaoParteAEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.visitaApoioReintegracaoParteA");
    return getEncounterType(uuid);
  }

  /**
   * encounter type 37 Coming from ICAP
   *
   * @return encounter type
   */
  public EncounterType getVisitaApoioReintegracaoParteBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.visitaApoioReintegracaoParteB");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 1
  public EncounterType getARVAdultInitialBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adultInitialBEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 3
  public EncounterType getARVPediatriaInitialBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pediatriaInitialBEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 19
  public EncounterType getArtAconselhamentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 24
  public EncounterType getArtAconselhamentoSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoSeguimentoEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type = 52
  public EncounterType getMasterCardDrugPickupEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.masterCardDrugPickupEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /** encounter_type = 53 */
  public EncounterType getMasterCardEncounterType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.masterCardEncounterType");
    return getEncounterType(uuid);
  }

  /** encounter_type = 51 */
  public EncounterType getFsrEncounterType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.fsrEncounterType");
    return getEncounterType(uuid);
  }

  // Programs

  /**
   * program_id=2
   *
   * @return
   */
  public Program getARTProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgram(uuid);
  }

  /**
   * ETV ProgramId = 8
   *
   * @return
   */
  public Program getPtvEtvProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgram(uuid);
  }

  /**
   * program_id=1
   *
   * @return
   */
  public Program getHIVCareProgram() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgram(uuid);
  }

  // Program Id 5 TB
  public Program getTBProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.tbProgramUuid");
    return getProgram(uuid);
  }

  // Identifier types
  public PatientIdentifierType getNidServiceTarvIdentifierType() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.nidServicoTarvUuid");
    return getPatientIdentifierType(uuid);
  }

  // Program Workflow States
  public ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFERRED OUT TO ANOTHER FACILITY");
  }

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

  public ProgramWorkflowState getArtCareTransferredFromOtherHealthFacilityWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    ProgramWorkflowState state =
        getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtTransferredFromOtherHealthFacilityWorkflowState() {
    Program hivCareProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    ProgramWorkflowState state =
        getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareTransferredOutToAnotherHealthFacilityWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    ProgramWorkflowState state =
        getTransferredOutToAnotherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareActiveOnProgramWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    ProgramWorkflowState state = getActiveOnProgramWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtActiveOnProgramWorkflowState() {
    Program hivCareProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    ProgramWorkflowState state = getActiveOnProgramWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareAbandonedWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    ProgramWorkflowState state = getAbandonedWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getSuspendedTreatmentWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "SUSPEND TREATMENT");
  }

  public ProgramWorkflowState getAbandonedWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "ABANDONED");
  }

  public ProgramWorkflowState getPatientHasDiedWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "PATIENT HAS DIED");
  }

  public ProgramWorkflowState getPatientGaveBirthWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "GAVE BIRTH");
  }

  public ProgramWorkflowState getArtCareDeadWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    return getDeadWorkflowState(hivCareProgram, workflow);
  }

  public ProgramWorkflowState getPateintActiveArtWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "c50d6bdc-8a79-43ae-ab45-abbaa6b45e7d");
  }

  public ProgramWorkflowState getPateintTransferedFromOtherFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFER FROM OTHER FACILITY");
  }

  public ProgramWorkflowState getPateintPregnantWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "PREGNANT");
  }

  public ProgramWorkflowState getPateintActiveOnHIVCareProgramtWorkflowState() {
    String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "ACTIVE ON PROGRAM");
  }

  public ProgramWorkflowState getPateintTransferedFromOtherFacilityHIVCareWorkflowState() {
    String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "TRANSFER FROM OTHER FACILITY");
  }

  public ProgramWorkflowState getPatientActiveOnTBProgramWorkflowState() {
    String tbProgramUuid = Context.getAdministrationService().getGlobalProperty(getTBProgramUuid);
    return getProgramWorkflowState(tbProgramUuid, "4", "ACTIVE ON PROGRAM");
  }

  // Concept 5356
  public Concept getCurrentWHOHIVStageConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStageConceptUuid"));
  }

  /**
   * FETCH PREGNANCY CONCEPT ConceptId = 44
   *
   * @return
   */
  public Concept getPregnancyResponseConcept() {
    return getConcept(
        Context.getAdministrationService().getGlobalProperty("eptsreports.pregnancyConceptUuid"));
  }

  // Concept 23818
  public Concept getReasonForRequestingViralLoadConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonForRequestingViralLoadConceptUuid"));
  }

  // Concept 23817
  public Concept getRoutineForRequestingViralLoadConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.routineForRequestingViralLoadConceptUuid"));
  }

  // Concept 843
  public Concept getRegimenFailureConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.regimenFailureConceptUuid"));
  }

  // Concept 23881
  public Concept getSuspectedImmuneFailureConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspectedImmuneFailureConceptUuid"));
  }

  // Concept 23864
  public Concept getRepeatAfterBreastfeedingConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.repeatAfterBreastfeedingConceptUuid"));
  }

  // Concept 23882
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

  public ProgramWorkflowState getArtCareInitiatedWorkflowState() {
    Program hivCareProgram = getHIVCareProgram();
    ProgramWorkflow workflow = getPreArtWorkflow();
    Concept startDrugs = getStartDrugsConcept();
    return getProgramWorkflowState(
        hivCareProgram.getUuid(), workflow.getUuid(), startDrugs.getUuid());
  }

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

  public ProgramWorkflowState getArtTransferredOutToAnotherHealthFacilityWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    ProgramWorkflowState state =
        getTransferredOutToAnotherHealthFacilityWorkflowState(artProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtDeadWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    return getDeadWorkflowState(artProgram, workflow);
  }

  public ProgramWorkflowState getArtAbandonedWorkflowState() {
    Program artProgram = getARTProgram();
    ProgramWorkflow workflow = getArtWorkflow();
    ProgramWorkflowState state = getAbandonedWorkflowState(artProgram, workflow);
    return state;
  }

  public PersonAttributeType getIdentificadorDefinidoLocalmente01() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.identificadorDefinidoLocalment01Uuid");
    return getPersonAttributeType(uuid);
  }

  // Concept 6331
  public Concept getBPlusConcept() {
    return getConcept(
        Context.getAdministrationService().getGlobalProperty("eptsreports.bPlusConceptUuid"));
  }

  // concept_id = 6275
  public Concept getPreTarvConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preTarvConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 23891
   *
   * @return
   */
  public Concept getDateOfMasterCardFileOpeningConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateOfMasterCardFileOpeningConceptUuid");
    return getConcept(uuid);
  }

  /** Concept_id = 21148 SECOND LINE - THERAPEUTIC LINE */
  public Concept getSecondLineConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artSecondLineSwitchUuid");
    return getConcept(uuid);
  }

  /** Concept_id = 21151 THERAPEUTIC LINE */
  public Concept getTherapeuticLineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.therapeuticLineConceptUuid");
    return getConcept(uuid);
  }
}
