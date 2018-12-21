package org.openmrs.module.eptsreports.reporting.definition;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiObsDefinition")
public class JembiObsDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	@ConfigurationProperty(required = true)
	private Concept question;
	
	@ConfigurationProperty
	private Concept answer;
	
	@ConfigurationProperty(required = true)
	private Location location;
	
	@ConfigurationProperty(required = true)
	private boolean sortByDatetime = true;
	
	public JembiObsDefinition() {
		super();
	}
	
	public JembiObsDefinition(String name) {
		super(name);
	}
	
	@Override
	public Class<?> getDataType() {
		return PatientProgram.class;
	}
	
	public Concept getQuestion() {
		return question;
	}
	
	public void setQuestion(Concept question) {
		this.question = question;
	}
	
	public Concept getAnswer() {
		return answer;
	}
	
	public void setAnswer(Concept answer) {
		this.answer = answer;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public boolean isSortByDatetime() {
		return sortByDatetime;
	}
	
	public void setSortByDatetime(boolean sortByDatetime) {
		this.sortByDatetime = sortByDatetime;
	}
	
}
