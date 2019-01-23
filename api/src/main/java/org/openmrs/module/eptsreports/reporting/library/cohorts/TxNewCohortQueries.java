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
	
	// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
	// TRATAMENTO) before or on end date
	@DocumentedDefinition(value = "inARTProgramDuringTimePeriod")
	public CohortDefinition getPatientsinARTProgramDuringTimePeriod() {
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod
		        .setQuery("select p.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
		                + "where pg.voided=0 and p.voided=0 and pg.program_id= "
		                + hivMetadata.getARTProgram().getProgramId()
		                + " and pg.date_enrolled <= :onOrBefore and pg.location_id=:location");
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("location", "location", Location.class));
		return inARTProgramDuringTimePeriod;
	}
	
	// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
	// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
	// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
	// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
	// SEGUIMENTO) before or on end date
	@DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
	public CohortDefinition getPatientWithSTARTDRUGSObs() {
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs.setQuery("select p.patient_id "
		        + "from patient p inner join encounter e on p.patient_id=e.patient_id "
		        + "inner join obs o on o.encounter_id=e.encounter_id "
		        + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		        + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ")" + " and o.concept_id="
		        + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded="
		        + hivMetadata.getstartDrugsConcept().getConceptId()
		        + " and e.encounter_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithSTARTDRUGSObs;
	}
	
	// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
	// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
	// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
	// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
	@DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
	public CohortDefinition getPatientWithHistoricalDrugStartDateObs() {
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery("select p.patient_id from patient p "
		        + "inner join encounter e on p.patient_id=e.patient_id inner join obs o on e.encounter_id=o.encounter_id "
		        + "where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
		        + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		        + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId() + " and o.value_datetime is not null "
		        + " and o.value_datetime <= :onOrBefore and e.location_id=:location group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("location", "location", Location.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Looks for patients with first drug pickup (encounter type 18=S.TARV:
	// FARMACIA) before or on end date
	@DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
	public CohortDefinition getPatientWithFirstDrugPickupEncounter() {
		SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
		patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");
		patientWithFirstDrugPickupEncounter
		        .setQuery("select e.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id "
		                + "where p.voided=0 and e.encounter_type= "
		                + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()
		                + " and e.voided=0 and e.encounter_datetime <= :onOrBefore and e.location_id=:location "
		                + "group by p.patient_id");
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientWithFirstDrugPickupEncounter.addParameter(new Parameter("location", "location", Location.class));
		return patientWithFirstDrugPickupEncounter;
	}
	
	// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
	// TRATAMENTO), transferred from other health facility (program workflow state
	// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery("select p.patient_id from patient p "
		        + "inner join patient_program pg on p.patient_id=pg.patient_id "
		        + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
		        + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
		        + hivMetadata.getARTProgram().getProgramId() + " and ps.state="
		        + hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()
		        + " and ps.start_date=pg.date_enrolled"
		        + " and ps.start_date between :onOrAfter and :onOrBefore and location_id=:location "
		        + "group by p.patient_id");
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
	public CohortDefinition getTxNewUnionNumerator(CohortDefinition AgeCohort) {
		
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
		
		if (AgeCohort != null && AgeCohort instanceof AgeCohortDefinition) {
			queryParameters.put("minAge", ((AgeCohortDefinition) AgeCohort).getMinAge());
			queryParameters.put("maxAge", ((AgeCohortDefinition) AgeCohort).getMaxAge());
		}
		
		SqlCohortDefinition txNewUnionNumerator = new SqlCohortDefinition();
		txNewUnionNumerator.setName("TxNewUnionNumerator");
		txNewUnionNumerator.setQuery(TxNewQueries.getTxNewUnionQueries(queryParameters));
		
		txNewUnionNumerator.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		txNewUnionNumerator.addParameter(new Parameter("location", "location", Location.class));
		return txNewUnionNumerator;
	}
	
	/**
	 * Build TxNew composition cohort definition
	 * 
	 * @param cohortName
	 * @param inARTProgramDuringTimePeriod
	 * @param patientWithSTARTDRUGSObs
	 * @param patientWithHistoricalDrugStartDateObs
	 * @param patientsWithDrugPickUpEncounters
	 * @param transferredFromOtherHealthFacility
	 * @param AgeCohort
	 * @param GenderCohort
	 * @return CompositionQuery
	 */
	@DocumentedDefinition(value = "getTxNewCompositionCohort")
	public CohortDefinition getTxNewCompositionCohort(String cohortName, CohortDefinition inARTProgramDuringTimePeriod,
	        CohortDefinition patientWithSTARTDRUGSObs, CohortDefinition patientWithHistoricalDrugStartDateObs,
	        CohortDefinition patientsWithDrugPickUpEncounters, CohortDefinition transferredFromOtherHealthFacility,
	        CohortDefinition restartedTreatment, CohortDefinition AgeCohort, CohortDefinition GenderCohort) {
		CompositionCohortDefinition TxNewComposition = new CompositionCohortDefinition();
		TxNewComposition.setName(cohortName);
		TxNewComposition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		TxNewComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		TxNewComposition.addParameter(new Parameter("location", "location", Location.class));
		TxNewComposition.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}";
		
		TxNewComposition.addSearch("1", EptsReportUtils.map(getTxNewUnionNumerator(AgeCohort), mappings));
		TxNewComposition.addSearch("2", EptsReportUtils.map(transferredFromOtherHealthFacility, mappings));
		TxNewComposition.addSearch("restartedTreatment", EptsReportUtils.map(restartedTreatment,
		    "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
		TxNewComposition.addSearch("baseCohort", EptsReportUtils.map(genericCohorts.getBaseCohort(),
		    "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
		
		String compositionString = "baseCohort AND (1 NOT (2 OR restartedTreatment))";
		
		if (GenderCohort != null) {
			TxNewComposition.addSearch("4", EptsReportUtils.map(GenderCohort, null));
			
			compositionString = compositionString + " AND 4";
		}
		
		TxNewComposition.setCompositionString(compositionString);
		return TxNewComposition;
	}
}
