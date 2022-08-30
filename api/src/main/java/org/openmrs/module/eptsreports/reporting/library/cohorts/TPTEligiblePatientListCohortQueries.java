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
import org.openmrs.module.eptsreports.reporting.library.queries.TPTEligiblePatientsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
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

  private final TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Autowired
  public TPTEligiblePatientListCohortQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      TxCurrCohortQueries txCurrCohortQueries,
      CommonMetadata commonMetadata,
      TPTInitiationCohortQueries tptInitiationCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.commonMetadata = commonMetadata;
    this.tptInitiationCohortQueries = tptInitiationCohortQueries;
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

    String mapping = "endDate=${endDate},location=${location}";

    String mappings = "startDate=${endDate-4m},endDate=${endDate},location=${location}";

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
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A2",
        EptsReportUtils.map(
            getINHStartA2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId()),
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
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentFollowUpTPTConcept().getConceptId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugsConcept().getConceptId(),
                tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId()),
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
        "threeHPA4",
        EptsReportUtils.map(
            tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hp(), mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA5",
        EptsReportUtils.map(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hp(), mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA6",
        EptsReportUtils.map(
            tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HP(), mapping));

    compositionCohortDefinition.addSearch(
        "threeHPA7",
        EptsReportUtils.map(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HP(), mapping));

    compositionCohortDefinition.addSearch(
        "IPTB1Part2", EptsReportUtils.map(getIPTB1part2(), mappings));

    compositionCohortDefinition.addSearch(
        "IPTB2",
        EptsReportUtils.map(
            getIPTB2(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getCompletedConcept().getConceptId(),
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
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getCompletedConcept().getConceptId(),
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
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getCompletedConcept().getConceptId(),
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
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTB5Part2",
        EptsReportUtils.map(
            getIPTB5Part2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.getDtINHConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "IPTBPart3",
        EptsReportUtils.map(
            getIPTB5Part3(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
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
        "threeHPC1part2",
        EptsReportUtils.map(
            get3HPC1part2(
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
        "threeHPC4",
        EptsReportUtils.map(get3HPLastProfilaxyDuringM3orM1PeriodsComposition(), mapping));

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
        "txcurr AND NOT (A1 OR A2 OR A4 OR A5 OR threeHPA1 OR threeHPA2 OR threeHPA3 OR threeHPA4 OR threeHPA5 OR threeHPA6 OR threeHPA7 OR IPTB1Part2 OR IPTB2 OR IPTB3 OR IPTB4 OR IPTB5Part1 OR IPTB5Part2 OR IPTBPart3 OR IPTB6Part1 OR IPTB6Part2 OR IPTB6Part3 OR threeHPC1 OR threeHPC2 OR threeHPC3 OR threeHPC4 OR threeHPC1part2 OR TBTreatmentPart1 OR TBTreatmentPart2 OR TBTreatmentPart3 OR TBTreatmentPart4 OR E1 OR F)");
    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A1.2: Select all patients with Ficha Resumo (encounter type 53) with Última
   *       profilaxia(concept id 23985) value coded INH(concept id 656) and Estado da Profilaxia
   *       (concept id 165308) value coded Início (concept id 1256) and obs_datetime for Estado da
   *       profilaxia (concept_id = 165308) between endDate-7months and endDate.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA1(
      int masterCardEncounterType,
      int regimenTpt,
      int inhRpt,
      int isoniazidConcept,
      int proflaxisStatus,
      int startDrugsConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Inicio)");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("23985", regimenTpt);
    map.put("23954", inhRpt);
    map.put("656", isoniazidConcept);
    map.put("165308", proflaxisStatus);
    map.put("1256", startDrugsConcept);

    String query =
        "SELECT p.patient_id FROM "
            + "                     patient p "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                 WHERE "
            + "                         p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND o2.voided = 0 "
            + "                   AND e.encounter_type = ${53} "
            + "                   AND e.location_id = :location "
            + "                   AND (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "                   AND ( (o2.concept_id = ${165308} AND o2.value_coded = ${1256}) "
            + "                     AND (o2.obs_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 7 MONTH) AND :endDate) ) ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>A2: Select all patients with Ficha clinica or Ficha de Seguimento (encounter type in 6,
   *       9) Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início (concept id 1256) with Data do Estado
   *       da Profilaxia (obs_datetime) between endDate-7months and endDate.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA2(
      int adultoSeguimentoEncounterType,
      int pediatriaSeguimentoEncounterType,
      int startDrugsConcept,
      int regimenTpt,
      int isoniazid,
      int proflaxisStatus) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Profilaxia INH");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("23985", regimenTpt);
    map.put("656", isoniazid);
    map.put("165308", proflaxisStatus);

    String query =
        " SELECT p.patient_id FROM patient p "
            + "                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                       INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                       WHERE e.encounter_type IN (${6}, ${9}) "
            + "                       AND o.voided = 0 AND e.voided = 0 AND p.voided = 0 AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND (o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "                       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                            AND o2.obs_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 7 MONTH) AND :endDate )";

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
   *       endDate-7months and endDate
   *   <li>and no other INH values (“Isoniazida” or “Isoniazida + Piridoxina”) marked on FILT in the
   *       7 months prior to the INH Start Date and
   *   <li>no Última profilaxia(concept id 23985) value coded INH(concept id 656) and Data Início
   *       registered in Ficha Resumo - Mastercard (Encounter Type ID 53) in the 7 months prior to
   *       ‘INH Start Date’
   *   <li>and no Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início (concept id 1256) marked on Ficha
   *       Clínica - Mastercard or Ficha de Seguimento (Encounter Type ID IN 6, 9) in the 7 months
   *       prior to ‘INH Start Date’
   *   <li>and No other INH Start Dates marked in Ficha Clinica (Profilaxia (INH) with the value “I”
   *       (Início) or (Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia
   *       with the value “Início (I)”) in the 7 months prior to this FILT Start Date
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
      int masterCardEncounterType,
      int startDrugsConcept,
      int proflaxisStatus) {
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
    map.put("53", masterCardEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("165308", proflaxisStatus);

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Min(e.encounter_datetime) first_pickup_date "
            + "        FROM   patient p "
            + "               inner join encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               inner join obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "               inner join obs o2 "
            + "                       ON o2.encounter_id = e.encounter_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${60} "
            + "               AND ( o.concept_id = ${23985} "
            + "                     AND o.value_coded IN ( ${656}, ${23982} ) ) "
            + "               AND ( ( o2.concept_id = ${23987} "
            + "                       AND ( o2.value_coded = ${1257} "
            + "                              OR o2.value_coded IS NULL ) ) "
            + "                      OR o2.concept_id NOT IN (SELECT oo.concept_id "
            + "                                               FROM   obs oo "
            + "                                               WHERE  oo.voided = 0 "
            + "                                                      AND oo.encounter_id = "
            + "                                                          e.encounter_id "
            + "                                                      AND oo.concept_id = ${23987}) "
            + "                   ) "
            + "               AND o2.obs_datetime BETWEEN Date_sub(:endDate, "
            + "                                                interval 7 month) "
            + "                                                AND :endDate "
            + "        GROUP  BY p.patient_id) first_filt "
            + "WHERE  patient_id NOT IN (SELECT pp.patient_id "
            + "                          FROM   patient pp "
            + "                                 inner join encounter ee "
            + "                                         ON ee.patient_id = pp.patient_id "
            + "                                 inner join obs oo "
            + "                                         ON oo.encounter_id = ee.encounter_id "
            + "                          WHERE  pp.voided = 0 "
            + "                                 AND first_filt.patient_id = pp.patient_id "
            + "                                 AND ee.voided = 0 "
            + "                                 AND oo.voided = 0 "
            + "                                 AND ee.location_id = :location "
            + "                                 AND ee.encounter_type = ${60} "
            + "                                 AND oo.concept_id = ${23985} "
            + "                                 AND oo.value_coded IN ( ${656}, ${23982} ) "
            + "                                 AND ee.encounter_datetime >= Date_sub( "
            + "                                     first_filt.first_pickup_date, "
            + "                                                              interval 7 month "
            + "                                                              ) "
            + "                                 AND ee.encounter_datetime < "
            + "                                     first_filt.first_pickup_date "
            + "                          UNION "
            + "                          SELECT pp.patient_id "
            + "                          FROM   patient pp "
            + "                                 inner join encounter ee "
            + "                                         ON ee.patient_id = pp.patient_id "
            + "                                 inner join obs oo "
            + "                                         ON oo.encounter_id = ee.encounter_id "
            + "                                 inner join obs oo2 "
            + "                                         ON oo2.encounter_id = ee.encounter_id "
            + "                          WHERE  ee.encounter_type = ${53} "
            + "                                 AND ( oo.concept_id = ${23985} AND oo.value_coded = ${656} ) "
            + "                                 AND oo.voided = 0 "
            + "                                 AND ee.voided = 0 "
            + "                                 AND pp.voided = 0 "
            + "                                 AND ee.location_id = :location "
            + "                                 AND first_filt.patient_id = pp.patient_id "
            + "                                 AND ( oo2.concept_id = ${165308} AND oo2.value_coded = ${1256} "
            + "                                 AND oo2.obs_datetime >= Date_sub( "
            + "                                     first_filt.first_pickup_date, interval "
            + "                                                          7 month "
            + "                                                          ) "
            + "                                 AND oo2.obs_datetime < "
            + "                                     first_filt.first_pickup_date ) "
            + "                          UNION "
            + "                           SELECT pp.patient_id FROM"
            + "                     patient pp"
            + "                         INNER JOIN encounter ee ON ee.patient_id = pp.patient_id"
            + "                         INNER JOIN obs o ON ee.encounter_id = o.encounter_id"
            + "                         INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id"
            + "                 WHERE"
            + "                         pp.voided = 0"
            + "                   AND ee.voided = 0"
            + "                   AND o.voided = 0"
            + "                   AND o2.voided = 0"
            + "                   AND ee.encounter_type IN (${6}, ${9})"
            + "                   AND ee.location_id = :location"
            + "                   AND first_filt.patient_id = pp.patient_id "
            + "                   AND (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "                   AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                     AND o2.obs_datetime >= Date_sub( "
            + "                                     first_filt.first_pickup_date, "
            + "                                                              interval 7 month "
            + "                                                              ) "
            + "                                 AND o2.obs_datetime < "
            + "                                     first_filt.first_pickup_date ) "
            + "                          UNION "
            + "                          SELECT pp.patient_id "
            + "                          FROM   patient pp "
            + "                                 inner join encounter ee "
            + "                                         ON ee.patient_id = pp.patient_id "
            + "                                 inner join obs oo "
            + "                                         ON oo.encounter_id = ee.encounter_id "
            + "                                 inner join obs oo2 "
            + "                                         ON oo2.encounter_id = ee.encounter_id "
            + "                          WHERE  ee.encounter_type = ${6} "
            + "                                 AND ( oo.concept_id = ${23985} "
            + "                                       AND oo.value_coded = ${656} )  "
            + "                                 AND ( oo2.concept_id = ${165308} "
            + "                                       AND oo2.value_coded = ${1256} ) "
            + "                                 AND oo.voided = 0 "
            + "                                 AND oo2.voided = 0 "
            + "                                 AND ee.voided = 0 "
            + "                                 AND pp.voided = 0 "
            + "                                 AND ee.location_id = :location "
            + "                                 AND first_filt.patient_id = pp.patient_id "
            + "                                 AND oo2.obs_datetime >= Date_sub( "
            + "                                     first_filt.first_pickup_date, "
            + "                                                              interval 7 month "
            + "                                                              ) "
            + "                                 AND oo2.obs_datetime < "
            + "                                     first_filt.first_pickup_date "
            + "GROUP  BY patient_id) ";

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
   *   <li>B1.2: Select all patients with Última profilaxia(concept id 23985) value coded
   *       INH(concept id 656) and Data Fim (value datetime) selected by reporting end date in Ficha
   *       Resumo – Mastercard (encounter type 53) and between 173 days and 365 days from the INH
   *       start date
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB1part2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Fim) B1.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "After Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + " LIMIT 1) AS DATE), "
            + "                                           INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "                                              LIMIT 1) AS DATE), "
            + "                                        INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                                                                              LIMIT 1) AS DATE), "
            + "                                                                        INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                                                                              LIMIT 1) AS DATE), "
            + "                                                                        INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                                                                              LIMIT 1) AS DATE), "
            + "                                                                        INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "  AND e.encounter_type = ${53} "
            + "  AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "  AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "  AND o2.obs_datetime BETWEEN DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                                               LIMIT 1) "
            + "                                             AS DATE), "
            + "                                         INTERVAL 173 DAY) AND DATE_ADD(CAST(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                                                                              LIMIT 1) "
            + "                                                                            AS DATE), "
            + "                                                                        INTERVAL 365 DAY) ) ) "
            + "GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B2: Select all patients with Profilaxia TPT (concept id 23985) value coded INH (concept
   *       id 656) and Data de Fim by reporting end date marked in Ficha de seguimento (encounter
   *       type IN 6, 9) and between 173 days and 365 days from the INH start date
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB2(
      int masterCardEncounterType,
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
      int completedConcept,
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
    map.put("1256", startDrugsConcept);
    map.put("1267", completedConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        " SELECT p.patient_id"
            + "     FROM   patient p"
            + "       INNER JOIN encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} )"
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "           LIMIT 1) AS"
            + "                date), INTERVAL 173 DAY) AND"
            + "           Date_add("
            + "               Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "                   LIMIT 1) AS"
            + "               date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} ) "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "           LIMIT 1)"
            + "                AS date), INTERVAL 173 DAY) AND Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "                      LIMIT 1)"
            + "           AS date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} )"
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} )"
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) )"
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} )"
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type IN ( ${6}, ${9} )"
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B3: Select all patients with Profilaxia TPT (conceptid 23985) value coded INH (concept id
   *       656) and Estado da Profilaxia (concept id 165308) value coded FIm (concept id 1267)
   *       marked in Ficha Clinica - Mastercard (encounter type 6) by reporting end date and between
   *       (date from Y + 173 DAYs) and (date from Y + 365 DAYs)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB3(
      int masterCardEncounterType,
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
      int completedConcept,
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
    map.put("1256", startDrugsConcept);
    map.put("1267", completedConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        " SELECT p.patient_id"
            + "     FROM   patient p"
            + "       INNER JOIN encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "           LIMIT 1) AS"
            + "                date), INTERVAL 173 DAY) AND"
            + "           Date_add("
            + "               Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "                   LIMIT 1) AS"
            + "               date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "           LIMIT 1)"
            + "                AS date), INTERVAL 173 DAY) AND Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "                      LIMIT 1)"
            + "           AS date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) )"
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${6} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B4: Select all patients with Profilaxia TPT (conceptid 23985) value coded INH (concept id
   *       656) and Data de Fim * by reporting end date marked in Ficha de Seguimento - Pediatrico
   *       (encounter type 9) * and between 173 days and 365 days from the INH start date
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB4(
      int masterCardEncounterType,
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
      int completedConcept,
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
    map.put("1256", startDrugsConcept);
    map.put("1267", completedConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        " SELECT p.patient_id"
            + "     FROM   patient p"
            + "       INNER JOIN encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "           LIMIT 1) AS"
            + "                date), INTERVAL 173 DAY) AND"
            + "           Date_add("
            + "               Cast(( "
            + TPTEligiblePatientsQueries.getY1Query()
            + "                   LIMIT 1) AS"
            + "               date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "           LIMIT 1)"
            + "                AS date), INTERVAL 173 DAY) AND Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY2Query()
            + "                      LIMIT 1)"
            + "           AS date), INTERVAL 365 DAY) ) ) "
            + "     GROUP  BY p.patient_id"
            + "     UNION"
            + "     SELECT p.patient_id"
            + "     FROM   patient p"
            + "            INNER JOIN encounter e"
            + "                    ON e.patient_id = p.patient_id"
            + "            INNER JOIN obs o"
            + "                    ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "     WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY3Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "   GROUP  BY p.patient_id"
            + "   UNION"
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY4Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) )"
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY5Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id"
            + " UNION "
            + "   SELECT p.patient_id"
            + "   FROM   patient p"
            + "          INNER JOIN encounter e"
            + "                  ON e.patient_id = p.patient_id"
            + "          INNER JOIN obs o"
            + "                  ON o.encounter_id = e.encounter_id"
            + " INNER JOIN obs o2 "
            + "       ON o2.encounter_id = e.encounter_id "
            + "   WHERE  p.voided = 0"
            + "       AND e.voided = 0"
            + "       AND o.voided = 0"
            + "       AND e.location_id = :location"
            + "       AND e.encounter_type = ${9} "
            + " AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND o2.obs_datetime BETWEEN Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                LIMIT 1) AS date), INTERVAL 173 DAY)"
            + "                                        AND"
            + "                                            Date_add("
            + "           Cast(( "
            + TPTEligiblePatientsQueries.getY6Query()
            + "                    LIMIT 1) AS date), INTERVAL 365 DAY) ) ) "
            + "         GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5- If date Y is registered on Ficha Clinica (encounter type 6) OR Ficha de Seguimento
   *       (encounter type 9) OR Ficha Resumo (encounter type 53) check if the patient has:
   *   <li>At least 5 consultations ((encounter type 6) (encounter type 9) Profilaxia TPT (concept
   *       id 23985) value coded INH (concept id 656) and Estado da Profilaxia (concept id 165308)
   *       value coded Início/continua (concept id in [1256,1257]) until a 7-month period after the
   *       INH Start Date from the date Y2,3 or
   *   <li>
   *   <li>
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getIPTB5Part1(
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
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
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1065", yesConcept);
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY1QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY2QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY3QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        "   SELECT result.patient_id "
            + "         FROM (SELECT p.patient_id,tabela.datetime   "
            + "            FROM   patient p   "
            + "            INNER JOIN encounter e "
            + "                    ON e.patient_id = p.patient_id "
            + "            INNER JOIN obs o   "
            + "                    ON o.encounter_id = e.encounter_id "
            + "            INNER JOIN(   "
            + unionQuery
            + " ) AS tabela    "
            + "                    ON tabela.patient_id = p.patient_id    "
            + "                    WHERE e.location_id= :location AND e.encounter_type IN  (${6}, ${9}, ${53}) AND e.voided= 0 AND o.voided= 0   "
            + "                    GROUP BY p.patient_id) result  "
            + "      WHERE   (( (SELECT Count(ee.patient_id)  "
            + "                     FROM   encounter ee   "
            + "                            INNER JOIN obs oo  "
            + "                                    ON oo.encounter_id = ee.encounter_id   "
            + "                            INNER JOIN obs o2  "
            + "                                    ON o2.encounter_id = ee.encounter_id   "
            + "                     WHERE  ee.voided = 0  "
            + "                            AND result.patient_id = ee.patient_id  "
            + "                            AND oo.voided = 0  "
            + "                            AND ee.location_id = :location   "
            + "AND (ee.encounter_type IN (${6},${9}) AND "
            + "  ( (oo.concept_id = ${23985} and oo.value_coded = ${656}) and "
            + "    (o2.concept_id = ${165308} and o2.value_coded in (${1256},${1257})"
            + "                            AND o2.obs_datetime BETWEEN  "
            + "                                result.datetime AND  "
            + "                    Date_add(result.datetime,    "
            + "                    INTERVAL 7 MONTH) ) ) "
            + " )) >= 5 )) "
            + "      GROUP  BY result.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5: At least 2 consultations registered in Ficha Clinca (encounter type 6) with
   *       (Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início/continua (concept id in [1256,1257])
   *       and Outras prescricoes(concept id 1719) value coded DT-INH (concept id 23955) until a
   *       5-month period from the INH Start Date (from Y2,3) or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB5Part2(
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
      int pediatriaSeguimentoEncounterType,
      int continuaConcept,
      int treatmentPrescribedConcept,
      int dtINHConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B5.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23955", dtINHConcept);
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY1QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY2QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY3QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        "   SELECT result.patient_id    "
            + "                FROM (SELECT p.patient_id,tabela.datetime    "
            + "                   FROM   patient p    "
            + "                   INNER JOIN encounter e  "
            + "                           ON e.patient_id = p.patient_id  "
            + "                   INNER JOIN obs o    "
            + "                           ON o.encounter_id = e.encounter_id  "
            + "                   INNER JOIN(  "
            + unionQuery
            + "  ) AS tabela "
            + "                           ON tabela.patient_id = p.patient_id "
            + "                           WHERE e.location_id= :location AND e.encounter_type IN (${6}, ${9}, ${53}) AND e.voided=0 AND o.voided=0    "
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
            + "                                 AND ( EXISTS( "
            + "                                       SELECT oo.obs_id "
            + "                                       FROM obs oo "
            + "                                       WHERE oo.encounter_id = ee.encounter_id "
            + "                                       AND oo.concept_id = ${23985} "
            + "                                       AND oo.value_coded = ${656} "
            + "                                       ) "
            + "                                       AND EXISTS( "
            + "                                       SELECT oo.obs_id "
            + "                                       FROM obs oo "
            + "                                       WHERE oo.encounter_id = ee.encounter_id "
            + "                                       AND oo.concept_id = ${165308} AND oo.value_coded IN (${1256},${1257}) "
            + "                                       ) "
            + "                                       AND EXISTS( "
            + "                                       SELECT oo.obs_id "
            + "                                       FROM obs oo "
            + "                                       WHERE oo.encounter_id = ee.encounter_id "
            + "                                       AND oo.concept_id = ${1719} AND oo.value_coded = ${23955} "
            + "                                       ) "
            + "                                   )"
            + "                                 AND ee.encounter_datetime BETWEEN "
            + "                                     result.datetime AND "
            + "                         Date_add(result.datetime,   "
            + "                         INTERVAL 5 MONTH)) >= 2 ) "
            + "               GROUP  BY result.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>B5: ((At least 3 consultations registered on Ficha Clínica (encounter type 6) with
   *       (Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início/continua (concept id in [1256,1257]) )
   *       AND ( at least 1 consultation registered on Ficha Clínica (encounter type 6) with
   *       (Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início/continua (concept id in [1256,1257])
   *       and Outras prescricoes(concept id 1719) value coded DT-INH (concept id 23955) ) ) until a
   *       7-month period from the INH Start Date (date from Y2,3.) OR
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB5Part3(
      int adultoSeguimentoEncounterType,
      int startDrugsConcept,
      int pediatriaSeguimentoEncounterType,
      int continuaConcept,
      int treatmentPrescribedConcept,
      int dtINHConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B5.3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("1256", startDrugsConcept);
    map.put("1257", continuaConcept);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23955", dtINHConcept);
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY1QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY2QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY3QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        " SELECT result.patient_id    "
            + "            FROM (SELECT p.patient_id,tabela.datetime    "
            + "               FROM   patient p    "
            + "               INNER JOIN encounter e  "
            + "                       ON e.patient_id = p.patient_id  "
            + "               INNER JOIN obs o    "
            + "                       ON o.encounter_id = e.encounter_id  "
            + "               INNER JOIN(  "
            + unionQuery
            + " ) AS tabela "
            + "                       ON tabela.patient_id = p.patient_id "
            + "                       WHERE e.location_id= :location AND e.encounter_type IN (${6}, ${9}, ${53}) AND e.voided=0 AND o.voided=0    "
            + "                       GROUP BY p.patient_id) result   "
            + "            WHERE  "
            + "                 ( ( "
            + "                   SELECT     Count(ee.patient_id) "
            + "                   FROM       encounter ee "
            + "                   INNER JOIN obs oo "
            + "                   ON         oo.encounter_id = ee.encounter_id "
            + "                   INNER JOIN obs o2 "
            + "                   ON         o2.encounter_id = ee.encounter_id "
            + "                   WHERE      ee.voided = 0 "
            + "                   AND        result.patient_id = ee.patient_id "
            + "                   AND        oo.voided = 0 "
            + "                   AND        o2.voided = 0 "
            + "                   AND        ee.location_id = :location "
            + "                   AND        ee.encounter_type = ${6} "
            + "                   AND         ( ( "
            + "                                                    oo.concept_id= ${23985} "
            + "                                         AND        oo.value_coded = ${656}) "
            + "                              AND        ( "
            + "                                                    o2.concept_id = ${165308} "
            + "                                         AND        o2.value_coded IN (${1256}, ${1257}) ) ) "
            + "                   AND   o2.obs_datetime BETWEEN result.datetime AND  "
            + "      date_add(result.datetime, interval 7 MONTH)) >= 3 "
            + "                AND "
            + "                ( "
            + "                           SELECT     count(ee.patient_id) "
            + "                           FROM       encounter ee "
            + "                           INNER JOIN obs oo "
            + "                           ON         oo.encounter_id = ee.encounter_id "
            + "                           INNER JOIN obs o2 "
            + "                           ON         o2.encounter_id = ee.encounter_id "
            + "                           INNER JOIN obs o3 "
            + "                           ON         o3.encounter_id = ee.encounter_id "
            + "                           WHERE      ee.voided = 0 "
            + "                           AND        oo.voided = 0 "
            + "                           AND        ee.location_id = :location "
            + "                           AND        ee.encounter_type = ${6} "
            + "                           AND         ( ( "
            + "                                                            oo.concept_id= ${23985} "
            + "                                                 AND        oo.value_coded = ${656}) "
            + "                                      AND        ( "
            + "                                                            o2.concept_id = ${165308} "
            + "                                                 AND        o2.value_coded IN (${1256}, ${1257}) ) "
            + "                                      AND        ( "
            + "                                                            o3.concept_id = ${1719} "
            + "                                                 AND        o3.value_coded = ${23955}) ) "
            + "                           AND        result.patient_id = ee.patient_id "
            + "                           AND        ee.encounter_datetime BETWEEN result.datetime AND "
            + "       date_add(result.datetime, interval 7 MONTH)) >= 1 ) "
            + "            GROUP  BY result.patient_id ";

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
   *       id 23986) with value coded “Mensal” (concept id 1098) until a 7-Month period from the
   *       date from the INH Start Date or
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
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY4QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY5QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY6QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        " SELECT p.patient_id"
            + "   FROM   patient p"
            + "       inner join encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       inner join obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + "       inner join (  "
            + unionQuery
            + " ) AS tabela"
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
            + "                          tabela.datetime AND"
            + "              Date_add(tabela.datetime,"
            + "              INTERVAL 7 MONTH)) >= 6 ))"
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
   *       dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720) until a 5
   *       Month Period from the INH Start Date (date from Y4) or
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
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY4QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY5QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY6QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        " SELECT p.patient_id   "
            + "FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join ( "
            + unionQuery
            + " ) AS tabela  "
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
            + "                          tabela.datetime AND    "
            + "              Date_add(tabela.datetime,  "
            + "              INTERVAL 7 MONTH)) >= 2 ))   "
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
   *       period from the INH Start Date (date from Y4)
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

    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY4QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY5QueryWithPatientIdForB5())
            .union(TPTEligiblePatientsQueries.getY6QueryWithPatientIdForB5())
            .buildQuery();

    String query =
        " SELECT p.patient_id"
            + "   FROM   patient p"
            + "       inner join encounter e"
            + "               ON e.patient_id = p.patient_id"
            + "       inner join obs o"
            + "               ON o.encounter_id = e.encounter_id"
            + "       inner join (  "
            + unionQuery
            + " ) AS tabela"
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
            + "                           tabela.datetime AND"
            + "               Date_add(tabela.datetime,"
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
            + "                               tabela.datetime AND"
            + "                   Date_add(tabela.datetime,"
            + "                   INTERVAL 7 MONTH)) >= 1 ) )"
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
   *       Clinica - Master Card (encounter type 6) or Ficha Resumo (encounter type 53) and:
   *   <li>The patient has at least 3 consultations in Ficha Clonica - Mastercard (encounter type 6)
   *       with Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   *       Profilaxia (concept id 165308) value coded Início/continua (concept id in [1256,1257])
   *       until 4-Month period from the 3HP Start Date (date from M.1;) or
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
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(sectionFromM.ONE.getQuery())
            .union(sectionFromM.THREE.getQuery())
            .union(sectionFromM.FOUR.getQuery())
            .union(sectionFromM.FIVE.getQuery())
            .union(sectionFromM.SIX.getQuery())
            .buildQuery();

    String query =
        "SELECT p.patient_id "
            + "             FROM   patient p "
            + "                    inner join encounter e "
            + "                            ON e.patient_id = p.patient_id "
            + "                    inner join obs o "
            + "                            ON o.encounter_id = e.encounter_id "
            + "                    inner join ( "
            + unionQuery
            + "                     "
            + "                 ) AS tabela "
            + "                            ON tabela.patient_id = p.patient_id "
            + "             WHERE  p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND ( (SELECT Count(*) "
            + "                           FROM   patient pp "
            + "                                  join encounter ee "
            + "                                    ON pp.patient_id = ee.patient_id "
            + "                                  join obs oo "
            + "                                    ON oo.encounter_id = ee.encounter_id "
            + "                               join obs o2 "
            + "                                    ON o2.encounter_id = ee.encounter_id "
            + "                          WHERE  pp.voided = 0 "
            + "                                 AND ee.voided = 0 "
            + "                                 AND oo.voided = 0 "
            + "                                 AND o2.voided = 0 "
            + "                                 AND p.patient_id = pp.patient_id "
            + "                                 AND ee.encounter_type = ${6} "
            + "                                 AND ee.location_id = :location "
            + "                                 AND ee.voided = 0 "
            + "                            AND ( "
            + "                                (oo.concept_id = ${23985} AND oo.value_coded = ${23954}) "
            + "                                    AND (o2.concept_id = ${165308} AND o2.value_coded IN (${1256},${1257}) "
            + "                                 AND o2.obs_datetime BETWEEN "
            + "                                     tabela.encounter_datetime AND "
            + "                         Date_add(tabela.encounter_datetime, "
            + "                         INTERVAL 7 MONTH)))) >= 3 ) "
            + "             GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>C: Select all patients from M and check if: The date from M is registered on Ficha
   *       Clinica - Master Card (encounter type 6) or Ficha Resumo (encounter type 53) and:
   *   <li>At least 1 consultation registered on Ficha Clínica (encounter type 6) with DT-3HP
   *       (concept ID 1719, value_coded =165307) until a 4-month period from the 3HP Strat Date
   *       (date from M.1;) or
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPC1part2(
      int adultoSeguimentoEncounterType, int threeHPConcept, int treatmentPrescribedConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes C1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", adultoSeguimentoEncounterType);
    map.put("23954", threeHPConcept);
    map.put("1719", treatmentPrescribedConcept);
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(sectionFromM.ONE.getQuery())
            .union(sectionFromM.THREE.getQuery())
            .union(sectionFromM.FOUR.getQuery())
            .union(sectionFromM.FIVE.getQuery())
            .union(sectionFromM.SIX.getQuery())
            .buildQuery();

    String query =
        "SELECT p.patient_id "
            + "             FROM   patient p "
            + "                    inner join encounter e "
            + "                            ON e.patient_id = p.patient_id "
            + "                    inner join obs o "
            + "                            ON o.encounter_id = e.encounter_id "
            + "                    inner join ( "
            + unionQuery
            + "                     "
            + "                 ) AS tabela "
            + "                            ON tabela.patient_id = p.patient_id "
            + "             WHERE  p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND ( (SELECT Count(*) "
            + "                           FROM   patient pp "
            + "                                  join encounter ee "
            + "                                    ON pp.patient_id = ee.patient_id "
            + "                                  join obs oo "
            + "                                    ON oo.encounter_id = ee.encounter_id "
            + "                          WHERE  pp.voided = 0 "
            + "                                 AND ee.voided = 0 "
            + "                                 AND oo.voided = 0 "
            + "                                 AND p.patient_id = pp.patient_id "
            + "                                 AND ee.encounter_type = ${6} "
            + "                                 AND ee.location_id = :location "
            + "                                 AND ee.voided = 0 "
            + "                                 AND oo.concept_id = ${1719} "
            + "                                 AND oo.value_coded = ${165307} "
            + "                                 AND ee.encounter_datetime BETWEEN "
            + "                                     tabela.encounter_datetime AND "
            + "                         Date_add(tabela.encounter_datetime, "
            + "                         INTERVAL 4 MONTH)) >= 1 ) "
            + "             GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>The patient date from M is registered on FILT (encounter type 60, encounter datetime<=
   *       enddate) and:
   *   <li>The patient has at least 1 drug pick-up on FILT (encounter type 60) with “Regime de TPT”
   *       (concept id 23985) value coded “3HP” doxina” (concept id in [23954, 23984]) and “Tipo de
   *       dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720) until a
   *       4-month period from the 3HP Strat Date (date from M.2;) or
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
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionFiltQuery =
        unionBuilder
            .unionBuilder(sectionFromM.TWO.getQuery())
            .union(sectionFromM.SEVEN.getQuery())
            .union(sectionFromM.EIGHT.getQuery())
            .buildQuery();

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join ( "
            + unionFiltQuery
            + "             "
            + "   ) AS tabela  "
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
            + "             INTERVAL 7 MONTH)) >= 1 ) "
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
   *       TPT” (concept id 23985) value coded “3HP” or “3HP+Pridoxina” (concept id -in [23954,
   *       23984]) and “Tipo de dispensa” (concept id 23986) with value coded “Mensal” (concept id
   *       1098) until a 4-month period from the FILT 3HP Start Date (date from M.2.)
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
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separeted query based on the given queries
    String unionFiltQuery =
        unionBuilder
            .unionBuilder(sectionFromM.TWO.getQuery())
            .union(sectionFromM.SEVEN.getQuery())
            .union(sectionFromM.EIGHT.getQuery())
            .buildQuery();

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join ( "
            + unionFiltQuery
            + "   "
            + "  ) AS tabela  "
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
            + "                                          AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                                          AND o.obs_datetime BETWEEN "
            + "                                            tabela.encounter_datetime AND "
            + "                                Date_add(tabela.encounter_datetime, "
            + "                                INTERVAL 4 MONTH))) "
            + "                           AND EXISTS (SELECT o.person_id  "
            + "                                       FROM   obs o    "
            + "                                       WHERE  o.encounter_id = ee.encounter_id "
            + "                                              AND o.concept_id = ${23986} "
            + "                                              AND o.value_coded IN ( ${1098} )) ) "
            + "                       >= 3 ) "
            + " GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  public enum sectionFromM {
    ONE {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart1();
      }
    },
    TWO {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart2();
      }
    },
    THREE {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart3();
      }
    },
    FOUR {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart4();
      }
    },
    FIVE {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart5();
      }
    },
    SIX {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart6();
      }
    },
    SEVEN {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart7();
      }
    },
    EIGHT {
      @Override
      public String getQuery() {
        return TPTEligiblePatientsQueries.getMpart8();
      }
    };

    public abstract String getQuery();
  }

  /**
   * <B>For each M:</B>
   * <li>Select all patients with Última profilaxia(concept id 23985) value coded 3HP(concept id
   *     23954) and Data Fim (value datetime) selected on Ficha Resumo by end date (Encounter type
   *     53) and with value datetime between 86 days and 365 days from the date of M.3
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition get3HPLastProfilaxyDuringM3Period(sectionFromM section) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        " all patients with Última profilaxia 3HP Between 86 days and 365 days from the date of M.3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        "SELECT mSection.patient_id "
            + "FROM   ( "
            + section.getQuery()
            + " ) AS mSection "
            + "       inner join (SELECT p.patient_id "
            + "                   FROM   patient p "
            + "                          inner join encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                          inner join obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "                          inner join obs o2 "
            + "                                  ON e.encounter_id = o2.encounter_id "
            + "                          inner join ( "
            + sectionFromM.THREE.getQuery()
            + " ) "
            + "                                     m3 "
            + "                                  ON m3.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND o2.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND ((o.concept_id = ${23985} "
            + "                          AND o.value_coded = ${23954}) "
            + "                          AND (o2.concept_id = ${165308} "
            + "                          AND o2.value_coded = ${1267} "
            + "                          AND o2.obs_datetime <= :endDate "
            + "                          AND o2.obs_datetime BETWEEN "
            + "                              Date_add(m3.encounter_datetime, "
            + "                              interval 86 day) AND "
            + "                              Date_add(m3.encounter_datetime, interval "
            + "                              365 day)))) completed3hp "
            + "               ON completed3hp.patient_id = mSection.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <B>For each M:</B>
   * <li>Select all patients with Profilaxia TPT (concept id 23985) value coded 3HP (concept id
   *     23954) and Estado da Profilaxia (concept id 165308) value coded Fim (concept id 1267)
   *     registered on Ficha clinica(encounter type 6) by end date and encounter datetime between 86
   *     days and 365 days from the date of M.1
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition get3HPProfilaxyStatusDuringM1Period(sectionFromM section) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        " all patients with Estado da Profilaxia = Fim Between 86 days and 365 days from the date of M.1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        "SELECT mSection.patient_id "
            + "     FROM   ( "
            + section.getQuery()
            + "            ) AS mSection "
            + "                inner join (SELECT p.patient_id "
            + "                            FROM   patient p "
            + "                                       inner join encounter e "
            + "                                                  ON p.patient_id = e.patient_id "
            + "                                       inner join obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                                       inner join obs o2 "
            + "                                                  ON e.encounter_id = o2.encounter_id "
            + "                                       inner join ( "
            + sectionFromM.ONE.getQuery()
            + "                                             ) m1     ON m1.patient_id = p.patient_id "
            + "                            WHERE  p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND o2.voided = 0 "
            + "                              AND e.location_id = :location "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND ( ( o.concept_id = ${23985} "
            + "                                AND o.value_coded = ${23954} ) "
            + "                              AND ( o2.concept_id = ${165308} "
            + "                                AND o2.value_coded = ${1267} "
            + "                              AND o2.obs_datetime <= :endDate "
            + "                              AND o2.obs_datetime BETWEEN "
            + "                                Date_add(m1.encounter_datetime, "
            + "                                         interval 86 day) "
            + "                                AND "
            + "                                Date_add(m1.encounter_datetime, "
            + "                                         interval 365 day) ) ) ) profStatus "
            + "                           ON profStatus.patient_id = mSection.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Composition for patients who already completed 3hp during M3 OR M1 period
   *
   * @see #get3HPLastProfilaxyDuringM3Period(sectionFromM)
   * @see #get3HPProfilaxyStatusDuringM1Period(sectionFromM)
   * @return {@link CompositionCohortDefinition}
   */
  public CohortDefinition get3HPLastProfilaxyDuringM3orM1PeriodsComposition() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Patients who already completed 3HP During M3 OR M1 period");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";

    // M3 period indicators
    cd.addSearch(
        "M31", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.ONE), mapping));
    cd.addSearch(
        "M32", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.TWO), mapping));

    cd.addSearch(
        "M33", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.THREE), mapping));

    cd.addSearch(
        "M34", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.FOUR), mapping));

    cd.addSearch(
        "M35", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.FIVE), mapping));

    cd.addSearch(
        "M36", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.SIX), mapping));

    cd.addSearch(
        "M37", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.SEVEN), mapping));

    cd.addSearch(
        "M38", EptsReportUtils.map(get3HPLastProfilaxyDuringM3Period(sectionFromM.EIGHT), mapping));

    // M1 period indicators
    cd.addSearch(
        "M11", EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.ONE), mapping));
    cd.addSearch(
        "M12", EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.TWO), mapping));

    cd.addSearch(
        "M13",
        EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.THREE), mapping));

    cd.addSearch(
        "M14",
        EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.FOUR), mapping));

    cd.addSearch(
        "M15",
        EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.FIVE), mapping));

    cd.addSearch(
        "M16", EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.SIX), mapping));

    cd.addSearch(
        "M17",
        EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.SEVEN), mapping));

    cd.addSearch(
        "M18",
        EptsReportUtils.map(get3HPProfilaxyStatusDuringM1Period(sectionFromM.EIGHT), mapping));

    cd.setCompositionString(
        "(M31 OR M32 OR M33 OR M34 OR M35 OR M36 OR M37 OR M38) OR (M11 OR M12 OR M13 OR M14 OR M15 OR M16 OR M17 OR M18)");

    return cd;
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
