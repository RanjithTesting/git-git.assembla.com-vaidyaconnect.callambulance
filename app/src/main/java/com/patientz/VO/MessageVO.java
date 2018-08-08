package com.patientz.VO;

import java.util.ArrayList;
import java.util.Date;

public class MessageVO {
		
		long messageId;
		Date dateCreated;
		Date lastUpdated;
		String subject;
		String message;
		long immediateParentId;
		long originalParentId;
		String source; 
		boolean isExclusive;
		String addedProfileIds;
		long replyCount;
		String status;
		String fileIdsCsv;
        ArrayList<FileVO> fileVOs;
		long createdByProfileId;
		long userProfileId;
		long serverTS;
		boolean isDirty;
		ArrayList<MessageVO> replies;
		ArrayList<MessageRecipientVO> recipients;
		ArrayList<UserProfileVO> recipientProfileVOs;
		String createdByDisplayName;
		String toList;
		String attachFileUri;
		String attachFileType;
		
		
		
		public String getToList() {
			return toList;
		}

		public void setToList(String toList) {
			this.toList = toList;
		}

		public String getCreatedByDisplayName() {
			return createdByDisplayName;
		}

		public void setCreatedByDisplayName(String createdByDisplayName) {
			this.createdByDisplayName = createdByDisplayName;
		}

		public ArrayList<UserProfileVO> getRecipientProfileVOs() {
			return recipientProfileVOs;
		}

		public void setRecipientProfileVOs(ArrayList<UserProfileVO> recipientProfileVOs) {
			this.recipientProfileVOs = recipientProfileVOs;
		}

		public boolean getIsExclusive() {
			return isExclusive;
		}
	
		public void setIsExclusive(boolean isExclusive) {
			this.isExclusive = isExclusive;
		}
		public long getServerTS() {
			return serverTS;
		}

		public void setServerTS(long serverTS) {
			this.serverTS = serverTS;
		}
		public ArrayList<MessageVO> getReplies() {
			return replies;
		}
		public void setReplies(ArrayList<MessageVO> replies) {
			this.replies = replies;
		}
		
		public ArrayList<MessageRecipientVO> getRecipients() {
			return recipients;
		}
		public void setRecipients(ArrayList<MessageRecipientVO> recipients) {
			this.recipients = recipients;
		}
		public boolean isDirty() {
			return isDirty;
		}
		public void setDirty(boolean isDirty) {
			this.isDirty = isDirty;
		}
		public long getUserProfileId() {
			return userProfileId;
		}
		public void setUserProfileId(long userProfileId) {
			this.userProfileId = userProfileId;
		}
		public long getMessageId() {
			return messageId;
		}
		public void setMessageId(long messageId) {
			this.messageId = messageId;
		}
		public Date getDateCreated() {
			return dateCreated;
		}
		public void setDateCreated(Date dateCreated) {
			this.dateCreated = dateCreated;
		}
		public Date getLastUpdated() {
			return lastUpdated;
		}
		public void setLastUpdated(Date lastUpdated) {
			this.lastUpdated = lastUpdated;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public long getImmediateParentId() {
			return immediateParentId;
		}
		public void setImmediateParentId(long immediateParentId) {
			this.immediateParentId = immediateParentId;
		}
		public long getOriginalParentId() {
			return originalParentId;
		}
		public void setOriginalParentId(long originalParentId) {
			this.originalParentId = originalParentId;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getAddedProfileIds() {
			return addedProfileIds;
		}
		public void setAddedProfileIds(String addedProfileIds) {
			this.addedProfileIds = addedProfileIds;
		}
		public long getReplyCount() {
			return replyCount;
		}
		public void setReplyCount(long replyCount) {
			this.replyCount = replyCount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getFileIdsCsv() {
			return fileIdsCsv;
		}
		public void setFileIdsCsv(String fileIdsCsv) {
			this.fileIdsCsv = fileIdsCsv;
		}
		public long getCreatedByProfileId() {
			return createdByProfileId;
		}

		public void setCreatedByProfileId(long createdByProfileId) {
			this.createdByProfileId = createdByProfileId;
		}
		public String getAttachedFile() {
			return attachFileUri;
		}
		public void setAttachedFile(String string) {
			this.attachFileUri = string;
		}

		public String getAttachFileType() {
			return attachFileType;
		}

		public void setAttachFileType(String attachFileType) {
			this.attachFileType = attachFileType;
		}
		
        public ArrayList<FileVO> getFileVOs() {
            return fileVOs;
        }

        public void setFileVOs(ArrayList<FileVO> fileVOs) {
            this.fileVOs = fileVOs;
        }
}
