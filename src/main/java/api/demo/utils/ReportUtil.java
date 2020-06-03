package api.demo.utils;

import org.testng.Reporter;

import api.demo.utils.ReportUtil;
import api.demo.utils.StringUtil;

public class ReportUtil {
	private static String reportName = "自动化测试报告";
	public static void log(String msg) {
		Reporter.log(msg, true);
	}

	public static String getReportName() {
		return reportName;
	}

	public static void setReportName(String reportName) {
		if(StringUtil.isNotEmpty(reportName)){
			ReportUtil.reportName = reportName;
		}
	}
}

