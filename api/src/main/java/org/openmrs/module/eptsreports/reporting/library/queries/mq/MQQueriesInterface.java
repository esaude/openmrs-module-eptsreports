package org.openmrs.module.eptsreports.reporting.library.queries.mq;

public interface MQQueriesInterface {

  class QUERY {
    public static final String findPatientsWhoAreNewlyEnrolledOnARTRF05 =
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
            + " ) tx_new WHERE art_start_date BETWEEN :startInclusionDate AND :endInclusionDate ";

    public static final String
        findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06 =
            "SELECT p.patient_id from patient p "
                + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
                + "INNER JOIN obs obsTrans ON e.encounter_id=obsTrans.encounter_id AND obsTrans.voided=0 AND obsTrans.concept_id=1369 AND obsTrans.value_coded=1065 "
                + "INNER JOIN obs obsTarv ON e.encounter_id=obsTarv.encounter_id AND obsTarv.voided=0 AND obsTarv.concept_id=6300 AND obsTarv.value_coded=6276 "
                + "WHERE p.voided=0 AND e.voided=0 AND e.encounter_type=53 AND  e.location_id=:location ";

    public static final String findPatientsWhoTransferedOutRF07 =
        "select saida.patient_id from ( "
            + "select p.patient_id, max(o.obs_datetime) data_estado from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs  o on e.encounter_id=o.encounter_id "
            + "where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (53,6) and "
            + "o.concept_id in(6272,6273) and o.value_coded=1706 and o.obs_datetime<=:endRevisionDate and e.location_id=:location "
            + "group by p.patient_id "
            + ") saida "
            + "inner join ( "
            + "select patient_id,max(encounter_datetime) encounter_datetime from ( "
            + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_datetime<=:endRevisionDate and "
            + "e.location_id=:location and e.encounter_type=6 "
            + "group by p.patient_id "
            + "union "
            + "Select p.patient_id,max(o.value_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs oLevantou on e.encounter_id=oLevantou.encounter_id "
            + "where  p.voided=0 and e.voided=0 and o.voided=0 and oLevantou.voided=0 and e.encounter_type=52 and o.concept_id=23866 and "
            + "o.value_datetime is not null and o.value_datetime<=:endRevisionDate and e.location_id=:location and "
            + "oLevantou.concept_id=23865 and oLevantou.value_coded=1065 "
            + "group by p.patient_id "
            + ") consultaLev "
            + "group by patient_id "
            + ") consultaOuARV on saida.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<=saida.data_estado and saida.data_estado<=:endRevisionDate ";

    public static final String findPatientsWhoArePregnantInclusionDateRF08 =
        "Select p.patient_id from 	person pe "
            + "inner join patient p on pe.person_id=p.patient_id "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs obsGravida on e.encounter_id=obsGravida.encounter_id "
            + "where pe.voided=0 and p.voided=0 and e.voided=0 and o.voided=0 and obsGravida.voided=0 and e.encounter_type=53 and e.location_id=:location and "
            + "o.concept_id=1190 and o.value_datetime is not null and "
            + "obsGravida.concept_id=1982 and obsGravida.value_coded=1065 and pe.gender='F' ";

    public static final String findPatientsWhoAreBreastfeedingInclusionDateRF09 =
        "Select p.patient_id from person pe "
            + "inner join patient p on pe.person_id=p.patient_id "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "inner join obs obsLactante on e.encounter_id=obsLactante.encounter_id "
            + "where 	pe.voided=0 and p.voided=0 and e.voided=0 and o.voided=0 and obsLactante.voided=0 and e.encounter_type=53 and e.location_id=:location and "
            + "o.concept_id=1190 and o.value_datetime is not null and "
            + "obsLactante.concept_id=6332 and obsLactante.value_coded=1065 and pe.gender='F' ";

