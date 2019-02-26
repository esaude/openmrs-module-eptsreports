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

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.Eri4MonthsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition} for pepfar early
 * indicator report
 */
@Component
public class Eri4MonthsCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	/**
	 * Get all patients who have 4 months ART retention after initiation (A)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoStartedArt4MonthsFromArtInitiation() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsRetentionFor4MonthsOnART - Started ART");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(Eri4MonthsQueries.allPatientsWhoInitiatedTreatmentDuringReportingPeriod(hivMetadata
		        .getARVPharmaciaEncounterType().getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType()
		        .getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getARVPlanConcept().getConceptId(), hivMetadata.getstartDrugsConcept().getConceptId(), hivMetadata
		            .gethistoricalDrugStartDateConcept().getConceptId(), hivMetadata.getARTProgram().getProgramId()));
		return cd;
	}
	
	/**
	 * C Get all patients who initiated treatment and had a drug pick up or had a consultation
	 * between 61 and 120 days from the encounter date
	 */
	public CohortDefinition getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("Patients who had consultation between 61 to 120 days from encounter date");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(Eri4MonthsQueries
		        .allPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(hivMetadata
		                .getARVPharmaciaEncounterType().getEncounterTypeId(), hivMetadata.getAdultoSeguimentoEncounterType()
		                .getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
		            hivMetadata.getARVPlanConcept().getConceptId(), hivMetadata.getstartDrugsConcept().getConceptId(),
		            hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(), hivMetadata.getARTProgram()
		                    .getProgramId(), hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState()
		                    .getProgramWorkflowStateId()));
		return cd;
	}
	
	/**
	 * Get lost to follow up patients
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoAreLostToFollowUpWithinPeriod() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get lost to follow up patients within period");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		
		int daysThreshold = 60;
		
		cd.addSearch("drugPickupLTFU", EptsReportUtils.map(getPatientsLostToFollowUpOnDrugPickup(daysThreshold),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		
		cd.addSearch("consultationLTFU", EptsReportUtils.map(getPatientsLostToFollowUpOnConsultation(daysThreshold),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		
		cd.setCompositionString("drugPickupLTFU AND consultationLTFU");
		
		return cd;
	}
	
	private SqlCohortDefinition getPatientsLostToFollowUpOnConsultation(int daysThreshold) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(Eri4MonthsQueries.getPatientsLostToFollowUpOnConsultation(hivMetadata.getAdultoSeguimentoEncounterType()
		        .getEncounterTypeId(), hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
		    hivMetadata.getReturnVisitDateConcept().getConceptId(), daysThreshold));
		return cd;
	}
	
	private SqlCohortDefinition getPatientsLostToFollowUpOnDrugPickup(int daysThreshold) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(Eri4MonthsQueries.getPatientsLostToFollowUpOnDrugPickup(hivMetadata.getARVPharmaciaEncounterType()
		        .getEncounterTypeId(), hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(), daysThreshold));
		return cd;
	}
	
	/**
	 * A and NOT B
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoInitiatedArtLessTransferIns() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who started ART 4 months less transfer ins");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedART", EptsReportUtils.map(getPatientsWhoStartedArt4MonthsFromArtInitiation(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.addSearch("transferIns", EptsReportUtils.map(genericCohortQueries.getPatientsBasedOnPatientStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState()
		        .getProgramWorkflowStateId()), "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.setCompositionString("initiatedART AND NOT transferIns");
		return cd;
	}
	
	/**
	 * Get pregnant women who have more than 4 months retention on ART
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenRetainedOnArtFor4MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant women retain on ART for more than 4 months from ART initiation date");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedART", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.setCompositionString("initiatedART AND pregnant");
		return cd;
	}
	
	/**
	 * Get breastfeeding women who have more than 4 months ART retention
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenRetainedOnArtFor4MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women retain on ART for more than 4 months from ART initiation date");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedART", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${cohortStartDate},onOrBefore=${cohortEndDate},location=${location}"));
		cd.setCompositionString("initiatedART AND breastfeeding");
		return cd;
	}
	
	/**
	 * Get Children (0-14, excluding pregnant and breastfeeding women)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getChildrenRetainedOnArtFor4MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Children having ART retention for than 3 months");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedART", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("children",
		    EptsReportUtils.map(genericCohortQueries.getAgeOnArtStartDate(0, 14), "location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${cohortStartDate},onOrBefore=${cohortEndDate},location=${location}"));
		cd.setCompositionString("(initiatedART AND children) AND NOT (pregnant OR breastfeeding)");
		return cd;
	}
	
	/**
	 * Get Adults (14+, excluding pregnant and breastfeeding women)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getAdultsRetaineOnArtFor4MonthsFromArtInitiation() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Adults having ART retention for than 3 months");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedART", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("adults",
		    EptsReportUtils.map(genericCohortQueries.getAgeOnArtStartDate(15, 200), "location=${location}"));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${cohortStartDate},onOrBefore=${cohortEndDate},location=${location}"));
		cd.setCompositionString("(initiatedART AND adults) AND NOT(pregnant OR breastfeeding)");
		return cd;
	}
	
	/**
	 * Get patients who are alive and on treatment - probably all those who have been on ART for
	 * more than 3 months excluding the dead, transfers or suspended (A AND NOT B) AND C
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoAreAliveAndOnTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who are a live and on treatment");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedArt", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("consultation", EptsReportUtils.map(
		    getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.addSearch("dead", EptsReportUtils.map(genericCohortQueries.getDeceasedPatients(),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.addSearch("transfersOut", EptsReportUtils.map(genericCohortQueries.getPatientsBasedOnPatientStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState()
		        .getProgramWorkflowStateId()),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.setCompositionString("(initiatedArt AND consultation) AND NOT (dead OR transfersOut)");
		return cd;
	}
	
	/**
	 * Get lost to follow up patients within 4 months from ART initiation
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getAllPatientsWhoAreLostToFollowUpDuringPeriod() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Lost to follow up patients");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedArt", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("missedVisit", EptsReportUtils.map(getPatientsWhoAreLostToFollowUpWithinPeriod(),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.addSearch("dead", EptsReportUtils.map(genericCohortQueries.getDeceasedPatients(),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.addSearch("transfersOut", EptsReportUtils.map(genericCohortQueries.getPatientsBasedOnPatientStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState()
		        .getProgramWorkflowStateId()),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.setCompositionString("initiatedArt AND missedVisit AND NOT (dead OR transfersOut)");
		return cd;
	}
	
	/**
	 * Get patients who are alive and on treatment - probably all those who have been on ART for
	 * more than 3 months excluding the dead, transfers or suspended (A AND NOT B) AND C
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoAreAliveAndNotOnTreatment() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients who are a live and NOT treatment");
		cd.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
		cd.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
		cd.addParameter(new Parameter("reportingEndDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("initiatedArt", EptsReportUtils.map(getPatientsWhoInitiatedArtLessTransferIns(),
		    "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
		cd.addSearch("consultation", EptsReportUtils.map(
		    getAllPatientsWhoHaveEitherClinicalConsultationOrDrugsPickupBetween61And120OfEncounterDate(),
		    "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
		cd.addSearch("dead", EptsReportUtils.map(genericCohortQueries.getDeceasedPatients(),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.addSearch("transfersOut", EptsReportUtils.map(genericCohortQueries.getPatientsBasedOnPatientStates(hivMetadata
		        .getARTProgram().getProgramId(), hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState()
		        .getProgramWorkflowStateId()),
		    "startDate=${cohortStartDate},endDate=${reportingEndDate},location=${location}"));
		cd.setCompositionString("initiatedArt AND NOT (consultation OR dead OR transfersOut)");
		return cd;
	}
}
