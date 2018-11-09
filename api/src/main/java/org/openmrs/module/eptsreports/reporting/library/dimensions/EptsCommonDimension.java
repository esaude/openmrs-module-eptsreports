package org.openmrs.module.eptsreports.reporting.library.dimensions;

import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {
	
	@Autowired
	private GenderCohortQueries genderCohortQueries;
	
	/**
	 * Gender dimension
	 * 
	 * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDefinitionDimension gender() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.setName("gender");
		dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.MaleCohort(), ""));
		dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
		return dim;
	}
}
