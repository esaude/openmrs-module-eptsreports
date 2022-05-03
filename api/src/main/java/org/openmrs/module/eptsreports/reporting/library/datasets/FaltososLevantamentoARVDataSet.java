package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.FaltososLevantamentoARVCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.*;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

    ColumnParameters lessThan10 =
        new ColumnParameters(
            "under10",
            "under 10 year ",
            EptsCommonDimensionKey.of(DimensionKeyForAge.bellow10Years).getDimensions(),
            "lessThan10");

    ColumnParameters lessTha1Female =
        new ColumnParameters(
            "under10Female",
            "under 10 years Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.bellow10Years)
                .getDimensions(),
            "lessThan10Female");

    ColumnParameters lessThan10Male =
        new ColumnParameters(
            "under10Male",
            "under 10 years Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.bellow10Years)
                .getDimensions(),
            "lessThan10Male");

    ColumnParameters between10And14 =
        new ColumnParameters(
            "between10and14",
            "between 10 and 14 years ",
            EptsCommonDimensionKey.of(DimensionKeyForAge.between10And14Years).getDimensions(),
            "between10And14");

    ColumnParameters between10And14Female =
        new ColumnParameters(
            "between10And14Female",
            " between 10 And 14 Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "between10And14Female");

    ColumnParameters between10And14Male =
        new ColumnParameters(
            "between10And14Male",
            "between 10 And 14 Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "between10And14Male");

    ColumnParameters lessThan15 =
        new ColumnParameters(
            "under15",
            "under 15 year ",
            EptsCommonDimensionKey.of(DimensionKeyForAge.bellow15Years).getDimensions(),
            "lessThan15");
    ColumnParameters lessTha15Female =
        new ColumnParameters(
            "under15Female",
            "under 15 years Female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.bellow15Years)
                .getDimensions(),
            "lessThan15Female");
    ColumnParameters lessThan15Male =
        new ColumnParameters(
            "under15Male",
            "under 15 years Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.bellow15Years)
                .getDimensions(),
            "lessThan15Male");

    return Arrays.asList(
        lessThan10,
        lessTha1Female,
        lessThan10Male,
        between10And14,
        between10And14Female,
        between10And14Male,
        lessThan15,
        lessTha15Female,
        lessThan15Male);
  }

  private List<ColumnParameters> getColumnsForAdult() {

    ColumnParameters between15And24 =
        new ColumnParameters(
            "between15And24",
            "between 15 And 24",
            EptsCommonDimensionKey.of(DimensionKeyForAge.between15And24Years).getDimensions(),
            "between15And24");

    ColumnParameters between15And24Female =
        new ColumnParameters(
            "between15And24Female",
            "between 15 And 24",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between15And24Years)
                .getDimensions(),
            "between15And24Female");

    ColumnParameters between15And24Male =
        new ColumnParameters(
            "between15And24Male",
            "between 15 And 24 Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between15And24Years)
                .getDimensions(),
            "between15And24Male");

    ColumnParameters between25And49 =
        new ColumnParameters(
            "between25And49",
            "between 25 And 49",
            EptsCommonDimensionKey.of(DimensionKeyForAge.between25And49Years).getDimensions(),
            "between25And49");

    ColumnParameters between25And49Female =
        new ColumnParameters(
            "between25And49Female",
            "between 25 And 49",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between25And49Years)
                .getDimensions(),
            "between25And49Female");

    ColumnParameters between25And49Male =
        new ColumnParameters(
            "between25And49Male",
            "between 25 And 49 Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between25And49Years)
                .getDimensions(),
            "between25And49Male");

    ColumnParameters greaterThan15 =
        new ColumnParameters(
            "EqualOrAbove15",
            "Equal above 15",
            EptsCommonDimensionKey.of(DimensionKeyForAge.overOrEqualTo15Years).getDimensions(),
            "greaterThan15");
    ColumnParameters greaterTha15Female =
        new ColumnParameters(
            "above15Female",
            "above 15 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.overOrEqualTo15Years)
                .getDimensions(),
            "greaterThan15Female");
    ColumnParameters greaterThan15Male =
        new ColumnParameters(
            "above15Male",
            "above15Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.overOrEqualTo15Years)
                .getDimensions(),
            "greaterThan15Male");

    ColumnParameters greaterThan50 =
        new ColumnParameters(
            "EqualOrAbove50",
            "Equal above 50",
            EptsCommonDimensionKey.of(DimensionKeyForAge.overOrEqualTo50Years).getDimensions(),
            "greaterThan50");
    ColumnParameters greaterTha50Female =
        new ColumnParameters(
            "above50Female",
            "above 50 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.overOrEqualTo50Years)
                .getDimensions(),
            "greaterThan50Female");
    ColumnParameters greaterThan50Male =
        new ColumnParameters(
            "above50Male",
            "above50Male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.overOrEqualTo50Years)
                .getDimensions(),
            "greaterThan50Male");

    return Arrays.asList(
        between15And24,
        between15And24Female,
        between15And24Male,
        between25And49,
        between25And49Female,
        between25And49Male,
        greaterThan15,
        greaterTha15Female,
        greaterThan15Male,
        greaterThan50,
        greaterTha50Female,
        greaterThan50Male);
  }
}
