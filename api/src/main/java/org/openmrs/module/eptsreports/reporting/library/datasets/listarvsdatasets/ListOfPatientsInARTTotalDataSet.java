package org.openmrs.module.eptsreports.reporting.library.datasets.listarvsdatasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PatientsInARTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsInARTTotalDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private PatientsInARTCohortQueries PatientsInARTCohortQueries;

  public DataSetDefinition constructTDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("INICIOS ARV Data Set");
    dataSetDefinition.setParameters(this.getParameters());

    final String mappings =
        "cohortStartDate=${cohortStartDate},cohorEndDate=${cohorEndDate},evaluationDate=${evaluationDate},location=${location}";

    dataSetDefinition.addColumn(
        "IT",
        "IT",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                PatientsInARTCohortQueries.findPatientsWhoInARTTotal(), "IT", mappings),
            mappings),
        "");

    return dataSetDefinition;
  }

  public CohortIndicator setIndicatorWithAllParameters(
      final CohortDefinition cohortDefinition, final String indicatorName, final String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    indicator.addParameter(new Parameter("cohorEndDate", "Cohort End Date", Date.class));
    indicator.addParameter(new Parameter("evaluationDate", "Evaluation Date", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("cohortStartDate", "Cohort Start Date", Date.class),
        new Parameter("cohorEndDate", "Cohort End Date", Date.class),
        new Parameter("evaluationDate", "Evaluation Date", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
