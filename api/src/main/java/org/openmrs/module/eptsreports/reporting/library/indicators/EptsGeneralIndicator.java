/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.indicators;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.stereotype.Component;

@Component
public class EptsGeneralIndicator extends BaseIndicators {

  /**
   * Methods that takes a cohort defintion and return and indicator definition for reuse
   *
   * @return CohortIndicator
   */
  public CohortIndicator getIndicator(String name, Mapped<CohortDefinition> cd) {
    return newCohortIndicator(name, cd);
  }
}
