package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.queries.PatientIdBuilder;
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
    cd.addSearch("A3HP4", EptsReportUtils.map(A3HP4, "endDate=${endDate},location=${location}"));
    cd.addSearch("A3HP5", EptsReportUtils.map(A3HP5, "endDate=${endDate},location=${location}"));
    cd.addSearch("A3HP6", EptsReportUtils.map(A3HP6, "endDate=${endDate},location=${location}"));
    cd.addSearch("A3HP7", EptsReportUtils.map(A3HP7, "endDate=${endDate},location=${location}"));

    cd.setCompositionString("(A3HP3 OR A3HP4 OR A3HP5 OR A3HP6 OR A3HP7)");

    return cd;
  }

  public CohortDefinition getIPTStartCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("B - IPT start 1 or 2 or 4 or 5 Cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition BIPT1 = getPatientsWithFichaResumoUltimaProfilaxia();
    CohortDefinition BIPT2 = getPatientsWithFichaClinicaProfilaxiaINH();
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
        "BIPT4",
        EptsReportUtils.map(
            BIPT4, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "BIPT5",
        EptsReportUtils.map(
            BIPT5, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(BIPT1 OR BIPT2 OR BIPT4 OR BIPT5)");

    return cd;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de
   * tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento de TPT (FILT)
   * during the reporting period or;
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

    String query =
        new PatientIdBuilder(getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values “3HP or 3HP + Piridoxina” and “Seguimento de
   * tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento de TPT (FILT)
   * during the reporting period or;
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT p.patient_id, o.obs_datetime AS tpt_date "
            + "  FROM patient p   "
            + "      INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "      INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "  WHERE p.voided = 0 AND e.voided = 0   "
            + "      AND o.voided = 0   "
            + "      AND e.location_id = :location "
            + "      AND e.encounter_type=  ${60}    "
            + "      AND ( (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954}  , ${23984} ) )   "
            + "      AND   (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} )   "
            + "      AND o.obs_datetime BETWEEN :startDate AND :endDate ) ) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Última Profilaxia TPT with value “3HP” and Data Inicio selected in Ficha
   * Resumo - Mastercard (3HP Start Date) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithUltimaProfilaxia3hp() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP4 - Patients with Ultima Profilaxia on Resumo");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientsWithUltimaProfilaxia3hpDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Última Profilaxia TPT with value “3HP” and Data Inicio selected in Ficha
   * Resumo - Mastercard (3HP Start Date) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWithUltimaProfilaxia3hpDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, o2.obs_datetime AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the
   * value “Inicio (I)” marked on Ficha Clínica – Mastercard (3HP Start Date) during the reporting
   * period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientWithProfilaxiaTpt3hp() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP5 - Patients with Profilaxia TPT on Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientWithProfilaxiaTpt3hpDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the
   * value “Inicio (I)” marked on Ficha Clínica – Mastercard (3HP Start Date) during the reporting
   * period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientWithProfilaxiaTpt3hpDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, o2.obs_datetime AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Outras Prescrições with the value “DT-3HP” marked on Ficha Clínica –
   * Mastercard (3HP Start Date) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithOutrasPerscricoesDT3HP() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP6 - Patients with Outras Prescricoes on Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientsWithOutrasPerscricoesDT3HPDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <p>Patients who have Outras Prescrições with the value “DT-3HP” marked on Ficha Clínica –
   * Mastercard (3HP Start Date) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWithOutrasPerscricoesDT3HPDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, e.encounter_datetime AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <ul>
   *   <li>Patients who have Regime de TPT with the values (3HP or 3HP + Piridoxina) and “Seguimento
   *       de Tratamento TPT” with values “Continua” or “Fim” or no value marked on the first
   *       pick-up on Ficha de Levantamento de TPT (FILT) during the reporting period (3HP FILT
   *       Start Date) and:
   *   <li>No other Regime de TPT = 3HP or 3HP + Piridoxina marked on FILT in the 4 months prior to
   *       the 3HP FILT Start Date and
   *   <li>No other 3HP Start Dates marked on Ficha Clinica (Profilaxia TPT with the value “3HP” and
   *       Estado da Profilaxia with the value “Inicio (I)”) or (Outras Prescrições with the value
   *       DT-3HP”) in the 4 months prior to this FILT 3HP Start Date and
   *   <li>No other 3HP Start Dates marked on Ficha Resumo (Última profilaxia TPT with value “3HP”
   *       and Data Inicio selected in the 4 months prior to this FILT 3HP Start Date:
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithRegimeDeTPT3HP() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("3HP7 - Patients with Regime de TPT & Seg.Trat TPT on FILT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientsWithRegimeDeTPT3HPDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TPT_INI_FR4 : Patients who initiated 3HP therapy</b>
   *
   * <blockquote>
   *
   * <ul>
   *   <li>Patients who have Regime de TPT with the values (3HP or 3HP + Piridoxina) and “Seguimento
   *       de Tratamento TPT” with values “Continua” or “Fim” or no value marked on the first
   *       pick-up on Ficha de Levantamento de TPT (FILT) during the reporting period (3HP FILT
   *       Start Date) and:
   *   <li>No other Regime de TPT = 3HP or 3HP + Piridoxina marked on FILT in the 4 months prior to
   *       the 3HP FILT Start Date and
   *   <li>No other 3HP Start Dates marked on Ficha Clinica (Profilaxia TPT with the value “3HP” and
   *       Estado da Profilaxia with the value “Inicio (I)”) or (Outras Prescrições with the value
   *       DT-3HP”) in the 4 months prior to this FILT 3HP Start Date and
   *   <li>No other 3HP Start Dates marked on Ficha Resumo (Última profilaxia TPT with value “3HP”
   *       and Data Inicio selected in the 4 months prior to this FILT 3HP Start Date:
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWithRegimeDeTPT3HPDate() {

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
        "SELECT p.patient_id, filt.start_date AS tpt_date "
            + "FROM   patient p "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Min(o2.obs_datetime) AS start_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                          INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND o2.voided = 0 "
            + "                          AND e.encounter_type = ${60} "
            + "                          AND e.location_id = :location "
            + "                          AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                AND ( ( o2.concept_id = ${23987} AND ( o2.value_coded IN ( ${1257}, ${1267} ) "
            + "                                               OR o2.value_coded IS NULL ) ) "
            + "                                      AND o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                   GROUP  BY p.patient_id) AS filt ON p.patient_id = filt.patient_id "
            + "                  AND NOT EXISTS (SELECT pp.patient_id, e.encounter_datetime "
            + "                                  FROM   patient pp "
            + "                                         INNER JOIN encounter e ON pp.patient_id = e.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                  WHERE  pp.voided = 0 "
            + "                                         AND e.voided = 0 "
            + "                                         AND o.voided = 0 "
            + "                                         AND pp.patient_id = p.patient_id "
            + "                                         AND e.location_id = :location "
            + "                                         AND e.encounter_type = ${60} "
            + "                                         AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                                         AND e.encounter_datetime >= Date_sub(filt.start_date, INTERVAL 4 month) "
            + "                                         AND e.encounter_datetime < filt.start_date) "
            + "                  AND NOT EXISTS (SELECT pp.patient_id "
            + "                                  FROM   patient pp "
            + "                                         INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                  WHERE  pp.voided = 0 "
            + "                                         AND pp.patient_id = p.patient_id "
            + "                                         AND e.voided = 0 "
            + "                                         AND o.voided = 0 "
            + "                                         AND o2.voided = 0 "
            + "                                         AND e.location_id = :location "
            + "                                         AND e.encounter_type = ${6} "
            + "                                         AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                                               AND ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                                                     AND o2.obs_datetime >= Date_sub(filt.start_date, INTERVAL 4 month) "
            + "                                                     AND o2.obs_datetime < filt.start_date ) ) ) "
            + "                   AND NOT EXISTS (SELECT pp.patient_id "
            + "                                   FROM   patient pp "
            + "                                          INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                   WHERE  pp.voided = 0 "
            + "                                          AND pp.patient_id = p.patient_id "
            + "                                          AND e.voided = 0 "
            + "                                          AND o.voided = 0 "
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND o.concept_id = ${1719} AND o.value_coded = ${165307} "
            + "                                          AND e.encounter_datetime >= Date_sub(filt.start_date, INTERVAL 4 month) "
            + "                                          AND e.encounter_datetime < filt.start_date) "
            + "                 AND NOT EXISTS (SELECT pp.patient_id "
            + "                                 FROM   patient pp "
            + "                                        INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                                        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                 WHERE  pp.voided = 0 "
            + "                                        AND e.voided = 0 "
            + "                                        AND o.voided = 0 "
            + "                                        AND o2.voided = 0 "
            + "                                        AND pp.patient_id = p.patient_id "
            + "                                        AND e.location_id = :location "
            + "                                        AND e.encounter_type = ${53} "
            + "                                        AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                                        AND ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                                        AND o2.obs_datetime >= Date_sub(filt.start_date, INTERVAL 4 month) "
            + "                                        AND o2.obs_datetime < filt.start_date ) ) )";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have (Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio
   * selected in Ficha Resumo - Mastercard during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFichaResumoUltimaProfilaxia() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "BIPT1 - Patients on Ficha Resumo with Ultima profilaxia Isoniazida");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        new PatientIdBuilder(getPatientsWithFichaResumoUltimaProfilaxiaDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have (Última profilaxia TPT with value “Isoniazida (INH)” and Data Inicio
   * selected in Ficha Resumo - Mastercard during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWithFichaResumoUltimaProfilaxiaDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, o2.obs_datetime AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia
   * with the value “Inicio (I)”) marked on Ficha Clínica -– Mastercard during the reporting period
   * or
   *
   * <p>Patients who have (Profilaxia TPT with the value “Isoniazida (INH)” and Data Início)
   * registered in Ficha de Seguimento and occurred during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFichaClinicaProfilaxiaINH() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("BIPT2 - Patients with Profilaxia TPT value INH on Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientsWithFichaClinicaProfilaxiaINHDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia
   * with the value “Inicio (I)”) marked on Ficha Clínica -– Mastercard during the reporting period
   * or
   *
   * <p>Patients who have (Profilaxia TPT with the value “Isoniazida (INH)” and Data Início)
   * registered in Ficha de Seguimento and occurred during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsWithFichaClinicaProfilaxiaINHDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT     p.patient_id, o2.obs_datetime AS tpt_date "
            + "        FROM       patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "        AND   e.location_id = :location "
            + "        AND   e.encounter_type IN ( ${6}, ${9} ) "
            + "        AND   ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "        AND     ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "        AND       o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "        GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”)
   * and “Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-
   * up on Ficha de Levantamento de TPT (FILT) during the reporting period as FILT INH Start Date
   * and:
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

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("BIPT4 - Patients with the First Filt with regime de TPT");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getPatientsWithFirstFiltRegimeTptDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”)
   * and “Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-
   * up on Ficha de Levantamento de TPT (FILT) during the reporting period as FILT INH Start Date
   * and:
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
   * @return {@link String}
   */
  public String getPatientsWithFirstFiltRegimeTptDate() {

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
        "SELECT p.patient_id, filt.start_date AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”)
   * and “Seguimento de tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento
   * de TPT (FILT) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getpatientswithRegimeTPTIsoniazid() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "BIPT5 - Patients with Regime de TPT Isoniazid / Isoniazid + Piridoxina");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = new PatientIdBuilder(getpatientswithRegimeTPTIsoniazidDate()).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b> TPT_INI_FR5 : Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”)
   * and “Seguimento de tratamento TPT” = (‘Inicio’ or ‘Re-Inicio’) marked on Ficha de Levantamento
   * de TPT (FILT) during the reporting period or
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getpatientswithRegimeTPTIsoniazidDate() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, o2.obs_datetime AS tpt_date "
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

    return stringSubstitutor.replace(query);
  }
}
