package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsWithPositiveTbScreeningQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWithPositiveTbScreeningCohortQueries {

  private final TxCurrCohortQueries txCurrCohortQueries;

  private final ListOfPatientsWithPositiveTbScreeningQueries
      listOfPatientsWithPositiveTbScreeningQueries;

  @Autowired
  public ListOfPatientsWithPositiveTbScreeningCohortQueries(
      TxCurrCohortQueries txCurrCohortQueries,
      ListOfPatientsWithPositiveTbScreeningQueries listOfPatientsWithPositiveTbScreeningQueries) {
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.listOfPatientsWithPositiveTbScreeningQueries =
        listOfPatientsWithPositiveTbScreeningQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Base Cohort for List of patients With Positive Tb Screening ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txcurr = this.txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true);

    CohortDefinition positiveScreening = this.getPatientsWithPositiveTbScreening();

    cd.addSearch(
        "txcurr", EptsReportUtils.map(txcurr, "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "positiveScreening",
        EptsReportUtils.map(
            positiveScreening, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("txcurr AND positiveScreening");

    return cd;
  }

  /**
   * <b>TB_SCRN_FR3</b> <b>the system will identify patients with positive TB Screening during the
   * reporting period as follows:</b>
   *
   * <ul>
   *   <li>Patients marked with “Tratamento TB– Início (I)” on Ficha Clínica Master Card during the
   *       reporting period; or
   *   <li>TB Date (Condições Médicas Importantes – Ficha Resumo – Mastercard) that falls within the
   *       reporting period; or
   *   <li>“TB Program Enrollment Date” (Data de Admissão) In SESP – Program Enrollment within the
   *       reporting period or
   *   <li>In the Ficha Clinica – Mastercard the following fields marked during reporting period:
   *       <p>Tem sintomas de TB? (Screening Value) = Sim; or Diagnóstico TB activa = Sim: or Quais
   *       sintomas de TB, one or more of following “FESTAC” criteria is marked: F – Febre, E –
   *       Emagrecimento, S – Sudorese nocturna, T – Tosse a mais de 2 semanas, A – Astenia, C –
   *       Contacto com TB, Adenopatia cervical indolor or
   *   <li>if Investigações - Pedidos Laboratoriais requests are marked for ‘TB LAM’, ‘GeneXpert’,
   *       ‘Cultura’, ‘BK’ or ‘Raio-X’; or
   *   <li>If Investigações - Resultados Laboratoriais result ANY RESULT is recorded for ‘TB LAM’,
   *       ‘GeneXpert’, ‘Cultura’, ‘BK’ or ‘Raio-X’; or
   *   <li>In Laboratory form the following fields marked during reporting period: Baciloscopia
   *       result = not BLANK or If GeneXpert result = not BLANK or If Xpert MTB result = not BLANK
   *       or If Cultura result = not BLANK or If TB LAM result = not BLANK or
   *   <li>AND
   *       <p>Exclude all patients on ART who started TB Treatment (TB_SCRN_FR3.1) at any point in
   *       the previous 6 months before the start date of the reporting period.
   * </ul>
   *
   * <p>The system will consider the earliest date from the different sources that falls during the
   * reporting period as patients Positive Screening Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithPositiveTbScreening() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("List of Patients currently on ART with Positive TB Screening ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition earliestTbScreeningDate = this.getPatientsWithEarliestTbScreeningDate();

    CohortDefinition startedTbTreatmentInPrevious6Months =
        this.getPatientsWhoStartedTbTreatmentInPrevious6Months();

    cd.addSearch(
        "earliestTbScreeningDate",
        EptsReportUtils.map(
            earliestTbScreeningDate,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "startedTbTreatmentInPrevious6Months",
        EptsReportUtils.map(
            startedTbTreatmentInPrevious6Months,
            "startDate=${startDate-6m},endDate=${startDate},location=${location}"));

    cd.setCompositionString("earliestTbScreeningDate AND NOT startedTbTreatmentInPrevious6Months");

    return cd;
  }

  /**
   * <b>the system will identify patients with positive TB Screening during the reporting period as
   * follows:</b>
   *
   * <ul>
   *   <li>Patients marked with “Tratamento TB– Início (I)” on Ficha Clínica Master Card during the
   *       reporting period; or
   *   <li>TB Date (Condições Médicas Importantes – Ficha Resumo – Mastercard) that falls within the
   *       reporting period; or
   *   <li>“TB Program Enrollment Date” (Data de Admissão) In SESP – Program Enrollment within the
   *       reporting period or
   *   <li>In the Ficha Clinica – Mastercard the following fields marked during reporting period:
   *       <p>Tem sintomas de TB? (Screening Value) = Sim; or Diagnóstico TB activa = Sim: or Quais
   *       sintomas de TB, one or more of following “FESTAC” criteria is marked: F – Febre, E –
   *       Emagrecimento, S – Sudorese nocturna, T – Tosse a mais de 2 semanas, A – Astenia, C –
   *       Contacto com TB, Adenopatia cervical indolor or
   *   <li>if Investigações - Pedidos Laboratoriais requests are marked for ‘TB LAM’, ‘GeneXpert’,
   *       ‘Cultura’, ‘BK’ or ‘Raio-X’; or
   *   <li>If Investigações - Resultados Laboratoriais result ANY RESULT is recorded for ‘TB LAM’,
   *       ‘GeneXpert’, ‘Cultura’, ‘BK’ or ‘Raio-X’; or
   *   <li>In Laboratory form the following fields marked during reporting period: Baciloscopia
   *       result = not BLANK or If GeneXpert result = not BLANK or If Xpert MTB result = not BLANK
   *       or If Cultura result = not BLANK or If TB LAM result = not BLANK or
   * </ul>
   *
   * <p>The system will consider the earliest date from the different sources that falls during the
   * reporting period as patients Positive Screening Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithEarliestTbScreeningDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Most Recent TB Screening Date ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

    String minPositiveScreeningDateQuery =
        new EptsQueriesUtil()
            .min(
                listOfPatientsWithPositiveTbScreeningQueries
                    .getTbPositiveScreeningFromSourcesQuery())
            .getQuery();

    String query =
        new EptsQueriesUtil().patientIdQueryBuilder(minPositiveScreeningDateQuery).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>TB_SCRN_FR3.1</b> <b>The system will identify patients who started TB treatment in the 6
   * months prior to the report start date as follows:</b>
   *
   * <ul>
   *   <li>Include all patients marked with “Tratamento TB– Início (I)” on Ficha Clínica Master Card
   *       in the 6 months prior to the report start date (>=startDate – 6 months and <startDate)
   *   <li>Include all patients who in “Patient Clinical Record of ART - Ficha de Seguimento (Adults
   *       and Pediatric)” have at least TB Treatment (Tratamento de TB) Start Date (Data de Início)
   *       in the 6 months prior to the report start date (>=startDate – 6 months and <startDate)
   *   <li>Include all patients with TB Date (Condições Médicas Importantes – Ficha Resumo –
   *       Mastercard) within the in the 6 months prior to the report start date (>=startDate – 6
   *       months and <startDate)
   *   <li>Include all patients who are enrolled in TB Program with enrollment Date (Data de
   *       Admissão) in the 6 months prior to the report start date (>=startDate – 6 months and
   *       <startDate)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoStartedTbTreatmentInPrevious6Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Most Recent TB Screening Date ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

    String query =
        new EptsQueriesUtil()
            .patientIdQueryBuilder(
                listOfPatientsWithPositiveTbScreeningQueries
                    .getPatientsTbTratment6MonthsPriorToReportStartDate())
            .getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }
}
