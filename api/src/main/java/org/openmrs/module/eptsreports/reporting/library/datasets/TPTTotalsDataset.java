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
package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.List;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPT_InitiationQueries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTTotalsDataset extends BaseDataSet {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private CommonMetadata commonMetadata;

  @Autowired
  public TPTTotalsDataset(
      HivMetadata hivMetadata, TbMetadata tbMetadata, CommonMetadata commonMetadata) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.commonMetadata = commonMetadata;
  }

  public DataSetDefinition constructDataset(List<Parameter> parameterList) {

    SqlDataSetDefinition sdd = new SqlDataSetDefinition();

    sdd.setName("TOTAL");
    sdd.addParameters(parameterList);
    sdd.setSqlQuery(
        TPT_InitiationQueries.getTPTInitiationTOTALS(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
            tbMetadata.get3HPConcept().getConceptId(),
            tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
            tbMetadata.getRegimeTPTConcept().getConceptId(),
            tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
            hivMetadata.getIsoniazidUsageConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getIsoniazidConcept().getConceptId(),
            tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
            commonMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getCriteriaForArtStart().getConceptId(),
            hivMetadata.getBpostiveConcept().getConceptId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
            commonMetadata.getPriorDeliveryDateConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
            tbMetadata.getDtINHConcept().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId(),
            tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
            hivMetadata.getMonthlyConcept().getConceptId(),
            hivMetadata.getQuarterlyConcept().getConceptId(),
            hivMetadata.getCompletedConcept().getConceptId(),
            hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId(),
            hivMetadata.getHIVCareProgram().getProgramId()));

    return sdd;
  }
}
