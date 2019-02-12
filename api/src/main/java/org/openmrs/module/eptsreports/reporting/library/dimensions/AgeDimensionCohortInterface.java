package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.List;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/** Represents a strategy for creating cohorts for the age dimension. */
public interface AgeDimensionCohortInterface {

  Mapped<CohortDefinition> createXtoYAgeCohort(String name, Integer minAge, Integer maxAge);

  Mapped<CohortDefinition> createUnknownAgeCohort();

  List<Parameter> getParameters();
}
