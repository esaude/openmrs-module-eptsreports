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
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TbPrevDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private AgeCohortQueries ageCohortQueries;

  public DataSetDefinition constructDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("TB PREV Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension("age", EptsReportUtils.map(getAgeDimension(), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "art-status",
        EptsReportUtils.map(
            getArtStatusDimension(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
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
    addRow(
        dsd,
        "R01",
        "Numerator Disaggregations",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Disaggregations",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getNumerator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        getColumns());
    addRow(
        dsd,
        "R02",
        "Denominator Disaggregations",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominator Disaggregations",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getDenominator(),
                    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}")),
            mappings),
        getColumns());
    return dsd;
  }

  public CohortDefinitionDimension getAgeDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
    dim.setName("TB-PREV age dimension");
    dim.addCohortDefinition(
        "0-14",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("0-14", 0, 14), "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "15+",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("15+", 15, 200),
            "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "UNK", EptsReportUtils.map(ageCohortQueries.createUnknownAgeCohort(), ""));
    return dim;
  }

  public CohortDefinitionDimension getArtStatusDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("TB-PREV art-status dimension");
    dim.addCohortDefinition(
        "new-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getNewOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "previously-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPreviouslyOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    return dim;
  }

  public List<ColumnParameters> getColumns() {
    return Arrays.asList(
        new ColumnParameters(
            "PM0-14", "PM0-14", "art-status=previously-on-art|gender=M|age=0-14", "01"),
        new ColumnParameters(
            "PM15+", "PM15+", "art-status=previously-on-art|gender=M|age=15+", "02"),
        new ColumnParameters(
            "PMUNK", "PMUNK", "art-status=previously-on-art|gender=M|age=UNK", "03"),
        new ColumnParameters(
            "PF0-14", "PF0-14", "art-status=previously-on-art|gender=F|age=0-14", "04"),
        new ColumnParameters(
            "PF15+", "PF15+", "art-status=previously-on-art|gender=F|age=15+", "05"),
        new ColumnParameters(
            "PFUNK", "PFUNK", "art-status=previously-on-art|gender=F|age=UNK", "06"),
        new ColumnParameters("NM0-14", "NM0-14", "art-status=new-on-art|gender=M|age=0-14", "07"),
        new ColumnParameters("NM15+", "NM15+", "art-status=new-on-art|gender=M|age=15+", "08"),
        new ColumnParameters("NMUNK", "NMUNK", "art-status=new-on-art|gender=M|age=UNK", "09"),
        new ColumnParameters("NF0-14", "NF0-14", "art-status=new-on-art|gender=F|age=0-14", "10"),
        new ColumnParameters("NF15+", "NF15+", "art-status=new-on-art|gender=F|age=15+", "11"),
        new ColumnParameters("NFUNK", "NFUNK", "art-status=new-on-art|gender=F|age=UNK", "12"));
  }
}
