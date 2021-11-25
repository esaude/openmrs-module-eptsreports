package org.openmrs.module.eptsreports.reporting.library.dimensions;

public enum DimensionKeyForGender implements DimensionKey {
  female("F"),
  male("M");

  private String key;

  private DimensionKeyForGender(String key) {
    this.key = key;
  }

  @Override
  public String getKey() {
    return this.key;
  }

  @Override
  public String getDimension() {
    StringBuilder sb = new StringBuilder();
    sb.append(getPrefix()).append(getKey());
    return sb.toString();
  }

  @Override
  public String getPrefix() {
    return "gender=";
  }
}
