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

package org.openmrs.module.eptsreports.reporting.utils;

public final class EptsReportConstants {

  // Concepts
  public static final String GLOBAL_PROPERTY_START_DRUGS_CONCEPT_UUID =
      "eptsreports.startDrugsConceptUuid";

  public static final String GLOBAL_PROPERTY_HISTORICAL_START_DATE_CONCEPT_UUID =
      "eptsreports.historicalStartDateConceptUuid";

  public static final String GLOBAL_PROPERTY_YES_CONCEPT_UUID = "eptsreports.yesConceptUuid";

  public static final String GLOBAL_PROPERTY_NO_CONCEPT_UUID = "eptsreports.noConceptUuid";

  public static final String GLOBAL_PROPERTY_ARV_PLAN_CONCEPT_UUID =
      "eptsreports.arvPlanConceptUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_TREATMENT_PLAN_CONCEPT_UUID =
      "eptsreports.tuberculosisTreatmentPlanConceptUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_TREATMENT_STARTDATE_CONCEPT_UUID =
      "eptsreports.tuberculosisTreatmentStartDateConceptUuid";

  public static final String GLOBAL_PROPERTY_TRANSFER_FROM_OTHER_FACILITY_CONCEPT_UUID =
      "eptsreports.transferFromOtherFacilityConceptUuid";

  public static final String GLOBAL_PROPERTY_HIV_VIRAL_LOAD_CONCEPT_UUID =
      "eptsreports.hivViralLoadConceptUuid";

  public static final String GLOBAL_PROPERTY_CRITERIA_FOR_ART_START_CONCEPT_UUID =
      "eptsreports.criteriaForArtStartUuid";

  public static final String GLOBAL_PROPERTY_RETURN_VISIT_DATE_FOR_ARV_DRUG_CONCEPT_UUID =
      "eptsreports.returnVisitDateForArvDrugConceptUuid";

  public static final String GLOBAL_PROPERTY_PREGNANT_CONCEPT_UUID =
      "eptsreports.pregnantConceptUuid";

  public static final String GLOBAL_PROPERTY_GESTATION_CONCEPT_UUID =
      "eptsreports.gestationConceptUuid";

  public static final String GLOBAL_PROPERTY_NUMBER_OF_WEEKS_PREGNANT_CONCEPT_UUID =
      "eptsreports.numberOfWeeksPregnantConceptUuid";

  public static final String GLOBAL_PROPERTY_PREGNANCY_DUE_DATE_CONCEPT_UUID =
      "eptsreports.pregnancyDueDateConceptUuid";

  public static final String GLOBAL_PROPERTY_BREASTFEEDING_CONCEPT_UUID =
      "eptsreports.breastfeedingConceptUuid";

  public static final String GLOBAL_PROPERTY_RETURN_VISIT_DATE_CONCEPT_UUID =
      "eptsreports.returnVisitDateConceptConceptUuid";

  public static final String GLOBAL_PROPERTY_PRIOR_DELIVERY_DATE_CONCEPT_UUID =
      "eptsreports.priorDeliveryDateConceptUuid";

  public static final String GLOBAL_PROPERTY_CHANGE_TO_ART_SECOND_LINE_CONCEPT_UUID =
      "eptsreports.artSecondLineSwitchUuid";

  public static final String GLOBAL_PROPERTY_REGIME_CONCEPT_UUID = "eptsreports.regimeUuid";

  public static final String GLOBAL_PROPERTY_RESTART_CONCEPT_UUID =
      "eptsreports.restartConceptUuid";

  public static final String GLOBAL_PROPERTY_AZT_3TC_ABC_EFV_CONCEPT_ID =
      "eptsreports.AZT_3TC_ABC_EFV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_D4T_3TC_ABC_EFV_CONCEPT_ID =
      "eptsreports.D4T_3TC_ABC_EFV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_AZT_3TC_ABC_LPV_CONCEPT_ID =
      "eptsreports.AZT_3TC_ABC_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_D4T_3TC_ABC_LPV_CONCEPT_ID =
      "eptsreports.D4T_3TC_ABC_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_AZT_DDI_LPV_CONCEPT_ID =
      "eptsreports.AZT_DDI_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_TDF_3TC_EFV_CONCEPT_ID =
      "eptsreports.TDF_3TC_EFV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_AZT_3TC_LPV_CONCEPT_ID =
      "eptsreports.AZT_3TC_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_ABC_3TC_EFV_CONCEPT_ID =
      "eptsreports.ABC_3TC_EFV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_ABC_3TC_NVP_CONCEPT_ID =
      "eptsreports.ABC_3TC_NVP_ConceptUuid";

