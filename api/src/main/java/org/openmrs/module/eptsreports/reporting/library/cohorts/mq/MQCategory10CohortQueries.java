package org.openmrs.module.eptsreports.reporting.library.cohorts.mq;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory10QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory10CohortQueries {

  @Autowired private MQCohortQueries mQCohortQueries;

  @DocumentedDefinition(value = "findAllPatientsDiagnosedWithThePCRTestCategory10_B")
  public CohortDefinition findAllPatientsDiagnosedWithThePCRTestCategory10_B() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("PCR_TEST");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQCategory10QueriesInterface.QUERY.findAllPatientsDiagnosedWithThePCRTestB;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findAllPatientsDiagnosedWithThePCRTestAndStartARTWithInMaximumOf15DaysCategory10_D")
  public CohortDefinition
      findAllPatientsDiagnosedWithThePCRTestAndStartARTWithInMaximumOf15DaysCategory10_D() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("PCR_TEST_START_ARV");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory10QueriesInterface.QUERY
            .findAllPatientsDiagnosedWithThePCRTestAndStartDateARTMinusPCRTestDaysBetweenZeroAndFifteen;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06")
  public CohortDefinition
      findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06;

    definition.setQuery(query);

    return definition;
  }

  /**
   * 10.3. % de crianças com PCR positivo para HIV que iniciaram TARV dentro de 2 semanas após o
   * diagnóstico/entrega do resultado ao cuidador Denominator: # de crianças com idade compreendida
   * entre 0 - 18 meses, diagnosticadas através do teste de PCR (registados na ficha resumo, no
   * campo Cuidados de HIV-Data-PCR) e que iniciaram o TARV no período de inclusão
   */
  @DocumentedDefinition(
      value =
          "findChildrenWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Denominador_10_3")
  public CohortDefinition
      findPatientsWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Denominador_10_3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PCR_TEST_START_TARV_15_DAYS_DENOMINATOR");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "A",
        EptsReportUtils.map(
            this.mQCohortQueries.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.addSearch(
        "B",
        EptsReportUtils.map(this.findAllPatientsDiagnosedWithThePCRTestCategory10_B(), mappings));

    definition.setCompositionString("(A AND B) NOT C");

    return definition;
  }

  /**
   * Numerator: # crianças com idade compreendida entre 0 - 18 meses, diagnosticadas através do PCR
   * (registado na ficha resumo, no campo Cuidados de HIV-Data-PCR) que tenham iniciado o TARV
   * dentro de 15 dias após o diagnóstico através do PCR. (Line 53, Column E in the template)
   */
  @DocumentedDefinition(
      value =
          "findChildrenWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Numerador_10_3")
  public CohortDefinition
      findPatientsWithPCRTestPositiveForHIVAndStartARTWithinTwoWeeksCategory10_Numerador_10_3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PCR_TEST_START_TARV_15_DAYS_NUMERATOR");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            this.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "A",
        EptsReportUtils.map(
            this.mQCohortQueries.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.addSearch(
        "B",
        EptsReportUtils.map(this.findAllPatientsDiagnosedWithThePCRTestCategory10_B(), mappings));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            this
                .findAllPatientsDiagnosedWithThePCRTestAndStartARTWithInMaximumOf15DaysCategory10_D(),
            mappings));

    definition.setCompositionString("((A AND B) AND D) NOT C");

    return definition;
  }
}
