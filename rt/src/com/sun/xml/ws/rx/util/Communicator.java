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
package com.sun.xml.ws.rx.util;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import com.sun.xml.ws.security.secconv.SecureConversationInitiator;
import com.sun.xml.ws.security.secconv.WSSecureConversationException;
import com.sun.xml.ws.security.secext10.SecurityTokenReferenceType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Transmits standalone protocol messages over the wire. Provides also some additional utility mehtods for creating and
 * unmarshalling JAXWS {@link Message} and {@link Header} objects.
 *
 * <b>
 * WARNING: This class is a private utility class used by WS-RX implementation. Any usage outside
 * the intedned scope is strongly discouraged. The API exposed by this class may be changed, replaced
 * or removed without any advance notice.
 * </b>
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class Communicator {

    // TODO P2 introduce an inner builder class

    private static final Logger LOGGER = Logger.getLogger(Communicator.class);
    public final QName soapMustUnderstandAttributeName;
    //
    private final SecureConversationInitiator scInitiator;
    private FiberExecutor fiberExecutor;
    private final EndpointAddress destinationAddress;
    //
    private final AddressingVersion addressingVersion;
    private final SOAPVersion soapVersion;
    private final JAXBRIContext jaxbContext;

    public Communicator(
            String name,
            EndpointAddress destinationAddress,
            Tube tubeline,
            SecureConversationInitiator scInitiator,
            AddressingVersion addressingVersion,
            SOAPVersion soapVersion,
            JAXBRIContext jaxbContext) {
        this.destinationAddress = destinationAddress;
        this.fiberExecutor = new FiberExecutor(name, tubeline);
        this.scInitiator = scInitiator;
        this.addressingVersion = addressingVersion;
        this.soapVersion = soapVersion;
        this.soapMustUnderstandAttributeName = new QName(soapVersion.nsUri, "mustUnderstand");
        this.jaxbContext = jaxbContext;
    }

    public final Packet createRequestPacket(Object jaxbElement, String wsaAction, boolean expectReply) {
        Message message = Messages.create(jaxbContext, jaxbElement, soapVersion);

        return createRequestPacket(message, wsaAction, expectReply);
    }

    public final Packet createRequestPacket(Message message, String wsaAction, boolean expectReply) {
        Packet packet = new Packet(message);
        packet.endpointAddress = destinationAddress;
        packet.expectReply = expectReply;
        message.getHeaders().fillRequestAddressingHeaders(
                packet,
                addressingVersion,
                soapVersion,
                !expectReply,
                wsaAction);

        return packet;
    }

    public final Packet createRequestPacket(Packet originalRequestPacket, Object jaxbElement, String wsaAction, boolean expectReply) {
        if (originalRequestPacket != null) { // this is actually a request carried in a response packet
            return createResponsePacket(originalRequestPacket, jaxbElement, wsaAction);
        } else {
            Message message = Messages.create(jaxbContext, jaxbElement, soapVersion);
            return createRequestPacket(message, wsaAction, expectReply);
        }
    }

    /**
     * Creates a new empty request packet
     *
     * @return a new empty request packet
     */
    public Packet createEmptyRequestPacket(boolean expectReply) {
        Packet packet = new Packet();
        packet.endpointAddress = destinationAddress;
        packet.expectReply = expectReply;

        return packet;
    }

    /**
     * Creates a new empty request packet with an empty message thatt has WS-A action set
     *
     * @return a new empty request packet
     */
    public Packet createEmptyRequestPacket(String requestWsaAction, boolean expectReply) {
        return createRequestPacket(Messages.createEmpty(soapVersion), requestWsaAction, expectReply);
    }

    /**
     * TODO javadoc
     *
     * @param requestPacket
     * @param responseWsaAction
     * @return
     */
    public Packet createResponsePacket(Packet requestPacket, Object jaxbElement, String responseWsaAction) {
        if (requestPacket != null) { // normal response
            return requestPacket.createServerResponse(
                    Messages.create(jaxbContext, jaxbElement, soapVersion),
                    addressingVersion,
                    soapVersion,
                    responseWsaAction);
        } else { // this is actually a response carried on a request
            return createRequestPacket(jaxbElement, responseWsaAction, false);
        }
    }

    /**
     * TODO javadoc
     *
     * @param requestPacket
     * @param responseWsaAction
     * @return
     */
    public Packet createResponsePacket(Packet requestPacket, Message message, String responseWsaAction) {
        if (requestPacket != null) { // normal response
            return requestPacket.createServerResponse(
                    message,
                    addressingVersion,
                    soapVersion,
                    responseWsaAction);
        } else { // this is actually a response carried on a request
            return createRequestPacket(message, responseWsaAction, false);
        }
    }

    /**
     * TODO javadoc
     *
     * @param requestPacket
     * @param responseWsaAction
     * @return
     */
    public Packet createEmptyResponsePacket(Packet requestPacket, String responseWsaAction) {
        return requestPacket.createServerResponse(
                Messages.createEmpty(soapVersion),
                addressingVersion,
                soapVersion,
                responseWsaAction);
    }

    public Packet createNullResponsePacket(Packet requestPacket) {
        if (requestPacket.transportBackChannel != null) {
            requestPacket.transportBackChannel.close();
        }
        
        Packet emptyReturnPacket = new Packet();
        emptyReturnPacket.invocationProperties.putAll(requestPacket.invocationProperties);
        return emptyReturnPacket;
    }

    /**
     * Creates a new JAX-WS {@link Message} object that doesn't have any payload
     * and sets it as the current packet content as a request message.
     *
     * @param wsaAction WS-Addressing action header to set
     *
     * @return the updated {@link PacketAdapter} instance
     */
    public final Packet setEmptyRequestMessage(Packet request, String wsaAction) {
        Message message = Messages.createEmpty(soapVersion);
        request.setMessage(message);
        message.getHeaders().fillRequestAddressingHeaders(
                request,
                addressingVersion,
                soapVersion,
                false,
                wsaAction);


        return request;
    }

    /**
     * TODO javadoc
     *
     * @param requestAdapter
     * @param wsaAction
     * @return
     */
    public final Packet setEmptyResponseMessage(Packet response, Packet request, String wsaAction) {

        Message message = Messages.createEmpty(soapVersion);
        response.setResponseMessage(request, message, addressingVersion, soapVersion, wsaAction);
        return response;
    }

    public String getWsaAction(Packet packet) {
        if (packet == null || packet.getMessage() == null) {
            return null;
        }
        
        return packet.getMessage().getHeaders().getAction(addressingVersion, soapVersion);
    }

    public String getWsaTo(Packet packet) {
        if (packet == null || packet.getMessage() == null) {
            return null;
        }

        return packet.getMessage().getHeaders().getTo(addressingVersion, soapVersion);
    }

    /**
     * If security is enabled, tries to initate secured conversation and obtain the security token reference.
     * 
     * @return security token reference of the initiated secured conversation, or {@code null} if there is no SC configured
     */
    public SecurityTokenReferenceType tryStartSecureConversation(Packet request) {
        SecurityTokenReferenceType strType = null;
        if (scInitiator != null) {
            try {
                Packet emptyPacket = createEmptyRequestPacket(false);
                emptyPacket.invocationProperties.putAll(request.invocationProperties);

                @SuppressWarnings("unchecked")
                JAXBElement<SecurityTokenReferenceType> strElement = scInitiator.startSecureConversation(emptyPacket);

                strType = (strElement != null) ? strElement.getValue() : null;
            } catch (WSSecureConversationException ex) {
                LOGGER.severe(LocalizationMessages.WSRM_1121_SECURE_CONVERSATION_INIT_FAILED(), ex);
            }
        }
        return strType;
    }

    /**
     * Sends the request {@link Packet} and returns the corresponding response {@link Packet}.
     * This method should be used for Req-Resp MEP
     *
     * @param request {@link Packet} containing the message to be send
     * @return response {@link Message} wrapped in a response {@link Packet} received
     */
    public Packet send(Packet request) {
        request.expectReply = Boolean.TRUE;
        return fiberExecutor.runSync(request);
    }

    public void sendAsync(Packet request, Fiber.CompletionCallback completionCallbackHandler) {
        fiberExecutor.start(request, completionCallbackHandler);
    }

    /**
     * Provides the destination endpoint reference this {@link ProtocolCommunicator} is pointing to. May return {@code null} 
     * in case the {@link ProtocolCommunicator} instance has not yet been initialized by a call to 
     * {@link #registerMusterRequestPacket(Packet)} method.
     * 
     * @return destination endpoint reference or {@code null} in case the {@link ProtocolCommunicator} instance has not 
     *         been initialized yet
     */
    public EndpointAddress getDestinationAddress() {
        return destinationAddress;
    }

    public AddressingVersion getAddressingVersion() {
        return addressingVersion;
    }
    public SOAPVersion getSoapVersion() {
        return soapVersion;
    }

    public void close() {
        final FiberExecutor fe = this.fiberExecutor;
        if (fe != null) {
            this.fiberExecutor.close();
            this.fiberExecutor = null;
        }
    }
}
