/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxRTTCohortQueries {

  public CohortDefinition findPatientsOnRTT() {

    final BaseFghCalculationCohortDefinition cohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "patientsOnRTT", Context.getRegisteredComponents(TxRttCalculation.class).get(0));

    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    return cohortDefinition;
  }
}
