/**
 * Defines the Build Event Protocol (BEP), a structured stream of events describing the progress and
 * results of a Bazel build invocation.
 *
 * <p>Events are organized as a directed acyclic graph where each event declares its identity and
 * announces child events, enabling consumers to reconstruct the build's structure. This package
 * provides the core interfaces for event production, serialization to protocol buffers, file
 * reference handling, and transport to local files or remote Build Event Services.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.buildeventstream.BuildEvent} &mdash; primary interface
 *       for all objects posted on the build event stream. Defines serialization to protocol buffers
 *       via {@code asStreamProto()}, file reference tracking via {@code referencedLocalFiles()}, and
 *       DAG structure via {@code getChildrenEvents()}.
 *   <li>{@link com.google.devtools.build.lib.buildeventstream.BuildEventIdUtil} &mdash; factory
 *       methods for creating structured event identifiers for all event types: build started, target
 *       completed, action completed, test result, configuration, build finished, etc.
 *   <li>{@link com.google.devtools.build.lib.buildeventstream.BuildEventTransport} &mdash;
 *       thread-safe transport interface for writing events to endpoints (local files, remote Build
 *       Event Services).
 *   <li>{@link com.google.devtools.build.lib.buildeventstream.BuildEventContext} &mdash; provides
 *       converters needed for protocol buffer serialization: path converters for file URIs, artifact
 *       group naming, and protocol options.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>The BEP is Bazel's primary mechanism for communicating build results to external consumers (CI
 * systems, build dashboards, IDEs). Events flow from throughout Bazel's internals through this
 * protocol to transports that write to local JSON/binary files or stream to remote services. The DAG
 * structure with forward-declared children ensures that consumers can detect when the stream is
 * complete. This package is the foundation for {@code --build_event_json_file}, {@code
 * --build_event_binary_file}, and {@code --bes_backend}.
 */
package com.google.devtools.build.lib.buildeventstream;
