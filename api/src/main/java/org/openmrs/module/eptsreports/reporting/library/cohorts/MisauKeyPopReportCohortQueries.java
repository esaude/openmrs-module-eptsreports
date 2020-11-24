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

    compositionDefinition.setName("Current On TARV with VL Result");
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
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

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

    compositionDefinition.setName("Current On TARV with VL Result");
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
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

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
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.setName("Current On TARV with VL Result");
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
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    compositionDefinition.setCompositionString("INICIOTARV AND ACTIVOTARV");

    return compositionDefinition;
  }

  @DocumentedDefinition(value = "patientsCoort12StartArt")
  public CohortDefinition getPatientsCoort12StartArt() {

    final CompositionCohortDefinition compositionDefinition = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionDefinition.setName("Current On TARV with VL Result");
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
        "TRANSFERIDOPARA",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsTransferidosParaInEndReportingPeriod",
                MisauKeyPopReportQueries.QUERY.findPatientsTransferidosParaInEndReportingPeriod),
            mappings));

    compositionDefinition.setCompositionString("INICIOTARV NOT TRANSFERIDOPARA");

    return compositionDefinition;
  }

  // INICIOTARV NOT TRANSFERIDOPARA
}
