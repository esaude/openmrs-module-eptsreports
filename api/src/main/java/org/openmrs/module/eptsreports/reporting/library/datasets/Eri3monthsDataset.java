package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri3monthsCohortQueries;
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
public class Eri3monthsDataset extends BaseDataSet {
	
	@Autowired
	private Eri3monthsCohortQueries pepfarEarlyRetentionCohortQueries;
	
	@Autowired
	private EptsGeneralIndicator eptsGeneralIndicator;
	
	@Autowired
	private EptsCommonDimension eptsCommonDimension;
	
	public DataSetDefinition constructPepfarEarlyRetentionDatset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("ERI-3months Data Set");
		dsd.addParameters(getParameters());
		
		// apply disagregations here
		dsd.addDimension("age",
		    EptsReportUtils.map(eptsCommonDimension.pvlsAges(), "endDate=${endDate},location=${location}"));
		dsd.addDimension("state", EptsReportUtils.map(eptsCommonDimension.getPatientStatesDimension(), mappings));
		dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
		// start forming the columns
		dsd.addColumn("I0", "Total patients retained on ART for 3 months", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("all patients", EptsReportUtils.map(
		        pepfarEarlyRetentionCohortQueries.getPatientsRetainedOnArtForXMonthsFromArtInitiation(), mappings)),
		    mappings), "");
		addRow(dsd, "I1", "All Patients retained on ART 3 months after ART initiation", EptsReportUtils.map(
		    eptsGeneralIndicator.getIndicator("all patients", EptsReportUtils.map(
		        pepfarEarlyRetentionCohortQueries.getPatientsRetainedOnArtForXMonthsFromArtInitiation(), mappings)),
		    mappings), retentionColumns());
		addRow(
		    dsd,
		    "I2",
		    "Pregnant women retained on ART 3 months after ART initiation",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("pregnant women", EptsReportUtils.map(
		            pepfarEarlyRetentionCohortQueries.getPregnantWomenRetainedOnArtFor3MonthsFromArtInitiation(), mappings)),
		        mappings), retentionColumns());
		addRow(dsd, "I3", "Breastfeeding women retained on ART 3 months after ART initiation",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("breastfeeding women", EptsReportUtils.map(
		            pepfarEarlyRetentionCohortQueries.getBreastfeedingWomenRetainedOnArtFor3MonthsFromArtInitiation(),
		            mappings)), mappings), retentionColumns());
		addRow(dsd, "I4",
		    "Children (0-14, excluding pregnant and breastfeeding women) retained on ART 3 months after ART initiation",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator(
		            "children",
		            EptsReportUtils.map(
		                pepfarEarlyRetentionCohortQueries.getChildrenRetaineOnArtFor3MonthsFromArtInitiation(), mappings)),
		        mappings), retentionColumns());
		addRow(dsd, "I4",
		    "Adults (14+, excluding pregnant and breastfeeding women)  retained on ART 3 months after ART initiation",
		    EptsReportUtils.map(
		        eptsGeneralIndicator.getIndicator("adults", EptsReportUtils.map(
		            pepfarEarlyRetentionCohortQueries.getAdultsRetaineOnArtFor3MonthsFromArtInitiation(), mappings)),
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
