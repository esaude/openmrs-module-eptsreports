package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Collections;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.NotApplicableIfNullConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.TestResultConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsEligibleForVLDataDefinitionQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWithPositiveTbScreeningCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsWithPositiveTbScreeningDataDefinitionQueries;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWithPositiveTbScreeningDataset extends BaseDataSet {

  private final TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet;

  private final ListOfPatientsWithPositiveTbScreeningCohortQueries
      listOfPatientsWithPositiveTbScreeningCohortQueries;

  private final ListOfPatientsEligibleForVLDataDefinitionQueries
      listOfpatientsEligibleForVLDataDefinitionQueries;

  private final ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset;

  private final ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;

  private final ListOfPatientsWithPositiveTbScreeningDataDefinitionQueries
      listOfPatientsWithPositiveTbScreeningDataDefinitionQueries;

  private final TbMetadata tbMetadata;

  private final HivMetadata hivMetadata;

  private final CommonMetadata commonMetadata;

  @Autowired
  public ListOfPatientsWithPositiveTbScreeningDataset(
      TPTListOfPatientsEligibleDataSet tptListOfPatientsEligibleDataSet,
      ListOfPatientsWithPositiveTbScreeningCohortQueries
          listOfPatientsWithPositiveTbScreeningCohortQueries,
      ListOfPatientsEligibleForVLDataDefinitionQueries
          listOfpatientsEligibleForVLDataDefinitionQueries,
      ListChildrenOnARTandFormulationsDataset listChildrenOnARTandFormulationsDataset,
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries,
      ListOfPatientsWithPositiveTbScreeningDataDefinitionQueries
          listOfPatientsWithPositiveTbScreeningDataDefinitionQueries,
      TbMetadata tbMetadata,
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata) {
    this.tptListOfPatientsEligibleDataSet = tptListOfPatientsEligibleDataSet;
    this.listOfPatientsWithPositiveTbScreeningCohortQueries =
        listOfPatientsWithPositiveTbScreeningCohortQueries;
    this.listOfpatientsEligibleForVLDataDefinitionQueries =
        listOfpatientsEligibleForVLDataDefinitionQueries;
    this.listChildrenOnARTandFormulationsDataset = listChildrenOnARTandFormulationsDataset;
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
    this.listOfPatientsWithPositiveTbScreeningDataDefinitionQueries =
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries;
    this.tbMetadata = tbMetadata;
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
  }

  public DataSetDefinition contructDataset() throws EvaluationException {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    String mappings =
        "startDate=${startDate},endDate=${endDate},generationDate=${generationDate},location=${location}";

    pdd.setName("List of Patients With Positive Tb Screening");

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

    PersonAttributeType contactAttributeType =
        Context.getPersonService()
            .getPersonAttributeTypeByUuid("e2e3fd64-1d5f-11e0-b929-000c29ad1d07");

    DataDefinition conctactDef =
        new ConvertedPersonDataDefinition(
            "contact",
            new PersonAttributeDataDefinition(contactAttributeType.getName(), contactAttributeType),
            null);

    pdd.addParameters(getParameters());

    pdd.addRowFilter(
        listOfPatientsWithPositiveTbScreeningCohortQueries.getBaseCohort(),
        "startDate=${startDate},endDate=${endDate},location=${location}");

    pdd.addColumn("patient_id", new PersonIdDataDefinition(), "");

    // 1- NID sheet 1: Column A
    pdd.addColumn(
        "nid",
        tptListOfPatientsEligibleDataSet.getNID(identifierType.getPatientIdentifierTypeId()),
        "");

    // 2 - Name (Nome) – Sheet 1: Column B
    pdd.addColumn("name", nameDef, "");

    // 3 - Sex (Sexo) – Sheet 1: Column C
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    // 4 - Age (Idade) – Sheet 1: Column D
    pdd.addColumn(
        "age",
        listOfPatientsArtCohortCohortQueries.getAge(),
        "evaluationDate=${endDate}",
        new NotApplicableIfNullConverter());

    // 5 - Contact (Contacto Telefónico) - Sheet 1: Column E
    pdd.addColumn("contact", conctactDef, "");

    // 6 - ART start date (Data Início Tarv) - Sheet 1: Column F
    pdd.addColumn(
        "art_start_date",
        listOfpatientsEligibleForVLDataDefinitionQueries.getPatientsAndARTStartDate(),
        "endDate=${endDate},location=${location}",
        null);

    // 7 - Last Clinical Consultation Date (Data da Última Consulta) Sheet 1: Column G
    pdd.addColumn(
        "last_consultation_date",
        listChildrenOnARTandFormulationsDataset.getLastFollowupConsultationDate(),
        "endDate=${generationDate},location=${location}",
        null);

    // 8 - Date of the Last Positive TB Screening - Sheet 1: Column H
    pdd.addColumn(
        "last_positive_screening_date",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getLastTbPositiveScreeningDate(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        null);

    // 9 - GeneXpert Request Date Ficha Clínica (Data do Pedido GeneXpert) Sheet 1: Column I
    pdd.addColumn(
        "genxpert_request_date_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries
            .getTbLaboratoryResearchRequestDate(
                Collections.singletonList(tbMetadata.getTBGenexpertTestConcept().getConceptId())),
        mappings,
        null);

    // 10 - GeneXpert Result Ficha Clinica - Sheet 1: Column J
    pdd.addColumn(
        "genxpert_result_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTBGenexpertTestConcept().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId())),
        mappings,
        new TestResultConverter());

    // 11 - GeneXpert Result Lab Form - Sheet 1: Column K
    pdd.addColumn(
        "genxpert_result_laboratorio",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTBGenexpertTestConcept().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId())),
        mappings,
        new TestResultConverter());

    // 12 - Xpert MTB Result Lab Form - Sheet 1: Column L
    pdd.addColumn(
        "xpertmtb_result_laboratorio",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTestXpertMtbUuidConcept().getConceptId()),
            Arrays.asList(
                commonMetadata.getYesConcept().getConceptId(),
                commonMetadata.getNoConcept().getConceptId())),
        mappings,
        new TestResultConverter());
    // 13 - Rifampin Resistance Lab Form - Sheet 1: Column M
    pdd.addColumn(
        "rifampin_resistance_laboratorio",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getRifampinResistanceResults(
            Collections.singletonList(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTestXpertMtbUuidConcept().getConceptId()),
            Arrays.asList(
                commonMetadata.getYesConcept().getConceptId(),
                commonMetadata.getNoConcept().getConceptId()),
            tbMetadata.getRifampinResistanceConcept().getConceptId(),
            commonMetadata.getIndeterminate().getConceptId()),
        mappings,
        new TestResultConverter());

    // 14 - BK Request Date - Sheet 1: Column N
    pdd.addColumn(
        "bk_request_date_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries
            .getTbLaboratoryResearchRequestDate(
                Collections.singletonList(hivMetadata.getResultForBasiloscopia().getConceptId())),
        mappings,
        null);

    // 15 - BK Result Ficha Clinica - Sheet 1: Column O
    pdd.addColumn(
        "bk_result_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getResultForBasiloscopia().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId())),
        mappings,
        new TestResultConverter());

    // 16 - BK Result Lab Form - Sheet 1: Column P
    pdd.addColumn(
        "bk_result_laboratorio",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()),
            Collections.singletonList(hivMetadata.getResultForBasiloscopia().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                tbMetadata.getNotFoundTestResultConcept().getConceptId())),
        mappings,
        new TestResultConverter());

    // 17 - TB LAM Request Date - Sheet 1: Column Q
    pdd.addColumn(
        "tblam_request_date_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries
            .getTbLaboratoryResearchRequestDate(
                Collections.singletonList(tbMetadata.getTestTBLAM().getConceptId())),
        mappings,
        null);

    // 18 - TB LAM Result Ficha Clínica - Sheet 1: Column R
    pdd.addColumn(
        "tblam_result_ficha_clinica",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTestTBLAM().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId())),
        mappings,
        new TestResultConverter());

    // 19 - TB LAM Result Lab Form - Sheet 1: Column S
    pdd.addColumn(
        "tblam_result_laboratorio",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries.getTbLaboratoryResearchResults(
            Collections.singletonList(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId()),
            Collections.singletonList(tbMetadata.getTestTBLAM().getConceptId()),
            Arrays.asList(
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId(),
                tbMetadata.getIndeterminate().getConceptId())),
        mappings,
        new TestResultConverter());

    // 20 - TB Treatment Start Date – Sheet 1: Column T
    pdd.addColumn(
        "tb_start_date",
        listOfPatientsWithPositiveTbScreeningDataDefinitionQueries
            .getTbTreatmentStartDateFromSources(),
        mappings,
        null);

    return pdd;
  }
}
