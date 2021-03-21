package org.openmrs.module.eptsreports.reporting.library.queries.mq;

public interface MQCategory13Section2QueriesInterface {

  class QUERY {

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreInSecondLineDenominatorB2 =
            "Select final.patient_id from ( "
                + "Select secondLine.patient_id,secondLine.dataLinha, secondLine.ultimaConsulta from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from 	patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where 	p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21151 and e.encounter_type=6 and obsLinha.value_coded=21148 "
                + "and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") secondLine "
                + "where (TIMESTAMPDIFF(MONTH,secondLine.dataLinha,secondLine.ultimaConsulta)) >= 6 "
                + ") final";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreNotInSecondLineDenominatorB2E =
            "Select final.patient_id from ( "
                + "Select secondLine.patient_id,secondLine.dataLinha, secondLine.ultimaConsulta,obsDiferenteLinha.obs_datetime dataLinhaDiferente from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where 	p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21151 and e.encounter_type=6 and obsLinha.value_coded=21148 and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") secondLine "
                + "left join obs obsDiferenteLinha on secondLine.patient_id=obsDiferenteLinha.person_id  "
                + "and obsDiferenteLinha.voided=0 and obsDiferenteLinha.concept_id=21151  "
                + "and obsDiferenteLinha.value_coded<>21148  "
                + "and obsDiferenteLinha.obs_datetime>secondLine.dataLinha  "
                + "and obsDiferenteLinha.obs_datetime<=secondLine.ultimaConsulta  "
                + "and obsDiferenteLinha.location_id=:location "
                + "where (TIMESTAMPDIFF(MONTH,secondLine.dataLinha,secondLine.ultimaConsulta)) >= 6  "
                + "and obsDiferenteLinha.obs_datetime is null "
                + ") final ";
  }
}
