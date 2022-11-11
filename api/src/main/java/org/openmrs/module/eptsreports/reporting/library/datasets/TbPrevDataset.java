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

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TbPrevDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("TB PREV Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "art-status",
        EptsReportUtils.map(
            eptsCommonDimension.getArtStatusDimension(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    CohortIndicator denominatorIndicator =
        eptsGeneralIndicator.getIndicator(
            "Denominator Total",
            EptsReportUtils.map(
                tbPrevCohortQueries.getDenominator(),
                "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    CohortIndicator numeratorIndicator =
        eptsGeneralIndicator.getIndicator(
            "Numerator Disaggregations",
            EptsReportUtils.map(
                tbPrevCohortQueries.getNumerator(),
                "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    dsd.addColumn(
        "NUM-TOTAL", "Numerator Total", EptsReportUtils.map(numeratorIndicator, mappings), "");
    dsd.addColumn(
        "DEN-TOTAL", "Denominator Total", EptsReportUtils.map(denominatorIndicator, mappings), "");
    addRow(
        dsd,
        "R01",
        "Numerator Disaggregations",
        EptsReportUtils.map(numeratorIndicator, mappings),
        getColumns());
    addRow(
        dsd,
        "R02",
        "Denominator Disaggregations",
        EptsReportUtils.map(denominatorIndicator, mappings),
        getColumns());
    return dsd;
  }

  public List<ColumnParameters> getColumns() {
    return Arrays.asList(
        new ColumnParameters(
            "PM0-14", "PM0-14", "art-status=previously-on-art|gender=M|age=<15", "01"),
        new ColumnParameters(
            "PM15+", "PM15+", "art-status=previously-on-art|gender=M|age=15+", "02"),
        new ColumnParameters(
            "PMUNK", "PMUNK", "art-status=previously-on-art|gender=M|age=UK", "03"),
        new ColumnParameters(
            "PF0-14", "PF0-14", "art-status=previously-on-art|gender=F|age=<15", "04"),
        new ColumnParameters(
            "PF15+", "PF15+", "art-status=previously-on-art|gender=F|age=15+", "05"),
        new ColumnParameters(
            "PFUNK", "PFUNK", "art-status=previously-on-art|gender=F|age=UK", "06"),
        new ColumnParameters("NM0-14", "NM0-14", "art-status=new-on-art|gender=M|age=<15", "07"),
        new ColumnParameters("NM15+", "NM15+", "art-status=new-on-art|gender=M|age=15+", "08"),
        new ColumnParameters("NMUNK", "NMUNK", "art-status=new-on-art|gender=M|age=UK", "09"),
        new ColumnParameters("NF0-14", "NF0-14", "art-status=new-on-art|gender=F|age=<15", "10"),
        new ColumnParameters("NF15+", "NF15+", "art-status=new-on-art|gender=F|age=15+", "11"),
        new ColumnParameters("NFUNK", "NFUNK", "art-status=new-on-art|gender=F|age=UK", "12"));
  }
}
