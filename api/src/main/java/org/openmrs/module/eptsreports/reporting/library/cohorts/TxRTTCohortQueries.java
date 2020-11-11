/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPLHIVLess12MonthCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoAreTransferedOutCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoExperiencedIITCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxRTTCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @DocumentedDefinition(value = "txRttPatientsOnRTT")
  public CohortDefinition getPatientsOnRTT() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT - Patients on RTT");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-1d},endDate=${startDate-1d},location=${location}";

    compositionDefinition.addSearch(
        "RTT-PREVIOUS-PERIOD",
        EptsReportUtils.map(this.getPatientsWhoExperiencedIITCalculation(), mappings));

    compositionDefinition.addSearch(
        "RTT-TRANFERRED-OUT",
        EptsReportUtils.map(this.getPatientsWhoWhereTransferredOutCalculation(), mappings));

    compositionDefinition.addSearch(
        "TX-CURR",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));

    compositionDefinition.setCompositionString(
        "(RTT-PREVIOUS-PERIOD NOT RTT-TRANFERRED-OUT) AND TX-CURR");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "txRTTPLHIVGreaterOrEqual12Month")
  public CohortDefinition getPLHIVGreaterOrEqual12Month() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("TX-RTT - PLHIV Greater 12 Month");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "RTT-NUMERATOR", EptsReportUtils.map(this.getPatientsOnRTT(), mappings));

    compositionDefinition.addSearch(
        "RTT-LESS-12MONTHS", EptsReportUtils.map(this.getPLHIVLess12MonthCalculation(), mappings));

    compositionDefinition.setCompositionString("RTT-NUMERATOR NOT RTT-LESS-12MONTHS");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "txRttPatientsWhoExperiencedIITCalculation")
  public CohortDefinition getPatientsWhoExperiencedIITCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoExperiencedIITCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoExperiencedIITCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "txRttPatientsWhoWhereTransferredOutCalculation")
  private CohortDefinition getPatientsWhoWhereTransferredOutCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoWhereTransferredOutCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoAreTransferedOutCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "txRttPLHIVLess12MonthCalculation")
  public CohortDefinition getPLHIVLess12MonthCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoAreDeadCalculation",
            Context.getRegisteredComponents(TxRTTPLHIVLess12MonthCalculation.class).get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }
}
