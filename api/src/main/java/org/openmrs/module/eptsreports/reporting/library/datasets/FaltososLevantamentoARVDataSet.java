package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.FaltososLevantamentoARVCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.DimensionKeyForAge;
import org.openmrs.module.eptsreports.reporting.library.dimensions.DimensionKeyForGender;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FaltososLevantamentoARVDataSet extends BaseDataSet {

  private FaltososLevantamentoARVCohortQueries faltososLevantamentoARVCohortQueries;
  private EptsGeneralIndicator eptsGeneralIndicator;
  private EptsCommonDimension eptsCommonDimension;
  private AgeDimensionCohortInterface ageDimensionCohortInterface;

  @Autowired
  public FaltososLevantamentoARVDataSet(
      FaltososLevantamentoARVCohortQueries faltososLevantamentoARVCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension,
      @Qualifier("commonAgeDimensionCohort")
          AgeDimensionCohortInterface ageDimensionCohortInterface) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.faltososLevantamentoARVCohortQueries = faltososLevantamentoARVCohortQueries;
    this.eptsCommonDimension = eptsCommonDimension;
    this.ageDimensionCohortInterface = ageDimensionCohortInterface;
  }

  public DataSetDefinition constructDataSet() {

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("FALTOSOS AO LEVANTAMENTO DE ARV");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohortInterface), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    CohortIndicator ciFaltosoDenominator =
        eptsGeneralIndicator.getIndicator(
            "CI denominator",
            EptsReportUtils.map(faltososLevantamentoARVCohortQueries.getDenominator(), mappings));

    dataSetDefinition.addColumn(
        "denominator", "DENOMINATOR", EptsReportUtils.map(ciFaltosoDenominator, mappings), "");

    addRow(
        dataSetDefinition,
        "denChildren",
        "Children on Denominator",
        EptsReportUtils.map(ciFaltosoDenominator, mappings),
        getColumnsForChildren());

    addRow(
        dataSetDefinition,
        "denAdult",
        "Adult on Denominator",
        EptsReportUtils.map(ciFaltosoDenominator, mappings),
        getColumnsForAdult());

    dataSetDefinition.addColumn(
        "denPregnant",
        "PREGNANT PATIENTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Pregnant",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getPregnantsOnDenominator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "denBreastfeeding",
        "BREASTFEEDING PATIENTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Breastfeeding",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getBreatfeedingOnDenominator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "denViralLoad",
        "VIRAL LOAD",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Viral Load",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getViralLoadOnDenominator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "denAPSSConsultation",
        "APSS CONSULTATION",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI APSS",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getAPSSConsultationOnDenominator(),
                    mappings)),
            mappings),
        "");

    CohortIndicator ciFaltososNumerador =
        eptsGeneralIndicator.getIndicator(
            "numerator",
            EptsReportUtils.map(faltososLevantamentoARVCohortQueries.getNumerator(), mappings));
    dataSetDefinition.addColumn(
        "numerator", "NUMERATOR COLUMN", EptsReportUtils.map(ciFaltososNumerador, mappings), "");

    addRow(
        dataSetDefinition,
        "numChildren",
        "Children on Numerator",
        EptsReportUtils.map(ciFaltososNumerador, mappings),
        getColumnsForChildren());

    addRow(
        dataSetDefinition,
        "numAdults",
        "Adult on Numerator",
        EptsReportUtils.map(ciFaltososNumerador, mappings),
        getColumnsForAdult());

    dataSetDefinition.addColumn(
        "numPregnant",
        "PREGNANT PATIENTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Pregnant and Numerator",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getPregnantsOnNumerator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "numBreastfeeding",
        "BREASTFEEDING PATIENTS",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Breastfeeding and numerator",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getBreatfeedingOnNumerator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "numViralLoad",
        "VIRAL LOAD",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI Viral Load and Numerator",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getViralLoadOnNumerator(), mappings)),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "numAPSSConsultation",
        "APSS CONSULTATION",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "CI APSS and Numerator",
                EptsReportUtils.map(
                    faltososLevantamentoARVCohortQueries.getAPSSConsultationOnNumerator(),
                    mappings)),
            mappings),
        "");

    return dataSetDefinition;
  }

  private List<ColumnParameters> getColumnsForChildren() {
    ColumnParameters lessThan15 =
        new ColumnParameters(
            "under15",
            "under 15 year ",
            DimensionKeyForAge.bellow15Years.getDimension(),
            "lessThan15");
    ColumnParameters lessTha15Female =
        new ColumnParameters(
            "under15Female",
            "under 15 years Female",
            DimensionKeyForGender.female.and(DimensionKeyForAge.bellow15Years).getDimension(),
            "lessThan15Female");
    ColumnParameters lessThan15Male =
        new ColumnParameters(
            "under15Male",
            "under 15 years Male",
            DimensionKeyForGender.male.and(DimensionKeyForAge.bellow15Years).getDimension(),
            "lessThan15Male");

    return Arrays.asList(lessThan15, lessTha15Female, lessThan15Male);
  }

  private List<ColumnParameters> getColumnsForAdult() {
    ColumnParameters greaterThan15 =
        new ColumnParameters(
            "EqualOrAbove15",
            "Equal above 15",
            DimensionKeyForAge.overOrEqualTo15Years.getDimension(),
            "greaterThan15");
    ColumnParameters greaterTha15Female =
        new ColumnParameters(
            "above15Female",
            "above 15 female",
            DimensionKeyForGender.male.and(DimensionKeyForAge.overOrEqualTo15Years).getDimension(),
            "greaterThan15Female");
    ColumnParameters greaterThan15Male =
        new ColumnParameters(
            "above15Male",
            "above15Male",
            DimensionKeyForGender.male.and(DimensionKeyForAge.overOrEqualTo15Years).getDimension(),
            "greaterThan15Male");

    return Arrays.asList(greaterThan15, greaterTha15Female, greaterThan15Male);
  }
}
