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

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoMensalAandBdisaggregations;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MisauKeyPopDataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;
  private KeyPopulationDimension keyPopulationDimension;

  private TxNewCohortQueries txNewCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;
  private TxRTTCohortQueries txRTTCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public MisauKeyPopDataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations,
      TxNewCohortQueries txNewCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      TxRTTCohortQueries txRTTCohortQueries,
      KeyPopulationDimension keyPopulationDimension) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.txNewCohortQueries = txNewCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.keyPopulationDimension = keyPopulationDimension;
    this.txRTTCohortQueries = txRTTCohortQueries;
  }

  public DataSetDefinition constructMisauKeyPopDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Misau Key Pop Dataset");
    dataSetDefinition.addParameters(getParameters());
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingEndDate = "endDate=${endDate},location=${location}";

    // CohortDefinition patientsWhoExperiencedIIT = txRTTCohortQueries.getTxRTTIndicatorNumerator();

    // final CohortDefinition patientEnrolledInART = this.txNewCohortQueries
    // .getTxNewCompositionCohort("patientEnrolledInART");
    //
    // final CohortIndicator patientEnrolledInHIVStartedARTIndicator =
    // this.eptsGeneralIndicator.getIndicator(
    // "patientNewlyEnrolledInHIVIndicator",
    // EptsReportUtils.map(patientEnrolledInART,
    // mappings));
    //
    // final CohortDefinition patientCurrentOnART =
    // this.txCurrCohortQueries.findPatientsWhoAreActiveOnART();

    // final CohortIndicator patientCurrentOnARTIndicator =
    // this.eptsGeneralIndicator.getIndicator(
    // "findPatientsWhoAreActiveOnART", EptsReportUtils.map(patientCurrentOnART,
    // mappingEndDate));

    //    final CohortIndicator rttNumeratorIndicator =
    //        this.eptsGeneralIndicator.getIndicator(
    //            "findPatientsWhoAreInIIT", EptsReportUtils.map(patientsWhoExperiencedIIT,
    // mappings));
    //
    // dataSetDefinition.addDimension("gender", map(eptsCommonDimension.gender(),
    // ""));
    // dataSetDefinition.addDimension(
    // "age", map(eptsCommonDimension.age(ageDimensionCohort),
    // "effectiveDate=${endDate}"));
    // dataSetDefinition.addDimension(
    // "homosexual",
    // EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(),
    // mappings));
    // dataSetDefinition.addDimension(
    // "drug-user",
    // EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(),
    // mappings));
    // dataSetDefinition.addDimension(
    // "prisioner",
    // EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(),
    // mappings));
    // dataSetDefinition.addDimension(
    // "sex-worker",
    // EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(),
    // mappings));
    //
    // dataSetDefinition.addColumn(
    // "E1",
    // "Numero adultos que iniciaram TARV - Total",
    // EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
    // "age=15+");
    //
    // dataSetDefinition.addColumn(
    // "E1-MSM",
    // "Homosexual",
    // EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
    // "age=15+|homosexual=homosexual");
    //
    // dataSetDefinition.addColumn(
    // "E1-PWID",
    // "Drugs User",
    // EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
    // "age=15+|drug-user=drug-user");
    //
    // dataSetDefinition.addColumn(
    // "E1-PRI",
    // "Prisioners",
    // EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
    // "age=15+|prisioner=prisioner");
    //
    // dataSetDefinition.addColumn(
    // "E1-FSW",
    // "Sex Worker",
    // EptsReportUtils.map(patientEnrolledInHIVStartedARTIndicator, mappings),
    // "age=15+|sex-worker=sex-worker");
    //
    // dataSetDefinition.addColumn(
    // "E2",
    // "Numero adultos actualmente em TARV - Total",
    // EptsReportUtils.map(patientCurrentOnARTIndicator, mappingEndDate),
    // "age=15+");

    //    dataSetDefinition.addColumn(
    //        "E3", "RTT- Total", EptsReportUtils.map(rttNumeratorIndicator, mappings), "");

    // dataSetDefinition.addColumn(
    // "E2-MSM",
    // "Homosexual",
    // EptsReportUtils.map(patientCurrentOnARTIndicator, mappingEndDate),
    // "age=15+|homosexual=homosexual");
    //
    // dataSetDefinition.addColumn(
    // "E2-PWID",
    // "Drugs User",
    // EptsReportUtils.map(patientCurrentOnARTIndicator, mappingEndDate),
    // "age=15+|drug-user=drug-user");
    //
    // dataSetDefinition.addColumn(
    // "E2-PRI",
    // "Prisioners",
    // EptsReportUtils.map(patientCurrentOnARTIndicator, mappingEndDate),
    // "age=15+|prisioner=prisioner");
    //
    // dataSetDefinition.addColumn(
    // "E2-FSW",
    // "Sex Worker",
    // EptsReportUtils.map(patientCurrentOnARTIndicator, mappingEndDate),
    // "age=15+|sex-worker=sex-worker");

    return dataSetDefinition;
  }
}
