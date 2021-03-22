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

package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.IMR1BCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.IMR1Dimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class IMR1BDataset extends BaseDataSet {

  @Autowired private IMR1BCohortQueries iMR1BCohortQueries;

  @Autowired private IMR1Dimensions iMR1Dimensions;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicatorDataSetDefinition constructIMR1BDataSet() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("IMR1 B Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";

    CohortDefinition denominatorDefinition =
        this.iMR1BCohortQueries.getPatientsNewlyEnrolledOnArtCare();
    CohortDefinition numeratorDefinition =
        this.iMR1BCohortQueries.getPatientsNewlyEnrolledOnArtWhoInitiatedArtTreatment();

    CohortDefinition denominatorExcludingPregnantAndBreastfeedingDefinition =
        this.iMR1BCohortQueries
            .getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastfeedingDenominator();
    CohortDefinition numeratorExcludingPregnantsAndBreastFeedingDefinition =
        this.iMR1BCohortQueries
            .getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreasFeedingNumerator();

    CohortIndicator denominatorIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCare1B",
            EptsReportUtils.map(denominatorDefinition, mappings));
    CohortIndicator numeratorIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumerator1B",
            EptsReportUtils.map(numeratorDefinition, mappings));

    CohortIndicator denominatorExcludingPregnantsAndBreastFeedingIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumeratorExcludingPregnantsAndBreastFeedingDenominator",
            EptsReportUtils.map(denominatorExcludingPregnantAndBreastfeedingDefinition, mappings));
    CohortIndicator numeratorExcludingPregnantsAndBreastFeedingIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastFeedingNumerator",
            EptsReportUtils.map(numeratorExcludingPregnantsAndBreastFeedingDefinition, mappings));

    dataSetDefinition.addDimension(
        "state", EptsReportUtils.map(iMR1Dimensions.getDimension(), mappings));
    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addColumn(
        "D1B-All", "Denominator 1B: All", EptsReportUtils.map(denominatorIndicator, mappings), "");
    dataSetDefinition.addColumn(
        "D1B-PREGNANT",
        "Denominator 1B Pregnant",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "D1B-BREASTFEEDING",
        "Denominator 1B Breastfeendig",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "D1B-CHILDREN",
        "Denominator 1B children",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "D1B-NON-PREGNANT",
        "Denominator 1B Non Pregnant and Breastfeeding",
        EptsReportUtils.map(denominatorExcludingPregnantsAndBreastFeedingIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "N1B-All", "Numerator 1B: All", EptsReportUtils.map(numeratorIndicator, mappings), "");
    dataSetDefinition.addColumn(
        "N1B-PREGNANT",
        "Numerator 1B Pregnant",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "N1B-BREASTFEEDING",
        "Numerator 1B Breastfeendig",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "N1B-CHILDREN",
        "Numerator 1B children",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "N1B-NON-PREGNANT",
        "Numerator 1B Non Pregnant and Breasfeeding",
        EptsReportUtils.map(numeratorExcludingPregnantsAndBreastFeedingIndicator, mappings),
        "age=15+");

    return dataSetDefinition;
  }
}
