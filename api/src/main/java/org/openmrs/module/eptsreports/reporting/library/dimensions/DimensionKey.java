package org.openmrs.module.eptsreports.reporting.library.dimensions;

public interface DimensionKey {

  public String getKey();

  public String getDimension();

  public DimensionKey and(DimensionKey dimensionKey);
}
