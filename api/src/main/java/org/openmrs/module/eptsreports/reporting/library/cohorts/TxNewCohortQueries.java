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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.PregnantQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the TxNew Cohort Definition instances we want to expose for EPTS
 */
@Component
public class TxNewCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private CommonMetadata commonMetadata;
	
	@Autowired
	private GenericCohortQueries genericCohorts;
	
	@Autowired
	private GenderCohortQueries genderCohorts;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	@Autowired
	private AgeCohortQueries ageCohortQueries;
	
	/**
	 * Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred
	 * from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY)
	 * between start date and end date
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		String query = "select p.patient_id from patient p "
		        + "inner join patient_program pg on p.patient_id=pg.patient_id "
		        + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%d" + " and ps.state=%d"
		        + " and ps.start_date=pg.date_enrolled"
		        + " and ps.start_date between :onOrAfter and :onOrBefore and location_id=:location "
		        + "group by p.patient_id";
		transferredFromOtherHealthFacility.setQuery(String.format(query, hivMetadata.getARTProgram().getProgramId(),
		    hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("location", "location", Location.class));
		return transferredFromOtherHealthFacility;
	}
	
	/**
	 * PATIENTS WITH UPDATED DATE OF DEPARTURE IN THE ART SERVICE Are patients with date of delivery
	 * updated in the tarv service. Note that the 'Start Date' and 'End Date' parameters refer to
	 * the date of delivery and not the date of registration (update)
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWithUpdatedDepartureInART() {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setName("patientsWithUpdatedDepartureInART");
		cd.setQuestion(commonMetadata.getPriorDeliveryDateConcept());
		cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);
		
		List<EncounterType> encounterTypes = new ArrayList<EncounterType>();
		encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
		encounterTypes.add(hivMetadata.getARVAdultInitialEncounterType());
		cd.setEncounterTypeList(encounterTypes);
		
		cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
		cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		
		return cd;
	}
	
	/**
	 * PREGNANCY ENROLLED IN THE ART SERVICE These are patients who are pregnant during the
	 * initiation of the process or during ART follow-up and who were notified as a new pregnancy
	 * during follow-up.
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsPregnantEnrolledOnART() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsPregnantEnrolledOnART");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(PregnantQueries.getPregnantWhileOnArt(commonMetadata.getPregnantConcept().getConceptId(), hivMetadata
		        .getGestationConcept().getConceptId(), hivMetadata.getNumberOfWeeksPregnant().getConceptId(), hivMetadata
		        .getPregnancyDueDate().getConceptId(), hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
		    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(), hivMetadata.getPtvEtvProgram()
		            .getProgramId()));
		
		return cd;
	}
	
	/**
	 * Women who gave birth 2 years ago These patients are enrolled in the PMTCT program and have
	 * been updated as a childbirth within 2 years of the reference date
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWhoGaveBirthTwoYearsAgo() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsWhoGaveBirthTwoYearsAgo");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(BreastfeedingQueries.getPatientsWhoGaveBirthTwoYearsAgo(hivMetadata.getPtvEtvProgram().getProgramId(),
		    27));
		
		return cd;
	}
	
	/**
	 * TxNew Breastfeeding Compisition Cohort
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "txNewBreastfeedingComposition")
	public CohortDefinition getTxNewBreastfeedingComposition() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setDescription("breastfeedingComposition");
		cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("DATAPARTO", EptsReportUtils.map(getPatientsWithUpdatedDepartureInART(),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
		cd.addSearch("INICIOLACTANTE", EptsReportUtils.map(
		    genericCohorts.hasCodedObs(hivMetadata.getCriteriaForArtStart(), BaseObsCohortDefinition.TimeModifier.FIRST,
		        SetComparator.IN, Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
		        Arrays.asList(commonMetadata.getBreastfeeding())),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
		cd.addSearch("GRAVIDAS", EptsReportUtils.map(getPatientsPregnantEnrolledOnART(),
		    "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
		cd.addSearch("LACTANTEPROGRAMA",
		    EptsReportUtils.map(getPatientsWhoGaveBirthTwoYearsAgo(), "startDate=${onOrAfter},location=${location}"));
		cd.addSearch("FEMININO", EptsReportUtils.map(genderCohorts.FemaleCohort(), ""));
		cd.addSearch("LACTANTE", EptsReportUtils.map(genericCohorts.hasCodedObs(commonMetadata.getBreastfeeding(),
		    BaseObsCohortDefinition.TimeModifier.LAST, SetComparator.IN,
		    Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()), Arrays.asList(commonMetadata.getYesConcept())),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
		
		String compositionString = "((DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA OR LACTANTE) NOT GRAVIDAS) AND FEMININO";
		
		cd.setCompositionString(compositionString);
		return cd;
	}
	
	/**
	 * Obtain patients from TxNew Union Query TODO: passing the start & end age like this is not
	 * ideal - needs to be refactored to use preferred approach using age cohort
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "txNewUnionNumerator")
	public CohortDefinition getTxNewUnionNumerator(String name, CohortDefinition ageCohort) {
		
		Map<String, Integer> queryParameters = new HashMap<String, Integer>();
		
		queryParameters.put("artProgram", hivMetadata.getARTProgram().getProgramId());
		queryParameters.put("arvPharmaciaEncounter", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
		queryParameters.put("arvAdultoSeguimentoEncounter", hivMetadata.getAdultoSeguimentoEncounterType()
		        .getEncounterTypeId());
		queryParameters.put("arvPediatriaSeguimentoEncounter", hivMetadata.getARVPediatriaSeguimentoEncounterType()
		        .getEncounterTypeId());
		queryParameters.put("arvPlanConcept", hivMetadata.getARVPlanConcept().getConceptId());
		queryParameters.put("startDrugsConcept", hivMetadata.getstartDrugsConcept().getConceptId());
		queryParameters.put("historicalDrugsConcept", hivMetadata.gethistoricalDrugStartDateConcept().getConceptId());
		
		if (ageCohort instanceof AgeCohortDefinition) {
			queryParameters.put("minAge", ((AgeCohortDefinition) ageCohort).getMinAge());
			queryParameters.put("maxAge", ((AgeCohortDefinition) ageCohort).getMaxAge());
		}
		
		SqlCohortDefinition txNewUnionNumerator = new SqlCohortDefinition();
		txNewUnionNumerator.setName(name);
		txNewUnionNumerator.setQuery(TxNewQueries.getTxNewUnionQueries(queryParameters));
		
		txNewUnionNumerator.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("location", "location", Location.class));
		return txNewUnionNumerator;
	}
	
	/**
	 * Build TxNew composition cohort definition
	 * 
	 * @param cohortName Cohort name
	 * @param ageCohort Age Cohort
	 * @return CompositionQuery
	 */
	public CohortDefinition getTxNewCompositionCohort(String cohortName, CohortDefinition ageCohort) {
		CompositionCohortDefinition txNewComposition = new CompositionCohortDefinition();
		txNewComposition.setName(cohortName);
		txNewComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		txNewComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		txNewComposition.addParameter(new Parameter("location", "location", Location.class));
		
		Mapped<CohortDefinition> startedART = Mapped.map(getTxNewUnionNumerator("all patients who started art", ageCohort),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}");
		Mapped<CohortDefinition> transferredIn = Mapped.map(getPatientsTransferredFromOtherHealthFacility(),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}");
		Mapped<CohortDefinition> restartedTreatment = Mapped.map(hivCohortQueries.getPatientsWhoRestartedTreatment(),
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}");
		
		txNewComposition.getSearches().put("startedART", startedART);
		txNewComposition.getSearches().put("transferredIn", transferredIn);
		txNewComposition.getSearches().put("restartedTreatment", restartedTreatment);
		
		txNewComposition.setCompositionString("startedART NOT (transferredIn OR restartedTreatment)");
		return txNewComposition;
	}
	
	/**
	 * @param minAge Minimum age
	 * @param maxAge Maximum age
	 * @return Patients with age in years between {@code minAge} and {@code maxAge} on ART start
	 *         date.
	 */
	public CohortDefinition createXtoYAgeOnArtStartDateCohort(int minAge, int maxAge) {
		String name = "patients with age between " + minAge + " and " + maxAge + " on ART start date";
		// add 1 to maxAge because in getTxNewUnionQueries '<' is used to compare to
		// maxAge
		CohortDefinition ageCohort = ageCohortQueries.createXtoYAgeCohort("", minAge, maxAge + 1);
		CohortDefinition cd = getTxNewCompositionCohort(name, ageCohort);
		cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		return cd;
	}
	
	/**
	 * @param maxAge Maximum age
	 * @return Patients with age in years bellow {@code maxAge} on ART start date.
	 */
	public CohortDefinition createBelowXAgeOnArtStartDateCohort(int maxAge) {
		String name = "patients with age bellow " + maxAge + " on ART start date";
		CohortDefinition ageCohort = ageCohortQueries.createBelowYAgeCohort("", maxAge + 1);
		CohortDefinition cd = getTxNewCompositionCohort(name, ageCohort);
		cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		return cd;
	}
	
	/**
	 * @param minAge Minimum age
	 * @return Patients with age in years equal or above {@code minAge} on ART start date.
	 */
	public CohortDefinition createOverXAgeOnArtStartDateCohort(int minAge) {
		String name = "patients with age over " + minAge + " on ART start date";
		CohortDefinition ageCohort = ageCohortQueries.createOverXAgeCohort("", minAge);
		CohortDefinition cd = getTxNewCompositionCohort(name, ageCohort);
		cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		return cd;
	}
}
