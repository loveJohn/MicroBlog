package com.example.test613;

/**
 * Created by LoveExtra on 2016/6/13.
 */
public class PhoneMessage {
    private int messageImage;
    private String messageName;
    private String messageContent;

    public PhoneMessage() {
    }

    public PhoneMessage(int messageImage, String messageName, String messageContent) {
        this.messageImage = messageImage;
        this.messageName = messageName;
        this.messageContent = messageContent;
    }

    public int getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(int messageImage) {
        this.messageImage = messageImage;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
