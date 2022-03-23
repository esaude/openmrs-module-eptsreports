package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.data.converter.*;
import org.openmrs.module.eptsreports.reporting.library.cohorts.DQACargaViralCohortQueries;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DQACargaViralDataset extends BaseDataSet {

  private final DQACargaViralCohortQueries dQACargaViralCohortQueries;

  @Autowired
  public DQACargaViralDataset(DQACargaViralCohortQueries dQACargaViralCohortQueries) {
    this.dQACargaViralCohortQueries = dQACargaViralCohortQueries;
  }

  public DataSetDefinition constructDQACargaViralDataset() {
    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.addParameter(new Parameter("startDate", "startDate", Date.class));
    pdd.addParameter(new Parameter("endDate", "endDate", Date.class));
    pdd.addParameter(new Parameter("location", "Location", Location.class));
    pdd.setName("DQA Carga Viral");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");

    pdd.addRowFilter(
        dQACargaViralCohortQueries.getBaseCohort(),
        "startDate=${startDate},endDate=${endDate},location=${location}");

    /** Patient counter - Sheet 1: Column A */
    pdd.addColumn("counter", new PersonIdDataDefinition(), "", new ObjectCounterConverter());

    /** 1 - NID - Sheet 1: Column B */
    pdd.addColumn(
        "nid", dQACargaViralCohortQueries.getNID(identifierType.getPatientIdentifierTypeId()), "");

    /** 2 - Sexo - Sheet 1: Column C */
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());

    /** 3 - Idade - Sheet 1: Column D */
    pdd.addColumn(
        "age",
        dQACargaViralCohortQueries.getAge("endDate"),
        "endDate=${endDate}",
        new NotApplicableIfNullConverter());

    /** 4 - Data Início TARV - Sheet 1: Column E */
    pdd.addColumn(
        "inicio_tarv",
        dQACargaViralCohortQueries.getArtStartDate(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new ForwardSlashDateConverter());

    /**
     * 5 - Data de Consulta onde Notificou o Resultado de CV dentro do Período de Revisão - Sheet 1:
     * Column F
     */
    pdd.addColumn(
        "data_consulta_resultado_cv",
        dQACargaViralCohortQueries.getDataNotificouCV(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new ForwardSlashDateConverter());

    /** 6 - Resultado da Carga Viral (Resultado Quantitativo) - Sheet 1: Column G */
    pdd.addColumn(
        "resultado_cv_quantitativo",
        dQACargaViralCohortQueries.getViralLoadQuantitativeResults(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new NotApplicableIfNullConverter());

    /** Resultado da Carga Viral (Resultado Qualitativo) - Sheet 1: Column H */
    pdd.addColumn(
        "resultado_cv_qualitativo",
        dQACargaViralCohortQueries.getViralLoadQualitativeResults(),
        "startDate=${startDate},endDate=${endDate},location=${location}",
        new NotApplicableIfNullConverter());

    return pdd;
  }
}
