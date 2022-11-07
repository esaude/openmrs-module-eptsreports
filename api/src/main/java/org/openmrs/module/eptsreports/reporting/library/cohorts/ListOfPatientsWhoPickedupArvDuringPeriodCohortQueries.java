package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries {

  private final TxCurrCohortQueries txCurrCohortQueries;

  private final HivMetadata hivMetadata;

  @Autowired
  public ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries(
      TxCurrCohortQueries txCurrCohortQueries, HivMetadata hivMetadata) {
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /**
   * From all patients active on ART (TX_CURR) (ARV_PICK_FR4) by reporting end date, the system will
   * include: Patients with ARV drug pick-up registered on FILA between the selected report start
   * date and end date;
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Base Cohort for List of patients who picked up ARV during the period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txcurr = this.txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true);

    CohortDefinition lastPickupFila = this.getPatientsWithDrugPickupOnFila();

    cd.addSearch(
        "txcurr", EptsReportUtils.map(txcurr, "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "pickup",
        EptsReportUtils.map(
            lastPickupFila, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("txcurr AND pickup");
    return cd;
  }

  /**
   * Patients with ARV drug pick-up registered on FILA between the selected report start date and
   * end date;
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithDrugPickupOnFila() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients with ARV drug pick-up registered on FILA");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());

    String sql =
        " SELECT p.patient_id "
            + " FROM   patient p  "
            + "          INNER JOIN encounter e  "
            + "                          ON p.patient_id = e.patient_id  "
            + " WHERE  p.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.location_id = :location "
            + "          AND e.encounter_type = ${18} "
            + "         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY p.patient_id ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(substitutor.replace(sql));

    return cd;
  }
}
