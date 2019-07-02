package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.OnArtForAtleastXmonthsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
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
        EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("moreThanOrEqual2Years", 2, 200), ""));
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
    cd.addSearch("5",
            EptsReportUtils.map(
                    getPatientsWhoAreStable(),
                    "endDate=${endDate}"));
    // ((1 AND 2) AND NOT (3 OR 4)) AND
    cd.setCompositionString("5");
    return cd;
  }
  // 5 (a, b, c ,d, e, f)
  private CohortDefinition getPatientsWhoAreStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Patients who are stable");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("5A", EptsReportUtils.map(getPatientsWhoAreStableA(), "onOrBefore=${endDate}"));
    cd.addSearch("5B", EptsReportUtils.map(hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), "onOrBefore=${endDate}"));
    cd.addSearch("5C", EptsReportUtils.map(getPatientsWhoAreStableC(), "onOrBefore=${endDate}"));
    cd.addSearch("5D", EptsReportUtils.map(getPatientsWhoAreStableD(), "onOrBefore=${endDate}"));
    cd.addSearch("5E", EptsReportUtils.map(getPatientsWhoAreStableE(), "onOrBefore=${endDate}"));

    return cd;
  }

  // 5 a
  private CohortDefinition getPatientsWhoAreStableA() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "onArtAtleastXmonths",
            Context.getRegisteredComponents(OnArtForAtleastXmonthsCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));

    return cd;
  }

  //5c
  private  CohortDefinition getPatientsWhoAreStableC(){
    return  null;
  }

  //5d
  private CohortDefinition getPatientsWhoAreStableD(){
    return null;
  }

  //5e
  private CohortDefinition getPatientsWhoAreStableE(){
    return null;
  }

  //5f
  private CohortDefinition getPatientsWhoAreStableF(){
    return null;
  }
  private  CohortDefinition getValueNumeric(Concept concept, Double value){
    NumericObsCohortDefinition cd =new NumericObsCohortDefinition();
    cd.setName("Numeric value based on "+ concept);
    cd.setOperator1(RangeComparator.GREATER_THAN);
    cd.setValue1(value);
    cd.setQuestion(concept);
    cd.setEncounterTypeList(Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType(),hivMetadata.getARVPediatriaSeguimentoEncounterType(),hivMetadata.getMisauLaboratorioEncounterType()));

    return cd;
  }

  private CohortDefinition getCD4CountAndCD4Percent(){
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("Cd4Abs", EptsReportUtils.map(getValueNumeric(hivMetadata.getCD4AbsoluteOBSConcept(), 750.0), "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch("Cd4Lab", EptsReportUtils.map(getValueNumeric(hivMetadata.getCD4AbsoluteConcept(),   750.0), "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch("Cd4Percent", EptsReportUtils.map(getValueNumeric(hivMetadata.getCD4PercentConcept(),  15.0), "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch("Age", EptsReportUtils.map(ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4),"effectiveDate=${endDate}"));

    cd.setCompositionString("(Cd4Abs OR Cd4Lab) OR (Cd4Percent AND Age)");

    return cd;
  }

}
