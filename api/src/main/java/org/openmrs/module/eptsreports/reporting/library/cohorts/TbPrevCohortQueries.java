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
import org.openmrs.module.eptsreports.metadata.HivMetadata;
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

  public CohortDefinition getPatientsThatFinalizedProfilaxiaIsoniazidaOnPeriod() {
    DateObsCohortDefinition definition = new DateObsCohortDefinition();
    definition.setName("getPatientsThatFinalizedProfilaxiaIsoniazidaOnPeriod");
    definition.setQuestion(hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept());
    definition.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
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
    definition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "active-art",
        EptsReportUtils.map(
            genericCohortQueries.getActiveOnArt(),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "started-profilaxia",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "value1=${onOrAfter-6m},value2=${onOrBefore-6m},locationList=${location}"));
    definition.addSearch(
        "finalized-profilaxia",
        EptsReportUtils.map(
            getPatientsThatFinalizedProfilaxiaIsoniazidaOnPeriod(),
            "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}"));
    definition.addSearch(
        "started-on-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString(
        "((active-art AND started-profilaxia AND finalized-profilaxia) NOT started-on-period) OR "
            + "(started-on-period AND started-profilaxia AND finalized-profilaxia)");
    return definition;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "active-art",
        EptsReportUtils.map(
            genericCohortQueries.getActiveOnArt(),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "started-profilaxia",
        EptsReportUtils.map(
            getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
            "value1=${onOrAfter-6m},value2=${onOrBefore-6m},locationList=${location}"));
    definition.addSearch(
        "started-on-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false),
            "onOrAfter=${onOrAfter-6m},onOrBefore=${onOrBefore-6m},location=${location}"));
    definition.setCompositionString(
        "((active-art AND started-profilaxia) NOT started-on-period) OR "
            + "(started-on-period AND started-profilaxia)");
    return definition;
  }
}
