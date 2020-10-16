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
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxNewDataset extends BaseDataSet {

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("txNewAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTxNewDataset() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_NEW Data Set");
    dataSetDefinition.addParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition patientEnrolledInART =
        txNewCohortQueries.getTxNewCompositionCohort("patientEnrolledInART");
    CohortIndicator patientEnrolledInHIVStartedARTIndicator =
        eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(
                patientEnrolledInART,
                "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    dataSetDefinition.addDimension(
        "maternity", EptsReportUtils.map(eptsCommonDimension.maternityDimension(), mappings));
    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age", EptsReportUtils.map(eptsCommonDimension.age(ageDimensionCohort), mappings));
    String keyPopMappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    dataSetDefinition.addDimension(
        "keypop", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), keyPopMappings));

    dataSetDefinition.addColumn(
        "1All",
        "TX_NEW: New on ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "");

    dataSetDefinition.addColumn(
        "ANC",
        "TX_NEW: Breastfeeding Started ART",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "maternity=breastfeeding");

    dataSetDefinition.addColumn(
        "PID",
        "TX_NEW: People who inject drugs",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "keypop=PID");

    dataSetDefinition.addColumn(
        "MSM",
        "TX_NEW: Men who have sex with men",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "keypop=MSM");

    dataSetDefinition.addColumn(
        "CSW",
        "TX_NEW: Female sex workers",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "keypop=CSW");

    dataSetDefinition.addColumn(
        "PRI",
        "TX_NEW: People in prison and other closed settings",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        "keypop=PRI");

    addRow(
        dataSetDefinition,
        "males",
        "Males",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        getMaleColumns());

    addRow(
        dataSetDefinition,
        "females",
        "Females",
        EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
        getFemaleColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getMaleColumns() {
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "unknownM");
    ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "under1M");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "oneTo4M");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "fiveTo9M");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "tenTo14M");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "fifteenTo19M");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "twentyTo24M");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "twenty5To29M");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "thirtyTo34M");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "thirty5To39M");
    ColumnParameters fortyTo44M =
        new ColumnParameters("fortyTo44M", "40 - 44 male", "gender=M|age=40-44", "fortyTo44M");
    ColumnParameters forty5To49M =
        new ColumnParameters("forty5To49M", "45 - 49 male", "gender=M|age=45-49", "forty5To49M");
    ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "above50M");

    return Arrays.asList(
        unknownM,
        under1M,
        oneTo4M,
        fiveTo9M,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        fortyTo44M,
        forty5To49M,
        above50M);
  }

  private List<ColumnParameters> getFemaleColumns() {
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "unknownF");
    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "under1F");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "oneTo4F");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "fiveTo9F");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "tenTo14F");
    ColumnParameters fifteenTo19F =
        new ColumnParameters(
            "fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "fifteenTo19F");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "twentyTo24F");
    ColumnParameters twenty5To29F =
        new ColumnParameters(
            "twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "twenty5To29F");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "thirtyTo34F");
    ColumnParameters thirty5To39F =
        new ColumnParameters(
            "thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "thirty5To39F");
    ColumnParameters fortyTo44F =
        new ColumnParameters("fortyTo44F", "40 - 44 female", "gender=F|age=40-44", "fortyTo44F");
    ColumnParameters forty5To49F =
        new ColumnParameters("forty5To49F", "45 - 49 female", "gender=F|age=45-49", "forty5To49F");
    ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "above50F");

    return Arrays.asList(
        unknownF,
        under1F,
        oneTo4F,
        fiveTo9F,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        fortyTo44F,
        forty5To49F,
        above50F);
  }
}