    public static final String
        findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator =
            " SELECT FichaResumo.patient_id "
                + " FROM "
                + " ( "
                + " SELECT patient_id, min(TR_Date) as TR_Date FROM "
                + " (SELECT p.patient_id AS patient_id, o.obs_datetime As TR_Date "
                + " FROM patient p "
                + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
                + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
                + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 22772 "
                + " AND o.value_coded IN (1030,1040) "
                + " AND e.encounter_type = 53 "
                + " AND e.location_id IN (:location) "
                + " AND o.obs_datetime <= :endInclusionDate "
                + " UNION "
                + " SELECT p.patient_id AS patient_id, o.obs_datetime As TR_Date "
                + " FROM patient p "
                + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
                + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
                + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o.concept_id = 23807 "
                + " AND o.value_coded = 1065 "
                + " AND e.encounter_type = 53 "
                + " AND e.location_id IN (:location) "
                + " AND o.obs_datetime <= :endInclusionDate) Minconsult "
                + " group by patient_id "
                + " ) FichaResumo "
                + " INNER JOIN "
                + " ( "
                + " SELECT p.patient_id AS patient_id, e.encounter_datetime AS consult_date "
                + " FROM patient p "
                + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
                + " WHERE p.voided = 0 AND e.voided = 0 "
                + " AND e.encounter_type = 6 "
                + " AND e.location_id IN (:location) "
                + " AND e.encounter_datetime <= :endInclusionDate "
                + " group by p.patient_id "
                + " ) FichaClinica on FichaResumo.patient_id = FichaClinica.patient_id "
                + " AND FichaClinica.consult_date between FichaResumo.TR_Date AND date_add(FichaResumo.TR_Date, INTERVAL 7 DAY)";

    public static final String findPatientsWhoHasNutritionalAssessmentInLastConsultation =
        "select  firstClinica.patient_id  from (  "
            + "select  "
            + "p.patient_id,  "
            + "max(e.encounter_datetime) art_start_date  from patient p  "
            + "inner join encounter e on e.patient_id=p.patient_id  "
            + "where p.voided=0  "
            + "and e.voided=0  "
            + "and e.encounter_datetime between :startInclusionDate and :endRevisionDate  "
            + "and  e.location_id=:location  and e.encounter_type=6  "
            + "group by p.patient_id  "
            + ")  "
            + "firstClinica  "
            + "inner join obs obsGrau on obsGrau.person_id=firstClinica.patient_id  "
            + "where firstClinica.art_start_date=obsGrau.obs_datetime  "
            + "and obsGrau.concept_id=6336  "
            + "and obsGrau.value_coded in (6335,1115,1844,68)  and obsGrau.voided=0 ";

    public static final String
        findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod =
            " select firstClinica.patient_id "
                + " from "
                + " ( "
                + " select p.patient_id, min(e.encounter_datetime) encounter_datetime "
                + " from patient p "
                + " inner join encounter e on e.patient_id = p.patient_id "
                + " where p.voided = 0 and e.voided = 0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and "
                + " e.location_id = :location and e.encounter_type = 6 "
                + " group by p.patient_id "
                + " ) firstClinica "
                + " inner join obs obsGrau on obsGrau.person_id = firstClinica.patient_id "
                + " where firstClinica.encounter_datetime = obsGrau.obs_datetime and obsGrau.concept_id = 6336 "
                + " and obsGrau.value_coded in (68,1844) and obsGrau.voided = 0 ";

    public static final String
        findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod =
            " select firstClinica.patient_id "
                + " from "
                + " ( "
                + " select p.patient_id, min(e.encounter_datetime) encounter_datetime "
                + " from patient p "
                + " inner join encounter e on e.patient_id = p.patient_id "
                + " where p.voided = 0 and e.voided = 0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and "
                + " e.location_id = :location and e.encounter_type = 6 "
                + " group by p.patient_id "
                + " ) firstClinica "
                + " inner join obs obsGrau on obsGrau.person_id = firstClinica.patient_id "
                + " inner join obs obsApoio on obsApoio.person_id = firstClinica.patient_id "
                + " where firstClinica.encounter_datetime = obsGrau.obs_datetime and obsGrau.concept_id = 6336 and obsGrau.value_coded "
                + " in (68,1844) and obsGrau.voided = 0 and "
                + " firstClinica.encounter_datetime = obsApoio.obs_datetime and obsApoio.concept_id = 2152 and obsApoio.value_coded in "
                + " (2151,6143) and obsApoio.voided = 0 ";

