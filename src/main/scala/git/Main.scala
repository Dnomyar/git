package git

import git.domain.*
import git.domain.usecase.HashObjectUseCase
import git.domain.usecase.HashObjectUseCase._

@main def main(str: String): Unit = {

  println(HashObjectUseCase.handleCommand(HashObjectCommand(str)))

}

