package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryCohortQuery;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
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
public class SummaryDataQualityDataset extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private SummaryDataQualityCohorts summaryDataQualityCohorts;
  private HivMetadata hivMetadata;

  public static int ENCONTER_TYPE_FSR = 51;
  public static int SAMPLE_COLLECTION_DATE = 23821;
  public static int DATE_OF_APPLICATION_OF_LABORATORY_TESTS = 6246;

  private SummaryCohortQuery summaryCohortQuery;

  @Autowired
  public SummaryDataQualityDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      SummaryDataQualityCohorts summaryDataQualityCohorts,
      HivMetadata hivMetadata,
      SummaryCohortQuery summaryCohortQuery) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.summaryCohortQuery = summaryCohortQuery;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructSummaryDataQualityDatset(List<Parameter> parameterList) {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Data Quality Summary Dataset");
    dsd.addParameters(parameterList);
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition summaryCohortQueryEC1 = summaryCohortQuery.getEC1Total();
    final CohortDefinition summaryCohortQueryEC2 = summaryCohortQuery.getEC2Total();
    final CohortDefinition summaryCohortQueryEC5 = summaryCohortQuery.getEC5Total();
    final CohortDefinition summaryCohortQueryEC6 = summaryCohortQuery.getEC6Total();
    final CohortDefinition summaryCohortQueryEC7 = summaryCohortQuery.getEC7Total();
    final CohortDefinition summaryCohortQueryEC8 = summaryCohortQuery.getEC8Total();
    final CohortDefinition summaryCohortQueryEC9 = summaryCohortQuery.getEC9Total();
    final CohortDefinition summaryCohortQueryEC15 = summaryCohortQuery.getEC15Total();
    final CohortDefinition summaryCohortQueryEC20 = summaryCohortQuery.getEC20Total();
    final CohortDefinition summaryCohortQueryEC23 = summaryCohortQuery.getEC23Total();

    dsd.addColumn(
        "EC1",
        "EC1: The patient’s sex is male and the patient is pregnant.",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC1Indicator",
                EptsReportUtils.map(summaryCohortQueryEC1, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC2",
        "EC2:The patient’s sex is male and the patient is breastfeeding.",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC2Indicator",
                EptsReportUtils.map(summaryCohortQueryEC2, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC3",
        "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s vital status is dead and the patient has an ART pick up date after the date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getDeadOrDeceasedPatientsHavingEncountersAfterEC3(),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC4",
        "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s vital status is dead and the patient has a clinical consultation after date of death or death notification",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getDeadOrDeceasedPatientsHavingEncountersAfterEC4(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
                        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                        hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                        hivMetadata.getStateOfStayPriorArtPatient().getConceptId(),
                        hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
                        hivMetadata.getPatientHasDiedConcept().getConceptId(),
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId(),
                            hivMetadata.getMasterCardEncounterType().getEncounterTypeId())),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC5",
        "EC5:The patient’s vital status is dead and the patient has a laboratory result (specimen collection date or test order date) after the date of death  or death notification ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC5Indicator",
                EptsReportUtils.map(summaryCohortQueryEC5, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC6",
        "EC6:The patient has been identified as transferred out but has an ART pick up date after the transfer out date",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC6Indicator",
                EptsReportUtils.map(summaryCohortQueryEC6, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC7",
        "EC7:The patient has been identified as transferred out but has an clinical consultation date after the transfer out date",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC7Indicator",
                EptsReportUtils.map(summaryCohortQueryEC7, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC8",
        "EC8:The patient has been identified as transferred out but has an laboratory results(specimen collection date or report date) after the transfer out date ",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC8Indicator",
                EptsReportUtils.map(summaryCohortQueryEC8, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC9",
        "EC9:The patient has been identified as abandoned  but has an ART pick up date after the abandoned date",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC9Indicator",
                EptsReportUtils.map(summaryCohortQueryEC9, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC10",
        "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned but has an clinical consultation date after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncountersEC10(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC11",
        "The patient has been identified as abandoned but has an laboratory results(specimen collection date or report date) after the abandoned date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient has been identified as abandoned but has an laboratory results(specimen collection date or report date) after the abandoned date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithStatesAndEncountersEC11(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                        hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                        ENCONTER_TYPE_FSR,
                        SAMPLE_COLLECTION_DATE,
                        DATE_OF_APPLICATION_OF_LABORATORY_TESTS),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC12",
        "The patient’s date of birth, estimated date of birth or entered age indicate the patient was born before 1920",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patient’s date of birth, estimated date of birth or entered age indicate the patient was born before 1920",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseBirthdateIsBeforeYear(1920),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC13",
        "The patients date of birth, estimated date of birth or age is negative",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients date of birth, estimated date of birth or age is negative",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWithNegativeAge(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC14",
        "The value of a date field registered on any form, with the exception of consultation date, is before 1985 ",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The value of a date field registered on any form, with the exception of consultation date, is before 1985 ",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getCountPatientsWithExceptionConsultation(),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC15",
        "EC15:The patient’s date of birth is after any drug pick up date",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC15Indicator",
                EptsReportUtils.map(summaryCohortQueryEC15, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC16",
        "The patients date of birth is after any clinical consultation date",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients date of birth is after any clinical consultation date",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseBirthDatesAreAfterEncounterDate(
                        Arrays.asList(
                            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                            hivMetadata
                                .getARVPediatriaSeguimentoEncounterType()
                                .getEncounterTypeId())),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC17",
        "The patients whose date of drug pick up is before 1985",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients whose date of drug pick up is before 1985",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseEncounterIsBeforeEC17(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC18",
        "The patients' whose date of clinical consultation is before 1985",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients' whose date of clinical consultation is before 1985",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseEncounterIsBeforeEC18(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC19",
        "The patients' whose date of laboratory test specimen collection date or results report date is before 1985",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The patients' whose date of laboratory test specimen collection date or results report date is before 1985",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsWhoseEncounterIsBefore1985EC19(
                        hivMetadata.getARTProgram().getProgramId(),
                        hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                        ENCONTER_TYPE_FSR,
                        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                        hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()),
                    mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC20",
        "EC20:Patients that are not enrolled in TARV but has a consultation or drugs pick up recorded in the system",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC23Indicator",
                EptsReportUtils.map(summaryCohortQueryEC20, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC21",
        "The Patient’s sex is not defined",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The Patient’s sex is not defined",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsSexNotDefinedEC21(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC22",
        "The Patient’s date of birth is not defined",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "The Patient’s date of birth is not defined",
                EptsReportUtils.map(
                    summaryDataQualityCohorts.getPatientsSexNotDefinedEC22(), mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC23",
        "EC23:The patient is female and has a last menstrual period date but is not pregnant",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC23Indicator",
                EptsReportUtils.map(summaryCohortQueryEC23, mappings)),
            mappings),
        "");

    return dsd;
  }
}
