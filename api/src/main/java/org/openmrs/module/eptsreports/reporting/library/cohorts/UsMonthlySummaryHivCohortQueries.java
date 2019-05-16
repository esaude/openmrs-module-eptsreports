package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private TbMetadata tbMetadata;

  public CohortDefinition getRegisteredInPreArtBooks1and2() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordPreArtFlowConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getPreArtEncounterType()),
        null);
  }

  public CohortDefinition getRegisteredInArtBooks1and2() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordArtFlowConcept(),
        TimeModifier.FIRST,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getArtEncounterType()),
        null);
  }

  public CohortDefinition getNewlyEnrolledInArtBooks1and2() {
    return getNewlyEnrolled(getRegisteredInPreArtBooks1and2());
  }

  public CohortDefinition getEnrolledByTransfer() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    cd.addSearch("INSCRITOS", map(getRegisteredInPreArtBooks1and2(), mappings));

    CohortDefinition transferredToArtCare =
        hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility();
    cd.addSearch("TRANSFERIDOS", mapStraightThrough(transferredToArtCare));

    cd.setCompositionString("INSCRITOS AND TRANSFERIDOS");

    return cd;
  }

  public CohortDefinition getTransferredOut() {
    return hivCohortQueries.getPatientsInArtCareTransferredOutToAnotherHealthFacility();
  }

  public CohortDefinition getEnrolledInPreArtOrArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("enrolledInPreArtOrArt");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrBefore=${onOrBefore},locationList=${location}";
    cd.addSearch("LIVROPRETARV", map(getRegisteredInPreArtBooks1and2(), mappings));
    cd.addSearch("LIVROTARV", map(getRegisteredInArt(), mappings));

    cd.setCompositionString("LIVROPRETARV OR LIVROTARV");

    return cd;
  }

  public CohortDefinition getInPreArtWhoScreenedForTb() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForTb = map(getTbScreening(), mappings);
    return getInPreArtBook1And(screenedForTb);
  }

  public CohortDefinition getInPreArtWhoScreenedForSti() {
    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> screenedForSti = map(getStiScreening(), mappings);
    return getInPreArtBook1And(screenedForSti);
  }

  public CohortDefinition getInPreArtWhoStartedCotrimoxazoleProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis =
        map(getStartedCotrimoxazoleProphylaxis(), mappings);
    return getInPreArtBook1And(startedProphylaxis);
  }

  public CohortDefinition getInPreArtWhoStartedIsoniazidProphylaxis() {
    String mappings = "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}";
    Mapped<CohortDefinition> startedProphylaxis = map(getStartedIsoniazidProphylaxis(), mappings);
    return getInPreArtBook1And(startedProphylaxis);
  }

  /**
   * @param toCompose Mapped cohort of screened patients. Parameters to map are {@code onOrBefore,
   *     onOrAfter} and {@code location}
   * @return Composition cohort of patients who are registered in pre ART Book 1 composed with
   *     {@code toCompose} param using an 'AND' operator.
   */
  private CohortDefinition getInPreArtBook1And(Mapped<CohortDefinition> toCompose) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("INSCRITOS", mapStraightThrough(getNewlyEnrolled(registeredInPreArtBook1())));
    cd.addSearch("COMPOSE", toCompose);

    cd.setCompositionString("INSCRITOS AND COMPOSE");

    return cd;
  }

  public CohortDefinition getAbandonedPreArt() {
    return hivCohortQueries.getPatientsInArtCareWhoAbandoned();
  }

  public CohortDefinition getDeadDuringPreArt() {
    return hivCohortQueries.getPatientsInArtCareWhoDied();
  }

  public CohortDefinition getInPreArtWhoInitiatedArt() {
    return hivCohortQueries.getPatientsInArtCareWhoInitiatedArt();
  }

  /**
   * @param preArtBook A cohort of patients registered in one of the pre ART books
   * @return Patients enrolled in pre ART excluding those transferred in
   */
  private CohortDefinition getNewlyEnrolled(CohortDefinition preArtBook) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    cd.addSearch("LIVRO", map(preArtBook, mappings));

    CohortDefinition transferredToArtCare =
        hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility();
    cd.addSearch("TRANSFERIDOSDE", mapStraightThrough(transferredToArtCare));

    cd.setCompositionString("LIVRO NOT TRANSFERIDOSDE");

    return cd;
  }

  private CohortDefinition getRegisteredInArt() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordArtFlowConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getArtEncounterType()),
        null);
  }

  private CohortDefinition getTbScreening() {
    List<Concept> values =
        Arrays.asList(commonMetadata.getNoConcept(), commonMetadata.getYesConcept());
    return genericCohortQueries.hasCodedObs(
        tbMetadata.getTbScreeningConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        values);
  }

  private CohortDefinition getStiScreening() {
    List<Concept> values =
        Arrays.asList(commonMetadata.getNoConcept(), commonMetadata.getYesConcept());
    return genericCohortQueries.hasCodedObs(
        commonMetadata.getStiScreeningConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getARVPediatriaSeguimentoEncounterType()),
        values);
  }

  private CohortDefinition registeredInPreArtBook1() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordPreArtFlowConcept(),
        TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getPreArtEncounterType()),
        Arrays.asList(hivMetadata.getPreArtBook1Concept()));
  }

  private CohortDefinition getStartedCotrimoxazoleProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("startedCotrimoxazoleProphylaxis");
    cd.setQuestion(commonMetadata.getCotrimoxazoleProphylaxisStartDateConcept());
    cd.setTimeModifier(TimeModifier.ANY);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getARVPediatriaSeguimentoEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }

  private CohortDefinition getStartedIsoniazidProphylaxis() {
    DateObsCohortDefinition cd = new DateObsCohortDefinition();
    cd.setName("startedIsoniazidProphylaxis");
    cd.setQuestion(commonMetadata.getIsoniazidProphylaxisStartDateConcept());
    cd.setTimeModifier(TimeModifier.FIRST);

    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getARVPediatriaSeguimentoEncounterType());
    cd.setEncounterTypeList(encounterTypes);

    cd.setOperator1(RangeComparator.GREATER_EQUAL);
    cd.setOperator2(RangeComparator.LESS_EQUAL);

    cd.addParameter(new Parameter("value1", "After Date", Date.class));
    cd.addParameter(new Parameter("value2", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));

    return cd;
  }
}
