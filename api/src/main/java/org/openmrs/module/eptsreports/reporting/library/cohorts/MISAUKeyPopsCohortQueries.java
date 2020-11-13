/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MISAUKeyPopsCohortQueries {

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Autowired
  public MISAUKeyPopsCohortQueries(
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      ResumoTrimestralCohortQueries resumoTrimestralCohortQueries) {
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.resumoTrimestralCohortQueries = resumoTrimestralCohortQueries;
  }
  /**
   * <b>Name: Numero de pacientes que iniciaram TARV</b>
   *
   * <p><b>Description:</b> Resumo Mensal B1 AND age>= 15 years
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsInART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who initiated TARV during period");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsInART =
        resumoMensalCohortQueries.getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1();

    cd.addSearch(
        "patientsInART",
        EptsReportUtils.map(
            patientsInART, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsInART");
    return cd;
  }

  /**
   * <b>Name: Numero de pacientes actualmente em TARV</b>
   *
   * <p><b>Description:</b> Resumo Mensal B13 AND age>= 15 years
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsCurrentlyInART() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who are currently in TARV");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsCurrentlyInART =
        resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth();

    cd.addSearch(
        "patientsCurrentlyInART",
        EptsReportUtils.map(
            patientsCurrentlyInART,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsCurrentlyInART");
    return cd;
  }

  /**
   * <b>Name: Numero de pacientes TARV com teste de carga viral</b>
   *
   * <p><b>Description:</b> Dos activos em TARV no fim do trimestre, subgrupo que recebeu um teste
   * de Carga Viral (CV) durante o trimestre (Notificação anual!)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsARTWithViralLoadTest() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients with VL test");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsARTWithVLTest =
        resumoMensalCohortQueries
            .getNumberOfActivePatientsInArtAtTheEndOfTheCurrentMonthHavingVlTestResults();

    cd.addSearch(
        "patientsARTWithVLTest",
        EptsReportUtils.map(
            patientsARTWithVLTest,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsARTWithVLTest");
    return cd;
  }

  /**
   * <b>Name: Numero de pacientes TARV com CV com supressão virológica</b>
   *
   * <p><b>Description:</b> Dos activos TARV no fim do trimestre, subgrupo que recebeu resultado de
   * CV com supressão virológica durante o trimestre (<1000 cópias/mL) (Notificação anual!)
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsARTWithVLSuppression() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients with VL suppression");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsARTWithVLSuppression =
        resumoMensalCohortQueries.getActivePatientsOnArtWhoReceivedVldSuppressionResults();

    cd.addSearch(
        "patientsARTWithVLSuppression",
        EptsReportUtils.map(
            patientsARTWithVLSuppression,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsARTWithVLSuppression");
    return cd;
  }

  /**
   * <b>Name: Número de adultos na coorte 12 meses - inicio de TARV</b>
   *
   * <p><b>Description:</b> Resumo Trimestral A AND age >= 15years
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsStartedARTInLast12Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who started ART in last 12 Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsStartedARTInLast12Months = resumoTrimestralCohortQueries.getA();

    cd.addSearch(
        "patientsStartedARTInLast12Months",
        EptsReportUtils.map(
            patientsStartedARTInLast12Months,
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("patientsStartedARTInLast12Months");
    return cd;
  }

  /**
   * <b>Name: Numero de adultos na coorte 12 meses - Activos em TARV</b>
   *
   * <p><b>Description:</b> Resumo Trimestral A OR B NOT C NOT I NOT J NOT L AND Age >=15years
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsOnARTInLast12Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who are on ART for the last 12 Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition A = resumoTrimestralCohortQueries.getA();
    CohortDefinition B = resumoTrimestralCohortQueries.getB();
    CohortDefinition C = resumoTrimestralCohortQueries.getC();
    CohortDefinition I = resumoTrimestralCohortQueries.getI();
    CohortDefinition J = resumoTrimestralCohortQueries.getJ();
    CohortDefinition L = resumoTrimestralCohortQueries.getL();

    String cohortMappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";

    cd.addSearch("A", EptsReportUtils.map(A, cohortMappings));
    cd.addSearch("B", EptsReportUtils.map(B, cohortMappings));
    cd.addSearch("C", EptsReportUtils.map(C, cohortMappings));
    cd.addSearch("I", EptsReportUtils.map(I, cohortMappings));
    cd.addSearch("J", EptsReportUtils.map(J, cohortMappings));
    cd.addSearch("L", EptsReportUtils.map(L, cohortMappings));

    cd.setCompositionString("(A OR B) AND NOT (C OR I OR J OR L)");
    return cd;
  }
}
