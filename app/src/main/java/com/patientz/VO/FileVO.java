package com.patientz.VO;

import java.util.Date;

public class FileVO {

	long fileId;
    String displayName;
    String name;
    String localPath;
    String path;
    String mobilePath;
    String extension;
    String contentType;
    Date dateCreated;
    long fileSize;
    int attachmentType;

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMobilePath() {
		return mobilePath;
	}
	public void setMobilePath(String mobilePath) {
		this.mobilePath = mobilePath;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public int getAttachmentType() {
		return attachmentType;
	}
	public void setAttachmentType(int attachmentType) {
		this.attachmentType = attachmentType;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
//	public String getPath() {
//		return path;
//	}
//	public void setPath(String path) {
//		this.path = path;
//	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
//	public String getMobilePath() {
//		return mobilePath;
//	}
//	public void setMobilePath(String mobilePath) {
//		this.mobilePath = mobilePath;
//	}
	
}
