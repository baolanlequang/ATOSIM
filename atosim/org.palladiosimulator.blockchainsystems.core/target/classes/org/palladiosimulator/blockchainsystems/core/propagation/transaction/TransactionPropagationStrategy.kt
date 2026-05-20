package org.palladiosimulator.blockchainsystems.core.propagation.transaction

import org.palladiosimulator.blockchainsystems.core.network.MessageDroppedTraceEvent
import org.palladiosimulator.blockchainsystems.core.propagation.GossipPropagationStrategy
import org.palladiosimulator.blockchainsystems.core.propagation.MessageImpl
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction

class TransactionPropagationStrategy : GossipPropagationStrategy<Transaction>() {
  override val INV_MESSAGE_KEY: String = "TRX_INV"
  override val GET_DATA_MESSAGE_KEY: String = "TRX_GET_DATA"
  override val ELEMENT_MESSAGE_KEY: String = "TRX_MSG"

  private val MESSAGE_HEADER_BYTE_SIZE = 24
  private val INV_MESSAGE_BYTE_SIZE = 20
  private val GET_DATA_MESSAGE_BYTE_SIZE = 20

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

  override fun handleMessageDropped(
    message: Message,
    recipientNetworkEndpoint: P2PNetworkEndpoint
  ) {
    if (networkInterface == null) {
      throw IllegalStateException("Network interface is not set for BlockPropagationStrategy.")
    }

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

  override fun handleGetDataMessageReceived(
    message: Message,
    senderNetworkEndpoint: P2PNetworkEndpoint
  ) {
    val txId = message.content as String

    context?.trxMemPool?.let { trxMemPool ->
      trxMemPool.getTransactionById(txId)?.let { trx ->
        networkInterface?.send(createElementMessage(trx), senderNetworkEndpoint)
        logTrxSent(trx, senderNetworkEndpoint)
      }
    }
  }

  override fun handleElementMessageReceived(
    message: Message,
    senderNetworkEndpoint: P2PNetworkEndpoint
  ) {
    val trx = message.content as Transaction

    requestedTransactionIds.remove(trx.txId)

    logTrxReceived(trx, senderNetworkEndpoint)
    notifyTrxReceived(trx)
  }

  private fun notifyTrxReceived(transaction: Transaction) {
    onReceivedCallback?.invoke(transaction)
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
}