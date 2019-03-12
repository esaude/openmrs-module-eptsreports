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

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TbPrevDataset extends BaseDataSet {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  public DataSetDefinition constructTbPrevDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dsd.setName("TB PREV Data Set");
    dsd.addParameters(getParameters());
    dsd.addColumn(
        "NUM-TOTAL",
        "Numerator Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Total",
                EptsReportUtils.map(
                    genericCohortQueries.getActiveOnArt(),
                    "onOrBefore=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "TEST-01",
        "Teste",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Numerator Total",
                EptsReportUtils.map(
                    tbPrevCohortQueries.getPatientsThatStartedProfilaxiaIsoniazidaOnPeriod(),
                    "value1=${startDate},value2=${endDate},locationList=${location}")),
            mappings),
        "");
    return dsd;
  }
}
