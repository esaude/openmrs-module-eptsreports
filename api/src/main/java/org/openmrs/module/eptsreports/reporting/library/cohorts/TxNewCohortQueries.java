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
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.txnew.TxNewBreastfeedingDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxNew Cohort Definition instances we want to expose for EPTS */
@Component
public class TxNewCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private HivCohortQueries hivCohortQueries;

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
  public CohortDefinition getPatientsPregnantEnrolledOnART() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsPregnantEnrolledOnART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        PregnantQueries.getPregnantWhileOnArt(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getGestationConcept().getConceptId(),
            hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getPregnancyDueDate().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfLastMenstruationConcept().getConceptId(),
            hivMetadata.getPtvEtvProgram().getProgramId()));
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
  @DocumentedDefinition(value = "txNewBreastfeedingCohort")
  public CohortDefinition getTxNewBreastfeedingCohort() {
    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setDescription("breastfeedingComposition");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setCalculation(
        Context.getRegisteredComponents(TxNewBreastfeedingDateCalculation.class).get(0));
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
    CohortDefinition transferredIn = getPatientsTransferredFromOtherHealthFacility();

    txNewComposition.getSearches().put("startedART", mapStraightThrough(startedART));
    txNewComposition.getSearches().put("transferredIn", mapStraightThrough(transferredIn));

    txNewComposition.setCompositionString("startedART NOT transferredIn");
    return txNewComposition;
  }

  /**
   * Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred
   * from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY) or
   * marked as transferred in via mastercard between start date and end date
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "transferredFromOtherHealthFacility")
  private CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("transferredFromOtherHealthFacility");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition transferredInViaProgram = getTransferredInViaProgram();
    CohortDefinition mastercard = getTransferredInViaMastercard();

    cd.addSearch("program", mapStraightThrough(transferredInViaProgram));
    cd.addSearch("mastercard", mapStraightThrough(mastercard));

    cd.setCompositionString("program or mastercard");

    return cd;
  }

  private CohortDefinition getTransferredInViaProgram() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("transferredFromOtherHealthFacility");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    String query =
        "select p.patient_id from patient p "
            + "inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d"
            + " and ps.state=%d"
            + " and ps.start_date=pg.date_enrolled"
            + " and ps.start_date between :onOrAfter and :onOrBefore and location_id=:location "
            + "group by p.patient_id";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    return cd;
  }

  private CohortDefinition getTransferredInViaMastercard() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId()));
    return cd;
  }
}
