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
import java.util.concurrent.TimeUnit;
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
  public static void purgeReportDefinition(final ReportManager reportManager) {
    final ReportDefinition findDefinition = findReportDefinition(reportManager.getUuid());
    final ReportDefinitionService reportService = Context.getService(ReportDefinitionService.class);
    if (findDefinition != null) {
      reportService.purgeDefinition(findDefinition);

      // Purge Global property used to track version of report definition
      final String gpName = "reporting.reportManager." + reportManager.getUuid() + ".version";
      final GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(gpName);
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
  public static ReportDefinition findReportDefinition(final String uuid) {
    final ReportDefinitionService reportService = Context.getService(ReportDefinitionService.class);
    return reportService.getDefinitionByUuid(uuid);
  }

  /**
   * Setup a Report Definition in a database
   *
   * @param reportManager the Report Definition
   */
  public static void setupReportDefinition(final ReportManager reportManager) {
    ReportManagerUtil.setupReport(reportManager);
  }

  /**
   * @param parameterizable
   * @param mappings
   * @param <T>
   * @return
   */
  public static <T extends Parameterizable> Mapped<T> map(
      final T parameterizable, final String mappings) {
    if (parameterizable == null) {
      throw new IllegalArgumentException("Parameterizable cannot be null");
    }
    final String m = mappings != null ? mappings : ""; // probably not
    // necessary,
    // just to be safe
    return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(m));
  }

  public static String mergeParameterMappings(final String... parameters) {
    if ((parameters == null) || (parameters.length == 0)) {
      throw new ReportingException("parameters are required");
    }
    final LinkedHashSet<String> params = new LinkedHashSet<>();
    for (final String p : parameters) {
      params.addAll(new LinkedHashSet<String>(Arrays.asList(p.split(","))));
    }
    return StringUtils.join(params, ",");
  }

  public static String removeMissingParameterMappingsFromCohortDefintion(
      final CohortDefinition definition, final String mappings) {
    if ((definition == null) || StringUtils.isEmpty(mappings)) {
      return mappings;
    }
    final Iterator<String> mappingsIterator =
        new LinkedHashSet<String>(Arrays.asList(mappings.split(","))).iterator();
    final LinkedHashSet<String> existingMappingsSet = new LinkedHashSet<String>();
    while (mappingsIterator.hasNext()) {
      final String mapping = mappingsIterator.next();
      for (final Parameter p : definition.getParameters()) {
        final String paramMap = "${" + p.getName() + "}";
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
  public static Parameter getProgramConfigurableParameter(final Program program) {
    final List<ProgramWorkflowState> defaultStates = new ArrayList<>();
    for (final ProgramWorkflowState p : program.getAllWorkflows().iterator().next().getStates()) {
      defaultStates.add(p);
    }

    final Parameter parameter = new Parameter();
    parameter.setName("state");
    parameter.setLabel("States");
    parameter.setType(ProgramWorkflowState.class);
    parameter.setCollectionType(List.class);
    parameter.setWidgetConfiguration(getProgramProperties(program));
    parameter.setDefaultValue(defaultStates);
    return parameter;
  }

  private static Properties getProgramProperties(final Program program) {
    final Properties properties = new Properties();
    properties.put("Program", program.getName());
    return properties;
  }

  public static String formatDateWithTime(final Date date) {

    final Format formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    return formatter.format(date);
  }

  public static String formatDate(final Date date) {

    final Format formatter = new SimpleDateFormat("dd-MM-yyyy");

    return formatter.format(date);
  }

  public static long getDifferenceInDaysBetweenDates(final Date first, final Date last) {
    final long diffInMillies = first.getTime() - last.getTime();
    final long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

    return days;
  }
}
