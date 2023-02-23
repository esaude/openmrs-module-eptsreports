package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsWithPositiveTbScreeningQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWithPositiveTbScreeningDataDefinitionQueries {

  private final HivMetadata hivMetadata;
  private final ListOfPatientsWithPositiveTbScreeningQueries
      listOfPatientsWithPositiveTbScreeningQueries;

  @Autowired
  public ListOfPatientsWithPositiveTbScreeningDataDefinitionQueries(
      HivMetadata hivMetadata,
      ListOfPatientsWithPositiveTbScreeningQueries listOfPatientsWithPositiveTbScreeningQueries) {
    this.hivMetadata = hivMetadata;
    this.listOfPatientsWithPositiveTbScreeningQueries =
        listOfPatientsWithPositiveTbScreeningQueries;
  }

  /**
   * <b>Date of the Earliest Positive TB screening (Data do Último Rastreio Positivo de TB)</b>
   *
   * <p>The system will identify and show the earliest date, amongst all sources, when the patient
   * had a positive screening for TB during the reporting period
   *
   * @see ListOfPatientsWithPositiveTbScreeningCohortQueries#getPatientsWithPositiveTbScreening()
   * @return {@link DataDefinition}
   */
  public DataDefinition getEarliestTbPositiveScreeningDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Date of the Earliest Positive TB screening");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        new EptsQueriesUtil()
            .min(
                listOfPatientsWithPositiveTbScreeningQueries
                    .getTbPositiveScreeningFromSourcesQuery())
            .getQuery();

    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }

  /**
   * <b>GeneXpert Request and Results</b>
   *
   * <p><b>GeneXpert Request Date - Sheet 1: Column I</b> <br>
   *
   * <p>The system will identify and show the most recent clinical consultation with GeneXpert
   * request marked in Investigações – Pedidos Laboratoriais section of Ficha Clínica registered
   * between the Earliest Positive TB Screening Date (Value of Column H) and report generation date.
   * <br>
   * <br>
   * <b>BK Request Date - Sheet 1: Column N</b> <br>
   *
   * <p>The system will identify and show the most recent clinical consultation with BK request
   * marked in Investigações – Pedidos Laboratoriais section of Ficha Clínica registered between the
   * Earliest Positive TB Screening Date (Value of Column H) and report generation date. <br>
   * <br>
   * <b>TB LAM Request Date - Sheet 1: Column Q</b> <br>
   *
   * <p>The system will identify and show the most recent clinical consultation with TB LAM request
   * marked in Investigações – Pedidos Laboratoriais section of Ficha Clínica registered between the
   * Earliest Positive TB Screening Date (Value of Column H) and report generation date. <br>
   *
   * @param testConcept The Type of test requested
   * @return {@link DataDefinition}
   */
  public DataDefinition getTbLaboratoryResearchRequestDate(List<Integer> testConcept) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("GeneXpert Request Date");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(
        new Parameter("generationDate", "generationDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put(
        "6", String.valueOf(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    map.put(
        "23722", String.valueOf(hivMetadata.getApplicationForLaboratoryResearch().getConceptId()));
    map.put("testConcept", StringUtils.join(testConcept, ","));

    String query =
        "SELECT p.patient_id, MAX(e.encounter_datetime) AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "      INNER JOIN ( "
            + " SELECT positive.patient_id, MIN(positive.earliest_date) as earliest_date FROM ( "
            + listOfPatientsWithPositiveTbScreeningQueries.getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${23722} "
            + "       AND o.value_coded IN ( ${testConcept} ) "
            + "       AND e.encounter_datetime >= positiveScreening.earliest_date "
            + "       AND e.encounter_datetime <= :generationDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + " GROUP BY p.patient_id";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>GeneXpert Request Date - Sheet 1: Column J</b>
   *
   * <p>Possible values are Positivo/Negativo or Blank if no result is found <br>
   * The system will identify and show the most recent GeneXpert result marked in Investigações –
   * Resultados Laboratoriais section of Ficha Clínica registered between the Earliest Positive TB
   * Screening Date (Value of Column H) and report generation date.<br>
   * <br>
   * <b>GeneXpert Result Lab Form - Sheet 1: Column K</b>
   *
   * <p>Possible values are Positivo/Negativo or Blank if no result is found <br>
   * The system will identify the most recent GeneXpert result marked in Laboratory form registered
   * between the Earliest Positive TB Screening Date (Value of Column H) and report generation date.
   * <br>
   * <br>
   * <b>Xpert MTB Result Lab Form - Sheet 1: Column L</b>
   *
   * <p>Possible values are Sim/Não or Blank if no result is found The system will identify and show
   * the most recent XpertMTB result marked in Laboratory form registered between the Earliest
   * Positive TB Screening Date (Value of Column H) and report generation date<br>
   * <br>
   * <b>BK Result Ficha Clinica - Sheet 1: Column O</b>
   *
   * <p>Possible values are Positivo/Negativo or Blank if no result is found The system will
   * identify and show the most recent BK result marked in Investigações – Resultados Laboratoriais
   * section of Ficha Clínica registered between the Earliest Positive TB Screening Date (Value of
   * Column H) and report generation date.<br>
   * <br>
   * <b>BK Result Lab Form - Sheet 1: Column P</b>
   *
   * <p>Possible values are Positivo/Não Encontrado or Blank if no result is found The system will
   * identify and show the most recent BK result marked in Laboratory form registered between the
   * Earliest Positive TB Screening Date (Value of Column H) and report generation date.<br>
   * <br>
   * <b>TB LAM Result Ficha Clínica - Sheet 1: Column R</b>
   *
   * <p>Possible values are Positivo/Negativo or Blank if no result is found The system will
   * identify and show the most recent TB LAM result marked in Investigações – Resultados
   * Laboratoriais section of Ficha Clínica registered between the Earliest Positive TB Screening
   * Date (Value of Column H) and report generation date.<br>
   * <br>
   * <b>TB LAM Result Lab Form - Sheet 1: Column S</b>
   *
   * <p>Possible values are Positivo/Negativo/Indeterminado or Blank if no result is found The
   * system will identify and show the most recent TB LAM result marked in Laboratory form
   * registered between the Earliest Positive TB Screening Date (Value of Column H) and report
   * generation date.<br>
   *
   * @param encounterTypeList EncounterTypes
   * @param examConceptList Exam Concepts
   * @param resultConceptList Result Concepts
   * @return {@link DataDefinition}
   */
  public DataDefinition getTbLaboratoryResearchResults(
      List<Integer> encounterTypeList,
      List<Integer> examConceptList,
      List<Integer> resultConceptList) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("GeneXpert Request Results");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(
        new Parameter("generationDate", "generationDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("encounterType", StringUtils.join(encounterTypeList, ","));
    map.put("examConcept", StringUtils.join(examConceptList, ","));
    map.put("resultConcept", StringUtils.join(resultConceptList, ","));

    String query =
        " SELECT exam_result.patient_id, exam_result.value_coded FROM ( "
            + " SELECT p.patient_id, o.value_coded, MAX(e.encounter_datetime) AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "      INNER JOIN ( "
            + " SELECT positive.patient_id, MIN(positive.earliest_date) as earliest_date FROM ( "
            + listOfPatientsWithPositiveTbScreeningQueries.getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type IN ( ${encounterType} ) "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id IN ( ${examConcept} ) "
            + "       AND o.value_coded IN ( ${resultConcept} ) "
            + "       AND e.encounter_datetime >= positiveScreening.earliest_date "
            + "       AND e.encounter_datetime <= :generationDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + " GROUP BY p.patient_id) exam_result ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Rifampin Resistance Lab Form</b>
   *
   * <p>Possible values are Sim/Nao/Indeterminado or Blank if no result is found The system will
   * identify and show the Rifampin Resistance result marked in the same Laboratory form used to
   * obtain the Xpert MTB Result (Value of Column L)
   *
   * @see #getTbLaboratoryResearchResults(List, List, List)
   * @param encounterTypeList EncounterTypes
   * @param examConceptList Exam Concepts
   * @param resultConceptList Result Concepts
   * @param rifampinResistanceConcept Rifampin Concept
   * @param indeterminateConcept Indeterminate Concept
   * @return {@link DataDefinition}
   */
  public DataDefinition getRifampinResistanceResults(
      List<Integer> encounterTypeList,
      List<Integer> examConceptList,
      List<Integer> resultConceptList,
      int rifampinResistanceConcept,
      int indeterminateConcept) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Rifampin Resistance Results");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(
        new Parameter("generationDate", "generationDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("encounterType", StringUtils.join(encounterTypeList, ","));
    map.put("examConcept", StringUtils.join(examConceptList, ","));
    map.put("resultConcept", StringUtils.join(resultConceptList, ","));
    map.put("1138", String.valueOf(indeterminateConcept));
    map.put("165192", String.valueOf(rifampinResistanceConcept));

    String query =
        "SELECT p.patient_id, o.value_coded "
            + "FROM   patient p "
            + "           INNER JOIN encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "           INNER JOIN obs o "
            + "                      ON o.encounter_id = e.encounter_id "
            + "           INNER JOIN ( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "      INNER JOIN ( "
            + " SELECT positive.patient_id, MIN(positive.earliest_date) as earliest_date FROM ( "
            + listOfPatientsWithPositiveTbScreeningQueries.getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type IN ( ${encounterType} ) "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id IN ( ${examConcept} ) "
            + "       AND o.value_coded IN ( ${resultConcept} ) "
            + "       AND e.encounter_datetime >= positiveScreening.earliest_date "
            + "       AND e.encounter_datetime <= :generationDate "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + " GROUP BY p.patient_id) mtb ON mtb.patient_id = p.patient_id "
            + "WHERE  e.encounter_type IN ( ${encounterType} ) "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${165192} "
            + "       AND o.value_coded IN ( ${resultConcept} , ${1138} ) "
            + "       AND e.encounter_datetime = mtb.recent_date "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>The system will identify patients who started on anti-TB treatment after the Positive TB
   * Screening and by reporting generation date as follows:</b>
   * <li>All patients marked with Tratamento TB = Início (I) in Ficha Clínica Master Card registered
   *     between the Last Positive TB Screening Date (Value of Column H) and report generation date
   * <li>All patients with TB Date registered in Condições Médicas Importantes – Ficha Resumo Master
   *     Card, falling between the Last Positive TB Screening Date (Value of Column H) and report
   *     generation date.
   * <li>All patients who are enrolled in TB Program with enrollment Date (Data de Admissão) between
   *     the Last Positive TB Screening Date (Value of Column H) and report generation date.
   * <li>All patients marked with diagnosis active TB = Yes (Diagnóstico TB active=S) in Ficha
   *     Clínica Master Card registered between the Last Positive TB Screening Date (Value of Column
   *     H) and report generation date
   *
   *     <p>The system will obtain the most recent date from the various sources identified above as
   *     TB Treatment Start Date.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getTbTreatmentStartDateFromSources() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Anti-TB treatment start Date");
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(
        new Parameter("generationDate", "generationDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        new EptsQueriesUtil()
            .max(listOfPatientsWithPositiveTbScreeningQueries.getAntiTbTreatmentFromSourcesQuery())
            .getQuery();

    sqlPatientDataDefinition.setQuery(query);

    return sqlPatientDataDefinition;
  }
}
