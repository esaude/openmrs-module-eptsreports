package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
    for (Integer patientId : ids) {
      List<Date> dates = new ArrayList<Date>();
      for (Map<Integer, Date> map : maps) {
        dates.add(map.get(patientId));
      }
      dates.removeAll(Collections.singleton(null));
      if (dates.size() > 0) {
        Collections.sort(dates);
        resutls.put(patientId, dates.get(dates.size() - 1));
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
    for (Integer patientId : ids) {
      List<Date> dates = new ArrayList<Date>();
      for (Map<Integer, Date> map : maps) {
        dates.add(map.get(patientId));
      }
      dates.removeAll(Collections.singleton(null));
      if (dates.size() > 0) {
        Collections.sort(dates);
        resutls.put(patientId, dates.get(0));
      }
    }
    return resutls;
  }

  public static Date adjustDaysInDate(Date date, int days) {
    return DateUtil.adjustDate(date, days, DurationUnit.DAYS);
  }

  public static Date getMaxDate(Integer patientId, CalculationResultMap... calculationResulsts) {
    List<Date> dates = new ArrayList<Date>();
    for (CalculationResultMap resultItem : calculationResulsts) {
      CalculationResult calculationResult = resultItem.get(patientId);
      if (calculationResult != null && calculationResult.getValue() != null) {
        if (calculationResult.getValue() instanceof Date) {
          dates.add((Date) calculationResult.getValue());
        }
      }
    }
    if (dates.size() > 0) {
      Collections.sort(dates);
      return dates.get(dates.size() - 1);
    }
    return null;
  }
}
