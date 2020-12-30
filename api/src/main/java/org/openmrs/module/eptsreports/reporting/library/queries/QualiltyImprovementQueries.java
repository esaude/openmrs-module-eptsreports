package org.openmrs.module.eptsreports.reporting.library.queries;

public class QualiltyImprovementQueries {

  /**
   * MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
   *
   * <p>São pacientes que iniciaram TARV dentro do periodo de inclusao, com mínimo de uma consulta
   * após início TARV
   *
   * @return String
   */
  public static String getPatientStartedTarvInInclusionPeriodWithAtLeastOneEncounter(
      int arvAdultInitialEncounterType,
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDate,
      int arvProgram,
      int ptvProgram,
      int numberOfWeekPregnant,
      int pregnantConcept,
      int yesConcept,
      int pStateActivePrioART,
      int transferredOutToAnotherHealthFacilityWorkflowState,
      int pateintTransferedFromOtherFacilityWorkflowState,
      int psPregnant) {

    return "select inicio.patient_id "
        + "from "
        + "(               "
        + "	Select   p.patient_id, e.encounter_datetime data_inicio "
        + "	from      patient p  "
        + "	inner join encounter e on p.patient_id=e.patient_id     "
        + "	inner join obs o on o.encounter_id=e.encounter_id "
        + "	where e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "	e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and  "
        + "	e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
        + " "
        + "	union "
        + " "
        + "	Select   p.patient_id, o.value_datetime data_inicio "
        + "	from      patient p "
        + "	inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "	o.concept_id="
        + arvStartDate
        + " and o.value_datetime is not null and  "
        + "	o.value_datetime between :startDate and :endDate and e.location_id=:location "
        + "		                        "
        + "	union "
        + " "
        + "	select    pg.patient_id, date_enrolled data_inicio "
        + "	from      patient p  "
        + "	inner join patient_program pg on p.patient_id=pg.patient_id "
        + "	inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "	where pg.voided=0 and p.voided=0 and program_id="
        + arvProgram
        + " and pg.date_enrolled=ps.start_date and ps.voided=0 and  "
        + "	date_enrolled between :startDate and :endDate and  "
        + "	location_id=:location and ps.state="
        + pStateActivePrioART
        + " "
        + " "
        + ") inicio "
        + "inner join "
        + "(               "
        + "	select    distinct patient_id, max(encounter_datetime) data_consulta "
        + "	from      encounter "
        + "	where encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and voided=0 and  "
        + "	encounter_datetime between :startDate and date_add(:startDate, interval 6 MONTH) and  "
        + "	location_id=:location "
        + "	group by patient_id "
        + ") consulta on consulta.patient_id=inicio.patient_id and timestampdiff(day, inicio.data_inicio,consulta.data_consulta)>0 "
        + "where inicio.patient_id  "
        + "not in  "
        + "( "
        + "	select    pg.patient_id "
        + "	from      patient p  "
        + "	inner join patient_program pg on p.patient_id=pg.patient_id "
        + "	inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "	where pg.voided=0 and ps.voided=0 and p.voided=0 and  "
        + "	pg.program_id="
        + arvProgram
        + " and ps.state="
        + transferredOutToAnotherHealthFacilityWorkflowState
        + "  "
        + "        and ps.start_date <=:revisionEndDate  "
        + "		                       "
        + "	union "
        + " "
        + "	select    pg.patient_id "
        + "	from      patient p  "
        + "	inner join patient_program pg on p.patient_id=pg.patient_id "
        + "	inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "	where pg.voided=0 and ps.voided=0 and p.voided=0 and  "
        + "	pg.program_id="
        + arvProgram
        + " and ps.state="
        + pateintTransferedFromOtherFacilityWorkflowState
        + " and ps.start_date<=:revisionEndDate  "
        + "	                       "
        + "	union "
        + " "
        + "	Select   p.patient_id "
        + "	from      patient p  "
        + "	inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnantConcept
        + " and value_coded="
        + yesConcept
        + " and  "
        + "	e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime <=:revisionEndDate "
        + " "
        + "	union                     "
        + "		                       "
        + "	Select   p.patient_id "
        + "	from      patient p inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + numberOfWeekPregnant
        + " and  "
        + "	e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and o.value_datetime<=:revisionEndDate                  "
        + "		                       "
        + "	union "
        + "		                       "
        + "	select    pp.patient_id "
        + "	from      patient_program pp  "
        + "	where pp.program_id="
        + ptvProgram
        + " and pp.voided=0 and pp.date_enrolled<=:revisionEndDate  "
        + " "
        + "	union "
        + " "
        + "	select    pp.patient_id "
        + "	from      patient_program pp  "
        + "	inner join patient_state ps on pp.patient_program_id=ps.patient_program_id "
        + "	where pp.program_id="
        + ptvProgram
        + " and pp.voided=0 and ps.voided=0 and ps.state="
        + psPregnant
        + " and ps.end_date is null and  "
        + "	ps.start_date<=:revisionEndDate  "
        + " "
        + " "
        + "	union "
        + " "
        + " "
        + "	Select   p.patient_id "
        + "	from      patient p  "
        + "	inner join encounter e on p.patient_id=e.patient_id     "
        + "	inner join obs o on o.encounter_id=e.encounter_id "
        + "	where e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "	e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and  "
        + "	e.encounter_datetime<:startDate "
        + " "
        + "	union "
        + " "
        + "	Select   p.patient_id "
        + "	from      patient p "
        + "	inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "	o.concept_id="
        + arvStartDate
        + " and o.value_datetime is not null and  "
        + "	o.value_datetime < :startDate "
        + "		                        "
        + "	union "
        + " "
        + "	select    pg.patient_id "
        + "	from      patient p  "
        + "	inner join patient_program pg on p.patient_id=pg.patient_id "
        + "	inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
        + "	where pg.voided=0 and p.voided=0 and program_id="
        + arvProgram
        + " and pg.date_enrolled=ps.start_date and ps.voided=0 and  "
        + "	date_enrolled<:startDate and ps.state="
        + pStateActivePrioART
        + " "
        + ")";
  }

