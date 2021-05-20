package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.intergrated.utils.DefinitionsFGHLiveTest;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralAPSSCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTCompletationCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class TxRTTCalculationTest extends DefinitionsFGHLiveTest {

  @Autowired private TxRTTCohortQueries txRTTCohortQueries;

  @Autowired private ResumoTrimestralAPSSCohortQueries resumoMensalAPSSCohortQueries;

  @Autowired private TXTBCohortQueries txTBCohortQueries;

  @Autowired private TPTCompletationCohortQueries tptCompletationCohortQueries;

  @Test
  public void shouldFindPatientsNewlyEnrolledInART() throws EvaluationException {

    final Location location = Context.getLocationService().getLocation(398);

    System.out.println(location.getName());
    final Date startDate = DateUtil.getDateTime(2020, 6, 21);
    final Date endDate = DateUtil.getDateTime(2020, 12, 20);

    System.out.println(startDate);
    System.out.println(endDate);

    final Map<Parameter, Object> parameters = new HashMap<>();
    parameters.put(new Parameter("startDate", "Start Date", Date.class), startDate);
    parameters.put(new Parameter("endDate", "End Date", Date.class), endDate);
    parameters.put(new Parameter("location", "Location", Location.class), location);

    CohortDefinition patientsWhoExperiencedIIT = txRTTCohortQueries.getPatientsOnRTT();
    CohortDefinition cohortDefinitionC1 =
        this.resumoMensalAPSSCohortQueries
            .findPatientsWhoAreCurrentlyEnrolledOnArtWithPrevencaoPosetivaD1();

    CohortDefinition numeratorPreviosPeriod =
        this.txTBCohortQueries.getSpecimenSentCohortDefinition();

    CohortDefinition txCurrWithTPTCompletation =
        this.tptCompletationCohortQueries.findTxCurrWithTPTCompletation();

    final EvaluatedCohort evaluateCohortDefinition =
        this.evaluateCohortDefinition(txCurrWithTPTCompletation, parameters);

    System.out.println(evaluateCohortDefinition.getMemberIds().size());
    assertFalse(evaluateCohortDefinition.getMemberIds().isEmpty());

    System.out.println("----------------------------------");

    for (int t : evaluateCohortDefinition.getMemberIds()) {
      System.out.println(t);
    }
  }

  // @Override
  // protected String username() {
  // return "admin";
  // }
  //
  // @Override
  // protected String password() {
  // return "H!$fGH0Mr$";
  // }

  @Override
  protected String username() {
    return "domingos.bernardo";
  }

  @Override
  protected String password() {
    return "dBernardo1";
  }
}
