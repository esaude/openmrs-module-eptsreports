package org.openmrs.module.eptsreports.metadata;

/**
 * Should be thrown when there is an error during lookup of metadata that can be configured via
 * global properties.
 */
public class ConfigurableMetadataLookupException extends MetadataLookupException {
  public ConfigurableMetadataLookupException(String message) {
    super(message);
  }
}
