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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;

/**
 * Excel Data Export Manager for EPTS reports
 */
public abstract class EptsDataExportManager extends EptsReportManager {
	
	/**
	 * @return the uuid for the report design for exporting to Excel
	 */
	public abstract String getExcelDesignUuid();
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		ReportDesign excelDesign = ReportManagerUtil.createExcelDesign(getExcelDesignUuid(), reportDefinition);
		l.add(excelDesign);
		return l;
	}
	
	protected ReportDesign createExcelTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition,
	        String templatePath) {
		String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
		return ReportManagerUtil.createExcelTemplateDesign(reportDesignUuid, reportDefinition, resourcePath);
	}
	
	/**
	 * @return a new ReportDesign for a standard Excel output
	 */
	public ReportDesign createExcelDesignWithProperties(String reportDesignUuid, ReportDefinition reportDefinition,
	        Properties props) {
		ReportDesign design = ReportManagerUtil.createExcelDesign(reportDesignUuid, reportDefinition);
		design.setProperties(props);
		return design;
	}
	
}
