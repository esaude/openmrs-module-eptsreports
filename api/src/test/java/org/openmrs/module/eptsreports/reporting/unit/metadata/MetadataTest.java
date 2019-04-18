package org.openmrs.module.eptsreports.reporting.unit.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.Metadata;
import org.openmrs.module.eptsreports.metadata.MetadataLookupException;
import org.openmrs.test.BaseContextMockTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class MetadataTest extends BaseContextMockTest {

  @Mock private Program hivProgram;

  @Mock private ProgramWorkflow hivInit;

  @Mock private Concept concept;

  @Mock private ConceptName conceptName;

  @Mock private ProgramWorkflowService programWorkflowService;

  private PatientService patientService;

  private ConceptService conceptService;

  private FormService formService;

  private EncounterService encounterService;

  private PersonService personService;

  private LocationService locationService;

  @Before
  public void init() {
    patientService = mock(PatientService.class);
    conceptService = mock(ConceptService.class);
    formService = mock(FormService.class);
    encounterService = mock(EncounterService.class);
    personService = mock(PersonService.class);
    locationService = mock(LocationService.class);
    PowerMockito.mockStatic(Context.class);
    when(Context.getProgramWorkflowService()).thenReturn(programWorkflowService);
    when(Context.getPatientService()).thenReturn(patientService);
    when(Context.getConceptService()).thenReturn(conceptService);
    when(Context.getFormService()).thenReturn(formService);
    when(Context.getEncounterService()).thenReturn(encounterService);
    when(Context.getPersonService()).thenReturn(personService);
    when(Context.getLocationService()).thenReturn(locationService);
  }

  private void setupProgramsMetadata() {
    when(hivProgram.getName()).thenReturn("HIV");
    when(programWorkflowService.getProgramByUuid("hiv90098-61c3-11e9-8647-d663bd873d93"))
        .thenReturn(hivProgram);
    when(programWorkflowService.getProgramByName("HIV")).thenReturn(hivProgram);
    when(programWorkflowService.getAllPrograms()).thenReturn(Arrays.asList(hivProgram));
    when(programWorkflowService.getProgram(1)).thenReturn(hivProgram);
    when(hivProgram.getWorkflowByName("INITIATION")).thenReturn(hivInit);
    when(hivProgram.getAllWorkflows()).thenReturn(new HashSet(Arrays.asList(hivInit)));
    when(hivInit.getConcept()).thenReturn(concept);
    when(concept.getName()).thenReturn(conceptName);
    when(conceptName.toString()).thenReturn("initiation");
    when(hivInit.getUuid()).thenReturn("hiv0Init-61c3-11e9-8647-d663bd873d93");
    when(hivInit.getId()).thenReturn(new Integer(1));
  }

  @Test
  public void getProgramShouldLookUpProgramByUuidOrNameOrId() {
    setupProgramsMetadata();

    assertEquals(hivProgram, Metadata.getProgram("hiv90098-61c3-11e9-8647-d663bd873d93"));
    assertEquals(hivProgram, Metadata.getProgram("HIV"));
    assertEquals(hivProgram, Metadata.getProgram("hiv"));
    assertEquals(hivProgram, Metadata.getProgram("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramShouldThrowMetadataLookupExceptionWhenNoProgramIsFound()
      throws MetadataLookupException {
    setupProgramsMetadata();

    Metadata.getProgram("missingProgramLookup");
  }

  @Test
  public void getProgramWorkflowShouldLookUpWorkFlowByNameOrUuidOrId() {
    setupProgramsMetadata();

    assertEquals(hivInit, Metadata.getProgramWorkflow("hiv", "INITIATION"));
    assertEquals(hivInit, Metadata.getProgramWorkflow("hiv", "initiation"));
    assertEquals(
        hivInit, Metadata.getProgramWorkflow("hiv", "hiv0Init-61c3-11e9-8647-d663bd873d93"));
    assertEquals(hivInit, Metadata.getProgramWorkflow("hiv", "1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramWorkflowShouldThrowMetadataLookupExceptionIfWorkflowIsNotFound()
      throws MetadataLookupException {
    setupProgramsMetadata();

    Metadata.getProgramWorkflow("hiv", "missingWorkflowLookup");
  }

  @Test
  public void getProgramWorkflowStateShouldLookUpStateByNameOrUuidOrId() {
    setupProgramsMetadata();

    ProgramWorkflowState artInitState = mock(ProgramWorkflowState.class);

    when(hivInit.getStateByName("art")).thenReturn(artInitState);
    assertEquals(artInitState, Metadata.getProgramWorkflowState("hiv", "INITIATION", "art"));

    when(hivInit.getStates()).thenReturn(new HashSet(Arrays.asList(artInitState)));
    when(artInitState.getConcept()).thenReturn(concept);
    when(concept.getName()).thenReturn(conceptName);
    when(conceptName.toString()).thenReturn("art");
    assertEquals(artInitState, Metadata.getProgramWorkflowState("hiv", "INITIATION", "ART"));

    when(artInitState.getUuid()).thenReturn("hivAInit-61c3-11e9-8647-d663bd873d93");
    assertEquals(
        artInitState,
        Metadata.getProgramWorkflowState(
            "hiv", "INITIATION", "hivAInit-61c3-11e9-8647-d663bd873d93"));

    when(artInitState.getId()).thenReturn(new Integer(3));
    assertEquals(artInitState, Metadata.getProgramWorkflowState("hiv", "INITIATION", "3"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramWorkflowStateShouldThrowMetadataLookupExceptionIfStateIsNotFound()
      throws MetadataLookupException {
    setupProgramsMetadata();

    Metadata.getProgramWorkflowState("hiv", "INITIATION", "missingStateLookup");
  }

  @Test
  public void getPatientIdentifierTypeShouldLookUpIdTypeByUuidOrNameOrId() {
    setupProgramsMetadata();

    PatientIdentifierType identifierType = new PatientIdentifierType(4);

    when(patientService.getPatientIdentifierTypeByUuid("identifier-c3-11e9-8647-d663bd873d93"))
        .thenReturn(identifierType);
    assertEquals(
        identifierType, Metadata.getPatientIdentifierType("identifier-c3-11e9-8647-d663bd873d93"));

    when(patientService.getPatientIdentifierTypeByName("PrimaryIdentifier"))
        .thenReturn(identifierType);
    assertEquals(identifierType, Metadata.getPatientIdentifierType("PrimaryIdentifier"));

    when(patientService.getPatientIdentifierType(4)).thenReturn(identifierType);
    assertEquals(identifierType, Metadata.getPatientIdentifierType("4"));
  }

  @Test(expected = RuntimeException.class)
  public void getPatientIdentifierTypeShouldThrowRuntimeExceptionIfNoneIsFound()
      throws RuntimeException {
    setupProgramsMetadata();

    Metadata.getPatientIdentifierType("missingIdentifierTypeLookup");
  }

  @Test
  public void getConceptShouldLookupConceptByNameOrUuidOrMappingOrId() {
    when(conceptService.getConceptByUuid("concept000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(concept);
    assertEquals(concept, Metadata.getConcept("concept000-c3-11e9-8647-d663bd873d93"));

    when(conceptService.getConceptByName("height")).thenReturn(concept);
    assertEquals(concept, Metadata.getConcept("height"));

    when(conceptService.getConceptByMapping("CIEL", "HT")).thenReturn(concept);
    assertEquals(concept, Metadata.getConcept("HT:CIEL"));

    when(conceptService.getConcept(1)).thenReturn(concept);
    assertEquals(concept, Metadata.getConcept("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getConceptShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getConcept("missingConceptLookup");
  }

  @Test
  public void getConceptListShouldLookupConceptsByNameOrUuidOrMappingOrId() {
    Concept weight = mock(Concept.class);
    Concept sbp = mock(Concept.class);
    Concept dbp = mock(Concept.class);
    when(conceptService.getConceptByName("weight")).thenReturn(weight);
    when(conceptService.getConceptByMapping("CIEL", "HT")).thenReturn(concept);
    when(conceptService.getConcept(1)).thenReturn(sbp);
    when(conceptService.getConceptByUuid("concept000-c3-11e9-8647-d663bd873d93")).thenReturn(dbp);

    assertEquals(
        Arrays.asList(concept, sbp, weight, dbp),
        Metadata.getConceptList("HT:CIEL,1,weight,concept000-c3-11e9-8647-d663bd873d93"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getConceptListShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    when(conceptService.getConceptByMapping("CIEL", "HT")).thenReturn(concept);
    Metadata.getConceptList("HT:CIEL,weight");
  }

  @Test
  public void getConceptListWithSeparatorShouldLookupConceptsByNameOrUuidOrMappingOrId() {
    Concept weight = mock(Concept.class);
    Concept sbp = mock(Concept.class);
    Concept dbp = mock(Concept.class);
    when(conceptService.getConceptByName("weight")).thenReturn(weight);
    when(conceptService.getConceptByMapping("CIEL", "HT")).thenReturn(concept);
    when(conceptService.getConcept(1)).thenReturn(sbp);
    when(conceptService.getConceptByUuid("concept000-c3-11e9-8647-d663bd873d93")).thenReturn(dbp);

    assertEquals(
        Arrays.asList(concept, sbp, weight, dbp),
        Metadata.getConceptList("HT:CIEL|1|weight|concept000-c3-11e9-8647-d663bd873d93", "\\|"));
  }

  @Test
  public void getConceptListWithSeparatorShouldLookupOneConceptWithoutSeparator() {
    when(conceptService.getConceptByName("height")).thenReturn(concept);
    assertEquals(Arrays.asList(concept), Metadata.getConceptList("height", null));
  }

  @Test(expected = MetadataLookupException.class)
  public void getConceptListWithSeparatorShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    Concept weight = mock(Concept.class);
    when(conceptService.getConceptByName("weight")).thenReturn(weight);
    when(conceptService.getConceptByMapping("CIEL", "HT")).thenReturn(concept);

    Metadata.getConceptList("HT:CIEL|1|weight", "\\|");
  }

  @Test
  public void getFormShouldLookUpFormByUuidOrNameOrId() {
    Form form = mock(Form.class);
    when(formService.getFormByUuid("form000000-c3-11e9-8647-d663bd873d93")).thenReturn(form);
    assertEquals(form, Metadata.getForm("form000000-c3-11e9-8647-d663bd873d93"));

    when(formService.getForm("basic")).thenReturn(form);
    assertEquals(form, Metadata.getForm("basic"));

    when(formService.getForm(1)).thenReturn(form);
    assertEquals(form, Metadata.getForm("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getFormShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getForm("missingFormLookup");
  }

  @Test
  public void getFormListShouldLookupFormsByNameOrUuidOrMappingOrId() {
    Form form = mock(Form.class);
    Form basic = mock(Form.class);
    when(formService.getFormByUuid("form000000-c3-11e9-8647-d663bd873d93")).thenReturn(form);
    when(formService.getForm("basic")).thenReturn(basic);
    assertEquals(
        Arrays.asList(form, basic),
        Metadata.getFormList("form000000-c3-11e9-8647-d663bd873d93,basic"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getFormListShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    Form basic = mock(Form.class);
    when(formService.getForm("basic")).thenReturn(basic);
    Metadata.getFormList("basic,form");
  }

  @Test
  public void getFormListWithSeparatorShouldLookupFormsByNameOrUuidOrMappingOrId() {
    Form form = mock(Form.class);
    Form basic = mock(Form.class);
    when(formService.getFormByUuid("form000000-c3-11e9-8647-d663bd873d93")).thenReturn(form);
    when(formService.getForm("basic")).thenReturn(basic);
    assertEquals(
        Arrays.asList(form, basic),
        Metadata.getFormList("form000000-c3-11e9-8647-d663bd873d93|basic", "\\|"));
  }

  @Test
  public void getFormListWithSeparatorShouldLookupOneFormWithoutSeparator() {
    Form basic = mock(Form.class);
    when(formService.getForm("basic")).thenReturn(basic);
    assertEquals(Arrays.asList(basic), Metadata.getFormList("basic", null));
  }

  @Test(expected = MetadataLookupException.class)
  public void getFormListWithSeparatorShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    Form basic = mock(Form.class);
    when(formService.getForm("basic")).thenReturn(basic);

    Metadata.getFormList("basic, form", "\\|");
  }

  @Test
  public void getEncounterTypeShouldLookUpEncounterTypeByUuidOrNameOrId() {
    EncounterType encounterType = mock(EncounterType.class);
    when(encounterService.getEncounterTypeByUuid("encT000000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(encounterType);
    assertEquals(encounterType, Metadata.getEncounterType("encT000000-c3-11e9-8647-d663bd873d93"));

    when(encounterService.getEncounterType("adultInitial")).thenReturn(encounterType);
    assertEquals(encounterType, Metadata.getEncounterType("adultInitial"));

    when(encounterService.getEncounterType(1)).thenReturn(encounterType);
    assertEquals(encounterType, Metadata.getEncounterType("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getEncounterTypeShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getEncounterType("missingEncounterTypeLookup");
  }

  @Test
  public void getEncounterTypeListShouldLookupEncounterTypesByNameOrUuidOrMappingOrId() {
    EncounterType encounterType = mock(EncounterType.class);
    EncounterType init = mock(EncounterType.class);
    when(encounterService.getEncounterTypeByUuid("encT000000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(encounterType);
    when(encounterService.getEncounterType("adultInit")).thenReturn(init);
    assertEquals(
        Arrays.asList(encounterType, init),
        Metadata.getEncounterTypeList("encT000000-c3-11e9-8647-d663bd873d93,adultInit"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getEncounterTypeListShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    EncounterType encounterType = mock(EncounterType.class);
    when(encounterService.getEncounterType("adultInit")).thenReturn(encounterType);
    Metadata.getFormList("adultInitial,adultReturn");
  }

  @Test
  public void
      getEncounterTypeListWithSeparatorShouldLookupEncounterTypesByNameOrUuidOrMappingOrId() {
    EncounterType adultInit = mock(EncounterType.class);
    EncounterType encounterType = mock(EncounterType.class);
    when(encounterService.getEncounterTypeByUuid("encT000000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(encounterType);
    when(encounterService.getEncounterType("adultInit")).thenReturn(adultInit);
    assertEquals(
        Arrays.asList(encounterType, adultInit),
        Metadata.getEncounterTypeList("encT000000-c3-11e9-8647-d663bd873d93|adultInit", "\\|"));
  }

  @Test
  public void getEncounterTypeListWithSeparatorShouldLookupOneEncounterTypeWithoutSeparator() {
    EncounterType encounterType = mock(EncounterType.class);
    when(encounterService.getEncounterType("adultReturn")).thenReturn(encounterType);
    assertEquals(Arrays.asList(encounterType), Metadata.getEncounterTypeList("adultReturn", null));
  }

  @Test(expected = MetadataLookupException.class)
  public void getEncounterTypeListWithSeparatorShouldThrowMetadataLookupExceptionIfOneIsNotFound()
      throws MetadataLookupException {
    EncounterType encounterType = mock(EncounterType.class);
    when(encounterService.getEncounterType("pedsInitial")).thenReturn(encounterType);

    Metadata.getEncounterTypeList("pedsInitial, adultInitial", "\\|");
  }

  @Test
  public void getRelationshipTypeShouldLookUpRelationshipTypeByUuidOrNameOrId() {
    RelationshipType relationshipType = mock(RelationshipType.class);
    when(personService.getRelationshipTypeByUuid("relT000000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(relationshipType);
    assertEquals(
        relationshipType, Metadata.getRelationshipType("relT000000-c3-11e9-8647-d663bd873d93"));

    when(personService.getRelationshipTypeByName("parent")).thenReturn(relationshipType);
    assertEquals(relationshipType, Metadata.getRelationshipType("parent"));

    when(personService.getRelationshipType(1)).thenReturn(relationshipType);
    assertEquals(relationshipType, Metadata.getRelationshipType("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getRelationshipTypeShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getRelationshipType("missingRelationshipTypeLookup");
  }

  @Test
  public void getLocationShouldLookUpLocationByUuidOrNameOrId() {
    Location location = mock(Location.class);
    when(locationService.getLocationByUuid("locT000000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(location);
    assertEquals(location, Metadata.getLocation("locT000000-c3-11e9-8647-d663bd873d93"));

    when(locationService.getLocation("CapeTown")).thenReturn(location);
    assertEquals(location, Metadata.getLocation("CapeTown"));

    when(locationService.getLocation(1)).thenReturn(location);
    assertEquals(location, Metadata.getLocation("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getLocationShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getLocation("missingLocationLookup");
  }

  //
  @Test
  public void getPersonAttributeTypeShouldLookUpLocationByUuidOrNameOrId() {
    PersonAttributeType personAttributeType = mock(PersonAttributeType.class);
    when(personService.getPersonAttributeTypeByUuid("phone20000-c3-11e9-8647-d663bd873d93"))
        .thenReturn(personAttributeType);
    assertEquals(
        personAttributeType,
        Metadata.getPersonAttributeType("phone20000-c3-11e9-8647-d663bd873d93"));

    when(personService.getPersonAttributeTypeByName("phoneNumber")).thenReturn(personAttributeType);
    assertEquals(personAttributeType, Metadata.getPersonAttributeType("phoneNumber"));

    when(personService.getPersonAttributeType(1)).thenReturn(personAttributeType);
    assertEquals(personAttributeType, Metadata.getPersonAttributeType("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getPersonAttributeTypeShouldThrowMetadataLookupExceptionIfNoneIsFound()
      throws MetadataLookupException {
    Metadata.getPersonAttributeType("missingPersonAttributeTypeLookup");
  }
}
