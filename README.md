# Git implementation
Implementation of a subset of git features

## Objectives
- Learn how git works in depth
- Try Scala3
- Work with hexagonal architecture
- Try to integrate practices and patterns from DDD 
- (double loop) TDD approach

## Chapters

### Chapter 1: Git objects


#### Episode 1: Primitive Blob Hashing
- motivations and presentation of the objectives
- generated project `sbt new scala/scala3.g8`
- hash a blob
  - What is a blob? 
    - SHA1 of file with a prefix `blob <content_size>\0<content>`
    - Hash of a blob: `echo -n 'test content' | git hash-object --stdin`
    - Comparing with sha1 hash of the same string `echo -n 'blob 12\0test content' | shasum -a 1`

#### Episode 2: Refactoring to use hexagonal architecture and introduce concepts like Command and UseCase
- refactoring and extension of the code to support other input options (file, write in database, type, etc.)
  - setup domain and infrastructure packages (hexagonal architecture)
  - write a test for Main
  - introducing a `HashObjectCommand`


#### Episode 3: Add ZIO with MockConsole 
- add zio (resource management, streaming, retries, parallelism, etc.)




Next:

- write a test for hashing a file from the file system
  - large file 
- implement the feature 




## Git internals
### Objects

Source: https://git-scm.com/book/en/v2/Git-Internals-Plumbing-and-Porcelain 

Git uses the concept of _Object_. There 3 types of object:
- **blobs**. A blob basically represents the content of a file. It is stored in a file named after the hash of the content.   
- **trees**. Trees are used to represent the hierarchy between blobs. A tree contains blobs and other trees with their names. For instance :
```
100644 blob dc711f442241823069c499197accce1537f30928    .gitignore
100644 blob e5d351c3cd44aa1d8c1cb967c7e7fde1dee4b0ad    README.md
100644 blob 7a010b786eb29b895ba5799306052b996516d63b    build.sbt
040000 tree 8bac5f27882165d313f5732bb4f140003156c693    project
040000 tree 163727ec9bd17ef32ee088a52a31fe0b483fa18f    src
```
- **commits**. Commits are used to capture :
    - the `tree` snapshot of the code
    - the `parent(s)` commits. Usually a commit has only one parent, but it can have 0 to n parents. The first commit does not have any parent. A merge commit has several parents (usually 2). 
    - the `author`
    - the `commiter` 
    - a blank line
    - the commit `message`

Those files are stored in `.git/objects`.

Useful git commands:
- `git cat-file` show information about an object
    - `-p <hash>` show the content of an object. `hash` can be `master^{tree}` to reference the tree object pointed to the last version of master.
    - `-t <hash>` show the type of object
- `git hash-object` (explicit)
- `git update-index` Register file contents in the working tree to the index
- `git write-tree` 