    public static final String findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation =
        "select firstClinica.patient_id from ( "
            + "select p.patient_id, max(e.encounter_datetime) art_start_date from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and "
            + "e.location_id=:location and e.encounter_type=6  group by p.patient_id "
            + ") "
            + "firstClinica "
            + "inner join obs obsGrau on obsGrau.person_id=firstClinica.patient_id "
            + "where firstClinica.art_start_date=obsGrau.obs_datetime and obsGrau.concept_id=6336 and obsGrau.value_coded in (1844,68) "
            + "and obsGrau.voided=0 ";

    public static final String
        findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation =
            "select firstClinica.patient_id from  (   "
                + "select p.patient_id,min(e.encounter_datetime) encounter_datetime  from  patient p   "
                + "inner join encounter e on e.patient_id=p.patient_id  "
                + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and   "
                + "e.location_id=:location and e.encounter_type=6   "
                + "group by p.patient_id   "
                + ") firstClinica   "
                + "inner join obs obsGrau on obsGrau.person_id=firstClinica.patient_id   "
                + "inner join obs obsApoio on obsApoio.person_id=firstClinica.patient_id  "
                + "where firstClinica.encounter_datetime=obsGrau.obs_datetime and obsGrau.concept_id=6336 and obsGrau.value_coded in (68,1844) and obsGrau.voided=0 and   "
                + "firstClinica.encounter_datetime=obsApoio.obs_datetime and obsApoio.concept_id=2152 and obsApoio.value_coded in (2151,6143) and obsApoio.voided=0  ";

    public static final String
        findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6 =
            "select lastClinica.patient_id from ( "
                + "select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on e.patient_id=p.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and "
                + "e.location_id=:location and e.encounter_type=6 "
                + "group by p.patient_id "
                + ") lastClinica "
                + "inner join obs obsTBActiva on obsTBActiva.person_id=lastClinica.patient_id "
                + "where lastClinica.encounter_datetime=obsTBActiva.obs_datetime and obsTBActiva.concept_id=23761 and obsTBActiva.value_coded=1065 and obsTBActiva.voided=0 ";

    public static final String
        findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6 =
            "select lastClinica.patient_id from ( "
                + "select p.patient_id,max(e.encounter_datetime) encounter_datetime  from patient p "
                + "inner join encounter e on e.patient_id=p.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endRevisionDate and "
                + "e.location_id=:location and e.encounter_type=6 "
                + "group by p.patient_id "
                + ") lastClinica "
                + "inner join obs obsRastreio on obsRastreio.person_id=lastClinica.patient_id "
                + "where lastClinica.encounter_datetime=obsRastreio.obs_datetime and obsRastreio.concept_id=23758 and obsRastreio.value_coded in (1065,1066) and obsRastreio.voided=0 ";

    public static final String findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7 =
        "select p.patient_id  from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs obsTBActiva on obsTBActiva.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endInclusionDate and  "
            + "e.location_id=:location and e.encounter_type=6 and obsTBActiva.concept_id=23761 and obsTBActiva.value_coded=1065 and obsTBActiva.voided=0 "
            + "group by p.patient_id ";

    public static final String findPatientsWithPositiveTBScreeningInDurindPeriodCategory7 =
        "select p.patient_id from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs obsTBPositivo on obsTBPositivo.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_datetime between :startInclusionDate and :endInclusionDate and "
            + "e.location_id=:location and e.encounter_type=6 and obsTBPositivo.concept_id=23758 and obsTBPositivo.value_coded=1065 and obsTBPositivo.voided=0 "
            + "group by p.patient_id ";

