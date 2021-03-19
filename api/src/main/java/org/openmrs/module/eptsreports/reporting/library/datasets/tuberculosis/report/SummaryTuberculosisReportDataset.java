package org.openmrs.module.eptsreports.reporting.library.datasets.tuberculosis.report;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.tuberculosis.reports.SummaryTBReportsCohortQuery;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryTuberculosisReportDataset extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private SummaryTBReportsCohortQuery summaryTBReportsCohortQuery;

  @Autowired
  public SummaryTuberculosisReportDataset(
      EptsGeneralIndicator eptsGeneralIndicator, SummaryTBReportsCohortQuery summaryCohortQuery) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryTBReportsCohortQuery = summaryCohortQuery;
  }

  public DataSetDefinition constructSummaryDataQualityDatset(List<Parameter> parameterList) {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Tuberculosis Reports Dataset");
    dsd.addParameters(parameterList);
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition summaryCohortQueryPatientsWithTBWhoStartedTPI =
        summaryTBReportsCohortQuery.getPatientsWithTBWhoStartedTPITotal();

    dsd.addColumn(
        "INICIO",
        "INICIO: LISTA DE PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI)",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryPatientsWithTBWhoStartedTPIIndicator",
                EptsReportUtils.map(summaryCohortQueryPatientsWithTBWhoStartedTPI, mappings)),
            mappings),
        "");

    return dsd;
  }
}
