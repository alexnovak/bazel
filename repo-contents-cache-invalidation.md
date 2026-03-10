# Remote Repo Contents Cache: Invalidation Analysis

This document investigates when Bazel's remote repo contents cache gets invalidated
and how to prevent unnecessary invalidations.

## Overview

The repo contents cache (`--repo_contents_cache`, defaulting to
`{--repository_cache}/contents`) stores fetched repository directories that can be
shared across workspaces. It uses a two-level lookup:

1. **Level 1 – predeclared input hash**: a fingerprint of everything known before
   the repo rule runs.
2. **Level 2 – recorded inputs**: a per-entry check of every file, directory, or
   env-var value that the repo rule _actually_ read at fetch time.

A cache hit requires both levels to match. An invalidation at either level causes a
full re-fetch (unless `--nofetch` is set).

---

## Level 1 – Predeclared Input Hash

**Source:** `DigestWriter.computePredeclaredInputHash()` –
`src/main/java/com/google/devtools/build/lib/bazel/repository/DigestWriter.java:218`

The hash is computed over:

| Input | What changes it |
|---|---|
| `MARKER_FILE_VERSION` (currently `7`) | Bumped by Bazel developers when the marker format changes; any Bazel upgrade that bumps this invalidates every cache entry. |
| `BuildLanguageOptions.stableFingerprint(starlarkSemantics)` | Any Starlark-affecting flag change (`--incompatible_*`, `--experimental_*`, etc.). |
| `repoDefinition.repoRule().id().bzlFileLabel()` | Moving or renaming the `.bzl` file that defines the repo rule. |
| `repoDefinition.repoRule().id().ruleName()` | Renaming the repo rule within that file. |
| `repoDefinition.repoRule().transitiveBzlDigest()` | **Any change to the `.bzl` file that defines the rule, or any `.bzl` it loads.** |
| `repoDefinition.name()` | Renaming the repository itself. |
| `repoDefinition.attrValues()` | Any attribute change (`urls`, `sha256`, `strip_prefix`, custom attrs…). |
| Declared env-var names + current values | Env vars listed in the rule's `environ` field (not arbitrary env vars – only the ones the rule declares it cares about). |

If the hash changes, Bazel discards all cached entries for that predeclared hash bucket
and falls through to a fresh fetch.

---

## Level 2 – Recorded Inputs

**Source:** `RepoRecordedInput.isAnyValueOutdated()` –
`src/main/java/com/google/devtools/build/lib/rules/repository/RepoRecordedInput.java:125`

After a predeclared hash match, Bazel validates each input that the repo rule recorded
during its last fetch:

### `FILE:<path>` – individual file content
- **Invalidates when:** file digest, permissions, or existence status changes (file
  appears, disappears, or is replaced by a directory).
- Recorded by calls like `repository_ctx.read()`, `repository_ctx.path()`, etc.

### `DIRENTS:<path>` – directory listing
- **Invalidates when:** the set of names in the directory changes (file added/removed).
- Does **not** track content of the files inside.
- Recorded by `repository_ctx.path().readdir()` and similar.

### `DIRTREE:<path>` – entire directory tree
- **Invalidates when:** anything under the tree changes (file names, contents,
  subdirectory structure).
- The most expensive recorded input but also the most comprehensive.

### `ENV:<name>` – environment variable value
- **Invalidates when:** the runtime value of the env var changes between builds.
- Only applies to env vars the rule reads _dynamically_ via `repository_ctx.os.environ`
  (as opposed to the statically-declared `environ` field, which is part of Level 1).

### `REPO_MAPPING:<from_repo>:<apparent_name>` – repository name mapping
- **Invalidates when:** the mapping of a repo apparent name changes (e.g. a `bazel_dep`
  version bump causes a label to resolve to a different canonical repo).

---

## Garbage Collection (expiry-based invalidation)

Even a valid cache entry will be removed by GC and force a re-fetch if it has not been
accessed recently.

**Source:** `LocalRepoContentsCache.runGc()` –
`src/main/java/com/google/devtools/build/lib/bazel/repository/cache/LocalRepoContentsCache.java:239`

| Option | Default | Effect |
|---|---|---|
| `--repo_contents_cache_gc_max_age` | `14d` | Entries not accessed within this window are deleted. Set to `0` to disable age-based GC (only duplicates will be removed). |
| `--repo_contents_cache_gc_idle_delay` | `5m` | GC runs only after the Bazel server has been idle this long. |

**How access is tracked:** every cache hit calls `CandidateRepo.touch()`, which sets
the mtime of the `.recorded_inputs` file to the current time. GC compares that mtime
against `now - maxAge`.

