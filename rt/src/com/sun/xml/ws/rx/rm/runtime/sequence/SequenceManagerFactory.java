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
package com.sun.xml.ws.rx.rm.runtime.sequence;

import com.sun.xml.ws.rx.rm.runtime.RmConfiguration;
import com.sun.xml.ws.rx.rm.runtime.delivery.DeliveryQueueBuilder;
import com.sun.xml.ws.rx.rm.runtime.sequence.invm.InVmSequenceManager;
import com.sun.xml.ws.rx.rm.runtime.sequence.persistent.PersistentSequenceManager;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public enum SequenceManagerFactory {
    INSTANCE;
        
    private SequenceManagerFactory() {
        // TODO: load from external configuration and revert to default if not present
    }

    /**
     * Creates new {@link SequenceManager} instance. This operation should be called only once per endpoint and/or endpoint client.
     *
     * @param persistent specifies whether returned {@link SequenceManager} instance should support persistent message storage
     * @param uniqueEndpointId unique identifier of the WS endpoint for which this particular sequence manager will be used. The endpoint
     * identifier must be different for the client and for the server side.
     * @param inboundQueueBuilder delivery queue builder that will be used to create delivery queue for all newly created inbound sequences
     * @param outboundQueueBuilder delivery queue builder that will be used to create delivery queue for all newly created outbound sequences
     * @param managedObjectManager object manager managing the newly created {@link SequenceManager} instance
     * @return newly created {@link SequenceManager} instance
     */
    public SequenceManager createSequenceManager(boolean persistent, String uniqueEndpointId, DeliveryQueueBuilder inboundQueueBuilder, DeliveryQueueBuilder outboundQueueBuilder, RmConfiguration configuration) {
        if (persistent) {
            return new PersistentSequenceManager(uniqueEndpointId, inboundQueueBuilder, outboundQueueBuilder, configuration);
        }
        
        return new InVmSequenceManager(uniqueEndpointId, inboundQueueBuilder, outboundQueueBuilder, configuration);
    }
}
