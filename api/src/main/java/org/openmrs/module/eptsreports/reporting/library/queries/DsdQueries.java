package org.openmrs.module.eptsreports.reporting.library.queries;


public class DsdQueries {

  public static String getPatientsEnrolledOnGAAC() {
    String query =
        "SELECT gm.member_id FROM gaac g INNER JOIN gaac_member gm ON g.gaac_id=gm.gaac_id "
            + "WHERE gm.start_date<:endDate AND gm.voided=0 AND g.voided=0 AND ((leaving is null) "
            + "OR (leaving=0) OR (leaving=1 AND gm.end_date>:endDate)) AND location_id=:location";
    return query;
  }

  public static String getPatientsParticipatingInDsdModel(
      int prevencaoPositivaInicialEncounterType,
      int prevencaoPositivaSeguimentoEncounterType,
      Integer gaac,
      Integer af,
      Integer ca,
      Integer pu,
      Integer fr,
      Integer dt,
      Integer dc,
      Integer otherModel,
      Integer valueCoded1,
      Integer valueCoded2) {

    String query =
        "SELECT p.patient_id FROM patient p JOIN encounter e ON p.patient_id=e.patient_id "
            + "JOIN obs o ON p.patient_id=o.person_id WHERE e.encounter_id IN (%d,%d) AND o.concept_id "
            + "IN (%d, %d, %d, %d, %d, %d, %d, %d) "
            + "AND o.value_coded IN (%d, %d) AND e.encounter_datetime=:endDate";

    return String.format(
        query,
        prevencaoPositivaInicialEncounterType,
        prevencaoPositivaSeguimentoEncounterType,
        gaac,
        af,
        ca,
        pu,
        fr,
        dt,
        dc,
        otherModel,
        valueCoded1,
        valueCoded2);
  }
}
