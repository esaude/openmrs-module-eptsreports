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
   * <p><b>Name:</b> TB GENEXPERT TEST
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
}
