package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.defaulters.SurveyDefaultersCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.SurveyDefaultQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class SurveyDefaultCohortQueries {

  @DocumentedDefinition(value = "findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod")
  public CohortDefinition findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        SurveyDefaultQueries.findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod());

    return definition;
  }

  @DocumentedDefinition(value = "TRF-OUT")
  public CohortDefinition getPatientsTransferredFromAnotherHealthFacilityUntilEndDate() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("getPatientsTransferredFromAnotherHealthFacilityUntilEndDate");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        SurveyDefaultQueries.getPatientsTransferredFromAnotherHealthFacilityUntilEndDate();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "DIED")
  public CohortDefinition getPatientsWhoDied() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("getPatientsWhoDied");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = SurveyDefaultQueries.getPatientsWhoDied();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "SUSPEND")
  public CohortDefinition getPatientsWhoSuspendTratment() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("getPatientsWhoSuspendTratment");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = SurveyDefaultQueries.getPatientsWhoSuspendTratment();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "NUMERATOR")
  public CohortDefinition findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumerator() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        SurveyDefaultQueries
            .findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumerator();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "CV")
  public CohortDefinition getPatientsWhoHaveViralLoadNotSupresed() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "CV", Context.getRegisteredComponents(SurveyDefaultersCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  public CohortDefinition getTotalDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalDenominator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(
            this.findPatientswhoHaveScheduledAppointmentsDuringReportingPeriod(), mappings));

    definition.addSearch(
        "TRF-OUT",
        EptsReportUtils.map(
            this.getPatientsTransferredFromAnotherHealthFacilityUntilEndDate(), mappings));

    definition.addSearch("DIED", EptsReportUtils.map(this.getPatientsWhoDied(), mappings));

    definition.addSearch(
        "SUSPEND", EptsReportUtils.map(this.getPatientsWhoSuspendTratment(), mappings));

    definition.setCompositionString(" DENOMINATOR NOT(TRF-OUT OR DIED OR SUSPEND)");

    return definition;
  }

  public CohortDefinition getTotalNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("getTotalNumerator");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch("DENOMINATOR", EptsReportUtils.map(this.getTotalDenominator(), mappings));

    definition.addSearch(
        "NUMERATOR",
        EptsReportUtils.map(
            this.findPatientswhoHaveScheduledAppointmentsDuringReportingPeriodNumerator(),
            mappings));

    definition.setCompositionString("DENOMINATOR AND NUMERATOR");

    return definition;
  }
}
