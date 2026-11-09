# Documentación de arquitectura - GLE-2026-II

Este directorio contiene los entregables de Ingeniería de Software I / Sesión 3 (09/11/2026). Es un **paquete para integrar al repositorio, no un commit automático**.

- `arquitectura/cuaderno.md`: versión Markdown del documento de arquitectura.
- `arquitectura/diagramas/*.dot`: fuente Graphviz editable de los cinco diagramas de A5.
- `arquitectura/diagramas/png/*.png`: imágenes exportadas incluidas en el PDF.
- `adr/0001..0004-*.md`: cuatro ADR completos con estado y fecha.

## Regeneración de los diagramas

Requiere [Graphviz](https://graphviz.org/download/):

```bash
for f in docs/arquitectura/diagramas/*.dot; do
  dot -Tpng -Gdpi=200 "$f" -o "docs/arquitectura/diagramas/png/$(basename "${f%.dot}").png"
done
```

En Windows PowerShell:

```powershell
Get-ChildItem docs/arquitectura/diagramas/*.dot | ForEach-Object { dot -Tpng -Gdpi=200 $_.FullName -o ("docs/arquitectura/diagramas/png/" + $_.BaseName + ".png") }
```

## Repositorios revisados

- Móvil: https://github.com/marecsg/proyecto-android (snapshot `193fc9e`)
- Backend: https://github.com/marecsg/proyecto-backend (snapshot `03b11e1`, señalado como desactualizado por el equipo)
