package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.EC23Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec15Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec1Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec20Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec2Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec5Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec6Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec7Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec8Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec9Queries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class SummaryCohortQuery {

  public CohortDefinition getEC1Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC1");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec1Queries.getEc1Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC2Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC2");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec2Queries.getEc2Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC5Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC5");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec5Queries.getEc5Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC6Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC6");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec6Queries.getEc6Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC7Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC7");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec7Queries.getEc7Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC8Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC8");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec8Queries.getEc8Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC9Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC9");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec9Queries.getEc9Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC15Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC15");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec15Queries.getEc15Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC20Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC20");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = Ec20Queries.getEc20Total();

    definition.setQuery(query);

    return definition;
  }

  public CohortDefinition getEC23Total() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("EC23");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = EC23Queries.getEc23Total();

    definition.setQuery(query);

    return definition;
  }
}
