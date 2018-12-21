package org.openmrs.module.eptsreports.reporting.definition;

import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.JembiProgramEnrollmentForPatientDefinition")
public class JembiProgramEnrollmentForPatientDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	@ConfigurationProperty(required = true)
	private Program program;
	
	@ConfigurationProperty(required = true)
	private Location location;
	
	public JembiProgramEnrollmentForPatientDefinition() {
		super();
	}
	
	public JembiProgramEnrollmentForPatientDefinition(String name) {
		super(name);
	}
	
	@Override
	public Class<?> getDataType() {
		return PatientProgram.class;
	}
	
	public void setProgram(Program program) {
		this.program = program;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Program getProgram() {
		return program;
	}
	
	public Location getLocation() {
		return location;
	}
	
}
