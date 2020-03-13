package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import java.util.List;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * This class is similar to {@link EncountersForPatientDataDefinition} the difference is its
 * evaluator {@link JembiEncounterPatientDataEvaluator} uses <i>whereLess</i> for <b>:endate</b>
 * instead of <i>whereLessOrEqualTo</i>
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiEncounterPatientDataDefinition")
public class JembiEncounterPatientDataDefinition extends BaseDataDefinition
    implements PatientDataDefinition {
  // ***** PROPERTIES *****

  @ConfigurationProperty private TimeQualifier which;

  @ConfigurationProperty(required = true)
  private List<EncounterType> types;

  @ConfigurationProperty private List<Location> locationList;

  @ConfigurationProperty private Date onOrAfter;

  @ConfigurationProperty private Date onOrBefore;

  @ConfigurationProperty private boolean onlyInActiveVisit;

  public JembiEncounterPatientDataDefinition() {
    super();
  }

  public Class<?> getDataType() {
    if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST) {
      return Encounter.class;
    }
    return List.class;
  }

  public TimeQualifier getWhich() {
    return which;
  }

  public void setWhich(TimeQualifier which) {
    this.which = which;
  }

  public boolean getOnlyInActiveVisit() {
    return onlyInActiveVisit;
  }

  public List<EncounterType> getTypes() {
    return types;
  }

  public void setTypes(List<EncounterType> types) {
    this.types = types;
  }

  public List<Location> getLocationList() {
    return locationList;
  }

  public void setLocationList(List<Location> locationList) {
    this.locationList = locationList;
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
}
