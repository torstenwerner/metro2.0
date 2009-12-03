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

package com.sun.xml.ws.addressing.policy;

import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import junit.framework.*;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;

import static com.sun.xml.ws.policy.testutils.PolicyResourceLoader.getWSDLModel;


/**
 *
 * @author japod
 */
public class AddressingFeatureConfiguratorTest extends TestCase {
    
    
    /**
     * Test of configure method, of class com.sun.xml.ws.policy.jaxws.addressing.AddressingFeatureConfigurator.
     * policy assertion present
     */
    public void testConfigureW3CAddressingAssertionPresent() throws Exception {
        WSDLModel model = getWSDLModel("jaxws-spi/testModelConfigProviderAddrW3C.wsdl");
        assertTrue(model.getService(new QName("http://example.org","DictionaryService")).
                getFirstPort().getFeature(AddressingFeature.class).isEnabled());
    }
    
    /**
     * Test of configure method, of class com.sun.xml.ws.policy.jaxws.addressing.AddressingFeatureConfigurator.
     * policy assertion present
     */
    public void testConfigureMEMBERAddressingAssertionPresent() throws Exception {
        WSDLModel model = getWSDLModel("jaxws-spi/testModelConfigProviderAddrMEMBER.wsdl");
        assertTrue(model.getService(new QName("http://example.org","DictionaryService")).
                getFirstPort().getFeature(MemberSubmissionAddressingFeature.class).isEnabled());
    }

    /**
     * Test of configure method, of class com.sun.xml.ws.policy.jaxws.addressing.AddressingFeatureConfigurator.
     * policy assertion not present
     */
    public void testConfigureAddressingAssertionNotPresent() throws Exception {
        WSDLModel model = getWSDLModel("jaxws-spi/testModelConfigProviderAddrPolicyNotPresent.wsdl");
        assertNull(model.getService(new QName("http://example.org","DictionaryService")).
                getFirstPort().getFeature(AddressingFeature.class));
    }
    
}
