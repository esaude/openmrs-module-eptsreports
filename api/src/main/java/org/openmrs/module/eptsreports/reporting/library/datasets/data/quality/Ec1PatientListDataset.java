package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.BirthDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PatientDemographicsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PregnantCriteriaCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PregnantEnrollmentStatusCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
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
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** A dataset to display list of patients who are pregnant and male */
@Component
public class Ec1PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  @Autowired
  public Ec1PatientListDataset(SummaryDataQualityCohorts summaryDataQualityCohorts) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
  }

  public DataSetDefinition ec1DataSetDefinition() {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC1");
    dsd.addParameters(getDataQualityParameters());
    dsd.addRowFilter(summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}");

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

    // start adding columns here
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
    dsd.addColumn(
        "Pregnant Criteria",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PC"));
    dsd.addColumn(
        "Encounter Date Pregnant Criteria",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Date",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PTVD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Status",
        getPregnancyStatus(),
        "location=${location}",
        new CalculationResultDataConverter("State"));

    return dsd;
  }

  private DataDefinition getBirthDateStatus() {
    return new CalculationDataDefinition(
        "estimated", Context.getRegisteredComponents(BirthDateCalculation.class).get(0));
  }

  private DataDefinition getPatientDemographics(String name) {
    return new CalculationDataDefinition(
        name, Context.getRegisteredComponents(PatientDemographicsCalculation.class).get(0));
  }

  private DataDefinition getPregnantCriteria() {
    CalculationDataDefinition pCriteria =
        new CalculationDataDefinition(
            "Pregnant Criteria",
            Context.getRegisteredComponents(PregnantCriteriaCalculation.class).get(0));
    pCriteria.addParameter(new Parameter("location", "Location", Location.class));
    pCriteria.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pCriteria;
  }

  private DataDefinition getPregnancyStatus() {
    CalculationDataDefinition pStatus =
        new CalculationDataDefinition(
            "Pregnant Status",
            Context.getRegisteredComponents(PregnantEnrollmentStatusCalculation.class).get(0));
    pStatus.addParameter(new Parameter("location", "Location", Location.class));
    pStatus.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pStatus;
  }
}
