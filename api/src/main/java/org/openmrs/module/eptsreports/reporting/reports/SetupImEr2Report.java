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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.Eri2MonthsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class SetupImEr2Report extends EptsDataExportManager {
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private Eri2MonthsDataset eri2MonthsDataset;
	
	@Override
	public String getExcelDesignUuid() {
		return "d5df7a6a-2852-11e9-b382-3bdd35cf3401";
	}
	
	@Override
	public String getUuid() {
		return "e2e62a74-2852-11e9-88ac-47ceb00c64cb";
	}
	
	@Override
	public String getName() {
		return "IM-ER2-Report";
	}
	
	@Override
	public String getDescription() {
		return "PEPFAR Early Retention Indicators - 2 Months";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(eri2MonthsDataset.getParameters());
		
		rd.addDataSetDefinition("ERI-2 Months Data Set",
		    Mapped.mapStraightThrough(eri2MonthsDataset.constructEri2MonthsDatset()));
		// add a base cohort here to help in calculations running
		rd.setBaseCohortDefinition(EptsReportUtils.map(genericCohortQueries.getBaseCohort(),
		    "endDate=${endDate},location=${location}"));
		
		return rd;
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign reportDesign = null;
		try {
			reportDesign = createXlsReportDesign(reportDefinition, "IM_ER2_Report.xls", "ERI-2Months-Report",
			    getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new ReportingException(e.toString());
		}
		
		return Arrays.asList(reportDesign);
	}
}
