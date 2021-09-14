package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.ListDefaultarsIITValidationInputParametersCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.library.queries.ListPatientsDefaultersIITQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListPatientsDefaultersIITDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("Find list of patients defaulters or IIT");
    dsd.addParameters(list);
    dsd.setSqlQuery(ListPatientsDefaultersIITQueries.QUERY.findPatientsDefaultersIITList);
    return dsd;
  }

  public DataSetDefinition getTotalDefaultersIITDataset(List<Parameter> list) {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Patients Defaulters IIT Total");
    dataSetDefinition.addParameters(list);

    final String mappings =
        "minDelayDays=${minDelayDays},maxDelayDays=${maxDelayDays},endDate=${endDate},location=${location}";

    dataSetDefinition.addColumn(
        "TOTALDEFAULTERSIIT",
        "Total de Pacientes abandonos e faltosos",
        EptsReportUtils.map(
            setIndicator(this.getPatientesDefaultersWithValidParameters(), mappings), mappings),
        "");

    return dataSetDefinition;
  }

  @DocumentedDefinition(value = "PatientesDefaultersWithValidParameters")
  private CohortDefinition getPatientesDefaultersWithValidParameters() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();

    compositionDefinition.setName("get List of Patients Defaulters IIT");
    compositionDefinition.addParameter(
        new Parameter("minDelayDays", "minDelayDays", Integer.class));
    compositionDefinition.addParameter(
        new Parameter("maxDelayDays", "maxDelayDays", Integer.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "minDelayDays=${minDelayDays},maxDelayDays=${maxDelayDays},endDate=${endDate},location=${location}";

    compositionDefinition.addSearch(
        "PATIENT-DEFAULTERS-IIT", EptsReportUtils.map(this.findPatientsDefaultersIIT(), mappings));

    compositionDefinition.addSearch(
        "VALID-PARAMETERS", EptsReportUtils.map(this.validateInputParameters(), mappings));

    compositionDefinition.setCompositionString("PATIENT-DEFAULTERS-IIT OR VALID-PARAMETERS");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "findPatientsDefaultersIIT")
  private CohortDefinition findPatientsDefaultersIIT() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsDefaultersIIT");
    definition.addParameter(new Parameter("minDelayDays", "minDelayDays", Integer.class));
    definition.addParameter(new Parameter("maxDelayDays", "maxDelayDays", Integer.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(ListPatientsDefaultersIITQueries.QUERY.findPatientsDefaultersIIT);

    return definition;
  }

  @DocumentedDefinition(value = "ListDefaultarsIITValidationInputParametersCalculation")
  private CohortDefinition validateInputParameters() {
    BaseFghCalculationCohortDefinition definition =
        new BaseFghCalculationCohortDefinition(
            "Validate input parameteres",
            Context.getRegisteredComponents(
                    ListDefaultarsIITValidationInputParametersCalculation.class)
                .get(0));
    definition.addParameter(new Parameter("minDelayDays", "minDelayDays", Integer.class));
    definition.addParameter(new Parameter("maxDelayDays", "maxDelayDays", Integer.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    return definition;
  }

  private CohortIndicator setIndicator(
      final CohortDefinition cohortDefinition, final String mappings) {

    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            "totalDefaultersIIT", EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("minDelayDays", "minDelayDays", Integer.class));
    indicator.addParameter(new Parameter("maxDelayDays", "maxDelayDays", Integer.class));
    indicator.addParameter(new Parameter("endDate", "End Date", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }
}
