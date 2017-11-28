package dk.alexandra.fresco.logging;

import dk.alexandra.fresco.framework.ProtocolCollection;
import dk.alexandra.fresco.framework.sce.evaluator.BatchEvaluationStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.NetworkBatchDecorator;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;

public class BatchEvaluationLoggingDecorator<ResourcePoolT extends ResourcePool>
    implements BatchEvaluationStrategy<ResourcePoolT>, PerformanceLogger {

  private BatchEvaluationStrategy<ResourcePoolT> delegate;
  private int counter = 0;
  private int noNativeProtocols = 0;
  private int minNoNativeProtocolsPerBatch = Integer.MAX_VALUE;
  private int maxNoNativeProtocolsPerBatch = 0;

  public BatchEvaluationLoggingDecorator(
      BatchEvaluationStrategy<ResourcePoolT> batchEvaluation) {
    this.delegate = batchEvaluation;
  }

  @Override
  public void processBatch(
      ProtocolCollection<ResourcePoolT> protocols, ResourcePoolT resourcePool,
      NetworkBatchDecorator network) {
    int size = protocols.size();
    this.counter++;
    noNativeProtocols += size;
    if (minNoNativeProtocolsPerBatch > size) {
      minNoNativeProtocolsPerBatch = size;
    }
    if (maxNoNativeProtocolsPerBatch < size) {
      maxNoNativeProtocolsPerBatch = size;
    }
    delegate.processBatch(protocols, resourcePool, network);
  }

  @Override
  public void printPerformanceLog(int myId) {
    log.info("=== P" + myId + ": Native protocols per batch metrics ===");
    log.info("Total amount of batches reached: " + counter);
    log.info("Total amount of native protocols evaluated: " + noNativeProtocols);
    log.info("minimum amount of native protocols evaluated in a single batch: "
        + minNoNativeProtocolsPerBatch);
    log.info("maximum amount of native protocols evaluated in a single batch: "
        + maxNoNativeProtocolsPerBatch);
    double avg = noNativeProtocols / (double) counter;
    log.info("Average amount of native protocols evaluated per batch: " + df.format(avg));
  }

  @Override
  public void reset() {
    counter = 0;
    noNativeProtocols = 0;
    minNoNativeProtocolsPerBatch = Integer.MAX_VALUE;
    maxNoNativeProtocolsPerBatch = 0;
  }


}