package org.openmrs.module.eptsreports.reporting.library.queries;

public interface QualityImprovementQueriesInterfaceCategory13Section1 {

  class QUERY {

    public static final String findPatientsWithLastClinicalConsultationDenominatorB1 =
        "Select maxEnc.patient_id from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
            + "group by p.patient_id "
            + ")maxEnc";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreInFistLineDenominatorB2 =
            "Select final.patient_id from ( "
                + "Select firstLine.patient_id,firstLine.dataLinha, firstLine.ultimaConsulta from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + " inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id	"
                + "where obsLinha.concept_id=21151 and e.encounter_type=6 and obsLinha.value_coded=21150 "
                + "and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") firstLine "
                + "where (TIMESTAMPDIFF(MONTH,firstLine.dataLinha,firstLine.ultimaConsulta)) >= 6 "
                + ") final ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreNotInFistLineDenominatorB2E =
            "Select final.patient_id from ("
                + "Select firstLine.patient_id,firstLine.dataLinha, firstLine.ultimaConsulta,obsDiferenteLinha.obs_datetime dataLinhaDiferente from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21151 and e.encounter_type=6 and obsLinha.value_coded=21150 "
                + "and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") firstLine "
                + "left join obs obsDiferenteLinha on firstLine.patient_id=obsDiferenteLinha.person_id  "
                + "and obsDiferenteLinha.voided=0 and obsDiferenteLinha.concept_id=21151  "
                + "and obsDiferenteLinha.value_coded<>21150  "
                + "and obsDiferenteLinha.obs_datetime>firstLine.dataLinha  "
                + "and obsDiferenteLinha.obs_datetime<=firstLine.ultimaConsulta  "
                + "and obsDiferenteLinha.location_id=:location "
                + "where (TIMESTAMPDIFF(MONTH,firstLine.dataLinha,firstLine.ultimaConsulta)) >= 6  "
                + "and obsDiferenteLinha.obs_datetime is null "
                + ") final ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreInLinhaAlternativaDenominatorB3 =
            "Select final.patient_id from ( "
                + "Select alternativeLine.patient_id,alternativeLine.dataLinha, alternativeLine.ultimaConsulta from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=23898 and e.encounter_type=53 and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") alternativeLine "
                + "where (TIMESTAMPDIFF(MONTH,alternativeLine.dataLinha,alternativeLine.ultimaConsulta)) >= 6 "
                + ") final ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreDiferentFirstLineLinhaAternativaDenominatorB3E =
            "Select final.patient_id from ( "
                + "Select diferenteDePrimeiraLinha.patient_id,diferenteDePrimeiraLinha.dataLinha, diferenteDePrimeiraLinha.ultimaConsulta,obsLinhaAlternativa.obs_datetime dataLinhaAlternativa from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21151 and e.encounter_type=6 and obsLinha.value_coded<>21150 "
                + "and obsLinha.voided=0 and e.voided=0 and e.encounter_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") diferenteDePrimeiraLinha "
                + "left join obs obsLinhaAlternativa on diferenteDePrimeiraLinha.patient_id=obsLinhaAlternativa.person_id  "
                + "and obsLinhaAlternativa.voided=0 and obsLinhaAlternativa.concept_id=23898 "
                + "and diferenteDePrimeiraLinha.dataLinha>obsLinhaAlternativa.obs_datetime  "
                + "and diferenteDePrimeiraLinha.dataLinha<=diferenteDePrimeiraLinha.ultimaConsulta  "
                + "and obsLinhaAlternativa.location_id=:location "
                + "where (TIMESTAMPDIFF(MONTH,diferenteDePrimeiraLinha.dataLinha,diferenteDePrimeiraLinha.ultimaConsulta)) >= 6 and obsLinhaAlternativa.obs_datetime is null "
                + ") final";

    public static final String findPatientsWithCVDenominatorB4E =
        "Select final.patient_id from ( "
            + "Select cv.patient_id,enc.encounter_datetime ultimaConsulta,min(cv.dataCV) data_cv from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate   and e.location_id=:location "
            + "group by p.patient_id "
            + ") enc "
            + "left join "
            + "( "
            + "Select p.patient_id,e.encounter_datetime as dataCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs obscv on obscv.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and obscv.concept_id = 856 and obscv.value_numeric is not null  "
            + "and e.encounter_type=6 and obscv.voided=0 and e.location_id=:location "
            + "group by p.patient_id "
            + "union "
            + "Select p.patient_id,obscv.obs_datetime as dataCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs obscv on obscv.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and obscv.concept_id=1305 and  obscv.value_coded is not null and e.encounter_type =53 and obscv.voided=0 and e.location_id=:location "
            + "group by p.patient_id "
            + ") cv on cv.patient_id=enc.patient_id "
            + "where cv.dataCV BETWEEN date_add(enc.encounter_datetime, interval -12 MONTH) and enc.encounter_datetime "
            + "group by patient_id "
            + ") final ";

    public static final String findPatientsWithRequestCVDenominatorB5E =
        "Select final.patient_id from ( "
            + "Select cvPedido.patient_id,enc.encounter_datetime ultimaConsulta,min(cvPedido.dataPedidoCV) dataPedidoCV from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate   and e.location_id=:location "
            + "group by p.patient_id "
            + ") enc left join ( "
            + "Select p.patient_id,o.obs_datetime as dataPedidoCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and o.concept_id = 23722 and o.value_coded=856 and e.encounter_type=6 and o.voided=0 and e.location_id=:location "
            + "group by p.patient_id "
            + ") cvPedido on cvPedido.patient_id=enc.patient_id "
            + "where cvPedido.dataPedidoCV BETWEEN date_add(enc.encounter_datetime, interval -3 MONTH) and enc.encounter_datetime "
            + "group by patient_id "
            + ") final";

    public static final String findNumeratorC =
        "Select final.patient_id from ( "
            + "Select cvPedido.patient_id,enc.encounter_datetime ultimaConsulta,max(cvPedido.dataPedidoCV) dataPedidoCV from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >:endInclusionDate and e.encounter_datetime<=:endRevisionDate   and e.location_id=:location "
            + "group by p.patient_id "
            + ") enc "
            + "inner join ( "
            + "Select p.patient_id,o.obs_datetime as dataPedidoCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id "
            + "where  e.voided=0 and o.concept_id = 23722 and o.value_coded=856 and e.encounter_type=6 and o.voided=0 and e.location_id=:location "
            + "group by p.patient_id "
            + ") cvPedido on cvPedido.patient_id=enc.patient_id "
            + "group by patient_id "
            + ") final ";
  }
}
