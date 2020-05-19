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

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset extends BaseDataSet {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicatorDataSetDefinition constructTxCurrDataset(boolean currentSpec) {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_CURR Data Set");
    dataSetDefinition.addParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    String keyPopMappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    String dispMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    CohortDefinitionDimension keyPopsDimension = eptsCommonDimension.getKeyPopsDimension();
    CohortDefinitionDimension qtyDimension = eptsCommonDimension.getDispensingQuantityDimension();
    dataSetDefinition.addDimension("keypop", EptsReportUtils.map(keyPopsDimension, keyPopMappings));
    dataSetDefinition.addDimension("disp", EptsReportUtils.map(qtyDimension, dispMappings));

    CohortDefinition txCurrCompositionCohort =
        txCurrCohortQueries.getTxCurrCompositionCohort("compositionCohort", currentSpec);
    CohortIndicator txCurrIndicator =
        eptsGeneralIndicator.getIndicator(
            "patientInYearRangeEnrolledInHIVStartedARTIndicator",
            EptsReportUtils.map(
                txCurrCompositionCohort, "onOrBefore=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "C1",
        "Children",
        EptsReportUtils.map(txCurrIndicator, mappings),
        getColumnsForChildren());

    addRow(
        dataSetDefinition,
        "C2",
        "Adults",
        EptsReportUtils.map(txCurrIndicator, mappings),
        getColumnsForAdults());

    dataSetDefinition.addColumn(
        "C1All", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");

    dataSetDefinition.addColumn(
        "PID",
        "TX_CURR: People who inject drugs",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "MSM",
        "TX_CURR: Men who have sex with men",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "CSW",
        "TX_CURR: Female sex workers",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "PRI",
        "TX_CURR: People in prison and other closed settings",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "keypop=PRI");

    dataSetDefinition.addColumn(
        "C3All",
        "TX_CURR: <3 months of ARVs dispense",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "disp=<3m");

    addRow(
        dataSetDefinition,
        "C3",
        "<3 Months",
        EptsReportUtils.map(txCurrIndicator, mappings),
        getDispensationColumnsLessThan3Months());

    dataSetDefinition.addColumn(
        "C4All",
        "TX_CURR: 3-5 months of ARVs dispense",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "disp=3-5m");

    addRow(
        dataSetDefinition,
        "C4",
        "3-5 Months",
        EptsReportUtils.map(txCurrIndicator, mappings),
        getDispensationColumns3To5Months());

    dataSetDefinition.addColumn(
        "C5All",
        "TX_CURR: More than 6 months of ARVs dispense",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "disp=>6m");

    addRow(
        dataSetDefinition,
        "C5",
        "6 Months",
        EptsReportUtils.map(txCurrIndicator, mappings),
        getDispensationColumnsMoreThan6Months());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumnsForChildren() {
    ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "M1");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "M2");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "M3");
    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "F1");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "F2");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "F3");

    return Arrays.asList(under1M, oneTo4M, fiveTo9M, under1F, oneTo4F, fiveTo9F);
  }

  private List<ColumnParameters> getColumnsForAdults() {
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "UNKM");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "M4");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "M5");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "M6");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "M7");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "M8");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "M9");
    ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "M10");
    ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "M11");
    ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "M12");

    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "UNKF");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "F4");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "F5");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "F6");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "F7");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "F8");
    ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "F9");
    ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "F10");
    ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "F11");
    ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "F12");
    ColumnParameters unknown = new ColumnParameters("unknown", "Unknown age", "age=UK", "UNK");

    return Arrays.asList(
        unknownM,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownF,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F,
        unknown);
  }

  private List<ColumnParameters> getDispensationColumnsLessThan3Months() {
    ColumnParameters under15M =
        new ColumnParameters(
            "under15M", "under 15 years male", "gender=M|age=<15|disp=<3m", "under15M");
    ColumnParameters plus15M =
        new ColumnParameters(
            "plus15M", "more than 15 years male", "gender=M|age=15+|disp=<3m", "plus15M");
    ColumnParameters unkM =
        new ColumnParameters("unkM", "more than 15 years male", "gender=M|age=UK|disp=<3m", "unkM");
    ColumnParameters under15F =
        new ColumnParameters(
            "under15F", "under 15 years male", "gender=F|age=<15|disp=<3m", "under15F");
    ColumnParameters plus15F =
        new ColumnParameters(
            "plus15F", "more than 15 years male", "gender=F|age=15+|disp=<3m", "plus15F");
    ColumnParameters unkF =
        new ColumnParameters("unkF", "more than 15 years male", "gender=F|age=UK|disp=<3m", "unkF");

    return Arrays.asList(under15M, plus15M, unkM, under15F, plus15F, unkF);
  }

  private List<ColumnParameters> getDispensationColumns3To5Months() {
    ColumnParameters under15M =
        new ColumnParameters(
            "under15M", "under 15 years male", "gender=M|age=<15|disp=3-5m", "under15M");
    ColumnParameters plus15M =
        new ColumnParameters(
            "plus15M", "more than 15 years male", "gender=M|age=15+|disp=3-5m", "plus15M");
    ColumnParameters unkM =
        new ColumnParameters(
            "unkM", "more than 15 years male", "gender=M|age=UK|disp=3-5m", "unkM");
    ColumnParameters under15F =
        new ColumnParameters(
            "under15F", "under 15 years male", "gender=F|age=<15|disp=3-5m", "under15F");
    ColumnParameters plus15F =
        new ColumnParameters(
            "plus15F", "more than 15 years male", "gender=F|age=15+|disp=3-5m", "plus15F");
    ColumnParameters unkF =
        new ColumnParameters(
            "unkF", "more than 15 years male", "gender=F|age=UK|disp=3-5m", "unkF");

    return Arrays.asList(under15M, plus15M, unkM, under15F, plus15F, unkF);
  }

  private List<ColumnParameters> getDispensationColumnsMoreThan6Months() {
    ColumnParameters under15M =
        new ColumnParameters(
            "under15M", "under 15 years male", "gender=M|age=<15|disp=>6m", "under15M");
    ColumnParameters plus15M =
        new ColumnParameters(
            "plus15M", "more than 15 years male", "gender=M|age=15+|disp=>6m", "plus15M");
    ColumnParameters unkM =
        new ColumnParameters("unkM", "more than 15 years male", "gender=M|age=UK|disp=>6m", "unkM");
    ColumnParameters under15F =
        new ColumnParameters(
            "under15F", "under 15 years male", "gender=F|age=<15|disp=>6m", "under15F");
    ColumnParameters plus15F =
        new ColumnParameters(
            "plus15F", "more than 15 years male", "gender=F|age=15+|disp=>6m", "plus15F");
    ColumnParameters unkF =
        new ColumnParameters("unkF", "more than 15 years male", "gender=F|age=UK|disp=>6m", "unkF");

    return Arrays.asList(under15M, plus15M, unkM, under15F, plus15F, unkF);
  }
}
