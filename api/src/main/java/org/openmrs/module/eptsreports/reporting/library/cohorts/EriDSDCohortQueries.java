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
import org.openmrs.module.reporting.common.SetComparator;
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
  public CohortDefinition getAllPatientsWhoAreActiveAndStable() {
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

    cd.setCompositionString("(1 AND 2) AND NOT (3 OR 4) AND 5");

    return cd;
  }

  /**
   * Filter patients (from 4) who are considered stable according to criteria 5: a,b,c,d,e,f
   *
   * @return
   */
  private CohortDefinition getPatientsWhoAreStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Patients who are stable");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("A", EptsReportUtils.map(getPatientsWhoAreStableA(), "onOrBefore=${endDate}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            getCD4CountAndCD4PercentCombined(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            getValueCoded(
                hivMetadata.getCurrentWHOHIVStageConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                hivMetadata.getWho3AdultStageConcept(),
                hivMetadata.getWho4AdultStageConcept()),
            "onOrAfter=${endDate},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getPoorAdherenceInLast3Visits(), "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "F",
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

    cd.setCompositionString("(A OR B OR C OR D OR E OR F)");

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
   * One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 and <=9) 5C (i) 5C (ii)
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
        "CI",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent1(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "CII",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent2(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(CI OR CII)");

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

    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "End Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

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

    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "End Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    cd.setName("Coded Values for " + concept);
    cd.setQuestion(concept);
    if (answers.length > 0) {
      cd.setValueList(Arrays.asList(answers));
    }

    cd.setEncounterTypeList(
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()));
    cd.setTimeModifier(timeModifier);
    cd.setOperator(SetComparator.IN);

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

  /**
   * D2: Number of active, unstable, patients on ART
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName =
        "All  patients  (adults  and  children)  currently  receiving  treatment (TxCurr)";

    cd.setName("Patients who are unstable");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStablePatients",
        EptsReportUtils.map(
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurr AND NOT activeAndStablePatients");

    return cd;
  }

  /**
   * Get patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastFeedingAndNotPregnant() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "All patients on ART (TxCurr)";

    cd.setName(
        "Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("TxCurr AND (breastfeeding AND NOT pregnant)");

    return cd;
  }

  /**
   * Get Patients who are pregnant and not breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndNotBreastFeeding() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "All patients on ART (TxCurr)";

    cd.setName("Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("TxCurr AND (pregnant AND NOT breastfeeding)");

    return cd;
  }

  /**
   * N1: Get the number of active patients on ART who participate in >=1 measured DSD model
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "All patients (Adult and Children) included in TxCurr";

    cd.setName("N1: Number of active patients on ART who participate in >=1 measured DSD model");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurrDsdModel",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurrDsdModel");

    return cd;
  }

  /**
   * N1: Get all patients who are active and participating in DSD model stable
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurrStable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurrStable");

    return cd;
  }

  /**
   * N1: Get all patients who are active and participating in DSD model unstable
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurrUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurrUnstable");

    return cd;
  }
}
