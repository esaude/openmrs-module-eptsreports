package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsDefaultersOrIITCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private CommonMetadata commonMetadata;

  private final String MAPPING = "location=${location}";

  private final String MAPPING2 = "onOrBefore=${endDate},location=${location}";

  private final String MAPPING3 =
      "endDate=${endDate},location=${location},minDay=${minDay},maxDay=${maxDay}";

  /**
   * <b>E1</b> - exclude all patients who are transferred out by end of report generation date,
   *
   * <p><b>1.1</b> - All transferred-outs registered in Patient Program State by reporting end date,
   * i.e LAST Transferred out state in program enrollment by end of period.
   * Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 7
   * (Transferred-out) or max(Patient_State.start_date) <= Report Generation Date => The most recent
   * start_date by Report Generation Date. Patient_state.end_date is null
   *
   * <p><b>1.2</b> - All transferred-outs registered in Ficha Resumo and Ficha Clinica of Master
   * Card by reporting end date Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) =
   * Transferred-out (Concept ID 1706) Encounter_datetime <= Report Generation Date OR Encounter
   * Type ID= 53 Estado de Permanencia (Concept Id 6272) = Transferred-out (Concept ID 1706)
   * obs_datetime <= Report Generation Date
   *
   * <p><b>1.3</b> - Patients who have REASON PATIENT MISSED VISIT (MOTIVOS DA FALTA - concept id
   * 2016) as “Transferido para outra US” or “Auto-transferência” ( 1706 OR 23863) marked in the
   * last Home Visit Card by the report generation date
   *
   * <p><b>1.4</b> - Exclude all patients who after the most recent date from 1.1 to 1.2, have a
   * drugs pick up or Consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most
   * recent date and <= Report Generation Date or Encounter Type ID = 52 and “Data de Levantamento”
   * (Concept Id 23866 value_datetime) > the most recent date and <= Report Generation Date
   * Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date and <= Report
   * Generation Date or Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866
   * value_datetime) > the most recent date and <= Report Generation Date
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getE1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition e1 = getPatientsConsultationAfterMostRecent();

    cd.addSearch("e1", EptsReportUtils.map(e1, MAPPING));

    cd.setCompositionString("e1");

    return cd;
  }

  /**
   * <b>E2</b> - exclude all patients who died by Report Generation date,
   *
   * <p><b>2.1</b> - All deaths registered in Patient Program State by reporting end date
   * Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10 (Died)
   * Patient_State.start_date <= Report Generation Date Patient_state.end_date is null
   *
   * <p><b>2.2</b> - All deaths registered in Patient Demographics by reporting end date
   * Person.Dead=1 and death_date <= Report Generation Date
   *
   * <p><b>2.3</b> - All deaths registered in Last Home Visit Card by reporting end date Last Home
   * Visit Card (Encounter Type 21, 36, 37) Reason of Not Finding (Concept ID 2031 or 23944 or
   * 23945) = Died (Concept Id 1366) Last Encounter_datetime <= Report Generation Date
   *
   * <p><b>2.4</b> - All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Dead (Concept
   * ID 1366) Encounter_datetime <= Report Generation Date OR Encounter Type ID= 53 Estado de
   * Permanencia (Concept Id 6272) = Dead (Concept ID 1366) obs_datetime <= Report Generation Date
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getE2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition e21 = getE21();
    CohortDefinition e22 = getE22();
    CohortDefinition e23 = getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate();
    CohortDefinition e24 = getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate();
    CohortDefinition e25 = getE25();

    cd.addSearch("e21", EptsReportUtils.map(e21, MAPPING));
    cd.addSearch("e22", EptsReportUtils.map(e22, MAPPING));
    cd.addSearch("e23", EptsReportUtils.map(e23, MAPPING));
    cd.addSearch("e24", EptsReportUtils.map(e24, MAPPING));
    cd.addSearch("e25", EptsReportUtils.map(e25, MAPPING));

    cd.setCompositionString("(e21 OR e22 OR e23 OR e24) AND e25");

    return cd;
  }

  /**
   * <b>E3</b> - exclude all patients who stopped/suspended treatment by end of the reporting
   * period,
   *
   * <p><b>3.1</b> - All suspended registered in Patient Program State by reporting end date i.e
   * LAST Transferred out state in program enrollment by end of period. Patient_program.program_id
   * =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 8 (Suspended treatment) OR
   * Patient_State.start_date <= ReportGenerationDate start_date by Report Generation Date.
   * Patient_state.end_date is null
   *
   * <p><b>2.2</b> - All suspensions registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Suspended
   * (Concept ID 1709) Encounter_datetime <= Report Generation Date OR Encounter Type ID= 53 Estado
   * de Permanencia (Concept Id 6272) = Suspended (Concept ID 1709) obs_datetime <= Report
   * Generation Date
   *
   * <p><b>3.3</b> - Except all patients who after the most recent date from 3.1 to 3.2, have a
   * drugs pick up or Consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most
   * recent date and <= Report Generation Date or Encounter Type ID = 52 and “Data de Levantamento”
   * (Concept Id 23866 value_datetime) > the most recent date and <= Report Generation Date
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getE3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition e3 = getPatientsConsultationAfterMostRecentE3();

    cd.addSearch("e3", EptsReportUtils.map(e3, MAPPING));

    cd.setCompositionString("e3");

    return cd;
  }

  /**
   * <b>2.4</b> - All deaths registered in Patient Program State by reporting end date *
   * Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10 (Died) *
   * Patient_State.start_date <= Report Generation Date Patient_state.end_date is null
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE21() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());

    String query =
        "SELECT p.patient_id FROM patient p    "
            + "    INNER JOIN patient_program pg ON pg.patient_id = p.patient_id "
            + "    INNER JOIN patient_state ps ON ps.patient_program_id = pg.patient_program_id "
            + " WHERE  pg.program_id = ${2}  "
            + " AND ps.state = ${10} "
            + " AND ps.start_date <= CURRENT_DATE() "
            + " AND ps.end_date IS NULL "
            + " AND pg.location_id = :location";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>2.2</b> - All deaths registered in Patient Demographics by reporting end date *
   * Person.Dead=1 and death_date <= Report Generation Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE22() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    String query =
        "SELECT p.person_id   "
            + "                FROM person p  "
            + "                INNER JOIN patient_program pg ON p.person_id=pg.patient_id "
            + "                INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "                WHERE p.dead=1  "
            + "                AND p.death_date <= CURRENT_DATE()  "
            + "                AND p.voided=0";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>2.3</b> - All deaths registered in Last Home Visit Card by reporting end date Last Home
   * Visit Card (Encounter Type 21, 36, 37) Reason of Not Finding (Concept ID 2031 or 23944 or
   * 23945) = Died (Concept Id 1366) Last Encounter_datetime <= Report Generation Date
   *
   * @param buscaActivaEncounterType
   * @param visitaApoioReintegracaoParteAEncounterType
   * @param visitaApoioReintegracaoParteBEncounterType
   * @param reasonPatientNotFound
   * @param reasonPatientNotFoundByActivist2ndVisit
   * @param reasonPatientNotFoundByActivist3rdVisit
   * @param patientIsDead
   * @return
   */
  public static String getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
      int buscaActivaEncounterType,
      int visitaApoioReintegracaoParteAEncounterType,
      int visitaApoioReintegracaoParteBEncounterType,
      int reasonPatientNotFound,
      int reasonPatientNotFoundByActivist2ndVisit,
      int reasonPatientNotFoundByActivist3rdVisit,
      int patientIsDead) {
    String query =
        "  SELECT  max_date.patient_id FROM  "
            + "    (SELECT  "
            + "      p.patient_id,  "
            + "      MAX(e.encounter_datetime) last   "
            + "    FROM patient p "
            + "      INNER  JOIN encounter e ON e.patient_id=p.patient_id "
            + "     WHERE  "
            + "      e.encounter_datetime <= CURRENT_DATE() "
            + "      AND e.location_id = :location "
            + "      AND e.encounter_type  in( ${buscaActiva},${visitaApoioReintegracaoParteA},${visitaApoioReintegracaoParteB})  "
            + "      AND e.voided=0 "
            + "      AND p.voided = 0 "
            + "    GROUP BY  p.patient_id  ) max_date "
            + "    INNER  JOIN encounter ee "
            + "            ON ee.patient_id = max_date.patient_id "
            + "    INNER  JOIN obs o ON ee.encounter_id = o.encounter_id  "
            + "        WHERE  "
            + "        ( "
            + "            (o.concept_id = ${reasonPatientNotFound} AND o.value_coded = ${patientIsDead}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist2ndVisit} AND o.value_coded = ${patientIsDead}) OR "
            + "            (o.concept_id = ${reasonPatientNotFoundByActivist3rdVisit} AND o.value_coded = ${patientIsDead} ) "
            + "        )  "
            + "    AND o.voided=0 "
            + "    AND ee.voided = 0 "
            + "    GROUP BY  max_date.patient_id";

    Map<String, Integer> map = new HashMap<>();
    map.put("buscaActiva", buscaActivaEncounterType);
    map.put("visitaApoioReintegracaoParteA", visitaApoioReintegracaoParteAEncounterType);
    map.put("visitaApoioReintegracaoParteB", visitaApoioReintegracaoParteBEncounterType);
    map.put("reasonPatientNotFound", reasonPatientNotFound);
    map.put("reasonPatientNotFoundByActivist2ndVisit", reasonPatientNotFoundByActivist2ndVisit);
    map.put("reasonPatientNotFoundByActivist3rdVisit", reasonPatientNotFoundByActivist3rdVisit);
    map.put("patientIsDead", patientIsDead);

    StringSubstitutor sub = new StringSubstitutor(map);
    return sub.replace(query);
  }

  public CohortDefinition getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientDeathRegisteredInLastHomeVisitCardByReportingEndDate");

    definition.setQuery(
        getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * <b>2.4</b> - All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Dead (Concept
   * ID 1366) Encounter_datetime <= Report Generation Date OR Encounter Type ID= 53 Estado de
   * Permanencia (Concept Id 6272) = Dead (Concept ID 1366) obs_datetime <= Report Generation Date
   *
   * @param adultoSeguimentoEncounterType
   * @param masterCardEncounterType
   * @param stateOfStayPriorArtPatientConcept
   * @param stateOfStayOfArtPatient
   * @param patientHasDiedConcept
   * @return
   */
  public static String getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
      int adultoSeguimentoEncounterType,
      int masterCardEncounterType,
      int stateOfStayPriorArtPatientConcept,
      int stateOfStayOfArtPatient,
      int patientHasDiedConcept) {

    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("masterCardEncounterType", masterCardEncounterType);
    map.put("stateOfStayPriorArtPatientConcept", stateOfStayPriorArtPatientConcept);
    map.put("stateOfStayOfArtPatient", stateOfStayOfArtPatient);
    map.put("patientHasDiedConcept", patientHasDiedConcept);

    String query =
        "SELECT  p.patient_id  "
            + "FROM patient p  "
            + "    INNER JOIN encounter e  "
            + "        ON e.patient_id=p.patient_id  "
            + "    INNER JOIN obs o  "
            + "        ON o.encounter_id=e.encounter_id  "
            + "WHERE e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "    AND e.encounter_datetime <= CURRENT_DATE() "
            + "    AND o.concept_id = ${stateOfStayOfArtPatient} "
            + "    AND o.value_coded=${patientHasDiedConcept}  "
            + "    AND e.location_id = :location  "
            + "    AND p.voided=0   "
            + "    AND e.voided=0  "
            + "    AND o.voided=0  "
            + "GROUP BY p.patient_id "
            + "UNION "
            + "SELECT  p.patient_id  "
            + "FROM patient p  "
            + "    INNER JOIN encounter e  "
            + "        ON e.patient_id=p.patient_id  "
            + "    INNER JOIN obs o  "
            + "        ON o.encounter_id=e.encounter_id  "
            + "WHERE e.encounter_type = ${masterCardEncounterType}  "
            + "    AND o.obs_datetime <= CURRENT_DATE() "
            + "    AND o.concept_id = ${stateOfStayPriorArtPatientConcept} "
            + "    AND o.value_coded=${patientHasDiedConcept}  "
            + "    AND e.location_id = :location  "
            + "    AND p.voided=0   "
            + "    AND e.voided=0  "
            + "    AND o.voided=0  "
            + "GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  public CohortDefinition getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate() {
    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("deadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate");

    definition.setQuery(
        getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  /**
   * <b>2.4</b> - All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Dead (Concept
   * ID 1366) Encounter_datetime <= Report Generation Date OR Encounter Type ID= 53 Estado de
   * Permanencia (Concept Id 6272) = Dead (Concept ID 1366) obs_datetime <= Report Generation Date
   *
   * @return
   */
  public SqlCohortDefinition getE25() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    map.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("36", hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId());
    map.put("37", hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId());
    map.put("2031", hivMetadata.getReasonPatientNotFound().getConceptId());
    map.put(
        "23944", hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId());
    map.put(
        "23945", hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId());
    map.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("6272", hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT transferout.patient_id,   Max(transferout.transferout_date) transferout_date "
            + "        FROM   (SELECT  max_date.patient_id, (max_date.last) AS transferout_date FROM "
            + "            (SELECT "
            + "                 p.patient_id, "
            + "                 MAX(e.encounter_datetime) last "
            + "             FROM patient p "
            + "                      INNER  JOIN encounter e ON e.patient_id=p.patient_id "
            + "             WHERE "
            + "                     e.encounter_datetime <= curdate() "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type  in(  ${21} , ${36} , ${37} ) "
            + "               AND e.voided=0 "
            + "               AND p.voided = 0 "
            + "             GROUP BY  p.patient_id  ) max_date "
            + "                INNER  JOIN encounter ee "
            + "                            ON ee.patient_id = max_date.patient_id "
            + "                INNER  JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                WHERE "
            + "                    ( (o.concept_id =  ${2031}  AND o.value_coded =  ${1366} ) OR "
            + "                            (o.concept_id =  ${23944}  AND o.value_coded =  ${1366} ) OR "
            + "                            (o.concept_id =  ${23945}  AND o.value_coded =  ${1366}  ) ) "
            + "                  AND o.voided=0 "
            + "                  AND ee.voided = 0 "
            + "                GROUP BY  max_date.patient_id "
            + " "
            + "                UNION "
            + "                 "
            + "                SELECT  p.patient_id , (e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${6} "
            + "                  AND e.encounter_datetime <= curdate() "
            + "                  AND o.concept_id =  ${6273} "
            + "                  AND o.value_coded= ${1366} "
            + "                  AND e.location_id = :location "
            + "                  AND p.voided= 0 "
            + "                  AND e.voided= 0 "
            + "                  AND o.voided= 0 "
            + "                GROUP BY p.patient_id "
            + " "
            + "                UNION "
            + " "
            + "                SELECT pg.patient_id, (ps.start_date) AS transferout_date FROM patient p "
            + "                INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "                INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "                WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + "                AND pg.program_id=${2} AND ps.state=${10} AND ps.end_date is null "
            + "                AND ps.start_date <= curdate() AND location_id=:location "
            + " "
            + "                UNION "
            + " "
            + "                SELECT p.person_id, (ps.start_date) AS transferout_date   "
            + "                FROM person p  "
            + "                INNER JOIN patient_program pg ON p.person_id=pg.patient_id "
            + "                INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "                WHERE p.dead= 1  "
            + "                AND p.death_date <= curdate()  "
            + "                AND p.voided=0 "
            + " "
            + "                UNION "
            + " "
            + "                SELECT  p.patient_id, (e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${53} "
            + "                  AND o.obs_datetime <= curdate() "
            + "                  AND o.concept_id =  ${6272} "
            + "                  AND o.value_coded= ${1366} "
            + "                  AND e.location_id = :location "
            + "                  AND p.voided=0 "
            + "                  AND e.voided=0 "
            + "                  AND o.voided=0 "
            + "                GROUP BY p.patient_id) transferout "
            + "        GROUP  BY transferout.patient_id) max_transferout "
            + " WHERE  max_transferout.patient_id NOT IN (SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                     JOIN encounter e "
            + "                                                          ON p.patient_id = "
            + "                                                             e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type IN (${6},${9},${18})  "
            + "                                            AND e.location_id = :location "
            + "                                            AND "
            + "                                                  e.encounter_datetime > transferout_date "
            + "                                            AND "
            + "                                                  e.encounter_datetime <= curdate() "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                     JOIN encounter e "
            + "                                                          ON p.patient_id = "
            + "                                                             e.patient_id "
            + "                                                     JOIN obs o "
            + "                                                          ON e.encounter_id = "
            + "                                                             o.encounter_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type =  ${52} "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.concept_id =  ${23866} "
            + "                                            AND o.value_datetime > transferout_date "
            + "                                            AND o.value_datetime <= curdate()); ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>E1</b> - exclude all patients who are transferred out by end of report generation date,
   *
   * <p><b>1.3</b> - Exclude all patients who after the most recent date from 1.1 to 1.2, have a
   * drugs pick up or Consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most
   * recent date and <= Report Generation Date or Encounter Type ID = 52 and “Data de Levantamento”
   * (Concept Id 23866 value_datetime) > the most recent date and <= Report Generation Date
   * Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date and <= Report
   * Generation Date or Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866
   * value_datetime) > the most recent date and <= Report Generation Date
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getPatientsConsultationAfterMostRecent() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "all patients who after the most recent date from 1.1 to 1.2, have a drugs pick up or Consultation");
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "7",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("23863", hivMetadata.getAutoTransferConcept().getConceptId());
    map.put("2016", hivMetadata.getDefaultingMotiveConcept().getConceptId());

    String query =
        "  SELECT patient_id  "
            + "           FROM   (SELECT transferout.patient_id,  "
            + "                          Max(transferout.transferout_date) transferout_date  "
            + "                   FROM   (SELECT p.patient_id,  "
            + "                                  Max(e.encounter_datetime) AS transferout_date  "
            + "                           FROM   patient p  "
            + "                                  JOIN encounter e  "
            + "                                    ON p.patient_id = e.patient_id  "
            + "                                  JOIN obs o  "
            + "                                    ON e.encounter_id = o.encounter_id  "
            + "                           WHERE  p.voided = 0  "
            + "                                  AND e.voided = 0  "
            + "                                  AND e.location_id = :location  "
            + "                                  AND e.encounter_type =  ${6}   "
            + "                                  AND e.encounter_datetime <= CURRENT_DATE()  "
            + "                                  AND o.voided = 0  "
            + "                                  AND o.concept_id =  ${6273}  "
            + "                                  AND o.value_coded =  ${1706}   "
            + "                           GROUP  BY p.patient_id  "
            + "                           UNION  "
            + "                           SELECT p.patient_id,  "
            + "                                  Max(o.obs_datetime) AS transferout_date  "
            + "                           FROM   patient p  "
            + "                                  JOIN encounter e  "
            + "                                    ON p.patient_id = e.patient_id  "
            + "                                  JOIN obs o  "
            + "                                    ON e.encounter_id = o.encounter_id  "
            + "                           WHERE  p.voided = 0  "
            + "                                  AND e.voided = 0  "
            + "                                  AND e.location_id = :location  "
            + "                                  AND e.encounter_type =  ${53}   "
            + "                                  AND o.obs_datetime <= CURRENT_DATE()  "
            + "                                  AND o.voided = 0  "
            + "                                  AND o.concept_id =  ${6272}   "
            + "                                  AND o.value_coded =  ${1706}   "
            + "                           GROUP  BY p.patient_id"
            + "                            UNION "
            + "                           SELECT p.patient_id, "
            + "                                  Max(e.encounter_datetime) AS transferout_date "
            + "                           FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                          ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                          ON e.encounter_id = o.encounter_id "
            + "                           WHERE  p.voided = 0 "
            + "                                  AND o.voided = 0 "
            + "                                  AND e.voided = 0 "
            + "                                  AND e.encounter_type = ${21} "
            + "                                  AND o.concept_id = ${2016} "
            + "                                  AND o.value_coded IN ( ${1706}, ${23863} ) "
            + "                                  AND e.encounter_datetime <= CURRENT_DATE() "
            + "                                  AND e.location_id = :location "
            + "                           GROUP  BY p.patient_id "
            + "                           UNION"
            + "                           "
            + "                           SELECT p.patient_id, Max(ps.start_date) AS transferout_date"
            + "                             FROM   patient p"
            + "                                 INNER JOIN patient_program pg"
            + "                                     ON p.patient_id = pg.patient_id"
            + "                                 INNER JOIN patient_state ps"
            + "                                     ON pg.patient_program_id = ps.patient_program_id"
            + "                                 INNER JOIN (SELECT p.patient_id"
            + "                                             FROM   patient p"
            + "                                                 INNER JOIN (SELECT pp.patient_id, Max(ps.start_date) AS max_startDate"
            + "                                                             FROM   patient pp"
            + "                                                                 INNER JOIN patient_program pg"
            + "                                                                     ON pp.patient_id = pg.patient_id"
            + "                                                                 INNER JOIN patient_state ps"
            + "                                                                     ON pg.patient_program_id = ps.patient_program_id"
            + "                                                 WHERE  pg.location_id = :location"
            + "                                                   AND ps.start_date IS NOT NULL AND ps.end_date IS NULL"
            + "                                                 GROUP  BY pp.patient_id) AS tbl"
            + "                                                     ON p.patient_id = tbl.patient_id"
            + "                                 WHERE  p.voided = 0"
            + "                                   AND tbl.max_startdate <= CURRENT_DATE()"
            + "                                 GROUP BY p.patient_id) AS max_date ON max_date.patient_id = p.patient_id"
            + "                            WHERE  pg.location_id = :location"
            + "                            AND pg.program_id = ${2}"
            + "                            AND ps.state = ${7}"
            + "                            GROUP BY p.patient_id"
            + "                           ) transferout  "
            + "                   GROUP  BY transferout.patient_id) max_transferout  "
            + "           WHERE  max_transferout.patient_id NOT IN (SELECT p.patient_id  "
            + "                                                     FROM   patient p  "
            + "                                                            JOIN encounter e  "
            + "                                                              ON p.patient_id =  "
            + "                                                                 e.patient_id  "
            + "                                                     WHERE  p.voided = 0  "
            + "                                                            AND e.voided = 0  "
            + "                                                            AND e.encounter_type =  ${6}   "
            + "                                                            AND e.location_id = :location  "
            + "                                                            AND  "
            + "                         e.encounter_datetime > transferout_date  "
            + "                                                            AND  "
            + "                         e.encounter_datetime <= CURRENT_DATE()  "
            + "                                                     UNION  "
            + "                                                     SELECT p.patient_id  "
            + "                                                     FROM   patient p  "
            + "                                                            JOIN encounter e  "
            + "                                                              ON p.patient_id =  "
            + "                                                                 e.patient_id  "
            + "                                                            JOIN obs o  "
            + "                                                              ON e.encounter_id =  "
            + "                                                                 o.encounter_id  "
            + "                                                     WHERE  p.voided = 0  "
            + "                                                            AND e.voided = 0  "
            + "                                                            AND e.encounter_type =  ${52}   "
            + "                                                            AND e.location_id = :location  "
            + "                                                            AND o.concept_id =  ${23866}   "
            + "                                                            AND o.value_datetime >  "
            + "                                                                transferout_date  "
            + "                                                            AND o.value_datetime <= CURRENT_DATE()"
            + "                                                                "
            + "                                                                UNION"
            + "                                                                "
            + "                                                          SELECT p.patient_id"
            + "                                                            FROM   patient p"
            + "                                                                    JOIN encounter e ON p.patient_id = e.patient_id"
            + "                                                            WHERE  p.voided = 0"
            + "                                                                    AND e.voided = 0"
            + "                                                                    AND e.encounter_type IN (${9},${18})"
            + "                                                                    AND e.location_id = :location"
            + "                                                                    AND e.encounter_datetime > transferout_date"
            + "                                                                    AND e.encounter_datetime <= CURRENT_DATE()) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>E3</b> - exclude all patients who stopped/suspended treatment by end of the reporting
   * period,
   *
   * <p><b>3.3</b> - Except all patients who after the most recent date from 3.1 to 3.2, have a
   * drugs pick up or consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most
   * recent date and <= Report Generation Date or Encounter Type ID = 52 and “Data de Levantamento”
   * (Concept Id 23866 value_datetime) > the most recent date and <= Report Generation Date
   * Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date and <= Report
   * Generation Date or Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866
   * value_datetime) > the most recent date and <= Report Generation Date
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getPatientsConsultationAfterMostRecentE3() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "all patients who after the most recent date from 3.1 to 3.2, have a drugs pick up or consultation");
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("8", hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "  SELECT patient_id  "
            + "           FROM   (SELECT transferout.patient_id,  "
            + "                          Max(transferout.transferout_date) transferout_date  "
            + "                   FROM   (SELECT p.patient_id,  "
            + "                                  Max(e.encounter_datetime) AS transferout_date  "
            + "                           FROM   patient p  "
            + "                                  JOIN encounter e  "
            + "                                    ON p.patient_id = e.patient_id  "
            + "                                  JOIN obs o  "
            + "                                    ON e.encounter_id = o.encounter_id  "
            + "                           WHERE  p.voided = 0  "
            + "                                  AND e.voided = 0  "
            + "                                  AND e.location_id = :location  "
            + "                                  AND e.encounter_type =  ${6}   "
            + "                                  AND e.encounter_datetime <= CURRENT_DATE()  "
            + "                                  AND o.voided = 0  "
            + "                                  AND o.concept_id =  ${6273}  "
            + "                                  AND o.value_coded =  ${1709}   "
            + "                           GROUP  BY p.patient_id  "
            + "                           UNION  "
            + "                           SELECT p.patient_id,  "
            + "                                  Max(o.obs_datetime) AS transferout_date  "
            + "                           FROM   patient p  "
            + "                                  JOIN encounter e  "
            + "                                    ON p.patient_id = e.patient_id  "
            + "                                  JOIN obs o  "
            + "                                    ON e.encounter_id = o.encounter_id  "
            + "                           WHERE  p.voided = 0  "
            + "                                  AND e.voided = 0  "
            + "                                  AND e.location_id = :location  "
            + "                                  AND e.encounter_type =  ${53}   "
            + "                                  AND o.obs_datetime <= CURRENT_DATE()  "
            + "                                  AND o.voided = 0  "
            + "                                  AND o.concept_id =  ${6272}   "
            + "                                  AND o.value_coded =  ${1709}   "
            + "                           GROUP  BY p.patient_id"
            + "                           "
            + "                           UNION"
            + "                           "
            + "                           SELECT p.patient_id, Max(ps.start_date) AS transferout_date"
            + "                             FROM   patient p"
            + "                                 INNER JOIN patient_program pg"
            + "                                     ON p.patient_id = pg.patient_id"
            + "                                 INNER JOIN patient_state ps"
            + "                                     ON pg.patient_program_id = ps.patient_program_id"
            + "                                 INNER JOIN (SELECT p.patient_id"
            + "                                             FROM   patient p"
            + "                                                 INNER JOIN (SELECT pp.patient_id, Max(ps.start_date) AS max_startDate"
            + "                                                             FROM   patient pp"
            + "                                                                 INNER JOIN patient_program pg"
            + "                                                                     ON pp.patient_id = pg.patient_id"
            + "                                                                 INNER JOIN patient_state ps"
            + "                                                                     ON pg.patient_program_id = ps.patient_program_id"
            + "                                                 WHERE  pg.location_id = :location"
            + "                                                   AND ps.start_date IS NOT NULL AND ps.end_date IS NULL"
            + "                                                 GROUP  BY pp.patient_id) AS tbl"
            + "                                                     ON p.patient_id = tbl.patient_id"
            + "                                 WHERE  p.voided = 0"
            + "                                   AND tbl.max_startdate <= CURRENT_DATE()"
            + "                                 GROUP BY p.patient_id) AS max_date ON max_date.patient_id = p.patient_id"
            + "                            WHERE  pg.location_id = :location"
            + "                            AND pg.program_id = ${2}"
            + "                            AND ps.state = ${8}"
            + "                            GROUP BY p.patient_id"
            + "                           ) transferout  "
            + "                   GROUP  BY transferout.patient_id) max_transferout  "
            + "           WHERE  max_transferout.patient_id NOT IN (SELECT p.patient_id  "
            + "                                                     FROM   patient p  "
            + "                                                            JOIN encounter e  "
            + "                                                              ON p.patient_id =  "
            + "                                                                 e.patient_id  "
            + "                                                     WHERE  p.voided = 0  "
            + "                                                            AND e.voided = 0  "
            + "                                                            AND e.encounter_type =  ${6}   "
            + "                                                            AND e.location_id = :location  "
            + "                                                            AND  "
            + "                         e.encounter_datetime > transferout_date  "
            + "                                                            AND  "
            + "                         e.encounter_datetime <= CURRENT_DATE()  "
            + "                                                     UNION  "
            + "                                                     SELECT p.patient_id  "
            + "                                                     FROM   patient p  "
            + "                                                            JOIN encounter e  "
            + "                                                              ON p.patient_id =  "
            + "                                                                 e.patient_id  "
            + "                                                            JOIN obs o  "
            + "                                                              ON e.encounter_id =  "
            + "                                                                 o.encounter_id  "
            + "                                                     WHERE  p.voided = 0  "
            + "                                                            AND e.voided = 0  "
            + "                                                            AND e.encounter_type =  ${52}   "
            + "                                                            AND e.location_id = :location  "
            + "                                                            AND o.concept_id =  ${23866}   "
            + "                                                            AND o.value_datetime >  "
            + "                                                                transferout_date  "
            + "                                                            AND o.value_datetime <= CURRENT_DATE()"
            + "                                                                "
            + "                                                                UNION"
            + "                                                                "
            + "                                                          SELECT p.patient_id"
            + "                                                            FROM   patient p"
            + "                                                                    JOIN encounter e ON p.patient_id = e.patient_id"
            + "                                                            WHERE  p.voided = 0"
            + "                                                                    AND e.voided = 0"
            + "                                                                    AND e.encounter_type IN (${9},${18})"
            + "                                                                    AND e.location_id = :location"
            + "                                                                    AND e.encounter_datetime > transferout_date"
            + "                                                                    AND e.encounter_datetime <= CURRENT_DATE()) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Have Ficha clinica (encounter type 6) with “TRATAMENTO DE TUBERCULOSE” (concept_id 1268) value
   * coded “Inicio” (concept_id IN 1256) or “Data início” (concept id 1113) and obs_datetime between
   * (for encounter type 6) Report Generation Date - 7months (210 days) and Report Generation Date
   * (obs_datetime >=reportGenerationDate – 7 months (210 days) and <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getTBTreatment() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All with “TRATAMENTO DE TUBERCULOSE” on Ficha clinica");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1113", hivMetadata.getTBDrugStartDateConcept().getConceptId());
    map.put("1268", hivMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p  "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type = ${6} "
            + "    AND o.concept_id = ${1268} AND o.value_coded IN (${1256},${1113}) "
            + "    AND e.location_id = :location "
            + "    AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.obs_datetime BETWEEN DATE_SUB(CURRENT_DATE(),INTERVAL 210 DAY) AND CURRENT_DATE() ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Have Ficha clinica (encounter type 6 or 9) with “ DATA DE INICIO DO TRATAMENTO DE
   * TB”(concept_id 1113, Value_datetime) between Report Generation Date - 7months (210 days) and
   * Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210 days) and
   * <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getStartTBTreatment() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All with “DATA DE INICIO DO TRATAMENTO DE TB” on Ficha clinica");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1113", hivMetadata.getTBDrugStartDateConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p  "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type IN (${6},${9}) "
            + "    AND o.concept_id = ${1113} "
            + "    AND e.location_id = :location "
            + "    AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.value_datetime BETWEEN DATE_SUB(CURRENT_DATE(),INTERVAL 210 DAY) AND CURRENT_DATE() ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * On Ficha Resumo (encounter type 53) have “Outros diagnósticos" (concept id 1406) with
   * “Tuberculose” (concept id 42) marked and obs datetime between Report Generation Date - 7months
   * (210 days) and Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210
   * days) and <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getMasterCardDiagnosesTB() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All with “Outros diagnósticos” on Ficha Resumo");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1406", hivMetadata.getOtherDiagnosis().getConceptId());
    map.put("42", tbMetadata.getPulmonaryTB().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p  "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.encounter_type = ${53} "
            + "    AND o.concept_id = ${1406} AND o.value_coded = ${42} "
            + "    AND e.location_id = :location "
            + "    AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(CURRENT_DATE(),INTERVAL 210 DAY) AND CURRENT_DATE()";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Enrolled on TB program (program id 5) patient state id = 6269 and start date >=
   * reportGenerationDate - 7months (210 days) and Report Generation Date (obs_datetime
   * >=reportGenerationDate – 7 months (210 days) and <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getEnrolledTBProgram() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All Enrolled on TB program");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("5", hivMetadata.getTBProgram().getProgramId());
    map.put("6269", hivMetadata.getActiveOnProgramConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p "
            + "                        INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "                        INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "                    WHERE pg.voided=0  "
            + "                        AND ps.voided=0  "
            + "                        AND p.voided=0 "
            + "                        AND pg.program_id= ${5}  "
            + "                        AND ps.patient_state_id=${6269}  "
            + "                        AND ps.start_date BETWEEN DATE_SUB(CURRENT_DATE(),INTERVAL 210 DAY) AND CURRENT_DATE()";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Have Ficha clinica (encounter type 6) with “Diagnótico TB activo” (concept_id 23761) value
   * coded “SIM”(concept id 1065): Encounter_datetime between reportGenerationDate - 7months (210
   * days) and Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210 days) and
   * <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getDiagnosesTBActive() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All with “Diagnótico TB activo” on Ficha clinica");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23761", tbMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id FROM patient p  "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.encounter_type = ${6} "
            + "    AND o.concept_id = ${23761} AND o.value_coded = ${1065} "
            + "    AND e.location_id = :location "
            + "    AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(CURRENT_DATE(),INTERVAL 210 DAY) AND CURRENT_DATE()";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Print the Date (value_datetime) of the most recent “Recepcao Levantou ARV” (encounter type 52)
   * with concept “Levantou ARV” (concept_id 23865) set to “SIM” (Concept id 1065) until report end
   * date (encounter_datetime <= endDate)
   *
   * @return sqlCohortDefinition
   */
  public DataDefinition getLastDrugPickUpDate() {
    SqlPatientDataDefinition sqlCohortDefinition = new SqlPatientDataDefinition();
    sqlCohortDefinition.setName("All with “Diagnótico TB activo” on Ficha clinica");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT p.patient_id, o.value_datetime  "
            + "             FROM   patient p   "
            + "                 INNER JOIN encounter e   "
            + "                     ON p.patient_id = e.patient_id   "
            + "                 INNER JOIN obs o   "
            + "                     ON e.encounter_id = o.encounter_id   "
            + "                 INNER JOIN ( "
            + "                         SELECT pp.patient_id, MAX(ee.encounter_datetime) as e_datetime  "
            + "                         FROM   patient pp   "
            + "                             INNER JOIN encounter ee   "
            + "                                 ON pp.patient_id = ee.patient_id   "
            + "                             INNER JOIN obs oo   "
            + "                                 ON ee.encounter_id =oo.encounter_id   "
            + "                         WHERE  pp.voided = 0   "
            + "                             AND ee.voided = 0   "
            + "                             AND oo.voided = 0   "
            + "                             AND ee.location_id = :location  "
            + "                             AND ee.encounter_type = ${52}  "
            + "                             AND ee.encounter_datetime <= :endDate  "
            + "                             AND oo.concept_id = ${23865} "
            + "                             AND oo.value_coded = ${1065} "
            + "                         GROUP BY pp.patient_id  "
            + "                               ) most_recent  ON p.patient_id = most_recent.patient_id    "
            + "             WHERE  p.voided = 0   "
            + "                 AND e.voided = 0   "
            + "                 AND o.voided = 0   "
            + "                 AND e.location_id = :location  "
            + "                 AND e.encounter_type = ${52}  "
            + "                 AND o.concept_id = ${23866} "
            + "                 AND o.value_datetime <= :endDate  "
            + "                 AND e.encounter_datetime = most_recent.e_datetime ;";

    StringSubstitutor substitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(substitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Print the Date (value_datetime) of the most recent “Recepcao Levantou ARV” (encounter type 52)
   * with concept “Levantou ARV” (concept_id 23865) set to “SIM” (Concept id 1065) until report end
   * date (encounter_datetime <= endDate)
   *
   * @return sqlCohortDefinition
   */
  public DataDefinition getNextDrugPickUpDateARV() {
    SqlPatientDataDefinition sqlCohortDefinition = new SqlPatientDataDefinition();
    sqlCohortDefinition.setName("All with “Diagnótico TB activo” on Ficha clinica");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT p.patient_id, DATE_ADD(o.value_datetime, INTERVAL 30 DAY)  "
            + "             FROM   patient p   "
            + "                 INNER JOIN encounter e   "
            + "                     ON p.patient_id = e.patient_id   "
            + "                 INNER JOIN obs o   "
            + "                     ON e.encounter_id = o.encounter_id   "
            + "                 INNER JOIN ( "
            + "                         SELECT pp.patient_id, MAX(ee.encounter_datetime) as e_datetime  "
            + "                         FROM   patient pp   "
            + "                             INNER JOIN encounter ee   "
            + "                                 ON pp.patient_id = ee.patient_id   "
            + "                             INNER JOIN obs oo   "
            + "                                 ON ee.encounter_id =oo.encounter_id   "
            + "                         WHERE  pp.voided = 0   "
            + "                             AND ee.voided = 0   "
            + "                             AND oo.voided = 0   "
            + "                             AND ee.location_id = :location  "
            + "                             AND ee.encounter_type = ${52}  "
            + "                             AND ee.encounter_datetime <= :endDate  "
            + "                             AND oo.concept_id = ${23865} "
            + "                             AND oo.value_coded = ${1065} "
            + "                         GROUP BY pp.patient_id  "
            + "                               ) most_recent  ON p.patient_id = most_recent.patient_id    "
            + "             WHERE  p.voided = 0   "
            + "                 AND e.voided = 0   "
            + "                 AND o.voided = 0   "
            + "                 AND e.location_id = :location  "
            + "                 AND e.encounter_type = ${52}  "
            + "                 AND o.concept_id = ${23866} "
            + "                 AND o.value_datetime <= :endDate  "
            + "                 AND e.encounter_datetime = most_recent.e_datetime ;";

    StringSubstitutor substitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(substitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Have Ficha clinica (encounter type 6) with “TRATAMENTO DE TUBERCULOSE” (concept_id 1268) value
   * coded “Inicio” (concept_id IN 1256) or “Data início” (concept id 1113) and obs_datetime between
   * (for encounter type 6) Report Generation Date - 7months (210 days) and Report Generation Date
   * (obs_datetime >=reportGenerationDate – 7 months (210 days) and <=reportGenerationDate)
   *
   * <p>OR
   *
   * <p>Have Ficha clinica (encounter type 6 or 9) with “ DATA DE INICIO DO TRATAMENTO DE
   * TB”(concept_id 1113, Value_datetime) between Report Generation Date - 7months (210 days) and
   * Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210 days) and
   * <=reportGenerationDate)
   *
   * <p>OR
   *
   * <p>On Ficha Resumo (encounter type 53) have “Outros diagnósticos" (concept id 1406) with
   * “Tuberculose” (concept id 42) marked and obs datetime between Report Generation Date - 7months
   * (210 days) and Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210
   * days) and <=reportGenerationDate)
   *
   * <p>OR
   *
   * <p>Enrolled on TB program (program id 5) patient state id = 6269 and start date >=
   * reportGenerationDate - 7months (210 days) and Report Generation Date (obs_datetime
   * >=reportGenerationDate – 7 months (210 days) and <=reportGenerationDate)
   *
   * <p>OR
   *
   * <p>Have Ficha clinica (encounter type 6) with “Diagnótico TB activo” (concept_id 23761) value
   * coded “SIM”(concept id 1065): Encounter_datetime between reportGenerationDate - 7months (210
   * days) and Report Generation Date (obs_datetime >=reportGenerationDate – 7 months (210 days) and
   * <=reportGenerationDate)
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getPatientsActiveOnTbTratment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition A = getTBTreatment();
    CohortDefinition B = getStartTBTreatment();
    CohortDefinition C = getMasterCardDiagnosesTB();
    CohortDefinition D = getEnrolledTBProgram();
    CohortDefinition E = getDiagnosesTBActive();

    cd.addSearch("A", EptsReportUtils.map(A, MAPPING));
    cd.addSearch("B", EptsReportUtils.map(B, MAPPING));
    cd.addSearch("C", EptsReportUtils.map(C, MAPPING));
    cd.addSearch("D", EptsReportUtils.map(D, MAPPING));
    cd.addSearch("E", EptsReportUtils.map(E, MAPPING));

    cd.setCompositionString("(A OR B OR C OR D OR E)");

    return cd;
  }

  /**
   * X - Filter all patients who are late for their last next scheduled pick-up within the period of
   * days of delay as follow: select all patients with “Data do próximo levantamento” (concept id
   * 5096, value_datetime) as “Last Next scheduled Pick-up Date” from the most recent FILA
   * (encounter type 18) by report end date(encounter_datetime <= endDate) and endDate minus “Last
   * Next scheduled Pick-up Date” >= minDays and <= maxDays OR select all patients with “Last Next
   * scheduled Pick up Date” (concept_id 23866, value_datetime + 30 days) from the most recent
   * “Recepcao Levantou ARV” (encounter type 52) with concept “Levantou ARV” (concept_id 23865) set
   * to “SIM” (Concept id 1065) by report end date (value_datetime <= endDate) and endDate minus
   * “Last Next scheduled Pick-up Date” >= minDays and <= maxDays
   *
   * @return
   */
  public CohortDefinition getLastARVRegimen() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Last ARV Regimen (FILA)");
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("minDay", "minDay", Integer.class));
    sqlCohortDefinition.addParameter(new Parameter("maxDay", "maxDay", Integer.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getYesConcept().getConceptId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String sql =
        " SELECT p.patient_id FROM patient p INNER JOIN "
            + "                         (SELECT last_next_pick_up.patient_id, MAX(last_next_pick_up.result_Value) AS max_datetame  FROM "
            + "                             ( SELECT p.patient_id, o.value_datetime AS  result_Value "
            + "                               FROM  patient p "
            + "                                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                         INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) as e_datetime "
            + "                                                     FROM   patient p "
            + "                                                                INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                     WHERE  p.voided = 0 "
            + "                                                       AND e.voided = 0 "
            + "                                                       AND e.location_id = :location "
            + "                                                       AND e.encounter_type =  ${18} "
            + "                                                       AND e.encounter_datetime <= :endDate "
            + "                                                     GROUP BY p.patient_id "
            + "                               ) most_recent  ON p.patient_id = most_recent.patient_id "
            + "                               WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type = ${18} "
            + "                                 AND o.concept_id = ${5096} "
            + "                                 AND e.encounter_datetime = most_recent.e_datetime "
            + "                               GROUP  BY p.patient_id "
            + " "
            + "                               UNION "
            + " "
            + "                               SELECT p.patient_id, DATE_ADD(Max(o.value_datetime), interval 30 DAY) AS  result_Value "
            + "                               FROM   patient p "
            + "                                          INNER JOIN encounter e ON p.patient_id =  e.patient_id "
            + "                                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                          INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                               WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND o2.voided = 0 "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_type =  ${52} "
            + "                                 AND (o.concept_id =  ${23866}   AND o.value_datetime <= :endDate) "
            + "                                 AND (o2.concept_id = ${23865}   AND o2.value_coded =  ${1065} ) "
            + "                               GROUP  BY p.patient_id "
            + "                             ) AS last_next_pick_up "
            + "                          GROUP  BY last_next_pick_up.patient_id) AS last_next_scheduled_pick_up ON last_next_scheduled_pick_up.patient_id = p.patient_id "
            + "WHERE TIMESTAMPDIFF(DAY, last_next_scheduled_pick_up.max_datetame, :endDate) >= :minDay "
            + "  AND TIMESTAMPDIFF(DAY, last_next_scheduled_pick_up.max_datetame, :endDate) <= :maxDay ";
    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(substitutor.replace(sql));
    return sqlCohortDefinition;
  }

  /** 6 */
  public DataDefinition getPatientsActiveOnTB() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("Active on TB");
    spdd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1268", hivMetadata.getTBTreatmentPlanConcept().getConceptId());
    valuesMap.put("1113", hivMetadata.getTBDrugStartDateConcept().getConceptId());
    valuesMap.put("1406", hivMetadata.getOtherDiagnosis().getConceptId());
    valuesMap.put("42", tbMetadata.getPulmonaryTB().getConceptId());
    valuesMap.put("6269", hivMetadata.getActiveOnProgramConcept().getConceptId());
    valuesMap.put("5", hivMetadata.getTBProgram().getProgramId());
    valuesMap.put("23761", hivMetadata.getActiveTBConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String sql =
        " SELECT final_query.patient_id, CASE WHEN final_query.result_Value IS NOT NULL THEN 'S' WHEN final_query.result_Value IS NULL THEN 'INACTIVE' ELSE '' END"
            + " FROM "
            + "( "
            + "                SELECT p.patient_id, o.value_coded  AS result_Value "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                WHERE p.voided = 0 "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND e.location_id = :location "
            + "                  AND e.encounter_type = ${6} "
            + "                  AND o.concept_id = ${1268} AND o.value_coded = ${1256} "
            + "                  AND o.obs_datetime BETWEEN DATE_SUB(CURRENT_DATE(), INTERVAL 210 DAY) AND CURRENT_DATE() "
            + "                  AND e.encounter_datetime <= CURRENT_DATE() "
            + "                UNION  "
            + "                SELECT p.patient_id, o.value_datetime AS result_Value "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                WHERE p.voided = 0 "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND e.location_id = :location "
            + "                  AND e.encounter_type IN (${6},${9}) "
            + "                  AND o.concept_id = ${1113} "
            + "                  AND o.value_datetime "
            + "                            BETWEEN DATE_SUB( CURRENT_DATE(), INTERVAL 210 DAY ) AND CURRENT_DATE() "
            + "                  AND e.encounter_datetime <= CURRENT_DATE() "
            + "                UNION  "
            + "                SELECT p.patient_id, o.value_coded AS result_Value "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                WHERE p.voided = 0 "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND e.location_id = :location "
            + "                  AND e.encounter_type = ${53} "
            + "                  AND o.concept_id = ${1406} "
            + "                  AND o.value_coded = ${42} "
            + "                  AND o.obs_datetime "
            + "                    BETWEEN DATE_SUB( CURRENT_DATE(), INTERVAL 210 DAY ) AND CURRENT_DATE() "
            + "                UNION  "
            + "                SELECT p.patient_id , cn.name AS result_Value "
            + "                FROM patient p "
            + "                         INNER JOIN patient_program pp ON p.patient_id = pp.patient_id "
            + "                         INNER JOIN program pgr ON pp.program_id = pgr.program_id "
            + "                         INNER JOIN patient_state ps on pp.patient_program_id = ps.patient_program_id "
            + "                         INNER JOIN program_workflow_state pws  on ps.state = pws.program_workflow_state_id          "
            + "                         INNER JOIN concept_name cn  on pws.concept_id = cn.concept_id          "
            + "                WHERE p.voided = 0 "
            + "                  AND pp.voided = 0 "
            + "                  AND ps.voided = 0 "
            + "                  AND ps.state = ${6269} "
            + "                  AND pgr.program_id = ${5} "
            + "                  AND cn.locale = 'pt' "
            + "                  AND ps.start_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 210 DAY) "
            + "                  AND ps.end_date <= CURRENT_DATE() "
            + "               UNION  "
            + "                SELECT p.patient_id, o.value_coded  AS result_Value "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                WHERE p.voided = 0 "
            + "                  AND e.voided = 0 "
            + "                  AND o.voided = 0 "
            + "                  AND e.encounter_type = ${6} "
            + "                  AND e.location_id = :location "
            + "                  AND o.concept_id = ${23761} "
            + "                  AND o.value_coded = ${1065} "
            + "                  AND e.encounter_datetime "
            + "                    BETWEEN DATE_SUB( CURRENT_DATE(), INTERVAL 210 DAY ) AND CURRENT_DATE() "
            + ") AS final_query";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  /**
   * 12 - Address (Localidade) – Sheet 1: Column K
   *
   * @return sqlCohortDefinition
   */
  public DataDefinition getLocation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get Patient Location");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    String query =
        "SELECT address.patient_id,address.location "
            + "FROM   (SELECT p.patient_id,pa.address6 location "
            + "        FROM   patient p "
            + "               INNER JOIN person pr ON p.patient_id = pr.person_id "
            + "               INNER JOIN person_address pa ON pa.person_id = pr.person_id "
            + "        WHERE  p.voided = 0 "
            + "               AND pr.voided = 0 "
            + "        ORDER  BY pa.person_address_id DESC) address "
            + "GROUP  BY address.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }
  /**
   * 11 - Contacto – Sheet 1: Column K
   *
   * @return sqlCohortDefinition
   */
  public DataDefinition getContact() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get Patient Contact");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    String query =
        " SELECT p.patient_id, pc.telemovel FROM patient p "
            + "    INNER JOIN paciente_com_celular pc on p.patient_id = pc.patient_id "
            + " WHERE p.voided =0 GROUP BY p.patient_id;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * 13 - Address (Bairro) – Sheet 1: Column M
   *
   * @return sqlPatientDataDefinition
   */
  public DataDefinition getNeighborhood() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get Patient Neighborhood");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    String query =
        "SELECT address.patient_id,address.neighborhood "
            + "FROM  (SELECT p.patient_id,pa.address5 neighborhood "
            + "       FROM   patient p "
            + "              INNER JOIN person_address pa ON pa.person_id = p.patient_id "
            + "       WHERE  p.voided = 0 "
            + "       ORDER  BY pa.person_address_id DESC) address "
            + "GROUP  BY address.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * 14 - Address (Ponto de Referencia) – Sheet 1: Column N
   *
   * @return sqlPatientDataDefinition
   */
  public DataDefinition getReferencePoint() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get Patient Reference Point");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    String query =
        "SELECT address.patient_id,address.reference_point "
            + "FROM   (SELECT p.patient_id,pa.address1 reference_point "
            + "        FROM   patient p "
            + "               INNER JOIN person_address pa "
            + "                       ON pa.person_id = p.patient_id "
            + "        WHERE  p.voided = 0 "
            + "        ORDER  BY pa.person_address_id DESC) address "
            + "GROUP  BY address.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>PRINT ‘S’ IF THE PATIENT HAS ONE OF THE FOLLOWING OPTIONS</b>
   *
   * <p>All patients marked as “Sim” (concept id 1065) on “O paciente/ cuidador concorda em ser
   * contactado, se necessário?” (Concept Id 6306) on the most recent Ficha APSS e PP (Encounter
   * Type 35) and “Data de Consentimento” (concept_id 23776) is before report generation date
   * (value_datetime < Report Generation Date) <b>OR</b>
   *
   * <p>All patients marked as “Sim” (concept id 1065) on “O confidente concorda em ser contactado,
   * se necessário?” (Concept Id 6306 6177) on the most recent Ficha APSS e PP (Encounter Type 35)
   * and “Data de Consentimento” (concept_id 23776) is before report generation date (value_datetime
   * < Report Generation Date)
   *
   * @return sqlPatientDataDefinition
   */
  public DataDefinition getPatientsConfidentConcent(Concept confidentConcent) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("All patients marked as: No");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("confidentConcent", confidentConcent.getConceptId());
    map.put("1066", hivMetadata.getNoConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("6177", hivMetadata.getConfidentAcceptContact().getConceptId());
    map.put("23776", hivMetadata.getConfidentConsentDate().getConceptId());

    String query =
        "SELECT p.patient_id, CASE WHEN patient_yes.patient_id IS NOT NULL THEN 'S' WHEN patient_no.patient_id IS NOT NULL THEN 'N' ELSE ''  END  "
            + " FROM   patient p  "
            + "             LEFT JOIN (SELECT  p.patient_id  FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id INNER JOIN   "
            + "             (SELECT p.patient_id, MAX(e.encounter_datetime) most_recent   "
            + "              FROM patient p  "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                WHERE e.encounter_type = ${35}   "
            + "                 AND o.concept_id = ${23776}   "
            + "                 AND p.voided = 0  "
            + "                 AND e.voided = 0  "
            + "                 AND o.voided = 0  "
            + "                 AND e.location_id = :location  "
            + "                 AND o.value_datetime < current_date()  "
            + "              GROUP BY p.patient_id  "
            + "             ) AS max_apss ON p.patient_id = max_apss.patient_id  "
            + "                WHERE e.encounter_type =  ${35}   "
            + "                  AND e.encounter_datetime = max_apss.most_recent   "
            + "                  AND e.location_id = :location  "
            + "                  AND o.concept_id = ${confidentConcent}                      "
            + "                  AND o.value_coded =${1065}                      "
            + "                  AND p.voided = 0  "
            + "                  AND e.voided = 0  "
            + "                  AND o.voided = 0) AS patient_yes  ON p.patient_id = patient_yes.patient_id"
            + "            LEFT JOIN (SELECT  p.patient_id  FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id INNER JOIN   "
            + "             (SELECT p.patient_id, MAX(e.encounter_datetime) most_recent   "
            + "              FROM patient p  "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                WHERE e.encounter_type =  ${35}   "
            + "                 AND o.concept_id =  ${23776}   "
            + "                 AND p.voided = 0  "
            + "                 AND e.voided = 0  "
            + "                 AND o.voided = 0  "
            + "                 AND e.location_id = :location  "
            + "                 AND o.value_datetime < current_date()  "
            + "              GROUP BY p.patient_id  "
            + "             ) AS max_apss ON p.patient_id = max_apss.patient_id  "
            + "                WHERE e.encounter_type =  ${35}    "
            + "                  AND e.encounter_datetime = max_apss.most_recent   "
            + "                  AND e.location_id = :location  "
            + "                  AND o.concept_id = ${confidentConcent}                      "
            + "                  AND o.value_coded =  ${1066}                      "
            + "                  AND p.voided = 0  "
            + "                  AND e.voided = 0  "
            + "                  AND o.voided = 0) AS patient_no  ON p.patient_id = patient_no.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Days of Delay </b> - Sheet 1: Column T
   *
   * <p>Days of Delay = Reporting End Date - Next Scheduled Pick Up Date Next Scheduled Pick Up Date
   * should be defined as following: If (Last Drug Pick-up Date - Sheet 1: Column P >= Last Drug
   * Pick-up Date - Sheet 1: Column Q) Return “Data do próximo levantamento” (concept id 5096,
   * value_datetime) of the most recent FILA (encounter type 18) until report end
   * date(encounter_datetime <= endDate)
   *
   * <p>If (Last Drug Pick-up Date - Sheet 1: Column P < Last Drug Pick-up Date - Sheet 1: Column Q)
   * Return the Date (value_datetime) +30 days of the most recent “Recepcao Levantou ARV” (encounter
   * type 52) with concept “Levantou ARV” (concept_id 23865) set to “SIM” (Concept id 1065) by
   * report end date (encounter_datetime <= endDate)
   *
   * @return sqlCohortDefinition
   */
  public DataDefinition getNumberOfDaysOfDelay() {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("THE NUMBER OF DAYS OF DELAY");
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "  SELECT p.patient_id,CASE WHEN last_next_scheduled_pick_up.patient_id IS NOT NULL  THEN  TIMESTAMPDIFF(DAY, last_next_scheduled_pick_up.max_datetame,:endDate)  "
            + "                                  END     FROM patient p INNER JOIN  "
            + "                                     (SELECT last_next_pick_up.patient_id, MAX(last_next_pick_up.result_Value) AS max_datetame  FROM  "
            + "                                         ( SELECT p.patient_id, o.value_datetime AS  result_Value  "
            + "                                           FROM  patient p  "
            + "                                                     INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                                                     INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) as e_datetime  "
            + "                                                                 FROM   patient p  "
            + "                                                                            INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                                                 WHERE  p.voided = 0  "
            + "                                                                   AND e.voided = 0  "
            + "                                                                   AND e.location_id = :location  "
            + "                                                                   AND e.encounter_type =   ${18}   "
            + "                                                                   AND e.encounter_datetime <= :endDate  "
            + "                                                                 GROUP BY p.patient_id  "
            + "                                           ) most_recent  ON p.patient_id = most_recent.patient_id  "
            + "                                           WHERE  p.voided = 0  "
            + "                                             AND e.voided = 0  "
            + "                                             AND o.voided = 0  "
            + "                                             AND e.location_id = :location  "
            + "                                             AND e.encounter_type =  ${18}   "
            + "                                             AND o.concept_id =  ${5096}   "
            + "                                             AND e.encounter_datetime = most_recent.e_datetime  "
            + "                                           GROUP  BY p.patient_id  "
            + "              "
            + "                                           UNION  "
            + "              "
            + "                                           SELECT p.patient_id, DATE_ADD(Max(o.value_datetime), interval 30 DAY) AS  result_Value  "
            + "                                           FROM   patient p  "
            + "                                                      INNER JOIN encounter e ON p.patient_id =  e.patient_id  "
            + "                                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                                                      INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id  "
            + "                                           WHERE  p.voided = 0  "
            + "                                             AND e.voided = 0  "
            + "                                             AND o.voided = 0  "
            + "                                             AND o2.voided = 0  "
            + "                                             AND e.location_id = :location  "
            + "                                             AND e.encounter_type =   ${52}   "
            + "                                             AND (o.concept_id =   ${23866}    AND o.value_datetime <= :endDate)  "
            + "                                             AND (o2.concept_id = ${23865}    AND o2.value_coded =   ${1065}  )  "
            + "                                           GROUP  BY p.patient_id  "
            + "                                         ) AS last_next_pick_up  "
            + "                                      GROUP  BY last_next_pick_up.patient_id) AS last_next_scheduled_pick_up ON last_next_scheduled_pick_up.patient_id = p.patient_id  ";
    StringSubstitutor substitutor = new StringSubstitutor(map);

    spdd.setQuery(substitutor.replace(query));
    return spdd;
  }
  /**
   * <b>B</b>
   *
   * <p>Select all patients who were transferred-in from another HF by end of reporting period as
   * follows: 6.1 Transferred-in patients TARV (by end of the period) => defined inCommon queries
   * changes
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getPatientsTransferredInTarv() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "all patients who were transferred-in from another HF by end of reporting period");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("2", hivMetadata.getARTProgram().getProgramId());
    map.put("29", hivMetadata.getHepatitisConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1369", commonMetadata.getTransferFromOtherFacilityConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("6276", hivMetadata.getArtStatus().getConceptId());
    map.put("6300", hivMetadata.getTypeOfPatientTransferredFrom().getConceptId());
    map.put("23891", hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId());

    String query =
        " SELECT pg.patient_id FROM patient p       "
            + "  INNER JOIN patient_program pg ON p.patient_id=pg.patient_id  "
            + "  INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id  "
            + "  WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0  "
            + "  AND pg.program_id=${2} AND ps.state=${29}  "
            + "  AND ps.start_date <= curdate() AND pg.location_id = :location "
            + "  UNION "
            + "  SELECT     p.patient_id    "
            + "  FROM       patient p "
            + "  INNER JOIN encounter e "
            + "  ON         p.patient_id = e.patient_id "
            + "  INNER JOIN obs o "
            + "  ON         e.encounter_id = o.encounter_id "
            + "  INNER JOIN obs oo "
            + "  ON         e.encounter_id = oo.encounter_id "
            + "  INNER JOIN "
            + "             (SELECT     p.patient_id, "
            + "                            e.encounter_datetime "
            + "                 FROM       patient p "
            + "                 INNER JOIN encounter e "
            + "                 ON         p.patient_id = e.patient_id "
            + "                 INNER JOIN obs o1 "
            + "                 ON         e.encounter_id = o1.encounter_id "
            + "                 WHERE    p.voided = 0 AND  e.voided = 0 "
            + "  AND        o1.voided = 0 and e.encounter_type = ${53} "
            + "                 AND        o1.concept_id = ${1369} AND e.location_id = :location "
            + "                 AND        o1.value_coded = ${1065}) tbl "
            + "  ON         tbl.patient_id = p.patient_id "
            + "  WHERE      p.voided = 0 "
            + "  AND        e.voided = 0 "
            + "  AND        o.voided = 0 AND e.location_id = :location "
            + "  AND        e.encounter_datetime = tbl.encounter_datetime "
            + "  AND        ( "
            + "                        oo.concept_id = ${6300} "
            + "             AND        oo.value_coded = ${6276}) "
            + "  AND        (  o.concept_id = ${23891}) GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>10: Tipo de Dispensa</b>
   *
   * <p>PRINT Dispensa Mensal/ Dispensa Trimestral/ Dispensa Semestral IF THE PATIENT HAS ONE OF THE
   * FOLLOWING OPTIONS:
   *
   * <p>Select the most recent from the following source: the most recent Drug Pick Pick up on Fila
   * ( encounter type 18, max encounter_datetime<= endDate) the most recent Consultation on Ficha
   * Clinica ( encounter type 6, max encounter_datetime<= endDate)
   *
   * <p>If the most recent source is FILA then: Print Dispensa Mensal if a minus b < 83 days where a
   * is the most recent Drug Pick Up on FILA ( encounter type 18, max encounter_datetime<=endDate)
   * and b is “RETURN VISIT DATE FOR ARV DRUG” (concept_id 5096, value_datetime) from the most
   * recent FILA ( encounter type 18, max encounter_datetime <= endDate) Encounter Type Id = 18
   * MAXIMUM Encounter DATE (encounter.encounter_datetime <= endDate) = a RETURN VISIT DATE FOR ARV
   * DRUG (Concept_id=5096) Value_datetime =b Difference between a and b is <83
   *
   * <p>Print Dispensa Trimestral if a minus b >= 83 days and <= 173 days, where a is the most
   * recent Drug Pick Up on FILA ( encounter type 18, max encounter_datetime <= endDate) and b is
   * “RETURN VISIT DATE FOR ARV DRUG” (concept_id 5096, value_datetime) from the most recent FILA (
   * encounter type 18, max encounter_datetime <= endDate)
   *
   * <p>Encounter Type Id = 18 MAXIMUM Encounter DATE (encounter.encounter_datetime <= endDate) = a
   * RETURN VISIT DATE FOR ARV DRUG (Concept_id=5096) Value_datetime =b Difference between a and b
   * is >=83 and <=173 days
   *
   * <p>Print Dispensa Semestral if a minus b > 173 days, where a is the most recent Drug Pick Up on
   * FILA ( encounter type 18, max encounter_datetime <= endDate) and b is “RETURN VISIT DATE FOR
   * ARV DRUG” (concept_id 5096, value_datetime) from the most recent FILA ( encounter type 18, max
   * encounter_datetime <= endDate)
   *
   * <p>Encounter Type Id = 18 MAXIMUM Encounter DATE (encounter.encounter_datetime) <= endDate= a
   * RETURN VISIT DATE FOR ARV DRUG (Concept_id=5096) Value_datetime =b Difference between a and b
   * is >173 days
   *
   * <p>If the most recent source is FICHA CLINICA then: Print Dispensa Mensal if “Tipo de
   * Levantamento” (concept_id=23739) is marked as “DM” (concept_id=1098) on the most recent
   * consultation on FICHA CLINICA ( encounter type 6, max encounter_datetime <= endDate) Encounter
   * Type Id = 6 Max encounter_datetime <= endDate Last TYPE OF DISPENSATION (id=23739) Value.coded
   * = MONTHLY (id=1098) OR
   *
   * <p>with one of the MDCs is marked as “DT - Dispensa Trimestral de ARV” with Estado do MDC as *
   * Iniciar (I) or Continuar (C) in the last Ficha Clinica with MDCs registered
   *
   * <p>Print Dispensa Trimestral if “Tipo de Levantamento” (concept_id=23739) is marked as “DT”
   * (concept_id=23720) on the most recent consultation on FICHA CLINICA ( encounter type 6, max
   * encounter_datetime <= endDate) Encounter Type Id = 6 Max encounter_datetime <= endDate Last
   * TYPE OF DISPENSATION (id=23739) Value.coded = QUARTERLY (id=23720) or marked in last “Dispensa
   * Trimestral (DT)” (concept_id=23720) as Iniciar (I) (concept_id=1256) or Continuar (C)
   * (concept_id=1257) in Ficha Clinica Mastercard ( encounter type 6, max encounter_datetime <=
   * endDate) Encounter Type Id = 6 Max encounter_datetime <= endDate Last QUARTERLY DISPENSATION
   * (DT) (id=23720) Value.coded= START DRUGS (id=1256) OR Value.coded= (CONTINUE REGIMEN id=1257)
   *
   * <p>Print Dispensa Semestral if “Tipo de Levantamento” (concept_id=23739) is marked as “DS”
   * (concept_id=23720) on the most recent consultation on FICHA CLINICA ( encounter type 6, max
   * encounter_datetime <= endDate) Encounter Type Id = 6 Max encounter_datetime <= endDate Last
   * TYPE OF DISPENSATION (id=23739) Value.coded = SEMESTRAL (id=23888) or marked in last “Dispensa
   * Semestral (DS)” (concept_id=23730) as Iniciar (I) (concept_id=1256) or Continuar (C)
   * (concept_id=1257) in Ficha Clinica Mastercard ( encounter type 6, max encounter_datetime <=
   * endDate) Encounter Type Id = 6 Max encounter_datetime <= endDate Last SEMESTRAL DISPENSATION
   * (DT) (id=23888) Value.coded= START DRUGS (id=1256) OR Value.coded= (CONTINUE REGIMEN id=1257)
   * OR
   *
   * <p>with one of the MDCs is marked as “DS - Dispensa Semestral de ARV” with Estado do MDC as *
   * Iniciar (I) or Continuar (C) in the last Ficha Clinica with MDCs registered <b>Print Dispensa
   * Anual </b>
   *
   * <p>next ART pick-up is scheduled for >334 days after the date of their last ART drug pick-up
   * (FILA) or
   *
   * <p>owith one of the MDCs is marked as “DA - Dispensa Anual de ARV” with Estado do MDC as
   * Iniciar (I) or Continuar (C) in the last Ficha Clinica with MDCs registered
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getTypeOfDispensation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();

    sqlPatientDataDefinition.setName("get Type Of Dispensation");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    map.put("23730", hivMetadata.getQuarterlyDispensation().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());
    map.put("1098", hivMetadata.getMonthlyConcept().getConceptId());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId());
    map.put("165322", hivMetadata.getMdcState().getConceptId());
    map.put("165314", hivMetadata.getAnnualArvDispensationConcept().getConceptId());

    String query =
        "SELECT result.patient_id,result.dispensa "
            + "FROM ( "
            + "         SELECT en.patient_id, 'Dispensa Mensal' AS dispensa FROM "
            + "             (SELECT p.patient_id,Max(e.encounter_datetime) AS encounter_date "
            + "              FROM   patient p "
            + "                         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              WHERE  p.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND e.encounter_datetime <= :endDate "
            + "                AND (e.encounter_type = ${18} OR (e.encounter_type = ${6} AND o.concept_id = ${23739})) "
            + "              GROUP  BY p.patient_id) AS most_recent "
            + "                 INNER JOIN encounter en ON en.patient_id = most_recent.patient_id "
            + "                 AND en.encounter_datetime = most_recent.encounter_date "
            + "                 INNER JOIN obs ob ON ob.encounter_id = en.encounter_id "
            + "         WHERE en.voided = 0 "
            + "           AND ob.voided = 0 "
            + "           AND en.location_id = :location "
            + "           AND (( en.encounter_type = ${18} "
            + "             AND ob.concept_id = ${5096} "
            + "             AND ob.value_datetime IS NOT NULL "
            + "             AND Timestampdiff(DAY, most_recent.encounter_date,ob.value_datetime) < 83 "
            + "                    ) OR ( "
            + "                            en.encounter_type = ${6} "
            + "                        AND ob.concept_id = ${23739} "
            + "                        AND ob.value_coded = ${1098} "
            + "                    )) GROUP BY en.patient_id "
            + " "
            + "         UNION "
            + " "
            + "         SELECT    dispensa_trimestral.patient_id, 'Dispensa Trimestral'  AS dispensa FROM ( "
            + "           SELECT    en.patient_id "
            + "            FROM "
            + "             (SELECT "
            + "                  e.patient_id, MAX(e.encounter_datetime) AS encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${18}  AND p.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id "
            + "UNION "
            + "SELECT "
            + "                                              e.patient_id, MAX(e.encounter_datetime) encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${6} "
            + "                AND (o.concept_id in ( ${23888} , ${23730} ) AND o.value_coded IN ( ${1256}  ,  ${1257} ) OR o.concept_id =  ${23739} ) "
            + "                AND p.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id) AS last_encounter "
            + "                 INNER JOIN "
            + "             encounter en ON en.patient_id = last_encounter.patient_id "
            + "                 AND DATE(en.encounter_datetime) = DATE(last_encounter.encounter_date) "
            + "                 INNER JOIN "
            + "             obs ob ON ob.encounter_id = en.encounter_id "
            + "         WHERE   en.voided = 0 AND ob.voided = 0 "
            + "           AND en.location_id = :location "
            + "           AND ((en.encounter_type =  ${18} "
            + "             AND ob.concept_id =  ${5096} "
            + "             AND ob.value_datetime IS NOT NULL "
            + "             AND TIMESTAMPDIFF(DAY, "
            + "                               DATE(last_encounter.encounter_date), "
            + "                               ob.value_datetime) BETWEEN 83 AND 173) "
            + "             OR (en.encounter_type =  ${6} "
            + "                 AND (ob.concept_id =  ${23739} "
            + "                     AND ob.value_coded =  ${23720} )) "
            + "                    AND en.patient_id NOT IN (SELECT "
            + "                                                  list.patient_id "
            + "                                              FROM "
            + "                                                  encounter list "
            + "                                              WHERE "
            + "                                                      list.patient_id = en.patient_id "
            + "                                                AND DATE(list.encounter_datetime) = DATE(last_encounter.encounter_date) "
            + "                                                AND TIMESTAMPDIFF(DAY, "
            + "                                                                  DATE(last_encounter.encounter_date), "
            + "                                                                  (SELECT "
            + "                                                                       MAX(o.value_datetime) "
            + "                                                                   FROM "
            + "                                                                       encounter e "
            + "                                                                           INNER JOIN "
            + "                                                                       obs o ON o.encounter_id = e.encounter_id "
            + "                                                                   WHERE "
            + "                                                                           e.voided = 0 AND o.voided = 0 "
            + "                                                                     AND e.patient_id = list.patient_id "
            + "                                                                     AND o.concept_id =  ${5096} "
            + "                                                                     AND o.value_datetime IS NOT NULL "
            + "                                                                     AND e.encounter_type =  ${18} "
            + "                                                                     AND e.location_id = :location "
            + "                                                                     AND DATE(e.encounter_datetime) = DATE(list.encounter_datetime) "
            + "                                                                   GROUP BY e.patient_id)) > 173) "
            + "                    AND en.patient_id NOT IN (SELECT "
            + "                                                  list.patient_id "
            + "                                              FROM "
            + "                                                  encounter list "
            + "                                              WHERE "
            + "                                                      list.patient_id = en.patient_id "
            + "                                                AND DATE(list.encounter_datetime) = DATE(last_encounter.encounter_date) "
            + "                                                AND TIMESTAMPDIFF(DAY, "
            + "                                                                  DATE(last_encounter.encounter_date), "
            + "                                                                  (SELECT "
            + "                                                                       MAX(o.value_datetime) "
            + "                                                                   FROM "
            + "                                                                       encounter e "
            + "                                                                           INNER JOIN "
            + "                                                                       obs o ON o.encounter_id = e.encounter_id "
            + "                                                                   WHERE "
            + "                                                                           e.voided = 0 AND o.voided = 0 "
            + "                                                                     AND e.patient_id = list.patient_id "
            + "                                                                     AND o.concept_id =  ${5096} "
            + "                                                                     AND o.value_datetime IS NOT NULL "
            + "                                                                     AND e.encounter_type =  ${18} "
            + "                                                                     AND e.location_id = :location "
            + "                                                                     AND DATE(e.encounter_datetime) = DATE(list.encounter_datetime) "
            + "                                                                   GROUP BY e.patient_id)) < 173)) "
            + "         GROUP BY en.patient_id "
            + "         UNION "
            + getDispensationTypeOnMDCQuery(hivMetadata.getQuarterlyDispensation())
            + "          ) dispensa_trimestral GROUP BY dispensa_trimestral.patient_id "
            + "         UNION "
            + "         SELECT dispensa_semestral.patient_id , 'Dispensa Semestral' AS dispensa FROM ( "
            + "         SELECT en.patient_id "
            + "         FROM "
            + "             (SELECT "
            + "                  e.patient_id, MAX(e.encounter_datetime) AS encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${18}  AND p.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id UNION SELECT "
            + "                                              e.patient_id, MAX(e.encounter_datetime) encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${6} "
            + "                AND (o.value_coded IN ( ${1256}  ,  ${1257} ) OR o.concept_id =  ${23739} ) "
            + "                AND p.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id) AS last_encounter "
            + "                 INNER JOIN "
            + "             encounter en ON en.patient_id = last_encounter.patient_id "
            + "                 AND DATE(en.encounter_datetime) = DATE(last_encounter.encounter_date) "
            + "                 INNER JOIN "
            + "             obs ob ON ob.encounter_id = en.encounter_id "
            + "         WHERE "
            + "                 en.voided = 0 AND ob.voided = 0 "
            + "           AND en.location_id = :location "
            + "           AND ((en.encounter_type =  ${18} "
            + "             AND ob.concept_id =  ${5096} "
            + "             AND ob.value_datetime IS NOT NULL "
            + "             AND TIMESTAMPDIFF(DAY, "
            + "                               DATE(last_encounter.encounter_date), "
            + "                               ob.value_datetime) > 173) "
            + "             OR (en.encounter_type =  ${6} "
            + "                 AND (ob.concept_id =  ${23739} "
            + "                     AND ob.value_coded =  ${23888} ) "
            + "                     )) "
            + "         GROUP BY en.patient_id "
            + "         UNION "
            + getDispensationTypeOnMDCQuery(hivMetadata.getSemiannualDispensation())
            + " ) dispensa_semestral GROUP BY dispensa_semestral.patient_id "
            + " UNION "
            + "         SELECT dispensa_anual.patient_id , 'Dispensa Anual' AS dispensa FROM ( "
            + "         SELECT en.patient_id "
            + "         FROM "
            + "             (SELECT "
            + "                  e.patient_id, MAX(e.encounter_datetime) AS encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${18}  AND p.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id UNION SELECT "
            + "                                              e.patient_id, MAX(e.encounter_datetime) encounter_date "
            + "              FROM "
            + "                  patient p "
            + "                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "              WHERE "
            + "                      e.encounter_type =  ${6} "
            + "                AND (o.value_coded IN ( ${1256}  ,  ${1257} ) OR o.concept_id =  ${23739} ) "
            + "                AND p.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.voided = 0 "
            + "                AND e.location_id = :location "
            + "                AND DATE(e.encounter_datetime) <= :endDate "
            + "              GROUP BY p.patient_id) AS last_encounter "
            + "                 INNER JOIN "
            + "             encounter en ON en.patient_id = last_encounter.patient_id "
            + "                 AND DATE(en.encounter_datetime) = DATE(last_encounter.encounter_date) "
            + "                 INNER JOIN "
            + "             obs ob ON ob.encounter_id = en.encounter_id "
            + "         WHERE "
            + "                 en.voided = 0 AND ob.voided = 0"
            + "           AND en.location_id = :location "
            + "           AND (en.encounter_type =  ${18} "
            + "             AND ob.concept_id =  ${5096} "
            + "             AND ob.value_datetime IS NOT NULL "
            + "             AND TIMESTAMPDIFF(DAY, "
            + "                               DATE(last_encounter.encounter_date), "
            + "                               ob.value_datetime) > 334)  "
            + "         GROUP BY en.patient_id "
            + "         UNION "
            + getDispensationTypeOnMDCQuery(hivMetadata.getAnnualArvDispensationConcept())
            + " ) dispensa_anual GROUP BY dispensa_anual.patient_id "
            + "     ) AS result  GROUP BY result.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  public CohortDefinition getArtStartDate() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "Art start date",
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
    return cd;
  }

  public CohortDefinition getBaseCohort() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("minDay", "minDay", Integer.class));
    cd.addParameter(new Parameter("maxDay", "maxDay", Integer.class));
    cd.setName("FATS base cohort for totals");

    CohortDefinition E1 = getE1();
    CohortDefinition E2 = getE2();
    CohortDefinition E3 = getE3();
    CohortDefinition X = getLastARVRegimen();
    CohortDefinition A = getArtStartDate();

    cd.addSearch("E1", EptsReportUtils.map(E1, MAPPING));
    cd.addSearch("E2", EptsReportUtils.map(E2, MAPPING));
    cd.addSearch("E3", EptsReportUtils.map(E3, MAPPING));
    cd.addSearch("X", EptsReportUtils.map(X, MAPPING3));
    cd.addSearch("A", EptsReportUtils.map(A, MAPPING2));

    cd.setCompositionString("((A AND X) AND NOT (E1 OR E2 OR E3))");

    return cd;
  }

  /**
   * Patients with one of the MDCs is marked as “ @param dispensationType ” with Estado do MDC as
   * Iniciar (I) or Continuar (C) in the last Ficha Clinica with MDCs registered
   *
   * @param dispensationType The type of dispensation required
   * @return {@link String}
   */
  private String getDispensationTypeOnMDCQuery(Concept dispensationType) {

    return " SELECT     p.patient_id "
        + "         FROM       patient p "
        + "         INNER JOIN encounter e "
        + "         ON         e.patient_id = p.patient_id "
        + "         INNER JOIN obs otype "
        + "         ON         otype.encounter_id = e.encounter_id "
        + "         INNER JOIN obs ostate "
        + "         ON         ostate.encounter_id = e.encounter_id "
        + "         INNER JOIN "
        + "                    ( "
        + "                               SELECT     p.patient_id, "
        + "                                          max(e.encounter_datetime) AS last_encounter_datetime "
        + "                               FROM       patient p "
        + "                               INNER JOIN encounter e "
        + "                               ON         p.patient_id = e.patient_id "
        + "                               INNER JOIN obs otype "
        + "                               ON         otype.encounter_id = e.encounter_id "
        + "                               INNER JOIN obs ostate "
        + "                               ON         ostate.encounter_id = e.encounter_id "
        + "                               WHERE      p.voided = 0 "
        + "                               AND        e.voided = 0 "
        + "                               AND        otype.voided = 0 "
        + "                               AND        ostate.voided = 0 "
        + "                               AND        e.encounter_type = ${6} "
        + "                               AND        otype.concept_id = ${165174} "
        + "                               AND        otype.value_coded IS NOT NULL "
        + "                               AND        ostate.concept_id = ${165322} "
        + "                               AND        ostate.value_coded IS NOT NULL "
        + "                               AND        otype.obs_group_id = ostate.obs_group_id "
        + "                               AND        e.encounter_datetime <= :endDate "
        + "                               AND        e.location_id = :location "
        + "                               GROUP BY   p.patient_id ) last_mdc_record "
        + "         ON         last_mdc_record.patient_id = p.patient_id "
        + "         WHERE      e.encounter_type = ${6} "
        + "         AND        e.location_id = :location "
        + "         AND        otype.concept_id = ${165174} "
        + "         AND        otype.value_coded = "
        + dispensationType.getConceptId()
        + "         AND        ostate.concept_id = ${165322} "
        + "         AND        ostate.value_coded IN (${1256},  ${1257}) "
        + "         AND        e.encounter_datetime = last_mdc_record.last_encounter_datetime "
        + "         AND        otype.obs_group_id = ostate.obs_group_id "
        + "         AND        e.voided = 0 "
        + "         AND        p.voided = 0 "
        + "         AND        otype.voided = 0 "
        + "         AND        ostate.voided = 0 "
        + "         GROUP BY   p.patient_id ";
  }

  /**
   * <b>The system will show the Support Groups informed in the Last APSS/PP consultation by report
   * end date as follows:</b> <b>This implementation works for both encountertypes (Ficha clinica
   * and APSS)</b>
   *
   * <ul>
   *   <li>Mães Mentoras (MM) Value of the Support Group field “Mães Mentoras (MM)” marked on the
   *       Last APSS/PP Consultation Date / Last Clinical Consultation Date. Possible values are
   *       “Início”, “Continua”, “Fim”. If no value is marked the column should be left Blank.
   *   <li>Adolescentes e Jovens Mentores (AJM) Value of the Support Group field “Adolescentes e
   *       Jovens Mentores (AJM)” marked on the Last APSS/PP Consultation Date/ Last Clinical
   *       Consultation Date. Possible values are “Início”, “Continua”, “Fim”. If no value is marked
   *       the column should be left Blank
   *   <li>omem Campeão (HC) Value of the Support Group field “Homem Campeão (HC)” marked on the
   *       Last APSS/PP Consultation Date / Last Clinical Consultation Date. Possible values are
   *       “Início”, “Continua”, “Fim”. If no value is marked the column should be left Blank.
   * </ul>
   *
   * @param encounterTypes List of desired encounters
   * @param supportGroupConcepts List of desired Support Groups
   * @param apssConsultation Apss most recent consultation date flag (True/false to use FC or APSS
   *     most recent date)
   * @return {@link DataDefinition}
   */
  public DataDefinition getSupportGroupsOnFichaClinicaOrSeguimento(
      List<Integer> encounterTypes, List<Integer> supportGroupConcepts, Boolean apssConsultation) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();

    sqlPatientDataDefinition.setName("Get Support Groups On Ficha Clinica Or Seguimento");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, String> map = new HashMap<>();
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    map.put(
        "6", String.valueOf(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    map.put(
        "9",
        String.valueOf(hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()));
    map.put(
        "35",
        String.valueOf(
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId()));
    map.put("1256", String.valueOf(hivMetadata.getStartDrugsConcept().getConceptId()));
    map.put("1257", String.valueOf(hivMetadata.getContinueRegimenConcept().getConceptId()));
    map.put("1267", String.valueOf(hivMetadata.getCompletedConcept().getConceptId()));
    map.put("encounterTypes", StringUtils.join(encounterTypes, ","));
    map.put("supportGroupConcepts", StringUtils.join(supportGroupConcepts, ","));

    String query =
        " SELECT p.patient_id, "
            + "       o.concept_id AS support_group "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime <= :endDate ";
    if (apssConsultation) {
      query += "                          AND e.encounter_type = ${35} ";
    } else {
      query += "                          AND e.encounter_type IN ( ${6}, ${9} ) ";
    }
    query +=
        "                   GROUP  BY p.patient_id) max_encounter "
            + "               ON p.patient_id = max_encounter.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type IN ( ${encounterTypes} ) "
            + "       AND o.concept_id IN ( ${supportGroupConcepts} ) "
            + "       AND o.value_coded IN ( ${1256}, ${1257}, ${1267}, NULL ) "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND e.encounter_datetime = max_encounter.encounter_datetime "
            + "GROUP  BY p.patient_id ";

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * Last APSS/PP Consultation Date – Sheet 1: Column T Date of the most recent consultation
   * registered on Ficha APSS/PP by report end date.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastApssConsultationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();

    sqlPatientDataDefinition.setName("Get Last APSS/PP Consultation Date");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "  SELECT p.patient_id, e.encounter_datetime "
            + "             FROM   patient p  "
            + "                 INNER JOIN encounter e  "
            + "                     ON p.patient_id = e.patient_id  "
            + "                 INNER JOIN ( "
            + "                   SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime <= :endDate "
            + "                          AND e.encounter_type = ${35} "
            + "                   GROUP  BY p.patient_id "
            + "                               ) most_recent ON p.patient_id = most_recent.patient_id   "
            + "             WHERE  p.voided = 0  "
            + "                 AND e.voided = 0  "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${35} "
            + "                 AND e.encounter_datetime = most_recent.encounter_datetime "
            + "             GROUP BY p.patient_id";

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * Next scheduled APSS/PP consultation Date marked on the last APSS/PP consultation (Column T)
   * occurred by the report end date
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getNextApssConsultationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();

    sqlPatientDataDefinition.setName("Get Next Scheduled APSS/PP Consultation Date");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("6310", hivMetadata.getDateOfNextCounselingConcept().getConceptId());

    String query =
        "  SELECT p.patient_id, o.value_datetime "
            + "             FROM   patient p  "
            + "                 INNER JOIN encounter e  "
            + "                     ON p.patient_id = e.patient_id  "
            + "                 INNER JOIN obs o  "
            + "                     ON e.encounter_id = o.encounter_id  "
            + "                 INNER JOIN ( "
            + "                   SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime <= :endDate "
            + "                          AND e.encounter_type = ${35} "
            + "                   GROUP  BY p.patient_id "
            + "                           ) most_recent ON p.patient_id = most_recent.patient_id   "
            + "             WHERE  p.voided = 0  "
            + "                 AND e.voided = 0  "
            + "                 AND o.voided = 0  "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type = ${35} "
            + "                 AND o.concept_id = ${6310} "
            + "                 AND e.encounter_datetime = most_recent.encounter_datetime "
            + "            GROUP BY p.patient_id";

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }
}
