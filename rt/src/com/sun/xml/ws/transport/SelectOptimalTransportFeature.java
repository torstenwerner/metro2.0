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

package com.sun.xml.ws.transport;

import com.sun.xml.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 * Optimized transport {@link javax.xml.ws.WebServiceFeature}
 *
 * @author Alexey Stashok
 */
@ManagedData
public class SelectOptimalTransportFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.transport.SelectOptimalTransportFeature";

    /**
     * This enumeration defines an optimized transport list
     *
     * @see DeliveryAssurance#TCP
     */
    public static enum Transport {

        /**
         * SOAP/TCP transport
         */
        TCP;

        /**
         * Provides a default optimized transport value.
         *
         * @return a default optimized transport value. Currently returns {@link #TCP}.
         */
        public static Transport getDefault() {
            return Transport.TCP;
        }
    }

    // Optimized transport to be used
    private Transport transport;

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    public SelectOptimalTransportFeature() {
        this(true);
    }

    /**
     * This constructor is here to satisfy JAX-WS specification requirements
     */
    @FeatureConstructor({
        "enabled"
    })
    public SelectOptimalTransportFeature(boolean enabled) {
        this(enabled, Transport.getDefault());
    }

    @FeatureConstructor({
        "enabled",
        "transport"
    })
    public SelectOptimalTransportFeature(boolean enabled, Transport transport) {
        super.enabled = enabled;
        this.transport = transport;
    }


    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public Transport getTransport() {
        return transport;
    }
}
