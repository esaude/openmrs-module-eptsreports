package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnArt")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArt() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("ADULT NOT TRANSFERED IN");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-ADULT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.setCompositionString("START-ART-ADULT NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3RF12Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("First Consult IN Inclusion Period");

    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";

    definition.addSearch(
        "RF11-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator(),
            mappings));

    definition.addSearch(
        "RF12-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF12",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator),
            mappings));

    definition.setCompositionString("RF11-DENOMINATOR AND RF12-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF13");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("First Consult IN Inclusion Period");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";

    definition.addSearch(
        "RF13-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator(),
            mappings));

    definition.addSearch(
        "RF14-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF12",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator),
            mappings));

    definition.setCompositionString("RF13-DENOMINATOR AND RF14-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF15");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN OR PREGNANT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF16");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF15-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL-ASSESSMENT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentInLastConsultation),
            mappings));

    definition.setCompositionString("RF15-DENOMINATOR AND NUTRITIONAL-ASSESSMENT");

    return definition;
  }

  @DocumentedDefinition(
      value = "findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator")
  public CohortDefinition
      findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    definition.addSearch(
        "RF16",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator(),
            mappings));
    definition.addSearch(
        "RF8",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF8",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));
    definition.setCompositionString("(RF16 AND RF8) NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITONAL ASSESSMENT");

    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF17-DENOMINATOR",
        EptsReportUtils.map(
            this.findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL_ASSESSMENT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentInLastConsultation),
            mappings));
    definition.setCompositionString("(RF17-DENOMINATOR AND NUTRITIONAL-ASSESSMENT)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Categoria 5 Pediátrico - Denominador");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF19",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN AND RF19-DENOMINATOR NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Categoria 5 Pediátrico - Numerador");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator(),
            mappings));

    definition.addSearch(
        "RF20-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF20",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod),
            mappings));

    definition.setCompositionString("RF19-DENOMINATOR AND RF20-NUMERATOR");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT DAM-DAG");

    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF5",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF5",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTRF05),
            mappings));

    definition.addSearch(
        "RF8",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF8",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT-DAM-DAG",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL-ASSESSMENT-DAM-DAG",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentDAMandDAGInLastConsultation),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));
    definition.setCompositionString(
        "(RF5 AND RF8 AND NUTRITIONAL-ASSESSMENT-DAM-DAG) NOT (TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT ATPU");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF21",
        EptsReportUtils.map(
            this
                .findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator(),
            mappings));

    definition.addSearch(
        "NUTRITIONAL-ASSESSMENT-ATPU",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL ASSESSMENT-ATPU",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentDAMandDAGAndATPUInLastConsultation),
            mappings));

    definition.setCompositionString("RF21 AND NUTRITIONAL-ASSESSMENT-ATPU");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator")
  public CohortDefinition
      findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL ASSESSMENT-ATPU",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF12Numerator")
  public CohortDefinition
      findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF12Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF12");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF11-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientWhoAreNewlyEnrolledOnARTHaveAreLastClinicalConsultationDuringRevisionPeriodCategory6RF11Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6),
            mappings));

    definition.setCompositionString("RF11-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF13");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF14Numerator")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF14Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF14");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF13-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF13Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6),
            mappings));

    definition.setCompositionString("RF13-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF15");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString("PREGNANT NOT (TB-ACTIVE OR TRANSFERED-IN OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "indPregnantNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF16Numerator(")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF16Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF16");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF15-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF15Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6),
            mappings));

    definition.setCompositionString("RF15-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator")
  public CohortDefinition
      findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF17");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TB-ACTIVE",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoDiagnosedWithTBActiveInTheLastConsultationIThePeriodCatetory6),
            mappings));

    definition.setCompositionString("BREASTFEEDING NOT (TB-ACTIVE OR TRANSFERED-IN OR PREGNANT )");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator(")
  public CohortDefinition
      findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF18");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF17-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodExcludingTBActiveCategory6RF17Denominator(),
            mappings));

    definition.addSearch(
        "TB-SCREENING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWwithTBScreeningAtTheLastConsultationOfThePeriodCategory6),
            mappings));

    definition.setCompositionString("RF17-DENOMINATOR AND TB-SCREENING");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator")
  public CohortDefinition
      findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-ADULT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findBreastfeedingNewlyEnrolledOnARTInInclusionPeriodAndHaveTBScreeningAtTheLastConsultationOfThePeriodCategory6RF18Numerator(")
  public CohortDefinition
      findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF20");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.setCompositionString("RF19-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator")
  public CohortDefinition
      findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString(
        "START-ART-CHILDREN NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF22Numerator(")
  public CohortDefinition
      findChildrenNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF22Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF22");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF21-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF21Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.setCompositionString("RF21-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator")
  public CohortDefinition
      findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.setCompositionString(
        "PREGNANT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR BREASTFEEDING)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator(")
  public CohortDefinition
      findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF24");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF23-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator(),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.setCompositionString("RF23-DENOMINATOR AND START-TPI");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-ADULT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-ADULT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdultDesagragation),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7),
            mappings));

    definition.setCompositionString(
        "START-ART-ADULT NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING OR START-TPI OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(")
  public CohortDefinition
      findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF26");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF25-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "END-TPI",
                QualityImprovementQueriesInterface.QUERY.findPatientWhoCompleteTPICategory7),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.setCompositionString(
        "(RF25-DENOMINATOR AND END-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator")
  public CohortDefinition
      findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenBiggerThen1neLess14),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7),
            mappings));

    definition.setCompositionString(
        "(START-ART-CHILDREN AND START-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR PREGNANT OR BREASTFEEDING OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(")
  public CohortDefinition
      findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF28Numerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF28");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF27-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findChildrenWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF27Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "END-TPI",
                QualityImprovementQueriesInterface.QUERY.findPatientWhoCompleteTPICategory7),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.setCompositionString(
        "(RF27-DENOMINATOR AND END-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator")
  public CohortDefinition
      findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    definition.addSearch(
        "START-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPIDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-OUT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoTransferedOutRF07),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "START-TPI4MONTHS",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-TPI",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientWhoStartTPI4MonthsAfterDateOfInclusionCategory7),
            mappings));

    definition.setCompositionString(
        "(PREGNANT AND START-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7 OR TRANSFERED-IN OR TRANSFERED-OUT OR START-TPI4MONTHS)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator(")
  public CohortDefinition
      findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("RF30");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "RF29-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator(),
            mappings));

    definition.addSearch(
        "END-TPI",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "END-TPI",
                QualityImprovementQueriesInterface.QUERY.findPatientWhoCompleteTPICategory7),
            mappings));

    definition.addSearch(
        "TB-ACTIVE-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-ACTIVE-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsDiagnosedWithActiveTBDuringDuringPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-SCREENING-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-SCREENING-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWithPositiveTBScreeningInDurindPeriodCategory7),
            mappings));

    definition.addSearch(
        "TB-TREATMENT-CAT7",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TB-TREATMENT-CAT7",
                QualityImprovementQueriesInterface.QUERY
                    .finPatientHaveTBTreatmentDuringPeriodCategory7),
            mappings));

    definition.setCompositionString(
        "(RF29-DENOMINATOR AND END-TPI) NOT (TB-ACTIVE-CAT7 OR TB-SCREENING-CAT7 OR TB-TREATMENT-CAT7)");

    return definition;
  }
}
