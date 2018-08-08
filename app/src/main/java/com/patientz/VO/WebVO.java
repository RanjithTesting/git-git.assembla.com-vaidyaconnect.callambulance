package com.patientz.VO;

public class WebVO {
    SMSMessageVO     sVO;
    String             iP;     // IP Address of the device the emergency was initiated
    String             lpd;     // Location provider
    String[]        eIPh;     // emergency invoking phone number(s) multiple if dual sim

    public SMSMessageVO getsVO() {
        return sVO;
    }
    public void setsVO(SMSMessageVO sVO) {
        this.sVO = sVO;
    }
    public String getiP() {
        return iP;
    }
    public void setiP(String iP) {
        this.iP = iP;
    }
    public String getLpd() {
        return lpd;
    }
    public void setLpd(String lpd) {
        this.lpd = lpd;
    }
    public String[] geteIPh() {
        return eIPh;
    }
    public void seteIPh(String[] eIPh) {
        this.eIPh = eIPh;
    }

}
