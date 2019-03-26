package org.openmrs.module.eptsreports.reporting.unit.utils;

import java.sql.Date;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class EptsReportUtilsTest {

  @Test(expected = ReportingException.class)
  public void mergeParameterMappingsShouldThrowExceptionOnNullParameters() {
    EptsReportUtils.mergeParameterMappings(null);
  }

  @Test(expected = ReportingException.class)
  public void mergeParameterMappingsShouldThrowExceptionOnEmptyParameters() {
    EptsReportUtils.mergeParameterMappings();
  }

  @Test
  public void mergeParameterMappingsShouldMergeParameters() {
    Assert.assertEquals(
        "startDate=${startDate},endDate=${endDate},location=${location},onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}",
        EptsReportUtils.mergeParameterMappings(
            "startDate=${startDate},endDate=${endDate},location=${location}",
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    Assert.assertEquals(
        "startDate=${startDate},endDate=${endDate},location=${location},onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location},locations=${location}",
        EptsReportUtils.mergeParameterMappings(
            "startDate=${startDate},endDate=${endDate},location=${location}",
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}",
            "onOrAfter=${startDate},onOrBefore=${endDate},locations=${location}"));
  }

  @Test
  public void
      removeMissingParameterMappingsFromCohortDefintionShouldReturnMappingsIfEitherDefintionOrMappingsIsNull() {
    CohortDefinition cd = new CodedObsCohortDefinition();
    Assert.assertEquals(
        "onOrAfter=${startDate}",
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(
            null, "onOrAfter=${startDate}"));
    Assert.assertEquals(
        "", EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, ""));
    Assert.assertNull(EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, null));
  }

  @Test
  public void removeMissingParameterMappingsFromCohortDefintionShouldWorkRightly() {
    CohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    Assert.assertEquals(
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore}",
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(
            cd, "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locations=${location}"));
    Assert.assertEquals(
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},endDate=${onOrBefore}",
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(
            cd,
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locations=${location},endDate=${onOrBefore}"));
  }
}
