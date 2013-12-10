package simpledb.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.file.FileMgr;
import simpledb.file.Page;

public class SimpleTask2Test {

	public static void main(String[] args) {
		SimpleDB.initFileLogAndBufferMgr("studentdb");
		BufferMgr bm = SimpleDB.bufferMgr();

		Block A = new Block("a.txt",1);
		Block B = new Block("b.txt",2);
		Block C = new Block("c.txt",3);
		Block D = new Block("d.txt",4);
		Block E = new Block("e.txt",5);
		Block F = new Block("f.txt",6);
		
		Buffer p;
		try {
			System.out.println(System.currentTimeMillis());
			Thread.sleep(1000);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(B);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(C);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(D);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(B);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(B);
			bm.unpin(p);
			Thread.sleep(1000);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000*5);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000*5);
			p=bm.pin(B);
			bm.unpin(p);
			Thread.sleep(1000*10);
			p=bm.pin(A);
			bm.unpin(p);
			Thread.sleep(1000*1);
			p=bm.pin(E);
			bm.unpin(p);
			Thread.sleep(1000*1);
			p=bm.pin(D);
			bm.unpin(p);
			Thread.sleep(1000*11);
			p=bm.pin(E);
			bm.unpin(p);
			Thread.sleep(1000*11);
			p=bm.pin(F);
			bm.unpin(p);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}




	}

}
