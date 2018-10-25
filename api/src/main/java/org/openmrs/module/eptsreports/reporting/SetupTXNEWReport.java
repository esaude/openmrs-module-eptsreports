/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.reporting;

import java.util.Date;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.utils.Cohorts;
import org.openmrs.module.eptsreports.reporting.utils.MetadataLookup;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;

public class SetupTXNEWReport {
	
	private Program ARTProgram;
	
	private Concept ARVPlan;
	
	private Concept startDrugs;
	
	private Concept historicalDrugStartDate;
	
	private EncounterType S_TARV_FARMACIA;
	
	private EncounterType S_TARV_ADULTO_SEGUIMENTO;
	
	private EncounterType S_TARV_PEDIATRIA_SEGUIMENTO;
	
	private ProgramWorkflowState transferredFromOtherHealthFacilityWorkflwoState;
	
	public void setup() throws Exception {
		
		setUpProperties();
		
		ReportDefinition rd = createReportDefinition();
		ReportDesign design = Helper.createRowPerPatientXlsOverviewReportDesign(rd, "TXNEW.xls", "TXNEW.xls_", null);
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,dataset:TX_NEW Data Set");
		props.put("sortWeight", "5000");
		design.setProperties(props);
		Helper.saveReportDesign(design);
		
	}
	
	public void delete() {
		ReportService rs = Context.getService(ReportService.class);
		for (ReportDesign rd : rs.getAllReportDesigns(false)) {
			if ("TXNEW.xls_".equals(rd.getName())) {
				rs.purgeReportDesign(rd);
			}
		}
		Helper.purgeReportDefinition("TX_NEW Report");
	}
	
	private ReportDefinition createReportDefinition() {
		
		ReportDefinition rd = new ReportDefinition();
		rd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		rd.setName("TX_NEW Report");
		
		rd.addDataSetDefinition(createBaseDataSet(), ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
		Helper.saveReportDefinition(rd);
		return rd;
	}
	
	private CohortIndicatorDataSetDefinition createBaseDataSet() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("TX_NEW Data Set");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		createIndicators(dsd);
		return dsd;
	}
	
	private void createIndicators(CohortIndicatorDataSetDefinition dsd) {
		
		// Looks for patients enrolled in ART program (program 2=SERVICO TARV - TRATAMENTO) before or on end date
		
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod.setQuery("select pp.patient_id from patient_program pp where pp.program_id=" + ARTProgram.getProgramId() + " and pp.voided=0   and pp.date_enrolled <= :onOrBefore and " + "(pp.date_completed >= :onOrAfter or pp.date_completed is null)");
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// Looks for patients registered as START DRUGS (answer to question 1255 = ARV PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) before or on end date
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + S_TARV_FARMACIA.getEncounterTypeId() + "," + S_TARV_ADULTO_SEGUIMENTO.getEncounterTypeId() + "," + S_TARV_PEDIATRIA_SEGUIMENTO.getEncounterTypeId() + ") and o.concept_id=" + ARVPlan.getConceptId() + " and o.value_coded=" + startDrugs.getConceptId() + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date		
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + S_TARV_FARMACIA.getEncounterTypeId() + "," + S_TARV_ADULTO_SEGUIMENTO.getEncounterTypeId() + "," + S_TARV_PEDIATRIA_SEGUIMENTO.getEncounterTypeId() + ") and o.concept_id=" + historicalDrugStartDate.getConceptId() + " and o.value_datetime is not null and o.value_datetime<=:onOrBefore group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		// Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY) between start date and end date				
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery("select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=" + ARTProgram.getProgramId() + " and ps.state=" + transferredFromOtherHealthFacilityWorkflwoState.getProgramWorkflowStateId() + " and ps.start_date=pg.date_enrolled and ps.start_date<=:onOrBefore and  ps.start_date>=:onOrAfter");
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("male Patients");
		males.setMaleIncluded(true);
		males.setFemaleIncluded(false);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("female Patients");
		females.setMaleIncluded(false);
		females.setFemaleIncluded(true);
		
		AgeCohortDefinition PatientBelow1Year = patientWithAgeBelow(1);
		PatientBelow1Year.setName("PatientBelow1Year");
		AgeCohortDefinition PatientBetween1And9Years = Cohorts.createXtoYAgeCohort("PatientBetween1And9Years", 1, 9);
		AgeCohortDefinition PatientBetween10And14Years = Cohorts.createXtoYAgeCohort("PatientBetween10And14Years", 10, 14);
		AgeCohortDefinition PatientBetween15And19Years = Cohorts.createXtoYAgeCohort("PatientBetween15And19Years", 15, 19);
		AgeCohortDefinition PatientBetween20And24Years = Cohorts.createXtoYAgeCohort("PatientBetween20And24Years", 20, 24);
		AgeCohortDefinition PatientBetween25And29Years = Cohorts.createXtoYAgeCohort("PatientBetween25And29Years", 25, 29);
		AgeCohortDefinition PatientBetween30And34Years = Cohorts.createXtoYAgeCohort("PatientBetween30And34Years", 30, 34);
		AgeCohortDefinition PatientBetween35And39Years = Cohorts.createXtoYAgeCohort("PatientBetween35And39Years", 35, 39);
		AgeCohortDefinition PatientBetween40And49Years = Cohorts.createXtoYAgeCohort("PatientBetween40And49Years", 40, 49);
		AgeCohortDefinition PatientBetween50YearsAndAbove = patientWithAgeAbove(50);
		PatientBetween50YearsAndAbove.setName("PatientBetween50YearsAndAbove");
		
	}
	
