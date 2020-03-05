package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class ResumoTrimestralCohortDefinitionTest extends DefinitionsFGHLiveTest {

  @Autowired private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Test
  public void shouldFindPatientsWhoInitiatedArtTreatmentInReportingPeriod()
      throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(221);
    final Date startDate = DateUtil.getDateTime(2019, 1, 1);
    final Date endDate = DateUtil.getDateTime(2019, 3, 31);
    //
    //    final CohortDefinition cohortDefinition =
    //        this.resumoTrimestralCohortQueries
    //            .getPatientsWhoHaveInitiatedArtTreatmentInReportingPeriodForJanuary();

    CohortDefinition cohortDefinition1 =
        txNewCohortQueries.getTxNewCompositionCohort("txNewComposition");

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    //    final EvaluatedCohort evaluateCohortDefinition1 =
    //        this.evaluateCohortDefinition(cohortDefinition, parameters);

    // System.out.println(evaluateCohortDefinition1.getMemberIds().size());
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "H!$fGH0Mr$";
  }
}