    public static final String finPatientHaveTBTreatmentDuringPeriodCategory7 =
        "select p.patient_id from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs obsTB on obsTB.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTB.obs_datetime between :startInclusionDate and :endInclusionDate and  "
            + "e.location_id=:location and e.encounter_type=6 and obsTB.concept_id=1268 and obsTB.value_coded in (1256,1257,1267) and obsTB.voided=0 "
            + "group by p.patient_id ";

    public static final String findPatientWhoStartTPIDuringPeriodCategory7 =
        " SELECT DISTINCT patient_id FROM "
            + " (SELECT p.patient_id, MAX(e.encounter_datetime) FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPI ON obsTPI.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPI.voided = 0 AND e.encounter_type = 6 AND e.location_id = :location "
            + " AND obsTPI.concept_id = 6122 AND obsTPI.value_coded = 1256 AND DATE(obsTPI.obs_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPIFIM ON obsTPIFIM.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPIFIM.voided = 0 AND e.encounter_type = 53 AND e.location_id = :location "
            + " AND obsTPIFIM.concept_id = 6128 AND DATE(obsTPIFIM.value_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id) TPI ";

    public static final String findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7 =
        "select p.patient_id from patient p "
            + "inner join encounter e on e.patient_id=p.patient_id "
            + "inner join obs obsTPI on obsTPI.encounter_id=e.encounter_id "
            + "where p.voided=0 and e.voided=0 and obsTPI.obs_datetime between (:startInclusionDate + INTERVAL 4 MONTH)  and :endRevisionDate and  "
            + "e.location_id=:location and e.encounter_type=6 and obsTPI.concept_id=6122 and obsTPI.value_coded=1256 and obsTPI.voided=0 "
            + "group by p.patient_id ";

    public static final String findPatientWhoCompleteTPICategory7 =
        " select DISTINCT patient_id from( "
            + " select B4_1_2.patient_id, B4_1_2.dataInicioTPI, obsFimTPI.obs_datetime dataFimTPI, "
            + " obsTBActiva.obs_datetime dataTBActiva, obsRastreio.obs_datetime dataRastreioPositivo, obsTB.obs_datetime dataTB from  ( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS dataInicioTPI FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPI ON obsTPI.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPI.voided = 0 AND e.encounter_type = 6 AND e.location_id = :location "
            + " AND obsTPI.concept_id = 6122 AND obsTPI.value_coded = 1256 AND DATE(obsTPI.obs_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS dataInicioTPI FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPIFIM ON obsTPIFIM.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPIFIM.voided = 0 AND e.encounter_type = 53 AND e.location_id = :location "
            + " AND obsTPIFIM.concept_id = 6128 AND DATE(obsTPIFIM.value_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " ) B4_1_2 "
            + " inner join "
            + " (SELECT obs.person_id, MAX(obs_datetime) obs_datetime FROM "
            + " obs obs "
            + " INNER JOIN encounter e ON e.encounter_id = obs.encounter_id "
            + " where obs.voided = 0 and e.voided = 0 and obs.location_id = :location and obs.concept_id = 6122 and obs.value_coded = 1267 and "
            + " e.encounter_type = 6 and e.location_id = :location and "
            + " DATE(obs.obs_datetime) between :startInclusionDate AND :endRevisionDate "
            + " group by obs.person_id "
            + " UNION "
            + " SELECT obs.person_id, MAX(value_datetime) obs_datetime FROM "
            + " obs obs "
            + " INNER JOIN encounter e ON e.encounter_id = obs.encounter_id "
            + " where obs.voided = 0 and e.voided = 0 and obs.concept_id = 6129 and obs.location_id = :location and "
            + " e.encounter_type = 53 and e.location_id = :location and "
            + " DATE(obs.value_datetime) between :startInclusionDate AND :endRevisionDate "
            + " group by obs.person_id  "
            + " ) obsFimTPI on obsFimTPI.person_id = B4_1_2.patient_id "
            + " left join obs obsTBActiva on obsTBActiva.person_id= B4_1_2.patient_id and obsTBActiva.voided=0 and "
            + " obsTBActiva.concept_id = 23761 and obsTBActiva.value_coded = 1065 and "
            + " obsTBActiva.obs_datetime between B4_1_2.dataInicioTPI and (B4_1_2.dataInicioTPI + INTERVAL 9 MONTH) and obsTBActiva.location_id = :location "
            + " left join obs obsRastreio on obsRastreio.person_id = B4_1_2.patient_id and obsRastreio.voided = 0 and "
            + " obsRastreio.concept_id = 23758 and obsRastreio.value_coded = 1065 and "
            + " obsRastreio.obs_datetime between B4_1_2.dataInicioTPI and (B4_1_2.dataInicioTPI + INTERVAL 9 MONTH) and obsRastreio.location_id = :location "
            + " left join obs obsTB on obsTB.person_id = B4_1_2.patient_id and obsTB.voided = 0 and "
            + " obsTB.concept_id = 1268 and obsTB.value_coded in (1256,1257,1267) and "
            + " obsTB.obs_datetime between B4_1_2.dataInicioTPI and (B4_1_2.dataInicioTPI + INTERVAL 9 MONTH) and obsTB.location_id = :location "
            + " where "
            + " obsFimTPI.obs_datetime between (B4_1_2.dataInicioTPI + INTERVAL 170 DAY) and (B4_1_2.dataInicioTPI + INTERVAL 297 DAY) and "
            + " obsTBActiva.person_id is null and "
            + " obsRastreio.person_id is null and "
            + " obsTB.person_id is null "
            + " )finalTPI ";

