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

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;

/**
 * Epts Reports module utilities
 */
public class EptsReportUtils {
	
	/**
	 * Purges a Report Definition from the database
	 * 
	 * @param reportManager the Report Definition
	 */
	public static void purgeReportDefinition(ReportManager reportManager) {
		ReportDefinition findDefinition = findReportDefinition(reportManager.getUuid());
		ReportDefinitionService reportService = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
		if (findDefinition != null) {
			reportService.purgeDefinition(findDefinition);
			
			// Purge Global property used to track version of report definition
			String gpName = "reporting.reportManager." + reportManager.getUuid() + ".version";
			GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(gpName);
			if (gp != null) {
				Context.getAdministrationService().purgeGlobalProperty(gp);
			}
		}
	}
	
	/**
	 * Returns the Report Definition matching the provided uuid.
	 * 
	 * @param uuid Report Uuid
	 * @throws RuntimeException a RuntimeException if the Report Definition can't be found
	 * @return Report Definition object
	 */
	
	public static ReportDefinition findReportDefinition(String uuid) {
		ReportDefinitionService reportService = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
		ReportDefinition reportDefinition = reportService.getDefinitionByUuid(uuid);
		if (reportDefinition != null) {
			return reportDefinition;
		}
		
		throw new RuntimeException("Couldn't find Report Definition " + uuid);
	}
	
	/**
	 * Setup a Report Definition in a database
	 * 
	 * @param reportManager the Report Definition
	 */
	public static void setupReportDefinition(ReportManager reportManager) {
		ReportManagerUtil.setupReport(reportManager);
	}
	
	/**
	 * @param parameterizable
	 * @param mappings
	 * @param <T>
	 * @return
	 */
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
}
