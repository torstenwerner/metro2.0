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
package com.sun.xml.ws.rx.mc.runtime;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxRuntimeException;
import com.sun.xml.ws.rx.mc.localization.LocalizationMessages;
import com.sun.xml.ws.rx.mc.protocol.wsmc200702.MakeConnectionElement;
import com.sun.xml.ws.rx.mc.protocol.wsmc200702.MessagePendingElement;
import com.sun.xml.ws.rx.util.FiberExecutor;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public class McServerTube extends AbstractFilterTubeImpl {

    private static final class ResponseStorage {

        final Map<String, Queue<Packet>> storage = new HashMap<String, Queue<Packet>>();
        final ReentrantReadWriteLock storageLock = new ReentrantReadWriteLock();

        void store(@NotNull Packet response, @NotNull String clientUID) {
            if (!getClientQueue(clientUID).offer(response)) {
                LOGGER.severe(LocalizationMessages.WSMC_0104_ERROR_STORING_RESPONSE(clientUID));
            }
        }

        private Packet getPendingResponsePacket(@NotNull String clientUID) {
            try {
                storageLock.readLock().lock();

                final Queue<Packet> clientQueue = storage.get(clientUID);
                return (clientQueue == null) ? null : clientQueue.poll();
            } finally {
                storageLock.readLock().unlock();
            }
        }

        private boolean hasPendingResponse(@NotNull String clientUID) {
            try {
                storageLock.readLock().lock();

                final Queue<Packet> clientQueue = storage.get(clientUID);
                return clientQueue != null && !clientQueue.isEmpty();
            } finally {
                storageLock.readLock().unlock();
            }
        }

        private Queue<Packet> getClientQueue(@NotNull String clientUID) {
            try {
                storageLock.readLock().lock();
                Queue<Packet> clientQueue = storage.get(clientUID);
                if (clientQueue == null) {
                    storageLock.readLock().unlock();

                    try {
                        storageLock.writeLock().lock();
                        clientQueue = storage.get(clientUID);
                        if (clientQueue == null) { // recheck
                            clientQueue = new ConcurrentLinkedQueue<Packet>();
                            storage.put(clientUID, clientQueue);
                        }
                        storageLock.readLock().lock();
                    } finally {
                        storageLock.writeLock().unlock();
                    }
                }

                return clientQueue;
            } finally {
                storageLock.readLock().unlock();
            }
        }
    }

    private static final class AppRequestProcessingCallback implements Fiber.CompletionCallback {

        private static final Logger LOGGER = Logger.getLogger(AppRequestProcessingCallback.class);
        private final ResponseStorage responseStorage;
        private final String clientUID;
        private final McConfiguration configuration;

        public AppRequestProcessingCallback(@NotNull ResponseStorage responseStorage, @NotNull String clientUID, @NotNull McConfiguration configuration) {
            this.responseStorage = responseStorage;
            this.clientUID = clientUID;
            this.configuration = configuration;
        }

        public void onCompletion(Packet response) {
            LOGGER.finer(LocalizationMessages.WSMC_0105_STORING_RESPONSE(clientUID));

            if (response.getMessage() != null) {
                final HeaderList headers = response.getMessage().getHeaders();
                headers.remove(configuration.getAddressingVersion().toTag);
                headers.add(Headers.create(
                        configuration.getAddressingVersion().toTag, 
                        configuration.getMcVersion().getWsmcAnonymousAddress(clientUID)));
            }

            responseStorage.store(response, clientUID);
        }

        public void onCompletion(Throwable error) {
            LOGGER.severe(LocalizationMessages.WSMC_0106_EXCEPTION_IN_REQUEST_PROCESSING(clientUID), error);
        }
    }
    //
    private static final Logger LOGGER = Logger.getLogger(McServerTube.class);
    //
    private final McConfiguration configuration;
    private final FiberExecutor fiberExecutor;
    private final ResponseStorage responseStorage;

    McServerTube(McConfiguration configuration, Tube tubelineHead) {
        super(tubelineHead);

        this.configuration = configuration;
        this.fiberExecutor = new FiberExecutor("McServerTubeCommunicator", tubelineHead);
        this.responseStorage = new ResponseStorage();
    }

    McServerTube(McServerTube original, TubeCloner cloner) {
        super(original, cloner);

        this.configuration = original.configuration;
        this.fiberExecutor = original.fiberExecutor;
        this.responseStorage = original.responseStorage;
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        LOGGER.entering();
        try {
            return new McServerTube(this, cloner);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processRequest(Packet request) {
        try {
            LOGGER.entering();

            assert request.getMessage() != null : "Unexpected [null] message in the server-side Tube.processRequest()";

            String clientUID = getClientUID(request);
            if (isMakeConnectionRequest(request)) {
                return handleMakeConnectionRequest(request, clientUID);
            }

            if (clientUID == null) {
                // don't bother - this is not a WS-MC enabled request
                return super.processRequest(request);
            } else {
                // TODO replace with proper code that replaces only address

                // removing replyTo header and faultTo header to prevent addressing server tube from
                // treating this request as non-anonymous
                request.getMessage().getHeaders().remove(configuration.getAddressingVersion().replyToTag);
                request.getMessage().getHeaders().remove(configuration.getAddressingVersion().faultToTag);
            }

            Packet requestCopy = request.copy(true);
            fiberExecutor.start(request, new AppRequestProcessingCallback(responseStorage, clientUID, configuration));
            return super.doReturnWith(createEmptyResponse(requestCopy));
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processResponse(Packet response) {
        try {
            LOGGER.entering();

            // with WS-MC enabled messages, this method gets never invoked
            return super.processResponse(response);
        } finally {
            LOGGER.exiting();
        }
    }

    private NextAction handleMakeConnectionRequest(Packet request, String clientUID) {
        try {
            LOGGER.entering();

            String selectionUID;
            try {
                MakeConnectionElement mcElement = request.getMessage().readPayloadAsJAXB(configuration.getMcVersion().getUnmarshaller(configuration.getAddressingVersion()));
                selectionUID = configuration.getMcVersion().getClientId(mcElement.getAddress().getValue());
            } catch (JAXBException ex) {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0107_ERROR_UNMARSHALLING_PROTOCOL_MESSAGE(), ex));
            }

            if (selectionUID == null) {
                // TODO return a MissingSelection SOAP fault
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0108_NULL_SELECTION_ADDRESS()));
            }

            if (!selectionUID.equals(clientUID)) {
                // TODO return a SOAP fault?
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0109_SELECTION_ADDRESS_NOT_MATCHING_WSA_REPLYTO()));
            }

            Packet response = null;
            if (selectionUID != null && responseStorage.hasPendingResponse(selectionUID)) {
                LOGGER.finer(LocalizationMessages.WSMC_0110_PENDING_MESSAGE_FOUND_FOR_SELECTION_UUID(selectionUID));
                response = responseStorage.getPendingResponsePacket(selectionUID);
            }

            if (response == null) {
                LOGGER.finer(LocalizationMessages.WSMC_0111_NO_PENDING_MESSAGE_FOUND_FOR_SELECTION_UUID(selectionUID));
                response = createEmptyResponse(request);
            } else {
                Message message = response.getMessage();
                if (message != null) {
                    HeaderList headers = message.getHeaders();
                    headers.add(Headers.create(
                            configuration.getMcVersion().getJaxbContext(configuration.getAddressingVersion()),
                            new MessagePendingElement(Boolean.valueOf(selectionUID != null && responseStorage.hasPendingResponse(selectionUID)))));
                }
            }

            return super.doReturnWith(response);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public NextAction processException(Throwable t) {
        try {
            LOGGER.entering();

            return super.processException(t);
        } finally {
            LOGGER.exiting();
        }
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
    }

    private String getClientUID(Packet request) {
        Header replyToHeader = request.getMessage().getHeaders().get(configuration.getAddressingVersion().replyToTag, false);
        if (replyToHeader != null) {
            try {
                String replyToAddress = replyToHeader.readAsEPR(configuration.getAddressingVersion()).getAddress();
                return configuration.getMcVersion().getClientId(replyToAddress);
            } catch (XMLStreamException ex) {
                throw LOGGER.logSevereException(new RxRuntimeException(LocalizationMessages.WSMC_0103_ERROR_RETRIEVING_WSA_REPLYTO_CONTENT(), ex));
            }
        }

        return null;
    }

    private boolean isMakeConnectionRequest(final Packet request) {
        return configuration.getMcVersion().wsmcAction.equals(request.getMessage().getHeaders().getAction(configuration.getAddressingVersion(), configuration.getSoapVersion()));
    }

    private Packet createEmptyResponse(Packet request) {
        return request.createServerResponse(null, null, null, "");
    }
}
