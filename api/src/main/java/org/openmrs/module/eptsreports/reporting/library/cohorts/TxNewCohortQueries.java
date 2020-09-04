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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxNew Cohort Definition instances we want to expose for EPTS */
@Component
public class TxNewCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired private CommonCohortQueries commonCohortQueries;

  /**
   * PATIENTS WITH UPDATED DATE OF DEPARTURE IN THE ART SERVICE Are patients with date of delivery
   * updated in the tarv service. Note that the 'Start Date' and 'End Date' parameters refer to the
   * date of delivery and not the date of registration (update)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithUpdatedDepartureInART() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsWithUpdatedDepartureInART");
    cd.setQuestion(commonMetadata.getPriorDeliveryDateConcept());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getARVAdultInitialEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));

    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * PREGNANCY ENROLLED IN THE ART SERVICE These are patients who are pregnant during the initiation
   * of the process or during ART follow-up and who were notified as a new pregnancy during
   * follow-up.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsPregnantEnrolledOnART(boolean dsd) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsPregnantEnrolledOnART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        PregnantQueries.getPregnantWhileOnArt(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            hivMetadata.getCriteriaForArtStart().getConceptId(),
            hivMetadata.getBPlusConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            commonMetadata.getPriorDeliveryDateConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
            dsd));
    return cd;
  }

  /**
   * Women who gave birth 2 years ago These patients are enrolled in the PMTCT program and have been
   * updated as a childbirth within 2 years of the reference date
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoGaveBirthWithinReportingPeriod() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsWhoGaveBirthWithinReportingPeriod");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        BreastfeedingQueries.getPatientsWhoGaveBirthWithinReportingPeriod(
            hivMetadata.getPtvEtvProgram().getProgramId(), 27));

    return cd;
  }

  /**
   * TxNew Breastfeeding Compisition Cohort
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxNewBreastfeedingComposition(boolean dsd) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsBreastfeedingEnrolledOnART");
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        PregnantQueries.getBreastfeedingWhileOnArt(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getPriorDeliveryDateConcept().getConceptId(),
            hivMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getCriteriaForArtStart().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getBPlusConcept().getConceptId(),
            hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
            dsd));
    return cd;
  }

  /**
   * Build TxNew composition cohort definition
   *
   * @param cohortName Cohort name
   * @return CompositionQuery
   */
  public CohortDefinition getTxNewCompositionCohort(String cohortName) {
    CompositionCohortDefinition txNewComposition = new CompositionCohortDefinition();
    txNewComposition.setName(cohortName);
    txNewComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    txNewComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    txNewComposition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = genericCohorts.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        resumoMensalCohortQueries
            .getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2E();

    txNewComposition.getSearches().put("startedART", mapStraightThrough(startedART));
    txNewComposition.getSearches().put("transferredIn", mapStraightThrough(transferredIn));

    txNewComposition.setCompositionString("startedART NOT transferredIn");
    return txNewComposition;
  }
}
