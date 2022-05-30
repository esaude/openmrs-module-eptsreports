package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
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
public class ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private TXTBCohortQueries txtbCohortQueries;

  @Autowired private CommonQueries commonQueries;

  @Autowired private HivMetadata hivMetadata;

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p><b>List of Patients currently on ART without TB Screening</b> <br>
   *
   * <p>From all patients currently on ART (TX_CURR) (TB_NSCRN_FR3) by reporting end date, the
   * system will exclude
   *
   * <p>All Patients on ART who were screened for TB symptoms at least once (TX_TB – Indicator
   * Denominator) during the 6 months’ period before the reporting end date
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsCurrentlyOnArtWithoutTbScreening() {

    CompositionCohortDefinition composition = new CompositionCohortDefinition();
    addParameters(composition);
    composition.setName("Currently on ART without TB Screening");

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrBaseCohort();
    CohortDefinition txTbDenominator = txtbCohortQueries.getDenominator();

    composition.addSearch(
        "tx-curr", EptsReportUtils.map(txCurr, "endDate=${endDate},location=${location}"));
    composition.addSearch(
        "txtb-denominator",
        EptsReportUtils.map(
            txTbDenominator, "startDate=${endDate-6m},endDate=${endDate},location=${location}"));

    composition.setCompositionString("tx-curr AND NOT txtb-denominator");

    return composition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p><b>Total number of patients on the List (TB_NSCRN_FR2) </b>
   *
   * <p>Number of Patients Currently on ART without TB screening and at least one Clinical
   * Consultation in last 6 months
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getPatientsCurrentlyOnArtWithoutTbScreeningAndWithClinicalConsultationInLast6Months() {

    CompositionCohortDefinition composition = new CompositionCohortDefinition();
    addParameters(composition);
    composition.setName("Currently on ART without TB Screening and With Clinical Consultation");

    CohortDefinition currentlyOnArtWithoutTbScreening =
        getPatientsCurrentlyOnArtWithoutTbScreening();
    CohortDefinition withClinicalConsultationInLast6Months =
        getPatientsWithClinicalConsultationInLast6Months();

    composition.addSearch(
        "onArtWithoutScreening",
        EptsReportUtils.map(
            currentlyOnArtWithoutTbScreening, "endDate=${endDate},location=${location}"));
    composition.addSearch(
        "withConsultation",
        EptsReportUtils.map(
            withClinicalConsultationInLast6Months,
            "startDate=${endDate-6m},endDate=${endDate},location=${location}"));

    composition.setCompositionString("onArtWithoutScreening AND withConsultation");

    return composition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p><b>Total number of patients on the List (TB_NSCRN_FR2) </b>
   *
   * <p>Number of Patients Currently on ART without TB screening and at least one Clinical
   * Consultation in last 6 months
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition
      getPatientsCurrentlyOnArtWithoutTbScreeningAndWithoutClinicalConsultationInLast6Months() {

    CompositionCohortDefinition composition = new CompositionCohortDefinition();
    addParameters(composition);
    composition.setName("Currently on ART without TB Screening and Without Clinical Consultation");

    CohortDefinition currentlyOnArtWithoutTbScreening =
        getPatientsCurrentlyOnArtWithoutTbScreening();
    CohortDefinition withClinicalConsultationInLast6Months =
        getPatientsWithClinicalConsultationInLast6Months();

    composition.addSearch(
        "onArtWithoutScreening",
        EptsReportUtils.map(
            currentlyOnArtWithoutTbScreening, "endDate=${endDate},location=${location}"));
    composition.addSearch(
        "withConsultation",
        EptsReportUtils.map(
            withClinicalConsultationInLast6Months,
            "startDate=${endDate-6m},endDate=${endDate},location=${location}"));

    composition.setCompositionString("onArtWithoutScreening AND NOT withConsultation");

    return composition;
  }

  public DataDefinition getArtStartDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get ART Start Date");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    sqlPatientDataDefinition.setQuery(commonQueries.getARTStartDate(true));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Type of Dispensation registered on the most recent Clinical Consultation (value of Column Q)
   * registered on Ficha Clínica – MasterCard by Report End Date
   *
   * <p>Possible values are:
   *
   * <ul>
   *   <li>DM
   *   <li>DT
   *   <li>DS
   * </ul>
   *
   * *
   *
   * <p>Note: For Patients without Type of Dispensation informed, the corresponding cell in the
   * excel file will show NA
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getDispensationTypeOnClinicalAndPediatricEncounter() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Dispensation Type on Encounter 6");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id , o.value_coded "
            + "FROM patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "INNER JOIN( "
            + "				SELECT p.patient_id , MAX(e.encounter_datetime) encounter_date "
            + "				FROM patient p "
            + "				INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "				WHERE e.encounter_type = ${6} "
            + "				AND e.location_id = :location "
            + "				AND e.encounter_datetime <= :endDate "
            + "				AND p.voided = 0 "
            + "				AND e.voided = 0 "
            + "				GROUP BY p.patient_id "
            + ") recent_fila ON recent_fila.patient_id = p.patient_id "
            + " "
            + "WHERE e.encounter_type = ${6} "
            + "AND e.location_id = :location "
            + "AND e.encounter_datetime = recent_fila.encounter_date "
            + "AND o.concept_id = ${23739} "
            + "AND p.voided = 0 "
            + "AND e.voided = 0 "
            + "AND o.voided = 0 "
            + "GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Modes of Dispensation registered on Last Drug Pick up Date (value of Column M) on FILA by
   * report end date.
   *
   * <p>Possible values are </br>
   *
   * <ul>
   *   <li>Horário Normal de Expediente or
   *   <li>Fora do Horário
   *   <li>FARMAC/Farmácia Privada
   *   <li>Dispensa Comunitária via Provedor
   *   <li>Dispensa Comunitária via APE
   *   <li>Brigadas Móveis Diurnas
   *   <li>Brigadas Móveis Nocturnas (Hotspots)
   *   <li>Clínicas Móveis Diurnas
   *   <li>Clínicas Móveis Nocturnas (Hotspots)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getDispensationTypeOnFila() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Dispensation Type on FILA ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId());

    String query =
        "SELECT p.patient_id, o.value_coded "
            + "FROM   patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN(SELECT p.patient_id, MAX(e.encounter_datetime) encounter_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  WHERE  e.encounter_type = ${18} "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_datetime <= :endDate "
            + "                         AND p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                  GROUP  BY p.patient_id) recent_fila "
            + "               ON recent_fila.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${18} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime = recent_fila.encounter_date "
            + "       AND o.concept_id = ${165174} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The most recent Date of the Drug Pick Up registered on Ficha Recepção Levantou ARV by report
   * end date.
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getMostRecentDrugPickupDateOnRecepcaoLevantouArv() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Most Recent Drug Pick-Up on Recepção Levantou ARVs ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, "
            + "       MAX(o.value_datetime) encounter_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${52} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND o.concept_id = ${23866} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Last Drug Pick up Date registered on Ficha Recepção Levantou ARV by report end date (value
   * of Column O) + 30 days
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getNextScheduledDrugPickupDateOnRecepcaoLevantouArv() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Most Recent Drug Pick-Up on Recepção Levantou ARVs ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id, "
            + "       MAX(DATE_ADD(o.value_datetime, INTERVAL 30 DAY)) encounter_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${52} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND o.concept_id = ${23866} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>
   *
   * <p>Patient’s Most Recent Ficha with MDCs Registered
   *
   * <p>Encounter DATE (encounter.encounter_datetime)
   *
   * <p>Modo De Dispensa (id= 165174)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getMostRecentMdcConsultationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("MDC Consultation Date ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId());

    String query =
        "SELECT p.patient_id, Max(e.encounter_datetime) consultation_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime <= CURRENT_DATE() "
            + "       AND o.concept_id = ${165174} "
            + "       AND e.voided = 0 "
            + "       AND p.voided = 0 "
            + "GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>The system will identify patient’s most recent records of MDCs (DSD) registered in Ficha
   * Clínica by report generation date and will show the registered MDC1, MDC2, MDC3, MDC4 and MDC5
   * if marked as “Inicio” or “Continua” as follows
   *
   * <p>Note: For MDC fields for which no MDC was registered, the corresponding cell in the excel
   * file will show NA
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getMdcDispensationType(DispensationColumn dispensationColumn) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Dispensation Type ");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId());
    valuesMap.put("165322", hivMetadata.getMdcState().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        "SELECT dispensation.patient_id, "
            + dispensationColumn.getQuery()
            + " FROM  (SELECT p.patient_id, e.encounter_id, otype.obs_id, otype.value_coded "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "              INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "              INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "              INNER JOIN (SELECT p.patient_id, Max(e.encounter_datetime) consultation_date "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                          WHERE  e.encounter_type = ${6} "
            + "                                 AND e.location_id = :location "
            + "                                 AND e.encounter_datetime <= CURRENT_DATE() "
            + "                                 AND o.concept_id = ${165174} "
            + "                                 AND e.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                          GROUP  BY p.patient_id) most_recent "
            + "                      ON most_recent.patient_id = p.patient_id "
            + "       WHERE  e.encounter_datetime = most_recent.consultation_date "
            + "              AND e.encounter_type = ${6} "
            + "              AND e.location_id = :location "
            + "              AND otype.concept_id = ${165174} "
            + "              AND ostate.concept_id = ${165322} "
            + "              AND ostate.value_coded IN ( ${1256}, ${1257} ) "
            + "              AND otype.obs_group_id = ostate.obs_group_id "
            + "              AND e.voided = 0 "
            + "              AND p.voided = 0 "
            + "              AND otype.voided = 0 "
            + "              AND ostate.voided = 0 "
            + "       GROUP  BY p.patient_id, otype.obs_id) dispensation "
            + "GROUP  BY dispensation.patient_id, dispensation.obs_id";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);
    sqlPatientDataDefinition.setQuery(sb.replace(query));

    return sqlPatientDataDefinition;
  }

  public enum DispensationColumn {
    MDC1 {
      @Override
      public String getQuery() {
        return " ( SELECT obs.value_coded "
            + "FROM   obs "
            + "WHERE  obs.encounter_id = dispensation.encounter_id "
            + "       AND obs.concept_id = 165174 "
            + "LIMIT  1 ) MDC1 ";
      }
    },
    MDC2 {
      @Override
      public String getQuery() {
        return " ( SELECT obs.value_coded "
            + "FROM   obs "
            + "WHERE  obs.encounter_id = dispensation.encounter_id "
            + "       AND obs.concept_id = 165174 "
            + "LIMIT  1,1 ) MDC2 ";
      }
    },
    MDC3 {
      @Override
      public String getQuery() {
        return " ( SELECT obs.value_coded "
            + "FROM   obs "
            + "WHERE  obs.encounter_id = dispensation.encounter_id "
            + "       AND obs.concept_id = 165174 "
            + "LIMIT  2,1 ) MDC3 ";
      }
    },
    MDC4 {
      @Override
      public String getQuery() {
        return " ( SELECT obs.value_coded "
            + "FROM   obs "
            + "WHERE  obs.encounter_id = dispensation.encounter_id "
            + "       AND obs.concept_id = 165174 "
            + "LIMIT  3,1 ) MDC4 ";
      }
    },
    MDC5 {
      @Override
      public String getQuery() {
        return " ( SELECT obs.value_coded "
            + "FROM   obs "
            + "WHERE  obs.encounter_id = dispensation.encounter_id "
            + "       AND obs.concept_id = 165174 "
            + "LIMIT  4,1 ) MDC5 ";
      }
    };

    public abstract String getQuery();
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Number of Patients Currently on ART without TB screening and at least one Clinical Consultation in last 6 months

   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public CohortDefinition getPatientsWithClinicalConsultationInLast6Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("With clinical consultation in last 6 months ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate"
            + "GROUP BY p.patient_id";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

}
