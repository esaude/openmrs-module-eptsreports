package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DQACohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private TxNewCohortQueries txNewCohortQueries;
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;
  @Autowired private HivMetadata hivMetadata;

  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final String mappingsB1M3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";

    cd.setName("Number of patientes who initiated TARV at this HF End Date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1",
        map(
            txNewCohortQueries.getTxNewCompositionCohort("Number of patientes who initiated TARV"),
            mappingsB1M3));
    cd.setCompositionString("B1");
    return cd;
  }

  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    final String mappingsB1M2 =
        "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";

    cd.setName("Number of patientes who initiated TARV at this HF End Date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1",
        map(
            txNewCohortQueries.getTxNewCompositionCohort("Number of patientes who initiated TARV"),
            mappingsB1M2));
    cd.setCompositionString("B1");
    return cd;
  }

  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1M1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final String mappingsB1M1 =
        "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";

    cd.setName("Number of patientes who initiated TARV at this HF End Date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1",
        map(
            txNewCohortQueries.getTxNewCompositionCohort("Number of patientes who initiated TARV"),
            mappingsB1M1));
    cd.setCompositionString("B1");
    return cd;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    final String mappingsB1M3 = "startDate=${startDate+2m},endDate=${endDate},location=${location}";

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "B13",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            mappingsB1M3));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M3));

    definition.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M3));

    definition.setCompositionString("(B13 AND VL) NOT Ex2");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    final String mappingsB1M2 =
        "startDate=${startDate+1m},endDate=${startDate+2m-1d},location=${location}";

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "B13",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            mappingsB1M2));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M2));

    definition.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M2));

    definition.setCompositionString("(B13 AND VL) NOT Ex2");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2B1M1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    final String mappingsB1M1 =
        "startDate=${startDate},endDate=${startDate+1m-1d},location=${location}";

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "B13",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            mappingsB1M1));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M1));

    definition.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsB1M1));

    definition.setCompositionString("(B13 AND VL) NOT Ex2");

    return definition;
  }
}
