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

  public CohortDefinitionDimension getTxCurrNewlyOnArtDimension() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("TX_CURR In The Last 6 Months Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.addCohortDefinition(
        "txcurrNewlyOnArt",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries.getPatientsEnrollendOnARTForTheLastSixMonths(),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getTxCurrPreviouslyOnArtDimension() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("TX_CURR For More Than 6 Months Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "txcurrPreviouslyOnArt",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries.getPatientsEnrolledOnArtForMoreThanSixMonths(),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getConsultationsInLastSixMonths() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Clinical Consultation in Last 6 Months Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "clinicalConsultationNewly",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries.getClinicalConsultationsInLastSixMonths(),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getConsultationsForMoreThanSixMonths() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Clinical Consultation For More Than 6 Months Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "clinicalConsultationPreviously",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries
                .gePatientsWithClinicalConsultationsForMoreThanSixMonths(),
            mappings));

    return dimension;
  }

  public CohortDefinitionDimension getDiagnosticGenexpertTest() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("GeneExpert Diagnositc Test Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "genexpert",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getGenExpertTests(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension getDiagnosticBaciloscopiaTest() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Baciloscopia Test Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "baciloscopia",
        EptsReportUtils.map(
            this.txtbDenominatorForTBMontlyCascade.getBaciloscopiaTests(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension getDiagnosticTBLAMTest() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("TBLAM Test Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "tblam",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getTBLAMTests(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension getDiagnosticAdditionalOthersTests() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Additional Other Diagnostic Test Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "additonalDiagnostic",
        EptsReportUtils.map(this.txtbDenominatorForTBMontlyCascade.getAdditionalTests(), mappings));

    return dimension;
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

  public CohortDefinitionDimension getGenExpertTests() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("GeneXpert MTB/RIF Dimension");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "genexpert",
        EptsReportUtils.map(
            this.montlyCascadeReporCohortQueries
                .gePatientsWithClinicalConsultationsForMoreThanSixMonths(),
            mappings));

    return dimension;
  }
}
