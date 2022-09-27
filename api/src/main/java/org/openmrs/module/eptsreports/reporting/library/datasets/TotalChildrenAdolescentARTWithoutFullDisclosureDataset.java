package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TotalChildrenAdolescentARTWithoutFullDisclosureDataset extends BaseDataSet {

  private final ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
      listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
  private final EptsGeneralIndicator eptsGeneralIndicator;
  private final HivMetadata hivMetadata;

  @Autowired
  public TotalChildrenAdolescentARTWithoutFullDisclosureDataset(
      ListChildrenAdolescentARTWithoutFullDisclosureCohortQueries
          listChildrenAdolescentARTWithoutFullDisclosureCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      HivMetadata hivMetadata) {
    this.listChildrenAdolescentARTWithoutFullDisclosureCohortQueries =
        listChildrenAdolescentARTWithoutFullDisclosureCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructTotalChildrenAdolescentARTWithoutFullDisclosureDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Total adolescent on ART");
    dsd.addParameters(getParameters());
    String mappings = "endDate=${endDate},location=${location}";

    // Totals
    dsd.addColumn(
        "T",
        "Total crianças e adolescentes 8 - 14 anos activos em TARV",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total crianças e adolescentes 8 - 14 anos activos em TARV",
                EptsReportUtils.map(
                    listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                        .getBaseCohortForAdolescent(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "BDR",
        "Crianças e adolescentes 8 - 14 anos activos em TARV com RD em branco",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Crianças e adolescentes 8 - 14 anos activos em TARV com RD em branco",
                EptsReportUtils.map(
                    listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                        .getAdolescentWithBlankDisclosure(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "NDR",
        "Crianças e adolescentes 8 - 14 anos activos em TARV sem RD (N)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Crianças e adolescentes 8 - 14 anos activos em TARV sem RD (N)",
                EptsReportUtils.map(
                    listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                        .getTotalPatientsNotRevealedDisclosure(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "PDR",
        "Crianças e adolescentes 8 - 14 anos activos em TARV com RD Parcial (P)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Crianças e adolescentes 8 - 14 anos activos em TARV com RD Parcial (P)",
                EptsReportUtils.map(
                    listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                        .getTotalPatientsWithPartialDisclosure(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "TDR",
        "Children and adolescents 8 - 14 years old on ART with Total DR (T)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Children and adolescents 8 - 14 years old on ART with Total DR (T)",
                EptsReportUtils.map(
                    listChildrenAdolescentARTWithoutFullDisclosureCohortQueries
                        .getAdolescentsWithRdMarkedAnyWhereByEndDate(
                            hivMetadata.getRevealdConcept().getConceptId()),
                    mappings)),
            mappings),
        "");
    return dsd;
  }
}
