package dk.alexandra.fresco.suite.dummy.arithmetic;

import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.network.SCENetwork;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigInteger;

/**
 * Implements openings for the Dummy Arithmetic protocol suite, where all operations are done in the
 * clear.
 */
public class DummyArithmeticOpenToAllProtocol extends DummyArithmeticNativeProtocol<BigInteger> {

  Computation<SInt> closed;
  BigInteger opened;

  /**
   * Constructs a native protocol to open a closed integer towards all parties.
   * 
   * @param s a computation supplying the {@link SInt} to open
   */
  public DummyArithmeticOpenToAllProtocol(Computation<SInt> s) {
    super();
    this.closed = s;
  }

  @Override
  public EvaluationStatus evaluate(int round, DummyArithmeticResourcePool resourcePool,
      SCENetwork network) {
    opened = ((DummyArithmeticSInt) closed.out()).getValue();
    return EvaluationStatus.IS_DONE;
  }

  @Override
  public BigInteger out() {
    return opened;
  }

}
