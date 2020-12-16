package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoMensalTransferredOutCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

@Component
public class IMER1DenominatorCohortQueries {

  private HivMetadata hivMetadata;

  private TxNewCohortQueries txNewCohortQueries;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public IMER1DenominatorCohortQueries(
      HivMetadata hivMetadata,
      TxNewCohortQueries txNewCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      GenericCohortQueries genericCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.txNewCohortQueries = txNewCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.genericCohortQueries = genericCohortQueries;
  }

  public CohortDefinition getAllPatients() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Denominator - IMER1 - All");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            getEarliestPreART(),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("A AND NOT D");

    return compositionCohortDefinition;
  }

  public CohortDefinition getPregnantWomen() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Denominator - IMER1 - Pregnant");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            getEarliestPreART(),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
            "D",
            EptsReportUtils.map(
                    getTransferredInPatients(),
                    "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("A AND B  AND NOT (C OR D)");
    return compositionCohortDefinition;
  }

  public CohortDefinition getBreastfeedingWoman() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Denominator - IMER1 - Breastfeeding");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            getEarliestPreART(),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
            "D",
            EptsReportUtils.map(
                    getTransferredInPatients(),
                    "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("A AND C AND NOT (B OR D)");

    return compositionCohortDefinition;
  }

  public CohortDefinition getChildreen() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Denominator - IMER1 - Children");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            getEarliestPreART(),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
            "D",
            EptsReportUtils.map(
                    getTransferredInPatients(),
                    "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnPreArtDate(0, 14),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.setCompositionString("(A AND CHILDREN) AND NOT (B OR C OR D)");

    return compositionCohortDefinition;
  }

  public CohortDefinition getAdults() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Denominator - IMER1 - Adults");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            getEarliestPreART(),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m-1d},endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
            "D",
            EptsReportUtils.map(
                    getTransferredInPatients(),
                    "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "ADULTS",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnPreArtDate(15, 200),
            "onOrAfter=${endDate-2m-1d},onOrBefore=${endDate-1m},location=${location}"));

    compositionCohortDefinition.setCompositionString("(A AND ADULTS) AND NOT (B OR C OR D)");

    return compositionCohortDefinition;
  }

  public CohortDefinition getEarliestPreART() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Get Earliest Pre-ART");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("hivCareProgram", hivMetadata.getHIVCareProgram().getId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("pPreArtStartDate", hivMetadata.getPreArtStartDate().getConceptId());

    String query =
        "SELECT final.patient_id "
            + "FROM "
            + "    ( "
            + "    SELECT earliest_date.patient_id ,MIN(earliest_date.min_date) "
            + "    FROM "
            + "        ( "
            + "            SELECT p.patient_id, MIN(pp.date_enrolled) AS min_date "
            + "            FROM patient p "
            + "                INNER JOIN patient_program pp "
            + "                    ON pp.patient_id = p.patient_id "
            + "                INNER JOIN program pg "
            + "                    ON pg.program_id = pp.program_id "
            + "            WHERE "
            + "                p.voided = 0 "
            + "                AND pp.date_enrolled <= :endDate "
            + "                AND pg.program_id = ${hivCareProgram} "
            + "                AND pp.location_id = :location "
            + "            GROUP BY p.patient_id "
            + "            UNION "
            + "            SELECT p.patient_id, MIN(o.obs_datetime) AS min_date "
            + "            FROM patient p "
            + "                INNER JOIN encounter e "
            + "                    ON e.patient_id = p.patient_id "
            + "                INNER JOIN obs o "
            + "                    ON o.encounter_id = e.encounter_id "
            + "            WHERE  "
            + "                p.voided =0 "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = ${masterCardEncounterType} "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = ${pPreArtStartDate} "
            + "                AND o.obs_datetime <= :endDate "
            + "            GROUP BY p.patient_id "
            + "        ) earliest_date "
            + "    WHERE earliest_date.min_date  "
            + "        BETWEEN :startDate AND :endDate "
            + "    GROUP BY earliest_date.patient_id "
            + "    )  "
            + "final";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b>  Transferred-in patients PRE-TARV
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredInPatients() {

    EptsTransferredInCohortDefinition2 transferredInPreviousMonth =
            new EptsTransferredInCohortDefinition2();
    transferredInPreviousMonth.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    transferredInPreviousMonth.addParameter(new Parameter("location", "Location", Location.class));
    transferredInPreviousMonth.addArtProgram(EptsTransferredInCohortDefinition2.ARTProgram.PRE_TARV);

    return transferredInPreviousMonth;
  }
}
