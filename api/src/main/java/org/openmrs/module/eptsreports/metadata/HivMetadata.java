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

  private final String gpSTarvFarmaciaEncounterTypeUuid =
      "eptsreports.sTarvFarmaciaEncounterTypeUuid";

  private final String gpArtProgramUuid = "eptsreports.artProgramUuid";

  private final String gpPtvEtvProgramUuid = "eptsreports.ptvEtvProgramUuid";

  // Concepts Id = 856
  public Concept getHivViralLoadConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivViralLoadConceptUuid");
    return getConcept(uuid);
  }

  public Concept getCriteriaForArtStart() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.criteriaForArtStartUuid");
    return getConcept(uuid);
  }

  // concept_id=5096
  public Concept getReturnVisitDateForArvDrugConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateForArvDrugConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6123
  public Concept getDateOfHivDiagnosisConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.dateOfHIVDiagnosis");
    return getConcept(uuid);
  }

  // concept_id=1190
  public Concept getARVStartDate() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1255
  public Concept getARVPlanConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.arvPlanConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1088
  public Concept getRegimeConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.regimeUuid");
    return getConcept(uuid);
  }
  // person_attribute_type_id=24
  public PersonAttributeType getPersonAttributeType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.personAttributeType24uuid");
    return getPersonAttributeType(uuid);
  }

  public Concept getRestartConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.restartConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 1707
  public Concept getAbandoned() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abandonedConceptUuid");
    return getConcept(uuid);
  }

  // Second line ARV concepts
  // 6328
  public Concept getAzt3tcAbcEfvConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6327
  public Concept getD4t3tcAbcEfvConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6326
  public Concept getAzt3tcAbcLpvConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6325
  public Concept getD4t3tcAbcLpvConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 6109
  public Concept getAztDdiLpvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_DDI_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1315
  public Concept getTdf3tcEfvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1314
  public Concept getAzt3tcLpvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.AZT_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1313
  public Concept getAbc3tcEfvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_EFV_ConceptUuid");
    return getConcept(uuid);
  }

  // 1312
  public Concept getAbc3tcNvpConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_NVP_ConceptUuid");
    return getConcept(uuid);
  }

  // 1311
  public Concept getAbc3tcLpvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ABC_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  //
  public Concept getTdf3tcLpvConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TDF_3TC_LPV_ConceptUuid");
    return getConcept(uuid);
  }

  // concept id 6306
  public Concept getAcceptContactConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.acceptContactConceptUuid");
    return getConcept(uuid);
  }

  // concept id 1066
  @Override
  public Concept getNoConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  public Concept getDataInicioProfilaxiaIsoniazidaConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataInicioProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  public Concept getDataFinalizacaoProfilaxiaIsoniazidaConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.DataFimProfilaxiaIsoniazidaConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6122
  public Concept getIsoniazidUsageConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.isoniazidUseConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1695
  public Concept getCD4AbsoluteOBSConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsoluteOBSUuid");
    return getConcept(uuid);
  }

  // concept_id=6314
  public Concept getCoucelingActivityConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coucelingActivityTypeUuid");
    return getConcept(uuid);
  }

  // concept_id=5356
  public Concept getcurrentWhoHivStageConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.currentWhoHivStageUuid");
    return getConcept(uuid);
  }

  // concept_id=1205
  public Concept getWho2AdultStageConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who2AdultStageUuid");
    return getConcept(uuid);
  }

  // concept_id=1206
  public Concept getWho3AdultStageConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who3AdultStageUuid");
    return getConcept(uuid);
  }

  // concept_id=1207
  public Concept getWho4AdultStageConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who4AdultStageUuid");
    return getConcept(uuid);
  }

  // concept_id = 1366
  public Concept getPatientHasDiedConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientHasDiedConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 1981
  public Concept getTypeOfVisitConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.typeOfVisit");
    return getConcept(uuid);
  }

  // concept_id = 2003
  public Concept getPatientFoundConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientFound");
    return getConcept(uuid);
  }

  // concept_id = 1065
  public Concept getPatientFoundYesConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 2160
  public Concept getBuscaConcept() {
    final String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.busca");
    return getConcept(uuid);
  }

  // concept_id = 6254
  public Concept getSecondAttemptConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.secondAttempt");
    return getConcept(uuid);
  }

  // concept_id = 6255
  public Concept getThirdAttemptConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.thirdAttempt");
    return getConcept(uuid);
  }

  // concept_id = 2016
  public Concept getDefaultingMotiveConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.defaultingMotive");
    return getConcept(uuid);
  }

  // concept_id = 2158
  public Concept getReportOfVisitSupportConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reportOfVisitSupport");
    return getConcept(uuid);
  }

  // concept_id = 2157
  public Concept getPatientHadDifficultyConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientHadDifficulty");
    return getConcept(uuid);
  }

  // concept_id = 1272
  public Concept getPatientFoundForwardedConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientFoundForwarded");
    return getConcept(uuid);
  }

  // concept_id = 2037
  public Concept getWhoGaveInformationConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.whoGaveInformation");
    return getConcept(uuid);
  }

  // concept_id = 2180
  public Concept getCardDeliveryDateConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cardDeliveryDate");
    return getConcept(uuid);
  }

  // concept_id = 1709
  public Concept getSuspendedTreatmentConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.suspendedTreatmentConceptUuid");
    return getConcept(uuid);
  }

  // concept_id = 6269
  private Concept getActiveOnProgramConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeOnProgramConcept");
    return getConcept(uuid);
  }

  // concept_id = 1126
  public Concept getUrogenitalExamFindingsConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.urogenitalExamFindingsConcept");
    return getConcept(uuid);
  }

  // concept_id = 1115
  public Concept getNormalConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.normalConcept");
    return getConcept(uuid);
  }

  // concept_id = 1116
  public Concept getAbnormalConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.abnormalConcept");
    return getConcept(uuid);
  }

  // concept_id = 1399
  public Concept getSecretionsConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.secretionsConcept");
    return getConcept(uuid);
  }

  // concept_id = 1400
  public Concept getCondylomasConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.condylomasConcept");
    return getConcept(uuid);
  }

  // concept_id = 1602
  public Concept getUlcersConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.ulcersConcept");
    return getConcept(uuid);
  }

  // concept_id=5488
  public Concept getAdherenceCoucelingConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adherenceCounselingUuid");
    return getConcept(uuid);
  }

  // concept_id=5497
  public Concept getCD4AbsoluteConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsolutoUuid");
    return getConcept(uuid);
  }

  // concept_id=730
  public Concept getCD4PercentConcept() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4PercentUuid");
    return getConcept(uuid);
  }

  // concept_id=1714
  public Concept getAdherence() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.poorAdherenceUuid");
    return getConcept(uuid);
  }

  // concept_id=2015
  public Concept getAdverseReaction() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adverseReactionUuid");
    return getConcept(uuid);
  }

  // Concept 6292
  public Concept getNeutropenia() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.neutropenia");
    return getConcept(uuid);
  }

  // Concept 6293
  public Concept getPancreatitis() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pancreatitis");
    return getConcept(uuid);
  }

  // Concept 6294
  public Concept getHepatotoxicity() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hepatotoxicity");
    return getConcept(uuid);
  }

  // Concept 6295
  public Concept getPsychologicalChanges() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.psychologicalChanges");
    return getConcept(uuid);
  }

  // Concept 6296
  public Concept getMyopathy() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.myopathy");
    return getConcept(uuid);
  }

  // Concept 6297
  public Concept getSkinAllergy() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.skinAllergy");
    return getConcept(uuid);
  }

  // Concept 6298
  public Concept getLipodystrophy() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lipodystrophy");
    return getConcept(uuid);
  }

  // Concept 6299
  public Concept getLacticAcidosis() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lacticAcidosis");
    return getConcept(uuid);
  }

  // Concept 821
  public Concept getPeripheralNeuropathy() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.peripheralNeuropathy");
    return getConcept(uuid);
  }

  // Concept 16
  public Concept getDiarrhea() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.diarrhea");
    return getConcept(uuid);
  }

  // Concept 1406
  public Concept getOtherDiagnosis() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.otherDiagnosis");
    return getConcept(uuid);
  }

  // Concept 23724 GAAC
  public Concept getGaac() {
    final String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.gaac");
    return getConcept(uuid);
  }

  // Concept 23725 AF
  public Concept getFamilyApproach() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.familyApproach");
    return getConcept(uuid);
  }

  // Concept 23726 CA
  public Concept getAccessionClubs() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.accessionClubs");
    return getConcept(uuid);
  }

  // Concept 23727 PU
  public Concept getSingleStop() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.singleStop");
    return getConcept(uuid);
  }

  // Concept 23729 FR
  public Concept getRapidFlow() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.rapidFlow");
    return getConcept(uuid);
  }

  // Concept 23730 DT
  public Concept getQuarterlyDispensation() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.quarterlyDispensation");
    return getConcept(uuid);
  }

  // Concept 23731 DC
  public Concept getCommunityDispensation() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.communityDispensation");
    return getConcept(uuid);
  }

  // Concept 23732 Other Model
  public Concept getAnotherModel() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.anotherModel");
    return getConcept(uuid);
  }

  // Concept 1256 Start Drugs
  public Concept getStartDrugs() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugs");
    return getConcept(uuid);
  }

  // Concept 1257 Continue Regimen
  public Concept getContinueRegimen() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.continueRegimen");
    return getConcept(uuid);
  }

  // Concept 23808 Pre-ART Start Date
  public Concept getPreArtStartDate() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtStartDate");
    return getConcept(uuid);
  }

  // Concept 23866 Date of ART Pickup
  public Concept getArtDatePickup() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.preArtPickupDate");
    return getConcept(uuid);
  }

  // Concept 6300 Date of ART Pickup
  public Concept getTypeOfPatientTransferredFrom() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeOfPatientTransferredFrom");
    return getConcept(uuid);
  }

  // Concept 6273 patient state
  public Concept getStateOfStayOfArtPatient() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.stateOfStayArtPatient");
    return getConcept(uuid);
  }

  // Concept 2031 REASON PATIENT NOT FOUND BY ACTIVIST
  public Concept getReasonPatientNotFound() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.reasonPatientNotFound");
    return getConcept(uuid);
  }

  // Concept 2027 PATIENT IS DEAD
  public Concept getPatientIsDead() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientIsdead");
    return getConcept(uuid);
  }

  // Concept 6276 Date of ART Pickup
  public Concept getArtStatus() {
    final String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.art");
    return getConcept(uuid);
  }

  // Concept 1706 TRANSFERRED OUT TO ANOTHER FACILITY
  public Concept getTransferredOutConcept() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferOutToAnotherFacilityConceptUuid");
    return getConcept(uuid);
  }

  // Concept 23722 APPLICATION FOR LABORATORY RESEARCH
  public Concept getApplicationForLaboratoryResearch() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.applicationForLaboratoryResearch");
    return getConcept(uuid);
  }

  // Concept 1305 HIV VIRAL LOAD, QUALITATIVE
  public Concept getHivViralLoadQualitative() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.viralLoadQualitativeConceptUuid");
    return getConcept(uuid);
  }

  // Concept 23863 AUTO TRANSFER
  public Concept getAutoTransfer() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.autoTransfer");
    return getConcept(uuid);
  }

  // Concept 6272 STATE OF STAY PRIOR ART PATIENT
  public Concept getStateOfStayPriorArtPatient() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateOfStayPriorArtPatient");
    return getConcept(uuid);
  }

  // Concept 23944 REASON PATIENT NOT FOUND BY ACTIVIST (2and Visit)
  public Concept getReasonPatientNotFoundByActivistSecondVisit() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivistSecondVisit");
    return getConcept(uuid);
  }

  // Concept 23945 REASON PATIENT NOT FOUND BY ACTIVIST (3rd Visit)
  public Concept getReasonPatientNotFoundByActivistThirdVisit() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.reasonPatientNotFoundByActivistThirdVisit");
    return getConcept(uuid);
  }

  // Concept 2005 PATIENT FORGOT VISIT DATE
  public Concept getPatientForgotVisitDate() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.patientForgotVisitDate");
    return getConcept(uuid);
  }

  // Concept 2006 PATIENT IS BEDRIDDEN AT HOME
  public Concept getPatientIsBedriddenAtHome() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientIsBedriddenAtHome");
    return getConcept(uuid);
  }

  // Concept 2007 DISTANCE OR MONEY FOR TRANSPORT IS TO MUCH FOR PATIENT
  public Concept getDistanceOrMoneyForTransportIsToMuchForPatient() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.distanceOrMoneyForTransportIsToMuchForPatient");
    return getConcept(uuid);
  }

  // Concept 2010 PATIENT IS DISSATISFIED WITH DAY HOSPITAL SERVICES
  public Concept getPatientIsDissatisfiedWithDayHospitalServices() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientIsDissatisfiedWithDayHospitalServices");
    return getConcept(uuid);
  }

  // Concept 23915 FEAR OF THE PROVIDER
  public Concept getFearOfTheProvider() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.fearOfTheProvider");
    return getConcept(uuid);
  }

  // Concept 23821 Data da colheita concept id
  public Concept getSampleDateCollectionConceptId() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.sampleDateCollection");
    return getConcept(uuid);
  }

  // Concept 6246 Data de requisicao de testes de laboratório concept id
  public Concept getDateApplicationLaboratoryConceptId() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dateApplicationTestLaboratory");
    return getConcept(uuid);
  }

  // Concept 23946 Absence of Health Provider in Health Unit
  public Concept getAbsenceOfHealthProviderInHealthUnit() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.absenceOfHealthProviderInHealthUnit");
    return getConcept(uuid);
  }

  // Concept 2013 PATIENT IS TREATING HIV WITH TRADITIONAL MEDICINE
  public Concept getPatientIsTreatingHivWithTradittionalMedicine() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.patientIsTreatingHivWithTradittionalMedicine");
    return getConcept(uuid);
  }

  // Concept 2017 OTHER REASON WHY PATIENT MISSED VISIT
  public Concept getOtherReasonWhyPatientMissedVisit() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.otherReasonWhyPatientMissedVisit");
    return getConcept(uuid);
  }

  // Encounter types
  // encounterType_id = 6
  public EncounterType getAdultoSeguimentoEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 9
  public EncounterType getARVPediatriaSeguimentoEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id= 21
  public EncounterType getBuscaActivaEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.buscaActivaEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 5
  public EncounterType getARVAdultInitialEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoInitialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 7
  public EncounterType getARVPediatriaInitialEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaInicialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 18
  public EncounterType getARVPharmaciaEncounterType() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty(this.gpSTarvFarmaciaEncounterTypeUuid);
    return getEncounterType(uuid);
  }

  // encounter_type_id = 29
  public EncounterType getEvaluationAndPrepForARTEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.evaluationAndPrepForARTEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter_type 13
  public EncounterType getMisauLaboratorioEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.misauLaboratorioEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter_type 51
  public EncounterType getFSREncounterType() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.fsrEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter type 34
  public EncounterType getPrevencaoPositivaInicialEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.apssPrevencaoPositivaInicialInicialEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter type 35
  public EncounterType getPrevencaoPositivaSeguimentoEncounterType() {
    final String uuid =
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
    final String uuid =
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
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.visitaApoioReintegracaoParteB");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 1
  public EncounterType getARVAdultInitialBEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adultInitialBEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 3
  public EncounterType getARVPediatriaInitialBEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pediatriaInitialBEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 19
  public EncounterType getArtAconselhamentoEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type_id = 24
  public EncounterType getArtAconselhamentoSeguimentoEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.artAconselhamentoSeguimentoEncounterType");
    return getEncounterType(uuid);
  }

  // encounter_type = 52
  public EncounterType getMasterCardDrugPickupEncounterType() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.masterCardDrugPickupEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounter_type = 53
  public EncounterType getMasterCardEncounterType() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.masterCardEncounterType");
    return getEncounterType(uuid);
  }
  // Programs

  // program_id=2
  public Program getARTProgram() {
    final String uuid = Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgram(uuid);
  }

  public Program getPtvEtvProgram() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty(this.gpPtvEtvProgramUuid);
    return getProgram(uuid);
  }

  // program_id=1
  public Program getHIVCareProgram() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgram(uuid);
  }

  // Identifier types
  public PatientIdentifierType getNidServiceTarvIdentifierType() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.nidServicoTarvUuid");
    return getPatientIdentifierType(uuid);
  }

  // Program Workflow States
  public ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFERRED OUT TO ANOTHER FACILITY");
  }

  public ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState() {
    // TODO Refactor this method, use
    // #getTransferredFromOtherHealthFacilityWorkflowState(Program,
    // ProgramWorkflow)
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    final String transferFromOtherUuid =
        Context.getAdministrationService()
            .getGlobalProperty(this.gpTransferFromOtherFacilityConceptUuid);
    return getProgramWorkflowState(artProgramUuid, "2", transferFromOtherUuid);
  }

  public ProgramWorkflowState getArtCareTransferredFromOtherHealthFacilityWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    final ProgramWorkflowState state =
        this.getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtTransferredFromOtherHealthFacilityWorkflowState() {
    final Program hivCareProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    final ProgramWorkflowState state =
        this.getTransferredFromOtherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareTransferredOutToAnotherHealthFacilityWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    final ProgramWorkflowState state =
        this.getTransferredOutToAnotherHealthFacilityWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareActiveOnProgramWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    final ProgramWorkflowState state =
        this.getActiveOnProgramWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtActiveOnProgramWorkflowState() {
    final Program hivCareProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    final ProgramWorkflowState state =
        this.getActiveOnProgramWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtCareAbandonedWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    final ProgramWorkflowState state = this.getAbandonedWorkflowState(hivCareProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getSuspendedTreatmentWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "SUSPEND TREATMENT");
  }

  public ProgramWorkflowState getAbandonedWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "ABANDONED");
  }

  public ProgramWorkflowState getPatientHasDiedWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "PATIENT HAS DIED");
  }

  public ProgramWorkflowState getPatientIsBreastfeedingWorkflowState() {
    final String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "GAVE BIRTH");
  }

  public ProgramWorkflowState getArtCareDeadWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    return this.getDeadWorkflowState(hivCareProgram, workflow);
  }

  public ProgramWorkflowState getPateintActiveArtWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "c50d6bdc-8a79-43ae-ab45-abbaa6b45e7d");
  }

  public ProgramWorkflowState getPateintTransferedFromOtherFacilityWorkflowState() {
    final String artProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFER FROM OTHER FACILITY");
  }

  public ProgramWorkflowState getPateintPregnantWorkflowState() {
    final String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(this.gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "PREGNANT");
  }

  public ProgramWorkflowState getPateintActiveOnHIVCareProgramtWorkflowState() {
    final String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "ACTIVE ON PROGRAM");
  }

  public ProgramWorkflowState getPateintTransferedFromOtherFacilityHIVCareWorkflowState() {
    final String hivCareProgramUuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgramWorkflowState(hivCareProgramUuid, "1", "TRANSFER FROM OTHER FACILITY");
  }

  // Concept 5356
  public Concept getCurrentWHOHIVStageConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStageConceptUuid"));
  }

  public ProgramWorkflow getPreArtWorkflow() {
    return getProgramWorkflow(this.getHIVCareProgram().getUuid(), "1");
  }

  private ProgramWorkflowState getActiveOnProgramWorkflowState(
      final Program program, final ProgramWorkflow programWorkflow) {
    final Concept activeOnProgram = this.getActiveOnProgramConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), activeOnProgram.getUuid());
  }

  private ProgramWorkflowState getAbandonedWorkflowState(
      final Program program, final ProgramWorkflow programWorkflow) {
    final Concept abandoned = this.getAbandoned();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), abandoned.getUuid());
  }

  private ProgramWorkflowState getDeadWorkflowState(
      final Program program, final ProgramWorkflow programWorkflow) {
    final Concept dead = this.getPatientHasDiedConcept();
    return getProgramWorkflowState(program.getUuid(), programWorkflow.getUuid(), dead.getUuid());
  }

  private ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState(
      final Program program, final ProgramWorkflow programWorkflow) {
    final Concept transferOutToAnotherFacility = this.getTransferOutToAnotherFacilityConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), transferOutToAnotherFacility.getUuid());
  }

  private ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState(
      final Program program, final ProgramWorkflow programWorkflow) {
    final Concept transferFromOtherFacility = this.getTransferFromOtherFacilityConcept();
    return getProgramWorkflowState(
        program.getUuid(), programWorkflow.getUuid(), transferFromOtherFacility.getUuid());
  }

  public ProgramWorkflowState getArtCareInitiatedWorkflowState() {
    final Program hivCareProgram = this.getHIVCareProgram();
    final ProgramWorkflow workflow = this.getPreArtWorkflow();
    final Concept startDrugs = this.getStartDrugsConcept();
    return getProgramWorkflowState(
        hivCareProgram.getUuid(), workflow.getUuid(), startDrugs.getUuid());
  }

  public ProgramWorkflowState getArtSuspendedTreatmentWorkflowState() {
    final Program artProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    final Concept suspendedTreatment = this.getSuspendedTreatmentConcept();
    return getProgramWorkflowState(
        artProgram.getUuid(), workflow.getUuid(), suspendedTreatment.getUuid());
  }

  private ProgramWorkflow getArtWorkflow() {
    return getProgramWorkflow(this.getARTProgram().getUuid(), "2");
  }

  public ProgramWorkflowState getArtTransferredOutToAnotherHealthFacilityWorkflowState() {
    final Program artProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    final ProgramWorkflowState state =
        this.getTransferredOutToAnotherHealthFacilityWorkflowState(artProgram, workflow);
    return state;
  }

  public ProgramWorkflowState getArtDeadWorkflowState() {
    final Program artProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    return this.getDeadWorkflowState(artProgram, workflow);
  }

  public ProgramWorkflowState getArtAbandonedWorkflowState() {
    final Program artProgram = this.getARTProgram();
    final ProgramWorkflow workflow = this.getArtWorkflow();
    final ProgramWorkflowState state = this.getAbandonedWorkflowState(artProgram, workflow);
    return state;
  }
}
