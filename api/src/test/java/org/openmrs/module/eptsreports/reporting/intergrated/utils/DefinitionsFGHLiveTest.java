package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class DefinitionsFGHLiveTest extends BaseModuleContextSensitiveTest {
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

  @Before
  public void initialize() throws Exception {
    Context.authenticate("admin", "eSaude123");
  }

  protected void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  protected void addParameters(DataSetDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {
    context.addParameterValue("startDate", startDate);
    context.addParameterValue("endDate", endDate);
    context.addParameterValue("location", location);
  }

  /** evaluate for location#103 from 06/feb/2013 to 06/mar/2019 */
  protected DataSet evaluateDatasetDefinition(DataSetDefinition cd) throws EvaluationException {
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("startDate", DateUtil.getDateTime(2013, 02, 06));
    context.addParameterValue("endDate", DateUtil.getDateTime(2019, 03, 06));
    context.addParameterValue("location", Context.getLocationService().getLocation(103));
    addParameters(cd);
    return Context.getService(DataSetDefinitionService.class).evaluate(cd, context);
  }

  /** evaluate for location#103 from 06/feb/2013 to 06/mar/2019 */
  protected EvaluatedCohort evaluateCodedObsCohortDefinition(CohortDefinition cd)
      throws EvaluationException {
    addParameters(cd);
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("onOrAfter", DateUtil.getDateTime(2013, 02, 06));
    context.addParameterValue("onOrBefore", DateUtil.getDateTime(2019, 03, 06));
    context.addParameterValue("locationList", Context.getLocationService().getLocation(103));
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  /** evaluate for location#103 from 06/feb/2019 to 06/mar/2019 */
  protected EvaluatedCohort evaluateCalculationCohortDefinition(CohortDefinition cd)
      throws EvaluationException {
    addParameters(cd);
    EvaluationContext context = new EvaluationContext();
    context.addParameterValue("onOrAfter", DateUtil.getDateTime(2019, 02, 06));
    context.addParameterValue("onOrBefore", DateUtil.getDateTime(2019, 03, 06));
    context.addParameterValue("location", Context.getLocationService().getLocation(103));
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  /** evaluate for location#103 from 06/feb/2013 to 06/mar/2019 */
  protected EvaluatedCohort evaluateCohortDefinition(CohortDefinition cd)
      throws EvaluationException {
    addParameters(cd);
    EvaluationContext context = new EvaluationContext();

    setParameters(
        DateUtil.getDateTime(2013, 02, 06),
        DateUtil.getDateTime(2019, 03, 06),
        Context.getLocationService().getLocation(103),
        context);
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }

  protected EvaluatedCohort evaluateCohortDefinition(
      CohortDefinition cd, Date startDate, Date endDate, Location location)
      throws EvaluationException {
    addParameters(cd);
    EvaluationContext context = new EvaluationContext();

    setParameters(startDate, endDate, location, context);
    return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
  }
}
