# Git implementation
Implementation of a subset of git features

## Objectives
- Learn how git works in depth
- Try Scala3
- Have several loosely-coupled interchangeable components thanks to hexagonal architecture
- Try to integrate practices and patterns from DDD 
- (double loop) TDD approach

## Chapters

### Chapter 1: Making a commit

#### [:tv: Episode 1: Primitive Blob Hashing](https://www.youtube.com/watch?v=gN1Hx3C4N2Q&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=1)
[Branch `episode1`](https://github.com/Dnomyar/git/tree/episode1)
- motivations and presentation of the objectives
- generated project `sbt new scala/scala3.g8`
- hash a blob
  - What is a blob? 
    - SHA1 of file with a prefix `blob <content_size>\0<content>`
    - Hash of a blob: `echo -n 'test content' | git hash-object --stdin`
    - Comparing with sha1 hash of the same string `echo -n 'blob 12\0test content' | shasum -a 1`

#### [:tv: Episode 2: Refactoring to use hexagonal architecture and introduce concepts like Command and UseCase](https://www.youtube.com/watch?v=wzo06-IVmwk&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=2)
[Branch `episode2`](https://github.com/Dnomyar/git/tree/episode2)
- refactoring and extension of the code to support other input options (file, write in database, type, etc.)
  - setup domain and infrastructure packages (hexagonal architecture)
  - write a test for Main
  - introducing a `HashObjectCommand`

#### [:tv: Episode 3: Add ZIO with MockConsole](https://www.youtube.com/watch?v=1V_IYyuluK4&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=3) 
[Branch `episode3`](https://github.com/Dnomyar/git/tree/episode3)
- add zio (resource management, streaming, retries, parallelism, etc.)

#### [:tv: Episode 4: Hashing a stream of bytes (ZStream)](https://www.youtube.com/watch?v=F3Jf_YDIwgk&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=4)
[Branch `episode4`](https://github.com/Dnomyar/git/tree/episode4)
- objective of the chapter: making a commit
- hash stdin string - change the way the command is used: 
  - `hash-object --text "test content"` instead of `hash-object "test content"`
- Fix the encoding issue
- Hashing a stream of bytes (ZStream and ZSink)

#### [:tv: Episode 5: Hash files](https://www.youtube.com/watch?v=0rWkvwdhUwI&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=5)
[Branch `episode5`](https://github.com/Dnomyar/git/tree/episode5)
- Write test to hash a file
- Refactor so the hash object usecase accepts several types of command 
- Implement hashing a file
- Model the return type of the usecase with a richer type
- Update test to hash several files and implement


### [:tv: Episode 6: Refactor to introduce FileSystemPort and Adapter using ZLayer](https://www.youtube.com/watch?v=VW6LnSzKHEI&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=6)
[Branch `episode6`](https://github.com/Dnomyar/git/tree/episode6)
- [Refactor/hexagonal arch.] extract reading a file and have the implementation in the infrastructure package.
  - problem in the hash object usecase
  - fixing the problem

![Hexagonal diagram](https://github.com/Dnomyar/git/tree/main/diagram/git-hexagon.png)

### [:tv: Episode 7: Mock Object repository](https://www.youtube.com/watch?v=ImlxtuG0mHo&list=PLhevSyucCuqH4--MqzA7q6kcgmOzPaU7G&index=7)
[Branch `episode7`](https://github.com/Dnomyar/git/tree/episode7)
- [Business Logic] write a blob in git objects directory
  - create an ObjectRepository
  - write a test for HashObjectUseCase verifying that the repository is called

### Next:
- create the implementation for the repository and test
- [Business Logic] write a tree in git object directory
- [Business Logic] write a commit (with a tree hash provided)





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
- `git ls-files`
  - `--stage` or `-s` show all files tracked




### Useful links:
- https://git-scm.com/book/sv/v2/Git-Internals-Git-Objects
- https://stackoverflow.com/questions/4084921/what-does-the-git-index-contain-exactly
- https://git-scm.com/docs/gitglossary
- https://github.com/git/git/blob/master/Documentation/technical/index-format.txt
- https://git-scm.com/book/en/v2/Git-Internals-Packfiles