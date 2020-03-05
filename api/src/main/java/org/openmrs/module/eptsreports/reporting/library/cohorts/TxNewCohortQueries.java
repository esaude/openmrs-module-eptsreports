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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
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

  @Autowired private BreastFeedingCohortQueries breastFeedingCohortQueries;

  /**
   * PATIENTS WITH UPDATED DATE OF DEPARTURE IN THE ART SERVICE Are patients with date of delivery
   * updated in the tarv service. Note that the 'Start Date' and 'End Date' parameters refer to the
   * date of delivery and not the date of registration (update)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithUpdatedDepartureInART() {
    final DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsWithUpdatedDepartureInART");
    cd.setQuestion(this.commonMetadata.getPriorDeliveryDateConcept());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);

    final List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
    encounterTypes.add(this.hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(this.hivMetadata.getARVAdultInitialEncounterType());
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
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsPregnantEnrolledOnART");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        PregnantQueries.getPregnantWhileOnArt(
            this.commonMetadata.getPregnantConcept().getConceptId(),
            this.hivMetadata.getYesConcept().getConceptId(),
            this.hivMetadata.getNumberOfWeeksPregnant().getConceptId(),
            this.hivMetadata.getPregnancyDueDate().getConceptId(),
            this.hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getPtvEtvProgram().getProgramId()));
    return cd;
  }

  /**
   * Women who gave birth 2 years ago These patients are enrolled in the PMTCT program and have been
   * updated as a childbirth within 2 years of the reference date
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoGaveBirthWithinReportingPeriod() {
    final SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsWhoGaveBirthWithinReportingPeriod");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        BreastfeedingQueries.getPatientsWhoGaveBirthWithinReportingPeriod(
            this.hivMetadata.getPtvEtvProgram().getProgramId(), 27));

    return cd;
  }

  /**
   * TxNew Breastfeeding Compisition Cohort
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "txNewBreastfeedingComposition")
  public CohortDefinition getTxNewBreastfeedingComposition() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setDescription("breastfeedingComposition");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "DATAPARTO",
        EptsReportUtils.map(
            this.getPatientsWithUpdatedDepartureInART(),
            "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "INICIOLACTANTE",
        EptsReportUtils.map(
            this.genericCohorts.hasCodedObs(
                this.hivMetadata.getCriteriaForArtStart(),
                BaseObsCohortDefinition.TimeModifier.FIRST,
                SetComparator.IN,
                Arrays.asList(this.hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(this.commonMetadata.getBreastfeeding())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "LACTANTEPROGRAMA",
        EptsReportUtils.map(
            this.getPatientsWhoGaveBirthWithinReportingPeriod(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
    cd.addSearch(
        "LACTANTE",
        EptsReportUtils.map(
            this.genericCohorts.hasCodedObs(
                this.commonMetadata.getBreastfeeding(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                SetComparator.IN,
                Arrays.asList(this.hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(this.commonMetadata.getYesConcept())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));

    final String compositionString =
        "(DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA OR LACTANTE)";

    cd.setCompositionString(compositionString);
    return cd;
  }

  /**
   * Build TxNew composition cohort definition
   *
   * @param cohortName Cohort name
   * @return CompositionQuery
   */
  public CohortDefinition getTxNewCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "START-ART AND (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  public CohortDefinition findPatientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant(
      final AgeRange ageRange) {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("patientsNewlyEnrolledByAgeInAPeriodExcludingBreastFeedingAndPregnant");

    definition.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    definition.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.addSearch(
        "IART",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(ageRange),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "patientsWhoArePregnantInAPeriod",
                PregnantQueries.findPatientsWhoArePregnantInAPeriod()),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.breastFeedingCohortQueries
                .findPatientsWhoAreBreastFeedingExcludingPregnantsInAPeriod(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));

    definition.setCompositionString("IART NOT (PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtByAgeRange(final AgeRange ageRange) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnArtByAge;
    query = String.format(query, ageRange.getMin(), ageRange.getMax());

    if (AgeRange.ADULT.equals(ageRange)) {
      query =
          query.replace(
              "BETWEEN " + ageRange.getMin() + " AND " + ageRange.getMax(),
              ">= " + ageRange.getMax());
    }

    definition.setQuery(query);

    return definition;
  }
}
