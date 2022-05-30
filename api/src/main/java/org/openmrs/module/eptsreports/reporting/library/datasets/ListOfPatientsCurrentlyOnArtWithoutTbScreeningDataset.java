package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.DispensationTypeConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.NotApplicableIfNullConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsDefaultersOrIITCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLDataDefinitionQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationDataDefinitionQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxtbDenominatorQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ListOfPatientsCurrentlyOnArtWithoutTbScreeningDataset extends BaseDataSet {

  private ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
      listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;

  private ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;

  private TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries;

  private TPTInitiationDataDefinitionQueries tptInitiationDataDefinitionQueries;

  private ListOfPatientsEligibleForVLDataDefinitionQueries
      listOfPatientsEligibleForVLDataDefinitionQueries;

  private TxtbDenominatorQueries txtbDenominatorQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public ListOfPatientsCurrentlyOnArtWithoutTbScreeningDataset(
      ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
          listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries,
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries,
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries,
      TPTInitiationDataDefinitionQueries tptInitiationDataDefinitionQueries,
      ListOfPatientsEligibleForVLDataDefinitionQueries
          listOfPatientsEligibleForVLDataDefinitionQueries,
      TxtbDenominatorQueries txtbDenominatorQueries,
      HivMetadata hivMetadata) {
    this.listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries =
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries;
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.listOfPatientsDefaultersOrIITCohortQueries = listOfPatientsDefaultersOrIITCohortQueries;
    this.tptInitiationDataDefinitionQueries = tptInitiationDataDefinitionQueries;
    this.listOfPatientsEligibleForVLDataDefinitionQueries =
        listOfPatientsEligibleForVLDataDefinitionQueries;
    this.txtbDenominatorQueries = txtbDenominatorQueries;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructListOfPatientsDataset() {
    PatientDataSetDefinition patientDefinition = new PatientDataSetDefinition();
    patientDefinition.setName("List of patients currently no ART without TB Screening");
    patientDefinition.setParameters(getParameters());

    patientDefinition.addRowFilter(
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
            .getPatientsCurrentlyOnArtWithoutTbScreening(),
        "endDate=${endDate},location=${location}");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");

    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");

    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);

    PersonAttributeType contactAttributeType =
        Context.getPersonService()
            .getPersonAttributeTypeByUuid("e2e3fd64-1d5f-11e0-b929-000c29ad1d07");

    patientDefinition.addColumn("patient_id", new PersonIdDataDefinition(), "");
    patientDefinition.addColumn(
        "nid",
        tptListOfPatientsEligibleDataSet.getNID(identifierType.getPatientIdentifierTypeId()),
        "");

    patientDefinition.addColumn("name", nameDef, "");

    patientDefinition.addColumn("sex", new GenderDataDefinition(), "", new GenderConverter());

    patientDefinition.addColumn(
        "age",
        listOfPatientsArtCohortCohortQueries.getAge(),
        "evaluationDate=${endDate}",
        new NotApplicableIfNullConverter());

    patientDefinition.addColumn(
        "location",
        listOfPatientsDefaultersOrIITCohortQueries.getLocation(),
        "location=${location}",
        null);

    patientDefinition.addColumn(
        "neighborhood",
        listOfPatientsDefaultersOrIITCohortQueries.getNeighborhood(),
        "location=${location}",
        null);

    patientDefinition.addColumn(
        "reference_point",
        listOfPatientsDefaultersOrIITCohortQueries.getReferencePoint(),
        "location=${location}",
        null);

    DataDefinition conctactDef =
        new ConvertedPersonDataDefinition(
            "contact",
            new PersonAttributeDataDefinition(contactAttributeType.getName(), contactAttributeType),
            null);
    patientDefinition.addColumn("contact", conctactDef, "");

    patientDefinition.addColumn(
        "art_start",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getArtStartDate(),
        "endDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "pregnant_breastfeeding",
        tptListOfPatientsEligibleDataSet.pregnantBreasfeediDefinition(),
        "location=${location}");

    patientDefinition.addColumn(
        "last_pickup_fila",
        listOfPatientsEligibleForVLDataDefinitionQueries.getPatientsAndLastDrugPickUpDateOnFila(),
        "startDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "next_scheduled_pickup_fila",
        listOfPatientsEligibleForVLDataDefinitionQueries.getPatientsAndNextDrugPickUpDateOnFila(),
        "startDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "fila_dispensation_mode",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getDispensationTypeOnFila(),
        "endDate=${endDate},location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "last_followup_consultation",
        listOfPatientsEligibleForVLDataDefinitionQueries
            .getPatientsAndLastFollowUpConsultationDate(),
        "startDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "next_followup_consultation",
        listOfPatientsEligibleForVLDataDefinitionQueries
            .getPatientsAndNextFollowUpConsultationDate(),
        "startDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "clinical_dispensation_mode",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
            .getDispensationTypeOnClinicalAndPediatricEncounter(),
        "endDate=${endDate},location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "recent_arv_pickup",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
            .getMostRecentDrugPickupDateOnRecepcaoLevantouArv(),
        "endDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "scheduled_arv_pickup",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
            .getNextScheduledDrugPickupDateOnRecepcaoLevantouArv(),
        "endDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "screening_date",
        txtbDenominatorQueries.getMostRecentTbScreeningDate(),
        "startDate=${startDate-6m},endDate=${endDate},location=${location}");

    patientDefinition.addColumn(
        "mdc_consultation_date",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries
            .getMostRecentMdcConsultationDate(),
        "location=${location}");

    patientDefinition.addColumn(
        "mdc1",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getMdcDispensationType(
            ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.DispensationColumn.MDC1),
        "location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "mdc2",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getMdcDispensationType(
            ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.DispensationColumn.MDC2),
        "location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "mdc3",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getMdcDispensationType(
            ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.DispensationColumn.MDC3),
        "location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "mdc4",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getMdcDispensationType(
            ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.DispensationColumn.MDC4),
        "location=${location}",
        new DispensationTypeConverter());

    patientDefinition.addColumn(
        "mdc5",
        listOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.getMdcDispensationType(
            ListOfPatientsCurrentlyOnArtWithoutTbScreeningCohortQueries.DispensationColumn.MDC5),
        "location=${location}",
        new DispensationTypeConverter());

    return patientDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("endDate", "Report End Date", Date.class),
        new Parameter("location", "Health Facility", Location.class));
  }
}
