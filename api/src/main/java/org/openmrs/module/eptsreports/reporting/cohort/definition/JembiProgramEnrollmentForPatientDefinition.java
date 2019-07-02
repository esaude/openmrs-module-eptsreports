/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.cohort.definition;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** Definition for Program Enrollment that considers location */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiProgramEnrollmentForPatientDefinition")
public class JembiProgramEnrollmentForPatientDefinition extends BaseDataDefinition
    implements PatientDataDefinition {

  @ConfigurationProperty(required = true)
  private Program program;

  @ConfigurationProperty(required = true)
  private Location location;

  @ConfigurationProperty(required = false)
  private TimeQualifier whichEnrollment;

  public JembiProgramEnrollmentForPatientDefinition() {
    super();
  }

  public JembiProgramEnrollmentForPatientDefinition(String name) {
    super(name);
  }

  @Override
  public Class<?> getDataType() {
    return PatientProgram.class;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Program getProgram() {
    return program;
  }

  public Location getLocation() {
    return location;
  }

  public TimeQualifier getWhichEnrollment() {
    return whichEnrollment;
  }

  public void setWhichEnrollment(TimeQualifier whichEnrollment) {
    this.whichEnrollment = whichEnrollment;
  }
}
