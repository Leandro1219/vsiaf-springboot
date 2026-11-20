package com.sis414.sis_414_parcial.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/** Solo se ofrecen valores presentes en el selector Expedido de VSIAF. */
final class DepartamentosBolivia {
    private static final Set<String> NUEVE = Set.of(
            "LA PAZ", "COCHABAMBA", "SANTA CRUZ", "ORURO", "POTOSI",
            "CHUQUISACA", "TARIJA", "BENI", "PANDO");

    private DepartamentosBolivia() {}

    static boolean valido(String descripcion) {
        if (descripcion == null) return false;
        String base = Normalizer.normalize(descripcion.trim().toUpperCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return NUEVE.contains(base);
    }
}
