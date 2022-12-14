/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.kkukie.jrtsp_gw.media.core.stream.stun.candidate;

import org.kkukie.jrtsp_gw.media.core.stream.stun.candidate.base.CandidateWrapper;

import java.nio.channels.DatagramChannel;

public class LocalCandidateWrapper implements CandidateWrapper, Comparable<LocalCandidateWrapper> {

    private final IceCandidate candidate;
    private final DatagramChannel udpChannel;

    public LocalCandidateWrapper(IceCandidate candidate, DatagramChannel udpChannel) {
        this.candidate = candidate;
        this.udpChannel = udpChannel;
    }

    public IceCandidate getCandidate () {
        return this.candidate;
    }

    public DatagramChannel getChannel () {
        return udpChannel;
    }

    public int compareTo (LocalCandidateWrapper other) {
        if (other == null) {
            return 1;
        }
        return this.candidate.compareTo(other.candidate);
    }

}
