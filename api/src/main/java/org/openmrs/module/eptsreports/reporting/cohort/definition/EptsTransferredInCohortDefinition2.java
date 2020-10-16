package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** Cohort for patients enrolled in Tarv or Pré-Tarv that were transferred-in */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class EptsTransferredInCohortDefinition2 extends BaseCohortDefinition {

  public enum ARTProgram {
    PRE_TARV,
    TARV
  }

  @ConfigurationProperty private Date onOrAfter;

  @ConfigurationProperty private Date onOrBefore;

  @ConfigurationProperty private Location location;

  @ConfigurationProperty private List<ARTProgram> artPrograms;

  public Date getOnOrBefore() {
    return onOrBefore;
  }

  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }

  public Date getOnOrAfter() {
    return onOrAfter;
  }

  public void setOnOrAfter(Date onOrAfter) {
    this.onOrAfter = onOrAfter;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public List<ARTProgram> getArtPrograms() {
    return artPrograms;
  }

  public void setArtPrograms(List<ARTProgram> artPrograms) {
    this.artPrograms = artPrograms;
  }

  public void addArtProgram(ARTProgram artProgram) {
    if (artPrograms == null) {
      artPrograms = new ArrayList<>();
    }
    artPrograms.add(artProgram);
  }
}
