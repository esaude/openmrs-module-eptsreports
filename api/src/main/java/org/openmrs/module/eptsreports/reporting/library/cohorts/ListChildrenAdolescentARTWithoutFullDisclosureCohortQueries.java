package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries {

  private final AgeCohortQueries ageCohortQueries;
  private final HivMetadata hivMetadata;
  private final ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries(
      AgeCohortQueries ageCohortQueries,
      HivMetadata hivMetadata,
      ResumoMensalCohortQueries resumoMensalCohortQueries) {
    this.ageCohortQueries = ageCohortQueries;
    this.hivMetadata = hivMetadata;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
  }

  /**
   * All patients must be officially enrolled on ART Service at the end of the reporting period in
   * the specified health facility. has “Processo Clínico Parte A” registered in health facility or
   * has been enrolled in “SERVICO TARV – CUIDADO” program in health facility or has been enrolled
   * in “SERVICO TARV – TRATAMENTO” in health facility or has a Ficha Resumo (Master Card)
   * registered in health facility Children and Adolescent between 8 and on ART BASE COHORT
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBaseCohortForAdolescent() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("List Children Adolescent ART Without Full Disclosure - base cohort");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("age", 8, 14), "effectiveDate=${endDate}"));
    cd.addSearch(
        "B13",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth(false),
            "startDate=${endDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("age AND B13");
    return cd;
  }

  public CohortDefinition getTotalPatientsWithoutDisclosure() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total patients without full disclosure");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "ALL",
        EptsReportUtils.map(
            getBaseCohortForAdolescent(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "R",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getRevealdConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("ALL AND NOT R");
    return cd;
  }

  public CohortDefinition getTotalPatientsWithPartialDisclosure() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total patients with partial disclosure");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "ALL",
        EptsReportUtils.map(
            getBaseCohortForAdolescent(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "R",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getRevealdConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "P",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getPartiallyRevealedConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("(ALL AND P) AND NOT R");
    return cd;
  }

  public CohortDefinition getTotalPatientsNotRevealedDisclosure() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total patients with not revealed disclosure");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "ALL",
        EptsReportUtils.map(
            getBaseCohortForAdolescent(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "R",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getRevealdConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "P",
        EptsReportUtils.map(
            getTotalPatientsWithPartialDisclosure(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "N",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getNotRevealedConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("(ALL AND N) AND NOT (R OR P)");
    return cd;
  }

  public CohortDefinition getAdolescentWithBlankDisclosure() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Adloscent with blank results");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "ALL",
        EptsReportUtils.map(
            getBaseCohortForAdolescent(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "R",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getRevealdConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "P",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getPartiallyRevealedConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "NR",
        EptsReportUtils.map(
            getAdolescentsWithRdMarkedAnyWhereByEndDate(
                hivMetadata.getNotRevealedConcept().getConceptId()),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("ALL AND NOT (R OR P OR NR)");

    return cd;
  }

  /**
   * Number of Children and Adolescent between 8 and 14 currently on ART with RD marked as any of
   * “N” (Não) , “P” (Partial) “T” Any results that will appear on encounter 35 and question 6340
   *
   * @param valueCoded
   * @return CohortDefinition
   */
  public CohortDefinition getAdolescentsWithRdMarkedAnyWhereByEndDate(int valueCoded) {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("answer", valueCoded);
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Adolescent patients with disclosures filled by end of the reporting period");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        " SELECT p.patient_id FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND e.encounter_datetime <= :endDate "
            + " AND e.location_id=:location AND o.value_coded= ${answer}";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }
}
