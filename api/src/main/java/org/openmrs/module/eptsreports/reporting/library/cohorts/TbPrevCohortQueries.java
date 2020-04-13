/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.prev.CompletedIsoniazidProphylaticTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TbPrev we want to expose for EPTS */
@Component
public class TbPrevCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  public CohortDefinition getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod() {
    Concept treatmentStartConcept = hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept();
    String encounterTypesList =
        StringUtils.join(
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getId(),
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
            ",");
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient states based on end of reporting period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "select distinct obs.person_id from obs "
            + "join encounter on encounter.encounter_id = obs.encounter_id "
            + "where obs.concept_id = %s and obs.voided = false "
            + "  and obs.value_datetime between :onOrAfter and :onOrBefore "
            + "  and obs.location_id = :location and encounter.encounter_type in (%s) "
            + "  and encounter.voided = false ";
    cd.setQuery(String.format(query, treatmentStartConcept.getConceptId(), encounterTypesList));
    return cd;
  }

  public CohortDefinition getNumerator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Numerator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    definition.setCompositionString(
        "started-by-end-previous-reporting-period AND completed-isoniazid");
    return definition;
  }

  public CohortDefinition getNewOnArt() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV New on ART");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-on-previous-period",
        EptsReportUtils.map(
            genericCohortQueries.getNewlyOrPreviouslyEnrolledOnART(true),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString("started-on-previous-period");
    return definition;
  }

  public CohortDefinition getPreviouslyOnArt() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Previously on ART");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getNewlyOrPreviouslyEnrolledOnART(false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString("started-by-end-previous-reporting-period");
    return definition;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "started-isoniazid",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "initiated-profilaxia",
        EptsReportUtils.map(
            getPatientsThatInitiatedProfilaxia(),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    definition.setCompositionString(
        "started-by-end-previous-reporting-period AND ((started-isoniazid OR initiated-profilaxia) NOT (transferred-out NOT completed-isoniazid))");
    return definition;
  }

  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidProphylaticTreatmentCalculation.class)
                .get(0));
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   * Patients who on “Ficha Clinica-MasterCard”, mastercard under “ Profilaxia INH” were marked with
   * an “I” (inicio) in a clinical consultation date occurred between ${onOrAfter} and ${onOrBefore}
   *
   * @return the cohort definition
   */
  public CohortDefinition getPatientsThatInitiatedProfilaxia() {
    Concept profilaxiaINH = hivMetadata.getIsoniazidUsageConcept();
    Concept inicio = hivMetadata.getStartDrugs();
    EncounterType adultoSeguimentoEncounterType = hivMetadata.getAdultoSeguimentoEncounterType();

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("Initiated Profilaxia");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.setEncounterTypeList(Collections.singletonList(adultoSeguimentoEncounterType));
    cd.setTimeModifier(TimeModifier.ANY);
    cd.setQuestion(profilaxiaINH);
    cd.setValueList(Collections.singletonList(inicio));
    cd.setOperator(SetComparator.IN);
    return cd;
  }
}
