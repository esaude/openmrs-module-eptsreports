package org.openmrs.module.eptsreports.reporting.unit.metadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.MetadataLookupException;
import org.openmrs.module.eptsreports.metadata.ProgramsMetadata;
import org.openmrs.module.eptsreports.reporting.unit.PowerMockBaseContextTest;
import org.powermock.api.mockito.PowerMockito;

public class ProgramsMetadataTest extends PowerMockBaseContextTest {

  @Mock private Program hivProgram;

  @Mock private ProgramWorkflow hivInit;

  @Mock private Concept concept;

  @Mock private ConceptName conceptName;

  @Mock private ProgramWorkflowService programWorkflowService;

  @Before
  public void init() {
    PowerMockito.mockStatic(Context.class);
    when(Context.getProgramWorkflowService()).thenReturn(programWorkflowService);
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
    assertEquals(hivProgram, ProgramsMetadata.getProgram("hiv90098-61c3-11e9-8647-d663bd873d93"));
    assertEquals(hivProgram, ProgramsMetadata.getProgram("HIV"));
    assertEquals(hivProgram, ProgramsMetadata.getProgram("hiv"));
    assertEquals(hivProgram, ProgramsMetadata.getProgram("1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramShouldThrowMetadataLookupExceptionWhenNoProgramIsFound()
      throws MetadataLookupException {
    ProgramsMetadata.getProgram("missingProgramLookup");
  }

  @Test
  public void getProgramWorkflowShouldLookUpWorkFlowByNameOrUuidOrId() {
    assertEquals(hivInit, ProgramsMetadata.getProgramWorkflow("hiv", "INITIATION"));
    assertEquals(hivInit, ProgramsMetadata.getProgramWorkflow("hiv", "initiation"));
    assertEquals(
        hivInit,
        ProgramsMetadata.getProgramWorkflow("hiv", "hiv0Init-61c3-11e9-8647-d663bd873d93"));
    assertEquals(hivInit, ProgramsMetadata.getProgramWorkflow("hiv", "1"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramWorkflowShouldThrowMetadataLookupExceptionIfWorkflowIsNotFound()
      throws MetadataLookupException {

    ProgramsMetadata.getProgramWorkflow("hiv", "missingWorkflowLookup");
  }

  @Test
  public void getProgramWorkflowStateShouldLookUpStateByNameOrUuidOrId() {

    ProgramWorkflowState artInitState = mock(ProgramWorkflowState.class);

    when(hivInit.getStateByName("art")).thenReturn(artInitState);
    assertEquals(
        artInitState, ProgramsMetadata.getProgramWorkflowState("hiv", "INITIATION", "art"));

    when(hivInit.getStates()).thenReturn(new HashSet(Arrays.asList(artInitState)));
    when(artInitState.getConcept()).thenReturn(concept);
    when(concept.getName()).thenReturn(conceptName);
    when(conceptName.toString()).thenReturn("art");
    assertEquals(
        artInitState, ProgramsMetadata.getProgramWorkflowState("hiv", "INITIATION", "ART"));

    when(artInitState.getUuid()).thenReturn("hivAInit-61c3-11e9-8647-d663bd873d93");
    assertEquals(
        artInitState,
        ProgramsMetadata.getProgramWorkflowState(
            "hiv", "INITIATION", "hivAInit-61c3-11e9-8647-d663bd873d93"));

    when(artInitState.getId()).thenReturn(new Integer(3));
    assertEquals(artInitState, ProgramsMetadata.getProgramWorkflowState("hiv", "INITIATION", "3"));
  }

  @Test(expected = MetadataLookupException.class)
  public void getProgramWorkflowStateShouldThrowMetadataLookupExceptionIfStateIsNotFound()
      throws MetadataLookupException {
    ProgramsMetadata.getProgramWorkflowState("hiv", "INITIATION", "missingStateLookup");
  }
}
