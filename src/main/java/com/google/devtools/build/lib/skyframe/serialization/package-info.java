/**
 * A framework for deterministic binary serialization and deserialization of Java objects.
 *
 * <p>This package is designed for serializing Skyframe graph nodes ({@link
 * com.google.devtools.build.skyframe.SkyValue} instances) and their transitive object graphs. It
 * provides the {@link com.google.devtools.build.lib.skyframe.serialization.ObjectCodec} interface
 * for type-specific serialization logic, context classes for stateful serialization sessions with
 * memoization and cycle handling, an {@link
 * com.google.devtools.build.lib.skyframe.serialization.ObjectCodecRegistry} for codec dispatch, and
 * the {@link com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec} annotation
 * for automatic codec generation. This infrastructure supports remote analysis caching and
 * cross-invocation state persistence.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.ObjectCodec} &mdash; the core
 *       interface defining {@code serialize()} and {@code deserialize()} for a specific type.
 *       Supports memoization strategies for handling cyclic and shared references.
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.SerializationContext} &mdash;
 *       provided to codecs during serialization. Handles recursive serialization of nested objects,
 *       codec lookup, memoization/back-references, and dependency injection.
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.DeserializationContext} &mdash;
 *       provided to codecs during deserialization. Handles tag-based codec dispatch and
 *       back-reference resolution for memoized objects.
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.ObjectCodecRegistry} &mdash;
 *       maps Java classes to their codecs and assigns deterministic integer tags for compact on-wire
 *       representation.
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.AsyncObjectCodec} &mdash; a
 *       codec variant supporting asynchronous deserialization for handling complex object graphs.
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.LeafObjectCodec} &mdash; a
 *       restricted codec that can only delegate to other leaf codecs, guaranteed acyclic.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>When Bazel needs to cache Skyframe values (e.g., configured targets, packages) across
 * invocations or send them to a remote cache, this framework handles converting complex Java object
 * graphs into bytes and back. There are hundreds of codec implementations across the codebase, one
 * per serializable type. The {@code AutoCodec} annotation processor reduces boilerplate by
 * generating codecs automatically at compile time.
 *
 * @see com.google.devtools.build.lib.skyframe.serialization.autocodec
 */
package com.google.devtools.build.lib.skyframe.serialization;
