/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.kkukie.jrtsp_gw.media.core.stream.rtcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 *
 * @author Amit Bhayani
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 */
public class RtcpPacket implements Serializable {

	private static final long serialVersionUID = -7175947723683038337L;

	private static final Logger logger = LoggerFactory.getLogger(RtcpPacket.class);

	/**
	 * Maximum number of reporting sources
	 */
	public static final int MAX_SOURCES = 31;

	private RtcpSenderReport senderReport = null;
	private RtcpReceiverReport receiverReport = null;
	private RtcpSdes sdes = null;
	private RtcpBye bye = null;
	private RtcpAppDefined appDefined = null;
//	private RtcpRtpFb rtcpRtpFb = null;
	private RtcpPsFb rtcpPsFb = null;

	private int packetCount = 0;
	private int size = 0;

	public RtcpPacket() {

	}

	public RtcpPacket(RtcpSenderReport senderReport, RtcpReceiverReport receiverReport, RtcpSdes sdes, RtcpBye bye, RtcpAppDefined appDefined, RtcpPsFb rtcpPsFb) {
		this.senderReport = senderReport;
		this.receiverReport = receiverReport;
		this.sdes = sdes;
		this.bye = bye;
		this.appDefined = appDefined;
		this.rtcpPsFb = rtcpPsFb;
	}

	public RtcpPacket(RtcpReport report, RtcpSdes sdes, RtcpBye bye) {
		if (report != null) {
			if (report.isSender()) {
				this.senderReport = (RtcpSenderReport) report;
			} else {
				this.receiverReport = (RtcpReceiverReport) report;
			}
		}
		this.sdes = sdes;
		this.bye = bye;
	}

	public RtcpPacket(RtcpReport report, RtcpSdes sdes) {
		this(report, sdes, null);
	}

	public RtcpPacket(RtcpPsFb rtcpPsFb) {
		this.rtcpPsFb = rtcpPsFb;
	}

	public int decode(byte[] rawData, int offSet) {
//		this.size = rawData.length - offSet;
		this.size = 0;
		while (offSet < rawData.length) {
			int type = rawData[offSet + 1] & 0x000000FF;
			switch (type) {
				case RtcpHeader.RTCP_SR:
					packetCount++;
					this.senderReport = new RtcpSenderReport();
					offSet = this.senderReport.decode(rawData, offSet);
					this.size += this.senderReport.length;
					logger.trace("RtcpPacket: Type=RTCP_SR, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_RR:
					packetCount++;
					this.receiverReport = new RtcpReceiverReport();
					offSet = this.receiverReport.decode(rawData, offSet);
					this.size += this.receiverReport.length;
					logger.trace("RtcpPacket: Type=RTCP_RR, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_SDES:
					packetCount++;
					this.sdes = new RtcpSdes();
					offSet = this.sdes.decode(rawData, offSet);
					this.size += this.sdes.length;
					logger.trace("RtcpPacket: Type=RTCP_SDES, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_APP:
					packetCount++;
					this.appDefined = new RtcpAppDefined();
					offSet = this.appDefined.decode(rawData, offSet);
					this.size += this.appDefined.length;
					logger.trace("RtcpPacket: Type=RTCP_APP, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_BYE:
					packetCount++;
					this.bye = new RtcpBye();
					offSet = this.bye.decode(rawData, offSet);
					this.size += this.bye.length;
					logger.trace("RtcpPacket: Type=RTCP_BYE, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_RTPFB:
					packetCount++;
					this.rtcpPsFb = new RtcpPsFb();
					offSet = rawData.length;
					// offSet = this.rtcpPsFb.decode(rawData, offSet);
					logger.trace("RtcpPacket: Type=RTCP_RTPFB, offSet={}", offSet);
					break;
				case RtcpHeader.RTCP_PSFB:
					packetCount++;
					offSet = rawData.length;
					// offSet = this.rtcpPsFb.decode(rawData, offSet);
					logger.trace("RtcpPacket: Type=RTCP_PSFB, offSet={}", offSet);
					break;
				default:
					logger.error("(ReceivedType={}) RTCP Packet decoding is failed. (offSet={}, PacketCount={})",
							type, offSet, packetCount
					);
					offSet = rawData.length;
					break;
			}
		}
		return offSet;
	}

	public int encode(byte[] rawData, int offSet) {
		int initialOffSet = offSet;

		if (this.rtcpPsFb != null){
			packetCount++;
			offSet = this.rtcpPsFb.encode(rawData, offSet);
		}

		if (this.senderReport != null) {
			packetCount++;
			offSet = this.senderReport.encode(rawData, offSet);
		}

		if (this.receiverReport != null) {
			packetCount++;
			offSet = this.receiverReport.encode(rawData, offSet);
		}

		if (this.sdes != null) {
			packetCount++;
			offSet = this.sdes.encode(rawData, offSet);
		}

		if (this.appDefined != null) {
			packetCount++;
			offSet = this.appDefined.encode(rawData, offSet);
		}

		if (this.bye != null) {
			packetCount++;
			offSet = this.bye.encode(rawData, offSet);
		}

		this.size = offSet - initialOffSet;
		return offSet;
	}

	public boolean isSender() {
		return this.senderReport != null;
	}

	public RtcpPacketType getPacketType() {
		if(this.bye == null) {
			return RtcpPacketType.RTCP_REPORT;
		}
		return RtcpPacketType.RTCP_BYE;
	}

	public RtcpReport getReport() {
		if(isSender()) {
			return this.senderReport;
		}
		return this.receiverReport;
	}

	public RtcpSenderReport getSenderReport() {
		return senderReport;
	}

	public RtcpReceiverReport getReceiverReport() {
		return receiverReport;
	}

	public RtcpSdes getSdes() {
		return sdes;
	}

	public RtcpBye getBye() {
		return bye;
	}

	public boolean hasBye() {
		return this.bye != null;
	}

	public RtcpAppDefined getAppDefined() {
		return appDefined;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		// Print RR/SR
		RtcpReport report = getReport();
		if(report != null) {
			builder.append(report.toString());
		}
		// Print SDES if exists
		if(this.sdes != null) {
			builder.append(this.sdes.toString());
		}
		// Print BYE if exists
		if(this.bye != null) {
			builder.append(bye.toString());
		}

		return builder.toString();
	}
}
