# CSV Analytics Engine

## Status

Tabular model, CSV reader/writer, analytics service, and Main skeleton created.

## Planned features

- Read UTF-8 CSV with quoted fields.
- Validate headers and row widths.
- Select columns and filter rows.
- Sum and average numeric columns.
- Group and count categorical values.
- Export result data sets.

## Current classes

- DataRow: named column values for one row.
- DataSet: column definitions and rows.
- CsvReader: planned parser.
- CsvWriter: planned exporter.
- AnalyticsService: filtering and aggregation.
- Main: safe runner that performs no file operation yet.

## Constraints

CSV parsing will be implemented manually with standard Java. No CSV library is used.

## Source layout

Source files are under src/csvanalyticsengine.
