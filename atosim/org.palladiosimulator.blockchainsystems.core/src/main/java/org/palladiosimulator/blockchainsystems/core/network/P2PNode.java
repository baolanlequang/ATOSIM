package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.P2PNetworkObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.NodeP2PNetworkInterface;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;

import java.util.Set;
import java.util.function.BiConsumer;

public class P2PNode extends P2PNetworkObject implements NodeP2PNetworkInterface, P2PNetworkEndpoint {

    private final String _endpointId;
    private P2PNetwork _network;
    private BiConsumer<Message, P2PNetworkEndpoint> _onMessageReceivedCallback;
    private BiConsumer<Message, P2PNetworkEndpoint> _onMessageDroppedCallback;

    public P2PNode(String endpointId) {
        _endpointId = endpointId;
    }

    @Override
    public String getEndpointId() {
        return _endpointId;
    }

    public void initNetwork(P2PNetwork network) {
        _network = network;
    }

    public void onReceive(Message messageContent, P2PNetworkEndpoint sender) {
        if (_onMessageReceivedCallback != null) {
            _onMessageReceivedCallback.accept(messageContent, sender);
        }
    }

    public void onMessageDropped(Message messageContent, P2PNetworkEndpoint recipient) {
        if (_onMessageDroppedCallback != null) {
            _onMessageDroppedCallback.accept(messageContent, recipient);
        }
    }

    @Override
    public void dispatchEvent(Event event) {
    }

    @Override
    public void multicast(Message message) {
        _network.multicast(this, message);
    }

    @Override
    public void setOnMessageReceivedCallback(BiConsumer<Message, P2PNetworkEndpoint> onMessageReceivedCallback) {
        _onMessageReceivedCallback = onMessageReceivedCallback;
    }

    @Override
    public void setOnMessageDroppedCallback(BiConsumer<Message, P2PNetworkEndpoint> onMessageDroppedCallback) {
        _onMessageDroppedCallback = onMessageDroppedCallback;
    }

    @Override
    public void send(Message message, P2PNetworkEndpoint recipient) {
        _network.send(this, (NodeP2PNetworkInterface) recipient, message);
    }

    @Override
    public Set<P2PNetworkEndpoint> getNeighbors() {
        return _network.getNeighbors(this);
    }
}
