package org.idempiere.tms.mapview.model;

import java.util.Locale;

/** Canonical identity: uppercase ASCII letters/digits, ignoring spaces and hyphens. */
public final class LicensePlate {
 private LicensePlate() { }
 public static String normalize(String value) {
  if (value == null || value.isBlank()) return null;
  String plate=value.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
  if (!plate.matches("[A-Z0-9]{1,20}"))
   throw new IllegalArgumentException("Placa inválida: use hasta 20 letras o números.");
  return plate;
 }
}
