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

package org.openmrs.module.eptsreports.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportInitializer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Initializes all EPTS reports. It can be called by any authenticated user. It is fetched in
	 * read only transaction.
	 * 
	 * @param void
	 * @return
	 * @throws Exception
	 */
	public void initializeReports() throws Exception {
		// remove depricated reports
		removeDepricatedReports();
		
		// setup missing reports
		setupReports();
	}
	
	/**
	 * Remove all depricated EPTS reports. It can be called by any authenticated user. It is fetched
	 * in read only transaction.
	 * 
	 * @param void
	 * @return
	 * @throws APIException
	 */
	private void removeDepricatedReports() throws Exception {
		// loop through list of reports marked as depricated and remove
		// dao.removeReport("1234");
		
		log.info("Removed depricted EPTS reports");
	}
	
	/**
	 * Setup all EPTS reports currently missing. It can be called by any authenticated user. It is
	 * fetched in read only transaction.
	 * 
	 * @param void
	 * @return
	 * @throws APIException
	 */
	private void setupReports() throws Exception {
		log.info("Setup EPTS reports");
	};
}
