/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.api.policy.ModelTranslator;
import com.sun.xml.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.*;

/**
 *
 * @author Mayank.Mishra@SUN.com
 */
public class UsernameTokenTest extends TestCase {
    
    public UsernameTokenTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UsernameTokenTest.class);
        
        return suite;
    }
    
    private PolicySourceModel unmarshalPolicyResource(String resource) throws PolicyException, IOException {
        Reader reader = getResourceReader(resource);
        PolicySourceModel model = ModelUnmarshaller.getUnmarshaller().unmarshalModel(reader);
        reader.close();
        return model;
    }
    
    private Reader getResourceReader(String resourceName) {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
    }
    
    public Policy unmarshalPolicy(String xmlFile)throws Exception{
        PolicySourceModel model =  unmarshalPolicyResource(
                xmlFile);
        Policy mbp = ModelTranslator.getTranslator().translate(model);
        return mbp;
        
    }
    
    public void testUserNameTokenAssertions_8() throws Exception{
        testTokenAssertionsType("security/UserNameTokenAssertions1.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions2.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions3.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions4.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_10);
        testTokenAssertionsType("security/UserNameTokenAssertions5.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions6.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions7.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
        testTokenAssertionsType("security/UserNameTokenAssertions8.xml", com.sun.xml.ws.security.policy.UserNameToken.WSS_USERNAME_TOKEN_11);
    }
    
    public void testTokenAssertionsType(String fileName, String tokenType) throws Exception{
        Policy policy = unmarshalPolicy(fileName);
        assertNotNull(policy);
        Iterator <AssertionSet> itr = policy.iterator();
        if(itr.hasNext()) {
            AssertionSet as = itr.next();
            for(PolicyAssertion assertion : as) {
                assertEquals("Invalid assertion","UsernameToken",assertion.getName().getLocalPart());
                com.sun.xml.ws.security.policy.UserNameToken ut = (com.sun.xml.ws.security.policy.UserNameToken)assertion;
                assertTrue(tokenType.equals(ut.getType()));
            }
        } else {
            throw new Exception("No Assertions found!. Unmarshalling of "+fileName+" failed!");
        }
    }

}
