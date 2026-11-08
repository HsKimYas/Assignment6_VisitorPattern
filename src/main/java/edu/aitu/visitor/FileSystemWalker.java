package edu.aitu.visitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** The only traversal owner: iterative, insertion-order depth-first traversal. */
public final class FileSystemWalker {
    private FileSystemWalker() {
    }

    /** Runs a fresh operation. A chosen subtree root always starts at depth zero. */
    public static void walk(FileSystemNode root, Visitor visitor) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(visitor, "visitor");
        visitor.reset();
        Deque<PendingVisit> pending = new ArrayDeque<>();
        pending.push(new PendingVisit(root, new VisitContext("", 0)));
        while (!pending.isEmpty()) {
            PendingVisit current = pending.pop();
            visitor.setContext(current.context());
            current.node().accept(visitor);
            pushChildren(current, pending);
        }
    }

    private static void pushChildren(PendingVisit current, Deque<PendingVisit> pending) {
        List<FileSystemNode> children = current.node().getChildren();
        for (int index = children.size() - 1; index >= 0; index--) {
            FileSystemNode child = children.get(index);
            String path = childPath(current.context().relativePath(), child.getName());
            int depth = Math.incrementExact(current.context().depth());
            pending.push(new PendingVisit(child, new VisitContext(path, depth)));
        }
    }

    private static String childPath(String parentPath, String childName) {
        return parentPath.isEmpty() ? childName : parentPath + "/" + childName;
    }

    private record PendingVisit(FileSystemNode node, VisitContext context) {
    }
}
