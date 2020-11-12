package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TRFINCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TRFINCalculationTest extends DefinitionsFGHLiveTest {

  @Autowired private TRFINCohortQueries txTrfInCohortQueries;

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(208);
    final Date startDate = DateUtil.getDateTime(2019, 9, 21);
    final Date endDate = DateUtil.getDateTime(2019, 12, 20);

    System.out.println(startDate);
    System.out.println(endDate);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    CohortDefinition patientsWhoAreTransferredIn =
        txTrfInCohortQueries.getPatiensWhoAreTransferredIn();

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(patientsWhoAreTransferredIn, parameters);

    System.out.println(evaluateCohortDefinition.getMemberIds().size());
    assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());

    System.out.println("----------------------------------");

    for (int t : evaluateCohortDefinition.getMemberIds()) {
      System.out.println(t);
    }
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }
}
