package org.openmrs.module.eptsreports.reporting.calculation.quarterly;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.openmrs.module.reporting.common.DateUtil;

public class MonthlyDateRange {

  private Date startDate;
  private Date endDate;

  public MonthlyDateRange(Date startDate, Date endDate) {
    super();
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Month getMonth() {
    return Month.of(this.startDate);
  }

  @Override
  public String toString() {
    ToStringBuilder sb = new ToStringBuilder(100);
    sb.append("Month: ", getMonth());
    sb.append("Start Date: ", this.startDate);
    sb.append("End Date: ", this.endDate);
    return sb.toString();
  }

  public enum Month {
    JANUARY,

    FEBRUARY,

    MARCH,

    APRIL,

    MAY,

    JUNE,

    JULY,

    AUGUST,

    SEPTEMBER,

    OCTOBER,

    NOVEMBER,

    DECEMBER;

    /** Private cache of all the constants. */
    private static final Month[] ENUMS = Month.values();

    public static Month of(Date date) {
      Calendar instance = Calendar.getInstance();
      instance.setTime(date);
      int month = instance.get(Calendar.MONTH);
      return ENUMS[month];
    }

    public int getValue() {
      return ordinal() + 1;
    }

    public int length(boolean leapYear) {
      switch (this) {
        case FEBRUARY:
          return (leapYear ? 29 : 28);
        case APRIL:
        case JUNE:
        case SEPTEMBER:
        case NOVEMBER:
          return 30;
        default:
          return 31;
      }
    }

    public int minLength() {
      switch (this) {
        case FEBRUARY:
          return 28;
        case APRIL:
        case JUNE:
        case SEPTEMBER:
        case NOVEMBER:
          return 30;
        default:
          return 31;
      }
    }

    public int maxLength() {
      switch (this) {
        case FEBRUARY:
          return 29;
        case APRIL:
        case JUNE:
        case SEPTEMBER:
        case NOVEMBER:
          return 30;
        default:
          return 31;
      }
    }

    public Month firstMonthOfQuarter() {
      return ENUMS[(ordinal() / 3) * 3];
    }
  }

  public static void main(String[] args) {

    Date startDate = DateUtil.getDateTime(2020, 1, 16);
    Date endDate = DateUtil.getDateTime(2020, 6, 25);
    MonthlyDateRange range = new MonthlyDateRange(startDate, endDate);
    System.out.println(range.getMonth());
  }
}
