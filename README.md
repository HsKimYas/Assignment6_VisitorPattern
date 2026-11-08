# Assignment 6 - Visitor Pattern

Yasina Bauyrzhanova | SE-2538

## Run

From the project root:

```bash
mvn test
mvn compile exec:java
```

Change the filename suffix and inclusive minimum byte size:

```bash
mvn compile exec:java "-Dexec.args=.md 1200"
```

In IntelliJ IDEA, open `pom.xml` as a project, select JDK 17 and let Maven sync.
Run `Demo.main()` or use the Maven tool window. Press Ctrl twice to run the
same Maven commands through Run Anything. The first dependency download needs internet.

## Behavior

- `FileNode` and `DirectoryNode` dispatch through `accept(Visitor)` to the
  matching `visit` overload; there are no type-checking chains.
- `FileSystemWalker` alone traverses the tree with iterative depth-first search.
  Elements and visitors never recurse. Each walk resets the visitor first;
  reuse instances sequentially through `walk`, including with another root.
- `TotalSizeVisitor` sums each file once. Sizes are non-negative `long` values;
  arithmetic overflow throws `ArithmeticException`.
- `SearchVisitor` combines a `Predicate<String>` on the filename with
  `size >= minimumSize`. The demo's suffix predicate is case-sensitive.
  Paths omit the selected directory root, use `/`, and are sorted
  lexicographically. A root file is returned as its filename.
- `StatisticsVisitor` counts every file and directory, including a directory
  root. Depth is the number of edges from the chosen root, starting at zero;
  empty directories also affect maximum depth.
- `addChild` rejects duplicate sibling names, multiple parents and cycles
  before changing the tree. Names are immutable single path segments.
  `removeChild` detaches an element. Child lists and search results are immutable
  snapshots. Traversal is single-threaded; do not mutate the tree during a walk.

## Demonstration

The fixture in `Demo.createSampleTree()` has **8500 bytes, 9 files,
7 directories and maximum depth 3**. Default search: `.java`, at least 800 bytes.

```text
src/Main.java
src/model/DirectoryNode.java
```

Complete sample output, UML and verification details are in `docs/Report.pdf`.

## UML diagram

The editable diagram is `docs/uml.puml` (PlantUML). Every visible relationship
is labeled.

With the PlantUML JAR available, generate the SVG from the project root:

```bash
java -jar plantuml.jar -tsvg docs/uml.puml
```

## Extension boundary

Adding `StatisticsVisitor` and its result record leaves `Element`, `Visitor`,
`FileNode`, `DirectoryNode`, their `accept` methods and the walker unchanged.
Only the demo wiring and new tests are added. A new element type instead
requires a `Visitor.visit` overload, implementations in existing visitors,
an `accept` method, and an update to the sealed base class's `permits` list.

## Tests

31 tests cover T1-T8 in `DoubleDispatchTest`, `TotalSizeVisitorTest`,
`SearchVisitorTest`, `StatisticsVisitorTest` and `TreeIntegrityTest`.
See `docs/test-results.txt` for the recorded Maven run.

