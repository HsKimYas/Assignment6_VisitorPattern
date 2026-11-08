package edu.aitu.visitor;

import java.util.Objects;

/** A root-relative path and edge depth, supplied by the walker before each visit. */
public record VisitContext(String relativePath, int depth) {
    public VisitContext {
        Objects.requireNonNull(relativePath, "relativePath");
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative");
        }
    }
}
