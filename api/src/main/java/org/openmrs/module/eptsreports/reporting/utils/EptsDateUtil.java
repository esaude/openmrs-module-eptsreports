package org.openmrs.module.eptsreports.reporting.utils;

import java.util.Date;
import org.openmrs.module.reporting.common.DateUtil;

public class EptsDateUtil {

  public static int getDaysBetween(Date fromDate, Date toDate) {
    return DateUtil.getDaysBetween(
        DateUtil.getStartOfDay(fromDate), DateUtil.getStartOfDay(toDate));
  }
}
