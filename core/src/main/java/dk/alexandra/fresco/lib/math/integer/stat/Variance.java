/*
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 */
package dk.alexandra.fresco.lib.math.integer.stat;

import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.builder.ComputationBuilder;
import dk.alexandra.fresco.framework.builder.numeric.NumericBuilder;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes the varians from a list of SInt values and the previously
 * computed {@link Mean mean}.
 */
public class Variance implements ComputationBuilder<SInt, ProtocolBuilderNumeric> {

  private final List<Computation<SInt>> data;
  private final Computation<SInt> mean;

  public Variance(List<Computation<SInt>> data, Computation<SInt> mean) {
    this.data = data;
    this.mean = mean;
  }

  @Override
  public Computation<SInt> buildComputation(ProtocolBuilderNumeric builder) {
    return builder.par((par) -> {
      List<Computation<SInt>> terms = new ArrayList<>(data.size());
      for (Computation<SInt> value : data) {
        Computation<SInt> term = par.seq((seq) -> {
          NumericBuilder numeric = seq.numeric();
          Computation<SInt> tmp = numeric.sub(value, mean);
          return numeric.mult(tmp, tmp);
        });
        terms.add(term);
      }
      return () -> terms;
    }).seq((seq, terms) -> seq.seq(new Mean(terms, data.size() - 1))
    );
  }

}
