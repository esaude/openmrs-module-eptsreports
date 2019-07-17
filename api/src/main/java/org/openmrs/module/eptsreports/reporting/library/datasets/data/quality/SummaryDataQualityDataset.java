package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Arrays;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDataset extends DataQualityBaseDataset {

  private EptsGeneralIndicator eptsGeneralIndicator;

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public SummaryDataQualityDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      GenericCohortQueries genericCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
    this.genericCohortQueries = genericCohortQueries;
  }

  public DataSetDefinition constructSummaryDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Data Quality Summary Dataset");
    dsd.addParameters(getDataQualityParameters());

    dsd.addColumn(
        "EC1",
        "Total Male Patients who are pregnant",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total Male Patients who are pregnant",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC2",
        "Total Male Patients who are breastfeeding",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total Male Patients who are breastfeeding",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getBreastfeedingMalePatients(),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC3",
        "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getDeadOrDeceasedPatientsHavingEncountersAfter(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC4",
        "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getDeadOrDeceasedPatientsHavingEncountersAfter(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC5",
        "he patient’s vital status is dead and the patient has a laboratory result (specimen collection date or test order date) after the date of death  or death notification / entry into EPTS.",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "he patient’s vital status is dead and the patient has a laboratory result (specimen collection date or test order date) after the date of death  or death notification / entry into EPTS.",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getDeadOrDeceasedPatientsHavingEncountersAfter(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC6",
        "The patient has been identified as transferred out but has an ART pick up date after the transfer out date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as transferred out but has an ART pick up date after the transfer out date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata
                            .getTransferredOutToAnotherHealthFacilityWorkflowState()
                            .getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC7",
        "The patient has been identified as transferred out but has an clinical consultation date after the transfer out date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as transferred out but has an clinical consultation date after the transfer out date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata
                            .getTransferredOutToAnotherHealthFacilityWorkflowState()
                            .getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId(),
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC8",
        "The patient has been identified as transferred out but has an laboratory results(specimen collection date or report date) after the transfer out date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as transferred out but has an laboratory results(specimen collection date or report date) after the transfer out date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata
                            .getTransferredOutToAnotherHealthFacilityWorkflowState()
                            .getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC9",
        "The patient has been identified as abandoned  but has an ART pick up date after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned  but has an ART pick up date after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC10",
        "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC10",
        "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC11",
        "The patient has been identified as abandoned but has an laboratory results(specimen collection date or report date) after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned but has an laboratory results(specimen collection date or report date) after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                        Arrays.asList(
                            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId())),
                    "location=${location}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location}"),
        "");

    dsd.addColumn(
        "EC12",
        "The patient’s date of birth, estimated date of birth or entered age indicate the patient was born before 1920",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s date of birth, estimated date of birth or entered age indicate the patient was born before 1920",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseBirthdateIsBeforeYear(1920), ""),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            ""),
        "");

    dsd.addColumn(
        "EC13",
        "The patients date of birth, estimated date of birth or age is negative",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients date of birth, estimated date of birth or age is negative ",
                EptsReportUtils.map(summaryDataQualityCohorts.getPatientsWithNegativeAge(), ""),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            ""),
        "");

    dsd.addColumn(
        "EC14",
        "The patients birth, estimated date of birth or age indicates they are > 100 years of age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients birth, estimated date of birth or age indicates they are > 100 years of age",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithAgeHigherThanXyears(100),
                    "endDate=${endDate}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "endDate=${endDate}"),
        "");

    dsd.addColumn(
        "EC15",
        "The patient’s date of birth is after any drug pick up date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s date of birth is after any drug pick up date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseBirthDatesAreAfterDrugPickUp(
                        Arrays.asList(
                            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())),
                    "location=${location},endDate=${endDate}"),
                Arrays.asList(genericCohortQueries.getArtProgramConfigurableParameter())),
            "location=${location},endDate=${endDate}"),
        "");

    return dsd;
  }
}
