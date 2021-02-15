package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.CXCASCRNCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private CXCASCRNCohortQueries cXCASCRNCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTMqDatset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.ageCX(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addColumn(
        "CXTOTAL",
        "Total patients with Viral load - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(this.cXCASCRNCohortQueries.getTotalNumerator(), mappings)),
            mappings),
        "gender=F");

    dataSetDefinition.addColumn(
        "CXFIRSTTOTAL",
        "Total patients with Viral load - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients with viral load",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries.getTotalNumeratorFirstScreeningNegative(),
                    mappings)),
            mappings),
        "gender=F");

    addRow(
        dataSetDefinition,
        "CXN",
        "Negative Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Negative Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries.getTotalNumeratorFirstScreeningNegative(),
                    mappings)),
            mappings),
        getCXColumns());

    addRow(
        dataSetDefinition,
        "CXP",
        "Positive Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Positive Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries.getTotalNumeratorFirstScreeningPositive(),
                    mappings)),
            mappings),
        getCXColumns());

    addRow(
        dataSetDefinition,
        "CXS",
        "Suspect Cancer Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Suspect Cancer Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries.getTotalNumeratorFirstScreeningSuspectCancer(),
                    mappings)),
            mappings),
        getCXColumns());

    dataSetDefinition.addColumn(
        "CXRNTOTAL",
        "Total patients with CX RN - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients with CX RN - Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorRescreenedAfterPreviousNegativeNegative(),
                    mappings)),
            mappings),
        "gender=F");

    addRow(
        dataSetDefinition,
        "RNN",
        "Negative Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Negative Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorRescreenedAfterPreviousNegativeNegative(),
                    mappings)),
            mappings),
        getCXColumns());

    addRow(
        dataSetDefinition,
        "RNP",
        "Positive Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Positive Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorRescreenedAfterPreviousNegativePositive(),
                    mappings)),
            mappings),
        getCXColumns());

    addRow(
        dataSetDefinition,
        "RNS",
        "Suspect Cancer Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Suspect Cancer Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorRescreenedAfterPreviousNegativeSuspectCancer(),
                    mappings)),
            mappings),
        getCXColumns());

    dataSetDefinition.addColumn(
        "CXPTTOTAL",
        "Total patients with CX PT - Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total patients with CX PT - Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpNegative(),
                    mappings)),
            mappings),
        "gender=F");

    addRow(
        dataSetDefinition,
        "PTN",
        " Negative Numerator",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Negative Numerator",
                EptsReportUtils.map(
                    this.cXCASCRNCohortQueries
                        .getTotalNumeratorfindpatientwithScreeningTypeVisitAsPostTreatmentFollowUpNegative(),
                    mappings)),
            mappings),
        getCXColumns());

    return dataSetDefinition;
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  private List<ColumnParameters> getCXColumns() {

    ColumnParameters a1 = new ColumnParameters("15-19", "15-19", "gender=F|age=15-19", "01");
    ColumnParameters a2 =
        new ColumnParameters("20-24", "20-24 years female", "gender=F|age=20-24", "02");
    ColumnParameters a3 =
        new ColumnParameters("25-29", "25-29 years female", "gender=F|age=25-29", "03");
    ColumnParameters a4 = new ColumnParameters("30-34", "30-34 female", "gender=F|age=30-34", "04");
    ColumnParameters a5 = new ColumnParameters("35-39", "35-39 female", "gender=F|age=35-39", "05");
    ColumnParameters a6 = new ColumnParameters("40-44", "40-44 female", "gender=F|age=40-44", "06");
    ColumnParameters a7 = new ColumnParameters("45-49", "45-49 female", "gender=F|age=45-49", "07");
    ColumnParameters a8 = new ColumnParameters("50+", "50+ female", "gender=F|age=50+", "08");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age male", "gender=F|age=UK", "09");

    return Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, unknownF);
  }
}
