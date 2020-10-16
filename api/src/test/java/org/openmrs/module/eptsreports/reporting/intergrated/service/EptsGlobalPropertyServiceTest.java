package org.openmrs.module.eptsreports.reporting.intergrated.service;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.module.eptsreports.api.EptsGlobalPropertyService;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EptsGlobalPropertyServiceTest extends DefinitionsTest {

  @Autowired private EptsGlobalPropertyService eptsGlobalPropertyRemover;

  @Before
  public void setup() throws Exception {

    executeDataSet("epts-globalProperty-remover-dataset.xml");
  }

  @Test
  public void removeEptsGlobalPropertiesEntriesShouldRemoveGpByPrefix() {

    List<GlobalProperty> remvedGlobalProperties =
        this.eptsGlobalPropertyRemover.removeEptsGlobalPropertiesEntries("eptsreports");

    assertFalse(remvedGlobalProperties.isEmpty());
    assertEquals(48, remvedGlobalProperties.size());
  }

  @Test
  public void removeEptsGlobalPropertiesEntriesShouldNotRemoveAnyEntry() {

    List<GlobalProperty> remvedGlobalProperties =
        this.eptsGlobalPropertyRemover.removeEptsGlobalPropertiesEntries("unexistant_patter");

    assertTrue(remvedGlobalProperties.isEmpty());
    assertEquals(0, remvedGlobalProperties.size());
  }
}
