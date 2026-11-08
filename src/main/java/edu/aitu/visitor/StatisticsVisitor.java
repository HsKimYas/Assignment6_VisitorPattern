package edu.aitu.visitor;

import java.util.Objects;

/** A third independent operation, added without editing either element class. */
public final class StatisticsVisitor implements Visitor {
    private long fileCount;
    private long directoryCount;
    private int maxDepth;
    private VisitContext context = new VisitContext("", 0);

    @Override
    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        context = new VisitContext("", 0);
    }

    @Override
    public void setContext(VisitContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        updateDepth();
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        updateDepth();
    }

    public Statistics getStatistics() {
        return new Statistics(fileCount, directoryCount, maxDepth);
    }

    private void updateDepth() {
        maxDepth = Math.max(maxDepth, context.depth());
    }
}
