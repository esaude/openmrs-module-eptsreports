package org.openmrs.module.eptsreports.reporting.library.queries.mq;

public interface MQCategory13P3QueriesInterface {

  class QUERY {

    public static final String
        findPatientsWhoAreInAlternativeLineFirstLineCategory13_3_BI1_Denominator =
            "Select p.patient_id from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=53 and obsLinha.voided=0 and "
                + "obsLinha.obs_datetime BETWEEN :startInclusionDate and :endInclusionDate  and e.location_id=:location and "
                + "obsLinha.concept_id=21190 "
                + "group by p.patient_id ";

    public static final String
        findPatientsWhoHasTherapeuthicLineDiferentThanFirstLineFromConsultationClinicalCategory13_3_B1E_Denominator =
            "select maxAlternativa.patient_id from ( "
                + "Select p.patient_id,max(obsLinha.obs_datetime) ultimaAlternativa from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=53 and obsLinha.voided=0 and "
                + "obsLinha.obs_datetime BETWEEN :startInclusionDate and :endInclusionDate  and e.location_id=:location and "
                + "obsLinha.concept_id=21190 "
                + "group by p.patient_id "
                + ") maxAlternativa "
                + "inner join encounter e on e.patient_id=maxAlternativa.patient_id "
                + "inner join obs o on o.encounter_id=e.encounter_id "
                + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and "
                + "e.encounter_datetime> maxAlternativa.ultimaAlternativa and e.encounter_datetime<=:endRevisionDate and "
                + "o.concept_id=21151 and o.value_coded <> 21150 and e.location_id=:location ";

    public static final String
        findPatientsWhoAlternativeLineFirstLineExcludePatintsFromClinicalConsultationWithTherapheuticLineDiferentFirstLineCategory13_3_Denominador_B1 =
            "select patient_id from( "
                + "select alternativa.patient_id,alternativa.data_linha23898,obsOutraLinha.obs_datetime dataOutraLinha "
                + "from "
                + "( "
                + "	Select 	p.patient_id,max(obsLinha.obs_datetime) data_linha23898 "
                + "	from 	patient p "
                + "			inner join encounter e on p.patient_id=e.patient_id "
                + "			inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "	where 	p.voided=0 and e.voided=0 and e.encounter_type=53 and obsLinha.concept_id=21190 and obsLinha.voided=0 and "
                + "			obsLinha.obs_datetime BETWEEN :startInclusionDate and :endInclusionDate  and e.location_id=:location "
                + "	group by p.patient_id "
                + ") alternativa "
                + "left join encounter enc on enc.patient_id=alternativa.patient_id and enc.voided=0 and enc.encounter_type=6 "
                + "left join obs obsOutraLinha on obsOutraLinha.encounter_id=enc.encounter_id and obsOutraLinha.voided=0 and obsOutraLinha.concept_id=21151 and obsOutraLinha.value_coded<>21150 and "
                + "	obsOutraLinha.obs_datetime > data_linha23898 and obsOutraLinha.obs_datetime<=:endRevisionDate and obsOutraLinha.location_id=:location "
                + "where obsOutraLinha.person_id is null "
                + ") final group by final.patient_id ";

    public static final String
        findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2Denominator =
            "select patient_id from ( "
                + " "
                + "select max_tipo_despensa.* "
                + "from "
                + "( "
                + "	select max_tl.patient_id, max_tl.max_datatl "
                + "	from "
                + "	( "
                + "		select p.patient_id,max(o.obs_datetime) max_datatl "
                + "		from patient p "
                + "			join encounter e on p.patient_id=e.patient_id "
                + "			join obs o on o.encounter_id=e.encounter_id "
                + "		where e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 "
                + "			and o.concept_id=21151 and e.location_id=:location "
                + "			and o.obs_datetime between :startInclusionDate and :endInclusionDate "
                + "		group by p.patient_id "
                + "	) max_tl "
                + "	join obs on obs.person_id=max_tl.patient_id and max_tl.max_datatl=obs.obs_datetime "
                + "	where obs.concept_id=21151 and obs.value_coded=21148 and obs.voided=0 and obs.location_id=:location "
                + ") max_tipo_despensa "
                + "left join obs pedidoCV on pedidoCV.person_id=max_tipo_despensa.patient_id and pedidoCV.voided=0 and pedidoCV.concept_id=23722 and pedidoCV.value_coded=856 and "
                + "	pedidoCV.obs_datetime  >=date_add(:startInclusionDate, interval -3 MONTH) and pedidoCV.obs_datetime<=:startInclusionDate and pedidoCV.location_id=:location "
                + "where pedidoCV.person_id is null "
                + ") final "
                + "group by final.patient_id ";

