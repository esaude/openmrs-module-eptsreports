package org.openmrs.module.eptsreports.api.impl;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.eptsreports.api.EptsGlobalPropertyService;
import org.springframework.stereotype.Service;

@Service
public class EptsGlobalPropertyServiceImpl extends BaseOpenmrsService
    implements EptsGlobalPropertyService {

  @Override
  public List<GlobalProperty> removeEptsGlobalPropertiesEntries(String patternName) {

    List<GlobalProperty> remvedProperties = new ArrayList<>();

    AdministrationService administrationService = Context.getAdministrationService();
    List<GlobalProperty> globalProperties =
        administrationService.getGlobalPropertiesByPrefix(patternName);

    for (GlobalProperty gp : globalProperties) {
      if (gp.getProperty().contains(patternName)) {
        administrationService.purgeGlobalProperty(gp);

        remvedProperties.add(gp);
      }
    }

    return remvedProperties;
  }
}
