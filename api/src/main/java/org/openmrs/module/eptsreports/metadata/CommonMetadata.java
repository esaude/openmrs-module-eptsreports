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
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component("commonMetadata")
public class CommonMetadata extends Metadata {

  protected String gpTransferFromOtherFacilityConceptUuid =
      "eptsreports.transferFromOtherFacilityConceptUuid";

  /**
   * concept_id = 664
   *
   * @return
   */
  public Concept getNegative() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.NegativeUuid");
    return getConcept(uuid);
  }

  /**
   * concept_id = 703
   *
   * @return
   */
  public Concept getPositive() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.positiveUuid");
    return getConcept(uuid);
  }
  // CONCEPTS
  // concept_id = 1065
  public Concept getYesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1066
  public Concept getNoConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1256
  public Concept getStartDrugsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugsConceptUuid");
    return getConcept(uuid);
  }
  // concept_id=1369
  public Concept getTransferFromOtherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(gpTransferFromOtherFacilityConceptUuid);
    return getConcept(uuid);
  }

  public Concept getTransferOutToAnotherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferOutToAnotherFacilityConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1190
  public Concept getHistoricalDrugStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1982
  public Concept getPregnantConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pregnantConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Number of weeks pregnant 1279
   *
   * @return
   */
  public Concept getNumberOfWeeksPregnant() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.numberOfWeeksPregnantConceptUuid");
    return getConcept(uuid);
  }

  /**
   * Pregnancy due date conceptId=1600
   *
   * @return
   */
  public Concept getPregnancyDueDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pregnancyDueDateConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6332
  public Concept getBreastfeeding() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.breastfeedingConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=1410
  public Concept getReturnVisitDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateConceptConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=5599
  public Concept getPriorDeliveryDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.priorDeliveryDateConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6258
  public Concept getScreeningForSTIConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.screeningForSTIIuid");
    return getConcept(uuid);
  }

  // concept_id=2066
  public Concept getStage4PediatricMozambiqueConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stage4PediatricMozambiqueUuid");
    return getConcept(uuid);
  }

  // concept_id=1670
  public Concept getAdultClinicalHistoryConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adultClinicalHistoryUuid");
    return getConcept(uuid);
  }

  // concept_id=1120
  public Concept getSkimExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.skimExamFindingsUuid");
    return getConcept(uuid);
  }

  // concept_id=1127
  public Concept getExtremityExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.extremityExamFindingsUuid");
    return getConcept(uuid);
  }

  // concept_id=1569
  public Concept getStateAdultMozambiqueConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateAdultMozambiqueUuid");
    return getConcept(uuid);
  }

  // concept_id=507
  public Concept getKaposiSarcomaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.kaposiSarcomaUuid");
    return getConcept(uuid);
  }
  // concept_id=6336
  public Concept getClassificationOfMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.classificationOfMalnutritionConceptUuid"));
  }

  // concept_id=6335
  public Concept getMalnutritionLightConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.malnutritionLightConceptUuid"));
  }

  // concept_id=68
  public Concept getMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.malnutritionConceptUuid"));
  }

  public Concept getChronicMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.chronicMalnutritionConceptUuid"));
  }

  // concept_id=5599
  public Concept getStiScreeningConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.stiScreeningConceptUuid");
    return getConcept(uuid);
  }

  // concept_id=6126
  public Concept getCotrimoxazoleProphylaxisStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cotrimoxazoleProphylaxisStartDateConceptUuid");
    return getConcept(uuid);
  }
}
