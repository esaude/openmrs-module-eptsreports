package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class DispensationTypeMdcConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }

    switch (obj.toString()) {
      case "23888":
        return "Dispensa Semestral";

      case "165175":
        return "Horario Normal De Expedinte";

      case "165176":
        return "Fora Do Horário";

      case "165177":
        return "Farmac/Farmácia Privada";

      case "165178":
        return "Dispensa Comunitária Via Provedor";

      case "165179":
        return "Dispensa Comunitária Via Ape";

      case "165180":
        return "Brigadas Móveis Diurnas";

      case "165181":
        return "Brigadas Moveis Noturnas(Hotspots)";

      case "165182":
        return "Clinicas Moveis Diurnas ";

      case "165183":
        return "Clinicas Moveis Noturnas(Hotspots)";

      case "23730":
        return "Dispensa Trimestral (DT)";

      case "165264":
        return "Brigadas Moveis (DCBM)";

      case "165265":
        return "Clinicas Moveis (DCCM)";

      case "23725":
        return "Abordagem Familiar";

      case "23729":
        return "Fluxo Rápido (FR)";

      case "23724":
        return "Gaac (GA)";

      case "165317":
        return "Paragem Única No Sector Da TB";

      case "165318":
        return "Paragem Única Nos Serviços De TARV";

      case "165319":
        return "Paragem Única No SAAJ";

      case "165320":
        return "Paragem Única Na SMI";

      case "165321":
        return "Doença Avançada Por HIV";

      case "165314":
        return "Dispensa Anual de ARV";

      case "165315":
        return "Dispensa Descentralizada de ARV";

      case "23726":
        return "Clubes de Adesão (CA)";

      case "165316":
        return "Extensão de Horarário";
      default:
        return "";
    }
  }

  @Override
  public Class<?> getInputDataType() {
    return String.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
