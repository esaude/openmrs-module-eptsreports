package org.openmrs.module.eptsreports.reporting.library.datasets.listarvsdatasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PatientsInARTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsInARTTotalDataSet extends BaseDataSet {

  @Autowired private PatientsInARTCohortQueries PatientsInARTCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructTDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("TXNEW");
    dataSetDefinition.addParameters(getParameters());

    final String mappings =
        "cohortStartDate=${cohortStartDate},cohorEndDate=${cohorEndDate},location=${location}";

    final CohortDefinition total = this.PatientsInARTCohortQueries.findPatientsWhoInARTTotal();

    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator("TXNEW", EptsReportUtils.map(total, mappings));

    indicator.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    indicator.addParameter(new Parameter("cohorEndDate", "Cohort End Date", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    dataSetDefinition.addColumn("TOTAL", "TXNEW", EptsReportUtils.map(indicator, mappings), "");
    
    return dataSetDefinition;
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
