package org.palladiosimulator.blockchainsystems.core.tracing

interface TraceEventLogOutput : TraceEventSubscriber {
  fun initialize()
  fun cleanUp()
}
