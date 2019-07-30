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

package org.openmrs.module.eptsreports.reporting.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;

/** Epts Reports module utilities */
public class EptsReportUtils {

  /**
   * Purges a Report Definition from the database
   *
   * @param reportManager the Report Definition
   */
  public static void purgeReportDefinition(ReportManager reportManager) {
    ReportDefinition findDefinition = findReportDefinition(reportManager.getUuid());
    ReportDefinitionService reportService =
        (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
    if (findDefinition != null) {
      reportService.purgeDefinition(findDefinition);

      // Purge Global property used to track version of report definition
      String gpName = "reporting.reportManager." + reportManager.getUuid() + ".version";
      GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(gpName);
      if (gp != null) {
        Context.getAdministrationService().purgeGlobalProperty(gp);
      }
    }
  }

  /**
   * Returns the Report Definition matching the provided uuid.
   *
   * @param uuid Report Uuid
   * @throws RuntimeException a RuntimeException if the Report Definition can't be found
   * @return Report Definition object
   */
  public static ReportDefinition findReportDefinition(String uuid) {
    ReportDefinitionService reportService =
        (ReportDefinitionService) Context.getService(ReportDefinitionService.class);
    return reportService.getDefinitionByUuid(uuid);
  }

  /**
   * Setup a Report Definition in a database
   *
   * @param reportManager the Report Definition
   */
  public static void setupReportDefinition(ReportManager reportManager) {
    ReportManagerUtil.setupReport(reportManager);
  }

  /**
   * @param parameterizable
   * @param mappings
   * @param <T>
   * @return
   */
  public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
    if (parameterizable == null) {
      throw new IllegalArgumentException("Parameterizable cannot be null");
    }
    String m = mappings != null ? mappings : ""; // probably not necessary, just to be safe
    return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(m));
  }

  public static String mergeParameterMappings(String... parameters) {
    if (parameters == null || parameters.length == 0) {
      throw new ReportingException("parameters are required");
    }
    LinkedHashSet<String> params = new LinkedHashSet<>();
    for (String p : parameters) {
      params.addAll(new LinkedHashSet<String>(Arrays.asList(p.split(","))));
    }
    return StringUtils.join(params, ",");
  }

  public static String removeMissingParameterMappingsFromCohortDefintion(
      CohortDefinition definition, String mappings) {
    if (definition == null || StringUtils.isEmpty(mappings)) {
      return mappings;
    }
    Iterator<String> mappingsIterator =
        new LinkedHashSet<String>(Arrays.asList(mappings.split(","))).iterator();
    LinkedHashSet<String> existingMappingsSet = new LinkedHashSet<String>();
    while (mappingsIterator.hasNext()) {
      String mapping = mappingsIterator.next();
      for (Parameter p : definition.getParameters()) {
        String paramMap = "${" + p.getName() + "}";
        if (mapping.trim().endsWith(paramMap)) {
          existingMappingsSet.add(mapping);
        }
      }
    }
    return StringUtils.join(existingMappingsSet, ",");
  }

  /**
   * Get the configurable widget parameter to be passed on the reporting UI TODO: redesign this to
   * be more configurable
   *
   * @return
   */
  public static Parameter getProgramConfigurableParameter(Program program) {
    List<ProgramWorkflowState> defaultStates = new ArrayList<>();
    for (ProgramWorkflowState p : program.getAllWorkflows().iterator().next().getStates()) {
      defaultStates.add(p);
    }

    Parameter parameter = new Parameter();
    parameter.setName("state");
    parameter.setLabel("States");
    parameter.setType(ProgramWorkflowState.class);
    parameter.setCollectionType(List.class);
    parameter.setWidgetConfiguration(getProgramProperties(program));
    parameter.setDefaultValue(defaultStates);
    return parameter;
  }

  private static Properties getProgramProperties(Program program) {
    Properties properties = new Properties();
    properties.put("Program", program.getName());
    return properties;
  }

  public static String formatDateWithTime(Date date) {

    Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    return formatter.format(date);
  }

  public static String formatDate(Date date) {

    Format formatter = new SimpleDateFormat("dd-MM-yyyy");

    return formatter.format(date);
  }
}
