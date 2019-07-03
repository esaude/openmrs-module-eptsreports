package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.OnArtForAtleastXmonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.PoorAdherenceInLastXClinicalCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.*;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EriDSDCohortQueries {
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private AgeCohortQueries ageCohortQueries;
  @Autowired private TxNewCohortQueries txNewCohortQueries;
  @Autowired private HivCohortQueries hivCohortQueries;
  @Autowired private HivMetadata hivMetadata;

  /** D1: Number of active, stable, patients on ART. Combinantion of Criteria 1,2,3,4,5 */
  public CohortDefinition getAllPatientsWhosAgeIsGreaterOrEqualTo2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "Number of active, stable, patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("moreThanOrEqual2Years", 2, 200), ""));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "4",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("5", EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("((1 AND 2) AND NOT (3 OR 4)) AND 5");

    return cd;
  }

  /**
   * Filter patients (from 4) who are considered stable according to criteria a,b,c,d,e,f
   *
   * @return
   */
  private CohortDefinition getPatientsWhoAreStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Patients who are stable");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("5A", EptsReportUtils.map(getPatientsWhoAreStableA(), "onOrBefore=${endDate}"));
    cd.addSearch(
        "5B",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(),
            "onOrBefore=${endDate}"));
    cd.addSearch(
        "5C",
        EptsReportUtils.map(
            getCD4CountAndCD4PercentCombined(),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "5D",
        EptsReportUtils.map(
            getValueCoded(
                hivMetadata.getCurrentWHOHIVStageConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                hivMetadata.getWho3AdultStageConcept(),
                hivMetadata.getWho4AdultStageConcept()),
            "onOrAfter=${endDate},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "5E",
        EptsReportUtils.map(
            getPoorAdherenceInLast3Visits(), "onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "5F",
        EptsReportUtils.map(
            getValueCoded(
                hivMetadata.getAdverseReaction(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                hivMetadata.getNeutropenia(),
                hivMetadata.getPancreatitis(),
                hivMetadata.getHepatotoxicity(),
                hivMetadata.getPsychologicalChanges(),
                hivMetadata.getMyopathy(),
                hivMetadata.getSkinAllergy(),
                hivMetadata.getLipodystrophy(),
                hivMetadata.getLacticAcidosis(),
                hivMetadata.getPeripheralNeuropathy(),
                hivMetadata.getDiarrhea(),
                hivMetadata.getOtherDiagnosis()),
            "onOrAfter=${endDate},onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("(5A OR 5B OR 5C OR 5D OR 5E)");

    return cd;
  }

  /**
   * 5A Patients who are on ART for at least 12 months (if patients age >=2 and <=9) or On ART for
   * at least 6 months (if patients age >=10)
   *
   * @return
   */
  private CohortDefinition getPatientsWhoAreStableA() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "onArtAtleastXmonths",
            Context.getRegisteredComponents(OnArtForAtleastXmonthsCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

    return cd;
  }

  /**
   * 5C One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year (if patients age >=2 and <=4) or
   * One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 and <=9)
   *
   * @return
   */
  private CohortDefinition getCD4CountAndCD4PercentCombined() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Only if VL does not exist");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "5CI",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent1(),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "5CII",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent2(),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("(5CI OR 5CII)");

    return cd;
  }

  /**
   * 5C (i) One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year (if patients age >=2 and
   * <=4)
   *
   * @return
   */
  private CohortDefinition getCD4CountAndCD4Percent1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "Cd4Abs",
        EptsReportUtils.map(
            getValueNumeric(hivMetadata.getCD4AbsoluteOBSConcept(), 750.0),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Lab",
        EptsReportUtils.map(
            getValueNumeric(hivMetadata.getCD4AbsoluteConcept(), 750.0),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Percent",
        EptsReportUtils.map(
            getValueNumeric(hivMetadata.getCD4PercentConcept(), 15.0),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4), "effectiveDate=${endDate}"));

    cd.setCompositionString("(Cd4Abs OR Cd4Lab) OR (Cd4Percent AND Age)");

    return cd;
  }

  /**
   * 5C (ii) One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 and <=9)
   *
   * @return
   */
  private CohortDefinition getCD4CountAndCD4Percent2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "CD4Abs",
        EptsReportUtils.map(
            getValueNumeric(hivMetadata.getCD4AbsoluteOBSConcept(), 200.0),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("5-9", 5, 9), "effectiveDate=${endDate}"));

    cd.setCompositionString("(CD4Abs AND Age)");

    return cd;
  }

  /**
   * 5C Generic method to find patients with CD4 Count greater than a given value
   *
   * @param concept
   * @param value
   * @return
   */
  private CohortDefinition getValueNumeric(Concept concept, Double value) {
    NumericObsCohortDefinition cd = new NumericObsCohortDefinition();
    cd.setName("Numeric value based on " + concept);
    cd.setOperator1(RangeComparator.GREATER_THAN);
    cd.setValue1(value);
    cd.setQuestion(concept);
    cd.setEncounterTypeList(
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType(),
            hivMetadata.getMisauLaboratorioEncounterType()));

    return cd;
  }

  /**
   * 5D Generic method to find patients with active clinical condition of WHO stages
   *
   * @param concept
   * @param timeModifier
   * @param answers
   * @return
   */
  private CohortDefinition getValueCoded(
      Concept concept, BaseObsCohortDefinition.TimeModifier timeModifier, Concept... answers) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();

    cd.setName("Coded Values for " + concept.getName());
    cd.setQuestion(concept);
    if (answers.length > 0) {
      cd.setValueList(Arrays.asList(answers));
    }

    cd.setEncounterTypeList(
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()));
    cd.setTimeModifier(timeModifier);

    return cd;
  }

  /**
   * 5E Returns patient with poor adherence in the last 3 visits
   *
   * @return
   */
  private CohortDefinition getPoorAdherenceInLast3Visits() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "Poor Adherence In Last 3 Visits",
            Context.getRegisteredComponents(PoorAdherenceInLastXClinicalCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "On or before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
