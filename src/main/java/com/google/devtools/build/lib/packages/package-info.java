/**
 * Represents the result of Bazel's loading phase: the in-memory model of BUILD files.
 *
 * <p>This package defines the core abstractions for packages (directories containing a BUILD file),
 * targets (the named entities declared within packages), rule classes (the schema for rules like
 * {@code cc_library}), and attributes (the typed, named parameters of rules). It also contains
 * support for Starlark-defined rules, aspects, providers, macros, and the visibility system.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.packages.Package} &mdash; a container of targets
 *       produced by evaluating a BUILD file. Holds all rules, input files, output files, macros,
 *       the default visibility, and package-level metadata. Immutable once built.
 *   <li>{@link com.google.devtools.build.lib.packages.Target} &mdash; a node in the build
 *       dependency graph, identified by a {@link com.google.devtools.build.lib.cmdline.Label}.
 *       Subtypes include {@link com.google.devtools.build.lib.packages.Rule}, {@link
 *       com.google.devtools.build.lib.packages.InputFile}, {@link
 *       com.google.devtools.build.lib.packages.OutputFile}, and {@link
 *       com.google.devtools.build.lib.packages.PackageGroup}.
 *   <li>{@link com.google.devtools.build.lib.packages.Rule} &mdash; an instance of a build rule
 *       (e.g., {@code cc_library(name='foo', srcs=[...])}). Belongs to a {@link
 *       com.google.devtools.build.lib.packages.Package}, has a {@link
 *       com.google.devtools.build.lib.packages.RuleClass}, and a set of typed attribute values.
 *   <li>{@link com.google.devtools.build.lib.packages.RuleClass} &mdash; the schema for a rule
 *       type, defining its attribute schema, implicit outputs, configuration fragments, and
 *       transition factories. Shared across all {@link com.google.devtools.build.lib.packages.Rule}
 *       instances of the same type.
 *   <li>{@link com.google.devtools.build.lib.packages.Attribute} &mdash; metadata for a single
 *       rule attribute: name, type, default value, mandatory flag, allowed rule classes, and
 *       configuration transitions.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is the primary output of the loading phase and the primary input to the analysis
 * phase. When Bazel parses a BUILD file, the Starlark interpreter evaluates it and produces a
 * {@link com.google.devtools.build.lib.packages.Package} containing targets. During analysis,
 * {@link com.google.devtools.build.lib.packages.Rule} objects are combined with build
 * configurations to create configured targets, which in turn produce actions for execution. The
 * packages layer is purely declarative: it knows what targets exist and their attributes, but not
 * how they are built.
 */
package com.google.devtools.build.lib.packages;
