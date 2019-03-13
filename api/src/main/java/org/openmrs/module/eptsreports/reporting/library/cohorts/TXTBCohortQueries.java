package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries.AbandonedWithoutNotificationParams;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
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
   * @param parameterValues
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
    CohortDefinition DATAINICIO = tbTreatmentStartUsingEndDate();
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
  public CohortDefinition getPatientsTransferredFromARTTreatment() {
    return genericCohortQueries.generalSql(
        "TRANSFDEPRG",
        TXTBQueries.patientsTransferredFromARTTreatment(
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
    CohortDefinition TRANSFDEPRG = getPatientsTransferredFromARTTreatment();
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

  /** ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL */
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
  public CohortDefinition tbTreatmentStartUsingEndDate() {
    return genericCohortQueries.generalSql(
        "startARTTarget",
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
        TXTBQueries.patientsWhoCameOutOfARVTreatmentProgram(
            artProgram.getProgramId(),
            hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getId(),
            hivMetadata.getAbandonedWorkflowState().getId(),
            hivMetadata.getPatientHasDiedWorkflowState().getId()));
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

  // -- TODO upwards retain as private

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

    // PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO
    CohortDefinition RASTREIONEGATIVO = codedNegativeTbScreening();

    // PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO
    CohortDefinition RASTREIOTBPOS = codedPositiveTbScreening();

    CohortDefinition INICIOTARV =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();

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

    CohortDefinition RASTREIOPOSITIVO = codedPositiveTbScreening();
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

    CohortDefinition RASTREIONEGATIVO = codedNegativeTbScreening();
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
  public CohortDefinition codedNegativeTbScreening() {
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
  public CohortDefinition codedPositiveTbScreening() {
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

    CohortDefinition RASTREIOTBPOS = codedPositiveTbScreening();

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

    CohortDefinition RASTREIOTBNEG = codedNegativeTbScreening();
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

  /**
   * NUMERO DE PACIENTES TARV COM RASTREIO DE TUBERCULOSE POSITIVO/NEGATIVO NUM DETERMINADO PERIODO
   */
  public CohortIndicator dd() {
    CohortIndicator ci = new CohortIndicator();
    // 342
    return ci;
  }
}
