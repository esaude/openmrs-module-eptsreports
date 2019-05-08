package org.openmrs.module.eptsreports.reporting.reports;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.api.EptsReportsService;
import org.openmrs.module.eptsreports.reporting.reports.manager.EptsDataExportManager;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EptsDataExportManagerTest extends PowerMockBaseContextTest {

  private SampleReport sampleReport = new SampleReport();

  private ReportDefinition rd;

  @Mock private EptsReportsService eptsReportsService;

  @Before
  public void init() {
    rd = sampleReport.constructReportDefinition();
    PowerMockito.mockStatic(Context.class);
  }

  @Test
  public void constructReportDesignsShouldBuildExcelReportDesignFromDefinition() {
    List<ReportDesign> designs = sampleReport.constructReportDesigns(rd);
    assertEquals(1, designs.size());
    assertEquals("07128f84-716c-11e9-a923-1681be663d3e", designs.get(0).getUuid());
    assertEquals("Excel", designs.get(0).getName());
    assertEquals(rd, designs.get(0).getReportDefinition());
    assertEquals(XlsReportRenderer.class, designs.get(0).getRendererType());
    assertEquals(
        "true",
        designs
            .get(0)
            .getProperties()
            .getProperty(XlsReportRenderer.INCLUDE_DATASET_NAME_AND_PARAMETERS_PROPERTY));
  }

  @Test
  public void createXlsReportDesignShouldBuildXlsReportDesignFromReportDefinition()
      throws IOException {
    when(Context.getService(EptsReportsService.class)).thenReturn(eptsReportsService);
    ReportDesign design =
        sampleReport.createXlsReportDesign(
            rd, "TXCURR_2.1.xls", "sampleFromTxCurr", "07129240-716c-11e9-a923-1681be663d3e", null);
    assertEquals("07129240-716c-11e9-a923-1681be663d3e", design.getUuid());
    assertEquals("sampleFromTxCurr", design.getName());
    assertEquals(rd, design.getReportDefinition());
    assertEquals(1, design.getResources().size());
    assertEquals(ExcelTemplateRenderer.class, design.getRendererType());
  }

  private class SampleReport extends EptsDataExportManager {
    @Override
    public String getExcelDesignUuid() {
      return "07128f84-716c-11e9-a923-1681be663d3e";
    }

    @Override
    public String getUuid() {
      return "0712939e-716c-11e9-a923-1681be663d3e";
    }

    @Override
    public String getName() {
      return "Sample Report";
    }

    @Override
    public String getDescription() {
      return "Sample unit test report";
    }

    @Override
    public String getVersion() {
      return "1.0-SNAPSHOT";
    }

    @Override
    public ReportDefinition constructReportDefinition() {
      ReportDefinition reportDefinition = new ReportDefinition();
      reportDefinition.setUuid(getUuid());
      reportDefinition.setName(getName());
      reportDefinition.setDescription(getDescription());
      return reportDefinition;
    }
  }
}
