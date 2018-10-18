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
package org.openmrs.module.eptsreports.reporting.reports.definitions;

import org.openmrs.module.eptsreports.reporting.reports.EptsDataExportManager;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
public class TX_CURR extends EptsDataExportManager {
	
	public TX_CURR() {
	}
	
	@Override
	public String getVersion() {
		return "0.1";
	}
	
	@Override
	public String getUuid() {
		return "eb6c9c1f-62f7-49fc-a8fd-748e77b9f806";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "ae928860-4a4e-48d4-bbc2-50902babcfc0";
	}
	
	@Override
	public String getName() {
		return "TX_CURR";
	}
	
	@Override
	public String getDescription() {
		return "Number of adults and children currently receiving antiretroviral therapy (ART).";
	}
	
	// @Override
	// public List<Parameter> getParameters() {
	// 	List<Parameter> parameters = new ArrayList<Parameter>();
	// 	parameters.add(dataFactory.getEndDateParameter());
	// 	parameters.add(dataFactory.getOptionalLocationParameter());
	// 	return parameters;
	// }
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		// reportDefinition.setDescription(getDescription());
		
		return reportDefinition;
	}
}
