package edu.aitu.visitor;

/** An immutable result; depth counts edges from the chosen operation root. */
public record Statistics(long fileCount, long directoryCount, int maxDepth) {
}
