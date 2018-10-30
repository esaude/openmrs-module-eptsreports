/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.eptsreports.reporting.SetupTXCURRReport;
import org.openmrs.module.eptsreports.reporting.SetupTXNEWReport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class EptsReportsController {

	public Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/module/eptsreports/eptsreports", method = RequestMethod.GET)
	public void manage() {
	}

	@RequestMapping("/module/eptsreports/register_TXNEW")
	public ModelAndView registerTXNEW() throws Exception {
		new SetupTXNEWReport().setup();
		return new ModelAndView(new RedirectView("eptsreports.form"));
	}

	@RequestMapping("/module/eptsreports/remove_TXNEW")
	public ModelAndView removeTXNEW() throws Exception {
		new SetupTXNEWReport().delete();
		return new ModelAndView(new RedirectView("eptsreports.form"));
	}

	@RequestMapping("/module/eptsreports/register_TXCURR")
	public ModelAndView registerTXCURR() throws Exception {
		new SetupTXCURRReport().setup();
		return new ModelAndView(new RedirectView("cmrreports.form"));
	}

	@RequestMapping("/module/eptsreports/remove_TXCURR")
	public ModelAndView removeTXCURR() throws Exception {
		new SetupTXCURRReport().delete();
		return new ModelAndView(new RedirectView("cmrreports.form"));
	}

}
