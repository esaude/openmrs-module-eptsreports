package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.CalculationWithResultFinder;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries.AbandonedWithoutNotificationParams;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  private String generalParameterMapping =
      "startDate=${startDate},endDate=${endDate},location=${location}";
  private String codedObsParameterMapping =
      "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";

  private Mapped<CohortDefinition> map(CohortDefinition cd, String parameterMappings) {
    return EptsReportUtils.map(
        cd,
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, parameterMappings));
  }

  private void addGeneralParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES
   *
   * @return
   */
  public CohortDefinition getNotifiedTBTreatmentPatientsOnART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES");

    CohortDefinition TBPROGRAMA = getInTBProgram();
    CohortDefinition INICIOST =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTBTreatmentPlanConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                tbMetadata.getTBProcessoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getStartDrugsConcept()));
    CohortDefinition DATAINICIO = tbTreatmentWithinReportingDate();
    addGeneralParameters(TBPROGRAMA);
    cd.addSearch("TBPROGRAMA", map(TBPROGRAMA, generalParameterMapping));
    addGeneralParameters(INICIOST);
    cd.addSearch("INICIOST", map(INICIOST, codedObsParameterMapping));
    addGeneralParameters(DATAINICIO);
    cd.addSearch("DATAINICIO", map(DATAINICIO, generalParameterMapping));
    cd.setCompositionString("TBPROGRAMA OR INICIOST OR DATAINICIO");
    return cd;
  }

  /**
   * PROGRAMA: PACIENTES TRANSFERIDOS DE NO PROGRAMA DE TRATAMENTO ARV: NUM PERIODO. codes include:
   * TRANSFDEPRG
   */
  public CohortDefinition getPatientsTransferredIntoARTTreatment() {
    return genericCohortQueries.generalSql(
        "TRANSFDEPRG",
        TXTBQueries.patientsTransferredFromOrIntoProgram(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
  }

  /**
   * INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA
   * (SQL) existing codes: NOVOSINICIOS
   */
  public CohortDefinition getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");

    Program artProgram = hivMetadata.getARTProgram();
    CohortDefinition TRANSFDEPRG = getPatientsTransferredIntoARTTreatment();
    CohortDefinition INICIO =
        genericCohortQueries.generalSql(
            "INICIO",
            TXTBQueries.arvTreatmentIncludesTransfersFromWithKnownStartData(
                artProgram.getConcept().getConceptId(),
                hivMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
                artProgram.getProgramId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()));
    addGeneralParameters(TRANSFDEPRG);
    cd.addSearch("TRANSFDEPRG", map(TRANSFDEPRG, generalParameterMapping));
    addGeneralParameters(INICIO);
    cd.addSearch("INICIO", map(INICIO, generalParameterMapping));
    cd.setCompositionString("INICIO NOT TRANSFDEPRG");
    return cd;
  }

  /** ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - FARMACIA */
  public CohortDefinition arTTreatmentFromPharmacy() {
    return genericCohortQueries.generalSql(
        "FRIDAFILA",
        TXTBQueries.encounterObs(hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
  }

  /** ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO) */
  public CohortDefinition anyTimeARVTreatmentFinalPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO)");

    CohortDefinition PROGRAMA = getInARTProgram();
    CohortDefinition CONCEITO1255 = everyTimeARVTreatedFinal();

    CohortDefinition CONCEITODATA = artTargetHistoricalStartUsingEndDate();
    CohortDefinition FRIDAFILA = arTTreatmentFromPharmacy();
    addGeneralParameters(PROGRAMA);
    cd.addSearch("PROGRAMA", map(PROGRAMA, generalParameterMapping));
    addGeneralParameters(CONCEITO1255);
    cd.addSearch("CONCEITO1255", map(CONCEITO1255, codedObsParameterMapping));
    addGeneralParameters(CONCEITODATA);
    cd.addSearch("CONCEITODATA", map(CONCEITODATA, generalParameterMapping));
    addGeneralParameters(FRIDAFILA);
    cd.addSearch("FRIDAFILA", map(FRIDAFILA, generalParameterMapping));
    cd.setCompositionString("CONCEITO1255 OR PROGRAMA OR CONCEITODATA OR FRIDAFILA");
    return cd;
  }

  /**
   * ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL 2.a.ii: All patients who have initiated the
   * drugs (ARV PLAN = START DRUGS) at the pharmacy or clinical visits that occurred during the
   * reporting period;
   */
  public CohortDefinition everyTimeARVTreatedFinal() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getARVPlanConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType(),
            hivMetadata.getARVPharmaciaEncounterType()),
        Arrays.asList(
            hivMetadata.getStartDrugsConcept(), hivMetadata.getTransferFromOtherFacilityConcept()));
  }

  /**
   * INICIO DE TRATAMENTO DE TUBERCULOSE DATA NOTIFICADA NAS FICHAS DE: SEGUIMENTO, RASTREIO E LIVRO
   * TB. codes: DATAINICIO
   */
  public CohortDefinition tbTreatmentWithinReportingDate() {
    return genericCohortQueries.generalSql(
        "startedTbTreatment",
        TXTBQueries.dateObs(
            tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
            Arrays.asList(
                tbMetadata.getTBLivroEncounterType().getId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getId(),
                tbMetadata.getTBRastreioEncounterType().getId(),
                tbMetadata.getTBProcessoEncounterType().getId()),
            true));
  }

  /** INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL */
  public CohortDefinition artTargetHistoricalStartUsingEndDate() {
    return genericCohortQueries.generalSql(
        "startARTHistoricalTarget",
        TXTBQueries.dateObs(
            tbMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()),
            false));
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) - PERIODO FINAL */
  public CohortDefinition getInARTProgram() {
    return genericCohortQueries.generalSql(
        "SAIDAPROGRAMA",
        TXTBQueries.inARTProgramToEndDateAtLocation(hivMetadata.getARTProgram().getProgramId()));
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO */
  public CohortDefinition getInTBProgram() {
    return genericCohortQueries.generalSql(
        "TBPROGRAMA",
        TXTBQueries.inTBProgramToEndDateAtLocation(tbMetadata.getTBProgram().getProgramId()));
  }

  /**
   * ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL. Existing indicator codes: TARV
   *
   * @return
   */
  public CohortDefinition getCurrentlyInARTTreatmentCompositionFinalPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL");

    CohortDefinition SAIDAPROGRAMA = getPatientsWhoCameOutOfARVTreatmentProgram();
    CohortDefinition ALGUMAVEZTARV = anyTimeARVTreatmentFinalPeriod();
    addGeneralParameters(SAIDAPROGRAMA);
    cd.addSearch("SAIDAPROGRAMA", map(SAIDAPROGRAMA, generalParameterMapping));
    addGeneralParameters(ALGUMAVEZTARV);
    cd.addSearch("ALGUMAVEZTARV", map(ALGUMAVEZTARV, generalParameterMapping));
    cd.setCompositionString("ALGUMAVEZTARV NOT SAIDAPROGRAMA");
    return cd;
  }

  /**
   * PACIENTES QUE SAIRAM DO PROGRAMA DE TRATAMENTO ARV: PERIODO FINAL
   *
   * @return
   */
  public CohortDefinition getPatientsWhoCameOutOfARVTreatmentProgram() {
    Program artProgram = hivMetadata.getARTProgram();
    return genericCohortQueries.generalSql(
        "SAIDAPROGRAMA",
        TXTBQueries.patientsAtProgramStates(
            artProgram.getProgramId(),
            Arrays.asList(
                hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getId(),
                hivMetadata.getAbandonedWorkflowState().getId(),
                hivMetadata.getPatientHasDiedWorkflowState().getId())));
  }

  /**
   * ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
   *
   * @return
   */
  public CohortDefinition getPatientsInARTWithoutAbandonedNotification() {
    Program artProgram = hivMetadata.getARTProgram();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS");
    CohortDefinition TARV = getCurrentlyInARTTreatmentCompositionFinalPeriod();
    // ABANDONO NÃO NOTIFICADO - TARV
    CohortDefinition NAONOTIFICADO =
        genericCohortQueries.generalSql(
            "NAONOTIFICADO",
            TXTBQueries.abandonedWithNoNotification(
                new AbandonedWithoutNotificationParams()
                    .programId(artProgram.getProgramId())
                    .returnVisitDateConceptId(
                        hivMetadata.getReturnVisitDateConcept().getConceptId())
                    .returnVisitDateForARVDrugConceptId(
                        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId())
                    .pharmacyEncounterTypeId(
                        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())
                    .artAdultFollowupEncounterTypeId(
                        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId())
                    .artPedInicioEncounterTypeId(
                        hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId())
                    .transferOutStateId(
                        hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId())
                    .treatmentSuspensionStateId(
                        hivMetadata.getSuspendedTreatmentWorkflowState().getId())
                    .treatmentAbandonedStateId(hivMetadata.getAbandonedWorkflowState().getId())
                    .deathStateId(hivMetadata.getPatientHasDiedWorkflowState().getId())));
    addGeneralParameters(TARV);
    cd.addSearch("TARV", map(TARV, generalParameterMapping));
    addGeneralParameters(NAONOTIFICADO);
    cd.addSearch("NAONOTIFICADO", map(NAONOTIFICADO, generalParameterMapping));
    cd.setCompositionString("TARV NOT NAONOTIFICADO");
    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - ACTIVOS EM TARV. Existing indicator
   * codes: ACTIVOSTARV, TARV, NOTIFICADOSTB
   *
   * @return
   */
  public CohortDefinition getNotifiedTBPatientsAtARVService() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - ACTIVOS EM TARV");

    CohortDefinition ACTIVOSTARV = getPatientsInARTWithoutAbandonedNotification();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    CohortDefinition NOTIFICADOSTB = getNotifiedTBTreatmentPatientsOnART();
    addGeneralParameters(ACTIVOSTARV);
    cd.addSearch("ACTIVOSTARV", map(ACTIVOSTARV, generalParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    addGeneralParameters(NOTIFICADOSTB);
    cd.addSearch("NOTIFICADOSTB", map(NOTIFICADOSTB, generalParameterMapping));
    cd.setCompositionString("(ACTIVOSTARV AND NOTIFICADOSTB) NOT NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - NOVOS INICIOS. Existing indicator
   * code: NOVOSINICIOS, T0310IM
   *
   * @return
   */
  public CohortDefinition notifiedTbPatientsOnARVNewStarting() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - NOVOS INICIOS");

    CohortDefinition NOTIFICADOSTB = getNotifiedTBTreatmentPatientsOnART();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(NOTIFICADOSTB);
    cd.addSearch("NOTIFICADOSTB", map(NOTIFICADOSTB, generalParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    cd.setCompositionString("NOTIFICADOSTB AND NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * TX_TB Numerator
   *
   * <p>PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV. Existing indicator codes: T.TXB,
   * T0310CALL
   */
  public CohortDefinition notifiedTbPatientsOnARTService() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV");

    CohortDefinition ACTIVOSTARV = getNotifiedTBPatientsAtARVService();
    CohortDefinition NOVOSINICIOS = notifiedTbPatientsOnARVNewStarting();
    addGeneralParameters(ACTIVOSTARV);
    cd.addSearch("ACTIVOSTARV", map(ACTIVOSTARV, generalParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    cd.setCompositionString("ACTIVOSTARV OR NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /** TX_TB base cohort. PACIENTES INSCRITOS NO SERVICO TARV - PERIODO FINAL (SQL); */
  public CohortDefinition getPatientsEnrolledInARTCareAndOnTreatment() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "",
            TXTBQueries.patientsEnrolledInARTCareAndOnTreatment(
                hivMetadata.getHIVCareProgram().getProgramId(),
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                28 /*28 is from query, refers to?*/,
                29 /*29 is from query, referes to?*/));
    addGeneralParameters(cd);
    return cd;
  }

  /** TX_TB Denominator PACIENTES TARV COM RASTREIO DE TUBERCULOSE POSITIVO/NEGATIVO */
  public CohortDefinition patientsWhoScreenTbNegativeOrPositive() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES TARV COM RASTREIO DE TUBERCULOSE POSITIVO/NEGATIVO");

    CohortDefinition ACTUALTARV = getPatientsInARTWithoutAbandonedNotification();

    CohortDefinition INICIOTARV =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();

    // PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO
    CohortDefinition RASTREIONEGATIVO = codedNoTbScreening();

    // PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO
    CohortDefinition RASTREIOTBPOS = codedYesTbScreening();

    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, generalParameterMapping));
    addGeneralParameters(RASTREIONEGATIVO);
    cd.addSearch("RASTREIONEGATIVO", map(RASTREIONEGATIVO, codedObsParameterMapping));
    addGeneralParameters(RASTREIOTBPOS);
    cd.addSearch("RASTREIOTBPOS", map(RASTREIOTBPOS, codedObsParameterMapping));
    addGeneralParameters(INICIOTARV);
    cd.addSearch("INICIOTARV", map(INICIOTARV, generalParameterMapping));
    cd.setCompositionString("(INICIOTARV OR ACTUALTARV) AND (RASTREIONEGATIVO OR RASTREIOTBPOS)");
    addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES ACTUALMENTE EM TARV COM RASTREIO DE TUBERCULOSE POSITIVO NUM DETERMINADO PERIODO */
  public CohortDefinition patientsOnARTWhoScreenedTBPositiveForAPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "PACIENTES ACTUALMENTE EM TARV COM RASTREIO DE TUBERCULOSE POSITIVO NUM DETERMINADO PERIODO");

    CohortDefinition ACTUALTARV = getPatientsInARTWithoutAbandonedNotification();

    CohortDefinition RASTREIOPOSITIVO = codedYesTbScreening();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();

    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, generalParameterMapping));
    addGeneralParameters(RASTREIOPOSITIVO);
    cd.addSearch("RASTREIOPOSITIVO", map(RASTREIOPOSITIVO, codedObsParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    cd.setCompositionString("(ACTUALTARV AND RASTREIOPOSITIVO) NOT NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * NUMERO DE PACIENTES ACTUALMENTE EM TARV COM RASTREIO DE TUBERCULOSE NEGATIVO NUM DETERMINADO
   * PERIODO
   */
  public CohortDefinition patientsOnARTWhoScreenedTBNegativeForAPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "NUMERO DE PACIENTES ACTUALMENTE EM TARV COM RASTREIO DE TUBERCULOSE NEGATIVO NUM DETERMINADO PERIODO");

    CohortDefinition ACTUALTARV = getPatientsInARTWithoutAbandonedNotification();

    CohortDefinition RASTREIONEGATIVO = codedNoTbScreening();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, generalParameterMapping));
    addGeneralParameters(RASTREIONEGATIVO);
    cd.addSearch("RASTREIONEGATIVO", map(RASTREIONEGATIVO, codedObsParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    cd.setCompositionString("(ACTUALTARV AND RASTREIONEGATIVO) NOT NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO codes: RASTREIOTBNEG */
  public CohortDefinition codedNoTbScreening() {
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getTbScreeningConcept(),
        TimeModifier.LAST,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        Arrays.asList(commonMetadata.getNoConcept()));
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO codes: RASTREIOTBPOS */
  public CohortDefinition codedYesTbScreening() {
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getTbScreeningConcept(),
        TimeModifier.LAST,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        Arrays.asList(commonMetadata.getYesConcept()));
  }

  /**
   * INICIO DE TARV E COM RASTREIO TB POSITIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA
   */
  public CohortDefinition patientsWithPositiveTBTrialNotTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TARV E COM RASTREIO TB POSITIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA");

    CohortDefinition RASTREIOTBPOS = codedYesTbScreening();

    CohortDefinition INICIOTARV =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(RASTREIOTBPOS);
    cd.addSearch(
        "RASTREIOTBPOS",
        map(
            RASTREIOTBPOS,
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));
    addGeneralParameters(INICIOTARV);
    cd.addSearch("INICIOTARV", map(INICIOTARV, generalParameterMapping));
    cd.setCompositionString("RASTREIOTBPOS AND INICIOTARV");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * INICIO DE TARV E COM RASTREIO TB NEGATIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA
   */
  public CohortDefinition patientsWithNegativeTBTrialNotTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TARV E COM RASTREIO TB NEGATIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA");

    CohortDefinition RASTREIOTBNEG = codedNoTbScreening();
    CohortDefinition INICIOTARVTB =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(RASTREIOTBNEG);
    cd.addSearch("RASTREIOTBNEG", map(RASTREIOTBNEG, codedObsParameterMapping));
    addGeneralParameters(INICIOTARVTB);
    cd.addSearch("INICIOTARVTB", map(INICIOTARVTB, generalParameterMapping));
    cd.setCompositionString("INICIOTARVTB AND RASTREIOTBNEG");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition patientWithFirstDrugPickupEncounterWithinReportingDate() {
    return genericCohortQueries.generalSql(
        "patientWithFirstDrugPickupEncounter",
        TXTBQueries.patientWithFirstDrugPickupEncounter(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
  }

  /** patients who were transferred in from another facility and do not have ART Initiation Date */
  public CohortDefinition patientsTranferredInWithoutARTInitiationDate() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition TRANSFDEPRG = getPatientsTransferredIntoARTTreatment();
    addGeneralParameters(TRANSFDEPRG);
    CalculationCohortDefinition NOARTINIT =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    NOARTINIT.setWithResultFinder(CalculationWithResultFinder.NULL);
    addGeneralParameters(NOARTINIT);

    cd.addSearch("TRANSFDEPRG", map(TRANSFDEPRG, generalParameterMapping));
    cd.addSearch("NOARTINIT", map(NOARTINIT, generalParameterMapping));
    cd.setCompositionString("TRANSFDEPRG AND NOARTINIT");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * patients who were transferred in from another facility and do have ART Initiation Date not
   * within the reporting period.
   */
  public CohortDefinition patientsTranferredInWithARTInitiationDateOutsideReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition TRANSFDEPRG = getPatientsTransferredIntoARTTreatment();
    addGeneralParameters(TRANSFDEPRG);
    CalculationCohortDefinition OUTARTINIT =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    OUTARTINIT.setWithResultFinder(CalculationWithResultFinder.DATE_OUTSIDE);
    addGeneralParameters(OUTARTINIT);

    cd.addSearch("TRANSFDEPRG", map(TRANSFDEPRG, generalParameterMapping));
    cd.addSearch("OUTARTINIT", map(OUTARTINIT, generalParameterMapping));
    cd.setCompositionString("TRANSFDEPRG AND OUTARTINIT");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artListA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = patientWithFirstDrugPickupEncounterWithinReportingDate();
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = everyTimeARVTreatedFinal();
    addGeneralParameters(ii);
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition iii = artTargetHistoricalStartUsingEndDate();
    addGeneralParameters(iii);
    cd.addSearch("iii", map(iii, generalParameterMapping));
    CohortDefinition iv = getInARTProgram();
    addGeneralParameters(iv);
    cd.addSearch("iv", map(iv, generalParameterMapping));
    CohortDefinition v1 = patientsTranferredInWithoutARTInitiationDate();
    addGeneralParameters(v1);
    cd.addSearch("v1", map(v1, generalParameterMapping));
    CohortDefinition v2 = patientsTranferredInWithARTInitiationDateOutsideReportingPeriod();
    addGeneralParameters(v2);
    cd.addSearch("v2", map(v2, generalParameterMapping));
    cd.setCompositionString("(i OR ii OR iii OR iv) OR NOT (v1 OR v2)");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artListB() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = everyTimeARVTreatedFinal();
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = artTargetHistoricalStartUsingEndDate();
    addGeneralParameters(ii);
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition iii = getInARTProgram();
    addGeneralParameters(iii);
    cd.addSearch("iii", map(iii, generalParameterMapping));

    cd.setCompositionString("i OR ii OR iii");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artList() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition a = artListA();
    addGeneralParameters(a);
    cd.addSearch("A", map(a, generalParameterMapping));
    CohortDefinition b = artListB();
    addGeneralParameters(b);
    cd.addSearch("B", map(b, generalParameterMapping));
    cd.setCompositionString("A OR B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveInvesitionResult() {
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getResearchResultConcept(),
        TimeModifier.LAST,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        Arrays.asList(tbMetadata.getPositiveConcept()));
  }

  public CohortDefinition negativeInvesitionResult() {
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getResearchResultConcept(),
        TimeModifier.LAST,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        Arrays.asList(tbMetadata.getNegativeConcept()));
  }

  /**
   * at least one “POS” or “NEG” selected for “Resultado da Investigação para TB de BK e/ou RX?”
   * during the reporting period consultations; ( response 703: POS or 664: NEG for question: 6277)
   */
  public CohortDefinition positiveOrNegativeInvesitionResult() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition P = positiveInvesitionResult();
    addGeneralParameters(P);
    cd.addSearch("P", map(P, codedObsParameterMapping));
    CohortDefinition N = negativeInvesitionResult();
    addGeneralParameters(N);
    cd.addSearch("N", map(N, codedObsParameterMapping));
    cd.setCompositionString("P OR N");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “S” or “N” selected for TB Screening (Rastreio de TB) during the reporting period
   * consultations; (response 1065: YES or 1066: NO for question 6257: SCREENING FOR TB)
   */
  public CohortDefinition yesOrNoInvesitionResult() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition S = codedYesTbScreening();
    addGeneralParameters(S);
    cd.addSearch("S", map(S, codedObsParameterMapping));
    CohortDefinition N = codedNoTbScreening();
    addGeneralParameters(N);
    cd.addSearch("N", map(N, codedObsParameterMapping));
    cd.setCompositionString("S OR N");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbDenominatorA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = yesOrNoInvesitionResult();
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = positiveOrNegativeInvesitionResult();
    addGeneralParameters(ii);
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition iii = tbTreatmentWithinReportingDate();
    addGeneralParameters(iii);
    cd.addSearch("iii", map(iii, generalParameterMapping));
    CohortDefinition iv = getInTBProgram();
    addGeneralParameters(iv);
    cd.addSearch("iv", map(iv, generalParameterMapping));
    CohortDefinition artList = artList();
    addGeneralParameters(artList);
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii OR iii OR iv) INTERSECTION artList");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * all patients who were transferred out by reporting end date except patients who started TB
   * treatment during the reporting period
   */
  public CohortDefinition transferredOutExceptStartedTbTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition transferredOut =
        genericCohortQueries.generalSql(
            "outOfTb",
            TXTBQueries.patientsAtProgramStates(
                tbMetadata.getTBProgram().getProgramId(),
                Arrays.asList(
                    hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId())));
    addGeneralParameters(transferredOut);
    cd.addSearch("transferredOut", map(transferredOut, generalParameterMapping));
    CohortDefinition startedTbTreatment = tbTreatmentWithinReportingDate();
    addGeneralParameters(startedTbTreatment);
    cd.addSearch("startedTbTreatment", map(startedTbTreatment, generalParameterMapping));

    cd.setCompositionString("transferredOut NOT startedTbTreatment");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * all patients who started TB Treatment at any point in the previous 6 months before to the start
   * date of the reporting period
   */
  public CohortDefinition startedTbTreatmentWith6MonthsBeforeStartDate() {
    return genericCohortQueries.generalSql(
        "started6MonthsBeforeStartDate",
        TXTBQueries.dateObsWithinXMonthsBeforeStartDate(
            tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
            Arrays.asList(
                tbMetadata.getTBLivroEncounterType().getId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getId(),
                tbMetadata.getTBRastreioEncounterType().getId(),
                tbMetadata.getTBProcessoEncounterType().getId()),
            6));
  }

  public CohortDefinition txTbDenominatorB() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = transferredOutExceptStartedTbTreatment();
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = startedTbTreatmentWith6MonthsBeforeStartDate();
    addGeneralParameters(ii);
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition artList = artList();
    addGeneralParameters(artList);
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString("(i AND ii) INTERSECTION artList");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = txTbDenominatorA();
    addGeneralParameters(A);
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = txTbDenominatorB();
    addGeneralParameters(B);
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("A OR B");
    addGeneralParameters(cd);
    return cd;
  }
}
