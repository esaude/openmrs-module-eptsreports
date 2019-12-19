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

import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupDsdReport extends EptsDataExportManager {
    @Override
    public String getExcelDesignUuid() {
        return "4d9133cc-2240-11ea-9717-ef2401bca520";
    }

    @Override
    public String getUuid() {
        return "5b87d260-2240-11ea-b814-5b98e8621a38";
    }

    @Override
    public String getName() {
        return "DSD Report";
    }

    @Override
    public String getDescription() {
        return "PEPFAR Early Retention Indicators";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
        ReportDesign reportDesign = null;
        try {
            reportDesign =
                    createXlsReportDesign(
                            reportDefinition, "DSD_Report.xls", "DSD-Report", getExcelDesignUuid(), null);
            Properties props = new Properties();
            props.put("sortWeight", "5000");
            reportDesign.setProperties(props);
        } catch (IOException e) {
            throw new ReportingException(e.toString());
        }

        return Arrays.asList(reportDesign);
    }
}
