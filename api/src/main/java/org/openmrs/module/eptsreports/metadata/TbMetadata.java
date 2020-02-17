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

  // Concepts
  public Concept getTBTreatmentPlanConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentPlanConceptUuid");
    return getConcept(uuid);
  }

  public Concept getTBDrugTreatmentStartDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.tuberculosisTreatmentStartDateConceptUuid");
    return getConcept(uuid);
  }

  public Concept getActiveTBConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.activeTBConceptUuid");
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

  // concept_id=23758
  public Concept getHasTbSymptomsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.hasTbSymptomsConceptUuid");
    return getConcept(uuid);
  }

  // Concept 42 PULMONARY TB, QUALITATIVE
  public Concept getPulmonaryTB() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pulmonaryTBConceptUuid");
    return getConcept(uuid);
  }

  // Concept 1763 FEVER LASTING MORE THAN 3 WEEKS
  public Concept getFeverLastingMoreThan3Weeks() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.feverLastingMoreThan3Weeks");
    return getConcept(uuid);
  }

  // Concept 1764 WEIGHT LOSS OF MORE THAN 3 KG IN LAST MONTH
  public Concept getWeightLossOfMoreThan3KgInLastMonth() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.weightLossOfMoreThan3KgInLastMonth");
    return getConcept(uuid);
  }

  // Concept 1762 NIGHTSWEATS LASTING MORE THAN 3 WEEKS
  public Concept getNightsweatsLastingMoreThan3Weeks() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nightsweatsLastingMoreThan3Weeks");
    return getConcept(uuid);
  }

  // Concept 1760 COUGH LASTING MORE THAN 3 WEEKS
  public Concept getCoughLastingMoreThan3Weeks() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.coughLastingMoreThan3Weeks");
    return getConcept(uuid);
  }

  // Concept 23760 ASTHENIA
  public Concept getAsthenia() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.asthenia");
    return getConcept(uuid);
  }

  // Concept 1765 COHABITANT BEING TREATED FOR TB
  public Concept getCohabitantBeingTreatedForTB() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cohabitantBeingTreatedForTB");
    return getConcept(uuid);
  }

  // Concept 161 LYMPHADENOPATHY
  public Concept getLymphadenopathy() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.lymphadenopathy");
    return getConcept(uuid);
  }

  // Concept 23723 B GENEXPERT TEST
  public Concept getTbGenexpertTest() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tbGenexpertTest");
    return getConcept(uuid);
  }

  // Concept 23774 CULTURE TEST
  public Concept getCultureTest() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.cultureTest");
    return getConcept(uuid);
  }

  // Concept 23951 TB LAM
  public Concept getTbLam() {
    final String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.tbLam");
    return getConcept(uuid);
  }

  // Concept 1766 TB LAM
  public Concept getTbObservations() {
    final String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.tbObservations");
    return getConcept(uuid);
  }

  // Concept 307 SPUTUM FOR ACID FAST BACILLI
  public Concept getSputumForAcidFastBacilli() {
    final String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sputumForAcidFastBacilli");
    return getConcept(uuid);
  }
}
