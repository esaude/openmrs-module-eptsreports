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
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  private String generalParameterMapping =
      "startDate=${startDate},endDate=${endDate},location=${location}";

  private String obsParameterMapping =
      "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";

  private String inProgramParameterMapping =
      "onOrAfter=${startDate},onOrBefore=${endDate},locations=${location}";

  private String mergeObsAndInProgramMappings(CohortDefinition cd) {
    return EptsReportUtils.mergeParameterMappings(obsParameterMapping, inProgramParameterMapping);
  }

  private String mergeAllMappings(CohortDefinition cd) {
    return EptsReportUtils.mergeParameterMappings(
        generalParameterMapping, obsParameterMapping, inProgramParameterMapping);
  }

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

  private void addObsParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("onOrAfter", "On Or After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
  }

  private void addInProgramParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("onOrAfter", "On Or After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before Date", Date.class));
    cd.addParameter(new Parameter("locations", "Location", Location.class));
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES
   *
   * @param parameterValues
   * @return
   */
  private CompositionCohortDefinition getNotifiedTBTreatmentPatientsOnART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES");

    InProgramCohortDefinition TBPROGRAMA =
        (InProgramCohortDefinition)
            genericCohortQueries.createInProgram("InTBProgram", tbMetadata.getTBProgram());
    TBPROGRAMA.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    CohortDefinition INICIOST =
        genericCohorts.hasCodedObs(
            tbMetadata.getTBTreatmentPlanConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                tbMetadata.getTBProcessoEncounterType(),
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(hivMetadata.getStartDrugsConcept()));

    DateObsCohortDefinition DATAINICIO =
        (DateObsCohortDefinition)
            genericCohorts.hasDateObs(
                tbMetadata.getStartDrugsConcept(),
                TimeModifier.ANY,
                Arrays.asList(
                    tbMetadata.getTBLivroEncounterType(),
                    tbMetadata.getTBProcessoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                RangeComparator.GREATER_EQUAL,
                RangeComparator.LESS_EQUAL);
    addGeneralParameters(TBPROGRAMA);
    cd.addSearch("TBPROGRAMA", map(TBPROGRAMA, inProgramParameterMapping));
    addGeneralParameters(INICIOST);
    cd.addSearch("INICIOST", map(INICIOST, obsParameterMapping));
    addGeneralParameters(DATAINICIO);
    cd.addSearch("DATAINICIO", map(DATAINICIO, obsParameterMapping));
    cd.setCompositionString("TBPROGRAMA OR INICIOST OR DATAINICIO");
    addObsParameters(cd);
    addInProgramParameters(cd);
    return cd;
  }

  /**
   * INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA
   * (SQL) existing codes: NOVOSINICIOS
   */
  private CompositionCohortDefinition
      getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");

    Program artProgram = hivMetadata.getARTProgram();
    CohortDefinition TRANSFDEPRG =
        genericCohortQueries.getPatientsBasedOnPatientStates(
            artProgram.getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId());
    SqlCohortDefinition INICIO =
        (SqlCohortDefinition)
            genericCohortQueries.generalSql(
                "INICIO",
                TXTBQueries.arvTreatmentIncludesTransfersFromWithKnownStartData(
                    artProgram.getConcept().getConceptId(),
                    hivMetadata.getStartDrugsConcept().getConceptId(),
                    hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(),
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

  /** ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO) */
  private CompositionCohortDefinition anyTimeARVTreatmentFinalPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO)");

    // PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) -
    // PERIODO FINAL
    InProgramCohortDefinition PROGRAMA =
        (InProgramCohortDefinition)
            genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
    PROGRAMA.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL
    CodedObsCohortDefinition CONCEITO1255 =
        (CodedObsCohortDefinition)
            genericCohorts.hasCodedObs(
                hivMetadata.getARVPlanConcept(),
                TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPharmaciaEncounterType()),
                Arrays.asList(
                    hivMetadata.getStartDrugsConcept(),
                    hivMetadata.getTransferFromOtherFacilityConcept()));

    // INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL
    DateObsCohortDefinition CONCEITODATA =
        (DateObsCohortDefinition)
            genericCohorts.hasDateObs(
                hivMetadata.getStartDrugsConcept(),
                TimeModifier.ANY,
                Arrays.asList(
                    hivMetadata.getARVPharmaciaEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                RangeComparator.LESS_EQUAL,
                null);
    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - FARMACIA
    CohortDefinition FRIDAFILA =
        genericCohortQueries.hasEncounter(
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()), TimeQualifier.ANY);
    addGeneralParameters(PROGRAMA);
    cd.addSearch("PROGRAMA", map(PROGRAMA, inProgramParameterMapping));
    addGeneralParameters(CONCEITO1255);
    cd.addSearch("CONCEITO1255", map(CONCEITO1255, obsParameterMapping));
    addGeneralParameters(CONCEITODATA);
    cd.addSearch("CONCEITODATA", map(CONCEITODATA, obsParameterMapping));
    addGeneralParameters(FRIDAFILA);
    cd.addSearch("FRIDAFILA", map(FRIDAFILA, generalParameterMapping));
    cd.setCompositionString("CONCEITO1255 OR PROGRAMA OR CONCEITODATA OR FRIDAFILA");
    addInProgramParameters(cd);
    addObsParameters(cd);
    return cd;
  }

  /**
   * ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL. Existing indicator codes: TARV
   *
   * @return
   */
  private CompositionCohortDefinition getCurrentlyInARTTreatmentCompositionFinalPeriod() {
    Program artProgram = hivMetadata.getARTProgram();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL");

    // [SAIDAPROGRAMA] PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE TRATAMENTO ARV: PERIODO FINAL
    CohortDefinition SAIDAPROGRAMA =
        genericCohortQueries.generalSql(
            "SAIDAPROGRAMA",
            TXTBQueries.patientsWhoCameOutOfARVTreatmentProgram(
                artProgram.getProgramId(),
                hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getId(),
                hivMetadata.getAbandonedWorkflowState().getId(),
                hivMetadata.getPatientHasDiedWorkflowState().getId()));
    CohortDefinition ALGUMAVEZTARV = anyTimeARVTreatmentFinalPeriod();
    addGeneralParameters(SAIDAPROGRAMA);
    cd.addSearch("SAIDAPROGRAMA", map(SAIDAPROGRAMA, generalParameterMapping));
    addGeneralParameters(ALGUMAVEZTARV);
    cd.addSearch("ALGUMAVEZTARV", map(ALGUMAVEZTARV, mergeObsAndInProgramMappings(ALGUMAVEZTARV)));
    cd.setCompositionString("ALGUMAVEZTARV NOT SAIDAPROGRAMA");
    addInProgramParameters(cd);
    addObsParameters(cd);
    return cd;
  }

  /**
   * ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
   *
   * @return
   */
  private CompositionCohortDefinition getPatientsInARTWithoutAbandonedNotification() {
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
    cd.addSearch("TARV", map(TARV, mergeAllMappings(TARV)));
    addGeneralParameters(NAONOTIFICADO);
    cd.addSearch("NAONOTIFICADO", map(NAONOTIFICADO, generalParameterMapping));
    cd.setCompositionString("TARV NOT NAONOTIFICADO");
    addInProgramParameters(cd);
    addObsParameters(cd);
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
    cd.addSearch("ACTIVOSTARV", map(ACTIVOSTARV, mergeAllMappings(ACTIVOSTARV)));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    addGeneralParameters(NOTIFICADOSTB);
    cd.addSearch("NOTIFICADOSTB", map(NOTIFICADOSTB, mergeObsAndInProgramMappings(NOTIFICADOSTB)));
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
    cd.addSearch("NOTIFICADOSTB", map(NOTIFICADOSTB, mergeObsAndInProgramMappings(NOTIFICADOSTB)));
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

    CohortDefinition ACTIVOSTARV = getPatientsInARTWithoutAbandonedNotification();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(ACTIVOSTARV);
    cd.addSearch("ACTIVOSTARV", map(ACTIVOSTARV, mergeAllMappings(ACTIVOSTARV)));
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
  public CohortDefinition patientsWhoScreenTbNegative() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES TARV COM RASTREIO DE TUBERCULOSE POSITIVO/NEGATIVO");

    CohortDefinition ACTUALTARV = getPatientsInARTWithoutAbandonedNotification();

    // PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO
    CohortDefinition RASTREIONEGATIVO =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getNoConcept()));

    // PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO
    CohortDefinition RASTREIOTBPOS =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));

    CohortDefinition INICIOTARV =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();

    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, mergeAllMappings(ACTUALTARV)));
    addGeneralParameters(RASTREIONEGATIVO);
    cd.addSearch("RASTREIONEGATIVO", map(RASTREIONEGATIVO, obsParameterMapping));
    addGeneralParameters(RASTREIOTBPOS);
    cd.addSearch("RASTREIOTBPOS", map(RASTREIOTBPOS, obsParameterMapping));
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

    CohortDefinition RASTREIOPOSITIVO =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));

    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();

    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, mergeAllMappings(ACTUALTARV)));
    addGeneralParameters(RASTREIOPOSITIVO);
    cd.addSearch("RASTREIOPOSITIVO", map(RASTREIOPOSITIVO, obsParameterMapping));
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

    CohortDefinition RASTREIONEGATIVO =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getNoConcept()));

    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(ACTUALTARV);
    cd.addSearch("ACTUALTARV", map(ACTUALTARV, mergeAllMappings(ACTUALTARV)));
    addGeneralParameters(RASTREIONEGATIVO);
    cd.addSearch("RASTREIONEGATIVO", map(RASTREIONEGATIVO, obsParameterMapping));
    addGeneralParameters(NOVOSINICIOS);
    cd.addSearch("NOVOSINICIOS", map(NOVOSINICIOS, generalParameterMapping));
    cd.setCompositionString("(ACTUALTARV AND RASTREIONEGATIVO) NOT NOVOSINICIOS");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * INICIO DE TARV E COM RASTREIO TB POSITIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE
   * INICIO CONHECIDA
   */
  public CohortDefinition patientsWithPositiveTBTrialNotTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TARV E COM RASTREIO TB POSITIVO - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA");

    CohortDefinition RASTREIOTBPOS =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));

    CohortDefinition INICIOTARV =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(RASTREIOTBPOS);
    cd.addSearch("RASTREIOTBPOS", map(RASTREIOTBPOS, obsParameterMapping));
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

    CohortDefinition RASTREIOTBNEG =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getNoConcept()));
    CohortDefinition INICIOTARVTB =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    addGeneralParameters(RASTREIOTBNEG);
    cd.addSearch("RASTREIOTBNEG", map(RASTREIOTBNEG, obsParameterMapping));
    addGeneralParameters(INICIOTARVTB);
    cd.addSearch("INICIOTARVTB", map(INICIOTARVTB, generalParameterMapping));
    cd.setCompositionString("INICIOTARVTB AND RASTREIOTBNEG");
    addGeneralParameters(cd);
    return cd;
  }
}
