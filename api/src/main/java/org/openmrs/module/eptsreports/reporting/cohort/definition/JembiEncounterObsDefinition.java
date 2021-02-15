package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiEncounterObsDefinition")
public class JembiEncounterObsDefinition extends BaseDataDefinition
    implements PatientDataDefinition {

  @ConfigurationProperty(required = true)
  private Concept question;

  @ConfigurationProperty(required = false)
  private List<Concept> answers;

  @ConfigurationProperty(required = true)
  private EncounterType encounterType;

  @ConfigurationProperty(required = true)
  private Location location;

  @ConfigurationProperty(required = false)
  private Date onOrAfter;

  @ConfigurationProperty(required = false)
  private Date onOrBefore;

  @ConfigurationProperty(required = true)
  private TimeQualifier timeQualifier;

  @ConfigurationProperty(required = true)
  private EPTSMetadataDatetimeQualifier eptsMetadataDatetimeQualifier;

  @Override
  public Class<?> getDataType() {
    return Obs.class;
  }

  public Concept getQuestion() {
    return question;
  }

  public void setQuestion(Concept question) {
    this.question = question;
  }

  public List<Concept> getAnswers() {
    return answers;
  }

  public void setAnswers(List<Concept> answers) {
    this.answers = answers;
  }

  public EncounterType getEncounterType() {
    return encounterType;
  }

  public void setEncounterType(EncounterType encounterType) {
    this.encounterType = encounterType;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Date getOnOrAfter() {
    return onOrAfter;
  }

  public void setOnOrAfter(Date onOrAfter) {
    this.onOrAfter = onOrAfter;
  }

  public Date getOnOrBefore() {
    return onOrBefore;
  }

  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }

  public TimeQualifier getTimeQualifier() {
    return timeQualifier;
  }

  public void setTimeQualifier(TimeQualifier timeQualifier) {
    this.timeQualifier = timeQualifier;
  }

  public EPTSMetadataDatetimeQualifier getEptsMetadataDatetimeQualifier() {
    return eptsMetadataDatetimeQualifier;
  }

  public void setEptsMetadataDatetimeQualifier(
      EPTSMetadataDatetimeQualifier eptsMetadataDatetimeQualifier) {
    this.eptsMetadataDatetimeQualifier = eptsMetadataDatetimeQualifier;
  }
}
