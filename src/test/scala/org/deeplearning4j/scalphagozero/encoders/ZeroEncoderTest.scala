package org.deeplearning4j.scalphagozero.encoders

import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.scalatest.FunSpec

class ZeroEncoderTest() extends FunSpec {
  describe("An Agent") {

    it("should have ...") {
      var arr = Nd4j.zeros(2, 3, 4)
      arr = arr.put(Array(1, 1, 1), Nd4j.scalar(12))
      System.out.print(arr)
    }
  }

}
