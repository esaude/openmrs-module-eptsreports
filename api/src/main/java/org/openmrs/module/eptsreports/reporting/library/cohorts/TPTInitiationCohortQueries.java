package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTInitiationCohortQueries {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public TPTInitiationCohortQueries(
      HivMetadata hivMetadata, TbMetadata tbMetadata, GenericCohortQueries genericCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.genericCohortQueries = genericCohortQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TPT = INH OR 3HP - Cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition baseCohort = genericCohortQueries.getBaseCohort();
    CohortDefinition A = get3HPStartCohort();
    CohortDefinition B = getIPTStartCohort();
    cd.addSearch(
        "basecohort",
        EptsReportUtils.map(
            baseCohort, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "A",
        EptsReportUtils.map(A, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(B, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("basecohort AND (A OR B)");

    return cd;
  }

  public CohortDefinition get3HPStartCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TPT Initiation Patient List Cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition A3HP1 = getPatientsWith3HP1Prescriptions();
    CohortDefinition A3HP2 = getPatientsWith3HP2FirstFILTAndRegimeTPT();
    CohortDefinition A3HP3 = getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamento();

    cd.addSearch(
        "A3HP1",
        EptsReportUtils.map(
            A3HP1, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP2",
        EptsReportUtils.map(
            A3HP2, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP3",
        EptsReportUtils.map(
            A3HP3, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(A3HP1 OR A3HP2 OR A3HP3)");

    return cd;
  }

  public CohortDefinition getIPTStartCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("B - IPT start 1 or 2 or 3 or 4 or 5 Cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition BIPT1 = getPatientsIPTStart1WithUltimaProxilaxiaIsonazida();
    CohortDefinition BIPT2 = getPatientsIPTStart2WithProfilaxiaINH();
    CohortDefinition BIPT3 = getPatientsIPTStart3WithFichaClinicaOrFichaPediatrica();
    CohortDefinition BIPT4 = getPatientsIPTStart4WithFirstFILTAndRegimeDeTPT();
    CohortDefinition BIPT5 = getPatientsIPTStart5WithTPTAndMarkedFILT();

    cd.addSearch(
        "BIPT1",
        EptsReportUtils.map(
            BIPT1, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "BIPT2",
        EptsReportUtils.map(
            BIPT2, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "BIPT3",
        EptsReportUtils.map(
            BIPT3, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "BIPT4",
        EptsReportUtils.map(
            BIPT4, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "BIPT5",
        EptsReportUtils.map(
            BIPT5, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(BIPT1 OR BIPT2 OR BIPT3 OR BIPT4 OR BIPT5)");

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Ficha Clinica - MasterCard (encounter type 6) with “Outras
   * prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id 23954) and
   * encounter_datetime between start date and end date as <b>3HP Start Date</b> and (exception-> no
   * other 3HP prescriptions [“Outras prescricoes” (concept id 1719) with value coded equal to “3HP”
   * (concept id 23954)] marked on Ficha-Clínica in the 4 months prior to the <b>3HP Start Date</b>
   * and no “Regime de TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept
   * id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) marked on
   * FILT (encounter type 60) in the 4 months prior to the <b>3HP Start Date</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWith3HP1Prescriptions() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP1 - Patients with other 3HP prescriptions");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());

    String query =
        "SELECT p.patient_id  "
            + "         FROM  patient p  "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id       "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id       "
            + "         INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date      "
            + "      FROM    patient p       "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id      "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id       "
            + "      WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.location_id = :location      "
            + "          AND e.encounter_type =   ${6} AND o.concept_id =   ${1719}       "
            + "          AND o.value_coded IN ( ${23954} ) AND e.encounter_datetime >= :startDate     "
            + "          AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) AS pickup      "
            + "      ON pickup.patient_id = p.patient_id     "
            + "         WHERE p.patient_id NOT IN (    "
            + "           SELECT patient_id       "
            + "           FROM patient p      "
            + "           WHERE p.voided = 0   AND e.voided = 0       "
            + "               AND o.voided = 0   AND e.location_id = :location      "
            + "               AND e.encounter_type =   ${6}  AND o.concept_id =   ${1719}       "
            + "               AND o.value_coded IN ( ${23954} )      "
            + "               AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH)       "
            + "               AND e.encounter_datetime < pickup.first_pickup_date   "
            + "           UNION   "
            + "           SELECT p.patient_id   "
            + "           FROM patient p   "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "           WHERE p.voided = 0 AND e.voided = 0  AND o.voided = 0 AND e.encounter_type=  ${60}    "
            + "               AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954} , ${23984} ))   "
            + "               AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH)       "
            + "               AND e.encounter_datetime < pickup.first_pickup_date)    ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>2: Select all patients with the First FILT (encounter type 60) with “Regime de TPT” (concept
   * id 23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and encounter
   * datetime (as <b>3HP Start Date</b>) between start date and end date and no other 3HP
   * prescriptions (concept id 1719) marked on Ficha-Clinica in the 4 months prior to the <b>3HP
   * Start Date</b> and no “Regime de TPT” (concept id 23985) with value coded “3HP” or ”
   * 3HP+Piridoxina” (concept id in [23954, 23984]) marked on FILT (encounter type 60) in the 4
   * months prior to the <b>3HP Start Date</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWith3HP2FirstFILTAndRegimeTPT() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP2 - Patients with First FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());

    String query =
        " SELECT p.patient_id     "
            + "         FROM  patient p       "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id       "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id       "
            + "         INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date      "
            + "      FROM    patient p       "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id      "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id       "
            + "      WHERE   p.voided = 0 AND e.voided = 0       "
            + "          AND o.voided = 0   AND e.location_id = :location      "
            + "          AND e.encounter_type =   ${60}       "
            + "          AND o.concept_id =   ${23985}       "
            + "          AND o.value_coded IN (${23954},${23984}  )      "
            + "          AND e.encounter_datetime >= :startDate     "
            + "          AND e.encounter_datetime <= :endDate     "
            + "      GROUP BY p.patient_id) AS pickup      "
            + "      ON pickup.patient_id = p.patient_id     "
            + "         WHERE p.patient_id NOT IN ( SELECT patient_id       "
            + "       FROM patient p      "
            + " WHERE p.voided = 0       "
            + "            AND e.voided = 0 AND o.voided = 0       "
            + "            AND e.location_id = :location      "
            + "            AND e.encounter_type =   ${6}  AND o.concept_id =   ${23985}       "
            + "            AND o.value_coded IN ( ${23954}  ,  ${23984} )      "
            + "            AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH)       "
            + "            AND e.encounter_datetime < pickup.first_pickup_date   "
            + "   UNION   "
            + "  SELECT p.patient_id   "
            + "  FROM patient p   "
            + "      INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "  WHERE p.voided = 0 AND e.voided = 0   "
            + "      AND o.voided = 0   "
            + "      AND e.encounter_type= ${60}    "
            + "      AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954} , ${23984} ))   "
            + "      AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH)       "
            + "      AND e.encounter_datetime < pickup.first_pickup_date)   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with “Regime de TPT” (concept id 23985) with value coded “3HP” or ”
   * 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento TPT”(concept ID
   * 23987) value coded “inicio” or “re-inicio” (concept ID in [1256, 1705]) marked on FILT
   * (encounter type 60) and encounter datetime between start date and end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamento() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP3 - Patients with Regime TPT marked on FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT p.patient_id   "
            + "  FROM patient p   "
            + "      INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "      INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "  WHERE p.voided = 0 AND e.voided = 0   "
            + "      AND o.voided = 0   "
            + "      AND e.encounter_type=  ${60}    "
            + "      AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954}  , ${23984} ))   "
            + "      AND (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} ))   "
            + "      AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "      GROUP BY p.patient_id   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia Isoniazida
   * (Data Inicio)” (concept id 6128) and value datetime not null and between start date and end
   * date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart1WithUltimaProxilaxiaIsonazida() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "IPT start 1 - Patients with ficha resumo and ultima profilaxia Isoniazida");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());

    String query =
        "  SELECT p.patient_id FROM patient p      "
            + "  JOIN encounter e ON e.patient_id = p.patient_id     "
            + "  JOIN obs o ON o.encounter_id = e.encounter_id     "
            + "  WHERE e.encounter_type =   ${53}   AND o.concept_id =   ${6128}       "
            + "  AND o.voided = 0 AND e.voided = 0      "
            + "  AND p.voided = 0 AND e.location_id = :location     "
            + "  AND o.value_datetime IS NOT NULL     "
            + "  AND o.value_datetime BETWEEN :startDate AND :endDate     ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Ficha clinica (encounter type 6) with “Profilaxia INH” (concept id
   * 6122) with value code “Inicio” (concept id 1256) and encounter datetime between start date and
   * end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart2WithProfilaxiaINH() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("IPT start 2 - Patient with Ficha Clínica and Profilaxia INH");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "    SELECT p.patient_id FROM patient p      "
            + "  JOIN encounter e ON e.patient_id = p.patient_id     "
            + "  JOIN obs o ON o.encounter_id = e.encounter_id     "
            + "  WHERE e.encounter_type =   ${6}   AND o.concept_id =   ${6122}       "
            + "  AND o.voided = 0 AND e.voided = 0      "
            + "  AND p.voided = 0 AND e.location_id = :location     "
            + "  AND o.value_coded =   ${1256}       "
            + "  AND e.encounter_datetime BETWEEN :startDate AND :endDate     ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Ficha clinica or Ficha Pediatrica (encounter type 6,9) with
   * “Profilaxia com INH” (concept id 6128) and value datetime is not null and between start date
   * and end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart3WithFichaClinicaOrFichaPediatrica() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("IPT start 3 - select patients with Ficha Clinica or Pediátrica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());

    String query =
        "  SELECT p.patient_id FROM patient p      "
            + "  JOIN encounter e ON e.patient_id = p.patient_id     "
            + "  JOIN obs o ON o.encounter_id = e.encounter_id     "
            + "  WHERE e.encounter_type IN (${6},${9}) AND o.concept_id = ${6128}       "
            + "  AND o.voided = 0 AND e.voided = 0      "
            + "  AND p.voided = 0 AND e.location_id = :location     "
            + "  AND o.value_datetime IS NOT NULL     "
            + "  AND o.value_datetime BETWEEN :startDate AND :endDate     ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Ficha clinica or Ficha Pediatrica (encounter type 6,9) with
   * “Profilaxia com INH” (concept id 6128) and value datetime is not null and between start date
   * and end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart4WithFirstFILTAndRegimeDeTPT() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("IPT start 4 - select patients with first FILT and Regime de TPT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        " SELECT p.patient_id   "
            + "   FROM  patient p   "
            + "           INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "           INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "           INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date   "
            + "                       FROM patient p INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "  INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "  INNER JOIN obs o2 on o2.encounter_id = e.encounter_id   "
            + "                       WHERE   p.voided = 0  AND e.voided = 0   "
            + "  AND o.voided = 0  AND e.location_id = :location   "
            + "  AND e.encounter_type = ${60}    "
            + "  AND (o.concept_id = ${23985}  AND o.value_coded IN (${656},${23982}))   "
            + "  AND ((o2.concept_id =  ${23987}  AND (o2.value_coded =  ${1257}  OR o2.value_coded IS NULL))   "
            + "  OR  o2.concept_id NOT IN( SELECT oo.concept_id FROM obs oo WHERE oo.voided = 0   "
            + "                  AND oo.encounter_id = e.encounter_id   "
            + "                  AND oo.concept_id =  ${23987}  ))   "
            + "  AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate   "
            + "                       GROUP BY p.patient_id) AS pickup   "
            + "                       ON pickup.patient_id = p.patient_id   "
            + "   WHERE p.patient_id NOT IN ( SELECT pp.patient_id   "
            + "       FROM patient pp   "
            + "               INNER JOIN encounter ee ON ee.patient_id = pp.patient_id   "
            + "               INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id   "
            + "       WHERE pp.voided = 0   "
            + "       AND p.patient_id = pp.patient_id   "
            + "       AND ee.voided = 0   "
            + "       AND oo.voided = 0   "
            + "       AND ee.location_id = :location   "
            + "       AND ee.encounter_type = ${60}    "
            + "       AND oo.concept_id = ${23985}    "
            + "       AND oo.value_coded IN ( ${656} ,  ${23982} )   "
            + "       AND ee.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 7 MONTH)   "
            + "       AND ee.encounter_datetime < pickup.first_pickup_date   "
            + "       UNION   "
            + "       SELECT p.patient_id FROM patient p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type = ${53}    AND o.concept_id = ${6128}    "
            + "       AND o.voided = 0 AND e.voided = 0   "
            + "       AND p.voided = 0 AND e.location_id = :location   "
            + "       AND o.value_datetime IS NOT NULL   "
            + "       AND o.value_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 7 MONTH)   "
            + "       AND o.value_datetime < pickup.first_pickup_date   "
            + "       UNION   "
            + "       SELECT p.patient_id FROM patient p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type = ${6}    AND o.concept_id = ${6122}    "
            + "       AND o.voided = 0 AND e.voided = 0   "
            + "       AND p.voided = 0 AND e.location_id = :location   "
            + "       AND o.value_coded = ${1256}    "
            + "       AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 7 MONTH)   "
            + "       AND e.encounter_datetime < pickup.first_pickup_date   "
            + "       UNION   "
            + "       SELECT p.patient_id FROM patient p   "
            + "       JOIN encounter e ON e.patient_id = p.patient_id   "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "       WHERE e.encounter_type IN (${6},${9}) AND o.concept_id = ${6128}    "
            + "       AND o.voided = 0 AND e.voided = 0   "
            + "       AND p.voided = 0 AND e.location_id = :location   "
            + "       AND o.value_datetime IS NOT NULL   "
            + "       AND o.value_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 7 MONTH)   "
            + "       AND o.value_datetime < pickup.first_pickup_date)   "
            + "   GROUP BY p.patient_id   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   * ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Seguimento de tratamento
   * TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in [1256, 1705]) marked
   * on FILT (encounter type 60) and encounter datetime between start date and end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart5WithTPTAndMarkedFILT() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("IPT start 5 -select patients with Regime TPT and marked on FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p   "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "   WHERE p.voided = 0 AND e.voided = 0   "
            + "       AND o.voided = 0   "
            + "       AND e.encounter_type=   ${60}    "
            + "       AND (o.concept_id=  ${23985}   AND o.value_coded IN (${656},${23982}))   "
            + "       AND (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} ))   "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "       GROUP BY p.patient_id  ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
