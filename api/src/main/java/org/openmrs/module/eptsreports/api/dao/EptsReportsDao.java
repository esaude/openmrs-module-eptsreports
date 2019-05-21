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

package org.openmrs.module.eptsreports.api.dao;

import java.util.List;
import org.hibernate.Transaction;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("eptsreports.EptsReportsDao")
public class EptsReportsDao {

  private DbSessionFactory sessionFactory;

  @Autowired
  public EptsReportsDao(DbSessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @SuppressWarnings("unchecked")
  public String getSerializedObjectByReportDesignUUID(String uuid) {
    List<String> list =
        sessionFactory
            .getCurrentSession()
            .createSQLQuery(
                "select concat(report_definition_uuid, '') as uuid from reporting_report_design where reporting_report_design.uuid = ?")
            .setString(0, uuid)
            .list();
    if (!list.isEmpty()) {
      return list.get(0);
    }
    return null;
  }

  public void purgeReportDesign(String designUuid, String serializedObjectUuid) {
    final DbSession session = sessionFactory.getCurrentSession();
    Transaction transaction = session.beginTransaction();
    session
        .createSQLQuery(
            "delete from reporting_report_design_resource "
                + "where reporting_report_design_resource.report_design_id = ("
                + "select id from reporting_report_design where reporting_report_design.uuid = ?)")
        .setString(0, designUuid)
        .executeUpdate();
    session
        .createSQLQuery(
            "delete from reporting_report_design where reporting_report_design.uuid = ?")
        .setString(0, designUuid)
        .executeUpdate();
    session
        .createSQLQuery("delete from serialized_object where uuid = ?")
        .setString(0, serializedObjectUuid)
        .executeUpdate();
    transaction.commit();
  }
}
