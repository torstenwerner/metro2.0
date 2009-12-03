/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.xml.ws.rx.rm.runtime;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.rm.RmVersion;
import com.sun.xml.ws.rx.rm.protocol.AcknowledgementData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CloseSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.CreateSequenceResponseData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceData;
import com.sun.xml.ws.rx.rm.protocol.TerminateSequenceResponseData;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.AckRequestedElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CloseSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.CreateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceAcknowledgementElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.TerminateSequenceResponseElement;
import com.sun.xml.ws.rx.rm.protocol.wsrm200702.UsesSequenceSTR;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;
import com.sun.xml.ws.rx.util.Communicator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
final class Wsrm200702ProtocolHandler extends WsrmProtocolHandler {

    private static final Logger LOGGER = Logger.getLogger(Wsrm200702ProtocolHandler.class);
    private final RuntimeContext rc;

    Wsrm200702ProtocolHandler(RmConfiguration configuration, RuntimeContext rc, Communicator communicator) {
        super(RmVersion.WSRM200702, configuration, communicator);

        assert rc != null;

        this.rc = rc;
    }

    public CreateSequenceData toCreateSequenceData(@NotNull Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CreateSequenceElement csElement = unmarshallMessage(message);

        // TODO process UsesSequenceSTR

        return csElement.toDataBuilder().build();
    }

    public Packet toPacket(CreateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createRequestPacket(requestPacket, new CreateSequenceElement(data), rmVersion.createSequenceAction, true);

        if (data.getStrType() != null) {
            UsesSequenceSTR usesSequenceSTR = new UsesSequenceSTR();
            usesSequenceSTR.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            packet.getMessage().getHeaders().add(Headers.create(getJaxbContext(), usesSequenceSTR));
        }

        return packet;
    }

    public CreateSequenceResponseData toCreateSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();

        CreateSequenceResponseElement csrElement = unmarshallMessage(message);

