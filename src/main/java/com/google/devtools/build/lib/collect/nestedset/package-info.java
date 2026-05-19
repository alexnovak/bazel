/**
 * Provides the {@link com.google.devtools.build.lib.collect.nestedset.NestedSet} data structure.
 *
 * <p>{@link com.google.devtools.build.lib.collect.nestedset.NestedSet} is an immutable,
 * memory-efficient, DAG-based set representation optimized for the accumulation and transitive
 * propagation of data through Bazel's dependency graph. It is the Java implementation underlying
 * Starlark's {@code depset} type, and is critical to Bazel's performance for aggregating artifacts,
 * compiler flags, and other transitive information across large build graphs without redundant
 * copying.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.collect.nestedset.NestedSet} &mdash; the core data
 *       structure. Each node represents a union of "direct" elements and "transitive" child sets.
 *       Construction is O(direct elements), while flattening (enumeration with deduplication) is
 *       proportional to the full reachable graph.
 *   <li>{@link com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder} &mdash; builder
 *       for constructing {@link com.google.devtools.build.lib.collect.nestedset.NestedSet}
 *       instances via {@code add()} (direct elements) and {@code addTransitive()} (child sets).
 *   <li>{@link com.google.devtools.build.lib.collect.nestedset.Order} &mdash; enum defining the
 *       four traversal orders: STABLE_ORDER, COMPILE_ORDER (postorder), LINK_ORDER (topological),
 *       and NAIVE_LINK_ORDER (preorder).
 *   <li>{@link com.google.devtools.build.lib.collect.nestedset.Depset} &mdash; the Starlark-visible
 *       wrapper around {@link com.google.devtools.build.lib.collect.nestedset.NestedSet}, exposing
 *       it to {@code .bzl} files as the {@code depset} type with runtime element-type tracking.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>During the analysis phase, every rule accumulates transitive information (compiler flags,
 * runfiles, transitive source jars, etc.) using {@link
 * com.google.devtools.build.lib.collect.nestedset.NestedSet}s. The DAG structure allows sets to be
 * composed in O(1) without flattening, meaning a build graph with millions of targets can
 * accumulate transitive closures without copying. Flattening only happens at consumption points
 * (e.g., writing a command line). Serialization support enables remote analysis caching.
 */
package com.google.devtools.build.lib.collect.nestedset;
