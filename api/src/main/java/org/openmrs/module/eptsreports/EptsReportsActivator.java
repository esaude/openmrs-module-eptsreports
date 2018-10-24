/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.eptsreports.reporting.EptsReportInitializer;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class EptsReportsActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private EptsReportInitializer reportsInitializer = new EptsReportInitializer();
	
	@Override
	public void contextRefreshed() {
		log.debug("EPTS Reports Module refreshed");
	}
	
	@Override
	public void willRefreshContext() {
		log.debug("Refreshing EPTS Reports Module");
	}
	
	@Override
	public void willStart() {
		log.debug("Starting EPTS Reports Module");
	}
	
	@Override
	public void willStop() {
		log.debug("Stopping EPTS Reports Module");
	}
	
	/**
	 * @see #started()
	 */
	public void started() {
		try {
			reportsInitializer.initializeReports();
			log.info("Started EPTS Reports Module");
		}
		catch (Exception e) {
			log.error("An error occured trying to initialize EPTS reports", e);
		}
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown EPTS Reports Module");
	}
}
