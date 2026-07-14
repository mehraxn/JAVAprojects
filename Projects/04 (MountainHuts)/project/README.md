# Mountain Huts

A Maven-based Java project for managing mountain-hut data for a region. It models regions,
municipalities, mountain huts, and altitude ranges; imports data from CSV; classifies huts by
altitude range; and answers report queries built with the **Java Stream API**.

> Educational/portfolio project — file/CSV based, no database, REST API, or UI
> (see [Known Limitations](#known-limitations)).

## Features

- Domain model: `Region` (aggregate), `Municipality`, `MountainHut`, `AltitudeRange`
- Robust **CSV import** (UTF-8, validated, row-numbered errors)
- Altitude-range classification (left-open/right-closed: `min < altitude ≤ max`)
- **Stream API** report queries (counts, totals, maxima, groupings)
- Defensive validation and immutable domain objects
- Deterministic, unmodifiable outputs
- Automated JUnit tests with ~95% line coverage

## Tech Stack

Java 21 · Maven (wrapper) · Java Stream API · JUnit 5 · JaCoCo · GitHub Actions. No external
runtime dependencies.

## Requirements

- **Java 21** (`maven.compiler.release = 21`).
- **Maven 3.9+**, or use the bundled wrapper (`mvnw` / `mvnw.cmd`).

## Build and Test

```bash
./mvnw clean test          # Linux/macOS/Git Bash  (or: mvn clean test)
bash scripts/test.sh
```

```powershell
.\mvnw.cmd clean test      # Windows PowerShell    (or: mvn clean test)
.\scripts\test.ps1
```

Coverage: `./mvnw clean test jacoco:report` → `target/site/jacoco/index.html` (not committed).

## Project Structure

```
project/
├── pom.xml, mvnw, mvnw.cmd, .mvn/        # build + Maven wrapper
├── .github/workflows/java-ci.yml         # CI
├── scripts/                              # test.sh / test.ps1
├── docs/                                 # architecture, testing, decisions, final review
├── data/mountain_huts.csv                # dataset (94 municipalities, 167 huts)
├── src/mountainhuts/                     # Region, Municipality, MountainHut, AltitudeRange
└── test/                                 # example/, it/polito/po/test/ (professor), custom/
```

## CSV Data Format

Semicolon-separated with a header row:

```
Province;Municipality;MunicipalityAltitude;Name;Altitude;Category;BedsNumber
```

The hut `Altitude` field may be empty (the classifier then uses the municipality altitude). The
importer targets this project format (simple `;`-separated fields), not full RFC-4180 CSV.

## Query / Report Examples

`countMunicipalitiesPerProvince`, `countMountainHutsPerMunicipalityPerProvince`,
`countMountainHutsPerAltitudeRange`, `totalBedsNumberPerProvince`,
`maximumBedsNumberPerAltitudeRange`, `municipalityNamesPerCountOfMountainHuts` — all return
deterministic, key-ordered maps. See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Known Limitations

- Educational/local project — **not production-ready**.
- File/CSV based; no database, no REST API, no UI.
- Format-specific CSV importer (no quoted fields); single Piemonte dataset.

## Resume Value

Demonstrates OOP domain modelling, robust CSV parsing/validation, Java Stream API grouping/report
queries, defensive programming, and a tested, CI-ready Maven project.

---

## Requirements specification (lab)

Develop an application for managing the information about mountain huts in a given region.

In addition to the mountain huts, the application must allow to insert the information about altitude ranges and municipalities.

All classes are inside the package `mountainhuts`.

(*The goal for this lab is to use the Stream API*)


## R1 - Altitude ranges

All interactions are through the class `Region`. The method `getName()` of `Region` returns the name of the region 
as it was specified in the constructor.

Huts are classified according to their altitude range, and such ranges could be freely defined according to the specific needs. Altitude ranges are defined through the method `setAltitudeRanges()` that gets as a parameter an array of strings. Each string describes an altitude range in the format `"minValue-maxValue"` (e.g., `"0-1000"`). The range `"0-1000"` represents altitudes above 0 up to and including 1,000 meters above sea level (i.e., 0 < altitude ≤ 1000). 
Ranges may be assumed non overlapping.

The method `getAltitudeRange()` gets as a parameter an altitude and returns the string describing the range that contains the altitude among the ranges defined through `setAltitudeRanges()`. If no range includes the altitude, the method should return the default string `"0-INF"`.


## R2 - Municipalities and mountain huts

Municipalities are defined using the factory method `createOrGetMunicipality()` that gets as parameters the unique name of the municipality, the province, and its altitude. The method returns an object of class `Municipality`. 
If a municipality with the same name already exists, the method shall return it, ignoring the remaining parameters.

Mountain huts are created using the factory method `createOrGetMountainHut()` which has two versions:
- One that gets as parameters the unique name of the hut, its category, number of beds, and the municipality where it is located.
- Another version that also accepts the altitude of the hut as a parameter (placed after the name).

The method returns an object of the class `MountainHut`. 
If a hut with the same name already exists, the method shall return it, ignoring the remaining parameters.

The class `Municipality` and the class `MountainHut` shall implement getters for all properties. 
The method `getAltitude()` in the class `MountainHut` returns an `Optional` that is empty if the altitude of the hut was not specified in `createOrGetMountainHut()`.

The collections containing the names of municipalities and the names huts are available through the methods `getMunicipalities()` and `getMountainHuts()`, respectively.


Hints:

* The class `Optional` is used to explicitly indicate a value that may not exist. The method `isPresent()` is used to check if a value is available in the optional.
* To create an `Optional` from a variable that might be null it is possible to use `Optional.ofNullable()` that returns an `Optional` wrapping the variable, or an empty `Optional` if the variable is `null`.


## R3 - Input from CSV

The static factory method `fromFile()` creates an object of class `Region` using the information stored inside a file whose name is passed as an argument. In more details, the method should populate the region with both the municipalities and the huts, described in a CSV file that is structured as follows:


| N | Columns				| `Municipality` | `MountainHut` |
|---|-----------------------|:-------------:|:------------:|
| 0	| Province				|	✓			|			|	
| 1	| Municipality			|	✓			|			|	
| 2	| MunicipalityAltitude	|	✓			|			|
| 3	| Name					|				|		✓   |
| 4	| Altitude				|				|		✓	|
| 5	| Category				|				|		✓	|
| 6	| BedsNumber			|				|		✓	|


Note: the file contains a line for each hut, therefore the information about municipalities may be duplicated.

CSV fields are separated by a semicolon (`;`). The altitude of a hut is empty if the information is not available.

All data about mountain huts in Piedmont are available in the file: `data/mountain_huts.csv`(*).


Hints:

* To read from a CSV file you can use the provided method `readData()`, which  reads a text file line by line, and returns a list of rows. The first rows contains the headers, while the actual data starts from the second row.


## R4 - Queries

The method `countMunicipalitiesPerProvince()` shall return a map with the name of the province as key, and the total number of the municipalities of that province as value.

The method `countMountainHutsPerMunicipalityPerProvince()` shall return a map with the name of the province as key and as value a second map with the name of the municipality as key, and the number of mountain huts located inside that 
municipality as value.

The method `countMountainHutsPerAltitudeRange()` shall return a map with the altitude range returned by `getAltitudeRange()` as key, and the number of huts in that altitude range as value. 
When no altitude is specified for the hut, do consider the altitude of the municipality.

The method `totalBedsNumberPerProvince()` shall return a map with the name of the province as key, and the total number of beds available in all huts located in that province as value.

The method `maximumBedsNumberPerAltitudeRange()` shall return a map with the altitude range returned by `getAltitudeRange()` as key, and as value the maximum number of beds available in a single mountain hut in that altitude range. When no altitude is specified for the hut, do consider the altitude of the municipality.

The method `municipalityNamesPerCountOfMountainHuts()` shall return a map with the number of available huts as key, and a list of the municipalities including exactly that number of huts as value. 
The list should be alphabetically sorted.

To implement the queries, usage of Stream API is recommended; they allow writing more compact and understandable code, with respect to explicit iterations on collections and maps.


----

(*) the file contains a simplified version of the data available on the open data portal of the Piedmont region, in particular <https://www.dati.piemonte.it/#/catalogodetail/regpie_ckan_ckan2_yucca_sdp_smartdatanet.it_RifugiOpenDa_2296>

