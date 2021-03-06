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

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.rx.RxException;
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
//
import com.sun.xml.ws.runtime.util.Session;
import com.sun.xml.ws.runtime.util.SessionManager;
import com.sun.xml.ws.security.secext10.ObjectFactory;
import com.sun.xml.ws.security.trust.WSTrustElementFactory;
import com.sun.xml.ws.security.trust.elements.str.DirectReference;
import com.sun.xml.ws.security.trust.elements.str.Reference;

/**
 * The non-instantiable utility class containing various common static utility methods 
 * used for runtime processing.
 * 
 * @author Marek Potociar (marek.potociar at sun.com)
 */
final class Utilities {

    private static final Logger LOGGER = Logger.getLogger(Utilities.class);

    /**
     * Non-instantiable constructor
     */
    private Utilities() {
        // nothing to do
    }

    /**
     * Checks whether the actual sequence identifier value equals to the expected value
     * and throws a logged exception if th check fails.
     * 
     * @param expected expected sequence identifier value
     * @param actual actual sequence identifier value
     * @throws java.lang.IllegalStateException if actual value does not equal to the expected value
     */
    static void assertSequenceId(String expected, String actual) throws IllegalStateException {
        if (expected != null && !expected.equals(actual)) {
            throw LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSRM_1105_SEQUENCE_ID_NOT_RECOGNIZED(actual, expected)));
        }
    }

    static String extractSecurityContextTokenId(com.sun.xml.ws.security.secext10.SecurityTokenReferenceType strType) throws RxException {
        Reference strReference = WSTrustElementFactory.newInstance().createSecurityTokenReference(
                new ObjectFactory().createSecurityTokenReference(strType)).getReference();
        if (!(strReference instanceof DirectReference)) {
            throw LOGGER.logSevereException(
                    new RxException(LocalizationMessages.WSRM_1132_SECURITY_REFERENCE_ERROR(strReference.getClass().getName())));
        }
        return ((DirectReference) strReference).getURIAttr().toString();
    }

    /**
     * Either creates a new <code>Session</code> for the
     * <code>InboundSequence</code> or returns one that has
     * already been created by the SC Pipe.
     *
     * @param endpoint endpoint instance
     * @param sessionId session identifier
     * @return The Session
     */
    static Session startSession(WSEndpoint endpoint, String sessionId) {
        SessionManager manager = SessionManager.getSessionManager(endpoint);
        Session session = manager.getSession(sessionId);
        if (session == null) {
            session = manager.createSession(sessionId);
        }

        return session;
    }

    /**
     * Terminates the session associated with the sequence if
     * RM owns the lifetime of the session, i.e. if SC is not present.
     *
     * @param endpoint endpoint instance
     * @param sessionId session identifier
     */
    static void endSessionIfExists(WSEndpoint endpoint, String sessionId) {
        SessionManager manager = SessionManager.getSessionManager(endpoint);
        if (manager.getSession(sessionId) != null) {
            manager.terminateSession(sessionId);
        }
    }
}