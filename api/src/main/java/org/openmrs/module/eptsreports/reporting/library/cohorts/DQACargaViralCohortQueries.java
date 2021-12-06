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
public class DQACargaViralCohortQueries {

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public DQACargaViralCohortQueries(ResumoMensalCohortQueries resumoMensalCohortQueries) {
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String mapping1 = "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";
    String mapping2 = "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";
    String mapping3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";

    CohortDefinition E2 =
        resumoMensalCohortQueries
            .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults();

    compositionCohortDefinition.addSearch("L1", EptsReportUtils.map(E2, mapping1));
    compositionCohortDefinition.addSearch("L2", EptsReportUtils.map(E2, mapping2));
    compositionCohortDefinition.addSearch("L3", EptsReportUtils.map(E2, mapping3));

    compositionCohortDefinition.setCompositionString("L1 OR L2 OR L3");

    return compositionCohortDefinition;
  }
}
