package org.palladiosimulator.blockchainsystems.core.propagation.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;

public class BlockReceivedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockReceivedTraceEvent";

    private final long _occurrenceTime;
    private final Block _sentBlock;
    private final P2PNetworkEndpoint _receivingNetworkEndpoint;

    public BlockReceivedTraceEvent(long occurrenceTime, Block sentBlock, P2PNetworkEndpoint receivingNetworkEndpoint) {
        _occurrenceTime = occurrenceTime;
        _sentBlock = sentBlock;
        _receivingNetworkEndpoint = receivingNetworkEndpoint;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getSentBlock() { return _sentBlock; }
    public P2PNetworkEndpoint getReceivingNetworkEndpoint() { return _receivingNetworkEndpoint; }
}
