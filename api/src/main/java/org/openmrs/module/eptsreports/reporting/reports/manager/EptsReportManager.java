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

package org.openmrs.module.eptsreports.reporting.reports.manager;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.manager.BaseReportManager;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class EptsReportManager extends BaseReportManager {
	
	protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
	}
	
	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToEncounterDataDefinition(pdd));
	}
	
	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		dsd.addColumn(columnName, edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","));
	}
	
	protected void addColumn(ObsDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToObsDataDefinition(pdd));
	}
	
	protected void addColumn(ObsDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		addColumn(dsd, columnName, new EncounterToObsDataDefinition(edd));
	}
	
	protected void addColumn(ObsDataSetDefinition dsd, String columnName, ObsDataDefinition odd) {
		dsd.addColumn(columnName, odd, ObjectUtil.toString(Mapped.straightThroughMappings(odd), "=", ","));
	}
	
}
