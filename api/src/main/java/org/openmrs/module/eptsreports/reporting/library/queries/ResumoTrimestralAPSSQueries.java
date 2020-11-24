/** */
package org.openmrs.module.eptsreports.reporting.library.queries;

public interface ResumoTrimestralAPSSQueries {

  public class QUERY {

    public static final String findPatientsReceivedTotalDiagnosticRevelationInReportingPeriod =
        "select totalDiagnosticoNoPeriodo.patient_id from "
            + "(select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id = 6340 and obs.value_coded =6337 "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location "
            + "group by encounter.patient_id ) totalDiagnosticoNoPeriodo left join "
            + "(select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id = 6340 and obs.value_coded =6337 "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime < :startDate and encounter.location_id = :location "
            + "group by encounter.patient_id) totalDiagnosticoAntesPeriodo on totalDiagnosticoAntesPeriodo.patient_id = totalDiagnosticoNoPeriodo.patient_id "
            + "where totalDiagnosticoAntesPeriodo.patient_id is null ";

    public static final String findPatientsRegisteredInAPSSPPAconselhamentoWithinReportingPeriod =
        "select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id = 23886 and obs.value_coded =1065 "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 "
            + "and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location group by patient.patient_id ";

    public static final String findPatientsWithSeguimentoDeAdesao =
        "select seguimentoAdesao.patient_id from (select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id in (23716,23887) and obs.value_coded in(1065,1066) "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location "
            + "group by patient.patient_id                                 "
            + "union                                                       "
            + "select patient.patient_id from patient                      "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and  ((obs.concept_id = 6223 and obs.value_coded in(1383,1749,1385)) or obs.concept_id = 	23717) "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location "
            + " group by patient.patient_id) seguimentoAdesao ";

    public static final String findPatientsWithPrevencaoPosetivaInReportingPeriod =
        "select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id in (6317,6318,6319,6320,5271,6321,6322) and obs.value_coded =1065 "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location "
            + "group by patient.patient_id having count(distinct obs.concept_id)=7 ";
  }
}
