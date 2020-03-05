package org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory;

import java.util.HashMap;
import java.util.Map;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralQuarterlyTotalCalculation.QUARTERLIES;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.indicator.ResumoTrimestralIndicatorCalculationB;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.indicator.ResumoTrimestralQuarterlyIndicatorCalculationB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralIndicatorFactoryB {

  private JanuaryResumoTrimestralCalculation januaryResumoTrimestralCalculation;

  private FebruaryResumoTrimestralCalculation februaryResumoTrimestralCalculation;

  private MarchResumoTrimestralCalculation marchResumoTrimestralCalculation;

  private AprilResumoTrimestralCalculation aprilResumoTrimestralCalculation;

  private MayResumoTrimestralCalculation mayResumoTrimestralCalculation;

  private JuneResumoTrimestralCalculation juneResumoTrimestralCalculation;

  private JulyResumoTrimestralCalculation julyResumoTrimestralCalculation;

  private AugustResumoTrimestralCalculation augustResumoTrimestralCalculation;

  private SeptemberResumoTrimestralCalculation septemberResumoTrimestralCalculation;

  private OctoberResumoTrimestralCalculation octoberResumoTrimestralCalculation;

  private NovemberResumoTrimestralCalculation novemberResumoTrimestralCalculation;

  private DecemberResumoTrimestralCalculation decemberResumoTrimestralCalculation;

  private ResumoTrimestralArtQuarterOneCalculation resumoTrimestralArtQuarterOneCalculation;

  private ResumoTrimestralArtQuarterTwoCalculation resumoTrimestralArtQuarterTwoCalculation;
  private ResumoTrimestralArtQuarterThreeCalculation resumoTrimestralArtQuarterThreeCalculation;

  private ResumoTrimestralArtQuarterFourCalculation resumoTrimestralArtQuarterFourCalculation;

  Map<Month, ResumoTrimestralMonthPeriodCalculation> mapArtUtils = new HashMap<>();

  Map<QUARTERLIES, ResumoTrimestralQuarterlyIndicatorCalculationB> mapArtQuartersUtil =
      new HashMap<>();

  @Autowired
  public ResumoTrimestralIndicatorFactoryB(
      JanuaryResumoTrimestralCalculation januaryResumoTrimestralCalculation,
      FebruaryResumoTrimestralCalculation februaryResumoTrimestralCalculation,
      MarchResumoTrimestralCalculation marchResumoTrimestralCalculation,
      AprilResumoTrimestralCalculation aprilResumoTrimestralCalculation,
      MayResumoTrimestralCalculation mayResumoTrimestralCalculation,
      JuneResumoTrimestralCalculation juneResumoTrimestralCalculation,
      JulyResumoTrimestralCalculation julyResumoTrimestralCalculation,
      AugustResumoTrimestralCalculation augustResumoTrimestralCalculation,
      SeptemberResumoTrimestralCalculation septemberResumoTrimestralCalculation,
      OctoberResumoTrimestralCalculation octoberResumoTrimestralCalculation,
      NovemberResumoTrimestralCalculation novemberResumoTrimestralCalculation,
      DecemberResumoTrimestralCalculation decemberResumoTrimestralCalculation,
      ResumoTrimestralArtQuarterOneCalculation resumoTrimestralArtQuarterOneCalculation,
      ResumoTrimestralArtQuarterTwoCalculation resumoTrimestralArtQuarterTwoCalculation,
      ResumoTrimestralArtQuarterThreeCalculation resumoTrimestralArtQuarterThreeCalculation,
      ResumoTrimestralArtQuarterFourCalculation resumoTrimestralArtQuarterFourCalculation) {

    this.januaryResumoTrimestralCalculation = januaryResumoTrimestralCalculation;
    this.februaryResumoTrimestralCalculation = februaryResumoTrimestralCalculation;
    this.marchResumoTrimestralCalculation = marchResumoTrimestralCalculation;
    this.aprilResumoTrimestralCalculation = aprilResumoTrimestralCalculation;
    this.mayResumoTrimestralCalculation = mayResumoTrimestralCalculation;
    this.juneResumoTrimestralCalculation = juneResumoTrimestralCalculation;
    this.julyResumoTrimestralCalculation = julyResumoTrimestralCalculation;
    this.augustResumoTrimestralCalculation = augustResumoTrimestralCalculation;
    this.septemberResumoTrimestralCalculation = septemberResumoTrimestralCalculation;
    this.octoberResumoTrimestralCalculation = octoberResumoTrimestralCalculation;
    this.novemberResumoTrimestralCalculation = novemberResumoTrimestralCalculation;
    this.decemberResumoTrimestralCalculation = decemberResumoTrimestralCalculation;

    this.resumoTrimestralArtQuarterOneCalculation = resumoTrimestralArtQuarterOneCalculation;
    this.resumoTrimestralArtQuarterTwoCalculation = resumoTrimestralArtQuarterTwoCalculation;
    this.resumoTrimestralArtQuarterThreeCalculation = resumoTrimestralArtQuarterThreeCalculation;
    this.resumoTrimestralArtQuarterFourCalculation = resumoTrimestralArtQuarterFourCalculation;

    mapArtUtils.put(Month.JANUARY, this.januaryResumoTrimestralCalculation);
    mapArtUtils.put(Month.FEBRUARY, this.februaryResumoTrimestralCalculation);
    mapArtUtils.put(Month.MARCH, this.marchResumoTrimestralCalculation);
    mapArtUtils.put(Month.APRIL, this.aprilResumoTrimestralCalculation);
    mapArtUtils.put(Month.MAY, this.mayResumoTrimestralCalculation);
    mapArtUtils.put(Month.JUNE, this.juneResumoTrimestralCalculation);
    mapArtUtils.put(Month.JULY, this.julyResumoTrimestralCalculation);
    mapArtUtils.put(Month.AUGUST, this.augustResumoTrimestralCalculation);
    mapArtUtils.put(Month.SEPTEMBER, this.septemberResumoTrimestralCalculation);
    mapArtUtils.put(Month.OCTOBER, this.octoberResumoTrimestralCalculation);
    mapArtUtils.put(Month.NOVEMBER, this.novemberResumoTrimestralCalculation);
    mapArtUtils.put(Month.DECEMBER, this.decemberResumoTrimestralCalculation);

    mapArtQuartersUtil.put(QUARTERLIES.QUARTER_ONE, this.resumoTrimestralArtQuarterOneCalculation);
    mapArtQuartersUtil.put(QUARTERLIES.QUARTER_TWO, this.resumoTrimestralArtQuarterTwoCalculation);
    mapArtQuartersUtil.put(
        QUARTERLIES.QUARTER_THREE, this.resumoTrimestralArtQuarterThreeCalculation);
    mapArtQuartersUtil.put(
        QUARTERLIES.QUARTER_FOUR, this.resumoTrimestralArtQuarterFourCalculation);
  }

  public ResumoTrimestralMonthPeriodCalculation getResumoTrimestralCalculator(Month month) {
    return mapArtUtils.get(month);
  }

  public ResumoTrimestralQuarterlyIndicatorCalculationB getResumoTrimestralQuartelyCalculation(
      QUARTERLIES quarter) {
    return mapArtQuartersUtil.get(quarter);
  }

  @Component
  public static class JanuaryResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JANUARY);
    }
  }

  @Component
  public static class FebruaryResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.FEBRUARY);
    }
  }

  @Component
  public static class MarchResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.MARCH);
    }
  }

  @Component
  public static class AprilResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.APRIL);
    }
  }

  @Component
  public static class MayResumoTrimestralCalculation extends ResumoTrimestralIndicatorCalculationB {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.MAY);
    }
  }

  @Component
  public static class JuneResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JUNE);
    }
  }

  @Component
  public static class JulyResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JULY);
    }
  }

  @Component
  public static class AugustResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.AUGUST);
    }
  }

  @Component
  public static class SeptemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.SEPTEMBER);
    }
  }

  @Component
  public static class OctoberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.OCTOBER);
    }
  }

  @Component
  public static class NovemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.NOVEMBER);
    }
  }

  @Component
  public static class DecemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationB {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.DECEMBER);
    }
  }

  @Component
  public static class ResumoTrimestralArtQuarterOneCalculation
      extends ResumoTrimestralQuarterlyIndicatorCalculationB {

    @Override
    public Map<QUARTERLIES, MonthlyDateRange> getQuarterToExecute(CalculationResultMap evaluated) {
      Map<QUARTERLIES, MonthlyDateRange> monthlyDateRange = new HashMap<>();

      CalculationResult calculationResult = evaluated.get(QUARTERLIES.QUARTER_ONE.getCode());
      monthlyDateRange.put(
          QUARTERLIES.QUARTER_ONE,
          (MonthlyDateRange) (MonthlyDateRange) calculationResult.getValue());
      return monthlyDateRange;
    }
  }

  @Component
  public static class ResumoTrimestralArtQuarterTwoCalculation
      extends ResumoTrimestralQuarterlyIndicatorCalculationB {

    @Override
    public Map<QUARTERLIES, MonthlyDateRange> getQuarterToExecute(CalculationResultMap evaluated) {
      Map<QUARTERLIES, MonthlyDateRange> monthlyDateRange = new HashMap<>();
      CalculationResult calculationResult = evaluated.get(QUARTERLIES.QUARTER_TWO.getCode());

      monthlyDateRange.put(
          QUARTERLIES.QUARTER_TWO, (MonthlyDateRange) calculationResult.getValue());
      return monthlyDateRange;
    }
  }

  @Component
  public static class ResumoTrimestralArtQuarterThreeCalculation
      extends ResumoTrimestralQuarterlyIndicatorCalculationB {

    @Override
    public Map<QUARTERLIES, MonthlyDateRange> getQuarterToExecute(CalculationResultMap evaluated) {
      Map<QUARTERLIES, MonthlyDateRange> monthlyDateRange = new HashMap<>();

      CalculationResult calculationResult = evaluated.get(QUARTERLIES.QUARTER_THREE.getCode());
      monthlyDateRange.put(
          QUARTERLIES.QUARTER_THREE, (MonthlyDateRange) calculationResult.getValue());
      return monthlyDateRange;
    }
  }

  @Component
  public static class ResumoTrimestralArtQuarterFourCalculation
      extends ResumoTrimestralQuarterlyIndicatorCalculationB {

    @Override
    public Map<QUARTERLIES, MonthlyDateRange> getQuarterToExecute(CalculationResultMap evaluated) {
      Map<QUARTERLIES, MonthlyDateRange> monthlyDateRange = new HashMap<>();

      CalculationResult calculationResult = evaluated.get(QUARTERLIES.QUARTER_FOUR.getCode());
      monthlyDateRange.put(
          QUARTERLIES.QUARTER_FOUR, (MonthlyDateRange) calculationResult.getValue());
      return monthlyDateRange;
    }
  }
}
