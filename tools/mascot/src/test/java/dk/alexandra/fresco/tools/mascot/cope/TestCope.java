package dk.alexandra.fresco.tools.mascot.cope;

import dk.alexandra.fresco.tools.mascot.CustomAsserts;
import dk.alexandra.fresco.tools.mascot.MascotSecurityParameters;
import dk.alexandra.fresco.tools.mascot.MascotTestContext;
import dk.alexandra.fresco.tools.mascot.MascotTestUtils;
import dk.alexandra.fresco.tools.mascot.NetworkedTest;
import dk.alexandra.fresco.tools.mascot.field.MascotFieldElement;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class TestCope extends NetworkedTest {

  private List<MascotFieldElement> runSigner(MascotTestContext ctx, Integer otherId,
      MascotFieldElement macKeyShare, int numExtends) {
    CopeSigner signer =
        new CopeSigner(ctx.getResourcePool(), ctx.getNetwork(), otherId, macKeyShare);
    return signer.extend(numExtends);
  }

  private List<MascotFieldElement> runInputter(MascotTestContext ctx, Integer otherId,
      List<MascotFieldElement> inputs) {
    CopeInputter inputter = new CopeInputter(ctx.getResourcePool(), ctx.getNetwork(), otherId);
    return inputter.extend(inputs);
  }

  @Test
  public void testSingleExtend() {
    initContexts(2);

    // left parties input (can be multiple)
    MascotFieldElement macKeyShare = new MascotFieldElement(new BigInteger("11231"), getModulus());

    // single right party input element
    MascotFieldElement input = new MascotFieldElement(7, getModulus());
    List<MascotFieldElement> inputs = Collections.singletonList(input);

    // define task each party will run
    Callable<List<MascotFieldElement>> partyOneTask = () -> runSigner(contexts.get(1), 2, macKeyShare, 1);
    Callable<List<MascotFieldElement>> partyTwoTask = () -> runInputter(contexts.get(2), 1, inputs);

    // run tasks and get ordered list of results
    List<List<MascotFieldElement>> results =
        testRuntime.runPerPartyTasks(Arrays.asList(partyOneTask, partyTwoTask));

    // get per party results
    List<MascotFieldElement> leftResults = results.get(0);
    List<MascotFieldElement> rightResults = results.get(1);

    MascotFieldElement expected = macKeyShare.multiply(input);
    MascotFieldElement actual = leftResults.get(0).add(rightResults.get(0));
    CustomAsserts.assertEquals(expected, actual);
  }

  private void testBatchedExtend(int lambdaSecurityParam) {
    initContexts(2,
        new MascotSecurityParameters(getDefaultParameters().getModBitLength(), lambdaSecurityParam,
            getDefaultParameters().getPrgSeedLength(), getDefaultParameters().getNumCandidatesPerTriple()));

    // left parties input (can be multiple)
    MascotFieldElement macKeyShare = new MascotFieldElement(new BigInteger("11231"), getModulus());

    // multiple input elements
    int[] inputArr = {7, 444, 112, 11};
    List<MascotFieldElement> inputs = MascotTestUtils.generateSingleRow(inputArr, getModulus());

    // define task each party will run
    Callable<List<MascotFieldElement>> partyOneTask =
        () -> runSigner(contexts.get(1), 2, macKeyShare, inputs.size());
    Callable<List<MascotFieldElement>> partyTwoTask = () -> runInputter(contexts.get(2), 1, inputs);

    // run tasks and get ordered list of results
    List<List<MascotFieldElement>> results =
        testRuntime.runPerPartyTasks(Arrays.asList(partyOneTask, partyTwoTask));

    // get per party results
    List<MascotFieldElement> leftResults = results.get(0);
    List<MascotFieldElement> rightResults = results.get(1);

    int[] expectedArr = {7 * 11231 % getModulus().intValue(), 444 * 11231 % getModulus().intValue(),
        112 * 11231 % getModulus().intValue(), 11 * 11231 % getModulus().intValue()};
    List<MascotFieldElement> expected =
        MascotTestUtils.generateSingleRow(expectedArr, getModulus());

    List<MascotFieldElement> actual = IntStream.range(0, expected.size())
        .mapToObj(idx -> leftResults.get(idx).add(rightResults.get(idx)))
        .collect(Collectors.toList());

    CustomAsserts.assertEquals(expected, actual);
  }

  @Test
  public void testEqualSecParamAndBitLength() {
    testBatchedExtend(this.getDefaultParameters().getLambdaSecurityParam());
  }

  @Test
  public void testUnequalSecParamAndBitLength() {
    testBatchedExtend(24);
  }

}
