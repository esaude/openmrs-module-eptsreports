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

package org.openmrs.module.eptsreports.reporting.reports.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.util.IOUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.OpenmrsClassLoader;

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
	
	protected ReportDesign createExcelTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition, String templatePath) {
		String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
		return ReportManagerUtil.createExcelTemplateDesign(reportDesignUuid, reportDefinition, resourcePath);
	}
	
	/**
	 * @return a new ReportDesign for a standard Excel output
	 */
	public ReportDesign createExcelDesignWithProperties(String reportDesignUuid, ReportDefinition reportDefinition, Properties props) {
		ReportDesign design = ReportManagerUtil.createExcelDesign(reportDesignUuid, reportDefinition);
		design.setProperties(props);
		return design;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @param rd the reportDesign to set
	 * @param resourceName
	 * @param name
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	public static ReportDesign createRowPerPatientXlsOverviewReportDesign(ReportDefinition rd, String resourceName, String name, Map<? extends Object, ? extends Object> properties) throws IOException {
		
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rdd : rs.getAllReportDesigns(false)) {
			if (name.equals(rdd.getName())) {
				rs.purgeReportDesign(rdd);
			}
		}
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(resourceName);
		resource.setExtension("xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName);
		resource.setContents(IOUtils.toByteArray(is));
		final ReportDesign design = new ReportDesign();
		design.setName(name);
		design.setReportDefinition(rd);
		design.setRendererType(ExcelTemplateRenderer.class);
		design.addResource(resource);
		if (properties != null) {
			design.getProperties().putAll(properties);
		}
		resource.setReportDesign(design);
		
		return design;
	}
	
}
