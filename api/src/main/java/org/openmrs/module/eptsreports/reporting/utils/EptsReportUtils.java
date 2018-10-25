/**
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
package org.openmrs.module.eptsreports.reporting.utils;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;

/**
 * Created by Nicholas Ingosi on 6/20/17 Taken from esaude-reports module
 */
public class EptsReportUtils {
	
	/**
	 * Purges a Report Definition from the database
	 * 
	 * @param reportManager the Report Definition
	 */
	public static void purgeReportDefinition(ReportManager reportManager) {
		ReportDefinition rd = reportManager.constructReportDefinition();
		if (rd != null) {
			Context.getService(ReportDefinitionService.class).purgeDefinition(rd);
		}
	}
	
	/**
	 * Setup a Report Definition in a database
	 * 
	 * @param reportManager the Report Definition
	 */
	public static void setupReportDefinition(ReportManager reportManager) {
		ReportManagerUtil.setupReport(reportManager);
	}
	
}
