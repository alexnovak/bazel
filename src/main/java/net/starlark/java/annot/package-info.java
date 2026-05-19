/**
 * Annotations for defining the Starlark-to-Java binding interface.
 *
 * <p>This package provides the annotations used to expose Java classes and methods to the Starlark
 * language interpreter. These annotations are processed both at compile time (by {@link
 * net.starlark.java.annot.processor.StarlarkMethodProcessor}) for validation and at runtime by the
 * Starlark evaluator ({@link net.starlark.java.eval}) for reflective dispatch.
 *
 * <h2>Key Annotations</h2>
 *
 * <ul>
 *   <li>{@link net.starlark.java.annot.StarlarkBuiltin} &mdash; declares a Java class or interface
 *       as a Starlark data type. The {@code name()} attribute corresponds to what {@code type(x)}
 *       returns in Starlark. All annotated types must implement {@link
 *       net.starlark.java.eval.StarlarkValue}.
 *   <li>{@link net.starlark.java.annot.StarlarkMethod} &mdash; annotates a Java method as callable
 *       from Starlark. Specifies the name, documentation, parameter list, whether it is a struct
 *       field, and whether it accepts {@code *args}/{@code **kwargs}. Methods must be public,
 *       non-static, and on a class implementing {@link net.starlark.java.eval.StarlarkValue}.
 *   <li>{@link net.starlark.java.annot.Param} &mdash; nested annotation describing a single
 *       parameter of a {@link net.starlark.java.annot.StarlarkMethod}: its name, documentation,
 *       default value, allowed types, and positional/named semantics.
 *   <li>{@link net.starlark.java.annot.ParamType} &mdash; specifies the Java class (and optional
 *       generic type parameter) for a parameter's allowed types.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>This package is the contract layer between Java and Starlark. Virtually every Bazel-specific
 * Starlark API (rule context, providers, actions, etc.) is defined by annotating Java classes with
 * {@link net.starlark.java.annot.StarlarkBuiltin} and their methods with {@link
 * net.starlark.java.annot.StarlarkMethod}. The package is part of the standalone Starlark
 * interpreter library ({@code net.starlark.java}), making it reusable by other projects embedding
 * Starlark.
 */
package net.starlark.java.annot;
