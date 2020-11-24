package org.openmrs.module.eptsreports.reporting.library.queries;

public interface MisauKeyPopReportQueries {
  class QUERY {

    // CARGAVIRAL
    public static final String findPatientsComResultadoCargaViralFichaClinicaInReportingPeriod =
        "SELECT pat.patient_id FROM patient pat "
            + "		INNER JOIN encounter enc ON pat.patient_id=enc.patient_id "
            + "		INNER JOIN obs ob ON enc.encounter_id=ob.encounter_id  "
            + "WHERE 	pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location AND "
            + "		enc.encounter_datetime BETWEEN :startDate AND :endDate AND ob.concept_id IN(856,1305) AND enc.encounter_type in (6,9) ";

    // CVANTERIOR
    public static final String findPatientsComCargaViralRegistadaMasqueNaoEPrimeiraRegistada =
        "SELECT 	p.patient_id FROM 	patient p "
            + "		JOIN encounter e ON p.patient_id=e.patient_id "
            + "		JOIN obs o ON e.encounter_id=o.encounter_id               "
            + "  JOIN                                                    "
            + "		(	SELECT 	pat.patient_id AS patient_id, enc.encounter_datetime AS endDate "
            + "			FROM 	patient pat        "
            + "					JOIN encounter enc ON pat.patient_id=enc.patient_id JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + "            WHERE 	pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location  "
            + "					AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND enc.encounter_type in (6,9) AND ob.concept_id in (856,1305) "
            + "		) ed        ON p.patient_id=ed.patient_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "        AND e.location_id = :location AND e.encounter_datetime BETWEEN  "
            + "        IF(MONTH(:startDate) = 12  && DAY(:startDate) = 21, :startDate, CONCAT(YEAR(:startDate)-1, '-12','-21')) "
            + "             AND (:startDate -interval 1 day) AND e.encounter_type in (6,9) "
            + "             AND o.concept_id in (856,1305);    ";

    // CARGAVIRALIND
    public static final String findPatientsWithCargaViralIndectetavelInReportingPeriod =
        "SELECT pat.patient_id FROM patient pat "
            + "		INNER JOIN encounter enc ON pat.patient_id=enc.patient_id  "
            + "		INNER JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + "WHERE 	pat.voided=0 AND enc.voided=0 AND ob.voided=0 AND enc.location_id=:location "
            + "		AND enc.encounter_datetime BETWEEN :startDate AND :endDate AND ob.value_numeric IS NOT NULL "
            + "		AND ob.concept_id=856 AND enc.encounter_type in (6,9) AND ob.value_numeric < 1000 "
            + "UNION                                                   "
            + "SELECT pat.patient_id FROM patient pat                  "
            + "		INNER JOIN encounter enc ON pat.patient_id=enc.patient_id "
            + "		INNER JOIN obs ob ON enc.encounter_id=ob.encounter_id "
            + "WHERE 	pat.voided = 0 AND enc.voided = 0 AND ob.voided = 0 AND enc.location_id = :location AND  "
            + "		enc.encounter_datetime BETWEEN :startDate AND :endDate AND enc.encounter_type in (6,9) AND ob.concept_id=1305 ";

