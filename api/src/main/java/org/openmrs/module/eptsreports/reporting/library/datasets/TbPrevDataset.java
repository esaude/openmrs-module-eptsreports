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
package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TbPrevDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TbPrevDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private TbPrevDimensions tbPrevDimensions;

  public DataSetDefinition constructTbPrevDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("TB PREV Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age", EptsReportUtils.map(tbPrevDimensions.getAgeDimension(), "effectiveDate=${endDate}"));
    dsd.addColumn(
        "NUM-TOTAL",
        "Numerator Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Total",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getNumerator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NUM-DIM-01",
        "Numerator Dimension 1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Dimension 1",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getNumeratorDimension1(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NUM-DIM-02",
        "Numerator Dimension 2",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Dimension 2",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getNumeratorDimesion2(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "DEN-TOTAL",
        "Denominator Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Total",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getDenominator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "DEN-DIM-01",
        "Denominator Dimension 1",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Dimension 1",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getDenominatorDimension1(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "DEN-DIM-02",
        "Denominator Dimension 2",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Dimension 2",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getDenominatorDimension2(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    addRow(
        dsd,
        "R01",
        "Ages of Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Ages of Numerator",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getNumerator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        tbPrevDimensions.getAgeColumns());
    addRow(
        dsd,
        "R02",
        "Ages of Denominator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Ages of Denominator",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getDenominator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        tbPrevDimensions.getAgeColumns());
    return dsd;
  }
}
