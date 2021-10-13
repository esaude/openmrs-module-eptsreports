package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.data.converter.DispensationTypeConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.EmptyToNaoAndAnyToSimConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.NotApplicableIfNullConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationDataDefinitionQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTInitiationNewDataSet extends BaseDataSet {

  private TPTInitiationDataDefinitionQueries tPTInitiationDataDefinitionQueries;
  private ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;
  private TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Autowired
  public TPTInitiationNewDataSet(
      TPTInitiationDataDefinitionQueries tPTInitiationDataDefinitionQueries,
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset,
      TPTInitiationCohortQueries tptInitiationCohortQueries) {

    this.tPTInitiationDataDefinitionQueries = tPTInitiationDataDefinitionQueries;
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.tptInitiationCohortQueries = tptInitiationCohortQueries;
  }

  public DataSetDefinition constructDataSet() {
    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.setName("TPT");

    pdd.addRowFilter(
        tptInitiationCohortQueries.getBaseCohort(),
        "endDate=${endDate},startDate=${startDate},location=${location}");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");

    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");

    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(identifierType.getName(), identifierType),
            identifierFormatter);
    DataConverter formatter = new ObjectFormatter("{givenName} {familyName}");
    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
    pdd.setParameters(getParameters());

    pdd.addColumn("NID", listChildrenOnARTandFormulationsDataset.getNID(), "");

    pdd.addColumn("name", nameDef, "");

    pdd.addColumn(
        "tarv_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndARTStartDate(),
        "endDate=${endDate},startDate=${startDate},location=${location}",
        null);

    pdd.addColumn(
        "age",
        tPTInitiationDataDefinitionQueries.getPatientsAndTheirAges(),
        "endDate=${endDate}",
        null);

    pdd.addColumn("gender", new GenderDataDefinition(), "", null);

    pdd.addColumn(
        "pregnant_breastfeeding",
        tPTInitiationDataDefinitionQueries.getPatientsThatArePregnantOrBreastfeeding(),
        "endDate=${endDate},startDate=${startDate},location=${location}",
        new NotApplicableIfNullConverter());

    pdd.addColumn(
        "followup_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndLastFollowUpConsultationDate(),
        "location=${location}",
        null);

    pdd.addColumn(
        "received_TPT",
        tPTInitiationDataDefinitionQueries.getPatientsReceivedTPTInTheLastFollowUpConsultation(),
        "location=${location}",
        new EmptyToNaoAndAnyToSimConverter());

    pdd.addColumn(
        "3HP_FILT_start_date",
        tPTInitiationDataDefinitionQueries.getPatientAnd3HPInitiationDateOnFILT(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "3HP_clinical_start_date",
        tPTInitiationDataDefinitionQueries.getPatientAnd3HPInitiationDateOnFichaClinica(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "FILT_3HP_dispensation_date",
        tPTInitiationDataDefinitionQueries.getPatientsAnd3HPDispensationDate(),
        "location=${location}",
        null);

    pdd.addColumn(
        "FILT_3HP_dispensation_type",
        tPTInitiationDataDefinitionQueries.getPatientsAndLast3HPTypeOfDispensation(),
        "location=${location}",
        new DispensationTypeConverter());

    pdd.addColumn(
        "IPT_FILT_start_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndIPTInitiationDateOnFilt(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "IPT_clinical_seg_start_date",
        tPTInitiationDataDefinitionQueries
            .getPatientsAndIPTInitiationDateOnFichaClinicaOrFichaSeguimento(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "IPT_mastercard_start_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndIPTInitiationDateOnFichaResumo(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "FILT_with_IPT_dispensation_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndDateOfLastFILTDispensationWithIPT(),
        "location=${location}",
        null);

    pdd.addColumn(
        "FILT_with_IPT_dispensation_type",
        tPTInitiationDataDefinitionQueries
            .getPatientsAndTypeOfDispensationInLastFILTDispensationWithIPT(),
        "location=${location}",
        new DispensationTypeConverter());

    pdd.addColumn(
        "IPT_end_date",
        tPTInitiationDataDefinitionQueries
            .getPatientsAndIPTCompletioDateOnFichaClinicaOrFichaSeguimento(),
        "location=${location}",
        null);

    pdd.addColumn(
        "IPT_mastercard_end_date",
        tPTInitiationDataDefinitionQueries.getPatientsAndIPTCompetionDateOnFichaResumo(),
        "location=${location}",
        null);

    pdd.addColumn(
        "IPT_expected_end_date",
        tPTInitiationDataDefinitionQueries
            .getPatientsAndIPTCompletionDateAndIPTStartDatePlus173Days(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn(
        "expected_registered_date_difference",
        tPTInitiationDataDefinitionQueries
            .getPatientAndDifferenceBetweenRegisteredCompletionDateAndExpectedCompletionDate(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    pdd.addColumn("person_id", new PersonIdDataDefinition(), "");

    return pdd;
  }
}
