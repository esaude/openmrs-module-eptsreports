package org.openmrs.module.eptsreports.reporting.calculation.quarterly;

import java.util.Map;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class ResumoTrimestralQuarterlyTotalCalculation
    extends ResumoTrimestralDateRangesCalculation {

  @SuppressWarnings({"unchecked"})
  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap evaluated = super.evaluate(parameterValues, context);
    Map<Month, MonthlyDateRange> mapRangesByMonth =
        (Map<Month, MonthlyDateRange>) evaluated.get(ALL_EXCUTIONS_PERIOD).getValue();

    CalculationResultMap resultMap = new CalculationResultMap();
    resultMap.put(
        QUARTERLIES.QUARTER_ONE.getCode(), new SimpleResult(getQuarterOne(mapRangesByMonth), this));
    resultMap.put(
        QUARTERLIES.QUARTER_TWO.getCode(), new SimpleResult(getQuarterTwo(mapRangesByMonth), this));
    resultMap.put(
        QUARTERLIES.QUARTER_THREE.getCode(),
        new SimpleResult(getQuarterThree(mapRangesByMonth), this));
    resultMap.put(
        QUARTERLIES.QUARTER_FOUR.getCode(),
        new SimpleResult(getQuarterFour(mapRangesByMonth), this));

    return resultMap;
  }

  private MonthlyDateRange getQuarterOne(Map<Month, MonthlyDateRange> mapRangesByMonth) {

    MonthlyDateRange januaryDatesRange = mapRangesByMonth.get(Month.JANUARY);
    MonthlyDateRange februaryDatesRange = mapRangesByMonth.get(Month.FEBRUARY);
    MonthlyDateRange marchDatesRange = mapRangesByMonth.get(Month.MARCH);

    MonthlyDateRange quarter1 = new MonthlyDateRange(null, null);

    if (nonNull(marchDatesRange) && Month.MARCH.equals(marchDatesRange.getMonth())) {
      quarter1.setStartDate(marchDatesRange.getStartDate());
      quarter1.setEndDate(marchDatesRange.getEndDate());
    }

    if (nonNull(februaryDatesRange) && Month.FEBRUARY.equals(februaryDatesRange.getMonth())) {
      quarter1.setStartDate(februaryDatesRange.getStartDate());
      if (isNull(marchDatesRange)) {
        quarter1.setEndDate(februaryDatesRange.getEndDate());
      }
    }

    if (nonNull(januaryDatesRange) && Month.JANUARY.equals(januaryDatesRange.getMonth())) {
      quarter1.setStartDate(januaryDatesRange.getStartDate());
      if (isNull(februaryDatesRange) && isNull(marchDatesRange)) {
        quarter1.setEndDate(januaryDatesRange.getEndDate());
      }
    }
    return quarter1;
  }

  private MonthlyDateRange getQuarterTwo(Map<Month, MonthlyDateRange> mapRangesByMonth) {

    MonthlyDateRange aprilDatesRange = mapRangesByMonth.get(Month.APRIL);
    MonthlyDateRange mayDatesRange = mapRangesByMonth.get(Month.MAY);
    MonthlyDateRange juneDatesRange = mapRangesByMonth.get(Month.JUNE);

    MonthlyDateRange quarter2 = new MonthlyDateRange(null, null);

    if (nonNull(juneDatesRange) && Month.JUNE.equals(juneDatesRange.getMonth())) {
      quarter2.setStartDate(juneDatesRange.getStartDate());
      quarter2.setEndDate(juneDatesRange.getEndDate());
    }

    if (nonNull(mayDatesRange) && Month.MAY.equals(mayDatesRange.getMonth())) {
      quarter2.setStartDate(mayDatesRange.getStartDate());
      if (isNull(juneDatesRange)) {
        quarter2.setEndDate(mayDatesRange.getEndDate());
      }
    }

    if (nonNull(aprilDatesRange) && Month.APRIL.equals(aprilDatesRange.getMonth())) {
      quarter2.setStartDate(aprilDatesRange.getStartDate());
      if (isNull(mayDatesRange) && isNull(juneDatesRange)) {
        quarter2.setEndDate(aprilDatesRange.getEndDate());
      }
    }
    return quarter2;
  }

  private MonthlyDateRange getQuarterThree(Map<Month, MonthlyDateRange> mapRangesByMonth) {

    MonthlyDateRange julyDatesRange = mapRangesByMonth.get(Month.JULY);
    MonthlyDateRange augustDatesRange = mapRangesByMonth.get(Month.AUGUST);
    MonthlyDateRange septemberDatesRange = mapRangesByMonth.get(Month.SEPTEMBER);

    MonthlyDateRange quarter3 = new MonthlyDateRange(null, null);

    if (nonNull(septemberDatesRange) && Month.SEPTEMBER.equals(septemberDatesRange.getMonth())) {
      quarter3.setStartDate(septemberDatesRange.getStartDate());
      quarter3.setEndDate(septemberDatesRange.getEndDate());
    }

    if (nonNull(augustDatesRange) && Month.AUGUST.equals(augustDatesRange.getMonth())) {
      quarter3.setStartDate(augustDatesRange.getStartDate());
      if (isNull(septemberDatesRange)) {
        quarter3.setEndDate(augustDatesRange.getEndDate());
      }
    }

    if (nonNull(julyDatesRange) && Month.JULY.equals(julyDatesRange.getMonth())) {
      quarter3.setStartDate(julyDatesRange.getStartDate());
      if (isNull(augustDatesRange) && isNull(septemberDatesRange)) {
        quarter3.setEndDate(julyDatesRange.getEndDate());
      }
    }
    return quarter3;
  }

  private MonthlyDateRange getQuarterFour(Map<Month, MonthlyDateRange> mapRangesByMonth) {

    MonthlyDateRange octoberDatesRange = mapRangesByMonth.get(Month.OCTOBER);
    MonthlyDateRange novemberDatesRange = mapRangesByMonth.get(Month.NOVEMBER);
    MonthlyDateRange decemberDatesRange = mapRangesByMonth.get(Month.DECEMBER);

    MonthlyDateRange quarter4 = new MonthlyDateRange(null, null);

    if (nonNull(decemberDatesRange) && Month.DECEMBER.equals(decemberDatesRange.getMonth())) {
      quarter4.setStartDate(decemberDatesRange.getStartDate());
      quarter4.setEndDate(decemberDatesRange.getEndDate());
    }

    if (nonNull(novemberDatesRange) && Month.NOVEMBER.equals(novemberDatesRange.getMonth())) {
      quarter4.setStartDate(novemberDatesRange.getStartDate());
      if (isNull(decemberDatesRange)) {
        quarter4.setEndDate(novemberDatesRange.getEndDate());
      }
    }

    if (nonNull(octoberDatesRange) && Month.OCTOBER.equals(octoberDatesRange.getMonth())) {
      quarter4.setStartDate(octoberDatesRange.getStartDate());
      if (isNull(novemberDatesRange) && isNull(decemberDatesRange)) {
        quarter4.setEndDate(octoberDatesRange.getEndDate());
      }
    }
    return quarter4;
  }

  private boolean nonNull(Object obj) {
    return obj != null;
  }

  private boolean isNull(Object obj) {
    return obj == null;
  }

  public enum QUARTERLIES {
    QUARTER_ONE(1),

    QUARTER_TWO(2),

    QUARTER_THREE(3),

    QUARTER_FOUR(4);

    private final Integer code;

    private QUARTERLIES(Integer code) {
      this.code = code;
    }

    public Integer getCode() {
      return this.code;
    }
  }
}
