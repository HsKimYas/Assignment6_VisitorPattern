package edu.aitu.visitor;

/** An object that dispatches an operation to its matching visitor overload. */
public interface Element {
    void accept(Visitor visitor);
}
