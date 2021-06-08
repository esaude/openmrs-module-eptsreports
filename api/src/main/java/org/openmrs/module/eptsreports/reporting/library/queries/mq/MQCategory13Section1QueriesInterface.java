package org.openmrs.module.eptsreports.reporting.library.queries.mq;

public interface MQCategory13Section1QueriesInterface {

  class QUERY {

    public static final String findPatientsWithLastClinicalConsultationDenominatorB1 =
        "Select maxEnc.patient_id from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
            + "group by p.patient_id "
            + ")maxEnc";

    public static final String findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
        int startAge, int endAge) {

      final String sql =
          "Select final.patient_id  from ( "
              + "Select maxEnc.patient_id,maxEnc.encounter_datetime from ( "
              + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
              + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
              + "group by p.patient_id "
              + ")maxEnc "
              + "INNER JOIN person pe ON maxEnc.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,maxEnc.encounter_datetime)) >= %s AND (TIMESTAMPDIFF(year,birthdate,maxEnc.encounter_datetime)) <= %s AND birthdate IS NOT NULL and pe.voided = 0 "
              + " ) final ";

      return String.format(sql, startAge, endAge);
    }

    public static final String
        findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus(int startAge) {

      final String sql =
          "Select final.patient_id from ( "
              + "Select maxEnc.patient_id,maxEnc.encounter_datetime from ( "
              + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
              + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
              + "group by p.patient_id "
              + ")maxEnc "
              + "INNER JOIN person pe ON maxEnc.patient_id=pe.person_id "
              + "WHERE (TIMESTAMPDIFF(year,birthdate,maxEnc.encounter_datetime)) >= %s AND birthdate IS NOT NULL and pe.voided = 0 "
              + ") final";

      return String.format(sql, startAge);
    }

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreInFistLineDenominatorB2 =
            "Select final.patient_id from ( "
                + "Select firstLine.patient_id,firstLine.dataLinha, firstLine.ultimaConsulta from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,min(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
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
                + "e.encounter_datetime >:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
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
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,max(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21190 and e.encounter_type=53 and obsLinha.voided=0 and e.voided=0 and obsLinha.obs_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") alternativeLine "
                + "where (TIMESTAMPDIFF(MONTH,alternativeLine.dataLinha,alternativeLine.ultimaConsulta)) >= 6 "
                + ") final ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreDiferentFirstLineLinhaAternativaDenominatorB3E =
            " Select linhaAlternativa.patient_id from ( "
                + "Select enc.patient_id,enc.encounter_datetime ultimaConsulta,max(obsLinha.obs_datetime) dataLinha from ( "
                + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
                + "inner join encounter e on p.patient_id=e.patient_id "
                + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
                + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate  and e.location_id=:location "
                + "group by p.patient_id "
                + ") enc "
                + "inner join encounter  e on e.patient_id=enc.patient_id "
                + "inner join obs obsLinha on obsLinha.encounter_id=e.encounter_id "
                + "where obsLinha.concept_id=21190 and e.encounter_type=53 "
                + "and obsLinha.voided=0 and e.voided=0 and obsLinha.obs_datetime<enc.encounter_datetime and e.location_id=:location "
                + "group by enc.patient_id "
                + ") linhaAlternativa "
                + " inner join encounter e_ on e_.patient_id = linhaAlternativa.patient_id and e_.voided = 0 and e_.encounter_type = 6 and e_.location_id = :location "
                + " inner join obs obsDiferenteLinha on e_.encounter_id = obsDiferenteLinha.encounter_id "
                + "and obsDiferenteLinha.voided=0 and obsDiferenteLinha.concept_id=21151 and obsDiferenteLinha.value_coded<>21150 "
                + "and obsDiferenteLinha.obs_datetime>linhaAlternativa.dataLinha "
                + "and obsDiferenteLinha.obs_datetime<=linhaAlternativa.ultimaConsulta "
                + "and obsDiferenteLinha.location_id=:location "
                + "where (TIMESTAMPDIFF(MONTH,linhaAlternativa.dataLinha,linhaAlternativa.ultimaConsulta)) >= 6 and obsDiferenteLinha.obs_datetime is not null group by linhaAlternativa.patient_id";

    public static final String findPatientsWithCVDenominatorB4E =
        "Select final.patient_id from ( "
            + "Select cv.patient_id,enc.encounter_datetime ultimaConsulta,min(cv.dataCV) data_cv from ( "
            + "Select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "inner join encounter e on p.patient_id=e.patient_id "
            + "where p.voided=0 and e.voided=0 and e.encounter_type=6 and "
            + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate   and e.location_id=:location "
            + "group by p.patient_id "
            + ") enc "
            + "inner join "
            + "( "
            + "Select p.patient_id,e.encounter_datetime as dataCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs obscv on obscv.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and obscv.concept_id in(856,1305) and e.encounter_datetime <=:endRevisionDate "
            + "and e.encounter_type=6 and obscv.voided=0 and e.location_id=:location "
            + "union "
            + "Select p.patient_id,obscv.obs_datetime as dataCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs obscv on obscv.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and obscv.concept_id in(856,1305) and obscv.obs_datetime<=:endRevisionDate and e.encounter_type =53 and obscv.voided=0 and e.location_id=:location "
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
            + "e.encounter_datetime >=:startInclusionDate and e.encounter_datetime<=:endRevisionDate   and e.location_id=:location "
            + "group by p.patient_id "
            + ") enc inner join ( "
            + "Select p.patient_id,o.obs_datetime as dataPedidoCV from patient p "
            + "inner join encounter  e on e.patient_id=p.patient_id "
            + "inner join obs o on o.encounter_id=e.encounter_id	 "
            + "where  e.voided=0 and o.concept_id = 23722 and o.value_coded=856 and e.encounter_type=6 and o.voided=0 and e.location_id=:location "
            + ") cvPedido on cvPedido.patient_id=enc.patient_id "
            + "where cvPedido.dataPedidoCV >= date_add(enc.encounter_datetime, interval -3 MONTH) and cvPedido.dataPedidoCV < enc.encounter_datetime "
            + "group by patient_id "
            + ") final";

    public static final String findNumeratorC =
        " Select enc.patient_id from ( "
            + " Select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 6 and "
            + " e.encounter_datetime >= :startInclusionDate and e.encounter_datetime <= :endRevisionDate and e.location_id = :location "
            + " group by p.patient_id "
            + " ) enc "
            + " inner join encounter e_ on e_.patient_id = enc.patient_id "
            + " inner join obs cvPedido on cvPedido.encounter_id = e_.encounter_id "
            + " where cvPedido.obs_datetime = enc.encounter_datetime and cvPedido.concept_id = 23722 and cvPedido.value_coded = 856 and cvPedido.voided = 0 "
            + " and e_.voided = 0 and e_.encounter_type = 6 and e_.location_id = :location and cvPedido.location_id = :location ";

    public static final String findPatientsWhoArePregnantCAT13Part1 =
        " select ultima_consulta.patient_id from ( "
            + " Select p.patient_id, max(e.encounter_datetime) as data_ultima_consulta from "
            + " patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 6 and e.location_id = :location "
            + " and e.encounter_datetime BETWEEN :startInclusionDate AND :endRevisionDate "
            + " group by p.patient_id "
            + " ) ultima_consulta "
            + " inner join encounter e_ on e_.patient_id = ultima_consulta.patient_id "
            + " inner join obs obsGravida on e_.encounter_id = obsGravida.encounter_id "
            + " inner join person pe on pe.person_id = e_.patient_id "
            + " where pe.voided = 0 and e_.voided = 0 and obsGravida.voided = 0 and e_.encounter_type = 6 and e_.location_id = :location "
            + " and obsGravida.concept_id = 1982 and obsGravida.value_coded = 1065 and pe.gender = 'F' "
            + " and e_.encounter_datetime = ultima_consulta.data_ultima_consulta ";

    public static final String findPatientsWhoAreBreastfeedingCAT13Part1 =
        " select ultima_consulta.patient_id from ( "
            + " Select p.patient_id, max(e.encounter_datetime) as data_ultima_consulta from "
            + " patient p "
            + " inner join encounter e on p.patient_id = e.patient_id "
            + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 6 and e.location_id = :location "
            + " and e.encounter_datetime BETWEEN :startInclusionDate AND :endRevisionDate "
            + " group by p.patient_id "
            + " ) ultima_consulta "
            + " inner join encounter e_ on e_.patient_id = ultima_consulta.patient_id "
            + " inner join obs obsGravida on e_.encounter_id = obsGravida.encounter_id "
            + " inner join person pe on pe.person_id = e_.patient_id "
            + " where pe.voided = 0 and e_.voided = 0 and obsGravida.voided = 0 and e_.encounter_type = 6 and e_.location_id = :location "
            + " and obsGravida.concept_id = 6332 and obsGravida.value_coded = 1065 and pe.gender = 'F' "
            + " and e_.encounter_datetime = ultima_consulta.data_ultima_consulta ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreNotInFistLineDenominatorB2NEW =
            " select art_start.patient_id "
                + " from "
                + " ( "
                + " SELECT p.patient_id, MIN(value_datetime) art_start_date "
                + " FROM patient p "
                + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
                + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
                + " WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type = 53 "
                + " AND o.concept_id = 1190 AND o.value_datetime is NOT NULL AND o.value_datetime <= :endRevisionDate "
                + " AND e.location_id = :location "
                + " GROUP BY p.patient_id  "
                + " ) art_start "
                + " inner join "
                + " ( "
                + " select maxLinha.patient_id, maxLinha.maxDataLinha "
                + " from ( "
                + " select p.patient_id, max(e.encounter_datetime) maxDataLinha "
                + " from patient p "
                + " join encounter e on p.patient_id=e.patient_id "
                + " where e.encounter_type = 6 and e.voided = 0 and p.voided = 0 "
                + " and e.encounter_datetime between :startInclusionDate and :endRevisionDate "
                + " group by p.patient_id  "
                + " ) maxLinha "
                + " join encounter e on e.patient_id = maxLinha.patient_id and maxLinha.maxDataLinha = e.encounter_datetime and e.encounter_type = 6 "
                + " join obs on obs.encounter_id = e.encounter_id "
                + " where obs.concept_id = 21151 and obs.value_coded = 21150 and maxLinha.maxDataLinha = e.encounter_datetime "
                + " and obs.voided = 0 and obs.location_id = :location and e.location_id = :location "
                + " and e.voided = 0 and e.encounter_type = 6 "
                + " ) primeiraLinha on art_start.patient_id = primeiraLinha.patient_id "
                + " where primeiraLinha.maxDataLinha >= date_add(art_start.art_start_date, INTERVAL 6 Month) ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreNotInFistLineDenominatorB2NEWPartII =
            " select maxEnc.patient_id "
                + " from "
                + " ( "
                + " Select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p "
                + " inner join encounter e on p.patient_id = e.patient_id "
                + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 6 and "
                + " e.encounter_datetime >= :startInclusionDate and e.encounter_datetime <= :endRevisionDate  and e.location_id = :location "
                + " group by p.patient_id  "
                + " )maxEnc "
                + " inner join "
                + " ( "
                + " select p.patient_id, o.obs_datetime "
                + " from patient p "
                + " join encounter e on p.patient_id = e.patient_id "
                + " join obs o on o.encounter_id = e.encounter_id "
                + " where e.encounter_type = 53 and e.voided = 0 and p.voided = 0 and o.voided = 0 and o.value_coded is not null "
                + " and o.concept_id = 21187 "
                + " and o.obs_datetime between :startInclusionDate and :endInclusionDate and e.location_id = :location "
                + " group by p.patient_id "
                + " ) segundaLinha on maxEnc.patient_id = segundaLinha.patient_id "
                + " where maxEnc.encounter_datetime >= date_add(segundaLinha.obs_datetime, INTERVAL 6 Month) ";

    public static final String
        findPatientsWithLastClinicalConsultationwhoAreNotInFistLineDenominatorB2ENEW =
            " Select linhaAlternativa.patient_id from ( "
                + " Select enc.patient_id, enc.encounter_datetime ultimaConsulta, max(obsLinha.obs_datetime) dataLinha from ( "
                + " Select p.patient_id, max(e.encounter_datetime) encounter_datetime from patient p "
                + " inner join encounter e on p.patient_id=e.patient_id "
                + " where p.voided = 0 and e.voided = 0 and e.encounter_type = 6 and "
                + " e.encounter_datetime > :startInclusionDate and e.encounter_datetime <= :endInclusionDate and e.location_id = :location "
                + " group by p.patient_id "
                + " ) enc "
                + " inner join encounter e on e.patient_id = enc.patient_id "
                + " inner join obs obsLinha on obsLinha.encounter_id = e.encounter_id "
                + " where obsLinha.concept_id = 21187 and e.encounter_type = 53 "
                + " and obsLinha.voided = 0 and e.voided = 0 and obsLinha.obs_datetime < enc.encounter_datetime and e.location_id = :location "
                + " group by enc.patient_id "
                + " ) linhaAlternativa "
                + " left join encounter e_ on e_.patient_id = linhaAlternativa.patient_id and e_.voided = 0 and e_.encounter_type = 6 and e_.location_id = :location "
                + " left join obs obsDiferenteLinha on e_.encounter_id = obsDiferenteLinha.encounter_id "
                + " and obsDiferenteLinha.voided = 0 and obsDiferenteLinha.concept_id = 21151 and obsDiferenteLinha.value_coded <> 21148 "
                + " and obsDiferenteLinha.obs_datetime > linhaAlternativa.dataLinha "
                + " and obsDiferenteLinha.obs_datetime <= linhaAlternativa.ultimaConsulta "
                + " and obsDiferenteLinha.location_id = :location "
                + " where (TIMESTAMPDIFF(MONTH,linhaAlternativa.dataLinha,linhaAlternativa.ultimaConsulta)) >= 6 and obsDiferenteLinha.obs_datetime is null "
                + " group by linhaAlternativa.patient_id ";
  }
}
