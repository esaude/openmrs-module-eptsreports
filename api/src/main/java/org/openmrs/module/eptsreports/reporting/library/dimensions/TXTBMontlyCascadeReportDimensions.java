package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBDenominatorForTBMontlyCascadeQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TXTBMontlyCascadeReporCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBMontlyCascadeReportDimensions {

  @Autowired private TXTBMontlyCascadeReporCohortQueries montlyCascadeReporCohortQueries;

  @Autowired private TXTBDenominatorForTBMontlyCascadeQueries txtbDenominatorForTBMontlyCascade;

  public CohortDefinitionDimension getClinicalConsultationDimension() {
    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("clinicalConsultation");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));

    dim.addCohortDefinition(
        "clinicalConsultationNewly",
        EptsReportUtils.map(
            montlyCascadeReporCohortQueries.getClinicalConsultationsInLastSixMonths(), mappings));

    dim.addCohortDefinition(
        "clinicalConsultationPreviously",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries
                .gePatientsWithClinicalConsultationsForMoreThanSixMonths(),
            mappings));

    return dim;
  }

  public CohortDefinitionDimension getArtStartRangeDimension() {
    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("artStartState");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));

    dim.addCohortDefinition(
        "txcurrNewlyOnArt",
        EptsReportUtils.map(
            montlyCascadeReporCohortQueries.getPatientsEnrollendOnARTForTheLastSixMonths(),
            mappings));
    dim.addCohortDefinition(
        "txcurrPreviouslyOnArt",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries.getPatientsEnrolledOnArtForMoreThanSixMonths(),
            mappings));

    return dim;
  }

  public CohortDefinitionDimension DiagnosticTest() {
    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("diagnostictest");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));

    dim.addCohortDefinition(
        "genexpert",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getGenExpertTests(), mappings));
    dim.addCohortDefinition(
        "baciloscopia",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getBaciloscopiaTests(), mappings));
    dim.addCohortDefinition(
        "tblam",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getTBLAMTests(), mappings));
    dim.addCohortDefinition(
        "additonalDiagnostic",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getAdditionalTests(), mappings));
    return dim;
  }

  public CohortDefinitionDimension getPositiveTestResults() {
    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("posetiveTestResult");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));

    dim.addCohortDefinition(
        "positiveGenexpert",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getGenExpertPositiveTestResults(), mappings));
    dim.addCohortDefinition(
        "positiveBaciloscopia",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getBaciloscopiaPositiveTestResults(), mappings));
    dim.addCohortDefinition(
        "positiveTblam",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getTBLAMPositiveTestResults(), mappings));
    dim.addCohortDefinition(
        "positiveAdditonalDiagnostic",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getAdditionalPositiveTestResults(), mappings));
    return dim;
  }

  public CohortDefinitionDimension getNegativeTestResults() {
    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("negativeTestResult");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));

    dim.addCohortDefinition(
        "negativeGenexpert",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getGenExpertNegativeTestResults(), mappings));
    dim.addCohortDefinition(
        "negativeBaciloscopia",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getBaciloscopiaNegativeTestResults(), mappings));
    dim.addCohortDefinition(
        "negativeTblam",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getTBLAMNegativeTestResults(), mappings));
    dim.addCohortDefinition(
        "negativeAdditonalDiagnostic",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getAdditionalNegativeTestResults(), mappings));
    return dim;
  }
}
