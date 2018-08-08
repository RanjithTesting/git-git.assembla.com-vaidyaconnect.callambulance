package com.patientz.VO;

public class SMSMessageVO {

    Long pId;     // Patient ID
    Long uId;     // Initiated by User ID
   // String iP;     // IP Address of the device the emergency was initiated
    Double gLt;     // lattitude
    Double gLn;     // longitude
    Long dt;     // timestamp when the emergency was initiated
    String st;     // Status --- inv/rev/loc/test
    String cd;     // Message code --for revoke
    String lacc; // Location accuracy in meters
    //String lpd; // Location provider
    String e; // environment DEV=DV,QA=QA,DEMO=DM,STAGING=SG,PREPROD=PP,PROD=PR
    String     ep;     // emergency phone number
    String 	et; 	// emergency type/Body Areas/Issues
    String    ce;        //- If 0 or not passed then call EMRI web service.If is 1 then do not call EMRI web service.
    long     bId; // preferred org branch id

    public long getbId() {
        return bId;
    }

    public void setbId(long bId) {
        this.bId = bId;
    }

    public Long getpId() {
        return pId;
    }
    public void setpId(Long pId) {
        this.pId = pId;
    }
    public Long getuId() {
        return uId;
    }
    public void setuId(Long uId) {
        this.uId = uId;
    }
/*    public String getiP() {
        return iP;
    }
    public void setiP(String iP) {
        this.iP = iP;
    }*/
    public Double getgLt() {
        return gLt;
    }
    public void setgLt(Double gLt) {
        this.gLt = gLt;
    }
    public Double getgLn() {
        return gLn;
    }
    public void setgLn(Double gLn) {
        this.gLn = gLn;
    }
    public Long getDt() {
        return dt;
    }
    public void setDt(Long dt) {
        this.dt = dt;
    }
    public String getSt() {
        return st;
    }
    public void setSt(String st) {
        this.st = st;
    }
    public String getCd() {
        return cd;
    }
    public void setCd(String cd) {
        this.cd = cd;
    }
	public String getLacc() {
		return lacc;
	}
	public void setLacc(String lacc) {
		this.lacc = lacc;
	}
/*	public String getLpd() {
		return lpd;
	}
	public void setLpd(String lpd) {
		this.lpd = lpd;
	}*/
	public String getE() {
		return e;
	}
	public void setE(String env) {
		e = env;
	}
    public String getEp() {
        return ep;
    }
    public void setEp(String ep) {
        this.ep = ep;
    }
	public String getEt() {
		return et;
	}
	public void setEt(String et) {
		this.et = et;
	}
	
    public String getCe() {
        return ce;
    }
    public void setCe(String ce) {
        this.ce = ce;
    }
}
