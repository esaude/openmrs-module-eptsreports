/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.PepfarEarlyRetentionQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Defines @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition} for pepfar early
 * indicator report
 */

@Component
public class PepfarEarlyRetentionCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	private CohortDefinition getPatientsRetainedOnArtFor3MonthsFromArtInitiation() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsRetentionFor3MonthsOnART");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(PepfarEarlyRetentionQueries.getPatientsRetainedOnArt3MonthsAfterArtInitiation(hivMetadata
		        .getARVPharmaciaEncounterType().getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType()
		        .getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getARVPlanConcept().getConceptId(), hivMetadata.getstartDrugsConcept().getConceptId(), hivMetadata
		            .gethistoricalDrugStartDateConcept().getConceptId(), hivMetadata.getARTProgram().getProgramId(),
		    hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()));
		return cd;
	}
	
	private CohortDefinition getExclusionCriteriaByStates() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get exclusion criteria");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("transferredOut", EptsReportUtils.map(genericCohortQueries.getPatientsToExcludeBasedOnStates(
		    hivMetadata.getARTProgram().getProgramId(), hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState()
		            .getProgramWorkflowStateId()), "endDate=${endDate},location=${location}"));
		cd.addSearch("transferredIn", EptsReportUtils.map(genericCohortQueries.getPatientsToExcludeBasedOnStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState()
		        .getProgramWorkflowStateId()), "endDate=${endDate},location=${location}"));
		cd.addSearch("suspended", EptsReportUtils.map(genericCohortQueries.getPatientsToExcludeBasedOnStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getSuspendedTreatmentWorkflowState()
		        .getProgramWorkflowStateId()), "endDate=${endDate},location=${location}"));
		cd.addSearch("abandoned", EptsReportUtils.map(genericCohortQueries.getPatientsToExcludeBasedOnStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId()),
		    "endDate=${endDate},location=${location}"));
		cd.setCompositionString("transferredOut OR transferredIn OR suspended OR abandoned");
		return cd;
	}
	
	private CohortDefinition getPatientToExcludeBasedOnLostToFollowUpPatients() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who are lost to follow up");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		return cd;
	}
	
	private CohortDefinition getTotalPatientsToExcludeFromMainQuery() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Total patients to be excluded from main query");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("excludeByState",
		    EptsReportUtils.map(getExclusionCriteriaByStates(), "endDate=${endDate},location=${location}"));
		cd.addSearch("excludeByLtfu", EptsReportUtils.map(getPatientToExcludeBasedOnLostToFollowUpPatients(),
		    "endDate=${endDate},location=${location}"));
		cd.setCompositionString("excludeByState OR excludeByLtfu");
		return cd;
	}
	
	public CohortDefinition getResultant3MonthsArtRetention() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("3 Months Retention with exclusions");
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("baseTotal", EptsReportUtils.map(getPatientsRetainedOnArtFor3MonthsFromArtInitiation(),
		    "endDate=${endDate},location=${location}"));
		cd.addSearch("exclusions",
		    EptsReportUtils.map(getTotalPatientsToExcludeFromMainQuery(), "endDate=${endDate},location=${location}"));
		return cd;
	}
}
