package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.eptsreports.reporting.library.datasets.TxNewDataset;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class TXNewDatasetFGHLiveTest extends DefinitionsFGHLiveTest {
  @Autowired private TxNewDataset txNewDataset;

  @Test
  public void constructTxNewDataset() throws EvaluationException {
    DataSetDefinition cd = txNewDataset.constructTxNewDataset();

    DataSet result = evaluateDatasetDefinition(cd);
    Assert.assertNotNull(result);
  }
}
