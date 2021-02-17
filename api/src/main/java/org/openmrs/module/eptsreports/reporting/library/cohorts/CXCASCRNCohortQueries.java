package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.CXCASCRNQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNCohortQueries {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  private static final int CONCEPT_2094 = 2094;
  private static final int ANSWER_2093 = 2093;
  private static final int ANSWER_664 = 664;
  private static final int ANSWER_703 = 703;

  @DocumentedDefinition(
      value = "findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod")
  public CohortDefinition findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod")
  public CohortDefinition findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY
            .findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative")
  public CohortDefinition
      findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY
            .findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodByReusult(
                CONCEPT_2094, ANSWER_664));

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodPositive")
  public CohortDefinition
      findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodPositive() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY
            .findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodByReusult(
                CONCEPT_2094, ANSWER_703));

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodSuspectCancer")
  public CohortDefinition
      findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodSuspectCancer() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY
            .findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodByReusult(
                CONCEPT_2094, ANSWER_2093));

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative")
  public CohortDefinition findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY.findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative);

    return definition;
  }

  @DocumentedDefinition(value = "findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp")
  public CohortDefinition findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        CXCASCRNQueries.QUERY.findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp);

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumerator")
  public CohortDefinition getTotalNumerator() {
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
        "SCREENING",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriod(), mappings));

    definition.setCompositionString(" TXCURR AND SCREENING");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumerator")
  public CohortDefinition getTotalNumeratorFirstScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("SCREENING", EptsReportUtils.map(this.getTotalNumerator(), mappings));

    definition.addSearch(
        "PREVIOUS-SCREENING",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod(),
            mappings));

    definition.setCompositionString("(SCREENING NOT PREVIOUS-SCREENING");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorFirstScreeningNegative")
  public CohortDefinition getTotalNumeratorFirstScreeningNegative() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.getTotalNumeratorFirstScreening(), mappings));

    definition.addSearch(
        "FIRST-SCREENING-NEGATIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative(),
            mappings));

    definition.setCompositionString("NUMERATOR AND FIRST-SCREENING-NEGATIVE");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorFirstScreeningPositive")
  public CohortDefinition getTotalNumeratorFirstScreeningPositive() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.getTotalNumeratorFirstScreening(), mappings));

    definition.addSearch(
        "SCREENING-POSITIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodPositive(),
            mappings));

    definition.setCompositionString("NUMERATOR AND SCREENING-POSITIVE");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorFirstScreeningSuspectCancer")
  public CohortDefinition getTotalNumeratorFirstScreeningSuspectCancer() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NUMERATOR", EptsReportUtils.map(this.getTotalNumeratorFirstScreening(), mappings));

    definition.addSearch(
        "SUSPECT-CANCER",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodSuspectCancer(),
            mappings));

    definition.setCompositionString("NUMERATOR AND SUSPECT-CANCER");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorRescreenedAfterPreviousNegativeNegative")
  public CohortDefinition getTotalNumeratorRescreenedAfterPreviousNegativeTotal() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("SCREENING", EptsReportUtils.map(this.getTotalNumerator(), mappings));

    definition.addSearch(
        "N2",
        EptsReportUtils.map(
            this.findPatientWithScreeningTypeVisitAsRescreenedAfterPreviousNegative(), mappings));

    definition.setCompositionString("SCREENING AND N2");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorRescreenedAfterPreviousNegativeNegative")
  public CohortDefinition getTotalNumeratorRescreenedAfterPreviousNegativeNegative() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NEGATIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative(),
            mappings));

    definition.addSearch(
        "N2",
        EptsReportUtils.map(
            this.getTotalNumeratorRescreenedAfterPreviousNegativeTotal(), mappings));

    definition.setCompositionString("NEGATIVE AND N2");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorRescreenedAfterPreviousNegativePositive")
  public CohortDefinition getTotalNumeratorRescreenedAfterPreviousNegativePositive() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "POSITIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodPositive(),
            mappings));

    definition.addSearch(
        "P2",
        EptsReportUtils.map(
            this.getTotalNumeratorRescreenedAfterPreviousNegativeTotal(), mappings));

    definition.setCompositionString("POSITIVE AND P2");

    return definition;
  }

  @DocumentedDefinition(value = "getTotalNumeratorRescreenedAfterPreviousNegativeSuspectCancer")
  public CohortDefinition getTotalNumeratorRescreenedAfterPreviousNegativeSuspectCancer() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "SUSPECT-CANCER",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodSuspectCancer(),
            mappings));

    definition.addSearch(
        "S2",
        EptsReportUtils.map(
            this.getTotalNumeratorRescreenedAfterPreviousNegativeTotal(), mappings));

    definition.setCompositionString("SUSPECT-CANCER AND S2");

    return definition;
  }

  @DocumentedDefinition(
      value = "getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpNegative")
  public CohortDefinition
      getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpTotal() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREVIUS-SCREENING",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerPreviousReportingPeriod(),
            mappings));

    definition.addSearch(
        "P2",
        EptsReportUtils.map(
            this.findpatientwithScreeningTypeVisitAsPostTreatmentFollowUp(), mappings));

    definition.setCompositionString("PREVIUS-SCREENING AND P2");

    return definition;
  }

  @DocumentedDefinition(
      value = "getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpNegative")
  public CohortDefinition
      getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpNegative() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "NEGATIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodNegative(),
            mappings));
    definition.addSearch(
        "P2",
        EptsReportUtils.map(
            this.getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpTotal(),
            mappings));

    definition.setCompositionString("NEGATIVE AND P2");

    return definition;
  }

  @DocumentedDefinition(
      value = "getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpPositive")
  public CohortDefinition
      getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpPositive() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "POSITIVE",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodPositive(),
            mappings));
    definition.addSearch(
        "P2",
        EptsReportUtils.map(
            this.getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpTotal(),
            mappings));

    definition.setCompositionString("POSITIVE AND P2");

    return definition;
  }

  @DocumentedDefinition(
      value = "getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpPositive")
  public CohortDefinition
      getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpSuspectCancer() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "SUSPECT-CANCER",
        EptsReportUtils.map(
            this.findPatientsWithScreeningTestForCervicalCancerDuringReportingPeriodSuspectCancer(),
            mappings));
    definition.addSearch(
        "P2",
        EptsReportUtils.map(
            this.getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpTotal(),
            mappings));

    definition.setCompositionString("SUSPECT-CANCER AND P2");

    return definition;
  }
}
