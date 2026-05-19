/**
 * Provides the runtime evaluator for the Starlark language.
 *
 * <p>Starlark is Bazel's embedded configuration language, used in BUILD and .bzl files. This
 * package defines the core Starlark value types, the evaluation thread, module scope, the
 * mutability/freezing model, and the entry points for calling Starlark functions and executing
 * Starlark programs. It is a standalone, Bazel-independent library under the {@code
 * net.starlark.java} namespace.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link net.starlark.java.eval.StarlarkValue} &mdash; base interface for all Starlark
 *       values (besides Java {@link String} and {@link Boolean}). Defines {@code repr()}, {@code
 *       str()}, {@code truth()}, and {@code isImmutable()}.
 *   <li>{@link net.starlark.java.eval.Starlark} &mdash; central utility class with entry points
 *       for calling Starlark functions ({@code call}, {@code fastcall}), executing programs, type
 *       checking, and the universe of predeclared bindings ({@code None}, {@code True}, {@code
 *       len}, etc.).
 *   <li>{@link net.starlark.java.eval.StarlarkThread} &mdash; represents a Starlark evaluation
 *       thread, holding the call stack, thread-local state, the associated {@link
 *       net.starlark.java.eval.Mutability}, step counting, and profiling state. Confined to a
 *       single Java thread.
 *   <li>{@link net.starlark.java.eval.Module} &mdash; a Starlark module, representing the global
 *       variable namespace populated by executing a .bzl or BUILD file.
 *   <li>{@link net.starlark.java.eval.Mutability} &mdash; controls whether Starlark values are
 *       mutable or frozen. When a thread's {@code Mutability} is closed, all values created by that
 *       thread become frozen and safe for cross-thread sharing.
 *   <li>{@link net.starlark.java.eval.Dict}, {@link net.starlark.java.eval.StarlarkList}, {@link
 *       net.starlark.java.eval.Tuple} &mdash; the core Starlark collection types.
 *   <li>{@link net.starlark.java.eval.EvalException} &mdash; the standard exception for Starlark
 *       evaluation errors.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is used pervasively across Bazel's loading and analysis phases. During loading,
 * the interpreter evaluates BUILD and .bzl files to populate package objects. During analysis,
 * Starlark rule implementations and aspects are called through this evaluator. Bazel injects its
 * domain objects (rules, providers, etc.) as {@link net.starlark.java.eval.StarlarkValue}
 * implementations and uses {@link net.starlark.java.eval.StarlarkThread} thread-locals to pass
 * Bazel-specific context.
 */
package net.starlark.java.eval;
