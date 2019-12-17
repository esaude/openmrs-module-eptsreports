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
   * Cohort to find all patients who have any ecnounters (6,9,18,52) that occurred more than 28 days
   * after the previously scheduled consultation AND drug pickup.
   *
   * @return CohortDefinition
   */
  private CohortDefinition getAllPatientsWhoDelayedMoreThan28Days() {
    return genericCohortQueries.generalSql(
        "Having visit 30 days later",
        TxRttQueries.getAllPatientsWhoMissedPreviousAppointmentBy28Days(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
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
        "RTT",
        EptsReportUtils.map(
            getAllPatientsWhoDelayedMoreThan28Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("RTT");
    return cd;
  }
}
