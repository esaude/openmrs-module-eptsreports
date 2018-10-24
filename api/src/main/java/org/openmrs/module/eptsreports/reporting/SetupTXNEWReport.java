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
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.utils.MetadataLookup;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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
	
	private EncounterType S_TARV_FARMACIA;
	
	private EncounterType S_TARV_ADULTO_SEGUIMENTO;
	
	private EncounterType S_TARV_PEDIATRIA_SEGUIMENTO;
	
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
		
		//Looks for patients enrolled in ART program (program 2=SERVICO TARV - TRATAMENTO) before or on end date
		
		SqlCohortDefinition inARTProgramDuringTimePeriod = new SqlCohortDefinition();
		inARTProgramDuringTimePeriod.setName("enrolledInART");
		
		inARTProgramDuringTimePeriod.setQuery("select pp.patient_id from patient_program pp where pp.program_id=" + ARTProgram.getProgramId() + " and pp.voided=0   and pp.date_enrolled <= :onOrBefore and " + "(pp.date_completed >= :onOrAfter or pp.date_completed is null)");
		
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithARTStartDate");
		patientWithSTARTDRUGSObs.setQuery("Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (" + S_TARV_FARMACIA.getEncounterTypeId() + "," + S_TARV_ADULTO_SEGUIMENTO.getEncounterTypeId() + "," + S_TARV_PEDIATRIA_SEGUIMENTO.getEncounterTypeId() + ") and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<=:endDate group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		
	}
	
	private void setUpProperties() {
		ARTProgram = MetadataLookup.getProgram("efe2481f-9e75-4515-8d5a-86bfde2b5ad3");
		ARVPlan = MetadataLookup.getConcept("e1d9ee10-1d5f-11e0-b929-000c29ad1d07");
		startDrugs = MetadataLookup.getConcept("e1d9ef28-1d5f-11e0-b929-000c29ad1d07");
		
		S_TARV_FARMACIA = MetadataLookup.getEncounterType("e279133c-1d5f-11e0-b929-000c29ad1d07");
		S_TARV_ADULTO_SEGUIMENTO = MetadataLookup.getEncounterType("e278f956-1d5f-11e0-b929-000c29ad1d07");
		S_TARV_PEDIATRIA_SEGUIMENTO = MetadataLookup.getEncounterType("e278fce4-1d5f-11e0-b929-000c29ad1d07");
		
	}
	
}
