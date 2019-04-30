package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import java.util.Date;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class UsMonthlySummaryCohortDefinitionsFGHLiveTest extends DefinitionsFGHLiveTest {
  @Autowired private UsMonthlySummaryCohortQueries usMonthlySummaryCohortQueries;

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "Admin123";
  }

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2017, 2, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2018, 9, 20);
  }

  protected Location getLocation() {
    return Context.getLocationService().getLocation(6);
  }

  @Test
  public void getPdfFormatAssetAtFinalDate() throws EvaluationException {
    // TODO remove startDate
    EvaluatedCohort result =
        evaluateCohortDefinition(usMonthlySummaryCohortQueries.getPdfFormatAssetAtFinalDate());
    Assert.assertEquals(0, result.size());
  }
}
