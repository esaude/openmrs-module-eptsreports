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

package org.openmrs.module.eptsreports.reporting.library.indicators;

import org.openmrs.module.eptsreports.reporting.library.cohorts.CompositionCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SqlCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BreastfeedingIndicators extends BaseIndicators {
	
	@Autowired
	private CompositionCohortQueries ccq;

	@Autowired
	private SqlCohortQueries sqlCohortQueries;
	
	/**
	 * Breastfeeding women with viral load suppression in the last 12 months to a common file for reuse
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getBreastfeedingWomenWithSuppressedViralLoadIn12Months() {
		return newCohortIndicator("pregnantWomenWithViralLoadSuppression", EptsReportUtils.map(ccq.breastfeedingWomenAndHasViralLoadSuppressionInTheLast12MonthsNumerator(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
	
	/**
	 * Breastfeeding women with viral load in the last 12 months to a common file for reuse
	 * 
	 * @return CohortIndicator
	 */
	public CohortIndicator getBreastfeedingWomenWithViralLoadIn12Months() {
		return newCohortIndicator("pregnantWomenWithViralLoad", EptsReportUtils.map(ccq.breastfeedingWomenAndHasViralLoadInTheLast12MonthsDenominator(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}

	public CohortIndicator getBreastfeedingWomenWhoAreInArtTreatment() {
		return newCohortIndicator("breastfeedingWomenInArtTreatment", EptsReportUtils.map(sqlCohortQueries.aRTStartForBeingBreastfeeding(), "startDate=${startDate},endDate=${endDate},location=${location}"));
	}
}
