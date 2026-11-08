package edu.aitu.visitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Owns named children; successful mutations preserve a single-parent tree. */
public final class DirectoryNode extends FileSystemNode {
    private final Map<String, FileSystemNode> children = new LinkedHashMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    /** Rejects duplicate names, shared ownership and cycles before changing state. */
    public void addChild(FileSystemNode child) {
        Objects.requireNonNull(child, "child");
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already belongs to a directory");
        }
        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name: " + child.getName());
        }
        rejectCycle(child);
        children.put(child.getName(), child);
        child.setParent(this);
    }

    /** Detaches the named child, or returns null when the name is absent. */
    public FileSystemNode removeChild(String name) {
        Objects.requireNonNull(name, "name");
        FileSystemNode removed = children.remove(name);
        if (removed != null) {
            removed.setParent(null);
        }
        return removed;
    }

    @Override
    public List<FileSystemNode> getChildren() {
        return List.copyOf(children.values());
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor").visit(this);
    }

    private void rejectCycle(FileSystemNode child) {
        for (FileSystemNode ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("A node cannot contain itself or an ancestor");
            }
        }
    }
}
