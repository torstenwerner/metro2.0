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
package com.sun.xml.ws.rx.rm;

import static com.sun.xml.ws.rx.rm.ReliableMessagingFeature.*;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public final class ReliableMessagingFeatureBuilder {
    // General RM config values
    private final RmVersion version;
    
    private boolean enabled = true;
    private long inactivityTimeout = DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT;
    private long destinationBufferQuota = DEFAULT_DESTINATION_BUFFER_QUOTA;
    private boolean orderedDelivery = false;
    private DeliveryAssurance deliveryAssurance = DeliveryAssurance.getDefault();
    private SecurityBinding securityBinding = SecurityBinding.getDefault();
    //
    private long messageRetransmissionInterval = DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL;
    private BackoffAlgorithm retransmissionBackoffAlgorithm = BackoffAlgorithm.getDefault();
    private long maxMessageRetransmissionCount = DEFAULT_MAX_MESSAGE_RETRANSMISSION_COUNT;
    //
    private long ackTransmittionInterval = DEFAULT_ACKNOWLEDGEMENT_TRANSMISSION_INTERVAL;
    private long ackRequestTransmissionInterval = DEFAULT_ACK_REQUEST_TRANSMISSION_INTERVAL;
    private long closeSequenceOperationTimeout = DEFAULT_CLOSE_SEQUENCE_OPERATION_TIMEOUT;
    private boolean persistenceEnabled = false;
    private long sequenceMaintenancePeriod = DEFAULT_SEQUENCE_MANAGER_MAINTENANCE_PERIOD;
    private long maxConcurrentSessions = DEFAULT_MAX_CONCURRENT_SESSIONS;

    public ReliableMessagingFeatureBuilder(RmVersion version) {
        this.version = version;
    }

    public ReliableMessagingFeature build() {
        return new ReliableMessagingFeature(
                this.enabled,
                this.version,
                this.inactivityTimeout,
                this.destinationBufferQuota,
                this.orderedDelivery,
                this.deliveryAssurance,
                this.securityBinding,
                this.messageRetransmissionInterval,
                this.retransmissionBackoffAlgorithm,
                this.maxMessageRetransmissionCount,
                this.ackTransmittionInterval,
                this.ackRequestTransmissionInterval,
                this.closeSequenceOperationTimeout,
                this.persistenceEnabled,
                this.sequenceMaintenancePeriod,
                this.maxConcurrentSessions);
    }

    /**
     * @see ReliableMessagingFeature#getAcknowledgementTransmissionInterval()
     */
    public ReliableMessagingFeatureBuilder acknowledgementTransmittionInterval(long value) {
        this.ackTransmittionInterval = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getAckRequestTransmissionInterval()
     */
    public ReliableMessagingFeatureBuilder ackRequestTransmissionInterval(long value) {
        this.ackRequestTransmissionInterval = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getMessageRetransmissionInterval() 
     */
    public ReliableMessagingFeatureBuilder messageRetransmissionInterval(long value) {
        this.messageRetransmissionInterval = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getRetransmissionBackoffAlgorithm()
     */
    public ReliableMessagingFeatureBuilder retransmissionBackoffAlgorithm(BackoffAlgorithm value) {
        this.retransmissionBackoffAlgorithm = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getMaxMessageRetransmissionCount() 
     */
    public ReliableMessagingFeatureBuilder maxMessageRetransmissionCount(long value) {
        this.maxMessageRetransmissionCount = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getDestinationBufferQuota()
     */
    public ReliableMessagingFeatureBuilder destinationBufferQuota(long value) {
        this.destinationBufferQuota = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getCloseSequenceOperationTimeout()
     */
    public ReliableMessagingFeatureBuilder closeSequenceOperationTimeout(long value) {
        this.closeSequenceOperationTimeout = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getDeliveryAssurance()
     */
    public ReliableMessagingFeatureBuilder deliveryAssurance(DeliveryAssurance value) {
        this.deliveryAssurance = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getSequenceInactivityTimeout()
     */
    public ReliableMessagingFeatureBuilder sequenceInactivityTimeout(long value) {
        this.inactivityTimeout = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#isOrderedDeliveryEnabled()
     */
    public ReliableMessagingFeatureBuilder enableOrderedDelivery() {
        this.orderedDelivery = true;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getVersion()
     */
    public RmVersion getVersion() {
        return this.version;
    }

    /**
     * @see ReliableMessagingFeature#getSecurityBinding()
     */
    public ReliableMessagingFeatureBuilder securityBinding(SecurityBinding value) {
        this.securityBinding = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#isPersistenceEnabled() 
     */
    public ReliableMessagingFeatureBuilder enablePersistence() {
        this.persistenceEnabled = true;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#isPersistenceEnabled()
     */
    public ReliableMessagingFeatureBuilder disablePersistence() {
        this.persistenceEnabled = false;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getSequenceManagerMaintenancePeriod() 
     */
    public ReliableMessagingFeatureBuilder sequenceMaintenancePeriod(long value) {
        this.sequenceMaintenancePeriod = value;
        return this;
    }

    /**
     * @see ReliableMessagingFeature#getMaxConcurrentSessions()
     */
    public ReliableMessagingFeatureBuilder maxConcurrentSessions(long value) {
        this.maxConcurrentSessions = value;

        return this;
    }
}
