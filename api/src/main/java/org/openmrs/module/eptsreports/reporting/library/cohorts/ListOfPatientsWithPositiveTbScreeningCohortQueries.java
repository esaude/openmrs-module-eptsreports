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

    CohortDefinition positiveScreening = this.getPatientsWithMostRecentTbScreeningDate();

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
   * <p>The system will consider the most recent date from the different sources that falls during
   * the reporting period as patients Positive Screening Date
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithMostRecentTbScreeningDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Most Recent TB Screening Date ");
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));

    String maxPositiveScreeningDateQuery =
        new EptsQueriesUtil()
            .max(
                listOfPatientsWithPositiveTbScreeningQueries
                    .getTbPositiveScreeningFromSourcesQuery())
            .getQuery();

    String query =
        new EptsQueriesUtil().patientIdQueryBuilder(maxPositiveScreeningDateQuery).getQuery();

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }
}
