package org.mipams.jumbf.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BoxSegment implements Comparable<BoxSegment> {

    private @Getter int size;

    private @Getter int boxInstanceNumber;

    private @Getter int packetSequenceNumber;

    private @Getter int LBox;

    private @Getter int TBox;

    private @Getter Long XlBox;

    private @Getter String payloadUrl;

    @Override
    public int compareTo(BoxSegment o) {

        if (this.getPacketSequenceNumber() == o.getPacketSequenceNumber()) {
            return 0;
        }

        return this.getPacketSequenceNumber() < o.getPacketSequenceNumber() ? -1 : 1;
    }

}
