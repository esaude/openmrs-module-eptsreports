package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.CxCaTXQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.CxType;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CxDimensions {

  @Autowired GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension findPatientsWhoHaveCryotherapyTretment() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Cryotherapy Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "cryotherapy",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "cryotherapy",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.CRYOTHERAPY)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoHaveThermocoagulationTretment() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("thermocoagulation Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "thermocoagulation",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "thermocoagulation",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.THERMOCOAGULATION)),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoHaveLeepTretment() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Cryotherapy Dimension");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dimension.addCohortDefinition(
        "leep",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "leep",
                CxCaTXQueries.QUERY.findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
                    CxType.LEEP)),
            mappings));

    return dimension;
  }
}
