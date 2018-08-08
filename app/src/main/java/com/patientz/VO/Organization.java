package com.patientz.VO;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunil on 13/1/17.
 */

public class Organization implements Serializable{

    long id;
    String name;
    String nameColorHex;
    String aboutUs;
    String valuesMission;
    String history;
    String handle;
    String protocol;
    String website;
    String email;
    String phone;
    String type;
    boolean isLive;
    String notes;
    UserUploadedMedia file;
    UserUploadedMedia orgLogo;
    UserUploadedMedia orgBanner;
    String prefix;
    String tagline;
    String taglineColorHex;
    String headingColorHex;
    String textHighlightColorHex;
    String buttonColorHex;
    String facebookPageURL;
    String gallery;
    boolean enableBranding;
    boolean patientReqAuto;
    boolean hideOrgName;
    Date dateCreated;
    Date lastUpdated;
    boolean isActive;
    boolean isNetwork;

    public String getNameColorHex() {
        return nameColorHex;
    }

    public void setNameColorHex(String nameColorHex) {
        this.nameColorHex = nameColorHex;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getValuesMission() {
        return valuesMission;
    }

    public void setValuesMission(String valuesMission) {
        this.valuesMission = valuesMission;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UserUploadedMedia getFile() {
        return file;
    }

    public void setFile(UserUploadedMedia file) {
        this.file = file;
    }

    public UserUploadedMedia getOrgLogo() {
        return orgLogo;
    }

    public void setOrgLogo(UserUploadedMedia orgLogo) {
        this.orgLogo = orgLogo;
    }

    public UserUploadedMedia getOrgBanner() {
        return orgBanner;
    }

    public void setOrgBanner(UserUploadedMedia orgBanner) {
        this.orgBanner = orgBanner;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTaglineColorHex() {
        return taglineColorHex;
    }

    public void setTaglineColorHex(String taglineColorHex) {
        this.taglineColorHex = taglineColorHex;
    }

    public String getHeadingColorHex() {
        return headingColorHex;
    }

    public void setHeadingColorHex(String headingColorHex) {
        this.headingColorHex = headingColorHex;
    }

    public String getTextHighlightColorHex() {
        return textHighlightColorHex;
    }

    public void setTextHighlightColorHex(String textHighlightColorHex) {
        this.textHighlightColorHex = textHighlightColorHex;
    }

    public String getButtonColorHex() {
        return buttonColorHex;
    }

    public void setButtonColorHex(String buttonColorHex) {
        this.buttonColorHex = buttonColorHex;
    }

    public String getFacebookPageURL() {
        return facebookPageURL;
    }

    public void setFacebookPageURL(String facebookPageURL) {
        this.facebookPageURL = facebookPageURL;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public boolean isEnableBranding() {
        return enableBranding;
    }

    public void setEnableBranding(boolean enableBranding) {
        this.enableBranding = enableBranding;
    }

    public boolean isPatientReqAuto() {
        return patientReqAuto;
    }

    public void setPatientReqAuto(boolean patientReqAuto) {
        this.patientReqAuto = patientReqAuto;
    }

    public boolean isHideOrgName() {
        return hideOrgName;
    }

    public void setHideOrgName(boolean hideOrgName) {
        this.hideOrgName = hideOrgName;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public void setNetwork(boolean network) {
        isNetwork = network;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
