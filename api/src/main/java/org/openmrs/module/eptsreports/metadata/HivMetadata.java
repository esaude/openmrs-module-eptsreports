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
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component("hivMetadata")
public class HivMetadata extends ProgramsMetadata {

  private String gpArtProgramUuid = "eptsreports.artProgramUuid";

  private String gpPtvEtvProgramUuid = "eptsreports.ptvEtvProgramUuid";

  // Concepts
  public Concept getHivViralLoadConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivViralLoadConceptUuid");
    return getConcept(uuid);
  }

  public Concept getCriteriaForArtStart() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.criteriaForArtStartUuid");
    return getConcept(uuid);
  }

  // concept_id=5096
  public Concept getReturnVisitDateForArvDrugConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateForArvDrugConceptUuid");
    return getConcept(uuid);
  }
  // concept_id=6123
  public Concept getDateOfHivDiagnosisConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.dateOfHIVDiagnosis");
    return getConcept(uuid);
  }

  // concept_id=1190
  public Concept getARVStartDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  public Concept getARVPlanConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.arvPlanConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=21148
  public Concept getChangeToArtSecondLine() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.artSecondLineSwitchUuid");
    return getConcept(uuid);
  }

  // concept_id=1088
  public Concept getRegimeConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.regimeUuid");
    return getConcept(uuid);
  }

  public Concept getRestartConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.restartConceptUuid");
    return getConcept(uuid);
  }

  // Second line ARV concepts
  // 6328
  public Concept getAzt3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.AZT_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }
  // 6327
  public Concept getD4t3tcAbcEfvConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.D4T_3TC_ABC_EFV_ConceptUuid");
    return getConcept(uuid);
  }
  // 6326
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

  // concept id 1066
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

  public Concept getIsoniazidUsageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.isoniazidUseConceptUuid");
    return getConcept(uuid);
  }
  // concept_id=1695
  public Concept getCD4AbsoluteOBSConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsoluteOBSUuid");
    return getConcept(uuid);
  }

  // concept_id=6314
  public Concept getCoucelingActivityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coucelingActivityTypeUuid");
    return getConcept(uuid);
  }

  // concept_id=5356
  public Concept getcurrentWhoHivStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.currentWhoHivStageUuid");
    return getConcept(uuid);
  }
  // concept_id=1205
  public Concept getWho2AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who2AdultStageUuid");
    return getConcept(uuid);
  }

  // concept_id=1206
  public Concept getWho3AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who3AdultStageUuid");
    return getConcept(uuid);
  }
  // concept_id=1207
  public Concept getWho4AdultStageConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.who4AdultStageUuid");
    return getConcept(uuid);
  }

  // concept_id=5488
  public Concept getAdherenceCoucelingConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.adherenceCounselingUuid");
    return getConcept(uuid);
  }
  // concept_id=5497
  public Concept getCD4AbsoluteConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4AbsolutoUuid");
    return getConcept(uuid);
  }
  // Encounter types
  // encounterType_id = 6
  public EncounterType getAdultoSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 9
  public EncounterType getARVPediatriaSeguimentoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaSeguimentoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 5
  public EncounterType getARVAdultInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvAdultoInitialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 7
  public EncounterType getARVPediatriaInitialEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaInicialAEncounterTypeUuid");
    return getEncounterType(uuid);
  }
  // encounterType_id = 3
  public EncounterType getARVPediatriaInitialBEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sTarvPediatriaInicialBEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  // encounterType_id = 18
  public EncounterType getARVPharmaciaEncounterType() {
    String gpSTarvFarmaciaEncounterTypeUuid = "eptsreports.sTarvFarmaciaEncounterTypeUuid";
    String uuid =
        Context.getAdministrationService().getGlobalProperty(gpSTarvFarmaciaEncounterTypeUuid);
    return getEncounterType(uuid);
  }

  public EncounterType getEvaluationAndPrepForARTEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.evaluationAndPrepForARTEncounterTypeUuid");
    return getEncounterType(uuid);
  }
  // encounter_type 13
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

  // Programs
  // program_id=2
  public Program getARTProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgram(uuid);
  }

  public Program getPtvEtvProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgram(uuid);
  }

  // program_id=1
  public Program getHIVCareProgram() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.hivCareProgramUuid");
    return getProgram(uuid);
  }

  // Program Workflow States
  public ProgramWorkflowState getTransferredOutToAnotherHealthFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    return getProgramWorkflowState(artProgramUuid, "2", "TRANSFERRED OUT TO ANOTHER FACILITY");
  }

  public ProgramWorkflowState getTransferredFromOtherHealthFacilityWorkflowState() {
    String artProgramUuid = Context.getAdministrationService().getGlobalProperty(gpArtProgramUuid);
    String transferFromOtherUuid =
        Context.getAdministrationService()
            .getGlobalProperty(gpTransferFromOtherFacilityConceptUuid);
    return getProgramWorkflowState(artProgramUuid, "2", transferFromOtherUuid);
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

  public ProgramWorkflowState getPatientIsPregnantWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "PREGNANT");
  }

  public ProgramWorkflowState getPatientIsBreastfeedingWorkflowState() {
    String ptvProgramUuid =
        Context.getAdministrationService().getGlobalProperty(gpPtvEtvProgramUuid);
    return getProgramWorkflowState(ptvProgramUuid, "5", "GAVE BIRTH");
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

  public Concept getCurrentWHOHIVStageConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStageConceptUuid"));
  }

  public Concept getCurrentWHOHIVStage1Concept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStage1ConceptUuid"));
  }

  public Concept getCurrentWHOHIVStage3Concept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStage3ConceptUuid"));
  }

  public Concept getCurrentWHOHIVStage4Concept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.currentWHOHIVStage4ConceptUuid"));
  }

  public Concept getCD4CountConcept() {
    return getConcept(
        Context.getAdministrationService().getGlobalProperty("eptsreports.cd4CountConceptUuid"));
  }

  public Concept getHivCareEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hivCareEntryPointConceptUuid"));
  }

  public Concept getMobileClinicEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.mobileClinicEntryPointConceptUuid"));
  }

  public Concept getProviderBasedServiceEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.providerBasedServiceEntryPointConceptUuid"));
  }

  public Concept getMedicalInPatientEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.medicalInPatientEntryPointConceptUuid"));
  }

  public Concept getExternalConsultationEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.externalConsultationEntryPointConceptUuid"));
  }

  public Concept getLabSamplesEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.labSamplesEntryPointConceptUuid"));
  }

  public Concept getHospitalEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hospitalEntryPointConceptUuid"));
  }

  public Concept getReferredFromPedTreatmentEntryPointConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.referredFromPedTreatmentEntryPointConceptUuid"));
  }
}
