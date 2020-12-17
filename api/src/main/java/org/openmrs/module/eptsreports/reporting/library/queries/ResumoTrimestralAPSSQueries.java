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
        "select seguimentoAdesao.patient_id from   																								"
            + "	(select patient.patient_id, encounter.encounter_datetime from patient  																"
            + "		join encounter on encounter.patient_id = patient.patient_id 																	"
            + "		join obs on obs.encounter_id = encounter.encounter_id 																			"
            + "	where encounter.encounter_type =35 and obs.concept_id in (23716,23887) and obs.value_coded in(1065,1066) 							"
            + "		and patient.voided =0 and encounter.voided = 0 and obs.voided = 0                                                               "
            + "		and encounter.encounter_datetime between :startDate and :endDate and encounter.location_id =:location                          	"
            + "	union                                                       																		"
            + "	select patient.patient_id, encounter.encounter_datetime from patient                      											"
            + "		join encounter on encounter.patient_id = patient.patient_id                                                                     "
            + "		join obs on obs.encounter_id = encounter.encounter_id  																		 	"
            + "	where encounter.encounter_type =35 and  ((obs.concept_id = 6223 and obs.value_coded in(1383,1749,1385)) or obs.concept_id = 23717) "
            + "		and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 														"
            + "		and encounter.encounter_datetime between :startDate and  :endDate and encounter.location_id =:location                  "
            + "	) seguimentoAdesao																											"
            + "	join 																														"
            + "	(																															"
            + "	select * from (																												"
            + "	select * from (																												"
            + "	select patient_id, min(art_start_date) art_start_date from 																	"
            + "	(	select p.patient_id, min(e.encounter_datetime) art_start_date from patient p   											"
            + "	 		join encounter e on p.patient_id=e.patient_id 																		"
            + "			join obs o on o.encounter_id=e.encounter_id 																		"
            + "		where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in (18,6,9) 										"
            + "			and o.concept_id=1255 and o.value_coded=1256 and e.encounter_datetime<=:endDate and e.location_id=:location 		"
            + "			group by p.patient_id 																								"
            + "		union 																													"
            + "		select p.patient_id, min(value_datetime) art_start_date from patient p   												"
            + "			join encounter e on p.patient_id=e.patient_id   																	"
            + "			join obs o on e.encounter_id=o.encounter_id   																		"
            + "		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) 										"
            + "			and o.concept_id=1190 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location   "
            + "			group by p.patient_id   																							"
            + "		union  																													"
            + "		select pg.patient_id, min(date_enrolled) art_start_date from patient p  												"
            + "			join patient_program pg on p.patient_id=pg.patient_id                                                               "
            + "		where pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location 				"
            + "		group by pg.patient_id  																								"
            + "		union 																													"
            + "		select e.patient_id, min(e.encounter_datetime) as art_start_date from patient p 										"
            + "			join encounter e on p.patient_id=e.patient_id  																		"
            + "		where p.voided=0 and e.encounter_type=18 and e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  "
            + "		group by p.patient_id 																									"
            + "		union 																													"
            + "		select p.patient_id, min(value_datetime) art_start_date from patient p 													"
            + "			join encounter e on p.patient_id=e.patient_id 																		"
            + "			join obs o on e.encounter_id=o.encounter_id   																		"
            + "		where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 													"
            + "			and o.concept_id=23866 and o.value_datetime is not null and o.value_datetime<=:endDate and e.location_id=:location  "
            + "		group by p.patient_id                                                                                                   "
            + "	) art_start group by patient_id ) 																							"
            + "	tx_new where art_start_date <= :endDate																						"
            + "	) tx_new                                                                                                  "
            + ")tx_new                                                                                                     "
            + "on tx_new.patient_id = seguimentoAdesao.patient_id                                                          "
            + "where (TIMESTAMPDIFF(day,tx_new.art_start_date ,seguimentoAdesao.encounter_datetime)) >30                   ";

    public static final String findPatientsWithPrevencaoPosetivaInReportingPeriod =
        "select patient.patient_id from patient "
            + "join encounter on encounter.patient_id = patient.patient_id "
            + "join obs on obs.encounter_id = encounter.encounter_id "
            + "where encounter.encounter_type =35 and obs.concept_id in (6317,6318,6319,6320,5271,6321,6322) and obs.value_coded =1065 "
            + "and patient.voided =0 and encounter.voided = 0 and obs.voided = 0 and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime <= :endDate and encounter.location_id =:location "
            + "group by patient.patient_id having count(distinct obs.concept_id)=7 ";
  }
}
