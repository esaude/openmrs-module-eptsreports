package org.openmrs.module.eptsreports.reporting.reports;

/** To be thrown when creating a report design fails. */
public class ReportDesignConstructionException extends RuntimeException {
  public ReportDesignConstructionException(Throwable throwable) {
    super(throwable);
  }
}
