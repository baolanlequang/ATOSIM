package org.palladiosimulator.blockchainsystems.core.propagation.transaction

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event
import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable
import org.palladiosimulator.blockchainsystems.core.network.MessageDroppedTraceEvent
import org.palladiosimulator.blockchainsystems.core.propagation.GossipPropagationStrategy
import org.palladiosimulator.blockchainsystems.core.propagation.MessageImpl
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction

class RaceAwareTransactionPropagationStrategy(
    private val attackerNodeIds: Set<String>,
    private val deltaA: Long,
    private val deltaB: Long
) : GossipPropagationStrategy<Transaction>() {
    override val INV_MESSAGE_KEY: String = "TRX_INV"
    override val GET_DATA_MESSAGE_KEY: String = "TRX_GET_DATA"
    override val ELEMENT_MESSAGE_KEY: String = "TRX_MSG"

    private val MESSAGE_HEADER_BYTE_SIZE = 24
    private val INV_MESSAGE_BYTE_SIZE = 20
    private val GET_DATA_MESSAGE_BYTE_SIZE = 20

    private val EVENT_TYPE_DELAYED_GOSSIP = "RACE_DELAYED_TRX_GOSSIP"

    private val requestedTransactionIds: MutableSet<String> = mutableSetOf()

    override fun createInvMessage(element: Transaction): Message {
        return MessageImpl(element.txId, INV_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + INV_MESSAGE_BYTE_SIZE)
    }

    override fun createGetDataMessage(elementId: String): Message {
        return MessageImpl(elementId, GET_DATA_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + GET_DATA_MESSAGE_BYTE_SIZE)
    }

    override fun createElementMessage(element: Transaction): Message {
        return MessageImpl(element, ELEMENT_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + element.size)
    }

    // KEY: override distribute to insert race timing
    override fun distribute(element: Transaction) {
        val delay = computeRaceDelay(element)
        val message = createInvMessage(element)

        if (delay <= 0L) {
            networkInterface?.multicast(message)
            return
        }

        val event = DelayedGossipEvent(
            occurrenceTime = simulationContext.systemClock.currentTime + delay,
            origin = this,
            message = message
        )

        simulationContext.eventCoordinator.raiseEvent(event)
    }

    override fun dispatchEvent(event: Event) {
        if (event.eventType == EVENT_TYPE_DELAYED_GOSSIP && event is DelayedGossipEvent) {
            networkInterface?.multicast(event.message)
            return
        }
        super.dispatchEvent(event)
    }

    private fun computeRaceDelay(trx: Transaction): Long {
        val baseDelay = 0L
        val isAttackerTx = attackerNodeIds.contains(trx.originNodeId)

        return if (isAttackerTx) {
            (baseDelay - deltaB).coerceAtLeast(0L)
        } else {
            baseDelay + deltaA
        }
    }

    override fun handleMessageDropped(message: Message, recipientNetworkEndpoint: P2PNetworkEndpoint) {
        if (networkInterface == null) throw IllegalStateException("Network interface is not set for TransactionPropagationStrategy.")

        val event = MessageDroppedTraceEvent(
            message,
            simulationContext.systemClock.currentTime,
            recipientNetworkEndpoint,
            networkInterface!!
        )
        traceEventLogger.logEvent(event)
    }

    override fun handleInvMessageReceived(
        message: Message,
        senderNetworkEndpoint: P2PNetworkEndpoint
    ) {
        val txId = message.content as String

        context?.trxMemPool?.let { memPool ->
            val trx = memPool.getTransactionById(txId)
            if (trx != null) {
                requestedTransactionIds.remove(txId)
                return
            }

            if (!requestedTransactionIds.add(txId)) {
                return
            }

            networkInterface?.send(
                createGetDataMessage(txId),
                senderNetworkEndpoint
            )
        }
    }

    override fun handleGetDataMessageReceived(message: Message, senderNetworkEndpoint: P2PNetworkEndpoint) {
        val txId = message.content as String

        context?.trxMemPool?.let { trxMemPool ->
            trxMemPool.getTransactionById(txId)?.let { trx ->
                networkInterface?.send(createElementMessage(trx), senderNetworkEndpoint)
                logTrxSent(trx, senderNetworkEndpoint)
            }
        }
    }

    override fun handleElementMessageReceived(message: Message, senderNetworkEndpoint: P2PNetworkEndpoint) {
        val trx = message.content as Transaction
        requestedTransactionIds.remove(trx.txId)
        logTrxReceived(trx, senderNetworkEndpoint)
        onReceivedCallback?.invoke(trx)
    }

    private fun logTrxSent(trx: Transaction, receiverNetworkEndpoint: P2PNetworkEndpoint) {
        val event = TransactionSentTraceEvent(
            simulationContext.systemClock.currentTime,
            trx,
            receiverNetworkEndpoint
        )
        traceEventLogger.logEvent(event)
    }

    private fun logTrxReceived(trx: Transaction, senderNetworkEndpoint: P2PNetworkEndpoint) {
        val event = TransactionReceivedTraceEvent(
            simulationContext.systemClock.currentTime,
            trx,
            senderNetworkEndpoint
        )
        traceEventLogger.logEvent(event)
    }

    private class DelayedGossipEvent(
        private val occurrenceTime: Long,
        private val origin: EventDispatchable,
        val message: Message
    ) : Event {
        override fun getOccurrenceTime(): Long = occurrenceTime
        override fun getEventType(): String = "RACE_DELAYED_TRX_GOSSIP"
        override fun getOrigin(): EventDispatchable = origin
        override fun getValueFormatted(): String = "Delayed transaction INV gossip"
    }
}