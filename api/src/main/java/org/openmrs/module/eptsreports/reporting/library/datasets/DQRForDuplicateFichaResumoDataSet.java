package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.data.converter.*;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.DqrDuplicateFichaResumoCohorts;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
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
    pdd.addSortCriteria("id", SortCriteria.SortDirection.ASC);
    pdd.addSortCriteria("encounterId", SortCriteria.SortDirection.DESC);

    pdd.addColumn("id", new PersonIdDataDefinition(), "");
    pdd.addColumn("encounterId", new EncounterIdDataDefinition(), "");
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
        new EncounterDatetimeDataDefinition(),
        "",
        new GeneralDateConverter());
    pdd.addColumn(
        "master_card_opening_date",
        getMasterCardOpeningDate(
            hivMetadata.getMasterCardEncounterType(),
            hivMetadata.getDateOfMasterCardFileOpeningConcept()),
        "location=${location}",
        new ObsResultsConveter());
    pdd.addColumn(
        "pre_art_start_date_on_mastercard",
        getPreArtStartDateOnMastercard(
            hivMetadata.getMasterCardEncounterType(), hivMetadata.getPreArtStartDate()),
        "location=${location}",
        new ObsResultsConveter());
    pdd.addColumn(
        "art_start_date_on_master_card",
        getArtStartDateOnMastercard(
            hivMetadata.getMasterCardEncounterType(), hivMetadata.getARVStartDateConcept()),
        "location=${location}",
        new ObsResultsConveter());

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

  private DataDefinition getMasterCardOpeningDate(EncounterType encounterType, Concept question) {
    ObsForPersonDataDefinition spdd = new ObsForPersonDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.setName("master_card_opening_date");
    spdd.addEncounterType(encounterType);
    spdd.setQuestion(question);
    spdd.setWhich(TimeQualifier.FIRST);

    return spdd;
  }

  private DataDefinition getPreArtStartDateOnMastercard(
      EncounterType encounterType, Concept question) {
    ObsForPersonDataDefinition spdd = new ObsForPersonDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.setName("pre_art_start_date_on_mastercard");
    spdd.addEncounterType(encounterType);
    spdd.setQuestion(question);
    spdd.setWhich(TimeQualifier.FIRST);

    return spdd;
  }

  private DataDefinition getArtStartDateOnMastercard(
      EncounterType encounterType, Concept question) {
    ObsForPersonDataDefinition spdd = new ObsForPersonDataDefinition();
    spdd.addParameter(new Parameter("location", "Location", Location.class));
    spdd.setName("art_start_date_on_master_card");

    spdd.addEncounterType(encounterType);
    spdd.setQuestion(question);
    spdd.setWhich(TimeQualifier.FIRST);
    return spdd;
  }
}