    // TRANSFERIDOPARA
    public static final String findPatientsTransferidosParaInEndReportingPeriod =
        "select transferidopara.patient_id from ( select patient_id,max(data_transferidopara) data_transferidopara "
            + "	from (select 	pg.patient_id,max(ps.start_date) data_transferidopara from 	patient p "
            + "				inner join patient_program pg on p.patient_id=pg.patient_id "
            + "				inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "		where 	pg.voided=0 and ps.voided=0 and p.voided=0 and "
            + "				pg.program_id=2 and ps.state=7 and ps.end_date is null and "
            + "				ps.start_date<=:endDate and location_id=:location group by p.patient_id "
            + "  union                                               "
            + "		select 	p.patient_id,max(e.encounter_datetime) data_transferidopara "
            + "		from patient p inner join encounter e on p.patient_id=e.patient_id "
            + "				inner join obs o on o.encounter_id=e.encounter_id "
            + "		where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and "
            + "				o.voided=0 and o.concept_id=2016 and o.value_coded in (1706,23863) and e.encounter_type=21 and e.location_id=:location "
            + "		group by p.patient_id                                            "
            + "		union                                                            "
            + "		select 	p.patient_id,max(o.obs_datetime) data_transferidopara from	patient p    "
            + "				inner join encounter e on p.patient_id=e.patient_id    "
            + "				inner join obs o on o.encounter_id=e.encounter_id      "
            + "		where 	e.voided=0 and p.voided=0 and o.obs_datetime<=:endDate and   "
            + "				o.voided=0 and o.concept_id=6272 and o.value_coded=1706 and e.encounter_type=53 and  e.location_id=:location  "
            + "		group by p.patient_id                                                  "
            + "		union                                                                   "
            + "		select 	p.patient_id,max(e.encounter_datetime) data_transferidopara "
            + "		from	patient p inner join encounter e on p.patient_id=e.patient_id "
            + "				inner join obs o on o.encounter_id=e.encounter_id         "
            + "		where 	e.voided=0 and p.voided=0 and e.encounter_datetime<=:endDate and "
            + "				o.voided=0 and o.concept_id=6273 and o.value_coded=1706 and e.encounter_type=6 and  e.location_id=:location  "
            + "		group by p.patient_id) transferido group by patient_id) transferidopara   "
            + "inner join ( select patient_id,max(encounter_datetime) encounter_datetime from ( "
            + "		select p.patient_id,max(e.encounter_datetime) encounter_datetime from patient p "
            + "				inner join encounter e on e.patient_id=p.patient_id "
            + "		where 	p.voided=0 and e.voided=0 and e.encounter_datetime<=:endDate and "
            + "				e.location_id=:location and e.encounter_type in (18,6,9) "
            + "		group by p.patient_id union          "
            + "		Select 	p.patient_id,max(value_datetime) encounter_datetime from 	patient p "
            + "				inner join encounter e on p.patient_id=e.patient_id "
            + "				inner join obs o on e.encounter_id=o.encounter_id "
            + "		where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and "
            + "				o.concept_id=23866 and o.value_datetime is not null and "
            + "				o.value_datetime<=:endDate and e.location_id=:location "
            + "		group by p.patient_id ) maxFilaRecepcao group by patient_id "
            + ") consultaOuARV on transferidopara.patient_id=consultaOuARV.patient_id "
            + "where consultaOuARV.encounter_datetime<=transferidopara.data_transferidopara ";

