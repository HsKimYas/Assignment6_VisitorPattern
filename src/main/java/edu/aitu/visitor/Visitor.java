package edu.aitu.visitor;

/** Operations use double dispatch; traversal belongs exclusively to the walker. */
public interface Visitor {
    void visit(FileNode file);

    void visit(DirectoryNode directory);

    /** Called once by the walker before each operation, including repeated runs. */
    default void reset() {
    }

    /** Called before accept; visitors that need paths or depth consume this context. */
    default void setContext(VisitContext context) {
    }
}
