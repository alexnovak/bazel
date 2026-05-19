/**
 * Provides Bazel's internal performance profiling infrastructure.
 *
 * <p>The profiler instruments and records timed tasks throughout the build lifecycle, collecting
 * hierarchical trace data for actions, Skyframe evaluations, VFS operations, Starlark execution,
 * and other subsystems. It outputs results in JSON trace format compatible with Chrome's {@code
 * chrome://tracing} viewer and Perfetto. The profiler is designed as a low-overhead, globally
 * accessible singleton that can be called pervasively without introducing dependency cycles.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.profiler.Profiler} &mdash; the singleton entry point
 *       for all profiling operations. Provides no-op behavior until a profiler service is installed,
 *       allowing profiler calls anywhere in the codebase without compile-time coupling to the full
 *       implementation.
 *   <li>{@link com.google.devtools.build.lib.profiler.ProfilerTask} &mdash; enum of all recognized
 *       task types (ACTION, SKYFUNCTION, VFS_STAT, STARLARK_USER_FN, REMOTE_EXECUTION, etc.), each
 *       with a human-readable description and minimum duration threshold for recording.
 *   <li>{@link com.google.devtools.build.lib.profiler.SilentCloseable} &mdash; an {@link
 *       AutoCloseable} subinterface that declares no checked exceptions, enabling the idiomatic
 *       try-with-resources profiling pattern: {@code try (SilentCloseable c =
 *       Profiler.instance().profile("task")) { ... }}.
 *   <li>{@link com.google.devtools.build.lib.profiler.ProfilePhase} &mdash; enum of build phase
 *       markers (LAUNCH, INIT, ANALYZE, EXECUTE, FINISH) used as separators in the profiling
 *       output.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>The profiler is deliberately dependency-free (no dependencies on other {@code build.lib}
 * packages) to avoid circular relationships, since those packages themselves contain profiler calls.
 * It instruments all major phases of a build and nearly all subsystems, feeding the {@code
 * --profile} flag and giving users visibility into performance bottlenecks.
 */
package com.google.devtools.build.lib.profiler;