    //// INICIOTARV
    public static final String findInicioTarvComDataFinalConhecida =
        "select patient_id                                                                                   "
            + "from                                                                                               "
            + "(	Select patient_id,min(data_inicio) data_inicio                                                   "
            + "		from                                                                                         "
            + "			(	                                                                                     "
            + "			                                                                                          "
            + "				/*Patients on ART who initiated the ARV DRUGS: ART Regimen Start Date*/               "
            + "				                                                                                      "
            + "				Select 	p.patient_id,min(e.encounter_datetime) data_inicio                            "
            + "				from 	patient p                                                                     "
            + "						inner join encounter e on p.patient_id=e.patient_id	                          "
            + "						inner join obs o on o.encounter_id=e.encounter_id                             "
            + "				where 	e.voided=0 and o.voided=0 and p.voided=0 and                                  "
            + "						e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and "
            + "						e.encounter_datetime<=:endDate and e.location_id=:location                    "
            + "				group by p.patient_id                                                                 "
            + "		                                                                                              "
            + "				union                                                                                 "
            + "		                                                                                              "
            + "				/*Patients on ART who have art start date: ART Start date*/                           "
            + "				Select 	p.patient_id,min(value_datetime) data_inicio                                  "
            + "				from 	patient p                                                                     "
            + "						inner join encounter e on p.patient_id=e.patient_id                           "
            + "						inner join obs o on e.encounter_id=o.encounter_id                             "
            + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and "
            + "						o.concept_id=1190 and o.value_datetime is not null and                           "
            + "						o.value_datetime<=:endDate and e.location_id=:location                           "
            + "				group by p.patient_id                                                                    "
            + "                                                                                                       "
            + "				union                                                                                    "
            + "                                                                                                       "
            + "				/*Patients enrolled in ART Program: OpenMRS Program*/                                    "
            + "				select 	pg.patient_id,min(date_enrolled) data_inicio                                     "
            + "				from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id            "
            + "				where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<=:endDate and location_id=:location "
            + "				group by pg.patient_id                                                                                    "
            + "			                                                                                                              "
            + "				union                                                                                                     "
            + "				                                                                                                           "
            + "				                                                                                                          "
            + "				/*Patients with first drugs pick up date set in Pharmacy: First ART Start Date*/                           "
            + "				  SELECT 	e.patient_id, MIN(e.encounter_datetime) AS data_inicio                                         "
            + "				  FROM 		patient p                                                                                      "
            + "							inner join encounter e on p.patient_id=e.patient_id                                            "
            + "				  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<=:endDate and e.location_id=:location "
            + "				  GROUP BY 	p.patient_id                                                                                              "
            + "			                                                                                                                          "
            + "				union                                                                                                                  "
            + "				                                                                                                                      "
            + "				/*Patients with first drugs pick up date set: Recepcao Levantou ARV*/                                                  "
            + "				Select 	p.patient_id,min(value_datetime) data_inicio                                                                   "
            + "				from 	patient p                                                                                                        "
            + "						inner join encounter e on p.patient_id=e.patient_id                                                             "
            + "						inner join obs o on e.encounter_id=o.encounter_id                                                                 "
            + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and                                              "
            + "						o.concept_id=23866 and o.value_datetime is not null and                                                           "
            + "						o.value_datetime<=:endDate and e.location_id=:location                                                            "
            + "				group by p.patient_id					                                                                                   "
            + "				                                                                                                                           "
            + "				                                                                                                                              "
            + "			) inicio_real                                                                                                         "
            + "		group by patient_id	                                                                                                       "
            + "	)inicio                                                                                                                 "
            + "where data_inicio between :startDate and :endDate                                                                              ";
    //// INICIOTARV
    // public static final String findInicioTarvComDataFinalConhecida= "select
    //// patient_id from (
    // Select patient_id,min(data_inicio) data_inicio " +
    // " from ( Select p.patient_id,min(e.encounter_datetime) data_inicio " +
    // " from patient p \n" +
    // " inner join encounter e on p.patient_id=e.patient_id \n" +
    // " inner join obs o on o.encounter_id=e.encounter_id\n" +
    // " where e.voided=0 and o.voided=0 and p.voided=0 and \n" +
    // " e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256
    //// and \n" +
    // " e.encounter_datetime<=:endDate and e.location_id=:location\n" +
    // " group by p.patient_id\n" +
    // " \n" +
    // " union\n" +
    // " \n" +
    // " /*Patients on ART who have art start date: ART Start date*/\n" +
    // " Select p.patient_id,min(value_datetime) data_inicio\n" +
    // " from patient p\n" +
    // " inner join encounter e on p.patient_id=e.patient_id\n" +
    // " inner join obs o on e.encounter_id=o.encounter_id\n" +
    // " where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in
    //// (18,6,9,53)
    // and \n" +
    // " o.concept_id=1190 and o.value_datetime is not null and \n" +
    // " o.value_datetime<=:endDate and e.location_id=:location\n" +
    // " group by p.patient_id\n" +
    // "\n" +
    // " union\n" +
    // "\n" +
    // " /*Patients enrolled in ART Program: OpenMRS Program*/\n" +
    // " select pg.patient_id,min(date_enrolled) data_inicio\n" +
    // " from patient p inner join patient_program pg on
    //// p.patient_id=pg.patient_id\n" +
    // " where pg.voided=0 and p.voided=0 and program_id=2 and
    //// date_enrolled<=:endDate and
    // location_id=:location\n" +
    // " group by pg.patient_id\n" +
    // " \n" +
    // " union\n" +
    // " \n" +
    // " \n" +
    // " /*Patients with first drugs pick up date set in Pharmacy: First ART Start
    //// Date*/\n" +
    // " SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio \n" +
    // " FROM patient p\n" +
    // " inner join encounter e on p.patient_id=e.patient_id\n" +
    // " WHERE p.voided=0 and e.encounter_type=18 AND e.voided=0 and
    // e.encounter_datetime<=:endDate and e.location_id=:location\n" +
    // " GROUP BY p.patient_id\n" +
    // " \n" +
    // " union\n" +
    // " \n" +
    // " /*Patients with first drugs pick up date set: Recepcao Levantou ARV*/\n" +
    // " Select p.patient_id,min(value_datetime) data_inicio\n" +
    // " from patient p\n" +
    // " inner join encounter e on p.patient_id=e.patient_id\n" +
    // " inner join obs o on e.encounter_id=o.encounter_id\n" +
    // " where p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and
    //// \n" +
    // " o.concept_id=23866 and o.value_datetime is not null and \n" +
    // " o.value_datetime<=:endDate and e.location_id=:location\n" +
    // " group by p.patient_id \n" +
    // " \n" +
    // " \n" +
    // " ) inicio_real\n" +
    // " group by patient_id \n" +
    // " )inicio\n" +
    // "where data_inicio between :startDate and :endDate"

  }
}
