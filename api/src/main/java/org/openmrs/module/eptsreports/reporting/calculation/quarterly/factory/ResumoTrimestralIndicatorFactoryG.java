package org.openmrs.module.eptsreports.reporting.calculation.quarterly.factory;

import java.util.HashMap;
import java.util.Map;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.ResumoTrimestralMonthPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.indicator.ResumoTrimestralIndicatorCalculationG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralIndicatorFactoryG {

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

  Map<Month, ResumoTrimestralMonthPeriodCalculation> mapArtUtils = new HashMap<>();

  @Autowired
  public ResumoTrimestralIndicatorFactoryG(
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
      DecemberResumoTrimestralCalculation decemberResumoTrimestralCalculation) {

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
  }

  public ResumoTrimestralMonthPeriodCalculation getResumoTrimestralCalculator(Month month) {
    return mapArtUtils.get(month);
  }

  @Component
  public static class JanuaryResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JANUARY);
    }
  }

  @Component
  public static class FebruaryResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.FEBRUARY);
    }
  }

  @Component
  public static class MarchResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.MARCH);
    }
  }

  @Component
  public static class AprilResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.APRIL);
    }
  }

  @Component
  public static class MayResumoTrimestralCalculation extends ResumoTrimestralIndicatorCalculationG {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.MAY);
    }
  }

  @Component
  public static class JuneResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JUNE);
    }
  }

  @Component
  public static class JulyResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.JULY);
    }
  }

  @Component
  public static class AugustResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.AUGUST);
    }
  }

  @Component
  public static class SeptemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.SEPTEMBER);
    }
  }

  @Component
  public static class OctoberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {

    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.OCTOBER);
    }
  }

  @Component
  public static class NovemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.NOVEMBER);
    }
  }

  @Component
  public static class DecemberResumoTrimestralCalculation
      extends ResumoTrimestralIndicatorCalculationG {
    @Override
    public MonthlyDateRange getMonthlExecutionPeriod(
        Map<Month, MonthlyDateRange> mapRangesByMonth) {
      return mapRangesByMonth.get(Month.DECEMBER);
    }
  }
}
