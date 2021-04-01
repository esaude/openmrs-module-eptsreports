package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
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
public class TPTCompletionCohortQueries {

  private HivMetadata hivMetadata;

  private TbMetadata tbMetadata;

  private TbPrevCohortQueries tbPrevCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  private TXTBCohortQueries txTbCohortQueries;

  @Autowired
  public TPTCompletionCohortQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      TbPrevCohortQueries tbPrevCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      TXTBCohortQueries txTbCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.tbPrevCohortQueries = tbPrevCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.txTbCohortQueries = txTbCohortQueries;
  }

  private final String mapping = "endDate=${endDate},location=${location}";
  private final String mapping2 = "onOrBefore=${endDate},location=${location}";

  public CohortDefinition getPatientsThatCompletedProphylaticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidTPTCalculation.class).get(0));
    cd.setName("Patients that completed prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   *
   *
   * <h4>TX_CURR with TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR with TPT Completion");
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
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A4",
        EptsReportUtils.map(
            getINHStartA4(
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "A5",
        EptsReportUtils.map(
            getINHStartA5(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "C1",
        EptsReportUtils.map(
            get3HPStartC1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "C2",
        EptsReportUtils.map(
            get3HPStartC2(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "completedAll",
        EptsReportUtils.map(getPatientsThatCompletedIsoniazidProphylacticTreatment(), mapping2));

    compositionCohortDefinition.setCompositionString(
        // "txcurr AND ((A1 OR A2 OR A3 OR A4 OR A5 OR C1 OR C2) AND completedAll)");
        "txcurr AND completedAll");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR without TPT Completion");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "txcurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true),
            "onOrBefore=${endDate},location=${location},locations=${location}"));

    compositionCohortDefinition.addSearch(
        "tpt0", EptsReportUtils.map(getTxCurrWithTPTCompletion(), mapping));

    compositionCohortDefinition.setCompositionString("txcurr AND NOT tpt0");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion with TB Treatment</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletionWithTB() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR without TPT Completion with TB Treatment");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "end Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "startDate=${endDate-1095d},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(txTbCohortQueries.txTbNumerator(), generalParameterMapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND E");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion with Positive TB Screening</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletionWithPositiveTBScreening() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName(
        "TX_CURR without TPT Completion with Positive TB Screening");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "startDate=${endDate-14d},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "Denominator",
        EptsReportUtils.map(txTbCohortQueries.getDenominator(), generalParameterMapping));

    compositionCohortDefinition.addSearch(
        "PositiveScreening",
        EptsReportUtils.map(txTbCohortQueries.positiveScreening(), generalParameterMapping));

    // F = Dnominator AND Positive Screening

    compositionCohortDefinition.setCompositionString(
        "tpt1 AND (Denominator AND PositiveScreening)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR eligible for TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrEligibleForTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR eligible for TPT Completion");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt2", EptsReportUtils.map(getTxCurrWithoutTPTCompletionWithTB(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt3",
        EptsReportUtils.map(getTxCurrWithoutTPTCompletionWithPositiveTBScreening(), mapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND NOT (tpt2 OR tpt3)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR with TPT in last 7 months</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithTPTInLast7Months() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR with TPT in last 7 months");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "onOrAfter=${endDate-210d},onOrBefore=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "G", EptsReportUtils.map(tbPrevCohortQueries.getDenominator(), generalParameterMapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND G");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR eligible for TPT Initiation</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrEligibleForTPTInitiation() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR eligible for TPT Initiation");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "tpt4", EptsReportUtils.map(getTxCurrEligibleForTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt5", EptsReportUtils.map(getTxCurrWithTPTInLast7Months(), mapping));

    compositionCohortDefinition.setCompositionString("tpt4 AND NOT tpt5");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>B - INH final and D - 3HP Final
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidTPTCalculation.class).get(0));
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A -A1: Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia
   *       Isoniazida (Data Inicio)” (concept id 6128) value datetime not null and before end date
   *   <li>
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
        "  SELECT"
            + "  p.patient_id"
            + "  FROM"
            + "  patient p"
            + "     INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "     INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + " AND e.encounter_type = ${53}"
            + " AND o.concept_id = ${6128}"
            + " AND o.value_datetime IS NOT NULL"
            + " AND o.value_datetime < :endDate"
            + " AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A2: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia INH”
   *       (concept id 6122) with value code “Inicio” (concept id 1256) and encounter datetime
   *       before end date
   *   <li>
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
        " SELECT"
            + " p.patient_id"
            + " FROM"
            + " patient p"
            + " INNER JOIN"
            + " encounter e ON p.patient_id = e.patient_id"
            + " INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + "    AND e.encounter_type = ${6}"
            + "    AND o.concept_id = ${6122}"
            + "    AND o.value_coded = ${1256}"
            + "    AND e.encounter_datetime < :endDate"
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A3: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia com INH”
   *       (concept id 6128) value datetime before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA3(int encounterType, int profilaxiaIsoniazidaConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", encounterType);
    map.put("6128", profilaxiaIsoniazidaConcept);

    String query =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE e.encounter_type = ${6} "
            + "AND o.concept_id = ${6128} "
            + "AND e.location_id = :location "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A4: Select all patients with Ficha Seguimento PEdiatrico (encounter type 9) with
   *       “Profilaxia com INH” (concept id 6128) value datetime before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA4(
      int pediatriaSeguimentoEncounterType, int dataInicioProfilaxiaIsoniazidaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ficha Seguimento Pediatrico ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("9", pediatriaSeguimentoEncounterType);
    map.put("6128", dataInicioProfilaxiaIsoniazidaConcept);

    String query =
        " SELECT "
            + "	p.patient_id "
            + " FROM "
            + "  	patient p "
            + "     	INNER JOIN "
            + " 	encounter e ON p.patient_id = e.patient_id "
            + "     	INNER JOIN "
            + " 	obs o ON e.encounter_id = o.encounter_id "
            + " WHERE "
            + " 	p.voided = 0 AND e.voided = 0 "
            + "     	AND o.voided = 0 "
            + "     	AND e.encounter_type = ${9} "
            + "     	AND o.concept_id = ${6128} "
            + " 	AND e.location_id = :location "
            + "     	AND e.encounter_datetime < :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A5: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982])
   *       and encounter datetime before the reporting period
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA5(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Regime de TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);

    String query =
        " SELECT"
            + " p.patient_id"
            + " FROM"
            + " patient p"
            + "    INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "    INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + "     AND e.encounter_type = ${60}"
            + "     AND o.concept_id = ${23985}"
            + "    AND o.value_coded IN (${656} , ${23982})"
            + "     AND e.encounter_datetime < :endDate"
            + "        AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C1: Select all patients with Ficha Clinica - Master Card (encounter type 6) with “Outras
   *       prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id 23954) and
   *       encounter datetime before end date;
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC1(
      int encounterType, int treatmentPrescribedConcept, int threeHPConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica Master Card ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "After Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", encounterType);
    map.put("1719", treatmentPrescribedConcept);
    map.put("23954", threeHPConcept);

    String query =
        "SELECT p.patient_id FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE e.encounter_type = ${6} "
            + "AND o.concept_id = ${1719} AND o.value_coded = ${23954} "
            + "AND e.location_id = :location "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C2: Select all patients with FILT (encounter type 6) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       encounter datetime before end date;
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC2(
      int fILT, int regimeTPTConcept, int hPConcept, int hPPiridoxinaConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with FILT ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", fILT);
    map.put("23985", regimeTPTConcept);
    map.put("23954", hPConcept);
    map.put("23984", hPPiridoxinaConcept);

    String query =
        " SELECT  "
            + "  p.patient_id "
            + " 	FROM "
            + "     	patient p "
            + " 	INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " 	INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " 	WHERE "
            + "    	e.encounter_type = ${60} AND p.voided = 0 "
            + "         	AND e.voided = 0 "
            + "         	AND o.voided = 0 "
            + "         	AND o.concept_id = ${23985} "
            + "         	AND o.value_coded in (${23954},${23984}) "
            + "         	AND e.location_id = :location "
            + "         	AND e.encounter_datetime < :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
