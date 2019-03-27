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

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.prev.CompletedIsoniazidProphylaticTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TbPrev we want to expose for EPTS */
@Component
public class TbPrevCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod() {
    DateObsCohortDefinition definition = new DateObsCohortDefinition();
    definition.setName("getPatientsThatStartedProfilaxiaIsoniazida");
    definition.setQuestion(hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept());
    definition.setTimeModifier(BaseObsCohortDefinition.TimeModifier.FIRST);
    definition.setOperator1(RangeComparator.GREATER_EQUAL);
    definition.setOperator2(RangeComparator.LESS_EQUAL);
    definition.addParameter(new Parameter("value1", "After Date", Date.class));
    definition.addParameter(new Parameter("value2", "Before Date", Date.class));
    definition.addParameter(new Parameter("locationList", "Location", Location.class));
    return definition;
  }

  public CohortDefinition getNumerator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Numerator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-on-previous-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "beginPeriodStartDate=${onOrAfter-6m},beginPeriodEndDate=${onOrBefore-6m},"
                + "completionPeriodStartDate=${onOrAfter},completionPeriodEndDate=${onOrBefore+1m},"
                + "location=${location}"));
    definition.setCompositionString(
        "(started-by-end-previous-reporting-period OR started-on-previous-period) AND completed-isoniazid");
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
            genericCohortQueries.getStartedArtOnPeriod(false, false),
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
        "started-on-previous-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString(
        "started-by-end-previous-reporting-period NOT started-on-previous-period");
    return definition;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-on-previous-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.addSearch(
        "started-isoniazid",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "value1=${onOrAfter-6m},value2=${onOrBefore-6m},locationList=${location}"));
    definition.setCompositionString(
        "(started-by-end-previous-reporting-period OR started-on-previous-period) AND started-isoniazid");
    return definition;
  }

  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidProphylaticTreatmentCalculation.class)
                .get(0));
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("beginPeriodStartDate", "beginPeriodStartDate", Date.class));
    cd.addParameter(new Parameter("beginPeriodEndDate", "beginPeriodEndDate", Date.class));
    cd.addParameter(
        new Parameter("completionPeriodStartDate", "completionPeriodStartDate", Date.class));
    cd.addParameter(
        new Parameter("completionPeriodEndDate", "completionPeriodEndDate", Date.class));
    return cd;
  }
}
