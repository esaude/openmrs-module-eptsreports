package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsDefaultersOrIITCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.reports.SetupListOfPatientsDefaultersOrIITReport;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalListOfPatientsDefaultersOrIITTemplateDataSet extends BaseDataSet {

  @Autowired ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries;

  @Autowired SetupListOfPatientsDefaultersOrIITReport setupListOfPatientsDefaultersOrIITReport;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Lista de Pacientes Faltosos ou Abandonos ao TARV");
    dataSetDefinition.addParameters(getParameters());

    CohortIndicator Total =
        eptsGeneralIndicator.getIndicator(
            "total",
            EptsReportUtils.map(
                listOfPatientsDefaultersOrIITCohortQueries.getBaseCohort(),
                "endDate=${endDate},minDay=${minDay},maxDay=${maxDay},location=${location}"));

    Total.addParameter(new Parameter("minDay", "minDay", Integer.class));
    Total.addParameter(new Parameter("maxDay", "maxDay", Integer.class));

    dataSetDefinition.addColumn(
        "total",
        "Total de Pacientes Faltosos ou Abandonos ao TARV",
        EptsReportUtils.map(
            Total, "endDate=${endDate},minDay=${minDay},maxDay=${maxDay},location=${location}"),
        "");

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "End date", Date.class),
        new Parameter("minDay", "Minimum number of days", Integer.class),
        new Parameter("maxDay", "Maximum number of days", Integer.class),
        new Parameter("location", "Location", Location.class));
  }
}
