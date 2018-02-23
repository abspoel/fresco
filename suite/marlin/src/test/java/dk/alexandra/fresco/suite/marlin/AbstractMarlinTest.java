package dk.alexandra.fresco.suite.marlin;

import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.TestThreadRunner;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.TestConfiguration;
import dk.alexandra.fresco.framework.network.Network;
import dk.alexandra.fresco.framework.network.async.AsyncNetwork;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.SecureComputationEngineImpl;
import dk.alexandra.fresco.framework.sce.evaluator.BatchEvaluationStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.BatchedProtocolEvaluator;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.logging.BatchEvaluationLoggingDecorator;
import dk.alexandra.fresco.logging.DefaultPerformancePrinter;
import dk.alexandra.fresco.logging.EvaluatorLoggingDecorator;
import dk.alexandra.fresco.logging.NetworkLoggingDecorator;
import dk.alexandra.fresco.logging.NumericSuiteLogging;
import dk.alexandra.fresco.logging.PerformanceLogger;
import dk.alexandra.fresco.logging.PerformanceLoggerCountingAggregate;
import dk.alexandra.fresco.logging.PerformancePrinter;
import dk.alexandra.fresco.suite.ProtocolSuiteNumeric;
import dk.alexandra.fresco.suite.marlin.datatypes.CompUInt;
import dk.alexandra.fresco.suite.marlin.datatypes.CompUIntFactory;
import dk.alexandra.fresco.suite.marlin.datatypes.UInt;
import dk.alexandra.fresco.suite.marlin.resource.MarlinResourcePool;
import dk.alexandra.fresco.suite.marlin.resource.storage.MarlinDataSupplier;
import dk.alexandra.fresco.suite.marlin.resource.storage.MarlinDummyDataSupplier;
import dk.alexandra.fresco.suite.marlin.resource.storage.MarlinOpenedValueStore;
import dk.alexandra.fresco.suite.marlin.resource.storage.MarlinOpenedValueStoreImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractMarlinTest<
    HighT extends UInt<HighT>,
    LowT extends UInt<LowT>,
    CompT extends CompUInt<HighT, LowT, CompT>,
    MarlinResourcePoolT extends MarlinResourcePool<HighT, LowT, CompT>> {

  protected Map<Integer, PerformanceLogger> performanceLoggers = new HashMap<>();

  protected void runTest(
      TestThreadRunner.TestThreadFactory<MarlinResourcePoolT, ProtocolBuilderNumeric> f,
      EvaluationStrategy evalStrategy, int noOfParties, boolean logPerformance) {
    List<Integer> ports = new ArrayList<>(noOfParties);
    for (int i = 1; i <= noOfParties; i++) {
      ports.add(9000 + i * (noOfParties - 1));
    }

    Map<Integer, NetworkConfiguration> netConf =
        TestConfiguration.getNetworkConfigurations(noOfParties, ports);
    Map<Integer, TestThreadRunner.TestThreadConfiguration<MarlinResourcePoolT, ProtocolBuilderNumeric>> conf =
        new HashMap<>();
    for (int playerId : netConf.keySet()) {
      PerformanceLoggerCountingAggregate aggregate
          = new PerformanceLoggerCountingAggregate();

      NetworkConfiguration partyNetConf = netConf.get(playerId);

      ProtocolSuiteNumeric<MarlinResourcePoolT> ps = createProtocolSuite();
      if (logPerformance) {
        ps = new NumericSuiteLogging<>(ps);
        aggregate.add((PerformanceLogger) ps);
      }

      BatchEvaluationStrategy<MarlinResourcePoolT> batchEvaluationStrategy =
          evalStrategy.getStrategy();
      if (logPerformance) {
        batchEvaluationStrategy =
            new BatchEvaluationLoggingDecorator<>(batchEvaluationStrategy);
        aggregate.add((PerformanceLogger) batchEvaluationStrategy);
      }
      ProtocolEvaluator<MarlinResourcePoolT> evaluator =
          new BatchedProtocolEvaluator<>(batchEvaluationStrategy, ps);
      if (logPerformance) {
        evaluator = new EvaluatorLoggingDecorator<>(evaluator);
        aggregate.add((PerformanceLogger) evaluator);
      }

      CompUIntFactory<HighT, LowT, CompT> factory = createFactory();
      MarlinOpenedValueStore<CompT> store = createOpenedValueStore();
      MarlinDataSupplier<CompT> supplier = createDataSupplier(playerId, noOfParties,
          factory);

      SecureComputationEngine<MarlinResourcePoolT, ProtocolBuilderNumeric> sce =
          new SecureComputationEngineImpl<>(ps, evaluator);

      Supplier<Network> networkSupplier = () -> {
        Network asyncNetwork = new AsyncNetwork(partyNetConf);
        if (logPerformance) {
          NetworkLoggingDecorator network = new NetworkLoggingDecorator(asyncNetwork);
          aggregate.add(network);
          return network;
        } else {
          return asyncNetwork;
        }
      };
      TestThreadRunner.TestThreadConfiguration<MarlinResourcePoolT, ProtocolBuilderNumeric> ttc =
          new TestThreadRunner.TestThreadConfiguration<>(
              sce,
              () -> createResourcePool(playerId, noOfParties, store, supplier,
                  factory, networkSupplier),
              networkSupplier);

      conf.put(playerId, ttc);
      performanceLoggers.putIfAbsent(playerId, aggregate);
    }

    TestThreadRunner.run(f, conf);
    PerformancePrinter printer = new DefaultPerformancePrinter();
    for (PerformanceLogger pl : performanceLoggers.values()) {
      printer.printPerformanceLog(pl);
    }
  }

  private MarlinOpenedValueStore<CompT> createOpenedValueStore() {
    return new MarlinOpenedValueStoreImpl<>();
  }

  private MarlinDataSupplier<CompT> createDataSupplier(int myId, int noOfParties,
      CompUIntFactory<HighT, LowT, CompT> factory) {
    return new MarlinDummyDataSupplier<>(myId, noOfParties, factory.createRandom(), factory);
  }
  
  protected abstract MarlinResourcePoolT createResourcePool(int playerId,
      int noOfParties,
      MarlinOpenedValueStore<CompT> store,
      MarlinDataSupplier<CompT> supplier,
      CompUIntFactory<HighT, LowT, CompT> factory, Supplier<Network> networkSupplier);

  protected abstract CompUIntFactory<HighT, LowT, CompT> createFactory();

  protected abstract ProtocolSuiteNumeric<MarlinResourcePoolT> createProtocolSuite();

}
