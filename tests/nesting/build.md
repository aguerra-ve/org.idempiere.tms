# Build iDempiere 12 para safe-product-nesting

Fecha: 2026-09-23

Tarea: T003

## Entorno validado

- Java: OpenJDK `17.0.20.1`.
- Maven: `3.9.10`.
- Reactor aislado: `/tmp/iqp-tms-validation-20260921`.
- Padre: `org.idempiere:org.idempiere.parent:12.0.0-SNAPSHOT`, SHA-256 del POM
  `3be7748194314e0d9337e9b8825187302543e3e730a46eaf715d28f93366bbe2`.
- Plataforma completa: imagen local iDempiere 12 en `/tmp/iqp-task29-image`.
- Bundle base: `org.adempiere.base` versión `12.0.0.202412280707`, SHA-256
  `2c35408e8ea768888d26737e832ae28ea7a2d62f0dbcf28452579c04607f0a9e`.
- Repositorio Maven desechable: `/tmp/iqp-mapview-m2`; no se escribió en `~/.m2`.

El reactor temporal y su origen están descritos también en
`02-DOCS/wiki/ftd/native-dispatch-map-requirement.md`. La copia temporal de `src/`,
`META-INF/MANIFEST.MF`, `plugin.xml` y `build.properties` se comparó con el árbol activo mediante
`diff -q`/`diff -qr` sin diferencias antes de aceptar el resultado.

## Preparación reproducible del staging

El POM activo conserva su `relativePath` histórico y no se modifica para acomodar el sandbox. En
la copia temporal:

1. El plugin apunta a `../org.idempiere.parent/pom.xml`.
2. `base.target` agrega como `Directory` la instalación `/tmp/iqp-task29-image`, que aporta los
   bundles centrales de iDempiere además de Eclipse/ZK.
3. El padre se instala primero en el repositorio Maven temporal:

```bash
mvn -Dmaven.repo.local=/tmp/iqp-mapview-m2 \
  -Drevision=12.0.0-SNAPSHOT -N -q install
```

El comando se ejecuta desde
`/tmp/iqp-tms-validation-20260921/org.idempiere.parent` y terminó con código `0`.

## Done-check

Desde `/tmp/iqp-tms-validation-20260921`:

```bash
mvn -Dmaven.repo.local=/tmp/iqp-mapview-m2 \
  -Drevision=12.0.0-SNAPSHOT -q -DskipTests package
```

Resultado: código `0`.

Artefacto:

```text
/tmp/iqp-tms-validation-20260921/org.idempiere.tms/target/
  org.idempiere.tms-12.0.0-SNAPSHOT.jar
size: 2613117 bytes
sha256: 1f4ad7a60fda53b062ef3b267cb93fe2f8bcaa29277ff1ea1874c39776e18ea1
```

La inspección del JAR confirmó, entre otros:

- `org/idempiere/tms/model/MDDProductNesting.class`
- `org/idempiere/tms/mapview/model/ProposalLoadSummary.class`
- `org/idempiere/tms/component/TMSFormFactory.class`
- `OSGI-INF/TMSFormFactory.xml`

## Intentos diagnósticos conservados

1. El comando sin repositorio temporal falló antes de compilar porque el sandbox no permite crear
   `.lastUpdated` bajo `~/.m2`. No fue un error del producto.
2. Con el padre instalado pero solo la plataforma Eclipse/ZK, Tycho rechazó la dependencia
   `org.adempiere.base 7.1.0`. Al incorporar la instalación completa de iDempiere 12 como target,
   el mismo build terminó correctamente.

## Límite de esta evidencia

T003 demuestra resolución del padre y compilación/empacado del plugin contra iDempiere 12. No
ejecuta pruebas de nesting, no importa 2Pack, no instala el JAR y no despliega a una instancia.
