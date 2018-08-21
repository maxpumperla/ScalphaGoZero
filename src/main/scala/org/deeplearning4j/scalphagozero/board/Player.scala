/*
 * Copyright 2016 Skymind
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deeplearning4j.scalphagozero.board

/**
  * Player of a game of Go (either black or white)
  *
  * @param color
  */
class Player(val color: Int) {

  if (!color.equals(PlayerColor.black) && !color.equals(PlayerColor.white))
    throw new IllegalArgumentException(s"Only black and white allowed as player. got $color")

  def other: Player = if (this.color.equals(PlayerColor.black)) Player(PlayerColor.white) else Player(PlayerColor.black)
}

object Player {
  def apply(color: Int) = new Player(color)
}
