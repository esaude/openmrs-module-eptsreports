/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * OpenMRS is also distributed under the terms of the Healthcare Disclaimer located at
 * http://openmrs.org/license.
 *
 * <p>Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS graphic logo is a
 * trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.eptsreports.api.dao.EptsReportsDao;
import org.openmrs.module.eptsreports.api.impl.EptsReportsServiceImpl;

/**
 * This is a unit test, which verifies logic in EPTSreportsService. It doesn't extend
 * BaseModuleContextSensitiveTest, thus it is run without the in-memory DB and Spring context.
 */
public class EptsReportsServiceTest {

  @InjectMocks private EptsReportsServiceImpl basicModuleService;

  @Mock private EptsReportsDao dao;

  @Before
  public void setupMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void purgeReportDesignIfExistsShouldPurgeReportDesign() {
    String uuid = "bed6c7a3-9506-4c98-be6e-2861c804005e";
    String serializedObjectUuid = "f0b2ce17-57ff-4f10-867e-0257c7515b2a";
    when(dao.getSerializedObjectByReportDesignUUID(uuid)).thenReturn(serializedObjectUuid);
    basicModuleService.purgeReportDesignIfExists(uuid);
    verify(dao).purgeReportDesign(uuid, serializedObjectUuid);
  }
}
