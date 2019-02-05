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
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {

  @Autowired private GenderCohortQueries genderCohortQueries;

  @Autowired private AgeCohortQueries ageCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * Gender dimension
   *
   * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension gender() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("gender");
    dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.MaleCohort(), ""));
    dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
    return dim;
  }

  /**
   * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
   *
   * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension age() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
    dim.setName("age dimension");

    dim.addCohortDefinition(
        "UK", EptsReportUtils.map(ageCohortQueries.getPatientsWithUnknownAge(), ""));
    dim.addCohortDefinition(
        "<1",
        EptsReportUtils.map(
            ageCohortQueries.createBelowYAgeCohort("patients with age bellow 1", 1),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "1-4",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 1 and 4", 1, 4),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 5 and 9", 5, 9),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 10 and 14", 10, 14),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "15-19",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 15 and 19", 15, 19),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "20-24",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 20 and 24", 20, 24),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "25-29",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 25 and 29", 25, 29),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "30-34",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 30 and 34", 30, 34),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "35-39",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 35 and 39", 35, 39),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "40-44",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 40 and 44", 40, 44),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "45-49",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("patients with age between 45 and 49", 45, 49),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "50+",
        EptsReportUtils.map(
            ageCohortQueries.createOverXAgeCohort("patients with age over 50", 50),
            "effectiveDate=${effectiveDate}"));
    return dim;
  }

  public CohortDefinitionDimension txNewAges() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("age dimension");
    dim.addParameter(new Parameter("startDate", "startDate", Date.class));
    dim.addParameter(new Parameter("endDate", "endDate", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    String mappings = "location=${location},onOrAfter=${startDate},onOrBefore=${endDate}";
    dim.addCohortDefinition(
        "<1",
        EptsReportUtils.map(txNewCohortQueries.createBelowXAgeOnArtStartDateCohort(1), mappings));
    dim.addCohortDefinition(
        "1-4",
        EptsReportUtils.map(txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(1, 4), mappings));
    dim.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(5, 9), mappings));
    dim.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(10, 14), mappings));
    dim.addCohortDefinition(
        "15-19",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(15, 19), mappings));
    dim.addCohortDefinition(
        "20-24",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(20, 24), mappings));
    dim.addCohortDefinition(
        "25-29",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(25, 29), mappings));
    dim.addCohortDefinition(
        "30-34",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(30, 34), mappings));
    dim.addCohortDefinition(
        "35-39",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(35, 39), mappings));
    dim.addCohortDefinition(
        "40-44",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(40, 44), mappings));
    dim.addCohortDefinition(
        "45-49",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(45, 49), mappings));
    dim.addCohortDefinition(
        "50+",
        EptsReportUtils.map(txNewCohortQueries.createOverXAgeOnArtStartDateCohort(50), mappings));
    dim.addCohortDefinition(
        "unknown", EptsReportUtils.map(genericCohortQueries.getUnknownAgeCohort(), ""));
    return dim;
  }

  /** @return CohortDefinitionDimension */
  public CohortDefinitionDimension maternityDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Maternity Dimension");

    dim.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }
}
