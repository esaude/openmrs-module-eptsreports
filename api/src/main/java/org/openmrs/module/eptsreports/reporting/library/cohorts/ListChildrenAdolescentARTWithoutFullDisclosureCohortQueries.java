package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries {

  private final GenericCohortQueries genericCohortQueries;
  private final AgeCohortQueries ageCohortQueries;
  private final CommonQueries commonQueries;
  private final HivMetadata hivMetadata;

  @Autowired
  public ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries(
      GenericCohortQueries genericCohortQueries,
      AgeCohortQueries ageCohortQueries,
      CommonQueries commonQueries,
      HivMetadata hivMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.ageCohortQueries = ageCohortQueries;
    this.commonQueries = commonQueries;
    this.hivMetadata = hivMetadata;
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
        "base",
        EptsReportUtils.map(
            genericCohortQueries.getBaseCohort(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("age", 8, 14), "effectiveDate=${endDate}"));
    cd.addSearch(
        "art", EptsReportUtils.map(getPatientsOnART(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("(base AND age AND art)");
    return cd;
  }

  /**
   * ART Start Date (Data Início Tarv) Patient’s first drugs pick up date set in Pharmacy form
   * (FILA) by reporting end Date or Date that patient started drugs (ARV PLAN = START DRUGS) during
   * the pharmacy or clinical visits by reporting end Date or●Patient’s first historical start drugs
   * date set in Pharmacy Tool (FILA) or Clinical tools (Ficha de Seguimento Adulto and Ficha de
   * Seguimento Pediatria) or Ficha Resumo - Master Card by reporting end Date or●Date that Patient
   * was enrolled in ART Program by reporting end Date or Patient’s first drug pick-up date set on
   * Recepção Levantou ARV – Master Card with “Levantou ARV”= “Sim” by reporting end Date The system
   * will define the earliest date amongst all sources as the Patients ART Start Date
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsOnART() {
    String query = commonQueries.getARTStartDate(true);
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get patients on ART");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        "SELECT p.patient_id FROM patient p "
            + " INNER JOIN( "
            + query
            + ") art ON p.patient_id=art.patient_id"
            + " WHERE p.voided=0 AND art.first_pickup IS NOT NULL ");
    return cd;
  }

  /**
   * Number of Children and Adolescent between 8 and 14 currently on ART with RD marked as any of
   * “N” (Não) , “P” (Partial) “T”
   *
   * @param valueCoded
   * @return CohortDefinition
   */
  public CohortDefinition getAdolescentsCurrentlyOnArtWithDisclosures(int valueCoded) {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("answer", valueCoded);
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Adolescent patients with disclosures filled");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "SELECT p.patient_id FROM patient p"
            + " INNER JOIN ("
            + " SELECT p.patient_id,MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND e.encounter_datetime <= :endDate "
            + " AND e.location_id=:location GROUP BY p.patient_id) tt ON p.patient_id=tt.patient_id "
            + " INNER JOIN encounter e1 ON p.patient_id=e1.patient_id "
            + " INNER JOIN obs ob ON e1.encounter_id=ob.encounter_id "
            + " WHERE tt.encounter_datetime=e1.encounter_datetime AND p.voided=0 "
            + " AND e1.encounter_type = ${53} AND e1.location_id=:location "
            + " AND e1.voided=0 AND ob.voided=0 AND ob.value_coded= ${answer} ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }

  /**
   * Number of Children and Adolescent between 8 and 14 currently on ART with RD marked
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTotalAdolescentsCurrentlyOnArtWithBlankDisclosures() {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Adolescent patients with blank disclosures made");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "SELECT pp.patient_id FROM patient pp WHERE pp.voided=0 AND pp.patient_id NOT IN("
            + " SELECT p.patient_id FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND e.encounter_datetime <= :endDate "
            + " AND e.location_id=:location)";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }

  /**
   * Number of Children and Adolescent between 8 and 14 currently on ART with RD Value coded value
   * that is NOT "T" Anything else including null
   *
   * @param valueCoded
   * @return
   */
  public CohortDefinition getAdolescentsCurrentlyOnArtWithoutDisclosures(int valueCoded) {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("answer", valueCoded);
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Adolescent patients without full disclosure");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String query =
        "SELECT wfd.patient_id FROM( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND e.encounter_datetime <= :endDate "
            + " AND e.location_id=:location "
            + " GROUP BY p.patient_id) wfd INNER JOIN encounter ee ON wfd.patient_id=ee.patient_id"
            + " INNER JOIN obs ob ON ee.encounter_id=ob.encounter_id "
            + " WHERE ee.voided=0 AND ob.voided=0 AND ee.encounter_datetime <= :endDate "
            + " AND wfd.encounter_datetime=ee.encounter_datetime "
            + " AND ee.encounter_type = ${35} AND ee.location_id=:location "
            + " AND (ob.value_coded NOT IN(${answer}) OR ob.value_coded IS NULL) ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }
}
