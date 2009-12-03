/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 *  Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 * 
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License. You can obtain
 *  a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 *  or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 *  language governing permissions and limitations under the License.
 * 
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *  Sun designates this particular file as subject to the "Classpath" exception
 *  as provided by Sun in the GPL Version 2 section of the License file that
 *  accompanied this code.  If applicable, add the following below the License
 *  Header, with the fields enclosed by brackets [] replaced by your own
 *  identifying information: "Portions Copyrighted [year]
 *  [name of copyright owner]"
 * 
 *  Contributor(s):
 * 
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */

package com.sun.xml.ws.rx.policy.assertion;

import com.sun.xml.ws.rx.policy.assertion.wsrm200502.Rm10Assertion;
import com.sun.xml.ws.rx.rm.ReliableMessagingFeature;
import com.sun.xml.ws.rx.testutil.ResourceLoader;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class Rm10AssertionTest extends TestCase {

    public Rm10AssertionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getInstantiator method, of class Rm10Assertion.
     */
    public void testGetInstantiator() {
        assertNotNull(Rm10Assertion.getInstantiator());
    }

    /**
     * Test of getInactivityTimeout method, of class Rm10Assertion.
     */
    public void testGetInactivityTimeout() {
        assertEquals(ReliableMessagingFeature.DEFAULT_SEQUENCE_INACTIVITY_TIMEOUT, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class).getInactivityTimeout());
        assertEquals(1000, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).getInactivityTimeout());
    }

    /**
     * Test of getBaseRetransmittionInterval method, of class Rm10Assertion.
     */
    public void testGetBaseRetransmittionInterval() {
        assertEquals(ReliableMessagingFeature.DEFAULT_MESSAGE_RETRANSMISSION_INTERVAL, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class).getBaseRetransmittionInterval());
        assertEquals(1000, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).getBaseRetransmittionInterval());
    }

    /**
     * Test of useExponentialBackoffAlgorithm method, of class Rm10Assertion.
     */
    public void testUseExponentialBackoffAlgorithm() {
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_DEFAULT_POLICY_RESOURCE_NAME, Rm10Assertion.class).useExponentialBackoffAlgorithm());
        assertEquals(true, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_0_CUSTOM_POLICY_RESOURCE_NAME, Rm10Assertion.class).useExponentialBackoffAlgorithm());
    }
}
