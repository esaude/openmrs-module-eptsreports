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
    CohortDefinition A3HP3 = getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamento();
    CohortDefinition A3HP4 = getPatientsWithUltimaProfilaxia3hp();
    CohortDefinition A3HP5 = getPatientWithProfilaxiaTpt3hp();
    CohortDefinition A3HP6 = getPatientsWithOutrasPerscricoesDT3HP();
    CohortDefinition A3HP7 = getPatientsWithRegimeDeTPT3HP();

    cd.addSearch(
        "A3HP1",
        EptsReportUtils.map(
            A3HP1, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP3",
        EptsReportUtils.map(
            A3HP3, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP4",
        EptsReportUtils.map(
            A3HP4, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP5",
        EptsReportUtils.map(
            A3HP5, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP6",
        EptsReportUtils.map(
            A3HP6, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A3HP7",
        EptsReportUtils.map(
            A3HP7, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(A3HP1 OR A3HP3 OR A3HP4 OR A3HP5 OR A3HP6 OR A3HP7)");

    return cd;
  }

  public CohortDefinition getIPTStartCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("B - IPT start 1 or 2 or 3 or 4 or 5 Cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition BIPT1 = getPatientsWithFichaResumoUltimaProfilaxia();
    CohortDefinition BIPT2 = getPatientsWithFichaClinicaProfilaxiaINH();
    CohortDefinition BIPT3 = getPatientsIPTStart3WithFichaClinicaOrFichaPediatrica();
    CohortDefinition BIPT4 = getPatientsWithFirstFiltRegimeTpt();
    CohortDefinition BIPT5 = getpatientswithRegimeTPTIsoniazid();

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
   * <p>Select all patients with Última profilaxia(concept id 23985) value coded 3HP(concept id
   * 23954) and Data Início da Profilaxia TPT(value datetime, concept id 6128) registered during the
   * reporting period on Ficha Resumo (Encounter type 53);
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithUltimaProfilaxia3hp() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("3HP4 - Patients with Ultima Profilaxia on Resumo");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "       AND ( o2.concept_id = ${6128} AND o2.value_datetime BETWEEN :startDate AND :endDate ) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Profilaxia TPT (concept id 23985) value coded 3HP (concept id
   * 23954) and Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256)
   * registered during the reporting period on Ficha Clinica (Encounter type 6)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientWithProfilaxiaTpt3hp() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("3HP5 - Patients with Profilaxia TPT on Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6}"
            + "       AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "       AND o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Outras prescricoes(concept id 1719) value coded DT-3HP (concept id
   * 165307) on Ficha clinica (encounter type 6) during the reporting period;
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithOutrasPerscricoesDT3HP() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("3HP6 - Patients with Outras Prescricoes on Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND o.concept_id = ${1719} "
            + "       AND o.value_coded = ${165307} "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>• Select all patients with “Regime de TPT” (concept id 23985) with value coded “3HP” or ”
   * 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento TPT”(concept ID
   * 23987) value coded “continua” or “fim” or no value(concept ID in [1257, 1267, null]) marked on
   * the first FILT (encounter type 60) and encounter datetime between start date and end date and:
   *
   * <p>◦ N1o other Regime de TPT (concept id 23985) value coded “3HP” or ” 3HP+Piridoxina” (concept
   * id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the FILT 3HP
   * start date.  ; and
   *
   * <p>◦ No other 3HP start dates marked on Ficha clinica (encounter type 6, encounter datetime)
   * with Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   * Profilaxia  (concept id 165308) value coded Início (concept id 1256) or Outras prescrições
   * (concept id 1719) value coded 3HP or DT-3HP (concept id in [23954,165307])in the 4 months prior
   * to the FILT 3HP start date.  ; and
   *
   * <p>◦ No other 3HP start dates marked on Ficha Resumo (encounter type 53) with Última
   * profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início da Profilaxia
   * TPT(value datetime, concept id 6128) in the 4 months prior to the FILT 3HP start date.  ;
   *
   * <p>Note: The system will consider the oldest date amongst all sources as the 3HP Start Date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithRegimeDeTPT3HP() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("3HP7 - Patients with Regime de TPT & Seg.Trat TPT on FILT");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       inner join encounter e ON p.patient_id = e.patient_id "
            + "       inner join obs o ON e.encounter_id = o.encounter_id "
            + "       inner join obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60} "
            + "       AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "       AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1257}, ${1267} ) OR o2.value_coded IS NULL ) "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "       AND p.patient_id NOT IN ( "
            + "           SELECT p.patient_id "
            + "           FROM   patient p "
            + "                  inner join encounter e ON e.patient_id = p.patient_id "
            + "                  inner join obs o ON e.encounter_id = o.encounter_id "
            + "                  inner join (SELECT p.patient_id, "
            + "                         Min(e.encounter_datetime) AS start_date "
            + "                             FROM   patient p "
            + "                             inner join encounter e ON p.patient_id = e.patient_id "
            + "                             inner join obs o ON e.encounter_id = o.encounter_id "
            + "                             inner join obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                             AND e.encounter_type = ${60} "
            + "                             AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                             AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                             AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                             ON filt.patient_id = p.patient_id "
            + "                  WHERE  p.voided = 0 "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND e.location_id = :location "
            + "                  AND e.encounter_type = ${60} "
            + "                  AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                  AND e.encounter_datetime >= Date_sub(filt.start_date, interval 4 month) "
            + "                  GROUP  BY p.patient_id "
            + "           UNION "
            + "           SELECT p.patient_id "
            + "           FROM   patient p "
            + "           inner join encounter e ON e.patient_id = p.patient_id "
            + "           inner join obs o3 ON e.encounter_id = o3.encounter_id "
            + "           inner join obs o4 ON e.encounter_id = o4.encounter_id "
            + "           inner join obs o5 ON e.encounter_id = o5.encounter_id "
            + "           inner join (SELECT p.patient_id, "
            + "                 Min(e.encounter_datetime) AS start_date "
            + "           FROM   patient p "
            + "           inner join encounter e ON p.patient_id = e.patient_id "
            + "           inner join obs o ON e.encounter_id = o.encounter_id "
            + "           inner join obs o2 ON e.encounter_id = o2.encounter_id "
            + "                 WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                 AND e.encounter_type = ${60} "
            + "                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                 ON filt.patient_id = p.patient_id "
            + "           WHERE  p.voided = 0 AND e.voided = 0 AND o3.voided = 0 AND o4.voided = 0 AND o5.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${6} "
            + "           AND ( o3.concept_id = ${23985} AND o3.value_coded = ${23954}   "
            + "                 AND o4.concept_id = ${165308} AND o4.value_coded = ${1256} ) "
            + "           OR  ( o5.concept_id = ${1719} AND o5.value_coded IN ( ${23954}, ${165307} ) ) "
            + "           AND e.encounter_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "           GROUP  BY p.patient_id "
            + "           UNION "
            + "           SELECT p.patient_id "
            + "           FROM   patient p "
            + "           inner join encounter e ON e.patient_id = p.patient_id "
            + "           inner join obs o6 ON e.encounter_id = o6.encounter_id "
            + "           inner join obs o7 ON e.encounter_id = o7.encounter_id "
            + "           inner join (SELECT p.patient_id, "
            + "                 Min(e.encounter_datetime) AS start_date "
            + "                 FROM   patient p "
            + "                 inner join encounter e ON p.patient_id = e.patient_id "
            + "                 inner join obs o ON e.encounter_id = o.encounter_id "
            + "                 inner join obs o2 ON e.encounter_id = o2.encounter_id "
            + "                 WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                 AND e.encounter_type = ${60} "
            + "                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                 ON filt.patient_id = p.patient_id "
            + "           WHERE p.voided = 0 AND e.voided = 0 AND o6.voided = 0 AND o7.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${53} "
            + "           AND o6.concept_id = ${23985} AND o6.value_coded = ${23954} "
            + "           AND o7.concept_id = ${6128} AND o7.value_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "           GROUP BY p.patient_id) "
            + "GROUP BY p.patient_id ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b> Technical Specs <b>
   *
   * <blockquote>
   *
   * <p>1: Select all patients with Ficha Resumo (encounter type 53) with “Ultima profilaxia
   * Isoniazida (Data Inicio)” (concept id 6128) and value datetime not null  or Última
   * profilaxia(concept id 23985) value coded INH(concept id 656) and Data Início da Profilaxia
   * TPT(value datetime, concept id 6128)  not null and exclude all patients with Última
   * profilaxia(concept id 23985) value coded 3HP(concept id 23954) and between start date and end
   * date.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFichaResumoUltimaProfilaxia() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("BIPT1 - Patients on Ficha Resumo with Ultima profilaxia Isoniazida");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( ( o.concept_id = ${6128} AND o.value_datetime IS NOT NULL ) "
            + "       OR ( ( o2.concept_id = ${23985} AND o2.value_coded = ${656} ) "
            + "              AND ( o3.concept_id = ${6128} AND o3.value_datetime IS NOT NULL ) ) ) "
            + "       AND p.patient_id NOT IN (SELECT p.patient_id "
            + "                                FROM   patient p "
            + "                                       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND o2.concept_id = ${23985} AND o2.value_coded = ${23954} "
            + "                                       AND e.encounter_datetime BETWEEN :startDate AND :endDate) "
            + "GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b> Technical Specs </b>
   *
   * <blockquote>
   *
   * <p>2: Select all patients with Ficha clinica (encounter type 6) with “Profilaxia INH” (concept
   * id 6122) with value code “Inicio” (concept id 1256) or  Profilaxia TPT (concept id 23985) value
   * coded INH (concept id 656) and Estado da Profilaxia (concept id 165308) value coded Início
   * (concept id 1256) and encounter datetime between start date and end date.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFichaClinicaProfilaxiaINH() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("BIPT2 - atients with Ficha Clinica with Profilaxia INH");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        "SELECT     p.patient_id "
            + "        FROM       patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "        AND   e.location_id = :location "
            + "        AND   e.encounter_type = ${6} "
            + "        AND   ( o.concept_id = ${6122} AND o.value_coded = ${1256} ) "
            + "        OR    ( (o2.concept_id = ${23985} AND o2.value_coded = ${656}) "
            + "              AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) ) "
            + "        AND   e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
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
    sqlCohortDefinition.setName("BIPT3 - select patients with Ficha Clinica or Pediátrica");
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
   * <b> Technical Specs </b>
   *
   * <blockquote>
   *
   * <p>4: Select all patients with the First FILT (encounter type 60) with “Regime de TPT” (concept
   * id 23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and
   * “Seguimento de Tratamento TPT” (concept ID 23987) with values “Continua” (concept ID 1257) or
   * no value(null) or concept 23987 does not exist and encounter datetime (as INH Start Date)
   * between start date and end date and:
   *
   * <ul>
   *   <li>No other INH values [Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *       ‘Isoniazid + piridoxina’ (concept id in [656, 23982])] marked on FILT in the 7 months
   *       prior to ‘INH Start Date’; and
   *   <li>No Última profilaxia Isoniazida (Data Início) (Concept ID 6128, value_datetime)  or
   *       Última profilaxia(concept id 23985) value coded INH(concept id 656) and Data Início da
   *       Profilaxia TPT(value datetime, concept id 6128)  not null  registered in Ficha Resumo -
   *       Mastercard (Encounter Type ID 53) in the 7 months prior to ‘INH Start Date’; and
   *   <li>No Profilaxia (INH) (Concept ID 6122) with the value “I” (Início) (Concept ID 1256) or
   *        Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início (concept id 1256) marked on Ficha
   *       Clínica - Mastercard (Encounter Type ID 6)  in the 7 months prior to ‘INH Start Date’;
   *       and
   *   <li>No Profilaxia com INH – TPI (Data Início) (Concept ID 6128, value_datetime) marked in
   *       Ficha de Seguimento (Adulto e Pediatria) (Encounter Type ID 6,9) in the 7 months prior to
   *       ‘INH Start Date’
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFirstFiltRegimeTpt() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("BIPT4 - Patients with the First Filt with regime de TPT");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

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
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "                FROM  patient p "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) start_date "
            + "                                    FROM patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               INNER JOIN obs o2 on o2.encounter_id = e.encounter_id "
            + "                                    WHERE   p.voided = 0  AND e.voided = 0 "
            + "               AND o.voided = 0  AND e.location_id = :location "
            + "               AND e.encounter_type = ${60} "
            + "               AND ( o.concept_id = ${23985}  AND o.value_coded IN (${656}, ${23982}) ) "
            + "               AND ((o2.concept_id =  ${23987}  AND (o2.value_coded =  ${1257}  OR o2.value_coded IS NULL)) "
            + "               OR  o2.concept_id NOT IN( SELECT oo.concept_id FROM obs oo WHERE oo.voided = 0 "
            + "                               AND oo.encounter_id = e.encounter_id "
            + "                               AND oo.concept_id =  ${23987}  )) "
            + "               AND e.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 210 DAY) AND :endDate "
            + "                                    GROUP BY p.patient_id) AS filt "
            + "                                        ON filt.patient_id = p.patient_id "
            + "                WHERE p.patient_id NOT IN ( SELECT pp.patient_id "
            + "                    FROM patient pp "
            + "                            INNER JOIN encounter ee ON ee.patient_id = pp.patient_id "
            + "                            INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id "
            + "                    WHERE pp.voided = 0 "
            + "                    AND p.patient_id = pp.patient_id "
            + "                    AND ee.voided = 0 "
            + "                    AND oo.voided = 0 "
            + "                    AND ee.location_id = :location "
            + "                    AND ee.encounter_type = ${60} "
            + "                    AND oo.concept_id = ${23985} "
            + "                    AND oo.value_coded IN ( ${656} , ${23982} ) "
            + "                    AND ee.encounter_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND ee.encounter_datetime < filt.start_date "
            + "                    GROUP BY p.patient_id "
            + "                    UNION "
            + "                    SELECT p.patient_id FROM patient p "
            + "                    JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                    JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                    WHERE o.voided = 0 AND e.voided = 0 AND p.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${53} "
            + "                    AND (o.concept_id = ${6128}  AND o.value_datetime IS NOT NULL) "
            + "                    AND (o2.concept_id = ${23985} AND o2.value_coded = ${656} AND o2.value_datetime IS NOT NULL) "
            + "                    AND e.encounter_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND e.encounter_datetime < filt.start_date "
            + "                    UNION "
            + "                    SELECT p.patient_id FROM patient p "
            + "                    JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                    Join obs o2 ON o2.encounter_id = e.encounter_id "
            + "                    Join obs o3 ON o3.encounter_id = e.encounter_id "
            + "                    WHERE p.voided = 0 AND o.voided = 0 AND e.voided = 0 AND o2.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND (o.concept_id = ${6122} AND o.value_coded = ${1256}) "
            + "                    OR  ( (o2.concept_id = ${23985} AND o2.value_coded = ${656}) "
            + "                         AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) ) "
            + "                    AND e.encounter_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND e.encounter_datetime < filt.start_date "
            + "                     UNION "
            + "                    SELECT p.patient_id FROM patient p "
            + "                    JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                    WHERE e.encounter_type IN (${6},${9}) AND o.concept_id = ${6128} "
            + "                    AND o.voided = 0 AND e.voided = 0 "
            + "                    AND p.voided = 0 AND e.location_id = :location "
            + "                    AND o.value_datetime IS NOT NULL "
            + "                    AND o.value_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND o.value_datetime < filt.start_date) "
            + "             GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b> Technical Specs </b>
   *
   * <blockquote>
   *
   * <p>5: Select all patients with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   * ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and   “Seguimento de tratamento
   * TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in [1256, 1705]) marked
   * on FILT (encounter type 60) and e ncounter datetime between start date and end date.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getpatientswithRegimeTPTIsoniazid() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("BIPT5 - Patients with Regime de TPT Isoniazid / Isoniazid + Piridoxina");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, "
            + " MIN(e.encounter_datetime) AS ipt_start_date "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o1 ON e.encounter_id = o1.encounter_id "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o1.voided = 0 AND o2.voided = 0 "
            + "AND   e.location_id = :location "
            + "AND   e.encounter_type = ${60} "
            + "AND   ( o1.concept_id = ${23985} AND o1.value_coded IN( ${656}, ${23982} ) ) "
            + "AND   ( o2.concept_id = ${23987} AND o2.value_coded IN( ${1256}, ${1705} ) ) "
            + "AND   e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
}
