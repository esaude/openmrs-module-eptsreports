package org.openmrs.module.eptsreports.reporting.library.indicators;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PregnantIndicators extends BaseIndicators {
	
	@Autowired
	private TxPvlsCohortQueries txPvls;
	
	/**
	 * Pregnant women with viral load suppression in the last 12 months to a common file for reuse
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithSuppressedViralLoadIn12Months() {
		return newCohortIndicator("pregnantWomenWithViralLoadSuppression", EptsReportUtils
		        .map(txPvls.getPatientsWithViralLoadSuppression(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months to a common file for reuse
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithViralLoadIn12Months() {
		return newCohortIndicator("pregnantWomenWithViralLoad", EptsReportUtils.map(txPvls.getPatientsWithViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
}
