package org.openmrs.module.eptsreports.reporting.utils;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.data.definition.DataDefinitions;
import org.openmrs.module.eptsreports.reporting.library.queries.DqQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonUtils {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private DataDefinitions definitions;
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
        definitions.getPatientDetails("Patient Date of Birth", DqQueries.getPatientBirthDate()),
        "");
    dsd.addColumn(
        "Estimated",
        definitions.getPatientDetails("Estimated", DqQueries.getExactOrEstimatedDateOfBirth()),
        "");
    dsd.addColumn("Sex", definitions.getPatientDetails("Sex", DqQueries.getPatientGender()), "");
    dsd.addColumn(
        "First Entry Date",
        definitions.getPatientDetails("First Entry Date", DqQueries.getPatientDateCreated()),
        "");
    dsd.addColumn(
        "Last Updated",
        definitions.getPatientDetails("Last Updated", DqQueries.getPatientDateChanged()),
        "");
  }
}
