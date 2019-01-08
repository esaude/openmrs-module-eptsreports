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
	 * and on routine - numerator
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithSuppressedViralLoadIn12MonthsAndOnRoutineNumerator() {
		return newCohortIndicator("pregnantWomenWithViralLoadSuppression and on routine", EptsReportUtils.map(
		    txPvls.getPregnantAndOnRoutineNumerator(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Pregnant women with viral load suppression in the last 12 months to a common file for reuse
	 * and not documented - numerator
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithSuppressedViralLoadIn12MonthsNotDocumentedNumerator() {
		return newCohortIndicator("pregnantWomenWithViralLoadSuppression and NOT documented", EptsReportUtils.map(
		    txPvls.getPregnantAndNotDocumentedNumerator(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months to a common file for reuse and on
	 * routine - denominator
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithViralLoadIn12MonthsAndOnRoutineDenominator() {
		return newCohortIndicator("pregnantWomenWithViralLoad and not documented denominator", EptsReportUtils.map(
		    txPvls.getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Pregnant women with viral load in the last 12 months to a common file for reuse and NOT
	 * documented - denominator
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getPregnantWomenWithViralLoadIn12MonthsAndNotDocumentedDenominator() {
		return newCohortIndicator("pregnantWomenWithViralLoad results not documented denominator", EptsReportUtils.map(
		    txPvls.getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
}
