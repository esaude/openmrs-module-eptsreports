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

  // CONCEPTS
  public Concept getYesConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.yesConceptUuid");
    return getConcept(uuid);
  }

  public Concept getNoConcept() {
    String uuid = Context.getAdministrationService().getGlobalProperty("eptsreports.noConceptUuid");
    return getConcept(uuid);
  }

  public Concept getStartDrugsConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.startDrugsConceptUuid");
    return getConcept(uuid);
  }

  public Concept getTransferFromOtherFacilityConcept() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty(gpTransferFromOtherFacilityConceptUuid);
    return getConcept(uuid);
  }

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

  public Concept getGestationConcept() {
    String uuid =
        Context.getAdministrationService().getGlobalProperty("eptsreports.gestationConceptUuid");
    return getConcept(uuid);
  }

  public Concept getNumberOfWeeksPregnant() {
    String uuid =
        Context.getAdministrationService()
            .getGlobalProperty("eptsreports.numberOfWeeksPregnantConceptUuid");
    return getConcept(uuid);
  }

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
}
