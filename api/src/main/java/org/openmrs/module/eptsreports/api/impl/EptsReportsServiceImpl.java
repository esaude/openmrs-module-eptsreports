/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.eptsreports.api.EptsReportsService;
import org.openmrs.module.eptsreports.api.dao.EptsReportsDao;

public class EptsReportsServiceImpl extends BaseOpenmrsService implements EptsReportsService {
	
	private EptsReportsDao dao;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Injected in moduleApplicationContext.xml
	 */
	public void setDao(EptsReportsDao dao) {
		this.dao = dao;
	}
	
}
