/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.IOUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Helper class for registering/purging report definitions and report designs
 */
public class Helper { // See how to merge this with EptsDataExportManager.java and EptsReportManager.java

	/**
	 * Deletes a Report Definition from the database.
	 * 
	 * @param name the Definition to purge
	 */
	public static void purgeReportDefinition(String name) {
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		try {
			ReportDefinition findDefinition = findReportDefinition(name);
			if (findDefinition != null) {
				rds.purgeDefinition(findDefinition);
			}
		}
		catch (RuntimeException e) {
			
		}
	}
	
	/**
	 * Returns the Definition whose name is equal to the passed name.
	 * 
	 * @param name The search string
	 * @throws RuntimeException a RuntimeException if the Definition can't be found
	 * @return Definition object whose name is equal to the passed name
	 */
	
	public static ReportDefinition findReportDefinition(String name) {
		ReportDefinitionService s = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
		List<ReportDefinition> defs = s.getDefinitions(name, true);
		for (ReportDefinition def : defs) {
			return def;
		}
		throw new RuntimeException("Couldn't find Definition " + name);
	}
	
	/**
	 * Persists a Definition, either as a save or update.
	 * 
	 * @param definition the Definition to persist
	 */
	public static void saveReportDefinition(ReportDefinition rd) {
		ReportDefinitionService rds = (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
		
		// try to find existing report definitions to replace
		List<ReportDefinition> definitions = rds.getDefinitions(rd.getName(), true);
		if (definitions.size() > 0) {
			ReportDefinition existingDef = definitions.get(0);
			rd.setId(existingDef.getId());
			rd.setUuid(existingDef.getUuid());
		}
		try {
			rds.saveDefinition(rd);
		}
		catch (Exception e) {
			SerializedDefinitionService s = (SerializedDefinitionService) Context
			        .getService(SerializedDefinitionService.class);
			s.saveDefinition(rd);
		}
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
	public static ReportDesign createRowPerPatientXlsOverviewReportDesign(ReportDefinition rd, String resourceName,
	        String name, Map<? extends Object, ? extends Object> properties) throws IOException {
		
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
	
	/**
	 * @return a new ReportDesign for a standard CSV output
	 */
	public static ReportDesign createCsvReportDesign(ReportDefinition reportDefinition, String reportDesignName) {
		ReportDesign design = new ReportDesign();
		design.setName(reportDesignName);
		design.setReportDefinition(reportDefinition);
		design.setRendererType(CsvReportRenderer.class);
		return design;
	}
	
	/**
	 * Save or update the given ReportDesign in the database. If this is a new ReportDesign, the
	 * returned ReportDesign will have a new {@link ReportDesign#getId()} inserted into it that was
	 * generated by the database
	 * 
	 * @param design The ReportDesign to save or update
	 */
	public static void saveReportDesign(ReportDesign design) {
		ReportService rs = Context.getService(ReportService.class);
		rs.saveReportDesign(design);
	}
	
}
