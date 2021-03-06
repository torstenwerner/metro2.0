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
import com.sun.xml.ws.rx.rm.localization.LocalizationMessages;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * An example of when this fault is generated is when a message is Received by 
 * the RM Source containing a SequenceAcknowledgement covering messages that have
 * not been sent.
 *
 * Properties:
 * [Code] Sender
 * [Subcode] wsrm:InvalidAcknowledgement
 * [Reason] The SequenceAcknowledgement violates the cumulative Acknowledgement invariant.
 * [Detail] <wsrm:SequenceAcknowledgement ...> ... </wsrm:SequenceAcknowledgement>
 *
 * Generated by: RM Source.
 * Condition : In response to a SequenceAcknowledgement that violate the invariants
 * stated in 2.3 or any of the requirements in 3.9 about valid combinations of AckRange,
 * Nack and None in a single SequenceAcknowledgement element or with respect to already
 * Received such elements.
 * Action Upon Generation : Unspecified.
 * Action Upon Receipt : Unspecified.
 *
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public final class InvalidAcknowledgementException extends AbstractSoapFaultException {
    private static final long serialVersionUID = 647447570493203088L;
    //
    private final List<Sequence.AckRange> ackedRanges;
    
    public InvalidAcknowledgementException(String sequenceId, long messageIdentifier, List<Sequence.AckRange> ackedRanges) {
        super(LocalizationMessages.WSRM_1125_ILLEGAL_MESSAGE_ID(sequenceId, messageIdentifier), // TODO P3 message
                "The SequenceAcknowledgement violates the cumulative Acknowledgement invariant.",
                false);

        this.ackedRanges = ackedRanges;
    }

    @Override
    public Code getCode() {
        return Code.Sender;
    }

    @Override
    public QName getSubcode(RmVersion rv) {
        return rv.invalidAcknowledgementFaultCode;
    }

    @Override
    public String getDetailValue() {
        return ""; // TODO P2 implement
    }


}
