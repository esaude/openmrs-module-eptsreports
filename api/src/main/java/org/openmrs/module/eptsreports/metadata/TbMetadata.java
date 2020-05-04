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
   * Concept_id = 161
   *
   * @return
   */
  public Concept getLymphadenopathy() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lymphadenopathyUuid");
    return getConcept(uuid);
  }
  /**
   * concept_id = 1760
   *
   * @return
   */
  public Concept getCoughLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coughLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 1762
   *
   * @return
   */
  public Concept getNightsWeatsLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nightsWeatsLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }
  /**
   * concept_id = 1763
   *
   * @return
   */
  public Concept getFeverLastingMoraThan3Weeks() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.feverLastingMoraThan3WeeksUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 1764
   *
   * @return
   */
  public Concept getWeightLossOfMoreThan3KgInLastMonth() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.weightLossOfMoreThan3KgInLastMonthUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 1765
   *
   * @return
   */
  public Concept getCohabitantBeingTreatedForTB() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cohabitantBeingTreatedForTBUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 1268
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
   * concept_id = 1766
   *
   * @return
   */
  public Concept getObservationTB() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.observationTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 42
   *
   * @return
   */
  public Concept getPulmonaryTB() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pulmonaryTBUuid");
    return getConcept(uuid);
  }

  public Concept getTBDrugTreatmentStartDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23723
   *
   * @return
   */
  public Concept getTBGenexpertTestConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.TBGenexpertTestUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 23774
   *
   * @return
   */
  public Concept getCultureTest() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cultureTestUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 23760
   *
   * @return
   */
  public Concept getAsthenia() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.astheniaUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 23761
   *
   * @return
   */
  public Concept getActiveTBConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeTBConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Concept_id = 23951
   *
   * @return
   */
  public Concept getTestTBLAM() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.testTBLAMUuid");
    return getConcept(uuid);
  }

  // Programs
  public Program getTBProgram() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.tbProgramUuid");
    return getProgram(uuid);
  }

  // encounter types
  public EncounterType getTBLivroEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbLivroEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  public EncounterType getTBProcessoEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbProcessoEncounterTypeUuid");
    return getEncounterType(uuid);
  }

  public EncounterType getTBRastreioEncounterType() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbRastreioEncounterTypeUuid");
    return getEncounterType(uuid);
  }
  /**
   * concept_id 6257
   *
   * @return
   */
  public Concept getTbScreeningConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tbScreeningConceptUuid");
    return getConcept(uuid);
  }

  public Concept getResearchResultConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.researchResultForTbConceptUuid");
    return getConcept(uuid);
  }

  public Concept getPositiveConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.positiveConceptUuid");
    return getConcept(uuid);
  }

  public Concept getNegativeConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.negativeConceptUuid");
    return getConcept(uuid);
  }

  public Concept gettbDgrusTreatmentEndDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tbDgrusTreatmentEndDateUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id=23758
   *
   * @return
   */
  public Concept getHasTbSymptomsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hasTbSymptomsConceptUuid");
    return getConcept(uuid);
  }
}
