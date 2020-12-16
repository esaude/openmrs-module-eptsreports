package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.MQCategory13Section1CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.MQCohortCategory15Queries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.MQCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MQDataSet extends BaseDataSet {

  @Autowired private MQCohortQueries mqCohortQueries;

  @Autowired private MQCategory13Section1CohortQueries mQCategory13Section1CohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private MQCohortCategory15Queries mqCohortCategory15Queries;

  public DataSetDefinition constructTMqDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(getParameters());

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dataSetDefinition.setName("MQ Data Set");

    dataSetDefinition.addDimension("gender", map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "age",
        map(eptsCommonDimension.mqAgeDimention(ageDimensionCohort), "effectiveDate=${endDate}"));

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

    final CohortIndicator CAT11AdultoAPSSPPDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11AdultoAPSSPPDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInCategory11Denominator(),
                mappings));
    CAT11AdultoAPSSPPDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11AdultoAPSSPPDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11AdultoAPSSPPDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11AdultoAPSSPPDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11AdultoAPSSPPNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11AdultoAPSSPPNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NUMERATOR(),
                mappings));
    CAT11AdultoAPSSPPNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11AdultoAPSSPPNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11AdultoAPSSPPNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11AdultoAPSSPPNUMERATOR.addParameter(new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11Adulto1000CVDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Adulto1000CVDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                mappings));
    CAT11Adulto1000CVDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Adulto1000CVDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Adulto1000CVDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Adulto1000CVDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11Adulto1000CVNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Adulto1000CVNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR(),
                mappings));
    CAT11Adulto1000CVNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Adulto1000CVNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Adulto1000CVNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Adulto1000CVNUMERATOR.addParameter(new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Denominator(),
                mappings));
    CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR.addParameter(
        new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildernOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Numerator(),
                mappings));
    CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR.addParameter(
        new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11Children1000CVDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Children1000CVDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11Denominator(),
                mappings));
    CAT11Children1000CVDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Children1000CVDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Children1000CVDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Children1000CVDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11Children1000CVNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Children1000CVNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutWITH1000CVCategory11NUMERATOR(),
                mappings));
    CAT11Children1000CVNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Children1000CVNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Children1000CVNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Children1000CVNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT11Least9APSSConsultationDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Least9APSSConsultationDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildernWith9MonthAnd2YearOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Denominator(),
                mappings));
    CAT11Least9APSSConsultationDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Least9APSSConsultationDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Least9APSSConsultationDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Least9APSSConsultationDENOMINATOR.addParameter(
        new Parameter("location", "location", Date.class));
    final CohortIndicator CAT11Least9APSSConsultationNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT11Least9APSSConsultationNUMERATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildernWith9MonthAnd2YearOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOutCategory11Numerator(),
                mappings));
    CAT11Least9APSSConsultationNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT11Least9APSSConsultationNUMERATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT11Least9APSSConsultationNUMERATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT11Least9APSSConsultationNUMERATOR.addParameter(
        new Parameter("location", "location", Date.class));

    // Categoria 12 Adultos PARTE 1

    // Categoria 12 Adultos

    final CohortIndicator CAT12ADULTDENOMINADOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12ADULTDENOMINADOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTDENOMINADOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTDENOMINADOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTDENOMINADOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12ADULTNUMERATOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATOR33DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line62ColumnDInTheTemplateNumerator1(),
                mappings));

    CAT12ADULTNUMERATOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTNUMERATOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTNUMERATOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTNUMERATOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12ADULTDENOMINADOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTDENOMINADOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12ADULTDENOMINADOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTDENOMINADOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTDENOMINADOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTDENOMINADOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12ADULTNUMERATOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator2(),
                mappings));

    CAT12ADULTNUMERATOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTNUMERATOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTNUMERATOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTNUMERATOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Criancas

    final CohortIndicator CAT12CHILDRENDENOMINADOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTDENOMINATOR",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12CHILDRENDENOMINADOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENDENOMINADOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENDENOMINADOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENDENOMINADOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12CHILDRENNUMERATOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENNUMERATOR33DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line67ColumnDInTheTemplateNumerator3(),
                mappings));

    CAT12CHILDRENNUMERATOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENNUMERATOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENNUMERATOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENNUMERATOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12CHILDRENDENOMINADOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENDENOMINADOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12CHILDRENDENOMINADOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENDENOMINADOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENDENOMINADOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENDENOMINADOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12CHILDRENNUMERATOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator4(),
                mappings));

    CAT12CHILDRENNUMERATOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENNUMERATOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENNUMERATOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENNUMERATOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Mulheres Gravidas

    final CohortIndicator CAT12PREGNANTDENOMINADOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12PREGNANTDENOMINADOR33DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantWhoStartedARTInTheInclusionPeriodCategory12ExcludingBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12PREGNANTDENOMINADOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTDENOMINADOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTDENOMINADOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTDENOMINADOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12PREGNANTNUMERATOR33DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12PREGNANTNUMERATOR33DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation33DaysAfterAtartingARTCategory12Line71ColumnDInTheTemplateNumerator5(),
                mappings));

    CAT12PREGNANTNUMERATOR33DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTNUMERATOR33DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTNUMERATOR33DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTNUMERATOR33DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12PREGNANTENOMINADOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12PREGNANTENOMINADOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodCategory12ExcludingPreganantAndBreastfeedingAndTrasferedInOrOut(),
                mappings));

    CAT12PREGNANTENOMINADOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTENOMINADOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTENOMINADOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTENOMINADOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12PREGNANTNUMERATOR99DAYS =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATOR99DAYS",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultsWhoStartedARTInTheInclusionPeriodAndReturnedForClinicalConsultation99DaysAfterAtartingARTCategory12Line63ColumnDInTheTemplateNumerator2(),
                mappings));

    CAT12PREGNANTNUMERATOR99DAYS.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTNUMERATOR99DAYS.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTNUMERATOR99DAYS.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTNUMERATOR99DAYS.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Adultos PARTE 2

    // Categoria 12 Adultos Primeira Linha

    final CohortIndicator CAT12ADULTDENOMINATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTOMINADORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator(),
                mappings));

    CAT12ADULTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTDENOMINATORFIRSTLINE.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12ADULTNUMERATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDNumerator(),
                mappings));

    CAT12ADULTNUMERATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTNUMERATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTNUMERATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTNUMERATORFIRSTLINE.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Adultos Segunda Linha

    final CohortIndicator CAT12ADULTDENOMINATORSECONDLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTDENOMINATORSECONDLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(),
                mappings));

    CAT12ADULTDENOMINATORSECONDLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTDENOMINATORSECONDLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTDENOMINATORSECONDLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTDENOMINATORSECONDLINE.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12ADULTNUMERATORSECONDLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12ADULTNUMERATORSECONDLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(),
                mappings));

    CAT12ADULTNUMERATORSECONDLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12ADULTNUMERATORSECONDLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12ADULTNUMERATORSECONDLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12ADULTNUMERATORSECONDLINE.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Adultos PARTE 2

    // Categoria 12 Criancas Primeira Linha

    final CohortIndicator CAT12CHILDRENDENOMINATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENDENOMINATORSECONDLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDDenominator(),
                mappings));

    CAT12CHILDRENDENOMINATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENDENOMINATORFIRSTLINE.addParameter(
        new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12CHILDRENNUMERATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENNUMERATORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findAChildrenInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line69ColumnDNumerator(),
                mappings));

    CAT12CHILDRENNUMERATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENNUMERATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENNUMERATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENNUMERATORFIRSTLINE.addParameter(new Parameter("location", "location", Date.class));

    // Categoria 12 Criancas Segunda Linha

    final CohortIndicator CAT12CHILDRENDENOMINATORSECONDLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENDENOMINATORSECONDLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnEDenominator(),
                mappings));

    CAT12CHILDRENDENOMINATORSECONDLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENDENOMINATORSECONDLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENDENOMINATORSECONDLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENDENOMINATORSECONDLINE.addParameter(
        new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12CHILDRENNUMERATORSECONDLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12CHILDRENNUMERATORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findChildrenInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line70ColumnDNumerator(),
                mappings));

    CAT12CHILDRENNUMERATORSECONDLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12CHILDRENNUMERATORSECONDLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12CHILDRENNUMERATORSECONDLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12CHILDRENNUMERATORSECONDLINE.addParameter(
        new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12PREGNANTDENOMINATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12PREGNANTDENOMINATORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(),
                mappings));

    CAT12PREGNANTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTDENOMINATORFIRSTLINE.addParameter(
        new Parameter("location", "location", Date.class));

    final CohortIndicator CAT12PREGNANTNUMERATORFIRSTLINE =
        this.eptsGeneralIndicator.getIndicator(
            "CAT12PREGNANTNUMERATORFIRSTLINE",
            EptsReportUtils.map(
                this.mqCohortQueries
                    .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator(),
                mappings));

    CAT12PREGNANTNUMERATORFIRSTLINE.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT12PREGNANTNUMERATORFIRSTLINE.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT12PREGNANTNUMERATORFIRSTLINE.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT12PREGNANTNUMERATORFIRSTLINE.addParameter(new Parameter("location", "location", Date.class));

    // Adicionando os Denominadores da Categoria 13 Parte I

    // 15+
    final CohortIndicator CAT13ADULT15CVDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT13ADULT15CVDENOMINATOR",
            EptsReportUtils.map(
                this.mQCategory13Section1CohortQueries.findDenominatorB(), mappings));

    CAT13ADULT15CVDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CAT13ADULT15CVDENOMINATOR.addParameter(
        new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CAT13ADULT15CVDENOMINATOR.addParameter(
        new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CAT13ADULT15CVDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    // 0-4

    final CohortIndicator CVDENOMINATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CAT1304CVDENOMINATOR",
            EptsReportUtils.map(
                this.mQCategory13Section1CohortQueries.findDenominatorB(), mappings));

    CVDENOMINATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CVDENOMINATOR.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CVDENOMINATOR.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CVDENOMINATOR.addParameter(new Parameter("location", "location", Date.class));

    final CohortIndicator CVNUMERATOR =
        this.eptsGeneralIndicator.getIndicator(
            "CVNUMERATOR",
            EptsReportUtils.map(
                this.mQCategory13Section1CohortQueries.findFinalNumeratorC(), mappings));

    CVNUMERATOR.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    CVNUMERATOR.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    CVNUMERATOR.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    CVNUMERATOR.addParameter(new Parameter("location", "location", Date.class));

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSDENOMINATOR",
        "CAT13CV15PLUSDENOMINATOR",
        EptsReportUtils.map(CVDENOMINATOR, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04DENOMINATOR",
        "CAT13CV04DENOMINATOR",
        EptsReportUtils.map(CVDENOMINATOR, mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59DENOMINATOR",
        "CAT13CV59DENOMINATOR",
        EptsReportUtils.map(CVDENOMINATOR, mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014DENOMINATOR",
        "CAT13CV1014DENOMINATOR",
        EptsReportUtils.map(CVDENOMINATOR, mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT13CV15PLUSNUMERATOR",
        "CAT13CV15PLUSNUMERATOR",
        EptsReportUtils.map(CVNUMERATOR, mappings),
        "age=15+");

    dataSetDefinition.addColumn(
        "CAT13CV04NUMERATOR",
        "CAT13CV04NUMERATOR",
        EptsReportUtils.map(CVNUMERATOR, mappings),
        "age=0-4");

    dataSetDefinition.addColumn(
        "CAT13CV59NUMERATOR",
        "CAT13CV59NUMERATOR",
        EptsReportUtils.map(CVNUMERATOR, mappings),
        "age=5-9");

    dataSetDefinition.addColumn(
        "CAT13CV1014NUMERATOR",
        "CAT13CV1014NUMERATOR",
        EptsReportUtils.map(CVNUMERATOR, mappings),
        "age=10-14");

    dataSetDefinition.addColumn(
        "CAT3ADULTODENOMINATOR",
        "CAT3ADULTODENOMINATOR",
        EptsReportUtils.map(CAT3ADULTODENOMINATOR, mappings),
        "");

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

    dataSetDefinition.addColumn(
        "CAT11AdultoAPSSPPDENOMINATOR",
        "CAT11AdultoAPSSPPDENOMINATOR",
        EptsReportUtils.map(CAT11AdultoAPSSPPDENOMINATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11AdultoAPSSPPNUMERATOR",
        "CAT11AdultoAPSSPPNUMERATOR",
        EptsReportUtils.map(CAT11AdultoAPSSPPNUMERATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11Adulto1000CVDENOMINATOR",
        "CAT11Adulto1000CVDENOMINATOR",
        EptsReportUtils.map(CAT11Adulto1000CVDENOMINATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11Adulto1000CVNUMERATOR",
        "CAT11Adulto1000CVNUMERATOR",
        EptsReportUtils.map(CAT11Adulto1000CVNUMERATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR",
        "CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR",
        EptsReportUtils.map(CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR",
        "CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR",
        EptsReportUtils.map(CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11Children1000CVDENOMINATOR",
        "CAT11Children1000CVDENOMINATOR",
        EptsReportUtils.map(CAT11Children1000CVDENOMINATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11Children1000CVNUMERATOR",
        "CAT11Children1000CVNUMERATOR",
        EptsReportUtils.map(CAT11Children1000CVNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT11Least9APSSConsultationDENOMINATOR",
        "CAT11Least9APSSConsultationDENOMINATOR",
        EptsReportUtils.map(CAT11Least9APSSConsultationDENOMINATOR, mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT11Least9APSSConsultationNUMERATOR",
        "CAT11Least9APSSConsultationNUMERATOR",
        EptsReportUtils.map(CAT11Least9APSSConsultationNUMERATOR, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINADOR33DAYS",
        "CAT12ADULTDENOMINADOR33DAYS",
        EptsReportUtils.map(CAT12ADULTDENOMINADOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATOR33DAYS",
        "CAT12ADULTNUMERATOR33DAYS",
        EptsReportUtils.map(CAT12ADULTNUMERATOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINADOR99DAYS",
        "CAT12ADULTDENOMINADOR99DAYS",
        EptsReportUtils.map(CAT12ADULTDENOMINADOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATOR99DAYS",
        "CAT12ADULTNUMERATOR99DAYS",
        EptsReportUtils.map(CAT12ADULTNUMERATOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINADOR33DAYS",
        "CAT12CHILDRENDENOMINADOR33DAYS",
        EptsReportUtils.map(CAT12CHILDRENDENOMINADOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATOR33DAYS",
        "CAT12CHILDRENNUMERATOR33DAYS",
        EptsReportUtils.map(CAT12CHILDRENNUMERATOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINADOR99DAYS",
        "CAT12CHILDRENDENOMINADOR99DAYS",
        EptsReportUtils.map(CAT12CHILDRENDENOMINADOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATOR99DAYS",
        "CAT12CHILDRENNUMERATOR99DAYS",
        EptsReportUtils.map(CAT12CHILDRENNUMERATOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTDENOMINADOR33DAYS",
        "CAT12PREGNANTDENOMINADOR33DAYS",
        EptsReportUtils.map(CAT12PREGNANTDENOMINADOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTNUMERATOR33DAYS",
        "CAT12PREGNANTNUMERATOR33DAYS",
        EptsReportUtils.map(CAT12PREGNANTNUMERATOR33DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTENOMINADOR99DAYS",
        "CAT12PREGNANTENOMINADOR99DAYS",
        EptsReportUtils.map(CAT12PREGNANTENOMINADOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTNUMERATOR99DAYS",
        "CAT12PREGNANTNUMERATOR99DAYS",
        EptsReportUtils.map(CAT12PREGNANTNUMERATOR99DAYS, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINATORFIRSTLINE",
        "CAT12ADULTDENOMINATORFIRSTLINE",
        EptsReportUtils.map(CAT12ADULTDENOMINATORFIRSTLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATORFIRSTLINE",
        "CAT12ADULTNUMERATORFIRSTLINE",
        EptsReportUtils.map(CAT12ADULTNUMERATORFIRSTLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTDENOMINATORSECONDLINE",
        "CAT12ADULTDENOMINATORSECONDLINE",
        EptsReportUtils.map(CAT12ADULTDENOMINATORSECONDLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12ADULTNUMERATORSECONDLINE",
        "CAT12ADULTNUMERATORSECONDLINE",
        EptsReportUtils.map(CAT12ADULTNUMERATORSECONDLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINATORFIRSTLINE",
        "CAT12CHILDRENDENOMINATORFIRSTLINE",
        EptsReportUtils.map(CAT12CHILDRENDENOMINATORFIRSTLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATORFIRSTLINE",
        "CAT12CHILDRENNUMERATORFIRSTLINE",
        EptsReportUtils.map(CAT12CHILDRENNUMERATORFIRSTLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENDENOMINATORSECONDLINE",
        "CAT12CHILDRENDENOMINATORSECONDLINE",
        EptsReportUtils.map(CAT12CHILDRENDENOMINATORSECONDLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12CHILDRENNUMERATORSECONDLINE",
        "CAT12CHILDRENNUMERATORSECONDLINE",
        EptsReportUtils.map(CAT12CHILDRENNUMERATORSECONDLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTDENOMINATORFIRSTLINE",
        "CAT12PREGNANTDENOMINATORFIRSTLINE",
        EptsReportUtils.map(CAT12PREGNANTDENOMINATORFIRSTLINE, mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT12PREGNANTNUMERATORFIRSTLINE",
        "CAT12PREGNANTNUMERATORFIRSTLINE",
        EptsReportUtils.map(CAT12PREGNANTNUMERATORFIRSTLINE, mappings),
        "");

    this.addColumnsForCategory15(dataSetDefinition, mappings);

    return dataSetDefinition;
  }

  private void addColumnsForCategory15(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {
    // Categoria 15 DENOMINATOR
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_1_2_3_4_DENOMINATOR",
        "CAT15INDICATOR_1_2_3_4_DENOMINATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_1_And_2_and_3_And_4(),
                "CAT15INDICATOR_1_2_3_4_DENOMINATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_5_7_9_11_DENOMINATOR",
        "CAT15INDICATOR_5_7_9_11_DENOMINATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_5_And_7_And_9_And_11(),
                "CAT15INDICATOR_5_7_9_11_DENOMINATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_6_8_10_12_DENOMINATOR",
        "CAT15INDICATOR_6_8_10_12_DENOMINATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries
                    .getDenominatorCategory15_Indicator_6_And_8_And_10_And_12(),
                "CAT15INDICATOR_6_8_10_12_DENOMINATOR",
                mappings),
            mappings),
        "");

    // Categoria 15 NUMERATOR
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_1_NUMERATOR",
        "CAT15INDICATOR_1_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_1(),
                "CAT15INDICATOR_1_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_2_NUMERATOR",
        "CAT15INDICATOR_2_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_2(),
                "CAT15INDICATOR_2_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_3_NUMERATOR",
        "CAT15INDICATOR_3_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_3(),
                "CAT15INDICATOR_3_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_4_NUMERATOR",
        "CAT15INDICATOR_4_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_4(),
                "CAT15INDICATOR_4_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_5_NUMERATOR",
        "CAT15INDICATOR_5_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_5(),
                "CAT15INDICATOR_5_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_6_NUMERATOR",
        "CAT15INDICATOR_6_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_6(),
                "CAT15INDICATOR_6_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_7_NUMERATOR",
        "CAT15INDICATOR_7_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_7(),
                "CAT15INDICATOR_7_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_8_NUMERATOR",
        "CAT15INDICATOR_8_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_8(),
                "CAT15INDICATOR_8_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_9_NUMERATOR",
        "CAT15INDICATOR_9_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_9(),
                "CAT15INDICATOR_9_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_10_NUMERATOR",
        "CAT15INDICATOR_10_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_10(),
                "CAT15INDICATOR_10_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_11_NUMERATOR",
        "CAT15INDICATOR_11_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_11(),
                "CAT15INDICATOR_11_NUMERATOR",
                mappings),
            mappings),
        "");
    dataSetDefinition.addColumn(
        "CAT15INDICATOR_12_NUMERATOR",
        "CAT15INDICATOR_12_NUMERATOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mqCohortCategory15Queries.getNumeratorCategory15_Indicator_12(),
                "CAT15INDICATOR_12_NUMERATOR",
                mappings),
            mappings),
        "");
  }

  private CohortIndicator setIndicatorWithAllParameters(
      CohortDefinition cohortDefinition, String indicatorName, String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    indicator.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    indicator.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
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
