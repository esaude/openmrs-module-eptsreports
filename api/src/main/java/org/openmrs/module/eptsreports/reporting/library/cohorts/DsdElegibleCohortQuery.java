package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DsdElegibleCohortQuery {

  @Autowired private GenericCohortQueries genericCohorts;
  @Autowired private DSDCohortQueries dsdCohortQueries;

  public CohortDefinition getAdultActiveOnArtElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE",
        EptsReportUtils.map(dsdCohortQueries.getPatientsActiveOnArtEligibleForDsd(""), mappings));

    dsd.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "ADULT", DsdQueriesInterface.QUERY.findPatientsAge15Plus),
            mappings));

    dsd.setCompositionString("ELEGIBLE AND ADULT");

    return dsd;
  }

  public CohortDefinition getChild2To4ActiveOnArtElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE",
        EptsReportUtils.map(dsdCohortQueries.getPatientsActiveOnArtEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD2TO4",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD2TO4", DsdQueriesInterface.QUERY.findPatientsAge2to4),
            mappings));

    dsd.setCompositionString("ELEGIBLE AND CHILD2TO4");

    return dsd;
  }

  public CohortDefinition getChild5To9ActiveOnArtElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE",
        EptsReportUtils.map(dsdCohortQueries.getPatientsActiveOnArtEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD5TO9",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD5TO9", DsdQueriesInterface.QUERY.findPatientsAge5to9),
            mappings));

    dsd.setCompositionString("ELEGIBLE AND CHILD5TO9");

    return dsd;
  }

  public CohortDefinition getChild10To14ActiveOnArtElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "ELEGIBLE",
        EptsReportUtils.map(dsdCohortQueries.getPatientsActiveOnArtEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD10T14",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD10T14", DsdQueriesInterface.QUERY.findPatientsAge10to14),
            mappings));

    dsd.setCompositionString("ELEGIBLE AND CHILD10T14");

    return dsd;
  }

  public CohortDefinition getAdultActiveOnArtNotElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(
            dsdCohortQueries.getPatientsActiveOnArtNotEligibleForDsd(""), mappings));

    dsd.addSearch(
        "ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "ADULT", DsdQueriesInterface.QUERY.findPatientsAge15Plus),
            mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND ADULT");

    return dsd;
  }

  public CohortDefinition getChildLessthan2ActiveOnArtNotElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(
            dsdCohortQueries.getPatientsActiveOnArtNotEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD2",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD2", DsdQueriesInterface.QUERY.findPatientsAgeLessThan2),
            mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND CHILD2");

    return dsd;
  }

  public CohortDefinition getChild2To4ActiveOnArtNotElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(
            dsdCohortQueries.getPatientsActiveOnArtNotEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD2TO4",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD2TO4", DsdQueriesInterface.QUERY.findPatientsAge2to4),
            mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND CHILD2TO4");

    return dsd;
  }

  public CohortDefinition getChild5To9ActiveOnNotArtElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(
            dsdCohortQueries.getPatientsActiveOnArtNotEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD5TO9",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD5TO9", DsdQueriesInterface.QUERY.findPatientsAge5to9),
            mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND CHILD5TO9");

    return dsd;
  }

  public CohortDefinition getChild10To14ActiveOnArtNotElegibleDsd(final String cohortName) {

    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName(cohortName);
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "NOT-ELEGIBLE",
        EptsReportUtils.map(
            dsdCohortQueries.getPatientsActiveOnArtNotEligibleForDsd(""), mappings));

    dsd.addSearch(
        "CHILD10T14",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "CHILD10T14", DsdQueriesInterface.QUERY.findPatientsAge10to14),
            mappings));

    dsd.setCompositionString("NOT-ELEGIBLE AND CHILD10T14");

    return dsd;
  }
}
