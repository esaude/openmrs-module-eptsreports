package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import java.util.HashSet;
import java.util.Set;

public class Match {
  // current=master[name]
  private String mapping;
  private Integer currentValue;
  private Integer masterValue;
  // in current not in master
  private Set<Integer> currentOffSetPatientIds = new HashSet<>();
  // in master not in current
  private Set<Integer> masterOffSetPatientIds = new HashSet<>();

  public String getMapping() {
    return mapping;
  }

  public Integer getCurrentValue() {
    return currentValue;
  }

  public Integer getMasterValue() {
    return masterValue;
  }

  public Set<Integer> getCurrentOffSetPatientIds() {
    return currentOffSetPatientIds;
  }

  public Set<Integer> getMasterOffSetPatientIds() {
    return masterOffSetPatientIds;
  }

  public void setCurrentOffSetPatientIds(Set<Integer> currentOffSetPatientIds) {
    this.currentOffSetPatientIds = currentOffSetPatientIds;
  }

  public void setMasterOffSetPatientIds(Set<Integer> masterOffSetPatientIds) {
    this.masterOffSetPatientIds = masterOffSetPatientIds;
  }

  public Match(String mapping, Integer currentValue, Integer masterValue) {
    this.mapping = mapping;
    this.currentValue = currentValue;
    this.masterValue = masterValue;
  }
}
