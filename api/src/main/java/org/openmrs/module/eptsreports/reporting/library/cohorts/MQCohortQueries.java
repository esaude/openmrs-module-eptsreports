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

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("First Consult IN Inclusion Period");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";

    defeinition.addSearch(
        "RF11-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultCategory3RF11Denominator(),
            mappings));

    defeinition.addSearch(
        "RF12-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF12",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator),
            mappings));

    defeinition.setCompositionString("RF11-DENOMINATOR AND RF12-NUMERATOR");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("RF13");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    defeinition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN)");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildrenAndHaveFirstConsultInclusionPeriodCategory3RF14Numerator() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("First Consult IN Inclusion Period");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";

    defeinition.addSearch(
        "RF13-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenCategory3RF13Denominator(),
            mappings));

    defeinition.addSearch(
        "RF14-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF12",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriodCategory3FR12Numerator),
            mappings));

    defeinition.setCompositionString("RF13-DENOMINATOR AND RF14-NUMERATOR");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("RF15");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    defeinition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    defeinition.setCompositionString("START-ART-CHILDREN NOT (TRANSFERED-IN OR PREGNANT)");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator")
  public CohortDefinition
      findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("RF16");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "RF15-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeChildrenDesagragationInAPeriodCategory4RF15Denominator(),
            mappings));

    defeinition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL-ASSESSMENT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentInLastConsultation),
            mappings));

    defeinition.setCompositionString("RF15-DENOMINATOR AND NUTRITIONAL-ASSESSMENT");

    return defeinition;
  }

  @DocumentedDefinition(
      value = "findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator")
  public CohortDefinition
      findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("PREGNANTS NEWLY ENRROLED IN ART");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";
    defeinition.addSearch(
        "RF16",
        EptsReportUtils.map(
            this
                .findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodCategory4RF16Numerator(),
            mappings));
    defeinition.addSearch(
        "RF8",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF8",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));
    defeinition.setCompositionString("(RF16 AND RF8) NOT (TRANSFERED-IN)");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentCategory4RF18() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITONAL ASSESSMENT");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "RF17-DENOMINATOR",
        EptsReportUtils.map(
            this.findPregnantHumansNewlyEnrolledOnARTInInclusionPeriodCategory4RF17Denominator(),
            mappings));

    defeinition.addSearch(
        "NUTRITIONAL-ASSESSMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL_ASSESSMENT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentInLastConsultation),
            mappings));
    defeinition.setCompositionString("(RF17-DENOMINATOR AND NUTRITIONAL-ASSESSMENT)");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Categoria 5 Pediátrico - Denominador");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART-CHILDREN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART-CHILDREN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildrenDesagragation),
            mappings));

    defeinition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF19",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriod),
            mappings));

    defeinition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoArePregnantInclusionDateRF08),
            mappings));

    defeinition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "BREASTFEEDING",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreBreastfeedingInclusionDateRF09),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06),
            mappings));

    defeinition.setCompositionString(
        "START-ART-CHILDREN AND RF19-DENOMINATOR NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator")
  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriodCategory5FR20Numerator() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Categoria 5 Pediátrico - Numerador");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "RF19-DENOMINATOR",
        EptsReportUtils.map(
            this
                .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGInclusionPeriodCategory5RF19Denominator(),
            mappings));

    defeinition.addSearch(
        "RF20-NUMERATOR",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF20",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearChildAndHaveFirstConsultMarkedDAMDAGANDATPUSOJAInclusionPeriod),
            mappings));

    defeinition.setCompositionString("RF19-DENOMINATOR AND RF20-NUMERATOR");

    return defeinition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator")
  public CohortDefinition
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGCategory5RF21Denominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT DAM-DAG");
    definition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

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
      findPregnantsNewlyEnrolledOnARTInInclusionPeriodAndHasNutritionalAssessmentDAMandDAGAndATPUCategory5RF22() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("PREGNANTS NEWLY ENRROLED IN ART AND HAS NUTRITIONAL ASSESSMENT ATPU");
    definition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
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
}
