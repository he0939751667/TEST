import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jcx.db.talk;
import jcx.net.smtp;

import com.ysk.util.DateTimeUtil;
import com.ysp.service.TaskService;

public class Oa314SendEmail extends TaskService {

	public Oa314SendEmail(String[] args) {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		Oa314SendEmail task = new Oa314SendEmail(args);
		task.executeTask(args);
	}

	@Override
	protected void executeTask(String[] arg0) {
		// TODO Auto-generated method stub
		try {
			talk t = TaskTalk1.getTalk();
			String mailHost = "10.1.1.60,25,ehr,ehr123";
			String from = "ehr@ysp.local";
			Calendar cal = Calendar.getInstance();
			// 取得今天日期
			String today = DateTimeUtil.getToday();
			// 由字串日期 轉成date
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date pDate = new Date();
			pDate = df.parse(today);
			// 取得狀態別為正常使用的單號
			String sql = "select PNO,EMPID from FQ_INSTRUMENT_REMIND where STATUS='1'";
			String ret[][] = t.queryFromPool(sql);
			for (int i = 0; i < ret.length; i++) {
				// 取得主檔單號的維護單號和維護類別
				String EMPID = ret[i][1];
				String maintain_sql = "select PNO,MAINTAIN_TYPE,NEXT_MA_DATE,INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where MPNO='"
						+ ret[i][0] + "'";
				String ret1[][] = t.queryFromPool(maintain_sql);
				for (int j = 0; j < ret1.length; j++) {
					String PNO1 = ret1[j][0];
					String TYPE1 = ret1[j][1];
					String INS_NO1 = ret1[j][3];
					String MAINTAIN_ITEM1 = ret1[j][4];
					String MAINTAIN_PERSON1 = ret1[j][5];
					Date mDate = new Date();
					mDate = df.parse(ret1[j][2]);
					// 維護日期小於等於今天日期，則每天通知
					if (mDate.compareTo(pDate) <= 0) {
						/**
						 * 每日提醒
						 */
						Vector vc1 = new Vector();
						String[] em1 = null;
						String Title = "維護日期逾期通知(測試信件請勿回覆)";
						String Content1 = "您好：" + "\r\n" + "您所負責維護的"
								+ "\r\n";
						Content1 += "單號:" + PNO1 + "\r\n";
						Content1 += "儀器編號：" + INS_NO1 + "\r\n";
						Content1 += "維護類別：" + TYPE1 + "\r\n";
						Content1 += "維護項目：" + MAINTAIN_ITEM1 + "\r\n";
						Content1 += "維護人員：" + MAINTAIN_PERSON1 + "\r\n";
						Content1 += "維護日期逾期通知";

						//取得維護人員的mail
//						String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON1+"'";
//						String maintain_mail[][] = t.queryFromPool(sql_maintain);
//						vc1.addElement(maintain_mail[0][0]); //通知維護人員
						//取得起單人的課主管
//						String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//						String dept[][] = t.queryFromPool(sql_dept);
//						String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//						String Master[][] = t.queryFromPool(sql_Master);
//						vc1.addElement(Master[0][0]+"@ysp.local");
						vc1.addElement("b0052@ysp.local");
						em1 = (String[]) vc1.toArray(new String[0]);
						String sendRS = smtp.sendMailbccUTF8(mailHost,
								from, em1, Title, Content1, null, "",
								"text/plain");
					} else {
						String PNO = ret1[j][0];
						String TYPE = ret1[j][1];
						// 判斷維護類別各別通知的時間
						if (TYPE.equals("A")) {
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 30); // 日期加30天
							String maintain_pointA1 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘30天
							String sql1 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointA1
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data1 = t.queryFromPool(sql1);
							for (int k = 0; k < data1.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data1[k][0];
								String MAINTAIN_TYPE = "4Q";
								String MAINTAIN_ITEM = data1[k][1];
								String MAINTAIN_PERSON = data1[k][2];
								/**
								 * 第一次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointA1
										+ "維護日期剩餘30天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘30天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 15); // 日期加15天
							String maintain_pointA2 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘15天
							String sql2 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointA2
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data2 = t.queryFromPool(sql2);
							for (int k = 0; k < data2.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data2[k][0];
								String MAINTAIN_TYPE = "4Q";
								String MAINTAIN_ITEM = data2[k][1];
								String MAINTAIN_PERSON = data2[k][2];
								/**
								 * 第二次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointA2
										+ "維護日期剩餘15天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘15天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
						} else if (TYPE.equals("B")) {
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 7); // 日期加7天
							String maintain_pointB1 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘7天
							String sql1 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointB1
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data1 = t.queryFromPool(sql1);
							for (int k = 0; k < data1.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data1[k][0];
								String MAINTAIN_TYPE = "校正";
								String MAINTAIN_ITEM = data1[k][1];
								String MAINTAIN_PERSON = data1[k][2];
								/**
								 * 第一次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointB1
										+ "維護日期剩餘7天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘7天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 3); // 日期加3天
							String maintain_pointB2 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘3天
							String sql2 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointB2
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data2 = t.queryFromPool(sql2);
							for (int k = 0; k < data2.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data2[k][0];
								String MAINTAIN_TYPE = "校正";
								String MAINTAIN_ITEM = data2[k][1];
								String MAINTAIN_PERSON = data2[k][2];
								/**
								 * 第二次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointB2
										+ "維護日期剩餘3天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘3天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
						} else {
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 7); // 日期加7天
							String maintain_pointC1 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘7天
							String sql1 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointC1
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data1 = t.queryFromPool(sql1);
							for (int k = 0; k < data1.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data1[k][0];
								String MAINTAIN_TYPE = "二級保養";
								String MAINTAIN_ITEM = data1[k][1];
								String MAINTAIN_PERSON = data1[k][2];
								/**
								 * 第一次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointC1
										+ "維護日期剩餘7天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘7天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
							cal.setTime(pDate);
							cal.add(Calendar.DATE, 3); // 日期加3天
							String maintain_pointC2 = df.format(cal.getTime()); // 日期格式轉String

							// 當日期剩餘3天
							String sql2 = "select INS_NO,MAINTAIN_ITEM,MAINTAIN_PERSON from FQ_INSTRUMENT_DETAIL where NEXT_MA_DATE='"
									+ maintain_pointC2
									+ "' and PNO='"
									+ PNO
									+ "'";
							String[][] data2 = t.queryFromPool(sql2);
							for (int k = 0; k < data2.length; k++) {
								String MAINTAIN_PNO = PNO;
								String INS_NO = data2[k][0];
								String MAINTAIN_TYPE = "二級保養";
								String MAINTAIN_ITEM = data2[k][1];
								String MAINTAIN_PERSON = data2[k][2];
								/**
								 * 第二次提醒
								 */
								Vector vc1 = new Vector();
								String[] em1 = null;
								String Title = maintain_pointC2
										+ "維護日期剩餘3天通知(測試信件請勿回覆)";
								String Content1 = "您好：" + "\r\n" + "您所負責維護的"
										+ "\r\n";
								Content1 += "單號:" + PNO + "\r\n";
								Content1 += "儀器編號：" + INS_NO + "\r\n";
								Content1 += "維護類別：" + MAINTAIN_TYPE + "\r\n";
								Content1 += "維護項目：" + MAINTAIN_ITEM + "\r\n";
								Content1 += "維護人員：" + MAINTAIN_PERSON + "\r\n";
								Content1 += "距離維護日期剩餘3天";

								//取得維護人員的mail
//								String sql_maintain="select EMAIL from HRUSER where EMPID='"+MAINTAIN_PERSON+"'";
//								String maintain_mail[][] = t.queryFromPool(sql_maintain);
//								vc1.addElement(maintain_mail[0][0]); //通知維護人員
								//取得起單人的課主管
//								String sql_dept ="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//								String dept[][] = t.queryFromPool(sql_dept);
//								String sql_Master ="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//								String Master[][] = t.queryFromPool(sql_Master);
//								vc1.addElement(Master[0][0]+"@ysp.local");
								vc1.addElement("b0052@ysp.local");
								em1 = (String[]) vc1.toArray(new String[0]);
								String sendRS = smtp.sendMailbccUTF8(mailHost,
										from, em1, Title, Content1, null, "",
										"text/plain");
							}
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("oa314" + e);
		}
	}

}
