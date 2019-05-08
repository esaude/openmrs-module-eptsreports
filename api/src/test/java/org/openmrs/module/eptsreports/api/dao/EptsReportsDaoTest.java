/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * OpenMRS is also distributed under the terms of the Healthcare Disclaimer located at
 * http://openmrs.org/license.
 *
 * <p>Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS graphic logo is a
 * trademark of OpenMRS Inc.
 */
package org.openmrs.module.eptsreports.api.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * It is an integration test (extends BaseModuleContextSensitiveTest), which verifies DAO methods
 * against the in-memory H2 database. The database is initially loaded with data from
 * standardTestDataset.xml in openmrs-api. All test methods are executed in transactions, which are
 * rolled back by the end of each test method.
 */
public class EptsReportsDaoTest extends BaseModuleContextSensitiveTest {

  @Autowired private EptsReportsDao dao;

  @Before
  public void setUp() throws Exception {
    executeDataSet("eptsReportsDaoTest.xml");
  }

  @Test
  public void getSerializedObjectByReportDesignUUIDShouldReturnSerializedObjectUuid() {
    String serializedObjectUuid = "d5f02dc5-5b94-449e-b894-6282f5f26414";
    String reportDesignUuid = "f4f0045b-3a64-4533-a6b6-9c6c8072fe3f";
    assertEquals(serializedObjectUuid, dao.getSerializedObjectByReportDesignUUID(reportDesignUuid));
  }

  @Test
  @Ignore
  public void purgeReportDesignShouldPurgeReportDesignResource() {
    fail("Not yet implemented");
  }

  @Test
  @Ignore
  public void purgeReportDesignShouldPurgeReportDesign() {
    fail("Not yet implemented");
  }

  @Test
  @Ignore
  public void purgeReportDesignShouldPurgeSerializedObject() {
    fail("Not yet implemented");
  }
}
