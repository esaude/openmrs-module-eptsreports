package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class PatientsWhoHadAtLeastDrugPickupCalculation extends AbstractPatientCalculation{
	
	private final String LOCATION = "location";
	
	private final String START_DATE = "onOrAfter";

	@Override
	public CalculationResultMap evaluate(
			Collection<Integer> cohort, 
			Map<String, 
			Object> parameterValues,
			PatientCalculationContext context) {
		
		Location location = (Location) context.getFromCache(LOCATION);
		Date startDate = (Date) context.getFromCache(START_DATE);
		
		
		HivMetadata  hivMetadata  = Context.getRegisteredComponents(HivMetadata.class).get(0);
		
		EncounterType  masterCardDrugPickupEncounterType = hivMetadata.getMasterCardDrugPickupEncounterType();
		Concept artDatePickupMasterCardConcept =   hivMetadata.getArtDatePickupMasterCard();
		
		EncounterType  arvPharmaciaEncounterType = hivMetadata.getARVPharmaciaEncounterType();
		Concept returnVisitDateForArvDrugConcept =   hivMetadata.getReturnVisitDateForArvDrugConcept();
		
		CalculationResultMap levantamentodeArvMap= getObsByEncounterAndConcept(cohort, 
				artDatePickupMasterCardConcept,
				Arrays.asList(masterCardDrugPickupEncounterType), location, startDate, context);
		
		CalculationResultMap pickUpFilaMap= getObsByEncounterAndConcept(cohort, 
				returnVisitDateForArvDrugConcept,
				Arrays.asList(arvPharmaciaEncounterType), location, startDate, context);
		
		
		
		CalculationResultMap map   = new CalculationResultMap();
		
		
		for (Integer patientId: cohort) {
			
			ListResult levantamentodeArvResultList  = (ListResult) levantamentodeArvMap.get(patientId);
			List<Obs> levantamentodeArvObs =  EptsCalculationUtils.extractResultValues(levantamentodeArvResultList);
			
			ListResult pickUpFilaMapResultList  = (ListResult) pickUpFilaMap.get(patientId);
			List<Obs> pickUpFilaMapResultObs =  EptsCalculationUtils.extractResultValues(pickUpFilaMapResultList);


			
			if(levantamentodeArvObs!=null && !levantamentodeArvObs.isEmpty()) {
				for(Obs o : levantamentodeArvObs) {
					if(o.getValueDatetime().compareTo(startDate)<=0) {
						map.put(patientId, new BooleanResult(true, this));
						continue;
					}
				}
			}
			if(pickUpFilaMapResultObs!=null && !pickUpFilaMapResultObs.isEmpty()) {
				for(Obs o : pickUpFilaMapResultObs) {
					if(o.getEncounter().getEncounterDatetime().compareTo(startDate)<=0 
							&& o.getValueDatetime()!=null) {
						map.put(patientId, new BooleanResult(true, this));
					}
				}
			}
		}
		
	 
		return map;
	}
	
	private CalculationResultMap getObsByEncounterAndConcept(Collection<Integer> cohort, 
			Concept concept,
			List<EncounterType>encounterTypes,
			Location location, 
			Date startDate,
			PatientCalculationContext context){
		
		   EPTSCalculationService eptsCalculationService  = 
				Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
		   return eptsCalculationService.allObservations(concept,
				null,
				encounterTypes, 
				location, 
				cohort, 
				context);
		 
	}

}
