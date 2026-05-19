/**
 * Provides Bazel's virtual file system (VFS) abstraction layer.
 *
 * <p>This package defines a platform-independent file system API that enables Bazel to work
 * uniformly across local, in-memory, remote, and delegating file systems. The abstraction allows
 * higher-level code to remain independent of the underlying storage mechanism.
 *
 * <h2>Key Abstractions</h2>
 *
 * <ul>
 *   <li>{@link com.google.devtools.build.lib.vfs.Path} &mdash; an absolute path bound to a
 *       specific {@link com.google.devtools.build.lib.vfs.FileSystem} instance. Provides all file
 *       operations (read, write, stat, symlink, etc.) by delegating to its file system.
 *   <li>{@link com.google.devtools.build.lib.vfs.PathFragment} &mdash; a lightweight, immutable,
 *       file-system-independent path representation. Can be absolute or relative. Used pervasively
 *       throughout Bazel for exec paths, package paths, and root-relative paths.
 *   <li>{@link com.google.devtools.build.lib.vfs.FileSystem} &mdash; abstract base class for file
 *       system implementations, defining a POSIX-like API (stat, read, write, mkdir, symlink,
 *       digest). Concrete implementations include {@code JavaIoFileSystem} (local disk) and {@code
 *       InMemoryFileSystem} (testing).
 *   <li>{@link com.google.devtools.build.lib.vfs.Root} &mdash; a root directory used in {@link
 *       com.google.devtools.build.lib.vfs.RootedPath} and {@code ArtifactRoot}. Represents a
 *       package path entry, exec root, output root, or the absolute root.
 *   <li>{@link com.google.devtools.build.lib.vfs.RootedPath} &mdash; a {@link
 *       com.google.devtools.build.lib.vfs.PathFragment} relative to a {@link
 *       com.google.devtools.build.lib.vfs.Root}. Used as a Skyframe key for file state tracking.
 * </ul>
 *
 * <h2>Architectural Role</h2>
 *
 * <p>The VFS layer is a cross-cutting foundation used in every phase of Bazel. During loading, it
 * reads BUILD files and resolves source paths. During analysis, {@link
 * com.google.devtools.build.lib.vfs.PathFragment} is used for exec paths and root-relative paths.
 * During execution, {@link com.google.devtools.build.lib.vfs.Path} provides actual file I/O for
 * reading inputs and writing outputs. The abstraction enables Bazel to swap in different file system
 * implementations (in-memory for tests, action file systems for remote execution, sandboxed file
 * systems) without changing higher-level code.
 */
package com.google.devtools.build.lib.vfs;
