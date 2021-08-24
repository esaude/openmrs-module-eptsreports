package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeyPopCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;
}
