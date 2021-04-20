package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovement2020Queries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntensiveMonitoringCohortQueries {

  private GenericCohortQueries genericCohortQueries;

  private HivMetadata hivMetadata;

  private CommonMetadata commonMetadata;

  private GenderCohortQueries genderCohortQueries;

  private AgeCohortQueries ageCohortQueries;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private CommonCohortQueries commonCohortQueries;

  private TbMetadata tbMetadata;

  private TxPvlsCohortQueries txPvls;

  private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  private final String MAPPING = "startDate=${startDate},endDate=${endDate},location=${location}";
  private final String MAPPING1 =
      "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}";
  private final String MAPPING2 =
      "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}";

  @Autowired
  public IntensiveMonitoringCohortQueries(
      GenericCohortQueries genericCohortQueries,
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata,
      GenderCohortQueries genderCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      CommonCohortQueries commonCohortQueries,
      TbMetadata tbMetadata,
      TxPvlsCohortQueries txPvls,
      AgeCohortQueries ageCohortQueries,
      QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
    this.genderCohortQueries = genderCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
    this.tbMetadata = tbMetadata;
    this.txPvls = txPvls;
    this.ageCohortQueries = ageCohortQueries;
    this.qualityImprovement2020CohortQueries = qualityImprovement2020CohortQueries;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.1 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202171Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.1 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithProphylaxyDuringRevisionPeriod(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND NOT C AND NOT D AND NOT E AND NOT F");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND NOT C AND NOT D AND NOT E AND NOT F");
    }
    return cd;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.2 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202172Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.2 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithProphylaxyDuringRevisionPeriod(),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND NOT C AND NOT D AND NOT E AND NOT F AND NOT H AND NOT I AND NOT J");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND NOT C AND NOT D AND NOT E AND NOT F AND G AND NOT H AND NOT I AND NOT J");
    }
    return cd;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.3 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202173Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.3 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithProphylaxyDuringRevisionPeriod(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND NOT C AND NOT D AND NOT E AND NOT F");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND NOT C AND NOT D AND NOT E AND NOT F");
    }
    return cd;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.4 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202174Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.4 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithProphylaxyDuringRevisionPeriod(),
            "startDate=${revisionEndDate-7m+1d},endDate=${evisionEndDate-6m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND  B4 AND NOT C AND NOT D AND NOT E AND NOT F AND NOT H AND NOT I AND NOT J");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND  B4 AND NOT C AND NOT D AND NOT E AND NOT F AND G AND NOT H AND NOT I AND NOT J ");
    }
    return cd;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.5 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202175Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.5 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND C AND NOT D AND NOT E AND NOT F");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND C AND NOT D AND NOT E AND NOT F");
    }
    return cd;
  }

  /**
   * Get CAT 7 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * 7.6 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7MOHIV202176Definition(String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("7.6 Numerator and denominator");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMOHArtStartDate(),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getActiveTBConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getHasTbSymptomsConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                tbMetadata.getTBTreatmentPlanConcept(),
                Arrays.asList(
                    tbMetadata.getStartDrugsConcept(),
                    hivMetadata.getContinueRegimenConcept(),
                    hivMetadata.getCompletedConcept()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "B4",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                false,
                "once",
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getIsoniazidUsageConcept(),
                Collections.singletonList(hivMetadata.getStartDrugs()),
                null,
                null),
            "startDate=${revisionEndDate-7m+1d},endDate=${revisionEndDate-6m},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithProphylaxyDuringRevisionPeriod(),
            "startDate=${revisionEndDate-7m+1d},endDate=${evisionEndDate-6m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getPatientsWithTBTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (type.equals("DEN")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND  B4 AND  C AND NOT D AND NOT E AND NOT F AND NOT H AND NOT I AND NOT J");

    } else if (type.equals("NUM")) {
      cd.setCompositionString(
          "A AND NOT B1 AND NOT B2 AND NOT B3 AND B4 AND C AND NOT D AND NOT E AND NOT F AND G AND NOT H AND NOT I AND NOT J");
    }
    return cd;
  }
}
