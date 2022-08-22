package org.mipams.jumbf.core.entities;

public class BoxSegment implements Comparable<BoxSegment> {

    private int size;

    private int boxInstanceNumber;

    private int packetSequenceNumber;

    private int LBox;

    private int TBox;

    private Long XlBox;

    private String payloadUrl;

    public BoxSegment(int size, int boxInstanceNumber, int packetSequenceNumber, int LBox, int TBox, Long XlBox,
            String payloadUrl) {
        setSize(size);
        setBoxInstanceNumber(boxInstanceNumber);
        setPacketSequenceNumber(packetSequenceNumber);
        setLBox(LBox);
        setTBox(TBox);
        setXlBox(XlBox);
        setpayloadUrl(payloadUrl);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getBoxInstanceNumber() {
        return this.boxInstanceNumber;
    }

    public void setBoxInstanceNumber(int boxInstanceNumber) {
        this.boxInstanceNumber = boxInstanceNumber;
    }

    public int getPacketSequenceNumber() {
        return this.packetSequenceNumber;
    }

    public void setPacketSequenceNumber(int packetSequenceNumber) {
        this.packetSequenceNumber = packetSequenceNumber;
    }

    public int getLBox() {
        return this.LBox;
    }

    public void setLBox(int LBox) {
        this.LBox = LBox;
    }

    public int getTBox() {
        return this.TBox;
    }

    public void setTBox(int TBox) {
        this.TBox = TBox;
    }

    public Long getXlBox() {
        return this.XlBox;
    }

    public void setXlBox(Long XlBox) {
        this.XlBox = XlBox;
    }

    public String getPayloadUrl() {
        return this.payloadUrl;
    }

    public void setpayloadUrl(String payloadUrl) {
        this.payloadUrl = payloadUrl;
    }

    @Override
    public int compareTo(BoxSegment o) {

        if (this.getPacketSequenceNumber() == o.getPacketSequenceNumber()) {
            return 0;
        }

        return this.getPacketSequenceNumber() < o.getPacketSequenceNumber() ? -1 : 1;
    }

}