---

## Conditions that Cause a Cache Miss / Invalidation

Below is a consolidated list roughly ordered by frequency in practice:

1. **`.bzl` rule implementation changed** (transitive bzl digest changed) – any edit
   to the rule's `.bzl` file or anything it loads.
2. **Repository attribute changed** – `urls`, `sha256`, `strip_prefix`, custom attrs.
3. **Starlark flag changed** – `--incompatible_*` or `--experimental_*` flag added,
   removed, or flipped between builds.
4. **Env var value changed** – for vars in the rule's `environ` field or read
   dynamically via `repository_ctx.os.environ`.
5. **Repository renamed** – the canonical name (`repoDefinition.name()`) changes.
6. **Bazel upgraded** – `MARKER_FILE_VERSION` bumped.
7. **A file/dir the rule read changed** – any recorded `FILE`, `DIRENTS`, or `DIRTREE`
   input is mutated on the local filesystem.
8. **A repo mapping changed** – a `bazel_dep` version bumped causing a label to resolve
   differently.
9. **GC expiry** – entry not touched within `--repo_contents_cache_gc_max_age`.

---

## How to Prevent Unnecessary Invalidations

### 1. Pin Starlark flag changes carefully

Every Starlark-affecting flag is part of the Level 1 hash. If you are experimenting
with `--incompatible_*` flags, toggling them will bust the entire cache for every repo.
Use `.bazelrc` to keep flags stable across developer machines and CI.

### 2. Avoid editing rule `.bzl` files unnecessarily

Any whitespace or comment change to the rule's `.bzl` file (or anything it loads via
`load()`) changes `transitiveBzlDigest` and invalidates all repos defined by that rule.
Treat rule implementations as stable artifacts; extract helper utilities into separate
`.bzl` files that the rule does **not** load if those helpers change frequently.

### 3. Declare only necessary `environ` entries

The `environ` field in a repo rule statically declares which env vars the rule depends
on. Every listed var becomes part of Level 1. If a var is listed but not actually
needed, its value will still invalidate the cache when it changes. Keep `environ`
minimal.

### 4. Use deterministic, hash-pinned sources

For `http_archive` and friends, always specify `sha256`. This makes the fetch
reproducible so the result is eligible for the global repo contents cache in the first
place (only repos with `RepoMetadata.Reproducibility.YES` are cached):

```python
# RepositoryFetchFunction.java:274
if (result.reproducible() == RepoMetadata.Reproducibility.YES
    && !repoDefinition.repoRule().local()) {
    repoContentsCache.moveToCache(...)
}
```

Repos marked non-reproducible (e.g. repos that fetch the "latest" of something) are
never written to or read from the cache.

### 5. Prefer `FILE` over `DIRTREE` recorded inputs

If a repo rule reads an entire directory tree, any change anywhere in that tree
invalidates the entry. If only specific files matter, prefer reading individual files
so that only changes to those files cause invalidation.

### 6. Extend GC max age in long-running CI / dev environments

If builds happen less than once every two weeks (e.g. nightly CI), cache entries may
expire before they can be reused. Raise the limit:

```
--repo_contents_cache_gc_max_age=30d
```

Or disable age-based GC entirely (only duplicates are removed):

```
--repo_contents_cache_gc_max_age=0
```

### 7. Share the cache directory across workspaces

The primary value of this cache is cross-workspace sharing. Point multiple workspaces
at the same directory:

```
# .bazelrc
common --repository_cache=/shared/bazel-repo-cache
# repo_contents_cache defaults to {repository_cache}/contents
```

If CI agents each have their own `$HOME`, the default cache location will be per-agent.
Consider a shared NFS/network path or pre-seeding the cache on a network volume.

### 8. Force-populate the cache with `bazel fetch`

After a version upgrade or flag change that busts the Level 1 hash, run:

```
bazel fetch //...
```

This re-fetches and re-populates the cache. Subsequent builds (or builds on other
machines sharing the cache) will hit it.

---

## Key Source Files

| File | Purpose |
|---|---|
| `DigestWriter.java` | Predeclared input hash, marker file read/write |
| `RepoRecordedInput.java` | Five recorded input types and their staleness checks |
| `LocalRepoContentsCache.java` | Cache directory layout, GC logic, candidate lookup |
| `RepositoryFetchFunction.java` | Orchestrates cache lookup → fetch → cache store |
| `RepositoryOptions.java` | `--repo_contents_cache*` flags |
| `BazelRepositoryModule.java` | Cache initialization, GC idle task registration |
