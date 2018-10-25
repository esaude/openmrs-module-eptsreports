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
package org.openmrs.module.eptsreports.reporting.definitions.reports;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.eptsreports.reporting.reports.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
public class MER_Quarterly extends EptsDataExportManager {
	
	public MER_Quarterly() {
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public String getUuid() {
		return "fa20c1ac-94ea-11e3-96de-0023156365e4";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "cea86583-9ca5-4ad9-94e4-e20081a57619";
	}
	
	@Override
	public String getName() {
		return "MER_Quarterly";
	}
	
	@Override
	public String getDescription() {
		return "MER Quarterly Report";
	}
	
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		return parameters;
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		return reportDefinition;
	}
	
}
