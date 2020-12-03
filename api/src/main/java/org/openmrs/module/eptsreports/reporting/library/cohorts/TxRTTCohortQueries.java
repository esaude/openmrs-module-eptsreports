/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPLHIVGreater12MonthCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPLHIVLess12MonthCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoAreTransferedOutCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRTTPatientsWhoExperiencedIITCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
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

  @Autowired private GenericCohortQueries genericCohortQueries;

  @DocumentedDefinition(value = "TxRttPatientsOnRTT")
  public CohortDefinition getPatientsOnRTT() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT - Patients on RTT");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-1d},endDate=${startDate-1d},realEndDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "IIT-PREVIOUS-PERIOD",
        EptsReportUtils.map(this.getPatientsWhoExperiencedIITCalculation(), mappings));

    compositionDefinition.addSearch(
        "RTT-TRANFERRED-OUT",
        EptsReportUtils.map(
            this.getPatientsWhoWhereTransferredOutCalculation(),
            "endDate=${startDate},location=${location}"));

    compositionDefinition.addSearch(
        "TX-CURR",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));

    compositionDefinition.setCompositionString(
        "(IIT-PREVIOUS-PERIOD NOT RTT-TRANFERRED-OUT) AND TX-CURR");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "TxRttPatientsWhoExperiencedIITCalculation")
  public CohortDefinition getPatientsWhoExperiencedIITCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoExperiencedIITCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoExperiencedIITCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "TxRttPatientsWhoWhereTransferredOutCalculation")
  public CohortDefinition getPatientsWhoWhereTransferredOutCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRTTPatientsWhoWhereTransferredOutCalculation",
            Context.getRegisteredComponents(TxRTTPatientsWhoAreTransferedOutCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    return definition;
  }

  @DocumentedDefinition(value = "TxRttPLHIVLess12MonthCalculation")
  public CohortDefinition getPLHIVLess12MonthCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRttPLHIVLess12MonthCalculation",
            Context.getRegisteredComponents(TxRTTPLHIVLess12MonthCalculation.class).get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "TxRttPLHIVGreater12MonthCalculation")
  public CohortDefinition getPLHIVGreather12MonthCalculation() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "txRttPLHIVGreater12MonthCalculation",
            Context.getRegisteredComponents(TxRTTPLHIVGreater12MonthCalculation.class).get(0));
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "end Date", Date.class));
    definition.addParameter(new Parameter("realEndDate", "Real End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    return definition;
  }

  @DocumentedDefinition(value = "TxRttPLHIVUnknownDesaggregation")
  public CohortDefinition getPLHIVUnknownDesaggregation() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("Tx RTT- Unknown Desaggretation");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate},endDate=${endDate},realEndDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "RTT-NUMERATOR",
        EptsReportUtils.map(
            this.getPatientsOnRTT(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionDefinition.addSearch(
        "RTT-GREATER12MONTHS",
        EptsReportUtils.map(this.getPLHIVGreather12MonthCalculation(), mappings));

    compositionDefinition.addSearch(
        "RTT-LESS12MONTHS", EptsReportUtils.map(this.getPLHIVLess12MonthCalculation(), mappings));

    compositionDefinition.setCompositionString(
        "RTT-NUMERATOR NOT (RTT-GREATER12MONTHS OR RTT-LESS12MONTHS)");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "C1")
  public CohortDefinition testRTTFromEurico() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("NumberOfPatientsWhoAreCurrentlyEnrolledOnArtMOHC1");

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "TX-CURR",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));

    compsitionDefinition.addSearch(
        "IIT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWithSeguimentoDeAdesao", TxRttQueries.QUERY.rttFromEurico),
            "endDate=${startDate-1d},location=${location}"));

    compsitionDefinition.setCompositionString("TX-CURR AND IIT");

    return compsitionDefinition;
  }
}
