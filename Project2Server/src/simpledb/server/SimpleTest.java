package simpledb.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import simpledb.file.FileMgr;
import simpledb.file.Page;

public class SimpleTest {

	public static void main(String[] args) {
		SimpleDB.initFileMgr("studentdb");
		FileMgr fm = SimpleDB.fileMgr();

		Page p1 = new Page();
		p1.setString(16, "Michael_in_spaaaaaace!");
		p1.setString(64, "Wheatly");
		p1.setString(32, "potados");
		String test_string = p1.getString(64);
		System.out.println("test string ="+test_string);
		
		p1.setShort(16, (short) 69);
		p1.setShort(64, (short) 42);
		p1.setShort(32, (short) 88);
		Short test_short = p1.getShort(64);
		System.out.println("test short ="+test_short);

		p1.setBoolean(16, true);
		p1.setBoolean(64, false);
		p1.setBoolean(32, true);
		boolean test_boolean = p1.getBoolean(64);
		System.out.println("test boolean ="+test_boolean);

		p1.setBytes(16, "Michael_in_spaaaaaace!".getBytes());
		p1.setBytes(64, "Wheatly".getBytes());
		p1.setBytes(32, "potados".getBytes());
		byte[] test_bytes = p1.getBytes(64);
		System.out.println("test string ="+new String(test_bytes));

		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		GregorianCalendar calendar = new GregorianCalendar();
		
		p1.setDate(16, new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -3);
		p1.setDate(128,  calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 5);
		p1.setDate(64,  calendar.getTime());
		Date test_date = p1.getDate(128);
		System.out.println("test date ="+dateFormat.format(test_date));

	}

}
