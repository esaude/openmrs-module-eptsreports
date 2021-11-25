package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.ArrayList;
import java.util.List;

public class EptsCommonDimensionKey {

  private List<DimensionKey> dimensionKeys;

  private EptsCommonDimensionKey() {
    dimensionKeys = new ArrayList<>();
  }

  public static EptsCommonDimensionKey of(DimensionKey dimensionKey) {
    EptsCommonDimensionKey commonDimensionKey = new EptsCommonDimensionKey();
    commonDimensionKey.dimensionKeys.add(dimensionKey);
    return commonDimensionKey;
  }

  public EptsCommonDimensionKey and(DimensionKey dimensionKey) {
    this.dimensionKeys.add(dimensionKey);
    return this;
  }

  public String getDimensions() {
    StringBuilder sb = new StringBuilder();
    for (DimensionKey dimensionKey : dimensionKeys) {
      sb.append(dimensionKey.getDimension()).append("|");
    }
    String dimensionOptions = sb.toString();
    return dimensionOptions.substring(0, dimensionOptions.length() - 1);
  }
}
