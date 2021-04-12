package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTCompletationCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxTbPrevCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TxTbPrevCalculationTest extends DefinitionsFGHLiveTest {
  @Autowired private TxTbPrevCohortQueries txTbPrevCohortQueries;
  @Autowired private TPTCompletationCohortQueries tPTCompletationCohortQueries;

  @Test
  public void shouldFindPatientsWhoAre3HP() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(398);
    final Date startDate = DateUtil.getDateTime(2020, 9, 21);

    final Date endDate = DateUtil.getDateTime(2021, 3, 20);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    CohortDefinition p = tPTCompletationCohortQueries.getTbPrevTotalDenominator();

    final EvaluatedCohort evaluateCohortDefinition = this.evaluateCohortDefinition(p, parameters);

    System.out.println(evaluateCohortDefinition.getMemberIds().size());
    assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());

    System.out.println("----------------------------------");

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

  // @Override
  // protected String username() {
  // return "admin";
  // }
  //
  // @Override
  // protected String password() {
  // return "eSaude123";
  // }
}
