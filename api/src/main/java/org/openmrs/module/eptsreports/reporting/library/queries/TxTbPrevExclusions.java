package org.openmrs.module.eptsreports.reporting.library.queries;

import org.springframework.stereotype.Component;

@Component
public class TxTbPrevExclusions {

  class QUERY {
    public static final String excludeDTINH3HP7MonthUntilINHStartDate =
        "select enc.patient_id,enc.data_inicio_tpi from ( "
            + "select p.patient_id,e.encounter_datetime data_inicio_tpi from patient p  "
            + "inner join encounter e on p.patient_id=e.patient_id  "
            + "inner join obs o on o.encounter_id=e.encounter_id  "
            + "where e.voided=0 and p.voided=0  and e.patient_id=:patientId and e.encounter_datetime between (:encounterDate) and (:encounterDate + interval 7 month) "
            + "and o.voided=0 and o.concept_id=1719 and e.encounter_type=6  and o.value_coded in(23954) and e.location_id=:location  "
            + "group by p.patient_id "
            + ")  enc  ";
  }
}
