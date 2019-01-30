package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.PepfarEarlyRetentionDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupPepfarEarlyRetentionIndicatorsReport extends EptsDataExportManager {
	
	@Autowired
	private PepfarEarlyRetentionDataset pepfarEarlyRetentionDataset;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Override
	public String getExcelDesignUuid() {
		return "a2a71742-22fe-11e9-be5e-7f732f0e15c6";
	}
	
	@Override
	public String getUuid() {
		return "b0b3b854-22fe-11e9-a16a-03dcc5ccbc2e";
	}
	
	@Override
	public String getName() {
		return "PEPFAR Early Retention Indicators Report";
	}
	
	@Override
	public String getDescription() {
		return "Implementation of new Retention Indicators report";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(pepfarEarlyRetentionDataset.getParameters());
		
		rd.addDataSetDefinition("Pepfar early retention Data Set",
		    Mapped.mapStraightThrough(pepfarEarlyRetentionDataset.constructPepfarEarlyRetentionDatset()));
		// add a base cohort here to help in calculations running
		/*
		 * rd.setBaseCohortDefinition(EptsReportUtils.map(genericCohortQueries.
		 * getBaseCohort(), "endDate=${endDate},location=${location}"));
		 */
		
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
			reportDesign = createXlsReportDesign(reportDefinition, "PEPFAR_Early_Retention.xls",
			    "PEPFAR_Early_Retention_Indicators", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return Arrays.asList(reportDesign);
	}
}
