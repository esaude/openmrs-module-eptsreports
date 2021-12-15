package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IntensiveMonitoringCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ViralLoadIntensiveMonitoringCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ViralLoadIntensiveMonitoringDataSet extends BaseDataSet {

  private ViralLoadIntensiveMonitoringCohortQueries viralLoadIntensiveMonitoringCohortQueries;

  private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  private final String MAPPINGS = "endDate=${endDate},location=${location}";

  @Autowired
  public ViralLoadIntensiveMonitoringDataSet(
      ViralLoadIntensiveMonitoringCohortQueries viralLoadIntensiveMonitoringCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries,
      EptsCommonDimension eptsCommonDimension) {
    this.viralLoadIntensiveMonitoringCohortQueries = viralLoadIntensiveMonitoringCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.intensiveMonitoringCohortQueries = intensiveMonitoringCohortQueries;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  public DataSetDefinition constructViralLoadIntensiveMonitoringDataSet() {

    CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    cohortIndicatorDataSetDefinition.setName("Viral Load Intensive Monitoring Dataset");
    cohortIndicatorDataSetDefinition.addParameters(getParameters());

    /* add dimensions */
    cohortIndicatorDataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    // indicators
    CohortIndicator indicatorDen1Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN1TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator1Den(), MAPPINGS));

    CohortIndicator indicatorNUm1Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM1TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator1Num(), MAPPINGS));

    CohortIndicator indicatorDen2Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN2TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator2Den(), MAPPINGS));

    CohortIndicator indicatorNUm2Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM2TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator2Num(), MAPPINGS));

    CohortIndicator indicatorDen3Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN3TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator3Den(), MAPPINGS));

    CohortIndicator indicatorNUm3Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM3TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator3Num(), MAPPINGS));

    CohortIndicator indicatorDen4Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN4TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator4Den(), MAPPINGS));

    CohortIndicator indicatorNUm4Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM4TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator4Num(), MAPPINGS));

    CohortIndicator indicatorDen5Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN5TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator5Den(), MAPPINGS));

    CohortIndicator indicatorNUm5Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM5TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getTotalIndicator5Num(), MAPPINGS));

    CohortIndicator indicatorDen6Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN6TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(15, "DEN15"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm6Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM6TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(15, "NUM15"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen7Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN7TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(16, "DEN16"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm7Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM7TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(16, "NUM16"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen8Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN8TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(17, "DEN17"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm8Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM8TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part2(17, "NUM17"),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen9Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN9TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part4(18, false),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm9Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM9TOTAL",
            EptsReportUtils.map(
                this.intensiveMonitoringCohortQueries.getMICat13Part4(18, true),
                "revisionEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen10Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN10TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getDenominator10(),
                "startDate=${endDate-5m-1d},endDate=${endDate-4m},location=${location}"));

    CohortIndicator indicatorNUm10Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM10TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getNumerator10(),
                "startDate=${endDate-5m-1d},endDate=${endDate-4m},location=${location}"));

    CohortIndicator indicatorDen11Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN11TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getDenominator11(),
                "startDate=${endDate-5m-1d},endDate=${endDate-4m},location=${location}"));

    CohortIndicator indicatorNUm11Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM11TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getNumerator11(),
                "startDate=${endDate-12m-1d},endDate=${endDate-11m},location=${location}"));

    CohortIndicator indicatorDen12 =
        this.eptsGeneralIndicator.getIndicator(
            "DEN12TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getNumDen12Indicators(true),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm12 =
        this.eptsGeneralIndicator.getIndicator(
            "NUM12TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getNumDen12Indicators(false),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen14 =
        this.eptsGeneralIndicator.getIndicator(
            "DEN14TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries
                    .getNumberOfPregnantWomenOnFirstLineDenominator(true),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm14 =
        this.eptsGeneralIndicator.getIndicator(
            "NUM14TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries.getNum14Indicator(),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen13Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN13TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries
                    .getNumberOfPregnantWomenOnFirstLineDenominator(true),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm13Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM13TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries
                    .getNumberOfPregnantWomenOnFirstLineDenominator(false),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorDen15Total =
        this.eptsGeneralIndicator.getIndicator(
            "DEN15TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries
                    .getNumberOfPregnantWomenOnFirstLineArtRegimen(true),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    CohortIndicator indicatorNUm15Total =
        this.eptsGeneralIndicator.getIndicator(
            "NUM15TOTAL",
            EptsReportUtils.map(
                this.viralLoadIntensiveMonitoringCohortQueries
                    .getNumberOfPregnantWomenOnFirstLineArtRegimen(false),
                "evaluationPeriodStartDate=${endDate},evaluationPeriodEndDate=${endDate},location=${location}"));

    // column mapping
    cohortIndicatorDataSetDefinition.addColumn(
        "DEN1TOTAL", "Description", EptsReportUtils.map(indicatorDen1Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM1TOTAL", "Description", EptsReportUtils.map(indicatorNUm1Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN2TOTAL", "Description", EptsReportUtils.map(indicatorDen2Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM2TOTAL", "Description", EptsReportUtils.map(indicatorNUm2Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN3TOTAL", "Description", EptsReportUtils.map(indicatorDen3Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM3TOTAL", "Description", EptsReportUtils.map(indicatorNUm3Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN4TOTAL", "Description", EptsReportUtils.map(indicatorDen4Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM4TOTAL", "Description", EptsReportUtils.map(indicatorNUm4Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN5TOTAL", "Description", EptsReportUtils.map(indicatorDen5Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM5TOTAL", "Description", EptsReportUtils.map(indicatorNUm5Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN6TOTAL", "Description", EptsReportUtils.map(indicatorDen6Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM6TOTAL", "Description", EptsReportUtils.map(indicatorNUm6Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN7TOTAL", "Description", EptsReportUtils.map(indicatorDen7Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM7TOTAL", "Description", EptsReportUtils.map(indicatorNUm7Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN8TOTAL", "Description", EptsReportUtils.map(indicatorDen8Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM8TOTAL", "Description", EptsReportUtils.map(indicatorNUm8Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN9TOTAL", "Description", EptsReportUtils.map(indicatorDen9Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM9TOTAL", "Description", EptsReportUtils.map(indicatorNUm9Total, MAPPINGS), "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN10TOTALT",
        "Pacientes com segundo resultado de Carga Viral alto após sessões de APSS/PP - Denominador Total",
        EptsReportUtils.map(indicatorDen10Total, MAPPINGS),
        "");
    addRow(
        cohortIndicatorDataSetDefinition,
        "DEN10TOTAL",
        "Pacientes com segundo resultado de Carga Viral alto após sessões de APSS/PP - Denominador",
        EptsReportUtils.map(indicatorDen10Total, MAPPINGS),
        getColumnsForAge());

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM10TOTALT",
        "Pacientes com segundo resultado de Carga Viral alto após sessões de APSS/PP - Numerador",
        EptsReportUtils.map(indicatorNUm10Total, MAPPINGS),
        "");
    addRow(
        cohortIndicatorDataSetDefinition,
        "NUM10TOTAL",
        "Pacientes com segundo resultado de Carga Viral alto após sessões de APSS/PP - Numerador",
        EptsReportUtils.map(indicatorNUm10Total, MAPPINGS),
        getColumnsForAge());

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN11TOTALT",
        "Pacientes com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Denominador Total",
        EptsReportUtils.map(indicatorDen11Total, MAPPINGS),
        "");

    addRow(
        cohortIndicatorDataSetDefinition,
        "DEN11TOTAL",
        "Pacientes com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Denominador",
        EptsReportUtils.map(indicatorDen11Total, MAPPINGS),
        getColumnsForAge());

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM11TOTALT",
        "Pacientes com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Numerador Total",
        EptsReportUtils.map(indicatorNUm11Total, MAPPINGS),
        "");

    addRow(
        cohortIndicatorDataSetDefinition,
        "NUM11TOTAL",
        "Pacientes com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Numerador",
        EptsReportUtils.map(indicatorNUm11Total, MAPPINGS),
        getColumnsForAge());

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN12TOTALT",
        "Pacientes que trocaram para segunda linha de TARV após segundo resultado alto de Carga Viral - Denominador Total",
        EptsReportUtils.map(indicatorDen12, MAPPINGS),
        "");

    addRow(
        cohortIndicatorDataSetDefinition,
        "DEN12TOTAL",
        "Number of patients in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with a request registered for a second VL test and with result of second VL above 1000",
        EptsReportUtils.map(indicatorDen12, MAPPINGS),
        getColumnsForAge());
    cohortIndicatorDataSetDefinition.addColumn(
        "NUM12TOTALT",
        "Pacientes que trocaram para segunda linha de TARV após segundo resultado alto de Carga Viral - Numerador Total",
        EptsReportUtils.map(indicatorNUm12, MAPPINGS),
        "");

    addRow(
        cohortIndicatorDataSetDefinition,
        "NUM12TOTAL",
        "Number of patients in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with result of second VL above 1000 and who changed to 2nd Line ART",
        EptsReportUtils.map(indicatorNUm12, MAPPINGS),
        getColumnsForAge());

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN14TOTAL",
        "Mulheres Grávidas com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Denominador Total",
        EptsReportUtils.map(indicatorDen14, MAPPINGS),
        "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM14TOTAL",
        "Mulheres Grávidas com segundo resultado de Carga Viral baixo após sessões de APSS/PP - Numerador Total",
        EptsReportUtils.map(indicatorNUm14, MAPPINGS),
        "");

    cohortIndicatorDataSetDefinition.addColumn(
        "DEN13TOTAL",
        "Mulheres Grávidas com segundo resultado de Carga Viral alto após sessões de APSS/PP - Denominador Total",
        EptsReportUtils.map(indicatorDen13Total, MAPPINGS),
        "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM13TOTAL",
        "Mulheres Grávidas com segundo resultado de Carga Viral alto após sessões de APSS/PP - Numerador Total",
        EptsReportUtils.map(indicatorNUm13Total, MAPPINGS),
        "");
    cohortIndicatorDataSetDefinition.addColumn(
        "DEN15TOTAL",
        "Mulheres Grávidas que trocaram para segunda linha de TARV após segundo resultado alto de Carga Viral - Denominador Total",
        EptsReportUtils.map(indicatorDen15Total, MAPPINGS),
        "");

    cohortIndicatorDataSetDefinition.addColumn(
        "NUM15TOTAL",
        "Mulheres Grávidas que trocaram para segunda linha de TARV após segundo resultado alto de Carga Viral - Numerador Total",
        EptsReportUtils.map(indicatorNUm15Total, MAPPINGS),
        "");

    return cohortIndicatorDataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "endDate", Date.class),
        new Parameter("location", "Location", Location.class));
  }

  private List<ColumnParameters> getColumnsForAge() {

    ColumnParameters zeroTo4 = new ColumnParameters("zeroTo4", "0 - 4", "age=0-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5 - 9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10 - 14", "age=10-14", "03");
    ColumnParameters greaterThan15 =
        new ColumnParameters("greaterThan15", "15 and above", "age=15+", "04");

    return Arrays.asList(zeroTo4, fiveTo9, tenTo14, greaterThan15);
  }
}
