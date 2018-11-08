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
import org.springframework.stereotype.Component;

@Component("tbMetadata")
public class TbMetadata extends CommonMetadata {
	
	// CONCEPTS
	private String TUBERCULOSIS_TREATMENT_PLAN = "e1d9fbda-1d5f-11e0-b929-000c29ad1d07";
	
	public Concept getTUBERCULOSIS_TREATMENT_PLANConcept() {
		return getConcept(TUBERCULOSIS_TREATMENT_PLAN);
	}
	
}
