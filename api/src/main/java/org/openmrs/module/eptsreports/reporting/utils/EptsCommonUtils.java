package org.openmrs.module.eptsreports.reporting.utils;

import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PatientDemographicsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.converter.BirthDateConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class EptsCommonUtils {

  /**
   * Adds the standard patient list columns
   *
   * @param dsd the data set definition
   */
  public static void addStandardColumns(PatientDataSetDefinition dsd) {
    // identifier
    PatientIdentifierType preARTNo =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");
    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(preARTNo.getName(), preARTNo),
            identifierFormatter);

    dsd.addColumn("Patient Id", new PatientIdDataDefinition(), "");
    dsd.addColumn("Patient NID", identifierDef, "");
    dsd.addColumn("Patient Name", new PreferredNameDataDefinition(), (String) null);
    dsd.addColumn(
        "Patient Date of Birth",
        new BirthdateDataDefinition(),
        "",
        new BirthDateConverter("birthDate"));
    dsd.addColumn("Estimated", new BirthdateDataDefinition(), "", new BirthDateConverter("state"));
    dsd.addColumn("Sex", new PersonToPatientDataDefinition(new GenderDataDefinition()), "");
    dsd.addColumn(
        "First Entry Date",
        getPatientDemographics("First Entry Date"),
        "",
        new CalculationResultDataConverter("F"));
    dsd.addColumn(
        "Last Updated",
        getPatientDemographics("Last Updated"),
        "",
        new CalculationResultDataConverter("L"));
  }

  public static DataDefinition getPatientDemographics(String name) {
    return new CalculationDataDefinition(
        name, Context.getRegisteredComponents(PatientDemographicsCalculation.class).get(0));
  }

  public static DataDefinition getPatientProgramEnrollment(
      Program program, TimeQualifier timeQualifier) {
    ProgramEnrollmentsForPatientDataDefinition cd =
        new ProgramEnrollmentsForPatientDataDefinition();
    cd.setName("The program the patient is enrolled in");
    cd.setProgram(program);
    cd.addParameter(new Parameter("enrolledOnOrBefore", "Before date", Date.class));
    cd.setWhichEnrollment(timeQualifier);
    return cd;
  }

  public static DataDefinition getEncounterForPatient(List<EncounterType> encounterType) {
    EncountersForPatientDataDefinition encounterDataDefinition =
        new EncountersForPatientDataDefinition();
    encounterDataDefinition.setName("Patient encounters");
    encounterDataDefinition.setTypes(encounterType);
    encounterDataDefinition.setWhich(TimeQualifier.LAST);
    return encounterDataDefinition;
  }
}
