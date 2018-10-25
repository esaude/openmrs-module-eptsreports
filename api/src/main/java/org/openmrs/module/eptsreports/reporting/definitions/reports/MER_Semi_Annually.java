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
public class MER_Semi_Annually extends EptsDataExportManager {
	
	public MER_Semi_Annually() {
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public String getUuid() {
		return "865c164d-71a1-4421-86b3-b73c8a733170";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "60108c4b-f20c-4b67-8fbc-1cb58f644ec5";
	}
	
	@Override
	public String getName() {
		return "MER_Semi_Annually";
	}
	
	@Override
	public String getDescription() {
		return "MER Semi-Annual Report";
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
