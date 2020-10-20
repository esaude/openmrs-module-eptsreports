package org.openmrs.module.eptsreports.reporting.library.queries;

public class UsMonthlySummaryQueries {
  private static final String inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDateQuery =
      "-- Encontrar os respectivos pacientes para cada estado  \n"
          + "SELECT patient_id  \n"
          + "FROM  \n"
          + "-- 1. Aqui determina-se a data que serÃ¡ usada para ser comparada com a data final  \n"
          + "-- 1.1 Se o paciente nÃ£o tiver data do proximo levantamento e tiver a data da proxima consulta, esta serÃ¡ usada, o resto dos casos a data do proximo levantamento e usado. \n"
          + "-- 1.2 Se o paciente tiver a consulta que lhe poe na situacao de activo, enquanto tem um fila com a data de proxima preenchida que lhe poe na situacao de abandono, este sera considerado abandono (Missed drug pickup) \n"
          + "-- 2. Determinacao do estado final  \n"
          + "-- 2.1 Se o paciente tiver um estado preenchido (Transferido para, suspenso, abandono, obito) , este serÃ¡ usado como estado final \n"
          + "-- 2.2 Se o paciente nÃ£o tiver nenhuma data no ponto 1 (paciente sem nenhum levantamento ou consulta clinica ) serÃ¡ considerado ABANDONO \n"
          + "-- 2.2 Se a data final for maior que a data encontrada em 1 serÃ¡ considerado abandono caso nÃ£o serÃ¡ activo  \n"
          + "(SELECT inicio_fila_seg_prox.*,  \n"
          + "        /*if (estado_id_real is not null,estado_id_real,  \n"
          + "          if(if(data_proximo_lev is null and data_proximo_seguimento is not null,data_proximo_seguimento,data_proximo_lev) is null,9,     \n"
          + "          if(:endDate>date_add(if(data_proximo_lev is null and data_proximo_seguimento is not null,data_proximo_seguimento,data_proximo_lev), interval 30 day),9,6))) estado_final*/ \n"
          + "        IF (estado_id_real IS NOT NULL, estado_id_real, IF(  \n"
          + "                                                        IF(  \n"
          + "        data_proximo_lev IS NULL  \n"
          + "        AND  \n"
          + "        data_proximo_seguimento IS NOT NULL, data_proximo_seguimento,  \n"
          + "                                                        data_proximo_lev  \n"
          + "                                                        ) IS NULL, 9, IF(  \n"
          + "        :endDate > Date_add(IF(  \n"
          + "                   data_proximo_lev IS  \n"
          + "                   NULL  \n"
          + "                   AND data_proximo_seguimento IS NOT NULL,  \n"
          + "                                 data_proximo_seguimento,  \n"
          + "                            IF  \n"
          + "                                                        (  \n"
          + "                                          data_proximo_lev IS NOT  \n"
          + "                                          NULL  \n"
          + "                                          AND  \n"
          + "                            data_proximo_seguimento IS NULL,  \n"
          + "                            data_proximo_lev,  \n"
          + "                            IF(  \n"
          + "                                        data_proximo_lev >  \n"
          + "                                        data_proximo_seguimento,  \n"
          + "                                          data_proximo_lev,  \n"
          + "                                          data_proximo_seguimento  \n"
          + "                                          ))), interval 30 day), 9,  \n"
          + "                   6))) estado_final  \n"
          + " FROM  \n"
          + "-- Aqui construimos uma tabela com informacao ultimo fila, seguimento e as datas proximas de fila e seguimento \n"
          + "-- Repare que para as Data do proximo levantamento ou seguimento serÃ£o adicionados +60 dias o que ira corresponder a definicao acordada de 99 e 150 para determinacao de ABANDONO \n"
          + "(SELECT inicio_fila_seg.*,  \n"
          + "        obs_fila.value_datetime       data_proximo_lev,  \n"
          + "        obs_seguimento.value_datetime data_proximo_seguimento,  \n"
          + "        --    inicio_fila_seg.estado_id estado_id_real  \n"
          + "        --    if(inicio_fila_seg.data_estado  \n"
          + "        inicio_fila_seg.estado_id     estado_id_real  \n"
          + " FROM  \n"
          + "-- Aqui construimos uma tabela que tem os campos: data de inicio de tarv do paciente, data do estado, estado, \n"
          + "-- data do ultimo levantamento, data da ultima consulta  \n"
          + "(SELECT inicio.*,  \n"
          + "        saida.estado,  \n"
          + "        saida.data_estado,  \n"
          + "        saida.estado_id,  \n"
          + "        max_fila.data_fila,  \n"
          + "        max_consulta.data_seguimento  \n"
          + " FROM  \n"
          + "-- A real data de inicio de TARV do paciente Ã© a minima das primeiras ocorrencias dos conceitos de Gestao de TARV, data de inicio de TARV, inscricao no programa de tratamento e fila inicial \n"
          + "(SELECT patient_id,  \n"
          + "        Min(data_inicio) data_inicio  \n"
          + " FROM   (  \n"
          + "        -- leva a primeira ocorrencia do conceito 1255: GestÃ£o de TARV e que a resposta foi 1256: Inicio  \n"
          + "        SELECT p.patient_id,  \n"
          + "               Min(e.encounter_datetime) data_inicio  \n"
          + "        FROM   patient p  \n"
          + "               inner join encounter e  \n"
          + "                       ON p.patient_id = e.patient_id  \n"
          + "               inner join obs o  \n"
          + "                       ON o.encounter_id = e.encounter_id  \n"
          + "        WHERE  e.voided = 0  \n"
          + "               AND o.voided = 0  \n"
          + "               AND p.voided = 0  \n"
          + "               AND e.encounter_type IN ( 18, 6, 9 )  \n"
          + "               AND o.concept_id = 1255  \n"
          + "               AND o.value_coded = 1256  \n"
          + "               AND e.encounter_datetime <= :endDate  \n"
          + "               AND e.location_id = :location  \n"
          + "        GROUP  BY p.patient_id  \n"
          + "        UNION  \n"
          + "        -- leva a primeira ocorrencia do conceito 1190: Data de Inicio de TARV  \n"
          + "        SELECT p.patient_id,  \n"
          + "               Min(value_datetime) data_inicio  \n"
          + "        FROM   patient p  \n"
          + "               inner join encounter e  \n"
          + "                       ON p.patient_id = e.patient_id  \n"
          + "               inner join obs o  \n"
          + "                       ON e.encounter_id = o.encounter_id  \n"
          + "        WHERE  p.voided = 0  \n"
          + "               AND e.voided = 0  \n"
          + "               AND o.voided = 0  \n"
          + "               AND e.encounter_type IN ( 18, 6, 9 )  \n"
          + "               AND o.concept_id = 1190  \n"
          + "               AND o.value_datetime IS NOT NULL  \n"
          + "               AND o.value_datetime <= :endDate  \n"
          + "               AND e.location_id = :location  \n"
          + "        GROUP  BY p.patient_id  \n"
          + "        UNION  \n"
          + "        -- leva a primeira ocorrencia da inscricao do paciente no programa de Tratamento ARV  \n"
          + "        SELECT pg.patient_id,  \n"
          + "               date_enrolled data_inicio  \n"
          + "        FROM   patient p  \n"
          + "               inner join patient_program pg  \n"
          + "                       ON p.patient_id = pg.patient_id  \n"
          + "        WHERE  pg.voided = 0  \n"
          + "               AND p.voided = 0  \n"
          + "               AND program_id = 2  \n"
          + "               AND date_enrolled <= :endDate  \n"
          + "               AND pg.location_id = :location  \n"
          + "         UNION  \n"
          + "         -- Leva a data do primeiro levantamento de ARV para cada paciente: Data do primeiro Fila do paciente \n"
          + "         SELECT e.patient_id,  \n"
          + "                Min(e.encounter_datetime) AS data_inicio  \n"
          + "         FROM   patient p  \n"
          + "                inner join encounter e  \n"
          + "                        ON p.patient_id = e.patient_id  \n"
          + "         WHERE  p.voided = 0  \n"
          + "                AND e.encounter_type = 18  \n"
          + "                AND e.voided = 0  \n"
          + "                AND e.encounter_datetime <= :endDate  \n"
          + "                AND e.location_id = :location  \n"
          + "         GROUP  BY p.patient_id) inicio_real  \n"
          + " GROUP  BY patient_id)inicio  \n"
          + "-- Aqui encontramos os estado do paciente ate a data final  \n"
          + "left join (SELECT pg.patient_id,  \n"
          + "                  CASE ps.state  \n"
          + "                    WHEN 7 THEN 'TRANSFERIDO PARA'  \n"
          + "                    WHEN 8 THEN 'SUSPENSO'  \n"
          + "                    WHEN 9 THEN 'ABANDONO'  \n"
          + "                    WHEN 10 THEN 'OBITO'  \n"
          + "                  END           AS estado,  \n"
          + "                  ps.state      estado_id,  \n"
          + "                  ps.start_date data_estado  \n"
          + "           FROM   patient p  \n"
          + "                  inner join patient_program pg  \n"
          + "                          ON p.patient_id = pg.patient_id  \n"
          + "                  inner join patient_state ps  \n"
          + "                          ON pg.patient_program_id = ps.patient_program_id  \n"
          + "           WHERE  pg.voided = 0  \n"
          + "                  AND ps.voided = 0  \n"
          + "                  AND p.voided = 0  \n"
          + "                  AND pg.program_id = 2  \n"
          + "                  AND ps.state IN ( 7, 8, 9, 10 )  \n"
          + "                  AND ps.end_date IS NULL  \n"
          + "                  AND ps.start_date <= :endDate  \n"
          + "                  AND location_id = :location) saida  \n"
          + "       ON inicio.patient_id = saida.patient_id  \n"
          + "-- Aqui encontramos a data do ultimo levantamento de ARV do paciente   \n"
          + "left join (SELECT p.patient_id,  \n"
          + "                  Max(encounter_datetime) data_fila  \n"
          + "           FROM   patient p  \n"
          + "                  inner join encounter e  \n"
          + "                          ON e.patient_id = p.patient_id  \n"
          + "           WHERE  p.voided = 0  \n"
          + "                  AND e.voided = 0  \n"
          + "                  AND e.encounter_type = 18  \n"
          + "                  AND e.location_id = :location  \n"
          + "                  AND e.encounter_datetime <= :endDate  \n"
          + "           GROUP  BY p.patient_id) max_fila  \n"
          + "       ON inicio.patient_id = max_fila.patient_id  \n"
          + "-- Aqui encontramos a data da ultima consulta clinica do paciente  \n"
          + "left join (SELECT p.patient_id,  \n"
          + "                  Max(encounter_datetime) data_seguimento  \n"
          + "           FROM   patient p  \n"
          + "                  inner join encounter e  \n"
          + "                          ON e.patient_id = p.patient_id  \n"
          + "           WHERE  p.voided = 0  \n"
          + "                  AND e.voided = 0  \n"
          + "                  AND e.encounter_type IN ( 6, 9 )  \n"
          + "                  AND e.location_id = :location  \n"
          + "                  AND e.encounter_datetime <= :endDate  \n"
          + "           GROUP  BY p.patient_id) max_consulta  \n"
          + "       ON inicio.patient_id = max_consulta.patient_id  \n"
          + " GROUP  BY inicio.patient_id) inicio_fila_seg  \n"
          + "-- Aqui encontramos a data do proximo levantamento marcado no ultimo levantamento de ARV  \n"
          + "left join obs obs_fila  \n"
          + "       ON obs_fila.person_id = inicio_fila_seg.patient_id  \n"
          + "          AND obs_fila.voided = 0  \n"
          + "          AND obs_fila.obs_datetime = inicio_fila_seg.data_fila  \n"
          + "          AND obs_fila.concept_id = 5096  \n"
          + "          AND obs_fila.location_id = :location  \n"
          + "-- Aqui encontramos a data da proxima consulta marcada na ultima consulta  \n"
          + "left join obs obs_seguimento  \n"
          + "       ON obs_seguimento.person_id = inicio_fila_seg.patient_id  \n"
          + "          AND obs_seguimento.voided = 0  \n"
          + "          AND obs_seguimento.obs_datetime = inicio_fila_seg.data_seguimento  \n"
          + "          AND obs_seguimento.concept_id = 1410  \n"
          + "          AND obs_seguimento.location_id = :location) inicio_fila_seg_prox  \n"
          + " GROUP  BY patient_id) coorte12meses_final  \n"
          + "-- VerificaÃ§Ã£o qual Ã© o estado final  \n"
          + "WHERE  estado_final = 6 \n";

