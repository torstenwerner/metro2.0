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
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.rx.rm.RmVersion;
import com.sun.xml.ws.rx.rm.runtime.sequence.Sequence;
import com.sun.xml.ws.rx.rm.runtime.sequence.SequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.UnknownSequenceException;
import com.sun.xml.ws.rx.util.Communicator;
import com.sun.xml.ws.commons.ScheduledTaskManager;
import com.sun.xml.ws.rx.util.SuspendedFiberStorage;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public final class RuntimeContext {

    public static Builder getBuilder(@NotNull RmConfiguration configuration, @NotNull Communicator communicator) {
        return new Builder(configuration, communicator);
    }

    public static final class Builder {

        private final @NotNull RmConfiguration configuration;
        private final @NotNull Communicator communicator;

        private @Nullable SequenceManager sequenceManager;
        private @Nullable SourceMessageHandler sourceMessageHandler;
        private @Nullable DestinationMessageHandler destinationMessageHandler;

        public Builder(@NotNull RmConfiguration configuration, @NotNull Communicator communicator) {
            assert configuration != null;
            assert communicator != null;

            this.configuration = configuration;
            this.communicator = communicator;

            this.sourceMessageHandler = new SourceMessageHandler(null);
            this.destinationMessageHandler = new DestinationMessageHandler(null);
        }

        public Builder sequenceManager(SequenceManager sequenceManager) {
            this.sequenceManager = sequenceManager;

            this.sourceMessageHandler.setSequenceManager(sequenceManager);
            this.destinationMessageHandler.setSequenceManager(sequenceManager);

            return this;
        }

        public RuntimeContext build() {
            return new RuntimeContext(
                    configuration,
                    sequenceManager,
                    communicator,
                    new SuspendedFiberStorage(),
                    new ScheduledTaskManager("RM Runtime Context"),
                    sourceMessageHandler,
                    destinationMessageHandler);
        }
    }
    public final RmConfiguration configuration;
    public final AddressingVersion addressingVersion;
    public final SOAPVersion soapVersion;
    public final RmVersion rmVersion;
    private volatile SequenceManager sequenceManager;
    public final Communicator communicator;
    public final SuspendedFiberStorage suspendedFiberStorage;
    public final WsrmProtocolHandler protocolHandler;
    public final ScheduledTaskManager scheduledTaskManager;
    final SourceMessageHandler sourceMessageHandler;
    final DestinationMessageHandler destinationMessageHandler;

    private RuntimeContext(
            RmConfiguration configuration,
            SequenceManager sequenceManager,
            Communicator communicator,
            SuspendedFiberStorage suspendedFiberStorage,
            ScheduledTaskManager scheduledTaskManager,
            SourceMessageHandler srcMsgHandler,
            DestinationMessageHandler dstMsgHandler) {

        this.configuration = configuration;
        this.sequenceManager = sequenceManager;
        this.communicator = communicator;
        this.suspendedFiberStorage = suspendedFiberStorage;
        this.scheduledTaskManager = scheduledTaskManager;
        this.sourceMessageHandler = srcMsgHandler;
        this.destinationMessageHandler = dstMsgHandler;

        this.addressingVersion = configuration.getAddressingVersion();
        this.soapVersion = configuration.getSoapVersion();
        this.rmVersion = configuration.getRmFeature().getVersion();

        this.protocolHandler = WsrmProtocolHandler.getInstance(configuration, communicator, this);
    }

    public void close() {
        scheduledTaskManager.shutdown();
        communicator.close();
    }

    public Sequence getSequence(String sequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        return sequenceManager.getSequence(sequenceId);
    }

    public Sequence getOutboundSequence(String sequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        return sequenceManager.getSequence(sequenceId);
    }

    public Sequence getBoundSequence(String sequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        return sequenceManager.getBoundSequence(sequenceId);
    }

    public String getBoundSequenceId(String sequenceId) throws UnknownSequenceException {
        assert sequenceManager != null;

        Sequence boundSequence = sequenceManager.getBoundSequence(sequenceId);
        return boundSequence != null ? boundSequence.getId() : null;
    }

    public SequenceManager sequenceManager() {
        assert sequenceManager != null;

        return this.sequenceManager;
    }

    public void setSequenceManager(@NotNull SequenceManager newValue) {
        assert newValue != null;

        this.sequenceManager = newValue;

        this.sourceMessageHandler.setSequenceManager(newValue);
        this.destinationMessageHandler.setSequenceManager(newValue);
    }
}
