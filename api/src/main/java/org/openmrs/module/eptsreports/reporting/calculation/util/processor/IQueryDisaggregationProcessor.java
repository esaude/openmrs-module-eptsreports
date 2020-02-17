package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

public interface IQueryDisaggregationProcessor {

  class QUERY {

    public static final String findMaxPatientStateDateByProgramAndPatientStateAndEndDate =
        "select pg.patient_id, max(ps.start_date) data_estado from patient p "
            + "	   inner join patient_program pg on p.patient_id=pg.patient_id "
            + "	   inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "  where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=%s "
            + "    and ps.state= %s and ps.end_date is null and ps.start_date<= :endDate "
            + "    and location_id= :location group by pg.patient_id";

    public static final String findMaxObsDatetimeByEncounterTypeAndConceptsAndAnsweresInPeriod =
        "select p.patient_id, max(baseObs.obs_datetime) data_estado from patient p "
            + "		inner join encounter e on p.patient_id=e.patient_id "
            + "		inner join obs baseObs on e.encounter_id=baseObs.encounter_id "
            + "	  where e.voided=0 and p.voided=0 and baseObs.voided=0 and e.encounter_type=%s "
            + "		and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate  and  e.location_id= :location and "
            + "		baseObs.concept_id in (%s) and baseObs.value_coded in (%s) group by p.patient_id ";

    public static final String findMaxObsDateTimeByEncounterTypesAndConceptsAndAnswerAndEndDate =
        "select p.patient_id, max(o.obs_datetime) data_estado from patient p "
            + "		inner join encounter e on p.patient_id=e.patient_id "
            + "		inner join obs  o on e.encounter_id=o.encounter_id "
            + "	  where e.voided=0 and o.voided=0 and p.voided=0 "
            + "		and e.encounter_type in (%s) and o.concept_id in (%s) and o.value_coded=%s "
            + "		and o.obs_datetime<= :endDate and e.location_id= :location group by p.patient_id";

    public static final String findPatientAndDateInDemographicModule =
        "select person_id as patient_id,death_date as data_estado from person "
            + "	  where dead=1 and death_date is not null and death_date<= :endDate ";
  }
}