  /** APDF1: PDF FORMADOS E ACTIVOS ATE UMA DATA FINAL */
  public static String pdfFormatAssetAtFinalDate() {
    return "select family_id from gaac_family where start_date<:endDate and voided=0 "
        + "and ((crumbled is null) or (crumbled=0) or (crumbled=1 and date_crumbled>:endDate)) and location_id=:location";
  }

  /** APDF2: PDFS FORMADOS NUM DETERMINADO PERIODO */
  public static String pdfFormatAssetWithinReportingPeriod() {
    return "select family_id from gaac_family where start_date between :startDate and :endDate and voided=0 and location_id=:location";
  }

  /** APDF3: PDFs DESINTEGRADOS NUM PERIODO */
  public static String disaggregatedPdfFormatAssetWithinReportingPeriod() {
    return "select family_id from gaac_family where start_date<=:endDate and crumbled=1 "
        + "and date_crumbled between :startDate and :endDate and voided=0 and location_id=:location";
  }

  /** APDF4: PDF FORMADOS E ACTIVOS COM CRIANÃ‡A ATE UMA DATA FINAL */
  public static String pdfFormatAssetAtFinalDateForChildren() {
    return "select family_id from gaac_family where start_date<:endDate and voided=0 "
        + "and ((crumbled is null) or (crumbled=0) or (crumbled=1 and date_crumbled>:endDate)) and location_id=:location and family_id in "
        + "(SELECT family_id FROM person p  inner join gaac_family_member ON member_id = person_id where round(datediff(:endDate,birthdate)/360) <=14 )";
  }

