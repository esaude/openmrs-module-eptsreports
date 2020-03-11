/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

/** @author Stélio Moiane */
public interface TxNewQueries {

  class QUERY {

    public static final String findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod =
        "SELECT pg.patient_id FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id=2 "
            + "AND ps.state=29 AND ps.start_date=pg.date_enrolled "
            + "AND ps.start_date BETWEEN :startDate AND :endDate AND location_id=:location";

    public static final String
        findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard =
            "SELECT tr.patient_id from  ("
                + "SELECT p.patient_id, MIN(obsData.value_datetime) from patient p  "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id  "
                + "INNER JOIN obs obsTrans ON e.encounter_id=obsTrans.encounter_id AND obsTrans.voided=0 AND obsTrans.concept_id=1369 AND obsTrans.value_coded=1065 "
                + "INNER JOIN obs obsTarv ON e.encounter_id=obsTarv.encounter_id AND obsTarv.voided=0 AND obsTarv.concept_id=6300 AND obsTarv.value_coded=6276 "
                + "INNER JOIN obs obsData ON e.encounter_id=obsData.encounter_id AND obsData.voided=0 AND obsData.concept_id=23891 "
                + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=53 AND obsData.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location GROUP BY p.patient_id "
                + ") tr GROUP BY tr.patient_id ";

    public static final String findPatientsWhoAreNewlyEnrolledOnART =
        "SELECT patient_id FROM "
            + "(SELECT patient_id, MIN(art_start_date) art_start_date FROM "
            + "(SELECT p.patient_id, MIN(e.encounter_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (18,6,9) "
            + "AND o.concept_id=1255 AND o.value_coded=1256 AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (18,6,9,53) "
            + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT pg.patient_id, MIN(date_enrolled) art_start_date FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled<=:endDate AND location_id=:location GROUP BY pg.patient_id "
            + "UNION SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 "
            + "AND o.concept_id=23866 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id) "
            + "art_start GROUP BY patient_id ) tx_new WHERE art_start_date BETWEEN :startDate AND :endDate";

    public static final String findPatientsWhoAreNewlyEnrolledOnArtByAgeAndGender =
        "SELECT patient_id FROM "
            + "(SELECT patient_id, MIN(art_start_date) art_start_date FROM "
            + "(SELECT p.patient_id, MIN(e.encounter_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (18,6,9) "
            + "AND o.concept_id=1255 AND o.value_coded=1256 AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (18,6,9,53) "
            + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT pg.patient_id, MIN(date_enrolled) art_start_date FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled<=:endDate AND location_id=:location GROUP BY pg.patient_id "
            + "UNION SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 "
            + "AND o.concept_id=23866 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endDate AND e.location_id=:location GROUP BY p.patient_id) "
            + "art_start GROUP BY patient_id ) tx_new "
            + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id WHERE TIMESTAMPDIFF(year,birthdate,art_start_date) BETWEEN %d AND %d AND gender='%s' AND birthdate IS NOT NULL";

    public static final String findPatientsWithUnknownAgeByGender =
        "SELECT patient_id FROM patient "
            + "INNER JOIN person ON patient_id=person_id "
            + "WHERE patient.voided=0 AND person.voided=0 AND gender = '%s' AND birthdate IS NULL";

    public static final String findPatientsWhoAreNewlyEnrolledOnArtByAge =
        "SELECT patient_id FROM "
            + "(SELECT patient_id, MIN(art_start_date) art_start_date FROM "
            + "(SELECT p.patient_id, MIN(e.encounter_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "WHERE e.voided=0 AND o.voided=0 AND p.voided=0 AND e.encounter_type in (18,6,9) "
            + "AND o.concept_id=1255 AND o.value_coded=1256 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type IN (18,6,9,53) "
            + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT pg.patient_id, MIN(date_enrolled) art_start_date FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "WHERE pg.voided=0 AND p.voided=0 AND program_id=2 AND date_enrolled BETWEEN :startDate AND :endDate AND location_id=:location GROUP BY pg.patient_id "
            + "UNION SELECT e.patient_id, MIN(e.encounter_datetime) AS art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "WHERE p.voided=0 AND e.encounter_type=18 AND e.voided=0 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=52 "
            + "AND o.concept_id=23866 AND o.value_datetime is NOT NULL AND o.value_datetime BETWEEN :startDate AND :endDate AND e.location_id=:location GROUP BY p.patient_id) "
            + "art_start GROUP BY patient_id ) tx_new "
            + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id WHERE TIMESTAMPDIFF(year,birthdate,art_start_date) BETWEEN %d AND %d AND birthdate IS NOT NULL";
  }
}
