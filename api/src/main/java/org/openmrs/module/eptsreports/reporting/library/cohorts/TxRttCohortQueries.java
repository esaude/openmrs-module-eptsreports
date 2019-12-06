/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRttCohortQueries {

  private HivMetadata hivMetadata;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public TxRttCohortQueries(HivMetadata hivMetadata, GenericCohortQueries genericCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.genericCohortQueries = genericCohortQueries;
  }

  /**
   * For each occurred consultation (Ficha de Seguimento Adulto, Pediatria or Ficha Clinica) of
   * Encounter Type Ids 6 or 9 during the reporting period, the occurred encounter date minus the
   * previous scheduled consultation date (Concept ID 1410 value_datetime) from the previous
   * consultation of Encounter Type Ids 6 or 9, is greater than 28 days
   *
   * @return CohortDefinition
   */
  private CohortDefinition getAllPatientsWhoDelayedMoreThan28Days() {
    return genericCohortQueries.generalSql(
        "Having visit 28 days later",
        TxRttQueries.getPatientsHavingConsultationAfter28DaysPriorToPreviousConsultation(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId()));
  }

  /**
   * * For each occurred drug pick ups of Encounter Types Ids 18 or 52 during the reporting period,
   * the occurred encounter date minus the previous scheduled drug pick up date (as the most recent
   * between previous encounter type 18 scheduled date (Concept ID 1410 value_datetime) and the
   * previous encounter type 52 pick up date (Concept ID 23866 value_datetime +30 days)), is greater
   * than 28 days
   *
   * @return CohortDefinition
   */
  private CohortDefinition getAllPatientsWhoDelayedMoreThan30Days() {
    return genericCohortQueries.generalSql(
        "Having visit 30 days later",
        TxRttQueries
            .getAllPatientsWhoMissedDrugPickupHavingPreviousMasterCardAppointment30DaysWhichIs28DaysLaterThanEncounterDate(
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
                hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
                hivMetadata.getArtDatePickup().getConceptId()));
  }

  /**
   * All patients (adults and children) who at ANY clinical contact (clinical consultation or drugs
   * pick up [Encounter Type Ids = 6,9,18,52]) registered during the reporting period had a delay
   * greater than 28/30 days from the last scheduled/expected, which may have happened during or
   * prior to the reporting period period
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllPatientsWhoMissedAppointmentBy28Or30DaysButLaterHadVisit() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "Patients who missed previous appointment by 28 or 30 days but later showed up for a visit");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "M1",
        EptsReportUtils.map(
            getAllPatientsWhoDelayedMoreThan28Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "M2",
        EptsReportUtils.map(
            getAllPatientsWhoDelayedMoreThan30Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("M1 OR M2");
    return cd;
  }
}
