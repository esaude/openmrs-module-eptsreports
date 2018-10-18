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
import org.openmrs.module.eptsreports.reporting.reports.EptsReportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;

public class EptsReportInitializer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Initializes all EPTS reports and remove deprocated reports from database.
	 * 
	 * @param void
	 * @return
	 * @throws Exception
	 */
	public void initializeReports() throws Exception {
		for (ReportManager reportManager : Context.getRegisteredComponents(EptsReportManager.class)) {
			if (reportManager.getClass().getAnnotation(Deprecated.class) != null) {
				// remove depricated reports
				removeReportDefinition(reportManager);
			} else {
				// setup EPTS active reports
				log.info("Setting up report " + reportManager.getName() + "...");
				ReportManagerUtil.setupReport(reportManager);
			}
		}
		ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
	}
	
	private void removeReportDefinition(ReportManager reportManager) {
		ReportDefinition rd = reportManager.constructReportDefinition();
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		if (rd != null) {
			Context.getService(ReportDefinitionService.class).purgeDefinition(rd);
		}
		
		log.warn("Report " + reportManager.getName() + " is deprecated.  Removing it from database.");
	}
}
