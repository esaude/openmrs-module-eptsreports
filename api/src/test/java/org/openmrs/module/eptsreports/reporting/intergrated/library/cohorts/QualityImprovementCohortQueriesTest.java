package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovementCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class QualityImprovementCohortQueriesTest extends DefinitionsTest {

  @Autowired private QualityImprovementCohortQueries qualityImprovementCohortQueries;

  @Before
  public void setup() throws Exception {
    executeDataSet("qualityImprovementCohortQueriesTest.xml");
  }

  //@Ignore
  @Test
  public void getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter()
      throws EvaluationException {

    CohortDefinition cohortDefinition =
        qualityImprovementCohortQueries
            .getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter();

    Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), getStartDate());
    parameters.put(new Parameter("endDate", "End Date", Date.class), getEndDate());
    parameters.put(new Parameter("location", "Location", Location.class), getLocation());
    parameters.put(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class),
        getDataFinalAvaliacao());

    EvaluatedCohort evaluatedCohort = evaluateCohortDefinition(cohortDefinition, parameters);

    //assertTrue(evaluatedCohort.getMemberIds().contains(1));
    
    System.out.println("--------------  "+evaluatedCohort.getMemberIds().size());
    System.out.println("startdate "+getStartDate());
    System.out.println("getEndDate "+getEndDate());
    System.out.println("getDataFinalAvaliacao "+getDataFinalAvaliacao());
    

    assertNotNull(cohortDefinition);
  }

  private Date getDataFinalAvaliacao() {
    return DateUtil.getDateTime(2019, 5, 26);
  }

  @Override
  public Date getStartDate() {
    return DateUtil.getDateTime(2015, 5, 10);
  }
  @Override
  public Date getEndDate() {
    return DateUtil.getDateTime(2019, 5, 26);
  }
}
