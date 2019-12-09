package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class OnArtInitiatedArvDrugsProcessor {

  @SuppressWarnings("unchecked")
  public Map<Integer, Date> getResutls(EvaluationContext context) {

    Map<Integer, Date> patientsInitiatedArvDrugs =
        getPatientsOnArtWhoInitiatedArvDrugsArtRegimen(context);

    Map<Integer, Date> patientsWithArtStartDate = getPatientsOnArtWhoHaveArtStartDate(context);

    Map<Integer, Date> patientEnrolledInArtProgram = getPatientsEnrolledInArtProgram(context);

    Map<Integer, Date> patientWithFirstDrugPickUpDateInPharmacy =
        getPatientsWithFirstDrugsPickUpInPharmacy(context);

    Map<Integer, Date> patientWithFirstDrugPickUpDateSetInReception =
        getPatientsWithFirsDrugPickInRecepcao(context);

    return CalculationProcessorUtils.getMinMapDateByPatient(
        patientsInitiatedArvDrugs,
        patientsWithArtStartDate,
        patientEnrolledInArtProgram,
        patientWithFirstDrugPickUpDateInPharmacy,
        patientWithFirstDrugPickUpDateSetInReception);
  }

  /** Patients on ART who initiated the ARV DRUGS: ART Regimen Start Date */
  private Map<Integer, Date> getPatientsOnArtWhoInitiatedArvDrugsArtRegimen(
      EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "Select p.patient_id,min(e.encounter_datetime) data_inicio "
                + "				from 	patient p "
                + "						inner join encounter e on p.patient_id=e.patient_id	"
                + "						inner join obs o on o.encounter_id=e.encounter_id "
                + "				where 	e.voided=0 and o.voided=0 and p.voided=0 and "
                + "						e.encounter_type in (18,6,9) and o.concept_id=1255 and o.value_coded=1256 and "
                + "						e.encounter_datetime<= :endDate and e.location_id= :location "
                + "				group by p.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  /** Patients on ART who have art start date: ART Start date */
  private Map<Integer, Date> getPatientsOnArtWhoHaveArtStartDate(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "Select p.patient_id,min(value_datetime) data_inicio from 	patient p "
                + "						inner join encounter e on p.patient_id=e.patient_id "
                + "						inner join obs o on e.encounter_id=o.encounter_id "
                + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type in (18,6,9,53) and "
                + "						o.concept_id=1190 and o.value_datetime is not null and "
                + "						o.value_datetime<= :endDate and e.location_id= :location "
                + "				group by p.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  /** Patients enrolled in ART Program: OpenMRS Program */
  private Map<Integer, Date> getPatientsEnrolledInArtProgram(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "select pg.patient_id,min(date_enrolled) data_inicio "
                + "				from 	patient p inner join patient_program pg on p.patient_id=pg.patient_id "
                + "				where 	pg.voided=0 and p.voided=0 and program_id=2 and date_enrolled<= :endDate and location_id= :location "
                + "				group by pg.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  /** Patients with first drugs pick up date set in Pharmacy: First ART Start Date */
  private Map<Integer, Date> getPatientsWithFirstDrugsPickUpInPharmacy(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "SELECT e.patient_id, MIN(e.encounter_datetime) AS data_inicio "
                + "				  FROM 		patient p "
                + "							inner join encounter e on p.patient_id=e.patient_id "
                + "				  WHERE		p.voided=0 and e.encounter_type=18 AND e.voided=0 and e.encounter_datetime<= :endDate and e.location_id= :location "
                + "				  GROUP BY 	p.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  /** Patients with first drugs pick up date set: Recepcao Levantou ARV */
  private Map<Integer, Date> getPatientsWithFirsDrugPickInRecepcao(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "Select p.patient_id,min(value_datetime) data_inicio from 	patient p "
                + "						inner join encounter e on p.patient_id=e.patient_id "
                + "						inner join obs o on e.encounter_id=o.encounter_id "
                + "				where 	p.voided=0 and e.voided=0 and o.voided=0 and e.encounter_type=52 and "
                + "						o.concept_id=23866 and o.value_datetime is not null and "
                + "						o.value_datetime<= :endDate and e.location_id= :location "
                + "				group by p.patient_id ",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }
}
