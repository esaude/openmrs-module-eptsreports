package org.openmrs.module.eptsreports.reporting.library.dimensions;

import org.springframework.stereotype.Service;

@Service
public class EptsCommonDimensionKey {

  private DimensionKey dimensionKey;

  public DimensionKey keyFor(DimensionKey dimensionKey) {
    this.dimensionKey = dimensionKey;
    return this.dimensionKey;
  }

  public DimensionKey and(DimensionKey dimensionKey) {
    if (dimensionKey == null) {
      throw new NullPointerException("");
    }
    this.dimensionKey.and(dimensionKey);
    return this.dimensionKey;
  }

  public String get() {
    return this.dimensionKey.getDimension();
  }
}
