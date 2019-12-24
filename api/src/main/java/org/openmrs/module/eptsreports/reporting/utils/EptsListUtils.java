/** */
package org.openmrs.module.eptsreports.reporting.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/** @author Stélio Moiane */
public class EptsListUtils {

  public static final <T> int listSlices(final Collection<T> list, final int chunk) {
    final BigDecimal listSize = new BigDecimal(list.size());
    final BigDecimal chunkSize = new BigDecimal(chunk);
    return listSize.divide(chunkSize, RoundingMode.UP).intValue();
  }
}
