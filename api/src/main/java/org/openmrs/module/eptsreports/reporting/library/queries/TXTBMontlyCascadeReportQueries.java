/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;

public interface TXTBMontlyCascadeReportQueries {

  public class QUERY {

    public static final String findPatientsWithClinicalConsultationIntheLastSixMonths =
        "																	"
            + "select distinct lastConsultation.patient_id 																									"
            + "from	( 																																		"
            + "	select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p 															"
            + "		inner join encounter e on e.patient_id = p.patient_id 																					"
            + "	where p.voided is false and e.voided is false and e.encounter_type in (6,9) and e.location_id = :location 									"
            + "		group by p.patient_id 																													"
            + "	) 																																			"
            + "lastConsultation 																															"
            + "	inner join encounter e on e.patient_id = lastConsultation.patient_id								 		"
            + "where e.voided is false and e.location_id = :location  																						"
            + "	and e.encounter_datetime between (:endDate - interval 6 month) and :endDate and e.encounter_type in (6,9) 																";

    public static final String findPatientsWithClinicalConsultationsForMoreThanSixMonths =
        "																"
            + "select distinct lastConsultation.patient_id 																									"
            + "from	( 																																		"
            + "	select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p 															"
            + "		inner join encounter e on e.patient_id = p.patient_id 																					"
            + "	where p.voided is false and e.voided is false and e.encounter_type in (6,9) and e.location_id = :location 									"
            + "		group by p.patient_id 																													"
            + "	) 																																			"
            + "lastConsultation 																															"
            + "	inner join encounter e on e.patient_id = lastConsultation.patient_id 																		"
            + "where e.voided is false and e.location_id = :location and e.encounter_datetime < (:endDate - interval 6 month) and :endDate and e.encounter_type in (6,9) 								";

    public enum EnrollmentPeriod {
      NEWLY,
      PREVIOUSLY;
    }

