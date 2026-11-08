package edu.aitu.visitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsVisitorTest {
    @Test
    void countsAllFilesAndDirectoriesAndFindsMaximumDepth() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        FileSystemWalker.walk(TestTrees.sample().root(), visitor);

        assertEquals(new Statistics(6, 5, 3), visitor.getStatistics());
    }

    @Test
    void includesAnEmptyDirectoryRootAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);

        assertEquals(new Statistics(0, 1, 0), visitor.getStatistics());
    }

    @Test
    void aFileRootHasNoDirectoryAndDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();

        FileSystemWalker.walk(new FileNode("only.bin", 0), visitor);

        assertEquals(new Statistics(1, 0, 0), visitor.getStatistics());
    }

    @Test
    void emptyDirectoriesStillContributeToDepthAndCounts() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        DirectoryNode third = new DirectoryNode("third");
        root.addChild(first);
        first.addChild(second);
        second.addChild(third);
        StatisticsVisitor visitor = new StatisticsVisitor();

        FileSystemWalker.walk(root, visitor);
        assertEquals(new Statistics(0, 4, 3), visitor.getStatistics());

        FileSystemWalker.walk(first, visitor);
        assertEquals(new Statistics(0, 3, 2), visitor.getStatistics());
    }

    @Test
    void repeatExecutionsResetAllCountersAndPreservePreviousSnapshots() {
        TestTrees.SampleTree tree = TestTrees.sample();
        StatisticsVisitor visitor = new StatisticsVisitor();
        FileSystemWalker.walk(tree.root(), visitor);
        Statistics originalResult = visitor.getStatistics();

        FileSystemWalker.walk(tree.root(), visitor);
        assertEquals(new Statistics(6, 5, 3), visitor.getStatistics());

        FileSystemWalker.walk(tree.notes(), visitor);
        assertEquals(new Statistics(3, 2, 2), visitor.getStatistics());
        assertEquals(new Statistics(6, 5, 3), originalResult);

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(new Statistics(0, 1, 0), visitor.getStatistics());
    }
}
