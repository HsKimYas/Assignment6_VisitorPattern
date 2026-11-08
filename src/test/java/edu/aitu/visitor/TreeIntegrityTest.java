package edu.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeSizesButAcceptsZero() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("invalid.bin", -1));
        assertEquals(0L, new FileNode("zero.bin", 0).getSize());
    }

    @Test
    void rejectsNamesThatWouldMakePathsAmbiguous() {
        for (String name : List.of("", "   ", ".", "..", "a/b", "a\\b", "line\nbreak")) {
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 0), name);
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name), name);
        }
        assertThrows(NullPointerException.class, () -> new FileNode(null, 0));
        assertThrows(NullPointerException.class, () -> new DirectoryNode(null));
        assertThrows(NullPointerException.class, () -> new DirectoryNode("root").addChild(null));
    }

    @Test
    void rejectsDuplicateSiblingNamesWithoutAttachingTheRejectedNode() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode existing = new FileNode("shared", 4);
        DirectoryNode duplicate = new DirectoryNode("shared");
        root.addChild(existing);

        assertThrows(IllegalArgumentException.class, () -> root.addChild(duplicate));
        assertThrows(IllegalArgumentException.class, () -> root.addChild(existing));
        assertEquals(List.of(existing), root.getChildren());
        assertSame(root, existing.getParent());
        assertNull(duplicate.getParent());
    }

    @Test
    void rejectsMultipleOwnersForBothFilesAndDirectories() {
        DirectoryNode firstParent = new DirectoryNode("first");
        DirectoryNode secondParent = new DirectoryNode("second");
        FileNode file = new FileNode("file.bin", 8);
        DirectoryNode directory = new DirectoryNode("child");
        firstParent.addChild(file);
        firstParent.addChild(directory);

        assertThrows(IllegalArgumentException.class, () -> secondParent.addChild(file));
        assertThrows(IllegalArgumentException.class, () -> secondParent.addChild(directory));
        assertEquals(List.of(file, directory), firstParent.getChildren());
        assertEquals(List.of(), secondParent.getChildren());
        assertSame(firstParent, file.getParent());
        assertSame(firstParent, directory.getParent());
    }

    @Test
    void rejectsADirectSelfCycle() {
        DirectoryNode root = new DirectoryNode("root");

        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));

        assertEquals(List.of(), root.getChildren());
        assertNull(root.getParent());
    }

    @Test
    void rejectsAnAncestorCycleBeforeChangingTheTree() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        DirectoryNode grandchild = new DirectoryNode("grandchild");
        root.addChild(child);
        child.addChild(grandchild);

        assertThrows(IllegalArgumentException.class, () -> grandchild.addChild(root));

        assertNull(root.getParent());
        assertSame(root, child.getParent());
        assertSame(child, grandchild.getParent());
        assertEquals(List.of(child), root.getChildren());
        assertEquals(List.of(grandchild), child.getChildren());
        assertEquals(List.of(), grandchild.getChildren());
    }

    @Test
    void childCollectionsCannotBypassIntegrityChecks() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("saved.txt", 6);
        root.addChild(file);
        List<FileSystemNode> children = root.getChildren();

        assertThrows(UnsupportedOperationException.class,
                () -> children.add(new FileNode("injected.txt", 1)));
        assertThrows(UnsupportedOperationException.class, () -> children.remove(file));
        assertThrows(UnsupportedOperationException.class, children::clear);
        assertThrows(UnsupportedOperationException.class,
                () -> file.getChildren().add(new FileNode("impossible.txt", 1)));
        assertEquals(List.of(file), root.getChildren());
        assertSame(root, file.getParent());
    }

    @Test
    void removalAllowsMovingANodeAndReusingItsOldName() {
        DirectoryNode firstParent = new DirectoryNode("first");
        DirectoryNode secondParent = new DirectoryNode("second");
        DirectoryNode moved = new DirectoryNode("folder");
        FileNode nested = new FileNode("nested.txt", 13);
        moved.addChild(nested);
        firstParent.addChild(moved);

        assertSame(moved, firstParent.removeChild("folder"));
        assertNull(moved.getParent());
        secondParent.addChild(moved);
        FileNode replacement = new FileNode("folder", 2);
        firstParent.addChild(replacement);

        assertSame(secondParent, moved.getParent());
        assertSame(moved, nested.getParent());
        assertEquals(List.of(moved), secondParent.getChildren());
        assertEquals(List.of(nested), moved.getChildren());
        assertEquals(List.of(replacement), firstParent.getChildren());
    }

    @Test
    void removingAMissingNameLeavesTheTreeUnchanged() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("kept.txt", 5);
        root.addChild(file);

        assertNull(root.removeChild("missing.txt"));

        assertEquals(List.of(file), root.getChildren());
        assertSame(root, file.getParent());
    }

    @Test
    void equalNamesAreAllowedInDifferentBranches() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode firstFile = new FileNode("same.txt", 11);
        FileNode secondFile = new FileNode("same.txt", 12);
        root.addChild(first);
        root.addChild(second);
        first.addChild(firstFile);
        second.addChild(secondFile);

        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        FileSystemWalker.walk(root, visitor);

        assertEquals(List.of("first/same.txt", "second/same.txt"), visitor.getMatches());
        assertSame(first, firstFile.getParent());
        assertSame(second, secondFile.getParent());
    }
}
