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
		dsd.addDimension("state", EptsReportUtils.map(eptsCommonDimension.getPatientStatesDimension(), mappings));
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
		ColumnParameters initiatedArt = new ColumnParameters("initiated ART", "Initiated ART", "state=IART", "01");
		ColumnParameters aliveInTreatment = new ColumnParameters("alive in Treatment", "Alive and In Treatment",
		        "state=AIT", "02");
		ColumnParameters dead = new ColumnParameters("dead", "Dead", "state=DP", "03");
		ColumnParameters lostTfu = new ColumnParameters("ltfu", "Lost to follow up", "state=LTFU", "04");
		ColumnParameters stopped = new ColumnParameters("stopped", "Stopped treatment", "state=STP", "05");
		ColumnParameters transfers = new ColumnParameters("transfers", "Transferred Out", "state=TOP", "06");
		return Arrays.asList(initiatedArt, aliveInTreatment, dead, lostTfu, transfers, stopped);
	}
}
