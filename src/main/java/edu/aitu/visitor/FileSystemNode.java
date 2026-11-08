package edu.aitu.visitor;

import java.util.List;
import java.util.Objects;

/** Shared identity and ownership; only DirectoryNode changes the parent. */
public abstract sealed class FileSystemNode implements Element
        permits FileNode, DirectoryNode {
    private final String name;
    private DirectoryNode parent;

    protected FileSystemNode(String name) {
        this.name = validateName(name);
    }

    public final String getName() {
        return name;
    }

    /** Returns null for an unattached node, including a tree root. */
    public final DirectoryNode getParent() {
        return parent;
    }

    /** Returns an immutable snapshot; files return an empty list. */
    public abstract List<FileSystemNode> getChildren();

    final void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")
                || name.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Name must be a non-blank path segment");
        }
        return name;
    }
}
