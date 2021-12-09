package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsBySourceQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsBySourceQueriesInterface.QUERY.SourceType;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueriesInterface.QUERY.WomanState;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class PvlsBySourceCohortQueries {

  @DocumentedDefinition(
      value = "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsBySource")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsBySource(
          SourceType sourceType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsBySource By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsBySourceQueriesInterface.QUERY
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                sourceType));

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTargetBySoure")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
          SourceType sourceType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTargetBySource By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsBySourceQueriesInterface.QUERY
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                sourceType));

    return definition;
  }

  @DocumentedDefinition(
      value = "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
          SourceType sourceType) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MOTHSVL12MONTHSVL",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsBySource(
                    sourceType),
            mappings));

    definition.addSearch(
        "TARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.setCompositionString("IART3MOTHSVL12MONTHSVL NOT TARGET");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsByLabAndFSRSources")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(
          SourceType sourceType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsByLabAndFSRSources By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsBySourceQueriesInterface.QUERY
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(
                sourceType));

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
          SourceType sourceType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsBySourceQueriesInterface.QUERY
            .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                sourceType));

    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
          SourceType sourceType) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MOTHSVL12MONTHSVL1000",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(
                    sourceType),
            mappings));

    definition.addSearch(
        "TARGET1000",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.setCompositionString("IART3MOTHSVL12MONTHSVL1000 NOT TARGET1000");

    return definition;
  }

  @DocumentedDefinition(
      value = "WomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
          SourceType sourceType, WomanState womanState) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "WomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TxPvlsBySourceQueriesInterface.QUERY
            .findWomanStateWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                sourceType, womanState));

    return definition;
  }

  @DocumentedDefinition(
      value = "WomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREGNANT3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.addSearch(
        "ROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                    sourceType),
            mappings));

    definition.setCompositionString("PREGNANT3MONTHSVL12 AND ROTINE");

    return definition;
  }

  @DocumentedDefinition(
      value = "WomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "PREGNANT3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.addSearch(
        "TARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.setCompositionString("PREGNANT3MONTHSVL12 AND TARGET");

    return definition;
  }

  @DocumentedDefinition(
      value = "womanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine")
  public CohortDefinition
      findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "BREASTEFEEDING3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.addSearch(
        "ROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsRotine(
                    sourceType),
            mappings));

    definition.setCompositionString("BREASTEFEEDING3MONTHSVL12 AND ROTINE");

    return definition;
  }

  @DocumentedDefinition(
      value = "womanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget")
  public CohortDefinition
      findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "BREASTEFEEDING3MONTHSVL12",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.addSearch(
        "TARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.setCompositionString("BREASTEFEEDING3MONTHSVL12 AND TARGET");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "womanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
                    sourceType),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.setCompositionString("IART3MONTHSVL12MONTHSROTINE AND PREGNANT");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "WomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target")
  public CohortDefinition
      findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSTARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.setCompositionString("IART3MONTHSVL12MONTHSTARGET AND PREGNANT");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine")
  public CohortDefinition
      findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Rotine(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSROTINE",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine(
                    sourceType),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.setCompositionString("IART3MONTHSVL12MONTHSROTINE AND BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target")
  public CohortDefinition
      findPregnantBreatsFeedingWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12MonthsWithVlMoreThan1000Target(
          SourceType sourceType, WomanState womanState) {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "PatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsRotine By State["
            + womanState.name()
            + "] and Source["
            + sourceType.name()
            + "]");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "IART3MONTHSVL12MONTHSTARGET",
        EptsReportUtils.map(
            this
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12MonthsTarget(
                    sourceType),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(
                    sourceType, womanState),
            mappings));

    definition.setCompositionString("IART3MONTHSVL12MONTHSTARGET AND BREASTFEEDING");

    return definition;
  }
}
