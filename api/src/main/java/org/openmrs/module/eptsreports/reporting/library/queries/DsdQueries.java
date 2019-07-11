package org.openmrs.module.eptsreports.reporting.library.queries;

public class DsdQueries {

  public static String getPatientsEnrolledOnGAAC() {
    String query =
        "SELECT gm.member_id FROM gaac g INNER JOIN gaac_member gm ON g.gaac_id=gm.gaac_id WHERE  gm.start_date<:endDate AND gm.voided=0 AND g.voided=0 AND ((leaving is null) OR (leaving=0) OR (leaving=1 and gm.end_date>:endDate)) AND location_id=:location";

    return query;
  }
}
