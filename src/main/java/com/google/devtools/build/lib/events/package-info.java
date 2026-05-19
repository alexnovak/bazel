/**
 * Provides the event reporting infrastructure used throughout Bazel for communicating diagnostics.
 *
 * <p>Errors, warnings, progress updates, and other diagnostic information are represented as
 * immutable {@link com.google.devtools.build.lib.events.Event} objects with a severity kind, then
 * dispatched through {@link com.google.devtools.build.lib.events.EventHandler} and {@link
 * com.google.devtools.build.lib.events.ExtendedEventHandler} implementations. The {@link
 * com.google.devtools.build.lib.events.Reporter} class serves as the central hub, routing events to
 * registered handlers and the Guava {@code EventBus}.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.events.Event} &mdash; an immutable diagnostic event
 *       with an {@link com.google.devtools.build.lib.events.EventKind} (ERROR, WARNING, INFO,
 *       PROGRESS, etc.) and a message. Created via factory methods such as {@code Event.error()},
 *       {@code Event.warn()}, and {@code Event.info()}.
 *   <li>{@link com.google.devtools.build.lib.events.EventHandler} &mdash; the basic listener
 *       interface with a single {@code handle(Event)} method.
 *   <li>{@link com.google.devtools.build.lib.events.ExtendedEventHandler} &mdash; extends {@link
 *       com.google.devtools.build.lib.events.EventHandler} with {@code post(Postable)} for
 *       structured domain-specific events. This is the primary handler type used throughout Bazel.
 *   <li>{@link com.google.devtools.build.lib.events.Reporter} &mdash; the central event
 *       dispatcher, routing events to all registered handlers and the event bus.
 *   <li>{@link com.google.devtools.build.lib.events.StoredEventHandler} &mdash; accumulates events
 *       in memory for later replay, used during Skyframe evaluation to buffer events that may be
 *       discarded if a computation restarts.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>Every Bazel subsystem &mdash; loading, analysis, execution, and query &mdash; reports
 * diagnostics through this package's event handlers. The integration with Skyframe ensures that
 * events emitted during node evaluation can be stored and replayed on incremental builds when a node
 * is reused without re-evaluation, providing consistent output to the user.
 */
package com.google.devtools.build.lib.events;
