/**
 * Contains the core abstractions and logic for Bazel's analysis phase.
 *
 * <p>In the analysis phase, targets from BUILD files are combined with build configurations to
 * produce {@link com.google.devtools.build.lib.analysis.ConfiguredTarget ConfiguredTarget}s. The
 * analysis phase resolves dependencies, evaluates rule implementations, creates actions (the build's
 * executable steps), and propagates transitive information between targets through {@link
 * com.google.devtools.build.lib.analysis.TransitiveInfoProvider TransitiveInfoProvider}s.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.analysis.ConfiguredTarget} &mdash; the central
 *       analysis result: a target combined with a build configuration. Provides access to {@link
 *       com.google.devtools.build.lib.analysis.TransitiveInfoProvider TransitiveInfoProvider}s
 *       carrying the transitive information that dependents need.
 *   <li>{@link com.google.devtools.build.lib.analysis.RuleContext} &mdash; the totality of data
 *       available during analysis of a rule: access to the rule's attributes, prerequisites,
 *       configuration fragments, the action registry, artifact creation methods, and error
 *       reporting.
 *   <li>{@link com.google.devtools.build.lib.analysis.TransitiveInfoProvider} &mdash; marker
 *       interface for objects containing rolled-up data about a target's transitive closure (e.g.,
 *       all C++ header files, all Java source jars). Implementations must be immutable.
 *   <li>{@link com.google.devtools.build.lib.analysis.RuleConfiguredTargetFactory} &mdash; the
 *       interface that rule implementations implement. Its {@code create(RuleContext)} method
 *       produces a {@link com.google.devtools.build.lib.analysis.ConfiguredTarget} with associated
 *       actions, artifacts, and providers.
 *   <li>{@link com.google.devtools.build.lib.analysis.ConfiguredAspect} &mdash; like {@link
 *       com.google.devtools.build.lib.analysis.ConfiguredTarget} but for aspects, providing
 *       cross-cutting transitive information computed on behalf of a dependent.
 *   <li>{@link com.google.devtools.build.lib.analysis.Runfiles} &mdash; represents the set of
 *       symlinks forming the runfiles tree for a binary at execution time.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is the heart of the analysis phase &mdash; the second of Bazel's three build
 * phases (loading &rarr; analysis &rarr; execution). It bridges the gap between loaded targets
 * (from {@link com.google.devtools.build.lib.packages}) and the action graph (from {@link
 * com.google.devtools.build.lib.actions}). Each (target, configuration) pair is analyzed to produce
 * a {@link com.google.devtools.build.lib.analysis.ConfiguredTarget}, which carries the providers
 * that dependent targets consume and the actions that the execution phase will run.
 */
package com.google.devtools.build.lib.analysis;
