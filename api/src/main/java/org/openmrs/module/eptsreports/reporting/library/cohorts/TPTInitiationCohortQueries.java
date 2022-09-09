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

    CohortDefinition A3HP3 = getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamento();
    CohortDefinition A3HP4 = getPatientsWithUltimaProfilaxia3hp();
    CohortDefinition A3HP5 = getPatientWithProfilaxiaTpt3hp();
    CohortDefinition A3HP6 = getPatientsWithOutrasPerscricoesDT3HP();
    CohortDefinition A3HP7 = getPatientsWithRegimeDeTPT3HP();

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

    cd.setCompositionString("(A3HP3 OR A3HP4 OR A3HP5 OR A3HP6 OR A3HP7)");

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
            + "      AND ( (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954}  , ${23984} ) )   "
            + "      AND   (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} )   "
            + "      AND o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
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
   * 23954) and Data Início selected in Ficha Resumo - Mastercard (Encounter type 53) (3HP Start
   * Date) between endDate-4 months and endDate
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
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0"
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "         AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "             AND o2.obs_datetime BETWEEN :startDate AND :endDate) )";

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
   * 23954) and Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256) marked
   * on Ficha Clínica – Mastercard (Encounter type 6) (3HP Start Date) during the reporting period
   * or
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
            + "       AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954})  "
            + "       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND o2.obs_datetime BETWEEN :startDate AND :endDate) )";

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
   * 165307) marked in Ficha Clínica – Mastercard (Encounter type 6) (3HP Start Date) during the
   * reporting period or
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
   * the first FILT (encounter type 60) during the reporting period as “FILT Start Date” and:
   *
   * <p>◦ No other Regime de TPT (concept id 23985) value coded “3HP” or ” 3HP+Piridoxina” (concept
   * id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the FILT 3HP
   * start date. and
   *
   * <p>◦ No other 3HP start dates marked on Ficha clinica (encounter type 6, encounter datetime)
   * with Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   * Profilaxia (concept id 165308) value coded Início (concept id 1256) or Outras prescrições
   * (concept id 1719) value coded DT-3HP (concept id 165307) in the 4 months prior to the FILT 3HP
   * start date. and
   *
   * <p>◦ No other 3HP start dates marked on Ficha Resumo (encounter type 53) with Última
   * profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início (value datetime)
   * in the 4 months prior to the FILT 3HP start date.
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
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Min(o2.obs_datetime) AS start_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "                          INNER JOIN obs o2 "
            + "                                  ON e.encounter_id = o2.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND o2.voided = 0 "
            + "                          AND e.encounter_type = ${60} "
            + "                          AND e.location_id = :location "
            + "                          AND ( ( o.concept_id = ${23985} "
            + "                                  AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                AND ( o2.concept_id = ${23987} "
            + "                                      AND ( o2.value_coded IN ( ${1257}, ${1267} ) "
            + "                                             OR o2.value_coded IS NULL ) "
            + "                                      AND o2.obs_datetime BETWEEN "
            + "                                          :startDate "
            + "                                          AND "
            + "                                          :endDate ) ) "
            + "                   GROUP  BY p.patient_id) AS filt "
            + "               ON p.patient_id = filt.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND NOT EXISTS (SELECT p.patient_id "
            + "                       FROM   patient p "
            + "                              INNER JOIN encounter e "
            + "                                      ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.location_id = :location "
            + "                              AND e.encounter_type = ${60} "
            + "                              AND o.concept_id = ${23985} "
            + "                              AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                              AND e.encounter_datetime <= "
            + "                                  Date_sub(filt.start_date, "
            + "                                  INTERVAL 4 month)) "
            + "       AND NOT EXISTS (SELECT p.patient_id "
            + "                       FROM   patient p "
            + "                              INNER JOIN encounter e "
            + "                                      ON e.patient_id = p.patient_id "
            + "                              INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs o2 "
            + "                                      ON e.encounter_id = o2.encounter_id "
            + "                              INNER JOIN obs o3 "
            + "                                      ON e.encounter_id = o3.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND o2.voided = 0 "
            + "                              AND o3.voided = 0 "
            + "                              AND e.location_id = :location "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND ( ( ( o.concept_id = ${23985} "
            + "                                        AND o.value_coded = ${23954} ) "
            + "                                      AND ( o2.concept_id = ${165308} "
            + "                                            AND o2.value_coded = ${1256} "
            + "                                            AND o2.obs_datetime <= "
            + "                                                Date_sub(filt.start_date, "
            + "                                                INTERVAL "
            + "                                                4 month) "
            + "                                          ) ) "
            + "                                     OR ( o3.concept_id = ${1719} "
            + "                                          AND o3.value_coded = ${165307} ) )) "
            + "       AND NOT EXISTS (SELECT p.patient_id "
            + "                       FROM   patient p "
            + "                              INNER JOIN encounter e "
            + "                                      ON e.patient_id = p.patient_id "
            + "                              INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs o2 "
            + "                                      ON e.encounter_id = o2.encounter_id "
            + "                       WHERE  p.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND o2.voided = 0 "
            + "                              AND e.location_id = :location "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND ( ( o.concept_id = ${23985} "
            + "                                      AND o.value_coded = ${23954} ) "
            + "                                    AND ( o2.concept_id = ${165308} "
            + "                                          AND o2.value_coded = ${1256} "
            + "                                          AND o2.obs_datetime <= "
            + "                                              Date_sub(filt.start_date, "
            + "                                              INTERVAL 4 month) ) "
            + "                                  ))";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * <b> Technical Specs <b>
   *
   * <blockquote>
   *
   * <p>1: Select all patients selected in Ficha Resumo - Mastercard (encounter type 53) with Última
   * Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Data Início (concept id
   * 165308 value 1256) occured during the reporting period (obs datetime between start date and end
   * date).
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
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "       AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
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
   * <p>2: Select all patients with Ficha clinica (encounter type 6) with Profilaxia TPT (concept id
   * 23985) value coded INH (concept id 656) and Estado da Profilaxia (concept id 165308) value
   * coded Início (concept id 1256) during the reporting period (obs datetime between start date and
   * end date).
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFichaClinicaProfilaxiaINH() {

    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("BIPT2 - Patients with Profilaxia TPT value INH on Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT     p.patient_id "
            + "        FROM       patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "        AND   e.location_id = :location "
            + "        AND   e.encounter_type = ${6} "
            + "        AND   ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "        AND     ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND       o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
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
   * <p>3: Select all patients with Ficha Seguimento (encounter type 9) with Profilaxia TPT (concept
   * id 23985) value coded INH (concept id 656) and Data Início (concept id 165308 value 1256)
   * occured during the reporting period (obs datetime between start date and end date).
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsIPTStart3WithFichaClinicaOrFichaPediatrica() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "BIPT3 - Patients with Profilaxia TPT value INH and Data Inicio in Ficha Seguimento");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT     p.patient_id "
            + "        FROM       patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "        AND   e.location_id = :location "
            + "        AND   e.encounter_type = ${9} "
            + "        AND   ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "        AND     ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND       o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "        GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b> Technical Specs </b>
   *
   * <blockquote>
   *
   * <p>4: Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida +
   * Piridoxina”) and “Seguimento de Tratamento TPT” with values “Continua” or no value marked on
   * the first pick- up on Ficha de Levantamento de TPT (FILT) during the reporting period as FILT
   * INH Start Date and:
   *
   * <ul>
   *   <li>No other INH values [Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *       ‘Isoniazid + piridoxina’ (concept id in [656, 23982])] marked on FILT in the 7 months
   *       prior to ‘INH Start Date’; and
   *   <li>No other INH Start Dates marked on Ficha resumo (Encounter Type ID 53) Última profilaxia
   *       TPT with value “Isoniazida (INH)” (concept id 23985 value 656) and Data Inicio (obs
   *       datetime for concept 165308 value 1256) selected in the 7 months prior to this FILT INH
   *       Start Date and
   *   <li>No other INH Start Dates marked on Ficha Clinica (Encounter Type ID 6) (Profilaxia TPT
   *       with the value “Isoniazida (INH)” (concept id 23985 value 656) and Estado da Profilaxia
   *       with the value “Inicio (I)” (concept 165308 value 1256) in the 7 months prior to this
   *       FILT INH Start Date and; and
   *   <li>No other INH Start Dates marked on Ficha de Seguimento (Encounter Type ID 9) Profilaxia
   *       TPT with the value “Isoniazida (INH)” (concept id 23985 value 656) and Data Início (obs
   *       datetime for concept 165308 value 1256) in the 7 months prior to this FILT INH Start Date
   * </ul>
   *
   * </blockquote>
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
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "                FROM  patient p "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN (SELECT  p.patient_id, MIN(o2.obs_datetime) start_date "
            + "                                    FROM patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               INNER JOIN obs o2 on o2.encounter_id = e.encounter_id "
            + "               WHERE   p.voided = 0  AND e.voided = 0 "
            + "               AND o.voided = 0  AND e.location_id = :location "
            + "               AND e.encounter_type = ${60} "
            + "               AND ( ( o.concept_id = ${23985}  AND o.value_coded IN (${656}, ${23982}) ) "
            + "               AND   ( o2.concept_id =  ${23987}  AND (o2.value_coded =  ${1257}  OR o2.value_coded IS NULL) ) "
            + "               AND o2.obs_datetime BETWEEN :startDate AND :endDate ) "
            + "                                    GROUP BY p.patient_id ) AS filt "
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
            + "                    AND ( ( o.concept_id = ${23985}  AND o.value_coded = ${656} ) "
            + "                    AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                    AND o2.obs_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND o2.obs_datetime < filt.start_date ) ) "
            + "                    UNION "
            + "                    SELECT p.patient_id FROM patient p "
            + "                    JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                    Join obs o2 ON o2.encounter_id = e.encounter_id "
            + "                    WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND  ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "                    AND    ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                    AND o2.obs_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND o2.obs_datetime < filt.start_date ) ) "
            + "                     UNION "
            + "                    SELECT p.patient_id FROM patient p "
            + "                    JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                    Join obs o2 ON o2.encounter_id = e.encounter_id "
            + "                    WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                    AND e.encounter_type = ${9} "
            + "                    AND  ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "                    AND    ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                    AND o2.obs_datetime >= DATE_SUB(filt.start_date, INTERVAL 7 MONTH) "
            + "                    AND o2.obs_datetime < filt.start_date ) ) )"
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
   * ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Seguimento de tratamento
   * TPT”(concept ID 23987) value coded “inicio” or “re-inicio”(concept ID in [1256, 1705]) marked
   * on FILT (encounter type 60) during the reporting period (between startDate and endDate) (obs
   * datetime for 23987).
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
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "AND   e.location_id = :location "
            + "AND   e.encounter_type = ${60} "
            + "AND   ( ( o.concept_id = ${23985} AND o.value_coded IN( ${656}, ${23982} ) ) "
            + "AND   ( o2.concept_id = ${23987} AND o2.value_coded IN( ${1256}, ${1705} ) "
            + "AND   o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
}