    public static final String
        findPatientsOnARTWithMinimum3APSSFollowupConsultationsIntheFirst3MonthsAfterStartingARTCategory11Numerator =
            " SELECT adult.patient_id FROM ( "
                + " SELECT second_consultation.patient_id, MIN(terceira_consulta.encounter_datetime) data_terceira_consulta "
                + " FROM "
                + " ( "
                + " SELECT first_consultetion.patient_id, MIN(segunda_consulta.encounter_datetime) data_segunda_consulta "
                + " FROM "
                + " ( "
                + " SELECT art_start.patient_id, MIN(primeira_consulta.encounter_datetime) data_primeira_consulta "
                + " FROM ( "
                + " SELECT p.patient_id, MIN(value_datetime) art_start_date FROM patient p "
                + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
                + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
                + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
                + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endInclusionDate AND e.location_id = :location "
                + " GROUP BY p.patient_id "
                + " ) art_start "
                + " INNER JOIN ( "
                + " SELECT e.patient_id, e.encounter_datetime "
                + " FROM encounter e "
                + " WHERE e.voided = 0 AND encounter_type = 35 AND e.location_id = :location "
                + " ) primeira_consulta ON primeira_consulta.patient_id =  art_start.patient_id AND "
                + " (TIMESTAMPDIFF(DAY, art_start.art_start_date, primeira_consulta.encounter_datetime)) BETWEEN 20 AND 33 "
                + " WHERE art_start.art_start_date BETWEEN :startInclusionDate AND :endInclusionDate "
                + " GROUP  by art_start.patient_id "
                + " ) first_consultetion "
                + " INNER JOIN "
                + " ( "
                + " SELECT e.patient_id, e.encounter_datetime "
                + " FROM encounter e "
                + " WHERE e.voided = 0 AND encounter_type = 35 AND e.location_id = :location "
                + " ) segunda_consulta ON segunda_consulta.patient_id = first_consultetion.patient_id AND "
                + " (TIMESTAMPDIFF(DAY, first_consultetion.data_primeira_consulta, segunda_consulta.encounter_datetime)) BETWEEN 20 AND 33 "
                + " GROUP BY first_consultetion.patient_id "
                + " ) second_consultation "
                + " INNER JOIN "
                + " ( "
                + " SELECT e.patient_id, e.encounter_datetime "
                + " FROM encounter e "
                + " WHERE e.voided = 0 AND encounter_type = 35 AND e.location_id = :location "
                + " ) terceira_consulta ON terceira_consulta.patient_id = second_consultation.patient_id AND "
                + " (TIMESTAMPDIFF(DAY, second_consultation.data_segunda_consulta, terceira_consulta.encounter_datetime)) BETWEEN 20 AND 33 "
                + " GROUP BY second_consultation.patient_id "
                + " ) adult ";

