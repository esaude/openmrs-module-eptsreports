package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EriDSDCohortQueries {
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  public CohortDefinition getAllPatientsWhosAgeIsGreaterOrEqualTo2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Number of active, stable, patients on ART");
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "total",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cd.getName(), true),
            "onOrBefore=${onOrBefore},location=${location}"));
    cd.setCompositionString("total");
    return cd;
  }
}
