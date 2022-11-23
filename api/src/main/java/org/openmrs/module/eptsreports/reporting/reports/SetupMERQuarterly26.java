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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.CXCASCRNDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.CXCASCRNPositiveDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.DatimCodeDatasetDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.PrepCtDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.PrepNewDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TXCXCADataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TransferredInDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxMlDataset25;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRTTPLHIVDataset;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxRttDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupMERQuarterly26 extends EptsDataExportManager {

  private TxPvlsDataset txPvlsDataset;

  private TxNewDataset txNewDataset;

  private TxCurrDataset txCurrDataset;

  private TxMlDataset25 txMlDataset25;

  private TxRttDataset txRttDataset;

  private GenericCohortQueries genericCohortQueries;

  private TransferredInDataset transferredInDataset;

  private TxRTTPLHIVDataset txRTTPLHIVDateset;

  private PrepNewDataset prepNewDataset;

  private PrepCtDataset prepCtDataset;

  @Autowired
  public SetupMERQuarterly26(
      TxPvlsDataset txPvlsDataset,
      TxNewDataset txNewDataset,
      TxCurrDataset txCurrDataset,
      TxMlDataset25 txMlDataset25,
      TxRttDataset txRttDataset,
      GenericCohortQueries genericCohortQueries,
      TransferredInDataset transferredInDataset,
      TxRTTPLHIVDataset txRTTPLHIVDateset,
      CXCASCRNDataset cxcascrnDataset,
      CXCASCRNPositiveDataset cxcascrnPositiveDataset,
      TXCXCADataset txcxcaDataset,
      PrepNewDataset prepNewDataset,
      PrepCtDataset prepCtDataset) {
    this.txPvlsDataset = txPvlsDataset;
    this.txNewDataset = txNewDataset;
    this.txCurrDataset = txCurrDataset;
    this.txMlDataset25 = txMlDataset25;
    this.txRttDataset = txRttDataset;
    this.genericCohortQueries = genericCohortQueries;
    this.transferredInDataset = transferredInDataset;
    this.txRTTPLHIVDateset = txRTTPLHIVDateset;
    this.prepNewDataset = prepNewDataset;
    this.prepCtDataset = prepCtDataset;
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

  @Override
  public String getUuid() {
    return "589327cd-1a84-11eb-b9a1-0242ac120002";
  }

  @Override
  public String getExcelDesignUuid() {
    return "628dbbdc-1a84-11eb-b9a1-0242ac120002";
  }

  @Override
  public String getName() {
    return "PEPFAR MER 2.6 Quarterly";
  }

  @Override
  public String getDescription() {
    return "MER 2.6 Quarterly Report";
  }

  @Override
  public ReportDefinition constructReportDefinition() {
    ReportDefinition rd = new ReportDefinition();
    rd.setUuid(getUuid());
    rd.setName(getName());
    rd.setDescription(getDescription());
    rd.setParameters(txPvlsDataset.getParameters());
    rd.addDataSetDefinition("N", Mapped.mapStraightThrough(txNewDataset.constructTxNewDataset()));
    rd.addDataSetDefinition(
        "C", Mapped.mapStraightThrough(txCurrDataset.constructTxCurrDataset(true)));
    rd.addDataSetDefinition("P", Mapped.mapStraightThrough(txPvlsDataset.constructTxPvlsDatset()));
    rd.addDataSetDefinition(
        "TXML", Mapped.mapStraightThrough(txMlDataset25.constructtxMlDataset()));
    rd.addDataSetDefinition("R", Mapped.mapStraightThrough(txRttDataset.constructTxRttDataset()));
    rd.addDataSetDefinition(
        "T", Mapped.mapStraightThrough(transferredInDataset.constructTransferInDataset()));
    rd.addDataSetDefinition(
        "PL", Mapped.mapStraightThrough(txRTTPLHIVDateset.constructTxRTTPLHIVDateset()));
    rd.addDataSetDefinition(
        "PREP", Mapped.mapStraightThrough(prepNewDataset.constructPrepNewDataset()));
    rd.addDataSetDefinition("DT", Mapped.mapStraightThrough(new DatimCodeDatasetDefinition()));
    rd.addDataSetDefinition(
        "PREPNUM", Mapped.mapStraightThrough(prepCtDataset.constructPrepCtDataset()));

    // add a base cohort here to help in calculations running
    rd.setBaseCohortDefinition(
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));

    return rd;
  }

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    ReportDesign rd = null;
    try {
      rd =
          createXlsReportDesign(
              reportDefinition,
              "PEPFAR_MER_2.6.1_Quarterly_v1.10.1.xls",
              "PEPFAR MER 2.6 Quarterly Report",
              getExcelDesignUuid(),
              null);
      Properties props = new Properties();
      props.put("sortWeight", "5000");
      rd.setProperties(props);
    } catch (IOException e) {
      throw new ReportingException(e.toString());
    }

    return Arrays.asList(rd);
  }
}
