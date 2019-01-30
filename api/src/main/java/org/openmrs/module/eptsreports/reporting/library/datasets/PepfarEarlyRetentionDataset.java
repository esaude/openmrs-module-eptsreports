package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PepfarEarlyRetentionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PepfarEarlyRetentionDataset extends BaseDataSet {
	
	@Autowired
	private PepfarEarlyRetentionCohortQueries pepfarEarlyRetentionCohortQueries;
	
	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	public DataSetDefinition constructPepfarEarlyRetentionDatset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("Pepfar early retention Data Set");
		dsd.addParameters(getParameters());
		
		// apply disagregations here
		dsd.addDimension("age",
		    EptsReportUtils.map(eptsCommonDimension.pvlsAges(), "endDate=${endDate},location=${location}"));
		dsd.addDimension("state", EptsReportUtils.map(eptsCommonDimension.getPatientStatesDimension(), mappings));
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		// start forming the columns
		addRow(dsd, "I1", "Patients retained on ART 3 months after ART initiation", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("Early retention", EptsReportUtils.map(
		        pepfarEarlyRetentionCohortQueries.getPatientsRetainedOnArtForXMonthsFromArtInitiation(), mappings)),
		    mappings), retentionColumns());
		return dsd;
	}
	
	private List<ColumnParameters> retentionColumns() {
		ColumnParameters pw = new ColumnParameters("pregnant women", "Pregnant Women", "gender=F|state=PW", "01");
		ColumnParameters bw = new ColumnParameters("breastfeeding women", "Breastfeeding Women", "gender=F|state=BW", "02");
		ColumnParameters cp = new ColumnParameters("children", "Children", "state=CP", "03");
		ColumnParameters adp = new ColumnParameters("Adults", "Adults", "state=ADP", "04");
		ColumnParameters dp = new ColumnParameters("Dead", "Dead", "state=DP", "05");
		ColumnParameters anp = new ColumnParameters("abandoned", "Abandoned", "state=ANP", "06");
		ColumnParameters sp = new ColumnParameters("suspended", "Suspended", "state=SP", "07");
		ColumnParameters top = new ColumnParameters("transfers", "Transfers", "state=TOP", "08");
		return Arrays.asList(pw, bw, cp, adp, dp, anp, sp, top);
	}
}