    public static final String findPatientWithCVOver1000CopiesCategory11B2 =
        "select carga_viral.patient_id from ( "
            + "Select p.patient_id, min(o.obs_datetime) data_carga from patient p "
            + "inner join encounter e on p.patient_id = e.patient_id "
            + "inner join obs o on e.encounter_id=o.encounter_id "
            + "where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 6 and  o.concept_id = 856 and "
            + "o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location and o.value_numeric > 1000 "
            + "group by p.patient_id "
            + ") carga_viral ";

    public static final String findPatientsWhoHaveLastFirstLineTerapeutic =
        " select distinct firstLine.patient_id from ( "
            + " select maxLinha.patient_id, maxLinha.maxDataLinha from ( "
            + " select p.patient_id,max(o.obs_datetime) maxDataLinha from patient p "
            + " join encounter e on p.patient_id=e.patient_id "
            + " join obs o on o.encounter_id=e.encounter_id "
            + " where e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 "
            + " and o.concept_id=21151 and e.location_id=:location "
            + " and o.obs_datetime between :startInclusionDate and :endInclusionDate "
            + " group by p.patient_id "
            + " ) maxLinha "
            + " inner join encounter e on e.patient_id = maxLinha.patient_id "
            + " inner join obs on obs.person_id=maxLinha.patient_id and maxLinha.maxDataLinha=obs.obs_datetime "
            + " where obs.concept_id=21151 and obs.value_coded=21150 and obs.voided=0 and obs.location_id=:location "
            + " and e.voided = 0 and e.encounter_type = 6 and e.location_id = :location "
            + ") firstLine ";

    public static final String
        findPatientsOnThe1stLineOfRTWithCVOver1000CopiesWhoHad3ConsecutiveMonthlyAPSSConsultationsCategory11Numerator =
            "select carga_viral.patient_id from ( "
                + "Select p.patient_id, min(o.obs_datetime) data_carga from patient p "
                + "inner join encounter e on p.patient_id = e.patient_id "
                + "inner join obs o on e.encounter_id=o.encounter_id "
                + "where p.voided = 0 and e.voided = 0 and o.voided = 0 and e.encounter_type = 6 and  o.concept_id = 856 and "
                + "o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location and o.value_numeric > 1000 "
                + "group by p.patient_id "
                + ") carga_viral "
                + "inner join encounter primeira_consulta on carga_viral.patient_id = primeira_consulta.patient_id and primeira_consulta.voided = 0 and primeira_consulta.encounter_type = 35 and "
                + "primeira_consulta.encounter_datetime=carga_viral.data_carga "
                + "inner join encounter segunda_consulta on carga_viral.patient_id = segunda_consulta.patient_id and segunda_consulta.voided = 0 and segunda_consulta.encounter_type = 35 "
                + "and (TIMESTAMPDIFF(DAY, carga_viral.data_carga, segunda_consulta.encounter_datetime)) between 20 and 33 "
                + "inner join encounter terceira_consulta on carga_viral.patient_id = terceira_consulta.patient_id and terceira_consulta.voided = 0 and terceira_consulta.encounter_type = 35 "
                + "and (TIMESTAMPDIFF(DAY, segunda_consulta.encounter_datetime, terceira_consulta.encounter_datetime)) between 20 and 33 ";

