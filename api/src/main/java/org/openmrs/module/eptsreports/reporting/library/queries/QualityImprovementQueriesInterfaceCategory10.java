package org.openmrs.module.eptsreports.reporting.library.queries;

public interface QualityImprovementQueriesInterfaceCategory10 {

  class QUERY {

    public static final String findAllPatientsDiagnosedWithThePCRTest =
        "select p.patient_id from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs obsPCR on obsPCR.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsPCR.obs_datetime is not null "
            + "and "
            + "e.location_id=:location and e.encounter_type=53 and obsPCR.concept_id=22772 and obsPCR.value_coded=1030 and obsPCR.voided=0 "
            + "group by p.patient_id ";

    public static final String
        findAllPatientsDiagnosedWithThePCRTestAndStartDateARTMinusPCRTestDaysBetweenZeroAndFifteen =
            "select patient_id from( "
                + "select tx_new.patient_id,tx_new.art_start_date as art_start_date,DATEDIFF(tx_new.art_start_date, PCR.obs_datetime)  from ( "
                + "SELECT patient_id, MIN(art_start_date) art_start_date  FROM ( "
                + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
                + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
                + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 "
                + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endInclusionDate  AND e.location_id=:location "
                + "GROUP BY p.patient_id "
                + ") art_start "
                + "GROUP BY patient_id "
                + ") tx_new "
                + " "
                + "inner join ( "
                + "select p.patient_id,obs_datetime from patient p "
                + "inner join encounter e on e.patient_id=p.patient_id "
                + "inner join obs obsPCR on obsPCR.encounter_id=e.encounter_id "
                + "where p.voided=0 and e.voided=0 and obsPCR.obs_datetime is not null "
                + "and "
                + "e.location_id=:location and e.encounter_type=53 and obsPCR.concept_id=22772 and obsPCR.value_coded=1030 and obsPCR.voided=0 "
                + "group by p.patient_id "
                + ") PCR on PCR.patient_id = tx_new.patient_id and DATEDIFF(tx_new.art_start_date, PCR.obs_datetime) between 0 AND 15 and tx_new.art_start_date between :startInclusionDate and :endInclusionDate "
                + ") final group by patient_id ";

    public static final String
        findPatientsWhoAreNewlyEnrolledOnARTAndAgeBetweenZeroAndEighteenMonths =
            "SELECT patient_id  FROM ( "
                + "SELECT patient_id, MIN(art_start_date) art_start_date FROM ( "
                + "SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
                + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
                + "WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=53 "
                + "AND o.concept_id=1190 AND o.value_datetime is NOT NULL AND o.value_datetime<=:endInclusionDate AND e.location_id=:location "
                + "GROUP BY p.patient_id "
                + "UNION "
                + "Select 	p.patient_id,min(o.value_datetime) art_start_date from  patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "inner join obs o on e.encounter_id=o.encounter_id "
                + "inner join obs oLevantou on e.encounter_id=oLevantou.encounter_id "
                + "where  p.voided=0 and e.voided=0 and o.voided=0 and oLevantou.voided=0 and e.encounter_type=52 and o.concept_id=23866 and "
                + "o.value_datetime is not null and o.value_datetime<=:endInclusionDate and e.location_id=:location and "
                + "oLevantou.concept_id=23865 and oLevantou.value_coded=1065 "
                + "group by p.patient_id "
                + ") art_start GROUP "
                + "BY patient_id "
                + ") tx_new "
                + "INNER JOIN person pe ON tx_new.patient_id=pe.person_id "
                + "WHERE (TIMESTAMPDIFF(month,birthdate,art_start_date)) between 0 and 18 AND birthdate IS NOT NULL and pe.voided=0 "
                + "AND art_start_date BETWEEN :startInclusionDate and :endInclusionDate ";
  }
}
