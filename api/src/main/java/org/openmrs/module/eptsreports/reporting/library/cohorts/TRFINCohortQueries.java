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

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.trfin.TRFINPatientsWhoAreTransferedInCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRFINCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @DocumentedDefinition(value = "patientsWhoAreTransferedIn")
  public CohortDefinition getPatiensWhoAreTransferredIn() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("TRF-IN-NUMERATOR");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "TRF-IN", EptsReportUtils.map(this.getPatientsWhoAreTransferredIn(), mappings));

    compositionDefinition.addSearch(
        "TX-CURR-PREVIOUS-PERIOD",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${startDate-1d},location=${location}"));

    compositionDefinition.setCompositionString("(TRF-IN NOT TX-CURR-PREVIOUS-PERIOD");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "trfInPatientsWhoAreTransferedIn")
  private CohortDefinition getPatientsWhoAreTransferredIn() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "trfInPatientsWhoAreTransferedInCalculation",
            Context.getRegisteredComponents(TRFINPatientsWhoAreTransferedInCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }
}
