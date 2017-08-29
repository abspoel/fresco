package dk.alexandra.fresco.suite.tinytables.prepro;

import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.builder.binary.BinaryBuilder;
import dk.alexandra.fresco.framework.builder.binary.BuilderFactoryBinary;
import dk.alexandra.fresco.framework.builder.binary.ProtocolBuilderBinary;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.suite.tinytables.prepro.protocols.TinyTablesPreproANDProtocol;
import dk.alexandra.fresco.suite.tinytables.prepro.protocols.TinyTablesPreproCloseProtocol;
import dk.alexandra.fresco.suite.tinytables.prepro.protocols.TinyTablesPreproNOTProtocol;
import dk.alexandra.fresco.suite.tinytables.prepro.protocols.TinyTablesPreproOpenToAllProtocol;
import dk.alexandra.fresco.suite.tinytables.prepro.protocols.TinyTablesPreproXORProtocol;

public class TinyTablesPreproBuilderFactory implements BuilderFactoryBinary {

  private TinyTablesPreproFactory factory;
  private int counter = 0;

  public TinyTablesPreproBuilderFactory(TinyTablesPreproFactory factory) {
    this.factory = factory;
  }

  private int getNextId() {
    return counter++;
  }

  @Override
  public BinaryBuilder createBinaryBuilder(ProtocolBuilderBinary builder) {
    return new BinaryBuilder() {

      @Override
      public Computation<SBool> xor(Computation<SBool> left, Computation<SBool> right) {
        SBool out = factory.getSBool();
        TinyTablesPreproXORProtocol p = new TinyTablesPreproXORProtocol(left, right, out);
        builder.append(p);
        return p;
      }

      @Override
      public Computation<SBool> randomBit() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Computation<Boolean> open(Computation<SBool> toOpen, int towardsPartyId) {
        throw new RuntimeException("Not implemented yet");
      }

      @Override
      public Computation<Boolean> open(Computation<SBool> toOpen) {
        TinyTablesPreproOpenToAllProtocol p =
            new TinyTablesPreproOpenToAllProtocol(getNextId(), toOpen);
        builder.append(p);
        // Always return false - preprocessing cannot compute output.
        return () -> false;
      }

      @Override
      public Computation<SBool> not(Computation<SBool> in) {
        SBool out = factory.getSBool();
        TinyTablesPreproNOTProtocol p = new TinyTablesPreproNOTProtocol(in, out);
        builder.append(p);
        return p;
      }

      @Override
      public Computation<SBool> known(boolean known) {
        return () -> factory.getKnownConstantSBool(known);
      }

      @Override
      public Computation<SBool> input(boolean in, int inputter) {
        TinyTablesPreproCloseProtocol p = new TinyTablesPreproCloseProtocol(getNextId(), inputter);
        builder.append(p);
        return p;
      }

      @Override
      public Computation<SBool> and(Computation<SBool> left, Computation<SBool> right) {
        SBool out = factory.getSBool();
        TinyTablesPreproANDProtocol p =
            new TinyTablesPreproANDProtocol(getNextId(), left, right, out);
        builder.append(p);
        return p;
      }
    };
  }

  @Deprecated
  @Override
  public ProtocolFactory getProtocolFactory() {
    // TODO Auto-generated method stub
    return null;
  }

}
