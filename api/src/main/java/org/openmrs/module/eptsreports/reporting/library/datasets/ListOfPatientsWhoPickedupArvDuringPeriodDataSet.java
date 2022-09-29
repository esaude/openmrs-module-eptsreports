package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.data.converter.*;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLDataDefinitionQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWhoPickedupArvDuringPeriodDataSet extends BaseDataSet {

  private final ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;

  private final TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private final ListOfPatientsEligibleForVLDataDefinitionQueries
      listOfpatientsEligibleForVLDataDefinitionQueries;

  private final ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries
      listOfPatientsWhoPickedupArvDuringPeriodCohortQueries;

  private final ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  private final ListOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries
      listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries;

  @Autowired
  public ListOfPatientsWhoPickedupArvDuringPeriodDataSet(
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries,
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      ListOfPatientsEligibleForVLDataDefinitionQueries
          listOfpatientsEligibleForVLDataDefinitionQueries,
      ListOfPatientsWhoPickedupArvDuringPeriodCohortQueries
          listOfPatientsWhoPickedupArvDuringPeriodCohortQueries,
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset,
      ListOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries
          listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries) {
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.listOfpatientsEligibleForVLDataDefinitionQueries =
        listOfpatientsEligibleForVLDataDefinitionQueries;
    this.listOfPatientsWhoPickedupArvDuringPeriodCohortQueries =
        listOfPatientsWhoPickedupArvDuringPeriodCohortQueries;
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries =
        listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries;
  }

  public DataSetDefinition contructDataset() throws EvaluationException {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.setName("List of patients who picked up ARV during the period");

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
        listOfPatientsWhoPickedupArvDuringPeriodCohortQueries.getBaseCohort(),
        "startDate=${startDate},endDate=${endDate},location=${location}");

    pdd.addColumn("patient_id", new PersonIdDataDefinition(), "");

    // 1- NID sheet 1 - Column A
    pdd.addColumn(
        "nid",
        tptListOfPatientsEligibleDataSet.getNID(identifierType.getPatientIdentifierTypeId()),
        "");

    // 2 - Name - Sheet 1: Column B
    pdd.addColumn("name", nameDef, "");

    // 3 - ART start date - Sheet 1: Column C
    pdd.addColumn(
        "art_start_date",
        listOfpatientsEligibleForVLDataDefinitionQueries.getPatientsAndARTStartDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 4 - Age - Sheet 1: Column D
    pdd.addColumn(
        "age",
        listOfPatientsArtCohortCohortQueries.getAge(),
        "evaluationDate=${endDate}",
        new NotApplicableIfNullConverter());

    // 5 - Gender - Sheet 1: Column E
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    // 6 - Pregnancy/Lactation (Gestante/Lactante): - Sheet 1: Column F
    pdd.addColumn(
        "pregnant_breastfeeding",
        tptListOfPatientsEligibleDataSet.pregnantBreasfeediDefinition(),
        "location=${location}",
        null);

    // 7 - Last Patient State (Último Estado de Permanência) - Sheet 1: Column G
    pdd.addColumn(
        "last_state_on_sources",
        listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries
            .getLastStateOrDateFromARTProgramOrFichaResumoOrClinica(true),
        "endDate=${generationDate},location=${location}",
        new StateOfStayArtPatientConverter());

    // 8 - Last Patient State Date (Data Último Estado de Permanência) - Sheet 1: Column H
    pdd.addColumn(
        "last_state_date_on_sources",
        listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries
            .getLastStateOrDateFromARTProgramOrFichaResumoOrClinica(false),
        "endDate=${generationDate},location=${location}",
        null);

    // 9 - Last Patient State Source - Sheet 1: Column I
    pdd.addColumn(
        "last_state_source",
        listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries
            .getLastStateSourceFromARTProgramOrFichaResumoOrClinica(),
        "endDate=${generationDate},location=${location}",
        new StateOfStaySourceConverter());

    // 10 - Last ARV Pick up Date – Sheet 1: Column J
    pdd.addColumn(
        "last_pickup_fila",
        listOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries.getLastDrugPickupDate(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    // 11 - Last ARV Regimen – Sheet 1: Column K
    pdd.addColumn(
        "last_regimen",
        listChildrenOnARTandFormulationsDataset.getLastARVRegimen(),
        "endDate=${endDate},location=${location}",
        new ConceptNameConverter());

    // 12 - Formulation 1 – Sheet 1: Column L
    pdd.addColumn(
        "formulation1",
        listChildrenOnARTandFormulationsDataset.getFormulation(
            ListChildrenOnARTandFormulationsDataset.Formulation.FORMULATION1),
        "onOrBefore=${endDate},location=${location}",
        null);

    // 13 - Formulation 2 – Sheet 1: Column M
    pdd.addColumn(
        "formulation2",
        listChildrenOnARTandFormulationsDataset.getFormulation(
            ListChildrenOnARTandFormulationsDataset.Formulation.FORMULATION2),
        "onOrBefore=${endDate},location=${location}",
        null);

    //  14 - Formulation 3 – Sheet 1: Column N
    pdd.addColumn(
        "formulation3",
        listChildrenOnARTandFormulationsDataset.getFormulation(
            ListChildrenOnARTandFormulationsDataset.Formulation.FORMULATION3),
        "onOrBefore=${endDate},location=${location}",
        null);

    // 15 - Formulation 4 – Sheet 1: Column O
    pdd.addColumn(
        "formulation4",
        listChildrenOnARTandFormulationsDataset.getFormulation(
            ListChildrenOnARTandFormulationsDataset.Formulation.FORMULATION4),
        "onOrBefore=${endDate},location=${location}",
        null);

    // 16 - Next Scheduled Drug Pick up – Sheet 1: Column P
    pdd.addColumn(
        "next_pickupdate_fila",
        listChildrenOnARTandFormulationsDataset.getNextDrugPickupDate(),
        "endDate=${endDate},location=${location}",
        null);

    return pdd;
  }
}
