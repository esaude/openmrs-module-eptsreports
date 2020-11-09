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
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferredInCohortQueries {

  private ResumoMensalCohortQueries resumoMensalCohortQueries;
  private TxCurrCohortQueries txCurrCohortQueries;
  private CommonCohortQueries commonCohortQueries;
  private TxRttCohortQueries txRttCohortQueries;

  @Autowired
  public TransferredInCohortQueries(
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      CommonCohortQueries commonCohortQueries,
      HivMetadata hivMetadata,
      TxRttCohortQueries txRttCohortQueries) {
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
    this.txRttCohortQueries = txRttCohortQueries;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Number Patients who were transferred in from other facility defined as following: - All
   * patients enrolled in ART Program and who have been registered with the following state
   * TRANSFERRED IN FROM OTHER FACILITY - All patients who have filled “Transferido de outra US” and
   * checked “Em TARV” in Ficha Resumo with MasterCard file opening Date during reporting period -
   * But excluding patients who were included in Tx CURR of previous reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTransferredInPatients() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Number of Transferred In patients by end of current period");
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition transferredIn = resumoMensalCohortQueries.getTransferredInPatients(false);

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true);

    CohortDefinition transferredOut =
        commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod();

    CohortDefinition homeVisitTrfOut =
        txCurrCohortQueries.getPatientsTransferedOutInLastHomeVisitCard();

    CohortDefinition clinicalVisit =
        txRttCohortQueries.getPatientsReturnedTreatmentDuringReportingPeriod();

    String mappingsTrfIn = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}";
    String mappingsCurr = "onOrBefore=${onOrBefore-3m},location=${location}";
    String mappingsTrfOut = "onOrBefore=${onOrBefore-1d},location=${location}";
    String mappingsHomeVisitTrfOut = "onOrBefore=${onOrBefore},location=${location}";
    String mappingsClinicalVisit =
        "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}";

    cd.addSearch("transferredIn", EptsReportUtils.map(transferredIn, mappingsTrfIn));
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, mappingsCurr));
    cd.addSearch("transferredOut", EptsReportUtils.map(transferredOut, mappingsTrfOut));
    cd.addSearch("homeVisitTrfOut", EptsReportUtils.map(homeVisitTrfOut, mappingsHomeVisitTrfOut));
    cd.addSearch("clinicalVisit", EptsReportUtils.map(clinicalVisit, mappingsClinicalVisit));

    cd.setCompositionString(
        "(transferredIn OR transferredOut OR homeVisitTrfOut) AND clinicalVisit AND NOT txCurr");

    return cd;
  }
}
