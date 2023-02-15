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
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component("tbMetadata")
public class TbMetadata extends ProgramsMetadata {

  /**
   * <b>concept_id = 161</b>
   *
   * <p><b>Name:</b> LYMPHADENOPATHY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getLymphadenopathy() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lymphadenopathyUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1760</b>
   *
   * <p><b>Name:</b> COUGH LASTING MORE THAN 3 WEEKS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCoughLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coughLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1762</b>
   *
   * <p><b>Name:</b> NIGHTSWEATS LASTING MORE THAN 3 WEEKS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getNightsWeatsLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nightsWeatsLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1763</b>
   *
   * <p><b>Name:</b> FEVER LASTING MORE THAN 3 WEEKS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getFeverLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.feverLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1764</b>
   *
   * <p><b>Name:</b> WEIGHT LOSS OF MORE THAN 3 KG IN LAST MONTH
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getWeightLossOfMoreThan3KgInLastMonth() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.weightLossOfMoreThan3KgInLastMonthUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1765</b>
   *
   * <p><b>Name:</b> COHABITANT BEING TREATED FOR TB
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCohabitantBeingTreatedForTB() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cohabitantBeingTreatedForTBUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1268</b>
   *
   * <p><b>Name:</b> TUBERCULOSIS TREATMENT PLAN
   *
   * <p><b>Description:</b> Question on encounter form. Collects information related to tuberculosis
   * drug therapy plans.
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
   * <b>concept_id = 1766</b>
   *
   * <p><b>Name:</b> TB OBSERVATIONS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getObservationTB() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.observationTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 42</b>
   *
   * <p><b>Name:</b> TB OBSERVATIONS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPulmonaryTB() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pulmonaryTBUuid");
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
  public Concept getTBDrugTreatmentStartDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23723</b>
   *
   * <p><b>Name:</b> TB MWRD TEST
   *
   * <p><b>Description:</b> Test for the diagnosis of tuberculosis
   *
   * @return {@link Concept}
   */
  public Concept getTBGenexpertTestConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TBGenexpertTestUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23774</b>
   *
   * <p><b>Name:</b> CULTURE TEST
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCultureTest() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cultureTestUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23760</b>
   *
   * <p><b>Name:</b> ASTHENIA
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAsthenia() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.astheniaUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23761</b>
   *
   * <p><b>Name:</b> ACTIVE TB
   *
   * <p><b>Description:</b> Active TB diagnosis
   *
   * @return {@link Concept}
   */
  public Concept getActiveTBConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23951</b>
   *
   * <p><b>Name:</b> TB LAM
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTestTBLAM() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.testTBLAMUuid");
    return getConcept(uuid);
  }

  // Programs

  /**
   * <b>program_id = 5</b>
   *
   * <p><b>Name:</b> TUBERCULOSIS
   *
   * <p><b>Description:</b> Programa de Combate a Tuberculose
   *
   * @return {@link Program}
   */
  public Program getTBProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.tbProgramUuid");
    return getProgram(uuid);
  }

  // encounter types

  /**
   * <b>encounterType_id = 25</b>
   *
   * <p><b>Name:</b> TUBERCULOSE: LIVRO
   *
   * <p><b>Description:</b> Livro de registo de doentes com Tuberculose
   *
   * @return {@link EncounterType}
   */
  public EncounterType getTBLivroEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbLivroEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 26</b>
   *
   * <p><b>Name:</b> TUBERCULOSE: PROCESSO
   *
   * <p><b>Description:</b> Tratamento de Tuberculose
   *
   * @return {@link EncounterType}
   */
  public EncounterType getTBProcessoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbProcessoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>encounterType_id = 20</b>
   *
   * <p><b>Name:</b> TUBERCULOSE: RASTREIO
   *
   * <p><b>Description:</b>
   *
   * @return {@link EncounterType}
   */
  public EncounterType getTBRastreioEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbRastreioEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>concept_id = 6257</b>
   *
   * <p><b>Name:</b> SCREENING FOR TB
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTbScreeningConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tbScreeningConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6277</b>
   *
   * <p><b>Name:</b> RESEARCH RESULT FOR TB
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getResearchResultConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.researchResultForTbConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 703</b>
   *
   * <p><b>Name:</b> POSITIVE
   *
   * <p><b>Description:</b> General finding of a positive result.
   *
   * @return {@link Concept}
   */
  public Concept getPositiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.positiveConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 664</b>
   *
   * <p><b>Name:</b> NEGATIVE
   *
   * <p><b>Description:</b> General finding of a negative result.
   *
   * @return {@link Concept}
   */
  public Concept getNegativeConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.negativeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6120</b>
   *
   * <p><b>Name:</b> TB DRUGS TREATMENT END DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept gettbDgrusTreatmentEndDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbDgrusTreatmentEndDateUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23758</b>
   *
   * <p><b>Name:</b> TB SYMPTOMS
   *
   * <p><b>Description:</b> has TB symptoms
   *
   * @return {@link Concept}
   */
  public Concept getHasTbSymptomsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hasTbSymptomsConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 12</b>
   *
   * <p><b>Name:</b> X-RAY, CHEST
   *
   * <p><b>Description: An examination using irradiation for imaging the chest.</b>
   *
   * @return {@link Concept}
   */
  public Concept getXRayChest() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.xrayChestConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>encounterType_id = 60</b>
   *
   * <p><b>Name:</b> Tratamento Profilático da Tuberculose (TPT)
   *
   * <p><b>Description: Tratamento Profilático da Tuberculose (TPT)</b>
   *
   * @return {@link EncounterType}
   */
  public EncounterType getRegimeTPTEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.regimeTPTEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  /**
   * <b>concept_id = 656</b>
   *
   * <p><b>Name:</b> Isoniazid
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getIsoniazidConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.isoniazidConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1719</b>
   *
   * <p><b>Name:</b> TREATMENT PRESCRIBED
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getTreatmentPrescribedConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.treatmentPrescribedConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23982</b>
   *
   * <p><b>Name:</b> Isoniazide + Piridoxina
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getIsoniazidePiridoxinaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.isoniazidePiridoxinaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23984</b>
   *
   * <p><b>Name:</b> Regime TPT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept get3HPPiridoxinaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.3HPPiridoxinaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23985</b>
   *
   * <p><b>Name:</b> Regime TPT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getRegimeTPTConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.regimeTPTConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23954</b>
   *
   * <p><b>Name:</b> 3HP
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept get3HPConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.3HPConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23955</b>
   *
   * <p><b>Name:</b> DT-INH
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDtINHConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.dtINHConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23986</b>
   *
   * <p><b>Name:</b> Tipo de dispensa TPT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getTypeDispensationTPTConceptUuid() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.typeDispensationTPTConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23987</b>
   *
   * <p><b>Name:</b> Seguimento de tratamento TPT
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getTreatmentFollowUpTPTConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.treatmentFollowUpTPTConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165307</b>
   *
   * <p><b>Name:</b> DT-3HP
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDT3HPConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.DT3HPConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165308</b>
   *
   * <p><b>Name:</b> ESTADO DA PROFLAXIA
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getDataEstadoDaProfilaxiaConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.dataEstadoDaProfilaxiaConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165189</b>
   *
   * <p><b>Name:</b> DETECTED MTB PRESENCE
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getTestXpertMtbUuidConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.testXpertMtbConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165192</b>
   *
   * <p><b>Name:</b> RESISTANCE TO RIFAMPIN
   *
   * <p><b>Description: RESISTANCE TO RIFAMPIN Concept
   *
   * @return {@link Concept}
   */
  public Concept getRifampinResistanceConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.rifampinResistanceConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 165184</b>
   *
   * <p><b>Name:</b> NOT FOUND
   *
   * <p><b>Description: NOT FOUND Concept
   *
   * @return {@link Concept}
   */
  public Concept getNotFoundTestResultConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.notFoundTestResultConceptUuid");
    return getConcept(uuid);
  }
}
