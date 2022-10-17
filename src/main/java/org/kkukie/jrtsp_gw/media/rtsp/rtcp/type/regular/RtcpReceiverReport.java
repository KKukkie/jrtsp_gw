package org.kkukie.jrtsp_gw.media.rtsp.rtcp.type.regular;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kkukie.jrtsp_gw.media.rtsp.rtcp.base.RtcpFormat;
import org.kkukie.jrtsp_gw.media.rtsp.rtcp.type.regular.base.report.RtcpReportBlock;

import java.util.ArrayList;
import java.util.List;

public class RtcpReceiverReport extends RtcpFormat {

    /**
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                 SSRC_1 (SSRC of first source)                 | report
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ block
     * | fraction lost |       cumulative number of packets lost       |   1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |           extended highest sequence number received           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                      interarrival jitter                      |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                         last SR (LSR)                         |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                   delay since last SR (DLSR)                  |
     * +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
     * |                 SSRC_2 (SSRC of second source)                | report
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ block
     * :                               ...                             :   2
     * +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
     * |                  profile-specific extensions                  |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */

    ////////////////////////////////////////////////////////////
    // VARIABLES
    // Report Block List
    private List<RtcpReportBlock> rtcpReportBlockList = null;

    // Profile-specific extensions
    transient private byte[] profileSpecificExtensions = null;

    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    public RtcpReceiverReport(List<RtcpReportBlock> rtcpReportBlockList, byte[] profileSpecificExtensions) {
        this.rtcpReportBlockList = rtcpReportBlockList;
        this.profileSpecificExtensions = profileSpecificExtensions;
    }

    public RtcpReceiverReport() {
    }

    public RtcpReceiverReport(byte[] data, int resourceCount) {
        int dataLength = data.length;
        if (dataLength > 0) {
            rtcpReportBlockList = new ArrayList<>();
            int index = 0;

            // ReportBlock
            for (int i = 0; i < resourceCount; i++) {
                byte[] curBlockData = new byte[RtcpReportBlock.LENGTH];
                System.arraycopy(data, index, curBlockData, 0, RtcpReportBlock.LENGTH);
                RtcpReportBlock rtcpReceiverRtcpReportBlock = new RtcpReportBlock(curBlockData);
                rtcpReportBlockList.add(rtcpReceiverRtcpReportBlock);
                index += RtcpReportBlock.LENGTH;
            }

            // Profile Specific Extensions
            int remainLength = dataLength - index;
            if (remainLength > 0) {
                profileSpecificExtensions = new byte[remainLength];
                System.arraycopy(data, index, profileSpecificExtensions, 0, remainLength);
            }
        }
    }
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // FUNCTIONS
    @Override
    public byte[] getData() {
        byte[] data = null;
        int index = 0;

        if (rtcpReportBlockList != null && !rtcpReportBlockList.isEmpty()) {
            data = new byte[rtcpReportBlockList.size() * RtcpReportBlock.LENGTH];
            for (RtcpReportBlock rtcpReceiverRtcpReportBlock : rtcpReportBlockList) {
                if (rtcpReceiverRtcpReportBlock == null) {
                    continue;
                }

                byte[] curReportBlockData = rtcpReceiverRtcpReportBlock.getByteData();
                System.arraycopy(curReportBlockData, 0, data, index, curReportBlockData.length);
                index += curReportBlockData.length;
            }
        }

        if (profileSpecificExtensions != null && profileSpecificExtensions.length > 0) {
            if (data != null) {
                byte[] newData = new byte[data.length + profileSpecificExtensions.length];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            } else {
                data = new byte[profileSpecificExtensions.length];
            }

            System.arraycopy(profileSpecificExtensions, 0, data, index, profileSpecificExtensions.length);
        }

        return data;
    }

    public void setData(List<RtcpReportBlock> rtcpReportBlockList, byte[] profileSpecificExtensions) {
        this.rtcpReportBlockList = rtcpReportBlockList;
        this.profileSpecificExtensions = profileSpecificExtensions;
    }

    public List<RtcpReportBlock> getReportBlockList() {
        return rtcpReportBlockList;
    }

    public void setReportBlockList(List<RtcpReportBlock> rtcpReportBlockList) {
        this.rtcpReportBlockList = rtcpReportBlockList;
    }

    public RtcpReportBlock getReportBlockByIndex(int index) {
        if (rtcpReportBlockList == null || index < 0 || index >= rtcpReportBlockList.size()) {
            return null;
        }
        return rtcpReportBlockList.get(index);
    }

    public RtcpReportBlock getReportBlockBySsrc(long ssrc) {
        if (rtcpReportBlockList == null || ssrc <= 0) {
            return null;
        }

        for (RtcpReportBlock rtcpReportBlock : rtcpReportBlockList) {
            if (rtcpReportBlock == null) {
                continue;
            }

            if (rtcpReportBlock.getSsrc() == ssrc) {
                return rtcpReportBlock;
            }
        }

        return null;
    }

    public byte[] getProfileSpecificExtensions() {
        return profileSpecificExtensions;
    }

    public void setProfileSpecificExtensions(byte[] profileSpecificExtensions) {
        this.profileSpecificExtensions = profileSpecificExtensions;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
    ////////////////////////////////////////////////////////////

}
