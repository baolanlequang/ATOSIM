package org.palladiosimulator.blockchainsystems.core.system.abstractions;

import java.util.Set;
import java.util.function.BiConsumer;

public interface NodeP2PNetworkInterface extends P2PNetworkEndpoint {
    void multicast(Message message);
    void send(Message message, P2PNetworkEndpoint recipient);
    Set<P2PNetworkEndpoint> getNeighbors();
    void setOnMessageReceivedCallback(BiConsumer<Message, P2PNetworkEndpoint> onMessageReceivedCallback);
    void setOnMessageDroppedCallback(BiConsumer<Message, P2PNetworkEndpoint> onMessageDroppedCallback);
}
