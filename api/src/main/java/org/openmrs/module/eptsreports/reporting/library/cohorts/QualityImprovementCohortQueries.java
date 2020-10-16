package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.QualiltyImprovementQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QualityImprovementCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private GenderCohortQueries genderCohortQueries;

  /**
   * MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
   *
   * <p>São pacientes que iniciaram TARV dentro do periodo de inclusao, com mínimo de uma consulta
   * após início TARV
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "initialARVInInclusionPeriodWithAtLeastOneEncounter")
  public CohortDefinition getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("initialARVInInclusionPeriodWithAtLeastOneEncounter");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));

    String query =
        QualiltyImprovementQueries.getPatientStartedTarvInInclusionPeriodWithAtLeastOneEncounter(
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            commonMetadata.getYesConcept().getConceptId(),
            hivMetadata.getPateintActiveArtWorkflowState().getProgramWorkflowStateId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata
                .getPateintTransferedFromOtherFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getPateintPregnantWorkflowState().getProgramWorkflowStateId());

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * PACIENTES COM CONSULTA CLINICA DENTRO DE 7 DIAS APOS DIAGNOSTICO
   *
   * <p>São pacientes que tiveram a primeira consulta clínica dentro de 7 dias depois da data de
   * CONSULTAINICdiagnóstico
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "encontersWithinSevenDaysAfterDiagnosis")
  private CohortDefinition getPatientWithEncontersWithinSevenDaysAfterDiagnostic() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("encontersWithinSevenDaysAfterDiagnosis");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWithEncontersWithinSevenDaysAfterDiagnostic(
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfHivDiagnosisConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA TARV E QUE TIVERAM CONSULTA CLINICA DENTRO DE 7 DIAS APOS DIAGNOSTICO
   *
   * @return CompositionCohortDefinition
   */
  public CohortDefinition getPatientInARVSampleWithEncounterIn7DaysAfterDianosis() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappingsAmostraARV =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    String mappingsConsulta = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappingsAmostraARV));
    cd.addSearch(
        "CONSULTA",
        EptsReportUtils.map(
            getPatientWithEncontersWithinSevenDaysAfterDiagnostic(), mappingsConsulta));
    cd.setCompositionString("AMOSTRATARV AND CONSULTA");
    return cd;
  }

  /**
   * PACIENTES QUE TIVERAM RASTREIO DE TUBERCULOSE EM CADA CONSULTA CLINICA
   *
   * <p>São pacientes que durante um periodo tiveram consulta clinica e que foram rastreiados para
   * tuberculose em cada visita (Numero de visitas igual ao numero de rastreios)
   *
   * @return String
   */
  @DocumentedDefinition(value = "patientWithTrackInEachTBEncounter")
  private CohortDefinition getPatientWithTrackInEachTBEncounter() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWithTrackInEachTBEncounter");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWithTrackInEachTBEncounter(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getTbScreeningConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA TARV E QUE NAO SE ENCONTRAM EM TRATAMENTO DE TB E RASTREIADOS EM CADA
   * CONSULTA
   */
  public CohortDefinition getPatientsInARVSampleNotInTBTrackEncounter() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappingsAmostraARV =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    String mappingsConsulta = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappingsAmostraARV));
    cd.addSearch(
        "RASTREIO", EptsReportUtils.map(getPatientWithTrackInEachTBEncounter(), mappingsConsulta));
    cd.setCompositionString("AMOSTRATARV AND RASTREIO");
    return cd;
  }

  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA
   * GRAVIDA) São gravidas inscritas no serviço TARV e que iniciaram tarv e que fazem parte da
   * amostra para a avaliação de qualidade de dados de MQ
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(
      value = "pragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample")
  public CohortDefinition
      getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "pragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries
            .getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(
                hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                commonMetadata.getPregnantConcept().getConceptId(),
                commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
                commonMetadata.getPregnancyDueDate().getConceptId(),
                hivMetadata.getPtvEtvProgram().getProgramId(),
                hivMetadata.getARVPlanConcept().getConceptId(),
                commonMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getARVStartDateConcept().getConceptId(),
                commonMetadata.getYesConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) E QUE FORAM RASTREIADAS PARA
   * TUBERCULOSE EM CADA VISITA Amostra de gravida, MQ e que foram rastreiadas para tuberculose em
   * cada visita
   */
  public CohortDefinition getPregnantPatientsInTBTrackForEachEncounter() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "AMOSTRAGRAVIDA",
        EptsReportUtils.map(
            getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(), mapping));
    cd.addSearch("RASTREIO", EptsReportUtils.map(getPatientWithTrackInEachTBEncounter(), mapping));

    cd.setCompositionString("AMOSTRAGRAVIDA AND RASTREIO");
    return cd;
  }

  /**
   * * PROFILAXIA COM ISONIAZIDA NO SERVIÇO TARV Pacientes que receberam profilaxia com ISONIAZIDA
   * no serviço TARV
   *
   * @return CohortDefinition
   */
  private CohortDefinition usesIsoniazida(
      Concept question,
      TimeModifier timeModifier,
      SetComparator operator,
      List<EncounterType> encounterTypes,
      List<Concept> values) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("uses isoniazid");
    cd.setQuestion(question);
    cd.setOperator(operator);
    cd.setTimeModifier(timeModifier);
    cd.setEncounterTypeList(encounterTypes);
    cd.setValueList(values);

    cd.addParameter(new Parameter("onOrAfter", "DE", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "ATE", Date.class));
    cd.addParameter(new Parameter("locationList", "Unidade Sanitaria", Location.class));

    return cd;
  }
  /**
   * * PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO São pacientes que tiveram rastreio de
   * tuberculose positivo
   *
   * @return CohortDefinition
   */
  private CohortDefinition tbTracking(
      Concept question,
      TimeModifier timeModifier,
      SetComparator operator,
      List<EncounterType> encounterTypes,
      List<Concept> values) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("TB SCREENING");
    cd.setQuestion(question);
    cd.setOperator(operator);
    cd.setTimeModifier(timeModifier);
    cd.setEncounterTypeList(encounterTypes);
    cd.setValueList(values);

    cd.addParameter(new Parameter("onOrAfter", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "US", Location.class));

    return cd;
  }
  /**
   * PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO São pacientes inscritos
   * no programa de tuberculose num determinado periodo
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "pacientsEnrolledInTBProgram")
  public CohortDefinition getPacientsEnrolledInTBProgram() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("pacientsEnrolledInTBProgram");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPacientsEnrolledInTBProgram(
            tbMetadata.getTBProgram().getProgramId()));

    return sqlCohortDefinition;
  }
  /**
   * INICIO DE TRATAMENTO DE TUBERCULOSE DATA NOTIFICADA NAS FICHAS DE: SEGUIMENTO, RASTREIO E LIVRO
   * TB Pacientes que iniciram TB no com a data de inicio de tratamento de TB notificada nas fichas
   * de seguimento adulto e pediatria, rastreio de tuberculose e livro de TB
   */
  private CohortDefinition getPatientsWithNotificationDateInForms() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsWithNotificationDateInForms");
    cd.setQuestion(tbMetadata.getTBDrugTreatmentStartDate());
    cd.setTimeModifier(TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(tbMetadata.getTBLivroEncounterType());
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getPediatriaSeguimentoEncounterType());
    encounterTypes.add(tbMetadata.getTBRastreioEncounterType());
    encounterTypes.add(tbMetadata.getTBProcessoEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * * PACIENTES QUE INICIARAM TRATAMENTO DA TUBERCULOSE NOTIFICADOS NO SERVICO TARV - FEV12
   * Pacientes que iniciaram o tratamento da tuberculose, e que este inicio foi documentado na ficha
   * de seguimento do paciente no serviço TARV.
   *
   * @return CohortDefinition
   */
  private CohortDefinition tbTreatment(
      Concept question,
      TimeModifier timeModifier,
      SetComparator operator,
      List<EncounterType> encounterTypes,
      List<Concept> values) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("tratamento tuberculose");
    cd.setQuestion(question);
    cd.setOperator(operator);
    cd.setTimeModifier(timeModifier);
    cd.setEncounterTypeList(encounterTypes);
    cd.setValueList(values);

    cd.addParameter(new Parameter("onOrAfter", "DE", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "ATE", Date.class));
    cd.addParameter(new Parameter("location", "Unidade Sanitaria", Location.class));

    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES São pacientes
   * notificados do tratamento de tuberculose notificados em diferentes fontes: Antecedentes
   * clinicos adulto e pediatria, seguimento, rastreio de tb, livro de TB.
   */
  private CohortDefinition getPatientsWhichWhereNotifiedOfTBTreatmentInARV() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    // INICIO DE TRATAMENTO DE TUBERCULOSE DATA NOTIFICADA NAS FICHAS DE: SEGUIMENTO, RASTREIO E
    // LIVRO TB
    cd.addSearch(
        "DATAINICIO",
        EptsReportUtils.map(
            getPatientsWithNotificationDateInForms(),
            "value1=${startDate},value2=${endDate},locationList=${location}"));

    // PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO
    cd.addSearch(
        "TBPROGRAMA",
        EptsReportUtils.map(
            getPacientsEnrolledInTBProgram(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    // PACIENTES QUE INICIARAM TRATAMENTO DA TUBERCULOSE NOTIFICADOS NO SERVICO TARV - FEV12
    cd.addSearch(
        "INICIOST",
        EptsReportUtils.map(
            tbTreatment(
                tbMetadata.getTBTreatmentPlanConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    tbMetadata.getTBProcessoEncounterType()),
                Arrays.asList(tbMetadata.getStartDrugsConcept())),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("DATAINICIO OR TBPROGRAMA OR INICIOST");
    return cd;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA (TPI) NUM DETERMINADO
   * PERIODO
   *
   * <p>São pacientes que são elegíveis a profilaxia com isoniazida num determinado periodo:
   * Criterios de Elegibilidade - 1) Fazer parte da amostra TARV 2) Não estar em tratamento de TB
   * actualmente e nos ultimos 2 anos 3) Não tiver feito TPI nos ultimos 2 anos 4) Nao tiver
   * rastreio de TB positivo no periodo
   */
  public CohortDefinition getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappingsAmostraARV =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappingsAmostraARV));

    // PROFILAXIA COM ISONIAZIDA NO SERVIÇO TARV
    cd.addSearch(
        "PROFILAXIATPI",
        EptsReportUtils.map(
            usesIsoniazida(
                hivMetadata.getIsoniazidUsageConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getARVPediatriaInitialBEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${startDate-2y},onOrBefore=${startDate-1d},locationList=${location}"));

    cd.addSearch(
        "RASTREIOPOSITIVO",
        EptsReportUtils.map(
            tbTracking(
                tbMetadata.getTbScreeningConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${startDate},onOrBefore=${dataFinalAvaliacao},locationList=${location}"));

    cd.addSearch(
        "TRATAMENTOTB",
        EptsReportUtils.map(
            getPatientsWhichWhereNotifiedOfTBTreatmentInARV(),
            "startDate=${startDate-2y},endDate=${dataFinalAvaliacao},location=${location}"));

    cd.setCompositionString("AMOSTRATARV NOT (TRATAMENTOTB OR RASTREIOPOSITIVO OR PROFILAXIATPI)");
    return cd;
  }

  /**
   * PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA
   *
   * <p>Pacientes que iniciaram profilaxia com Isoniazida num periodo. Repare que e diferente de
   * pacientes que receberam profilaxia
   */
  private CohortDefinition getPatientsWhoStartedProfilaxiaWithIzoniazida() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsWhoStartedProfilaxiaWithIzoniazida");
    cd.setQuestion(hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.FIRST);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA (TPI) E QUE RECEBERAM
   *
   * <p>São pacientes na amostra TARV de melhoria de qualidade elegiveis a profilaxia com isoniazida
   * e que estão a receber ou que receberam o tratamento profilatico no periodo
   */
  public CohortDefinition getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaTPI() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    cd.addSearch(
        "PROFILAXIAINICIO",
        EptsReportUtils.map(
            getPatientsWhoStartedProfilaxiaWithIzoniazida(),
            "value1=${startDate},value2=${dataFinalAvaliacao},locationList=${location}"));
    cd.addSearch(
        "AMOSTRAELEGIVELTPI",
        EptsReportUtils.map(
            getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod(), mappings));

    cd.setCompositionString("PROFILAXIAINICIO AND AMOSTRAELEGIVELTPI");

    return cd;
  }

  /**
   * MQ_PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI) NO PERIODO DE INCLUSAO E QUE
   * TERMINARAM
   *
   * <p>São pacientes que iniciaram a profilaxia com isoniazida no período de inclusão e que já
   * terminaram
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "patientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted")
  public CohortDefinition getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "patientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries
            .getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted(
                hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA QUE INICIARAM, DEVERIAM
   * TERMINAR E TERMINARAM São pacientes na amostra TARV e que são elegíveis a profilaxia com
   * isoniazida e que iniciaram a profilaxia com isoniazida e que deveriam terminar, isto é, já
   * passaram mais de 6 meses desde que iniciou a profilaxia e que terminaram
   */
  public CohortDefinition getPatientsInARVSampleElegibleToProphilaxisIzoniazidWhomCompleted() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    cd.addSearch(
        "ELEGIVEIS",
        EptsReportUtils.map(
            getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod(), mappings));

    cd.addSearch(
        "PROFILAXIATPI",
        EptsReportUtils.map(
            getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriodAndCompleted(), mappings));

    cd.setCompositionString("ELEGIVEIS AND PROFILAXIATPI");

    return cd;
  }

  /**
   * MQ_PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA (TPI) NO PERIODO DE INCLUSAO São pacientes
   * que iniciaram a profilaxia com INH no período de inclusão
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "patientWhoStartedIsoniazidProphylaxisInInclusioPeriod")
  private CohortDefinition getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWhoStartedIsoniazidProphylaxisInInclusioPeriod");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriod(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA, INICIARAM E QUE DEVERIAM
   * TERMINAR São pacientes na amostra TARV elegiveis a profilaxia com Isoniazida, que iniciaram a
   * profilaxia e que deveriam terminar, isto é, já passam mais de 6 meses desde a data de inicio da
   * profilaxia
   */
  public CohortDefinition
      getPatientInARVSampleElegibleToProphylaxisIsoniazisStaredShouldComplete() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    cd.addSearch(
        "PROFILAXIATPI",
        EptsReportUtils.map(
            getPatientWhoStartedIsoniazidProphylaxisInInclusioPeriod(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "ELEGIVEIS",
        EptsReportUtils.map(
            getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod(), mappings));

    cd.setCompositionString("ELEGIVEIS AND PROFILAXIATPI");

    return cd;
  }

  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) ELEGIVEIS A PROFILAXIA COM ISONIAZIDA
   * São gravidas na amostra MQ e que sao elegiveis a profilaxia com INH
   */
  public CohortDefinition getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazid() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    // PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO
    cd.addSearch(
        "RASTREIOPOSITIVO",
        EptsReportUtils.map(
            tbTracking(
                tbMetadata.getTbScreeningConcept(),
                TimeModifier.LAST,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${startDate},onOrBefore=${dataFinalAvaliacao},locationList=${location}"));

    // MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA
    // GRAVIDA)
    cd.addSearch(
        "AMOSTRAGRAVIDA",
        EptsReportUtils.map(
            getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    // PROFILAXIA COM ISONIAZIDA NO SERVIÇO TARV
    cd.addSearch(
        "PROFILAXIAINH",
        EptsReportUtils.map(
            usesIsoniazida(
                hivMetadata.getIsoniazidUsageConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getARVPediatriaInitialBEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${startDate-2y},onOrBefore=${startDate-1d},locationList=${location}"));

    // PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES
    cd.addSearch(
        "TRATAMENTOTB",
        EptsReportUtils.map(
            getPatientsWhichWhereNotifiedOfTBTreatmentInARV(),
            "startDate=${startDate-2y},endDate=${dataFinalAvaliacao},location=${location}"));

    cd.setCompositionString(
        "AMOSTRAGRAVIDA NOT (PROFILAXIAINH OR TRATAMENTOTB OR RASTREIOPOSITIVO)");

    return cd;
  }

  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) ELEGIVEIS A PROFILAXIA COM ISONIAZIDA E
   * QUE RECEBERAM Gravidas na amostra de MQ, elegíveis a profilaxia com INH e que receberam
   */
  public CohortDefinition
      getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) ELEGIVEIS A PROFILAXIA COM ISONIAZIDA
    cd.addSearch(
        "GRAVIDAELEGINH",
        EptsReportUtils.map(
            getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazid(), mappings));

    // PROFILAXIA COM ISONIAZIDA NO SERVIÇO TARV
    cd.addSearch(
        "PROFILAXIAINH",
        EptsReportUtils.map(
            usesIsoniazida(
                hivMetadata.getIsoniazidUsageConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getARVPediatriaInitialBEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${startDate},onOrBefore=${dataFinalAvaliacao},locationList=${location}"));

    // PACIENTES QUE INICIARAM PROFILAXIA COM ISONIAZIDA
    cd.addSearch(
        "PROFILAXIAINHINICIO",
        EptsReportUtils.map(
            getPatientsWhoStartedProfilaxiaWithIzoniazida(),
            "value1=${startDate},value2=${dataFinalAvaliacao},locationList=${location}"));

    cd.setCompositionString("(PROFILAXIAINH OR PROFILAXIAINHINICIO) AND GRAVIDAELEGINH");

    return cd;
  }

  /**
   * MQ_PACIENTES QUE TIVERAM CONSULTA CLINICA NUM PERIODO E QUE TIVERAM RASTREIO DE ITS EM CADA
   * VISITA
   *
   * <p>Sao pacientes que tiveram consulta clinica num periodo e que tiveram rastreio de ITS em cada
   * consulta
   */
  @DocumentedDefinition(value = "patientsWithEnconterInPeriodAndHadScreeningForSTI")
  private CohortDefinition getPatientsWithEnconterInPeriodAndHadScreeningForSTI() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWithEnconterInPeriodAndHadScreeningForSTI");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWithEnconterInPeriodAndHadScreeningForSTI(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            commonMetadata.getScreeningForSTIConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA TARV E QUE TIVERAM RASTREIO DE ITS EM CADA VISITA DO PERIODO DE ANALISE
   * Sao pacientes que fazem parte da amostra TARV e que foram rastreiados para ITS em cada consulta
   * durante o periodo em analise
   */
  public CohortDefinition getPacientsInARVSampleWhichHadScreeningForSTIInEncounterAnalisisPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    // MQ_PACIENTES QUE TIVERAM CONSULTA CLINICA NUM PERIODO E QUE TIVERAM RASTREIO DE ITS EM CADA
    // VISITA
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "RASTREIOITS",
        EptsReportUtils.map(getPatientsWithEnconterInPeriodAndHadScreeningForSTI(), mappings));

    cd.setCompositionString("AMOSTRATARV AND RASTREIOITS");
    return cd;
  }

  /**
   * MQ_PACIENTES COM CD4 REGISTADO DENTRO DE 33 DIAS APOS A INSCRICAO Sao pacientes que abriram
   * processo (inscritos) e que tiveram CD4 registado dentro de 33 dias após a inscrição
   */
  @DocumentedDefinition(value = "pacientsWithCD4RegisteredIn33Days")
  private CohortDefinition getPacientsWithCD4RegisteredIn33Days() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("pacientsWithCD4RegisteredIn33Days");

    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPacientsWithCD4RegisteredIn33Days(
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata
                .getPateintActiveOnHIVCareProgramtWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getCD4AbsoluteOBSConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV COM CD4 REGISTADO DENTRO DE 35 DIAS APOS A INSCRICAO Sao pacientes
   * na amostra tarv com CD4 registado na ficha de seguimento dentro de 35 dias apos inscricao. A
   * inscrição pode ter ocorrido fora do período de inclusão.
   */
  public CohortDefinition getPacientsInARVWithCD4SampleRegisteredWithin35Days() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    // MQ_PACIENTES COM CD4 REGISTADO DENTRO DE 33 DIAS APOS A INSCRICAO
    cd.addSearch(
        "CD4",
        EptsReportUtils.map(
            getPacientsWithCD4RegisteredIn33Days(), "endDate=${endDate},location=${location}"));

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    cd.setCompositionString("AMOSTRATARV AND CD4");
    return cd;
  }

  /**
   * MQ_PACIENTES QUE INICIARAM TARV DENTRO DE 15 DIAS DEPOIS DA INSCRICAO Sao pacientes que
   * iniciaram TARV dentro de 15 dias depois da abertura de processo ou de inscricão no programa de
   * tratamento
   */
  @DocumentedDefinition(value = "patientsWhomStartedARVIn15Days")
  private CohortDefinition getPatientsWhomStartedARVIn15Days() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWhomStartedARVIn15Days");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWhomStartedARVIn15Days(
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV E QUE INICIARAM TARV DENTRO DE 15 DIAS DEPOIS DE DECLARADAS
   * ELEGIVEIS São pacientes na amostra de TARV e que iniciaram tarv dentro de 15 dias depois de
   * declaradas elegiveis ao TARV
   */
  public CohortDefinition getPacientsInARVSampleStartedIn15DaysAfterBeingDeclaredAsElegible() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    // MQ_PACIENTES QUE INICIARAM TARV DENTRO DE 15 DIAS DEPOIS DA INSCRICAO
    cd.addSearch(
        "ELEGIVEIS",
        EptsReportUtils.map(
            getPatientsWhomStartedARVIn15Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    cd.setCompositionString("AMOSTRATARV AND ELEGIVEIS");
    return cd;
  }

  /**
   * PACIENTES QUE INICIARAM TARV NUM PERIODO E QUE TIVERAM SEGUNDO LEVANTAMENTO OU CONSULTA CLINICA
   * DENTRO DE 33 DIAS DEPOIS DE INICIO São pacientes que iniciaram TARV num determinado periodo e
   * que tiveram segundo levantamento de ARV ou segunda consulta clinica dentro de 33 dias do inicio
   * de TARV
   */
  @DocumentedDefinition(value = "pacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining")
  private CohortDefinition getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("pacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getPateintTransferedFromOtherFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV QUE RETORNARAM PARA 2ª CONSULTA CLINICA OU LEVANTAMENTO DE ARV
   * DENTRO DE 33 DIAS APÓS INICIO DO TARV
   *
   * <p>São pacientes na amostra de tarv de melhoria de qualidade que retornaram para a 2ª consulta
   * clínica ou 2º levantamento de ARVs dentro de 33 dias depois de terem iniciado o TARV
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPacientsARVInAPeriodWhoReturnedToEncounter33DaysAfterBegining() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";
    // PACIENTES QUE INICIARAM TARV NUM PERIODO E QUE TIVERAM SEGUNDO LEVANTAMENTO OU CONSULTA
    // CLINICA DENTRO DE 33 DIAS DEPOIS DO INICIO
    cd.addSearch(
        "SEGUNDAVISITA",
        EptsReportUtils.map(
            getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    cd.setCompositionString("AMOSTRATARV AND SEGUNDAVISITA");
    return cd;
  }

  /**
   * MQ_PACIENTES QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTOS DENTRO DE 3 MESES
   * DEPOIS DE INICIO DE TARV - NOVO São pacientes que iniciaram tarv num determinado período e que
   * tiveram 3 consultas clínicas ou levantamentos nos primeiros 3 meses depois de inicio de tarv. A
   * tolerância é de 9 dias. A consulta que coincide com a data de inicio de tarv não é contada.
   */
  @DocumentedDefinition(value = "patientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART")
  private CohortDefinition getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("testStart", "Test Start ", Boolean.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTO DE ARV
   * DENTRO DE 3 MESES DEPOIS DE INICIO DE TARV - NOVO
   *
   * <p>São pacientes na amostra de tarv de melhoria de qualidade que tiveram pelo menos 3 consultas
   * ou levantamentos nos primeiros 3 meses de TARV.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPacientsARVSampleWhoHadAtLeast3Encounters3MonthsAfterTARVStartNew() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    // MQ_PACIENTES QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTOS DENTRO DE 3 MESES
    // DEPOIS DE INICIO DE TARV - NOVO
    cd.addSearch(
        "TRESCONSULTAS",
        EptsReportUtils.map(
            getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART(),
            "startDate=${startDate},endDate=${endDate},location=${location},testStart=${testStart}"));

    cd.setCompositionString("AMOSTRATARV AND TRESCONSULTAS");
    return cd;
  }

  /**
   * PACIENTES QUE TIVERAM PELO MENOS 3 AVALIAÇÕES DE ADESÃO DENTRO DE 3 MESES DEPOIS DE INICIO DE
   * TARV - ARIEL São pacientes que tiveram 3 avaliações de adesão (Ficha de apoio psicossocial e
   * PP: Actividade=Seguimento de Aconselhamento) dentro de 3 meses depois de inicio de TARV. A
   * avaliação de adesão feita no mesmo dia de inicio de TARV não é contada.
   */
  @DocumentedDefinition(value = "patientWhoAtLeast3AdherenceEvaluationWithin3MothsARIEL")
  private CohortDefinition getPatientWhoAtLeast3AdherenceEvaluationWithin3MothsARIEL() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWhoAtLeast3AdherenceEvaluationWithin3MothsARIEL");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("testStart", "Test Start ", Boolean.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWhoAtLeast3JoiningEvaluationWithin3MothsARIEL(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getCoucelingActivityConcept().getConceptId(),
            hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getAdherenceCoucelingConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA TARV QUE TIVERAM PELO MENOS 3 AVALIAÇÕES DE ADESÃO DENTRO DE 3 MESES
   * DEPOIS DE INICIO DE TARV - NOVO São pacientes na amostra de TARV de melhoria de qualidade e que
   * tiveram pelo menos 3 avaliações de adesão nos primeiros 3 meses de inicio de TARV. A avalição
   * de adesão que coincide com a data de inicio de TARV não é contada
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientInTARVSampleWhoHaddAtLeast3AdherenceEvaluationWithin3MothsARIEL() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "AMOSTRATARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    // PACIENTES QUE TIVERAM PELO MENOS 3 AVALIAÇÕES DE ADESÃO DENTRO DE 3 MESES DEPOIS DE INICIO
    // TARV - ARIEL
    cd.addSearch(
        "AVALIACAOADESAO",
        EptsReportUtils.map(
            getPatientWhoAtLeast3AdherenceEvaluationWithin3MothsARIEL(),
            "startDate=${startDate},endDate=${endDate},location=${location},testStart=${testStart}"));

    cd.setCompositionString("AMOSTRATARV AND AVALIACAOADESAO");
    return cd;
  }
  /**
   * MQ_PACIENTES COM CONSULTAS MENSAIS APOS INICIO DE TARV Sao pacientes que tem consultas mensais
   * durante um periodo contado a partir da data de início do TARV: O Calculo de consultas mensais
   * é: O numero de consultas no periodo deve ser superior ou igual ao numero de meses desse periodo
   */
  @DocumentedDefinition(value = "pacientesWithMonthEncountersAfterInitialization")
  private CohortDefinition getPacientesWithMonthEncountersAfterInitialization() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("pacientesWithMonthEncountersAfterInitialization");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPacientesWithMonthEncountersAfterInitialization(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES NA AMOSTRA (INICIO TARV) E QUE TIVERAM CONSULTAS MENSAIS APOS INICIO DE TARV -
   * NOVO Sao pacientes na amostra de inicio de TARV e que tiveram consultas mensais dentro do
   * periodo em anáslise
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientInTARVSampleWhomHadMonthEncountersAfterTARVInitialization() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    // MQ_PACIENTES COM CONSULTAS MENSAIS APOS INICIO DE TARV
    cd.addSearch(
        "CONSULTAS",
        EptsReportUtils.map(getPacientesWithMonthEncountersAfterInitialization(), mappings));

    cd.setCompositionString("CONSULTAS AND INICIOTARV");
    return cd;
  }
  /**
   * MQ_PACIENTES COM CONSULTAS MENSAIS DE APSS APOS INICIO DE TARV Sao pacientes que tem consultas
   * mensais de APSS durante um período contado a partir da data de início do TARV: O Cálculo de
   * consultas mensais é: O número de consultas no período deve ser superior ou igual ao numero de
   * meses desse período. Para pacientes com idade superior a 2 anos, 3 consultas nos primeiros 3
   * meses após início TARV.
   */
  @DocumentedDefinition(value = "patientsWithAPSSMonthEncountersAfterTARVInitialization")
  private CohortDefinition getPatientsWithAPSSMonthEncountersAfterTARVInitialization() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWithAPSSMonthEncountersAfterTARVInitialization");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWithAPSSMonthEncountersAfterTARVInitialization(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            commonMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getCoucelingActivityConcept().getConceptId(),
            hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getAdherenceCoucelingConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /**
   * MQ_PACIENTES NA AMOSTRA (INICIO TARV) E QUE TIVERAM CONSULTAS MENSAIS DE APSS APOS INICIO DE
   * TARV São pacientes na amostra de inicio de TARV e que tiveram consultas mensais de APSS dentro
   * do periodo em análise. Pacientes com menos de 2 anos, 3 consultas nos primeiros 3 meses após
   * início de TARV
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientInTARVSampleWhomHadAPSSMonthEncountersAfterTARVInitialization() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO
    cd.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(
            getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(), mappings));

    // MQ_PACIENTES COM CONSULTAS MENSAIS DE APSS APOS INICIO DE TARV
    cd.addSearch(
        "CONSULTAS",
        EptsReportUtils.map(getPatientsWithAPSSMonthEncountersAfterTARVInitialization(), mappings));

    cd.setCompositionString("CONSULTAS AND INICIOTARV");
    return cd;
  }

  /**
   * MQ_GRAVIDAS NA AMOSTRA TARV QUE RETORNARAM PARA 2ª CONSULTA CLINICA OU LEVANTAMENTO DE ARV
   * DENTRO DE 33 DIAS APÓS INICIO DO TARV São grávidas na amostra de tarv de melhoria de qualidade
   * que retornaram para a 2ª consulta clínica ou 2º levantamento de ARVs dentro de 33 dias depois
   * de terem iniciado o TARV
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregantPatientInTARVSampleReturnedToSecondEncounterIn33ARVStart() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA
    // GRAVIDA)
    cd.addSearch(
        "AMOSTRAGRAVIDA",
        EptsReportUtils.map(
            getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(),
            mappings));

    // PACIENTES QUE INICIARAM TARV NUM PERIODO E QUE TIVERAM SEGUNDO LEVANTAMENTO OU CONSULTA
    // CLINICA DENTRO DE 33 DIAS DEPOIS DE INICIO
    cd.addSearch(
        "SEGUNDAVISITA",
        EptsReportUtils.map(
            getPacientsStartedARVInAPeriodAndHadEncounter33DaysAfterBegining(), mappings));

    cd.setCompositionString("AMOSTRAGRAVIDA AND SEGUNDAVISITA");
    return cd;
  }

  /**
   * MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) E QUE TIVERAM 3 CONSULTAS OU 3
   * LEVANTAMENTOS NOS PRIMEIROS 3 MESES DE TARV - NOVO Sao grávidas na amostra de MQ e que tiveram
   * 3 consultas ou levantamentos nos primeiros 3 meses de inicio de TARV
   */
  public CohortDefinition
      getPregantPatientsEnrolledInTARVServiceWhoHas3EncountersInFisrt3MonthsTARVNew() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // MQ_PACIENTES QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTOS DENTRO DE 3 MESES
    // DEPOIS DE INICIO DE TARV - NOVO
    cd.addSearch(
        "CONSULTAS",
        EptsReportUtils.map(
            getPatientsWhoHadAtLeast3EncountersIn3MonthsAfterBeginingART(), mappings));

    // MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA
    // GRAVIDA)
    cd.addSearch(
        "AMOSTRAGRAVIDA",
        EptsReportUtils.map(
            getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(),
            mappings));

    cd.setCompositionString("CONSULTAS AND AMOSTRAGRAVIDA");
    return cd;
  }

  /**
   * PACIENTES INSCRITOS NO GAAC NUM PERIODO Sao pacientes que foram inscritos num grupo GAAC
   * durante um determinado periodo
   */
  @DocumentedDefinition(value = "patientsEnrolledInGaacInAPeriod")
  private CohortDefinition getPatientsEnrolledInGaacInAPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsEnrolledInGaacInAPeriod");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(QualiltyImprovementQueries.getPatientsEnrolledInGaacInAPeriod());
    return sqlCohortDefinition;
  }

  /**
   * PACIENTES COM CD4>200 OU CV<1000 NOS ULTIMOS 12 MESES São pacientes com último CD4>200 ou
   * última carga viral<1000 nos últimos 12 meses
   */
  @DocumentedDefinition(value = "patientsWithCD4GreterThan200ORCVLessThan1000InLast12Months")
  private CohortDefinition getPatientsWithCD4GreterThan200ORCVLessThan1000InLast12Months() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWithCD4GreterThan200ORCVLessThan1000InLast12Months");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWithCD4GreterThan200ORCVLessThan1000InLast12Months(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getCD4AbsoluteConcept().getConceptId(),
            hivMetadata.getCD4AbsoluteOBSConcept().getConceptId(),
            hivMetadata.getHivViralLoadConcept().getConceptId()));
    return sqlCohortDefinition;
  }
  /**
   * PACIENTES QUE ESTAO HA MAIS DE 6 MESES EM TARV Sao pacientes que iniciaram tarv ha mais de 6
   * meses
   */
  @DocumentedDefinition(value = "patientsInTARVMoreThan6Months")
  private CohortDefinition getPatientsInTARVMoreThan6Months() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsInTARVMoreThan6Months");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsInTARVMoreThan6Months(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }

  /**
   * INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL São pacientes que iniciaram TARV
   * registado no conceito 'Data de Inicio de TARV' na ficha de seguimento
   */
  private CohortDefinition getPatientsInitializedTARVRegisteredInConceptStartDateInFollowingForm() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("patientsInitializedTARVRegisteredInConceptStartDateInFollowingForm");
    cd.setQuestion(hivMetadata.getARVStartDateConcept());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getARVPharmaciaEncounterType());
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "Unidade Sanitaria", Location.class));

    return cd;
  }
  /**
   * ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL Pacientes que alguma vez esteve em
   * tratamento ARV (Iniciou ou veio transferido de outra us em TARV) até um determinado periodo
   * final
   */
  public CohortDefinition getPatientWhoWereInARVTreatmentFinalPeriod() {

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("patientWhoWereInARVTreatmentFinalPeriod");
    cd.setQuestion(hivMetadata.getARVPlanConcept());
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(TimeModifier.ANY);
    cd.setEncounterTypeList(
        Arrays.asList(
            hivMetadata.getARVPharmaciaEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getPediatriaSeguimentoEncounterType()));
    cd.setValueList(
        Arrays.asList(
            hivMetadata.getStartDrugsConcept(), hivMetadata.getTransferFromOtherFacilityConcept()));

    cd.addParameter(new Parameter("onOrBefore", "ATE", Date.class));
    cd.addParameter(new Parameter("locationList", "Unidade Sanitaria", Location.class));

    return cd;
  }
  /**
   * ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - FARMACIA Pacientes que alguma vez esteve
   * em tratamento ARV, levantou pelo menos uma vez ARV na Famacia (tem pelo menos um FRIDA/FILA
   * preenchido) até um determinado periodo final
   */
  private CohortDefinition getPatientWhoWereInARVTreatmentFinalPeriodPharmacy() {
    EncounterCohortDefinition encounterCohortDefinition = new EncounterCohortDefinition();
    encounterCohortDefinition.setName("patientWhoWereInARVTreatmentFinalPeriodPharmacy");
    encounterCohortDefinition.setEncounterTypeList(
        Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()));
    encounterCohortDefinition.setTimeQualifier(TimeQualifier.ANY);
    encounterCohortDefinition.setReturnInverse(false);
    encounterCohortDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    encounterCohortDefinition.addParameter(
        new Parameter("locationList", "Location", Location.class));

    return encounterCohortDefinition;
  }
  /**
   * PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) - PERIODO FINAL Sao pacientes
   * inscritos no programa de tratamento ARV até um determinado periodo final
   */
  @DocumentedDefinition(value = "patientEnrolledARTProgramFinalPeriod")
  public CohortDefinition getPatientEnrolledARTProgramFinalPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientEnrolledARTProgramFinalPeriod");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientEnrolledARTProgramFinalPeriod(
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }

  /**
   * ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO) São pacientes que
   * alguma vez esteve em tratamento ARV, contruido atravez de: 1) Pacientes que alguma vez esteve
   * registado no programa de TARV TRATAMENTO 2) Paciente que teve algum formulario FRIDA/FILA
   * preenchido 3) Paciente com data de inicio preenchido na ficha de seguimento 4) Paciente com
   * inicio registado atraves do conceito 1255 - Gestão de TARV e a resposta é 1256 - Inicio
   */
  private CohortDefinition getPatientInTARVFinalPeriodRealComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    // PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) - PERIODO FINAL
    cd.addSearch(
        "PROGRAMA", EptsReportUtils.map(getPatientEnrolledARTProgramFinalPeriod(), mappings));

    // INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL
    cd.addSearch(
        "CONCEITODATA",
        EptsReportUtils.map(
            getPatientsInitializedTARVRegisteredInConceptStartDateInFollowingForm(),
            "value1=${endDate},locationList=${location}"));

    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL
    cd.addSearch(
        "CONCEITO1255",
        EptsReportUtils.map(
            getPatientWhoWereInARVTreatmentFinalPeriod(),
            "onOrBefore=${endDate},locationList=${location}"));

    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - FARMACIA
    cd.addSearch(
        "FRIDAFILA",
        EptsReportUtils.map(
            getPatientWhoWereInARVTreatmentFinalPeriodPharmacy(),
            "onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("CONCEITO1255 OR PROGRAMA OR CONCEITODATA OR FRIDAFILA");
    return cd;
  }

  /**
   * PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE TRATAMENTO ARV: PERIODO FINAL São pacientes que
   * sairam do programa de tratamento ARV até um determinado periodo final. Inclui todo tipo de
   * saída: ABANDONO, OBITO, TRANSFERIDO PARA e SUSPENSO
   */
  @DocumentedDefinition(value = "patientWhoCameOutARTProgramFinalPeriod")
  public CohortDefinition getPatientWhoCameOutARTProgramFinalPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWhoCameOutARTProgramFinalPeriod");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWhoCameOutARTProgramFinalPeriod(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()));

    return sqlCohortDefinition;
  }

  /**
   * ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL Pacientes que estão actualmente em
   * tratamento ARV. Construido atraves de Alguma vez esteve em tratamento menos os que sairam de
   * tratamento
   */
  private CohortDefinition getPatientInARTTreatmentCompositionFinalPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO)
    cd.addSearch(
        "ALGUMAVEZTARV",
        EptsReportUtils.map(getPatientInTARVFinalPeriodRealComposition(), mappings));

    // PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE TRATAMENTO ARV: PERIODO FINAL
    cd.addSearch(
        "SAIDAPROGRAMA",
        EptsReportUtils.map(getPatientWhoCameOutARTProgramFinalPeriod(), mappings));

    cd.setCompositionString("ALGUMAVEZTARV NOT SAIDAPROGRAMA");
    return cd;
  }
  /**
   * ABANDONO NÃO NOTIFICADO - TARV São pacientes que desde a ultima data marcada para levantamento
   * até a data final passam mais de 60 dias sem voltar e que ainda não foram notificados como
   * Abandono.
   */
  @DocumentedDefinition(
      value = "patientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment")
  private CohortDefinition
      getPatientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "patientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries
            .getPatientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
                hivMetadata.getARTProgram().getProgramId(),
                commonMetadata.getReturnVisitDateConcept().getConceptId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
                hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
                hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId()));

    return sqlCohortDefinition;
  }

  /**
   * ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
   * Sao pacientes que actualmente estao em TARV sem incluir abandonos nao notificados
   */
  private CohortDefinition
      getPatientInARTTreatmentTillSpecificFinalPeriodWithoutAbandonmentNotNotified() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    // ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) -  PERIODO FINAL
    cd.addSearch(
        "TARV", EptsReportUtils.map(getPatientInARTTreatmentCompositionFinalPeriod(), mappings));

    // ABANDONO NÃO NOTIFICADO - TARV
    cd.addSearch(
        "NAONOTIFICADO",
        EptsReportUtils.map(
            getPatientWhichMoreThan60DaysPassedWithoutReturnAndNotNitifiedAsAbandonment(),
            mappings));

    cd.setCompositionString("TARV NOT NAONOTIFICADO");
    return cd;
  }

  /**
   * PACIENTES ACTUALMENTE EM TARV E QUE ESTAO HA MAIS DE 6 MESES EM TRATAMENTO Sao pacientes
   * actualmente em TARV e que estão há mais de 6 meses em tratamento ARV
   */
  private CohortDefinition getPatientInARTTreatmentWithMoreThan6Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "endDate=${endDate},location=${location}";

    // ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
    cd.addSearch(
        "ACTUALTARV",
        EptsReportUtils.map(
            getPatientInARTTreatmentTillSpecificFinalPeriodWithoutAbandonmentNotNotified(),
            mappings));

    // PACIENTES QUE ESTAO HA MAIS DE 6 MESES EM TARV
    cd.addSearch("HA6MESES", EptsReportUtils.map(getPatientsInTARVMoreThan6Months(), mappings));

    cd.setCompositionString("ACTUALTARV AND HA6MESES");
    return cd;
  }
  /**
   * GRAVIDAS INSCRITAS NO SERVIÇO TARV São pacientes que estão gravidas durante a abertura do
   * processo ou durante o seguimento no serviço TARV e que foi notificado como nova gravidez
   * durante o seguimemento.
   */
  @DocumentedDefinition(value = "pregnantPatientEnrolledInTARVService")
  public CohortDefinition getPregnantPatientEnrolledInTARVService() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("pregnantPatientEnrolledInTARVService");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPregnantPatientEnrolledInTARVService(
            commonMetadata.getPregnantConcept().getConceptId(),
            commonMetadata.getPregnancyDueDate().getConceptId(),
            commonMetadata.getNumberOfWeeksPregnant().getConceptId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPtvEtvProgram().getProgramId(),
            commonMetadata.getYesConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * PACIENTES COM DATA DE PARTO ACTUALIZADO NO SERVICO TARV Sao pacientes com data de parto
   * actualizado no servico tarv. Repare que os parametros 'Data Inicial' e 'Data Final' refere-se a
   * data de parto e nao data de registo (actualizacao)
   */
  private CohortDefinition getPregantPatientWithDueDateUpdateInTARV() {

    DateObsCohortDefinition cd = new DateObsCohortDefinition();

    cd.setName("pregantPatientWithDueDateUpdateInTARV");
    cd.setQuestion(commonMetadata.getPriorDeliveryDateConcept());
    cd.setTimeModifier(TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getARVAdultInitialEncounterType());
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());

    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("value2", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "US", Location.class));

    return cd;
  }
  /**
   * LACTANTES REGISTADAS São pacientes que foram actualizados como lactantes na ficha de seguimento
   */
  public CohortDefinition getInfantPatientsEnrolledInTarvSample() {

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("infantPatientsEnrolledInTarvSample");
    cd.setQuestion(commonMetadata.getBreastfeeding());
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(TimeModifier.LAST);
    cd.setEncounterTypeList(Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()));
    cd.setValueList(Arrays.asList(commonMetadata.getYesConcept()));

    cd.addParameter(new Parameter("onOrAfter", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "US", Location.class));

    return cd;
  }

  /**
   * INICIO DE TARV POR SER LACTANTE São pacientes que iniciaram TARV por serem lactantes. Conceito
   * 6334
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsStartTARVBecouseOfBreatFeeding() {

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("infantPatientsEnrolledInTarvSample");
    cd.setQuestion(hivMetadata.getCriteriaForArtStart());
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(TimeModifier.FIRST);
    cd.setEncounterTypeList(Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()));
    cd.setValueList(Arrays.asList(commonMetadata.getBreastfeeding()));

    cd.addParameter(new Parameter("onOrAfter", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "US", Location.class));

    return cd;
  }

  /**
   * PROGRAMA: PACIENTES QUE DERAM PARTO HÁ DOIS ANOS ATRÁS DA DATA DE REFERENCIA - LACTANTES São
   * pacientes inscritos no programa de PTV e que foram actualizados como parto num periodo de 2
   * anos atrás da data de referencia
   */
  @DocumentedDefinition(value = "patientWithDeliveryDate2YearsAgoBreatFeeding")
  private CohortDefinition getPatientWithDeliveryDate2YearsAgoBreatFeeding() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWithDeliveryDate2YearsAgoBreatFeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWithDeliveryDate2YearsAgoBreatFeeding(
            hivMetadata.getPtvEtvProgram().getProgramId(),
            hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId()));

    return sqlCohortDefinition;
  }

  /**
   * LACTANTES OU PUERPUERAS (POS-PARTO) REGISTADAS: PROCESSO CLINICO E FICHA DE SEGUIMENTO São
   * pacientes puerpueras ou lactantes registadas. O registo pode ser na ficha de seguimento ou no
   * processo clinico durante a abertura de processo
   */
  private CohortDefinition getPatientInBreastFeedingEnrolledClinicProcess() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    // PACIENTES COM DATA DE PARTO ACTUALIZADO NO SERVICO TARV
    cd.addSearch(
        "DATAPARTO",
        EptsReportUtils.map(
            getPregantPatientWithDueDateUpdateInTARV(),
            "value1=${startDate},value2=${endDate},locationList=${location}"));

    //   INICIO DE TARV POR SER LACTANTE
    cd.addSearch(
        "INICIOLACTANTE",
        EptsReportUtils.map(
            getPatientsStartTARVBecouseOfBreatFeeding(),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));

    // PROGRAMA: PACIENTES QUE DERAM PARTO HÁ DOIS ANOS ATRÁS DA DATA DE REFERENCIA - LACTANTES
    cd.addSearch(
        "LACTANTEPROGRAMA",
        EptsReportUtils.map(
            getPatientWithDeliveryDate2YearsAgoBreatFeeding(),
            "startDate=${startDate},location=${location}"));
    // LACTANTES REGISTADAS
    cd.addSearch(
        "LACTANTE",
        EptsReportUtils.map(
            getInfantPatientsEnrolledInTarvSample(),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));

    // GRAVIDAS INSCRITAS NO SERVIÇO TARV
    cd.addSearch(
        "GRAVIDAS",
        EptsReportUtils.map(
            getPregnantPatientEnrolledInTARVService(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    // FEMININO
    cd.addSearch("FEMININO", EptsReportUtils.map(genderCohortQueries.femaleCohort(), ""));

    cd.setCompositionString(
        "((DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA  OR LACTANTE) NOT GRAVIDAS) AND FEMININO");
    return cd;
  }

  /**
   * GRAVIDAS INSCRITAS NO SERVICO TARV INCLUINDO PUERPUERAS
   *
   * <p>Sao gravidas inscritas no servico TARV num periodo incluindo puerpueras do mesmo periodo
   */
  private CohortDefinition getPregnantPatientsEnrolledInARTServiceIncludingPuerPueras() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // GRAVIDAS INSCRITAS NO SERVIÇO TARV
    cd.addSearch(
        "GRAVIDAS", EptsReportUtils.map(getPregnantPatientEnrolledInTARVService(), mappings));

    //  LACTANTES OU PUERPUERAS (POS-PARTO) REGISTADAS: PROCESSO CLINICO E FICHA DE SEGUIMENTO
    cd.addSearch(
        "PUERPUERA",
        EptsReportUtils.map(getPatientInBreastFeedingEnrolledClinicProcess(), mappings));

    cd.setCompositionString("GRAVIDAS OR PUERPUERA");
    return cd;
  }

  /**
   * ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS:
   * SEM INCLUIR GRÁVIDAS OU LACTANTES São pacientes actualmente em TARV sem incluir Gravidas ou
   * lactantes
   */
  private CohortDefinition getPatientsEnrolledInARTServiceNotIncludingNotNotifiedAbandonment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "startDate=${endDate-24m},endDate=${endDate},location=${location}";

    // PACIENTES ACTUALMENTE EM TARV E QUE ESTAO HA MAIS DE 6 MESES EM TRATAMENTO
    cd.addSearch(
        "ACTUALTARV",
        EptsReportUtils.map(
            getPatientInARTTreatmentWithMoreThan6Months(),
            "endDate=${endDate},location=${location}"));

    //  GRAVIDAS INSCRITAS NO SERVICO TARV INCLUINDO PUERPUERAS
    cd.addSearch(
        "GRAVIDASLACTANTE",
        EptsReportUtils.map(
            getPregnantPatientsEnrolledInARTServiceIncludingPuerPueras(), mappings));

    cd.setCompositionString("ACTUALTARV NOT GRAVIDASLACTANTE");
    return cd;
  }

  /**
   * PACIENTES COM PELO MENOS UMA CONSULTA CLINICA NUM DETERMINADO PERIODO São pacientes que têm
   * pelo menos uma consulta clínica (seguimento) num determinado periodo
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "patientWithAtLeastOneEncounterInPeriod")
  public CohortDefinition getPatientWithAtLeastOneEncounterInPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWithAtLeastOneEncounterInPeriod");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWithAtLeastOneEncounterInPeriod(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()));

    return sqlCohortDefinition;
  }

  /**
   * MQ_PACIENTES QUE INICIARAM TRATAMENTO DE TUBERCULOSE E NAO TERMINARAM ATE PERIODO FINAL
   *
   * <p>São pacientes que iniciaram tratamento de TB e não terminaram até o início do período de
   * revisão
   */
  @DocumentedDefinition(value = "patientsStartTuberculoseTreatmentNotComplete")
  private CohortDefinition getPatientsStartTuberculoseTreatmentNotComplete() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsStartTuberculoseTreatmentNotComplete");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsStartTuberculoseTreatmentNotComplete(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            tbMetadata.getTBLivroEncounterType().getEncounterTypeId(),
            tbMetadata.getTBRastreioEncounterType().getEncounterTypeId(),
            tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
            tbMetadata.gettbDgrusTreatmentEndDateConcept().getConceptId(),
            tbMetadata.getTBProgram().getProgramId()));

    return sqlCohortDefinition;
  }

  /**
   * NOTIFICACÃO DE SARCOMA DE KAPOSI São pacientes que foram notificados sarcoma de kaposi durante
   * o periodo de reportagem
   */
  @DocumentedDefinition(value = "patientsNotifiedSarcomaKaposi")
  public CohortDefinition getPatientsNotifiedSarcomaKaposi() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsNotifiedSarcomaKaposi");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsNotifiedSarcomaKaposi(
            commonMetadata.getStage4PediatricMozambiqueConcept().getConceptId(),
            commonMetadata.getAdultClinicalHistoryConcept().getConceptId(),
            commonMetadata.getSkimExamFindingsConcept().getConceptId(),
            commonMetadata.getExtremityExamFindingsConcept().getConceptId(),
            commonMetadata.getStateAdultMozambiqueConcept().getConceptId(),
            commonMetadata.getKaposiSarcomaConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * PACIENTES ACTUALMENTE EM TARV ELEGIVEIS PARA SEREM INSCRITOS EM ALGUM MODELO DIFERENCIADO
   *
   * <p>São pacientes actualmente em tarv (exclui grávidas e lactantes) elegíveis para serem
   * inscritos em algum modelo diferenciado: (1) activos em TARV com o mínino de 6 meses em TARV;
   * (2) que tenham no mínimo de uma consulta clínica no período de inclusão; (3) com resultado de
   * carga viral< 1000 cópias ou CD4 >200 cels/mm3 nos últimos 12 meses (contando da data do início
   * do período de revisão para trás); (4) sem condição clínica activa do estadio III ou IV
   * (Tuberculose ou Sarcoma de Kaposi)
   */
  public CohortDefinition getPatientInARTElegibleToBeEnrolledInSomeDiffModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS:
    // SEM INCLUIR GRÁVIDAS OU LACTANTES
    cd.addSearch(
        "ACTUALTARV",
        EptsReportUtils.map(
            getPatientsEnrolledInARTServiceNotIncludingNotNotifiedAbandonment(),
            "endDate=${startDate},location=${location}"));
    // PACIENTES QUE ESTAO HA MAIS DE 6 MESES EM TARV
    cd.addSearch(
        "HA6MESESEMTARV",
        EptsReportUtils.map(
            getPatientsInTARVMoreThan6Months(), "endDate=${startDate},location=${location}"));

    // PACIENTES COM CD4>200 OU CV<1000 NOS ULTIMOS 12 MESES
    cd.addSearch(
        "CVCD4",
        EptsReportUtils.map(
            getPatientsWithCD4GreterThan200ORCVLessThan1000InLast12Months(),
            "startDate=${startDate},location=${location}"));

    // PACIENTES COM PELO MENOS UMA CONSULTA CLINICA NUM DETERMINADO PERIODO
    cd.addSearch(
        "CONSULTA", EptsReportUtils.map(getPatientWithAtLeastOneEncounterInPeriod(), mappings));

    // NOTIFICACÃO DE SARCOMA DE KAPOSI
    cd.addSearch(
        "KAPOSI",
        EptsReportUtils.map(
            getPatientsNotifiedSarcomaKaposi(),
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    // MQ_PACIENTES QUE INICIARAM TRATAMENTO DE TUBERCULOSE E NAO TERMINARAM ATE PERIODO FINAL
    cd.addSearch(
        "TUBERCULOSE",
        EptsReportUtils.map(
            getPatientsStartTuberculoseTreatmentNotComplete(),
            "endDate=${startDate},location=${location}"));

    cd.setCompositionString(
        "(ACTUALTARV AND HA6MESESEMTARV AND CVCD4 AND CONSULTA) NOT (KAPOSI OR TUBERCULOSE)");
    return cd;
  }

  /**
   * PACIENTES QUE TIVERAM CONSULTA CLINICA NOS ULTIMOS 7 MESES E QUE FORAM MARCADOS PARA CONSULTA
   * SEGUINTE PARA 6 MESES São pacientes que visitaram a US nos últimos 7 meses que foram marcados
   * para consulta seguinte para 6 meses. Portanto a diferenca em dias entre a data da consulta e a
   * data marcada está entre 175 a 190 dias
   */
  @DocumentedDefinition(
      value = "patientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months")
  private CohortDefinition
      getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "patientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries
            .getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getReturnVisitDateConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /**
   * PACIENTES QUE TIVERAM LEVANTAMENTO DE ARVs NOS ULTIMOS 5 MESES E QUE FORAM MARCADOS PARA
   * PROXIMO LEVANTAMENTO PARA 3 MESES Pacientes que tiveram levantamento de ARVs nos ultimos 5
   * meses e que foram marcados para proximo levantamento entre 83-97 dias depois
   */
  @DocumentedDefinition(value = "patientsTookARVInLast5MonthAndWereMarkedToNextEncounter")
  private CohortDefinition getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsTookARVInLast5MonthAndWereMarkedToNextEncounter");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * PACIENTES ELEGIVEIS A MODELOS DIFERENCIADOS E QUE ESTÃO INSCRITOS EM ALGUM MODELO DIFERENCIADO
   * Sao pacientes elegíveis a serem inscritos em modelos diferenciados com agendamento de consultas
   * de 6 em 6 meses (Fluxo rápido) ou que levantaram frascos de ARVs para 3 meses (Dispensa
   * trimestral) ou inscritos em GAACs
   */
  public CohortDefinition getPatientsElegibleInDiffModelAndAreEnrolledInDiffModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    // PACIENTES ACTUALMENTE EM TARV ELEGIVEIS  PARA SEREM  INSCRITOS EM ALGUM MODELO DIFERENCIADO
    cd.addSearch(
        "ELEGIVEIS",
        EptsReportUtils.map(getPatientInARTElegibleToBeEnrolledInSomeDiffModel(), mappings));

    // PACIENTES INSCRITOS NO GAAC NUM PERIODO
    cd.addSearch(
        "INSCRITOSGAAC",
        EptsReportUtils.map(
            getPatientsEnrolledInGaacInAPeriod(),
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    // PACIENTES QUE TIVERAM LEVANTAMENTO DE ARVs NOS ULTIMOS 5 MESES E QUE FORAM MARCADOS PARA
    // PROXIMO LEVANTAMENTO PARA 3 MESES
    cd.addSearch(
        "LEVANTAMENTO3MESES",
        EptsReportUtils.map(
            getPatientsTookARVInLast5MonthAndWereMarkedToNextEncounter(),
            "endDate=${dataFinalAvaliacao},location=${location}"));

    // PACIENTES QUE TIVERAM CONSULTA CLINICA NOS ULTIMOS 7 MESES E QUE FORAM MARCADOS PARA CONSULTA
    // SEGUINTE PARA 6 MESES
    cd.addSearch(
        "CONSULTA6MESES",
        EptsReportUtils.map(
            getPatientsWhoHadEnconterInLast7MonthAndWereMarkedToNextEncounterIn6Months(),
            "endDate=${dataFinalAvaliacao},location=${location}"));

    cd.setCompositionString(
        "ELEGIVEIS AND (INSCRITOSGAAC OR LEVANTAMENTO3MESES OR CONSULTA6MESES)");
    return cd;
  }

  /**
   * MQ_PACIENTES QUE RECEBERAM RESULTADO DA CARGA VIRAL ENTRE O SEXTO E NONO MES DEPOIS DE INICIO
   * DE TARV São pacientes que iniciaram TARV nos primeiros 3 meses voltando 12 meses da data de
   * avaliação e que tiveram resultado de carga viral entre o sexto e nono mês depois da data de
   * inicio de TARV
   */
  @DocumentedDefinition(value = "patientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart")
  public CohortDefinition getPatientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries
            .getPatientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPlanConcept().getConceptId(),
                commonMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getARVStartDateConcept().getConceptId(),
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getHivViralLoadConcept().getConceptId()));

    return sqlCohortDefinition;
  }
  /** PACIENTES QUE ESTAO NA SEGUNDA LINHA DE ARV - PERIODO FINAL */
  private CohortDefinition getPatientInSecondARVLineFinalPeriod() {

    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("patientInSecondARVLineFinalPeriod");
    cd.setQuestion(hivMetadata.getRegimeConcept());
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(TimeModifier.LAST);
    cd.setEncounterTypeList(Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()));
    cd.setValueList(
        Arrays.asList(
            hivMetadata.getD4t3tcAbcEfvConcept(),
            hivMetadata.getD4t3tcAbcLpvConcept(),
            hivMetadata.getAzt3tcAbcEfvConcept(),
            hivMetadata.getAzt3tcAbcLpvConcept(),
            hivMetadata.getAzt3tcLpvConcept(),
            hivMetadata.getAbc3tcEfvConcept(),
            hivMetadata.getAbc3tcNvpConcept(),
            hivMetadata.getAbc3tcLpvConcept(),
            hivMetadata.getTdf3tcEfvConcept(),
            hivMetadata.getTdf3tcLpvConcept(),
            hivMetadata.getAztDdiLpvConcept()));

    cd.addParameter(new Parameter("onOrBefore", "Data Final", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }
  /**
   * PACIENTES ACTUALMENTE EM TARV E QUE ESTAO NA SEGUNDA LINHA DE ARV - PERIODO FINAL (ABANDONO
   * RETIRA NOTIFICADO E NAO NOTIFICADO)
   */
  public CohortDefinition
      getPatientInTARVInSecondLinheFinalPeriodoNotifiedAbandonmentAndNotNotified() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    // ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
    cd.addSearch(
        "ACTUALARV",
        EptsReportUtils.map(
            getPatientInARTTreatmentTillSpecificFinalPeriodWithoutAbandonmentNotNotified(),
            "endDate=${endDate},location=${location}"));

    // PACIENTES QUE ESTAO NA SEGUNDA LINHA DE ARV - PERIODO FINAL
    cd.addSearch(
        "SEGUNDALINHA",
        EptsReportUtils.map(
            getPatientInSecondARVLineFinalPeriod(),
            "onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("ACTUALARV AND SEGUNDALINHA");
    return cd;
  }

  /** FALHAS IMUNOLOGICAS - SQL */
  @DocumentedDefinition(value = "patientsWithImmunologicFailture")
  private CohortDefinition getPatientsWithImmunologicFailture() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientsWithImmunologicFailture");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientsWithImmunologicFailture(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getCD4AbsoluteConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId()));

    return sqlCohortDefinition;
  }

  /** FALHAS CLINICAS - SQL */
  @DocumentedDefinition(value = "patientWithClinicFailtures")
  private CohortDefinition getPatientWithClinicFailtures() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("patientWithClinicFailtures");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setQuery(
        QualiltyImprovementQueries.getPatientWithClinicFailtures(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getcurrentWhoHivStageConcept().getConceptId(),
            hivMetadata.getWho2AdultStageConcept().getConceptId(),
            hivMetadata.getWho3AdultStageConcept().getConceptId(),
            hivMetadata.getWho4AdultStageConcept().getConceptId()));

    return sqlCohortDefinition;
  }

  /** PACIENTES ACTUALMENTE EM TARV E NOTIFICADOS DE FALHAS CLINICAS OU IMUNOLOGICAS */
  public CohortDefinition getPatientInTARVWithClinicOrImmunogicsFailtureNotification() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    // FALHAS CLINICAS - SQL
    cd.addSearch(
        "CLINICAS",
        EptsReportUtils.map(
            getPatientWithClinicFailtures(), "endDate=${endDate},location=${location}"));

    // FALHAS IMUNOLOGICAS - SQL
    cd.addSearch(
        "IMUNOLOGICAS",
        EptsReportUtils.map(
            getPatientsWithImmunologicFailture(), "endDate=${endDate},location=${location}"));

    // ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
    cd.addSearch(
        "TARV",
        EptsReportUtils.map(
            getPatientInARTTreatmentTillSpecificFinalPeriodWithoutAbandonmentNotNotified(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("(CLINICAS OR IMUNOLOGICAS) AND TARV");
    return cd;
  }
}
