package org.openmrs.module.eptsreports.reporting.library.dimensions;

import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.CohortDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
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
	
	/**
	 * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-49, >=50
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDimension age() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.setName("age");
		dim.addCohortDefinition("<1", EptsReportUtils.map(ageCohortQueries.patientWithAgeBelow(1), ""));
		dim.addCohortDefinition("1-9", EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("oneTo9", 1, 9), ""));
		dim.addCohortDefinition("10-14", EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("tenTo14", 10, 14), ""));
		dim.addCohortDefinition("15-19",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("fifteenTo19", 15, 19), ""));
		dim.addCohortDefinition("20-24",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("twentyTo24", 20, 24), ""));
		dim.addCohortDefinition("25-29",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("twenty5To29", 25, 29), ""));
		dim.addCohortDefinition("30-34",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("thirtyTo34", 30, 34), ""));
		dim.addCohortDefinition("35-39",
		    EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("thirty5To39", 35, 39), ""));
		dim.addCohortDefinition("40-49", EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("fortyTo49", 40, 49), ""));
		dim.addCohortDefinition(">=50", EptsReportUtils.map(ageCohortQueries.patientWithAgeAbove(50), ""));
		return dim;
	}
	
	/**
	 * Pregnant and breast feeding
	 * 
	 * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
	 */
	public CohortDimension breastfeedingAndPregnant() {
		CohortDefinitionDimension dim = new CohortDefinitionDimension();
		dim.setName("bp");
		
		return dim;
	}
}
