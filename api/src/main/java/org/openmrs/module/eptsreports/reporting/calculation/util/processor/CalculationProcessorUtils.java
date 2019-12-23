package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

public class CalculationProcessorUtils {

  @SuppressWarnings("unchecked")
  public static Map<Integer, Date> getMaxMapDateByPatient(Map<Integer, Date>... maps) {

    Map<Integer, Date> resutls = new HashMap<>();

    Set<Integer> ids = new TreeSet<>();
    for (Map<Integer, Date> map : maps) {
      ids.addAll(map.keySet());
    }
    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    for (Integer patientId : ids) {
      Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
      for (int i = 0; i < maps.length; i++) {
        Date dateMap = getDate(maps[i], patientId);

        if (dateMap != null && dateMap.compareTo(maxDate) > 0) {
          maxDate = dateMap;
        }
      }
      if (!DateUtils.isSameDay(maxDate, finalComparisonDate)) {
        resutls.put(patientId, maxDate);
      }
    }
    return resutls;
  }

  @SuppressWarnings("unchecked")
  public static Map<Integer, Date> getMinMapDateByPatient(Map<Integer, Date>... maps) {

    Map<Integer, Date> resutls = new HashMap<>();
    Set<Integer> ids = new TreeSet<>();
    for (Map<Integer, Date> map : maps) {
      ids.addAll(map.keySet());
    }
    Date finalComparisonDate = DateUtil.getDateTime(Integer.MIN_VALUE, 1, 1);
    for (Integer patientId : ids) {

      Date minDate = DateUtil.getDateTime(Integer.MIN_VALUE, 1, 1);
      for (int i = 0; i < maps.length; i++) {
        Date dateMap = getDate(maps[i], patientId);

        if (dateMap != null && dateMap.compareTo(minDate) < 0) {
          minDate = dateMap;
        }
      }
      if (!DateUtils.isSameDay(minDate, finalComparisonDate)) {
        resutls.put(patientId, minDate);
      }
    }
    return resutls;
  }

  public static Date adjustDaysInDate(Date date, int days) {
    return DateUtil.adjustDate(date, days, DurationUnit.DAYS);
  }

  public static Date getMaxDate(Integer patientId, CalculationResultMap... calculationResulsts) {
    Date finalComparisonDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);

    for (CalculationResultMap resultItem : calculationResulsts) {
      CalculationResult calculationResult = resultItem.get(patientId);
      if (calculationResult != null && calculationResult.getValue() != null) {
        if (calculationResult.getValue() instanceof Date) {
          Date date = (Date) calculationResult.getValue();
          if (date.compareTo(maxDate) > 0) {
            maxDate = date;
          }
        }
      }
    }
    if (!DateUtils.isSameDay(maxDate, finalComparisonDate)) {
      return maxDate;
    }
    return null;
  }

  private static Date getDate(Map<Integer, Date> map, Integer patientId) {
    return map != null ? map.get(patientId) : null;
  }
}
