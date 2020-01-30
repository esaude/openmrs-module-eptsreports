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

import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_FIFTY;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTEEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIVE_TO_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_FIVE_TO_FORTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_TO_FORTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ONE_TO_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TEN_TO_FOURTEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_FIVE_TO_THIRTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_TO_THRITY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_FIVE_TO_TWENTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_TO_TWENTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNDER_ONE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNKNOWN;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxCurrDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDataset extends BaseDataSet {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private TxCurrDimensions txCurrDimensions;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicatorDataSetDefinition constructTxCurrDataset(final boolean currentSpec) {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TX_CURR Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";

    final CohortDefinition txCurrCompositionCohort =
        this.txCurrCohortQueries.findPatientsWhoAreActiveOnART();

    final CohortIndicator txCurrIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreActiveOnART",
            EptsReportUtils.map(txCurrCompositionCohort, mappings));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dataSetDefinition.addDimension(
        "arvdispenseless3months",
        EptsReportUtils.map(
            this.txCurrDimensions.findPatientsOnArtOnArvDispenseForLessThan3Months(), mappings));

    dataSetDefinition.addDimension(
        "arvdispensefor3and5months",
        EptsReportUtils.map(
            this.txCurrDimensions.findPatientsOnArtOnArvDispenseBetween3And5Months(), mappings));

    dataSetDefinition.addDimension(
        "arvdispensefor6andmoremonths",
        EptsReportUtils.map(
            this.txCurrDimensions.findPatientsOnArtOnArvDispenseFor6OrMoreMonths(), mappings));

    dataSetDefinition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    dataSetDefinition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    dataSetDefinition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    dataSetDefinition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    this.addDimensions(
        dataSetDefinition,
        "endDate=${endDate}",
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    dataSetDefinition.addDimension(
        this.getName(Gender.MALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getName(Gender.MALE, AgeRange.UNKNOWN), Gender.MALE),
            ""));

    dataSetDefinition.addDimension(
        this.getName(Gender.FEMALE, AgeRange.UNKNOWN),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getName(Gender.FEMALE, AgeRange.UNKNOWN), Gender.FEMALE),
            ""));

    dataSetDefinition.addColumn(
        "C1All", "TX_CURR: Currently on ART", EptsReportUtils.map(txCurrIndicator, mappings), "");

    this.setPatientsOnrArvLess3MonthsColumnsDisagregations(
        dataSetDefinition, EptsReportUtils.map(txCurrIndicator, mappings));

    this.setPatientsOnrArvFor3and5MonthsColumnsDisagregations(
        dataSetDefinition, EptsReportUtils.map(txCurrIndicator, mappings));

    this.setPatientsOnrArvFor6OrMoreMonthsColumnsDisagregations(
        dataSetDefinition, EptsReportUtils.map(txCurrIndicator, mappings));

    this.addColums(
        dataSetDefinition,
        mappings,
        txCurrIndicator,
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    this.addColums(dataSetDefinition, "", txCurrIndicator, UNKNOWN);

    dataSetDefinition.addColumn(
        "C-MSM",
        "Homosexual",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "homosexual=homosexual");

    dataSetDefinition.addColumn(
        "C-PWID",
        "Drugs User",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "drug-user=drug-user");

    dataSetDefinition.addColumn(
        "C-PRI",
        "Prisioners",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "prisioner=prisioner");

    dataSetDefinition.addColumn(
        "C-FSW",
        "Sex Worker",
        EptsReportUtils.map(txCurrIndicator, mappings),
        "sex-worker=sex-worker");

    return dataSetDefinition;
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
      final String mappings,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      cohortIndicatorDataSetDefinition.addDimension(
          this.getName(Gender.MALE, range),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getName(Gender.MALE, range), range, Gender.MALE),
              mappings));

      cohortIndicatorDataSetDefinition.addDimension(
          this.getName(Gender.FEMALE, range),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getName(Gender.FEMALE, range), range, Gender.FEMALE),
              mappings));
    }
  }

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final AgeRange... rannges) {

    for (final AgeRange range : rannges) {

      final String maleName = this.getName(Gender.MALE, range);
      final String femaleName = this.getName(Gender.FEMALE, range);

      dataSetDefinition.addColumn(
          maleName,
          maleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          maleName + "=" + maleName);

      dataSetDefinition.addColumn(
          femaleName,
          femaleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          femaleName + "=" + femaleName);
    }
  }

  private String getName(final Gender gender, final AgeRange ageRange) {
    String name = "C-males-" + ageRange.getName() + "" + gender.getName();

    if (gender.equals(Gender.FEMALE)) {
      name = "C-females-" + ageRange.getName() + "" + gender.getName();
    }

    return name;
  }

  private void setPatientsOnrArvLess3MonthsColumnsDisagregations(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      Mapped<? extends CohortIndicator> indicator) {

    for (ColumnParameters column : getColumnsForArvDispenseDisagregatioins()) {
      String name = "C1" + "-" + column.getColumn();
      String label = "Patients On Arv Dispensation < 3 Months" + " (" + column.getLabel() + ")";
      String newDimension =
          (column.getDimensions().length() > 2)
              ? column.getDimensions() + "|arvdispenseless3months=arvdispenseless3months"
              : "arvdispenseless3months=arvdispenseless3months";
      dataSetDefinition.addColumn(name, label, indicator, newDimension);
    }
  }

  private void setPatientsOnrArvFor3and5MonthsColumnsDisagregations(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      Mapped<? extends CohortIndicator> indicator) {

    for (ColumnParameters column : getColumnsForArvDispenseDisagregatioins()) {
      String name = "C2" + "-" + column.getColumn();
      String label =
          "Patients On Arv Dispensation for 3 and 5 Months" + " (" + column.getLabel() + ")";
      String newDimension =
          (column.getDimensions().length() > 2)
              ? column.getDimensions() + "|arvdispensefor3and5months=arvdispensefor3and5months"
              : "arvdispensefor3and5months=arvdispensefor3and5months";
      dataSetDefinition.addColumn(name, label, indicator, newDimension);
    }
  }

  private void setPatientsOnrArvFor6OrMoreMonthsColumnsDisagregations(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      Mapped<? extends CohortIndicator> indicator) {

    for (ColumnParameters column : getColumnsForArvDispenseDisagregatioins()) {
      String name = "C3" + "-" + column.getColumn();
      String label =
          "Patients On Arv Dispensation for 6 or More Months" + " (" + column.getLabel() + ")";
      String newDimension =
          (column.getDimensions().length() > 2)
              ? column.getDimensions()
                  + "|arvdispensefor6andmoremonths=arvdispensefor6andmoremonths"
              : "arvdispensefor6andmoremonths=arvdispensefor6andmoremonths";
      dataSetDefinition.addColumn(name, label, indicator, newDimension);
    }
  }

  private List<ColumnParameters> getColumnsForArvDispenseDisagregatioins() {

    ColumnParameters under15M =
        new ColumnParameters("under15M", "under 15 year male", "gender=M|age=<15", "01");
    ColumnParameters above15M =
        new ColumnParameters("above15M", "above 15 year male", "gender=M|age=15+", "02");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "03");

    ColumnParameters under15F =
        new ColumnParameters("under15F", "under 15 year female", "gender=F|age=<15", "04");
    ColumnParameters above15F =
        new ColumnParameters("above15F", "above 15 year female", "gender=F|age=15+", "05");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "06");

    ColumnParameters total = new ColumnParameters("totals", "Totals", "", "07");

    return Arrays.asList(under15M, above15M, unknownM, under15F, above15F, unknownF, total);
  }
}