        return csrElement.toDataBuilder().build();
    }

    public Packet toPacket(CreateSequenceResponseData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        return communicator.createResponsePacket(requestPacket, new CreateSequenceResponseElement(data), rmVersion.createSequenceResponseAction);
    }

    public CloseSequenceData toCloseSequenceData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CloseSequenceElement csElement = unmarshallMessage(message);
        final CloseSequenceData.Builder dataBuilder = csElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    public Packet toPacket(CloseSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createRequestPacket(requestPacket, new CloseSequenceElement(data), rmVersion.closeSequenceAction, true);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    public CloseSequenceResponseData toCloseSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        CloseSequenceResponseElement csrElement = unmarshallMessage(message); // consuming message here
        final CloseSequenceResponseData.Builder dataBuilder = csrElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    public Packet toPacket(CloseSequenceResponseData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createResponsePacket(requestPacket, new CloseSequenceResponseElement(data), rmVersion.closeSequenceResponseAction);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    public TerminateSequenceData toTerminateSequenceData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();
        TerminateSequenceElement tsElement = unmarshallMessage(message);
        final TerminateSequenceData.Builder dataBuilder = tsElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    public Packet toPacket(TerminateSequenceData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createRequestPacket(requestPacket, new TerminateSequenceElement(data), rmVersion.terminateSequenceAction, true);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    public TerminateSequenceResponseData toTerminateSequenceResponseData(Packet packet) throws RxRuntimeException {
        assert packet != null;
        assert packet.getMessage() != null;
        assert !packet.getMessage().isFault();

        Message message = packet.getMessage();


        TerminateSequenceResponseElement tsrElement = unmarshallMessage(message);
        final TerminateSequenceResponseData.Builder dataBuilder = tsrElement.toDataBuilder();

        dataBuilder.acknowledgementData(getAcknowledgementData(message));

        return dataBuilder.build();
    }

    public Packet toPacket(TerminateSequenceResponseData data, @Nullable Packet requestPacket) throws RxRuntimeException {
        Packet packet = communicator.createResponsePacket(requestPacket, new TerminateSequenceResponseElement(data), rmVersion.terminateSequenceResponseAction);

        if (data.getAcknowledgementData() != null) {
            appendAcknowledgementHeaders(packet, data.getAcknowledgementData());
        }

        return packet;
    }

    public void appendSequenceHeader(@NotNull Message jaxwsMessage, @NotNull ApplicationMessage message) throws RxRuntimeException {
        assert message != null;
        assert message.getSequenceId() != null;
        assert jaxwsMessage != null;

        SequenceElement sequenceHeaderElement = new SequenceElement();
        sequenceHeaderElement.setId(message.getSequenceId());
        sequenceHeaderElement.setMessageNumber(message.getMessageNumber());

        sequenceHeaderElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
        jaxwsMessage.getHeaders().add(createHeader(sequenceHeaderElement));
    }

    public void appendAcknowledgementHeaders(@NotNull Packet packet, @NotNull AcknowledgementData ackData) {
        assert packet != null;
        assert packet.getMessage() != null;
        assert ackData != null;


        Message jaxwsMessage = packet.getMessage();
        // ack requested header
        if (ackData.getAckReqestedSequenceId() != null) {
            AckRequestedElement ackRequestedElement = new AckRequestedElement();
            ackRequestedElement.setId(ackData.getAckReqestedSequenceId());

            ackRequestedElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            jaxwsMessage.getHeaders().add(createHeader(ackRequestedElement));

            packet.invocationProperties.put(RmConfiguration.ACK_REQUESTED_HEADER_SET, Boolean.TRUE);
        }

        // sequence acknowledgement header
        if (ackData.containsSequenceAcknowledgementData()) {
            SequenceAcknowledgementElement ackElement = new SequenceAcknowledgementElement();
            ackElement.setId(ackData.getAcknowledgedSequenceId());

            for (Sequence.AckRange range : ackData.getAcknowledgedRanges()) {
                ackElement.addAckRange(range.lower, range.upper);
            }

            if (ackData.isFinalAcknowledgement()) {
                ackElement.setFinal(new SequenceAcknowledgementElement.Final());
            }

// TODO decide whether we will advertise remaining buffer
//        if (configuration.getDestinationBufferQuota() != Configuration.UNSPECIFIED) {
//            ackElement.setBufferRemaining(-1/*calculate remaining quota*/);
//        }

            ackElement.getOtherAttributes().put(communicator.soapMustUnderstandAttributeName, "true");
            jaxwsMessage.getHeaders().add(createHeader(ackElement));
        }
    }

    public void loadSequenceHeaderData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException {
        assert message != null;
        assert message.getSequenceId() == null; // not initialized yet

        SequenceElement sequenceElement = readHeaderAsUnderstood(rmVersion.namespaceUri, "Sequence", jaxwsMessage);
        if (sequenceElement != null) {
            message.setSequenceData(sequenceElement.getId(), sequenceElement.getMessageNumber());
        }
    }

    public void loadAcknowledgementData(@NotNull ApplicationMessage message, @NotNull Message jaxwsMessage) throws RxRuntimeException {
        assert message != null;
        assert message.getAcknowledgementData() == null; // not initialized yet

        message.setAcknowledgementData(getAcknowledgementData(jaxwsMessage));
    }

    public AcknowledgementData getAcknowledgementData(Message jaxwsMessage) throws UnknownSequenceException, RxRuntimeException {
        assert jaxwsMessage != null;

        AcknowledgementData.Builder ackDataBuilder = AcknowledgementData.getBuilder();
        AckRequestedElement ackRequestedElement = readHeaderAsUnderstood(rmVersion.namespaceUri, "AckRequested", jaxwsMessage);
        if (ackRequestedElement != null) {
            ackDataBuilder.ackReqestedSequenceId(ackRequestedElement.getId());
        }
        SequenceAcknowledgementElement ackElement = readHeaderAsUnderstood(rmVersion.namespaceUri, "SequenceAcknowledgement", jaxwsMessage);
        if (ackElement != null) {
            List<Sequence.AckRange> ranges = new LinkedList<Sequence.AckRange>();
            if (ackElement.getNone() == null) {
                if (!ackElement.getNack().isEmpty()) {
                    List<BigInteger> nacks = new ArrayList<BigInteger>(ackElement.getNack());
                    Collections.sort(nacks);
                    long lastLowerBound = 1;
                    for (BigInteger nackId : nacks) {
                        if (lastLowerBound == nackId.longValue()) {
                            lastLowerBound++;
                        } else {
                            ranges.add(new Sequence.AckRange(lastLowerBound, nackId.longValue() - 1));
                            lastLowerBound = nackId.longValue() + 1;
                        }
                    }
                    long lastMessageId = rc.getSequence(ackElement.getId()).getLastMessageNumber();
                    if (lastLowerBound <= lastMessageId) {
                        ranges.add(new Sequence.AckRange(lastLowerBound, lastMessageId));
                    }
                } else if (ackElement.getAcknowledgementRange() != null && !ackElement.getAcknowledgementRange().isEmpty()) {
                    for (SequenceAcknowledgementElement.AcknowledgementRange rangeElement : ackElement.getAcknowledgementRange()) {
                        ranges.add(new Sequence.AckRange(rangeElement.getLower().longValue(), rangeElement.getUpper().longValue()));
                    }
                }
            }
            ackDataBuilder.acknowledgements(ackElement.getId(), ranges, ackElement.getFinal() != null);
            // TODO handle remaining buffer in the header
            // ackElement.getBufferRemaining();
        }
        return ackDataBuilder.build();
    }

    @Override
    public Header createSequenceFaultElementHeader(QName subcode, Object detail) {
        return Headers.create(rmVersion.getJaxbContext(addressingVersion),
                new com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceFaultElement(subcode));
// TODO include detail: new com.sun.xml.ws.rx.rm.protocol.wsrm200702.SequenceFaultElement(subcode, detail));
    }
    
    @Override
    public Packet createEmptyAcknowledgementResponse(AcknowledgementData ackData, Packet requestPacket) throws RxRuntimeException {
        if (ackData.getAckReqestedSequenceId() != null || ackData.containsSequenceAcknowledgementData()) {
            // create acknowledgement response only if there is something to send in the SequenceAcknowledgement header
            Packet response = rc.communicator.createEmptyResponsePacket(requestPacket, rc.rmVersion.sequenceAcknowledgementAction);
            response = rc.communicator.setEmptyResponseMessage(response, requestPacket, rc.rmVersion.sequenceAcknowledgementAction);
            appendAcknowledgementHeaders(response, ackData);
            return response;
        } else {
            return rc.communicator.createNullResponsePacket(requestPacket);
        }
    }
}
