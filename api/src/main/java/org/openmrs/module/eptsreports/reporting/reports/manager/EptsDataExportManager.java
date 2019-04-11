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

package org.openmrs.module.eptsreports.reporting.reports.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.util.IOUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.util.OpenmrsClassLoader;

/** Excel Data Export Manager for EPTS reports */
public abstract class EptsDataExportManager extends EptsReportManager {

  /** @return the uuid for the report design for exporting to Excel */
  public abstract String getExcelDesignUuid();

  @Override
  public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
    List<ReportDesign> l = new ArrayList<ReportDesign>();
    ReportDesign excelDesign =
        ReportManagerUtil.createExcelDesign(getExcelDesignUuid(), reportDefinition);
    l.add(excelDesign);
    return l;
  }

  protected ReportDesign createExcelTemplateDesign(
      String reportDesignUuid, ReportDefinition reportDefinition, String templatePath) {
    String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
    return ReportManagerUtil.createExcelTemplateDesign(
        reportDesignUuid, reportDefinition, resourcePath);
  }

  /** @return a new ReportDesign for a standard Excel output */
  public ReportDesign createExcelDesignWithProperties(
      String reportDesignUuid, ReportDefinition reportDefinition, Properties props) {
    ReportDesign design = ReportManagerUtil.createExcelDesign(reportDesignUuid, reportDefinition);
    design.setProperties(props);
    return design;
  }

  /**
   * Auto generated method comment
   *
   * @param reportDefinition the reportDesign to set
   * @param resourceName
   * @param reportDesignName
   * @param properties
   * @return
   * @throws IOException
   */
  public ReportDesign createXlsReportDesign(
      ReportDefinition reportDefinition,
      String resourceName,
      String reportDesignName,
      String excelDesignUuid,
      Map<? extends Object, ? extends Object> properties)
      throws IOException {

    ReportService rs = Context.getService(ReportService.class);
    SerializedObjectDAO serializedObjectDAO =
        Context.getRegisteredComponents(SerializedObjectDAO.class).get(0);

    ReportDesign reportDesign = rs.getReportDesignByUuid(excelDesignUuid);
    if (reportDesign != null) {
      if (reportDesign.getReportDefinition() == null) {
        SerializedObject serializedObject =
            serializedObjectDAO.getSerializedObjectByUuid(getUuid());
        reportDesign.setReportDefinition(new ReportDefinition());
        reportDesign.getReportDefinition().setId(serializedObject.getId());
        reportDesign.getReportDefinition().setUuid(serializedObject.getUuid());
        rs.purgeReportDesign(reportDesign);
      }
    }

    ReportDesignResource resource = new ReportDesignResource();
    resource.setName(resourceName);
    resource.setExtension("xls");
    InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(resourceName);
    resource.setContents(IOUtils.toByteArray(is));
    final ReportDesign design = new ReportDesign();
    design.setName(reportDesignName);
    design.setReportDefinition(reportDefinition);
    design.setRendererType(ExcelTemplateRenderer.class);
    design.addResource(resource);
    if (properties != null) {
      design.getProperties().putAll(properties);
    }
    if (excelDesignUuid != null && excelDesignUuid.length() > 1) {
      design.setUuid(excelDesignUuid);
    }
    resource.setReportDesign(design);

    return design;
  }
}
