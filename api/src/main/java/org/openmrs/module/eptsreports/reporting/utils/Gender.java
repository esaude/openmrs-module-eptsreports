/** */
package org.openmrs.module.eptsreports.reporting.utils;

/** @author Stélio Moiane */
public enum Gender {
  MALE("M"),

  FEMALE("F");

  private final String name;

  private Gender(final String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
