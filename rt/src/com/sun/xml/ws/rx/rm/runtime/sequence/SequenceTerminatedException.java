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

import com.sun.xml.ws.rx.rm.RmVersion;
import com.sun.xml.ws.rx.rm.faults.AbstractSoapFaultException;
import javax.xml.namespace.QName;

/**
 *
 * The Endpoint that generates this fault SHOULD make every reasonable effort to
 * notify the corresponding Endpoint of this decision.
 *
 * Properties:
 * [Code] Sender or Receiver
 * [Subcode] wsrm:SequenceTerminated
 * [Reason] The Sequence has been terminated due to an unrecoverable error.
 * [Detail] <wsrm:Identifier ...> xs:anyURI </wsrm:Identifier>
 *
 * Generated by: RM Source or RM Destination.
 * Condition : Encountering an unrecoverable condition or detection of violation of the protocol.
 * Action Upon Generation : Sequence termination.
 * Action Upon Receipt : MUST terminate the Sequence if not otherwise terminated.
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class SequenceTerminatedException extends AbstractSoapFaultException {

    private static final long serialVersionUID = -4689338956255310299L;
    //
    private final Code code;

    public SequenceTerminatedException(String message, Code code) {
        super(message, "The Sequence has been terminated due to an unrecoverable error.", true);

        this.code = code;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public QName getSubcode(RmVersion rv) {
        return rv.sequenceTerminatedFaultCode;
    }

    @Override
    public String getDetailValue() {
        return ""; // TODO P2 implement
    }
}
