package org.openmrs.module.eptsreports.reporting.reports;

import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

@Component
public class SetupMERQuartelyML extends EptsDataExportManager {
    @Override
    public String getExcelDesignUuid() {
        return null;
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public ReportDefinition constructReportDefinition() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }
}
