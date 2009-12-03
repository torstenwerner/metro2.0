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
package com.sun.xml.ws.dump;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.ws.api.FeatureConstructor;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
@ManagedData
public final class MessageDumpingFeature extends WebServiceFeature {

    public static final String ID = "com.sun.xml.ws.messagedump.MessageDumpingFeature";
    //
    private static final Level DEFAULT_MSG_LOG_LEVEL = Level.FINE;
    //
    private final Queue<String> messageQueue;
    private final AtomicBoolean messageLoggingStatus;
    private final String messageLoggingRoot;
    private final Level messageLoggingLevel;

    public MessageDumpingFeature() {
        this(null, null, true);
    }

    public MessageDumpingFeature(String msgLogRoot, Level msgLogLevel, boolean storeMessages) {
        this.messageQueue =  (storeMessages) ? new java.util.concurrent.ConcurrentLinkedQueue<String>() : null;
        this.messageLoggingStatus = new AtomicBoolean(true);
        this.messageLoggingRoot = (msgLogRoot != null && msgLogRoot.length() > 0) ? msgLogRoot : MessageDumpingTube.DEFAULT_MSGDUMP_LOGGING_ROOT;
        this.messageLoggingLevel = (msgLogLevel != null) ? msgLogLevel : DEFAULT_MSG_LOG_LEVEL;

        super.enabled = true;
    }

    public MessageDumpingFeature(boolean enabled) {
        // this constructor is here just to satisfy JAX-WS specification requirements
        this();
        super.enabled = enabled;
    }

    @FeatureConstructor({"enabled", "messageLoggingRoot", "messageLoggingLevel", "storeMessages"})
    public MessageDumpingFeature(boolean enabled, String msgLogRoot, String msgLogLevel, boolean storeMessages) {
        // this constructor is here just to satisfy JAX-WS specification requirements
        this(msgLogRoot, Level.parse(msgLogLevel), storeMessages);
        
        super.enabled = enabled;
    }

    @Override
    @ManagedAttribute
    public String getID() {
        return ID;
    }

    public String nextMessage() {
        return (messageQueue != null) ? messageQueue.poll() : null;
    }

    public void enableMessageLogging() {
        messageLoggingStatus.set(true);
    }

    public void disableMessageLogging() {
        messageLoggingStatus.set(false);
    }

    @ManagedAttribute
    public boolean getMessageLoggingStatus() {
        return messageLoggingStatus.get();
    }

    @ManagedAttribute
    public String getMessageLoggingRoot() {
        return messageLoggingRoot;
    }

    @ManagedAttribute
    public Level getMessageLoggingLevel() {
        return messageLoggingLevel;
    }

    boolean offerMessage(String message) {        
        return (messageQueue != null) ? messageQueue.offer(message) : false;
    }
}
