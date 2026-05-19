/**
 * Bazel's integration with the Skyframe incremental evaluation framework.
 *
 * <p>This package provides the concrete {@link com.google.devtools.build.skyframe.SkyFunction}
 * implementations that drive Bazel's loading, analysis, and execution phases. It bridges the generic
 * Skyframe evaluation engine ({@link com.google.devtools.build.skyframe}) with Bazel-specific build
 * concepts such as packages, configured targets, actions, and artifacts, and contains the central
 * {@link com.google.devtools.build.lib.skyframe.SkyframeExecutor} that orchestrates all
 * Skyframe-based evaluation within a Bazel server.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.skyframe.SkyframeExecutor} &mdash; the central
 *       orchestrator that sets up the {@link com.google.devtools.build.skyframe.MemoizingEvaluator},
 *       registers all {@link com.google.devtools.build.skyframe.SkyFunction} implementations,
 *       injects external state, and drives evaluation of all build phases.
 *   <li>{@link com.google.devtools.build.lib.skyframe.PackageFunction} &mdash; computes a {@code
 *       PackageValue} from a package identifier. Implements the loading phase: reads BUILD files,
 *       evaluates Starlark, resolves globs, and produces {@link
 *       com.google.devtools.build.lib.packages.Package} objects.
 *   <li>{@link com.google.devtools.build.lib.skyframe.ConfiguredTargetFunction} &mdash; produces
 *       configured target values during the analysis phase: resolves dependencies, resolves
 *       toolchains, and invokes rule logic to produce actions.
 *   <li>{@link com.google.devtools.build.lib.skyframe.ActionExecutionFunction} &mdash; drives the
 *       execution phase: resolves input artifact metadata, runs actions, and handles input discovery
 *       and action rewinding.
 *   <li>{@link com.google.devtools.build.lib.skyframe.SkyFunctions} &mdash; constants class
 *       defining all {@link com.google.devtools.build.skyframe.SkyFunctionName} values used in
 *       Bazel (e.g., PACKAGE, CONFIGURED_TARGET, ACTION_EXECUTION, GLOB, BZL_LOAD).
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This is arguably the most important package in Bazel. The generic Skyframe framework (in
 * {@link com.google.devtools.build.skyframe}) provides a parallel, incremental, memoizing
 * evaluation engine. This package populates that engine with the concrete {@link
 * com.google.devtools.build.skyframe.SkyFunction} implementations that define what Bazel actually
 * does: loading packages, analyzing configured targets, executing actions, resolving toolchains, and
 * more. {@link com.google.devtools.build.lib.skyframe.SkyframeExecutor} is the single point of
 * entry that wires everything together and is held by the runtime's command environment.
 *
 * @see com.google.devtools.build.skyframe
 */
package com.google.devtools.build.lib.skyframe;
