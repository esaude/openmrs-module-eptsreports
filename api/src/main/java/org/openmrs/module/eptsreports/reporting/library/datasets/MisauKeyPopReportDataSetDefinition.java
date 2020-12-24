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

import org.openmrs.module.eptsreports.reporting.library.cohorts.MisauKeyPopReportCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoMensalAandBdisaggregations;
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
public class MisauKeyPopReportDataSetDefinition extends BaseDataSet {

  private EptsCommonDimension eptsCommonDimension;

  private EptsGeneralIndicator eptsGeneralIndicator;
  private KeyPopulationDimension keyPopulationDimension;

  private TxNewCohortQueries txNewCohortQueries;

  private MisauKeyPopReportCohortQueries misauKeyPopReportCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public MisauKeyPopReportDataSetDefinition(
      EptsCommonDimension eptsCommonDimension,
      EptsGeneralIndicator eptsGeneralIndicator,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations,
      TxNewCohortQueries txNewCohortQueries,
      KeyPopulationDimension keyPopulationDimension,
      MisauKeyPopReportCohortQueries misauKeyPopReportCohortQueries) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.txNewCohortQueries = txNewCohortQueries;
    this.keyPopulationDimension = keyPopulationDimension;
    this.misauKeyPopReportCohortQueries = misauKeyPopReportCohortQueries;
  }

  public DataSetDefinition constructMisauKeyPopDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Misau Key Pop Dataset");
    dataSetDefinition.addParameters(getParameters());
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingEndDate = "endDate=${endDate},location=${location}";

    CohortDefinition txNewPatientsEnrolledInARTDefinition =
        this.txNewCohortQueries.getTxNewCompositionCohort("patientEnrolledInART");

    CohortDefinition txCurrPatientCurrentOnARTDefinition =
        this.misauKeyPopReportCohortQueries.getPatientsCurrentOnTarvMisauDefinition();

    CohortDefinition txCurrWithVLResultDefinition =
        this.misauKeyPopReportCohortQueries.getPatientsCurrentlyOnTarvWhoReceicevedVLResults();
    CohortDefinition currentlyOnTarvWhitSuppressedVLResultsDefinition =
        this.misauKeyPopReportCohortQueries.getPatientsCurrentlyOnTarvWhitSuppressedVLResults();
    CohortDefinition patientsCoort12StartArtDefinition =
        this.misauKeyPopReportCohortQueries.getPatientsCoort12StartArt();
    CohortDefinition patientsCoort12CurrentOnArtDefinition =
        this.misauKeyPopReportCohortQueries.getPatientsCoort12CurrentOnArt();

    final CohortIndicator txNewPatientEnrolledInArtIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientNewlyEnrolledInHIVIndicator",
            EptsReportUtils.map(txNewPatientsEnrolledInARTDefinition, mappings));

    final CohortIndicator txCurrPatientCurrentOnARTIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsCurrentlyEnrolledInArt",
            EptsReportUtils.map(txCurrPatientCurrentOnARTDefinition, mappingEndDate));

    final CohortIndicator txCurrWithVLResultIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsCurrentlyEnrolledInArt",
            EptsReportUtils.map(txCurrWithVLResultDefinition, mappings));

    final CohortIndicator patientsCoort12StartArtIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsCoort12StartArt",
            EptsReportUtils.map(patientsCoort12StartArtDefinition, mappings));

    final CohortIndicator patientsCoort12CurrentOnArtIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsCoort12CurrentOnArt",
            EptsReportUtils.map(patientsCoort12CurrentOnArtDefinition, mappings));

    final CohortIndicator currentlyOnTarvWhitSuppressedVLResultsIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "currentlyOnTarvWhitSuppressedVLResults",
            EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsDefinition, mappings));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
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

    dataSetDefinition.addColumn(
        "E1",
        "Nr. Adultos que iniciaram TARV - Total",
        EptsReportUtils.map(txNewPatientEnrolledInArtIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "E1-MSM",
        "Homosexual",
        EptsReportUtils.map(txNewPatientEnrolledInArtIndicator, mappings),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E1-PWID",
        "Drugs User",
        EptsReportUtils.map(txNewPatientEnrolledInArtIndicator, mappings),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E1-PRI",
        "Prisioners",
        EptsReportUtils.map(txNewPatientEnrolledInArtIndicator, mappings),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E1-FSW",
        "Sex Worker",
        EptsReportUtils.map(txNewPatientEnrolledInArtIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "E2",
        "Nr. Adultos actualmente em TARV - Total",
        EptsReportUtils.map(txCurrPatientCurrentOnARTIndicator, mappingEndDate),
        "age=15+");

    dataSetDefinition.addColumn(
        "E2-MSM",
        "Homosexual",
        EptsReportUtils.map(txCurrPatientCurrentOnARTIndicator, mappingEndDate),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E2-PWID",
        "Drugs User",
        EptsReportUtils.map(txCurrPatientCurrentOnARTIndicator, mappingEndDate),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E2-PRI",
        "Prisioners",
        EptsReportUtils.map(txCurrPatientCurrentOnARTIndicator, mappingEndDate),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E2-FSW",
        "Sex Worker",
        EptsReportUtils.map(txCurrPatientCurrentOnARTIndicator, mappingEndDate),
        "age=15+|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "E3",
        "Nr. Adultos com um teste de Carga Viral (CV) Durante o trimestre - Total",
        EptsReportUtils.map(txCurrWithVLResultIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "E3-MSM",
        "Homosexual",
        EptsReportUtils.map(txCurrWithVLResultIndicator, mappings),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E3-PWID",
        "Drugs User",
        EptsReportUtils.map(txCurrWithVLResultIndicator, mappings),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E3-PRI",
        "Prisioners",
        EptsReportUtils.map(txCurrWithVLResultIndicator, mappings),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E3-FSW",
        "Sex Worker",
        EptsReportUtils.map(txCurrWithVLResultIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    dataSetDefinition.addColumn(
        "E4",
        "Nr. Adultos com supressao de CV (<1000 cópias/mL) - Total",
        EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "E4-MSM",
        "Homosexual",
        EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsIndicator, mappings),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E4-PWID",
        "Drugs User",
        EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsIndicator, mappings),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E4-PRI",
        "Prisioners",
        EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsIndicator, mappings),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E4-FSW",
        "Sex Worker",
        EptsReportUtils.map(currentlyOnTarvWhitSuppressedVLResultsIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    //
    dataSetDefinition.addColumn(
        "E5",
        "Nr. Adultos na coorte 12 - inicio de TARV - Total",
        EptsReportUtils.map(patientsCoort12StartArtIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "E5-MSM",
        "Homosexual",
        EptsReportUtils.map(patientsCoort12StartArtIndicator, mappings),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E5-PWID",
        "Drugs User",
        EptsReportUtils.map(patientsCoort12StartArtIndicator, mappings),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E5-PRI",
        "Prisioners",
        EptsReportUtils.map(patientsCoort12StartArtIndicator, mappings),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E5-FSW",
        "Sex Worker",
        EptsReportUtils.map(patientsCoort12StartArtIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    //
    dataSetDefinition.addColumn(
        "E6",
        "Nr. Adultos na coorte 12 meses - Activos em TARV - Total",
        EptsReportUtils.map(patientsCoort12CurrentOnArtIndicator, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "E6-MSM",
        "Homosexual",
        EptsReportUtils.map(patientsCoort12CurrentOnArtIndicator, mappings),
        "age=15+|homosexual=homosexual");

    dataSetDefinition.addColumn(
        "E6-PWID",
        "Drugs User",
        EptsReportUtils.map(patientsCoort12CurrentOnArtIndicator, mappings),
        "age=15+|drug-user=drug-user");

    dataSetDefinition.addColumn(
        "E6-PRI",
        "Prisioners",
        EptsReportUtils.map(patientsCoort12CurrentOnArtIndicator, mappings),
        "age=15+|prisioner=prisioner");

    dataSetDefinition.addColumn(
        "E6-FSW",
        "Sex Worker",
        EptsReportUtils.map(patientsCoort12CurrentOnArtIndicator, mappings),
        "age=15+|sex-worker=sex-worker");

    return dataSetDefinition;
  }
}
