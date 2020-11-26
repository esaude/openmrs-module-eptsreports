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
    CohortDefinition numeratorDefinition =
        this.iMR1CohortQueries.getPatientsNewlyEnrolledOnArtCareNumerator();
    CohortDefinition denominatorExcludingPregnantDefinition =
        this.iMR1CohortQueries.getPatientsNewlyEnrolledOnArtCareExcludingPregnants();
    CohortDefinition numeratorExcludingPregnantsDefinition =
        this.iMR1CohortQueries.getPatientsNewlyEnrolledOnArtCareNumeratorExcludingPregnants();

    CohortIndicator denominatorIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCare", EptsReportUtils.map(denominatorDefinition, mappings));
    CohortIndicator numeratorIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumerator",
            EptsReportUtils.map(numeratorDefinition, mappings));
    CohortIndicator denominatorExcludingPregnantIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareNumeratorExcludingPregnants",
            EptsReportUtils.map(denominatorExcludingPregnantDefinition, mappings));
    CohortIndicator numeratorExcludingPregnantsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "PatientsNewlyEnrolledOnArtCareExcludingPregnants",
            EptsReportUtils.map(numeratorExcludingPregnantsDefinition, mappings));

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
        "D-NON-PREGNANT",
        "Denominator Non Pregnant",
        EptsReportUtils.map(denominatorExcludingPregnantIndicator, mappings),
        "age=15+");
    dataSetDefinition.addColumn(
        "D-CHILDREN",
        "Denominator children",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "D-ADULTS",
        "Denominator Adults",
        EptsReportUtils.map(denominatorIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "N-All", "Numerator: All", EptsReportUtils.map(numeratorIndicator, mappings), "");
    dataSetDefinition.addColumn(
        "N-PREGNANT",
        "Numerator Pregnant",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "state=PREGNANT");
    dataSetDefinition.addColumn(
        "N-BREASTFEEDING",
        "Numerator Breastfeendig",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "state=BREASTFEEDING");
    dataSetDefinition.addColumn(
        "N-NON-PREGNANT",
        "Numerator Non Pregnant",
        EptsReportUtils.map(numeratorExcludingPregnantsIndicator, mappings),
        "age=15+");
    dataSetDefinition.addColumn(
        "N-CHILDREN",
        "Numerator children",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "age=0-14");
    dataSetDefinition.addColumn(
        "N-ADULTS",
        "Numerator Adults",
        EptsReportUtils.map(numeratorIndicator, mappings),
        "age=15+");

    return dataSetDefinition;
  }
}
