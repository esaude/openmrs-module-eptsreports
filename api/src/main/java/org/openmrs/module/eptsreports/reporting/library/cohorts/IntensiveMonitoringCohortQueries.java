package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovement2020Queries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntensiveMonitoringCohortQueries {

  private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  private HivMetadata hivMetadata;

  private CommonCohortQueries commonCohortQueries;

  private CommonMetadata commonMetadata;

  private TbMetadata tbMetadata;

  private final String MAPPING2 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}";

  private final String MAPPING3 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}";
  private final String MAPPING =
      "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},revisionEndDate=${revisionEndDate},location=${location}";

  @Autowired
  public IntensiveMonitoringCohortQueries(
      QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries,
      HivMetadata hivMetadata,
      CommonCohortQueries commonCohortQueries,
      CommonMetadata commonMetadata,
      TbMetadata tbMetadata) {
    this.qualityImprovement2020CohortQueries = qualityImprovement2020CohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonCohortQueries = commonCohortQueries;
    this.commonMetadata = commonMetadata;
    this.tbMetadata = tbMetadata;
  }

  /**
   * Get CAT 7.1, to 7.3 and 7.5 deno Monitoria Intensiva MQHIV 2021 for the selected location and
   * reporting period Section 7.1 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7DenMI2021Part135Definition(Integer den) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 7.1, 7.3, 7.5 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    if (den == 1 || den == 3) {
      cd.setName("MI 1 OR 3 A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 5) {
      cd.setName("(MI 5 A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    CohortDefinition startedART = qualityImprovement2020CohortQueries.getMOHArtStartDate();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
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
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            startedART,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            tbActive,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            tbSymptoms,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            tbTreatment,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            breastfeeding,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}"));

    if (den == 1 || den == 3) {
      cd.setCompositionString("A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 5) {
      cd.setCompositionString("(A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    return cd;
  }

  /**
   * Get CAT 7.2, to 7.4 and 7.6 deno Monitoria Intensiva MQHIV 2021 for the selected location and
   * reporting period Section 7.1 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7DenMI2021Part246Definition(Integer den) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 7.2, 7.4, 7.6 denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    if (den == 2 || den == 4) {
      cd.setName(
          "MI 2 OR 4 (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR I OR J)");
    } else if (den == 6) {
      cd.setName("MI 6 (A AND B4 AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    CohortDefinition startedART = qualityImprovement2020CohortQueries.getMOHArtStartDate();

    CohortDefinition b41 = qualityImprovement2020CohortQueries.getB4And1();
    CohortDefinition b42 = qualityImprovement2020CohortQueries.getB4And2();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
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
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition tbDiagOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive();

    CohortDefinition tbSymptomsOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms();

    CohortDefinition tbTreatmentOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBTreatment();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            startedART,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            tbActive,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            tbSymptoms,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    cd.addSearch(
        "B3",
        EptsReportUtils.map(
            tbTreatment,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            breastfeeding,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            tbDiagOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "I",
        EptsReportUtils.map(
            tbSymptomsOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "J",
        EptsReportUtils.map(
            tbTreatmentOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "B41",
        EptsReportUtils.map(
            b41,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    cd.addSearch(
        "B42",
        EptsReportUtils.map(
            b42,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    if (den == 2 || den == 4) {
      cd.setCompositionString( "(A AND (B41 OR B42)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR I OR J)");
    } else if (den == 6) {
      cd.setCompositionString(
          "(A AND (B41 OR B42) AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    return cd;
  }

  /**
   * Get CAT 7.1, to 7.3 and 7.5 numerator Monitoria Intensiva MQHIV 2021 for the selected location
   * and reporting period Section 7 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7NumMI2021Part135Definition(Integer num) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setName(
          "MI NUM 1 OR 3 (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 5) {
      compositionCohortDefinition.setName(
          "MI NUM 5(A AND C AND B4) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    CohortDefinition startedART = qualityImprovement2020CohortQueries.getMOHArtStartDate();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
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
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition b41 = qualityImprovement2020CohortQueries.getB4And1();

    CohortDefinition b42 = qualityImprovement2020CohortQueries.getB4And2();

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            startedART,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B1",
        EptsReportUtils.map(
            tbActive,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B2",
        EptsReportUtils.map(
            tbSymptoms,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B3",
        EptsReportUtils.map(
            tbTreatment,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            breastfeeding,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B41",
        EptsReportUtils.map(
            b41,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B42",
        EptsReportUtils.map(
            b42,
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "GNEW",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getGNew(),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},revisionEndDate=${revisionEndDate},location=${location}"));

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setCompositionString(
          "(A AND  (B41 OR B42)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 5) {
      compositionCohortDefinition.setCompositionString(
          "(A AND C AND (B41 OR B42) ) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    return compositionCohortDefinition;
  }

  /**
   * Get CAT 7.2, to 7.4 and 7.6 numerator Monitoria Intensiva MQHIV 2021 for the selected location
   * and reporting period Section 7 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7NumMI2021Part246Definition(Integer num) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (num == 2 || num == 4) {
      compositionCohortDefinition.setName(
          "MI NUM 2 OR 4(A AND B4 AND G) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F )");
    } else if (num == 6) {
      compositionCohortDefinition.setName(
          "MI NUM 6 (A AND B4 AND C AND G) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    CohortDefinition startedART = qualityImprovement2020CohortQueries.getMOHArtStartDate();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
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
            null);

    CohortDefinition tbProphilaxy =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getIsoniazidUsageConcept(),
            Collections.singletonList(hivMetadata.getStartDrugs()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition tbDiagOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBDiagActive();

    CohortDefinition tbSymptomsOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBSymtoms();

    CohortDefinition tbTreatmentOnPeriod =
        qualityImprovement2020CohortQueries.getPatientsWithTBTreatment();

    CohortDefinition b41 = qualityImprovement2020CohortQueries.getB4And1();

    CohortDefinition b42 = qualityImprovement2020CohortQueries.getB4And2();

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            startedART,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B1",
        EptsReportUtils.map(
            tbActive,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B2",
        EptsReportUtils.map(
            tbSymptoms,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B3",
        EptsReportUtils.map(
            tbTreatment,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B4",
        EptsReportUtils.map(
            tbProphilaxy,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            breastfeeding,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "H",
        EptsReportUtils.map(
            tbDiagOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "I",
        EptsReportUtils.map(
            tbSymptomsOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "J",
        EptsReportUtils.map(
            tbTreatmentOnPeriod,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B41",
        EptsReportUtils.map(
            b41,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B42",
        EptsReportUtils.map(
            b42,
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "GNEW",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getGNew(),
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},revisionEndDate=${revisionEndDate},location=${location}"));

    if (num == 2 || num == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42) AND GNEW) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR I OR J)");
    } else if (num == 6) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42) AND C AND GNEW) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    return compositionCohortDefinition;
  }

  /**
   * MEPTS-862_MI_REPORT_CAT13_P2 Get CAT 13.15, 13.16 and 13.17 P2 for Numerator and Denominator
   * Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMICat13Part2(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.15, 13.16 and 13.17 Numerator and Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    // DENOMINATOR
    if (level == 15) {
      cd.addSearch(
          "MI13DEN15",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod(), MAPPING));
    } else if (level == 16) {
      cd.addSearch(
          "MI13DEN16",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod33Month(),
              MAPPING));
    } else if (level == 17) {
      cd.addSearch(
          "MI13DEN17",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC13P2DenMGInIncluisionPeriod33Days(),
              MAPPING));
    }

    // NUMERATOR
    if (level == 15) {
      cd.addSearch(
          "MI13NUM15",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num1(), MAPPING));
    } else if (level == 16) {
      cd.addSearch(
          "MI13NUM16",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num2(), MAPPING));
    } else if (level == 17) {
      cd.addSearch(
          "MI13NUM17",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num3(), MAPPING));
    }

    if ("DEN15".equals(type)) {
      cd.setCompositionString("MI13DEN15");
    } else if ("DEN16".equals(type)) {
      cd.setCompositionString("MI13DEN16");
    } else if ("DEN17".equals(type)) {
      cd.setCompositionString("MI13DEN17");
    } else if ("NUM15".equals(type)) {
      cd.setCompositionString("MI13NUM15");
    } else if ("NUM16".equals(type)) {
      cd.setCompositionString("MI13NUM16");
    } else if ("NUM17".equals(type)) {
      cd.setCompositionString("MI13NUM17");
    }
    return cd;
  }

  /* Get CAT 13.1, 13.4, 13.6, 13.7, 13.8, 13.13 Monitoria Intensiva MQHIV 2021 for the selected
   * location and reporting period Section (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat13Den(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.1, 13.4, 13.6, 13.7, 13.8, 13.13 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    String mapp =
        "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},revisionEndDate=${revisionEndDate},location=${location}";
    cd.addSearch(
        "MI13DEN",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13(true, level), mapp));
    cd.addSearch(
        "MI13NUM",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13(false, level), mapp));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI13DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI13NUM");
    }
    return cd;
  }

  /**
   * Get the indicators (denominators) from CATEGORY 11 from report named “Monitoria Intensiva de
   * HIV-2021” for the selected location and reporting month (endDateRevision)
   */
  public CohortDefinition getMIC11DEN(int indicatorFlag) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 11.1 to 11.7 denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    if (indicatorFlag == 1
        || indicatorFlag == 2
        || indicatorFlag == 3
        || indicatorFlag == 5
        || indicatorFlag == 6) {
      cd.addSearch(
          "MI11DEN",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11DEN(indicatorFlag, "MI"),
              "revisionEndDate=${revisionEndDate},location=${location}"));
    } else if (indicatorFlag == 4 || indicatorFlag == 7) {
      cd.addSearch(
          "MI11DEN",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11DEN(indicatorFlag, "MI"),
              "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}"));
    }
    cd.setCompositionString("MI11DEN");
    return cd;
  }
  /* Get CAT 12.1, 12.2, 12.5, 12.6, 12.9, 12.10 Monitoria Intensiva MQHIV 2021 for the selected
   * location and reporting period (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat12P1DenNum(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 12.1, 12.2, 12.5, 12.6, 12.9, 12.10 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    String MAPPING = "";
    if (level == 1 || level == 5 || level == 9)
      MAPPING =
          "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},revisionEndDate=${revisionEndDate},location=${location}";
    else
      MAPPING =
          "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}";
    cd.addSearch(
        "MI12P1DEN",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ12DEN(level), MAPPING));
    cd.addSearch(
        "MI12P1NUM",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ12NUM(level), MAPPING));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI12P1DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI12P1NUM");
    }
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.2 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN2(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.2 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN2",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.setCompositionString("MI13DEN2");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.2 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM2(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.2 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM2",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.setCompositionString("MI13NUM2");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.5 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN5(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.5 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN5",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13DEN5");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.5 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM5(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.5 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM5",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13NUM5");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.9 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN9(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.9 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN9",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN9");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.9 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM9(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.9 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM9",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM9");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.10 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN10(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.10 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN10",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN10");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.10 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM10(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.10 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM10",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM10");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.11 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN11(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.11 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN11",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN11");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.11 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM11(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.11 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM11",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM11");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.14 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN14(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.14 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN14",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13DEN14");

    return cd;
  }

  /**
   * Get the indicators (numerators) from CATEGORY 11 from report named “Monitoria Intensiva de
   * HIV-2021” for the selected location and reporting month (endDateRevision)
   */
  public CohortDefinition getMIC11NUM(int indicatorFlag) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 11.1 to 11.7 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    String MAPPING =
        "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}";
    String MAPPING1 =
        "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},revisionEndDate=${revisionEndDate},location=${location}";
    if (indicatorFlag == 1) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFandGAdultss("MI"),
              MAPPING));
    } else if (indicatorFlag == 2) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumB1nB2notCnotDnotEnotEnotFnHandAdultss(
                  "MI"),
              MAPPING1));
    } else if (indicatorFlag == 3) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumAnB3nCnotDnotEnotEnotFnG("MI"),
              MAPPING));
    } else if (indicatorFlag == 4) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumB1nB2nB3nCnotDnotEnotEnotFnH("MI"),
              MAPPING1));
    } else if (indicatorFlag == 5) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFnotGnChildren("MI"),
              MAPPING));
    } else if (indicatorFlag == 6) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumAnotCnotDnotEnotFnotIlessThan9Month(
                  "MI"),
              MAPPING));
    } else if (indicatorFlag == 7) {
      cd.addSearch(
          "MI11NUM",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC11NumB1nB2notCnotDnotEnotFnHChildren("MI"),
              MAPPING1));
    }
    cd.setCompositionString("MI11NUM");

    return cd;
  }
  /*
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.14 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM14(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.14 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM14",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13NUM14");

    return cd;
  }

  /**
   * Get CAT 13 P4 NUMERATOR AND DENOMINATOR Monitoria Intensiva MQHIV 2021 for the selected
   * location and reporting period (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMICat13Part4(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.3, 13.12 AND 13.18 Numerator AND Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    cd.addSearch(
        "MI13DEN",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13P4(true, level), MAPPING3));

    cd.addSearch(
        "MI13NUM",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13P4(false, level), MAPPING2));

    if ("DEN".equals(type)) {
      cd.setCompositionString("MI13DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI13NUM");
    }
    return cd;
  }

  /**
   * A - Select all patients with Last Clinical Consultation (encounter type 6, encounter_datetime)
   * occurred during the inclusion period (encounter_datetime>= startDateInclusion and <=
   * endDateInclusion
   */
  public CohortDefinition getMI15A() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + " FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + " WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND  e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "       GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * B2 - Select all patients with the earliest “Data de Início TARV” (concept_id 1190, not null,
   * value_datetime) recorded in Ficha Resumo (encounter type 53, obs_datetime) and “Last
   * Consultation Date” (encounter_datetime from A) minus “Data de Início TARV” (concept id 1190
   * value_datetime) is greater than (>) 21 months
   */
  public CohortDefinition getMI15B2() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B2 Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());

    String query =
        "SELECT tabela.patient_id  "
            + "             FROM  "
            + "             (SELECT p.patient_id, min(o.value_datetime) as value_datetime  "
            + "             FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id  "
            + "             INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + "             WHERE o.voided=0 AND e.voided=0 AND p.voided=0 AND  "
            + "             o.concept_id=${1190} AND o.value_datetime is not NULL AND  "
            + "             e.encounter_type=${53} AND  "
            + "             e.location_id= :location  "
            + "             GROUP by p.patient_id) as tabela  "
            + "             INNER JOIN ( "
            + "                 SELECT pp.patient_id, MAX(e.encounter_datetime) as encounter_datetime "
            + "             FROM   patient pp INNER JOIN encounter e  "
            + "             ON e.patient_id = pp.patient_id  "
            + "             WHERE  pp.voided = 0 AND e.voided = 0  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_type = 6  "
            + "             AND  e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "             GROUP by pp.patient_id) as last_encounter "
            + "        ON last_encounter.patient_id=tabela.patient_id "
            + "WHERE timestampdiff(month,tabela.value_datetime,( last_encounter.encounter_datetime "
            + "             ))>21";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * D - All female patients registered as “Breastfeeding” (concept_id 6332, value_coded equal to
   * concept_id 1065) in Ficha Clínica (encounter type 6, encounter_datetime) occurred during the
   * following period (encounter_datetime >= startDate and <= endDate)
   *
   * @return
   */
  public CohortDefinition getMI15D() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id FROM patient p  "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id  "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided=0 AND e.location_id=:location "
            + " AND e.encounter_type= ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + " AND o.concept_id = ${6332} AND o.value_coded= ${1065} AND o.voided=0 "
            + " GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>B1 – Select all patients with the earliest “Data de Início TARV” (concept_id 1190, not
   *       Tnull, value_datetime) recorded in Ficha Resumo (encounter type 53) and “Last
   *       Consultation Date” (encounter_datetime from A) minus “ Data de Início TARV” (concept id
   *       1190 value_datetime) is greater than (>) 3 months.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMI15B1() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    String query =
        "SELECT tabela.patient_id  "
            + "             FROM  "
            + "             (SELECT p.patient_id, min(o.value_datetime) as value_datetime  "
            + "             FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id  "
            + "             INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + "             WHERE o.voided=0 AND e.voided=0 AND p.voided=0 AND  "
            + "             o.concept_id=${1190} AND o.value_datetime is not NULL AND  "
            + "             e.encounter_type=${53} AND  "
            + "             e.location_id= :location  "
            + "             GROUP by p.patient_id) as tabela  "
            + "             INNER JOIN ( "
            + "                 SELECT pp.patient_id, MAX(e.encounter_datetime) as encounter_datetime "
            + "             FROM   patient pp INNER JOIN encounter e  "
            + "             ON e.patient_id = pp.patient_id  "
            + "             WHERE  pp.voided = 0 AND e.voided = 0  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_type = 6  "
            + "             AND  e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "             GROUP by pp.patient_id) as last_encounter "
            + "        ON last_encounter.patient_id=tabela.patient_id "
            + "WHERE timestampdiff(month,tabela.value_datetime,( last_encounter.encounter_datetime "
            + "             ))>3";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>G - Select all patients with the last (Viral Load Result (concept id 856) )and the result
   *       is >= 1000 (value_numeric) registered on Ficha Clinica (encounter type 6) before “Last
   *       Consultation Date” (encounter_datetime from A).
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMI15G() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p "
            + "       INNER JOIN encounter ee "
            + "               ON ee.patient_id = p.patient_id "
            + "       INNER JOIN obs oo"
            + "               ON oo.encounter_id = ee.encounter_id "
            + " WHERE  p.voided = 0 "
            + "       AND ee.voided = 0 "
            + "       AND oo.voided = 0 "
            + "       AND ee.location_id = :location "
            + "       AND ee.encounter_type = ${6} "
            + "       AND oo.concept_id = ${856} "
            + "       AND oo.value_numeric >= 1000 "
            + "       AND ee.encounter_datetime < (SELECT "
            + "           Max(e.encounter_datetime) AS last_consultation_date "
            + "                                     FROM   encounter e "
            + "                                     WHERE  e.voided = 0 "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_type = ${6} "
            + "                                            AND e.patient_id = p.patient_id "
            + "                                            AND e.encounter_datetime BETWEEN "
            + "                                                :startDate AND :endDate "
            + "                                     LIMIT  1)  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * C- All female patients registered as “Pregnant” (concept_id 1982, value_coded equal to
   * concept_id 1065) in Ficha Clínica (encounter type 6, encounter_datetime) occurred during the
   * following period (encounter_datetime >= startDate and <= endDate)
   *
   * @return
   */
  public CohortDefinition getMI15C() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id FROM patient p  "
            + " INNER JOIN encounter e ON e.patient_id=p.patient_id  "
            + " INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided=0 AND e.location_id=:location "
            + " AND e.encounter_type= ${6} AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + " AND o.concept_id = ${1982} AND o.value_coded= ${1065} AND o.voided=0 "
            + " GROUP BY p.patient_id ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>H - Select all patients with Viral Load result (concept id 856, value_numeric) >= 1000 on
   *       registered in Ficha Clinica (encounter type 6) on “Last Consultation” (encounter_datetime
   *       from A)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMI15H() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter ee "
            + "               ON ee.patient_id = p.patient_id "
            + "       INNER JOIN obs oo "
            + "               ON oo.encounter_id = ee.encounter_id "
            + " WHERE  p.voided = 0 "
            + "       AND ee.voided = 0 "
            + "       AND oo.voided = 0 "
            + "       AND ee.location_id = :location "
            + "       AND ee.encounter_type = ${6} "
            + "       AND oo.concept_id = ${856} "
            + "       AND oo.value_numeric >= 1000 "
            + "       AND ee.encounter_datetime = (SELECT "
            + "           Max(e.encounter_datetime) AS last_consultation_date "
            + "                                    FROM   patient pp "
            + "                                           INNER JOIN encounter e "
            + "                                                   ON e.patient_id = "
            + "                                                      pp.patient_id "
            + "                                    WHERE  pp.voided = 0 "
            + "                                           AND e.voided = 0 "
            + "                                           AND e.location_id = :location AND pp.patient_id=p.patient_id "
            + "                                           AND e.encounter_type = ${6} "
            + "                                           AND e.encounter_datetime BETWEEN "
            + "                                               :startDate AND :endDate "
            + "                                    LIMIT  1)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
  /**
   * J - Select all patients with at least one of the following models registered in Ficha Clinica
   * (encounter type 6, encounter_datetime) before “ Last Consultation Date” (encounter_datetime
   * from A): Last record of GAAC (concept id 23724) and the response is “Iniciar” (value_coded,
   * concept id 1256) or “Continua” (value_coded, concept id 1257) Last record of DT (concept id
   * 23730) and the response is “Iniciar” (value_coded, concept id 1256) or “Continua” (value_coded,
   * concept id 1257) Last record of DS (concept id 23888) and the response is “Iniciar”
   * (value_coded, concept id 1256) or “Continua” (value_coded, concept id 1257) Last record of FR
   * (concept id 23729) and the response is “ Iniciar” (value_coded, concept id 1256) or “Continua”
   * (value_coded, concept id 1257) Last record of DC (concept id 23731) and the response is
   * “Iniciar” (value_coded, concept id 1256) or “Continua” (value_coded, concept id 1257)
   */
  public CohortDefinition getMI15J() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("23724", hivMetadata.getGaac().getConceptId());
    map.put("23730", hivMetadata.getQuarterlyDispensation().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());
    map.put("23729", hivMetadata.getRapidFlow().getConceptId());
    map.put("23731", hivMetadata.getCommunityDispensation().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) last_consultation "
            + "               ON last_consultation.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND ( ( o.concept_id = ${23724} "
            + "               AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23730} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23888} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23729} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23731} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256}))) "
            + "       AND e.encounter_datetime < last_consultation.encounter_datetime";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
  /**
   * F - Select all patients with the last CD4 result (concept id 1695) and the result is <= 200
   * (value_numeric) registered on Ficha Clinica (encounter type 6) before “Last Consultation Date”
   * (encounter_datetime from A).
   */
  public CohortDefinition getMI15F() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1695", hivMetadata.getCD4AbsoluteOBSConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter ee "
            + "               ON ee.patient_id = p.patient_id "
            + "       INNER JOIN obs oo "
            + "               ON oo.encounter_id = ee.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND ee.voided = 0 "
            + "       AND oo.voided = 0 "
            + "       AND ee.location_id = :location "
            + "       AND ee.encounter_type = ${6} "
            + "       AND oo.concept_id = ${1695} "
            + "       AND oo.value_numeric <= 200 "
            + "       AND ee.encounter_datetime < (SELECT "
            + "           Max(e.encounter_datetime) AS last_consultation_date "
            + "                                     FROM   encounter e "
            + "                                     WHERE  e.voided = 0 "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_type = ${6} "
            + "                                            AND e.patient_id = p.patient_id "
            + "                                            AND e.encounter_datetime BETWEEN "
            + "                                                :startDate AND :endDate LIMIT  1) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
  /**
   * K - Select all patients with at least one of the following models registered in Ficha Clinica
   * (encounter type 6, encounter_datetime) on “ Last Consultation Date” (encounter_datetime from
   * A): GAAC (concept id 23724) = “Iniciar” (value_coded, concept id 1256) DT (concept id 23730) =
   * “Iniciar” (value_coded, concept id 1256) DS (concept id 23888) = “Iniciar” (value_coded,
   * concept id 1256) FR (concept id 23729) = “Iniciar” (value_coded, concept id 1256) DC (concept
   * id 23731) = “Iniciar” (value_coded, concept id 1256)
   */
  public CohortDefinition getMI15K() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("23724", hivMetadata.getGaac().getConceptId());
    map.put("23730", hivMetadata.getQuarterlyDispensation().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());
    map.put("23729", hivMetadata.getRapidFlow().getConceptId());
    map.put("23731", hivMetadata.getCommunityDispensation().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) last_consultation "
            + "               ON last_consultation.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND ( ( o.concept_id = ${23724} "
            + "               AND o.value_coded = ${1256} ) "
            + "              OR ( o.concept_id = ${23730} "
            + "                   AND o.value_coded = ${1256} ) "
            + "              OR ( o.concept_id = ${23888} "
            + "                   AND o.value_coded = ${1256} ) "
            + "              OR ( o.concept_id = ${23729} "
            + "                   AND o.value_coded = ${1256} ) "
            + "              OR ( o.concept_id = ${23731} "
            + "                   AND o.value_coded = ${1256} ) ) "
            + "       AND e.encounter_datetime = last_consultation.encounter_datetime";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
  /**
   * L - Select all patients with at least one of the following models registered in Ficha Clinica
   * (encounter type 6, encounter_datetime) on “ Last Consultation Date” (encounter_datetime from
   * A): GAAC (concept id 23724) = “Fim” (value_coded, concept id 1267) DT (concept id 23730) =
   * “Fim” (value_coded, concept id 1267) DS (concept id 23888) = “Fim” (value_coded, concept id
   * 1267) FR (concept id 23729) = “Fim” (value_coded, concept id 1267) DC (concept id 23731) =
   * “Fim” (value_coded, concept id 1267)
   */
  public CohortDefinition getMI15L() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("23724", hivMetadata.getGaac().getConceptId());
    map.put("23730", hivMetadata.getQuarterlyDispensation().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());
    map.put("23729", hivMetadata.getRapidFlow().getConceptId());
    map.put("23731", hivMetadata.getCommunityDispensation().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) last_consultation "
            + "               ON last_consultation.patient_id = p.patient_id "
            + " WHERE  p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND ( ( o.concept_id = ${23724} "
            + "               AND o.value_coded = ${1267} ) "
            + "              OR ( o.concept_id = ${23730} "
            + "                   AND o.value_coded = ${1267} ) "
            + "              OR ( o.concept_id = ${23888} "
            + "                   AND o.value_coded = ${1267} ) "
            + "              OR ( o.concept_id = ${23729} "
            + "                   AND o.value_coded = ${1267} ) "
            + "              OR ( o.concept_id = ${23731} "
            + "                   AND o.value_coded = ${1267} ) ) "
            + "       AND e.encounter_datetime = last_consultation.encounter_datetime";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b>E - Select all patients with the following Clinical Consultations or ARV Drugs Pick Ups:</b>
   *
   * <ul>
   *   <li>at least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one
   *       ARV Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   *       Consultation” (encounter_datetime from A) minus 30 days and “Last Clinical Consultation”
   *       (encounter_datetime from A) minus 1 day
   *   <li>at least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one
   *       ARV Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   *       Consultation” (encounter_datetime from A) minus 60 days and “Last Clinical Consultation”
   *       (encounter_datetime from A) minus 31 days
   *   <li>at least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one
   *       ARV Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   *       Consultation” (encounter_datetime from A) minus 90 days and “Last Clinical Consultation”
   *       (encounter_datetime from A) minus 61 days
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMI15E(int upper, int lower) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("upper", upper);
    map.put("lower", lower);

    String query =
        "SELECT juncao.patient_id "
            + " FROM ( "
            + "         SELECT p.patient_id, e.encounter_datetime AS encounter_date "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + " AND e.location_id =:location "
            + "           AND e.encounter_type = ${6} "
            + "         UNION "
            + "         SELECT p.patient_id, o.value_datetime AS encounter_date "
            + "         FROM patient p "
            + "            INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "            INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + " AND e.location_id =:location "
            + "           AND o.concept_id = ${23866} "
            + "           AND e.encounter_type = ${52} "
            + "     ) AS juncao "
            + "    INNER JOIN ( "
            + "                SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime "
            + "                FROM  patient p "
            + "                    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                WHERE p.voided =0 "
            + "                    AND  e.voided = 0 "
            + " AND e.location_id =:location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                GROUP BY p.patient_id "
            + "                 )  AS max_ficha on juncao.patient_id = max_ficha.patient_id "
            + "WHERE juncao.encounter_date "
            + "    BETWEEN DATE_SUB(max_ficha.encounter_datetime, INTERVAL ${upper} DAY) "
            + "        AND DATE_SUB(max_ficha.encounter_datetime, INTERVAL  ${lower} DAY)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * E - Select all patients with the following Clinical Consultations or ARV Drugs Pick Ups: at
   * least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one ARV
   * Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   * Consultation” ( encounter_datetime from A) minus 30 days and “Last Clinical Consultation”
   * (encounter_datetime from A) minus 1 day
   *
   * <p>AND
   *
   * <p>at least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one
   * ARV Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   * Consultation” (encounter_datetime from A) minus 60 days and “Last Clinical Consultation” (
   * encounter_datetime from A) minus 31 days AND
   *
   * <p>at least one Clinical Consultation (encounter type 6, encounter_datetime) or at least one
   * ARV Pickup (encounter type 52, value_datetime(concept_id 23866)) between “Last Clinical
   * Consultation” (encounter_datetime from A) minus 90 days and “Last Clinical Consultation”
   * (encounter_datetime from A) minus 61 days
   *
   * @return
   */
  public CohortDefinition getMI15EComplete() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Select all patients with the following Clinical Consultations or ARV Drugs Pick Ups");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMI15E(30, 1);
    CohortDefinition b = getMI15E(60, 31);
    CohortDefinition c = getMI15E(90, 61);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(b, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(c, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("A AND B AND C");

    return cd;
  }
  /**
   * I - Select all patients with the last Viral Load Result (concept id 856, value_numeric) < 1000
   * (value_numeric) OR Viral Load QUALITATIVE (concept id 1305) with value coded not null
   * registered on Ficha Clinica (encounter type 6) before “Last Consultation Date”
   * (encounter_datetime from A) minus 12 months, as “Last VL Result <1000”, and filter all patients
   * with at least one Viral Load Result (concept id 856, value_numeric not NULL) registered on
   * Ficha Clinica (encounter type 6, encounter_datetime) between “Last VL Result <1000”+ 12 months
   * and “Last VL Result <1000” + 18 months
   */
  public CohortDefinition getMI15I() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients From Ficha Clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setName("Patients From Ficha Clinica");
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e on p.patient_id = e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id  "
            + " INNER JOIN (SELECT juncao.patient_id,juncao.encounter_date "
            + " FROM ( "
            + "         SELECT p.patient_id, e.encounter_datetime AS encounter_date "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e on p.patient_id = e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "         WHERE p.voided = 0 AND e.voided = 0 AND e.location_id =:location AND e.encounter_type = ${6} "
            + "         AND o.concept_id=${856} AND o.value_numeric < 1000 "
            + "         UNION "
            + "         SELECT p.patient_id, o.value_datetime AS encounter_date "
            + "         FROM patient p "
            + "            INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "            INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.location_id =:location "
            + "           AND o.concept_id = ${1305} and o.value_coded is not null AND e.encounter_type = ${6} "
            + "     ) juncao "
            + " INNER JOIN( SELECT p.patient_id, MAX(e.encounter_datetime) AS last_consultation_date   "
            + "            FROM  patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "            WHERE  p.voided = 0 AND e.voided = 0 AND e.location_id =:location AND e.encounter_type = ${6} "
            + "            AND e.encounter_datetime BETWEEN :startDate AND :endDate GROUP BY p.patient_id "
            + "            )  "
            + " as last_consultation on last_consultation.patient_id = juncao.patient_id "
            + " WHERE juncao.encounter_date < DATE_SUB(last_consultation.last_consultation_date, INTERVAL 18 MONTH)) as lastVLResult "
            + " ON lastVLResult.patient_id=p.patient_id "
            + " WHERE "
            + " o.concept_id=${856} AND o.value_numeric is not null AND e.encounter_type=${6} AND  "
            + " e.encounter_datetime BETWEEN DATE_ADD(lastVLResult.encounter_date,INTERVAL 12 MONTH)  "
            + " AND DATE_ADD(lastVLResult.encounter_date,INTERVAL 18 MONTH)AND e.location_id=:location";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String str = stringSubstitutor.replace(query);
    cd.setQuery(str);
    return cd;
  }

  public CohortDefinition getCat15P1DenNum(boolean isDenominator, int level) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    String name1 = "15.1 - % de pacientes elegíveis a MDS, que foram inscritos em MDS";
    String name2 =
        "15.2 - % de inscritos em MDS que receberam CV acima de 1000 cópias que foram suspensos de MDS";
    String name3 =
        "15.3 - % de pacientes inscritos em MDS em TARV há mais de 21 meses, que conhecem o seu resultado de CV de seguimento";

    CohortDefinition a = getMI15A();
    CohortDefinition b1 = getMI15B1();
    CohortDefinition b2 = getMI15B2();
    CohortDefinition c = getMI15C();
    CohortDefinition d = getMI15D();
    CohortDefinition e = getMI15EComplete();
    CohortDefinition f = getMI15F();
    CohortDefinition g = getMI15G();
    CohortDefinition h = getMI15H();
    CohortDefinition i = getMI15I();
    CohortDefinition j = getMI15J();
    CohortDefinition k = getMI15K();
    CohortDefinition l = getMI15L();
    CohortDefinition p = getMI15P();
    CohortDefinition major2 = getAgeOnLastConsultationMoreThan2Years();
    String MAPPINGA =
        "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}";
    String MAPPINGC =
        "startDate=${revisionEndDate-11m+1d},endDate=${revisionEndDate-2m},location=${location}";
    String MAPPINGD =
        "startDate=${revisionEndDate-20m+1d},endDate=${revisionEndDate-2m},location=${location}";

    cd.addSearch("A", EptsReportUtils.map(a, MAPPINGA));
    cd.addSearch("B1", EptsReportUtils.map(b1, MAPPINGA));
    cd.addSearch("B2", EptsReportUtils.map(b2, MAPPINGA));
    cd.addSearch("C", EptsReportUtils.map(c, MAPPINGC));
    cd.addSearch("D", EptsReportUtils.map(d, MAPPINGD));
    cd.addSearch("E", EptsReportUtils.map(e, MAPPINGA));
    cd.addSearch("F", EptsReportUtils.map(f, MAPPINGA));
    cd.addSearch("G", EptsReportUtils.map(g, MAPPINGA));
    cd.addSearch("H", EptsReportUtils.map(h, MAPPINGA));
    cd.addSearch("I", EptsReportUtils.map(i, MAPPINGA));
    cd.addSearch("J", EptsReportUtils.map(j, MAPPINGA));
    cd.addSearch("K", EptsReportUtils.map(k, MAPPINGA));
    cd.addSearch("L", EptsReportUtils.map(l, MAPPINGA));
    cd.addSearch("P", EptsReportUtils.map(p, MAPPINGA));
    cd.addSearch("AGE2", EptsReportUtils.map(major2, MAPPINGA));

    if (isDenominator) {

      if (level == 1) {
        cd.setName("Denominator: " + name1);
        cd.setCompositionString("A AND B1 AND E AND NOT (C OR D OR F OR G OR J) AND AGE2 ");
      }
      if (level == 2) {
        cd.setName("Denominator: " + name2);
        cd.setCompositionString("A AND J AND H AND AGE2");
      }
      if (level == 3) {
        cd.setName("Denominator: " + name3);
        cd.setCompositionString("A AND J AND B2 AND NOT P AND AGE2");
      }
      return cd;
    }

    if (level == 1) {
      cd.setName("Numerator: " + name1);
      cd.setCompositionString("A AND B1 AND E AND NOT (C OR D OR F OR G OR J) AND K AND AGE2 ");
    }
    if (level == 2) {
      cd.setName("Numerator: " + name2);
      cd.setCompositionString("A AND J AND H AND L AND AGE2");
    }
    if (level == 3) {
      cd.setName("Numerator: " + name3);
      cd.setCompositionString("A AND J AND B2 AND NOT P AND I AND AGE2");
    }
    return cd;
  }

  /**
   * Age should be calculated on “Last Consultation Date” (Check A for the algorithm to define this
   * date).
   *
   * @return
   */
  public CohortDefinition getAgeOnLastConsultationMoreThan2Years() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get age  on last Consultation ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String sql =
        "SELECT p.person_id "
            + "FROM   person p "
            + "    INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime "
            + "                FROM  patient p "
            + "                    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                WHERE p.voided =0 "
            + "                  AND  e.voided = 0 "
            + "                  AND e.encounter_type = ${6} "
            + "                  AND e.location_id = :location "
            + "                  AND e.encounter_datetime BETWEEN  :startDate AND :endDate "
            + "                GROUP BY p.patient_id) "
            + "        AS last_clinical ON last_clinical.patient_id = p.person_id "
            + "WHERE p.voided = 0 "
            + "    AND  TIMESTAMPDIFF(YEAR,p.birthdate,last_clinical.encounter_datetime) >= 2 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String str = stringSubstitutor.replace(sql);
    cd.setQuery(str);
    return cd;
  }

  /**
   * P- Select all patients with concept “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id 23722)
   * and value coded “HIV CARGA VIRAL” (Concept Id 856) registered in Ficha Clinica (encounter type
   * 6) during the last 3 months from the “Last Consultation Date” (encounter_datetime from A), i.e,
   * at least one “Pedido de Carga Viral” encounter_datetime >= “Last Consultation Date”-3months and
   * < “Last Consultation Date”.
   */
  public CohortDefinition getMI15P() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with Pedido de Carga Viral");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setName("Patients From Ficha Clinica");
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + " on p.patient_id = e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " INNER JOIN (SELECT pp.patient_id, MAX(ee.encounter_datetime) AS last_consultation_date"
            + " FROM patient pp INNER JOIN encounter ee ON ee.patient_id = pp.patient_id"
            + " WHERE pp.voided = 0 AND ee.voided = 0 AND ee.location_id = :location AND ee.encounter_type = ${6}"
            + " AND ee.encounter_datetime BETWEEN :startDate AND :endDate"
            + " GROUP BY pp.patient_id) last_consultation ON p.patient_id = last_consultation.patient_id"
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.location_id = :location AND e.encounter_type = ${6}"
            + " AND o.concept_id = ${23722} AND o.value_coded = ${856} "
            + " AND e.encounter_datetime >= DATE_SUB(last_consultation.last_consultation_date, INTERVAL 3 MONTH)"
            + " AND e.encounter_datetime < last_consultation.last_consultation_date"
            + " GROUP BY p.patient_id";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String str = stringSubstitutor.replace(query);
    cd.setQuery(str);
    return cd;
  }
}
