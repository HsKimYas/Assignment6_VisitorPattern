package edu.aitu.visitor;

/** Sums file bytes; a sum beyond long capacity is rejected rather than wrapped. */
public final class TotalSizeVisitor implements Visitor {
    private long totalSize;

    @Override
    public void reset() {
        totalSize = 0;
    }

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Directories contribute no bytes; the walker visits their children.
    }

    public long getTotalSize() {
        return totalSize;
    }
}
