package org.openmrs.module.eptsreports.reporting.library.queries.mq;

public interface GenericMQQueryIntarface {

  class QUERY {
    public static final String findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(
        int startAge, int endAge) {

      final String sql =
          " SELECT patient_id FROM ( "
              + " SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + " SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
              + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endInclusionDate AND e.location_id = :location "
              + " GROUP BY p.patient_id "
              + " ) art_start "
              + " GROUP BY patient_id "
              + " ) tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) BETWEEN %s  AND %s "
              + "AND birthdate IS NOT NULL and pe.voided = 0  AND art_start_date BETWEEN :startInclusionDate AND :endInclusionDate ";

      return String.format(sql, startAge, endAge);
    }

    public static final String findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(
        int startAge, int endAge) {

      final String sql =
          " SELECT patient_id FROM ( "
              + " SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + " SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
              + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endInclusionDate AND e.location_id = :location "
              + " GROUP BY p.patient_id "
              + " ) art_start "
              + " GROUP BY patient_id "
              + " ) tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(month,birthdate,art_start_date)) BETWEEN %s AND %s AND birthdate IS NOT NULL and pe.voided = 0 "
              + "AND art_start_date BETWEEN :startInclusionDate AND :endInclusionDate ";

      return String.format(sql, startAge, endAge);
    }

    public static final String findPatientsWhoAreNewlyEnrolledOnARTBiggerThanParam(int startAge) {

      final String sql =
          " SELECT patient_id FROM ( "
              + " SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + " SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
              + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endInclusionDate AND e.location_id = :location "
              + " GROUP BY p.patient_id "
              + " ) art_start "
              + " GROUP BY patient_id "
              + " ) tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) >= %s AND birthdate IS NOT NULL and pe.voided = 0  "
              + "AND art_start_date BETWEEN :startInclusionDate AND :endInclusionDate ";

      return String.format(sql, startAge);
    }

    public static final String findPatientsWhoAreNewlyEnrolledOnARTLessThanParam(int startAge) {

      final String sql =
          " SELECT patient_id FROM ( "
              + " SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + " SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
              + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
              + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
              + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endInclusionDate AND e.location_id = :location "
              + " GROUP BY p.patient_id "
              + " ) art_start "
              + " GROUP BY patient_id "
              + " ) tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) < %s AND birthdate IS NOT NULL and pe.voided = 0  "
              + "AND art_start_date BETWEEN :startInclusionDate AND :endInclusionDate ";

      return String.format(sql, startAge);
    }

    public static final String findPAtientWithCVOver1000CopiesAgeRenge(int startAge, int endAge) {
      final String sql =
          "select carga_viral.patient_id from ( "
              + "Select p.patient_id, max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id = e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 6 and  o.concept_id = 856 and "
              + "o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location and o.value_numeric > 1000 "
              + "group by p.patient_id "
              + ") carga_viral "
              + "inner join person on person_id = carga_viral.patient_id "
              + "WHERE (TIMESTAMPDIFF(year, birthdate, carga_viral.data_carga)) BETWEEN %s  AND %s AND birthdate IS NOT NULL and voided = 0  ";

      return String.format(sql, startAge, endAge);
    }

    public static final String findPAtientWithCVOver1000CopiesBiggerThanParam(int startAge) {
      final String sql =
          "select carga_viral.patient_id from ( "
              + "Select p.patient_id, max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id = e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 6 and  o.concept_id = 856 and "
              + "o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location and o.value_numeric > 1000 "
              + "group by p.patient_id "
              + ") carga_viral "
              + "inner join person on person_id = carga_viral.patient_id "
              + "WHERE (TIMESTAMPDIFF(year, birthdate, carga_viral.data_carga)) >= %s  AND birthdate IS NOT NULL and voided = 0  ";

      return String.format(sql, startAge);
    }

    public static final String findPAtientWithCVOver1000CopiesLessThanParam(int startAge) {
      final String sql =
          "select carga_viral.patient_id from ( "
              + "Select p.patient_id, max(o.obs_datetime) data_carga from patient p "
              + "inner join encounter e on p.patient_id = e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 6 and  o.concept_id = 856 and "
              + "o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location and o.value_numeric > 1000 "
              + "group by p.patient_id "
              + ") carga_viral "
              + "inner join person on person_id = carga_viral.patient_id "
              + "WHERE (TIMESTAMPDIFF(year, birthdate, carga_viral.data_carga)) < %s AND birthdate IS NOT NULL and voided = 0  ";
      ;

      return String.format(sql, startAge);
    }

    public static final String findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateByAgeRenge(
        int startAge, int endAge) {

      final String sql =
          "SELECT patient_id FROM ( "
              + "SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
              + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
              + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 "
              + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endRevisionDate AND e.location_id=:location "
              + "GROUP BY p.patient_id "
              + ") art_start GROUP "
              + "BY patient_id "
              + ") tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) BETWEEN %s AND %s AND birthdate IS NOT NULL and pe.voided=0 "
              + "AND art_start_date BETWEEN date_add(:endRevisionDate, interval -14 MONTH) AND  date_add(:endRevisionDate, interval -11 MONTH) ";

      return String.format(sql, startAge, endAge);
    }

    public static final String
        findPatientsWhoAreNewlyEnrolledOnARTTUntilRevisionDateBiggerThanParam(int startAge) {

      final String sql =
          "SELECT patient_id FROM ( "
              + "SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
              + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
              + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 "
              + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endRevisionDate AND e.location_id=:location "
              + "GROUP BY p.patient_id "
              + ") art_start GROUP "
              + "BY patient_id "
              + ") tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) > %s AND birthdate IS NOT NULL and pe.voided=0 "
              + "AND art_start_date BETWEEN date_add(:endRevisionDate, interval -14 MONTH) AND  date_add(:endRevisionDate, interval -11 MONTH) ";

      return String.format(sql, startAge);
    }

    public static final String findPatientsWhoAreNewlyEnrolledOnARTTUntilRevisionDateLessThanParam(
        int startAge) {

      final String sql =
          "SELECT patient_id FROM ( "
              + "SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
              + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
              + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
              + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
              + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 "
              + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endRevisionDate AND e.location_id=:location "
              + "GROUP BY p.patient_id "
              + ") art_start GROUP "
              + "BY patient_id "
              + ") tx_new "
              + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,art_start_date)) < %s  AND birthdate IS NOT NULL and pe.voided=0 "
              + "AND art_start_date BETWEEN date_add(:endRevisionDate, interval -14 MONTH) AND  date_add(:endRevisionDate, interval -11 MONTH) ";

      return String.format(sql, startAge);
    }
  }
}
