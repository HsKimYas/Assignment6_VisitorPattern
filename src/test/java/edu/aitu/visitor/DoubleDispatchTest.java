package edu.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoubleDispatchTest {
    @Test
    void acceptSelectsTheMatchingOverloadThroughElementReferences() {
        FileNode file = new FileNode("note.txt", 7);
        DirectoryNode directory = new DirectoryNode("notes");
        directory.addChild(file);
        RecordingVisitor visitor = new RecordingVisitor();

        Element fileElement = file;
        Element directoryElement = directory;
        fileElement.accept(visitor);
        directoryElement.accept(visitor);

        assertEquals(List.of(file), visitor.files);
        assertEquals(List.of(directory), visitor.directories);
        // Directory.accept dispatches once; traversal belongs to the walker.
        assertEquals(List.of(file, directory), visitor.elements);
    }

    @Test
    void walkerVisitsEachReachableNodeOnceAndSuppliesRootRelativeContext() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode branch = new DirectoryNode("branch");
        DirectoryNode empty = new DirectoryNode("empty");
        FileNode first = new FileNode("first.txt", 3);
        FileNode second = new FileNode("second.txt", 4);
        root.addChild(first);
        root.addChild(branch);
        branch.addChild(second);
        branch.addChild(empty);
        RecordingVisitor visitor = new RecordingVisitor();

        FileSystemWalker.walk(root, visitor);

        assertEquals(List.of(root, first, branch, second, empty), visitor.elements);
        assertEquals(List.of(first, second), visitor.files);
        assertEquals(List.of(root, branch, empty), visitor.directories);
        assertEquals(List.of("", "first.txt", "branch", "branch/second.txt", "branch/empty"),
                visitor.paths);
        assertEquals(List.of(0, 1, 1, 2, 2), visitor.depths);

        FileSystemWalker.walk(empty, visitor);
        assertEquals(List.of(empty), visitor.elements);
        assertEquals(List.of(""), visitor.paths);
        assertEquals(List.of(0), visitor.depths);
    }

    private static final class RecordingVisitor implements Visitor {
        private final List<FileNode> files = new ArrayList<>();
        private final List<DirectoryNode> directories = new ArrayList<>();
        private final List<FileSystemNode> elements = new ArrayList<>();
        private final List<String> paths = new ArrayList<>();
        private final List<Integer> depths = new ArrayList<>();

        @Override
        public void reset() {
            files.clear();
            directories.clear();
            elements.clear();
            paths.clear();
            depths.clear();
        }

        @Override
        public void setContext(VisitContext context) {
            paths.add(context.relativePath());
            depths.add(context.depth());
        }

        @Override
        public void visit(FileNode file) {
            files.add(file);
            elements.add(file);
        }

        @Override
        public void visit(DirectoryNode directory) {
            directories.add(directory);
            elements.add(directory);
        }
    }
}
