package dk.alexandra.fresco.suite.spdz.gates;

import dk.alexandra.fresco.framework.builder.numeric.field.FieldElement;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePool;
import dk.alexandra.fresco.suite.spdz.datatypes.SpdzSInt;

public class SpdzKnownSIntProtocol extends SpdzNativeProtocol<SInt> {

  private final FieldElement value;
  private SpdzSInt secretValue;

  /**
   * Creates a gate loading a given value into a given SInt.
   *
   * @param value the value
   */
  public SpdzKnownSIntProtocol(FieldElement value) {
    this.value = value;
  }

  @Override
  public SInt out() {
    return secretValue;
  }

  @Override
  public EvaluationStatus evaluate(int round, SpdzResourcePool spdzResourcePool, Network network) {
    secretValue = createKnownSpdzElement(spdzResourcePool, value);
    return EvaluationStatus.IS_DONE;
  }

  static SpdzSInt createKnownSpdzElement(SpdzResourcePool spdzResourcePool, FieldElement input) {
    SpdzSInt elm;
    FieldElement globalKeyShare =
        spdzResourcePool.getDataSupplier().getSecretSharedKey();

    FieldElement mac = input.multiply(globalKeyShare);

    if (spdzResourcePool.getMyId() == 1) {
      elm = new SpdzSInt(input, mac);
    } else {
      elm = new SpdzSInt(spdzResourcePool.getFieldDefinition().createElement(0), mac);
    }
    return elm;
  }
}
