package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.SurveyDefaultQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurveyDefaultDimentions {

  @Autowired GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension getDefaultersDimentions() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("defaulters Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "BREASTFEEDING", SurveyDefaultQueries.getPatientsWhoAreBreastfeeding()),
            mappings));

    dimension.addCohortDefinition(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "PREGNANT", SurveyDefaultQueries.getPatientsWhoArePregnant()),
            mappings));

    dimension.addCohortDefinition(
        "CV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "CV", SurveyDefaultQueries.getPatientsWhoHaveViralLoadNotSupresed()),
            mappings));

    dimension.addCohortDefinition(
        "APSS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "APSS", SurveyDefaultQueries.getPatientsWhoHaveAPSSConsultation()),
            mappings));

    return dimension;
  }
}