    public static final String
        findFirstPatientChildrenAPSSConsultationWithinInclusionReportingPeriod =
            "select min_consultation.patient_id "
                + "from (                                                                                                 		"
                + "	select patient_id, min(art_start_date) art_start_date                                                 		"
                + "	from (                                                                                                		"
                + "		select p.patient_id, min(value_datetime) art_start_date                                            		"
                + " 		from patient p                                                                                 		"
                + " 			join encounter e on p.patient_id=e.patient_id                                              		"
                + " 			join obs o on e.encounter_id=o.encounter_id                                                		"
                + " 		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=53                         		"
                + " 			and o.concept_id=1190 and o.value_datetime is not null                                     		"
                + "			and o.value_datetime<=:endInclusionDate and e.location_id=:location                        			"
                + " 		group by p.patient_id                                                                          		"
                + " 	) art_start                                                                                       		"
                + " 	group by patient_id) tx_new                                                                       		"
                + " 	join                                                                                              		"
                + " 	(select p.patient_id, min(e.encounter_datetime) min_consultation_date                                   "
                + " 	from patient p                                                                                   		"
                + "	 	join encounter e on e.patient_id = p.patient_id                             							"
                + "	where p.voided=0 and e.voided=0 and e.encounter_type = 35         								  			"
                + "	 	and e.encounter_datetime between :startInclusionDate and :endInclusionDate and e.location_id=:location 	"
                + "	group by p.patient_id                                                            				  			"
                + "	) min_consultation on min_consultation.patient_id = tx_new.patient_id	                          			"
                + "	join person pe on pe.person_id= tx_new.patient_id                                                 			"
                + "where  tx_new.art_start_date between :startInclusionDate and :endInclusionDate and tx_new.art_start_date < min_consultation.min_consultation_date";

    public static final String
        findPatientsWhithCD4RegistredInClinicalConsultationUnder33DaysFromTheFirstClinicalConsultation =
            "select firstClinica.patient_id  from  ( "
                + "select p.patient_id,min(e.encounter_datetime) encounter_datetime  from  patient p "
                + "inner join encounter e on e.patient_id=p.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_datetime <= :endRevisionDate and "
                + "e.location_id=:location and e.encounter_type=6 "
                + "group by p.patient_id "
                + ") firstClinica "
                + "inner join obs obsCD4 on obsCD4.person_id=firstClinica.patient_id "
                + "where obsCD4.obs_datetime > firstClinica.encounter_datetime and obsCD4.obs_datetime <=  DATE_ADD(firstClinica.encounter_datetime, INTERVAL 33 DAY) and obsCD4.concept_id=1695 and obsCD4.value_numeric is not null and obsCD4.voided=0 "
                + "and obsCD4.location_id = :location ";

    public static final String findPatientsAgeRange =
        "SELECT patient_id FROM patient "
            + "INNER JOIN person ON patient_id = person_id WHERE patient.voided=0 AND person.voided=0 "
            + "AND TIMESTAMPDIFF(year,birthdate,:endInclusionDate) BETWEEN %d AND %d AND birthdate IS NOT NULL";

    public static final String findPatientsBiggerThan =
        "SELECT patient_id FROM patient "
            + "INNER JOIN person ON patient_id = person_id WHERE patient.voided=0 AND person.voided=0 "
            + "AND TIMESTAMPDIFF(year,birthdate,:endInclusionDate) > %d AND birthdate IS NOT NULL";

    public static final String findPatientsLessThan =
        "SELECT patient_id FROM patient "
            + "INNER JOIN person ON patient_id = person_id WHERE patient.voided=0 AND person.voided=0 "
            + "AND TIMESTAMPDIFF(year,birthdate,:endInclusionDate) <  %d AND birthdate IS NOT NULL";

