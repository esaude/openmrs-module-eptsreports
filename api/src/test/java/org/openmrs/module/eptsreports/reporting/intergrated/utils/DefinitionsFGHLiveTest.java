package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Before;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
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

import java.util.Date;

public abstract class DefinitionsFGHLiveTest extends BaseModuleContextSensitiveTest {
    /**
     * @see BaseContextSensitiveTest#useInMemoryDatabase()
     */
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

    protected abstract String username();

    protected abstract String password();

    @Before
    public void initialize() throws ContextAuthenticationException {
        Context.authenticate(username(), password());
    }

    private void addParameters(CohortDefinition cd) {
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addParameter(new Parameter("location", "Location", Location.class));
    }

    private void addParameters(DataSetDefinition cd) {
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.addParameter(new Parameter("location", "Location", Location.class));
    }

    private void setParameters(
            Date startDate, Date endDate, Location location, EvaluationContext context) {
        context.addParameterValue("startDate", startDate);
        context.addParameterValue("endDate", endDate);
        context.addParameterValue("location", location);
    }

    /**
     * evaluate for location#103 from 06/feb/2013 to 06/mar/2019
     */
    protected DataSet evaluateDatasetDefinition(DataSetDefinition cd) throws EvaluationException {
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", getStartDate());
        context.addParameterValue("endDate", getEndDate());
        context.addParameterValue("location", getLocation());
        addParameters(cd);
        return Context.getService(DataSetDefinitionService.class).evaluate(cd, context);
    }

    /**
     * evaluate for location#103 from 06/feb/2013 to 06/mar/2019
     */
    protected EvaluatedCohort evaluateCodedObsCohortDefinition(CohortDefinition cd)
            throws EvaluationException {
        addParameters(cd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("onOrAfter", getStartDate());
        context.addParameterValue("onOrBefore", getEndDate());
        context.addParameterValue("locationList", getLocation());
        return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
    }

    protected Date getStartDate() {
        return DateUtil.getDateTime(2013, 2, 6);
    }

    protected Date getEndDate() {
        return DateUtil.getDateTime(2019, 3, 6);
    }

    protected Location getLocation() {
        return Context.getLocationService().getLocation(103);
    }

    /**
     * evaluate for location#103 from 06/feb/2019 to 06/mar/2019
     */
    protected EvaluatedCohort evaluateCalculationCohortDefinition(CohortDefinition cd)
            throws EvaluationException {
        addParameters(cd);
        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("onOrAfter", DateUtil.getDateTime(2019, 2, 6));
        context.addParameterValue("onOrBefore", getEndDate());
        context.addParameterValue("location", getLocation());
        return Context.getService(CohortDefinitionService.class).evaluate(cd, context);
    }

    /**
     * evaluate for location#103 from 06/feb/2013 to 06/mar/2019
     */
    protected EvaluatedCohort evaluateCohortDefinition(CohortDefinition cd)
            throws EvaluationException {
        addParameters(cd);
        EvaluationContext context = new EvaluationContext();

        setParameters(getStartDate(), getEndDate(), getLocation(), context);
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
