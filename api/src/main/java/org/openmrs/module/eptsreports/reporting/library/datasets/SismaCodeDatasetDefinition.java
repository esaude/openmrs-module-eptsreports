package org.openmrs.module.eptsreports.reporting.library.datasets;
/**
 * Simple dataset Class to get SISMA CODE from location_attribute properties reusing
 * #DatimCodeDatasetDefinition implementation
 */
public class SismaCodeDatasetDefinition extends DatimCodeDatasetDefinition {

  public SismaCodeDatasetDefinition() {
    typeId = 1;
    columnName = "sismaCode";
  }
}
