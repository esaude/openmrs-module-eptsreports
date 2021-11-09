package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.SurveyDefaultCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.SurveyDefaultDimentions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SurveyDefaultDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private SurveyDefaultCohortQueries surveyDefaultCohortQueries;
  @Autowired private SurveyDefaultDimentions surveyDefaultDimentions;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructDatset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addDimension(
        "df", EptsReportUtils.map(surveyDefaultDimentions.getDefaultersDimentions(), mappings));

    dataSetDefinition.addColumn(
        "FD-TOTAL",
        "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período  (total)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período  (total)",
                EptsReportUtils.map(
                    this.surveyDefaultCohortQueries.getTotalDenominator(), mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "FD",
        "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período",
                EptsReportUtils.map(
                    this.surveyDefaultCohortQueries.getTotalDenominator(), mappings)),
            mappings),
        getColumns());

    dataSetDefinition.addColumn(
        "FN-TOTAL",
        "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período  (total)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período  (total)",
                EptsReportUtils.map(
                    this.surveyDefaultCohortQueries.getTotalDenominator(), mappings)),
            mappings),
        "");

    addRow(
        dataSetDefinition,
        "FN",
        "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Denominador: Nº de pacientes marcados para levantamento de ARV durante o período",
                EptsReportUtils.map(this.surveyDefaultCohortQueries.getTotalNumerator(), mappings)),
            mappings),
        getColumns());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumns() {

    ColumnParameters a1 = new ColumnParameters("subTotal", "subTotal", "age=0-14", "01");

    ColumnParameters a2 =
        new ColumnParameters("0-14", "0-14 years female", "gender=F|age=0-14", "02");
    ColumnParameters a3 =
        new ColumnParameters("0-14", "0-14 years male", "gender=M|age=0-14", "03");

    ColumnParameters a4 = new ColumnParameters("subTotal", "subTotal", "age=15+", "04");

    ColumnParameters a5 =
        new ColumnParameters("15+", "0-14 years famele", "gender=F|age=15+", "05");

    ColumnParameters a6 = new ColumnParameters("0-14", "0-14 years male", "gender=M|age=15+", "06");

    ColumnParameters a7 = new ColumnParameters("pregnant", "pregnant", "df=PREGNANT", "07");

    ColumnParameters a8 =
        new ColumnParameters("breastfeeding", "breastfeeding", "df=BREASTFEEDING", "08");

    ColumnParameters a9 = new ColumnParameters("cv", "cv", "df=CV", "09");

    ColumnParameters a10 = new ColumnParameters("apss", "apss", "df=APSS", "010");

    return Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  }
}
