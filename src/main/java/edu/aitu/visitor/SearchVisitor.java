package edu.aitu.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Combines a filename predicate and an inclusive minimum byte size. */
public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private VisitContext context = new VisitContext("", 0);

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "filenameCriterion");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size cannot be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void reset() {
        matches.clear();
        context = new VisitContext("", 0);
    }

    @Override
    public void setContext(VisitContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public void visit(FileNode file) {
        if (file.getSize() >= minimumSize && filenameCriterion.test(file.getName())) {
            String path = context.relativePath().isEmpty()
                    ? file.getName() : context.relativePath();
            matches.add(path);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Only filenames are searched; directory names are not candidates.
    }

    /** Returns a sorted immutable snapshot; a file used as root has its own name. */
    public List<String> getMatches() {
        return matches.stream().sorted().toList();
    }
}
