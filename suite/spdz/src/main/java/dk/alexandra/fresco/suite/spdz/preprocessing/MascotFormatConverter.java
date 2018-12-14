package dk.alexandra.fresco.suite.spdz.preprocessing;

import dk.alexandra.fresco.framework.builder.numeric.field.FieldElement;
import dk.alexandra.fresco.suite.spdz.datatypes.SpdzInputMask;
import dk.alexandra.fresco.suite.spdz.datatypes.SpdzSInt;
import dk.alexandra.fresco.suite.spdz.datatypes.SpdzTriple;
import dk.alexandra.fresco.tools.mascot.field.AuthenticatedElement;
import dk.alexandra.fresco.tools.mascot.field.InputMask;
import dk.alexandra.fresco.tools.mascot.field.MultiplicationTriple;

public class MascotFormatConverter {

  /**
   * Converts single {@link AuthenticatedElement} to {@link SpdzSInt}.
   *
   * @param element authenticated element
   * @return spdz element
   */
  public SpdzSInt toSpdzSInt(AuthenticatedElement element) {
    return new SpdzSInt(element.getShare(), element.getMac());
  }

  /**
   * Converts single {@link MultiplicationTriple} to {@link SpdzTriple}.
   *
   * @param triple triple to convert
   * @return converted triple
   */
  public SpdzTriple toSpdzTriple(MultiplicationTriple triple) {
    SpdzSInt a = toSpdzSInt(triple.getLeft());
    SpdzSInt b = toSpdzSInt(triple.getRight());
    SpdzSInt c = toSpdzSInt(triple.getProduct());
    return new SpdzTriple(a, b, c);
  }

  /**
   * Converts single {@link InputMask} to {@link SpdzInputMask}.
   *
   * @param mask to convert
   * @return converted mask
   */
  public SpdzInputMask toSpdzInputMask(InputMask mask) {
    FieldElement openMask = mask.getOpenValue();
    if (openMask == null) {
      return new SpdzInputMask(toSpdzSInt(mask.getMaskShare()));
    } else {
      return new SpdzInputMask(toSpdzSInt(mask.getMaskShare()), openMask);
    }
  }
}
