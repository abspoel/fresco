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
package dk.alexandra.fresco.suite.dummy.bool;

import dk.alexandra.fresco.framework.network.NetworkingStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.framework.sce.resources.ResourcePoolImpl;
import dk.alexandra.fresco.lib.bool.BasicBooleanTests;
import dk.alexandra.fresco.lib.bool.ComparisonBooleanTests;
import dk.alexandra.fresco.lib.collections.sort.CollectionsSortingTests;
import dk.alexandra.fresco.lib.compare.CompareTests;
import dk.alexandra.fresco.lib.crypto.BristolCryptoTests;
import dk.alexandra.fresco.lib.debug.BinaryDebugTests;
import dk.alexandra.fresco.lib.field.bool.generic.FieldBoolTests;
import dk.alexandra.fresco.lib.math.bool.add.AddTests;
import dk.alexandra.fresco.lib.math.bool.log.LogTests;
import dk.alexandra.fresco.lib.math.bool.mult.MultTests;
import org.junit.Test;


/**
 * Various tests of the dummy protocol suite.
 *
 * Currently, we simply test that AES works using the dummy protocol suite.
 */
public class TestDummyProtocolSuite
    extends AbstractDummyBooleanTest {

  // Basic tests for boolean suites
  @Test
  public void test_basic_logic() throws Exception {
    runTest(new BasicBooleanTests.TestInput<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
    runTest(new BasicBooleanTests.TestRandomBitOutputToSingle<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
    runTest(new BasicBooleanTests.TestXOR<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
    runTest(new BasicBooleanTests.TestAND<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
    runTest(new BasicBooleanTests.TestNOT<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  // lib.field.bool.generic
  // Slightly more advanced protocols for lowlevel logic operations
  @Test
  public void test_XNor() throws Exception {
    runTest(new FieldBoolTests.TestXNorFromXorAndNot<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
    runTest(new FieldBoolTests.TestXNorFromOpen<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_OR() throws Exception {
    runTest(new FieldBoolTests.TestOrFromXorAnd<ResourcePoolImpl>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
    runTest(new FieldBoolTests.TestOrFromCopyConst<ResourcePoolImpl>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_NAND() throws Exception {
    runTest(new FieldBoolTests.TestNandFromAndAndNot<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
    runTest(new FieldBoolTests.TestNandFromOpen<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_AndFromCopy() throws Exception {
    runTest(new FieldBoolTests.TestAndFromCopyConst<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  // lib.math.bool
  @Test
  public void test_One_Bit_Half_Adder() throws Exception {
    runTest(new AddTests.TestOnebitHalfAdder<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_One_Bit_Full_Adder() throws Exception {
    runTest(new AddTests.TestOnebitFullAdder<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Binary_Adder() throws Exception {
    runTest(new AddTests.TestFullAdder<>(), EvaluationStrategy.SEQUENTIAL_BATCHED,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Binary_BitIncrementAdder() throws Exception {
    runTest(new AddTests.TestBitIncrement<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Binary_Mult() throws Exception {
    runTest(new MultTests.TestBinaryMult<>(), EvaluationStrategy.SEQUENTIAL_BATCHED,
        NetworkingStrategy.KRYONET);
  }

  // Bristol tests
  @Test
  public void test_Mult32x32_Sequential() throws Exception {
    runTest(new BristolCryptoTests.Mult32x32Test<>(true),
        EvaluationStrategy.SEQUENTIAL, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_AES_Sequential() throws Exception {
    runTest(new BristolCryptoTests.AesTest<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_AES_SequentialBatched() throws Exception {
    runTest(new BristolCryptoTests.AesTest<>(true),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_DES_Sequential() throws Exception {
    runTest(new BristolCryptoTests.DesTest<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_MD5_Sequential() throws Exception {
    runTest(new BristolCryptoTests.MD5Test<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_SHA1_Sequential() throws Exception {
    runTest(new BristolCryptoTests.Sha1Test<>(true), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_SHA256_Sequential() throws Exception {
    runTest(new BristolCryptoTests.Sha256Test<>(true),
        EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_basic_logic_all_in_one() throws Exception {
    runTest(new BasicBooleanTests.TestBasicProtocols<>(true),
        EvaluationStrategy.SEQUENTIAL, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_comparison() throws Exception {
    runTest(new ComparisonBooleanTests.TestGreaterThan<>(),
        EvaluationStrategy.SEQUENTIAL, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_comparison_unequal_length() throws Exception {
    runTest(new ComparisonBooleanTests.TestGreaterThanUnequalLength<>(),
        EvaluationStrategy.SEQUENTIAL, NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_equality() throws Exception {
    runTest(new ComparisonBooleanTests.TestEquality<>(),
        EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  // collections.sort
  @Test
  public void test_Uneven_Odd_Even_Merge_2_parties() throws Exception {
    runTest(new CollectionsSortingTests.TestOddEvenMerge<>(),
        EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Keyed_Compare_And_Swap_2_parties() throws Exception {
    runTest(new CollectionsSortingTests.TestKeyedCompareAndSwap<>(),
        EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Compare_And_Swap() throws Exception {
    runTest(new CompareTests.CompareAndSwapTest<>(), EvaluationStrategy.SEQUENTIAL,
        NetworkingStrategy.KRYONET);
  }

  @Test
  public void test_Debug_Marker() throws Exception {
    runTest(new BinaryDebugTests.TestBinaryOpenAndPrint<>(),
        EvaluationStrategy.SEQUENTIAL_BATCHED, NetworkingStrategy.KRYONET);
  }


  @Test
  public void test_Binary_Log_Nice() throws Exception {
    runTest(new LogTests.TestLogNice<>(), EvaluationStrategy.SEQUENTIAL_BATCHED,
        NetworkingStrategy.KRYONET);
  }

}
