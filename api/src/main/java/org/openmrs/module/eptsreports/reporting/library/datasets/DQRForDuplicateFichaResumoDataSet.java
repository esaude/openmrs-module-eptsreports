package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.EstimatedBirthdateConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GeneralDateConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.PatientProgramConverter;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.DqrDuplicateFichaResumoCohorts;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DQRForDuplicateFichaResumoDataSet extends BaseDataSet {

  private DqrDuplicateFichaResumoCohorts duplicateFichaResumoCohorts;
  private HivMetadata hivMetadata;

  @Autowired
  public DQRForDuplicateFichaResumoDataSet(
      DqrDuplicateFichaResumoCohorts duplicateFichaResumoCohorts, HivMetadata hivMetadata) {
    this.duplicateFichaResumoCohorts = duplicateFichaResumoCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructPatientDataSet() {
    EncounterDataSetDefinition pdd = new EncounterDataSetDefinition();

    pdd.setName("EC1");
    pdd.addParameter(new Parameter("location", "Location", Location.class));
    pdd.addParameter(new Parameter("endDate", "End Date", Date.class));
    pdd.addRowFilter(
        duplicateFichaResumoCohorts.getEncounterQuery(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
        "endDate=${endDate},location=${location}");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");
    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
    pdd.setParameters(getParameters());

    pdd.addColumn("id", new PersonIdDataDefinition(), "");
    pdd.addColumn("nid", getNID(identifierType.getPatientIdentifierTypeId()), "");
    pdd.addColumn("name", nameDef, "");
    pdd.addColumn("DOB", new BirthdateDataDefinition(), "", new BirthdateConverter("dd-MM-yyyy"));
    pdd.addColumn("estimated_birth_date", estimatedDOB(), "", new EstimatedBirthdateConverter());
    pdd.addColumn("sex", new GenderDataDefinition(), "", new GenderConverter());
    pdd.addColumn("age", getAge(), "endDate=${endDate}", null);
    pdd.addColumn("first_entry_date", firstEntryDate(), "", new GeneralDateConverter());
    pdd.addColumn("date_last_updated", dateLastUpdated(), "", new GeneralDateConverter());
    pdd.addColumn(
        "art_program_enrollment_date",
        getArtProgramEnrollmentDate(hivMetadata.getARTProgram()),
        "endDate=${endDate},location=${location}",
        new PatientProgramConverter());
    pdd.addColumn(
        "ficha_resumo_encounter_date",
        getFichaResumoEncounterDate(hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
        "endDate=${endDate},location=${location}",
        new GeneralDateConverter());
    pdd.addColumn(
        "master_card_opening_date",
        getMasterCardOpeningDate(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId()),
        "endDate=${endDate},location=${location}",
        new GeneralDateConverter());
    pdd.addColumn(
        "pre_art_start_date_on_mastercard",
        getPreArtStartDateOnMastercard(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getPreArtStartDate().getConceptId()),
        "endDate=${endDate},location=${location}",
        new GeneralDateConverter());
    pdd.addColumn(
        "art_start_date_on_master_card",
        getArtStartDateOnMastercard(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getARVStartDateConcept().getConceptId()),
        "endDate=${endDate},location=${location}",
        new GeneralDateConverter());

    return pdd;
  }

  private DataDefinition getAge() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("Patient Age at Reporting End Date");
    spdd.addParameter(new Parameter("endDate", "endDate", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        " SELECT p.patient_id ,TIMESTAMPDIFF(YEAR, ps.birthdate, :endDate) AS age FROM patient p INNER JOIN person ps ON p.patient_id=ps.person_id WHERE p.voided=0 AND ps.voided=0";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition getNID(int identifierType) {
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

  private DataDefinition estimatedDOB() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("EDOB");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        " SELECT p.patient_id,pe.birthdate_estimated  FROM patient p INNER JOIN person pe ON p.patient_id=pe.person_id "
            + " WHERE p.voided=0 AND pe.voided=0 ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition firstEntryDate() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("first_entry_date");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql = " SELECT p.patient_id,p.date_created  FROM patient p  " + " WHERE p.voided=0 ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition dateLastUpdated() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("date_last_updated");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql = " SELECT p.patient_id,p.date_changed  FROM patient p  " + " WHERE p.voided=0 ";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition getArtProgramEnrollmentDate(Program artProgram) {
    ProgramEnrollmentsForPatientDataDefinition spdd =
        new ProgramEnrollmentsForPatientDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Location.class));
    spdd.setName("art_program_enrollment_date");
    spdd.setProgram(artProgram);
    spdd.setWhichEnrollment(TimeQualifier.FIRST);
    return spdd;
  }

  private DataDefinition getFichaResumoEncounterDate(int encounterType) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Location.class));
    spdd.setName("ficha_resumo_encounter_date");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        "SELECT tbl.patient_id, tbl.encounter_date FROM "
            + " (SELECT p.patient_id,MAX(e.encounter_datetime) AS encounter_date  FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN encounter_type et ON et.encounter_type_id=e.encounter_type "
            + " WHERE p.voided=0 AND e.voided=0 AND et.retired=0 AND e.encounter_datetime <=:endDate AND et.encounter_type_id="
            + encounterType
            + " GROUP BY p.patient_id) tbl";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition getMasterCardOpeningDate(int encounterType, int conceptId) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Location.class));
    spdd.setName("master_card_opening_date");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        "SELECT p.patient_id,ob.value_datetime  FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN encounter_type et ON et.encounter_type_id=e.encounter_type "
            + " INNER JOIN obs ob ON ob.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND et.retired=0 AND e.encounter_datetime <=:endDate "
            + " AND et.encounter_type_id="
            + encounterType
            + " AND ob.concept_id="
            + conceptId;

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition getPreArtStartDateOnMastercard(int encounterType, int conceptId) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Location.class));
    spdd.setName("pre_art_start_date_on_mastercard");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        "SELECT p.patient_id,ob.value_datetime  FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN encounter_type et ON et.encounter_type_id=e.encounter_type "
            + " INNER JOIN obs ob ON ob.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND et.retired=0 AND e.encounter_datetime <=:endDate "
            + " AND et.encounter_type_id="
            + encounterType
            + " AND ob.concept_id="
            + conceptId;
    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  private DataDefinition getArtStartDateOnMastercard(int encounterType, int conceptId) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.addParameter(new Parameter("endDate", "End Date", Location.class));
    spdd.setName("art_start_date_on_master_card");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        "SELECT p.patient_id,ob.value_datetime  FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + " INNER JOIN encounter_type et ON et.encounter_type_id=e.encounter_type "
            + " INNER JOIN obs ob ON ob.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND et.retired=0 AND e.encounter_datetime <=:endDate "
            + " AND et.encounter_type_id="
            + encounterType
            + " AND ob.concept_id="
            + conceptId;

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }
}
