package org.openmrs.module.eptsreports.reporting.utils;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.BirthDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PatientDemographicsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.GenderConverter;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;

public class EptsCommonColumns {

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
        new BirthdateConverter("dd-MM-yyyy"));
    dsd.addColumn("Estimated", getBirthDateStatus(), "", new CalculationResultDataConverter());
    dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
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

  private static DataDefinition getBirthDateStatus() {
    return new CalculationDataDefinition(
        "estimated", Context.getRegisteredComponents(BirthDateCalculation.class).get(0));
  }

  private static DataDefinition getPatientDemographics(String name) {
    return new CalculationDataDefinition(
        name, Context.getRegisteredComponents(PatientDemographicsCalculation.class).get(0));
  }
}
