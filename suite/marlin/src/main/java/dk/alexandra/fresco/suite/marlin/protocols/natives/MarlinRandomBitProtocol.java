package dk.alexandra.fresco.suite.marlin.protocols.natives;

import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.suite.marlin.datatypes.CompUInt;
import dk.alexandra.fresco.suite.marlin.resource.MarlinResourcePool;

public class MarlinRandomBitProtocol<
    CompT extends CompUInt<?, ?, CompT>>
    extends MarlinNativeProtocol<SInt, CompT> {

  private SInt bit;

  @Override
  public EvaluationStatus evaluate(int round, MarlinResourcePool<CompT> resourcePool,
      Network network) {
    this.bit = resourcePool.getDataSupplier().getNextBitShare();
    return EvaluationStatus.IS_DONE;
  }

  @Override
  public SInt out() {
    return bit;
  }

}