    public static final String
        findAllPatientsWhoHaveLaboratoryInvestigationsRequestsAndViralChargeFromConsultaionClinicalCategory13_3_B2E_Denominator =
            "Select patient_id from ( "
                + "Select maxEnc.patient_id,max(obsLinha.obs_datetime) from ( "
                + "Select p.patient_id,max(e.encounter_datetime) data_linha from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime BETWEEN date_add(:startInclusionDate', interval -3 MONTH) AND  :startInclusionDate'  and e.location_id=:location "
                + "group by p.patient_id "
                + ") maxEnc "
                + "inner join encounter  e on e.patient_id=maxEnc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=23722 and obsLinha.value_coded = 856 and obsLinha.voided=0 and e.voided=0 "
                + "group by maxEnc.patient_id "
                + ") final "
                + "group by final.patient_id ";

    public static final String
        findAllPatientsWhoHaveClinicalConsultationWithViralChargeBetweenSixAndNineMonthsAfterARTStartDateCategory13_3_G_Numerator =
            "select final.patient_id from ( "
                + "select tx_new.patient_id,tx_new.art_start_date as art_start_date from ( "
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
                + "inner join encounter e on e.patient_id=tx_new.patient_id "
                + "inner join obs o on o.encounter_id=e.encounter_id "
                + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and "
                + "e.encounter_datetime between date_add(tx_new.art_start_date, interval +6 MONTH) AND  date_add(tx_new.art_start_date, interval +9 MONTH) and "
                + "o.concept_id in (856,1305) and e.location_id=:location "
                + ") final";

    public static final String
        findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenAlternativeFirstLineDateCategory13_3_H_Numerator =
            "select maxAlternativa.patient_id from ( "
                + "Select p.patient_id,max(obsLinha.obs_datetime) ultimaAlternativa from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=53 and obsLinha.voided=0 and "
                + "obsLinha.obs_datetime BETWEEN :startInclusionDate and :endInclusionDate  and e.location_id=:location and obsLinha.concept_id=21190 "
                + "group by p.patient_id "
                + ") maxAlternativa "
                + "inner join encounter e on e.patient_id=maxAlternativa.patient_id "
                + "inner join obs o on o.encounter_id=e.encounter_id "
                + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and "
                + "e.encounter_datetime between date_add(maxAlternativa.ultimaAlternativa, interval +6 MONTH) AND  date_add(maxAlternativa.ultimaAlternativa, interval +9 MONTH) and "
                + "o.concept_id in (856,1305) and e.location_id=:location ";

    public static final String
        findAllPatientsWhoHaveClinicalConsultationAndEncounterDateTimeBetweenSecondTherapheuticLineDateCategory13_3_I_Numerator =
            "select max_tipo_despensa.patient_id from ( "
                + "select max_tl.patient_id, max_tl.max_datatl from ( "
                + "select p.patient_id,max(o.obs_datetime) max_datatl from patient p "
                + "join encounter e on p.patient_id=e.patient_id "
                + "join obs o on o.encounter_id=e.encounter_id "
                + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and p.voided=0 "
                + "and o.concept_id=21151 and e.location_id=:location "
                + "and o.obs_datetime between :startInclusionDate and :endInclusionDate "
                + "group by p.patient_id "
                + ") max_tl "
                + "join obs on obs.person_id=max_tl.patient_id and max_tl.max_datatl=obs.obs_datetime "
                + "where obs.concept_id=21151 and obs.value_coded=21148 and obs.voided=0 and obs.location_id=:location "
                + ") max_tipo_despensa "
                + "inner join encounter e on e.patient_id=max_tipo_despensa.patient_id "
                + "inner join obs o on o.encounter_id=e.encounter_id "
                + "where e.encounter_type=6 and e.voided=0 and o.voided=0 and "
                + "e.encounter_datetime between date_add(max_tipo_despensa.max_datatl, interval +6 MONTH) AND  date_add(max_tipo_despensa.max_datatl, interval +9 MONTH) and "
                + "o.concept_id in (856,1305) and e.location_id=:location ";
  }
}
