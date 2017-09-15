/*******************************************************************************
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL, and Bouncy Castle.
 * Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.lib.math.bool.add;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.Computation;
import dk.alexandra.fresco.framework.builder.binary.ProtocolBuilderBinary;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.value.SBool;

public class OneBitHalfAdder
    implements Computation<Pair<SBool, SBool>, ProtocolBuilderBinary> {

  private DRes<SBool> left, right;

  public OneBitHalfAdder(DRes<SBool> left, DRes<SBool> right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public DRes<Pair<SBool, SBool>> buildComputation(ProtocolBuilderBinary builder) {
    return builder.par(par -> {
      DRes<SBool> res = par.binary().xor(left, right);
      DRes<SBool> carry = par.binary().and(left, right);
      return () -> new Pair<SBool, SBool>(res.out(), carry.out());
    });
  }

}