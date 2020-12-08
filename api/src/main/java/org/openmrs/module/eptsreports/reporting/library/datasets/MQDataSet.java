package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.MQCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQDataSet extends BaseDataSet {

  @Autowired private MQCohortQueries mqCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition constructTMqDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(getParameters());

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dataSetDefinition.setName("MQ Data Set");

    final CohortIndicator CAT3ADULTODENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT3ADULTODENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator(),
                mappings));

    CAT3ADULTODENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT3ADULTODENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT3ADULTODENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT3ADULTODENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT3ADULTONUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT3ADULTONUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator(),
                mappings));

    CAT3ADULTONUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT3ADULTONUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT3ADULTONUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT3ADULTONUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT3CHIDRENDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT3ADULTONUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator(),
                mappings));

    CAT3CHIDRENDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT3CHIDRENDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT3CHIDRENDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT3CHIDRENDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT3CHIDRENNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT3ADULTONUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator(),
                mappings));

    CAT3CHIDRENNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT3CHIDRENNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT3CHIDRENNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT3CHIDRENNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT4CHIDRENDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT4CHIDRENDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator(),
                mappings));

    CAT4CHIDRENDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT4CHIDRENDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT4CHIDRENDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT4CHIDRENDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT4CHIDRENNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT4CHIDRENNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator(),
                mappings));

    CAT4CHIDRENNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT4CHIDRENNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT4CHIDRENNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT4CHIDRENNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT4PregnantDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT4PregnantDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator(),
                mappings));

    CAT4PregnantDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT4PregnantDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT4PregnantDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT4PregnantDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT4PregnantNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT4PregnantNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18(),
                mappings));

    CAT4PregnantNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT4PregnantNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT4PregnantNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT4PregnantNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT5CHIDRENDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT5CHIDRENDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator(),
                mappings));

    CAT5CHIDRENDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT5CHIDRENDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT5CHIDRENDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT5CHIDRENDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT5CHIDRENNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT5CHIDRENNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator(),
                mappings));

    CAT5CHIDRENNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT5CHIDRENNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT5CHIDRENNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT5CHIDRENNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT5PregnantDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT5PregnantDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator(),
                mappings));

    CAT5PregnantDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT5PregnantDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT5PregnantDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT5PregnantDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT5PregnantNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT5PregnantNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22Denominator(),
                mappings));

    CAT5PregnantNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT5PregnantNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT5PregnantNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT5PregnantNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6AdultoDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6AdultoDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator(),
                mappings));

    CAT6AdultoDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6AdultoDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6AdultoDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6AdultoDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6AdultoNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6AdultoNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF12Numerator(),
                mappings));

    CAT6AdultoNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6AdultoNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6AdultoNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6AdultoNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6ChildrenDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6ChildrenDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator(),
                mappings));

    CAT6ChildrenDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6ChildrenDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6ChildrenDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6ChildrenDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6ChildrenNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6ChildrenNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF14Numerator(),
                mappings));

    CAT6ChildrenNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6ChildrenNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6ChildrenNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6ChildrenNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6PregnantDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6PregnantDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator(),
                mappings));

    CAT6PregnantDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6PregnantDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6PregnantDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6PregnantDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6PregnantNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6PregnantNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF16Numerator(),
                mappings));

    CAT6PregnantNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6PregnantNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6PregnantNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6PregnantNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6BreastfeedingDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6BreastfeedingDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator(),
                mappings));

    CAT6BreastfeedingDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6BreastfeedingDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6BreastfeedingDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6BreastfeedingDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT6BreastfeedingNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT6BreastfeedingNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator(),
                mappings));

    CAT6BreastfeedingNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT6BreastfeedingNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT6BreastfeedingNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT6BreastfeedingNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7AdultDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7AdultDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator(),
                mappings));

    CAT7AdultDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7AdultDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7AdultDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7AdultDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7AdultoNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7AdultoNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator(),
                mappings));

    CAT7AdultoNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7AdultoNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7AdultoNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7AdultoNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7ChildrenDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7ChildrenDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator(),
                mappings));

    CAT7ChildrenDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7ChildrenDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7ChildrenDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7ChildrenDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7ChildrenNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7ChildrenNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF22Numerator(),
                mappings));

    CAT7ChildrenNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7ChildrenNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7ChildrenNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7ChildrenNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7PregnantDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7PregnantDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator(),
                mappings));

    CAT7PregnantDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7PregnantDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7PregnantDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7PregnantDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7PregnantNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7PregnantNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator(),
                mappings));

    CAT7PregnantNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7PregnantNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7PregnantNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7PregnantNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7AdultoTPIDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7AdultoTPIDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator(),
                mappings));

    CAT7AdultoTPIDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7AdultoTPIDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7AdultoTPIDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7AdultoTPIDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7AdultoTPINUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7AdultoTPINUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(),
                mappings));

    CAT7AdultoTPINUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7AdultoTPINUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7AdultoTPINUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7AdultoTPINUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7ChildrenTPIDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7ChildrenTPIDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator(),
                mappings));

    CAT7ChildrenTPIDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7ChildrenTPIDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7ChildrenTPIDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7ChildrenTPIDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7ChildrenTPINUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7ChildrenTPINUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF28Numerator(),
                mappings));

    CAT7ChildrenTPINUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7ChildrenTPINUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7ChildrenTPINUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7ChildrenTPINUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7PragnantTPIDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7PragnantTPIDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator(),
                mappings));

    CAT7PragnantTPIDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7PragnantTPIDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7PragnantTPIDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7PragnantTPIDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT7PragnantTPINUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT7PragnantTPINUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator(),
                mappings));

    CAT7PragnantTPINUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT7PragnantTPINUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT7PragnantTPINUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT7PragnantTPINUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    dataSetDefinition.addColumn(
        "CAT3ADULTODENOMINATOR",
        "CAT3ADULTODENOMINATOR",
        EptsReportUtils.map(CAT3ADULTODENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT3ADULTONUMERATOR",
        "CAT3ADULTONUMERATOR",
        EptsReportUtils.map(CAT3ADULTONUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT3CHIDRENDENOMINATOR",
        "CAT3CHIDRENDENOMINATOR",
        EptsReportUtils.map(CAT3CHIDRENDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT3CHIDRENNUMERATOR",
        "CAT3CHIDRENNUMERATOR",
        EptsReportUtils.map(CAT3CHIDRENNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT4CHIDRENDENOMINATOR",
        "CAT4CHIDRENDENOMINATOR",
        EptsReportUtils.map(CAT4CHIDRENDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT4CHIDRENNUMERATOR",
        "CAT4CHIDRENNUMERATOR",
        EptsReportUtils.map(CAT4CHIDRENNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT4PregnantDENOMINATOR",
        "CAT4PregnantDENOMINATOR",
        EptsReportUtils.map(CAT4PregnantDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT4PregnantNUMERATOR",
        "CAT4PregnantNUMERATOR",
        EptsReportUtils.map(CAT4PregnantNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT5CHIDRENDENOMINATOR",
        "CAT5CHIDRENDENOMINATOR",
        EptsReportUtils.map(CAT5CHIDRENDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT5CHIDRENNUMERATOR",
        "CAT5CHIDRENNUMERATOR",
        EptsReportUtils.map(CAT5CHIDRENNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT5PregnantDENOMINATOR",
        "CAT5PregnantDENOMINATOR",
        EptsReportUtils.map(CAT5PregnantDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT5PregnantNUMERATOR",
        "CAT5PregnantNUMERATOR",
        EptsReportUtils.map(CAT5PregnantNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6AdultoDENOMINATOR",
        "CAT6AdultoDENOMINATOR",
        EptsReportUtils.map(CAT6AdultoDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6AdultoNUMERATOR",
        "CAT6AdultoNUMERATOR",
        EptsReportUtils.map(CAT6AdultoNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6ChildrenDENOMINATOR",
        "CAT6ChildrenDENOMINATOR",
        EptsReportUtils.map(CAT6ChildrenDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6ChildrenNUMERATOR",
        "CAT6ChildrenNUMERATOR",
        EptsReportUtils.map(CAT6ChildrenNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6PregnantDENOMINATOR",
        "CAT6PregnantDENOMINATOR",
        EptsReportUtils.map(CAT6PregnantDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6PregnantNUMERATOR",
        "CAT6PregnantNUMERATOR",
        EptsReportUtils.map(CAT6PregnantNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6BreastfeedingDENOMINATOR",
        "CAT6BreastfeedingDENOMINATOR",
        EptsReportUtils.map(CAT6BreastfeedingDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT6BreastfeedingNUMERATOR",
        "CAT6BreastfeedingNUMERATOR",
        EptsReportUtils.map(CAT6BreastfeedingNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7AdultDENOMINATOR",
        "CAT7AdultDENOMINATOR",
        EptsReportUtils.map(CAT7AdultDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7AdultoNUMERATOR",
        "CAT7AdultoNUMERATOR",
        EptsReportUtils.map(CAT7AdultoNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7ChildrenDENOMINATOR",
        "CAT7ChildrenDENOMINATOR",
        EptsReportUtils.map(CAT7ChildrenDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7ChildrenNUMERATOR",
        "CAT7ChildrenNUMERATOR",
        EptsReportUtils.map(CAT7ChildrenNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PregnantDENOMINATOR",
        "CAT7PregnantDENOMINATOR",
        EptsReportUtils.map(CAT7PregnantDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PregnantNUMERATOR",
        "CAT7PregnantNUMERATOR",
        EptsReportUtils.map(CAT7PregnantNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7AdultoTPIDENOMINATOR",
        "CAT7AdultoTPIDENOMINATOR",
        EptsReportUtils.map(CAT7AdultoTPIDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7AdultoTPINUMERATOR",
        "CAT7AdultoTPINUMERATOR",
        EptsReportUtils.map(CAT7AdultoTPINUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7ChildrenTPIDENOMINATOR",
        "CAT7ChildrenTPIDENOMINATOR",
        EptsReportUtils.map(CAT7ChildrenTPIDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7ChildrenTPINUMERATOR",
        "CAT7ChildrenTPINUMERATOR",
        EptsReportUtils.map(CAT7ChildrenTPINUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PragnantTPIDENOMINATOR",
        "CAT7PragnantTPIDENOMINATOR",
        EptsReportUtils.map(CAT7PragnantTPIDENOMINATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PragnantTPINUMERATOR",
        "CAT7PragnantTPINUMERATOR",
        EptsReportUtils.map(CAT7PragnantTPINUMERATOR, mappings),
        "");

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class),
        new Parameter("endInclusionDate", "  Data Final Inclusão", Date.class),
        new Parameter("endRevisionDate", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
