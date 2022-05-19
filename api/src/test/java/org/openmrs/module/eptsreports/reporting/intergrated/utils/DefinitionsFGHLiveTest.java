package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.api.context.Credentials;
import org.openmrs.api.context.UsernamePasswordCredentials;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "/TestingApplicationContext.xml")
public abstract class DefinitionsFGHLiveTest extends DefinitionsTest {
  /** @see BaseContextSensitiveTest#useInMemoryDatabase() */
  @Override
  public Boolean useInMemoryDatabase() {
    return false;
  }

  @Override
  public void executeDataSet(String datasetFilename) {}

  @Override
  public void deleteAllData() {};

  @Override
  protected Credentials getCredentials() {
    return new UsernamePasswordCredentials(username(), password());
  }

  protected abstract String username();

  protected abstract String password();

  @Before
  public void initialize() throws ContextAuthenticationException {
    authenticate();
    setStartDate(DateUtil.getDateTime(2013, 2, 6));
    setEndDate(DateUtil.getDateTime(2019, 3, 6));
    setLocation(new Location(103));
  }
}
