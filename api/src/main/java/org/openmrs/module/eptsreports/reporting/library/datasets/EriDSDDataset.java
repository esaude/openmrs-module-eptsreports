package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EriDSDDataset extends BaseDataSet {

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructEriDSDDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.setName("DSD Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dsd.setName("total");
    dsd.addColumn(
        "D1T",
        "DSD D1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D1 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D1SNPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D1SNPNBC",
        "Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2T",
        "DSD D2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D2 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D2NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D2NPNBC",
        "Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2BNP",
        "Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2BNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastFeedingAndNotPregnant(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D2PNB",
        "Pregnant (exclude breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2PNB",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(), mappings)),
            mappings),
        "");
    /*dsd.addColumn(
        "NT",
        "DSD N Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NT",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModel(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NSST",
        "DSD N Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NSST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NSNPNB",
        "Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NSNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "NSNPNBC",
        "Stable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NSNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "NUST",
        "DSD N Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NUST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NUNPNB",
        "Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NUNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "NUNPNBC",
        "Unstable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NUNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "NUBNP",
        "N Unstable Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NUBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreBreastFeedingAndNotPregnantAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "NUPB",
        "N Unstable Pregnant (include breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "NUPB",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoArePregnantAndNotBreastFeedingAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");*/
    dsd.addColumn(
        "N2T",
        "DSD N2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3Months(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N2SST",
        "DSD N2 Stable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Stable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N2SNPNBA",
        "N2 Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Stable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N2SNPNBC",
        "N2 Non-pregnant and Non-Breastfeeding Children (<15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Stable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N2UST",
        "DSD N2 Unstable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N2UNPNBA",
        "N2 Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Unstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N2UNPNBC",
        "N2 Non-pregnant and Non-Breastfeeding Children (<15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Unstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N3T",
        "DSD N3 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWithNextConsultationScheduled175To190Days(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N3SST",
        "DSD N3 Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Stable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N3SNPNBA",
        "DSD N3 Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Stable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N3SNPNBC",
        " DSD N3 Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Stable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N3UST",
        "DSD N3 Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N3UNPNBA",
        "DSD N3 Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Unstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N3UNPNBC",
        " DSD N3 Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Unstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N4T",
        "DSD N4 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticpatingInGaac(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4SST",
        "DSD N4 Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Stable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4SNPNBA",
        "DSD N4 Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Stable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N4SNPNBC",
        " DSD N4 Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Stable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N4UST",
        "DSD N4 Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticpatingInGaacUnstable(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4UNPNBA",
        "DSD N4 Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4UNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Unstable(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N4UNPNBC",
        " DSD N4 Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Unstable(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N4UBNP",
        "N4 Patients who are breastfeeding excluding pregnant patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4UBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingAndNotPregnantN4(),
                    mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4UPB",
        "N4: Pregnant: includes breastfeeding patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4UPB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoArePregnantAndBreastfeedingN4(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5T",
        "N5: Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5T",
                EptsReportUtils.map(eriDSDCohortQueries.getActivePatientsOnARTAF(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5EST",
        "N5: Eligible subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5EST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5ENPNBA",
        "N5: Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N5ENPNBC",
        "N5: Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5ENPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreEligible(), mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N5NEST",
        "N5: Not Eligible subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5NEST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5NENPNBA",
        "N5: Not Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5NENPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N5NENPNBC",
        "N5: Not Eligible Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5NENPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsOnMasterCardAFWhoAreNotEligible(), mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N7T",
        "N7: Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7T",
                EptsReportUtils.map(eriDSDCohortQueries.getActivePatientsOnARTDC(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N7EST",
        "N7 : Eligible Sub Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7EST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getActiveARTEligiblePatientsDC(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N7ENPNBA",
        "N7: Eligible Non-Pregnant Non-Breastfeeding adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7ENPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getActiveARTEligiblePatientsDC(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N7ENPNBAC",
        "N7: Eligible Non-Pregnant Non-Breastfeeding adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7ENPNBAC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getActiveARTEligiblePatientsDC(), mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N7NEST",
        "N7 : Non-Eligible Sub-Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7NEST",
                EptsReportUtils.map(eriDSDCohortQueries.getActiveInARTUnstableDC(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N7NENPNBA",
        "N7 : Non-Eligible Non-Pregnant Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7NENPNBA",
                EptsReportUtils.map(eriDSDCohortQueries.getActiveInARTUnstableDC(), mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N7NENPNBC",
        "N7 : Non-eligible Non Pregnant Non Breast Feeding Children",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7NENPNBC",
                EptsReportUtils.map(eriDSDCohortQueries.getActiveInARTUnstableDC(), mappings)),
            mappings),
        getChildrenColumn());
    addRow(
        dsd,
        "N8E",
        "Active patients on ART who participate in at least one measured DSD model - Eligible(Stable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8E",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        getChildrenColumn());

    addRow(
        dsd,
        "N8NE",
        "Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8NE",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        getChildrenColumn());

    dsd.addColumn(
        "N8NE-05",
        "Adult Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8NE-05",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        "age=15+");

    dsd.addColumn(
        "N8NE-ST",
        "Active patients on ART who participate in at least one measured DSD model - Not-Eligible(UnStable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8NE-ST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable(),
                    mappings)),
            mappings),
        "");
    
    addRow(
        dsd,
        "N8E",
        "Active patients on ART who participate in at least one measured DSD model - Eligible(Stable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8E",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        getChildrenColumn());

    dsd.addColumn(
        "N8E-05",
        "Adult Active patients on ART who participate in at least one measured DSD model - Eligible(Stable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8E-05",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        "age=15+");

    dsd.addColumn(
        "N8E-ST",
        "Active patients on ART who participate in at least one measured DSD model - Eligible(Stable)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8E-ST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable(),
                    mappings)),
            mappings),
        "");
    
    dsd.addColumn(
        "N8-T",
        "Active patients on ART who participate in at least one measured DSD model)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModel(),
                    mappings)),
            mappings),
        "");
    return dsd;
  }

  /**
   * 2-14 years old children List
   *
   * @return
   */
  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");
    ColumnParameters lesThan2 = new ColumnParameters("lesThan2", "<2", "age=<2", "04");

    return Arrays.asList(lesThan2, twoTo4, fiveTo9, tenTo14);
  }
}
