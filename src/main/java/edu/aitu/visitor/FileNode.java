package edu.aitu.visitor;

import java.util.List;
import java.util.Objects;

/** An immutable file with a non-negative size in bytes. */
public final class FileNode extends FileSystemNode {
    private final long size;

    public FileNode(String name, long size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    @Override
    public List<FileSystemNode> getChildren() {
        return List.of();
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor, "visitor").visit(this);
    }
}
