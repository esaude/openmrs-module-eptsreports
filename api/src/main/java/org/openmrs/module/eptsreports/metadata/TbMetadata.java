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
}
