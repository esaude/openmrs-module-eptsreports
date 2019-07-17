package org.openmrs.module.eptsreports.reporting.utils;

import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.DqQueries;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonUtils {

  @Autowired private HivMetadata hivMetadata;
  /**
   * Adds the standard patient list columns
   *
   * @param dsd the data set definition
   */
  public void addStandardColumns(PatientDataSetDefinition dsd) {
    // identifier
    PatientIdentifierType artNo = hivMetadata.getNidServiceTarvIdentifierType();

    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(artNo.getName(), artNo),
            identifierFormatter);

    dsd.addColumn("Patient Id", new PatientIdDataDefinition(), "");
    dsd.addColumn("Patient NID", identifierDef, "");
    dsd.addColumn("Patient Name", new PreferredNameDataDefinition(), (String) null);
    dsd.addColumn(
        "Patient Date of Birth",
        getPatientDetails("Patient Date of Birth", DqQueries.getPatientBirthDate()),
        "");
    dsd.addColumn(
        "Estimated",
        getPatientDetails("Estimated", DqQueries.getExactOrEstimatedDateOfBirth()),
        "");
    dsd.addColumn("Sex", getPatientDetails("Sex", DqQueries.getPatientGender()), "");
    dsd.addColumn(
        "First Entry Date",
        getPatientDetails("First Entry Date", DqQueries.getPatientDateCreated()),
        "");
    dsd.addColumn(
        "Last Updated", getPatientDetails("Last Updated", DqQueries.getPatientDateChanged()), "");
  }

  public DataDefinition getPatientProgramEnrollment(Program program, TimeQualifier timeQualifier) {
    ProgramEnrollmentsForPatientDataDefinition cd =
        new ProgramEnrollmentsForPatientDataDefinition();
    cd.setName("The program the patient is enrolled in");
    cd.setProgram(program);
    cd.addParameter(new Parameter("enrolledOnOrBefore", "Before date", Date.class));
    cd.setWhichEnrollment(timeQualifier);
    return cd;
  }

  public DataDefinition getEncounterForPatient(List<EncounterType> encounterType) {
    EncountersForPatientDataDefinition encounterDataDefinition =
        new EncountersForPatientDataDefinition();
    encounterDataDefinition.setName("Patient encounters");
    encounterDataDefinition.setTypes(encounterType);
    encounterDataDefinition.setWhich(TimeQualifier.LAST);
    return encounterDataDefinition;
  }

  public DataDefinition getPatientDetails(String name, String sql) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(name);
    sqlPatientDataDefinition.setSql(sql);
    return sqlPatientDataDefinition;
  }
}
