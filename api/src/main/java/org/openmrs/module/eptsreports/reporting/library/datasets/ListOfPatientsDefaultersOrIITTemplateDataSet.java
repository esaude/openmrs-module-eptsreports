package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Collections;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.SupportGroupsConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsDefaultersOrIITCohortQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsDefaultersOrIITTemplateDataSet extends BaseDataSet {

  private ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  private TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public ListOfPatientsDefaultersOrIITTemplateDataSet(
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset,
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      ListOfPatientsDefaultersOrIITCohortQueries listOfPatientsDefaultersOrIITCohortQueries,
      HivMetadata hivMetadata) {
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.listOfPatientsDefaultersOrIITCohortQueries = listOfPatientsDefaultersOrIITCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructDataSet() {
    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.addParameter(new Parameter("endDate", "endDate", Date.class));
    pdd.addParameter(new Parameter("minDay", "Minimum number of days", Integer.class));
    pdd.addParameter(new Parameter("maxDay", "Maximum number of days", Integer.class));
    pdd.addParameter(new Parameter("location", "Location", Location.class));
    pdd.setName("FATL");

    PersonAttributeType contactAttributeType =
        Context.getPersonService()
            .getPersonAttributeTypeByUuid("e2e3fd64-1d5f-11e0-b929-000c29ad1d07");
    DataDefinition conctactDef =
        new ConvertedPersonDataDefinition(
            "contact",
            new PersonAttributeDataDefinition(contactAttributeType.getName(), contactAttributeType),
            null);
    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");

    pdd.addColumn("id", new PersonIdDataDefinition(), "");

    // 1 - NID - Sheet 1: Column A */
    pdd.addColumn("nid", listChildrenOnARTandFormulationsDataset.getNID(), "");

    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
    pdd.setParameters(getParameters());

    //     2 - Name - Sheet 1: Column B */
    pdd.addColumn("name", nameDef, "");

    // 3 - ART Start Date - Sheet 1: Column C */
    pdd.addColumn(
        "inicio_tarv",
        listChildrenOnARTandFormulationsDataset.getArtStartDate(),
        "onOrBefore=${endDate},location=${location}",
        new CalculationResultConverter());

    // 5 - Sex - Sheet 1: Column E */
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    // 4 - Age - Sheet 1: Column D */
    pdd.addColumn(
        "age", listChildrenOnARTandFormulationsDataset.getAge(), "endDate=${endDate}", null);

    // 6 - Pregnancy/Breastfeeding status (Grávida/Lactante) – Sheet 1: Column F */
    pdd.addColumn(
        "pregnant_or_breastfeeding",
        tptListOfPatientsEligibleDataSet.pregnantBreasfeediDefinition(),
        "location=${location}",
        null);

    // 7 - Patients active on TB Treatment - Sheet 1: Column G */
    pdd.addColumn(
        "tb_treatment",
        listOfPatientsDefaultersOrIITCohortQueries.getPatientsActiveOnTB(),
        "location=${location}",
        null);

    // 8 -· Consentimento Informado – Sheet 1: Column H */
    pdd.addColumn(
        "patient_informed_consent",
        listOfPatientsDefaultersOrIITCohortQueries.getPatientsConfidentConcent(
            hivMetadata.getAcceptContactConcept()),
        "location=${location}",
        null);

    // 9 - PRINT ‘N’ IF THE PATIENT HAS ONE OF THE FOLLOWING OPTIONS: Sheet 1: Column I */
    pdd.addColumn(
        "confidant_informed_consent",
        listOfPatientsDefaultersOrIITCohortQueries.getPatientsConfidentConcent(
            hivMetadata.getConfidentAcceptContact()),
        "location=${location}",
        null);

    // 10 -· Tipo de Dispensa – Sheet 1: Column J */
    pdd.addColumn(
        "type_of_dispensation",
        listOfPatientsDefaultersOrIITCohortQueries.getTypeOfDispensation(),
        "endDate=${endDate},location=${location}",
        null);

    // 11 Contacto – Sheet 1: Column K */
    pdd.addColumn("contact", conctactDef, "", null);

    // 12 Address (Localidade) – Sheet 1: Column L */
    pdd.addColumn(
        "location",
        listOfPatientsDefaultersOrIITCohortQueries.getLocation(),
        "location=${location}",
        null);

    // 13 Address (Bairro) – Sheet 1: Column M */
    pdd.addColumn(
        "neighborhood",
        listOfPatientsDefaultersOrIITCohortQueries.getNeighborhood(),
        "location=${location}",
        null);

    // 14 Address (Ponto de Referencia) – Sheet 1: Column N */
    pdd.addColumn(
        "reference_point",
        listOfPatientsDefaultersOrIITCohortQueries.getReferencePoint(),
        "location=${location}",
        null);

    // 15 - Last Follow up Consultation Date - Sheet 1: Column O */
    pdd.addColumn(
        "last_consultation_date",
        listChildrenOnARTandFormulationsDataset.getLastFollowupConsultationDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 16 - Next Follow up Consultation Date - Sheet 1: Column P */
    pdd.addColumn(
        "next_consultation_date",
        listChildrenOnARTandFormulationsDataset.getNextFollowUpConsultationDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 17 - Mães Mentoras (MM) for Ficha clinica – Sheet 1: Columns Q
    pdd.addColumn(
        "menthor_mother_ficha_clinica",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getMentoringMotherConcept().getConceptId()),
            false),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 18 - Adolescentes e Jovens Mentores (AJM) for Ficha clinica – Sheet 1: Columns R
    pdd.addColumn(
        "youth_teenage_ficha_clinica",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(
                hivMetadata.getYouthAndTeenageMenthorConcept().getConceptId()),
            false),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 19 - Homem Campeão (HC) for Ficha clinica – Sheet 1: Columns S
    pdd.addColumn(
        "champion_man_ficha_clinica",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getChampioManConcept().getConceptId()),
            false),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 20 - Last APSS/PP Consultation Date – Sheet 1: Column T
    pdd.addColumn(
        "last_apss_consultation_date",
        listOfPatientsDefaultersOrIITCohortQueries.getLastApssConsultationDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 21 - Next Scheduled APSS/PP Consultation Date – Sheet 1: Column U
    pdd.addColumn(
        "next_apss_consultation_date",
        listOfPatientsDefaultersOrIITCohortQueries.getNextApssConsultationDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 22 - Mães Mentoras (MM) for APSS – Sheet 1: Columns V
    pdd.addColumn(
        "menthor_mother_apss",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getMentoringMotherConcept().getConceptId()),
            true),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 23 - Adolescentes e Jovens Mentores (AJM) for APSS – Sheet 1: Columns W
    pdd.addColumn(
        "youth_teenage_apss",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(
                hivMetadata.getYouthAndTeenageMenthorConcept().getConceptId()),
            true),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 24 - Homem Campeão (HC) for APSS – Sheet 1: Columns X
    pdd.addColumn(
        "champion_man_apss",
        listOfPatientsDefaultersOrIITCohortQueries.getSupportGroupsOnFichaClinicaOrSeguimento(
            Collections.singletonList(
                hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getChampioManConcept().getConceptId()),
            true),
        "endDate=${endDate},location=${location}",
        new SupportGroupsConverter());

    // 25 - Last Drug Pick-up Date - Sheet 1: Column Y */
    pdd.addColumn(
        "date_of_last_survey_fila",
        listChildrenOnARTandFormulationsDataset.getLastDrugPickupDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 26 - Last Drug Pick-up Date - Sheet 1: Column Z */
    pdd.addColumn(
        "date_of_last_survey_reception_raised_ARV",
        listOfPatientsDefaultersOrIITCohortQueries.getLastDrugPickUpDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 27 - Next Drug pick-up Date - Sheet 1: Column AA */
    pdd.addColumn(
        "next_date_survey_fila",
        listChildrenOnARTandFormulationsDataset.getNextDrugPickupDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 28 - Next Drug pick-up Date - Sheet 1: Column AB */
    pdd.addColumn(
        "next_date_survey _reception_raised_ARV",
        listOfPatientsDefaultersOrIITCohortQueries.getNextDrugPickUpDateARV(),
        "endDate=${endDate},location=${location}",
        null);

    // 29 - Days of Delay - Sheet 1: Column AC */
    pdd.addColumn(
        "days_of_absence_to_survey",
        listOfPatientsDefaultersOrIITCohortQueries.getNumberOfDaysOfDelay(),
        "endDate=${endDate},location=${location}",
        null);

    return pdd;
  }
}