	private void setUpProperties() {
		
		//TODO Configure the following variables as global properties
		
		ARTProgram = MetadataLookup.getProgram("efe2481f-9e75-4515-8d5a-86bfde2b5ad3");
		
		ARVPlan = MetadataLookup.getConcept("e1d9ee10-1d5f-11e0-b929-000c29ad1d07");
		startDrugs = MetadataLookup.getConcept("e1d9ef28-1d5f-11e0-b929-000c29ad1d07");
		historicalDrugStartDate = MetadataLookup.getConcept("e1d8f690-1d5f-11e0-b929-000c29ad1d07");
		
		S_TARV_FARMACIA = MetadataLookup.getEncounterType("e279133c-1d5f-11e0-b929-000c29ad1d07");
		S_TARV_ADULTO_SEGUIMENTO = MetadataLookup.getEncounterType("e278f956-1d5f-11e0-b929-000c29ad1d07");
		S_TARV_PEDIATRIA_SEGUIMENTO = MetadataLookup.getEncounterType("e278fce4-1d5f-11e0-b929-000c29ad1d07");
		
		transferredFromOtherHealthFacilityWorkflwoState = MetadataLookup.getProgramWorkflowState("efe2481f-9e75-4515-8d5a-86bfde2b5ad3", "2", "e1da7d3a-1d5f-11e0-b929-000c29ad1d07");
		
	}
	
	private AgeCohortDefinition patientWithAgeBelow(int age) {
		AgeCohortDefinition patientsWithAgebilow = new AgeCohortDefinition();
		patientsWithAgebilow.setName("patientsWithAgebilow");
		patientsWithAgebilow.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAgebilow.setMaxAge(age - 1);
		patientsWithAgebilow.setMaxAgeUnit(DurationUnit.YEARS);
		return patientsWithAgebilow;
	}
	
	private AgeCohortDefinition patientWithAgeAbove(int age) {
		AgeCohortDefinition patientsWithAge = new AgeCohortDefinition();
		patientsWithAge.setName("patientsWithAge");
		patientsWithAge.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientsWithAge.setMinAge(age);
		patientsWithAge.setMinAgeUnit(DurationUnit.YEARS);
		return patientsWithAge;
	}
	
}
