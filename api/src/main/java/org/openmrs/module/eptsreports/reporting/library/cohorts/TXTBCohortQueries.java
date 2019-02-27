package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * NOTIFIED TB TREATMENT PATIENTS AT TARV: DIFFERENT SOURCES
   *
   * @param parameterValues
   * @return
   */
  public CompositionCohortDefinition getNotifiedTBTreatmentPatientsOnART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES");
    cd.setDescription(
        "São pacientes notificados do tratamento de tuberculose notificados em diferentes fontes: Antecedentes clinicos adulto e pediatria, seguimento, rastreio de tb, livro de TB.");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    InProgramCohortDefinition TBPROGRAMA =
        (InProgramCohortDefinition)
            genericCohortQueries.createInProgram("InTBProgram", tbMetadata.getTBProgram());
    TBPROGRAMA.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    CodedObsCohortDefinition INICIOST =
        (CodedObsCohortDefinition)
            genericCohorts.hasCodedObs(
                tbMetadata.getTBTreatmentPlanConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    tbMetadata.getTBProcessoEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                Arrays.asList(hivMetadata.getStartDrugsConcept()));
    addStartEndDatesAndLocationParameters(INICIOST);

    DateObsCohortDefinition DATAINICIO =
        (DateObsCohortDefinition)
            genericCohorts.hasDateObs(
                tbMetadata.getStartDrugsConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                Arrays.asList(
                    tbMetadata.getTBLivroEncounterType(),
                    tbMetadata.getTBProcessoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                RangeComparator.GREATER_EQUAL,
                RangeComparator.LESS_EQUAL);
    addStartEndDatesAndLocationParameters(DATAINICIO);
    cd.addSearch(
        "TBPROGRAMA",
        EptsReportUtils.map(TBPROGRAMA, "onOrAfter=${startDate},onOrBefore=${endDate}"));
    cd.addSearch("INICIOST", EptsReportUtils.map(INICIOST, mappings));
    cd.addSearch("DATAINICIO", EptsReportUtils.map(DATAINICIO, mappings));
    cd.setCompositionString("TBPROGRAMA OR INICIOST OR DATAINICIO");

    return cd;
  }

  private void addStartEndDatesAndLocationParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA
   * (SQL) existing codes: NOVOSINICIOS
   */
  public CompositionCohortDefinition
      getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Program artProgram = hivMetadata.getARTProgram();
    CohortDefinition TRANSFDEPRG =
        genericCohortQueries.getPatientsBasedOnPatientStates(
            artProgram.getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId());
    addStartEndDatesAndLocationParameters(TRANSFDEPRG);
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
    addStartEndDatesAndLocationParameters(INICIO);
    cd.addSearch("TRANSFDEPRG", EptsReportUtils.map(TRANSFDEPRG, mappings));
    cd.addSearch("INICIO", EptsReportUtils.map(INICIO, mappings));
    cd.setCompositionString("INICIO NOT TRANSFDEPRG");
    return cd;
  }

  /** ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO) */
  public CompositionCohortDefinition anyTimeARVTreatmentFinalPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO)");
    cd.setDescription(
        "São pacientes que alguma vez esteve em tratamento ARV, contruido atravez de: 1) Pacientes que alguma vez esteve registado no programa de TARV TRATAMENTO 2) Paciente que teve algum formulario FRIDA/FILA preenchido 3) Paciente com data de inicio preenchido na ficha de seguimento 4) Paciente com inicio registado atraves do conceito 1255 - Gestão de TARV e a resposta é 1256 - Inicio");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) -
    // PERIODO FINAL
    InProgramCohortDefinition PROGRAMA =
        (InProgramCohortDefinition)
            genericCohortQueries.createInProgram("InARTProgram", hivMetadata.getARTProgram());
    PROGRAMA.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    addStartEndDatesAndLocationParameters(PROGRAMA);
    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL
    CodedObsCohortDefinition CONCEITO1255 =
        (CodedObsCohortDefinition)
            genericCohorts.hasCodedObs(
                hivMetadata.getARVPlanConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPharmaciaEncounterType()),
                Arrays.asList(
                    hivMetadata.getStartDrugsConcept(),
                    hivMetadata.getTransferFromOtherFacilityConcept()));
    addStartEndDatesAndLocationParameters(CONCEITO1255);

    // INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL
    DateObsCohortDefinition CONCEITODATA =
        (DateObsCohortDefinition)
            genericCohorts.hasDateObs(
                hivMetadata.getStartDrugsConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                Arrays.asList(
                    hivMetadata.getARVPharmaciaEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                RangeComparator.LESS_EQUAL,
                null);
    addStartEndDatesAndLocationParameters(CONCEITODATA);
    // ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - FARMACIA
    CohortDefinition FRIDAFILA =
        genericCohortQueries.hasEncounter(
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()), TimeQualifier.ANY);
    addStartEndDatesAndLocationParameters(FRIDAFILA);
    cd.addSearch("PROGRAMA", EptsReportUtils.map(PROGRAMA, mappings));
    cd.addSearch("CONCEITO1255", EptsReportUtils.map(CONCEITO1255, mappings));
    cd.addSearch("CONCEITODATA", EptsReportUtils.map(CONCEITODATA, mappings));
    cd.addSearch("FRIDAFILA", EptsReportUtils.map(FRIDAFILA, mappings));
    cd.setCompositionString("CONCEITO1255 OR PROGRAMA OR CONCEITODATA OR FRIDAFILA");

    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - ACTIVOS EM TARV. Existing indicator
   * codes: ACTIVOSTARV, TARV, NOTIFICADOSTB
   *
   * @return
   */
  public CompositionCohortDefinition getNotifiedTBPatientsAtARVService() {
    Program artProgram = hivMetadata.getARTProgram();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - ACTIVOS EM TARV");
    cd.setDescription("");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    // [SAIDAPROGRAMA] PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE
    // TRATAMENTO ARV: PERIODO FINAL
    CohortDefinition SAIDAPROGRAMA =
        genericCohortQueries.generalSql(
            "SAIDAPROGRAMA",
            TXTBQueries.patientsWhoCameOutOfARVTreatmentProgram(
                artProgram.getProgramId(),
                hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getId(),
                hivMetadata.getAbandonedWorkflowState().getId(),
                hivMetadata.getPatientHasDiedWorkflowState().getId()));
    addStartEndDatesAndLocationParameters(SAIDAPROGRAMA);
    CohortDefinition ALGUMAVEZTARV = anyTimeARVTreatmentFinalPeriod();
    addStartEndDatesAndLocationParameters(ALGUMAVEZTARV);
    cd.addSearch("SAIDAPROGRAMA", EptsReportUtils.map(SAIDAPROGRAMA, mappings));
    cd.addSearch("ALGUMAVEZTARV", EptsReportUtils.map(ALGUMAVEZTARV, mappings));
    cd.setCompositionString("ALGUMAVEZTARV NOT SAIDAPROGRAMA");

    return cd;
  }

  /**
   * ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS
   *
   * @return
   */
  public CompositionCohortDefinition getPatientsInARTWithoutAbandonedNotification() {
    Program artProgram = hivMetadata.getARTProgram();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortDefinition TARV = getNotifiedTBPatientsAtARVService();

    // ABANDONO NÃO NOTIFICADO - TARV
    CohortDefinition NAONOTIFICADO =
        genericCohortQueries.generalSql(
            "NAONOTIFICADO",
            TXTBQueries.abandonedWithNoNotification(
                artProgram.getProgramId(),
                hivMetadata.getReturnVisitDateConcept().getConceptId(),
                hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState().getId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getId(),
                hivMetadata.getAbandonedWorkflowState().getId(),
                hivMetadata.getPatientHasDiedWorkflowState().getId()));
    cd.addSearch("TARV", EptsReportUtils.map(TARV, mappings));
    cd.addSearch("NAONOTIFICADO", EptsReportUtils.map(NAONOTIFICADO, mappings));
    cd.setCompositionString("TARV NOT NAONOTIFICADO");

    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - NOVOS INICIOS. Existing indicator
   * code: NOVOSINICIOS
   *
   * @return
   */
  public CompositionCohortDefinition notifiedTbPatientsOnARVNewStarting(
      Map<String, Object> parameterValues) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - NOVOS INICIOS");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition NOTIFICADOSTB = getNotifiedTBPatientsAtARVService();
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    cd.addSearch("NOTIFICADOSTB", EptsReportUtils.map(NOTIFICADOSTB, mappings));
    cd.addSearch("NOVOSINICIOS", EptsReportUtils.map(NOVOSINICIOS, mappings));
    cd.setCompositionString("NOTIFICADOSTB AND NOVOSINICIOS");

    return cd;
  }

  /**
   * PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV. Existing indicator codes: TX_TB_NUM
   */
  public CohortDefinition notifiedTbPatientsOnARTService() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV - NOVOS INICIOS");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    CohortDefinition ACTIVOSTARV = getNotifiedTBPatientsAtARVService();
    addStartEndDatesAndLocationParameters(ACTIVOSTARV);
    CohortDefinition NOVOSINICIOS =
        getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation();
    cd.addSearch("ACTIVOSTARV", EptsReportUtils.map(ACTIVOSTARV, mappings));
    cd.addSearch("NOVOSINICIOS", EptsReportUtils.map(NOVOSINICIOS, mappings));
    cd.setCompositionString("ACTIVOSTARV OR NOVOSINICIOS");

    return cd;
  }
}
