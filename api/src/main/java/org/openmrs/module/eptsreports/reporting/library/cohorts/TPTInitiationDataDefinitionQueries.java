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
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
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

  private TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Autowired
  public TPTInitiationDataDefinitionQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      CommonMetadata commonMetadata,
      CommonQueries commonQueries,
      TPTInitiationCohortQueries tptInitiationCohortQueries) {

    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.commonMetadata = commonMetadata;
    this.commonQueries = commonQueries;
    this.tptInitiationCohortQueries = tptInitiationCohortQueries;
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
   *       1719) value coded “DT-3HP” (concept id 165307) in the last follow up consultation date
   *       before the report generation date (same as field 7); or
   *   <li>If patient have Ficha Clinica (encounter_type 6) with Profilaxia TPT (concept id 23985)
   *       value coded INH or 3HP (concept id in [656, 23954]) and Estado da Profilaxia (concept id
   *       165308) value coded Início or continua (concept id [1256, 1257]) in the last follow up
   *       consultation date before the report generation date (same as field 7);
   *   <li>or Select all patients with Ficha de Seguimento (encounter type 9) with “Profilaxia - TPT
   *       with value “Isoniazida (INH)” (Concept 23985 value 656) and Data Início ” (Concept 165308
   *       value 1256) in the last follow up consultation date before the reporting end date;
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
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
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
            + "       AND o.value_coded = ${165307}  "
            + "UNION "
            + "SELECT p.patient_id, "
            + "       last_fu_consultation.followup_date AS followup_date "
            + "FROM   patient p "
            + "       JOIN encounter e ON e.patient_id = p.patient_id "
            + "       JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       JOIN obs o2 ON e.encounter_id = o2.encounter_id "
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
            + "       AND o2.voided = 0 "
            + "        AND ( ( o.concept_id = ${23985} AND o.value_coded IN ( ${656}, ${23954} ) ) "
            + "                 AND ( o2.concept_id = ${165308} AND o2.value_coded IN ( ${1256}, ${1257} ) ) ) "
            + "UNION "
            + "SELECT p.patient_id, "
            + "       last_fu_consultation.followup_date AS followup_date "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON e.patient_id = p.patient_id "
            + "       JOIN obs o "
            + "         ON o.encounter_id = e.encounter_id "
            + "       JOIN obs o2 "
            + "         ON o2.encounter_id = e.encounter_id "
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
            + "       AND e.encounter_type = ${9} "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o2.voided = 0 "
            + "       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "       AND ( o2.concept_id = ${165308} AND o.value_coded = ${1256} ) ) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR14: Report:3HP Initiation Dates (Sheet 1: Columns I, J, K)</b>
   *
   * <blockquote>
   *
   * <p>3HP Initiation Date on FILT – Sheet 1: Column I
   *
   * <p>The earliest 3HP drug-pick up date registered on FILT (Regime de TPT = 3HP or 3HP +
   * Piridoxina) that falls during the reporting period (TPT_INI_FR4)
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

    String query =
        new EptsQueriesUtil()
            .unionBuilder(
                tptInitiationCohortQueries
                    .getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .union(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HPDate())
            .buildQuery();

    String tptFilt3hp = new EptsQueriesUtil().min(query).getQuery();

    sqlPatientDataDefinition.setQuery(tptFilt3hp);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR14: Report:3HP Initiation Dates (Sheet 1: Columns I, J, K)</b>
   *
   * <blockquote>
   *
   * <p>3HP Initiation Date on Ficha Clínica – Sheet 1: Column J
   *
   * <p>The earliest 3HP initiation date registered on Ficha Clínica Profilaxia TPT with the value
   * “3HP” and Estado da Profilaxia with the value “Inicio (I)” that falls during the reporting
   * period (TPT_INI_FR4)
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

    String query =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hpDate())
            .union(tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HPDate())
            .buildQuery();

    String clinica3HPPresc = new EptsQueriesUtil().min(query).getQuery();

    sqlPatientDataDefinition.setQuery(clinica3HPPresc);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR14: Report:3HP Initiation Dates (Sheet 1: Columns I, J, K)</b>
   *
   * <blockquote>
   *
   * <p>3HP Initiation Date on Ficha Resumo – Sheet 1: Column K
   *
   * <p>The earliest 3HP Initiation date registered in Ficha Resumo – Mastercard (Última profilaxia
   * TPT with value “3HP” with Data Inicio that falls during the reporting period (TPT_INI_FR4)
   *
   * <p>The system will determine the earliest date from these sources as the 3HP Start Date
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition get3HPInitiationDateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("12 - 3HP Initiation Date On FIcha Resumo ");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        new EptsQueriesUtil()
            .min(tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hpDate())
            .getQuery();

    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR15: Report: Last FILT Dispensation with 3HP(Sheet 1: Columns L, M)</b>
   *
   * <blockquote>
   *
   * <p>Last 3HP FILT: Date – Sheet 1: Column L
   *
   * <p>Most recent FILT that has Regime de TPT with the values (3HP or 3HP + Piridoxina) marked
   * until the report generation date
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
   * <b>TPT_INI_FR15: Report: Last FILT Dispensation with 3HP(Sheet 1: Columns L, M)</b>
   *
   * <blockquote>
   *
   * <p>Last 3HP FILT: Type of Dispensation – Sheet 1: Column M Value of Tipo de Dispensa (Mensal or
   * Trimestral) on the most recent FILT that has Regime de TPT with the values (3HP or 3HP +
   * Piridoxina) marked until the report generation date. Possible values are: Mensal or Trimestral
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
   * <b>TPT_INI_FR16: Report: 3HP Completion Dates (Sheet 1: Columns N, O)<b>
   *
   * <blockquote>
   *
   * <p>3HP Completion Date on Ficha Clínica – Sheet 1: Column N Profilaxia TPT=”3HP” and Estado da
   * Profilaxia marked with the value Fim(F) on Ficha Clínica between the 3HP Start Date (obtained
   * in TPT_INI_FR14) and until the report generation date
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
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String threeHPStart =
        new EptsQueriesUtil()
            .unionBuilder(
                tptInitiationCohortQueries
                    .getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .union(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HPDate())
            .union(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hpDate())
            .union(tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HPDate())
            .union(tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hpDate())
            .buildQuery();

    String query =
        "SELECT p.patient_id, "
            + "                   Max(o2.obs_datetime) AS completion_date "
            + "             FROM patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                   INNER JOIN ( "
            + "                          SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                          FROM ( "
            + threeHPStart
            + ") threeHP "
            + "                          GROUP BY threeHP.patient_id "
            + "                   ) 3hp_start ON 3hp_start.patient_id = p.patient_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o2.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${6} "
            + "               AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "               AND ( o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "               AND o2.obs_datetime BETWEEN 3hp_start.earliest_date AND CURRENT_DATE() ) ) "
            + "             GROUP BY   p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR16: Report: 3HP Completion Dates (Sheet 1: Columns N, O)<b>
   *
   * <blockquote>
   *
   * <p>Select all patients with Última profilaxia(concept id 23985) value coded 3HP(concept id
   * 23954) and Data Fim (concep id 165308 value 1267) registered on Ficha Resumo (Encounter type
   * 53) with value datetime between the 3HP start date and Report generation date
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
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String threeHPStart =
        new EptsQueriesUtil()
            .unionBuilder(
                tptInitiationCohortQueries
                    .getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .union(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HPDate())
            .union(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hpDate())
            .union(tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HPDate())
            .union(tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hpDate())
            .buildQuery();

    String query =
        "SELECT p.patient_id, "
            + "                        Max(o2.obs_datetime) AS completion_date "
            + "             FROM patient p "
            + "                            INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                            INNER JOIN ( "
            + "                            SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                            FROM ( "
            + threeHPStart
            + " ) threeHP "
            + "                            GROUP BY threeHP.patient_id ) 3hp_start ON 3hp_start.patient_id = p.patient_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o2.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${53} "
            + "               AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "               AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "               AND o2.obs_datetime BETWEEN 3hp_start.earliest_date AND CURRENT_DATE()) ) "
            + "             GROUP BY   p.patient_id;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b> Technical Specs <b>
   *
   * <blockquote>
   *
   * <p>Only for patients who initiated 3HP (TPT_INI_FR4) the system will calculate the Expected 3HP
   * Completion Date as follows:
   *
   * <p>Expected 3HP Completion Date – Sheet 1: Column P
   *
   * <p>Expected 3HP Completion Date = 3HP Start Date (defined in TPT_INI_FR14) + 86 days
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
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());

    String threeHPStart =
        new EptsQueriesUtil()
            .unionBuilder(
                tptInitiationCohortQueries
                    .getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .union(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HPDate())
            .union(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hpDate())
            .union(tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HPDate())
            .union(tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hpDate())
            .buildQuery();

    String query =
        "SELECT patient_id, DATE_ADD(MIN(tpt_date), INTERVAL 86 DAY) AS expected_date "
            + "                                       FROM ( "
            + "             "
            + threeHPStart
            + " "
            + "             ) threeHPFinal "
            + "        GROUP BY threeHPFinal.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>18 - Only for patients who initiated 3HP (TPT_INI_FR4) the system will calculate the
   * difference between Registered Completion Date and Expected Completion Date as follows:
   *
   * <p>Difference between Registered vs Expected 3HP Completion Date – Sheet 1: Column Q Difference
   * between Registered vs Expected 3HP Completion Date (In Number of Days) = 3HP End Date
   * (TPT_INI_FR16) – Expected Completion Date (TPT_INI_FR17)
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
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());

    String threeHPStart =
        new EptsQueriesUtil()
            .unionBuilder(
                tptInitiationCohortQueries
                    .getPatientsWith3HP3RegimeTPTAndSeguimentoDeTratamentoDate())
            .union(tptInitiationCohortQueries.getPatientsWithRegimeDeTPT3HPDate())
            .union(tptInitiationCohortQueries.getPatientWithProfilaxiaTpt3hpDate())
            .union(tptInitiationCohortQueries.getPatientsWithOutrasPerscricoesDT3HPDate())
            .union(tptInitiationCohortQueries.getPatientsWithUltimaProfilaxia3hpDate())
            .buildQuery();

    String query =
        "SELECT patient_id, DATEDIFF(completion_date, DATE_ADD(start_date, INTERVAL 86 DAY))"
            + "              FROM (SELECT p.patient_id, "
            + "                           MIN(earliest_date)   AS start_date, "
            + "                           Max(o2.obs_datetime) AS completion_date "
            + "                    FROM patient p "
            + "                             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             INNER JOIN (SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                                         FROM ( "
            + "                                                  "
            + threeHPStart
            + " "
            + "                                                  ) threeHP "
            + "                                         GROUP BY threeHP.patient_id) 3hp_start ON 3hp_start.patient_id = p.patient_id "
            + "                    WHERE p.voided = 0 "
            + "                      AND e.voided = 0 "
            + "                      AND o.voided = 0 "
            + "                      AND o2.voided = 0 "
            + "                      AND e.location_id = :location "
            + "                      AND e.encounter_type IN (${6}, ${53}) "
            + "                      AND ((o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "                        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "                            AND o2.obs_datetime BETWEEN 3hp_start.earliest_date AND CURRENT_DATE())) "
            + "                     GROUP BY p.patient_id) diff_table "
            + "                GROUP BY diff_table.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR19: Report: IPT Initiation Dates (Sheet 1: Columns R, S, T) </b>
   *
   * <blockquote>
   *
   * <p>IPT Initiation Date on FILT – Sheet 1: Column R The earliest IPT drug pick-up date
   * registered on FILT (Regime TPT = “Isoniazida” or “Isoniazida + Piridoxina”) that falls during
   * the reporting period (TPT_INI_FR5)
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

    String query =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazidDate())
            .union(tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTptDate())
            .buildQuery();

    String tptFiltInh = new EptsQueriesUtil().min(query).getQuery();

    sqlPatientDataDefinition.setQuery(tptFiltInh);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR19: Report: IPT Initiation Dates (Sheet 1: Columns R, S, T) </b>
   *
   * <blockquote>
   *
   * <p>IPT Initiation Date on Ficha Clínica or Ficha de Seguimento – Sheet 1: Column S The earliest
   * IPT initiation date (Data Início) registered on Ficha Clínica Profilaxia TPT with the value
   * “Isoniazida (INH)” and Estado da Profilaxia with the value “Inicio (I)”) or Ficha de Seguimento
   * Profilaxia TPT with the value “Isoniazida (INH)” and with Data Fim) that falls during the
   * reporting period (TPT_INI_FR5)
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

    String query =
        new EptsQueriesUtil()
            .min(tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINHDate())
            .getQuery();

    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR19: Report: IPT Initiation Dates (Sheet 1: Columns R, S, T) </b>
   *
   * <blockquote>
   *
   * <p>IPT Initiation Date on Ficha Resumo – Sheet 1: Column T The earliest IPT initiation date
   * registered in Ficha Resumo – Mastercard Última profilaxia TPT with value “Isoniazida (INH)” and
   * with Data Inicio that falls during the reporting period (TPT_INI_FR5)
   *
   * <ul>
   *   <li>The system will determine the earliest date from these sources as the IPT Start Date
   * </ul>
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

    String query =
        new EptsQueriesUtil()
            .min(tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxiaDate())
            .getQuery();

    sqlPatientDataDefinition.setQuery(query);

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
   * <b>TPT_INI_FR21: Report: IPT Completion Dates (Sheet 1: Columns W, X)</b>
   *
   * <blockquote>
   *
   * <p>Only for patients who initiated IPT (TPT_INI_FR5) the system will show the most recent IPT
   * completion dates in Columns W and X for each patient as follows
   *
   * <p>IPT Completion Date on Ficha Clínica or Ficha de Seguimento– Sheet 1: Column W
   *
   * <p>Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia with the value
   * “Fim (F)”) marked on Ficha Clínica – Mastercard or
   *
   * <p>Profilaxia TPT with the value “Fim(F)” and Data Fim marked in Ficha de Seguimento between
   * the IPT Start Date (obtained in TPT_INI_FR19) and until the report generation date
   *
   * <p>Note: if more than one Ficha Clínica or Ficha de Seguimento exists the system should
   * consider the most recent date amongst the sources
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
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String iptStart =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazidDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINHDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxiaDate())
            .union(tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTptDate())
            .buildQuery();

    String query =
        "SELECT p.patient_id, "
            + "                   Max(o2.obs_datetime) AS completion_date "
            + "             FROM patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                   INNER JOIN ( "
            + "                          SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                          FROM ( "
            + iptStart
            + ") ipt "
            + "                          GROUP BY ipt.patient_id "
            + "                   ) ipt_start ON ipt_start.patient_id = p.patient_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o2.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type IN ( ${6}, ${9} ) "
            + "               AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "               AND ( o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "               AND o2.obs_datetime BETWEEN ipt_start.earliest_date AND CURRENT_DATE() ) ) "
            + "             GROUP BY   p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>TPT_INI_FR21: Report: IPT Completion Dates (Sheet 1: Columns W, X)</b>
   *
   * <blockquote>
   *
   * <p>IPT Completion Date on Ficha Resumo – Sheet 1: Column X
   *
   * <p>The most recent Última profilaxia TPT with value “Isoniazida (INH)” and Data de Fim selected
   * in Ficha Resumo – Mastercard between the IPT Start Date (obtained in TPT_INI_FR19) and until
   * the report generation date The system will determine the most recent from these sources as the
   * IPT End Date
   *
   * <p>The system will determine the most recent from these sources as the IPT End Date
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
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String iptStart =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazidDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINHDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxiaDate())
            .union(tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTptDate())
            .buildQuery();

    String query =
        "SELECT p.patient_id, "
            + "                   Max(o2.obs_datetime) AS completion_date "
            + "             FROM patient p "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                   INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                   INNER JOIN ( "
            + "                          SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                          FROM ( "
            + iptStart
            + " ) ipt "
            + "                          GROUP BY ipt.patient_id ) ipt_start ON ipt_start.patient_id = p.patient_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o2.voided = 0 "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type = ${53} "
            + "               AND ( (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "               AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "               AND  o2.obs_datetime BETWEEN ipt_start.earliest_date AND CURRENT_DATE()) ) "
            + "             GROUP BY   p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Only for patients who initiated IPT (TPT_INI_FR5) the system will calculate the Expected
   * Completion Date as follows:
   *
   * <p>Difference between Registered vs Expected Completion Date – Sheet 1: Column Z Difference
   * between Registered vs Expected Completion Date (In Number of Days) = IPT End Date
   * (TPT_INI_FR21) – Expected Completion Date (TPT_INI_FR22)
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
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String iptStart =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazidDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINHDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxiaDate())
            .union(tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTptDate())
            .buildQuery();

    String query =
        "SELECT patient_id, DATE_ADD(MIN(tpt_date), INTERVAL 173 DAY) AS expected_date "
            + "                                       FROM ( "
            + "             "
            + iptStart
            + " "
            + "             ) iptFinal "
            + "        GROUP BY iptFinal.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Only for patients who initiated IPT (TPT_INI_FR5) the system will calculate the difference
   * between Registered Completion Date and Expected Completion Date as follows:
   *
   * <p>Difference between Registered vs Expected Completion Date – Sheet 1: Column Z
   *
   * <p>Difference between Registered vs Expected Completion Date (In Number of Days) = IPT End Date
   * (TPT_INI_FR21) – Expected Completion Date (TPT_INI_FR22)
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
    valuesMap.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("23954", tbMetadata.get3HPConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String iptStart =
        new EptsQueriesUtil()
            .unionBuilder(tptInitiationCohortQueries.getpatientswithRegimeTPTIsoniazidDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaClinicaProfilaxiaINHDate())
            .union(tptInitiationCohortQueries.getPatientsWithFichaResumoUltimaProfilaxiaDate())
            .union(tptInitiationCohortQueries.getPatientsWithFirstFiltRegimeTptDate())
            .buildQuery();

    String query =
        "SELECT patient_id, DATEDIFF(completion_date, DATE_ADD(start_date, INTERVAL 173 DAY))"
            + "              FROM (SELECT p.patient_id, "
            + "                           MIN(earliest_date)   AS start_date, "
            + "                           Max(o2.obs_datetime) AS completion_date "
            + "                    FROM patient p "
            + "                             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                             INNER JOIN (SELECT patient_id, MIN(tpt_date) AS earliest_date "
            + "                                         FROM ( "
            + "                                                  "
            + iptStart
            + " "
            + "                                                  ) ipt "
            + "                                         GROUP BY ipt.patient_id) ipt_start ON ipt_start.patient_id = p.patient_id "
            + "                    WHERE p.voided = 0 "
            + "                      AND e.voided = 0 "
            + "                      AND o.voided = 0 "
            + "                      AND o2.voided = 0 "
            + "                      AND e.location_id = :location "
            + "                      AND e.encounter_type IN (${6}, ${9}, ${53}) "
            + "                      AND ((o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "                        AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + "                            AND o2.obs_datetime BETWEEN ipt_start.earliest_date AND CURRENT_DATE())) "
            + "                     GROUP BY p.patient_id) diff_table "
            + "                GROUP BY diff_table.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }
}
