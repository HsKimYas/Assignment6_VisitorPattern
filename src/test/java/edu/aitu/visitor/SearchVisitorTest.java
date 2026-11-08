package edu.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchVisitorTest {
    @Test
    void combinesTheFilenamePredicateWithAnInclusiveMinimumSize() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 120);

        FileSystemWalker.walk(TestTrees.sample().root(), visitor);

        // week1.txt equals the threshold; draft.txt is one byte below it.
        assertEquals(List.of("notes/archive/report.txt", "notes/week1.txt"), visitor.getMatches());
    }

    @Test
    void acceptsACustomCriterionAndAppliesItOnlyToTheFilename() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode folder = new DirectoryNode("keep-folder");
        folder.addChild(new FileNode("other.txt", 40));
        folder.addChild(new FileNode("keep-empty.txt", 0));
        root.addChild(folder);
        root.addChild(new FileNode("keep-note.md", 20));
        SearchVisitor visitor = new SearchVisitor(name -> name.startsWith("keep-"), 0);

        FileSystemWalker.walk(root, visitor);

        assertEquals(List.of("keep-folder/keep-empty.txt", "keep-note.md"), visitor.getMatches());
    }

    @Test
    void sortsPathsLexicographicallyRegardlessOfInsertionOrder() {
        DirectoryNode root = new DirectoryNode("not-in-the-result");
        DirectoryNode zFolder = new DirectoryNode("z");
        zFolder.addChild(new FileNode("first.txt", 1));
        root.addChild(zFolder);
        root.addChild(new FileNode("beta.txt", 1));
        DirectoryNode aFolder = new DirectoryNode("a");
        aFolder.addChild(new FileNode("zulu.txt", 1));
        aFolder.addChild(new FileNode("alpha.txt", 1));
        root.addChild(aFolder);
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);

        FileSystemWalker.walk(root, visitor);

        assertEquals(List.of("a/alpha.txt", "a/zulu.txt", "beta.txt", "z/first.txt"),
                visitor.getMatches());
    }

    @Test
    void usesTheChosenSubtreeAsThePathRoot() {
        TestTrees.SampleTree tree = TestTrees.sample();
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 120);

        FileSystemWalker.walk(tree.notes(), visitor);

        assertEquals(List.of("archive/report.txt", "week1.txt"), visitor.getMatches());
    }

    @Test
    void returnsTheFilenameWhenTheChosenRootIsAFile() {
        DirectoryNode parent = new DirectoryNode("parent");
        FileNode file = new FileNode("single.txt", 25);
        parent.addChild(file);
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 25);

        FileSystemWalker.walk(file, visitor);

        assertEquals(List.of("single.txt"), visitor.getMatches());
    }

    @Test
    void handlesEmptyTreesAndNoMatchResults() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".java"), 0);

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(List.of(), visitor.getMatches());

        FileSystemWalker.walk(TestTrees.sample().root(), visitor);
        assertEquals(List.of(), visitor.getMatches());
    }

    @Test
    void reuseClearsOldMatchesAndReturnedResultsAreImmutableSnapshots() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 120);
        DirectoryNode root = TestTrees.sample().root();
        FileSystemWalker.walk(root, visitor);
        List<String> originalResult = visitor.getMatches();

        FileSystemWalker.walk(root, visitor);
        assertEquals(originalResult, visitor.getMatches());

        FileSystemWalker.walk(new FileNode("new.txt", 400), visitor);
        assertEquals(List.of("new.txt"), visitor.getMatches());
        assertEquals(List.of("notes/archive/report.txt", "notes/week1.txt"), originalResult);
        assertThrows(UnsupportedOperationException.class, () -> originalResult.add("injected.txt"));

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(List.of(), visitor.getMatches());
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}
