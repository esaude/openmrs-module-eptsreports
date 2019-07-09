package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Arrays;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDataset extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  @Autowired
  public SummaryDataQualityDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructSummaryDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings =
        "startDate=${startDate},endDate=${endDate},location=${location},state=${state}";
    dsd.setName("Data Quality Summary Dataset");
    dsd.addParameters(getDataQualityParameters());

    dsd.addColumn(
        "EC1",
        "Total Male Patients who are pregnant",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "Total Male Patients who are pregnant",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC2",
        "Total Male Patients who are breastfeeding",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "Total Male Patients who are breastfeeding",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getBreastfeedingMalePatients(),
                    "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC3",
        "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC4",
        "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId())),
                    "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC5",
        "he patient’s vital status is dead and the patient has a laboratory result (specimen collection date or test order date) after the date of death  or death notification / entry into EPTS.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "he patient’s vital status is dead and the patient has a laboratory result (specimen collection date or test order date) after the date of death  or death notification / entry into EPTS.",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId())),
                    "location=${location}")),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC6",
        "The patient has been identified as transferred out but has an ART pick up date after the transfer out date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicatorsForDataQuality(
                "The patient has been identified as transferred out but has an ART pick up date after the transfer out date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata
                            .getTransferredOutToAnotherHealthFacilityWorkflowState()
                            .getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location}")),
            "location=${location}"),
        "");

    return dsd;
  }
}
