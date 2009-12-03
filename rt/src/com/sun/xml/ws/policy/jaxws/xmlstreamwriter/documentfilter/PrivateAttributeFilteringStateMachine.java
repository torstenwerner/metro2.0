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
package com.sun.xml.ws.policy.jaxws.xmlstreamwriter.documentfilter;

import com.sun.xml.ws.policy.jaxws.xmlstreamwriter.Invocation;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import javax.xml.stream.XMLStreamWriter;

import static com.sun.xml.ws.policy.PolicyConstants.VISIBILITY_ATTRIBUTE;
import static com.sun.xml.ws.policy.PolicyConstants.VISIBILITY_VALUE_PRIVATE;
import static com.sun.xml.ws.policy.jaxws.xmlstreamwriter.documentfilter.ProcessingStateChange.*;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class PrivateAttributeFilteringStateMachine implements FilteringStateMachine {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PrivateAttributeFilteringStateMachine.class);
    
    private int depth; // indicates the depth in which we are currently nested in the element that should be filtered out
    private boolean filteringOn; // indicates that currently processed elements will be filtered out.
    private boolean cmdBufferingOn; // indicates whether the commands should be buffered or whether they can be directly executed on the underlying XML output stream
    
    
    /** Creates a new instance of PrivateAttributeFilteringStateMachine */
    public PrivateAttributeFilteringStateMachine() {
        // nothing to initialize
    }
    
    public ProcessingStateChange getStateChange(final Invocation invocation, final XMLStreamWriter writer) {
        LOGGER.entering(invocation);
        ProcessingStateChange resultingState = NO_CHANGE;
        try {
            switch (invocation.getMethodType()) {
                case WRITE_START_ELEMENT:
                    if (filteringOn) {
                        depth++;
                    } else if (cmdBufferingOn) {
                        resultingState = RESTART_BUFFERING;
                    } else {
                        cmdBufferingOn = true;
                        resultingState = START_BUFFERING;
                    }
                    break;
                case WRITE_END_ELEMENT:
                    if (filteringOn) {
                        if (depth == 0) {
                            filteringOn = false;
                            resultingState = STOP_FILTERING;
                        } else {
                            depth--;
                        }
                    } else if (cmdBufferingOn) {
                        cmdBufferingOn = false;
                        resultingState = STOP_BUFFERING;
                    }
                    break;
                case WRITE_ATTRIBUTE:
                    if (!filteringOn && cmdBufferingOn && startFiltering(invocation, writer)) {
                        filteringOn = true;
                        cmdBufferingOn = false;
                        resultingState = START_FILTERING;
                    }
                    break;
                case CLOSE:
                    if (filteringOn) {
                        filteringOn = false;
                        resultingState = STOP_FILTERING;
                    } else if (cmdBufferingOn) {
                        cmdBufferingOn = false;
                        resultingState = STOP_BUFFERING;
                    }
                    break;
                default:
                    break;
            }
            
            return resultingState;            
        } finally {
            LOGGER.exiting(resultingState);
        }
    }
    
    private boolean startFiltering(final Invocation invocation, final XMLStreamWriter writer) {
        final XmlFilteringUtils.AttributeInfo attributeInfo = XmlFilteringUtils.getAttributeNameToWrite(invocation, XmlFilteringUtils.getDefaultNamespaceURI(writer));
        return VISIBILITY_ATTRIBUTE.equals(attributeInfo.getName()) && VISIBILITY_VALUE_PRIVATE.equals(attributeInfo.getValue());
    }
}
