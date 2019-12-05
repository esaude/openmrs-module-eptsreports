package org.openmrs.module.eptsreports.reporting.unit.library.cohorts;

import java.util.Date;
import java.util.Set;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveTest extends DefinitionsFGHLiveTest {

  @Autowired private TxPvlsCohortQueries hivCohortQueries;

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(221);
  }

  @Override
  protected Date getEndDate() {
    Date date = DateUtil.getDateTime(2019, 10, 20);
    return date;
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2019, 9, 21);
  }

  @Override
  protected void setParameters(
      Date startDate, Date endDate, Location location, EvaluationContext context) {
    context.addParameterValue("startDate", startDate);
    context.addParameterValue("endDate", endDate);
    context.addParameterValue("location", location);
  }

  @Test
  public void testMethod() throws EvaluationException {
    CohortDefinition cd = hivCohortQueries.getPatientsWithViralLoadResults();
    EvaluatedCohort evaluated = evaluateCohortDefinition(cd);
    Set<Integer> patients = evaluated.getMemberIds();
    for (Integer i : patients) {
      System.out.println(i);
    }
    System.out.println("size:=> " + patients.size());
  }
}
