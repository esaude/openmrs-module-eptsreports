/** */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.MisauKeyPopReportQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MisauKeyPopReportCohortQueries {

  private GenericCohortQueries genericCohortQueries;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public MisauKeyPopReportCohortQueries(
      GenericCohortQueries genericCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  @DocumentedDefinition(value = "patientsCurrentlyOnTarvWhoReceicevedVLResults")
  public CohortDefinition getPatientsCurrentlyOnTarvWhoReceicevedVLResults() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.setName(
        " Pacientes que receberam um teste de Carga Viral (CV)  durante o trimestre (Notificação anual!)");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "CARGAVIRAL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsComResultadoCargaViralFichaClinicaInReportingPeriod",
                MisauKeyPopReportQueries.QUERY
                    .findPatientsComResultadoCargaViralFichaClinicaInReportingPeriod),
            mappings));

    compositionDefinition.addSearch(
        "ACTIVOTARV",
        EptsReportUtils.map(
            this.getPatientsCurrentOnTarvMisauDefinition(),
            "endDate=${endDate},location=${location}"));

    compositionDefinition.addSearch(
        "CVANTERIOR",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsComCargaViralRegistadaMasqueNaoEPrimeiraRegistada",
                MisauKeyPopReportQueries.QUERY
                    .findPatientsComCargaViralRegistadaMasqueNaoEPrimeiraRegistada),
            mappings));

    compositionDefinition.setCompositionString("(CARGAVIRAL AND ACTIVOTARV) NOT CVANTERIOR");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "patientsCurrentlyOnTarvWhitSuppressedVLResults")
  public CohortDefinition getPatientsCurrentlyOnTarvWhitSuppressedVLResults() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.setName(
        " Pacientes Com supressão virológica durante o trimestre  (<1000 cópias/mL) (Notificação anual!)");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "CARGAVIRALIND",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWithCargaViralIndectetavelInReportingPeriod",
                MisauKeyPopReportQueries.QUERY
                    .findPatientsWithCargaViralIndectetavelInReportingPeriod),
            mappings));

    compositionDefinition.addSearch(
        "ACTIVOTARV",
        EptsReportUtils.map(
            this.getPatientsCurrentOnTarvMisauDefinition(),
            "endDate=${endDate},location=${location}"));

    compositionDefinition.addSearch(
        "CVANTERIOR",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsComCargaViralRegistadaMasqueNaoEPrimeiraRegistada",
                MisauKeyPopReportQueries.QUERY
                    .findPatientsComCargaViralRegistadaMasqueNaoEPrimeiraRegistada),
            mappings));

    compositionDefinition.setCompositionString("(CARGAVIRALIND AND ACTIVOTARV) NOT CVANTERIOR");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "patientsCoort12CurrentOnART")
  public CohortDefinition getPatientsCoort12CurrentOnArt() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate-1y},endDate=${endDate-1y},location=${location}";

    compositionDefinition.setName("Numero de pacientes na coorte 12 meses - Activos em TARV");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART),
            mappings));

    compositionDefinition.addSearch(
        "ACTIVOTARV",
        EptsReportUtils.map(
            this.getPatientsCurrentOnTarvMisauDefinition(),
            "endDate=${endDate},location=${location}"));

    compositionDefinition.setCompositionString("INICIOTARV AND ACTIVOTARV");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "patientsCoort12StartArt")
  public CohortDefinition getPatientsCoort12StartArt() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate-1y},endDate=${endDate-1y},location=${location}";

    compositionDefinition.setName("Numero de pacientes na coorte 12 - inicio de TARV");
    compositionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreNewlyEnrolledOnART",
                MisauKeyPopReportQueries.QUERY.findInicioTarvComDataFinalConhecida),
            mappings));

    compositionDefinition.addSearch(
        "TRANSFERIDOPARA",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsTransferidosParaInEndReportingPeriod",
                MisauKeyPopReportQueries.QUERY.findPatientsTransferidosParaInEndReportingPeriod),
            mappings));

    compositionDefinition.setCompositionString("INICIOTARV NOT TRANSFERIDOPARA");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "PatientsCurrentOnTarvMisauDefinition")
  public CohortDefinition getPatientsCurrentOnTarvMisauDefinition() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "endDate=${endDate},location=${location}";

    compositionDefinition.setName("Current On TARV - Misau Definition");
    compositionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionDefinition.addSearch(
        "ACTIVOTARV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsCurrentlyOnTarvTarvMisauDefinition",
                MisauKeyPopReportQueries.QUERY.findPatientsCurrentlyOnTarvTarvMisauDefinition),
            mappings));

    compositionDefinition.setCompositionString("ACTIVOTARV");

    return compositionDefinition;
  }
}
