/** */
package org.openmrs.module.eptsreports.reporting.unit.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.utils.EptsListUtils;

/** @author Stélio Moiane */
public class EptsListUtilTest {

  @Test
  public void shouldfindListSlices() {

    final List<Integer> asList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12);

    final int listSlices = EptsListUtils.listSlices(asList, 2);

    assertEquals(6, listSlices);
  }
}
