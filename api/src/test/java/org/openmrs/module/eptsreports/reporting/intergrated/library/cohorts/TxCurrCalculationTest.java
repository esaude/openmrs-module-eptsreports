package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TxCurrCalculationTest extends DefinitionsFGHLiveTest {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(208);

    System.out.println(location.getName());
    final Date startDate = DateUtil.getDateTime(2020, 6, 21);
    final Date endDate = DateUtil.getDateTime(2020, 9, 20);

    System.out.println(startDate);
    System.out.println(endDate);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    CohortDefinition cohortDefinition =
        txCurrCohortQueries.getPatientsOnArtOnArvDispenseForLessThan3Months();

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(cohortDefinition, parameters);

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

  public static void main(String[] args) {
    Date stardDate = DateUtil.getDateTime(2020, 6, 17);

    Date endDate = DateUtil.getDateTime(2020, 9, 8);
    System.out.println(" Start Date: " + stardDate);
    System.out.println(" End Date: " + endDate);
    System.out.println(DateUtil.getDaysBetween(stardDate, endDate));
  }
}
