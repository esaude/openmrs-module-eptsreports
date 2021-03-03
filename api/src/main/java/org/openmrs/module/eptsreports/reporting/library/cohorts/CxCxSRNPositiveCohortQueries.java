package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.CxCaSCRNQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CxCxSRNPositiveCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private CxCaSCRNCohortQueries cxCaSCRNCohortQueries;

  @DocumentedDefinition(value = "findpatientwithCxCaPositive")
  public CohortDefinition findpatientwithCxCaPositive() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(CxCaSCRNQueries.QUERY.findpatientwithCxCaPositive);

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithCxCaPositiveTotal")
  public CohortDefinition findpatientwithCxCaPositiveTotal() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TXCURR",
        EptsReportUtils.map(
            txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));
    definition.addSearch(
        "POSITIVE", EptsReportUtils.map(this.findpatientwithCxCaPositive(), mappings));

    definition.addSearch(
        "PREVIOS-SCREEN",
        EptsReportUtils.map(
            cxCaSCRNCohortQueries
                .findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod(),
            mappings));

    definition.setCompositionString("(TXCURR AND POSITIVE) NOT(PREVIOS-SCREEN)");

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithCxCaPositiveTotal")
  public CohortDefinition findpatientwithCxCaPositiveFirstScreen() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.findpatientwithCxCaPositiveTotal(), mappings));

    definition.addSearch(
        "SCREENING",
        EptsReportUtils.map(
            cxCaSCRNCohortQueries
                .findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod(),
            mappings));

    definition.setCompositionString("NUMERATOR AND SCREENING");

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithCxCaRescreenedAfterPreviousNegative")
  public CohortDefinition findpatientwithCxCaRescreenedAfterPreviousNegative() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.findpatientwithCxCaPositiveTotal(), mappings));

    definition.addSearch(
        "SCREENING-NEGATIVE",
        EptsReportUtils.map(
            cxCaSCRNCohortQueries
                .findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative(),
            mappings));

    definition.setCompositionString("NUMERATOR AND SCREENING-NEGATIVE");

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithCxPositivePostTreatmentFollowUp")
  public CohortDefinition findpatientwithCxPositivePostTreatmentFollowUp() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.findpatientwithCxCaPositiveTotal(), mappings));

    definition.addSearch(
        "PT",
        EptsReportUtils.map(
            cxCaSCRNCohortQueries.findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp(),
            mappings));

    definition.setCompositionString("NUMERATOR AND PT");

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithCxPositiveRescreenedAfterPreviousPositive")
  public CohortDefinition findpatientwithCxPositiveRescreenedAfterPreviousPositive() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.findpatientwithCxCaPositiveTotal(), mappings));

    definition.addSearch(
        "PT",
        EptsReportUtils.map(
            cxCaSCRNCohortQueries
                .findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousPositive(),
            mappings));

    definition.setCompositionString("NUMERATOR AND PT");

    return definition;
  }
}
