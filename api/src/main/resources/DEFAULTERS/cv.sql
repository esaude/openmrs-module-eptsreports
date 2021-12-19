select 
    
    f.patient_id, f.encounter_datetime,f.concept_numeric,f.concept_coded,if(f.value_numeric>=1000,f.value_numeric,null) value_numeric,f.value_coded

    from (
            
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id =:location
            AND enc.encounter_type IN (13)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT  pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (51)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
           INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (6)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT  pat.patient_id,max(o.obs_datetime) encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (53)
            AND o.concept_id in(856,1305)
            AND o.obs_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            )f
            inner join (
            select f.patient_id,max(f.encounter_datetime) encounter_datetime from (
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id =:location
            AND enc.encounter_type IN (13)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT  pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (51)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT pat.patient_id,enc.encounter_datetime encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
           INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (6)
            AND o.concept_id in(856,1305)
            AND enc.encounter_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            union
            SELECT cv1.patient_id,cv1.encounter_datetime encounter_datetime,cv1.concept_numeric,cv1.concept_coded,cv1.value_numeric,cv1.value_coded FROM (
            SELECT  pat.patient_id,max(o.obs_datetime) encounter_datetime,enc.encounter_id,obsNumeric.concept_id concept_numeric,obsQualitative.concept_id concept_coded,obsNumeric.value_numeric,obsQualitative.value_coded FROM patient pat
            INNER JOIN encounter enc ON pat.patient_id = enc.patient_id
            INNER JOIN obs o on enc.encounter_id=o.encounter_id
            left JOIN obs obsNumeric ON enc.encounter_id = obsNumeric.encounter_id AND obsNumeric.voided = 0 AND obsNumeric.concept_id = 856
            left JOIN obs obsQualitative ON enc.encounter_id = obsQualitative.encounter_id AND obsQualitative.voided = 0 AND obsQualitative.concept_id = 1305
            WHERE pat.voided = 0
            AND enc.voided = 0
            AND o.voided = 0
            AND enc.location_id=:location
            AND enc.encounter_type IN (53)
            AND o.concept_id in(856,1305)
            AND o.obs_datetime BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate
            ) cv1
            
            )f group by f.patient_id
            ) maxima on maxima.patient_id = f.patient_id and maxima.encounter_datetime = f.encounter_datetime