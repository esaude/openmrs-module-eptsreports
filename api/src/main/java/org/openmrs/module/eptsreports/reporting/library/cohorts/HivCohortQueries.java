package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivCohortQueries {

	@Autowired
	private CommonCohortQueries commonCohortQueries;

	@Autowired
	private HivMetadata hivMetadata;

	public CohortDefinition getPatientsWhoRestartedTreatment() {
		return commonCohortQueries.hasCodedObs(hivMetadata.getARVPlanConcept(), hivMetadata.getRestartConcept());
	}
}
