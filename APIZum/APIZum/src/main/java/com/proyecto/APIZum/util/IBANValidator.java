package com.proyecto.APIZum.util;

import java.math.BigInteger;

// ================================================================
//  Validador de IBAN según la norma ISO 13616
// ================================================================
//  Algoritmo:
//  1. Mover los primeros 4 caracteres al final
//  2. Reemplazar letras por números (A=10, B=11, ..., Z=35)
//  3. Calcular el número resultante módulo 97
//  4. Si el resultado es 1, el IBAN es válido
//
//  IBAN español: ES + 2 dígitos de control + 20 dígitos = 24 caracteres
// ================================================================

public final class IBANValidator {

    private IBANValidator() {}

    /**
     * Valida un IBAN según la norma ISO 13616.
     *
     * @param iban cadena con el IBAN (con o sin espacios, mayúsculas o minúsculas)
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValid(String iban) {
        if (iban == null) return false;

        // Normalizar: quitar espacios y convertir a mayúsculas
        String normalized = iban.replaceAll("\\s", "").toUpperCase();

        // Longitud mínima (IBANs válidos tienen entre 15 y 34 caracteres)
        if (normalized.length() < 15 || normalized.length() > 34) return false;

        // Solo letras y dígitos
        if (!normalized.matches("[A-Z0-9]+")) return false;

        // Reordenar: mover los primeros 4 caracteres al final
        String rearranged = normalized.substring(4) + normalized.substring(0, 4);

        // Convertir letras a números: A=10, B=11, ..., Z=35
        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericString.append(c - 'A' + 10);
            } else {
                numericString.append(c);
            }
        }

        // Calcular módulo 97
        BigInteger numericValue = new BigInteger(numericString.toString());
        return numericValue.mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    /**
     * Valida que el IBAN sea específicamente español (empieza por "ES" y tiene 24 caracteres).
     */
    public static boolean isValidSpanish(String iban) {
        if (iban == null) return false;
        String normalized = iban.replaceAll("\\s", "").toUpperCase();
        if (!normalized.startsWith("ES") || normalized.length() != 24) return false;
        return isValid(normalized);
    }
}