  public static final String GLOBAL_PROPERTY_ABC_3TC_LPV_CONCEPT_ID =
      "eptsreports.ABC_3TC_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_TDF_3TC_LPV_CONCEPT_ID =
      "eptsreports.TDF_3TC_LPV_ConceptUuid";

  public static final String GLOBAL_PROPERTY_DATA_INICIO_PROFILAXIA_ISONIAZIDA_CONCEPT_ID =
      "eptsreports.DataInicioProfilaxiaIsoniazidaConceptUuid";

  public static final String GLOBAL_PROPERTY_DATA_FINALIZACAO_PROFILAXIA_ISONIAZIDA_CONCEPT_ID =
      "eptsreports.DataFimProfilaxiaIsoniazidaConceptUuid";

  public static final String GLOBAL_PROPERTY_ISONIAZID_USE_CONCEPT_ID =
      "eptsreports.isoniazidUseConceptUuid";

  public static final String GLOBAL_PROPERTY_ACCEPT_CONTACT_CONCEPT_ID =
      "eptsreports.acceptContactConceptUuid";

  public static final String GLOBAL_PROPERTY_NO_CONCEPT_ID = "eptsreports.noConceptUuid";

  // Encounter types
  public static final String GLOBAL_PROPERTY_S_TARV_ADULTO_SEGUIMENTO_ENCOUNTER_TYPE_UUID =
      "eptsreports.sTarvAdultoSeguimentoEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_S_TARV_PEDIATRIA_SEGUIMENTO_ENCOUNTER_TYPE_UUID =
      "eptsreports.sTarvPediatriaSeguimentoEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_S_TARV_FARMACIA_ENCOUNTER_TYPE_UUID =
      "eptsreports.sTarvFarmaciaEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_S_TARV_ADULTO_INITIAL_A_ENCOUNTER_TYPE_UUID =
      "eptsreports.sTarvAdultoInitialAEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_S_TARV_PEDIATRIA_INITIAL_A_ENCOUNTER_TYPE_UUID =
      "eptsreports.sTarvPediatriaInicialAEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID =
      "eptsreports.misauLaboratorioEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_APSS_PREVENCAO_POSITIVA_INICIAL_ENCOUNTER_TYPE_UUID =
      "eptsreports.apssPrevencaoPositivaInicialInicialEncounterTypeUuid";

  public static final String
      GLOBAL_PROPERTY_APSS_PREVENCAO_POSITIVA_SEGUIMENTO_ENCOUNTER_TYPE_UUID =
          "eptsreports.apssPrevencaoPositivaSeguimentoEncounterTypeUuid";

  // Programs
  public static final String GLOBAL_PROPERTY_ART_PROGRAM_UUID = "eptsreports.artProgramUuid";

  public static final String GLOBAL_PTV_ETV_PROGRAM_UUID = "eptsreports.ptvEtvProgramUuid";

  public static final String GLOBAL_PROPERTY_TB_PROGRAM_UUID = "eptsreports.tbProgramUuid";

  public static final String GLOBAL_PROPERTY_HIV_CARE_PROGRAM_UUID =
      "eptsreports.hivCareProgramUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_LIVRO_ENCOUNTER_TYPE_UUID =
      "eptsreports.tbLivroEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_PROCESSO_ENCOUNTER_TYPE_UUID =
      "eptsreports.tbProcessoEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_RASTREIO_ENCOUNTER_TYPE_UUID =
      "eptsreports.tbRastreioEncounterTypeUuid";

  public static final String GLOBAL_PROPERTY_TUBERCULOSIS_SCREENING_CONCEPT_UUID =
      "eptsreports.tbScreeningConceptUuid";

  public static final String GLOBAL_PROPERTY_RESEARCH_RESULT_CONCEPT_UUID =
      "eptsreports.researchResultForTbConceptUuid";

  public static final String GLOBAL_PROPERTY_POSITIVE_CONCEPT_UUID =
      "eptsreports.positiveConceptUuid";

  public static final String GLOBAL_PROPERTY_NEGATIVE_CONCEPT_UUID =
      "eptsreports.negativeConceptUuid";

  // Enumeration
  public enum PatientsOnRoutineEnum {
    BREASTFEEDINGPREGNANT,
    ADULTCHILDREN
  }

  public enum PregnantOrBreastfeedingWomen {
    PREGNANTWOMEN,
    BREASTFEEDINGWOMEN
  }
}
