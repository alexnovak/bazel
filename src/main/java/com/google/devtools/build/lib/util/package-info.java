/**
 * Provides general-purpose utility classes used throughout the Bazel codebase.
 *
 * <p>This package contains cross-cutting infrastructure that does not belong to any specific Bazel
 * subsystem, including operating system detection, exit code and failure detail handling,
 * cryptographic fingerprinting, string and shell escaping utilities, file type matching, resource
 * usage tracking, and various data structure helpers.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.util.ExitCode} &mdash; defines Bazel's numeric exit
 *       codes (SUCCESS, BUILD_FAILURE, ANALYSIS_FAILURE, etc.) with classification as permanent vs.
 *       transient failures.
 *   <li>{@link com.google.devtools.build.lib.util.DetailedExitCode} &mdash; pairs an {@link
 *       com.google.devtools.build.lib.util.ExitCode} with a protobuf {@code FailureDetail} for
 *       structured error reporting.
 *   <li>{@link com.google.devtools.build.lib.util.AbruptExitException} &mdash; an exception
 *       carrying a {@link com.google.devtools.build.lib.util.DetailedExitCode}, thrown for
 *       conditions severe enough to halt a command.
 *   <li>{@link com.google.devtools.build.lib.util.Fingerprint} &mdash; wrapper around {@link
 *       java.security.MessageDigest} for computing cryptographic hashes, used pervasively for
 *       action cache keys and change detection.
 *   <li>{@link com.google.devtools.build.lib.util.OS} &mdash; enum detecting the current operating
 *       system (Linux, macOS, Windows, etc.).
 *   <li>{@link com.google.devtools.build.lib.util.ShellEscaper} &mdash; escapes strings for safe
 *       insertion into shell commands.
 *   <li>{@link com.google.devtools.build.lib.util.FileType} &mdash; predicate-based file extension
 *       matching used to validate rule attribute values.
 * </ul>
 */
package com.google.devtools.build.lib.util;
