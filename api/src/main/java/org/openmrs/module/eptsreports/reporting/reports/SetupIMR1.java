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

package org.openmrs.module.eptsreports.reporting.reports;

public class SetupIMR1 {}

/**
 * @author guimino @Component public class SetupIMR1 extends EptsDataExportManager { @Autowired
 *     private IMR1Dataset imr1Dataset; @Override public String getVersion() { return
 *     "1.0-SNAPSHOT"; } @Override public String getUuid() { return
 *     "9fd6ee80-6e51-472f-a54e-ead6105b86db"; } @Override public String getExcelDesignUuid() {
 *     return "d0481386-faf2-4165-a9d0-7d8d0a8a4090"; } @Override public String getName() { return
 *     "IM-ER1 REPORT Reporting Module"; } @Override public String getDescription() { return "Number
 *     of ART Patients who were transferred from another health facility during the reporting
 *     period"; } @Override public ReportDefinition constructReportDefinition() { ReportDefinition
 *     reportDefinition = new ReportDefinition(); reportDefinition.setUuid(getUuid());
 *     reportDefinition.setName(getName()); reportDefinition.setDescription(getDescription());
 *     reportDefinition.setParameters(imr1Dataset.getParameters());
 *     <p>reportDefinition.addDataSetDefinition( "I",
 *     Mapped.mapStraightThrough(this.imr1Dataset.constructIMR1DataSet()));
 *     <p>// add a base cohort here to help in calculations running //
 *     reportDefinition.setBaseCohortDefinition(EptsReportUtils.map( //
 *     this.genericCohortQueries.generalSql("baseCohortQuery", // BaseQueries.getBaseCohortQuery()),
 *     // "endDate=${endDate},location=${location}"));
 *     <p>return reportDefinition; } @Override public List<ReportDesign>
 *     constructReportDesigns(final ReportDefinition reportDefinition) { ReportDesign reportDesign =
 *     null; try { reportDesign = this.createXlsReportDesign( reportDefinition, "IM-ER1-Report.xls",
 *     "IM-ER1-Report", this.getExcelDesignUuid(), null); final Properties props = new Properties();
 *     props.put("sortWeight", "5000"); reportDesign.setProperties(props); } catch (final
 *     IOException e) { throw new ReportingException(e.toString()); }
 *     <p>return Arrays.asList(reportDesign); } }
 */
