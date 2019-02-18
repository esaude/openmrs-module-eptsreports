package org.openmrs.module.eptsreports.reporting.unit.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.test.BaseContextMockTest;


public class EptsCalculationUtilsTest extends BaseContextMockTest {

	@Test
	public void ensureEmptyListResultsShouldReplaceNullsWithEmptyList() {
		CalculationResultMap map = new CalculationResultMap();
		ListResult list = new ListResult();
		map.put(1, list);
		map.put(2, null);
		map.put(3, list);
		Assert.assertNull(map.get(2));
		CalculationResultMap replacedMap = EptsCalculationUtils.ensureEmptyListResults(map, Arrays.asList(1,2,3));
		Assert.assertEquals(map, replacedMap);
		Assert.assertNotNull(replacedMap.get(2));
	}
	
}
