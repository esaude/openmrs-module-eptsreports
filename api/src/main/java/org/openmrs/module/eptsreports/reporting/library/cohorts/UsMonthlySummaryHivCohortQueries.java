package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivCohortQueries {

  private static final Date NEW_HIV_INSTRUMENTS_DATE = DateUtil.getDateTime(2012, 3, 21);

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private TbMetadata tbMetadata;

  /**
   * @return Pacientes registados até o fim do mês anterior, contando o nº total de linhas
   *     preenchidas em todas as páginas de todos os Livros de Registo Nº 1 e Nº 2 de Pré-TARV até o
   *     fim do mês anterior.
   */
  public CohortDefinition getRegisteredInPreArtByEndOfPreviousMonth() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_CUMULATIVO DE PACIENTES REGISTADOS ATÉ O FIM DO MÊS ANTERIOR (PRE-TARV)");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    Map<String, Object> mappings = new HashMap<>();
    mappings.put("startDate", NEW_HIV_INSTRUMENTS_DATE);
    mappings.put("endDate", "${endDate}");
    mappings.put("location", "${location}");
    cd.addSearch("INSCRITOS", getEnrolled(), mappings);
    cd.addSearch("CONSULTA", hasFollowUpConsultation(), mappings);
    cd.addSearch("TRANSFERIDODE", getInArtCareEnrolledByTransfer(), mappings);
    cd.addSearch("ENTRADAPRETARV", hasEntryInPreArt(), mappings);

    Date newInstrumentsDayBefore = DateUtil.getDateTime(2012, 3, 20);
    Map<String, Object> initialEncounterMappings = new HashMap<>();
    initialEncounterMappings.put("onOrBefore", newInstrumentsDayBefore);
    initialEncounterMappings.put("locationList", "${location}");
    cd.addSearch("INSCRITOFINAL", hasInitialEncounter(), initialEncounterMappings);

    Map<String, Object> everOnArtMappings = new HashMap<>();
    everOnArtMappings.put("endDate", newInstrumentsDayBefore);
    everOnArtMappings.put("location", "${location}");
    cd.addSearch("ALGUMAVEZTARV", getEverOnART(), everOnArtMappings);

    cd.setCompositionString(
        "INSCRITOS OR ((INSCRITOFINAL NOT ALGUMAVEZTARV) AND CONSULTA) OR TRANSFERIDODE OR ENTRADAPRETARV");

    return cd;
  }

  /**
   * @return Pacientes que foram registados durante o mês na coluna 6 em todos os Livros de Registo
   *     Nº 1 e Nº 2 de Pré-TARV independentemente do estado de permanência do paciente.
   */
  public CohortDefinition getRegisteredInPreArtDuringMonth() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_NUMERO DE PACIENTES REGISTADO DURANTE O MES (PRE-TARV)");

    cd.addParameters(getParameters());

    cd.addSearch("INSCRITOPERIODO", mapStraightThrough(getEnrolled()));

    Map<String, Object> mappings = new HashMap<>();
    mappings.put("onOrBefore", DateUtil.getDateTime(2012, 3, 20));
    mappings.put("locationList", "${location}");
    cd.addSearch("INSCRITOATE0312", hasInitialEncounter(), mappings);

    Map<String, Object> everOnArtMappings = new HashMap<>();
    everOnArtMappings.put("endDate", DateUtil.getDateTime(2012, 3, 20));
    everOnArtMappings.put("location", "${location}");
    cd.addSearch("TARVATE0312", getEverOnART(), everOnArtMappings);

    Map<String, Object> followUpMappings = new HashMap<>();
    followUpMappings.put("startDate", DateUtil.getDateTime(2012, 3, 20));
    followUpMappings.put("endDate", "${startDate-1d}");
    followUpMappings.put("location", "${location}");
    cd.addSearch("CONSULTA0312ATEINICIOPERIODO", hasFollowUpConsultation(), followUpMappings);

    cd.addSearch("CONSULTAPERIODO", mapStraightThrough(hasFollowUpConsultation()));
    cd.addSearch("TRANSFERIDODEPERIODO", mapStraightThrough(getInArtCareEnrolledByTransfer()));
    cd.addSearch("ENTRADAPRETARV", mapStraightThrough(hasEntryInPreArt()));

    cd.setCompositionString(
        "INSCRITOPERIODO OR (((INSCRITOATE0312 NOT TARVATE0312) NOT CONSULTA0312ATEINICIOPERIODO) AND CONSULTAPERIODO) OR TRANSFERIDODEPERIODO OR ENTRADAPRETARV");

    return cd;
  }

  /**
   * @return Pacientes inscritos no servico tarv em um periodo, sem incluir os transferidos de (TARV
   *     e NAO TARV)
   */
  public CohortDefinition getEnrolled() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INSCRITOS NO SERVICO TARV NUM PERIODO (SEM INCLUIR TRANSFERIDOS DE TARV E NAO TARV)");
    cd.addParameters(getParameters());
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("INSCRITOS", map(hasInitialEncounter(), mappings));
    cd.addSearch("TRANSDEPRETARV", mapStraightThrough(getInArtCareEnrolledByTransfer()));
    cd.addSearch("TRANSDETARV", mapStraightThrough(getInArtEnrolledByTransfer()));
    cd.setCompositionString("INSCRITOS NOT (TRANSDEPRETARV OR TRANSDETARV)");
    return cd;
  }

  public CohortDefinition getEnrolledInArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_CUMULATIVO DE PACIENTES REGISTADOS ATÉ O FIM DO MÊS ANTERIOR (TARV)");

    cd.addParameters(getParameters());

    Map<String, Object> mappings = new HashMap<>();
    mappings.put("startDate", NEW_HIV_INSTRUMENTS_DATE);
    mappings.put("endDate", "${endDate}");
    mappings.put("location", "${location}");
    cd.addSearch("INICIO0312FINAL", getInitiatedIncludingTransfers(), mappings);
    cd.addSearch("TRANSFERIDODEPERIODO", getInArtEnrolledByTransfer(), mappings);
    cd.addSearch("CONSULTA0312ATEINICIOPERIODO", hasFollowUpConsultation(), mappings);

    Map<String, Object> m = new HashMap<>();
    m.put("endDate", NEW_HIV_INSTRUMENTS_DATE);
    m.put("location", "${location}");
    cd.addSearch("INICIOATE20032012", getStartedArt(), m);

    Map<String, Object> n = new HashMap<>();
    n.put("onOrAfter", NEW_HIV_INSTRUMENTS_DATE);
    n.put("onOrBefore", "${endDate}");
    n.put("locationList", "${location}");
    cd.addSearch("FARMACIA0312ATEINICIOPERIODO", getEverOnARTPharmacy(), n);

    cd.setCompositionString(
        "INICIO0312FINAL OR TRANSFERIDODEPERIODO OR (INICIOATE20032012 AND (CONSULTA0312ATEINICIOPERIODO OR FARMACIA0312ATEINICIOPERIODO))");

    return cd;
  }

  /**
   * @return Pacientes que entraram no programa de cuidado pre-tarv vindos transferidos de - num
   *     periodo
   */
  public CohortDefinition getInArtCareEnrolledByTransfer() {
    Program program = hivMetadata.getHIVCareProgram();
    ProgramWorkflowState state =
        hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState();
    String name =
        "PROGRAMA: PACIENTES TRANSFERIDOS DE NO PROGRAMA DE CUIDADO (PRE-TARV): NUM PERIODO";
    return getInProgramWithState(program, state, name);
  }

  /**
   * @return Pacientes que entraram no programa de tratamento ARV num periodo vindos transferidos de
   */
  public CohortDefinition getInArtEnrolledByTransfer() {
    Program program = hivMetadata.getARTProgram();
    ProgramWorkflowState state = hivMetadata.getTransferredFromOtherHealthFacilityWorkflowState();
    String name = "PROGRAMA: PACIENTES TRANSFERIDOS DE NO PROGRAMA DE TRATAMENTO ARV: NUM PERIODO";
    return getInProgramWithState(program, state, name);
  }

  public CohortDefinition getTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO CUIDADO PRE-TARV: TRANSFERIDOS PARA - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    cd.addSearch(
        "CUMULATIVOENTRADAS", mapStraightThrough(getRegisteredInPreArtByEndOfPreviousMonth()));

    CohortDefinition transferredOut =
        hivCohortQueries.getPatientsInArtCareTransferredOutToAnotherHealthFacility();
    cd.addSearch("TRANSFERIDOPARA", mapStraightThrough(transferredOut));

    Map<String, Object> mappings = new HashMap<>();
    mappings.put("startDate", NEW_HIV_INSTRUMENTS_DATE);
    mappings.put("endDate", "${endDate}");
    mappings.put("location", "${location}");
    cd.addSearch("INICIO", getInitiatedArt(), mappings);

    cd.addSearch("OBITO", mapStraightThrough(getDeadDuringArtCareProgram()));

    cd.setCompositionString("(CUMULATIVOENTRADAS AND TRANSFERIDOPARA) NOT (INICIO OR OBITO)");

    return cd;
  }

  /**
   * @return Pacientes que iniciaram tratamento ARV num periodo excluindo os transferidos de com a
   *     data de inicio conhecida e mesmo que coincida no periodo
   */
  public CohortDefinition getInitiatedArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");

    cd.addParameters(getParameters());

    cd.addSearch("INICIO", mapStraightThrough(getInitiatedIncludingTransfers()));
    cd.addSearch("TRANSFDEPRG", mapStraightThrough(getInArtEnrolledByTransfer()));

    cd.setCompositionString("INICIO NOT TRANSFDEPRG");

    return cd;
  }

  public CohortDefinition getInPreArtWhoScreenedForTb() {
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    Mapped<CohortDefinition> screenedForTb = map(getTbScreening(), mappings);
    Mapped<CohortDefinition> enrolled = mapStraightThrough(getEnrolled());
    return getEnrolledInArtBookAnd(enrolled, screenedForTb);
  }

  public CohortDefinition getInPreArtWhoScreenedForSti() {
    Mapped<CohortDefinition> screenedForSti = mapStraightThrough(getStiScreening());
    Mapped<CohortDefinition> enrolled = mapStraightThrough(getEnrolled());
    return getEnrolledInArtBookAnd(enrolled, screenedForSti);
  }

  public CohortDefinition getInPreArtWhoStartedCotrimoxazoleProphylaxis() {
    String mappings = "value1=${startDate},value2=${endDate},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis =
        map(getStartedCotrimoxazoleProphylaxis(), mappings);
    Mapped<CohortDefinition> enrolled = mapStraightThrough(getEnrolled());
    return getEnrolledInArtBookAnd(enrolled, startedProphylaxis);
  }

  public CohortDefinition getInPreArtWhoStartedIsoniazidProphylaxis() {
    String mappings = "value1=${startDate},value2=${endDate},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis = map(getStartedIsoniazidProphylaxis(), mappings);
    Mapped<CohortDefinition> enrolled = mapStraightThrough(getEnrolled());
    return getEnrolledInArtBookAnd(enrolled, startedProphylaxis);
  }

  public CohortDefinition getInArtWhoScreenedForTb() {
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    Mapped<CohortDefinition> screenedForTb = map(getTbScreening(), mappings);
    Mapped<CohortDefinition> initiated = mapStraightThrough(getInitiatedArt());
    return getEnrolledInArtBookAnd(initiated, screenedForTb);
  }

  public CohortDefinition getInArtWhoScreenedForSti() {
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    Mapped<CohortDefinition> screenedForSti = map(getStiScreeningForArt(), mappings);
    Mapped<CohortDefinition> initiated = mapStraightThrough(getInitiatedArt());
    return getEnrolledInArtBookAnd(initiated, screenedForSti);
  }

  public CohortDefinition getArtWhoStartedCotrimoxazoleProphylaxis() {
    String mappings = "value1=${startDate},value2=${endDate},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis =
        map(getStartedCotrimoxazoleProphylaxis(), mappings);
    Mapped<CohortDefinition> initiated = mapStraightThrough(getInitiatedArt());
    return getEnrolledInArtBookAnd(initiated, startedProphylaxis);
  }

  public CohortDefinition getInArtWhoStartedIsoniazidProphylaxis() {
    String mappings = "value1=${startDate},value2=${endDate},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis = map(getStartedIsoniazidProphylaxis(), mappings);
    Mapped<CohortDefinition> initiated = mapStraightThrough(getInitiatedArt());
    return getEnrolledInArtBookAnd(initiated, startedProphylaxis);
  }

  public CohortDefinition getAbandonedArtCare() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO CUIDADO PRE-TARV: ABANDONO - NUM PERIODO");

    cd.addParameters(getParameters());

    cd.addSearch(
        "CUMULATIVOENTRADAS", mapStraightThrough(getRegisteredInPreArtByEndOfPreviousMonth()));
    cd.addSearch("ABANDONO", mapStraightThrough(getAbandonedArt()));

    CohortDefinition transferredOut =
        hivCohortQueries.getPatientsInArtCareTransferredOutToAnotherHealthFacility();
    cd.addSearch("TRANSFERIDOPARA", mapStraightThrough(transferredOut));

    cd.addSearch("OBITO", mapStraightThrough(getDeadDuringArtCareProgram()));
    cd.addSearch("INICIO", mapStraightThrough(getInitiatedArt()));

    cd.setCompositionString(
        "(CUMULATIVOENTRADAS AND ABANDONO) NOT (TRANSFERIDOPARA OR OBITO OR INICIO)");

    return cd;
  }

  public CohortDefinition getInArtCareWhoInitiatedArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO CUIDADO PRE-TARV: INICIO TARV - NUM PERIODO");

    cd.addParameters(getParameters());

    CohortDefinition cohort = getRegisteredInPreArtByEndOfPreviousMonth();
    cd.addSearch("CUMULATIVOENTRADAS", mapStraightThrough(cohort));

    cd.addSearch("INICIO", mapStraightThrough(getInitiatedArt()));

    cd.setCompositionString("CUMULATIVOENTRADAS AND INICIO");

    return cd;
  }

  public CohortDefinition getInArtWhoSuspendedTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO TRATAMENTO ARV: SUSPENSO - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String mappings = "endDate=${endDate},location=${location}";
    cd.addSearch("CUMULATIVOENTRADAS", map(getEnrolledInArt(), mappings));

    CohortDefinition suspended = hivCohortQueries.getPatientsInArtWhoSuspendedTreatment();
    cd.addSearch("SUSPENSO", mapStraightThrough(suspended));

    cd.addSearch("ABANDONO", mapStraightThrough(abandonmentNotifiedAndNotNotified()));

    cd.setCompositionString("(CUMULATIVOENTRADAS AND SUSPENSO) NOT ABANDONO");

    return cd;
  }

  public CohortDefinition getInArtTransferredOut() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO TRATAMENTO ARV: TRANSFERIDO PARA - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String mappings = "endDate=${endDate},location=${location}";
    cd.addSearch("CUMULATIVOENTRADAS", map(getEnrolledInArt(), mappings));

    CohortDefinition transferredOut =
        hivCohortQueries.getPatientsInArtTransferredOutToAnotherHealthFacility();
    cd.addSearch("TRANSFERIDOPARA", mapStraightThrough(transferredOut));

    CohortDefinition suspendedTreatment = hivCohortQueries.getPatientsInArtWhoSuspendedTreatment();
    cd.addSearch("SUSPENSO", mapStraightThrough(suspendedTreatment));

    cd.addSearch("ABANDONO", mapStraightThrough(abandonmentNotifiedAndNotNotified()));

    cd.setCompositionString("(CUMULATIVOENTRADAS AND TRANSFERIDOPARA) NOT (SUSPENSO OR ABANDONO)");

    return cd;
  }

  public CohortDefinition getDeadDuringArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO TRATAMENTO TARV: OBITO - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    CohortDefinition died = hivCohortQueries.getPatientsInArtWhoDied();
    cd.addSearch("OBITO", mapStraightThrough(died));

    cd.addSearch("CUMULATIVOENTRADAS", mapStraightThrough(getEnrolledInArt()));
    CohortDefinition transferredOut =
        hivCohortQueries.getPatientsInArtTransferredOutToAnotherHealthFacility();

    cd.addSearch("TRANSFERIDOPARA", mapStraightThrough(transferredOut));

    CohortDefinition suspended = hivCohortQueries.getPatientsInArtWhoSuspendedTreatment();
    cd.addSearch("SUSPENSO", mapStraightThrough(suspended));

    cd.addSearch("ABANDONO", mapStraightThrough(abandonmentNotifiedAndNotNotified()));

    cd.setCompositionString(
        "(OBITO AND CUMULATIVOENTRADAS) NOT (TRANSFERIDOPARA OR SUSPENSO OR ABANDONO)");

    return cd;
  }

  public CohortDefinition getDeadDuringArtCare() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO CUIDADO PRE-TARV: OBITO - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    cd.addSearch("OBITO", mapStraightThrough(getDeadDuringArtCareProgram()));
    cd.addSearch(
        "CUMULATIVOENTRADAS", mapStraightThrough(getRegisteredInPreArtByEndOfPreviousMonth()));

    Map<String, Object> mappings = new HashMap<>();
    mappings.put("startDate", DateUtil.getDateTime(2012, 3, 20));
    mappings.put("endDate", "${endDate}");
    mappings.put("location", "${location}");
    cd.addSearch("INICIO", getInitiatedArt(), mappings);

    cd.setCompositionString("(OBITO AND CUMULATIVOENTRADAS) NOT INICIO");

    return cd;
  }

  public CohortDefinition getEnrolledInArtDuringMonth() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_NUMERO DE PACIENTES REGISTADO DURANTE O MES (TARV)");

    cd.addParameters(getParameters());

    cd.addSearch("INICIOPERIODO", mapStraightThrough(getInitiatedIncludingTransfers()));
    cd.addSearch("TRANSFERIDODEPERIODO", mapStraightThrough(getInArtEnrolledByTransfer()));
    Map<String, Object> mappings = new HashMap<>();
    mappings.put("endDate", DateUtil.getDateTime(2012, 3, 20));
    mappings.put("location", "${location}");
    cd.addSearch("INICIOATE0312", getStartedArt(), mappings);

    Map<String, Object> pharmacyMappings = new HashMap<>();
    pharmacyMappings.put("onOrAfter", NEW_HIV_INSTRUMENTS_DATE);
    pharmacyMappings.put("onOrBefore", "${startDate-1d}");
    pharmacyMappings.put("locationList", "${location}");
    cd.addSearch("FARMACIA0312ATEINICIOPERIODO", getEverOnARTPharmacy(), pharmacyMappings);

    Map<String, Object> followUpMappings = new HashMap<>();
    followUpMappings.put("startDate", NEW_HIV_INSTRUMENTS_DATE);
    followUpMappings.put("endDate", "${startDate-1d}");
    followUpMappings.put("location", "${location}");
    cd.addSearch("CONSULTA0312ATEINICIOPERIODO", hasFollowUpConsultation(), followUpMappings);

    Map<String, Object> artPharmacyMappings = new HashMap<>();
    artPharmacyMappings.put("onOrAfter", "${startDate}");
    artPharmacyMappings.put("onOrBefore", "${endDate}");
    artPharmacyMappings.put("locationList", "${location}");
    cd.addSearch("FARMACIAPERIODO", getEverOnARTPharmacy(), artPharmacyMappings);

    cd.addSearch("CONSULTAPERIODO", mapStraightThrough(hasFollowUpConsultation()));

    cd.setCompositionString(
        "INICIOPERIODO OR TRANSFERIDODEPERIODO OR ((INICIOATE0312 NOT (FARMACIA0312ATEINICIOPERIODO OR CONSULTA0312ATEINICIOPERIODO)) AND (FARMACIAPERIODO OR CONSULTAPERIODO))");

    return cd;
  }

  public CohortDefinition getCurrentlyInTreatment() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "ACTUALMENTE EM TARV ATÉ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS EM 60 DIAS - REAL (SQL)");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT inicio_fila_seg_prox.*, "
            + "               IF (estado_id_real IS NOT NULL, "
            + "                   estado_id_real, "
            + "                   IF(IF(data_proximo_lev IS NULL AND data_proximo_seguimento IS NOT NULL, "
            + "                         data_proximo_seguimento,  "
            + "                         data_proximo_lev) IS NULL, "
            + "                      9,  "
            + "                      IF(:endDate > Date_add("
            + "                         IF(data_proximo_lev IS NULL AND data_proximo_seguimento IS NOT NULL,"
            + "                            data_proximo_seguimento, "
            + "                            IF(data_proximo_lev IS NOT NULL AND data_proximo_seguimento IS NULL, "
            + "                               data_proximo_lev,"
            + "                               IF(data_proximo_lev > data_proximo_seguimento , "
            + "                                  data_proximo_lev,"
            + "                                  data_proximo_seguimento))), "
            + "                         INTERVAL 60 day),"
            + "                         9, "
            + "                         6))) estado_final "
            + "        FROM   (SELECT inicio_fila_seg.*, "
            + "                       obs_fila.value_datetime       data_proximo_lev, "
            + "                       obs_seguimento.value_datetime data_proximo_seguimento, "
            + "                       inicio_fila_seg.estado_id     estado_id_real "
            + "                FROM   (SELECT inicio.*, "
            + "                               saida.estado, "
            + "                               saida.data_estado, "
            + "                               saida.estado_id, "
            + "                               max_fila.data_fila, "
            + "                               max_consulta.data_seguimento "
            + "                        FROM   (SELECT patient_id, "
            + "                                       Min(data_inicio) data_inicio "
            + "                                FROM   (SELECT p.patient_id, "
            + "                                               Min(e.encounter_datetime) "
            + "                                               data_inicio "
            + "                                        FROM   patient p "
            + "                                               INNER JOIN encounter e "
            + "                                                       ON p.patient_id = "
            + "                                                          e.patient_id "
            + "                                               INNER JOIN obs o "
            + "                                                       ON o.encounter_id = "
            + "                                                          e.encounter_id "
            + "                                        WHERE  e.voided = 0 "
            + "                                               AND o.voided = 0 "
            + "                                               AND p.voided = 0 "
            + "                                               AND e.encounter_type IN ( %d, %d, %d ) "
            + "                                               AND o.concept_id = %d "
            + "                                               AND o.value_coded = %d "
            + "                                               AND e.encounter_datetime <= "
            + "                                                   :endDate "
            + "                                               AND e.location_id = :location "
            + "                                        GROUP  BY p.patient_id "
            + "                                        UNION "
            + "                                        SELECT p.patient_id, "
            + "                                               Min(value_datetime) data_inicio "
            + "                                        FROM   patient p "
            + "                                               INNER JOIN encounter e "
            + "                                                       ON p.patient_id = "
            + "                                                          e.patient_id "
            + "                                               INNER JOIN obs o "
            + "                                                       ON e.encounter_id = "
            + "                                                          o.encounter_id "
            + "                                        WHERE  p.voided = 0 "
            + "                                               AND e.voided = 0 "
            + "                                               AND o.voided = 0 "
            + "                                               AND e.encounter_type IN ( %d, %d, %d ) "
            + "                                               AND o.concept_id = %d "
            + "                                               AND o.value_datetime IS NOT NULL "
            + "                                               AND o.value_datetime <= :endDate "
            + "                                               AND e.location_id = :location "
            + "                                        GROUP  BY p.patient_id "
            + "                                        UNION "
            + "                                        SELECT pg.patient_id, "
            + "                                               date_enrolled data_inicio "
            + "                                        FROM   patient p "
            + "                                               INNER JOIN patient_program pg "
            + "                                                       ON p.patient_id = "
            + "                                                          pg.patient_id "
            + "                                        WHERE  pg.voided = 0 "
            + "                                               AND p.voided = 0 "
            + "                                               AND program_id = %d "
            + "                                               AND date_enrolled <= :endDate "
            + "                                               AND pg.location_id = :location "
            + "                                        UNION "
            + "                                        SELECT e.patient_id, "
            + "                                               Min(e.encounter_datetime) AS "
            + "                                               data_inicio "
            + "                                        FROM   patient p "
            + "                                               INNER JOIN encounter e "
            + "                                                       ON p.patient_id = "
            + "                                                          e.patient_id "
            + "                                        WHERE  p.voided = 0 "
            + "                                               AND e.encounter_type = %d "
            + "                                               AND e.voided = 0 "
            + "                                               AND e.encounter_datetime <= "
            + "                                                   :endDate "
            + "                                               AND e.location_id = :location "
            + "                                        GROUP  BY p.patient_id) inicio_real "
            + "                                GROUP  BY patient_id)inicio "
            + "                               LEFT JOIN (SELECT pg.patient_id, "
            + "                                                 CASE ps.state "
            + "                                                   WHEN 7 THEN "
            + "                                                   'TRANSFERIDO PARA' "
            + "                                                   WHEN 8 THEN 'SUSPENSO' "
            + "                                                   WHEN 9 THEN 'ABANDONO' "
            + "                                                   WHEN 10 THEN 'OBITO' "
            + "                                                 end           AS estado, "
            + "                                                 ps.state      estado_id, "
            + "                                                 ps.start_date data_estado "
            + "                                          FROM   patient p "
            + "                                                 INNER JOIN patient_program pg "
            + "                                                         ON p.patient_id = "
            + "                                                            pg.patient_id "
            + "                               INNER JOIN patient_state ps "
            + "                                       ON pg.patient_program_id = "
            + "                                          ps.patient_program_id "
            + "                                          WHERE  pg.voided = 0 "
            + "                                                 AND ps.voided = 0 "
            + "                                                 AND p.voided = 0 "
            + "                                                 AND pg.program_id = %d "
            + "                                                 AND ps.state IN ( %d, %d, %d, %d ) "
            + "                                                 AND ps.end_date IS NULL "
            + "                                                 AND ps.start_date <= :endDate "
            + "                                                 AND location_id = :location) "
            + "                                         saida "
            + "                                      ON inicio.patient_id = saida.patient_id "
            + "                               LEFT JOIN (SELECT p.patient_id, "
            + "                                                 Max(encounter_datetime) "
            + "                                                 data_fila "
            + "                                          FROM   patient p "
            + "                                                 INNER JOIN encounter e "
            + "                                                         ON e.patient_id = "
            + "                                                            p.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND e.encounter_type = %d "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_datetime <= "
            + "                                                     :endDate "
            + "                                          GROUP  BY p.patient_id) max_fila "
            + "                                      ON inicio.patient_id = max_fila.patient_id "
            + "                               LEFT JOIN (SELECT p.patient_id, "
            + "                                                 Max(encounter_datetime) "
            + "                                                 data_seguimento "
            + "                                          FROM   patient p "
            + "                                                 INNER JOIN encounter e "
            + "                                                         ON e.patient_id = "
            + "                                                            p.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND e.encounter_type IN ( %d, %d ) "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_datetime <= "
            + "                                                     :endDate "
            + "                                          GROUP  BY p.patient_id) max_consulta "
            + "                                      ON inicio.patient_id = "
            + "                                         max_consulta.patient_id "
            + "                        GROUP  BY inicio.patient_id) inicio_fila_seg "
            + "                       LEFT JOIN obs obs_fila "
            + "                              ON obs_fila.person_id = inicio_fila_seg.patient_id "
            + "                                 AND obs_fila.voided = 0 "
            + "                                 AND obs_fila.obs_datetime = "
            + "                                     inicio_fila_seg.data_fila "
            + "                                 AND obs_fila.concept_id = %d "
            + "                                 AND obs_fila.location_id = :location "
            + "                       LEFT JOIN obs obs_seguimento "
            + "                              ON obs_seguimento.person_id = "
            + "                                 inicio_fila_seg.patient_id "
            + "                                 AND obs_seguimento.voided = 0 "
            + "                                 AND obs_seguimento.obs_datetime = "
            + "                                     inicio_fila_seg.data_seguimento "
            + "                                 AND obs_seguimento.concept_id = %d "
            + "                                 AND obs_seguimento.location_id = :location) "
            + "               inicio_fila_seg_prox "
            + "        GROUP  BY patient_id) coorte12meses_final "
            + "WHERE  estado_final = 6 ";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId()));

    return cd;
  }

  public CohortDefinition getEnteredInGAAC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("PACIENTES INSCRITOS NO GAAC NUM PERIODO");

    cd.addParameters(getParameters());

    String query =
        "SELECT gm.member_id "
            + "FROM   gaac g "
            + "       INNER JOIN gaac_member gm "
            + "               ON g.gaac_id = gm.gaac_id "
            + "WHERE  gm.start_date BETWEEN :startDate AND :endDate "
            + "       AND gm.voided = 0 "
            + "       AND g.voided = 0 "
            + "       AND location_id = :location ";

    cd.setQuery(query);

    return cd;
  }

  public CohortDefinition getLeftGAAC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("PACIENTES QUE SAIRAM DO GAAC");

    cd.addParameters(getParameters());

    String query =
        "SELECT gm.member_id "
            + "FROM   gaac g "
            + "       INNER JOIN gaac_member gm "
            + "               ON g.gaac_id = gm.gaac_id "
            + "WHERE  gm.end_date BETWEEN :startDate AND :endDate "
            + "       AND gm.leaving = 1 "
            + "       AND gm.voided = 0 "
            + "       AND g.voided = 0 "
            + "       AND location_id = :location "
            + "       AND reason_leaving_type IN ( 1, 2, 3, 4, "
            + "                                    5, 6 ); ";

    cd.setQuery(query);

    return cd;
  }

  public CohortDefinition getActiveGAAC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("PACIENTES QUE SAIRAM DO GAAC");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT gm.member_id "
            + "FROM   gaac g "
            + "       INNER JOIN gaac_member gm "
            + "               ON g.gaac_id = gm.gaac_id "
            + "WHERE  gm.start_date < :endDate "
            + "       AND gm.voided = 0 "
            + "       AND g.voided = 0 "
            + "       AND ( ( leaving IS NULL ) "
            + "              OR ( leaving = 0 ) "
            + "              OR ( leaving = 1 "
            + "                   AND gm.end_date > :endDate ) ) "
            + "       AND location_id = :location ";

    cd.setQuery(query);

    return cd;
  }

  public CohortDefinition getGAACGroups() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("GAACS FORMADOS E ACTIVOS ATÉ UMA DATA FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT gaac_id "
            + "FROM   gaac "
            + "WHERE  start_date <= :endDate "
            + "       AND voided = 0 "
            + "       AND ( ( crumbled IS NULL ) "
            + "              OR ( crumbled = 0 ) "
            + "              OR ( crumbled = 1 "
            + "                   AND date_crumbled >= :endDate ) ) "
            + "       AND location_id = :location ";

    cd.setQuery(query);

    return cd;
  }

  public CohortDefinition getInArtAbandoned() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("RM_PACIENTES QUE SAIRAM DO TRATAMENTO ARV: ABANDONO - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String mappings = "endDate=${endDate},location=${location}";
    cd.addSearch("CUMULATIVOENTRADAS", map(getEnrolledInArt(), mappings));

    cd.addSearch("ABANDONO", mapStraightThrough(abandonmentNotifiedAndNotNotified()));

    cd.setCompositionString("CUMULATIVOENTRADAS AND ABANDONO");

    return cd;
  }

  private CohortDefinition getEverOnART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL - REAL (COMPOSICAO)");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String mappings = "onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("CONCEITO1255", map(getAnyTimeOnART(), mappings));

    cd.addSearch("PROGRAMA", mapStraightThrough(getEnrolledInArtProgram()));

    String startDateMappings = "value2=${endDate},locationList=${location}";
    cd.addSearch("CONCEITODATA", map(getWithARTStartDate(), startDateMappings));

    String pharmacyMappings = "onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("FRIDAFILA", map(getEverOnARTPharmacy(), pharmacyMappings));

    cd.setCompositionString("CONCEITO1255 OR PROGRAMA OR CONCEITODATA OR FRIDAFILA");
    return cd;
  }

  private CohortDefinition getInProgramWithState(
      Program program, ProgramWorkflowState state, String name) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(name);

    cd.addParameters(getParameters());

    String query =
        "SELECT pg.patient_id  "
            + "FROM   patient p  "
            + "       INNER JOIN patient_program pg  "
            + "               ON p.patient_id = pg.patient_id  "
            + "       INNER JOIN patient_state ps  "
            + "               ON pg.patient_program_id = ps.patient_program_id  "
            + "WHERE  pg.voided = 0  "
            + "       AND ps.voided = 0  "
            + "       AND p.voided = 0  "
            + "       AND pg.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND ps.start_date = pg.date_enrolled  "
            + "       AND ps.start_date BETWEEN :startDate AND :endDate  "
            + "       AND location_id = :location ";

    cd.setQuery(String.format(query, program.getProgramId(), state.getProgramWorkflowStateId()));

    return cd;
  }

  private CohortDefinition getWithARTStartDate() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("INICIO DE TARV USANDO O CONCEITO DE DATA - PERIODO FINAL");

    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    cd.setTimeModifier(TimeModifier.ANY);

    cd.setQuestion(hivMetadata.getHistoricalDrugStartDateConcept());

    cd.addEncounterType(hivMetadata.getARVPharmaciaEncounterType());
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.addEncounterType(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setOperator2(RangeComparator.LESS_EQUAL);

    return cd;
  }

  private CohortDefinition getEnrolledInArtProgram() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA TRATAMENTO ARV (TARV) - PERIODO FINAL");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT pg.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pg "
            + "               ON p.patient_id = pg.patient_id "
            + "WHERE  pg.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND program_id = %d "
            + "       AND date_enrolled <= :endDate "
            + "       AND location_id = :location ";

    cd.setQuery(String.format(query, hivMetadata.getARTProgram().getProgramId()));

    return cd;
  }

  private CohortDefinition getAnyTimeOnART() {
    CodedObsCohortDefinition cd =
        ((CodedObsCohortDefinition)
            genericCohortQueries.hasCodedObs(hivMetadata.getARVPlanConcept(), null));

    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - PERIODO FINAL");

    cd.setTimeModifier(TimeModifier.ANY);

    cd.addEncounterType(hivMetadata.getARVPharmaciaEncounterType());
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.addEncounterType(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setOperator(SetComparator.IN);

    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getTransferFromOtherFacilityConcept());

    return cd;
  }

  /**
   * @return Pacientes que tiveram consulta clinica durante o período, mas que não tem fila no mesmo
   *     período ou se tiver fila este fila é superior a data primeira consulta clinica. lembrando
   *     que o paciente não deve ter mais consultas na mesma unidade sanitária anteriores a data
   *     inicial do período
   */
  private CohortDefinition hasEntryInPreArt() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("RM_PACIENTES QUE ENTRARAM EM PRE-TARV NUM DETERMINADO PERIODO");
    cd.addParameters(getParameters());
    String query =
        "select patient_id "
            + "from  "
            + "(select consulta.patient_id,data_consulta,data_fila "
            + "from "
            + "(   select  p.patient_id,min(e.encounter_datetime) data_consulta "
            + "    from    patient p "
            + "            inner join encounter e on p.patient_id=e.patient_id "
            + "    where   p.voided=0 and e.voided=0 and e.encounter_type in (%d,%d) and  "
            + "            e.encounter_datetime between :startDate and :endDate and e.location_id=:location and  "
            + "            p.patient_id not in  "
            + "            ( "
            + "                select  distinct p.patient_id "
            + "                from    patient p "
            + "                        inner join encounter e on p.patient_id=e.patient_id "
            + "                where   p.voided=0 and e.voided=0 and   "
            + "                        e.encounter_datetime<:startDate and e.location_id=:location "
            + "                         "
            + "                union "
            + "                 "
            + "                select  distinct pg.patient_id "
            + "                from    patient p  "
            + "                        inner join patient_program pg on p.patient_id=pg.patient_id "
            + "                        inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + "                where   pg.voided=0 and ps.voided=0 and p.voided=0 and  "
            + "                        pg.program_id=%d and ps.state=%d and ps.start_date=pg.date_enrolled and  "
            + "                        ps.start_date<=:endDate and location_id=:location "
            + "            ) "
            + "    group by p.patient_id "
            + ") consulta "
            + "left join  "
            + "( "
            + "    select  p.patient_id,min(e.encounter_datetime) data_fila "
            + "    from    patient p "
            + "            inner join encounter e on p.patient_id=e.patient_id "
            + "    where   p.voided=0 and e.voided=0 and e.encounter_type=%d and  "
            + "            e.encounter_datetime between :startDate and :endDate and e.location_id=:location     "
            + "    group by p.patient_id "
            + ") fila on consulta.patient_id=fila.patient_id "
            + ")consulta_fila "
            + "where (data_fila is null ) or (data_consulta<data_fila)";
    cd.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * @return Pacientes que têm pelo menos uma consulta clínica (seguimento) num determinado periodo
   */
  private CohortDefinition hasFollowUpConsultation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("PACIENTES COM PELO MENOS UMA CONSULTA CLINICA NUM DETERMINADO PERIODO");
    cd.addParameters(getParameters());
    String query =
        "SELECT e.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type IN ( %d, %d ) "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "       AND e.location_id = :location ";
    cd.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  /**
   * @return Pacientes que alguma vez esteve em tratamento ARV, levantou pelo menos uma vez ARV na
   *     Famacia (tem pelo menos um FRIDA/FILA preenchido) num determinado periodo
   */
  private CohortDefinition getEverOnARTPharmacy() {
    EncounterCohortDefinition cd = new EncounterCohortDefinition();

    cd.setName("ALGUMA VEZ ESTEVE EM TRATAMENTO ARV - NUM PERÍODO - FARMÁCIA");

    cd.addParameter(new Parameter("onOrAfter", "", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "", Date.class));
    cd.addParameter(new Parameter("locationList", "", Location.class));

    cd.setTimeQualifier(TimeQualifier.ANY);
    cd.addEncounterType(hivMetadata.getARVPharmaciaEncounterType());

    return cd;
  }

  private List<Parameter> getParameters() {
    return Arrays.asList(
        ReportingConstants.START_DATE_PARAMETER,
        ReportingConstants.END_DATE_PARAMETER,
        ReportingConstants.LOCATION_PARAMETER);
  }

  /**
   * @return Paciente que iniciou o tratamento ARV (Na unidade sanitaria seleccionada) até um
   *     determinado periodo final. São inclusos os transferidos de com data de inicio conhecida
   */
  private CohortDefinition getStartedArt() {
    // TODO use StartedArtBeforeDateCalculation
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName(
        "INICIO DE TRATAMENTO ARV - PERIODO FINAL: INCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND e.encounter_type IN ( %d, %d, %d ) "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND e.location_id = :location "
            + "GROUP  BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.encounter_type IN ( %d, %d, %d ) "
            + "       AND o.concept_id = %d "
            + "       AND o.value_datetime IS NOT NULL "
            + "       AND o.value_datetime <= :endDate "
            + "       AND e.location_id = :location "
            + "GROUP  BY p.patient_id "
            + "UNION "
            + "SELECT pg.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pg "
            + "               ON p.patient_id = pg.patient_id "
            + "WHERE  pg.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND program_id = %d "
            + "       AND date_enrolled <= :endDate "
            + "       AND location_id = :location "
            + "UNION "
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.encounter_type = %d "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND e.location_id = :location "
            + "GROUP  BY p.patient_id ";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));

    return cd;
  }

  private CohortDefinition getStartedIsoniazidProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("startedIsoniazidProphylaxis");
    cd.setQuestion(hivMetadata.getIsoniazidUsageConcept());
    cd.setTimeModifier(TimeModifier.FIRST);

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

  private CohortDefinition getStartedCotrimoxazoleProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("PACIENTES QUE INICIARAM PROFILAXIA COM COTRIMOXAZOL");

    cd.setTimeModifier(TimeModifier.ANY);
    cd.setQuestion(commonMetadata.getCotrimoxazoleProphylaxisStartDateConcept());

    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.addEncounterType(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  private CohortDefinition getStiScreeningFromClinicalProcess() {

    CodedObsCohortDefinition cd =
        ((CodedObsCohortDefinition)
            genericCohortQueries.hasCodedObs(null, TimeModifier.ANY, SetComparator.IN, null, null));

    cd.addEncounterType(hivMetadata.getARVAdultInitialBEncounterType());
    cd.setQuestion(hivMetadata.getUrogenitalExamFindingsConcept());
    cd.addValue(hivMetadata.getNormalConcept());
    cd.addValue(hivMetadata.getAbnormalConcept());
    cd.addValue(hivMetadata.getSecretionsConcept());
    cd.addValue(hivMetadata.getCondylomasConcept());
    cd.addValue(hivMetadata.getUlcersConcept());

    return cd;
  }

  private CohortDefinition getStiScreeningFromFollowUp() {
    CodedObsCohortDefinition cd =
        (CodedObsCohortDefinition)
            genericCohortQueries.hasCodedObs(null, TimeModifier.ANY, SetComparator.IN, null, null);

    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.addEncounterType(hivMetadata.getPediatriaSeguimentoEncounterType());
    cd.setQuestion(commonMetadata.getStiScreeningConcept());
    cd.addValue(commonMetadata.getNoConcept());
    cd.addValue(commonMetadata.getYesConcept());

    return cd;
  }

  private CohortDefinition getStiScreeningForArt() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();

    cd.setName("PACIENTES QUE TIVERAM RASTREIO DE ITS – FICHA DE SEGUIMENTO");

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    cd.setQuestion(commonMetadata.getStiScreeningConcept());
    cd.setTimeModifier(TimeModifier.ANY);

    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.addEncounterType(hivMetadata.getPediatriaSeguimentoEncounterType());

    cd.setOperator(SetComparator.IN);

    cd.addValue(commonMetadata.getNoConcept());
    cd.addValue(commonMetadata.getYesConcept());

    return cd;
  }

  private CohortDefinition getStiScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES QUE TIVERAM RASTREIO DE ITS – PROCESSO CLINICO E FICHA DE SEGUIMENTO");

    cd.addParameters(getParameters());

    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("PROCESSO", map(getStiScreeningFromClinicalProcess(), mappings));
    cd.addSearch("SEGUIMENTO", map(getStiScreeningFromFollowUp(), mappings));

    cd.setCompositionString("PROCESSO OR SEGUIMENTO");

    return cd;
  }

  private CohortDefinition getTbScreening() {
    List<Concept> values =
        Arrays.asList(commonMetadata.getNoConcept(), commonMetadata.getYesConcept());
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getTbScreeningConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getPediatriaSeguimentoEncounterType()),
        values);
  }

  /**
   * @param enrolled Cohort of patients registered in one of the ART books
   * @param toCompose Mapped cohort of screened patients. Parameters to map are {@code onOrBefore,
   *     onOrAfter} and {@code location}
   * @return Composition cohort of patients who are registered in pre ART Book 1 composed with
   *     {@code toCompose} param using an 'AND' operator.
   */
  private CohortDefinition getEnrolledInArtBookAnd(
      Mapped<CohortDefinition> enrolled, Mapped<CohortDefinition> toCompose) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("enrolledInArtAnd");

    cd.addParameters(getParameters());

    cd.addSearch("INSCRITOS", enrolled);
    cd.addSearch("COMPOSE", toCompose);

    cd.setCompositionString("INSCRITOS AND COMPOSE");

    return cd;
  }

  private CohortDefinition getPatientsInArtWhoAbandonedWithNoNotification() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("ABANDONO NÃO NOTIFICADO - TARV");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(encounter_datetime) encounter_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "        WHERE  p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND e.encounter_type = %d "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_datetime <= :endDate "
            + "        GROUP  BY p.patient_id) max_frida "
            + "       INNER JOIN obs o "
            + "               ON o.person_id = max_frida.patient_id "
            + "WHERE  max_frida.encounter_datetime = o.obs_datetime "
            + "       AND o.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.location_id = :location "
            + "       AND patient_id NOT IN (SELECT pg.patient_id "
            + "                              FROM   patient p "
            + "                                     INNER JOIN patient_program pg "
            + "                                             ON p.patient_id = pg.patient_id "
            + "                                     INNER JOIN patient_state ps "
            + "                                             ON "
            + "           pg.patient_program_id = ps.patient_program_id "
            + "                              WHERE  pg.voided = 0 "
            + "                                     AND ps.voided = 0 "
            + "                                     AND p.voided = 0 "
            + "                                     AND pg.program_id = %d "
            + "                                     AND ps.state IN ( %d, %d, %d, %d ) "
            + "                                     AND ps.end_date IS NULL "
            + "                                     AND ps.start_date <= :endDate "
            + "                                     AND location_id = :location "
            + "                              UNION "
            + "                              SELECT person_id "
            + "                              FROM   person "
            + "                              WHERE  dead = 1 "
            + "                                     AND death_date <= :endDate "
            + "                                     AND voided = 0) "
            + "       AND patient_id NOT IN(SELECT patient_id "
            + "                             FROM   (SELECT p.patient_id, "
            + "                                            Max(encounter_datetime) "
            + "                                            encounter_datetime "
            + "                                     FROM   patient p "
            + "                                            INNER JOIN encounter e "
            + "                                                    ON "
            + "                                            e.patient_id = p.patient_id "
            + "                                     WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type IN ( %d, %d ) "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_datetime <= :endDate "
            + "                                     GROUP  BY p.patient_id) max_mov "
            + "                                    INNER JOIN obs o "
            + "                                            ON o.person_id = max_mov.patient_id "
            + "                             WHERE  max_mov.encounter_datetime = o.obs_datetime "
            + "                                    AND o.voided = 0 "
            + "                                    AND o.concept_id = %d "
            + "                                    AND o.location_id = :location "
            + "                                    AND "
            + "                            Datediff(:endDate, o.value_datetime) <= 60) "
            + "       AND patient_id NOT IN(SELECT abandono.patient_id "
            + "                             FROM   (SELECT pg.patient_id "
            + "                                     FROM   patient p "
            + "                                            INNER JOIN patient_program pg "
            + "                                                    ON "
            + "                                            p.patient_id = pg.patient_id "
            + "                                            INNER JOIN patient_state ps "
            + "                                                    ON "
            + "       pg.patient_program_id = ps.patient_program_id "
            + "       WHERE  pg.voided = 0 "
            + "       AND ps.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND pg.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND ps.end_date IS NULL "
            + "       AND ps.start_date <= :endDate "
            + "       AND location_id = :location)abandono "
            + "       INNER JOIN (SELECT max_frida.patient_id, "
            + "                  max_frida.encounter_datetime, "
            + "                  o.value_datetime "
            + "           FROM   (SELECT p.patient_id, "
            + "                          Max(encounter_datetime) "
            + "                          encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON "
            + "                          e.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.encounter_type = %d "
            + "                          AND "
            + "                  e.location_id = :location "
            + "                          AND "
            + "                  e.encounter_datetime <= :endDate "
            + "                   GROUP  BY p.patient_id) max_frida "
            + "                  INNER JOIN obs o "
            + "                          ON o.person_id = "
            + "                             max_frida.patient_id "
            + "           WHERE "
            + "       max_frida.encounter_datetime = o.obs_datetime "
            + "       AND o.voided = 0 "
            + "       AND o.concept_id = %d "
            + "       AND o.location_id = :location) ultimo_fila "
            + "       ON abandono.patient_id = ultimo_fila.patient_id "
            + "       WHERE  Datediff(:endDate, ultimo_fila.value_datetime) < 60) "
            + "       AND Datediff(:endDate, o.value_datetime) >= 60; ";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getArtSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getArtAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()));

    return cd;
  }

  /**
   * @return Pacientes que abriram processo no servico TARV (Preencheram as partes A do processo
   *     clinico Adulto ou Pediatria)
   */
  private CohortDefinition hasInitialEncounter() {
    EncounterCohortDefinition cd = new EncounterCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "", Date.class));
    cd.addParameter(new Parameter("locationList", "", Location.class));
    cd.setName("PACIENTES INSCRITOS NO SERVICO TARV - NUM PERIODO");
    List<EncounterType> encounterTypeList =
        Arrays.asList(
            hivMetadata.getARVAdultInitialEncounterType(),
            hivMetadata.getARVPediatriaInitialEncounterType());
    cd.setTimeQualifier(TimeQualifier.FIRST);
    cd.setEncounterTypeList(encounterTypeList);
    return cd;
  }

  private CohortDefinition getAbandonedArt() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("PACIENTES PRÉ-TARV QUE ABANDONARAM O CUIDADO: NUM PERÍODO");

    cd.addParameters(getParameters());

    String query =
        "SELECT pg.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pg "
            + "               ON p.patient_id = pg.patient_id "
            + "       INNER JOIN patient_state ps "
            + "               ON pg.patient_program_id = ps.patient_program_id "
            + "WHERE  pg.voided = 0 "
            + "       AND ps.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND pg.program_id = %d "
            + "       AND ps.state = %d "
            + "       AND ps.end_date IS NULL "
            + "       AND ps.start_date >= :startDate "
            + "       AND ps.start_date <= :endDate "
            + "       AND location_id = :location "
            + "UNION "
            + "SELECT patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Max(data_consulta) data_consulta, "
            + "               Max(data_proxima)  data_proxima "
            + "        FROM   (SELECT p.patient_id, "
            + "                       Max(e.encounter_datetime) data_consulta, "
            + "                       Max(e.encounter_datetime) data_proxima "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.encounter_datetime >= :startDate "
            + "                       AND e.encounter_datetime <= :endDate "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type IN ( %d, %d, %d, %d, "
            + "                                                 %d, %d, %d, %d, "
            + "                                                 %d, %d, %d ) "
            + "                GROUP  BY p.patient_id "
            + "                UNION "
            + "                SELECT max_seguimento.patient_id, "
            + "                       max_seguimento.encounter_datetime data_consulta, "
            + "                       o.value_datetime                  data_proxima "
            + "                FROM   (SELECT p.patient_id, "
            + "                               Max(encounter_datetime) encounter_datetime "
            + "                        FROM   patient p "
            + "                               INNER JOIN encounter e "
            + "                                       ON e.patient_id = p.patient_id "
            + "                        WHERE  p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND e.encounter_type IN ( %d, %d ) "
            + "                               AND e.location_id = :location "
            + "                               AND e.encounter_datetime >= :startDate "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                        GROUP  BY p.patient_id) max_seguimento "
            + "                       INNER JOIN obs o "
            + "                               ON o.person_id = max_seguimento.patient_id "
            + "                WHERE  max_seguimento.encounter_datetime = o.obs_datetime "
            + "                       AND o.voided = 0 "
            + "                       AND o.concept_id = %d "
            + "                       AND o.location_id = :location "
            + "                UNION "
            + "                SELECT pg.patient_id, "
            + "                       date_enrolled data_consulta, "
            + "                       date_enrolled data_proxima "
            + "                FROM   patient p "
            + "                       INNER JOIN patient_program pg "
            + "                               ON p.patient_id = pg.patient_id "
            + "                       INNER JOIN patient_state ps "
            + "                               ON pg.patient_program_id = ps.patient_program_id "
            + "                WHERE  pg.voided = 0 "
            + "                       AND p.voided = 0 "
            + "                       AND program_id = %d "
            + "                       AND pg.date_enrolled = ps.start_date "
            + "                       AND ps.voided = 0 "
            + "                       AND date_enrolled >= :startDate "
            + "                       AND date_enrolled <= :endDate "
            + "                       AND location_id = :location "
            + "                       AND ps.state IN ( %d, %d )) seguimento "
            + "        GROUP  BY patient_id) ultima_consulta "
            + "WHERE  patient_id NOT IN (SELECT pg.patient_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN patient_program pg "
            + "                                         ON p.patient_id = pg.patient_id "
            + "                                 INNER JOIN patient_state ps "
            + "                                         ON "
            + "                                 pg.patient_program_id = ps.patient_program_id "
            + "                          WHERE  pg.voided = 0 "
            + "                                 AND ps.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                 AND pg.program_id = %d "
            + "                                 AND ps.state IN ( %d, %d, %d ) "
            + "                                 AND ps.end_date IS NULL "
            + "                                 AND ps.start_date <= :endDate "
            + "                                 AND location_id = :location "
            + "                          UNION "
            + "                          SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e "
            + "                                         ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o "
            + "                                         ON o.encounter_id = e.encounter_id "
            + "                          WHERE  e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                 AND e.encounter_type IN ( %d, %d, %d ) "
            + "                                 AND o.concept_id = %d "
            + "                                 AND o.value_coded = %d "
            + "                                 AND e.encounter_datetime <= :endDate "
            + "                                 AND e.location_id = :location "
            + "                          UNION "
            + "                          SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e "
            + "                                         ON p.patient_id = e.patient_id "
            + "                          WHERE  e.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                 AND e.encounter_type = %d "
            + "                                 AND e.encounter_datetime <= :endDate "
            + "                                 AND e.location_id = :location "
            + "                          UNION "
            + "                          SELECT p.patient_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e "
            + "                                         ON p.patient_id = e.patient_id "
            + "                                 INNER JOIN obs o "
            + "                                         ON e.encounter_id = o.encounter_id "
            + "                          WHERE  p.voided = 0 "
            + "                                 AND e.voided = 0 "
            + "                                 AND o.voided = 0 "
            + "                                 AND e.encounter_type IN ( %d, %d, %d ) "
            + "                                 AND o.concept_id = %d "
            + "                                 AND o.value_datetime IS NOT NULL "
            + "                                 AND o.value_datetime <= :endDate "
            + "                                 AND e.location_id = :location "
            + "                          UNION "
            + "                          SELECT pg.patient_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN patient_program pg "
            + "                                         ON p.patient_id = pg.patient_id "
            + "                                 INNER JOIN patient_state ps "
            + "                                         ON "
            + "                                 pg.patient_program_id = ps.patient_program_id "
            + "                          WHERE  pg.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                 AND program_id = %d "
            + "                                 AND pg.date_enrolled = ps.start_date "
            + "                                 AND ps.voided = 0 "
            + "                                 AND date_enrolled <= :endDate "
            + "                                 AND location_id = :location "
            + "                                 AND ps.state IN ( %d, %d ) "
            + "                          UNION "
            + "                          SELECT person_id "
            + "                          FROM   patient p "
            + "                                 INNER JOIN person pe "
            + "                                         ON pe.person_id = p.patient_id "
            + "                          WHERE  pe.voided = 0 "
            + "                                 AND p.voided = 0 "
            + "                                 AND pe.dead = 1 "
            + "                                 AND pe.death_date <= :endDate) "
            + "       AND Datediff(:endDate, data_proxima) > 60; ";

    cd.setQuery(
        String.format(
            query,
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getArtCareAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVAdultInitialBEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialBEncounterType().getEncounterTypeId(),
            hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getArtAconselhamentoEncounterType().getEncounterTypeId(),
            hivMetadata.getArtAconselhamentoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getEvaluationAndPrepForARTEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata
                .getArtCareTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getArtCareActiveOnProgramWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata.getArtCareAbandonedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata
                .getArtCareTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getArtCareDeadWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getArtActiveOnProgramWorkflowState().getProgramWorkflowStateId(),
            hivMetadata
                .getArtTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));

    return cd;
  }

  private CohortDefinition abandonmentNotifiedAndNotNotified() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("ABANDONO AO TRATAMENTO ARV (ABANDONO NOTIFICADO E NAO NOTIFICADO)");

    cd.addParameter(ReportingConstants.END_DATE_PARAMETER);
    cd.addParameter(ReportingConstants.LOCATION_PARAMETER);

    CohortDefinition notified = hivCohortQueries.getPatientsInArtWhoAbandoned();
    cd.addSearch("NOTIFICADO", mapStraightThrough(notified));

    CohortDefinition notNotified = getPatientsInArtWhoAbandonedWithNoNotification();
    cd.addSearch("NAONOTIFICADO", mapStraightThrough(notNotified));

    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");
    return cd;
  }

  private CohortDefinition getDeadDuringArtCareProgram() {
    CohortDefinition cd = hivCohortQueries.getPatientsInArtCareWhoDied();
    cd.setName(
        "PROGRAMA: PACIENTES QUE SAIRAM DO PROGRAMA DE CUIDADO (PRE-TARV) - OBITO: PERIODO FINAL");
    return cd;
  }

  /**
   * @return Pacientes que iniciaram o tratamento ARV (Na unidade sanitaria seleccionada). São
   *     inclusos os transferidos de com data de inicio conhecida
   */
  private CohortDefinition getInitiatedIncludingTransfers() {
    // TODO use StartedArtOnPeriodCalculation
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "INICIO DE TRATAMENTO ARV - NUM PERIODO: INCLUI TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)");
    cd.addParameters(getParameters());
    String query =
        "SELECT patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Min(data_inicio) data_inicio "
            + "        FROM   (SELECT p.patient_id, "
            + "                       Min(e.encounter_datetime) data_inicio "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o "
            + "                               ON o.encounter_id = e.encounter_id "
            + "                WHERE  e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND p.voided = 0 "
            + "                       AND e.encounter_type IN ( %d, %d, %d ) "
            + "                       AND o.concept_id = %d "
            + "                       AND o.value_coded = %d "
            + "                       AND e.encounter_datetime <= :endDate "
            + "                       AND e.location_id = :location "
            + "                GROUP  BY p.patient_id "
            + "                UNION "
            + "                SELECT p.patient_id, "
            + "                       Min(value_datetime) data_inicio "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       INNER JOIN obs o "
            + "                               ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type IN ( %d, %d, %d ) "
            + "                       AND o.concept_id = %d "
            + "                       AND o.value_datetime IS NOT NULL "
            + "                       AND o.value_datetime <= :endDate "
            + "                       AND e.location_id = :location "
            + "                GROUP  BY p.patient_id "
            + "                UNION "
            + "                SELECT pg.patient_id, "
            + "                       date_enrolled data_inicio "
            + "                FROM   patient p "
            + "                       INNER JOIN patient_program pg "
            + "                               ON p.patient_id = pg.patient_id "
            + "                WHERE  pg.voided = 0 "
            + "                       AND p.voided = 0 "
            + "                       AND program_id = %d "
            + "                       AND date_enrolled <= :endDate "
            + "                       AND location_id = :location "
            + "                UNION "
            + "                SELECT e.patient_id, "
            + "                       Min(e.encounter_datetime) AS data_inicio "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.encounter_type = %d "
            + "                       AND e.voided = 0 "
            + "                       AND e.encounter_datetime <= :endDate "
            + "                       AND e.location_id = :location "
            + "                GROUP  BY p.patient_id) inicio_real "
            + "        GROUP  BY patient_id)inicio "
            + "WHERE  data_inicio BETWEEN :startDate AND :endDate ";
    cd.setQuery(
        String.format(
            query,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPlanConcept().getConceptId(),
            hivMetadata.getStartDrugsConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId()));
    return cd;
  }
}
