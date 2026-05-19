/**
 * Skyframe: Bazel's incremental computation framework.
 *
 * <p>Skyframe models the build as a directed acyclic graph of nodes, where each node is identified
 * by a {@link com.google.devtools.build.skyframe.SkyKey} and computes a {@link
 * com.google.devtools.build.skyframe.SkyValue} via a {@link
 * com.google.devtools.build.skyframe.SkyFunction}. The framework automatically tracks dependencies
 * between nodes, memoizes computed values, and performs parallel, incremental re-evaluation when
 * inputs change &mdash; only recomputing nodes whose transitive dependencies have been invalidated.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.skyframe.SkyKey} &mdash; identifies a node in the graph;
 *       effectively a ({@link com.google.devtools.build.skyframe.SkyFunctionName}, argument) pair.
 *   <li>{@link com.google.devtools.build.skyframe.SkyValue} &mdash; the computed result produced by
 *       a {@link com.google.devtools.build.skyframe.SkyFunction} for a given key.
 *   <li>{@link com.google.devtools.build.skyframe.SkyFunction} &mdash; the computation logic for a
 *       node type. Its {@code compute(SkyKey, Environment)} method returns a {@link
 *       com.google.devtools.build.skyframe.SkyValue} or {@code null} if dependencies are missing
 *       (triggering a Skyframe restart).
 *   <li>{@link com.google.devtools.build.skyframe.SkyFunction.Environment} &mdash; services
 *       provided to a {@link com.google.devtools.build.skyframe.SkyFunction} during evaluation:
 *       requesting dependency values, checking for missing values, and accessing event listeners.
 *   <li>{@link com.google.devtools.build.skyframe.MemoizingEvaluator} &mdash; the top-level graph
 *       evaluator that accepts root keys, evaluates their transitive closures, caches results, and
 *       supports invalidation of stale values.
 *   <li>{@link com.google.devtools.build.skyframe.NodeEntry} &mdash; the mutable, thread-safe
 *       in-graph representation of a single node, tracking its lifecycle state, dependencies,
 *       reverse dependencies, and value.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>Skyframe is the computational core of Bazel. Every major Bazel operation &mdash; loading
 * packages, resolving configurations, analyzing configured targets, executing actions &mdash; is
 * implemented as a {@link com.google.devtools.build.skyframe.SkyFunction}. The framework's
 * automatic dependency tracking and memoization enable Bazel's hallmark incrementality: on
 * subsequent builds, only nodes whose transitive inputs have changed are recomputed. Skyframe also
 * handles parallelism (evaluating independent nodes concurrently), error propagation (with
 * keep-going semantics), and cycle detection. It sits beneath all three build phases (loading,
 * analysis, execution) and unifies them into a single dependency graph.
 *
 * @see com.google.devtools.build.lib.skyframe
 */
package com.google.devtools.build.skyframe;
