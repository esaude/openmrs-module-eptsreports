package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private GenericCohortQueries genericCohortQueries;

  /**
   * NOTIFIED TB TREATMENT PATIENTS AT TARV: DIFFERENT SOURCES
   *
   * @param parameterValues
   * @return
   */
  public CompositionCohortDefinition getNotifiedTBTreatmentPatientsOnART(
      Map<String, Object> parameterValues) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV: DIFERENTES FONTES");
    cd.setDescription(
        "São pacientes notificados do tratamento de tuberculose notificados em diferentes fontes: Antecedentes clinicos adulto e pediatria, seguimento, rastreio de tb, livro de TB.");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    InProgramCohortDefinition TBPROGRAMA =
        (InProgramCohortDefinition)
            genericCohortQueries.createInProgram("InTBProgram", tbMetadata.getTBProgram());
    TBPROGRAMA.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

    CodedObsCohortDefinition INICIOST =
        (CodedObsCohortDefinition)
            genericCohorts.hasCodedObs(
                tbMetadata.getTBTreatmentPlanConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    tbMetadata.getTBProcessoEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                Arrays.asList(tbMetadata.getTBTreatmentPlanIniciarConcept()),
                parameterValues);

    DateObsCohortDefinition DATAINICIO =
        (DateObsCohortDefinition)
            genericCohorts.hasDateObs(
                tbMetadata.getStartDrugsConcept(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                Arrays.asList(
                    tbMetadata.getTBLivroEncounterType(),
                    tbMetadata.getTBProcessoEncounterType(),
                    tbMetadata.getTBRastreioEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
                RangeComparator.GREATER_EQUAL,
                RangeComparator.LESS_EQUAL,
                parameterValues);
    cd.addSearch(
        "TBPROGRAMA",
        EptsReportUtils.map(TBPROGRAMA, "onOrAfter=${startDate},onOrBefore=${endDate}"));
    cd.addSearch("INICIOST", EptsReportUtils.map(INICIOST, mappings));
    cd.addSearch("DATAINICIO", EptsReportUtils.map(DATAINICIO, mappings));
    cd.setCompositionString("TBPROGRAMA OR INICIOST OR DATAINICIO");

    return cd;
  }

  private void addStartEndDatesAndLocationParameters(CompositionCohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /** PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV ?? */
  public CompositionCohortDefinition
      getNonVoidedPatientsAtProgramStateWithinStartAndEndDatesAtLocation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("PACIENTES NOTIFICADOS DO TRATAMENTO DE TB NO SERVICO TARV");
    cd.setDescription(
        "Pacientes notificados do tratamento de TB no serviço tarv em todas as fontes (novos inícios e activos)");
    addStartEndDatesAndLocationParameters(cd);
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    Program artProgram = hivMetadata.getARTProgram();
    CohortDefinition TRANSFDEPRG =
        genericCohortQueries.getPatientsBasedOnPatientStates(
            artProgram.getProgramId(),
            hivMetadata
                .getTransferredFromOtherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId());
    SqlCohortDefinition INICIO =
        (SqlCohortDefinition)
            genericCohortQueries.generalSql(
                "",
                TXTBQueries.arvTreatmentIncludesTransfersFromWithKnownStartData(
                    artProgram.getConcept().getConceptId(),
                    hivMetadata.getStartDrugsConcept().getConceptId(),
                    hivMetadata.gethistoricalDrugStartDateConcept().getConceptId(),
                    artProgram.getProgramId(),
                    hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId()));
    cd.addSearch("TRANSFDEPRG", EptsReportUtils.map(TRANSFDEPRG, mappings));
    cd.addSearch("INICIO", EptsReportUtils.map(INICIO, mappings));
    cd.setCompositionString("INICIO NOT TRANSFDEPRG");
    return cd;
  }
}
