/**
 * Defines the core naming and identification types for Bazel's build model.
 *
 * <p>This package provides the fundamental types used to identify build entities throughout all
 * phases of a Bazel build: labels, packages, repositories, and target patterns.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.cmdline.Label} &mdash; the unique identifier for a
 *       build target (e.g., {@code @repo//package:name}). Immutable, interned, and thread-safe.
 *       Composed of a {@link com.google.devtools.build.lib.cmdline.PackageIdentifier} and a target
 *       name. The most widely-used identifier type in Bazel.
 *   <li>{@link com.google.devtools.build.lib.cmdline.PackageIdentifier} &mdash; uniquely identifies
 *       a package: a {@link com.google.devtools.build.lib.cmdline.RepositoryName} combined with a
 *       {@link com.google.devtools.build.lib.vfs.PathFragment} for the package path. Used as a
 *       Skyframe key for loading packages.
 *   <li>{@link com.google.devtools.build.lib.cmdline.RepositoryName} &mdash; the canonical name of
 *       an external repository. Defines well-known constants for the main repository and built-in
 *       repositories.
 *   <li>{@link com.google.devtools.build.lib.cmdline.RepositoryMapping} &mdash; maps apparent
 *       repository names (as used in BUILD/bzl files) to canonical names, from the viewpoint of a
 *       specific context repository. Critical for Bzlmod's dependency resolution.
 *   <li>{@link com.google.devtools.build.lib.cmdline.TargetPattern} &mdash; the parsed
 *       representation of a command-line target pattern: a single target ({@code //foo:bar}), all
 *       targets in a package ({@code //foo:all}), or a recursive pattern ({@code //foo/...}).
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>These types form Bazel's universal naming system, used in all phases: loading (resolving labels
 * in BUILD files), analysis (dependency graph construction), execution (artifact path computation),
 * and querying (target pattern evaluation).
 */
package com.google.devtools.build.lib.cmdline;
