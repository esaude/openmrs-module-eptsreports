package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueryCategory14 {

  @Autowired private PvlsCohortQueries pvlsCohortQueries;

  @DocumentedDefinition(value = "Denominator Category 14")
  public CohortDefinition getDenominatorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - Denominator Category14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "DENOMINATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "A1-PREGNANT",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "A2-BREASTFEEDING",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("DENOMINATOR-TXPVLS NOT (A1-PREGNANT OR A2-BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(value = "Denominator Category 14")
  public CohortDefinition getPregnantDenominatorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ -  Denominator Category14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "DENOMINATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "A1-PREGNANT",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("DENOMINATOR-TXPVLS AND A1-PREGNANT");

    return definition;
  }

  @DocumentedDefinition(value = "Denominator Category 14")
  public CohortDefinition getBreastfeedingDenominatorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - Denominator Category14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "DENOMINATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "A2-BREASTFEEDING",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("DENOMINATOR-TXPVLS AND A2-BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(value = "Numerator Category 14")
  public CohortDefinition getNumeratorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "NUMERATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B1-PREGNANT",
        EptsReportUtils.map(
            this.pvlsCohortQueries
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B2-BREASTFEEDING",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("NUMERATOR-TXPVLS NOT (B1-PREGNANT OR B2-BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(value = "Denominator Category 14")
  public CohortDefinition getPregnantNumeratorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "NUMERATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B1-PREGNANT",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B2-BREASTFEEDING",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("(NUMERATOR-TXPVLS AND B1-PREGNANT) NOT B2-BREASTFEEDING");

    return definition;
  }

  @DocumentedDefinition(value = "Denominator Category 14")
  public CohortDefinition getBreastfeedingNumeratorCategory14Indicator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("MQ - NumeratorCategory14_Indicator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    definition.addSearch(
        "NUMERATOR-TXPVLS",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPatientsWhoHaveMoreThan3MonthsOnArtWithViralLoadResultLessthan1000RegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B2-BREASTFEEDING",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findBreastfeedingWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.addSearch(
        "B1-PREGNANT",
        EptsReportUtils.map(
            pvlsCohortQueries
                .findPregnantWomanWhoHaveMoreThan3MonthsOnArtWithViralLoadRegisteredInTheLast12Months(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("(NUMERATOR-TXPVLS AND B2-BREASTFEEDING) NOT B1-PREGNANT");

    return definition;
  }
}
