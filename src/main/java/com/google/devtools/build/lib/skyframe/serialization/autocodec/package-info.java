/**
 * Compile-time annotation processing for automatically generating {@link
 * com.google.devtools.build.lib.skyframe.serialization.ObjectCodec} implementations.
 *
 * <p>By annotating a class with {@link
 * com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec @AutoCodec}, developers
 * trigger a javac annotation processor that inspects the class's constructor or factory method
 * parameters, matches them to fields, and generates efficient serialization/deserialization code.
 * This package also provides {@link
 * com.google.devtools.build.lib.skyframe.serialization.autocodec.SerializationConstant
 * @SerializationConstant} for trivially serializing singleton static final fields as integer tags.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec} &mdash;
 *       the primary annotation applied to classes that need generated codecs. Works by identifying a
 *       unique instantiator (constructor or factory method) whose parameters correspond to
 *       serialized fields. Supports nested annotations: {@code @AutoCodec.Instantiator} to mark a
 *       specific constructor/factory, and {@code @AutoCodec.Interner} to mark a static interning
 *       method for deduplication during deserialization.
 *   <li>{@link
 *       com.google.devtools.build.lib.skyframe.serialization.autocodec.SerializationConstant}
 *       &mdash; annotation for static final fields that should be serialized as integer tags
 *       (singleton references).
 *   <li>{@code AutoCodecProcessor} &mdash; the javac annotation processor that processes {@code
 *       @AutoCodec} annotations and generates codec classes following the {@code
 *       Target_AutoCodec} naming convention.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>Bazel's Skyframe framework needs to serialize and deserialize {@link
 * com.google.devtools.build.skyframe.SkyValue} objects for remote caching and cross-invocation
 * persistence. Writing codecs by hand for every type is tedious and error-prone, so {@code
 * @AutoCodec} automates this. The generated codecs are automatically registered in the {@link
 * com.google.devtools.build.lib.skyframe.serialization.ObjectCodecRegistry} and discovered at
 * runtime.
 *
 * @see com.google.devtools.build.lib.skyframe.serialization
 */
package com.google.devtools.build.lib.skyframe.serialization.autocodec;
