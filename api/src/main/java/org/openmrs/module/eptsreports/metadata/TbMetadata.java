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
package org.openmrs.module.eptsreports.metadata;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.springframework.stereotype.Component;

@Component("tbMetadata")
public class TbMetadata extends CommonMetadata {

	// Concepts
	public Concept getTUBERCULOSIS_TREATMENT_PLANConcept() {
		String uuid = Context
				.getAdministrationService()
				.getGlobalProperty(
						EptsReportConstants.GLOBAL_PROPERTY_TUBERCULOSIS_TREATMENT_PLAN_CONCEPT_UUID);
		return getConcept(uuid);
	}

	// Programs
	public Program getTBProgram() {
		String uuid = Context.getAdministrationService().getGlobalProperty(
				EptsReportConstants.GLOBAL_PROPERTY_TB_PROGRAM_UUID);
		return getProgram(uuid);
	}

}
