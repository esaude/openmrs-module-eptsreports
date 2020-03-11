package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryEc20DataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryEc20DataQualityDataset extends BaseDataSet {
  private EptsGeneralIndicator eptsGeneralIndicator;
  private SummaryEc20DataQualityCohorts summaryEc20DataQualityCohorts;
  private HivMetadata hivMetadata;

  @Autowired
  public SummaryEc20DataQualityDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      SummaryEc20DataQualityCohorts summaryEc20DataQualityCohorts,
      HivMetadata hivMetadata) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryEc20DataQualityCohorts = summaryEc20DataQualityCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructSummaryEc20DataQualityDatset(List<Parameter> parameterList) {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("EC20 Data Quality Summary Dataset");
    dsd.addParameters(parameterList);

    dsd.addColumn(
        "EC20",
        "The patients who are not enrolled on TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients who are not enrolled on TARV",
                EptsReportUtils.map(
                    summaryEc20DataQualityCohorts.getPatientsNotEnrolledOnTARV(
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
                        hivMetadata.getARTProgram().getProgramId()),
                    "location=${location},endDate=${endDate}")),
            "location=${location},endDate=${endDate}"),
        "");

    return dsd;
  }
}
