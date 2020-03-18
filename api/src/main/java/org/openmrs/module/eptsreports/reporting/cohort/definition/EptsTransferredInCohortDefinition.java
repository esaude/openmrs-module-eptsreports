package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Cohort for transferred-in patients as specified in Resumo Mensal. Should e used by other reports
 * in the future.
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class EptsTransferredInCohortDefinition extends BaseCohortDefinition {

  @ConfigurationProperty private Date onOrAfter;

  @ConfigurationProperty private Date onOrBefore;

  @ConfigurationProperty private Location location;

  @ConfigurationProperty(required = true)
  private Concept typeOfPatientTransferredFromAnswer;

  @ConfigurationProperty(required = true)
  private Program programEnrolled;

  @ConfigurationProperty(required = true)
  private ProgramWorkflowState patientState;

  public Date getOnOrBefore() {
    return onOrBefore;
  }

  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }

  public Date getOnOrAfter() {
    return onOrAfter;
  }

  public void setOnOrAfter(Date onOrAfter) {
    this.onOrAfter = onOrAfter;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Concept getTypeOfPatientTransferredFromAnswer() {
    return typeOfPatientTransferredFromAnswer;
  }

  public void setTypeOfPatientTransferredFromAnswer(Concept typeOfPatientTransferredFromAnswer) {
    this.typeOfPatientTransferredFromAnswer = typeOfPatientTransferredFromAnswer;
  }

  public Program getProgramEnrolled() {
    return programEnrolled;
  }

  public void setProgramEnrolled(Program programEnrolled) {
    this.programEnrolled = programEnrolled;
  }

  public ProgramWorkflowState getPatientState() {
    return patientState;
  }

  public void setPatientState(ProgramWorkflowState patientState) {
    this.patientState = patientState;
  }
}
