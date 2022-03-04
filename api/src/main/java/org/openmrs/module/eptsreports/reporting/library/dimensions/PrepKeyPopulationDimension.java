/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.KeyPopType;
import org.openmrs.module.eptsreports.reporting.library.queries.PrepKeyPopQuery;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepKeyPopulationDimension {

  @Autowired GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension findPatientsWhoAreHomosexual() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("HOMOSEXUAL Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "homosexual",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "homosexual", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.HOMOSEXUAL)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoUseDrugs() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Drugs User Patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "drug-user",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "drug-user", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.DRUGUSER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreInPrison() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Patients in prision");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "prisioner",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "prisioner", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.PRISIONER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreSexWorker() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Sex worker patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "sex-worker",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "sex-worker", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.SEXWORKER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreTransGender() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Transgender patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "transgender",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "transgender", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.TRANSGENDER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreSeroDiscordants() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Serodiscordants patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "serodiscordants",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "serodiscordants",
                PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.SERODISCORDANTS)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreTrucker() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Trucker patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "trucker",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "trucker", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.TRUCKER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreMiner() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Miners patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "miner",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "miner", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.MINER)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreMilitary() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Military patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "military",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "military", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.MILITARY)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreBreastfeedingAgeGreaterOrEqualThan15() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Breastfeeding patients 15+");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "breastfeeding",
                PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.BREASTFEEDING)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoArePregnantAgeGreaterOrEqualThan15() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Pregnant patients 15+");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "pregnants",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "pregnants", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.PREGNANT)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findAdolescentsAndYouthsPatientsInRisk() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Adolescents an Youths patients in Risk");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "youths",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "youths", PrepKeyPopQuery.findPatientsWhoAreKeyPop(KeyPopType.YOUTHS)),
            mappings));

    return dimension;
  }
}
