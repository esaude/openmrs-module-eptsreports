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
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.Eri4MonthsDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri4MonthsQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ErimType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Eri4MonthsDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private Eri4MonthsDimensions eri4MonthsDimensions;

  @Autowired private GenericCohortQueries genericCohortQueries;

  public DataSetDefinition constructEri4MonthsDataset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    final String cohortPeriodMappings =
        "cohortStartDate=${endDate-5m+1d},cohortEndDate=${endDate-4m},location=${location}";

    final String reportingPeriodMappings =
        "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.setName("ERI-4months Data Set");

    dataSetDefinition.addParameters(this.getParameters());

    dataSetDefinition.addDimension(
        "state",
        EptsReportUtils.map(this.eri4MonthsDimensions.getDimension(), cohortPeriodMappings));

    this.addColumns(
        dataSetDefinition,
        "01",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsAll",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.TOTAL)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "02",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients in treatment",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsInTreatment",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.IN_TREATMENT)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "03",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients dead",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsDead",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.DEAD)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "04",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients lftu",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsLftu",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.LFTU)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "05",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients in tranferred out",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsTransferredOut",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.TRANFERED_OUT)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "06",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients who stopped treatment",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsStoppedTreatment",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.SPTOPPED_TREATMENT)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    this.addColumns(
        dataSetDefinition,
        "07",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "all patients defaulters",
                EptsReportUtils.map(
                    this.genericCohortQueries.generalSql(
                        "patientsDefaulters",
                        Eri4MonthsQueries
                            .findPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120ForASpecificPatientType(
                                ErimType.DEFAULTER)),
                    "endDate=${endDate},location=${location}")),
            reportingPeriodMappings),
        this.get4MonthsRetentionColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> get4MonthsRetentionColumns() {

    final ColumnParameters allPatients =
        new ColumnParameters("initiated ART", "Initiated ART", "", "I");
    final ColumnParameters pregnantWoman =
        new ColumnParameters("pregnant woman", "Pregnant Woman", "state=PREGNANT", "I");
    final ColumnParameters brestfedding =
        new ColumnParameters("breastfeeding", "Breastfeeding", "state=BREASTFEEDING", "I");
    final ColumnParameters children =
        new ColumnParameters("children", "Children", "state=CHILDREN", "I");
    final ColumnParameters adult = new ColumnParameters("adult", "Adult", "state=ADULT", "I");

    return Arrays.asList(allPatients, pregnantWoman, brestfedding, children, adult);
  }

  private void addColumns(
      final CohortIndicatorDataSetDefinition definition,
      final String columNumber,
      final Mapped<CohortIndicator> indicator,
      final List<ColumnParameters> columns) {

    int position = 1;

    for (final ColumnParameters column : columns) {

      final String name = column.getColumn() + "" + position + "-" + columNumber;
      final String label = column.getLabel() + "(" + name + ")";

      definition.addColumn(name, label, indicator, column.getDimensions());

      position++;
    }
  }
}
