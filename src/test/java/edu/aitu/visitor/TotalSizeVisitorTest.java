package edu.aitu.visitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TotalSizeVisitorTest {
    @Test
    void sumsFilesAcrossSeveralLevelsExactlyOnce() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        FileSystemWalker.walk(TestTrees.sample().root(), visitor);

        assertEquals(2_199L, visitor.getTotalSize());
    }

    @Test
    void chosenSubtreeExcludesItsParentsAndSiblings() {
        TestTrees.SampleTree tree = TestTrees.sample();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        FileSystemWalker.walk(tree.notes(), visitor);

        assertEquals(499L, visitor.getTotalSize());
    }

    @Test
    void supportsAnEmptyDirectoryAndAFileAsRoot() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(0L, visitor.getTotalSize());

        FileSystemWalker.walk(new FileNode("large.bin", Long.MAX_VALUE), visitor);
        assertEquals(Long.MAX_VALUE, visitor.getTotalSize());
    }

    @Test
    void reuseStartsFromZeroForEveryOperation() {
        DirectoryNode root = TestTrees.sample().root();
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        FileSystemWalker.walk(root, visitor);
        FileSystemWalker.walk(root, visitor);
        assertEquals(2_199L, visitor.getTotalSize());

        FileSystemWalker.walk(new FileNode("small.bin", 9), visitor);
        assertEquals(9L, visitor.getTotalSize());

        FileSystemWalker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(0L, visitor.getTotalSize());
    }

    @Test
    void overflowIsReportedAndTheNextRunCanRecover() {
        DirectoryNode root = new DirectoryNode("oversized");
        root.addChild(new FileNode("max.bin", Long.MAX_VALUE));
        root.addChild(new FileNode("extra.bin", 1));
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        assertThrows(ArithmeticException.class, () -> FileSystemWalker.walk(root, visitor));

        FileSystemWalker.walk(new FileNode("valid.bin", 12), visitor);
        assertEquals(12L, visitor.getTotalSize());
    }

    @Test
    void traversesThreeThousandNestedDirectoriesWithoutRecursiveCalls() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode current = root;
        for (int depth = 0; depth < 3_000; depth++) {
            DirectoryNode next = new DirectoryNode("d");
            current.addChild(next);
            current = next;
        }
        current.addChild(new FileNode("deep.bin", 17));
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        FileSystemWalker.walk(root, visitor);

        assertEquals(17L, visitor.getTotalSize());
    }
}
