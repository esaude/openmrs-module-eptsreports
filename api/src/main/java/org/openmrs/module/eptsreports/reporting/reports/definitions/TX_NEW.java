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

import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
public class TX_NEW extends EptsDataExportManager {
	
	public TX_NEW() {
	}
	
	@Override
	public String getVersion() {
		return "0.1";
	}
	
	@Override
	public String getUuid() {
		return "8c123e90-5c71-11e5-a151-e82aea237783";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "6c5fcaf3-02d1-11e4-a73c-54ee7513a7ff";
	}
	
	@Override
	public String getName() {
		return "TX_NEW";
	}
	
	@Override
	public String getDescription() {
		return "Number of adults and children newly enrolled on antiretroviral therapy (ART).";
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
		reportDefinition.setDescription(getDescription());
		// reportDefinition.setParameters(getParameters());
		
		return reportDefinition;
	}
}
