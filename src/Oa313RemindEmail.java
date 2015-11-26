import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jcx.db.talk;
import jcx.net.smtp;
import jcx.util.convert;

import org.json.JSONException;
import com.ysk.util.DateTimeUtil;

public class Oa313RemindEmail extends TaskService {

	public Oa313RemindEmail(String[] args) {

	}

	public static void main(String[] args) throws SQLException, Throwable {
		Oa313RemindEmail task = new Oa313RemindEmail(args);
		task.executeTask(args);

	}

	@Override
	protected void executeTask(String[] args) {
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

			cal.setTime(pDate);
			cal.add(Calendar.DATE, 14); // 日期加14天
			String sample_point1 = df.format(cal.getTime()); // 日期格式轉String
			// 當日期剩餘14天且抽樣點沒有填寫完成日期時
			String sql = "select PNO,SAMPLE_POINT from STABLEDETAIL where SAMPLE_POINT='"
					+ sample_point1 + "' and CONFIRM=''";
			String[][] date = t.queryFromPool(sql);
			String PNO = "";
			String SAMPLE_POINT = "";
			String EMPID = "";
			String PRODUCT_NAME = "";
			String LOT_NUMBER = "";
			String STANDARD = "";
			String LOCATION = "";
			String HURRY_PEOPLE = "";

			/**
			 * 第一次跟催 當距離抽樣點日期剩14天時
			 **/
			for (int i = 0; i < date.length; i++) {
				PNO = date[i][0];
				SAMPLE_POINT = date[i][1];
				// 取得相關資料
				String hurry = "select EMPID,PRODUCT_NAME,LOT_NUMBER,STANDARD,LOCATION,HURRY_PEOPLE from STABLEREMIND where PNO='"
						+ PNO + "'";
				String remind_data[][] = t.queryFromPool(hurry);
				EMPID = remind_data[0][0];
				PRODUCT_NAME = remind_data[0][1];
				LOT_NUMBER = remind_data[0][2];
				STANDARD = remind_data[0][3];
				LOCATION = remind_data[0][4];
				HURRY_PEOPLE = remind_data[0][5];
				Vector vc1 = new Vector();
				String[] em1 = null;
				String Title = sample_point1 + "抽樣點剩餘14天通知(測試信件請勿回覆)";
				String Content1 = "您好：" + "\r\n" + "您所負責跟催的" + "\r\n";
				Content1 += "單號:" + PNO + "\r\n";
				Content1 += "品名：" + PRODUCT_NAME + "\r\n";
				Content1 += "批號：" + LOT_NUMBER + "\r\n";
				Content1 += "規格：" + STANDARD + "\r\n";
				Content1 += "位置：" + LOCATION + "\r\n";
				Content1 += "距離抽樣點還剩餘14天 請盡早去填寫完成日期";

//               //取得跟催人員的mail
//				 String sql_hurry="select EMAIL from HRUSER where EMPID='"+HURRY_PEOPLE+"'";
//				 String hurry_mail[][] = t.queryFromPool(sql_hurry);
//				 vc1.addElement(hurry_mail[0][0]); //通知跟催人員
				vc1.addElement("b0052@ysp.local");
				em1 = (String[]) vc1.toArray(new String[0]);
				String sendRS = smtp.sendMailbccUTF8(mailHost, from, em1,
						Title, Content1, null, "", "text/plain");
			}
			/**
			 * 第二次跟催 當距離抽樣點日期剩7天時
			 * */
			cal.setTime(pDate);
			cal.add(Calendar.DATE, 7); // 日期加7天
			String sample_point2 = df.format(cal.getTime());

			// 當日期剩餘7天且抽樣點沒有填寫完成日期時
			String sql2 = "select PNO,SAMPLE_POINT from STABLEDETAIL where SAMPLE_POINT='"
					+ sample_point2 + "' and CONFIRM=''";
			String[][] date2 = t.queryFromPool(sql2);

			for (int i = 0; i < date2.length; i++) {
				PNO = date2[i][0];
				SAMPLE_POINT = date2[i][1];
				// 取得相關資料
				String hurry = "select EMPID,PRODUCT_NAME,LOT_NUMBER,STANDARD,LOCATION,HURRY_PEOPLE from STABLEREMIND where PNO='"
						+ PNO + "'";
				String remind_data[][] = t.queryFromPool(hurry);
				EMPID = remind_data[0][0];
				PRODUCT_NAME = remind_data[0][1];
				LOT_NUMBER = remind_data[0][2];
				STANDARD = remind_data[0][3];
				LOCATION = remind_data[0][4];
				HURRY_PEOPLE = remind_data[0][5];

				Vector vc1 = new Vector();
				String[] em1 = null;
				String Title = sample_point2 + "抽樣點剩餘7天通知(測試信件請勿回覆)";
				String Content1 = "您好：" + "\r\n" + "您所負責跟催的" + "\r\n";
				Content1 += "單號:" + PNO + "\r\n";
				Content1 += "品名：" + PRODUCT_NAME + "\r\n";
				Content1 += "批號：" + LOT_NUMBER + "\r\n";
				Content1 += "規格：" + STANDARD + "\r\n";
				Content1 += "位置：" + LOCATION + "\r\n";
				Content1 += "距離抽樣點還剩餘7天 請盡早去填寫完成日期";

//				 //取得跟催人員的mail
//				 String sql_hurry="select EMAIL from HRUSER where EMPID='"+HURRY_PEOPLE+"'";
//				 String hurry_mail[][] = t.queryFromPool(sql_hurry);
//				 vc1.addElement(hurry_mail[0][0]); //通知跟催人員
//				 //取得起單人的課主管
//				 String sql_dept="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//				 String dept[][] = t.queryFromPool(sql_dept);
//				 String sql_Master="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//				 String Master[][] = t.queryFromPool(sql_Master);
//				 vc1.addElement(Master[0][0]+"@ysp.local");
				vc1.addElement("b0052@ysp.local");
				em1 = (String[]) vc1.toArray(new String[0]);
				String sendRS = smtp.sendMailbccUTF8(mailHost, from, em1,
						Title, Content1, null, "", "text/plain");
			}
			/**
			 * 最後一次跟催 當今天日期等於抽樣點日期
			 * **/
			cal.setTime(pDate);
			String sample_point3 = df.format(cal.getTime());

			// 當日期等於抽樣點當天且抽樣點沒有填寫完成日期時
			String sql3 = "select PNO,SAMPLE_POINT from STABLEDETAIL where SAMPLE_POINT='"
					+ sample_point3 + "' and CONFIRM=''";
			String[][] date3 = t.queryFromPool(sql3);

			for (int i = 0; i < date3.length; i++) {
				PNO = date3[i][0];
				SAMPLE_POINT = date3[i][1];
				// 取得相關資料
				String hurry = "select EMPID,PRODUCT_NAME,LOT_NUMBER,STANDARD,LOCATION,HURRY_PEOPLE from STABLEREMIND where PNO='"
						+ PNO + "'";
				String remind_data[][] = t.queryFromPool(hurry);
				EMPID = remind_data[0][0];
				PRODUCT_NAME = remind_data[0][1];
				LOT_NUMBER = remind_data[0][2];
				STANDARD = remind_data[0][3];
				LOCATION = remind_data[0][4];
				HURRY_PEOPLE = remind_data[0][5];

				Vector vc1 = new Vector();
				String[] em1 = null;
				String Title = sample_point3 + "抽樣點即將逾期通知(測試信件請勿回覆)";
				String Content1 = "您好：" + "\r\n" + "您所負責跟催的" + "\r\n";
				Content1 += "單號:" + PNO + "\r\n";
				Content1 += "品名：" + PRODUCT_NAME + "\r\n";
				Content1 += "批號：" + LOT_NUMBER + "\r\n";
				Content1 += "規格：" + STANDARD + "\r\n";
				Content1 += "位置：" + LOCATION + "\r\n";
				Content1 += "距離抽樣點即將逾期 請盡早去填寫完成日期";

//				 //取得跟催人員的mail
//				 String sql_hurry="select EMAIL from HRUSER where EMPID='"+HURRY_PEOPLE+"'";
//				 String hurry_mail[][] = t.queryFromPool(sql_hurry);
//				 vc1.addElement(hurry_mail[0][0]); //通知跟催人員
//				 //取得起單人的課主管
//				 String sql_dept="select dept_no from HRUSER where EMPID='"+EMPID+"'";
//				 String dept[][] = t.queryFromPool(sql_dept);
//				 String sql_Master="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+dept[0][0]+"'";
//				 String Master[][] = t.queryFromPool(sql_Master);
//				 vc1.addElement(Master[0][0]+"@ysp.local");
//				 //取得起單人的處主管
//				 String sql_parent_dept="select parent_no from HRUSER_DEPT_BAS where DEP_CHIEF='"+Master[0][0]+"'";
//				 String parent_dept[][] = t.queryFromPool(sql_parent_dept);
//				 String sql_parent_Master="select DEP_CHIEF from HRUSER_DEPT_BAS where dep_no='"+parent_dept[0][0]+"'";
//				 String parent_Master[][] = t.queryFromPool(sql_parent_Master);
//				 vc1.addElement(parent_Master[0][0]+"@ysp.local");
				 vc1.addElement("b0052@ysp.local");
				em1 = (String[]) vc1.toArray(new String[0]);
				String sendRS = smtp.sendMailbccUTF8(mailHost, from, em1,
						Title, Content1, null, "", "text/plain");
			}

		} catch (Exception e) {
			System.out.println("oa313" + e);
		}
	}
}
