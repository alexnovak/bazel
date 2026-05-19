/**
 * Implements the lexer, parser, name resolver, and abstract syntax tree (AST) for the Starlark
 * language.
 *
 * <p>Starlark is the configuration language used for BUILD files, .bzl files, and WORKSPACE files.
 * This package is a self-contained, Bazel-independent library that transforms Starlark source text
 * into a typed syntax tree of {@link net.starlark.java.syntax.Node} objects, then resolves variable
 * bindings and scopes. The AST is consumed downstream by the Starlark evaluator ({@link
 * net.starlark.java.eval}) for interpretation.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link net.starlark.java.syntax.Node} &mdash; abstract base class for all AST nodes.
 *       Provides source location, pretty-printing, and visitor dispatch.
 *   <li>{@link net.starlark.java.syntax.Expression} &mdash; abstract base for all expression nodes
 *       (identifiers, calls, binary operators, comprehensions, literals, etc.). Uses a {@code Kind}
 *       enum for efficient dispatch.
 *   <li>{@link net.starlark.java.syntax.Statement} &mdash; abstract base for all statement nodes
 *       (assignments, function definitions, for loops, if statements, load statements, returns,
 *       etc.).
 *   <li>{@link net.starlark.java.syntax.StarlarkFile} &mdash; root AST node representing a parsed
 *       {@code .bzl} or BUILD file. Contains the list of statements, comments, and parse errors.
 *   <li>{@link net.starlark.java.syntax.Resolver} &mdash; post-parse pass that resolves each
 *       identifier to its binding (LOCAL, GLOBAL, CELL, FREE, PREDECLARED, UNIVERSAL scope),
 *       validates variable usage, and attaches binding information to the AST.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This is the front-end of Bazel's Starlark implementation. Every BUILD, .bzl, and WORKSPACE
 * file goes through this package's lexer &rarr; parser &rarr; resolver pipeline to produce an AST.
 * The package is intentionally independent of Bazel (under the {@code net.starlark.java} namespace)
 * so it can be reused by other Starlark implementations and tools.
 *
 * @see net.starlark.java.eval
 */
package net.starlark.java.syntax;
