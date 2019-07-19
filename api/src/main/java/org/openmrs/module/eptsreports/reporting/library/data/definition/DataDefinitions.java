package org.openmrs.module.eptsreports.reporting.library.data.definition;

import java.util.Date;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class DataDefinitions {

  public DataDefinition getPatientProgramEnrollment(Program program, TimeQualifier timeQualifier) {
    ProgramEnrollmentsForPatientDataDefinition cd =
        new ProgramEnrollmentsForPatientDataDefinition();
    cd.setName("The program the patient is enrolled in");
    cd.setProgram(program);
    cd.addParameter(new Parameter("enrolledOnOrBefore", "Before date", Date.class));
    cd.setWhichEnrollment(timeQualifier);
    return cd;
  }

  public DataDefinition getPatientDetails(String name, String sql) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(name);
    sqlPatientDataDefinition.setSql(sql);
    return sqlPatientDataDefinition;
  }
}
