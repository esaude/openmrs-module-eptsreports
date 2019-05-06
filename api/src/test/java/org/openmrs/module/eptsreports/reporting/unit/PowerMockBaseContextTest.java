package org.openmrs.module.eptsreports.reporting.unit;

import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore({"org.apache.log4j.*", "org.apache.commons.logging.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class PowerMockBaseContextTest extends BaseContextMockTest {}
