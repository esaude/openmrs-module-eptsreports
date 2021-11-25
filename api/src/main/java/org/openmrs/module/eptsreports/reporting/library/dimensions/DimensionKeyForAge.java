package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.ArrayList;
import java.util.List;

public enum DimensionKeyForAge implements DimensionKey {
  unknown("UK"),
  bellowOneYear("<1"),
  bellow2Years("<2"),
  betweenZeroAnd4Years("0-4"),
  betweenZeroAnd14Years("0-14"),
  betweenZeroAnd15Years("0-15"),
  betweenOneAnd4Years("1-4"),
  between2And4Years("2-4"),
  between5And9Years("5-9"),
  bellow15Years("<15"),
  overAndEqualTo14Years("14+"),
  between10And14Years("10-14"),
  between25And29Years("25-29"),
  between30And34Years("30-34"),
  between35And39Years("35-39"),
  between40And44Years("40-44"),
  between45and49Years("45-49"),
  overOrEqualTo14Years("14+"),
  overOrEqualTo15Years("15+"),
  between15And19Years("15-19"),
  between20And24Years("20-24"),
  overOrEqualTo50Years("50+"),
  overOrEqualTo20Years("20+"),
  between10And19Years("10-19"),
  between2And14Years("2-14"),
  between1And14Years("1-14"),
  between2And9Years("2-9"),
  overOrEqualTo25Years("25+"),
  between50And54Years("50-54"),
  between55And59Years("55-59"),
  between60And64Years("60-64"),
  overOrEqualTo65Years("65+");

  private String key;
  private List<DimensionKey> dimensionKeys;

  private DimensionKeyForAge(String key) {
    this.key = key;
    dimensionKeys = new ArrayList<>();
    dimensionKeys.add(this);
  }

  @Override
  public String getKey() {

    return this.key;
  }

  @Override
  public String getDimension() {

    StringBuilder sb = new StringBuilder();
    for (DimensionKey dimensionKey : dimensionKeys) {
      sb.append("age=").append(getKey()).append("|");
    }
    String dimensionOptions = sb.toString();
    return dimensionOptions.substring(0, dimensionOptions.length() - 1);
  }

  @Override
  public DimensionKey and(DimensionKey dimensionKey) {
    dimensionKeys.add(dimensionKey);
    return this;
  }
}
