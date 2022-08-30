package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTInitiationDataDefinitionQueries {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private CommonMetadata commonMetadata;
  private CommonQueries commonQueries;

  @Autowired
  public TPTInitiationDataDefinitionQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      CommonMetadata commonMetadata,
      CommonQueries commonQueries) {

    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.commonMetadata = commonMetadata;
    this.commonQueries = commonQueries;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patient ART Start Date is the oldest date from the set of criterias defined in the common
   * query: 1/1 Patients who initiated ART and ART Start Date as earliest from the following
   * criterias is by End of the period (reporting endDate)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndARTStartDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("3 - ART Start Date  ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    String query = commonQueries.getARTStartDate(true);
    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patient’s birth date information registered in the system should be used to calculate the
   * age of the patient at the end date of reporting period (reporting end date minus birthdate /
   * 365)
   *
   * <p>Patients without birth date information will be considered as unknown age and the
   * corresponding cell in the excel file will be filled with N/A
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndTheirAges() {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("4 - Age");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    String query =
        " SELECT p.patient_id, CASE  WHEN ps.birthdate IS NULL THEN 'N/A'   "
            + " ELSE TIMESTAMPDIFF(YEAR,ps.birthdate,:endDate)   END AS age "
            + " FROM patient p "
            + " INNER JOIN person ps ON p.patient_id = ps.person_id WHERE p.voided=0 AND ps.voided=0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>
   *
   * <ul>
   *   <li>Set value equal to “Grávida” if the patient is female and Pregnant as defined in the
   *       Pregnant Women common query
   *   <li>Common query 8. Pregnant women (during startDate and reporting generation date) * Common
   *       queries
   *   <li>Set value equal to “Lactante” if the patient is female and Breastfeeding as defined in
   *       the Breastfeeding Women common query Common query 7. Breastfeeding women (during
   *       startDate and reporting generation date) * Common queries
   *   <li>If the patient has both states (pregnant and breastfeeding) the most recent one should be
   *       considered. For patients who have both states (pregnant and breastfeeding) marked on the
   *       same day, the system will consider the patient as pregnant.
   * </ul>
   *
   * </blockquote>
   *
   * }
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsThatArePregnantOrBreastfeeding() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("6 - Pregnant and Breastfeeding ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    String query = getPregnantAndBreastfeeding();
    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }

  public String getPregnantAndBreastfeeding() {
    String pregnantWomen = commonQueries.getPregnantWomenAndMostEarliestPregnancyDateQuery();
    String breastfeedingWomen =
        commonQueries.getBreastFeedingWomenAndMostRecentBreastfeedingDateQuery();

    String sql =
        " select patient_id, CASE WHEN (pregnancy_date IS NOT NULL) THEN 'Grávida' END AS pregnant_breastfeeding_value "
            + " FROM ( "
            + pregnantWomen
            + "    ) AS pregnant  "
            + " UNION   "
            + " select patient_id, CASE WHEN (breastfeeding_date IS NOT NULL) THEN 'Lactante' END AS pregnant_breastfeeding_value "
            + " FROM ( "
            + breastfeedingWomen
            + "  ) breastfeeding ";

    return sql;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Date (encounter_datetime) of the most recent clinical consultation registered on Ficha
   * Clínica – MasterCard or Ficha de Seguimento (encounter type 6) until report generation date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndLastFollowUpConsultationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("7 -  Last Follow up Consultation Date ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "       SELECT p.patient_id, MAX(e.encounter_datetime) AS followup_date FROM patient p    "
            + "    JOIN encounter e ON e.patient_id = p.patient_id   "
            + "    WHERE e.encounter_type = ${6}  AND e.location_id = :location   "
            + "    AND e.voided = 0 AND p.voided = 0   "
            + "    AND e.encounter_datetime < curdate()   "
            + "    GROUP BY p.patient_id   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Set value equal to “Sim” if the patient received TPT in the last follow-up consultation Date
   * (field 7 date) as following otherwise set “Não”:
   *
   * <ul>
   *   <li>if patient have Ficha Clinica (encounter type 6) with “Outras Prescricoes” (concept id
   *       1719) value coded “3HP” (concept id 23954) or “DT-INH” (concept id 23955) or DT-3HP
   *       (concept id 165307) in the last follow up consultation date before the report generation
   *       date (same as field 7); or
   *   <li>If patient have Ficha Clinica (encounter_type 6) with “Profilaxia (INH)” (concept id
   *       6122) value coded “Inicio” (concept id 1256) or “Continua” (concept id 1257) or
   *       Profilaxia TPT (concept id 23985) value coded INH or 3HP (concept id in [656, 23954]) and
   *       Estado da Profilaxia (concept id 165308) value coded Início or continua (concept id
   *       [1256, 1257]) in the last follow up consultation date before the report generation date
   *       (same as field 7); or Select all patients with Ficha de Seguimento (encounter type 6)
   *       with “Profilaxia com INH - TPI (Data Inicio) ” (Concept 6128) in the last follow up
   *       consultation date before the reporting end date;
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsReceivedTPTInTheLastFollowUpConsultation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("8 - Received TPT in the last follow-up consultation");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23955", tbMetadata.getDtINHConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "       last_fu_consultation.followup_date AS followup_date "
            + "FROM   patient p "
            + "       JOIN encounter e ON e.patient_id = p.patient_id "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          MAX(e.encounter_datetime) AS followup_date "
            + "                   FROM   patient p "
            + "                          JOIN encounter e ON e.patient_id = p.patient_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND e.location_id = :location "
            + "                          AND e.voided = 0 "
            + "                          AND p.voided = 0 "
            + "                          AND e.encounter_datetime < CURRENT_DATE() "
            + "                   GROUP  BY p.patient_id) last_fu_consultation "
            + "               ON last_fu_consultation.patient_id = p.patient_id "
            + "WHERE  e.encounter_datetime = last_fu_consultation.followup_date "
            + "       AND e.encounter_type = ${6} "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = ${1719} "
            + "       AND o.voided = 0 "
            + "       AND o.value_coded IN ( ${23954}, ${23955}, ${165307} ) "
            + "UNION "
            + "SELECT p.patient_id, "
            + "       last_fu_consultation.followup_date AS followup_date "
            + "FROM   patient p "
            + "       JOIN encounter e ON e.patient_id = p.patient_id "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS followup_date "
            + "                   FROM   patient p "
            + "                          JOIN encounter e "
            + "                            ON e.patient_id = p.patient_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND e.location_id = :location "
            + "                          AND e.voided = 0 "
            + "                          AND p.voided = 0 "
            + "                          AND e.encounter_datetime < CURRENT_DATE() "
            + "                   GROUP  BY p.patient_id) last_fu_consultation "
            + "               ON last_fu_consultation.patient_id = p.patient_id "
            + "WHERE  e.encounter_datetime = last_fu_consultation.followup_date "
            + "       AND e.encounter_type = ${6} "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND ( o.concept_id = ${6122} AND o.value_coded IN ( ${1256}, ${1257} ) ) "
            + "        OR ( ( o2.concept_id = ${23985} AND o2.value_coded IN ( ${656}, ${23954} ) ) "
            + "                 AND ( o3.concept_id = ${165308} AND o3.value_coded IN ( ${1256}, ${1257} ) ) ) "
            + "UNION "
            + "SELECT p.patient_id, "
            + "       last_fu_consultation.followup_date AS followup_date "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON e.patient_id = p.patient_id "
            + "       JOIN obs o "
            + "         ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS followup_date "
            + "                   FROM   patient p "
            + "                          JOIN encounter e "
            + "                            ON e.patient_id = p.patient_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND e.location_id = :location "
            + "                          AND e.voided = 0 "
            + "                          AND p.voided = 0 "
            + "                          AND e.encounter_datetime < CURRENT_DATE() "
            + "                   GROUP  BY p.patient_id) last_fu_consultation "
            + "               ON last_fu_consultation.patient_id = p.patient_id "
            + "WHERE  e.encounter_datetime = last_fu_consultation.followup_date "
            + "       AND e.encounter_type = ${6} "
            + "       AND e.voided = 0 "
            + "       AND o.concept_id = ${6128} "
            + "       AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The earliest date (encounter_datetime) on FILT (encounter type 60) with “Regime de TPT”
   * (concept id 23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   * encounter datetime between start date and end date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientAnd3HPInitiationDateOnFILT() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("10 - 3HP Initiation Dates - On FILT ");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        "           SELECT  p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime   "
            + "                FROM   patient p   "
            + "         INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                WHERE   p.voided = 0 AND e.voided = 0 "
            + "         AND o.voided = 0   "
            + "         AND e.encounter_type = ${60}  "
            + "         AND e.location_id = :location "
            + "         AND o.concept_id = ${23985} "
            + "         AND o.value_coded IN ( ${23954}  ,  ${23984} ) "
            + "         AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "         GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The earliest date (encounter_datetime) on Ficha Clinica - Master Card (encounter type 6)
   * with “Outras prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id 23954)
   * or Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   * Profilaxia (concept id 165308) value coded Início (concept id 1256) and encounter datetime
   * between start date and end date; </>
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientAnd3HPInitiationDateOnFichaClinica() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("11 - 3HP Initiation Dates - on Ficha Clínica ");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "       MIN(e.encounter_datetime) AS earliest_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "       OR  ( (o2.concept_id = ${23985} AND o2.value_coded = ${23954}) "
            + "             AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) ) "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "GROUP  BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs<b>
   *
   * <blockquote>
   *
   * <p>Última profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início value
   * datetime selected in Ficha Resumo - MasterCard (encounter type 53) by reporting end date.
   *
   * <p>
   *
   * <p>Note: if there is more than one Ficha Resumo then information from all Ficha Resumo should
   * be included. </bloackquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition get3HPInitiationDateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("12 - 3HP Initiation Date On FIcha Resumo ");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${53} "
            + "       AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "       AND o.value_datetime <= :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Most recent FILT (encounter type 60) that has Regime de TPT (concept id 23985) with the
   * values (3HP or 3HP + Piridoxina) (concept id in [23954, 23984]) marked until the report
   * generation dat
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAnd3HPDispensationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("13 - FILT with 3HP Dispensation - Date ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        "    SELECT  p.patient_id,  MAX(e.encounter_datetime) AS encounter_datetime   "
            + "                FROM  patient p   "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                WHERE   p.voided = 0 AND e.voided = 0   "
            + "         AND o.voided = 0 "
            + "         and e.encounter_type=  ${60} "
            + "         and e.location_id = :location "
            + "         and o.concept_id=  ${23985}  "
            + "         and o.value_coded in ( ${23954} ,  ${23984} ) "
            + "         and e.encounter_datetime <= curdate() "
            + "         GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Tipo de Dispensa (concept id 23986) with value coded Mensal or Trimestral (concept id IN
   * [1098, 23720]) on the most recent FILT (encounter type 60) that has Regime de TPT (concept id
   * 23985) with the values (3HP or 3HP + Piridoxina) (concept id in [23954, 23984]) marked until
   * the report generation date
   *
   * <p>Note: If a patient has more than one FILTs registered on the same most recent date the
   * system will show information from the most recently entered FILT in the system on that specific
   * day.
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndLast3HPTypeOfDispensation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "14 - Last FILT with 3HP Dispensation - Type of Dispensation ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23986", tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("1098", hivMetadata.getMonthlyConcept().getConceptId());
    valuesMap.put("23720", hivMetadata.getQuarterlyConcept().getConceptId());

    String query =
        "        SELECT p.patient_id, o.value_coded AS dispensation_type FROM patient p   "
            + "                JOIN  encounter e ON e.patient_id = p.patient_id   "
            + "                JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "                JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) AS recent_filt "
            + "                FROM  patient p   "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN  obs o ON o.encounter_id = e.encounter_id WHERE  p.voided = 0 AND e.voided = 0  AND o.voided = 0 "
            + "         AND e.encounter_type = ${60} "
            + "         AND o.concept_id = ${23985} "
            + "         AND o.value_coded IN ( ${23954},${23984} ) "
            + "         AND e.location_id = :location "
            + "         AND e.encounter_datetime <= CURDATE() "
            + "         GROUP BY p.patient_id "
            + "         ) AS latest_filt ON latest_filt.patient_id = p.patient_id   "
            + "         WHERE latest_filt.recent_filt = e.encounter_datetime "
            + "         AND o.concept_id =  ${23986} "
            + "         AND o.value_coded IN ( ${1098} , ${23720} ) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs<b>
   *
   * <blockquote>
   *
   * <p>Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and Estado da
   * Profilaxia (concept id 165308) value coded Fim (concept id 1267) registered on ficha
   * clinica(encounter type 6) encounter datetime between the 3HP start date and Report generation
   * date
   *
   * <p>Note: if more than one Ficha Clínica exists the system should consider the most recent date
   * amongst the sources
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition get3HPCompletionDateFichaClínica() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("15 - 3HP Completion Date On FIcha Clínica");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "       Max(e.encounter_datetime) AS completion_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "       INNER JOIN(SELECT p.patient_id, "
            + "                         Min(e.encounter_datetime) AS start_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                  WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                         AND e.encounter_type = ${60} "
            + "                         AND e.location_id = :location "
            + "                         AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                  GROUP  BY p.patient_id "
            + "                  UNION "
            + "                  SELECT p.patient_id, "
            + "                         Min(e.encounter_datetime) AS start_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                         INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                  WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_type = ${6} "
            + "                         AND ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "                          OR ( (o2.concept_id = ${23985} AND o2.value_coded = ${23954}) "
            + "                               AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) )"
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                  GROUP  BY p.patient_id "
            + "                  UNION "
            + "                  SELECT p.patient_id, "
            + "                         Min(o2.value_datetime) AS start_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                  WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_type = ${53} "
            + "                         AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                         AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                  GROUP  BY p.patient_id) 3hp ON 3hp.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "       AND o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "       AND e.encounter_datetime BETWEEN 3hp.start_date AND CURRENT_DATE() "
            + "GROUP  BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs<b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Última profilaxia(concept id 23985) value coded 3HP(concept id
   * 23954) and Data Fim da Profilaxia TPT(value datetime, concept id 6129) registered on Ficha
   * Resumo (Encounter type 53) with value datetime between the 3HP start date and Report generation
   * date
   *
   * <p>The system will determine the most recent from these sources as the 3HP End Date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition get3HPCompletionDateonFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("16 - 3HP Completion Date On FIcha Resumo");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put(
        "6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "                    o2.value_datetime AS recent_date "
            + "             FROM   patient p "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                    INNER JOIN(SELECT  p.patient_id, "
            + "                    MIN(e.encounter_datetime) AS start_date "
            + "                    FROM   patient p "
            + "                    INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                    INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                        WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                    AND e.encounter_type = ${60} "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = ${23985} "
            + "                    AND o.value_coded IN ( ${23954}  , ${23984} ) "
            + "                    AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                    GROUP BY p.patient_id "
            + "                    UNION "
            + "                    SELECT p.patient_id, "
            + "                               MIN(e.encounter_datetime) AS start_date "
            + "                        FROM   patient p "
            + "                               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                               INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                               INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                        WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                               AND e.location_id = :location "
            + "                               AND e.encounter_type = ${6} "
            + "                               AND ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "                                OR ( (o2.concept_id = ${23985} AND o2.value_coded = ${23954}) "
            + "                                     AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) ) "
            + "                               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                        GROUP  BY p.patient_id "
            + "                    UNION "
            + "                    SELECT p.patient_id, "
            + "                              MIN(o2.value_datetime) AS start_date "
            + "                       FROM   patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                              INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                       WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                              AND e.location_id = :location "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                              AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                       GROUP BY p.patient_id) 3hp ON 3hp.patient_id = p.patient_id "
            + "             WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${53} "
            + "                    AND o.concept_id = ${23985} "
            + "                    AND o.value_coded = ${23954} "
            + "                    AND o2.concept_id = ${6129} "
            + "                    AND o2.value_datetime BETWEEN 3hp.start_date AND CURRENT_DATE() "
            + "             GROUP  BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b> Technical Specs <b>
   *
   * <blockquote>
   *
   * <p>Expected 3HP Completion Date = 3HP Start Date + 86 days Only for patients who initiated
   * 3HP(patients from A)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getExpected3HPCompletionDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("17 - Expected 3HP Completion Date");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        "SELECT patient_id, "
            + "       DATE_ADD(MIN(consultation_date), INTERVAL 86 DAY) as expected_date "
            + "       FROM( "
            + "           SELECT  p.patient_id, MIN(e.encounter_datetime) AS consultation_date "
            + "              FROM   patient p "
            + "                     INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                     INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "              WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                     AND e.encounter_type = ${60} "
            + "                     AND e.location_id = :location "
            + "                     AND o.concept_id = ${23985} "
            + "                     AND o.value_coded IN ( ${23954}  ,  ${23984} ) "
            + "                     AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                     GROUP BY p.patient_id "
            + "       UNION "
            + "              SELECT p.patient_id, MIN(e.encounter_datetime) AS consultation_date "
            + "              FROM patient p "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                    INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "              WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "                      OR ( (o2.concept_id = ${23985} AND o2.value_coded = ${23954}) "
            + "                           AND (o3.concept_id = ${165308} AND o3.value_coded = ${1256}) ) "
            + "                     AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                     GROUP BY p.patient_id "
            + "       UNION "
            + "              SELECT p.patient_id, MIN(o2.value_datetime) AS consultation_date "
            + "              FROM patient p "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "               WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${53} "
            + "                    AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                    AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                    GROUP BY p.patient_id) union_tbl "
            + "WHERE union_tbl.patient_id IN ( "
            + "    SELECT p.patient_id "
            + "        FROM  patient p "
            + "        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "        INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "            FROM patient p "
            + "            INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "            INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "            WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.location_id = :location "
            + "                AND e.encounter_type =   ${6} AND o.concept_id =  ${1719} "
            + "                AND o.value_coded IN ( ${23954} ) AND e.encounter_datetime >= :startDate "
            + "                AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) AS pickup "
            + "            ON pickup.patient_id = p.patient_id "
            + "    WHERE p.patient_id NOT IN ( "
            + "        SELECT patient_id "
            + "        FROM patient p "
            + "        WHERE p.voided = 0   AND e.voided = 0 "
            + "            AND o.voided = 0   AND e.location_id = :location "
            + "            AND e.encounter_type =   ${6}  AND o.concept_id =   ${1719} "
            + "            AND o.value_coded IN ( ${23954} ) "
            + "            AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
            + "            AND e.encounter_datetime < pickup.first_pickup_date "
            + "        UNION "
            + "        SELECT p.patient_id "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                WHERE p.voided = 0 AND e.voided = 0  AND o.voided = 0 AND e.encounter_type=  ${60} "
            + "                    AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954} , ${23984} )) "
            + "                    AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
            + "                    AND e.encounter_datetime < pickup.first_pickup_date) "
            + "UNION "
            + "    SELECT p.patient_id "
            + "    FROM patient p "
            + "        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "   WHERE p.voided = 0 AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.encounter_type=  ${60} "
            + "        AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954}  , ${23984} )) "
            + "        AND (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} )) "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP BY p.patient_id "
            + "UNION "
            + "    SELECT p.patient_id "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "            AND e.location_id = :location "
            + "            AND e.encounter_type = ${53} "
            + "            AND ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "            AND ( o2.concept_id = ${6128} AND o2.value_datetime BETWEEN :startDate AND :endDate ) "
            + "UNION "
            + "    SELECT p.patient_id "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "            AND e.location_id = :location "
            + "            AND e.encounter_type = ${6} "
            + "            AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "            AND o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "            AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "UNION "
            + "    SELECT p.patient_id "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "            AND e.location_id = :location "
            + "            AND e.encounter_type = ${6} "
            + "            AND o.concept_id = ${1719} "
            + "            AND o.value_coded = ${165307} "
            + "            AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "UNION "
            + "    SELECT p.patient_id "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "            AND e.location_id = :location "
            + "            AND e.encounter_type = ${60} "
            + "            AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "            AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1257}, ${1267} ) OR o2.value_coded IS NULL ) "
            + "            AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "            AND p.patient_id NOT IN ( "
            + "                SELECT p.patient_id "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN (SELECT p.patient_id, "
            + "                        Min(e.encounter_datetime) AS start_date "
            + "                        FROM patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                        WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                            AND e.encounter_type = ${60} "
            + "                            AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                            AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                            AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                        ON filt.patient_id = p.patient_id "
            + "                    WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${60} "
            + "                        AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                        AND e.encounter_datetime >= Date_sub(filt.start_date, interval 4 month) "
            + "                        GROUP  BY p.patient_id "
            + "            UNION "
            + "                SELECT p.patient_id "
            + "                FROM   patient p "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                    INNER JOIN obs o4 ON e.encounter_id = o4.encounter_id "
            + "                    INNER JOIN obs o5 ON e.encounter_id = o5.encounter_id "
            + "                    INNER JOIN (SELECT p.patient_id, "
            + "                        Min(e.encounter_datetime) AS start_date "
            + "                        FROM   patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                        WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                            AND e.encounter_type = ${60} "
            + "                            AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                            AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                            AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                            ON filt.patient_id = p.patient_id "
            + "                    WHERE  p.voided = 0 AND e.voided = 0 AND o3.voided = 0 AND o4.voided = 0 AND o5.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${6} "
            + "                        AND ( o3.concept_id = ${23985} AND o3.value_coded = ${23954} "
            + "                              AND o4.concept_id = ${165308} AND o4.value_coded = ${1256} ) "
            + "                        OR  ( o5.concept_id = ${1719} AND o5.value_coded IN ( ${23954}, ${165307} ) ) "
            + "                        AND e.encounter_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "                        GROUP  BY p.patient_id "
            + "            UNION "
            + "                SELECT p.patient_id "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o6 ON e.encounter_id = o6.encounter_id "
            + "                    INNER JOIN obs o7 ON e.encounter_id = o7.encounter_id "
            + "                    INNER JOIN (SELECT p.patient_id, "
            + "                        Min(e.encounter_datetime) AS start_date "
            + "                        FROM patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                        WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                            AND e.encounter_type = ${60} "
            + "                            AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                            AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                            AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                            ON filt.patient_id = p.patient_id "
            + "                    WHERE p.voided = 0 AND e.voided = 0 AND o6.voided = 0 AND o7.voided = 0 "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_type = ${53} "
            + "                        AND o6.concept_id = ${23985} AND o6.value_coded = ${23954} "
            + "                        AND o7.concept_id = ${6128} AND o7.value_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "                        GROUP BY p.patient_id)) "
            + "        GROUP BY patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>18 - Difference between Registered vs Expected 3HP Completion Date – Sheet 1: Column Q
   * Difference between Registered vs Expected 3HP Completion Date (In Number of Days) = 3HP End
   * Date (column N or O) – Expected Completion Date (column P)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getDifferencebetweenRegisteredvsExpected3HPCompletionDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "18 -  Difference between Registered vs Expected 3HP Completion Date");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put(
        "6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        "SELECT p.patient_id, DATEDIFF(MAX(union_tbl.encounter_datetime), tbl_17.expected_date) AS result FROM patient p "
            + "                 JOIN ( "
            + "                    SELECT p.patient_id, "
            + "                    Max(e.encounter_datetime) AS encounter_datetime "
            + "             FROM   patient p "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                    INNER JOIN(SELECT p.patient_id, "
            + "                                      Min(e.encounter_datetime) AS start_date "
            + "                               FROM   patient p "
            + "                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                               WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                      AND e.encounter_type = ${60} "
            + "                                      AND e.location_id = :location "
            + "                                      AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                                      AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                               GROUP  BY p.patient_id "
            + "                               UNION "
            + "                               SELECT p.patient_id, "
            + "                                      Min(e.encounter_datetime) AS start_date "
            + "                               FROM   patient p "
            + "                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                      INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                      INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                               WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                                      AND e.location_id = :location "
            + "                                      AND e.encounter_type = ${6} "
            + "                                      AND ( ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "                                             OR ( o2.concept_id = ${23985} AND o2.value_coded = ${23954} ) ) "
            + "                                      AND o3.concept_id = ${165308} AND o3.value_coded = ${1256} "
            + "                                      AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                               GROUP  BY p.patient_id "
            + "                               UNION "
            + "                               SELECT p.patient_id, "
            + "                                      Min(o2.value_datetime) AS start_date "
            + "                               FROM   patient p "
            + "                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                      INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                               WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                      AND e.location_id = :location "
            + "                                      AND e.encounter_type = ${53} "
            + "                                      AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                                      AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                               GROUP  BY p.patient_id) 3hp ON 3hp.patient_id = p.patient_id "
            + "             WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                    AND o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "                    AND e.encounter_datetime BETWEEN 3hp.start_date AND CURRENT_DATE() "
            + "             GROUP  BY p.patient_id "
            + "             UNION "
            + "             SELECT p.patient_id, "
            + "                                 Max(o2.value_datetime) AS encounter_datetime "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                 INNER JOIN(SELECT  p.patient_id, "
            + "                                 MIN(e.encounter_datetime) AS start_date "
            + "                                 FROM   patient p "
            + "                                 INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                                     WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                 AND e.encounter_type = ${60} "
            + "                                 AND e.location_id = :location "
            + "                                 AND o.concept_id = ${23985} "
            + "                                 AND o.value_coded IN ( ${23954}  , ${23984} ) "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                 GROUP BY p.patient_id "
            + "                                 UNION "
            + "                                 SELECT p.patient_id, "
            + "                                            MIN(e.encounter_datetime) AS start_date "
            + "                                     FROM   patient p "
            + "                                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                            INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                                     WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_type = ${6} "
            + "                                            AND ( ((o.concept_id = ${1719}) AND (o.value_coded = ${23954})) "
            + "                                            OR ((o2.concept_id = ${23985}) AND (o2.value_coded = ${23954})) ) "
            + "                                                AND o3.concept_id = ${165308} AND o3.value_coded = ${1256} "
            + "                                                AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                     GROUP  BY p.patient_id "
            + "                                 UNION "
            + "                                 SELECT p.patient_id, "
            + "                                           MIN(o2.value_datetime) AS start_date "
            + "                                    FROM   patient p "
            + "                                           INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                           INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                           INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                    WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                           AND e.location_id = :location "
            + "                                           AND e.encounter_type = ${53} "
            + "                                           AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                                           AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                                    GROUP BY p.patient_id) 3hp ON 3hp.patient_id = p.patient_id "
            + "                          WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${53} "
            + "                                 AND o.concept_id = ${23985} "
            + "                                 AND o.value_coded = ${23954} "
            + "                                 AND o2.concept_id = ${6129} "
            + "                                 AND o2.value_datetime BETWEEN 3hp.start_date AND CURRENT_DATE() "
            + "                          GROUP  BY p.patient_id) union_tbl "
            + "                          ON union_tbl.patient_id = p.patient_id "
            + "                          LEFT JOIN "
            + "                          ( "
            + "                            SELECT patient_id, "
            + "                            DATE_ADD(MIN(consultation_date), INTERVAL 86 DAY) as expected_date "
            + "                            FROM( "
            + "                                SELECT  p.patient_id, MIN(e.encounter_datetime) AS consultation_date "
            + "                                   FROM   patient p "
            + "                                          INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                                          INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                                   WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                          AND e.encounter_type = ${60} "
            + "                                          AND e.location_id = :location "
            + "                                          AND o.concept_id = ${23985} "
            + "                                          AND o.value_coded IN ( ${23954}  ,  ${23984} ) "
            + "                                          AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                          GROUP BY p.patient_id "
            + "                            UNION "
            + "                                   SELECT p.patient_id, MIN(e.encounter_datetime) AS consultation_date "
            + "                                   FROM patient p "
            + "                                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                         INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                                   WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                                          AND e.location_id = :location "
            + "                                          AND e.encounter_type = ${6} "
            + "                                          AND ( ( o.concept_id = ${1719} AND o.value_coded = ${23954} ) "
            + "                                               OR ( o2.concept_id = ${23985} AND o2.value_coded = ${23954} ) ) "
            + "                                          AND o3.concept_id = ${165308} AND o3.value_coded = ${1256} "
            + "                                          AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                          GROUP BY p.patient_id "
            + "                            UNION "
            + "                                   SELECT p.patient_id, MIN(o2.value_datetime) AS consultation_date "
            + "                                   FROM patient p "
            + "                                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                    WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                         AND e.location_id = :location "
            + "                                         AND e.encounter_type = ${53} "
            + "                                         AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                                         AND o2.concept_id = ${6128} AND o2.value_datetime < CURRENT_DATE() "
            + "                                         GROUP BY p.patient_id) union_tbl "
            + "                                             WHERE union_tbl.patient_id IN ( "
            + "                                                 SELECT p.patient_id "
            + "                             FROM  patient p "
            + "                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                             INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                             INNER JOIN (SELECT  p.patient_id, MIN(e.encounter_datetime) first_pickup_date "
            + "                                 FROM patient p "
            + "                                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                 WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.location_id = :location "
            + "                                     AND e.encounter_type =   ${6} AND o.concept_id =  ${1719} "
            + "                                     AND o.value_coded IN ( ${23954} ) AND e.encounter_datetime >= :startDate "
            + "                                     AND e.encounter_datetime <= :endDate GROUP BY p.patient_id) AS pickup "
            + "                                 ON pickup.patient_id = p.patient_id "
            + "                         WHERE p.patient_id NOT IN ( "
            + "                             SELECT patient_id "
            + "                             FROM patient p "
            + "                             WHERE p.voided = 0   AND e.voided = 0 "
            + "                                 AND o.voided = 0   AND e.location_id = :location "
            + "                                 AND e.encounter_type =   ${6}  AND o.concept_id =   ${1719} "
            + "                                 AND o.value_coded IN ( ${23954} ) "
            + "                                 AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
            + "                                 AND e.encounter_datetime < pickup.first_pickup_date "
            + "                             UNION "
            + "                             SELECT p.patient_id "
            + "                             FROM patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                     WHERE p.voided = 0 AND e.voided = 0  AND o.voided = 0 AND e.encounter_type=  ${60} "
            + "                                         AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954} , ${23984} )) "
            + "                                         AND e.encounter_datetime >= DATE_SUB(pickup.first_pickup_date, INTERVAL 4 MONTH) "
            + "                                         AND e.encounter_datetime < pickup.first_pickup_date) "
            + "                     UNION "
            + "                         SELECT p.patient_id "
            + "                         FROM patient p "
            + "                             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                        WHERE p.voided = 0 AND e.voided = 0 "
            + "                             AND o.voided = 0 "
            + "                             AND e.encounter_type=  ${60} "
            + "                             AND (o.concept_id=  ${23985}   AND o.value_coded IN ( ${23954}  , ${23984} )) "
            + "                             AND (o2.concept_id=  ${23987}   AND o2.value_coded IN ( ${1256} , ${1705} )) "
            + "                             AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                             GROUP BY p.patient_id "
            + "                     UNION "
            + "                         SELECT p.patient_id "
            + "                             FROM patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${53} "
            + "                                 AND ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                                 AND ( o2.concept_id = ${6128} AND o2.value_datetime BETWEEN :startDate AND :endDate ) "
            + "                     UNION "
            + "                         SELECT p.patient_id "
            + "                             FROM patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${6} "
            + "                                 AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "                                 AND o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                     UNION "
            + "                         SELECT p.patient_id "
            + "                             FROM patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${6} "
            + "                                 AND o.concept_id = ${1719} "
            + "                                 AND o.value_coded = ${165307} "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                     UNION "
            + "                         SELECT p.patient_id "
            + "                             FROM patient p "
            + "                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${60} "
            + "                                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1257}, ${1267} ) OR o2.value_coded IS NULL ) "
            + "                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                 AND p.patient_id NOT IN ( "
            + "                                     SELECT p.patient_id "
            + "                                     FROM patient p "
            + "                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                         INNER JOIN (SELECT p.patient_id, "
            + "                                             Min(e.encounter_datetime) AS start_date "
            + "                                             FROM patient p "
            + "                                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                             WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                                 AND e.encounter_type = ${60} "
            + "                                                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                                             ON filt.patient_id = p.patient_id "
            + "                                         WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.encounter_type = ${60} "
            + "                                             AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                                             AND e.encounter_datetime >= Date_sub(filt.start_date, interval 4 month) "
            + "                                             GROUP  BY p.patient_id "
            + "                                 UNION "
            + "                                     SELECT p.patient_id "
            + "                                     FROM   patient p "
            + "                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                         INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                                         INNER JOIN obs o4 ON e.encounter_id = o4.encounter_id "
            + "                                         INNER JOIN obs o5 ON e.encounter_id = o5.encounter_id "
            + "                                         INNER JOIN (SELECT p.patient_id, "
            + "                                             Min(e.encounter_datetime) AS start_date "
            + "                                             FROM   patient p "
            + "                                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                             WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                                 AND e.encounter_type = ${60} "
            + "                                                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                                                 ON filt.patient_id = p.patient_id "
            + "                                         WHERE  p.voided = 0 AND e.voided = 0 AND o3.voided = 0 AND o4.voided = 0 AND o5.voided = 0 "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.encounter_type = ${6} "
            + "                                             AND ( o3.concept_id = ${23985} AND o3.value_coded = ${23954} "
            + "                                                   AND o4.concept_id = ${165308} AND o4.value_coded = ${1256} ) "
            + "                                             OR  ( o5.concept_id = ${1719} AND o5.value_coded IN ( ${23954}, ${165307} ) ) "
            + "                                             AND e.encounter_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "                                             GROUP  BY p.patient_id "
            + "                                 UNION "
            + "                                     SELECT p.patient_id "
            + "                                     FROM patient p "
            + "                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                         INNER JOIN obs o6 ON e.encounter_id = o6.encounter_id "
            + "                                         INNER JOIN obs o7 ON e.encounter_id = o7.encounter_id "
            + "                                         INNER JOIN (SELECT p.patient_id, "
            + "                                             Min(e.encounter_datetime) AS start_date "
            + "                                             FROM patient p "
            + "                                                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                 INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                             WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                                 AND e.encounter_type = ${60} "
            + "                                                 AND ( o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) ) "
            + "                                                 AND ( o2.concept_id = ${23987} AND o2.value_coded IN ( ${1256}, ${1705} ) ) "
            + "                                                 AND e.encounter_datetime BETWEEN :startDate AND :endDate) filt "
            + "                                                 ON filt.patient_id = p.patient_id "
            + "                                         WHERE p.voided = 0 AND e.voided = 0 AND o6.voided = 0 AND o7.voided = 0 "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.encounter_type = ${53} "
            + "                                             AND o6.concept_id = ${23985} AND o6.value_coded = ${23954} "
            + "                                             AND o7.concept_id = ${6128} AND o7.value_datetime <= Date_sub(filt.start_date, interval 4 month) "
            + "                                             GROUP BY p.patient_id)) "
            + "                             GROUP BY patient_id)tbl_17 "
            + "                             ON tbl_17.patient_id = p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The earliest IPT drug pick-up date registered on FILT (encounter type 60) during the
   * reporting period with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or ‘Isoniazid
   * + piridoxina’ (concept id in [656, 23982]) and encounter datetime between start date and end
   * date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTInitiationDateOnFilt() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("19 - IPT Initiation Date - On FILT ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());

    String query =
        "  SELECT  p.patient_id, MIN(e.encounter_datetime) AS pickup_date "
            + "                FROM  patient p "
            + "         INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN   obs o ON e.encounter_id = o.encounter_id "
            + "                WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "         AND e.encounter_type = ${60} "
            + "         AND o.concept_id = ${23985}   AND e.location_id = :location   "
            + "         AND o.value_coded IN (${656},${23982}) "
            + "         AND e.encounter_datetime BETWEEN :startDate AND  :endDate "
            + "                GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The earliest IPT initiation date registered on Ficha Clínica or Ficha de Seguimento
   * (encounter type 6, 9) during the reporting period with following concepts:
   *
   * <ul>
   *   <li>with “Profilaxia INH” (concept id 6122) with value code “Inicio” (concept id 1256) and
   *       encounter datetime between start date and end date
   *   <li>Profilaxia TPT (concept id 23985) value coded INH (concept id 656) and Estado da
   *       Profilaxia (concept id 165308) value coded Início (concept id 1256) or
   *   <li>with “Profilaxia com INH” (concept id 6128) and value datetime is not null and between
   *       start date and end date
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTInitiationDateOnFichaClinicaOrFichaSeguimento() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "20 - IPT Initiation Date - on Ficha Clínica or Ficha de Seguimento ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "              CASE  WHEN o.concept_id = ${6122}  THEN MIN(e.encounter_datetime) "
            + "                  WHEN o1.concept_id = ${23985} AND o2.concept_id = ${165308} THEN MIN(e.encounter_datetime) "
            + "                  WHEN o.concept_id = ${6128}  THEN MIN(o.value_datetime) "
            + "              END AS initiation_date "
            + "            FROM patient p "
            + "                  INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                  INNER JOIN obs o1 ON e.encounter_id = o1.encounter_id "
            + "                  INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "           WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o1.voided = 0 "
            + "                  AND o2.voided = 0 AND e.location_id = :location "
            + "                  AND e.encounter_type IN (${6} , ${9}) "
            + "                  AND ((o.concept_id = ${6122} AND o.value_coded = ${1256} AND e.encounter_datetime BETWEEN :startDate AND  :endDate) "
            + "                  OR ((o1.concept_id = ${23985} AND o1.value_coded = ${656} "
            + "                  AND o2.concept_id = ${165308} AND o2.value_coded = ${1256})) "
            + "                  OR (o.concept_id = ${6128} AND o.value_datetime IS NOT NULL AND o.value_datetime BETWEEN :startDate AND  :endDate)) "
            + "           GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The earliest “Ultima profilaxia Isoniazida (Data Inicio)” (concept id 6128) registered on
   * Ficha Resumo (encounter type 53) and value datetime not null and between start date and end
   * date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTInitiationDateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("21 - IPT Initiation Date -on Ficha Resumo ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "        Min(o.value_datetime) AS encounter_datetime "
            + " FROM   patient p "
            + "        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "        INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + " WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_type = ${53} "
            + "        AND o.value_datetime IS NOT NULL "
            + "        AND o.concept_id = ${6128} "
            + "        AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "        OR ( (o2.concept_id = ${23985} AND o2.value_coded = ${656} ) "
            + "              AND ( o3.concept_id = ${6128} AND o3.value_datetime <= CURRENT_DATE() ) ) "
            + "        AND p.patient_id NOT IN(SELECT p.patient_id "
            + "                                FROM   patient p "
            + "                                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                       AND e.location_id = :location "
            + "                                       AND e.encounter_type = ${53} "
            + "                                       AND o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + " GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The most recent FILT (encounter type 60) that has Regime de TPT (concept id 23985) with the
   * values (Isoniazida or Isoniazida + Piridoxina) (concept id in [656, 23982]) marked until the
   * report generation date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndDateOfLastFILTDispensationWithIPT() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("22 - Last FILT Dispensation with IPT - Date ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());

    String query =
        "                SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime   "
            + "                    FROM patient p   "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                    WHERE e.encounter_type = ${60}  AND p.voided = 0  "
            + "             AND e.voided = 0 "
            + "             AND o.voided = 0 "
            + "             AND o.concept_id = ${23985} "
            + "             AND o.value_coded IN (${656},${23982}) "
            + "             AND e.location_id = :location "
            + "             AND e.encounter_datetime <= CURDATE() GROUP BY p.patient_id   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Value of Tipo de Dispensa (Mensal or Trimestral) on the most recent FILT (encounter type 60)
   * that has Regime de TPT (concept id 23985) with the values(Isoniazida or Isoniazida +
   * Piridoxina) (concept id in [656, 23982]) marked until the report generation date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndTypeOfDispensationInLastFILTDispensationWithIPT() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("23 - Last FILT Dispensation with IPT Type of Dispensation ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23986", tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId());
    valuesMap.put("1098", hivMetadata.getMonthlyConcept().getConceptId());
    valuesMap.put("23720", hivMetadata.getQuarterlyConcept().getConceptId());

    String query =
        " SELECT   p.patient_id, o.value_coded AS dispensation_type FROM patient p "
            + "                JOIN encounter e ON e.patient_id = p.patient_id "
            + "                JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime "
            + "                    FROM patient p   "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    WHERE e.encounter_type = ${60} AND p.voided = 0  "
            + "             AND e.voided = 0 "
            + "             AND o.voided = 0  "
            + "             AND o.concept_id = ${23985} "
            + "             AND o.value_coded IN (${656},${23982}) "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime <= CURDATE() "
            + "                    GROUP BY p.patient_id ) AS latest_filt   "
            + "         ON latest_filt.patient_id = p.patient_id "
            + "         WHERE  latest_filt.encounter_datetime = e.encounter_datetime "
            + "         AND o.concept_id =  ${23986}  AND o.value_coded IN ( ${1098} , ${23720} )   ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The most recent date from the following criterias:
   *
   * <p>Profilaxia (INH) (Concept ID 6122) marked with the value Fim (Concept ID 1267) or Profilaxia
   * TPT (concept id 23985) value coded INH (concept id 656) and Estado da Profilaxia (concept id
   * 165308) value coded Fim (concept id 1267) on Ficha Clínica – Mastercard (Encounter Type 6)
   * registered between IPT Start Date and report generation date </>
   *
   * <p>Profilaxia com INH – TPI (Data Fim) (Concept ID 6129) marked in Ficha de Seguimento until
   * the report generation date
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTCompletioDateOnFichaClinicaOrFichaSeguimento() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "24- IPT completion Date - on Ficha Clinica or Ficha Seguimento  ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        "SELECT p.patient_id, MAX(e.encounter_datetime) AS recent_date "
            + "              FROM   patient p "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                    INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                    INNER JOIN ( "
            + "                            SELECT  p.patient_id, MIN(e.encounter_datetime) AS start_date "
            + "                            FROM  patient p "
            + "                                   INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN   obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE   p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                   AND e.encounter_type = ${60} "
            + "                                   AND o.concept_id = ${23985}   AND e.location_id = :location "
            + "                                   AND o.value_coded IN (${656},${23982}) "
            + "                                   AND e.encounter_datetime BETWEEN :startDate AND  :endDate "
            + "                                   GROUP BY p.patient_id "
            + "                     UNION "
            + "                            SELECT p.patient_id, "
            + "                            CASE  WHEN o.concept_id = ${6122}  THEN MIN(e.encounter_datetime) "
            + "                                   WHEN o1.concept_id = ${23985} AND o2.concept_id = ${165308} THEN MIN(e.encounter_datetime) "
            + "                                   WHEN o.concept_id = ${6128} THEN MIN(o.value_datetime) "
            + "                            END AS start_date "
            + "                                   FROM patient p "
            + "                                        INNER JOIN  encounter e ON p.patient_id = e.patient_id "
            + "                                        INNER JOIN  obs o ON e.encounter_id = o.encounter_id "
            + "                                        INNER JOIN obs o1 ON e.encounter_id = o1.encounter_id "
            + "                                        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                   WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o1.voided = 0 "
            + "                                        AND o2.voided = 0 AND e.location_id = :location "
            + "                                        AND e.encounter_type IN (${6} , ${9}) "
            + "                                        AND ((o.concept_id = ${6122} AND o.value_coded = ${1256} "
            + "                                        AND e.encounter_datetime BETWEEN :startDate AND  :endDate) "
            + "                                        OR ((o1.concept_id = ${23985} AND o1.value_coded = ${656} "
            + "                                        AND o2.concept_id = ${165308} AND o2.value_coded = ${1256})) "
            + "                                        OR (o.concept_id = ${6128} AND o.value_datetime IS NOT NULL "
            + "                                        AND o.value_datetime BETWEEN :startDate AND  :endDate)) "
            + "                                   GROUP BY p.patient_id "
            + "                     UNION "
            + "                            SELECT p.patient_id, "
            + "                            MIN(o.value_datetime) AS start_date "
            + "                            FROM patient p "
            + "                                   INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                   INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                                   INNER JOIN obs o3 ON e.encounter_id = o3.encounter_id "
            + "                            WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                   AND e.location_id = :location "
            + "                                   AND e.encounter_type = ${53} "
            + "                                   AND o.value_datetime IS NOT NULL "
            + "                                   AND ( o.concept_id = ${6128} "
            + "                                          OR ( o2.concept_id = ${23985} AND o2.value_coded = ${656} ) ) "
            + "                                   AND o3.concept_id = ${6128} AND o3.value_datetime <= CURRENT_DATE() "
            + "                                   AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "                                   AND p.patient_id NOT IN(SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                 INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                          WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_type = ${53} "
            + "                                                 AND o.concept_id = ${23985} "
            + "                                                 AND o.value_coded = ${23954}) "
            + "                            GROUP  BY p.patient_id) AS ipt ON ipt.patient_id = p.patient_id "
            + "              WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 AND o3.voided = 0 "
            + "                    AND e.encounter_type = ${6} "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = ${6122} AND o.value_coded = ${1267} "
            + "                    OR  ( (o2.concept_id = ${23985} AND o2.value_coded = ${656}) "
            + "                           AND (o3.concept_id = ${165308} AND o3.value_coded = ${1267}) )"
            + "                    AND e.encounter_datetime BETWEEN ipt.start_date AND CURRENT_DATE() ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The most recent “Última Profilaxia Isoniazida (Data Fim)” (Concept ID 6129) or Última
   * profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Fim da Profilaxia
   * TPT(value datetime, concept id 6129) registered in Ficha Resumo – Mastercard (Encounter Type
   * 53) until the report generation date </>
   *
   * <p>For 24 and 25: The system will determine the most recent from these sources as the IPT End
   * Date </>
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTCompetionDateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("25 - IPT Completion Date - on Ficha Resumo  ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put(
        "6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT p.patient_id, "
            + "       CASE WHEN o.concept_id = ${6129} THEN MAX(o.value_datetime) "
            + "            WHEN o2.concept_id = ${23985} THEN MAX(o2.value_datetime) "
            + "       END AS most_recent_date "
            + "       FROM patient p "
            + "                          	INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                          	INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                            WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0 "
            + "                            AND e.location_id = :location "
            + "                            AND e.encounter_type = ${53} "
            + "                            AND (o.concept_id = ${6129} AND o.value_datetime <= CURRENT_DATE()) "
            + "                            OR  (o2.concept_id = ${23985} AND o2.value_coded = ${23954} AND o2.value_datetime <= CURRENT_DATE()) "
            + "                            GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Expected Completion Date = IPT Start Date (earliest date between 14,15 and 16) + 173 days
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndIPTCompletionDateAndIPTStartDatePlus173Days() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "26 - IPT expected completion Date - IPT Start Date + 173 Days  ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    String query =
        "            SELECT    "
            + "                patient_id, DATE_ADD(MAX(consultation_date), INTERVAL 173 DAY) AS expected_date   "
            + "                FROM  (SELECT  p.patient_id, MIN(e.encounter_datetime) AS consultation_date   "
            + "                FROM  patient p   "
            + "         INNER JOIN  encounter e ON p.patient_id = e.patient_id   "
            + "         INNER JOIN  obs o ON e.encounter_id = o.encounter_id   "
            + "                WHERE  p.voided = 0 AND e.voided = 0   "
            + "         AND o.voided = 0   "
            + "         AND e.encounter_type = ${60}    "
            + "         AND o.concept_id = ${23985} AND e.location_id = :location   "
            + "         AND o.value_coded IN (${656},${23982})   "
            + "         AND e.encounter_datetime BETWEEN :startDate AND  :endDate   "
            + "                GROUP BY p.patient_id   "
            + "                UNION "
            + "           SELECT p.patient_id, CASE  "
            + "             WHEN o.concept_id = ${6122}  THEN MIN(e.encounter_datetime)  "
            + "             WHEN o.concept_id = ${6128}  THEN MIN(o.value_datetime)  "
            + "         END AS consultation_date  "
            + "      FROM  patient p  "
            + "             INNER JOIN  encounter e ON p.patient_id = e.patient_id  "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE p.voided = 0 AND e.voided = 0  "
            + "             AND o.voided = 0 AND e.location_id = :location  "
            + "             AND e.encounter_type IN (${6} , ${9})  "
            + "             AND ((o.concept_id = ${6122}   "
            + "             AND o.value_coded = ${1256}   "
            + "             AND e.encounter_datetime BETWEEN :startDate AND  :endDate)  "
            + "             OR (o.concept_id = ${6128}   "
            + "             AND o.value_datetime IS NOT NULL  "
            + "             AND o.value_datetime BETWEEN :startDate AND  :endDate))  "
            + "      GROUP BY p.patient_id   "
            + "                UNION    "
            + "                 SELECT p.patient_id, o.value_datetime AS  consultation_date  "
            + "      FROM  patient p  "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE  e.encounter_type = ${53}  AND p.voided = 0  "
            + "      AND e.voided = 0  "
            + "      AND o.voided = 0  "
            + "      AND o.value_datetime IS NOT NULL  "
            + "      AND o.concept_id = ${6128}   "
            + "      AND e.location_id = :location  "
            + "      AND o.value_datetime BETWEEN :startDate AND :endDate  "
            + "                 ) union_tbl GROUP BY patient_id  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Difference between Registered vs Expected Completion Date (In Number of Days) = IPT End Date
   * (the most recent between 19 and 20) minus Expected Completion Date (21)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition
      getPatientAndDifferenceBetweenRegisteredCompletionDateAndExpectedCompletionDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "27 -  Difference between Registered Completion Date and Expected Completion Date  ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put(
        "6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    // , union_tbl.encounter_datetime AS real_date,tbl_21.expected_date AS expected
    String query =
        "           SELECT p.patient_id, DATEDIFF(union_tbl.encounter_datetime, tbl_21.expected_date) AS result FROM patient p   "
            + "                JOIN (SELECT "
            + "         fila.patient_id, MAX(fila.encounter_datetime) AS encounter_datetime FROM   "
            + "         (SELECT  p.patient_id, e.encounter_datetime   "
            + "                    FROM patient p   "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                    WHERE e.encounter_type = ${6}     "
            + "             AND p.voided = 0 AND e.voided = 0   "
            + "             AND o.voided = 0 AND o.concept_id = ${6122}    "
            + "             AND o.value_coded = ${1267}  AND e.location_id = :location  "
            + "             AND e.encounter_datetime <= CURDATE()   "
            + "             UNION  "
            + "             SELECT p.patient_id, e.encounter_datetime   "
            + "                    FROM   patient p   "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                    WHERE e.encounter_type IN (${6},${9}) AND p.voided = 0   "
            + "             AND e.voided = 0 AND o.voided = 0   "
            + "             AND o.concept_id = ${6129}  AND e.location_id = :location   "
            + "             AND e.encounter_datetime <= CURDATE()   "
            + "             UNION "
            + "            SELECT p.patient_id, o.value_datetime AS encounter_datetime  "
            + "      FROM   patient p  "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE  e.encounter_type = ${53}    "
            + "      AND p.voided = 0  "
            + "      AND e.voided = 0  "
            + "      AND o.voided = 0  "
            + "      AND o.concept_id = ${6129} "
            + "      AND e.location_id = :location  "
            + "      AND o.value_datetime <= CURDATE() ) AS fila "
            + "              GROUP BY fila.patient_id) union_tbl   "
            + "              ON union_tbl.patient_id = p.patient_id   "
            + "              LEFT JOIN   "
            + "              (SELECT patient_id, DATE_ADD(MAX(consultation_date), INTERVAL 173 DAY) AS expected_date   "
            + "                FROM  (SELECT p.patient_id, MIN(e.encounter_datetime) AS consultation_date   "
            + "                FROM patient p   "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                WHERE p.voided = 0 AND e.voided = 0   "
            + "         AND o.voided = 0   "
            + "         AND e.encounter_type = ${60} "
            + "         AND o.concept_id = ${23985}  "
            + "         AND e.location_id = :location   "
            + "         AND o.value_coded IN (${656},${23982})   "
            + "         AND e.encounter_datetime BETWEEN :startDate AND  :endDate GROUP BY p.patient_id   "
            + "                UNION   "
            + "                SELECT p.patient_id, CASE  "
            + "             WHEN o.concept_id = ${6122}  THEN MIN(e.encounter_datetime)  "
            + "             WHEN o.concept_id = ${6128}  THEN MIN(o.value_datetime)  "
            + "         END AS consultation_date  "
            + "      FROM  patient p  "
            + "             INNER JOIN  encounter e ON p.patient_id = e.patient_id  "
            + "             INNER JOIN  obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE  p.voided = 0 AND e.voided = 0  "
            + "             AND o.voided = 0 AND e.location_id = :location  "
            + "             AND e.encounter_type IN (${6} , ${9})  "
            + "             AND ((o.concept_id = ${6122}   "
            + "             AND o.value_coded = ${1256}   "
            + "             AND e.encounter_datetime BETWEEN :startDate AND  :endDate)  "
            + "             OR (o.concept_id = ${6128}   "
            + "             AND o.value_datetime IS NOT NULL  "
            + "             AND o.value_datetime BETWEEN :startDate AND  :endDate)) GROUP BY p.patient_id   "
            + "                UNION   "
            + "      SELECT p.patient_id, o.value_datetime AS consultation_date  "
            + "      FROM patient p  "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "      WHERE e.encounter_type = ${53}  AND p.voided = 0  "
            + "      AND e.voided = 0  "
            + "      AND o.voided = 0  "
            + "      AND o.value_datetime IS NOT NULL  "
            + "      AND o.concept_id = ${6128}   "
            + "      AND e.location_id = :location  "
            + "      AND o.value_datetime BETWEEN :startDate AND :endDate  ) union_tbl   "
            + "                    GROUP BY patient_id) tbl_21   "
            + "                    ON tbl_21.patient_id = p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }
}
