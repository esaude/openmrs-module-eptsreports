package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import java.util.Set;

public class Match {
  // current=master[name]
  private String mapping;
  private Integer currentValue;
  private Integer masterValue;
  private Integer currentOffSet = 0;
  private Integer masterOffSet = 0;
  // in current not in master
  private Set<Integer> currentOffSetPatientIds;
  // in master not in current
  private Set<Integer> masterOffSetPatientIds;

  public String getMapping() {
    return mapping;
  }

  public Integer getCurrentValue() {
    return currentValue;
  }

  public Integer getMasterValue() {
    return masterValue;
  }

  public Integer getCurrentOffSet() {
    return currentOffSet;
  }

  public Integer getMasterOffSet() {
    return masterOffSet;
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
    if(currentValue != null && masterValue != null) {
      this.currentOffSet = currentValue - masterValue > 0 ? currentValue - masterValue : currentOffSet;
      this.masterOffSet = masterValue - currentValue > 0 ? masterValue - currentValue : masterOffSet;
    }
  }
}
