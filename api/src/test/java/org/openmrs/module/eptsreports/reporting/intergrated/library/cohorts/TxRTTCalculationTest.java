package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.TxRttCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class TxRTTCalculationTest extends DefinitionsFGHLiveTest {

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(208);
    final Date startDate = DateUtil.getDateTime(2019, 10, 21);
    final Date endDate = DateUtil.getDateTime(2020, 1, 20);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    final BaseFghCalculationCohortDefinition rttCohortDefinition =
        new BaseFghCalculationCohortDefinition(
            "patientsOnRTT", Context.getRegisteredComponents(TxRttCalculation.class).get(0));

    rttCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    rttCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    rttCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(rttCohortDefinition, parameters);

    System.out.println(evaluateCohortDefinition.getMemberIds().size());
    assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());

    for (int t : evaluateCohortDefinition.getMemberIds()) {
      System.out.println(t);
    }
  }

  @Override
  protected String username() {
    return "domingos.bernardo";
  }

  @Override
  protected String password() {
    return "dBernardo1";
  }
}
