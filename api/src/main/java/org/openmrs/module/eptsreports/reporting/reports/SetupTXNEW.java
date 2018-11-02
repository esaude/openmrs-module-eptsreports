/**
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
 **/
package org.openmrs.module.eptsreports.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.DataFactory;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortDefinitionLibrary;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.utils.Indicators;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupTXNEW extends EptsDataExportManager {
	
	@Autowired
	private DataFactory df;
	
	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;
	
	@Autowired
	private HivMetadata hivMetadata;
	
	public SetupTXNEW() {
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public String getUuid() {
		return "74698e1c-cda9-49cf-a58f-cc6771574ee6";
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "05b84f1b-fd23-4b37-8185-aca65be91875";
	}
	
	@Override
	public String getName() {
		return "TX_NEW Report";
	}
	
	@Override
	public String getDescription() {
		return "Number of adults and children newly enrolled on antiretroviral therapy (ART).";
	}
	
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(ReportingConstants.START_DATE_PARAMETER);
		parameters.add(ReportingConstants.END_DATE_PARAMETER);
		return parameters;
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setName("TX_NEW Data Set");
		dsd.addParameter(ReportingConstants.START_DATE_PARAMETER);
		dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		
		// Looks for patients enrolled in ART program (program 2=SERVICO TARV - TRATAMENTO) before or on end date
		
		SqlCohortDefinition inARTProgramDuringTimePeriod = hivCohorts.getPatientsinARTProgramDuringTimePeriod();
		
		// Looks for patients registered as START DRUGS (answer to question 1255 = ARV PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) before or on end date
		SqlCohortDefinition patientWithSTARTDRUGSObs = hivCohorts.getPatientWithSTARTDRUGSObs();
		
		// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date		
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = hivCohorts.getPatientWithHistoricalDrugStartDateObs();
		
		// Looks for patients enrolled on ART program (program 2=SERVICO TARV - TRATAMENTO), transferred from other health facility (program workflow state is 29=TRANSFER FROM OTHER FACILITY) between start date and end date				
		SqlCohortDefinition transferredFromOtherHealthFacility = hivCohorts.getPatientsTransferredFromOtherHealthFacility();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("male Patients");
		males.setMaleIncluded(true);
		males.setFemaleIncluded(false);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("female Patients");
		females.setMaleIncluded(false);
		females.setFemaleIncluded(true);
		
		AgeCohortDefinition PatientBelow1Year = df.patientWithAgeBelow(1);
		PatientBelow1Year.setName("PatientBelow1Year");
		AgeCohortDefinition PatientBetween1And9Years = df.createXtoYAgeCohort("PatientBetween1And9Years", 1, 9);
		AgeCohortDefinition PatientBetween10And14Years = df.createXtoYAgeCohort("PatientBetween10And14Years", 10, 14);
		AgeCohortDefinition PatientBetween15And19Years = df.createXtoYAgeCohort("PatientBetween15And19Years", 15, 19);
		AgeCohortDefinition PatientBetween20And24Years = df.createXtoYAgeCohort("PatientBetween20And24Years", 20, 24);
		AgeCohortDefinition PatientBetween25And29Years = df.createXtoYAgeCohort("PatientBetween25And29Years", 25, 29);
		AgeCohortDefinition PatientBetween30And34Years = df.createXtoYAgeCohort("PatientBetween30And34Years", 30, 34);
		AgeCohortDefinition PatientBetween35And39Years = df.createXtoYAgeCohort("PatientBetween35And39Years", 35, 39);
		AgeCohortDefinition PatientBetween40And49Years = df.createXtoYAgeCohort("PatientBetween40And49Years", 40, 49);
		AgeCohortDefinition PatientBetween50YearsAndAbove = df.patientWithAgeAbove(50);
		PatientBetween50YearsAndAbove.setName("PatientBetween50YearsAndAbove");
		
		ArrayList<AgeCohortDefinition> agesRange = new ArrayList<AgeCohortDefinition>();
		// agesRange.add(PatientBelow1Year);
		// agesRange.add(PatientBetween1And9Years);
		agesRange.add(PatientBetween10And14Years);
		agesRange.add(PatientBetween15And19Years);
		agesRange.add(PatientBetween20And24Years);
		agesRange.add(PatientBetween25And29Years);
		agesRange.add(PatientBetween30And34Years);
		agesRange.add(PatientBetween35And39Years);
		agesRange.add(PatientBetween40And49Years);
		agesRange.add(PatientBetween50YearsAndAbove);
		
		// Male and Female <1
		
		CompositionCohortDefinition patientBelow1YearEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBelow1YearEnrolledInHIVStartedART.setName("patientBelow1YearEnrolledInHIVStartedART");
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientBelow1YearEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(PatientBelow1Year, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBelow1YearEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		
		CohortIndicator patientBelow1YearEnrolledInHIVStartedARTIndicator = Indicators.newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator", patientBelow1YearEnrolledInHIVStartedART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn("1<1", "TX_NEW: New on ART: Patients below 1 year", new Mapped(patientBelow1YearEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		// Male and Female between 1 and 9 years
		
		CompositionCohortDefinition patientBetween1And9YearsEnrolledInHIVStartedART = new CompositionCohortDefinition();
		patientBetween1And9YearsEnrolledInHIVStartedART.setName("patientBetween1And9YearsEnrolledInHIVStartedART");
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		
		patientBetween1And9YearsEnrolledInHIVStartedART.getSearches().put("5", new Mapped<CohortDefinition>(PatientBetween1And9Years, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
		patientBetween1And9YearsEnrolledInHIVStartedART.setCompositionString("((1 or 2 or 3) and (not 4)) and 5");
		
		CohortIndicator patientBetween1And9YearsEnrolledInHIVStartedARTIndicator = Indicators.newCohortIndicator("patientBelow1YearEnrolledInHIVStartedARTIndicator", patientBetween1And9YearsEnrolledInHIVStartedART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));
		
		dsd.addColumn("119", "TX_NEW: New on ART: Patients Between 1 and 9 years", new Mapped(patientBetween1And9YearsEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		// Male
		
		int i = 2;
		for (AgeCohortDefinition ageCohort : agesRange) {
			CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
			patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			
			patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(males, null));
			patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 or 2 or 3) and (not 4)) and 5 and 6");
			
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = Indicators.newCohortIndicator("patientInYearRangeEnrolledInHIVStartedARTIndicator", patientInYearRangeEnrolledInARTStarted, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));
			
			dsd.addColumn("1M" + i, "Males:TX_NEW: New on ART by age and sex: " + ageCohort.getName(), new Mapped(patientInYearRangeEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
			
			i++;
		}
		
		// Female
		
		int j = 2;
		for (AgeCohortDefinition ageCohort : agesRange) {
			CompositionCohortDefinition patientInYearRangeEnrolledInARTStarted = new CompositionCohortDefinition();
			patientInYearRangeEnrolledInARTStarted.setName("patientInYearRangeEnrolledInHIVStarted");
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
			patientInYearRangeEnrolledInARTStarted.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
			
			patientInYearRangeEnrolledInARTStarted.getSearches().put("5", new Mapped<CohortDefinition>(ageCohort, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}")));
			patientInYearRangeEnrolledInARTStarted.getSearches().put("6", new Mapped<CohortDefinition>(females, null));
			patientInYearRangeEnrolledInARTStarted.setCompositionString("((1 or 2 or 3) and (not 4)) and 5 and 6");
			
			CohortIndicator patientInYearRangeEnrolledInHIVStartedARTIndicator = Indicators.newCohortIndicator("patientInYearRangeEnrolledInHIVStartedARTIndicator", patientInYearRangeEnrolledInARTStarted, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate},effectiveDate=${endDate}"));
			
			dsd.addColumn("1F" + j, "Females:TX_NEW: New on ART by age and sex: " + ageCohort.getName(), new Mapped(patientInYearRangeEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
			
			j++;
		}
		
		CompositionCohortDefinition patientEnrolledInART = new CompositionCohortDefinition();
		patientEnrolledInART.setName("patientEnrolledInART");
		patientEnrolledInART.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientEnrolledInART.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		patientEnrolledInART.getSearches().put("1", new Mapped<CohortDefinition>(inARTProgramDuringTimePeriod, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("2", new Mapped<CohortDefinition>(patientWithSTARTDRUGSObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("3", new Mapped<CohortDefinition>(patientWithHistoricalDrugStartDateObs, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.getSearches().put("4", new Mapped<CohortDefinition>(transferredFromOtherHealthFacility, ParameterizableUtil.createParameterMappings("onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}")));
		patientEnrolledInART.setCompositionString("(1 or 2 or 3) and (not 4)");
		
		CohortIndicator patientEnrolledInHIVStartedARTIndicator = Indicators.newCohortIndicator("patientNewlyEnrolledInHIVIndicator", patientEnrolledInART, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("1All", "TX_NEW: New on ART", new Mapped(patientEnrolledInHIVStartedARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		// Obtain patients notified to be on TB treatment 
		SqlCohortDefinition notifiedToBeOnTbTreatment = new SqlCohortDefinition();
		notifiedToBeOnTbTreatment.setName("notifiedToBeOnTbTreatment");
		notifiedToBeOnTbTreatment.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + "," + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id=" + hivMetadata.getTUBERCULOSIS_TREATMENT_PLANConcept().getConceptId() + " and o.value_coded=" + hivMetadata.getYesConcept().getConceptId() + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		CohortIndicator tuberculosePatientNewlyInitiatingARTIndicator = Indicators.newCohortIndicator("tuberculosePatientNewlyInitiatingARTIndicator", notifiedToBeOnTbTreatment, ParameterizableUtil.createParameterMappings("onOrAfter=${startDate},onOrBefore=${endDate}"));
		
		dsd.addColumn("TB", "TX_NEW: TB Started ART", new Mapped(tuberculosePatientNewlyInitiatingARTIndicator, ParameterizableUtil.createParameterMappings("startDate=${startDate},endDate=${endDate}")), "");
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		reportDefinition.addDataSetDefinition(dsd, ParameterizableUtil.createParameterMappings("endDate=${endDate},startDate=${startDate}"));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign rd = null;
		try {
			rd = createRowPerPatientXlsOverviewReportDesign(reportDefinition, "TXNEW.xls", "TXNEW.xls_", null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,dataset:TX_NEW Data Set");
			props.put("sortWeight", "5000");
			rd.setProperties(props);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Arrays.asList(rd);
	}
	
}
