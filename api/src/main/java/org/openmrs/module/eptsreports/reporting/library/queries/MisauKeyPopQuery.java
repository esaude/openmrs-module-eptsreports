package org.openmrs.module.eptsreports.reporting.library.queries;

public class MisauKeyPopQuery {

  public static final String findPatientsWhoAreKeyPop(final KeyPopType keyPopType) {

    String query = "";

    switch (keyPopType) {
      case HOMOSEXUAL:
        query = query + "";
        break;

      case PRISIONER:
        query = query + "";
        break;

      case SEXWORKER:
        query = query + "";
        break;

      case DRUGUSER:
        query = query + "";
        break;
    }

    return query;
  }
}
