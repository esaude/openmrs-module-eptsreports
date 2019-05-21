package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Uses age cohorts that determine age on ART start date. */
@Component("txNewAgeDimensionCohort")
public class TxNewAgeDimensionCohort implements AgeDimensionCohortInterface {

  private GenericCohortQueries genericCohorts;

  private AgeCohortQueries ageCohortQueries;

  @Autowired
  public TxNewAgeDimensionCohort(
      GenericCohortQueries genericCohorts, AgeCohortQueries ageCohortQueries) {
    this.genericCohorts = genericCohorts;
    this.ageCohortQueries = ageCohortQueries;
  }

  @Override
  public Mapped<CohortDefinition> createXtoYAgeCohort(String name, Integer minAge, Integer maxAge) {
    return EptsReportUtils.map(
        genericCohorts.getAgeOnArtStartDate(minAge, maxAge, true), "location=${location}");
  }

  @Override
  public Mapped<CohortDefinition> createUnknownAgeCohort() {
    CohortDefinition ageCohort = ageCohortQueries.createUnknownAgeCohort();
    return EptsReportUtils.map(ageCohort, "");
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "startDate", Date.class),
        new Parameter("endDate", "endDate", Date.class),
        new Parameter("location", "location", Location.class));
  }
}
