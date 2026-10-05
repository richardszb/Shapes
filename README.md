# Biggest Bounding Box - solution of the 1st take-home assignment

A geometric analysis of regular shapes: the program reads a collection of shapes from a text file, calculates their axis-aligned bounding boxes using object-oriented inheritance, and prints the winner – the shape whose **bounding box has the largest area**.
## The task

Populate a collection with various regular shapes (circle, equilateral triangle, square, regular hexagon). Determine which shape has the bounding rectangle with the largest area. A shape's bounding rectangle completely covers the shape, and its sides are parallel to the axes. Each shape can be represented by its center coordinates and side length (or radius), assuming that for polygons, one side is parallel to the coordinate system's horizontal axis, and the remaining vertices are located above the line containing this side. Load the shapes from a text file. The first line of the file should contain the number of shapes, followed by the individual shapes. The first character identifies the type of the shape, followed by the center coordinates and the required length. In the tasks, apart from reading the data, handle the shapes uniformly; to achieve this, derive the classes describing the shapes from a common base class.

## Input file
```text
6
k 0 0 2.5
h 5 1 10
n -3 2 7
s 4 4 6
k 10 -3 4
n 1 1 3
```
*  **Line 1:** the number of shapes (must not be negative).
*  **One line per shape:** shape code (`k`, `h`, `n`, `s`), X coordinate of the center, Y coordinate of the center, and its size (radius or side length), separated by whitespace.

Files are read as UTF-8.

### What counts as invalid input

The program refuses the file, throwing an `InvalidInputException`, when:
* the file is empty, or the file does not start with the number of shapes,
* the declared number of shapes is negative,
* there are fewer shape data lines than declared (data is missing),
* a coordinate or size field is not a valid number,
* the shape code is unknown (codes are case-insensitive based on the implementation),
* there is anything after the declared number of shapes are read (more data than expected).

## Running the program
The project needs **JDK 17 or newer**.

### In NetBeans

Open the `Shapes` project and press **F6** (Run Project). The program asks for the name of the input file in the Output window; relative names are resolved against the project folder, so the bundled samples can be entered simply as `data.txt`.

### From the command line

Compile, then run from the project folder. The file name can be passed as an argument; without it the program asks for it.

```bash
javac -encoding  UTF-8  -d  build/classes  src/shapes/*.java  src/Main.java
java -cp  build/classes  Main  data.txt
```
```text
Name of the input file (default:test1.txt): test1.txt
Shapes in the collection:
Circle {center=(0.0, 0.0), size=2.5}, bounding box area: 25.00
Regular Triangle {center=(5.0, 1.0), size=10.0}, bounding box area: 86.60
Square {center=(-3.0, 2.0), size=7.0}, bounding box area: 49.00
Regular Hexagon {center=(4.0, 4.0), size=6.0}, bounding box area: 124.71
Circle {center=(10.0, -3.0), size=4.0}, bounding box area: 64.00
Square {center=(1.0, 1.0), size=3.0}, bounding box area: 9.00

The shape with the largest bounding box: Regular Hexagon {center=(4.0, 4.0), size=6.0} 
Its bounding box: [(-2.00, -1.20) - (10.00, 9.20)], area: 124.71
```

Results are written to the standard output, errors to the standard error. The exit code is `0` on success and `1` if the file could not be read or was invalid.

### Bundled sample files
| File | Result |
| :--- | :--- |
| `test1.txt` | The sample of the assignment: Single winner: RegularHexagon (area ~124.71) |
| `test2.txt` | Single winner: Circle, area 100.00 |
| `test3.txt` | Area tie: the first shape in the file wins (Square, area 16.00) |
| `test_invalid.txt` | Error: `Shape 2: Unknown shape code: x` |

If more shapes share the largest area, the first one in the file is returned. If the collection is empty, there is no winner.

### Running the tests
The tests are JUnit 4 tests in the `test` folder. JUnit 4 and Hamcrest come from the libraries bundled with NetBeans, nothing has to be downloaded.
* **NetBeans:** press **Alt+F6** (Test Project), or right-click a test class &rarr; *Test File*. The results appear in the *Test Results* window.
* **Command line, with Ant:** run `ant test` in the project folder. If Ant is not installed separately, the one shipped with NetBeans can be used (`<NetBeans>/extide/ant/bin/ant`). Outside the IDE Ant also has to be told where NetBeans keeps its library definitions:
```text
ant -Duser.properties.file=%APPDATA%\NetBeans\<version>\build.properties test
```
## Running the tests

The tests are JUnit 4 tests in the `test` folder. JUnit 4 and Hamcrest come from the libraries bundled with NetBeans, nothing has to be downloaded.

- **NetBeans:** press **Alt+F6** (Test Project), or right-click a test class → *Test File*. The results appear in the *Test Results* window.
- **Command line, with Ant:** run `ant test` in the project folder. If Ant is not installed separately, the one shipped with NetBeans can be used (`<NetBeans>/extide/ant/bin/ant`). Outside the IDE Ant also has to be told where NetBeans keeps its library definitions:

  ```
  ant -Duser.properties.file=%APPDATA%\NetBeans\<version>\build.properties test
  ``` 

| Test class | What it covers |
| :--- | :--- |
| `ShapeTest` | Constructor checks, validation of invalid sizes or coordinates, and bounding box calculations for each shape. |
| `ShapeCollectionTest` | Finding the largest bounding box, handling ties (returning the first shape), and empty collections. |
| `ShapeFileReaderTest` | Parsing valid files and every kind of invalid input listed above. |
| `MainTest` | The whole program: output formatting, asking for the file name, error messages, and exit codes. |


The API documentation can be generated in NetBeans with *Run → Generate Javadoc*, or with `ant javadoc`; it is written to `dist/javadoc`.

## Design

![Shape Collection Class Diagram](images/class-diagram.svg)

| Class | Responsibility |
| :--- | :--- |
| `Shape` | Abstract base class. Stores the center coordinates and size, validates initial data, and defines abstract methods for bounding box calculation. |
| `Circle`, `RegularHexagon`, `RegularTriangle`, `Square` | The four shape types: supply their specific type name and bounding box computation rules. |
| `BoundingBox` | Immutable class representing an axis-parallel rectangle. Calculates its own width, height, and area. |
| `ShapeCollection` | Stores the shapes in a collection, reads and parses the input file, finds the shape with the largest bounding box, and reports the results. |
| `InvalidInputException` | Checked exception for invalid input, with a message fit for the user. |
| `Main` | Command line interface: gets the file name, initiates the reading and reporting, and handles the exceptions and exit codes. |
