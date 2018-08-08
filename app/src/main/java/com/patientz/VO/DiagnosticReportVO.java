package com.patientz.VO;

public class DiagnosticReportVO {

	long diagnosticReportId;
	String reportFileIds;
	String testName;
	String firstName;
	String lastName;

	public long getDiagnosticReportId() {
		return diagnosticReportId;
	}

	public void setDiagnosticReportId(long diagnosticReportId) {
		this.diagnosticReportId = diagnosticReportId;
	}

	public String getReportFileIds() {
		return reportFileIds;
	}

	public void setReportFileIds(String reportFileIds) {
		this.reportFileIds = reportFileIds;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
}
