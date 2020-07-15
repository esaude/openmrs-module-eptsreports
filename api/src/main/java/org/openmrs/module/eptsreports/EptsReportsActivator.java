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

package org.openmrs.module.eptsreports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.eptsreports.api.EptsGlobalPropertyService;
import org.openmrs.module.eptsreports.metadata.ConfigurableMetadataLookupException;
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
    try {
      reportsInitializer.purgeReports();
      EptsGlobalPropertyService eptsGlobalPropertyService =
          Context.getRegisteredComponents(EptsGlobalPropertyService.class).get(0);
      eptsGlobalPropertyService.removeEptsGlobalPropertiesEntries("eptsreports");
      log.debug("EPTS Reports purged");
    } catch (Exception e) {
      log.error("An error occured trying to purge EPTS reports", e);
    }
  }

  /** @see #started() */
  public void started() {
    try {
      reportsInitializer.initializeReports();
      log.info("Started EPTS Reports Module");
    } catch (ConfigurableMetadataLookupException e) {
      Context.getAlertService()
          .notifySuperUsers("eptsreports.startuperror.globalproperties", null, e.getMessage());
      throw e;
    } catch (Exception e) {
      Context.getAlertService().notifySuperUsers("eptsreports.startuperror.general", null);
      throw e;
    }
  }

  /** @see #stopped() */
  public void stopped() {
    log.info("Stopped EPTS Reports Module");
  }
}
