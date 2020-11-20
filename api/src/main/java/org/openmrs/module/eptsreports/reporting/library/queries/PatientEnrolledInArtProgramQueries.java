package org.openmrs.module.eptsreports.reporting.library.queries;

public interface PatientEnrolledInArtProgramQueries {
  class QUERY {
    public static final String findPatientsEnrolledInArtProgramOnReportingPeriod =
        "select patient.patient_id from patient "
            + "inner join patient_program on patient_program.patient_id = patient.patient_id "
            + "inner join program on program.program_id = patient_program.program_id "
            + "where patient.voided = 0 and patient_program.voided = 0 and program.retired =0 "
            + "and program.program_id =2 and patient_program.date_enrolled >= :startDate and patient_program.date_enrolled <= :endDate and patient_program.location_id = :location ";
  }
}
