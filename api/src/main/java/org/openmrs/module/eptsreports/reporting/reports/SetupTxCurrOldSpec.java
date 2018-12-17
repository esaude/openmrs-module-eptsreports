/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxCurrDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTxCurrOldSpec extends EptsDataExportManager {
	
	@Autowired
	private TxCurrDataset txCurrDataset;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	public SetupTxCurrOldSpec() {
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public String getUuid() {
		return "381077e6-ceb8-4288-863e-818499515a30";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "8a75c3be-995a-4448-a259-4d57d991b614";
	}
	
	@Override
	public String getName() {
		return "TX_CURR Report Old Spec";
	}
	
	@Override
	public String getDescription() {
		return "Number of adults and children currently receiving antiretroviral therapy (ART) (Old Spec).";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(txCurrDataset.getParameters());
		
		reportDefinition.addDataSetDefinition(txCurrDataset.constructTxNewDatset(false),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate},location=${location}"));
		
		reportDefinition.setBaseCohortDefinition(genericCohortQueries.getBaseCohort(),
		    ParameterizableUtil.createParameterMappings("endDate=${endDate},location=${location}"));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign reportDesign = null;
		try {
			reportDesign = createXlsReportDesign(reportDefinition, "TXCURR.xls", "TXCURR_OLD_SPEC.xls_", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,dataset:TX_CURR Data Set");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return Arrays.asList(reportDesign);
	}
	
}
