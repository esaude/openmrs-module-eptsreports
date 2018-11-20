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

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.BreastfeedingQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the SQL Cohort Definition instances we want to expose for EPTS
 */
@Component
public class SqlCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TbMetadata tbMetadata;
	
	@Autowired
	private CommonCohortQueries commonCohortQueries;
	
	// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
	// TRATAMENTO) before or on end date
	@DocumentedDefinition(value = "inARTProgramDuringTimePeriod")
	public CohortDefinition getPatientsinARTProgramDuringTimePeriod() {
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod.setQuery("select pp.patient_id from patient_program pp where pp.program_id="
		        + hivMetadata.getARTProgram().getProgramId() + " and pp.voided=0   and pp.date_enrolled <= :onOrBefore and "
		        + "(pp.date_completed >= :onOrAfter or pp.date_completed is null)");
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
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
		patientWithSTARTDRUGSObs.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded="
		            + hivMetadata.getstartDrugsConcept().getConceptId()
		            + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
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
		patientWithHistoricalDrugStartDateObs.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId()
		            + " and o.value_datetime is not null and o.value_datetime<=:onOrBefore group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
	// TRATAMENTO), transferred from other health facility (program workflow state
	// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public CohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery(
		    "select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
		            + hivMetadata.getARTProgram().getProgramId() + " and ps.state="
		            + hivMetadata.gettransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()
		            + " and ps.start_date=pg.date_enrolled and ps.start_date<=:onOrBefore and  ps.start_date>=:onOrAfter");
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return transferredFromOtherHealthFacility;
	}
	
	// Obtain patients notified to be on TB treatment
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public CohortDefinition getPatientsNotifiedToBeOnTbTreatment() {
		SqlCohortDefinition notifiedToBeOnTbTreatment = new SqlCohortDefinition();
		notifiedToBeOnTbTreatment.setName("notifiedToBeOnTbTreatment");
		notifiedToBeOnTbTreatment.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + tbMetadata.getTUBERCULOSIS_TREATMENT_PLANConcept().getConceptId() + " and o.value_coded="
		            + hivMetadata.getYesConcept().getConceptId()
		            + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return notifiedToBeOnTbTreatment;
	}
	
	/**
	 * Number of adult and pediatric ART patients with a viral load result documented in the patient
	 * medical record and/ or laboratory records in the past 12 months.
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "viralLoadWithin12Months")
	public CohortDefinition getPatientsViralLoadWithin12Months() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("viralLoadWithin12Months");
		sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
		sql.addParameter(new Parameter("endDate", "End Date", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery("SELECT p.patient_id FROM  patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN"
		        + " obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND"
		        + " e.encounter_type IN (" + hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") AND o.concept_id="
		        + hivMetadata.getHivViralLoadConcept().getConceptId() + " AND o.value_numeric IS NOT NULL AND"
		        + " e.encounter_datetime BETWEEN date_add(:endDate, interval -12 MONTH) AND :endDate AND"
		        + " e.location_id=:location");
		return sql;
	}
	
	/**
	 * Adult and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml)
	 * documented in the medical records and /or supporting laboratory results within the past 12 months
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "suppressedViralLoadWithin12Months")
	public CohortDefinition getPatientsWithSuppressedViralLoadWithin12Months() {
		SqlCohortDefinition sql = new SqlCohortDefinition();
		sql.setName("suppressedViralLoadWithin12Months");
		sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
		sql.addParameter(new Parameter("endDate", "End Date", Date.class));
		sql.addParameter(new Parameter("location", "Location", Location.class));
		sql.setQuery("SELECT ultima_carga.patient_id FROM(SELECT p.patient_id,MAX(o.obs_datetime) data_carga"
		        + " FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id"
		        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
		        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN ("
		        + hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		        + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") AND  o.concept_id="
		        + hivMetadata.getHivViralLoadConcept().getConceptId() + " AND o.value_numeric IS NOT NULL AND"
		        + " e.encounter_datetime BETWEEN date_add(:endDate, interval -12 MONTH) and :endDate AND"
		        + " e.location_id=:location GROUP BY p.patient_id" + ") ultima_carga"
		        + " INNER JOIN obs ON obs.person_id=ultima_carga.patient_id AND obs.obs_datetime="
		        + "ultima_carga.data_carga  WHERE obs.voided=0 AND obs.concept_id="
		        + hivMetadata.getHivViralLoadConcept().getConceptId() + " AND obs.location_id=:location AND"
		        + " obs.value_numeric < 1000");
		return sql;
	}
	
	/**
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "registeredBreastFeeding")
	public CohortDefinition registeredBreastFeeding() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Registered Breastfeeding");
		cd.setDescription("Patient with breastfeeding obs collected from encounters");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("hasEncounter",
		    EptsReportUtils.map(commonCohortQueries.hasEncounter(hivMetadata.getAdultoSeguimentoEncounterType()),
		        "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("hasObs",
		    EptsReportUtils.map(commonCohortQueries.hasCodedObs(hivMetadata.getBreastfeeding(), hivMetadata.getYesConcept()),
		        "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.setCompositionString("hasEncounter AND hasObs");
		
		return cd;
	}
	
	/**
	 *
	 */
	@DocumentedDefinition(value = "dil")
	public CohortDefinition dil() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setDescription("dil");// comprise of DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("DATAPARTO", EptsReportUtils.map(patientsWithDateOfBirthUpdatedOnARTService(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("INICIOLACTANTE", EptsReportUtils.map(aRTStartForBeingBreastfeeding(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("LACTANTEPROGRAMA",
		    EptsReportUtils.map(patientsWhoGaveBirthTwoYearsAgo(), "startDate=${startDate},location=${location}"));
		
		cd.setCompositionString("DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA");
		return cd;
		
	}
	
	/**
	 *
	 */
	@DocumentedDefinition(value = "patientsWithDateOfBirthUpdatedOnARTService")
	public CohortDefinition patientsWithDateOfBirthUpdatedOnARTService() {
		DateObsCohortDefinition cd = new DateObsCohortDefinition();
		cd.setName("patientsWithDateOfBirthUpdatedOnARTService");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuestion(hivMetadata.getDateOfDelivery());
		cd.setEncounterTypeList(
		    Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType(), hivMetadata.getARVAdultInitialEncounterType()));
		return cd;
	}
	
	/**
	 *
	 */
	@DocumentedDefinition(value = "aRTStartForBeingBreastfeeding")
	public CohortDefinition aRTStartForBeingBreastfeeding() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		
		cd.setName("aRTStartForBeingBreastfeeding");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("hasEncounter",
		    EptsReportUtils.map(commonCohortQueries.hasEncounter(hivMetadata.getAdultoSeguimentoEncounterType()),
		        "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("hasObs",
		    EptsReportUtils.map(
		        commonCohortQueries.hasCodedObs(hivMetadata.getCriteriaForArtStart(), hivMetadata.getBreastfeeding()),
		        "onOrAfter=${startDate},onOrBefore=${endDate}"));
		cd.setCompositionString("hasObs AND hasEncounter");
		
		return cd;
	}
	
	/**
	 *
	 */
	public CohortDefinition patientsWhoGaveBirthTwoYearsAgo() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("patientsWhoGaveBirthTwoYearsAgo");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(BreastfeedingQueries.getPatientsWhoGaveBirthTwoYearsAgo());
		
		return cd;
	}
	
	/**
	 *
	 */
	public CohortDefinition pregnantsInscribedOnARTService() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setName("pregnantsInscribedOnARTService");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setQuery(BreastfeedingQueries.getPregnantWhileOnArt());
		
		return cd;
	}
	
}
