/**
 * The Bazel server runtime framework.
 *
 * <p>This package provides the top-level server lifecycle management, command dispatching, and
 * module extension system. It contains {@link com.google.devtools.build.lib.runtime.BlazeRuntime},
 * the long-lived server singleton holding immutable configuration and services; {@link
 * com.google.devtools.build.lib.runtime.CommandEnvironment}, the per-command-invocation state; the
 * {@link com.google.devtools.build.lib.runtime.BlazeCommand} interface for defining CLI commands;
 * and the {@link com.google.devtools.build.lib.runtime.BlazeModule} plugin system through which
 * Bazel's functionality is composed from modular components.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.runtime.BlazeRuntime} &mdash; the immutable singleton
 *       for the Bazel server process. Holds the file system, the list of modules, the package
 *       factory, the rule class provider, the command registry, and other server-lifetime services.
 *   <li>{@link com.google.devtools.build.lib.runtime.CommandEnvironment} &mdash; encapsulates the
 *       mutable state for a single command invocation, including the reporter, event bus,
 *       SkyframeExecutor reference, parsed options, and working directory. Dropped after each
 *       command completes.
 *   <li>{@link com.google.devtools.build.lib.runtime.BlazeCommand} &mdash; interface for CLI
 *       commands. Each implementation has an {@code exec()} method and is annotated with {@link
 *       com.google.devtools.build.lib.runtime.Command @Command}.
 *   <li>{@link com.google.devtools.build.lib.runtime.BlazeModule} &mdash; abstract class for
 *       Bazel's plugin/extension system. Modules augment Bazel by implementing lifecycle hooks
 *       ({@code blazeStartup}, {@code beforeCommand}, {@code afterAnalysis}, {@code executorInit},
 *       etc.). Used for features like remote execution, the build event protocol, and Android
 *       support.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is the top-level entry point for the Bazel server. The client/server architecture
 * is reflected here: {@link com.google.devtools.build.lib.runtime.BlazeRuntime} lives as long as
 * the server process, while {@link com.google.devtools.build.lib.runtime.CommandEnvironment} lives
 * for a single {@code bazel build} or {@code bazel test} invocation. When a command arrives via
 * gRPC from the client, the command dispatcher creates a {@link
 * com.google.devtools.build.lib.runtime.CommandEnvironment} and invokes the relevant {@link
 * com.google.devtools.build.lib.runtime.BlazeCommand}.
 */
package com.google.devtools.build.lib.runtime;
