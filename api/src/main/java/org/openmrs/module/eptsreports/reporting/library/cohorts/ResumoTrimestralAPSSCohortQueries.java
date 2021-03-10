/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoTrimestralAPSSQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralAPSSCohortQueries {

  private GenericCohortQueries genericCohortQueries;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public ResumoTrimestralAPSSCohortQueries(
      HivMetadata hivMetadata,
      GenericCohortQueries genericCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  /**
   * A1 :Nº de Pacients que receberam revelação total do diagnóstico durante o trimestre
   *
   * <p>month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "A1")
  public CohortDefinition
      getNumberOfPatientsWhoReceivedTotalDiagnosticRevelationInReportingPeriodA1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsReceivedTotalDiagnosticRevelationInReportingPeriod");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    definition.setQuery(
        ResumoTrimestralAPSSQueries.QUERY
            .findPatientsReceivedTotalDiagnosticRevelationInReportingPeriod);

    return definition;
  }

  @DocumentedDefinition(value = "B1")
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthB1() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("NumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA2");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.setName("patientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "A2",
        EptsReportUtils.map(
            resumoMensalCohortQueries
                .getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
            mappings));

    definition.addSearch(
        "APSS-ACONSELHAMENTO",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsRegisteredInAPSSPPWithinReportingPeriod",
                ResumoTrimestralAPSSQueries.QUERY
                    .findPatientsRegisteredInAPSSPPAconselhamentoWithinReportingPeriod),
            mappings));

    definition.setCompositionString("A2 AND APSS-ACONSELHAMENTO");

    return definition;
  }

  @DocumentedDefinition(value = "C1")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHC1() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("NumberOfPatientsWhoAreCurrentlyEnrolledOnArtMOHC1");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));
    compsitionDefinition.addSearch(
        "TXCURR",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoAreCurrentlyEnrolledOnArtMOHC1",
                ResumoMensalQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13()),
            mappings));
    compsitionDefinition.addSearch(
        "APSSPP",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWithSeguimentoDeAdesao",
                ResumoTrimestralAPSSQueries.QUERY.findPatientsWithSeguimentoDeAdesao),
            mappings));

    compsitionDefinition.setCompositionString("TXCURR AND APSSPP");

    return compsitionDefinition;
  }

  @DocumentedDefinition(value = "D1")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtWithPrevencaoPosetivaD1() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("NumberOfPatientsWhoAreCurrentlyEnrolledOnArtMOHC1");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "B1",
        EptsReportUtils.map(
            getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(mappings), mappings));

    compsitionDefinition.addSearch(
        "PREVENCAOPOSETIVA",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWithPrevencaoPosetivaInReportingPeriod",
                ResumoTrimestralAPSSQueries.QUERY
                    .findPatientsWithPrevencaoPosetivaInReportingPeriod),
            "startDate=${startDate-3m},endDate=${endDate},location=${location}"));

    compsitionDefinition.setCompositionString("B1 AND PREVENCAOPOSETIVA");

    return compsitionDefinition;
  }

  private CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(
      String mappings) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Number of patientes who initiated TARV at this HF End Date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1", map(getTxNewCompositionCohort("Number of patientes who initiated TARV"), mappings));
    cd.setCompositionString("B1");
    return cd;
  }

  private CohortDefinition getTxNewCompositionCohort(final String cohortName) {
    final CompositionCohortDefinition txNewCompositionCohort = new CompositionCohortDefinition();

    txNewCompositionCohort.setName(cohortName);
    txNewCompositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    txNewCompositionCohort.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate-3m},endDate=${endDate-3m},location=${location}";

    txNewCompositionCohort.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoAreNewlyEnrolledOnART",
                TxNewQueries.QUERY.findPatientsWhoAreNewlyEnrolledOnART),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    txNewCompositionCohort.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    txNewCompositionCohort.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return txNewCompositionCohort;
  }

  @DocumentedDefinition(value = "E1")
  public CohortDefinition findFaultsOrAbandonedPatientsReferredToCallOrVisitReintegrationE1() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName(
        "NumberOfPatientsMissingAbandonedReferredToCallVisitReintegrationE1");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "FALTOSOS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWithFaultsInARTTreatment",
                ResumoTrimestralAPSSQueries.QUERY.findPatientsWhoFaultTreatmentRF15E1),
            mappings));

    compsitionDefinition.addSearch(
        "ABANDONOS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsAbandonedTheARTTreatment",
                ResumoTrimestralAPSSQueries.QUERY.findPatientsWhoAbandonedTreatmentRF15E1),
            mappings));

    compsitionDefinition.addSearch(
        "REINTEGRACAO",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientSRegistredToReintegracao",
                ResumoTrimestralAPSSQueries.QUERY
                    .findPatientsRegistredInLivroDeChamadasVisitasDomiciliariasWithElegivelParaReintegracaoRF15E1),
            mappings));

    compsitionDefinition.setCompositionString("FALTOSOS OR ABANDONOS AND REINTEGRACAO");

    return compsitionDefinition;
  }

  @DocumentedDefinition(value = "E2")
  public CohortDefinition findPatientsReferredToReintegrationContactedAndFoundedE2() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("NumberOfPatientsReferredToReintegrationContactedVisitedAndFound");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "E1",
        EptsReportUtils.map(
            findFaultsOrAbandonedPatientsReferredToCallOrVisitReintegrationE1(), mappings));

    compsitionDefinition.addSearch(
        "ENCONTRADOS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsThatWasCalledAndVisitedAndFounded",
                ResumoTrimestralAPSSQueries.QUERY
                    .findPatientsRegistredInLivroDeChamadasVisitasDomiciliariasForChamadaOrEncontradoRF16E2),
            mappings));

    compsitionDefinition.setCompositionString("E1 AND ENCONTRADOS");

    return compsitionDefinition;
  }

  @DocumentedDefinition(value = "E3")
  public CohortDefinition findFaultsAbandonedPatientsReturnedToHospitalInReportPeriodE3() {

    final CompositionCohortDefinition compsitionDefinition = new CompositionCohortDefinition();
    compsitionDefinition.setName("NumberOfMissingAbandonedPatientsReturnedHealthFacility");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compsitionDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compsitionDefinition.addParameter(new Parameter("location", "location", Location.class));

    compsitionDefinition.addSearch(
        "E2",
        EptsReportUtils.map(findPatientsReferredToReintegrationContactedAndFoundedE2(), mappings));

    compsitionDefinition.addSearch(
        "RETORNADOS",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsReturnedToHealthFacility",
                ResumoTrimestralAPSSQueries.QUERY
                    .findPatientsReturnedToHospitalAfterChamadaOrVisitaDomiciliarRF17E3),
            mappings));

    compsitionDefinition.setCompositionString("E2 AND RETORNADOS");

    return compsitionDefinition;
  }
}
