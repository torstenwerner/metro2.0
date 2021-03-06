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

package com.sun.xml.ws.rx.policy.assertion.metro.rm200603;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.rx.policy.assertion.AssertionInstantiator;
import com.sun.xml.ws.rx.policy.assertion.AssertionNamespace;
import com.sun.xml.ws.rx.policy.assertion.RmConfigurator;
import com.sun.xml.ws.rx.rm.ReliableMessagingFeatureBuilder;
import com.sun.xml.ws.rx.rm.RmVersion;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

/**
 * <sunc:Ordered />
 */
/**
  * Proprietary assertion that works with WS-RM v1.0 (WSRM200502) and enables
 * "In Order" message delivery:
 * <p />
 * Messages from each individual Sequence are to be delivered in the same order
 * they have been sent by the Application Source. The requirement on an RM Source
 * is that it MUST ensure that the ordinal position of each message in the Sequence
 * (as indicated by a message Sequence number) is consistent with the order in
 * which the messages have been sent from the Application Source. The requirement
 * on the RM Destination is that it MUST deliver received messages for each Sequence
 * in the order indicated by the message numbering. This DeliveryAssurance can be
 * used in combination with any of the AtLeastOnce, AtMostOnce or ExactlyOnce
 * assertions, and the requirements of those assertions MUST also be met. In
 * particular if the AtLeastOnce or ExactlyOnce assertion applies and the RM
 * Destination detects a gap in the Sequence then the RM Destination MUST NOT
 * deliver any subsequent messages from that Sequence until the missing messages
 * are received or until the Sequence is closed.
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class OrderedDeliveryAssertion extends SimpleAssertion implements RmConfigurator {
    public static final QName NAME = AssertionNamespace.METRO_200603.getQName("Ordered");
    
    private static AssertionInstantiator instantiator = new AssertionInstantiator() {
        public PolicyAssertion newInstance(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative){
            return new OrderedDeliveryAssertion(data, assertionParameters);
        }
    };
    
    public static AssertionInstantiator getInstantiator() {
        return instantiator;
    }

    public OrderedDeliveryAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }

    public ReliableMessagingFeatureBuilder update(ReliableMessagingFeatureBuilder builder) {
        if (builder.getVersion() != RmVersion.WSRM200502) {
            throw new WebServiceException(LocalizationMessages.WSRM_1001_ASSERTION_NOT_COMPATIBLE_WITH_RM_VERSION(NAME, builder.getVersion()));
        }

        return builder.enableOrderedDelivery();
    }

    public boolean isCompatibleWith(RmVersion version) {
        return RmVersion.WSRM200502 == version;
    }
}
