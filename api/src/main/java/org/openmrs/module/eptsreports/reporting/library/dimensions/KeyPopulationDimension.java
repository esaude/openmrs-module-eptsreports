/** */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.keypopulation.DrugsUserCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.keypopulation.HomosexualCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.keypopulation.PrisionerCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.keypopulation.SexWorkerCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class KeyPopulationDimension {

  @Autowired GenericCohortQueries genericCohortQueries;

  public CohortDefinitionDimension findPatientsWhoAreHomosexual() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Homosexual Patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition cohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "homosexualPatients",
            Context.getRegisteredComponents(HomosexualCalculation.class).get(0));

    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition("homosexual", EptsReportUtils.map(cohortDefinition, mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoUseDrugs() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Drugs User Patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition cohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "drugsUserPatients",
            Context.getRegisteredComponents(DrugsUserCalculation.class).get(0));

    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition("drug-user", EptsReportUtils.map(cohortDefinition, mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreInPrison() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Patients in prision");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition cohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "prisionersPatients",
            Context.getRegisteredComponents(PrisionerCalculation.class).get(0));

    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition("prisioner", EptsReportUtils.map(cohortDefinition, mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsWhoAreSexWorker() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("Sex worker patients");
    dimension.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition cohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "sexWorkerPatients",
            Context.getRegisteredComponents(SexWorkerCalculation.class).get(0));

    cohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    cohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition("sex-worker", EptsReportUtils.map(cohortDefinition, mappings));

    return dimension;
  }
}