    public static final String findPatientsWhoAreCurrentlyEnrolledOnARTByPeriod(
        EnrollmentPeriod enrollmentPeriod) {

      String query =
          "	select patient_id                                                                          							"
              + "	from                                                                                        							"
              + "	(select   	inicio_fila_seg_prox.*,																					"
              + "			GREATEST(COALESCE(data_fila,data_seguimento,data_recepcao_levantou),COALESCE(data_seguimento,data_fila,data_recepcao_levantou),COALESCE(data_recepcao_levantou,data_seguimento,data_fila))  data_usar_c,      "
              + "		    GREATEST(COALESCE(data_proximo_lev,data_proximo_seguimento,data_recepcao_levantou30),COALESCE(data_proximo_seguimento,data_proximo_lev,data_recepcao_levantou30),COALESCE(data_recepcao_levantou30,data_proximo_seguimento,data_proximo_lev)) data_usar "
              + "	from																														"
              + "		(select 	inicio_fila_seg.*,																							"
              + "		max(obs_fila.value_datetime) data_proximo_lev,																			"
              + "		max(obs_seguimento.value_datetime) data_proximo_seguimento,																"
              + "		date_add(data_recepcao_levantou, interval 30 day) data_recepcao_levantou30												"
              + "  from																															"
              + "(select inicio.*,																													"
              + "		saida.data_estado,																											"
              + "		max_fila.data_fila,																											"
              + "		max_consulta.data_seguimento,																								"
              + "		max_recepcao.data_recepcao_levantou																							"
              + " from																																"
              + " (	Select patient_id,min(data_inicio) data_inicio																				"
              + "		from																														"
              + "			(																														"
              /* Patients on ART who initiated the ARV DRUGS: ART Regimen Start Date */
              + "				Select 	p.patient_id,min(e.encounter_datetime) data_inicio															"
              + "				from 	patient p 																									"
              + "						inner join encounter e on p.patient_id=e.patient_id															"
              + "						inner join obs o on o.encounter_id=e.encounter_id															"
              + "				where 	e.voided=0 and o.voided=0 and p.voided=0 and																"
              + "						e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and 								"
              + "						e.encounter_datetime<=:endDate and e.location_id=:location													"
              + "				group by p.patient_id																								"
              + "				union																												"
              /* Patients on ART who have art start date: ART Start date */
              + "				Select 	p.patient_id,min(value_datetime) data_inicio																"
              + "				from 	patient p																									"
              + "						inner join encounter e on p.patient_id=e.patient_id															"
              + "						inner join obs o on e.encounter_id=o.encounter_id															"
              + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and 							"
              + "						o.concept_id=1190 and o.value_datetime is not null and														"
              + "						o.value_datetime<=:endDate and e.location_id=:location														"
              + "				group by p.patient_id																								"
              + "				union																												"
              /* Patients enrolled in ART Program: OpenMRS Program */
              + "				select 	pg.patient_id,min(date_enrolled) data_inicio																"
              + "				from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id										"
              + "				where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location			"
              + "				group by pg.patient_id																								"
              + "				union																												"
              /*
               * Patients with first drugs pick up date set in Pharmacy: First ART Start Date
               */
              + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio 													"
              + "				  FROM 		patient p																								"
              + "							inner join encounter e on p.patient_id=e.patient_id														"
              + "				  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location 	"
              + "				  GROUP BY 	p.patient_id																									 	"
              + "				union																															"
              /* Patients with first drugs pick up date set: Recepcao Levantou ARV */
              + "				Select 	p.patient_id,min(value_datetime) data_inicio																			"
              + "				from 	patient p																												"
              + "						inner join encounter e on p.patient_id=e.patient_id																		"
              + "						inner join obs o on e.encounter_id=o.encounter_id																		"
              + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and													"
              + "						o.concept_id=23866 and o.value_datetime is not null and 																"
              + "						o.value_datetime<=:endDate and e.location_id=:location																	"
              + "				group by p.patient_id																											"
              + "			) inicio_real																														"
              + "		group by patient_id																														"
              + "  )inicio																																	"
              /* Aqui encontramos os estado do paciente ate a data final */
              + "  left join																																	"
              + "	(																																			"
              + "	select patient_id,max(data_estado) data_estado																								"
              + "	from																																		"
              + "		(																																		"
              /* Estado no programa */
              + "			select distinct max_estado.patient_id, max_estado.data_estado from (                                          						"
              + "				select	pg.patient_id,																											"
              + "						max(ps.start_date) data_estado																							"
              + "				from	patient p																												"
              + "					inner join patient_program pg on p.patient_id = pg.patient_id																"
              + "					inner join patient_state ps on pg.patient_program_id = ps.patient_program_id												"
              + "				where pg.voided=0 and ps.voided=0 and p.voided=0  																				"
              + "					and ps.start_date<= :endDate and location_id =:location group by pg.patient_id                                              "
              + "			) max_estado                                                                                                                        "
              + "				inner join patient_program pp on pp.patient_id = max_estado.patient_id															"
              + "				inner join patient_state ps on ps.patient_program_id = pp.patient_program_id and ps.start_date = max_estado.data_estado	        "
              + "			where pp.program_id = 2 and ps.state in (7,8,10) and pp.voided = 0 and ps.voided = 0 and pp.location_id = :location					"
              + "			union																																"
              /* Estado no estado de permanencia da Ficha Resumo, Ficha Clinica */
              + "			select 	p.patient_id,																												"
              + "					max(o.obs_datetime) data_estado																								"
              + "			from 	patient p																													"
              + "					inner join encounter e on p.patient_id=e.patient_id																			"
              + "					inner join obs  o on e.encounter_id=o.encounter_id																			"
              + "			where 	e.voided=0 and o.voided=0 and p.voided=0 and																				"
              + "					e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded in (1706,1366,1709) and  						"
              + "					o.obs_datetime<=:endDate and e.location_id=:location																		"
              + "			group by p.patient_id																												"
              + "			union																																"
              /* Obito demografico */
              + "			select person_id as patient_id,death_date as data_estado																			"
              + "			from person																															"
              + "			where dead=1 and death_date is not null and death_date<=:endDate																	"
              + "			union																																"
              /* Obito na ficha de busca */
              + "			select 	p.patient_id,																												"
              + "					max(obsObito.obs_datetime) data_estado																						"
              + "			from 	patient p																													"
              + "					inner join encounter e on p.patient_id=e.patient_id																			"
              + "					inner join obs obsObito on e.encounter_id=obsObito.encounter_id																"
              + "			where 	e.voided=0 and p.voided=0 and obsObito.voided=0 and																			"
              + "					e.encounter_type in (21,36,37) and  e.encounter_datetime<=:endDate and  e.location_id=:location and 						"
              + "					obsObito.concept_id in (2031,23944,23945) and obsObito.value_coded=1366														"
              + "			group by p.patient_id																												"
              + "			union																																"
              /* Transferido Para na Ficha de Busca */
              + "			select 	p.patient_id,max(e.encounter_datetime) data_estado																			"
              + "			from	patient p																													"
              + "					inner join encounter e on p.patient_id=e.patient_id																			"
              + "					inner join obs o on o.encounter_id=e.encounter_id																			"
              + "			where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and															"
              + "					o.voided=0 and o.concept_id=2016 and o.value_coded in (1706,23863) and e.encounter_type in (21,36,37) and  e.location_id=:location "
              + "			group by p.patient_id																														"
              + "																																						"
              + "		) allSaida																																		"
              + "	group by patient_id																																	"
              + "  ) saida on inicio.patient_id=saida.patient_id																										"
              /* Aqui encontramos a data do ultimo levantamento de ARV do paciente: Fila */
              + " left join																																			"
              + "  ( Select p.patient_id,max(encounter_datetime) data_fila																								"
              + "	from 	patient p																																	"
              + "			inner join encounter e on e.patient_id=p.patient_id																							"
              + "	where 	p.voided=0 and e.voided=0 and e.encounter_type=18 and																						"
              + "			e.location_id=:location and e.encounter_datetime<=:endDate																					"
              + "	group by p.patient_id																																"
              + " ) max_fila on inicio.patient_id=max_fila.patient_id																									"
              /*
               * Aqui encontramos a data da ultima consulta clinica do paciente: Ficha Clinica
               */
              + "  left join																																			"
              + "  (	Select 	p.patient_id,max(encounter_datetime) data_seguimento																					"
              + "	from 	patient p																																	"
              + "			inner join encounter e on e.patient_id=p.patient_id																							"
              + "	where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and																					"
              + "			e.location_id=:location and e.encounter_datetime<=:endDate																					"
              + "	group by p.patient_id																																"
              + " ) max_consulta on inicio.patient_id=max_consulta.patient_id																							"
              /*
               * Aqui encontramos a data do ultimo levantamento de ARV do paciente: Recepcao
               */
              + "  left join																																			"
              + "  (																																					"
              + "	Select 	p.patient_id,max(value_datetime) data_recepcao_levantou																						"
              + "	from 	patient p																																	"
              + "			inner join encounter e on p.patient_id=e.patient_id																							"
              + "			inner join obs o on e.encounter_id=o.encounter_id																							"
              + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and 																		"
              + "			o.concept_id=23866 and o.value_datetime is not null and																						"
              + "			o.value_datetime<=:endDate and e.location_id=:location																						"
              + "	group by p.patient_id																																"
              + "  ) max_recepcao on inicio.patient_id=max_recepcao.patient_id																							"
              + "  group by inicio.patient_id																															"
              + " ) inicio_fila_seg																																	"
              /*
               * Aqui encontramos a data do proximo levantamento marcado no ultimo
               * levantamento de ARV
               */
              + "  left join																																			"
              + "	 obs obs_fila on obs_fila.person_id=inicio_fila_seg.patient_id																						"
              + "   and obs_fila.voided=0																																"
              + "	 and obs_fila.obs_datetime=inicio_fila_seg.data_fila																								"
              + "	 and obs_fila.concept_id=5096																														"
              + "	 and obs_fila.location_id=:location																													"
              /* -- Aqui encontramos a data da proxima consulta marcada na ultima consulta */
              + "  left join																																			"
              + "	obs obs_seguimento on obs_seguimento.person_id=inicio_fila_seg.patient_id																			"
              + "	and obs_seguimento.voided=0																															"
              + "	and obs_seguimento.obs_datetime=inicio_fila_seg.data_seguimento																						"
              + "	and obs_seguimento.concept_id=1410																													"
              + "	and obs_seguimento.location_id=:location																											"
              + " group by inicio_fila_seg.patient_id																													"
              + " ) inicio_fila_seg_prox																																"
              + " group by patient_id																																	"
              + " ) coorte12meses_final																																"
              /* -- Verificação qual é o estado final */
              /* -- where estado_final=6 */
              + " where (data_estado is null or (data_estado is not null and  data_usar_c>data_estado))     ";

      switch (enrollmentPeriod) {
        case NEWLY:
          query += " and data_inicio between (:endDate - interval 6 month) and :endDate";
          break;

        case PREVIOUSLY:
          query += " and data_inicio < (:endDate - interval 6 month)";
          break;
      }
      return query;
    }

