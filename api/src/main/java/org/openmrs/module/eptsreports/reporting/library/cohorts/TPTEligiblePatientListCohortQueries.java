package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.tpt.CompletedIsoniazidTPTCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTEligiblePatientListCohortQueries {

  private HivMetadata hivMetadata;

  private TbMetadata tbMetadata;

  private TxCurrCohortQueries txCurrCohortQueries;

  private CommonMetadata commonMetadata;

  private final String mapping = "endDate=${endDate},location=${location}";

  @Autowired
  public TPTEligiblePatientListCohortQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      TxCurrCohortQueries txCurrCohortQueries,
      CommonMetadata commonMetadata) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.commonMetadata = commonMetadata;
  }

  public CohortDefinition getPatientsThatCompletedProphylaticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidTPTCalculation.class).get(0));
    cd.setName("Patients that completed prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  public CohortDefinition getTxCurrWithoutTPT() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR without TPT");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "txcurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true),
            "onOrBefore=${endDate},location=${location},locations=${location}"));

    compositionCohortDefinition.addSearch(
        "A1",
        EptsReportUtils.map(
            getINHStartA1(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A2",
        EptsReportUtils.map(
            getINHStartA2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A3",
        EptsReportUtils.map(
            getINHStartA3(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A4",
        EptsReportUtils.map(
            getINHStartA4(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentFollowUpTPTConcept().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugsConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A5",
        EptsReportUtils.map(
            getINHStartA5(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                tbMetadata.getTreatmentFollowUpTPTConcept().getConceptId(),
                hivMetadata.getRestartConcept().getConceptId(),
                hivMetadata.getStartDrugsConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA1",
        EptsReportUtils.map(
            get3HPStartA1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA2",
        EptsReportUtils.map(
            get3HPStartA2(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA3",
        EptsReportUtils.map(
            get3HPStartA3(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                hivMetadata.getStartDrugsConcept().getConceptId(),
                tbMetadata.getTreatmentFollowUpTPTConcept().getConceptId(),
                hivMetadata.getRestartConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB1",
        EptsReportUtils.map(
            getIPTB1(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getCompletedConcept().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB2",
        EptsReportUtils.map(
            getIPTB2(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getCompletedConcept().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB3",
        EptsReportUtils.map(
            getIPTB3(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB4",
        EptsReportUtils.map(
            getIPTB4(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB5Part1",
        EptsReportUtils.map(
            getIPTB5Part1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB5Part2",
        EptsReportUtils.map(
            getIPTB5Part2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.getDtINHConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTBPart3",
        EptsReportUtils.map(
            getIPTB5Part3(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getIsoniazidUsageConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.getDtINHConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB6Part1",
        EptsReportUtils.map(
            getIPTB6Part1(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB6Part2",
        EptsReportUtils.map(
            getIPTB6Part2(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB6Part3",
        EptsReportUtils.map(
            getIPTB6Part3(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPC1",
        EptsReportUtils.map(
            get3HPC1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPC2",
        EptsReportUtils.map(
            get3HPC2(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "threeHPC3",
        EptsReportUtils.map(
            get3HPC3(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "TBTreatmentPart1",
        EptsReportUtils.map(
            getTBTreatmentPart1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                tbMetadata.getTBTreatmentPlanConcept().getConceptId(),
                tbMetadata.getTBDrugTreatmentStartDate().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "TBTreatmentPart2",
        EptsReportUtils.map(
            getTBTreatmentPart2(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getPulmonaryTB().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "TBTreatmentPart3",
        EptsReportUtils.map(
            getTBTreatmentPart3(
                hivMetadata.getTBProgram().getProgramId(),
                hivMetadata.getActiveOnProgramConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "TBTreatmentPart4",
        EptsReportUtils.map(
            getTBTreatmentPart4(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                tbMetadata.getTBTreatmentPlanConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "E1",
        EptsReportUtils.map(
            getE1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getActiveTBConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            mapping));
    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getPositiveSymtomScreening(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getTBSymptomsConcept().getConceptId(),
                tbMetadata.getObservationTB().getConceptId(),
                tbMetadata.getFeverLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getNightsWeatsLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getWeightLossOfMoreThan3KgInLastMonth().getConceptId(),
                tbMetadata.getCoughLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getAsthenia().getConceptId(),
                tbMetadata.getCohabitantBeingTreatedForTB().getConceptId(),
                tbMetadata.getLymphadenopathy().getConceptId(),
                tbMetadata.getTestTBLAM().getConceptId(),
                tbMetadata.getTBGenexpertTestConcept().getConceptId(),
                hivMetadata.getResultForBasiloscopia().getConceptId(),
                tbMetadata.getXRayChest().getConceptId(),
                tbMetadata.getCultureTest().getConceptId(),
                tbMetadata.getNegativeConcept().getConceptId(),
                tbMetadata.getPositiveConcept().getConceptId(),
                commonMetadata.getSugestive().getConceptId(),
                commonMetadata.getIndeterminate().getConceptId(),
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTbScreeningConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId(),
                tbMetadata.getResearchResultConcept().getConceptId(),
                tbMetadata.getNoConcept().getConceptId(),
                hivMetadata.getApplicationForLaboratoryResearch().getConceptId()),
            mapping));

    compositionCohortDefinition.setCompositionString(
        "txcurr AND NOT (A1 OR A2 OR A3 OR A4 OR A5 OR threeHPA1 OR threeHPA2 OR threeHPA3 OR IPTB1 OR IPTB2 OR IPTB3 OR IPTB4 OR IPTB5Part1 OR IPTB5Part2 OR IPTBPart3 OR IPTB6Part1 OR IPTB6Part2 OR IPTB6Part3 OR threeHPC1 OR threeHPC2 OR threeHPC3 OR TBTreatmentPart1 OR TBTreatmentPart2 OR TBTreatmentPart3 OR TBTreatmentPart4 OR E1 OR F)");
    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A1:Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia
   *       Isoniazida (Data Inicio)” (concept id 6128) and value datetime not null and between
   *       endDate-7months (210 DAYs) and endDate. or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA1(
      int masterCardEncounterType, int dataInicioProfilaxiaIsoniazidaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Inicio)");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);

    String query =
        " SELECT p.patient_id"
            + " FROM"
            + " patient p"
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0"
            + " AND e.voided = 0"
            + " AND o.voided = 0"
            + " AND e.encounter_type = ${53} "
            + " AND o.value_datetime IS NOT NULL"
            + " AND o.concept_id = ${6128}"
            + " AND e.location_id = :location"
            + " AND o.value_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate"
            + " GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A2: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia INH”
   *       (concept id 6122) with value code “Inicio” (concept id 1256) and encounter datetime
   *       between endDate-7months (210 DAYs) and endDate. or
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA2(
      int adultoSeguimentoEncounterType, int startDrugsConcept, int isoniazidUsageConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Profilaxia INH");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);

    String query =
        " SELECT p.patient_id FROM patient p  "
            + "          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "          WHERE e.encounter_type = ${6} AND o.concept_id = ${6122} "
            + "          AND o.voided = 0 AND e.voided = 0  "
            + "          AND p.voided = 0 AND e.location_id = :location "
            + "          AND o.value_coded = ${1256} "
            + "          AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A3: Select all patients with Ficha clinica or Ficha Pediatrica (encounter type 6,9) with
   *       “Profilaxia com INH” (concept id 6128) and value datetime is not null and
   *       betweenendDate-7months (210 DAYs) and endDate. or
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA3(
      int pediatriaSeguimentoEncounterType,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int adultoSeguimentoEncounterType) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ficha clinica or Ficha Pediatrica A3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);

    String query =
        "SELECT p.patient_id FROM patient p  "
            + "          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "          WHERE e.encounter_type IN (${6}, ${9}) AND o.concept_id = ${6128} "
            + "          AND o.voided = 0 AND e.voided = 0  "
            + "          AND p.voided = 0 AND e.location_id = :location "
            + "          AND o.value_datetime IS NOT NULL "
            + "          AND o.value_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A4: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982])
   *       and “Seguimento de Tratamento TPT” (concept ID 23987) with values “Continua” (concept ID
   *       1257) or no value(null) or concept 23987 does not exist and encounter datetime between
   *       endDate-7months (210 DAYs) and endDate and no other INH values (“Isoniazida” or
   *       “Isoniazida + Piridoxina”) marked on FILT in the 210 DAYs prior to the INH Start Date and
   *       no Última profilaxia Isoniazida (Data Início) (Concept ID 6128, value_datetime)
   *       registered in Ficha Resumo - Mastercard (Encounter Type ID 53) in the 7 months prior to
   *       ‘INH Start Date’ and no Profilaxia (INH) (Concept ID 6122) with the value “I” (Início)
   *       (Concept ID 1256) marked on Ficha Clínica - Mastercard (Encounter Type ID 6) in the 7
   *       months prior to ‘INH Start Date’ and no Profilaxia com INH – TPI (Data Início) (Concept
   *       ID 6128, value_datetime) marked in Ficha de Seguimento (Adulto e Pediatria) (Encounter
   *       Type ID 6,9) in the 7 months prior to ‘INH Start Date’
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA4(
      int adultoSeguimentoEncounterType,
      int pediatriaSeguimentoEncounterType,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int continuaConcept,
      int treatmentFollowUpTPTConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int masterCardEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Regime de TPT A4");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("1257", continuaConcept);
    map.put("23987", treatmentFollowUpTPTConcept);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("53", masterCardEncounterType);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);

    String query =
        " SELECT p.patient_id   "
            + "    FROM  patient p      "
            + "        INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "        INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "        INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date  "
            + "                    FROM patient p INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                       INNER JOIN obs o2 on o2.encounter_id = e.encounter_id   "
            + "                    WHERE   p.voided = 0   "
            + "                      AND e.voided = 0 "
            + "                      AND o.voided = 0 "
            + "                      AND e.location_id = :location    "
            + "                      AND e.encounter_type = ${60}    "
            + "                      AND (o.concept_id = ${23985} AND o.value_coded IN (${656},${23982}))  "
            + "                      AND ((o2.concept_id = ${23987} AND (o2.value_coded = ${1257} OR o2.value_coded IS NULL))   "
            + "                      OR  o2.concept_id NOT IN( SELECT oo.concept_id FROM obs oo WHERE oo.voided = 0   "
            + "                         AND oo.encounter_id = e.encounter_id "
            + "                         AND oo.concept_id = ${23987} )) "
            + "                      AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate   "
            + "                    GROUP BY p.patient_id) AS pickup   "
            + "                   ON pickup.patient_id = p.patient_id "
            + "    WHERE p.patient_id NOT IN (  "
            + "        SELECT pp.patient_id "
            + "        FROM patient pp  "
            + "            INNER JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "            INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id   "
            + "        WHERE pp.voided = 0  "
            + "          AND p.patient_id = pp.patient_id   "
            + "          AND ee.voided = 0  "
            + "          AND oo.voided = 0  "
            + "          AND ee.location_id = :location "
            + "          AND ee.encounter_type = ${60} "
            + "          AND oo.concept_id = ${23985}  "
            + "          AND oo.value_coded IN (${656}, ${23982}) "
            + "          AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 210 DAY)  "
            + "          AND ee.encounter_datetime < pickup.first_pickup_date   "
            + "        UNION    "
            + "        SELECT pp.patient_id FROM patient pp "
            + "          INNER JOIN encounter ee ON ee.patient_id = pp.patient_id   "
            + "          INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "        WHERE ee.encounter_type = ${53}   AND oo.concept_id =  ${6128}    "
            + "          AND oo.voided = 0 AND ee.voided = 0    "
            + "          AND pp.voided = 0 AND ee.location_id = :location   "
            + "          AND p.patient_id = pp.patient_id   "
            + "          AND oo.value_datetime IS NOT NULL  "
            + "          AND oo.value_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 210 DAY)  "
            + "          AND oo.value_datetime < pickup.first_pickup_date   "
            + "        UNION    "
            + "        SELECT pp.patient_id FROM patient pp "
            + "           INNER JOIN encounter ee ON ee.patient_id = pp.patient_id  "
            + "           INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id    "
            + "        WHERE ee.encounter_type =   ${6}   AND oo.concept_id =   ${6122}   "
            + "          AND oo.voided = 0 AND ee.voided = 0    "
            + "          AND pp.voided = 0 AND ee.location_id = :location   "
            + "          AND p.patient_id = pp.patient_id   "
            + "          AND oo.value_coded =   ${1256}    "
            + "          AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 210 DAY)  "
            + "          AND ee.encounter_datetime < pickup.first_pickup_date   "
            + "        UNION    "
            + "        SELECT pp.patient_id FROM patient pp "
            + "           INNER JOIN encounter ee ON ee.patient_id = pp.patient_id  "
            + "           INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id    "
            + "        WHERE ee.encounter_type IN (${6},${9}) AND oo.concept_id = ${6128}    "
            + "          AND oo.voided = 0 AND ee.voided = 0    "
            + "          AND pp.voided = 0 AND ee.location_id = :location   "
            + "          AND p.patient_id = pp.patient_id   "
            + "          AND oo.value_datetime IS NOT NULL  "
            + "          AND oo.value_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 210 DAY)  "
            + "          AND oo.value_datetime < pickup.first_pickup_date)  "
            + " GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A5:Select all patients with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *       ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Seguimento de tratamento
   *       TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in [1256, 1705])
   *       “Continua” (concept ID 1257) or no value(null) or concept 23987 does not exist marked on
   *       FILT (encounter type 60) and encounter datetime between endDate-7months (210 days) and
   *       end date
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA5(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int treatmentFollowUpTPTConcept,
      int restartConcept,
      int startDrugsConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Regime de TPT A5");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23987", treatmentFollowUpTPTConcept);
    map.put("1705", restartConcept);
    map.put("1256", startDrugsConcept);

    String query =
        "   SELECT p.patient_id   "
            + "    FROM  patient p      "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id      "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "      INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "    WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0   "
            + "      AND  e.location_id = :location AND e.encounter_type = ${60}   "
            + "      AND (o.concept_id = ${23985} AND o.value_coded IN (${656},${23982}))    "
            + "      AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256},${1705}))  "
            + "      AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A3.1:Select all patients with Ficha Clinica - Master Card (encounter type 6) with “Outras
   *       prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id 23954) and
   *       encounter datetime between endDate – 4 months (120 DAYs) and end date and no other 3HP
   *       prescriptions marked on Ficha-Clinica in the 4 months prior to the 3HP Start Date; and no
   *       “Regime de TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept
   *       id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the 3HP
   *       Start Date;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartA1(
      int adultoSeguimentoEncounterType,
      int treatmentPrescribedConcept,
      int threeHPConcept,
      int regimeTPTConcept,
      int regimeTPTEncounterType,
      int hPPiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras Prescricoes A1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23954", threeHPConcept);
    map.put("23985", regimeTPTConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23984", hPPiridoxinaConcept);

    String query =
        "  SELECT p.patient_id    "
            + "     FROM  patient p     "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id      "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "      INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date      "
            + "          FROM    patient p      "
            + "          INNER JOIN encounter e ON e.patient_id = p.patient_id      "
            + "          INNER JOIN obs o ON o.encounter_id = e.encounter_id    "
            + "          WHERE   p.voided = 0       "
            + "              AND e.voided = 0       "
            + "              AND o.voided = 0       "
            + "              AND e.location_id = :location      "
            + "              AND e.encounter_type = ${6}   "
            + "              AND o.concept_id = ${1719}    "
            + "              AND o.value_coded = ${23954}      "
            + "              AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 120 DAY) AND :endDate "
            + "          GROUP BY p.patient_id) AS pickup   "
            + "  ON pickup.patient_id = p.patient_id    "
            + "     WHERE p.patient_id NOT IN ( SELECT pp.patient_id    "
            + "          FROM patient pp    "
            + "                INNER JOIN encounter ee ON ee.patient_id = pp.patient_id     "
            + "                INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id   "
            + "          WHERE pp.voided = 0    "
            + "                AND p.patient_id = pp.patient_id     "
            + "               AND ee.voided = 0     "
            + "               AND oo.voided = 0     "
            + "               AND ee.location_id = :location    "
            + "               AND ee.encounter_type = ${6}     "
            + "               AND oo.concept_id = ${1719}  "
            + "               AND oo.value_coded = ${23954}    "
            + "               AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 120 DAY)     "
            + "               AND ee.encounter_datetime < pickup.first_pickup_date  "
            + "     UNION    "
            + "        SELECT pp.patient_id     "
            + "              FROM patient pp    "
            + "                    INNER JOIN encounter ee ON ee.patient_id = pp.patient_id     "
            + "                    INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id   "
            + "              WHERE pp.voided = 0    "
            + "                    AND p.patient_id = pp.patient_id     "
            + "                   AND ee.voided = 0     "
            + "                   AND oo.voided = 0     "
            + "                   AND ee.location_id = :location    "
            + "                   AND ee.encounter_type = ${60}    "
            + "                   AND oo.concept_id = ${23985}     "
            + "                   AND oo.value_coded IN (${23954},${23984})   "
            + "                   AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 120 DAY)     "
            + "                   AND ee.encounter_datetime < pickup.first_pickup_date)     ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A3.2:Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       encounter datetime between endDate – 4 months (120 DAYs) and end date and no other 3HP
   *       pick-ups marked on FILT in the 4 months prior to the 3HP Start Date. and no “Regime de
   *       TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept id in
   *       [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the 3HP Start
   *       Date;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartA2(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int threeHPConcept,
      int hPPiridoxinaConcept,
      int adultoSeguimentoEncounterType,
      int treatmentPrescribedConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras Prescricoes A2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23954", threeHPConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1719", treatmentPrescribedConcept);

    String query =
        " SELECT p.patient_id                                                                               "
            + "     FROM  patient p   "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "     INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "     INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date  "
            + "  FROM    patient p   "
            + "  INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "  INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "  WHERE   p.voided = 0   "
            + "      AND e.voided = 0   "
            + "      AND o.voided = 0   "
            + "      AND e.location_id = :location  "
            + "      AND e.encounter_type = ${60} "
            + "      AND o.concept_id = ${23985} "
            + "      AND o.value_coded IN (${23954}, ${23984})  "
            + "      AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 120 DAY) AND :endDate"
            + "  GROUP BY p.patient_id) AS pickup  "
            + "  ON pickup.patient_id = p.patient_id "
            + "     WHERE p.patient_id NOT IN ( SELECT pp.patient_id   "
            + "   FROM patient pp  "
            + "         INNER JOIN encounter ee ON ee.patient_id = pp.patient_id  "
            + "         INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "   WHERE pp.voided = 0   "
            + "         AND p.patient_id = pp.patient_id "
            + "        AND ee.voided = 0   "
            + "        AND oo.voided = 0   "
            + "        AND ee.location_id = :location  "
            + "        AND ee.encounter_type = ${60}"
            + "        AND oo.concept_id = ${23985} "
            + "        AND oo.value_coded IN (${23954}, ${23984})  "
            + "        AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 120 DAY)   "
            + "        AND ee.encounter_datetime < pickup.first_pickup_date                      "
            + "       UNION"
            + "       SELECT pp.patient_id   "
            + "   FROM patient pp  "
            + "         INNER JOIN encounter ee ON ee.patient_id = pp.patient_id  "
            + "         INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "   WHERE pp.voided = 0   "
            + "         AND p.patient_id = pp.patient_id "
            + "        AND ee.voided = 0   "
            + "        AND oo.voided = 0   "
            + "        AND ee.location_id = :location  "
            + "        AND ee.encounter_type = ${6} "
            + "        AND oo.concept_id = ${1719} "
            + "        AND oo.value_coded = ${23954}  "
            + "        AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 120 DAY)   "
            + "        AND ee.encounter_datetime < pickup.first_pickup_date )  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A3.3:Select all patients with “Regime de TPT” (concept id 23985) with value coded “3HP”
   *       or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento
   *       TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in [1256, 1705])
   *       marked on FILT (encounter type 60) and encounter datetime between endDate - 4months and
   *       end date
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartA3(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int threeHPConcept,
      int hPPiridoxinaConcept,
      int startDrugsConcept,
      int treatmentFollowUpTPTConcept,
      int restartConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras Prescricoes A3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23954", threeHPConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("1256", startDrugsConcept);
    map.put("23987", treatmentFollowUpTPTConcept);
    map.put("1705", restartConcept);

    String query =
        " SELECT p.patient_id  "
            + " FROM  patient p  "
            + "   INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "   INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "   INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "   AND e.location_id = :location AND e.encounter_type = ${60} "
            + "   AND (o.concept_id = ${23985} AND o.value_coded IN (${23954},${23984})) "
            + "   AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256},${1705})) "
            + "   AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 120 DAY) AND :endDate ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B1: Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia
   *       Isoniazida (Data Fim)” (concept id 6129) value datetime not null and between (date from
   *       Y+ 173 DAYs) and (date from Y + 365 DAYs)
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB1(
      int masterCardEncounterType,
      int dataFinalizacaoProfilaxiaIsoniazidaConcept,
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int completedConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int pediatriaSeguimentoEncounterType,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Fim) B1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6129", dataFinalizacaoProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("1267", completedConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        "   SELECT "
            + "                p.patient_id "
            + "            FROM "
            + "                patient p "
            + "                 INNER JOIN"
            + "             encounter e ON e.patient_id = p.patient_id"
            + "                 INNER JOIN"
            + "             obs o ON o.encounter_id = e.encounter_id"
            + "         WHERE"
            + "             p.voided = 0 AND e.voided = 0"
            + "                 AND o.voided = 0"
            + "                 AND e.location_id = :location"
            + "                AND e.encounter_type = ${53}   "
            + "                AND o.concept_id = ${6129}  "
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT "
            + "                             oo.value_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 JOIN"
            + "                             obs oo ON oo.encounter_id = ee.encounter_id"
            + "                         WHERE"
            + "                             ee.encounter_type = ${53}"
            + "                                 AND oo.concept_id = ${6128}"
            + "                                 AND p.patient_id = ee.patient_id"
            + "                                 AND oo.voided = 0"
            + "                                 AND ee.voided = 0"
            + "                                 AND ee.location_id = :location"
            + "                                 AND oo.value_datetime IS NOT NULL"
            + "                                 AND oo.value_datetime <= :endDate"
            + "                         LIMIT 1)"
            + "                     AS DATE),"
            + "                 INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT "
            + "                                 oo.value_datetime"
            + "                             FROM"
            + "                                 encounter ee"
            + "                                     JOIN"
            + "                                 obs oo ON oo.encounter_id = ee.encounter_id"
            + "                             WHERE"
            + "                                 ee.encounter_type = ${53}"
            + "                                     AND oo.concept_id = ${6128}"
            + "                                     AND p.patient_id = ee.patient_id"
            + "                                     AND oo.voided = 0"
            + "                                     AND ee.voided = 0"
            + "                                     AND ee.location_id = :location"
            + "                                     AND oo.value_datetime IS NOT NULL"
            + "                                     AND oo.value_datetime <= :endDate"
            + "                             LIMIT 1) AS DATE),"
            + "                 INTERVAL 365 DAY)"
            + "         GROUP BY p.patient_id"
            + "     UNION "
            + "         SELECT "
            + "             p.patient_id"
            + "       FROM"
            + "              patient p "
            + "                  INNER JOIN"
            + "             encounter e ON e.patient_id = p.patient_id"
            + "                 INNER JOIN"
            + "             obs o ON o.encounter_id = e.encounter_id"
            + "      WHERE"
            + "          p.voided = 0 AND e.voided = 0"
            + "              AND o.voided = 0"
            + "                 AND e.location_id = :location"
            + "                AND e.encounter_type = ${53}   "
            + "                AND o.concept_id = ${6129}  "
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT "
            + "                             ee.encounter_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 JOIN"
            + "                             obs oo ON oo.encounter_id = ee.encounter_id"
            + "                         WHERE"
            + "                             ee.encounter_type = ${6}"
            + "                                 AND oo.concept_id = ${6122}"
            + "                                 AND oo.voided = 0"
            + "                                 AND ee.voided = 0"
            + "                                 AND p.patient_id = ee.patient_id"
            + "                                 AND ee.location_id = :location"
            + "                                 AND oo.value_coded = ${1256}"
            + "                                 AND ee.encounter_datetime <= :endDate"
            + "                         LIMIT 1)"
            + "                     AS DATE),"
            + "                 INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT "
            + "                                 ee.encounter_datetime"
            + "                             FROM"
            + "                                 encounter ee"
            + "                                     JOIN"
            + "                                 obs oo ON oo.encounter_id = ee.encounter_id"
            + "                             WHERE"
            + "                                 ee.encounter_type = ${6}"
            + "                                     AND oo.concept_id = ${6122}"
            + "                                     AND oo.voided = 0"
            + "                                     AND ee.voided = 0"
            + "                                     AND p.patient_id = ee.patient_id"
            + "                                     AND ee.location_id = :location"
            + "                                     AND oo.value_coded = ${1256}"
            + "                                     AND ee.encounter_datetime <= :endDate"
            + "                             LIMIT 1) AS DATE),"
            + "                 INTERVAL 365 DAY)"
            + "         GROUP BY p.patient_id"
            + "     UNION "
            + "         SELECT "
            + "             p.patient_id"
            + "         FROM"
            + "             patient p"
            + "                 INNER JOIN"
            + "             encounter e ON e.patient_id = p.patient_id"
            + "                 INNER JOIN"
            + "             obs o ON o.encounter_id = e.encounter_id"
            + "         WHERE"
            + "             p.voided = 0 AND e.voided = 0"
            + "                 AND o.voided = 0"
            + "                 AND e.location_id = :location"
            + "                AND e.encounter_type = ${53}   "
            + "                AND o.concept_id = ${6129}  "
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT "
            + "                             ee.encounter_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 INNER JOIN"
            + "                             obs oo ON ee.encounter_id = oo.encounter_id"
            + "                         WHERE"
            + "                             ee.voided = 0 AND oo.voided = 0"
            + "                                 AND p.patient_id = ee.patient_id"
            + "                                 AND ee.encounter_type IN (${6} , ${9})"
            + "                                 AND oo.concept_id = ${6128}"
            + "                                 AND ee.location_id = :location"
            + "                                 AND oo.value_datetime IS NOT NULL"
            + "                                 AND oo.value_datetime <= :endDate"
            + "                         LIMIT 1)"
            + "                     AS DATE),"
            + "                 INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT "
            + "                             ee.encounter_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 INNER JOIN"
            + "                             obs oo ON ee.encounter_id = oo.encounter_id"
            + "                         WHERE"
            + "                             ee.voided = 0 AND oo.voided = 0"
            + "                                 AND p.patient_id = ee.patient_id"
            + "                                 AND ee.encounter_type IN (${6} , ${9})"
            + "                                 AND oo.concept_id = ${6128}"
            + "                                 AND ee.location_id = :location"
            + "                                 AND oo.value_datetime IS NOT NULL"
            + "                                 AND oo.value_datetime <= :endDate"
            + "                         LIMIT 1)"
            + "                     AS DATE),"
            + "                 INTERVAL 365 DAY)"
            + "         GROUP BY p.patient_id"
            + "     UNION "
            + "         SELECT "
            + "             p.patient_id"
            + "         FROM"
            + "             patient p"
            + "                 INNER JOIN"
            + "             encounter e ON e.patient_id = p.patient_id"
            + "                 INNER JOIN"
            + "             obs o ON o.encounter_id = e.encounter_id"
            + "         WHERE"
            + "             p.voided = 0 AND e.voided = 0"
            + "                 AND o.voided = 0"
            + "                 AND e.location_id = :location"
            + "                AND e.encounter_type = ${53}   "
            + "                AND o.concept_id = ${6129}  "
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT "
            + "                             ee.encounter_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 INNER JOIN"
            + "                             obs oo ON ee.encounter_id = oo.encounter_id"
            + "                         WHERE"
            + "                             ee.voided = 0 AND oo.voided = 0"
            + "                                 AND p.patient_id = ee.patient_id"
            + "                                 AND ee.encounter_type = ${60}"
            + "                                 AND oo.concept_id = ${23985}"
            + "                                 AND oo.value_coded IN (${656} , ${23982})"
            + "                                 AND ee.location_id = :location"
            + "                                 AND ee.encounter_datetime <= :endDate"
            + "                         LIMIT 1)"
            + "                     AS DATE),"
            + "                 INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT "
            + "                             ee.encounter_datetime"
            + "                         FROM"
            + "                             encounter ee"
            + "                                 INNER JOIN"
            + "                                obs oo ON ee.encounter_id = oo.encounter_id"
            + "                            WHERE"
            + "                                ee.voided = 0 AND oo.voided = 0"
            + "                                    AND p.patient_id = ee.patient_id"
            + "                                    AND ee.encounter_type = ${60}"
            + "                                    AND oo.concept_id = ${23985}"
            + "                                    AND oo.value_coded IN (${656} , ${23982})"
            + "                                    AND ee.location_id = :location"
            + "                                    AND ee.encounter_datetime <= :endDate"
            + "                            LIMIT 1)"
            + "                        AS DATE),"
            + "                    INTERVAL 365 DAY)"
            + "         GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B2: Select all patients with Ficha clinica - Master Card (encounter type 6) with
   *       “Profilaxia INH” (concept id 6122) with value code “Fim” (concept id 1267) and encounter
   *       datetime between (date from Y + 173 DAYs) and (date from Y + 365 DAYs)
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB2(
      int masterCardEncounterType,
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int completedConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int pediatriaSeguimentoEncounterType,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("1267", completedConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        " SELECT p.patient_id"
            + "     FROM   patient p"
            + "       INNER JOIN encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6122}"
            + "       AND o.value_coded = ${1267}"
            + "       AND e.encounter_datetime BETWEEN Date_add("
            + "           Cast((SELECT oo.value_datetime FROM"
            + "           encounter"
            + "           ee JOIN obs oo ON"
            + "           oo.encounter_id"
            + "                ="
            + "                ee.encounter_id WHERE ee.encounter_type ="
            + "           ${53} AND oo.concept_id = ${6128}"
            + "           AND"
            + "                p.patient_id ="
            + "                ee.patient_id AND oo.voided = 0 AND"
            + "           ee.voided = 0 AND ee.location_id ="
            + "           :location AND"
            + "                oo.value_datetime IS NOT NULL AND"
            + "           oo.value_datetime <= :endDate"
            + "           LIMIT 1) AS"
            + "                date), INTERVAL 173 DAY) AND"
            + "           Date_add("
            + "               Cast((SELECT oo.value_datetime FROM encounter ee JOIN obs oo ON"
            + "               oo.encounter_id"
            + "               ="
            + "               ee.encounter_id WHERE"
            + "               ee.encounter_type = ${53} AND oo.concept_id = ${6128}"
            + "                               AND"
            + "               p.patient_id ="
            + "               ee.patient_id AND oo.voided = 0"
            + "               AND ee.voided = 0 AND"
            + "               ee.location_id ="
            + "                     :location AND"
            + "               oo.value_datetime IS NOT NULL AND"
            + "               oo.value_datetime <= :endDate"
            + "                   LIMIT 1) AS"
            + "               date), INTERVAL 365 DAY)"
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6122}"
            + "       AND o.value_coded = ${1267}"
            + "       AND e.encounter_datetime BETWEEN Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter ee JOIN obs oo ON"
            + "                oo.encounter_id ="
            + "                ee.encounter_id WHERE ee.encounter_type ="
            + "           ${6} AND oo.concept_id = ${6122}"
            + "           AND"
            + "                oo.voided = 0 AND ee.voided = 0 AND"
            + "           p.patient_id = ee.patient_id AND"
            + "                ee.location_id ="
            + "                :location AND oo.value_coded = ${1256} AND"
            + "           ee.encounter_datetime <= :endDate"
            + "           LIMIT 1)"
            + "                AS date), INTERVAL 173 DAY) AND Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter ee JOIN obs oo ON"
            + "           oo.encounter_id ="
            + "           ee.encounter_id WHERE"
            + "           ee.encounter_type = 6 AND oo.concept_id = ${6122}"
            + "                              AND"
            + "           oo.voided = 0 AND ee.voided ="
            + "           0 AND p.patient_id ="
            + "           ee.patient_id AND"
            + "           ee.location_id ="
            + "           :location AND oo.value_coded = ${1256}"
            + "           AND ee.encounter_datetime <="
            + "           :endDate"
            + "                      LIMIT 1)"
            + "           AS date), INTERVAL 365 DAY)"
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6122}"
            + "       AND o.value_coded = ${1267}"
            + "       AND e.encounter_datetime BETWEEN Date_add("
            + "           Cast((SELECT ee.encounter_datetime"
            + "           FROM"
            + "           encounter ee INNER JOIN obs oo ON"
            + "                ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "           ee.voided = 0 AND oo.voided = 0"
            + "                                                 AND"
            + "                p.patient_id ="
            + "                ee.patient_id AND ee.encounter_type IN"
            + "           (${6},"
            + "           ${9}) AND oo.concept_id = ${6128}"
            + "                                                 AND"
            + "                ee.location_id ="
            + "                :location AND oo.value_datetime IS NOT NULL"
            + "           AND"
            + "           oo.value_datetime <="
            + "           :endDate"
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter ee INNER JOIN obs oo ON"
            + "                    ee.encounter_id = oo.encounter_id"
            + "           WHERE ee.voided = 0 AND oo.voided = 0"
            + "                                            AND"
            + "                    p.patient_id ="
            + "                    ee.patient_id AND ee.encounter_type IN"
            + "           (${6}, ${9}) AND oo.concept_id = ${6128}"
            + "                                            AND"
            + "                    ee.location_id ="
            + "                    :location AND oo.value_datetime IS NOT NULL"
            + "           AND oo.value_datetime <="
            + "                                            :endDate"
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY)"
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6122}"
            + "       AND o.value_coded = ${1267}"
            + "       AND e.encounter_datetime BETWEEN Date_add("
            + "           Cast((SELECT ee.encounter_datetime"
            + "           FROM"
            + "           encounter ee INNER JOIN obs oo ON"
            + "                ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "           ee.voided = 0 AND oo.voided = 0"
            + "                                                 AND"
            + "                p.patient_id ="
            + "                ee.patient_id AND ee.encounter_type ="
            + "           ${60}"
            + "           AND oo.concept_id = ${23985} AND"
            + "                oo.value_coded IN ("
            + "                ${656}, ${23982}) AND ee.location_id = :location"
            + "           AND"
            + "           ee.encounter_datetime <="
            + "           :endDate"
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter ee INNER JOIN obs oo ON"
            + "                    ee.encounter_id = oo.encounter_id"
            + "           WHERE ee.voided = 0 AND oo.voided = 0"
            + "                                            AND"
            + "                    p.patient_id ="
            + "                    ee.patient_id AND ee.encounter_type ="
            + "           ${60} AND oo.concept_id = ${23985} AND"
            + "                    oo.value_coded IN ("
            + "                    ${656}, ${23982}) AND ee.location_id = :location"
            + "           AND ee.encounter_datetime <="
            + "                                            :endDate"
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY)"
            + "         GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B3: Select all patients with Ficha de Seguimento - Adulto (encounter type 6) with
   *       “Profilaxia com INH” (concept id 6129) value datetime between (date from Y + 173 DAYs)
   *       and (date from Y + 365 DAYs)
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB3(
      int masterCardEncounterType,
      int dataFinalizacaoProfilaxiaIsoniazidaConcept,
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int pediatriaSeguimentoEncounterType,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6129", dataFinalizacaoProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        "   SELECT p.patient_id"
            + " FROM   patient p"
            + "       INNER JOIN encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + " WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6129}"
            + "       AND o.value_datetime IS NOT NULL"
            + "       AND o.value_datetime BETWEEN Date_add("
            + "               Cast((SELECT oo.value_datetime FROM encounter ee JOIN obs oo ON"
            + "                                        oo.encounter_id"
            + "                                             ="
            + "                                             ee.encounter_id WHERE"
            + "               ee.encounter_type ="
            + "               ${53}"
            + "                                                 AND oo.concept_id = ${6128}"
            + "                                                                          AND"
            + "                                             p.patient_id ="
            + "                                             ee.patient_id AND oo.voided = 0 AND"
            + "               ee.voided"
            + "                                                 = 0 AND ee.location_id ="
            + "                                                                          :location"
            + "               AND"
            + "                                             oo.value_datetime IS NOT NULL AND"
            + "               oo.value_datetime <="
            + "               :endDate"
            + "                                                                          LIMIT"
            + "               1) AS"
            + "                                             date), INTERVAL 173 DAY) AND"
            + "                                        Date_add(Cast((SELECT oo.value_datetime"
            + "                                                 FROM"
            + "                                                 encounter ee JOIN obs oo ON"
            + "                                                 oo.encounter_id"
            + "                                                 ="
            + "                                                 ee.encounter_id WHERE"
            + "                                                 ee.encounter_type = ${53} AND"
            + "                                                 oo.concept_id ="
            + "                                                 ${6128}"
            + "                                                                 AND"
            + "                                                 p.patient_id ="
            + "                                                 ee.patient_id AND oo.voided = 0"
            + "                                                 AND ee.voided = 0 AND"
            + "                                                 ee.location_id ="
            + "                                                       :location AND"
            + "                                                 oo.value_datetime IS NOT NULL"
            + "                                                 AND"
            + "                                                 oo.value_datetime <="
            + "                                                 :endDate"
            + "                                                     LIMIT 1) AS"
            + "                                                 date), INTERVAL 365 DAY)"
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "       INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6129}"
            + "       AND o.value_datetime IS NOT NULL"
            + "       AND o.value_datetime BETWEEN Date_add("
            + "               Cast((SELECT ee.encounter_datetime FROM encounter ee JOIN obs oo"
            + "               ON"
            + "                                             oo.encounter_id ="
            + "                                             ee.encounter_id WHERE"
            + "               ee.encounter_type ="
            + "               ${6}"
            + "                                                 AND oo.concept_id = ${6122}"
            + "                                                                          AND"
            + "                                             oo.voided = 0 AND ee.voided = 0 AND"
            + "               p.patient_id ="
            + "               ee.patient_id AND"
            + "                                             ee.location_id ="
            + "                                             :location AND oo.value_coded = ${1256} AND"
            + "               ee.encounter_datetime <= :endDate"
            + "                                                                          LIMIT"
            + "               1)"
            + "                                             AS date), INTERVAL 173 DAY) AND"
            + "                                        Date_add(Cast((SELECT"
            + "                                                 ee.encounter_datetime"
            + "                                                 FROM"
            + "                                                 encounter ee JOIN obs oo ON"
            + "                                                 oo.encounter_id ="
            + "                                                 ee.encounter_id WHERE"
            + "                                                 ee.encounter_type = ${6} AND"
            + "                                                 oo.concept_id ="
            + "                                                 ${6122}"
            + "                                                                    AND"
            + "                                                 oo.voided = 0 AND ee.voided ="
            + "                                                 0 AND p.patient_id ="
            + "                                                 ee.patient_id AND"
            + "                                                 ee.location_id ="
            + "                                                 :location AND oo.value_coded = ${1256}"
            + "                                                 AND ee.encounter_datetime <="
            + "                                                 :endDate"
            + "                                                            LIMIT 1)"
            + "                                                 AS date), INTERVAL 365 DAY)"
            + "    GROUP  BY p.patient_id"
            + "    UNION"
            + "    SELECT p.patient_id"
            + "    FROM   patient p"
            + "       INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6129}"
            + "       AND o.value_datetime IS NOT NULL"
            + "       AND o.value_datetime BETWEEN Date_add("
            + "           Cast((SELECT ee.encounter_datetime"
            + "           FROM"
            + "           encounter ee INNER JOIN obs oo"
            + "                                        ON"
            + "           ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "               ee.voided = 0 AND oo.voided = 0"
            + "                                        AND"
            + "           p.patient_id ="
            + "           ee.patient_id AND ee.encounter_type"
            + "           IN (${6},"
            + "           ${9})"
            + "               AND oo.concept_id = ${6128}"
            + "                                        AND"
            + "           ee.location_id ="
            + "           :location AND oo.value_datetime IS NOT NULL AND"
            + "               oo.value_datetime <="
            + "                                        :endDate"
            + "           LIMIT 1) AS date), INTERVAL 173 DAY) AND"
            + "                                        Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter"
            + "           ee INNER JOIN obs oo ON"
            + "                    ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "           ee.voided = 0 AND oo.voided = 0"
            + "                                        AND"
            + "                    p.patient_id ="
            + "                    ee.patient_id AND ee.encounter_type IN"
            + "           (${6},"
            + "           ${9}) AND oo.concept_id = ${6128}"
            + "                                        AND"
            + "                    ee.location_id ="
            + "                    :location AND oo.value_datetime IS NOT NULL"
            + "           AND"
            + "           oo.value_datetime <="
            + "                                        :endDate"
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY)"
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "       INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${6129}"
            + "       AND o.value_datetime IS NOT NULL"
            + "       AND o.value_datetime BETWEEN Date_add("
            + "           Cast((SELECT ee.encounter_datetime"
            + "           FROM"
            + "           encounter ee INNER JOIN obs oo"
            + "                                        ON"
            + "           ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "               ee.voided = 0 AND oo.voided = 0"
            + "                                        AND"
            + "           p.patient_id ="
            + "           ee.patient_id AND ee.encounter_type"
            + "           = ${60}"
            + "           AND"
            + "               oo.concept_id = ${23985} AND"
            + "           oo.value_coded IN ("
            + "           ${656}, ${23982}) AND ee.location_id = :location AND"
            + "               ee.encounter_datetime <="
            + "                                        :endDate"
            + "           LIMIT 1) AS date), INTERVAL 173 DAY) AND"
            + "                                        Date_add("
            + "           Cast((SELECT ee.encounter_datetime FROM"
            + "           encounter"
            + "           ee INNER JOIN obs oo ON"
            + "                    ee.encounter_id = oo.encounter_id"
            + "           WHERE"
            + "           ee.voided = 0 AND oo.voided = 0"
            + "                                        AND"
            + "                    p.patient_id ="
            + "                    ee.patient_id AND ee.encounter_type ="
            + "           ${60}"
            + "           AND oo.concept_id = ${23985} AND"
            + "                    oo.value_coded IN ("
            + "                    ${656}, ${23982}) AND ee.location_id = :location "
            + "         AND"
            + "        ee.encounter_datetime <="
            + "                                     :endDate"
            + "                 LIMIT 1) AS date), INTERVAL 365 DAY)"
            + "          GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B4: Select all patients with Ficha de Seguimento - Pediatrico (encounter type 9) with
   *       “Profilaxia com INH” (concept id 6129) value datetime between (date from Y+173 DAYs) and
   *       (date from Y + 365 DAYs)
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB4(
      int masterCardEncounterType,
      int dataFinalizacaoProfilaxiaIsoniazidaConcept,
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int pediatriaSeguimentoEncounterType,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B4");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6129", dataFinalizacaoProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        "   SELECT"
            + "            p.patient_id"
            + "        FROM"
            + "            patient p"
            + "                INNER JOIN"
            + "            encounter e ON e.patient_id = p.patient_id"
            + "                INNER JOIN"
            + "            obs o ON o.encounter_id = e.encounter_id"
            + "        WHERE"
            + "            p.voided = 0 AND e.voided = 0"
            + "                AND o.voided = 0"
            + "                AND e.location_id = :location"
            + "                AND e.encounter_type = ${9}"
            + "                AND o.concept_id = ${6129}"
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT"
            + "                            oo.value_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                JOIN"
            + "                            obs oo ON oo.encounter_id = ee.encounter_id"
            + "                        WHERE"
            + "                            ee.encounter_type = ${53}"
            + "                                AND oo.concept_id = ${6128}"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND oo.voided = 0"
            + "                                AND ee.voided = 0"
            + "                                AND ee.location_id = :location"
            + "                                AND oo.value_datetime IS NOT NULL"
            + "                                AND oo.value_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT"
            + "                                oo.value_datetime"
            + "                            FROM"
            + "                                encounter ee"
            + "                                    JOIN"
            + "                                obs oo ON oo.encounter_id = ee.encounter_id"
            + "                            WHERE"
            + "                                ee.encounter_type = ${53}"
            + "                                    AND oo.concept_id = ${6128}"
            + "                                    AND p.patient_id = ee.patient_id"
            + "                                    AND oo.voided = 0"
            + "                                    AND ee.voided = 0"
            + "                                    AND ee.location_id = :location"
            + "                                    AND oo.value_datetime IS NOT NULL"
            + "                                    AND oo.value_datetime <= :endDate"
            + "                            LIMIT 1) AS DATE),"
            + "                INTERVAL 365 DAY)"
            + "        GROUP BY p.patient_id"
            + "     UNION"
            + "        SELECT"
            + "            p.patient_id"
            + "        FROM"
            + "            patient p"
            + "                INNER JOIN"
            + "            encounter e ON e.patient_id = p.patient_id"
            + "                INNER JOIN"
            + "            obs o ON o.encounter_id = e.encounter_id"
            + "        WHERE"
            + "            p.voided = 0 AND e.voided = 0"
            + "                AND o.voided = 0"
            + "                AND e.location_id = :location"
            + "                AND e.encounter_type = ${9}"
            + "                AND o.concept_id = ${6129}"
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT"
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                JOIN"
            + "                            obs oo ON oo.encounter_id = ee.encounter_id"
            + "                        WHERE"
            + "                            ee.encounter_type = ${6}"
            + "                                AND oo.concept_id = ${6122}"
            + "                                AND oo.voided = 0"
            + "                                AND ee.voided = 0"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND ee.location_id = :location"
            + "                                AND oo.value_coded = ${1256}"
            + "                                AND ee.encounter_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT"
            + "                                ee.encounter_datetime"
            + "                            FROM"
            + "                                encounter ee"
            + "                                    JOIN"
            + "                                obs oo ON oo.encounter_id = ee.encounter_id"
            + "                            WHERE"
            + "                                ee.encounter_type = ${6}"
            + "                                    AND oo.concept_id = ${6122}"
            + "                                    AND oo.voided = 0"
            + "                                    AND ee.voided = 0"
            + "                                    AND p.patient_id = ee.patient_id"
            + "                                    AND ee.location_id = :location"
            + "                                    AND oo.value_coded = ${1256}"
            + "                                    AND ee.encounter_datetime <= :endDate"
            + "                            LIMIT 1) AS DATE),"
            + "                INTERVAL 365 DAY)"
            + "        GROUP BY p.patient_id"
            + "     UNION"
            + "        SELECT"
            + "            p.patient_id"
            + "        FROM"
            + "            patient p"
            + "                INNER JOIN"
            + "            encounter e ON e.patient_id = p.patient_id"
            + "                INNER JOIN"
            + "            obs o ON o.encounter_id = e.encounter_id"
            + "        WHERE"
            + "            p.voided = 0 AND e.voided = 0"
            + "                AND o.voided = 0"
            + "                AND e.location_id = :location"
            + "                AND e.encounter_type = ${9}"
            + "                AND o.concept_id = ${6129}"
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT"
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                INNER JOIN"
            + "                            obs oo ON ee.encounter_id = oo.encounter_id"
            + "                        WHERE"
            + "                            ee.voided = 0 AND oo.voided = 0"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND ee.encounter_type IN (${6} , ${9})"
            + "                                AND oo.concept_id = ${6128}"
            + "                                AND ee.location_id = :location"
            + "                                AND oo.value_datetime IS NOT NULL"
            + "                                AND oo.value_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT"
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                INNER JOIN"
            + "                            obs oo ON ee.encounter_id = oo.encounter_id"
            + "                        WHERE"
            + "                            ee.voided = 0 AND oo.voided = 0"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND ee.encounter_type IN (${6} , ${9})"
            + "                                AND oo.concept_id = ${6128}"
            + "                                AND ee.location_id = :location"
            + "                                AND oo.value_datetime IS NOT NULL"
            + "                                AND oo.value_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 365 DAY)"
            + "        GROUP BY p.patient_id"
            + "     UNION"
            + "        SELECT"
            + "            p.patient_id"
            + "        FROM"
            + "            patient p"
            + "                INNER JOIN"
            + "            encounter e ON e.patient_id = p.patient_id"
            + "                INNER JOIN"
            + "            obs o ON o.encounter_id = e.encounter_id"
            + "        WHERE"
            + "            p.voided = 0 AND e.voided = 0"
            + "                AND o.voided = 0"
            + "                AND e.location_id = :location"
            + "                AND e.encounter_type = ${9}"
            + "                AND o.concept_id = ${6129}"
            + "                AND o.value_datetime IS NOT NULL"
            + "                AND o.value_datetime BETWEEN DATE_ADD(CAST((SELECT"
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                INNER JOIN"
            + "                            obs oo ON ee.encounter_id = oo.encounter_id"
            + "                        WHERE"
            + "                            ee.voided = 0 AND oo.voided = 0"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND ee.encounter_type = ${60}"
            + "                                AND oo.concept_id = ${23985}"
            + "                                AND oo.value_coded IN (${656} , ${23982})"
            + "                                AND ee.location_id = :location"
            + "                                AND ee.encounter_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 173 DAY) AND DATE_ADD(CAST((SELECT"
            + "                            ee.encounter_datetime"
            + "                        FROM"
            + "                            encounter ee"
            + "                                INNER JOIN"
            + "                            obs oo ON ee.encounter_id = oo.encounter_id"
            + "                        WHERE"
            + "                            ee.voided = 0 AND oo.voided = 0"
            + "                                AND p.patient_id = ee.patient_id"
            + "                                AND ee.encounter_type = ${60}"
            + "                                AND oo.concept_id = ${23985}"
            + "                                AND oo.value_coded IN (${656} , ${23982})"
            + "                                AND ee.location_id = :location"
            + "                                AND ee.encounter_datetime <= :endDate"
            + "                        LIMIT 1)"
            + "                    AS DATE),"
            + "                INTERVAL 365 DAY)"
            + "        GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5- If date Y is registered on Ficha Clinica (encounter type 6) check if the patient has:
   *   <li>At least 5 consultations (((encounter type 6) with (profilaxia INH (6122)= inicio(I) or
   *       Continua(C) (concept id in [1256, 1257])) or ((encounter type 9) “Profilaxia com INH-TPI”
   *       (concept id 6122) with value coded “YES” (concept id 1065))) during 210 DAYs from the
   *       date Y2,3 or
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB5Part1(
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int pediatriaSeguimentoEncounterType,
      int continuaConcept,
      int yesConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B5.1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1065", yesConcept);

    String query =
        "   SELECT result.patient_id "
            + "         FROM (SELECT p.patient_id,tabela.encounter_datetime   "
            + "            FROM   patient p   "
            + "            INNER JOIN encounter e "
            + "                    ON e.patient_id = p.patient_id "
            + "            INNER JOIN obs o   "
            + "                    ON o.encounter_id = e.encounter_id "
            + "            INNER JOIN(SELECT p.patient_id,    "
            + "                              e.encounter_datetime "
            + "                       FROM   patient p    "
            + "                              INNER JOIN encounter e   "
            + "                                      ON e.patient_id = p.patient_id   "
            + "                              INNER JOIN obs o "
            + "                                      ON o.encounter_id = e.encounter_id   "
            + "                       WHERE  p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.location_id = :location   "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND ( ( o.concept_id = ${6122}  "
            + "                                      AND o.value_coded = ${1256} )   "
            + "                                     OR ( o.concept_id = ${6128}  "
            + "                                          AND o.value_datetime IS NOT NULL ) ) "
            + "                              AND e.encounter_datetime <= :endDate GROUP by p.patient_id) AS tabela    "
            + "                    ON tabela.patient_id = p.patient_id    "
            + "                    WHERE e.location_id= :location AND e.encounter_type= ${6} AND e.voided= 0 AND o.voided= 0   "
            + "                    GROUP BY p.patient_id) result  "
            + "      WHERE   (( (SELECT Count(ee.patient_id)  "
            + "                     FROM   encounter ee   "
            + "                            INNER JOIN obs oo  "
            + "                                    ON oo.encounter_id = ee.encounter_id   "
            + "                     WHERE  ee.voided = 0  "
            + "                            AND result.patient_id = ee.patient_id  "
            + "                            AND oo.voided = 0  "
            + "                            AND ee.location_id = :location   "
            + "                            AND oo.concept_id = ${6122}   "
            + "                            AND ee.encounter_datetime BETWEEN  "
            + "                                result.encounter_datetime AND  "
            + "                    Date_add(result.encounter_datetime,    "
            + "                    INTERVAL 210 DAY)  "
            + "                    AND ( ( ee.encounter_type = ${6}  "
            + "                    AND oo.value_coded IN ( ${1256}, ${1257} ) ) "
            + "                    OR ( ee.encounter_type = ${9} "
            + "                    AND oo.value_coded = ${1065} ) )) >= 5 )) "
            + "      GROUP  BY result.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5: At least 2 consultations (encounter type 6) (with profilaxia INH (concept id 6122) =
   *       inicio(I) or Continua(C) (concept id in [1256, 1257]) and has “Outras prescricoes”
   *       (concept id 1719) with value coded equal to “DT-INH” (concept id 23955) )during 150 DAYs
   *       from the date from Y2,3 or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB5Part2(
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int continuaConcept,
      int treatmentPrescribedConcept,
      int dtINHConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B5.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23955", dtINHConcept);

    String query =
        "   SELECT result.patient_id    "
            + "                FROM (SELECT p.patient_id,tabela.encounter_datetime    "
            + "                   FROM   patient p    "
            + "                   INNER JOIN encounter e  "
            + "                           ON e.patient_id = p.patient_id  "
            + "                   INNER JOIN obs o    "
            + "                           ON o.encounter_id = e.encounter_id  "
            + "                   INNER JOIN(SELECT p.patient_id, "
            + "                                     e.encounter_datetime  "
            + "                              FROM   patient p "
            + "                                     INNER JOIN encounter e    "
            + "                                             ON e.patient_id = p.patient_id    "
            + "                                     INNER JOIN obs o  "
            + "                                             ON o.encounter_id = e.encounter_id    "
            + "                              WHERE  p.voided = 0  "
            + "                                     AND e.voided = 0  "
            + "                                     AND o.voided = 0  "
            + "                                     AND e.location_id = :location    "
            + "                                     AND e.encounter_type = ${6}  "
            + "                                     AND ( ( o.concept_id = ${6122}   "
            + "                                             AND o.value_coded = ${1256} )    "
            + "                                            OR ( o.concept_id = ${6128}   "
            + "                                                 AND o.value_datetime IS NOT NULL ) )  "
            + "                                     AND e.encounter_datetime <= :endDate GROUP by p.patient_id) AS tabela "
            + "                           ON tabela.patient_id = p.patient_id "
            + "                           WHERE e.location_id= :location AND e.encounter_type= ${6} AND e.voided=0 AND o.voided=0    "
            + "                           GROUP BY p.patient_id) result   "
            + "               WHERE   ( (SELECT Count(*)  "
            + "                          FROM   patient pp    "
            + "                                 JOIN encounter ee "
            + "                                   ON pp.patient_id = ee.patient_id    "
            + "                          WHERE  pp.voided = 0 "
            + "                                 AND ee.voided = 0 "
            + "                                 AND result.patient_id = pp.patient_id "
            + "                                 AND ee.encounter_type = ${6} "
            + "                                 AND ee.location_id = :location  "
            + "                                 AND ee.voided = 0 "
            + "                                 AND ( EXISTS (SELECT oo.obs_id    "
            + "                                               FROM obs oo "
            + "                                               WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                                      AND oo.concept_id = ${6122} "
            + "                                                      AND oo.value_coded IN(${1256}, ${1257} ))  "
            + "                                       AND EXISTS (SELECT oo.obs_id    "
            + "                                                   FROM   obs oo   "
            + "                                                   WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                                          AND oo.concept_id = ${1719} "
            + "                                                          AND oo.value_coded IN( ${23955} )) )    "
            + "                                 AND ee.encounter_datetime BETWEEN "
            + "                                     result.encounter_datetime AND "
            + "                         Date_add(result.encounter_datetime,   "
            + "                         INTERVAL 150 DAY)) >= 2 ) "
            + "               GROUP  BY result.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5: ((At least 3 consultations registered on Ficha Clínica (encounter type 6) with INH =
   *       Iniício or Continua (concept ID 6122, value_coded in [1257, 1256]) ) AND ( at least 1
   *       consultation registered on Ficha Clínica (encounter type 6) with DT-INH (concept ID 1719,
   *       value_coded =23955) ) )until a 7-month (210 DAYs) period from the date from Y2,3. OR
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB5Part3(
      int adultoSeguimentoEncounterType,
      int isoniazidUsageConcept,
      int startDrugsConcept,
      int dataInicioProfilaxiaIsoniazidaConcept,
      int continuaConcept,
      int treatmentPrescribedConcept,
      int dtINHConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B5.3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);
    map.put("6122", isoniazidUsageConcept);
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23955", dtINHConcept);

    String query =
        " SELECT result.patient_id    "
            + "            FROM (SELECT p.patient_id,tabela.encounter_datetime    "
            + "               FROM   patient p    "
            + "               INNER JOIN encounter e  "
            + "                       ON e.patient_id = p.patient_id  "
            + "               INNER JOIN obs o    "
            + "                       ON o.encounter_id = e.encounter_id  "
            + "               INNER JOIN(SELECT p.patient_id, "
            + "                                 e.encounter_datetime  "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e    "
            + "                                         ON e.patient_id = p.patient_id    "
            + "                                 INNER JOIN obs o  "
            + "                                         ON o.encounter_id = e.encounter_id    "
            + "                          WHERE  p.voided = 0  "
            + "                                 AND e.voided = 0  "
            + "                                 AND o.voided = 0  "
            + "                                 AND e.location_id = :location    "
            + "                                 AND e.encounter_type = ${6}  "
            + "                                 AND ( ( o.concept_id = ${6122}   "
            + "                                         AND o.value_coded = ${1256} )    "
            + "                                        OR ( o.concept_id = ${6128}   "
            + "                                             AND o.value_datetime IS NOT NULL ) )  "
            + "                                 AND e.encounter_datetime <= :endDate GROUP by p.patient_id) AS tabela "
            + "                       ON tabela.patient_id = p.patient_id "
            + "                       WHERE e.location_id= :location AND e.encounter_type= ${6} AND e.voided=0 AND o.voided=0    "
            + "                       GROUP BY p.patient_id) result   "
            + "            WHERE  "
            + "                 (  (SELECT count(ee.patient_id)   "
            + "                            FROM   encounter ee    "
            + "                                INNER JOIN obs oo  "
            + "                                        ON oo.encounter_id = ee.encounter_id   "
            + "                            WHERE  ee.voided = 0   "
            + "                                AND result.patient_id = ee.patient_id  "
            + "                                AND oo.voided = 0  "
            + "                                AND ee.location_id = :location    "
            + "                                AND oo.concept_id = ${6122}   "
            + "                                AND ee.encounter_type = ${6}  "
            + "                                AND oo.value_coded IN ( ${1256}, ${1257} )   "
            + "                                AND ee.encounter_datetime BETWEEN  "
            + "                                    result.encounter_datetime AND  "
            + "                        Date_add(result.encounter_datetime,    "
            + "                        INTERVAL 210 DAY)) >= 3    "
            + "                        AND (SELECT Count(ee.patient_id)   "
            + "                            FROM   encounter ee    "
            + "                                    INNER JOIN obs oo  "
            + "                                            ON oo.encounter_id = ee.encounter_id   "
            + "                            WHERE  ee.voided = 0   "
            + "                                    AND oo.voided = 0  "
            + "                                    AND ee.location_id = :location    "
            + "                                    AND ee.encounter_type = ${6}  "
            + "                                    AND oo.concept_id = ${1719}   "
            + "                                    AND oo.value_coded = ${23955} "
            + "                                    AND result.patient_id = ee.patient_id  "
            + "                                    AND ee.encounter_datetime BETWEEN  "
            + "                                        result.encounter_datetime AND  "
            + "                            Date_add(result.encounter_datetime,    "
            + "                            INTERVAL 210 DAY)) >= 1 )  "
            + "            GROUP  BY result.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B6: If date from Y is registered on FILT (encounter type 60) check if the patient has:
   *   <li>At least 6 drug pick-ups with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’
   *       or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Tipo de dispensa” (concept
   *       id 23986) with value coded “Mensal” (concept id 1098) during 210 DAYs from the date from
   *       or
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB6Part1(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int monthlyConcept,
      int typeDispensationTPTConceptUuid) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT B6.1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("1098", monthlyConcept);

    String query =
        " SELECT p.patient_id"
            + "   FROM   patient p"
            + "       inner join encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       inner join obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + "       inner join (SELECT p.patient_id,"
            + "                          e.encounter_datetime"
            + "                   FROM   patient p"
            + "                          inner join encounter e"
            + "                                  ON e.patient_id = p.patient_id"
            + "                          inner join obs o"
            + "                                  ON e.encounter_id = o.encounter_id"
            + "                   WHERE  e.voided = 0"
            + "                          AND o.voided = 0"
            + "                          AND p.voided = 0"
            + "                          AND e.encounter_type = ${60}"
            + "                          AND o.concept_id = ${23985}"
            + "                          AND o.value_coded IN ( ${656}, ${23982} )"
            + "                          AND e.location_id = :location"
            + "                          AND e.encounter_datetime <= :endDate) AS tabela"
            + "               ON tabela.patient_id = p.patient_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${60}"
            + "       AND (( (SELECT Count(*)"
            + "               FROM   patient pp"
            + "                      inner join encounter ee"
            + "                              ON pp.patient_id = ee.patient_id"
            + "               WHERE  pp.voided = 0"
            + "                      AND ee.voided = 0"
            + "                      AND p.patient_id = pp.patient_id"
            + "                      AND ee.location_id = :location"
            + "                      AND ( EXISTS(SELECT oo.obs_id"
            + "                                   FROM   obs oo"
            + "                                   WHERE  oo.encounter_id = ee.encounter_id"
            + "                                          AND oo.concept_id = ${23985}"
            + "                                          AND oo.value_coded IN ( ${656}, ${23982} ))"
            + "                            AND EXISTS(SELECT oo.obs_id"
            + "                                       FROM   obs oo"
            + "                                       WHERE  oo.encounter_id = ee.encounter_id"
            + "                                              AND oo.concept_id = ${23986}"
            + "                                              AND oo.value_coded IN ( ${1098} )) )"
            + "                      AND ee.encounter_datetime BETWEEN"
            + "                          tabela.encounter_datetime AND"
            + "              Date_add(tabela.encounter_datetime,"
            + "              INTERVAL 210 DAY)) >= 6 ))"
            + "   GROUP  BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B6: At least 2 drug pick-ups with “Regime de TPT” (concept id 23985) value coded
   *       ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Tipo de
   *       dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720) during 210
   *       DAYs from the date from Y4 or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB6Part2(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int quarterlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT B6.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("23720", quarterlyConcept);

    String query =
        " SELECT p.patient_id   "
            + "FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join (SELECT p.patient_id,    "
            + "                          e.encounter_datetime "
            + "                   FROM   patient p    "
            + "                          inner join encounter e   "
            + "                                  ON e.patient_id = p.patient_id   "
            + "                          inner join obs o "
            + "                                  ON e.encounter_id = o.encounter_id   "
            + "                   WHERE  e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND p.voided = 0 "
            + "                          AND e.encounter_type = ${60}    "
            + "                          AND o.concept_id = ${23985} "
            + "                          AND o.value_coded IN ( ${656}, ${23982} )  "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_datetime <= :endDate) AS tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND (( (SELECT Count(*) "
            + "               FROM   patient pp   "
            + "                      inner join encounter ee  "
            + "                              ON pp.patient_id = ee.patient_id "
            + "               WHERE  pp.voided = 0    "
            + "                      AND ee.voided = 0    "
            + "                      AND p.patient_id = pp.patient_id "
            + "                      AND ee.location_id = :location "
            + "                      AND ( EXISTS(SELECT oo.obs_id    "
            + "                                   FROM   obs oo   "
            + "                                   WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                          AND oo.concept_id = ${23985}    "
            + "                                          AND oo.value_coded IN ( ${656}, ${23982} ))    "
            + "                            AND EXISTS(SELECT oo.obs_id    "
            + "                                       FROM   obs oo   "
            + "                                       WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                              AND oo.concept_id = ${23986}    "
            + "                                              AND oo.value_coded IN ( ${23720} )) )   "
            + "                      AND ee.encounter_datetime BETWEEN    "
            + "                          tabela.encounter_datetime AND    "
            + "              Date_add(tabela.encounter_datetime,  "
            + "              INTERVAL 210 DAY)) >= 2 ))   "
            + " GROUP  BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B6: ((At least 3 drug pick-ups registered on FILT (encounter type 60) with INH Mensal
   *       (concept ID 23986, value_coded 1098 and concept id 23985 value_coded in (656, 23982) )
   *       AND (at least 1 drug pick-up on FILT (encounter type 60) with DT-INH (concept ID 23986,
   *       value_coded 23720 and concept id 23985 value_coded in (656, 23982))) until a 7-month
   *       period (210 DAYs) from the date from Y4
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB6Part3(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int monthlyConcept,
      int typeDispensationTPTConceptUuid,
      int quarterlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with INH Mensal and DT-INH B6.3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("1098", monthlyConcept);
    map.put("23720", quarterlyConcept);

    String query =
        " SELECT p.patient_id"
            + "   FROM   patient p"
            + "       inner join encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       inner join obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + "       inner join (SELECT p.patient_id,"
            + "                          e.encounter_datetime"
            + "                   FROM   patient p"
            + "                          inner join encounter e"
            + "                                  ON e.patient_id = p.patient_id"
            + "                          inner join obs o"
            + "                                  ON e.encounter_id = o.encounter_id"
            + "                   WHERE  e.voided = 0"
            + "                          AND o.voided = 0"
            + "                          AND p.voided = 0"
            + "                          AND e.encounter_type = ${60}"
            + "                          AND o.concept_id = ${23985}"
            + "                          AND o.value_coded IN ( ${656}, ${23982} )"
            + "                          AND e.location_id = :location"
            + "                          AND e.encounter_datetime <= :endDate) AS tabela"
            + "               ON tabela.patient_id = p.patient_id"
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${60}"
            + "       AND ( ( (SELECT Count(*)"
            + "                FROM   patient pp"
            + "                       inner join encounter ee"
            + "                               ON pp.patient_id = ee.patient_id"
            + "                WHERE  pp.voided = 0"
            + "                       AND ee.voided = 0"
            + "                       AND p.patient_id = pp.patient_id"
            + "                       AND ee.location_id = :location"
            + "                       AND ( EXISTS(SELECT oo.obs_id"
            + "                                    FROM   obs oo"
            + "                                    WHERE  oo.encounter_id = ee.encounter_id"
            + "                                           AND oo.concept_id = ${23985}"
            + "                                           AND oo.value_coded IN ( ${656}, ${23982} ))"
            + "                             AND EXISTS(SELECT oo.obs_id"
            + "                                        FROM   obs oo"
            + "                                        WHERE  oo.encounter_id = ee.encounter_id"
            + "                                               AND oo.concept_id = ${23986}"
            + "                                               AND oo.value_coded IN ( ${1098} )) )"
            + "                       AND ee.encounter_datetime BETWEEN"
            + "                           tabela.encounter_datetime AND"
            + "               Date_add(tabela.encounter_datetime,"
            + "               INTERVAL 210 DAY)) >= 3 )"
            + "             AND ( (SELECT Count(*)"
            + "                    FROM   patient pp"
            + "                           inner join encounter ee"
            + "                                   ON pp.patient_id = ee.patient_id"
            + "                    WHERE  pp.voided = 0"
            + "                           AND ee.voided = 0"
            + "                           AND p.patient_id = pp.patient_id"
            + "                           AND ee.location_id = :location"
            + "                           AND ( EXISTS(SELECT oo.obs_id"
            + "                                        FROM   obs oo"
            + "                                        WHERE  oo.encounter_id = ee.encounter_id"
            + "                                               AND oo.concept_id = ${23985}"
            + "                                               AND oo.value_coded IN ( ${656},"
            + "                                                   ${23982} ))"
            + "                                 AND EXISTS(SELECT oo.obs_id"
            + "                                            FROM   obs oo"
            + "                                            WHERE  oo.encounter_id ="
            + "                                                   ee.encounter_id"
            + "                                                   AND oo.concept_id = ${23986}"
            + "                                                   AND oo.value_coded IN ( ${23720}"
            + "                                                       )) )"
            + "                           AND ee.encounter_datetime BETWEEN"
            + "                               tabela.encounter_datetime AND"
            + "                   Date_add(tabela.encounter_datetime,"
            + "                   INTERVAL 210 DAY)) >= 1 ) )"
            + "   GROUP  BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>C: Select all patients from M and check if: The date from M is registered on Ficha
   *       Clinica - Master Card (encounter type 6) and:
   *   <li>The patient has at least 3 consultations (encounter type 6) with “Outras prescricoes”
   *       (concept id 1719) with value coded equal to “3HP” (concept id 23954) during 120 DAYs from
   *       the date from M.1; or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPC1(
      int adultoSeguimentoEncounterType, int threeHPConcept, int treatmentPrescribedConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes C1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23954", threeHPConcept);
    map.put("1719", treatmentPrescribedConcept);

    String query =
        " SELECT p.patient_id"
            + " FROM   patient p"
            + "        inner join encounter e"
            + "                ON e.patient_id = p.patient_id"
            + "        inner join obs o"
            + "                ON o.encounter_id = e.encounter_id"
            + "        inner join (SELECT p.patient_id,"
            + "                           e.encounter_datetime"
            + "                    FROM   patient p"
            + "                           inner join encounter e"
            + "                                   ON e.patient_id = p.patient_id"
            + "                           inner join obs o"
            + "                                   ON o.encounter_id = e.encounter_id"
            + "                    WHERE  p.voided = 0"
            + "                           AND e.voided = 0"
            + "                           AND o.voided = 0"
            + "                           AND e.location_id = :location"
            + "                           AND e.encounter_type = ${6}"
            + "                           AND o.concept_id = ${1719}"
            + "                           AND o.value_coded IN ( ${23954})"
            + "                           AND e.encounter_datetime <= :endDate) AS tabela"
            + "                ON tabela.patient_id = p.patient_id"
            + " WHERE  p.voided = 0"
            + "        AND e.voided = 0"
            + "        AND o.voided = 0"
            + "        AND e.location_id = :location"
            + "        AND e.encounter_type = ${6}"
            + "        AND ( (SELECT Count(*)"
            + "               FROM   patient pp"
            + "                      join encounter ee"
            + "                        ON pp.patient_id = ee.patient_id"
            + "                      join obs oo"
            + "                        ON oo.encounter_id = ee.encounter_id"
            + "              WHERE  pp.voided = 0"
            + "                     AND ee.voided = 0"
            + "                     AND oo.voided = 0"
            + "                     AND p.patient_id = pp.patient_id"
            + "                     AND ee.encounter_type = ${6}"
            + "                     AND ee.location_id = :location"
            + "                     AND ee.voided = 0"
            + "                     AND oo.concept_id = ${1719}"
            + "                     AND oo.value_coded = ${23954}"
            + "                     AND ee.encounter_datetime BETWEEN"
            + "                         tabela.encounter_datetime AND"
            + "             Date_add(tabela.encounter_datetime,"
            + "             INTERVAL 120 DAY)) >= 3 )"
            + " GROUP  BY p.patient_id;  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>The patient has at least 1 drug pick-up on FILT (encounter type 60) with “Regime de TPT”
   *       (concept id 23985) value coded “3HP” doxina” (concept id in [23954, 23984]) and “Tipo de
   *       dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720) during 120
   *       DAYs from the date from M.2; or
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPC2(
      int threeHPConcept,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int hPPiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int quarterlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT C2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("23954", threeHPConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("23720", quarterlyConcept);

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join (SELECT p.patient_id,    "
            + "                          e.encounter_datetime "
            + "                   FROM   patient p    "
            + "                          inner join encounter e   "
            + "                                  ON e.patient_id = p.patient_id   "
            + "                          inner join obs o "
            + "                                  ON o.encounter_id = e.encounter_id   "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_type = ${60}    "
            + "                          AND o.concept_id = ${23985} "
            + "                          AND o.value_coded IN ( ${23954}, ${23984} )    "
            + "                          AND e.encounter_datetime <= :endDate) AS tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND ( (SELECT Count(*)  "
            + "              FROM   patient pp    "
            + "                     join encounter ee "
            + "                       ON pp.patient_id = ee.patient_id    "
            + "              WHERE  pp.voided = 0 "
            + "                     AND ee.voided = 0 "
            + "                     AND p.patient_id = pp.patient_id  "
            + "                     AND ee.encounter_type = ${60}    "
            + "                     AND ee.location_id = :location  "
            + "                     AND ee.voided = 0 "
            + "                     AND ( EXISTS (SELECT o.person_id  "
            + "                                   FROM   obs o    "
            + "                                   WHERE  o.encounter_id = ee.encounter_id "
            + "                                          AND o.concept_id = ${23985} "
            + "                                          AND o.value_coded IN ( ${23954}, ${23984} ))   "
            + "                           AND EXISTS (SELECT o.person_id  "
            + "                                       FROM   obs o    "
            + "                                       WHERE  o.encounter_id = ee.encounter_id "
            + "                                              AND o.concept_id = ${23986} "
            + "                                              AND o.value_coded IN ( ${23720} )) )    "
            + "                     AND ee.encounter_datetime BETWEEN "
            + "                         tabela.encounter_datetime AND "
            + "             Date_add(tabela.encounter_datetime,   "
            + "             INTERVAL 120 DAY)) >= 1 ) "
            + " GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>C: The patient has at least 3 drug pick-up on FILT (encounter type 60) with “Regime de
   *       TPT” (concept id 23985) value coded “3HP” doxina” (concept id -in [23954, 23984]) and
   *       “Tipo de dispensa” (concept id 23986) with value coded “Trimestral” (concept id 1098)
   *       during 120 DAYs from the date from M.2.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPC3(
      int threeHPConcept,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int hPPiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int monthlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT C3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("23954", threeHPConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("1098", monthlyConcept);

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join (SELECT p.patient_id,    "
            + "                          e.encounter_datetime "
            + "                   FROM   patient p    "
            + "                          inner join encounter e   "
            + "                                  ON e.patient_id = p.patient_id   "
            + "                          inner join obs o "
            + "                                  ON o.encounter_id = e.encounter_id   "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_type = ${60}    "
            + "                          AND o.concept_id = ${23985} "
            + "                          AND o.value_coded IN ( ${23954}, ${23984} )    "
            + "                          AND e.encounter_datetime <= :endDate) AS tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND ( (SELECT Count(*)  "
            + "              FROM   patient pp    "
            + "                     join encounter ee "
            + "                       ON pp.patient_id = ee.patient_id    "
            + "              WHERE  pp.voided = 0 "
            + "                     AND ee.voided = 0 "
            + "                     AND p.patient_id = pp.patient_id  "
            + "                     AND ee.encounter_type = ${60}    "
            + "                     AND ee.location_id = :location  "
            + "                     AND ee.voided = 0 "
            + "                     AND ( EXISTS (SELECT o.person_id  "
            + "                                   FROM   obs o    "
            + "                                   WHERE  o.encounter_id = ee.encounter_id "
            + "                                          AND o.concept_id = ${23985} "
            + "                                          AND o.value_coded IN ( ${23954}, ${23984} ))   "
            + "                           AND EXISTS (SELECT o.person_id  "
            + "                                       FROM   obs o    "
            + "                                       WHERE  o.encounter_id = ee.encounter_id "
            + "                                              AND o.concept_id = ${23986} "
            + "                                              AND o.value_coded IN ( ${1098} )) ) "
            + "                     AND ee.encounter_datetime BETWEEN "
            + "                         tabela.encounter_datetime AND "
            + "             Date_add(tabela.encounter_datetime,   "
            + "             INTERVAL 120 DAY)) >= 3 ) "
            + " GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>D: Select all patients with the following conditions: Have Ficha clinica (encounter type
   *       6 or 9) with “DATA DE INICIO DE TRATAMENTO DE TUBERCULOSE” (concept id 1113,
   *       Value_datetime) between EndDate - 7months (210 days)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTBTreatmentPart1(
      int adultoSeguimentoEncounterType,
      int pediatriaSeguimentoEncounterType,
      int startDrugsConcept,
      int tBTreatmentPlanConcept,
      int tBDrugTreatmentStartDate) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with TRATAMENTO DE TUBERCULOSE D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("1268", tBTreatmentPlanConcept);
    map.put("1113", tBDrugTreatmentStartDate);

    String query =
        "   SELECT p.patient_id                                                                 "
            + "  FROM   patient p     "
            + "         inner join encounter e    "
            + "                 ON e.patient_id = p.patient_id    "
            + "         inner join obs o      "
            + "                 ON o.encounter_id = e.encounter_id    "
            + "  WHERE  p.voided = 0      "
            + "         AND o.voided = 0      "
            + "         AND e.voided = 0  "
            + "         AND e.location_id = :location   "
            + "         AND e.encounter_type IN (${6},${9})     "
            + "         AND o.concept_id = ${1113}   "
            + "         AND o.value_datetime BETWEEN Date_sub(:endDate, INTERVAL 210 DAY) "
            + "         AND :endDate  "
            + "   AND e.encounter_datetime <= :endDate"
            + "    GROUP by p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>D: On Ficha Resumo (encounter type 53) have “Outros diagnósticos" (concept id 1406) with
   *       “Tuberculose” (concept id 42) marked and obs datetime between EndDate - 7months (210
   *       DAYs) and endDate
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTBTreatmentPart2(
      int masterCardEncounterType, int pediatriaSeguimentoEncounterType, int tbConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with TRATAMENTO DE TUBERCULOSE D2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("1406", pediatriaSeguimentoEncounterType);
    map.put("42", tbConcept);

    String query =
        " SELECT p.patient_id   "
            + "   FROM   patient p   "
            + "          inner join encounter e  "
            + "                  ON e.patient_id = p.patient_id  "
            + "          inner join obs o    "
            + "                  ON o.encounter_id = e.encounter_id  "
            + "   WHERE  p.voided = 0    "
            + "          AND o.voided = 0    "
            + "          AND e.voided = 0    "
            + "          AND e.location_id = :location "
            + "          AND e.encounter_type = ${53}   "
            + "          AND o.concept_id = ${1406} "
            + "          AND o.value_coded = ${42}  "
            + "          AND o.obs_datetime BETWEEN Date_sub(:endDate, INTERVAL 210 DAY) AND "
            + "                                     :endDate "
            + "   GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>D: Enrolled on TB program (program id 5) patient state id = 6269 and start date(Date
   *       enrolled) >= EndDate - 7months (210 DAYs) and endDate(date Completed) <= reporting
   *       endDate
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTBTreatmentPart3(int tbProgramConcept, int activeOnProgramConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with TRATAMENTO DE TUBERCULOSE D3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("5", tbProgramConcept);
    map.put("6269", activeOnProgramConcept);

    String query =
        " SELECT p.patient_id  "
            + "   FROM   patient p   "
            + "          inner join patient_program pp   "
            + "                  ON pp.patient_id = p.patient_id "
            + "          inner join patient_state ps "
            + "                  ON ps.patient_program_id = pp.patient_program_id    "
            + "   WHERE  p.voided = 0    "
            + "          AND ps.voided = 0   "
            + "          AND pp.voided = 0   "
            + "          AND pp.program_id = ${5}   "
            + "          AND ps.patient_state_id = ${6269}  "
            + "          AND pp.location_id = :location    "
            + "          AND pp.date_enrolled >= Date_sub(:endDate, INTERVAL 210 DAY)    "
            + "          AND pp.date_completed <= :endDate "
            + "   GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>D: Select all patients with the following conditions: Have Ficha (encounter type 6) with
   *       “TRATAMENTO DE TUBERCULOSE”(concept_id 1268) with: value coded “Inicio” (concept_id IN
   *       1256) and obs_datetime between (for encounter type 6) EndDate - 7months (210 days) and
   *       endDate
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTBTreatmentPart4(
      int adultoSeguimentoEncounterType, int startDrugsConcept, int tBTreatmentPlanConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with TRATAMENTO DE TUBERCULOSE D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("1268", tBTreatmentPlanConcept);

    String query =
        " SELECT p.patient_id   "
            + "  FROM patient p "
            + "           INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "           INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "  WHERE p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.location_id = :location "
            + "    AND e.encounter_type = ${6} "
            + "    AND o.concept_id = ${1268} AND o.value_coded = ${1256} "
            + "    AND o.obs_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate "
            + "    AND e.encounter_datetime <= :endDate ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>E: Select all patients from Ficha Clinica (encounter type 6) with the following
   *       conditions: “Diagnótico TB activo” (concept_id 23761) value coded “SIM”(concept id 1065):
   *       Encounter_datetime between EndDate - 7months (210 DAYs) and endDate
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getE1(
      int adultoSeguimentoEncounterType, int activeTBConcept, int yesConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Diagnótico TB activo");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23761", activeTBConcept);
    map.put("1065", yesConcept);

    String query =
        " SELECT p.patient_id "
            + "   FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "   WHERE  p.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6}    "
            + "       AND o.concept_id = ${23761}    "
            + "       AND o.value_coded = ${1065}    "
            + "       AND e.encounter_datetime BETWEEN Date_sub(:endDate, INTERVAL 210 DAY) "
            + "       AND  :endDate  "
            + "   GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List<br>
   * <i> Patients with positive symptom screening for TB in last 2 weeks<br>
   * <i> Select all patients from Ficha clinica (encounter type 6) with the following
   * conditions:</i> <br>
   *
   * <ul>
   *   <li>“TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” (concept_id 1065) and
   *   <li>Encounter_datetime between EndDate - 2 weeks (14 days) and endDate
   *   <li>
   *   <li>Quais sintomas de TB” (concept id 1766) value coded “F – Febre, E - Emagrecimento, S-
   *       Sudorese noturna- Tosse a mais de 2 semanas, A-Astenia, C- Contacto recente com
   *       Tuberculose, criança - Adenopatia cervical indolor ” (concept id IN [1763, 1762, 176 4,
   *       1760, 23760, 1765, 161])
   *   <li>Encounter_datetime between EndDate - 2 weeks (14 days) and endDate
   *   <li>
   *   <li>“Pedido de investigacoes laboratoriais” (concept id 23722) value coded ‘TB LAM’,
   *       ‘GeneXpert’, ‘Cultura’, ‘BK’ or ‘Raio-X’ (concept id IN [23951, 23723, 307, 12, 23774])
   *   <li>Encounter_datetime between EndDate - 2 weeks (14 days) and endDate
   *   <li>
   *   <li>TB LAM” (concept id 23951) with any value code (concept id in [703, 664]) or
   *   <li>“GeneXpert” (concept id 23723) with any value code (concept id in [703, 664]) or
   *   <li>“Cultura” (concept id 23774) with any value code (concept id in [703, 664]) or
   *   <li>“BK” (concept id 307) with any value code (concept id in [703, 664]) or
   *   <li>“Raio X” (concept id 12) with any value code (concept id in [23956, 664, 1138])
   *   <li>Encounter_datetime between EndDate - 2 weeks and endDate
   *   <li>Select all patients from Lab Form (encounter type 13) with the following conditions:
   *   <li>“TB LAM” (concept id 23951) with any value code (concept id in [703, 664]) or
   *   <li>“GeneXpert” (concept id 23723) with any value code (concept id in [703, 664]) or
   *   <li>“Cultura” (concept id 23774) with any value code (concept id in [703, 664]) or
   *   <li>“BK” (concept id 307) with any value code (concept id in [703, 664]) or
   *   <li>“Raio X” (concept id 12) with any value code (concept id in [23956, 664, 1138])
   *   <li>Encounter_datetime between EndDate - 2 weeks and endDate
   *   <li>Select all patients from Ficha de seguimento adulto ou pediatrico (encounter type 6 or 9)
   *       with at least one::
   *   <li>“Rastreio de TB” (concept id 6257) value coded “SIM” (concept id 1065) or
   *   <li>“Resultado da Investigação para TB de BK e/ou RX?” (concept id 6277) value coded
   *       ‘positivo’ (concept id 703) or
   *   <li>“Resultado da Investigação para TB de BK e/ou RX?” (concept id 6277) value coded
   *       ‘negativo’ (concept id 664) and one “SIM” or “Nao” (concept id in [1065, 1066]) selected
   *       in “Rastreio de TB” (concept id 6257)
   *   <li>Encounter_datetime between EndDate - 2 weeks (14 days) and endDate
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPositiveSymtomScreening(
      int adultoSeguimentoEncounterType,
      int tbSymptomsConcept,
      int observationTB,
      int feverLastingMoraThan3Weeks,
      int nightsWeatsLastingMoraThan3Weeks,
      int weightLossOfMoreThan3KgInLastMonth,
      int coughLastingMoraThan3Weeks,
      int asthenia,
      int cohabitantBeingTreatedForTB,
      int lymphadenopathy,
      int testTBLAM,
      int tbGenexpertTestConcept,
      int resultForBasiloscopia,
      int xRayChest,
      int cultureTest,
      int negativeConcept,
      int positiveConcept,
      int sugestiveConcept,
      int indeterminateConcept,
      int misauLaboratorioEncounterType,
      int pediatriaSeguimentoEncounterType,
      int tbScreeningConcept,
      int yesConcept,
      int researchResultConcept,
      int noConcept,
      int applicationForLaboratoryResearch) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        " all patients with Positive Symptom Screening for TB in last 2 weeks");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23758", tbSymptomsConcept);
    map.put("1766", observationTB);
    map.put("1763", feverLastingMoraThan3Weeks);
    map.put("1762", nightsWeatsLastingMoraThan3Weeks);
    map.put("1764", weightLossOfMoreThan3KgInLastMonth);
    map.put("1760", coughLastingMoraThan3Weeks);
    map.put("23760", asthenia);
    map.put("1765", cohabitantBeingTreatedForTB);
    map.put("161", lymphadenopathy);
    map.put("23951", testTBLAM);
    map.put("23723", tbGenexpertTestConcept);
    map.put("307", resultForBasiloscopia);
    map.put("12", xRayChest);
    map.put("23774", cultureTest);
    map.put("664", negativeConcept);
    map.put("703", positiveConcept);
    map.put("23956", sugestiveConcept);
    map.put("1138", indeterminateConcept);
    map.put("13", misauLaboratorioEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6257", tbScreeningConcept);
    map.put("1065", yesConcept);
    map.put("6277", researchResultConcept);
    map.put("1066", noConcept);
    map.put("23722", applicationForLaboratoryResearch);

    String query =
        "   SELECT     "
            + "    p.patient_id   "
            + "   FROM   "
            + "    patient p  "
            + "        INNER JOIN "
            + "    encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN "
            + "    obs o ON o.encounter_id = e.encounter_id   "
            + "   WHERE  "
            + "    p.voided = 0 AND o.voided = 0  "
            + "        AND e.voided = 0   "
            + "        AND e.location_id = :location    "
            + "        AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 14 DAY) AND :endDate  "
            + "        AND ((e.encounter_type = ${6} "
            + "        AND ((o.concept_id = ${23758} "
            + "        AND o.value_coded = ${1065})  "
            + "        OR (o.concept_id = ${1766}    "
            + "        AND o.value_coded IN (${1763} , ${1762}, ${1764}, ${1760}, ${23760}, ${1765}, ${161}))  "
            + "        OR (o.concept_id = ${23722}   "
            + "        AND o.value_coded IN (${23951} , ${23723}, ${307}, ${12}, ${23774}))  "
            + "        OR (EXISTS( SELECT     "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23951}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23723}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23774}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${307} "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${12}  "
            + "                AND o.value_coded IN (${23956} , ${664}, ${1138})))))   "
            + "        OR (e.encounter_type = ${13}  "
            + "        AND ((o.concept_id = ${23758} "
            + "        AND o.value_coded = ${1065})  "
            + "        OR (o.concept_id = ${1766}    "
            + "        AND o.value_coded IN (${1763} , ${1762}, ${1764}, ${1760}, ${23760}, ${1765}, ${161}))  "
            + "        OR (o.concept_id = ${23722}   "
            + "        AND o.value_coded IN (${23951} , ${23723}, ${307}, ${12}, ${23774}))  "
            + "        OR (EXISTS( SELECT     "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23951}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23723}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${23774}   "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${307} "
            + "                AND o.value_coded IN (${703} , ${664}))  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${12}  "
            + "                AND o.value_coded IN (${23956} , ${664}, ${1138})))))   "
            + "        OR (e.encounter_type IN (${6} , ${9})    "
            + "        AND (EXISTS( SELECT    "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${6257}    "
            + "                AND o.value_coded = ${1065})  "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND o.concept_id = ${6277}    "
            + "                AND o.value_coded = ${703})   "
            + "        OR EXISTS( SELECT  "
            + "            o.person_id    "
            + "        FROM   "
            + "            obs o  "
            + "        WHERE  "
            + "            o.encounter_id = e.encounter_id    "
            + "                AND ((o.concept_id = ${6277}  "
            + "                AND o.value_coded = ${664})   "
            + "                OR (o.concept_id = ${6257}    "
            + "                AND o.value_coded IN (${1065} , ${1066})))))))   "
            + "   GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }
}
