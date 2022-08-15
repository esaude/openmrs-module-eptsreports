package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.AdolescentDisclosureConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.DateOfPatientFirstConsultationConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.NotApplicableIfNullConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListOfPatientsArtCohortCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListChildrenAdolescentARTWithoutFullDisclosureDataset extends BaseDataSet {

  private final ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries;
  private final CommonQueries commonQueries;
  private final ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
      listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
  private final HivMetadata hivMetadata;

  @Autowired
  public ListChildrenAdolescentARTWithoutFullDisclosureDataset(
      ListOfPatientsArtCohortCohortQueries listOfPatientsArtCohortCohortQueries,
      CommonQueries commonQueries,
      ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
          listChildrenAdolescentARTWithoutFullDisclosureCohortQueries,
      HivMetadata hivMetadata) {
    this.listOfPatientsArtCohortCohortQueries = listOfPatientsArtCohortCohortQueries;
    this.commonQueries = commonQueries;
    this.listChildrenAdolescentARTWithoutFullDisclosureCohortQueries =
        listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructListChildrenAdolescentARTWithoutFullDisclosureDataset() {

    PatientDataSetDefinition pdsd = new PatientDataSetDefinition();
    pdsd.setName("LCA");
    pdsd.setParameters(getParameters());
    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");

    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
    pdsd.addRowFilter(
        listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
            .getAdolescentsCurrentlyOnArtWithoutDisclosures(
                hivMetadata.getRevealdConcept().getConceptId()),
        "endDate=${endDate},location=${location}");

    pdsd.addColumn("patient_id", new PersonIdDataDefinition(), "");

    pdsd.addColumn("nid", getNID(identifierType.getPatientIdentifierTypeId()), "");

    pdsd.addColumn("name", nameDef, "");

    pdsd.addColumn("sex", new GenderDataDefinition(), "", new GenderConverter());
    pdsd.addColumn(
        "age",
        listOfPatientsArtCohortCohortQueries.getAge(),
        "evaluationDate=${endDate}",
        new NotApplicableIfNullConverter());
    pdsd.addColumn(
        "art",
        getArtStartDate(),
        "endDate=${endDate},location=${location}",
        new DateOfPatientFirstConsultationConverter("date"));
    pdsd.addColumn(
        "edrF",
        getAdolescentsCurrentlyOnArtWithDisclosures(),
        "onOrBefore=${endDate},locationList=${location}",
        new AdolescentDisclosureConverter());
    pdsd.addColumn(
        "dpsG",
        getAdolescentsCurrentlyOnArtWithPartialDisclosures(
            hivMetadata.getPartiallyRevealedConcept()),
        "onOrBefore=${endDate},locationList=${location}",
        new DateOfPatientFirstConsultationConverter("date"));
    pdsd.addColumn(
        "dpsH",
        getAdolescentsCurrentlyOnArtWithPartialDisclosuresDifferenceFromReportingDate(
            hivMetadata.getPartiallyRevealedConcept()),
        "onOrBefore=${endDate},locationList=${location}",
        new DateOfPatientFirstConsultationConverter("value"));

    return pdsd;
  }

  public DataDefinition getNID(int identifierType) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("NID");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        " SELECT p.patient_id,pi.identifier  FROM patient p INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id "
            + " INNER JOIN patient_identifier_type pit ON pit.patient_identifier_type_id=pi.identifier_type "
            + " WHERE p.voided=0 AND pi.voided=0 AND pit.retired=0 AND pit.patient_identifier_type_id ="
            + identifierType;

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  public DataDefinition getArtStartDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Get ART Start Date");
    sqlPatientDataDefinition.addParameter(new Parameter("location", "Location", Location.class));
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));

    sqlPatientDataDefinition.setQuery(commonQueries.getARTStartDate(true));

    return sqlPatientDataDefinition;
  }

  private DataDefinition getAdolescentsCurrentlyOnArtWithDisclosures() {
    ObsForPersonDataDefinition obsForPersonDataDefinition = new ObsForPersonDataDefinition();
    obsForPersonDataDefinition.setName("Adolescent patients with disclosures");
    obsForPersonDataDefinition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    obsForPersonDataDefinition.addParameter(
        new Parameter("locationList", "Location", Location.class));
    obsForPersonDataDefinition.setEncounterTypeList(
        Arrays.asList(hivMetadata.getPrevencaoPositivaSeguimentoEncounterType()));
    obsForPersonDataDefinition.setQuestion(
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept());
    obsForPersonDataDefinition.setWhich(TimeQualifier.LAST);
    obsForPersonDataDefinition.setValueCodedList(
        Arrays.asList(
            hivMetadata.getPartiallyRevealedConcept(), hivMetadata.getNotRevealedConcept()));

    return obsForPersonDataDefinition;
  }

  private DataDefinition getAdolescentsCurrentlyOnArtWithPartialDisclosures(Concept valueCoded) {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("answer", valueCoded.getConceptId());
    SqlPatientDataDefinition cd = new SqlPatientDataDefinition();
    cd.setName("Adolescent patients with disclosure of " + valueCoded);
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    String query =
        "SELECT p.patient_id,MIN(e.encounter_datetime) FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND o.value_coded= ${answer} AND e.encounter_datetime <= :onOrBefore "
            + " AND e.location_id=:locationList "
            + " GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }

  private DataDefinition
      getAdolescentsCurrentlyOnArtWithPartialDisclosuresDifferenceFromReportingDate(
          Concept valueCoded) {
    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "6340",
        hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept().getConceptId());
    map.put("answer", valueCoded.getConceptId());
    SqlPatientDataDefinition cd = new SqlPatientDataDefinition();
    cd.setName(
        "Adolescent patients with disclosure of partial disclosure days difference from the reporting date");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    String query =
        "SELECT t.patient_id, DATEDIFF(:onOrBefore,t.encounter_date) AS diff FROM ("
            + " SELECT p.patient_id,MIN(e.encounter_datetime) AS encounter_date FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type = ${35} "
            + " AND o.concept_id=${6340} AND o.value_coded= ${answer} AND e.encounter_datetime <= :onOrBefore "
            + " AND e.location_id=:locationList "
            + " GROUP BY p.patient_id"
            + ") t";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedQuery = sb.replace(query);
    cd.setQuery(replacedQuery);
    return cd;
  }
}
