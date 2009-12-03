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
package com.sun.xml.ws.rx.policy.spi_impl;

import com.sun.xml.ws.rx.policy.assertion.wsrm200502.Rm10Assertion;
import com.sun.xml.ws.rx.policy.assertion.wsrm200702.Rm11Assertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.ws.policy.spi.PolicyAssertionValidator.Fitness;

import com.sun.xml.ws.rx.policy.assertion.net.rm200502.RmFlowControlAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200603.AckRequestIntervalClientAssertion;
import com.sun.xml.ws.rx.policy.assertion.net.rm200702.AcknowledgementIntervalAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200603.AllowDuplicatesAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200603.CloseTimeoutClientAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200603.OrderedDeliveryAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200603.ResendIntervalClientAssertion;
import com.sun.xml.ws.rx.policy.assertion.net.rm200702.InactivityTimeoutAssertion;
import com.sun.xml.ws.rx.policy.assertion.AssertionNamespace;

import com.sun.xml.ws.rx.policy.assertion.wsmc200702.MakeConnectionSupportedAssertion;
import com.sun.xml.ws.rx.policy.assertion.metro.rm200702.PersistentAssertion;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RxAssertionValidator implements PolicyAssertionValidator {

    private static final ArrayList<QName> SERVER_SIDE_ASSERTIONS = new ArrayList<QName>(5);
    private static final ArrayList<QName> CLIENT_SIDE_ASSERTIONS = new ArrayList<QName>(8);

    private static final List<String> SUPPORTED_DOMAINS = Collections.unmodifiableList(AssertionNamespace.namespacesList());

    static {
        SERVER_SIDE_ASSERTIONS.add(Rm10Assertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(Rm11Assertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(MakeConnectionSupportedAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(OrderedDeliveryAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(AllowDuplicatesAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(RmFlowControlAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(InactivityTimeoutAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(AcknowledgementIntervalAssertion.NAME);
        SERVER_SIDE_ASSERTIONS.add(PersistentAssertion.NAME);

        CLIENT_SIDE_ASSERTIONS.add(AckRequestIntervalClientAssertion.NAME);
        CLIENT_SIDE_ASSERTIONS.add(ResendIntervalClientAssertion.NAME);
        CLIENT_SIDE_ASSERTIONS.add(CloseTimeoutClientAssertion.NAME);
        CLIENT_SIDE_ASSERTIONS.addAll(SERVER_SIDE_ASSERTIONS);
    }

    public RxAssertionValidator() {
    }

    public Fitness validateClientSide(PolicyAssertion assertion) {
        return CLIENT_SIDE_ASSERTIONS.contains(assertion.getName()) ? Fitness.SUPPORTED : Fitness.UNKNOWN;
    }

    public Fitness validateServerSide(PolicyAssertion assertion) {
        QName assertionName = assertion.getName();
        if (SERVER_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.SUPPORTED;
        } else if (CLIENT_SIDE_ASSERTIONS.contains(assertionName)) {
            return Fitness.UNSUPPORTED;
        } else {
            return Fitness.UNKNOWN;
        }
    }

    public String[] declareSupportedDomains() {
        return SUPPORTED_DOMAINS.toArray(new String[SUPPORTED_DOMAINS.size()]);
    }
}
