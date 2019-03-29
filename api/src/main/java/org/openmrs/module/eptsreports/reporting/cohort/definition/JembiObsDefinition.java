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

package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiObsDefinition")
public class JembiObsDefinition extends BaseDataDefinition implements PatientDataDefinition {

  @ConfigurationProperty(required = true)
  private Concept question;

  @ConfigurationProperty private Concept answer;

  @ConfigurationProperty(required = true)
  private Location location;

  @ConfigurationProperty(required = true)
  private boolean sortByDatetime = true;

  @ConfigurationProperty(required = true)
  private boolean first = true;

  @ConfigurationProperty(required = false)
  private Date valueDateTimeOnOrAfter;

  @ConfigurationProperty(required = false)
  private Date valueDateTimeOnOrBefore;

  public JembiObsDefinition() {
    super();
  }

  public JembiObsDefinition(String name) {
    super(name);
  }

  @Override
  public Class<?> getDataType() {
    return PatientProgram.class;
  }

  public Concept getQuestion() {
    return question;
  }

  public void setQuestion(Concept question) {
    this.question = question;
  }

  public Concept getAnswer() {
    return answer;
  }

  public void setAnswer(Concept answer) {
    this.answer = answer;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public boolean isSortByDatetime() {
    return sortByDatetime;
  }

  public void setSortByDatetime(boolean sortByDatetime) {
    this.sortByDatetime = sortByDatetime;
  }

  public boolean isFirst() {
    return first;
  }

  public void setFirst(boolean first) {
    this.first = first;
  }

  public Date getValueDateTimeOnOrAfter() {
    return valueDateTimeOnOrAfter;
  }

  public void setValueDateTimeOnOrAfter(Date valueDateTimeOnOrAfter) {
    this.valueDateTimeOnOrAfter = valueDateTimeOnOrAfter;
  }

  public Date getValueDateTimeOnOrBefore() {
    return valueDateTimeOnOrBefore;
  }

  public void setValueDateTimeOnOrBefore(Date valueDateTimeOnOrBefore) {
    this.valueDateTimeOnOrBefore = valueDateTimeOnOrBefore;
  }
}
