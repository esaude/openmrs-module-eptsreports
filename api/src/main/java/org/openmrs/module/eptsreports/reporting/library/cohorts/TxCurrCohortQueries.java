/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispense6OrMoreMonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txcurr.TxCurrPatientsOnArvDispenseLessThan3MonthCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxCurrQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxCurrCohortQueries we want to expose for EPTS */
@Component
public class TxCurrCohortQueries {

  private static final String HAS_NEXT_APPOINTMENT_QUERY =
      "select distinct obs.person_id from obs "
          + "where obs.obs_datetime <= :onOrBefore and obs.location_id = :location and obs.concept_id = %s and obs.voided = false and obs.value_datetime is not null "
          + "and obs.obs_datetime = (select max(encounter.encounter_datetime) from encounter "
          + "where encounter.encounter_type in (%s) and encounter.patient_id = obs.person_id and encounter.location_id = obs.location_id and encounter.voided = false and encounter.encounter_datetime <= :onOrBefore) ";

  private static final int OLD_SPEC_ABANDONMENT_DAYS = 60;

  private static final int CURRENT_SPEC_ABANDONMENT_DAYS = 31;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  /**
   * @param cohortName Cohort name
   * @param currentSpec
   * @return TxCurr composition cohort definition
   */
  @DocumentedDefinition(value = "getTxCurrCompositionCohort")
  public CohortDefinition getTxCurrCompositionCohort(
      final String cohortName, final boolean currentSpec) {

    final int abandonmentDays =
        currentSpec ? CURRENT_SPEC_ABANDONMENT_DAYS : OLD_SPEC_ABANDONMENT_DAYS;
    final CompositionCohortDefinition txCurrComposition = new CompositionCohortDefinition();
    txCurrComposition.setName(cohortName);

    txCurrComposition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    txCurrComposition.addParameter(new Parameter("location", "location", Location.class));
    txCurrComposition.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
    txCurrComposition.addParameter(new Parameter("locations", "location", Location.class));

    final CohortDefinition inARTProgramAtEndDate =
        this.genericCohortQueries.createInProgram("InARTProgram", this.hivMetadata.getARTProgram());
    txCurrComposition
        .getSearches()
        .put(
            "1",
            EptsReportUtils.map(
                inARTProgramAtEndDate, "onOrBefore=${onOrBefore},locations=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "2",
            EptsReportUtils.map(
                this.getPatientWithSTARTDRUGSObsBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "3",
            EptsReportUtils.map(
                this.hivCohortQueries.getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "4",
            EptsReportUtils.map(
                this.getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "5",
            EptsReportUtils.map(
                this.getPatientsWhoLeftARTProgramBeforeOrOnEndDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "6",
            EptsReportUtils.map(
                this.getPatientsThatMissedNexPickup(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "7",
            EptsReportUtils.map(
                this.getPatientsThatDidNotMissNextConsultation(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "8",
            EptsReportUtils.map(
                this.getPatientsReportedAsAbandonmentButStillInPeriod(),
                String.format(
                    "onOrBefore=${onOrBefore},location=${location},abandonmentDays=%s",
                    abandonmentDays)));
    txCurrComposition
        .getSearches()
        .put(
            "11",
            EptsReportUtils.map(
                this.getPatientsWithNextPickupDate(),
                "onOrBefore=${onOrBefore},location=${location}"));
    txCurrComposition
        .getSearches()
        .put(
            "12",
            EptsReportUtils.map(
                this.getPatientsWithNextConsultationDate(),
                "onOrBefore=${onOrBefore},location=${location}"));

    String compositionString;
    if (currentSpec) {
      compositionString =
          "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR ((6 OR (NOT 11)) AND (NOT (7 OR 8))))) AND (11 OR 12)";
    } else {
      compositionString = "(1 OR 2 OR 3 OR 4) AND (NOT (5 OR (6 AND (NOT (7 OR 8)))))";
    }

    txCurrComposition.setCompositionString(compositionString);
    return txCurrComposition;
  }

  /**
   * @return Cohort of patients with first drug pickup (encounter type 18=S.TARV: FARMACIA) before
   *     or on end date
   */
  @DocumentedDefinition(value = "patientWithFirstDrugPickupEncounter")
  private CohortDefinition getPatientWithFirstDrugPickupEncounterBeforeOrOnEndDate() {
    final SqlCohortDefinition patientWithFirstDrugPickupEncounter = new SqlCohortDefinition();
    patientWithFirstDrugPickupEncounter.setName("patientWithFirstDrugPickupEncounter");
    final String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=%d "
            + "AND e.voided=0 AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithFirstDrugPickupEncounter.setQuery(
        String.format(query, this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithFirstDrugPickupEncounter.addParameter(
        new Parameter("location", "location", Location.class));
    return patientWithFirstDrugPickupEncounter;
  }

  /**
   * @return Cohort of patients registered as START DRUGS (answer to question 1255 = ARV PLAN is
   *     1256 = START DRUGS) in the first drug pickup (encounter type 18=S.TARV: FARMACIA) or follow
   *     up consultation for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
   *     9=S.TARV: PEDIATRIA SEGUIMENTO) before or on end date
   */
  @DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
  private CohortDefinition getPatientWithSTARTDRUGSObsBeforeOrOnEndDate() {
    final SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
    patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
    final String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (%d, %d, %d) "
            + "AND o.concept_id=%d AND o.value_coded in (%d, %d) "
            + "AND e.encounter_datetime <= :onOrBefore AND e.location_id=:location GROUP BY p.patient_id";
    patientWithSTARTDRUGSObs.setQuery(
        String.format(
            query,
            this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPlanConcept().getConceptId(),
            this.hivMetadata.getStartDrugsConcept().getConceptId(),
            this.hivMetadata.getTransferFromOtherFacilityConcept().getConceptId()));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    patientWithSTARTDRUGSObs.addParameter(new Parameter("location", "location", Location.class));
    return patientWithSTARTDRUGSObs;
  }

  /**
   * @return Cohort of patients who left ART program before or on end date(4). Includes: dead,
   *     transferred to, stopped and abandoned (patient state 10, 7, 8 or 9)
   */
  @DocumentedDefinition(value = "leftARTProgramBeforeOrOnEndDate")
  private CohortDefinition getPatientsWhoLeftARTProgramBeforeOrOnEndDate() {
    final SqlCohortDefinition leftARTProgramBeforeOrOnEndDate = new SqlCohortDefinition();
    leftARTProgramBeforeOrOnEndDate.setName("leftARTProgramBeforeOrOnEndDate");

    final String leftARTProgramQueryString =
        "select p.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id "
            + "inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s"
            + " and ps.state in (%s) and ps.end_date is null and ps.start_date<=:onOrBefore and pg.location_id=:location group by p.patient_id";

    final String abandonStates =
        StringUtils.join(
            Arrays.asList(
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId(),
                this.hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
                this.hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId(),
                this.hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId()),
            ',');

    leftARTProgramBeforeOrOnEndDate.setQuery(
        String.format(
            leftARTProgramQueryString,
            this.hivMetadata.getARTProgram().getProgramId(),
            abandonStates));

    leftARTProgramBeforeOrOnEndDate.addParameter(
        new Parameter("onOrBefore", "onOrBefore", Date.class));
    leftARTProgramBeforeOrOnEndDate.addParameter(
        new Parameter("location", "location", Location.class));
    return leftARTProgramBeforeOrOnEndDate;
  }

  /**
   * @return Cohort of patients that from the date scheduled for next drug pickup (concept
   *     5096=RETURN VISIT DATE FOR ARV DRUG) until end date have completed 28 days and have not
   *     returned
   */
  @DocumentedDefinition(value = "patientsThatMissedNexPickup")
  private CohortDefinition getPatientsThatMissedNexPickup() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsThatMissedNexPickup");
    final String query =
        "SELECT patient_id FROM (SELECT p.patient_id,max(encounter_datetime) encounter_datetime FROM patient p INNER JOIN encounter e on e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=%s"
            + " AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida INNER JOIN obs o on o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%s"
            + " AND o.location_id=:location AND datediff(:onOrBefore,o.value_datetime)>=:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            this.hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
   * @return Cohort of patients that from the date scheduled for next follow up consultation
   *     (concept 1410=RETURN VISIT DATE) until the end date have not completed 28 days
   */
  @DocumentedDefinition(value = "patientsThatDidNotMissNextConsultation")
  private CohortDefinition getPatientsThatDidNotMissNextConsultation() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsThatDidNotMissNextConsultation");
    final String query =
        "SELECT patient_id FROM "
            + "(SELECT p.patient_id,max(encounter_datetime) encounter_datetime "
            + "FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type in (%d, %d) "
            + "AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_mov "
            + "INNER JOIN obs o ON o.person_id=max_mov.patient_id "
            + "WHERE max_mov.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d "
            + "AND o.location_id=:location AND DATEDIFF(:onOrBefore,o.value_datetime)<:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            this.hivMetadata.getReturnVisitDateConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
   * @return Cohort of patients that were registered as abandonment (program workflow state is
   *     9=ABANDONED) but from the date scheduled for next drug pick up (concept 5096=RETURN VISIT
   *     DATE FOR ARV DRUG) until the end date have not completed 28 days
   */
  @DocumentedDefinition(value = "patientsReportedAsAbandonmentButStillInPeriod")
  private CohortDefinition getPatientsReportedAsAbandonmentButStillInPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsReportedAsAbandonmentButStillInPeriod");
    final String query =
        "SELECT abandono.patient_id FROM (SELECT pg.patient_id FROM patient p INNER JOIN patient_program pg ON p.patient_id=pg.patient_id INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=%d "
            + "AND ps.state=%d "
            + "AND ps.end_date is null AND ps.start_date<=:onOrBefore AND location_id=:location )abandono INNER JOIN ( SELECT max_frida.patient_id,max_frida.encounter_datetime,o.value_datetime FROM ( SELECT p.patient_id,max(encounter_datetime) encounter_datetime FROM patient p INNER JOIN encounter e ON e.patient_id=p.patient_id WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=%d "
            + "AND e.location_id=:location AND e.encounter_datetime<=:onOrBefore group by p.patient_id ) max_frida INNER JOIN obs o ON o.person_id=max_frida.patient_id WHERE max_frida.encounter_datetime=o.obs_datetime AND o.voided=0 AND o.concept_id=%d "
            + "AND o.location_id=:location ) ultimo_fila ON abandono.patient_id=ultimo_fila.patient_id WHERE datediff(:onOrBefore,ultimo_fila.value_datetime)<:abandonmentDays";
    definition.setQuery(
        String.format(
            query,
            this.hivMetadata.getARTProgram().getProgramId(),
            this.hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            this.hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.addParameter(new Parameter("abandonmentDays", "abandonmentDays", Integer.class));
    return definition;
  }

  /**
   * Patients that have next pickup date set on their most recent encounter
   *
   * @return
   */
  @DocumentedDefinition(value = "patientsWithNextPickupDate")
  private CohortDefinition getPatientsWithNextPickupDate() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsWithNextPickupDate");
    final String encounterTypes =
        StringUtils.join(
            Arrays.asList(this.hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()),
            ',');
    definition.setQuery(
        String.format(
            HAS_NEXT_APPOINTMENT_QUERY,
            this.hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            encounterTypes));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    return definition;
  }

  /**
   * Patients that have next consultation date set on their most recent encounter
   *
   * @return
   */
  @DocumentedDefinition(value = "patientsWithNextConsultationDate")
  private CohortDefinition getPatientsWithNextConsultationDate() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsWithNextConsultationDate");
    final String encounterTypes =
        StringUtils.join(
            Arrays.asList(
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()),
            ',');
    definition.setQuery(
        String.format(
            HAS_NEXT_APPOINTMENT_QUERY,
            this.hivMetadata.getReturnVisitDateConcept().getConceptId(),
            encounterTypes));
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "patientsWhoAreActiveOnART")
  public CohortDefinition findPatientsWhoAreActiveOnART() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsWhoAreActiveOnART");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(TxCurrQueries.QUERY.findPatientsWhoAreCurrentlyEnrolledOnART);

    return definition;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseForLessThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation less than 3 months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";
    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));
    cd.addSearch(
        "arvDispenseForLessThan3Months",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseForLessThan3MonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseForLessThan3Months");
    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseBetween3And5Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation Between 3 and 5 Months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";
    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));
    cd.addSearch(
        "arvDispenseBetween3And5Months",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseBetween3And5MonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseBetween3And5Months");

    return cd;
  }

  public CohortDefinition getPatientsOnArtOnArvDispenseFor6OrMoreMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients On Art On ARV Dispensation For 6 Or More Months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "endDate=${endDate},location=${location}";
    cd.addSearch(
        "patientsWhoAreActiveOnART",
        EptsReportUtils.map(this.findPatientsWhoAreActiveOnART(), mapping));
    cd.addSearch(
        "arvDispenseFor6OrMoreMonths",
        EptsReportUtils.map(
            this.getPatientsOnArtOnArvDispenseFor6OrMoreMonthsCalculation(), mapping));

    cd.setCompositionString("patientsWhoAreActiveOnART AND arvDispenseFor6OrMoreMonths");

    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseForLessThan3Months")
  private CohortDefinition getPatientsOnArtOnArvDispenseForLessThan3MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseForLessThan3Months",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispenseLessThan3MonthCalculation.class)
                .get(0));

    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseBetween3And5Months")
  private CohortDefinition getPatientsOnArtOnArvDispenseBetween3And5MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseBetween3And5Months",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispenseBetween3And5MonthsCalculation.class)
                .get(0));

    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsOnArtOnArvDispenseFor6OrMoreMonths")
  private CohortDefinition getPatientsOnArtOnArvDispenseFor6OrMoreMonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsOnArtOnArvDispenseFor6OrMoreMonths",
            Context.getRegisteredComponents(
                    TxCurrPatientsOnArvDispense6OrMoreMonthsCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
