package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.library.datasets.TxPvlsDataset;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupTxPvls extends EptsDataExportManager {
	
	@Autowired
	private TxPvlsDataset txPvlsDataset;
	
	@Override
	public String getExcelDesignUuid() {
		return "e57f0ec4-e287-11e8-acd5-e311f10b26ed";
	}
	
	@Override
	public String getUuid() {
		return "f3af9590-e287-11e8-bba1-e73bca3486f8";
	}
	
	@Override
	public String getName() {
		return "Tx_Pvls Report";
	}
	
	@Override
	public String getDescription() {
		return "Percentage of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (<1000 copies/ml)";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(txPvlsDataset.getParameters());
		
		rd.addDataSetDefinition("PV", Mapped.mapStraightThrough(txPvlsDataset.constructTxPvlsDatset()));
		
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
			reportDesign = createRowPerPatientXlsOverviewReportDesign(reportDefinition, "TxPvls.xls", "TxPvls.xls_", null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,dataset:Tx_Pvls Dataset");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Arrays.asList(reportDesign);
	}
}
