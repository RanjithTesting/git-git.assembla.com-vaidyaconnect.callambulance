package com.patientz.VO;


public class MessageRecipientVO {
	long messageRecipientId;
	String status;
	long messageId;
	long recipientId;

	public long getMessageRecipientId() {
		return messageRecipientId;
	}
	public void setMessageRecipientId(long messageRecipientId) {
		this.messageRecipientId = messageRecipientId;
	}
	public long getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(long recipientId) {
		this.recipientId = recipientId;
	}
	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
