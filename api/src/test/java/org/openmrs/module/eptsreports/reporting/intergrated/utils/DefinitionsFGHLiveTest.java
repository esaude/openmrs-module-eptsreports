package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "/TestingApplicationContext.xml")
public abstract class DefinitionsFGHLiveTest extends DefinitionsTest {
  /** @see BaseContextSensitiveTest#useInMemoryDatabase() */
  @Override
  public Boolean useInMemoryDatabase() {
    /*
     * ensure ~/.OpenMRS/openmrs-runtime.properties exists with your properties
     * such as; connection.username=openmrs
     * connection.url=jdbc:mysql://127.0.0.1:3316/openmrs
     * connection.password=wTV.Tpp0|Q&c
     */
    return false;
  }

  @Override
  public void executeDataSet(String datasetFilename) {}

  protected abstract String username();

  protected abstract String password();

  @Before
  public void initialize() throws ContextAuthenticationException {
    Context.authenticate(username(), password());
    setStartDate(DateUtil.getDateTime(2013, 2, 6));
    setEndDate(DateUtil.getDateTime(2019, 3, 6));
    setLocation(new Location(103));
  }
}
