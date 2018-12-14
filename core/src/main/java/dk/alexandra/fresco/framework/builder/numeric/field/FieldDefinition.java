package dk.alexandra.fresco.framework.builder.numeric.field;

import dk.alexandra.fresco.framework.network.serializers.ByteSerializer;
import dk.alexandra.fresco.framework.util.StrictBitVector;
import java.math.BigInteger;
import java.util.List;

//todo maybe remove ByteSerializer interface
public interface FieldDefinition extends ByteSerializer<FieldElement> {

  BigInteger convertToUnsigned(FieldElement value);

  BigInteger convertToSigned(BigInteger signed);

  BigInteger getModulus();

  FieldElement createElement(int value);

  FieldElement createElement(String value);

  FieldElement createElement(BigInteger value);

  FieldElement deserialize(byte[] bytes);

  List<FieldElement> deserializeList(byte[] bytes);

  byte[] serialize(FieldElement fieldElement);

  byte[] serialize(List<FieldElement> fieldElements);

  StrictBitVector convertToBitVector(FieldElement fieldElement);
}

