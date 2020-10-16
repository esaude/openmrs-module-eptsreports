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

package org.openmrs.module.eptsreports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsReportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.util.ReportUtil;

public class EptsReportInitializer {

  private Log log = LogFactory.getLog(this.getClass());

  /** Initializes all EPTS reports and remove deprocated reports from database. */
  public void initializeReports() {

    for (ReportManager reportManager : Context.getRegisteredComponents(EptsReportManager.class)) {
      if (reportManager.getClass().getAnnotation(Deprecated.class) != null) {
        // remove depricated reports
        EptsReportUtils.purgeReportDefinition(reportManager);
        log.info(
            "Report " + reportManager.getName() + " is deprecated.  Removing it from database.");
      } else {
        // setup EPTS active reports
        EptsReportUtils.setupReportDefinition(reportManager);
        log.info("Setting up report " + reportManager.getName() + "...");
      }
    }
    ReportUtil.updateGlobalProperty(
        ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
  }

  /** Purges all EPTS reports from database. */
  public void purgeReports() {
    for (ReportManager reportManager : Context.getRegisteredComponents(EptsReportManager.class)) {
      EptsReportUtils.purgeReportDefinition(reportManager);
      log.info("Report " + reportManager.getName() + " removed from database.");
    }
  }
}
