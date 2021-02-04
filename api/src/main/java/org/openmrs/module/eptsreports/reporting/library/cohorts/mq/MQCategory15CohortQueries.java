package org.openmrs.module.eptsreports.reporting.library.cohorts.mq;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory15QueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory15CohortQueries {

  @Autowired private MQCohortQueries mQCohortQueries;

  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_1")
  public CohortDefinition getNumeratorCategory15_Indicator_1() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_1");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));
    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(), mappings));

    definition.setCompositionString("G2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWithDTandGaacWithRequestForVLCategory15H1")
  public CohortDefinition findPatientsWithDTandGaacWithRequestForVLCategory15H1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory15QueriesInterface.QUERY.findPatientsWithDTandGaacWithRequestForVLCategory15H1;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWithDTandGaacWithRequestForVLCategory15H2")
  public CohortDefinition findPatientsWithDTandGaacWithRequestForVLCategory15H2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory15QueriesInterface.QUERY.findPatientsWithDTandGaacWithRequestForVLCategory15H2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWithDTandGaacWithCV1000Category15I")
  public CohortDefinition findPatientsWithDTandGaacWithCV1000Category15I() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory15QueriesInterface.QUERY.findPatientsWithDTandGaacWithCV1000Category15I;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_2")
  public CohortDefinition getNumeratorCategory15_Indicator_2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_2");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "H1",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H1(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(), mappings));

    definition.setCompositionString("H1 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_3")
  public CohortDefinition getNumeratorCategory15_Indicator_3() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_3");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "H2",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H2(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(), mappings));

    definition.setCompositionString("G2 AND H2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_4")
  public CohortDefinition getNumeratorCategory15_Indicator_4() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_4");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "I", EptsReportUtils.map(this.findPatientsWithDTandGaacWithCV1000Category15I(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(), mappings));

    definition.setCompositionString("G2 AND I AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_5")
  public CohortDefinition getNumeratorCategory15_Indicator_5() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_5");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(), mappings));

    definition.setCompositionString("G2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_6")
  public CohortDefinition getNumeratorCategory15_Indicator_6() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_6");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(), mappings));

    definition.setCompositionString("G2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_7")
  public CohortDefinition getNumeratorCategory15_Indicator_7() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_7");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "H1",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H1(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(), mappings));

    definition.setCompositionString("H1 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_8")
  public CohortDefinition getNumeratorCategory15_Indicator_8() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_8");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "H1",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H1(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(), mappings));

    definition.setCompositionString("H1 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_9")
  public CohortDefinition getNumeratorCategory15_Indicator_9() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_9");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "H2",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H2(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(), mappings));

    definition.setCompositionString("G2 AND H2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_10")
  public CohortDefinition getNumeratorCategory15_Indicator_10() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_10");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "H2",
        EptsReportUtils.map(
            this.findPatientsWithDTandGaacWithRequestForVLCategory15H2(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(), mappings));

    definition.setCompositionString("G2 AND H2 AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_11")
  public CohortDefinition getNumeratorCategory15_Indicator_11() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_11");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "I", EptsReportUtils.map(this.findPatientsWithDTandGaacWithCV1000Category15I(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(), mappings));

    definition.setCompositionString("G2 AND I AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "NumeratorCategory15_Indicator_12")
  public CohortDefinition getNumeratorCategory15_Indicator_12() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory15_Indicator_12");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "G2",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "I", EptsReportUtils.map(this.findPatientsWithDTandGaacWithCV1000Category15I(), mappings));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(), mappings));

    definition.setCompositionString("G2 AND I AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "DenominatorCategory15_Indicator_1_And_2_and_3_And_4")
  public CohortDefinition getDenominatorCategory15_Indicator_1_And_2_and_3_And_4() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - DenominatorCategory15_Indicator_1_And_2_and_3_And_4");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    final String mappingEndRevisionDate = "endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "A",
        EptsReportUtils.map(
            findPatientsFromFichaClinicaForGivenConceptsDenominadorCategoria15A(),
            mappingEndRevisionDate));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            findPatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1(),
            mappingEndRevisionDate));

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "E",
        EptsReportUtils.map(
            mQCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "F", EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "ADULT-AGE",
        EptsReportUtils.map(mQCohortQueries.findPatientsWhoAreNewlyEnrolledOnARTRF05(), mappings));

    definition.setCompositionString("(A AND ADULT-AGE) NOT (B1 OR C OR D OR E OR F)");

    return definition;
  }

  @DocumentedDefinition(value = "DenominatorCategory15_Indicator_5_And_7_And_9_And_11")
  public CohortDefinition getDenominatorCategory15_Indicator_5_And_7_And_9_And_11() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - DenominatorCategory15_Indicator_5_And_7_And_9_And_11");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    final String mappingEndRevisionDate = "endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "A2",
        EptsReportUtils.map(
            findPatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2(),
            mappingEndRevisionDate));

    definition.addSearch(
        "A3",
        EptsReportUtils.map(
            findPatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3(),
            mappingEndRevisionDate));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            findPatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1(),
            mappingEndRevisionDate));

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "F", EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "CHILDREN-2-9",
        EptsReportUtils.map(
            findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingAgeRange(2, 9), mappings));

    definition.setCompositionString("((A2 OR A3) AND CHILDREN-2-9) NOT (B1 OR C OR D OR F)");

    return definition;
  }

  @DocumentedDefinition(value = "DenominatorCategory15_Indicator_6_And_8_And_10_And_12")
  public CohortDefinition getDenominatorCategory15_Indicator_6_And_8_And_10_And_12() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - DenominatorCategory15_Indicator_6_And_8_And_10_And_12");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    final String mappingEndRevisionDate = "endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "A2",
        EptsReportUtils.map(
            findPatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2(),
            mappingEndRevisionDate));

    definition.addSearch(
        "A3",
        EptsReportUtils.map(
            findPatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3(),
            mappingEndRevisionDate));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            findPatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1(),
            mappingEndRevisionDate));

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            mQCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "F", EptsReportUtils.map(mQCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.addSearch(
        "CHILDREN-10-14",
        EptsReportUtils.map(
            findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingAgeRange(10, 14), mappings));

    definition.setCompositionString("((A2 OR A3) AND CHILDREN-10-14) NOT (B1 OR C OR D OR F)");

    return definition;
  }

  @DocumentedDefinition(value = "PatientsFromFichaClinicaForGivenConceptsDenominadorCategoria15A")
  private CohortDefinition findPatientsFromFichaClinicaForGivenConceptsDenominadorCategoria15A() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("MQ - PatientsFromFichaClinicaForGivenConceptsDenominadorCategoria15A");
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        MQCategory15QueriesInterface.QUERY
            .findPatientsFromFichaClinicaForGivenConceptsDenominadorCategoria15A;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value =
          "PatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1")
  private CohortDefinition
      findPatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "MQ - PatientsWithLastGaacOrDispensaTrimestralInClinicaForGivenConceptsDenominadorCategoria15B1");
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        MQCategory15QueriesInterface.QUERY
            .findPatientsWithLastGaacOrLastDispensaTrimestralRegisteredInFichaClinicaWithinRevisionPeriodB1;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "PatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2")
  private CohortDefinition
      findPatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "MQ - PatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2");
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        MQCategory15QueriesInterface.QUERY
            .findPatientsWithDispensaTrimestralInicarInFichaClinicaDuringTheRevisionPeriodA2;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(
      value = "PatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3")
  private CohortDefinition
      findPatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        "MQ - PatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3");
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        MQCategory15QueriesInterface.QUERY
            .findPatientsWithLastTipoDeDispensaTrimestralInFichaClinicaWithinRevisionPeriodA3;
    definition.setQuery(query);
    return definition;
  }

  @DocumentedDefinition(value = "PatientsWhoAreNewlyEnrolledOnARTByAgeUsingAgeRange")
  private CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingAgeRange(
      int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName(
        String.format(
            "MQ - PatientsWhoAreNewlyEnrolledOnARTByAgeUsingAgeRange %s To %s ", startAge, endAge));
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQCategory15QueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByAgeBetweenAgeRange(
            startAge, endAge);

    definition.setQuery(query);

    return definition;
  }
}
