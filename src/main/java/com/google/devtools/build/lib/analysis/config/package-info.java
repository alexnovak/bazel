/**
 * Defines the build configuration model for Bazel's analysis phase.
 *
 * <p>A build configuration ({@link
 * com.google.devtools.build.lib.analysis.config.BuildConfigurationValue}) captures all
 * "environmental" information that affects build output &mdash; such as target platform, compilation
 * mode, and command-line flags &mdash; and is composed of modular {@link
 * com.google.devtools.build.lib.analysis.config.Fragment Fragment}s, each derived from its
 * corresponding {@link com.google.devtools.build.lib.analysis.config.FragmentOptions}. This package
 * also provides the configuration transition framework (in the {@code transitions} subpackage) that
 * enables dependencies to be analyzed under different configurations than their parents.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.analysis.config.BuildConfigurationValue} &mdash; the
 *       top-level configuration object, a Skyframe value containing all fragments, build options,
 *       output directories, action environment, and settings like compilation mode and coverage.
 *   <li>{@link com.google.devtools.build.lib.analysis.config.BuildOptions} &mdash; the complete set
 *       of command-line options from all {@link
 *       com.google.devtools.build.lib.analysis.config.FragmentOptions} classes (both native and
 *       Starlark). Supports cloning, diffing, and serialization.
 *   <li>{@link com.google.devtools.build.lib.analysis.config.FragmentOptions} &mdash; abstract base
 *       for a module's command-line options. Each rule domain (C++, Java, Python, etc.) defines its
 *       own subclass.
 *   <li>{@link com.google.devtools.build.lib.analysis.config.Fragment} &mdash; abstract base for
 *       language-specific configuration data (e.g., {@code CppConfiguration}, {@code
 *       JavaConfiguration}). Immutable, Starlark-accessible via {@code ctx.fragments}.
 *   <li>{@link com.google.devtools.build.lib.analysis.config.CoreOptions} &mdash; universal options
 *       affecting all configurations regardless of language, such as {@code --compilation_mode},
 *       {@code --cpu}, and {@code --stamp}.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>Build configurations are fundamental to Bazel's correctness and multi-platform support. During
 * analysis, every configured target is created with a specific configuration that determines output
 * directories, compiler flags, and platform settings. The transition framework allows dependency
 * edges to change configurations (e.g., building a tool for the exec platform while building the
 * main target for the target platform), supporting multi-architecture builds and correct caching.
 */
package com.google.devtools.build.lib.analysis.config;
