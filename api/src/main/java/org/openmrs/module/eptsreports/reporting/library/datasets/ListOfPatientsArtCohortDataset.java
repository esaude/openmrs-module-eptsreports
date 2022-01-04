package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.data.converter.*;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLDataDefinitionQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTInitiationDataDefinitionQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ListOfPatientsArtCohortDataset extends BaseDataSet {

  private ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;

  private TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private TPTInitiationDataDefinitionQueries tptInitiationDataDefinitionQueries;

  private ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  private ListOfPatientsEligibleForVLDataDefinitionQueries
      listOfPatientsEligibleForVLDataDefinitionQueries;

  @Autowired
  public ListOfPatientsArtCohortDataset(
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries,
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      TPTInitiationDataDefinitionQueries tptInitiationDataDefinitionQueries,
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset,
      ListOfPatientsEligibleForVLDataDefinitionQueries
          listOfPatientsEligibleForVLDataDefinitionQueries) {
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.tptInitiationDataDefinitionQueries = tptInitiationDataDefinitionQueries;
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.listOfPatientsEligibleForVLDataDefinitionQueries =
        listOfPatientsEligibleForVLDataDefinitionQueries;
  }

  public DataSetDefinition contructDataset() throws EvaluationException {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.setName("ARV");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");
    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");

    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(identifierType.getName(), identifierType),
            identifierFormatter);

    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");

    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);

    pdd.setParameters(getParameters());

    pdd.addRowFilter(
        listOfPatientsArtCohortCohortQueries.getPatientsInitiatedART(),
        "startDate=${startDate},endDate=${endDate},location=${location}");
    // 1- NID sheet 1 - Column A
    pdd.addColumn(
        "nid",
        tptListOfPatientsEligibleDataSet.getNID(identifierType.getPatientIdentifierTypeId()),
        "");

    // 2 - Name - Sheet 1: Column B
    pdd.addColumn("name", nameDef, "");

    // 3 - Sexo - Sheet 1: Column C
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    // 4 - Idade - Sheet 1: Column D
    pdd.addColumn(
        "age",
        listOfPatientsArtCohortCohortQueries.getAge(),
        "evaluationDate=${evaluationDate}",
        new NotApplicableIfNullConverter());

    // 5 - Data Inicio Tarv - Sheet 1: Column E
    pdd.addColumn(
        "inicio_tarv",
        tptInitiationDataDefinitionQueries.getPatientsAndARTStartDate(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    // 6 - Pregnant/Breastfeeding: - Sheet 1: Column F
    pdd.addColumn(
        "pregnant_breastfeeding",
        tptInitiationDataDefinitionQueries.getPatientsThatArePregnantOrBreastfeeding(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new EmptyIfNullConverter());

    // 7 - Patients active on TB Treatment - Sheet 1: Column G
    pdd.addColumn(
        "ontbtreatment",
        listChildrenOnARTandFormulationsDataset.getPatientsActiveOnTB(),
        "endDate=${generationDate},location=${location}",
        new StoYesAndNtoNoConverter());

    // 8 - Last Drug Pick-up Date on FILA- Sheet 1: Column H
    pdd.addColumn(
        "lastpickupdate_fila",
        listChildrenOnARTandFormulationsDataset.getLastDrugPickupDate(),
        "endDate=${evaluationDate},location=${location}",
        null);

    // 9 - Next Drug pick-up Date on FILA- Sheet 1: Column I
    pdd.addColumn(
        "nextpickupdate_fila",
        listChildrenOnARTandFormulationsDataset.getNextDrugPickupDate(),
        "endDate=${evaluationDate},location=${location}",
        null);

    // 10 - Last Drug Pick-up Date on Ficha Mestre- Sheet 1: Column J
    pdd.addColumn(
        "lastpickupdate_mestre",
        listOfPatientsArtCohortCohortQueries.getPatientsAndLastDrugPickUpDateOnFichaMestre(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 11 - Next Drug pick-up Date on Ficha Mestre- Sheet 1: Column K
    pdd.addColumn(
        "nextpickupdate_mestre",
        listOfPatientsArtCohortCohortQueries.getPatientsAndNextpickUpDateOnFichaMestre(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 12 - Last Follow up Consultation Date - Sheet 1: Column L
    pdd.addColumn(
        "lastfollowup",
        listOfPatientsEligibleForVLDataDefinitionQueries
            .getPatientsAndLastFollowUpConsultationDate(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 13 - Next Follow up Consultation Date - Sheet 1: Column M
    pdd.addColumn(
        "nextfollowup",
        listOfPatientsArtCohortCohortQueries.getPatientsAndNextFollowUpConsultationDate(),
        "endDate=${evaluationDate},location=${location}",
        null);

    // 14 - Last state on Program Enrollment - Sheet 1: Column N
    pdd.addColumn(
        "last_state_program",
        listOfPatientsArtCohortCohortQueries.getLastStateOnProgramEnrollment(),
        "startDate=${evaluationDate},location=${location}",
        new StateOfStayArtPatientConverter());

    // 15 - Last state date on Program Enrollment - Sheet 1: Column O
    pdd.addColumn(
        "last_state_date_program",
        listOfPatientsArtCohortCohortQueries.getLastStateDateOnProgramEnrollment(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 16 - Last state on Clinical Consultation - Sheet 1: Column P
    pdd.addColumn(
        "last_state_clinical_consultation",
        listOfPatientsArtCohortCohortQueries.getLastStateOnClinicalConsultation(),
        "startDate=${evaluationDate},location=${location}",
        new StateOfStayArtPatientConverter());

    // 17 - Last state date on Clinical Consultation - Sheet 1: Column Q
    pdd.addColumn(
        "last_state_date_clinical_consultation",
        listOfPatientsArtCohortCohortQueries.getLastStateDateOnClinicalConsultation(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 18 - Last state on Ficha Resumo - Sheet 1: Column R
    pdd.addColumn(
        "last_state_resumo",
        listOfPatientsArtCohortCohortQueries.getLastStateOnFichaResumo(),
        "startDate=${evaluationDate},location=${location}",
        new StateOfStayArtPatientConverter());

    // 19 - Last state date on Ficha Resumo - Sheet 1: Column S
    pdd.addColumn(
        "last_state_date_resumo",
        listOfPatientsArtCohortCohortQueries.getLastStateDateOnFichaResumo(),
        "startDate=${evaluationDate},location=${location}",
        null);

    // 20 - Last state on Home Visit Card - Sheet 1: Column T
    pdd.addColumn(
        "last_state_homevisit",
        listOfPatientsArtCohortCohortQueries.getLastStateOnHomeVisitCard(),
        "startDate=${evaluationDate},location=${location}",
        new StateOfStayArtPatientConverter());

    // 21 - Last state date on Home Visit Card - Sheet 1: Column U
    pdd.addColumn(
        "last_state_date_homevisit",
        listOfPatientsArtCohortCohortQueries.getLastStateDateOnHomeVisitCard(),
        "startDate=${evaluationDate},location=${location}",
        null);

    return pdd;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Cohort Start Date", Date.class),
        new Parameter("endDate", "Cohort End Date", Date.class),
        new Parameter("evaluationDate", "Evaluation Date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
