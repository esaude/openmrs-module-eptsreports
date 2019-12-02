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

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class TxRttDataset extends BaseDataSet {

  private TxRttCohortQueries txRttCohortQueries;

  @Autowired
  public TxRttDataset(TxRttCohortQueries txRttCohortQueries) {
    this.txRttCohortQueries = txRttCohortQueries;
  }

  public DataSetDefinition constructTxRttDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("TX_RTT Dataset");
    dsd.addParameters(getParameters());
    return dsd;
  }
}