    public enum DiagnosticTestTypes {
      GENEXPERT,
      BACILOSCOPIA,
      TBLAM,
      CULTURA;
    }

    public static final String findDiagnosticTests(DiagnosticTestTypes diagnosticTestType) {
      String query =
          "																														"
              + "select distinct obs.person_id 																																"
              + "from obs 																																					"
              + "		inner join encounter on encounter.encounter_id = obs.encounter_id 																						"
              + "where encounter.encounter_type in (6,9) 																														"
              + "		and ((obs.concept_id = 23722 and obs.value_coded in (#)) or (obs.concept_id in(#) and obs.value_coded in (703,664)) )  							"
              + "		and encounter.voided = 0 and obs.voided =0 and obs.location_id =:location 																				"
              + "		and encounter.encounter_datetime >=:startDate and encounter.encounter_datetime <= :endDate																"
              + "union																																						"
              + "select distinct obs.person_id 																																"
              + "from obs 																																					"
              + "		inner join encounter on encounter.encounter_id = obs.encounter_id 																						"
              + "where encounter.encounter_type = 13 																															"
              + "		and obs.concept_id in (#)and obs.value_coded in (703,664,1065,1066,165184) and encounter.voided = 0 and obs.voided =0 and obs.location_id =:location 					"
              + "  	and encounter.encounter_datetime >=:startDate  and encounter.encounter_datetime <=:endDate																";

      switch (diagnosticTestType) {
        case GENEXPERT:
          query =
              StringUtils.replace(query, "#", StringUtils.join(Arrays.asList(23723, 165189), ","));
          break;

        case BACILOSCOPIA:
          query = StringUtils.replace(query, "#", "307");
          break;
        case TBLAM:
          query = StringUtils.replace(query, "#", "23951");
          break;
        case CULTURA:
          query = StringUtils.replace(query, "#", "23774");
          break;
      }
      return query;
    }
  }
}
