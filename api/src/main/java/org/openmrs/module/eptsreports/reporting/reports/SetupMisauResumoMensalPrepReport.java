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
package org.openmrs.module.eptsreports.reporting.reports;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.LocationDataSetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.MisauResumoMensalPrepDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupMisauResumoMensalPrepReport extends EptsDataExportManager {

  @Autowired private TxRttDataset txRttDataset;

  @Autowired private MisauResumoMensalPrepDataset misauResumoMensalPrepDataset;

  @Autowired protected GenericCohortQueries genericCohortQueries;
  @Autowired private DatinCodeDataSet DatinCodeDataSet;

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "604b4df7-6a84-493f-9d52-8bc00133af16";
  }

  @Override
  public String getExcelDesignUuid() {
    return "00fd06c7-cae4-41cd-a761-4687466ffe32";
  }

  @Override
  public String getName() {
    return "Resumo Mensal da PrEP – MISAU";
  }

  @Override
  public String getDescription() {
    return "Este relatório apresenta os dados do resumo mensal da PrEP da Unidade Sanitária para o Programa do ITS-HIV/SIDA, provenientes da ferramenta 'Ficha de Seguimento da Profilaxia Pré- Exposição' e outras existentes no sistema";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    final ReportDefinition reportDefinition = new ReportDefinition();

    reportDefinition.setUuid(this.getUuid());
    reportDefinition.setName(this.getName());
    reportDefinition.setDescription(this.getDescription());
    reportDefinition.setParameters(this.txRttDataset.getParameters());

    reportDefinition.addDataSetDefinition(
        "RM", Mapped.mapStraightThrough(this.misauResumoMensalPrepDataset.constructDataset()));

    reportDefinition.addDataSetDefinition(
        "D",
        Mapped.mapStraightThrough(this.DatinCodeDataSet.constructDataset(this.getParameters())));

    reportDefinition.addDataSetDefinition(
        "HF", mapStraightThrough(new LocationDataSetDefinition()));

    reportDefinition.setBaseCohortDefinition(
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "baseCohortQuery", BaseQueries.getBaseCohortQuery()),
            "endDate=${endDate},location=${location}"));

    return reportDefinition;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(final ReportDefinition reportDefinition) {
    ReportDesign reportDesign = null;
    try {
      reportDesign =
          this.createXlsReportDesign(
              reportDefinition,
              "Misau_Resumo_Mensal_PrEP.xls",
              "Resumo Mensal da PrEP – MISAU",
              this.getExcelDesignUuid(),
              null);
      final Properties props = new Properties();
      props.put("sortWeight", "5000");
      reportDesign.setProperties(props);
    } catch (final IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(reportDesign);
  }
}
