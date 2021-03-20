package org.openmrs.module.eptsreports.reporting.library.queries;

public interface CxCaTXQueries {
  class QUERY {
    public static final String findPatientsWhoerceivedTreatmentTypeDuringReportingPeriod(
        final CxType cxType) {

      String query =
          "select patient_id from ( "
              + "select rastreioPositivo.patient_id, "
              + "if(o.concept_id=2149 and o.value_coded=23970,1, "
              + "if(o.concept_id=2149 and o.value_coded=23972,2,3)) tipoTratamento from  ( "
              + "select rastreioPeriodo.patient_id,rastreioPeriodo.dataRastreio dataRastreioPositivo from ( "
              + "Select p.patient_id,min(o.obs_datetime) dataRastreio from patient p  "
              + "inner join encounter e on p.patient_id=e.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where p.voided=0 and e.voided=0 and o.voided=0 and concept_id=2094 and value_coded in (703,664,2093) and  "
              + "e.encounter_type=28 and e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
              + "group by p.patient_id "
              + ") rastreioPeriodo "
              + "inner join encounter rastreio on rastreioPeriodo.patient_id=rastreio.patient_id "
              + "inner join obs obsRastreio on obsRastreio.encounter_id=rastreio.encounter_id "
              + "where obsRastreio.voided=0 and obsRastreio.obs_datetime=rastreioPeriodo.dataRastreio and  "
              + "obsRastreio.concept_id=2094 and obsRastreio.value_coded=703	and  "
              + "rastreio.voided=0 and rastreio.location_id=:location and rastreio.encounter_type=28 "
              + ") rastreioPositivo "
              + "inner join encounter e on e.patient_id=rastreioPositivo.patient_id "
              + "inner join obs o on e.encounter_id=o.encounter_id "
              + "where e.voided=0 and e.voided=0 and e.encounter_type=28 and ( "
              + "(o.concept_id in (2117,2149) and o.value_coded in (1065,23974,23972,23970,23973) "
              + "and e.encounter_datetime between rastreioPositivo.dataRastreioPositivo and :endDate "
              + ") or "
              + "(o.concept_id=23967 and o.value_datetime between rastreioPositivo.dataRastreioPositivo and :endDate)) and  "
              + "e.location_id=:location "
              + ") tratamento ";

      switch (cxType) {
        case CRYOTHERAPY:
          query = query + "where tipoTratamento=3 ";
          break;

        case THERMOCOAGULATION:
          query = query + "where tipoTratamento=2 ";
          break;

        case LEEP:
          query = query + "where tipoTratamento=1 ";
          break;

        case ALL:
          query = query + "";
          break;
      }

      return query;
    }
  }
}
