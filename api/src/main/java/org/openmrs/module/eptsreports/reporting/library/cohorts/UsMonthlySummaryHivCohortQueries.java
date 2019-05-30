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
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
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
    mappings.put("startDate", DateUtil.getDateTime(2012, 3, 21));
    mappings.put("endDate", "${endDate}");
    mappings.put("location", "${location}");
    cd.addSearch("INSCRITOS", getEnrolled(), mappings);
    cd.addSearch("CONSULTA", hasFollowUpConsultation(), mappings);
    cd.addSearch("TRANSFERIDODE", getInArtCareEnrolledByTransfer(), mappings);
    cd.addSearch("ENTRADAPRETARV", hasEntryInPreArt(), mappings);

    Map<String, Object> mappings2 = new HashMap<>();
    mappings2.put("onOrBefore", DateUtil.getDateTime(2012, 3, 20));
    mappings2.put("locationList", "${location}");
    cd.addSearch("INSCRITOFINAL", hasInitialEncounter(), mappings2);

    Map<String, Object> mappings1 = new HashMap<>();
    mappings1.put("endDate", DateUtil.getDateTime(2012, 3, 20));
    mappings1.put("location", "${location}");
    cd.addSearch("ALGUMAVEZTARV", getEverOnART(), mappings1);

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

  private CohortDefinition getEverOnART() {
    // TODO use txTbCohortQueries after fix
    // return txTbCohortQueries.anyTimeARVTreatmentFinalPeriod();
    CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
    CohortDefinition cd =
        service.getDefinition(
            "a9043c4d-47f7-4e8d-96c7-4ff8389999e3", CompositionCohortDefinition.class);
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  private List<Parameter> getParameters() {
    return Arrays.asList(
        ReportingConstants.START_DATE_PARAMETER,
        ReportingConstants.END_DATE_PARAMETER,
        ReportingConstants.LOCATION_PARAMETER);
  }

  /**
   * @return Pacientes inscritos no servico tarv em um periodo, sem incluir os transferidos de (TARV
   *     e NAO TARV)
   */
  private CohortDefinition getEnrolled() {
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

  public CohortDefinition getRegisteredInArtBooks() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordArtFlowConcept(),
        TimeModifier.FIRST,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getArtBookEncounterType()),
        null);
  }

  public CohortDefinition getNewlyEnrolledInPreArtBooks() {
    String mappings = "endDate=${onOrBefore},location=${location}";
    Mapped<CohortDefinition> transferredFrom =
        map(hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility(), mappings);
    Mapped<CohortDefinition> preArtBooks1And2 =
        map(getRegisteredInPreArtByEndOfPreviousMonth(), mappings);
    return getNewlyEnrolledInArtBookExcludingTransfers(preArtBooks1And2, transferredFrom);
  }

  public CohortDefinition getNewlyEnrolledInArt() {
    Mapped<CohortDefinition> transferredFrom =
        mapStraightThrough(hivCohortQueries.getPatientsTransferredFromOtherHealthFacility());
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> artBook1 = map(registeredInArtBook1(), mappings);
    return getNewlyEnrolledInArtBookExcludingTransfers(artBook1, transferredFrom);
  }

  /**
   * @return Pacientes que entraram no programa de cuidado pre-tarv vindos transferidos de - num
   *     periodo
   */
  public CohortDefinition getInArtCareEnrolledByTransfer() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
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
    cd.setQuery(
        String.format(
            query,
            hivMetadata.getHIVCareProgram().getProgramId(),
            hivMetadata
                .getArtCareTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    cd.setName(
        "PROGRAMA: PACIENTES TRANSFERIDOS DE NO PROGRAMA DE CUIDADO (PRE-TARV): NUM PERIODO");
    return cd;
  }

  /**
   * @return Pacientes que entraram no programa de tratamento ARV num periodo vindos transferidos de
   */
  public CohortDefinition getInArtEnrolledByTransfer() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
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
    cd.setQuery(
        String.format(
            query,
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId()));
    cd.setName("PROGRAMA: PACIENTES TRANSFERIDOS DE NO PROGRAMA DE TRATAMENTO ARV: NUM PERIODO");
    return cd;
  }

  public CohortDefinition getTransferredOut() {
    return hivCohortQueries.getPatientsInArtCareTransferredOutToAnotherHealthFacility();
  }

  public CohortDefinition getRegisteredInPreArtOrArtBooks() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("registeredInPreArtOrArtBooks");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrBefore=${onOrBefore},locationList=${location}";
    String mappings1 = "endDate=${onOrBefore},location=${location}";
    cd.addSearch("LIVROPRETARV", map(getRegisteredInPreArtByEndOfPreviousMonth(), mappings1));
    cd.addSearch("LIVROTARV", map(getRegisteredInArt(), mappings));

    cd.setCompositionString("LIVROPRETARV OR LIVROTARV");

    return cd;
  }

  public CohortDefinition getInPreArtWhoScreenedForTb() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForTb = map(getTbScreening(), mappings);
    Mapped<CohortDefinition> inPreArtBook1 = map(registeredInPreArtBook1(), mappings);
    return getEnrolledInArtBookAnd(inPreArtBook1, screenedForTb);
  }

  public CohortDefinition getInPreArtWhoScreenedForSti() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForSti = map(getStiScreening(), mappings);
    Mapped<CohortDefinition> inPreArtBook1 = map(registeredInPreArtBook1(), mappings);
    return getEnrolledInArtBookAnd(inPreArtBook1, screenedForSti);
  }

  public CohortDefinition getInPreArtWhoStartedCotrimoxazoleProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis =
        map(getStartedCotrimoxazoleProphylaxis(), mappings);
    String artBookMappings =
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> inPreArtBook1 = map(registeredInPreArtBook1(), artBookMappings);
    return getEnrolledInArtBookAnd(inPreArtBook1, startedProphylaxis);
  }

  public CohortDefinition getInPreArtWhoStartedIsoniazidProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis = map(getStartedIsoniazidProphylaxis(), mappings);
    String artBookMappings =
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> inPreArtBook1 = map(registeredInPreArtBook1(), artBookMappings);
    return getEnrolledInArtBookAnd(inPreArtBook1, startedProphylaxis);
  }

  public CohortDefinition getInArtWhoScreenedForTb() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForTb = map(getTbScreening(), mappings);
    Mapped<CohortDefinition> inArtBook1 = map(registeredInArtBook1(), mappings);
    return getEnrolledInArtBookAnd(inArtBook1, screenedForTb);
  }

  public CohortDefinition getInArtWhoScreenedForSti() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForSti = map(getStiScreening(), mappings);
    Mapped<CohortDefinition> inArtBook1 = map(registeredInArtBook1(), mappings);
    return getEnrolledInArtBookAnd(inArtBook1, screenedForSti);
  }

  public CohortDefinition getArtWhoStartedCotrimoxazoleProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis =
        map(getStartedCotrimoxazoleProphylaxis(), mappings);
    String artBookMappings =
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> inArtBook1 = map(registeredInArtBook1(), artBookMappings);
    return getEnrolledInArtBookAnd(inArtBook1, startedProphylaxis);
  }

  public CohortDefinition getInArtWhoStartedIsoniazidProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis = map(getStartedIsoniazidProphylaxis(), mappings);
    String artBookMappings =
        "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> inArtBook1 = map(registeredInArtBook1(), artBookMappings);
    return getEnrolledInArtBookAnd(inArtBook1, startedProphylaxis);
  }

  public CohortDefinition getAbandonedArtCare() {
    return hivCohortQueries.getPatientsInArtCareWhoAbandoned();
  }

  public CohortDefinition getDeadDuringArtCare() {
    return hivCohortQueries.getPatientsInArtCareWhoDied();
  }

  public CohortDefinition getInArtCareWhoInitiatedArt() {
    return hivCohortQueries.getPatientsInArtCareWhoInitiatedArt();
  }

  public CohortDefinition getInArtWhoSuspendedTreatment() {
    return hivCohortQueries.getPatientsInArtWhoSuspendedTreatment();
  }

  public CohortDefinition getInArtTransferredOut() {
    return hivCohortQueries.getPatientsInArtTransferredOutToAnotherHealthFacility();
  }

  public CohortDefinition getDeadDuringArt() {
    return hivCohortQueries.getPatientsInArtWhoDied();
  }

  public CohortDefinition getAbandonedArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("abandonedArt");

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "endDate=${onOrBefore},location=${location}";
    cd.addSearch("NOTIFICADO", map(hivCohortQueries.getPatientsInArtWhoAbandoned(), mappings));
    cd.addSearch("NAONOTIFICADO", map(getPatientsInArtWhoAbandonedWithNoNotification(), mappings));

    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");

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

  private CohortDefinition getPatientsInArtWhoAbandonedWithNoNotification() {
    Concept returnVisitDateForArvDrug = hivMetadata.getReturnVisitDateForArvDrugConcept();
    EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType arvPediatriaSeguimento = hivMetadata.getARVPediatriaSeguimentoEncounterType();
    ProgramWorkflowState transferredOut =
        hivMetadata.getTransferredOutToAnotherHealthFacilityWorkflowState();

    TXTBQueries.AbandonedWithoutNotificationParams params =
        new TXTBQueries.AbandonedWithoutNotificationParams();

    params
        .programId(hivMetadata.getARTProgram().getProgramId())
        .returnVisitDateConceptId(hivMetadata.getReturnVisitDateConcept().getConceptId())
        .returnVisitDateForARVDrugConceptId(returnVisitDateForArvDrug.getConceptId())
        .pharmacyEncounterTypeId(hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId())
        .artAdultFollowupEncounterTypeId(adultoSeguimento.getEncounterTypeId())
        .artPedInicioEncounterTypeId(arvPediatriaSeguimento.getEncounterTypeId())
        .transferOutStateId(transferredOut.getId())
        .treatmentSuspensionStateId(hivMetadata.getSuspendedTreatmentWorkflowState().getId())
        .treatmentAbandonedStateId(hivMetadata.getAbandonedWorkflowState().getId())
        .deathStateId(hivMetadata.getPatientHasDiedWorkflowState().getId());

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsInArtWhoAbandonedWithNoNotification");
    cd.setQuery(TXTBQueries.abandonedWithNoNotification(params));

    cd.addParameter(new Parameter("endDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  private CohortDefinition getEnrolledByTransfer(
      Mapped<CohortDefinition> enrolled, Mapped<CohortDefinition> transferredFrom) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("enrolledByTransfer");

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("INSCRITOS", enrolled);
    cd.addSearch("TRANSFERIDOS", transferredFrom);

    cd.setCompositionString("INSCRITOS AND TRANSFERIDOS");

    return cd;
  }

  /**
   * @param artBook Cohort of patients registered in one of the ART books
   * @param toCompose Mapped cohort of screened patients. Parameters to map are {@code onOrBefore,
   *     onOrAfter} and {@code location}
   * @return Composition cohort of patients who are registered in pre ART Book 1 composed with
   *     {@code toCompose} param using an 'AND' operator.
   */
  private CohortDefinition getEnrolledInArtBookAnd(
      Mapped<CohortDefinition> artBook, Mapped<CohortDefinition> toCompose) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("enrolledInArtBookAnd");

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}";
    Mapped<CohortDefinition> transferredFrom =
        map(hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility(), mappings);

    CohortDefinition newInArtCare =
        getNewlyEnrolledInArtBookExcludingTransfers(artBook, transferredFrom);

    cd.addSearch("INSCRITOS", mapStraightThrough(newInArtCare));
    cd.addSearch("COMPOSE", toCompose);

    cd.setCompositionString("INSCRITOS AND COMPOSE");

    return cd;
  }

  /**
   * @param artBook Cohort of patients registered in one of the ART books
   * @param transferredFrom Cohort of patients transferred from another facility
   * @return Patients enrolled in ART excluding those transferred in
   */
  private CohortDefinition getNewlyEnrolledInArtBookExcludingTransfers(
      Mapped<CohortDefinition> artBook, Mapped<CohortDefinition> transferredFrom) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("newlyEnrolledInArtBookExcludingTransfers");

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("LIVRO", artBook);
    cd.addSearch("TRANSFERIDOSDE", transferredFrom);

    cd.setCompositionString("LIVRO NOT TRANSFERIDOSDE");

    return cd;
  }

  private CohortDefinition getRegisteredInArt() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordArtFlowConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getArtBookEncounterType()),
        null);
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        values);
  }

  private CohortDefinition getStiScreening() {
    List<Concept> values =
        Arrays.asList(commonMetadata.getNoConcept(), commonMetadata.getYesConcept());
    return genericCohortQueries.hasCodedObs(
        commonMetadata.getStiScreeningConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        values);
  }

  private CohortDefinition registeredInPreArtBook1() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordPreArtFlowConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getPreArtBookEncounterType()),
        Arrays.asList(hivMetadata.getPreArtBook1Concept()));
  }

  private CohortDefinition registeredInArtBook1() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordArtFlowConcept(),
        TimeModifier.FIRST,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getArtBookEncounterType()),
        Arrays.asList(hivMetadata.getArtBook1Concept()));
  }

  private CohortDefinition getStartedCotrimoxazoleProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("startedCotrimoxazoleProphylaxis");
    cd.setQuestion(commonMetadata.getCotrimoxazoleProphylaxisStartDateConcept());
    cd.setTimeModifier(TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getARVPediatriaSeguimentoEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  private CohortDefinition getStartedIsoniazidProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("startedIsoniazidProphylaxis");
    cd.setQuestion(commonMetadata.getIsoniazidProphylaxisStartDateConcept());
    cd.setTimeModifier(TimeModifier.FIRST);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getARVPediatriaSeguimentoEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }
}
