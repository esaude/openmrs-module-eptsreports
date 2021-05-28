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
   * <b>concept_id = 664</b>
   *
   * <p><b>Name:</b> NEGATIVE
   *
   * <p><b>Description:</b> General finding of a negative result.
   *
   * @return {@link Concept}
   */
  public Concept getNegative() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.NegativeUuid");
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
  public Concept getPositive() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.positiveUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1065</b>
   *
   * <p><b>Name:</b> YES
   *
   * <p><b>Description:</b> Generic answer to a question.
   *
   * @return {@link Concept}
   */
  public Concept getYesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1066</b>
   *
   * <p><b>Name:</b> NO
   *
   * <p><b>Description:</b> Generic answer to a question.
   *
   * @return {@link Concept}
   */
  public Concept getNoConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1256</b>
   *
   * <p><b>Name:</b> START DRUGS
   *
   * <p><b>Description:</b> Answer on encounter form. Implies that a patient will be started on
   * drugs for that particular encounter.
   *
   * @return {@link Concept}
   */
  public Concept getStartDrugsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugsConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1369</b>
   *
   * <p><b>Name:</b> TRANSFER FROM OTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTransferFromOtherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(gpTransferFromOtherFacilityConceptUuid);
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1706</b>
   *
   * <p><b>Name:</b> TRANSFER OUT TO ANOTHER FACILITY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getTransferOutToAnotherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.transferOutToAnotherFacilityConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1190</b>
   *
   * <p><b>Name:</b> HISTORICAL DRUG START DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getHistoricalDrugStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.historicalStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1982</b>
   *
   * <p><b>Name:</b> PREGANT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPregnantConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.pregnantConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1279</b>
   *
   * <p><b>Name:</b> NUMBER OF WEEKS PREGNANT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getNumberOfWeeksPregnant() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.numberOfWeeksPregnantConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1600</b>
   *
   * <p><b>Name:</b> PREGNANCY DUE DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getPregnancyDueDate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.pregnancyDueDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6332</b>
   *
   * <p><b>Name:</b> BREASTFEEDING
   *
   * <p><b>Description:</b> Describes the secretion of milk from the mammary glands and the period
   * of time that a mother lactates to feed her young
   *
   * @return {@link Concept}
   */
  public Concept getBreastfeeding() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.breastfeedingConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1410</b>
   *
   * <p><b>Name:</b> RETURN VISIT DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getReturnVisitDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.returnVisitDateConceptConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 5599</b>
   *
   * <p><b>Name:</b> PRIOR DELIVERY DATE
   *
   * <p><b>Description:</b> Date in which a mother delivered her child.
   *
   * @return {@link Concept}
   */
  public Concept getPriorDeliveryDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.priorDeliveryDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6258</b>
   *
   * <p><b>Name:</b> SCREENING FOR STI
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getScreeningForSTIConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.screeningForSTIIuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2066</b>
   *
   * <p><b>Name:</b> STAGE 4 PEDIATRIC, MOZAMBIQUE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStage4PediatricMozambiqueConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stage4PediatricMozambiqueUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1670</b>
   *
   * <p><b>Name:</b> ADULT CLINICAL HISTORY
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAdultClinicalHistoryConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.adultClinicalHistoryUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1120</b>
   *
   * <p><b>Name:</b> SKIN EXAM FINDINGS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSkimExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.skimExamFindingsUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1127</b>
   *
   * <p><b>Name:</b> EXTREMITY EXAM FINDINGS
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getExtremityExamFindingsConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.extremityExamFindingsUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1569</b>
   *
   * <p><b>Name:</b> STAGE 4 ADULT, MOZAMBIQUE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getStateAdultMozambiqueConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.stateAdultMozambiqueUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 507</b>
   *
   * <p><b>Name:</b> KAPOSI'S SARCOMA
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getKaposiSarcomaConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.kaposiSarcomaUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6336</b>
   *
   * <p><b>Name:</b> CLASSIFICATION OF MALNUTRITION
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getClassificationOfMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.classificationOfMalnutritionConceptUuid"));
  }

  /**
   * <b>concept_id = 6335</b>
   *
   * <p><b>Name:</b> MALNUTRITION LIGHT
   *
   * <p><b>Description:</b> Slightly malnourished
   *
   * @return {@link Concept}
   */
  public Concept getMalnutritionLightConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.malnutritionLightConceptUuid"));
  }

  /**
   * <b>concept_id = 68</b>
   *
   * <p><b>Name:</b> MALNUTRITION
   *
   * <p><b>Description:</b> Inadequate oral intake of unspecified nutrients (eg, calories, protein,
   * vitamins, etc.)
   *
   * @return {@link Concept}
   */
  public Concept getMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.malnutritionConceptUuid"));
  }

  /**
   * <b>concept_id = 1844</b>
   *
   * <p><b>Name:</b> CHRONIC MALNUTRITION
   *
   * <p><b>Description:</b> Slightly malnourished
   *
   * @return {@link Concept}
   */
  public Concept getChronicMalnutritionConcept() {
    return getConcept(
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.chronicMalnutritionConceptUuid"));
  }

  /**
   * <b>concept_id = 6258</b>
   *
   * <p><b>Name:</b> SCREENING FOR STI
   *
   * <p><b>Description:</b> Slightly malnourished
   *
   * @return {@link Concept}
   */
  public Concept getStiScreeningConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.stiScreeningConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6126</b>
   *
   * <p><b>Name:</b> COTRIMOXAZOLE PROPHYLAXIS START DATE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getCotrimoxazoleProphylaxisStartDateConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.cotrimoxazoleProphylaxisStartDateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2152</b>
   *
   * <p><b>Name:</b> NUTRITIONAL SUPPLEMENT
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getNutritionalSupplememtConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.nutritionalSupplememtConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 6143</b>
   *
   * <p><b>Name:</b> ATPU
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getATPUSupplememtConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.ATPUSupplememtConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 2151</b>
   *
   * <p><b>Name:</b> Soja
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSojaSupplememtConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.sojaSupplememtConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23898</b>
   *
   * <p><b>Name:</b> Alternativa de Linha de Tratamento Conjunto
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAlternativeLineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.alternativeLineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23741</b>
   *
   * <p><b>Name:</b> Alternativa de Primeira Linha de Tratamento
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getAlternativeFirstLineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.alternativeFirstLineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1371</b>
   *
   * <p><b>Name:</b> Mudanca de Regime
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getRegimeChangeConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.regimeChangeConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 21190</b>
   *
   * <p><b>Name:</b> REGIME ARV ALTERNATIVO A PRIMEIRA LINHA
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getRegimenAlternativeToFirstLineConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.regimenAlternativeToFirstLineConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 1138</b>
   *
   * <p><b>Name:</b> INDETERMINATE
   *
   * <p><b>Description: </b>
   *
   * @return {@link Concept}
   */
  public Concept getIndeterminate() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.indeterminateConceptUuid");
    return getConcept(uuid);
  }

  /**
   * <b>concept_id = 23956</b>
   *
   * <p><b>Name:</b> SUGESTIVE
   *
   * <p><b>Description:</b>
   *
   * @return {@link Concept}
   */
  public Concept getSugestive() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.sugestiveConceptUuid");
    return getConcept(uuid);
  }
}
