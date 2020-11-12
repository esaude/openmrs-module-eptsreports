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

import org.openmrs.module.eptsreports.reporting.library.cohorts.TRFINCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TRFINDataset extends BaseDataSet {

  @Autowired private TRFINCohortQueries txTrfInCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicatorDataSetDefinition constructTxTRFIN() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("TRF_IN Data Set");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition txTRFINDefinition =
        this.txTrfInCohortQueries.getPatiensWhoAreTransferredIn();

    final CohortIndicator txTRFINIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "findPatientsWhoAreTransferredIn", EptsReportUtils.map(txTRFINDefinition, mappings));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

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
        "C1All", "TRF_IN: All Transferred In", EptsReportUtils.map(txTRFINIndicator, mappings), "");

    this.addColums(
        dataSetDefinition,
        mappings,
        txTRFINIndicator,
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

    dataSetDefinition.addColumn(
        "C-malesUnknownM",
        "unknownM",
        EptsReportUtils.map(txTRFINIndicator, mappings),
        this.getName(Gender.MALE, AgeRange.UNKNOWN)
            + "="
            + this.getName(Gender.MALE, AgeRange.UNKNOWN));

    dataSetDefinition.addColumn(
        "C-femalesUnknownF",
        "unknownF",
        EptsReportUtils.map(txTRFINIndicator, mappings),
        this.getName(Gender.FEMALE, AgeRange.UNKNOWN)
            + "="
            + this.getName(Gender.FEMALE, AgeRange.UNKNOWN));

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
}
