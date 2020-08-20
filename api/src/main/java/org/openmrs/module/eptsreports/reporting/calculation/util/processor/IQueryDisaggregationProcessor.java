package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

public interface IQueryDisaggregationProcessor {

  class QUERY {

    public static final String findMaxPatientStateDateByProgramAndPatientStateAndReportingPeriod =
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

    public static final String
        findMaxEncounterDateTimeByEncounterTypesAndConceptsAndAnswerAndEndOfReportingPeriod =
            "select p.patient_id, max(e.encounter_datetime) data_estado from patient p "
                + "		inner join encounter e on p.patient_id=e.patient_id "
                + "		inner join obs  o on e.encounter_id=o.encounter_id "
                + "	  where e.voided=0 and o.voided=0 and p.voided=0 "
                + "		and e.encounter_type =%s and o.concept_id in (%s) and o.value_coded=%s "
                + "		and e.encounter_datetime <= :endDate and e.location_id= :location group by p.patient_id ";

    public static final String
        findMaxObsDateTimeByEncounterTypesAndConceptsAndAnswerAndEndOfReportingPeriod =
            "select p.patient_id, max(o.obs_datetime) data_estado from patient p "
                + "		inner join encounter e on p.patient_id=e.patient_id "
                + "		inner join obs  o on e.encounter_id=o.encounter_id "
                + "	  where e.voided=0 and o.voided=0 and p.voided=0 "
                + "		and e.encounter_type =%s and o.concept_id in (%s) and o.value_coded=%s "
                + "		and o.obs_datetime <= :endDate and e.location_id= :location group by p.patient_id";

    public static final String findPatientAndDateInDemographicModule =
        "select person_id as patient_id,death_date as data_estado from person "
            + "	  where dead=1 and death_date is not null and death_date<= :endDate ";

    public static final String findUntrackedPatientsWithinReportingPeriodCriteriaOne =
        "select encounter.patient_id, max(encounter.encounter_datetime) from encounter "
            + "   join patient on patient.patient_id = encounter.patient_id      		        	"
            + "   join obs on obs.encounter_id = encounter.encounter_id                 		    "
            + " where patient.voided = 0 and encounter.voided = 0 and obs.voided = 0                "
            + " 	and obs.concept_id = 1981 and obs.value_coded = 2160                            "
            + " 	and encounter.encounter_type =21 and encounter.location_id = :location          "
            + " 	and encounter.encounter_datetime >= :startDate                                  "
            + " 	and encounter.encounter_datetime <= :endDate group by encounter.patient_id      ";

    public static final String findUntracedByNotHavefilledDataCriteriaTwo =
        "select encounter.patient_id, encounter.encounter_datetime from obs  "
            + "      join encounter on encounter.encounter_id = obs.encounter_id where not EXISTS(  "
            + "   select * from obs o join encounter e on e.encounter_id = o.encounter_id           "
            + "   where o.voided = 0 and e.voided =0                                                "
            + "	    and o.concept_id in(2017, 2158, 2157, 1748, 2031, 23944, 23945, 2032, 2037, 2038, 1272, 2040, 23923, 23933, 23934, 23934, 23935, 2180) "
            + "	    and (o.value_coded is not null or o.value_text is not null or o.value_datetime is not null)                                "
            + "	    and e.encounter_type = 21 and o.encounter_id = obs.encounter_id and o.obs_datetime = obs.obs_datetime                      "
            + "	    and e.encounter_datetime >= :startDate and e.encounter_datetime  <= :endDate                                               "
            + "	    and e.location_id =:location)                                                                                              "
            + "  and encounter.encounter_type = 21                                                                                             "
            + "  and obs.concept_id = 1981 and obs.value_coded = 2160                                                                          "
            + "  and encounter.encounter_datetime >= :startDate                                                                                "
            + "  and encounter.encounter_datetime  <= :endDate                                                                                 "
            + "  and encounter.location_id =:location                                                                                          "
            + "  group by encounter.patient_id                                                                                                 ";

    public static final String findTrackedPatientsWithinReportingPeriodCriteriaThree =
        "select encounter.patient_id, encounter.encounter_datetime from obs "
            + "      join encounter on encounter.encounter_id = obs.encounter_id where exists (                                                "
            + "  select * from obs o join encounter e on e.encounter_id = o.encounter_id                                                       "
            + "  where o.voided = 0 and e.voided =0 and ((o.concept_id in(2031, 23944, 23945) and o.value_coded in(2024, 2026, 2011, 2032)) or o.concept_id = 2032 ) "
            + "     and e.encounter_type = 21 and o.encounter_id = obs.encounter_id and o.obs_datetime =  obs.obs_datetime                     "
            + "	    and e.encounter_datetime >= :startDate and e.encounter_datetime  <= :endDate                                               "
            + "	    and e.location_id =:location)                                                                                              "
            + "  and encounter.encounter_type = 21 and obs.concept_id = 1981 and obs.value_coded = 2160                                        "
            + "  and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime  <= :endDate                                  "
            + "  and encounter.location_id =:location group by encounter.patient_id                                                            ";

    public static final String findTrackedPatientsWithinReportingPeriodCriteriaThreeNegation =
        "select encounter.patient_id, encounter.encounter_datetime from obs "
            + "      join encounter on encounter.encounter_id = obs.encounter_id where not exists (                                            "
            + "  select * from obs o join encounter e on e.encounter_id = o.encounter_id                                                       "
            + "  where o.voided = 0 and e.voided =0 and ((o.concept_id in(2031, 23944, 23945) and o.value_coded in(2024, 2026, 2011, 2032)) or o.concept_id = 2032 ) "
            + "     and e.encounter_type = 21 and o.encounter_id = obs.encounter_id and o.obs_datetime =  obs.obs_datetime                     "
            + "	    and e.encounter_datetime >= :startDate and e.encounter_datetime  <= :endDate                                               "
            + "	    and e.location_id =:location)                                                                                              "
            + "  and encounter.encounter_type = 21 and obs.concept_id = 1981 and obs.value_coded = 2160                                        "
            + "  and encounter.encounter_datetime >= :startDate and encounter.encounter_datetime  <= :endDate                                  "
            + "  and encounter.location_id =:location group by encounter.patient_id                                                            ";
  }
}
