package org.deeplearning4j.scalphagozero.board

/**
  * Maps a point on a board grid to a set of other points on that same grid.
  *
  * @author Barry Becker
  */
class NeighborMap(var map: Map[Point, List[Point]] = Map()) {

  def + (pair: (Point, List[Point])): NeighborMap = new NeighborMap(map + pair)
  def apply(point: Point): List[Point] = map(point)

  def findNumTrueNeighbors(player: Player, point: Point, grid: Grid): Int =
    this(point).count(neighbor => {
      val str = grid.getString(neighbor)
      str.isDefined && str.get.player == player
    })
}
