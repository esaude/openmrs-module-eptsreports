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
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
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

  @Autowired private HivCohortQueries hivCohortQueries;

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
   * @return CompositionCohortDefinition
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
    CohortDefinition DATAINICIO = tbTreatmentStartDateWithinReportingDate();
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
   * INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA
   * (SQL) existing codes: NOVOSINICIOS
   */
  public CohortDefinition getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");

    Program artProgram = hivMetadata.getARTProgram();
    CohortDefinition TRANSFDEPRG = hivCohortQueries.getPatientsTransferredFromOtherHealthFacility();
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
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition inARTProgramAtEndDate =
        genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
    CohortDefinition CONCEITO1255 = everyTimeARVTreatedFinal();

    CohortDefinition CONCEITODATA =
        hivCohortQueries.getPatientWithHistoricalDrugStartDateObsBeforeOrOnEndDate();
    CohortDefinition FRIDAFILA = arTTreatmentFromPharmacy();

    cd.addSearch(
        "inARTProgramAtEndDate",
        EptsReportUtils.map(
            inARTProgramAtEndDate, "onOrBefore=${onOrBefore},locations=${location}"));
    addGeneralParameters(CONCEITO1255);
    cd.addSearch("CONCEITO1255", map(CONCEITO1255, codedObsParameterMapping));
    addGeneralParameters(CONCEITODATA);
    cd.addSearch(
        "CONCEITODATA",
        EptsReportUtils.map(CONCEITODATA, "onOrBefore=${onOrBefore},location=${location}"));
    addGeneralParameters(FRIDAFILA);
    cd.addSearch("FRIDAFILA", map(FRIDAFILA, generalParameterMapping));
    cd.setCompositionString("CONCEITO1255 OR inARTProgramAtEndDate OR CONCEITODATA OR FRIDAFILA");
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
  public CohortDefinition tbTreatmentStartDateWithinReportingDate() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
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
    addGeneralParameters(cd);
    return cd;
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO */
  public CohortDefinition getInTBProgram() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TBPROGRAMA",
            TXTBQueries.inTBProgramWithinReportingPeriodAtLocation(
                tbMetadata.getTBProgram().getProgramId()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * ACTUALMENTE EM TRATAMENTO ARV (COMPOSICAO) - PERIODO FINAL. Existing indicator codes: TARV
   *
   * @return CompositionCohortDefinition
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
   * @return SqlCohortDefinition
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
   * @return CompositionCohortDefinition
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
   * @return CompositionCohortDefinition
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
   * @return CompositionCohortDefinition
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
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getNoConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO codes: RASTREIOTBPOS */
  public CohortDefinition codedYesTbScreening() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition tbScreening() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            null);
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
        TXTBQueries.patientWithFirstDrugPickupEncounterInReportingPeriod(
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
  }

  /** patients who were transferred in from another facility and do not have ART Initiation Date */
  public CohortDefinition patientsTranferredInWithoutARTInitiationDate() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    addGeneralParameters(cd);

    CohortDefinition TRANSFDEPRG = hivCohortQueries.getPatientsTransferredFromOtherHealthFacility();
    addGeneralParameters(TRANSFDEPRG);
    CalculationCohortDefinition NOARTINIT =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    NOARTINIT.setWithResultFinder(CalculationWithResultFinder.NULL);
    addGeneralParameters(NOARTINIT);

    cd.addSearch(
        "TRANSFDEPRG",
        map(TRANSFDEPRG, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("NOARTINIT", map(NOARTINIT, generalParameterMapping));
    cd.setCompositionString("TRANSFDEPRG AND NOARTINIT");
    return cd;
  }

  /**
   * patients who were transferred in from another facility and do have ART Initiation Date not
   * within the reporting period.
   */
  public CohortDefinition patientsTranferredInWithARTInitiationDateOutsideReportingPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition TRANSFDEPRG = hivCohortQueries.getPatientsTransferredFromOtherHealthFacility();
    addGeneralParameters(TRANSFDEPRG);
    CalculationCohortDefinition OUTARTINIT =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    OUTARTINIT.setWithResultFinder(CalculationWithResultFinder.DATE_OUTSIDE);
    addGeneralParameters(OUTARTINIT);

    cd.addSearch(
        "TRANSFDEPRG",
        map(TRANSFDEPRG, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("OUTARTINIT", map(OUTARTINIT, generalParameterMapping));
    cd.setCompositionString("TRANSFDEPRG AND OUTARTINIT");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artList() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addSearch(
        "started-by-end-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("started-by-end-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveInvesitionResult() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getResearchResultConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(tbMetadata.getPositiveConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition negativeInvesitionResult() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getResearchResultConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(tbMetadata.getNegativeConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “POS” or “NEG” selected for “Resultado da Investigação para TB de BK e/ou RX?”
   * during the reporting period consultations; ( response 703: POS or 664: NEG for question: 6277)
   */
  public CohortDefinition positiveOrNegativeInvesitionResult() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition P = positiveInvesitionResult();
    cd.addSearch("P", map(P, codedObsParameterMapping));
    CohortDefinition N = negativeInvesitionResult();
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
    cd.addSearch("S", map(S, codedObsParameterMapping));
    CohortDefinition N = codedNoTbScreening();
    cd.addSearch("N", map(N, codedObsParameterMapping));
    cd.setCompositionString("S OR N");
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
    CohortDefinition startedTbTreatment = tbTreatmentStartDateWithinReportingDate();
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
    CohortDefinition cd =
        genericCohortQueries.generalSql(
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
    addGeneralParameters(cd);
    return cd;
  }

  // Filter Art_list to Tb_list
  public CohortDefinition txTbDenominatorA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = yesOrNoInvesitionResult();
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = positiveOrNegativeInvesitionResult();
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition iii = tbTreatmentStartDateWithinReportingDate();
    cd.addSearch("iii", map(iii, generalParameterMapping));
    CohortDefinition iv = getInTBProgram();
    cd.addSearch("iv", map(iv, generalParameterMapping));
    CohortDefinition artList = artList();
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii OR iii OR iv) AND artList");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbDenominatorB() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i = transferredOutExceptStartedTbTreatment();
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = startedTbTreatmentWith6MonthsBeforeStartDate();
    cd.addSearch("ii", map(ii, generalParameterMapping));
    cd.setCompositionString("(i OR ii)");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumeratorA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i =
        genericCohortQueries.generalSql(
            "onTbTreatment",
            TXTBQueries.dateObs(
                tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = getInTBProgram();
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition artList = artList();
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii) AND artList");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = txTbNumeratorA();
    cd.addSearch("A", map(A, generalParameterMapping));

    CohortDefinition B = startedTbTreatmentWith6MonthsBeforeStartDate();
    cd.addSearch("B", map(B, generalParameterMapping));

    cd.setCompositionString("A NOT B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = txTbDenominatorA();
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = txTbDenominatorB();
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("A NOT B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = codedYesTbScreening();
    addGeneralParameters(A);
    cd.addSearch("A", map(A, codedObsParameterMapping));
    CohortDefinition B = positiveOrNegativeInvesitionResult();
    addGeneralParameters(B);
    cd.addSearch("B", map(B, generalParameterMapping));
    CohortDefinition C = tbTreatmentStartDateWithinReportingDate();
    addGeneralParameters(C);
    cd.addSearch("C", map(C, generalParameterMapping));
    CohortDefinition D = getInTBProgram();
    cd.addSearch("D", map(D, generalParameterMapping));
    cd.setCompositionString("A OR B OR C OR D");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition negativeScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = tbScreening();
    addGeneralParameters(A);
    cd.addSearch("A", map(A, codedObsParameterMapping));
    CohortDefinition B = positiveScreening();
    addGeneralParameters(B);
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("A NOT B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition newOnARTPositiveScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition DEN = txTbDenominator();
    addGeneralParameters(DEN);
    cd.addSearch("DEN", map(DEN, generalParameterMapping));
    CohortDefinition A = artList();
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = positiveScreening();
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("DEN AND A AND B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition newOnARTNegativeScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition DEN = txTbDenominator();
    cd.addSearch("DEN", map(DEN, generalParameterMapping));
    CohortDefinition A = artList();
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = negativeScreening();
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("DEN AND A AND B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition previouslyOnARTPostiveScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition DEN = txTbDenominator();
    cd.addSearch("DEN", map(DEN, generalParameterMapping));
    CohortDefinition A = artList();
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = positiveScreening();
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("DEN AND A AND B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition previouslyOnARTNegativeScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition DEN = txTbDenominator();
    cd.addSearch("DEN", map(DEN, generalParameterMapping));
    CohortDefinition A = artList();
    cd.addSearch("A", map(A, generalParameterMapping));
    CohortDefinition B = negativeScreening();
    cd.addSearch("B", map(B, generalParameterMapping));
    cd.setCompositionString("DEN AND A AND B");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition patientsNewOnARTNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition NUM = txTbNumerator();
    cd.addSearch("NUM", map(NUM, generalParameterMapping));
    cd.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("NUM AND started-during-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition patientsPreviouslyOnARTNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition NUM = txTbNumerator();
    cd.addSearch("NUM", map(NUM, generalParameterMapping));
    cd.addSearch(
        "started-before-start-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${startDate},location=${location}"));
    cd.setCompositionString("NUM AND started-before-start-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }
}
