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
import org.openmrs.module.eptsreports.reporting.library.cohorts.IMR1CohortQueries;
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
public class IMR1Dataset extends BaseDataSet {

  @Autowired private IMR1CohortQueries iMR1CohortQueries;
  @Autowired private IMR1BCohortQueries iMR1BCohortQueries;

  @Autowired private IMR1Dimensions iMR1Dimensions;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicatorDataSetDefinition constructIMR1DataSet() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("IMR1 Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";

    CohortDefinition denominatorDefinition =
        this.iMR1CohortQueries.getPatientsNewlyEnrolledOnArtCare();

    CohortDefinition numerator1_Definition =
        this.iMR1CohortQueries.getPatientsNewlyEnrolledOnArtCareNumerator();
    CohortDefinition numerator1B_Definition =
        this.iMR1BCohortQueries.getPatientsNewlyEnrolledOnArtWhoInitiatedArtTreatment();

    CohortDefinition denominatorExcludingPregnantAndBreastfeedingDefinition =
        this.iMR1CohortQueries
            .getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastfeedingDenominator();
    CohortDefinition numerator1_ExcludingPregnantsAndBreastFeedingDefinition =
        this.iMR1CohortQueries
            .getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreasFeedingNumerator();

    CohortDefinition numerator1B_ExcludingPregnantsAndBreastFeedingDefinition =
        this.iMR1BCohortQueries
            .getPatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreasFeedingNumerator();

    CohortIndicator denominatorIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCare", EptsReportUtils.map(denominatorDefinition, mappings));

    CohortIndicator numerator1_Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumerator1B",
            EptsReportUtils.map(numerator1_Definition, mappings));

    CohortIndicator numerator1B_Indicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumerator1B",
            EptsReportUtils.map(numerator1B_Definition, mappings));

    CohortIndicator denominatorExcludingPregnantsAndBreastFeedingIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumeratorExcludingPregnantsAndBreastFeedingDenominator",
            EptsReportUtils.map(denominatorExcludingPregnantAndBreastfeedingDefinition, mappings));

    CohortIndicator numerator1_ExcludingPregnantsAndBreastFeedingIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastFeedingNumerator",
            EptsReportUtils.map(numerator1_ExcludingPregnantsAndBreastFeedingDefinition, mappings));

    CohortIndicator numerator1B_ExcludingPregnantsAndBreastFeedingIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareExcludingPregnantsAndBreastFeedingNumerator1B",
            EptsReportUtils.map(
                numerator1B_ExcludingPregnantsAndBreastFeedingDefinition, mappings));

    dataSetDefinition.addDimension(
        "state", EptsReportUtils.map(iMR1Dimensions.getDimension(), mappings));
    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addColumn(
        "D-All", "Denominator: All", EptsReportUtils.map(denominatorIndicator, mappings), "");
    dataSetDefinition.addColumn(
        "D-PREGNANT",
        "Denominator Pregnant",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "D-BREASTFEEDING",
        "Denominator Breastfeendig",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "D-CHILDREN",
        "Denominator children",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "D-NON-PREGNANT",
        "Denominator Non Pregnant and Breastfeeding",
        EptsReportUtils.map(denominatorExcludingPregnantsAndBreastFeedingIndicator, mappings),
        "age=15+");

    // dataSetDefinition.addColumn("D-ADULTS", "Denominator Adults",
    // EptsReportUtils.map(denominatorIndicator, mappings), "age=15+");

    dataSetDefinition.addColumn(
        "N-All", "Numerator: All", EptsReportUtils.map(numerator1_Indicator, mappings), "");
    dataSetDefinition.addColumn(
        "N-PREGNANT",
        "Numerator Pregnant",
        EptsReportUtils.map(numerator1_Indicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "N-BREASTFEEDING",
        "Numerator Breastfeendig",
        EptsReportUtils.map(numerator1_Indicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "N-CHILDREN",
        "Numerator children",
        EptsReportUtils.map(numerator1_Indicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "N-NON-PREGNANT",
        "Numerator Non Pregnant and Breasfeeding",
        EptsReportUtils.map(numerator1_ExcludingPregnantsAndBreastFeedingIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "N-1B-All", "Numerator 1B: All", EptsReportUtils.map(numerator1B_Indicator, mappings), "");
    dataSetDefinition.addColumn(
        "N-1B-PREGNANT",
        "Numerator 1B Pregnant",
        EptsReportUtils.map(numerator1B_Indicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "N-1B-BREASTFEEDING",
        "Numerator 1B Breastfeendig",
        EptsReportUtils.map(numerator1B_Indicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "N-1B-CHILDREN",
        "Numerator 1B children",
        EptsReportUtils.map(numerator1B_Indicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "N-1B-NON-PREGNANT",
        "Numerator 1B Non Pregnant and Breasfeeding",
        EptsReportUtils.map(numerator1B_ExcludingPregnantsAndBreastFeedingIndicator, mappings),
        "age=15+");

    // dataSetDefinition.addColumn("N-ADULTS", "Numerator Adults",
    // EptsReportUtils.map(numeratorIndicator, mappings),
    // "age=15+");

    return dataSetDefinition;
  }
}
