package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.NextAndPrevDatesCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.OnArtForAtleastXmonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.PoorAdherenceInLastXClinicalCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueries;
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
  @Autowired private GenericCohortQueries genericCohortQueries;

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
            genericCohortQueries.hasCodedObs(
                hivMetadata.getCurrentWHOHIVStageConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                SetComparator.IN,
                null,
                Arrays.asList(
                    hivMetadata.getWho3AdultStageConcept(),
                    hivMetadata.getWho4AdultStageConcept())),
            "onOrAfter=${endDate},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getPoorAdherenceInLast3Visits(), "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                hivMetadata.getAdverseReaction(),
                Arrays.asList(
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
                    hivMetadata.getOtherDiagnosis())),
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
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteOBSConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                RangeComparator.GREATER_THAN,
                750.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Lab",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                RangeComparator.GREATER_THAN,
                750.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Percent",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4PercentConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                RangeComparator.GREATER_THAN,
                15.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4), "effectiveDate=${endDate}"));

    cd.setCompositionString("((Cd4Abs OR Cd4Lab) AND Age) OR (Cd4Percent AND Age)");

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
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteOBSConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                RangeComparator.GREATER_THAN,
                200.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("5-9", 5, 9), "effectiveDate=${endDate}"));

    cd.setCompositionString("(CD4Abs AND Age)");

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
   * D2: Get patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastFeedingAndNotPregnant() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

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
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (breastfeeding AND NOT pregnant)");

    return cd;
  }

  /**
   * D2: Get Patients who are pregnant including those that are pregnant and breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnant() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

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
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND pregnant");

    return cd;
  }

  /**
   * N1: Get the number of active patients on ART who participate in >=1 measured DSD model
   * The indicator has been commented out in this release.
   *
   * @return
   */
 /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModel() {
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
    cd.addSearch(
        "patientsParticipatingInDsdModel",
        EptsReportUtils.map(
            getAllPatientsParticipatingInDsdModel(), "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurrDsdModel AND patientsParticipatingInDsdModel");

    return cd;
  }*/

  /**
   * N1 STABLE: Get all patients who are active and participating in DSD model
   *
   * @return
   */
 /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsInDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("allPatientsInDsdModel AND patientsWhoAreStable");

    return cd;
  }*/

  /**
   * N1 UNSTABLE: Get all patients who are active and participating in DSD model
   *
   * @return
   */
 /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsInDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsInDsdModel AND NOT stablePatientsDsdModel");

    return cd;
  }*/

  /**
   * N1: Get patients who are breastfeeding and not pregnant
   *
   * @return
   */
 /* public CohortDefinition
      getPatientsWhoAreBreastFeedingAndNotPregnantAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N1 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
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
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (breastfeeding AND NOT pregnant)");

    return cd;
  }*/

  /**
   * N1: Get Patients who are pregnant and also breastfeeding
   *
   * @return
   */
  /*public CohortDefinition
      getPatientsWhoArePregnantAndNotBreastFeedingAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N1: Pregnant: includes all pregnant patients");
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
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (pregnant OR breastfeeding)");

    return cd;
  }*/

  /**
   * N1 Get All patients that are participating in DSD Model
   *
   * @return
   */
 /* private CohortDefinition getAllPatientsParticipatingInDsdModel() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("All Patients participating in >=1 measured DSD model");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        DsdQueries.getPatientsParticipatingInDsdModel(
            hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getFamilyApproach().getConceptId(),
            hivMetadata.getAccessionClubs().getConceptId(),
            hivMetadata.getSingleStop().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getCommunityDispensation().getConceptId(),
            hivMetadata.getAnotherModel().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimen().getConceptId()));
    return cd;
  }*/

  /**
   * N2: Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date of
   * their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N2: Number of active, stable, patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "scheduled",
        EptsReportUtils.map(
            getPatientsScheduled(
                hivMetadata.getReturnVisitDateForArvDrugConcept(),
                Arrays.asList(hivMetadata.getARVPharmaciaEncounterType())),
            "location=${location}"));

    cd.setCompositionString("TxCurr AND scheduled");

    return cd;
  }

  /**
   * N2: Get all patients who are scheduled for the next pickup.
   *
   * @return
   */
  private CohortDefinition getPatientsScheduled(
      Concept conceptId, List<EncounterType> encounterTypes) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "scheduledPatients",
            Context.getRegisteredComponents(NextAndPrevDatesCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("conceptId", conceptId);
    cd.addCalculationParameter("encounterTypes", encounterTypes);

    return cd;
  }

  /**
   * N2 STABLE: Get number of active on ART whose next ART pick-up is schedule for 83-97 days after
   * the date of their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupAs3Months",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3Months(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("patientsWithNextPickupAs3Months AND patientsWhoAreStable");

    return cd;
  }

  /**
   * N2 UNSTABLE: Get number of active on ART whose next ART pick-up is schedule for 83-97 days
   * after the date of their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupDate",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3Months(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsWithNextPickupDate AND NOT patientsWhoAreStable");

    return cd;
  }

  /**
   * N2 UNSTABLE: Get Patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnantN2Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N2 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeedingN2",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnantN2",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN2",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN2 AND (breastfeedingN2 AND NOT pregnantN2)");

    return cd;
  }

  /**
   * N2 UNSTABLE: Get Patient who are pregnant and including breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndNotBreastfeedingN2Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N2: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnantN2",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeedingN2",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN2",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN2 AND (pregnantN2 OR breastfeedingN2)");

    return cd;
  }

  /**
   * N3 : Get the number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190Days() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName =
        "N3: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "scheduledN3",
        EptsReportUtils.map(
            getPatientsScheduled(
                hivMetadata.getReturnVisitDateConcept(),
                Arrays.asList(
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType())),
            "location=${location}"));
    cd.setCompositionString("TxCurr AND scheduledN3");

    return cd;
  }

  /**
   * N3 STABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190DaysStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 STABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients", EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("allPatients AND stablePatients");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190DaysUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 UNSTABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190DaysStable(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatients AND NOT stablePatients");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get Patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnantN3Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeedingN3",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnantN3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN3",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN3 AND (breastfeedingN3 AND NOT pregnantN3)");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get Patient who are pregnant and includes breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndNotBreastfeedingN3Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N3: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnantN3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeedingN3",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN3",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN3 AND (pregnantN3 OR breastfeedingN3)");

    return cd;
  }

  /**
   * N4: Get number of active patients on ART that are participating in GAAC at the end of the month
   * prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaac() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N4: Patients who are active and participating in GAAC";

    cd.setName(
        "N4: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "patientsEnrolledOnGaac",
        EptsReportUtils.map(
            getAllPatientsEnrolledOnGaac(), "endDate=${endDate},location=${location}"));

    cd.setCompositionString("TxCurr AND patientsEnrolledOnGaac");

    return cd;
  }

  /**
   * N4 STABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 STABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4Stable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsN4Stable",
        EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("allPatientsN4Stable AND stablePatientsN4Stable");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 UNSTABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4Unstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsN4Unstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaacStable(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsN4Unstable AND NOT stablePatientsN4Unstable");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get Patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnantN4Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeedingN4",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnantN4",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN4",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaacUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN4 AND (breastfeedingN4 AND NOT pregnantN4)");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get Patient who are pregnant and including breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndNotBreastfeedingN4Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N4: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnantN4",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeedingN4",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstableN4",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaacUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstableN4 AND (pregnantN4 OR breastfeedingN4)");

    return cd;
  }

  /**
   * Get All patients who have been enrolled in the GAAC program
   *
   * @return
   */
  private CohortDefinition getAllPatientsEnrolledOnGaac() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("All Patients Enrolled On GAAC");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(DsdQueries.getPatientsEnrolledOnGAAC());
    return cd;
  }
}