    public static final String findPatientHaveTBACTIVEAndTPIDuringPeriodCategory7AsH =
        " select DISTINCT B4.patient_id FROM "
            + " (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPI ON obsTPI.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPI.voided = 0 AND e.encounter_type = 6 AND e.location_id = :location "
            + " AND obsTPI.concept_id = 6122 AND obsTPI.value_coded = 1256 AND DATE(obsTPI.obs_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPIFIM ON obsTPIFIM.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPIFIM.voided = 0 AND e.encounter_type = 53 AND e.location_id = :location "
            + " AND obsTPIFIM.concept_id = 6128 AND DATE(obsTPIFIM.value_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id) AS B4 "
            + " INNER JOIN encounter e ON e.patient_id = B4.patient_id "
            + " INNER JOIN obs obsTBActiva ON obsTBActiva.encounter_id = e.encounter_id "
            + " WHERE e.voided = 0 AND e.location_id = :location AND obsTBActiva.voided = 0 AND "
            + " e.encounter_datetime BETWEEN B4.encounter_datetime AND (B4.encounter_datetime + INTERVAL 9 MONTH) AND "
            + " e.encounter_type = 6 AND obsTBActiva.concept_id = 23761 AND obsTBActiva.value_coded = 1065 ";

    public static final String findPatientHaveTBSCREENINGAndTPIDuringPeriodCategory7AsI =
        " select DISTINCT B4.patient_id FROM "
            + " (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPI ON obsTPI.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPI.voided = 0 AND e.encounter_type = 6 AND e.location_id = :location "
            + " AND obsTPI.concept_id = 6122 AND obsTPI.value_coded = 1256 AND DATE(obsTPI.obs_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPIFIM ON obsTPIFIM.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPIFIM.voided = 0 AND e.encounter_type = 53 AND e.location_id = :location "
            + " AND obsTPIFIM.concept_id = 6128 AND DATE(obsTPIFIM.value_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id) AS B4 "
            + " INNER JOIN encounter e ON e.patient_id = B4.patient_id "
            + " INNER JOIN obs obsTBPositivo ON obsTBPositivo.encounter_id = e.encounter_id "
            + " WHERE e.voided = 0 AND obsTBPositivo.voided = 0 AND e.encounter_datetime BETWEEN B4.encounter_datetime AND (B4.encounter_datetime + INTERVAL 9 MONTH) AND "
            + " e.location_id = :location AND e.encounter_type = 6 AND obsTBPositivo.concept_id = 23758 AND obsTBPositivo.value_coded = 1065 ";

    public static final String findPatientHaveTBTREATMENTAndTPIDuringPeriodCategory7AsJ =
        " select DISTINCT B4.patient_id FROM "
            + " (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPI ON obsTPI.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPI.voided = 0 AND e.encounter_type = 6 AND e.location_id = :location "
            + " AND obsTPI.concept_id = 6122 AND obsTPI.value_coded = 1256 AND DATE(obsTPI.obs_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs obsTPIFIM ON obsTPIFIM.encounter_id = e.encounter_id "
            + " WHERE p.voided = 0 AND e.voided = 0 AND obsTPIFIM.voided = 0 AND e.encounter_type = 53 AND e.location_id = :location "
            + " AND obsTPIFIM.concept_id = 6128 AND DATE(obsTPIFIM.value_datetime) BETWEEN :startInclusionDate AND :endInclusionDate "
            + " GROUP BY p.patient_id) AS B4 "
            + " INNER JOIN encounter e ON e.patient_id = B4.patient_id "
            + " INNER JOIN obs obsTB ON obsTB.encounter_id = e.encounter_id "
            + " WHERE e.voided = 0 AND e.location_id = :location  AND obsTB.voided = 0 AND "
            + " obsTB.obs_datetime BETWEEN B4.encounter_datetime AND (B4.encounter_datetime + INTERVAL 9 MONTH) AND "
            + " e.encounter_type = 6 AND obsTB.concept_id = 1268 AND obsTB.value_coded IN (1256,1257,1267) ";
  }
}
