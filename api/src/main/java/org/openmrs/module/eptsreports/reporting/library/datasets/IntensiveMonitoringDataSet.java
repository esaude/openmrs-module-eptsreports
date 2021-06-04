package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IntensiveMonitoringCohortQueries;
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
public class IntensiveMonitoringDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructIntensiveMonitoringDataSet() {

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Intensive Monitoring DataSet");
    dataSetDefinition.addParameters(getParameters());

    /**
     * ******** DIMENSIONS will be added here based on individual indicators required
     * *****************************
     */

    // 7.1
    dataSetDefinition.addColumn(
        "MI7DEN1",
        "% de adultos HIV+ em TARV elegíveis ao TPT e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(1),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.2
    dataSetDefinition.addColumn(
        "MI7DEN2",
        "% de adultos HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(2),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.3
    dataSetDefinition.addColumn(
        "MI7DEN3",
        "% de crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(3),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.4
    dataSetDefinition.addColumn(
        "MI7DEN4",
        "% de crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(4),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.5
    dataSetDefinition.addColumn(
        "MI7DEN5",
        "% de mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(5),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");
    // 7.6
    dataSetDefinition.addColumn(
        "MI7DEN6",
        "% de MG HIV+ em TARV que iniciou TPI e que terminou TPI",
        EptsReportUtils.map(
            customCohortIndicator(
                intensiveMonitoringCohortQueries.getCat7DenMOHIV202171Definition(6),
                "revisionEndDate=${revisionEndDate},location=${location}"),
            "revisionEndDate=${revisionEndDate},location=${location}"),
        "");

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("revisionEndDate", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }

  private CohortIndicator customCohortIndicator(CohortDefinition cd, String mapping) {
    CohortIndicator cohortIndicator =
        eptsGeneralIndicator.getIndicator(cd.getName(), EptsReportUtils.map(cd, mapping));
    cohortIndicator.addParameter(new Parameter("revisionEndDate", "Revision Date", Date.class));
    return cohortIndicator;
  }
}
