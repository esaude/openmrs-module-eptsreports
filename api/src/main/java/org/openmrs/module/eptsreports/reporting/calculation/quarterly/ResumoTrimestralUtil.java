package org.openmrs.module.eptsreports.reporting.calculation.quarterly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.module.eptsreports.reporting.calculation.quarterly.MonthlyDateRange.Month;
import org.openmrs.module.reporting.common.DateUtil;

public class ResumoTrimestralUtil {

  public static Map<Month, MonthlyDateRange> getDisaggregatedDates(Integer year, String quarter) {

    MonthlyDateRange dateInterval = getStartAndEndDates(year, quarter);
    List<Date> allStartDates =
        getAllStartDates(dateInterval.getStartDate(), dateInterval.getEndDate());
    List<Date> allEndDates = getAllEndDates(allStartDates, dateInterval.getEndDate());

    Map<Month, MonthlyDateRange> mapRangesByMonth = new HashMap<>();
    for (int i = 0; i < allStartDates.size(); i++) {
      MonthlyDateRange monthlyDateRange =
          new MonthlyDateRange(allStartDates.get(i), allEndDates.get(i));
      mapRangesByMonth.put(monthlyDateRange.getMonth(), monthlyDateRange);
    }
    return mapRangesByMonth;
  }

  private static List<Date> getAllEndDates(List<Date> allStartDates, Date endDate) {
    List<Date> result = new ArrayList<Date>();
    for (Date date : allStartDates) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
      result.add(DateUtil.getStartOfDay(calendar.getTime()));
    }
    result.remove(allStartDates.size() - 1);
    result.add(DateUtil.getStartOfDay(endDate));
    return result;
  }

  private static List<Date> getAllStartDates(Date startDate, Date endDate) {
    int months = monthsBetween(startDate, endDate);
    List<Date> results = new ArrayList<Date>();
    results.add(DateUtil.getStartOfDay(startDate));
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int countMonths = 1;
    while (startDate.before(endDate) && months > 1) {
      Calendar iter = Calendar.getInstance();
      iter.set(year, month + countMonths, 1);
      Date dateIter = iter.getTime();
      if (!dateIter.after(endDate)) {
        results.add(DateUtil.getStartOfDay(dateIter));
      }
      months--;
      countMonths++;
    }
    Calendar endDateCalendar = Calendar.getInstance();
    endDateCalendar.setTime(endDate);
    endDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
    results.add(DateUtil.getStartOfDay(endDateCalendar.getTime()));
    return results;
  }

  private static int monthsBetween(Date d1, Date d2) {
    if (d2 == null || d1 == null) {
      return -1;
    }
    Calendar m_calendar = Calendar.getInstance();
    m_calendar.setTime(d1);
    int nMonth1 = 12 * m_calendar.get(Calendar.YEAR) + m_calendar.get(Calendar.MONTH);
    m_calendar.setTime(d2);
    int nMonth2 = 12 * m_calendar.get(Calendar.YEAR) + m_calendar.get(Calendar.MONTH);
    return java.lang.Math.abs(nMonth2 - nMonth1);
  }

  private static MonthlyDateRange getStartAndEndDates(Integer year, String quarterDescription) {
    return getMonthlyDateRangeByYearAndQuarter(year, quarterDescription);
  }

  private static MonthlyDateRange getMonthlyDateRangeByYearAndQuarter(
      Integer year, String quarterDescription) {

    QUARTERLIES quarter = null;
    for (QUARTERLIES iter : QUARTERLIES.values()) {
      if (iter.getDescription().equals(quarterDescription)) {
        quarter = iter;
      }
    }

    MonthlyDateRange dateRange = new MonthlyDateRange(null, null);

    if (QUARTERLIES.QUARTER_ONE.equals(quarter)) {
      dateRange.setStartDate(DateUtil.getDateTime(year, 1, 1));
      dateRange.setEndDate(DateUtil.getDateTime(year, 3, 31));
    }

    if (QUARTERLIES.QUARTER_TWO.equals(quarter)) {
      dateRange.setStartDate(DateUtil.getDateTime(year, 4, 1));
      dateRange.setEndDate(DateUtil.getDateTime(year, 6, 30));
    }
    if (QUARTERLIES.QUARTER_THREE.equals(quarter)) {
      dateRange.setStartDate(DateUtil.getDateTime(year, 7, 1));
      dateRange.setEndDate(DateUtil.getDateTime(year, 9, 30));
    }

    if (QUARTERLIES.QUARTER_FOUR.equals(quarter)) {
      dateRange.setStartDate(DateUtil.getDateTime(year, 10, 1));
      dateRange.setEndDate(DateUtil.getDateTime(year, 12, 31));
    }
    return dateRange;
  }

  public enum QUARTERLIES {
    QUARTER_ONE(1, "Trimestre 01"),

    QUARTER_TWO(2, "Trimestre 02"),

    QUARTER_THREE(3, "Trimestre 03"),

    QUARTER_FOUR(4, "Trimestre 04");

    private final Integer code;
    private final String description;

    private QUARTERLIES(Integer code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getDescription() {
      return this.description;
    }

    public Integer getCode() {
      return code;
    }
  }
}