  /** APDF5: PDFS FORMADOS NUM DETERMINADO PERIODO CONTENDO CRIANÃ‡A */
  public static String pdfFormatAssetWithinReportingPeriodForChildren() {
    return "select family_id from gaac_family where start_date between :startDate and :endDate and voided=0 and location_id=:location and family_id in "
        + "(SELECT family_id FROM person p  inner join gaac_family_member ON member_id = person_id where round(datediff(:endDate,birthdate)/360) <=14)";
  }

  /** APDF6: PDFs DESINTEGRADOS NUM PERIODO CONTENDO CRIANÃ‡A */
  public static String disaggregatedPdfFormatAssetWithinReportingPeriodForChildren() {
    return "select family_id from gaac_family where start_date<=:endDate and crumbled=1 and date_crumbled between :startDate and :endDate and voided=0 and location_id=:location and family_id in "
        + "(SELECT family_id FROM person p  inner join gaac_family_member ON member_id = person_id where round(datediff(:endDate,birthdate)/360) <=14 )";
  }

  /**
   * B1PDF: PACIENTES ACTIVOS NO PDF ATÃ‰ UM DETERMINADO PERIODO FINAL; B4PDF_PDF: ACTUALMENTE EM
   * TARV ATÃ‰ UM DETERMINADO PERIODO FINAL - SEM INCLUIR ABANDONOS NAO NOTIFICADOS EM 4 SEMANAS -
   * REAL (SQL)
   */
  public static String activePatientsToEndDate() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id = fm.family_id where fm.start_date<:endDate and fm.voided=0 and f.voided=0 and "
        + "((leaving is null) or (leaving=0) or (leaving=1 and fm.end_date>:endDate)) and location_id=:location ";
  }

  /** B2PDF: PACIENTES INSCRITOS NO PDF NUM PERIODO */
  public static String enrolledInReportingPeriod() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id where fm.start_date between :startDate "
        + "and :endDate and fm.voided=0 and f.voided=0 and location_id=:location";
  }

  /** B3PDF: PACIENTES QUE RETORNARAM AO PDF NUM PERIODO */
  public static String returnedToPDFInReportingPeriod() {
    return "Select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id "
        + "where fm.restart_date between :startDate and :endDate and fm.restart=1 and fm.voided=0 and f.voided=0 and location_id=:location";
  }

  /**
   * B1PDF, B4PDF_TARVACTUAL: ACTUALMENTE EM TARV ATÃ‰ UM DETERMINADO PERIODO FINAL - SEM INCLUIR
   * ABANDONOS NAO NOTIFICADOS EM 4 SEMANAS - REAL (SQL)
   */
  public static String inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDate() {
    return inARTExcludingNotAnsweredAbandonedIn4WeeksUntilEndDateQuery;
  }

  /** B5PDF: PACIENTES QUE SAIRAM DO PDF - TRANSFERIDOS PARA */
  public static String exitFromPdfTransferredTo() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id where fm.end_date between :startDate and :endDate and fm.leaving=1 and  "
        + "fm.voided=0 and f.voided=0 and location_id=:location and reason_leaving_type=1;";
  }

  /** B6PDF: PACIENTES QUE SAIRAM DO PDF - RETIRADOS */
  public static String comeOutOfPdfRemoved() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id where "
        + "fm.end_date between :startDate and :endDate and fm.leaving=1 and  fm.voided=0 and f.voided=0 and location_id=:location and reason_leaving_type in (2,4,5);";
  }

  /** B7PDF: PACIENTES QUE SAIRAM DO PDF - OBITOS */
  public static String comeOutOfPdfObitos() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id "
        + "where fm.end_date between :startDate and :endDate and fm.leaving=1 and  fm.voided=0 and f.voided=0 and location_id=:location and reason_leaving_type = 3;";
  }

  /** B8PDF: PACIENTES QUE SAIRAM DO PDF - SUSPENSO */
  public static String comeOutOfPdfSuspension() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id "
        + "where fm.end_date between :startDate and :endDate and fm.leaving=1 and fm.voided=0 and f.voided=0 and location_id=:location and reason_leaving_type = 4;";
  }

  /** C1PDF: PACIENTES PDF QUE TIVERAM CONSULTA CLINICA NUM PERIODO */
  public static String clinicalConsultationWithinReportingPeriod() {
    return "select fm.member_id from gaac_family f inner join gaac_family_member fm on f.family_id=fm.family_id "
        + "inner join encounter e on e.patient_id=fm.member_id where e.encounter_datetime between :startDate and :endDate and fm.voided=0 and f.voided=0 "
        + "and e.voided=0 and f.location_id=:location and e.encounter_type in (6,9) and e.location_id=:location;";
  }
}