  /**
   * PACIENTES COM CONSULTA CLINICA DENTRO DE 7 DIAS APOS DIAGNOSTICO
   *
   * <p>São pacientes que tiveram a primeira consulta clínica dentro de 7 dias depois da data de
   * diagnóstico
   *
   * @param aRVAdultInitialEncounterType
   * @param aRVPediatriaInitialEncounterType
   * @param dateOfHivDiagnosisConcept
   * @param adultoSeguimentoEncounterType
   * @param aRVPediatriaSeguimentoEncounterType
   * @return String
   */
  public static String getPatientWithEncontersWithinSevenDaysAfterDiagnostic(
      int aRVAdultInitialEncounterType,
      int aRVPediatriaInitialEncounterType,
      int dateOfHivDiagnosisConcept,
      int adultoSeguimentoEncounterType,
      int aRVPediatriaSeguimentoEncounterType) {

    String query =
        "select diagnostico.patient_id "
            + "from "
            + "( "
            + "    select     p.patient_id,o.value_datetime data_diagnostico "
            + "    from     patient p "
            + "    inner join encounter e on e.patient_id=p.patient_id "
            + "    inner join obs o on o.encounter_id=e.encounter_id "
            + "    where     e.voided=0 and p.voided=0"
            + "  and e.encounter_type in (%d,%d) "
            + " and o.concept_id=%d and "
            + "    e.encounter_datetime between :startDate and :endDate and e.location_id=:location "
            + ") diagnostico "
            + "inner join encounter e on diagnostico.patient_id = e.patient_id "
            + "where     e.voided=0 and datediff(e.encounter_datetime,diagnostico.data_diagnostico)<=7 and "
            + "e.encounter_type in (%d,%d) and e.location_id=:location";

    return String.format(
        query,
        aRVAdultInitialEncounterType,
        aRVPediatriaInitialEncounterType,
        dateOfHivDiagnosisConcept,
        adultoSeguimentoEncounterType,
        aRVPediatriaSeguimentoEncounterType);
  }
  /**
   * São pacientes que durante um periodo tiveram consulta clinica e que foram rastreiados para
   * tuberculose em cada visita (Numero de visitas igual ao numero de rastreios)
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param tbScreeningConcept
   * @return String
   */
  public static String getPatientWithTrackInEachTBEncounter(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int tbScreeningConcept) {

    return "select consulta.patient_id "
        + "from "
        + "(Select 	p.patient_id,count(*) consultas "
        + "from 	patient p inner join encounter e on e.patient_id=p.patient_id "
        + "where 	p.voided=0 and e.voided=0 and e.encounter_datetime between :startDate and :endDate and  "
        + "		e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") "
        + "group by patient_id) consulta "
        + "inner join "
        + "(Select 	p.patient_id,count(*) rastreios "
        + "from 	patient p  "
        + "		inner join encounter e on e.patient_id=p.patient_id "
        + "		inner join obs o on o.encounter_id=e.encounter_id "
        + "where 	p.voided=0 and e.voided=0 and e.encounter_datetime between :startDate and :endDate and  "
        + "		e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.voided=0 and o.concept_id="
        + tbScreeningConcept
        + " "
        + "group by patient_id) rastreio on consulta.patient_id=rastreio.patient_id "
        + " "
        + "where rastreio.rastreios>=consulta.consultas ";
  }
  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA
   * GRAVIDA) São gravidas inscritas no serviço TARV e que iniciaram tarv e que fazem parte da
   * amostra para a avaliação de qualidade de dados de MQ
   *
   * @return String
   */
  public static String getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(
      int arvAdultInitialEncounterType,
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int pregnantConcept,
      int numberOfWeekPregnant,
      int pregnancyDueDateConcept,
      int ptvEtvProgram,
      int arvPlan,
      int startDrugsConcept,
      int artProgram,
      int arvStartDate,
      int yesConcept) {

    return "select gravida.patient_id  "
        + "from								  "
        + "(  "
        + "  "
        + "	Select 	p.patient_id  "
        + "	from 	patient p   "
        + "			inner join encounter e on p.patient_id=e.patient_id  "
        + "			inner join obs o on e.encounter_id=o.encounter_id  "
        + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnantConcept
        + " and value_coded="
        + yesConcept
        + " and   "
        + "			e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location  "
        + "  "
        + "	union		  "
        + "			  "
        + "	Select 	p.patient_id  "
        + "	from 	patient p inner join encounter e on p.patient_id=e.patient_id  "
        + "			inner join obs o on e.encounter_id=o.encounter_id  "
        + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + numberOfWeekPregnant
        + " and   "
        + "			e.encounter_type in ( "
        + arvAdultInitialEncounterType
        + ", "
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location  "
        + "  "
        + "  "
        + "	union		  "
        + "			  "
        + "	Select 	p.patient_id  "
        + "	from 	patient p inner join encounter e on p.patient_id=e.patient_id  "
        + "			inner join obs o on e.encounter_id=o.encounter_id  "
        + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnancyDueDateConcept
        + " and   "
        + "			e.encounter_type in ( "
        + arvAdultInitialEncounterType
        + ", "
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location		  "
        + "			  "
        + "	union  "
        + "			  "
        + "	select 	pp.patient_id  "
        + "	from 	patient_program pp   "
        + "	where 	pp.program_id="
        + ptvEtvProgram
        + " and pp.voided=0 and   "
        + "			pp.date_enrolled between :startDate and :endDate and pp.location_id=:location  "
        + "  "
        + ") gravida  "
        + "inner join  "
        + "(  select   distinct patient_id  "
        + "   from     encounter  "
        + "   where 	encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and voided=0 and   "
        + "			encounter_datetime between :startDate and date_add(:endDate, interval 6 MONTH) and   "
        + "			location_id=:location  "
        + ") consulta on consulta.patient_id=gravida.patient_id  "
        + "inner join   "
        + "(	select patient_id,data_inicio  "
        + "	from  "
        + "	(	Select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "				(	Select 	p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "					from 	patient p   "
        + "							inner join encounter e on p.patient_id=e.patient_id	  "
        + "							inner join obs o on o.encounter_id=e.encounter_id  "
        + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and   "
        + "							e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and   "
        + "							e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "			  "
        + "					union  "
        + "			  "
        + "					Select 	p.patient_id,min(value_datetime) data_inicio  "
        + "					from 	patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "							inner join obs o on e.encounter_id=o.encounter_id  "
        + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and   "
        + "							o.concept_id="
        + arvStartDate
        + " and o.value_datetime is not null and   "
        + "							o.value_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "  "
        + "					union  "
        + "  "
        + "					select 	pg.patient_id,date_enrolled data_inicio  "
        + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "					where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location  "
        + "					  "
        + "					union  "
        + "					  "
        + "					  "
        + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio   "
        + "				  FROM 		patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "				  WHERE		p.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "				  GROUP BY 	p.patient_id				  "
        + "					  "
        + "					  "
        + "				) inicio  "
        + "			group by patient_id	  "
        + "	)inicio1  "
        + "	where data_inicio between :startDate and  :endDate  "
        + ") inicio_real on inicio_real.patient_id=gravida.patient_id";
  }

  /**
   * PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO São pacientes inscritos
   * no programa de tuberculose num determinado periodo
   *
   * @return String
   */
  public static String getPacientsEnrolledInTBProgram(int tbProgram) {
    String query =
        "select 	pg.patient_id "
            + "from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
            + "where 	pg.voided=0 and p.voided=0 and program_id=%d and date_enrolled between "
            + ":startDate and :endDate and location_id=:location";

    return String.format(query, tbProgram);
  }
  /**
   * MQ_PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI) NO PERIODO DE INCLUSAO E QUE
   * TERMINARAM
   *
   * <p>São pacientes que iniciaram a profilaxia com isoniazida no período de inclusão e que já
   * terminaram
   *
   * @return String
   */
  public static String getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted(
      int isoniazidProphylaxisStartDate,
      int isoniazidProphylaxisEndDate,
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType) {

    return "select inicio.patient_id  "
        + "from  "
        + "(	select p.patient_id,min(value_datetime) data_inicio  "
        + "	from	patient p  "
        + "	inner join encounter e on p.patient_id=e.patient_id  "
        + "	inner join obs o on o.encounter_id=e.encounter_id  "
        + "	where e.voided=0 and p.voided=0 and o.voided=0  "
        + "	and o.value_datetime between :startDate and :endDate   "
        + "	and o.concept_id="
        + isoniazidProphylaxisStartDate
        + "   "
        + "	and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "	and e.location_id= :location  "
        + "	group by p.patient_id  "
        + ") inicio  "
        + "inner join  "
        + "(	select p.patient_id,max(value_datetime) data_final  "
        + "	from	patient p  "
        + "	inner join encounter e on p.patient_id=e.patient_id  "
        + "	inner join obs o on o.encounter_id=e.encounter_id  "
        + "	where e.voided=0 and p.voided=0 and o.voided=0  "
        + "	and o.value_datetime between :startDate and :revisionEndDate  "
        + "	and o.concept_id="
        + isoniazidProphylaxisEndDate
        + "	and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "	and e.location_id= :location  "
        + "	group by p.patient_id  "
        + ") termino  "
        + "on inicio.patient_id=termino.patient_id  "
        + "where data_inicio<data_final";
  }
  /**
   * MQ_PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI) NO PERIODO DE INCLUSAO São pacientes
   * que iniciaram a profilaxia com INH no período de inclusão
   *
   * @return String
   */
  public static String getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriod(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int isoniazidProphylaxisStartDate) {

    String query =
        "select p.patient_id  "
            + "from	patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "inner join obs o on o.encounter_id=e.encounter_id  "
            + "where e.voided=0 and p.voided=0 and o.voided=0  "
            + "and o.value_datetime between :startDate and :endDate   "
            + "and o.concept_id=%d   "
            + "and e.encounter_type in (%d,%d)   "
            + "and e.location_id=:location  "
            + "group by p.patient_id";

    return String.format(
        query,
        isoniazidProphylaxisStartDate,
        adultoSeguimentoEncounterType,
        arvPediatriaSeguimentoEncounterType);
  }
  /**
   * MQ_PACIENTES QUE TIVERAM CONSULTA CLINICA NUM PERIODO E QUE TIVERAM RASTREIO DE ITS EM CADA
   * VISITA
   *
   * <p>Sao pacientes que tiveram consulta clinica num periodo e que tiveram rastreio de ITS em cada
   * consulta
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param screeningForSTIConcept
   * @return
   */
  public static String getPatientsWithEnconterInPeriodAndHadScreeningForSTI(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int screeningForSTIConcept) {

    return "select consulta.patient_id "
        + "from "
        + "(Select 	p.patient_id,count(*) consultas "
        + "from 	patient p inner join encounter e on e.patient_id=p.patient_id "
        + "where 	p.voided=0 and e.voided=0 and e.encounter_datetime between :startDate and :endDate and  "
        + "		e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") "
        + "group by patient_id) consulta "
        + "inner join "
        + "(Select 	p.patient_id,count(*) itss "
        + "from 	patient p  "
        + "		inner join encounter e on e.patient_id=p.patient_id "
        + "		inner join obs o on o.encounter_id=e.encounter_id "
        + "where 	p.voided=0 and e.voided=0 and e.encounter_datetime between :startDate and :endDate and  "
        + "		e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") "
        + "and o.voided=0 and o.concept_id="
        + screeningForSTIConcept
        + " "
        + "group by patient_id) its on consulta.patient_id=its.patient_id "
        + "where its.itss>=consulta.consultas";
  }

  /**
   * MQ_PACIENTES COM CD4 REGISTADO DENTRO DE 33 DIAS APOS A INSCRICAO Sao pacientes que abriram
   * processo (inscritos) e que tiveram CD4 registado dentro de 33 dias após a inscrição
   */
  public static String getPacientsWithCD4RegisteredIn33Days(
      int arvAdultInitialEncounterType,
      int adultoSeguimentoEncounterType,
      int pateintActiveOnHIVCareProgramtWorkflowState,
      int arvPediatriaInitialEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int cd4AbsoluteOBSConcept) {
    String query =
        "Select 	inscricao.patient_id "
            + "from 	(	Select 	p.patient_id,min(encounter_datetime) as data_inscricao "
            + "			from 	patient p  "
            + "					inner join encounter e on p.patient_id=e.patient_id "
            + "			where 	p.voided=0 and e.voided=0 and e.encounter_type in (%d,%d) and  "
            + "					e.location_id=:location and e.encounter_datetime <= :endDate "
            + "			group 	by p.patient_id "
            + "			 "
            + "			union "
            + " "
            + "			select 	pg.patient_id,min(date_enrolled) data_inscricao "
            + "			from 	patient p  "
            + "					inner join patient_program pg on p.patient_id=pg.patient_id "
            + "					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "			where 	pg.voided=0 and p.voided=0 and program_id=1 and pg.date_enrolled=ps.start_date and ps.voided=0 and  "
            + "					date_enrolled <= :endDate and location_id=:location and ps.state=%d "
            + "			group by pg.patient_id					 "
            + "		) inscricao  "
            + "		inner join  "
            + "		(	select 	p.patient_id,min(encounter_datetime) data_cd4 "
            + "			from 	patient p  "
            + "					inner join encounter e on p.patient_id=e.patient_id "
            + "					inner join obs o on o.encounter_id=e.encounter_id "
            + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and "
            + "					e.encounter_datetime <= date_add(:endDate, interval 36 DAY) and  "
            + "					e.location_id=:location and e.encounter_type in (%d,%d) and o.concept_id=%d "
            + "			group by p.patient_id "
            + "		) cd4  on inscricao.patient_id=cd4.patient_id "
            + "		 "
            + "where   datediff(data_cd4,data_inscricao)<=33 ";

    return String.format(
        query,
        arvAdultInitialEncounterType,
        arvPediatriaInitialEncounterType,
        pateintActiveOnHIVCareProgramtWorkflowState,
        adultoSeguimentoEncounterType,
        arvPediatriaSeguimentoEncounterType,
        cd4AbsoluteOBSConcept);
  }
  /**
   * MQ_PACIENTES QUE INICIARAM TARV DENTRO DE 15 DIAS DEPOIS DA INSCRICAO Sao pacientes que
   * iniciaram TARV dentro de 15 dias depois da abertura de processo ou de inscricão no programa de
   * tratamento
   *
   * @param arvAdultInitialEncounterType
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaInitialEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param hivCareProgram
   * @param artProgram
   * @return
   */
  public static String getPatientsWhomStartedARVIn15Days(
      int arvAdultInitialEncounterType,
      int adultoSeguimentoEncounterType,
      int arvPediatriaInitialEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int hivCareProgram,
      int artProgram) {

    return "select inicio_real.patient_id  "
        + "from  "
        + "(	select patient_id,data_inicio  "
        + "	from  "
        + "	(	Select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "		(	Select p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "			from patient p   "
        + "			inner join encounter e on p.patient_id=e.patient_id	  "
        + "			inner join obs o on o.encounter_id=e.encounter_id  "
        + "			where e.voided=0 and o.voided=0 and p.voided=0   "
        + "			and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "			and o.concept_id="
        + arvPlan
        + "   "
        + "			and o.value_coded="
        + startDrugsConcept
        + "   "
        + "			and e.encounter_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "	  "
        + "			union  "
        + "	  "
        + "			Select p.patient_id,min(value_datetime) data_inicio  "
        + "			from patient p  "
        + "			inner join encounter e on p.patient_id=e.patient_id  "
        + "			inner join obs o on e.encounter_id=o.encounter_id  "
        + "			where p.voided=0 and e.voided=0 and o.voided=0   "
        + "			and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "			and o.concept_id="
        + arvStartDateConcept
        + "   "
        + "			and o.value_datetime is not null   "
        + "			and o.value_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "  "
        + "			union  "
        + "  "
        + "			select pg.patient_id,date_enrolled data_inicio  "
        + "			from patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "			where pg.voided=0 and p.voided=0   "
        + "			and program_id="
        + artProgram
        + "   "
        + "			and date_enrolled<=:endDate   "
        + "			and location_id=:location  "
        + "			  "
        + "			union  "
        + "			  "
        + "			select e.patient_id, MIN(e.encounter_datetime) AS data_inicio   "
        + "			from patient p  "
        + "			inner join encounter e on p.patient_id=e.patient_id  "
        + "			where p.voided=0 and e.voided=0   "
        + "			and e.encounter_type="
        + arvPharmaciaEncounterType
        + "  "
        + "			and e.encounter_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "		) inicio  "
        + "		group by patient_id	  "
        + "	)inicio1  "
        + "	where data_inicio between :startDate and :endDate  "
        + ")inicio_real  "
        + "inner join  "
        + "(  "
        + "	select p.patient_id, e.encounter_datetime as data_inscricao  "
        + "	from patient p   "
        + "	inner join encounter e on e.patient_id=p.patient_id  "
        + "	left join obs o1 on o1.encounter_id=e.encounter_id and o1.concept_id=1611  "
        + "	where e.voided=0 and p.voided=0   "
        + "	and e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + arvPediatriaInitialEncounterType
        + ")   "
        + "	and e.encounter_datetime<=:endDate   "
        + "	and e.location_id = :location  "
        + "  "
        + "	union  "
        + "  "
        + "	select pg.patient_id, date_enrolled as data_inscricao  "
        + "	from patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "	where pg.voided=0 and p.voided=0   "
        + "	and program_id="
        + hivCareProgram
        + "   "
        + "	and date_enrolled<=:endDate   "
        + "	and location_id=:location  "
        + ")inscricao on inicio_real.patient_id=inscricao.patient_id  "
        + "and datediff(data_inicio,data_inscricao)<=15 ";
  }
  /**
   * PACIENTES QUE INICIARAM TARV NUM PERIODO E QUE TIVERAM SEGUNDO LEVANTAMENTO OU CONSULTA CLINICA
   * DENTRO DE 33 DIAS DEPOIS DE INICIO São pacientes que iniciaram TARV num determinado periodo e
   * que tiveram segundo levantamento de ARV ou segunda consulta clinica dentro de 33 dias do inicio
   * de TARV
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @return String
   */
  public static String getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram,
      int pateintTransferedFromOtherFacilityWorkflowState) {

    return "select inicio_real.patient_id  "
        + "from  "
        + "(	select patient_id,data_inicio  "
        + "	from  "
        + "	(	Select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "				(	Select 	p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "					from 	patient p   "
        + "							inner join encounter e on p.patient_id=e.patient_id	  "
        + "							inner join obs o on o.encounter_id=e.encounter_id  "
        + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and   "
        + "							e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and   "
        + "							e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "			  "
        + "					union  "
        + "			  "
        + "					Select 	p.patient_id,min(value_datetime) data_inicio  "
        + "					from 	patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "							inner join obs o on e.encounter_id=o.encounter_id  "
        + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and   "
        + "							o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and   "
        + "							o.value_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "  "
        + "					union  "
        + "  "
        + "					select 	pg.patient_id,date_enrolled data_inicio  "
        + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "					where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location  "
        + "					  "
        + "					union  "
        + "					  "
        + "					  "
        + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio   "
        + "				  FROM 		patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "				  WHERE		p.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "				  GROUP BY 	p.patient_id				  "
        + "					  "
        + "					  "
        + "				) inicio  "
        + "			group by patient_id	  "
        + "	)inicio1  "
        + "	where data_inicio between :startDate and :endDate  "
        + ") inicio_real  "
        + "inner join encounter e on e.patient_id=inicio_real.patient_id  "
        + "where 	e.voided=0 and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ","
        + arvPharmaciaEncounterType
        + ") and e.location_id=:location and   "
        + "		e.encounter_datetime between inicio_real.data_inicio and date_add(inicio_real.data_inicio, interval 33 day) and   "
        + "		inicio_real.patient_id not in   "
        + "		(  "
        + "			select 	pg.patient_id  "
        + "			from 	patient p   "
        + "					inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
        + "			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and   "
        + "					pg.program_id="
        + artProgram
        + " and ps.state="
        + pateintTransferedFromOtherFacilityWorkflowState
        + " and ps.start_date=pg.date_enrolled and   "
        + "					ps.start_date between :startDate and :endDate and location_id=:location  "
        + "		)  "
        + "group by inicio_real.patient_id  "
        + "having min(e.encounter_datetime)<max(e.encounter_datetime)";
  }
  /**
   * MQ_PACIENTES QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTOS DENTRO DE 3 MESES
   * DEPOIS DE INICIO DE TARV - NOVO São pacientes que iniciaram tarv num determinado período e que
   * tiveram 3 consultas clínicas ou levantamentos nos primeiros 3 meses depois de inicio de tarv. A
   * tolerância é de 9 dias. A consulta que coincide com a data de inicio de tarv não é contada.
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @return
   */
  public static String getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram) {

    return "select inicio_real.patient_id  "
        + "from  "
        + "(	select patient_id,data_inicio  "
        + "	from  "
        + "	(	Select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "				(	Select 	p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "					from 	patient p   "
        + "							inner join encounter e on p.patient_id=e.patient_id	  "
        + "							inner join obs o on o.encounter_id=e.encounter_id  "
        + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and   "
        + "							e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and   "
        + "							e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "			  "
        + "					union  "
        + "			  "
        + "					Select 	p.patient_id,min(value_datetime) data_inicio  "
        + "					from 	patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "							inner join obs o on e.encounter_id=o.encounter_id  "
        + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and   "
        + "							o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and   "
        + "							o.value_datetime<=:endDate and e.location_id=:location  "
        + "					group by p.patient_id  "
        + "  "
        + "					union  "
        + "  "
        + "					select 	pg.patient_id,date_enrolled data_inicio  "
        + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "					where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location  "
        + "					  "
        + "					union  "
        + "					  "
        + "					  "
        + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio   "
        + "				  FROM 		patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id  "
        + "				  WHERE		p.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "				  GROUP BY 	p.patient_id				  "
        + "					  "
        + "					  "
        + "				) inicio  "
        + "			group by patient_id	  "
        + "	)inicio1  "
        + "	where data_inicio between :startDate and  :endDate  "
        + ") inicio_real  "
        + "inner join encounter e on e.patient_id=inicio_real.patient_id  "
        + "where 	e.voided=0 and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ","
        + arvPharmaciaEncounterType
        + ") and e.location_id=:location and   "
        + "		e.encounter_datetime between date_add(inicio_real.data_inicio, interval 1 day) and date_add(inicio_real.data_inicio, interval 99 day)  "
        + "group by inicio_real.patient_id  "
        + "having count(distinct e.encounter_datetime)>=3";
  }
  /**
   * PACIENTES QUE TIVERAM PELO MENOS 3 AVALIAÇÕES DE ADESÃO DENTRO DE 3 MESES DEPOIS DE INICIO DE
   * TARV - ARIEL São pacientes que tiveram 3 avaliações de adesão (Ficha de apoio psicossocial e
   * PP: Actividade=Seguimento de Aconselhamento) dentro de 3 meses depois de inicio de TARV. A
   * avaliação de adesão feita no mesmo dia de inicio de TARV não é contada.
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @param coucelingActivityConcept
   * @param prevencaoPositivaInicialEncounterType
   * @param prevencaoPositivaSeguimentoEncounterType
   * @param adherenceCounselingConcept
   * @return String
   */
  public static String getPatientWhoAtLeast3JoiningEvaluationWithin3MothsARIEL(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram,
      int coucelingActivityConcept,
      int prevencaoPositivaInicialEncounterType,
      int prevencaoPositivaSeguimentoEncounterType,
      int adherenceCounselingConcept) {

    return "select inicio_real.patient_id  "
        + "from  "
        + "(	select patient_id,data_inicio  "
        + "	from  "
        + "	(	Select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "		(	Select p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "			from patient p   "
        + "			inner join encounter e on p.patient_id=e.patient_id	  "
        + "			inner join obs o on o.encounter_id=e.encounter_id  "
        + "			where 	e.voided=0 and o.voided=0 and p.voided=0   "
        + "			and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "			and o.concept_id="
        + arvPlan
        + "   "
        + "			and o.value_coded="
        + startDrugsConcept
        + "   "
        + "			and e.encounter_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "	  "
        + "			union  "
        + "	  "
        + "			Select p.patient_id,min(value_datetime) data_inicio  "
        + "			from patient p  "
        + "			inner join encounter e on p.patient_id=e.patient_id  "
        + "			inner join obs o on e.encounter_id=o.encounter_id  "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0   "
        + "			and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "			and o.concept_id="
        + arvStartDateConcept
        + "   "
        + "			and o.value_datetime is not null   "
        + "			and o.value_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "  "
        + "			union  "
        + "  "
        + "			select pg.patient_id,date_enrolled data_inicio  "
        + "			from patient p   "
        + "			inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "			where pg.voided=0 and p.voided=0   "
        + "			and program_id="
        + artProgram
        + "   "
        + "			and date_enrolled<=:endDate   "
        + "			and location_id=:location  "
        + "			  "
        + "			union  "
        + "						  "
        + "			select e.patient_id, MIN(e.encounter_datetime) AS data_inicio   "
        + "			FROM patient p  "
        + "			inner join encounter e on p.patient_id=e.patient_id  "
        + "			where p.voided=0 and e.voided=0   "
        + "			and e.encounter_type="
        + arvPharmaciaEncounterType
        + "    "
        + "			and e.encounter_datetime<=:endDate   "
        + "			and e.location_id=:location  "
        + "			group by p.patient_id	  "
        + "		) inicio  "
        + "		group by patient_id	  "
        + "	)inicio1  "
        + "	where data_inicio between :startDate and  :endDate  "
        + ") inicio_real  "
        + "inner join encounter e on e.patient_id=inicio_real.patient_id  "
        + "inner join obs o on o.encounter_id=e.encounter_id  "
        + "where e.voided=0 and o.voided=0   "
        + "and e.encounter_type in ("
        + prevencaoPositivaInicialEncounterType
        + ","
        + prevencaoPositivaSeguimentoEncounterType
        + ")   "
        + "and e.location_id=:location   "
        + "and o.concept_id="
        + coucelingActivityConcept
        + "   "
        + "and o.value_coded="
        + adherenceCounselingConcept
        + "  "
        + "and e.encounter_datetime between inicio_real.data_inicio and date_add(inicio_real.data_inicio, interval 99 day)   "
        + "and e.encounter_datetime>inicio_real.data_inicio  "
        + "group by inicio_real.patient_id  "
        + "having if(:testStart='true',count(*)>=4,count(*)>=3)";
  }

  /**
   * MQ_PACIENTES COM CONSULTAS MENSAIS APOS INICIO DE TARV Sao pacientes que tem consultas mensais
   * durante um periodo contado a partir da data de início do TARV: O Calculo de consultas mensais
   * é: O numero de consultas no periodo deve ser superior ou igual ao numero de meses desse periodo
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @return String
   */
  public static String getPacientesWithMonthEncountersAfterInitialization(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram) {

    return "select domain.patient_id "
        + "from "
        + "( "
        + "	select inicio_real.patient_id, inicio_real.data_inicio "
        + "	from "
        + "	(	select patient_id,data_inicio "
        + "		from "
        + "			(	Select patient_id,min(data_inicio) data_inicio "
        + "				from "
        + "				(	Select 	p.patient_id,min(e.encounter_datetime) data_inicio "
        + "					from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id	 "
        + "					inner join obs o on o.encounter_id=e.encounter_id "
        + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "					e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and  "
        + "					e.encounter_datetime<=:endDate and e.location_id=:location "
        + "					group by p.patient_id "
        + "			 "
        + "					union "
        + "			 "
        + "					Select 	p.patient_id,min(value_datetime) data_inicio "
        + "					from 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "					o.value_datetime<=:endDate and e.location_id=:location "
        + "					group by p.patient_id "
        + "	 "
        + "					union "
        + "	 "
        + "					select 	pg.patient_id,date_enrolled data_inicio "
        + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "					where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "					 "
        + "					union "
        + "					 "
        + "					 "
        + "					SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio  "
        + "					FROM 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					WHERE	p.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
        + "					GROUP BY 	p.patient_id "
        + "						 "
        + "					 "
        + "					 "
        + "				) inicio "
        + "				group by patient_id	 "
        + "			)inicio1 "
        + "		where data_inicio between :startDate and :endDate "
        + "	)inicio_real "
        + ""
        + "	inner join "
        + "	( "
        + "		select p.patient_id, e.encounter_datetime data_consulta "
        + "		from 	patient p  "
        + "		inner join encounter e on p.patient_id=e.patient_id "
        + "		where 	p.voided=0 and e.voided=0 and e.encounter_datetime between :startDate and :revisionEndDate and  "
        + "		e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") "
        + "	) consulta on inicio_real.patient_id = consulta.patient_id and consulta.data_consulta > inicio_real.data_inicio "
        + "	group by inicio_real.patient_id "
        + "	having count(*)>=round(datediff(:revisionEndDate,inicio_real.data_inicio)/30)-1 "
        + ") domain";
  }

  /**
   * MQ_PACIENTES COM CONSULTAS MENSAIS DE APSS APOS INICIO DE TARV Sao pacientes que tem consultas
   * mensais de APSS durante um período contado a partir da data de início do TARV: O Cálculo de
   * consultas mensais é: O número de consultas no período deve ser superior ou igual ao numero de
   * meses desse período. Para pacientes com idade superior a 2 anos, 3 consultas nos primeiros 3
   * meses após início TARV.
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @param coucelingActivityConcept
   * @param prevencaoPositivaInicialEncounterType
   * @param prevencaoPositivaSeguimentoEncounterType
   * @param adherenceCounselingConcept
   * @return
   */
  public static String getPatientsWithAPSSMonthEncountersAfterTARVInitialization(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram,
      int coucelingActivityConcept,
      int prevencaoPositivaInicialEncounterType,
      int prevencaoPositivaSeguimentoEncounterType,
      int adherenceCounselingConcept) {

    return "select patient_id  "
        + "from(  "
        + "	select inicio_real.patient_id, inicio_real.data_inicio,pe.birthdate  "
        + "	from  "
        + "	(	select patient_id,data_inicio  "
        + "		from  "
        + "		(	Select patient_id,min(data_inicio) data_inicio  "
        + "			from  "
        + "			(	Select p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "				from patient p   "
        + "				inner join encounter e on p.patient_id=e.patient_id	  "
        + "				inner join obs o on o.encounter_id=e.encounter_id  "
        + "				where 	e.voided=0 and o.voided=0 and p.voided=0   "
        + "				and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "				and o.concept_id="
        + arvPlan
        + "  "
        + "				and o.value_coded="
        + startDrugsConcept
        + "   "
        + "				and e.encounter_datetime<=:endDate  "
        + "				and e.location_id=:location  "
        + "				group by p.patient_id  "
        + "		  "
        + "				union  "
        + "		  "
        + "				Select p.patient_id,min(value_datetime) data_inicio  "
        + "				from patient p  "
        + "				inner join encounter e on p.patient_id=e.patient_id  "
        + "				inner join obs o on e.encounter_id=o.encounter_id  "
        + "				where 	p.voided=0 and e.voided=0 and o.voided=0   "
        + "				and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ")   "
        + "				and o.concept_id="
        + arvStartDateConcept
        + "   "
        + "				and o.value_datetime is not null   "
        + "				and o.value_datetime<=:endDate  "
        + "				and e.location_id=:location  "
        + "				group by p.patient_id  "
        + "	  "
        + "				union  "
        + "	  "
        + "				select pg.patient_id,date_enrolled data_inicio  "
        + "				from patient p   "
        + "				inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "				where pg.voided=0 and p.voided=0   "
        + "				and program_id="
        + artProgram
        + "   "
        + "				and date_enrolled<=:endDate  "
        + "				and location_id=:location  "
        + "				  "
        + "				union  "
        + "							  "
        + "				select e.patient_id, min(e.encounter_datetime) AS data_inicio   "
        + "				from patient p  "
        + "				inner join encounter e on p.patient_id=e.patient_id  "
        + "				where p.voided=0 and e.voided=0   "
        + "				and e.encounter_type="
        + arvPharmaciaEncounterType
        + "    "
        + "				and e.encounter_datetime<=:endDate   "
        + "				and e.location_id=:location  "
        + "				group by p.patient_id	  "
        + "			) inicio  "
        + "			group by patient_id	  "
        + "		)inicio1  "
        + "		where data_inicio between :startDate and  :endDate  "
        + "	) inicio_real  "
        + "	inner join person pe on inicio_real.patient_id=pe.person_id  "
        + "	left join  "
        + "	(  "
        + "		Select p.patient_id, e.encounter_datetime data_consulta  "
        + "		from patient p  "
        + "		inner join encounter e on e.patient_id=p.patient_id  "
        + "		inner join obs o on o.encounter_id=e.encounter_id  "
        + "		where p.voided=0 and e.voided=0 and o.voided=0  "
        + "		and e.encounter_type in ("
        + prevencaoPositivaInicialEncounterType
        + ","
        + prevencaoPositivaSeguimentoEncounterType
        + ")  "
        + "		and o.concept_id="
        + coucelingActivityConcept
        + "   "
        + "		and o.value_coded="
        + adherenceCounselingConcept
        + "   "
        + "		and e.location_id=:location  "
        + "	) seguimentos3 on inicio_real.patient_id=seguimentos3.patient_id   "
        + "		and seguimentos3.data_consulta between date_add(inicio_real.data_inicio, interval 1 day) and date_add(inicio_real.data_inicio, interval 99 day)  "
        + "		and round(datediff(inicio_real.data_inicio,pe.birthdate)/365)>2  "
        + "	left join  "
        + "	(  "
        + "		select p.patient_id, e.encounter_datetime data_consulta  "
        + "		from patient p   "
        + "		inner join encounter e on p.patient_id=e.patient_id  "
        + "		inner join obs o on o.encounter_id=e.encounter_id  "
        + "		where p.voided=0 and e.voided=0 and o.voided=0  "
        + "		and e.encounter_type in ("
        + prevencaoPositivaInicialEncounterType
        + ","
        + prevencaoPositivaSeguimentoEncounterType
        + ")  "
        + "		and o.concept_id="
        + coucelingActivityConcept
        + "   "
        + "		and o.value_coded="
        + adherenceCounselingConcept
        + "   "
        + "		and e.location_id=:location  "
        + "	) seguimentos_mensais on inicio_real.patient_id=seguimentos_mensais.patient_id   "
        + "		and seguimentos_mensais.data_consulta between date_add(inicio_real.data_inicio, interval 1 day) and :revisionEndDate  "
        + "		and round(datediff(data_inicio,pe.birthdate)/365)<=2  "
        + "	group by inicio_real.patient_id  "
        + "	having count(seguimentos3.data_consulta)>=3  "
        + "		or count(seguimentos_mensais.data_consulta)>=round(datediff(:revisionEndDate,inicio_real.data_inicio)/30)-1  "
        + ") domain";
  }
  /**
   * PACIENTES INSCRITOS NO GAAC NUM PERIODO Sao pacientes que foram inscritos num grupo GAAC
   * durante um determinado periodo
   *
   * @return
   */
  public static String getPatientsEnrolledInGaacInAPeriod() {

    return "Select gm.member_id "
        + "from gaac g inner join gaac_member gm on g.gaac_id=gm.gaac_id "
        + "where gm.start_date between :startDate and :endDate and gm.voided=0 and g.voided=0 and location_id=:location";
  }
  /**
   * PACIENTES COM CD4>200 OU CV<1000 NOS ULTIMOS 12 MESES São pacientes com último CD4>200 ou
   * última carga viral<1000 nos últimos 12 meses
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param misauLaboratorioEncounterType
   * @param cd4AbsoluteConcept
   * @param cd4AbsoluteOBSConcept
   * @param hivViralLoadConcept
   * @return query
   */
  public static String getPatientsWithCD4GreterThan200ORCVLessThan1000InLast12Months(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int misauLaboratorioEncounterType,
      int cd4AbsoluteConcept,
      int cd4AbsoluteOBSConcept,
      int hivViralLoadConcept) {

    return "Select ultima_carga.patient_id "
        + "from "
        + "(	Select 	p.patient_id,max(o.obs_datetime) data_carga "
        + "	from 	patient p "
        + "	inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + misauLaboratorioEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "	o.concept_id in ("
        + cd4AbsoluteConcept
        + ","
        + cd4AbsoluteOBSConcept
        + ") and o.value_numeric is not null and  "
        + "	e.encounter_datetime between date_add(:startDate, interval -12 MONTH) and :startDate and e.location_id=:location "
        + "	group by p.patient_id "
        + ") ultima_carga "
        + "inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
        + "where 	obs.voided=0 and obs.concept_id in ("
        + cd4AbsoluteConcept
        + ","
        + cd4AbsoluteOBSConcept
        + ") and obs.location_id=:location and obs.value_numeric>200 "
        + " "
        + "union "
        + " "
        + "Select ultima_carga.patient_id "
        + "from "
        + "(	Select 	p.patient_id,max(o.obs_datetime) data_carga "
        + "	from 	patient p "
        + "	inner join encounter e on p.patient_id=e.patient_id "
        + "	inner join obs o on e.encounter_id=o.encounter_id "
        + "	where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + misauLaboratorioEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "	o.concept_id="
        + hivViralLoadConcept
        + " and o.value_numeric is not null and  "
        + "	e.encounter_datetime between date_add(:startDate, interval -12 MONTH) and :startDate and e.location_id=:location "
        + "	group by p.patient_id "
        + ") ultima_carga "
        + "inner join obs on obs.person_id=ultima_carga.patient_id and obs.obs_datetime=ultima_carga.data_carga "
        + "where 	obs.voided=0 and obs.concept_id="
        + hivViralLoadConcept
        + " and obs.location_id=:location and obs.value_numeric<1000";
  }
  /**
   * PACIENTES QUE ESTAO HA MAIS DE 6 MESES EM TARV Sao pacientes que iniciaram tarv ha mais de 6
   * meses
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @return query
   */
  public static String getPatientsInTARVMoreThan6Months(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram) {

    return "select patient_id  "
        + "from  "
        + "(	select patient_id,min(data_inicio) data_inicio  "
        + "		from  "
        + "		(  "
        + "			Select 	p.patient_id,min(e.encounter_datetime) data_inicio  "
        + "			from 	patient p   "
        + "					inner join encounter e on p.patient_id=e.patient_id	  "
        + "					inner join obs o on o.encounter_id=e.encounter_id  "
        + "			where 	e.voided=0 and o.voided=0 and p.voided=0 and   "
        + "					e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and   "
        + "					e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "			  "
        + "			union  "
        + "		  "
        + "			Select p.patient_id,min(value_datetime) data_inicio  "
        + "			from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id  "
        + "					inner join obs o on e.encounter_id=o.encounter_id  "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and   "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and   "
        + "					o.value_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "			  "
        + "			union  "
        + "			  "
        + "			select 	pg.patient_id,date_enrolled as data_inicio  "
        + "			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "			where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and   "
        + "					pg.date_enrolled<=:endDate and pg.location_id=:location  "
        + "  "
        + "			union  "
        + "  "
        + "			select 	p.patient_id,min(encounter_datetime) as data_inicio  "
        + "			from 	patient p inner join encounter e on p.patient_id=e.patient_id  "
        + "			where 	p.voided=0 and e.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id  "
        + "		) inicio  "
        + "	group by patient_id  "
        + ")inicio_real  "
        + "where timestampdiff (MONTH,data_inicio,:endDate)>=6;";
  }

  /**
   * PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) - PERIODO FINAL Sao pacientes
   * inscritos no programa de tratamento ARV até um determinado periodo final
   *
   * @param artProgram
   * @return query
   */
  public static String getPatientEnrolledARTProgramFinalPeriod(int artProgram) {
    String query =
        "select 	pg.patient_id  "
            + "from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "where 	pg.voided=0 and p.voided=0 and program_id=%d and date_enrolled<=:endDate and location_id=:location";
    return String.format(query, artProgram);
  }
  /**
   * PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE TRATAMENTO ARV: PERIODO FINAL São pacientes que
   * sairam do programa de tratamento ARV até um determinado periodo final. Inclui todo tipo de
   * saída: ABANDONO, OBITO, TRANSFERIDO PARA e SUSPENSO
   *
   * @param artProgram
   */
  public static String getPatientWhoCameOutARTProgramFinalPeriod(
      int artProgram,
      int transferredOutToAnotherHealthFacilityWorkflowState,
      int suspendedTreatmentWorkflowState,
      int abandonedWorkflowState,
      int patientHasDiedWorkflowState) {
    String query =
        "select 	pg.patient_id  "
            + "			from 	patient p   "
            + "					inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
            + "			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and   "
            + "					pg.program_id=%d and ps.state in (%d,%d,%d,%d) and ps.end_date is null and   "
            + "					ps.start_date<=:endDate and location_id=:location";
    return String.format(
        query,
        artProgram,
        transferredOutToAnotherHealthFacilityWorkflowState,
        suspendedTreatmentWorkflowState,
        abandonedWorkflowState,
        patientHasDiedWorkflowState);
  }

  /**
   * ABANDONO NÃO NOTIFICADO - TARV São pacientes que desde a ultima data marcada para levantamento
   * até a data final passam mais de 60 dias sem voltar e que ainda não foram notificados como
   * Abandono.
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param returnVisitDateForArvDrugConcept
   * @param artProgram
   * @param returnVisitDateConcept
   */
  public static String getPatientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int returnVisitDateForArvDrugConcept,
      int artProgram,
      int returnVisitDateConcept,
      int transferredOutToAnotherHealthFacilityWorkflowState,
      int suspendedTreatmentWorkflowState,
      int abandonedWorkflowState,
      int patientHasDiedWorkflowState) {

    return "select patient_id  "
        + "from  "
        + "		(	Select 	p.patient_id,max(encounter_datetime) encounter_datetime  "
        + "			from 	patient p   "
        + "					inner join encounter e on e.patient_id=p.patient_id  "
        + "			where 	p.voided=0 and e.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " and   "
        + "					e.location_id=:location and e.encounter_datetime<=:endDate  "
        + "			group by p.patient_id  "
        + "		) max_frida   "
        + "		inner join obs o on o.person_id=max_frida.patient_id  "
        + "where 	max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id="
        + returnVisitDateForArvDrugConcept
        + " and o.location_id=:location and   "
        + "		patient_id not in 	  "
        + "		(	select 	pg.patient_id  "
        + "			from 	patient p   "
        + "					inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
        + "			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and   "
        + "					pg.program_id="
        + artProgram
        + " and ps.state in ("
        + transferredOutToAnotherHealthFacilityWorkflowState
        + ","
        + suspendedTreatmentWorkflowState
        + ","
        + abandonedWorkflowState
        + ","
        + patientHasDiedWorkflowState
        + ") and ps.end_date is null and   "
        + "					ps.start_date<=:endDate and location_id=:location  "
        + "  "
        + "		) and patient_id not in(  "
        + "			  "
        + "			select patient_id  "
        + "			from  "
        + "				(	Select 	p.patient_id,max(encounter_datetime) encounter_datetime  "
        + "					from 	patient p   "
        + "							inner join encounter e on e.patient_id=p.patient_id  "
        + "					where 	p.voided=0 and e.voided=0 and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "							e.location_id=:location and e.encounter_datetime<=:endDate  "
        + "					group by p.patient_id  "
        + "				) max_mov  "
        + "				inner join obs o on o.person_id=max_mov.patient_id  "
        + "			where 	max_mov.encounter_datetime=o.obs_datetime and o.voided=0 and   "
        + "				o.concept_id="
        + returnVisitDateConcept
        + " and o.location_id=:location and datediff(:endDate,o.value_datetime)<=60  "
        + "				  "
        + "		) and patient_id not in(			  "
        + "			  "
        + "			select abandono.patient_id  "
        + "			from   "
        + "			(		  "
        + "				select 	pg.patient_id  "
        + "				from 	patient p   "
        + "						inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "						inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
        + "				where 	pg.voided=0 and ps.voided=0 and p.voided=0 and   "
        + "						pg.program_id="
        + artProgram
        + " and ps.state="
        + abandonedWorkflowState
        + " and ps.end_date is null and   "
        + "						ps.start_date<=:endDate and location_id=:location  "
        + "			)abandono  "
        + "			inner join 	  "
        + "			(	  "
        + "				select max_frida.patient_id,max_frida.encounter_datetime,o.value_datetime  "
        + "				from  "
        + "				(	Select 	p.patient_id,max(encounter_datetime) encounter_datetime  "
        + "					from 	patient p   "
        + "							inner join encounter e on e.patient_id=p.patient_id  "
        + "					where 	p.voided=0 and e.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " and   "
        + "							e.location_id=:location and e.encounter_datetime<=:endDate  "
        + "					group by p.patient_id  "
        + "				) max_frida   "
        + "				inner join obs o on o.person_id=max_frida.patient_id  "
        + "				where 	max_frida.encounter_datetime=o.obs_datetime and o.voided=0 and o.concept_id="
        + returnVisitDateForArvDrugConcept
        + " and o.location_id=:location  "
        + "			) ultimo_fila on abandono.patient_id=ultimo_fila.patient_id  "
        + "			where datediff(:endDate,ultimo_fila.value_datetime)<60  "
        + "			  "
        + "		)  "
        + "		and datediff(:endDate,o.value_datetime)>=60;";
  }

  /**
   * GRAVIDAS INSCRITAS NO SERVIÇO TARV São pacientes que estão gravidas durante a abertura do
   * processo ou durante o seguimento no serviço TARV e que foi notificado como nova gravidez
   * durante o seguimemento.
   *
   * @param pregnantConcept
   * @param pregnancyDueDateConcept
   * @param numberOfWeekPregnant
   * @param arvAdultInitialEncounterType
   * @param adultoSeguimentoEncounterType
   * @param ptvEtvProgram
   * @return
   */
  public static String getPregnantPatientEnrolledInTARVService(
      int pregnantConcept,
      int pregnancyDueDateConcept,
      int numberOfWeekPregnant,
      int arvAdultInitialEncounterType,
      int adultoSeguimentoEncounterType,
      int ptvEtvProgram,
      int yesConcept) {

    return "Select 	p.patient_id  "
        + "from 	patient p   "
        + "		inner join encounter e on p.patient_id=e.patient_id  "
        + "		inner join obs o on e.encounter_id=o.encounter_id  "
        + "where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnantConcept
        + " and value_coded="
        + yesConcept
        + " and   "
        + "		e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location  "
        + "  "
        + "union		  "
        + "		  "
        + "Select 	p.patient_id  "
        + "from 	patient p inner join encounter e on p.patient_id=e.patient_id  "
        + "		inner join obs o on e.encounter_id=o.encounter_id  "
        + "where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + numberOfWeekPregnant
        + " and   "
        + "		e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location  "
        + "  "
        + "  "
        + "union		  "
        + "		  "
        + "Select 	p.patient_id  "
        + "from 	patient p inner join encounter e on p.patient_id=e.patient_id  "
        + "		inner join obs o on e.encounter_id=o.encounter_id  "
        + "where 	p.voided=0 and e.voided=0 and o.voided=0 and concept_id="
        + pregnancyDueDateConcept
        + " and   "
        + "		e.encounter_type in ("
        + arvAdultInitialEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ") and e.encounter_datetime between :startDate and :endDate and e.location_id=:location		  "
        + "		  "
        + "union  "
        + "		  "
        + "select 	pp.patient_id  "
        + "from 	patient_program pp   "
        + "where 	pp.program_id="
        + ptvEtvProgram
        + " and pp.voided=0 and   "
        + "		pp.date_enrolled between :startDate and :endDate and pp.location_id=:location";
  }

  /**
   * PROGRAMA: PACIENTES QUE DERAM PARTO HÁ DOIS ANOS ATRÁS DA DATA DE REFERENCIA - LACTANTES São
   * pacientes inscritos no programa de PTV e que foram actualizados como parto num periodo de 2
   * anos atrás da data de referencia
   *
   * @param ptvEtvProgram
   */
  public static String getPatientWithDeliveryDate2YearsAgoBreatFeeding(
      int ptvEtvProgram, int patientIsBreastfeedingWorkflowState) {
    String query =
        "select 	pg.patient_id  "
            + "from 	patient p   "
            + "		inner join patient_program pg on p.patient_id=pg.patient_id  "
            + "		inner join patient_state ps on pg.patient_program_id=ps.patient_program_id  "
            + "where 	pg.voided=0 and ps.voided=0 and p.voided=0 and   "
            + "		pg.program_id=%d and ps.state=%d and ps.end_date is null and   "
            + "		ps.start_date between date_add(:startDate, interval -2 year) "
            + " and date_add(:startDate, interval -1 day) and location_id=:location ";

    return String.format(query, ptvEtvProgram, patientIsBreastfeedingWorkflowState);
  }

  /**
   * PACIENTES COM PELO MENOS UMA CONSULTA CLINICA NUM DETERMINADO PERIODO São pacientes que têm
   * pelo menos uma consulta clínica (seguimento) num determinado periodo
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @return
   */
  public static String getPatientWithAtLeastOneEncounterInPeriod(
      int adultoSeguimentoEncounterType, int arvPediatriaSeguimentoEncounterType) {
    String query =
        "Select 	e.patient_id  "
            + "from 	patient p   "
            + "		inner join encounter e on e.patient_id=p.patient_id  "
            + "where 	p.voided=0 and e.voided=0 and e.encounter_type in (%d,%d) and   "
            + "		e.encounter_datetime between :startDate and :endDate and e.location_id=:location";

    return String.format(query, adultoSeguimentoEncounterType, arvPediatriaSeguimentoEncounterType);
  }

  /**
   * MQ_PACIENTES QUE INICIARAM TRATAMENTO DE TUBERCULOSE E NAO TERMINARAM ATE PERIODO FINAL
   *
   * <p>São pacientes que iniciaram tratamento de TB e não terminaram até o início do período de
   * revisão
   */
  public static String getPatientsStartTuberculoseTreatmentNotComplete(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int tbLivroEncounterType,
      int tbRastreioEncounterType,
      int tbDrugTreatmentStartDate,
      int tbDgrusTreatmentEndDateConcept,
      int tbProgram) {

    return "select inicio_tb.patient_id  "
        + "from  "
        + "(	Select 	p.patient_id,max(o.value_datetime) data_inicio_tb  "
        + "	from 	patient p   "
        + "	inner join encounter e on e.patient_id=p.patient_id  "
        + "	inner join obs o on o.encounter_id=e.encounter_id  "
        + "	where 	p.voided=0 and e.voided=0 and o.value_datetime <= :endDate and "
        + "	e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ","
        + tbLivroEncounterType
        + ","
        + tbRastreioEncounterType
        + ") and o.voided=0 and o.concept_id="
        + tbDrugTreatmentStartDate
        + "  "
        + "	group by patient_id  "
        + "  "
        + "	union  "
        + "  "
        + "	select 	pg.patient_id,max(date_enrolled) data_inicio_tb  "
        + "	from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "	where 	pg.voided=0 and p.voided=0 and program_id="
        + tbProgram
        + " and date_enrolled <= :endDate and location_id=:location  "
        + "	group by patient_id  "
        + ") inicio_tb  "
        + "where inicio_tb.patient_id not in  "
        + "(  "
        + "	select fim_tb.patient_id  "
        + "	from  "
        + "	(	Select 	p.patient_id,max(o.value_datetime) data_fim_tb  "
        + "		from 	patient p   "
        + "		inner join encounter e on e.patient_id=p.patient_id  "
        + "		inner join obs o on o.encounter_id=e.encounter_id  "
        + "		where 	p.voided=0 and e.voided=0 and o.value_datetime <= :endDate and "
        + "	e.location_id=:location and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ","
        + tbLivroEncounterType
        + ","
        + tbRastreioEncounterType
        + ") and o.voided=0 and o.concept_id="
        + tbDgrusTreatmentEndDateConcept
        + "  "
        + "		group by patient_id  "
        + "	  "
        + "		union  "
        + "	  "
        + "		select 	pg.patient_id,max(date_completed) data_fim_tb  "
        + "		from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id  "
        + "		where 	pg.voided=0 and p.voided=0 and program_id="
        + tbProgram
        + " and date_enrolled is not null and   "
        + "		date_completed is not null and date_completed <= :endDate and location_id=:location  "
        + "		group by patient_id  "
        + "	) fim_tb  "
        + "	where inicio_tb.data_inicio_tb < fim_tb.data_fim_tb  "
        + ")";
  }

  /**
   * NOTIFICACÃO DE SARCOMA DE KAPOSI São pacientes que foram notificados sarcoma de kaposi durante
   * o periodo de reportagem
   */
  public static String getPatientsNotifiedSarcomaKaposi(
      int stage4PediatricMozambique,
      int adultClinicalHistoryUuid,
      int skimExamFindingsConcept,
      int extremityExamFindingsConcept,
      int stateAdultMozambiqueConcept,
      int kaposiSarcomaConcept) {

    String query =
        "select  p.patient_id  "
            + "from 	patient p   "
            + "		inner join encounter e on e.patient_id=p.patient_id                     "
            + "		inner join obs o on o.encounter_id=e.encounter_id  "
            + "where 	o.obs_datetime between :startDate and :endDate and o.location_id=:location and   "
            + "		o.voided=0 and e.voided=0 and p.voided=0 and  "
            + "		o.concept_id in (%d,%d,%d,%d,%d) and value_coded=%d";

    return String.format(
        query,
        stage4PediatricMozambique,
        adultClinicalHistoryUuid,
        skimExamFindingsConcept,
        extremityExamFindingsConcept,
        stateAdultMozambiqueConcept,
        kaposiSarcomaConcept);
  }

  /**
   * PACIENTES QUE TIVERAM CONSULTA CLINICA NOS ULTIMOS 7 MESES E QUE FORAM MARCADOS PARA CONSULTA
   * SEGUINTE PARA 6 MESES São pacientes que visitaram a US nos últimos 7 meses que foram marcados
   * para consulta seguinte para 6 meses. Portanto a diferenca em dias entre a data da consulta e a
   * data marcada está entre 175 a 190 dias
   */
  public static String getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int returnVisitDateConcept) {
    String query =
        "select ultimaConsulta.patient_id  "
            + "from  "
            + "	(	Select 	p.patient_id,max(e.encounter_datetime) data_consulta  "
            + "		from 	patient p  "
            + "				inner join encounter e on p.patient_id=e.patient_id  "
            + "		where 	p.voided=0 and e.voided=0 and e.encounter_type in (%d,%d) and   "
            + "				e.encounter_datetime between date_add(:endDate, interval -7 MONTH) and :endDate and e.location_id=:location  "
            + "		group by p.patient_id  "
            + "	)ultimaConsulta inner join obs o on o.person_id=ultimaConsulta.patient_id  "
            + "where 	ultimaConsulta.data_consulta=o.obs_datetime and o.concept_id=%d and o.voided=0 and o.location_id=:location and   "
            + "		datediff(o.value_datetime,ultimaConsulta.data_consulta) between 175 and 190";

    return String.format(
        query,
        adultoSeguimentoEncounterType,
        arvPediatriaSeguimentoEncounterType,
        returnVisitDateConcept);
  }

  /**
   * PACIENTES QUE TIVERAM LEVANTAMENTO DE ARVs NOS ULTIMOS 5 MESES E QUE FORAM MARCADOS PARA
   * PROXIMO LEVANTAMENTO PARA 3 MESES Pacientes que tiveram levantamento de ARVs nos ultimos 5
   * meses e que foram marcados para proximo levantamento entre 83-97 dias depois
   */
  public static String getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter(
      int arvPharmaciaEncounterType, int returnVisitDateForArvDrugConcept) {
    String query =
        "select levantamento.patient_id  "
            + "from  "
            + "	(	Select 	p.patient_id,max(e.encounter_datetime) data_levantamento  "
            + "		from 	patient p  "
            + "				inner join encounter e on p.patient_id=e.patient_id  "
            + "		where 	p.voided=0 and e.voided=0 and e.encounter_type=%d and   "
            + "				e.encounter_datetime between date_add(:endDate, interval -5 MONTH) and :endDate and e.location_id=:location  "
            + "		group by p.patient_id  "
            + "	)levantamento inner join obs o on o.person_id=levantamento.patient_id  "
            + "where 	levantamento.data_levantamento=o.obs_datetime and o.concept_id=%d and o.voided=0 and o.location_id=:location and   "
            + "		datediff(o.value_datetime,levantamento.data_levantamento) between 83 and 97 ";
    return String.format(query, arvPharmaciaEncounterType, returnVisitDateForArvDrugConcept);
  }
  /**
   * MQ_PACIENTES QUE RECEBERAM RESULTADO DA CARGA VIRAL ENTRE O SEXTO E NONO MES DEPOIS DE INICIO
   * DE TARV São pacientes que iniciaram TARV nos primeiros 3 meses voltando 12 meses da data de
   * avaliação e que tiveram resultado de carga viral entre o sexto e nono mês depois da data de
   * inicio de TARV
   *
   * @param adultoSeguimentoEncounterType
   * @param arvPediatriaSeguimentoEncounterType
   * @param arvPharmaciaEncounterType
   * @param arvPlan
   * @param startDrugsConcept
   * @param artProgram
   * @param hivViralLoadConcept
   * @return query
   */
  public static String getPatientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram,
      int hivViralLoadConcept) {

    return "select inicio_real.patient_id "
        + "from "
        + "(	select patient_id,data_inicio "
        + "	from "
        + "	(	Select patient_id,min(data_inicio) data_inicio "
        + "		from "
        + "				(	Select 	p.patient_id,min(e.encounter_datetime) data_inicio "
        + "					from 	patient p  "
        + "							inner join encounter e on p.patient_id=e.patient_id	 "
        + "							inner join obs o on o.encounter_id=e.encounter_id "
        + "					where 	e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "							e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and  "
        + "							e.encounter_datetime<=:endDate and e.location_id=:location "
        + "					group by p.patient_id "
        + "			 "
        + "					union "
        + "			 "
        + "					Select 	p.patient_id,min(value_datetime) data_inicio "
        + "					from 	patient p "
        + "							inner join encounter e on p.patient_id=e.patient_id "
        + "							inner join obs o on e.encounter_id=o.encounter_id "
        + "					where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "							o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "							o.value_datetime<=:endDate and e.location_id=:location "
        + "					group by p.patient_id "
        + " "
        + "					union "
        + " "
        + "					select 	pg.patient_id,date_enrolled data_inicio "
        + "					from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "					where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "					 "
        + "					union "
        + "					 "
        + "					 "
        + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio  "
        + "				  FROM 		patient p "
        + "							inner join encounter e on p.patient_id=e.patient_id "
        + "				  WHERE		p.voided=0 and e.encounter_type="
        + arvPharmaciaEncounterType
        + " AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
        + "				  GROUP BY 	p.patient_id				 "
        + "					 "
        + "					 "
        + "				) inicio "
        + "			group by patient_id	 "
        + "	)inicio1 "
        + "	where data_inicio between date_add(date_add(:endDate, interval -12 month), interval 1 day) and  date_add(:endDate, interval -9 month) "
        + ") inicio_real "
        + "inner join encounter e on e.patient_id=inicio_real.patient_id "
        + "inner join obs o on o.encounter_id=e.encounter_id "
        + "where 	e.voided=0 and e.encounter_type in ("
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and e.location_id=:location and  "
        + "		o.voided=0 and o.concept_id="
        + hivViralLoadConcept
        + " and  "
        + "		e.encounter_datetime between date_add(inicio_real.data_inicio, interval 6 month) and date_add(inicio_real.data_inicio, interval 9 month)";
  }

  /** FALHAS IMUNOLOGICAS - SQL */
  public static String getPatientsWithImmunologicFailture(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int cd4AbsoluteConcept,
      int artProgram) {

    return "Select CD41.patient_id "
        + "from "
        + " "
        + "	(Select CD4_ANTES.patient_id,CD4_ANTES.data_inicio,CD4_ANTES.data_cd4_antes,obs.value_numeric as valor_cd4_antes "
        + "	from "
        + "	(select patient_id,max(data_inicio) data_inicio,max(obs_datetime) as data_cd4_antes "
        + " "
        + "	from "
        + " "
        + "		(	Select 	p.patient_id,min(encounter_datetime)  data_inicio "
        + "			from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and "
        + "					e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "					e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			Select p.patient_id,min(value_datetime) data_inicio "
        + "			from 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "					o.value_datetime<=:endDate and e.location_id=:location "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			select 	pg.patient_id,date_enrolled data_inicio "
        + "			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "			where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "			 "
        + "			 "
        + "		) inicio, "
        + "		(	Select 	o.person_id,o.obs_datetime,o.value_numeric "
        + "			from 	obs o "
        + "			where 	o.concept_id="
        + cd4AbsoluteConcept
        + " and o.voided=0 and o.obs_datetime<=:endDate and o.location_id=:location "
        + "		) cd4 "
        + " "
        + "	where 	cd4.person_id=inicio.patient_id and cd4.obs_datetime<inicio.data_inicio "
        + "	group by patient_id) CD4_ANTES, "
        + "	obs "
        + "	where obs.person_id=CD4_ANTES.patient_id and obs.concept_id="
        + cd4AbsoluteConcept
        + " and obs.obs_datetime=CD4_ANTES.data_cd4_antes and obs.voided=0 and obs.location_id=:location "
        + "	) CD41,	 "
        + "	(Select CD4_DEPOIS.patient_id,CD4_DEPOIS.data_inicio,CD4_DEPOIS.data_cd4_depois,obs.value_numeric as valor_cd4_depois,CD4_DEPOIS.cd4_pico "
        + "	from "
        + "	(select patient_id,max(data_inicio) as data_inicio,max(obs_datetime) data_cd4_depois,max(value_numeric) cd4_pico "
        + "	from "
        + "		(	Select 	p.patient_id,min(encounter_datetime)  data_inicio "
        + "			from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	o.concept_id="
        + arvPlan
        + " and o.value_coded="
        + startDrugsConcept
        + " and "
        + "					e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "					e.encounter_type="
        + arvPharmaciaEncounterType
        + " and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			Select p.patient_id,min(value_datetime) data_inicio "
        + "			from 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "					o.value_datetime<=:endDate and e.location_id=:location "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			select 	pg.patient_id,date_enrolled data_inicio "
        + "			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "			where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "			 "
        + "		) inicio, "
        + "		(	Select 	o.person_id,o.obs_datetime,o.value_numeric "
        + "			from 	obs o "
        + "			where 	o.concept_id="
        + cd4AbsoluteConcept
        + " and o.voided=0 and o.obs_datetime<=:endDate and o.location_id=:location "
        + "		) cd4 "
        + "		where cd4.person_id=inicio.patient_id and cd4.obs_datetime>date_add(inicio.data_inicio,interval 6 month) "
        + "		group by patient_id "
        + "		) CD4_DEPOIS, "
        + "		obs "
        + "		where obs.person_id=CD4_DEPOIS.patient_id and obs.concept_id="
        + cd4AbsoluteConcept
        + " and obs.obs_datetime=CD4_DEPOIS.data_cd4_depois and obs.voided=0 and obs.location_id=:location "
        + "		) CD42, "
        + "		person "
        + "		where CD41.patient_id=CD42.patient_id and person.person_id=CD41.patient_id and  "
        + "		 "
        + "		( "
        + "			(round(datediff(:endDate,birthdate)/365)<=4 and valor_cd4_antes<200 and valor_cd4_depois<200) or "
        + "			(round(datediff(:endDate,birthdate)/365) between 5 and 14 and ((valor_cd4_antes<100 and valor_cd4_depois<100) or (valor_cd4_depois<valor_cd4_antes))) or "
        + "			(round(datediff(:endDate,birthdate)/365)>=15 and ((valor_cd4_antes<100 and valor_cd4_depois<100) or (valor_cd4_depois<valor_cd4_antes) or (valor_cd4_depois<(cd4_pico/2)))) "
        + "		)";
  }
  /** FALHAS CLINICAS - SQL */
  public static String getPatientWithClinicFailtures(
      int adultoSeguimentoEncounterType,
      int arvPediatriaSeguimentoEncounterType,
      int arvPharmaciaEncounterType,
      int arvPlan,
      int startDrugsConcept,
      int arvStartDateConcept,
      int artProgram,
      int transferredFromOtherHealthFacilityWorkflowState,
      int currentWhoHivStageConcept,
      int who2AdultStageConcept,
      int who3AdultStageConcept,
      int who4AdultStageConcept) {

    return "Select ESTADIO1.patient_id "
        + "from "
        + "(	Select estadio_antes.patient_id,estadio_antes.data_inicio,estadio_antes.data_estadio_antes,obs.value_coded as valor_estadio_antes "
        + "	from "
        + "	(select patient_id,max(data_inicio) as data_inicio,max(obs_datetime) data_estadio_antes "
        + " "
        + "	from "
        + " "
        + "		(	Select 	p.patient_id,min(encounter_datetime)  data_inicio "
        + "			from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	o.concept_id="
        + arvPlan
        + " and o.value_coded in ("
        + startDrugsConcept
        + ","
        + transferredFromOtherHealthFacilityWorkflowState
        + ") and "
        + "					e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "					e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			Select p.patient_id,min(value_datetime) data_inicio "
        + "			from 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "					o.value_datetime<=:endDate and e.location_id=:location "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			select 	pg.patient_id,date_enrolled data_inicio "
        + "			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "			where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "			 "
        + "		) inicio, "
        + "		(	Select 	o.person_id,o.obs_datetime "
        + "			from 	obs o "
        + "			where 	o.concept_id="
        + currentWhoHivStageConcept
        + " and o.voided=0 and o.obs_datetime<=:endDate and o.location_id=:location "
        + "		) estadio "
        + " "
        + "	where 	estadio.person_id=inicio.patient_id and estadio.obs_datetime<inicio.data_inicio "
        + "	group by patient_id "
        + "	) estadio_antes, "
        + "	obs  "
        + "	where obs.person_id=estadio_antes.patient_id and obs.concept_id="
        + currentWhoHivStageConcept
        + " and obs.obs_datetime=estadio_antes.data_estadio_antes and obs.voided=0 and obs.location_id=:location "
        + "	) ESTADIO1,	 "
        + "	(select estadio_depois.patient_id,estadio_depois.data_inicio,estadio_depois.data_estadio_depois,obs.value_coded as valor_estadio_depois "
        + "	from "
        + "	(select patient_id,max(data_inicio) as data_inicio,max(obs_datetime) data_estadio_depois "
        + "	from "
        + "		(	Select 	p.patient_id,min(encounter_datetime)  data_inicio "
        + "			from 	patient p  "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	o.concept_id="
        + arvPlan
        + " and o.value_coded in ("
        + startDrugsConcept
        + ","
        + transferredFromOtherHealthFacilityWorkflowState
        + ") and "
        + "					e.voided=0 and o.voided=0 and p.voided=0 and  "
        + "					e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and e.encounter_datetime<=:endDate and e.location_id=:location  "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			Select p.patient_id,min(value_datetime) data_inicio "
        + "			from 	patient p "
        + "					inner join encounter e on p.patient_id=e.patient_id "
        + "					inner join obs o on e.encounter_id=o.encounter_id "
        + "			where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in ("
        + arvPharmaciaEncounterType
        + ","
        + adultoSeguimentoEncounterType
        + ","
        + arvPediatriaSeguimentoEncounterType
        + ") and  "
        + "					o.concept_id="
        + arvStartDateConcept
        + " and o.value_datetime is not null and  "
        + "					o.value_datetime<=:endDate and e.location_id=:location "
        + "			group by p.patient_id "
        + "			 "
        + "			union "
        + "			 "
        + "			select 	pg.patient_id,date_enrolled data_inicio "
        + "			from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
        + "			where 	pg.voided=0 and p.voided=0 and program_id="
        + artProgram
        + " and date_enrolled<=:endDate and location_id=:location "
        + "			 "
        + "			 "
        + "		) inicio, "
        + "		(	Select 	o.person_id,o.obs_datetime "
        + "			from 	obs o "
        + "			where 	o.concept_id="
        + currentWhoHivStageConcept
        + " and o.voided=0 and o.obs_datetime<=:endDate and o.location_id=:location "
        + "		) estadio "
        + "		where estadio.person_id=inicio.patient_id and estadio.obs_datetime>date_add(inicio.data_inicio,interval 6 month) "
        + "		group by patient_id "
        + "		) estadio_depois, "
        + "		obs "
        + "		where obs.person_id=estadio_depois.patient_id and obs.concept_id="
        + currentWhoHivStageConcept
        + " and obs.obs_datetime=estadio_depois.data_estadio_depois and obs.voided=0 and obs.location_id=:location "
        + "	) ESTADIO2, "
        + "	person  "
        + "	where  "
        + "	ESTADIO1.patient_id=ESTADIO2.patient_id and  "
        + "	ESTADIO1.patient_id=person.person_id and  "
        + "	( "
        + "		(round(datediff(:endDate,birthdate)/365)<=14 and valor_estadio_antes<="
        + who2AdultStageConcept
        + " and valor_estadio_depois>="
        + who3AdultStageConcept
        + ") or "
        + "		(round(datediff(:endDate,birthdate)/365)>=15 and valor_estadio_antes<="
        + who3AdultStageConcept
        + " and valor_estadio_depois="
        + who4AdultStageConcept
        + ") "
        + "	) and valor_estadio_depois>valor_estadio_antes";
  }
}
