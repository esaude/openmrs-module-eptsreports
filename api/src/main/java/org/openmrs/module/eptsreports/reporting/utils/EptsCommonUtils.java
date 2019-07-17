package org.openmrs.module.eptsreports.reporting.utils;

import java.util.Date;
import java.util.List;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PatientDemographicsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
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
        convert(getBirthdateConverted(), new BirthdateConverter("dd-MM-yyyy")),
        "");
    dsd.addColumn(
        "Estimated",
        convert(getBirthdateConverted(), new PropertyConverter(Birthdate.class, "estimated")),
        "");
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

  public DataDefinition getPatientDemographics(String name) {
    return new CalculationDataDefinition(
        name, Context.getRegisteredComponents(PatientDemographicsCalculation.class).get(0));
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

  private PatientDataDefinition convert(PatientDataDefinition pdd, DataConverter... converters) {
    return new ConvertedPatientDataDefinition(pdd, converters);
  }

  private PatientDataDefinition convert(PersonDataDefinition pdd, DataConverter... converters) {
    return new ConvertedPatientDataDefinition(new PersonToPatientDataDefinition(pdd), converters);
  }

  private PatientDataDefinition getBirthdateConverted(DataConverter... converters) {
    return convert(new BirthdateDataDefinition(), converters);
  }
}
