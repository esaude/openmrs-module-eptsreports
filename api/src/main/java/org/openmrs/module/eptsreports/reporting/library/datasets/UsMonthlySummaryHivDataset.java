package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.noMappings;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  public DataSetDefinition constructUsMonthlySummaryHivDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("US Monthly Summary HIV Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", noMappings(eptsCommonDimension.gender()));
    dataSetDefinition.addDimension(
        "age", map(eptsCommonDimension.getUsMonthlySummaryHivAges(), "effectiveDate=${endDate}"));

    addRow(
        dataSetDefinition,
        "A1",
        "Nº cumulativo de pacientes registados até o fim do mês anterior",
        getRegisteredInPreArtBooks1and2(),
        getColumnParameters());

    return dataSetDefinition;
  }

  private Mapped<CohortIndicator> getRegisteredInPreArtBooks1and2() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV REGISTADOS ATE O FIM DE UM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInPreArtBooks1and2();
    String mappings = "onOrBefore=${startDate-1d},locationList=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return map(indicator, "startDate=${startDate},endDate=${endDate},location=${location}");
  }

  private List<ColumnParameters> getColumnParameters() {
    return Arrays.asList(
        new ColumnParameters("Female under 15", "Female under 15", "gender=F|age=0-14", "F014"),
        new ColumnParameters("Female above 15", "Female above 15", "gender=F|age=15+", "F15"),
        new ColumnParameters("Male under 15", "Male under 15", "gender=M|age=0-14", "M014"),
        new ColumnParameters("Male above 15", "Male above 15", "gender=M|age=15+", "M15"));
  }
}
