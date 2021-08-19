package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListChildrenOnARTandFormulationsCohortQueries {

  private TxCurrCohortQueries txCurrCohortQueries;

  private AgeCohortQueries ageCohortQueries;

  @Autowired
  public ListChildrenOnARTandFormulationsCohortQueries(
      TxCurrCohortQueries txCurrCohortQueries, AgeCohortQueries ageCohortQueries) {
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.ageCohortQueries = ageCohortQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("base cohort");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txcurr = this.txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true);

    CohortDefinition children = this.ageCohortQueries.createXtoYAgeCohort("children", 0, 14);

    cd.addSearch(
        "txcurr", EptsReportUtils.map(txcurr, "onOrBefore=${endDate},location=${location}"));

    cd.addSearch("children", EptsReportUtils.map(children, "effectiveDate=${endDate}"));

    cd.setCompositionString("txcurr AND children");

    return cd;
  }
}
