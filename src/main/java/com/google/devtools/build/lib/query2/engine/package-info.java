/**
 * Implements the core query language engine for Bazel's {@code query}, {@code cquery}, and {@code
 * aquery} commands.
 *
 * <p>This package defines the grammar, parser, expression AST, evaluation semantics, and built-in
 * functions ({@code deps}, {@code rdeps}, {@code allpaths}, {@code somepath}, {@code attr}, {@code
 * kind}, {@code filter}, etc.) for querying the build dependency graph. The engine is generic over
 * the node type {@code T} to support different graph representations across query variants, and uses
 * an asynchronous callback-based evaluation model for streaming results.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.query2.engine.QueryExpression} &mdash; abstract base
 *       class for all query language AST nodes. Defines asynchronous evaluation returning a {@code
 *       QueryTaskFuture} with callback-based result delivery.
 *   <li>{@link com.google.devtools.build.lib.query2.engine.QueryEnvironment} &mdash; the central
 *       interface parameterizing query evaluation over node type {@code T}. Provides graph
 *       operations (forward/reverse deps, transitive closure), target pattern resolution, and the
 *       function registry.
 *   <li>{@link com.google.devtools.build.lib.query2.engine.QueryEnvironment.QueryFunction} &mdash;
 *       interface for built-in query functions such as {@code deps()}, {@code rdeps()}, {@code
 *       attr()}, and {@code kind()}.
 *   <li>{@link com.google.devtools.build.lib.query2.engine.Callback} &mdash; functional interface
 *       for receiving streaming partial results during query evaluation.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is the evaluation engine behind {@code bazel query}, {@code bazel cquery}, and
 * {@code bazel aquery}. The engine is generic: {@code query} uses loaded {@code Target} nodes,
 * {@code cquery} uses {@code ConfiguredTarget} nodes, and {@code aquery} uses action graph nodes.
 * The {@link com.google.devtools.build.lib.query2.engine.QueryEnvironment} abstraction allows
 * different backends to provide graph traversal implementations backed by Bazel's package loading or
 * Skyframe. The asynchronous evaluation model enables streaming results before the full computation
 * completes.
 */
package com.google.devtools.build.lib.query2.engine;
