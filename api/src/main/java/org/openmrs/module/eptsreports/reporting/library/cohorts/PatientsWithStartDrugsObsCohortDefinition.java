package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;

public class PatientsWithStartDrugsObsCohortDefinition extends BaseCohortDefinition {
	
	private HivMetadata hivMetadata = new HivMetadata();
	
	@ConfigurationProperty
	private Date onOrAfter;
	
	@ConfigurationProperty
	private Date onOrBefore;
	
	private Concept arvPlan = hivMetadata.getARVPlanConcept();
	
	private EncounterType arvPediatriaSeguimento = hivMetadata.getARVPediatriaSeguimentoEncounterType();
	
	private EncounterType adultoSeguimento = hivMetadata.getAdultoSeguimentoEncounterType();
	
	private EncounterType arvPharmacia = hivMetadata.getARVPharmaciaEncounterType();
	
	private Concept startDrugs = hivMetadata.getstartDrugsConcept();
	
	public PatientsWithStartDrugsObsCohortDefinition() {
		super();
		this.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		this.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
	}
	
	public Date getOnOrAfter() {
		return onOrAfter;
	}
	
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}
	
	public Date getOnOrBefore() {
		return onOrBefore;
	}
	
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
	
	public Concept getArvPlan() {
		return arvPlan;
	}
	
	public EncounterType getArvPediatriaSeguimento() {
		return arvPediatriaSeguimento;
	}
	
	public EncounterType getAdultoSeguimento() {
		return adultoSeguimento;
	}
	
	public EncounterType getArvPharmacia() {
		return arvPharmacia;
	}
	
	public Concept getStartDrugs() {
		return startDrugs;
	}
}
