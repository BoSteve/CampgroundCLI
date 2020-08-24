package com.techelevator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;

import com.techelevator.CLI.CampgroundCLI;
import com.techelevator.parks.model.ReservationDAO;
import com.techelevator.parks.model.jdbc.JDBCReseravtionDAO;

public class CLITests {
	static BasicDataSource dataSource = new BasicDataSource();
	CampgroundCLI cc = new CampgroundCLI(dataSource);
	
	Map<String, String> monthMap = new HashMap<String, String>() {
		{
			put("01", "Jan");
			put("02", "Feb");
			put("03", "Mar");
			put("04", "Apr");
			put("05", "May");
			put("06", "Jun");
			put("07", "Jul");
			put("08", "Aug");
			put("09", "Sep");
			put("10", "Oct");
			put("11", "Nov");
			put("12", "Dec");
		}
	};

	@Test
	public void printMonthNameTest() {
		String actual = cc.printMonthName("01");
		String expected = "Jan";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("02");
		expected = "Feb";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("03");
		expected = "Mar";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("04");
		expected = "Apr";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("05");
		expected = "May";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("06");
		expected = "Jun";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("07");
		expected = "Jul";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("08");
		expected = "Aug";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("09");
		expected = "Sep";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("10");
		expected = "Oct";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("11");
		expected = "Nov";
		Assert.assertEquals(expected, actual);
		
		actual = cc.printMonthName("12");
		expected = "Dec";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void printCommaFormatTest() {
	String actual = cc.printCommaFormat(Long.parseLong("10000"));
	String expected = "10,000";
	Assert.assertEquals(expected, actual);
	
	actual = cc.printCommaFormat(Long.parseLong("598700"));
	expected = "598,700";
	Assert.assertEquals(expected, actual);
	
	actual = cc.printCommaFormat(Long.parseLong("700"));
	expected = "700";
	Assert.assertEquals(expected, actual);
	
	actual = cc.printCommaFormat(Long.parseLong("987654321"));
	expected = "987,654,321";
	Assert.assertEquals(expected, actual);
	
	actual = cc.printCommaFormat(Long.parseLong("7654321"));
	expected = "7,654,321";
	Assert.assertEquals(expected, actual);
	
	actual = cc.printCommaFormat(Long.parseLong("9000"));
	expected = "9,000";
	Assert.assertEquals(expected, actual);
	} 
	
	@Test
	public void printYearMonthNameTest() {
		LocalDate holder = LocalDate.of(2020, 12, 24);
		String actual = cc.printYearMonthName(holder);
		String expected = "Dec 24 2020";
		Assert.assertEquals(expected, actual);
		
		holder = LocalDate.of(1776, 07, 04);
		actual = cc.printYearMonthName(holder);
		expected = "Jul 04 1776";
		Assert.assertEquals(expected, actual);
		
		holder = LocalDate.of(1969, 07, 20);
		actual = cc.printYearMonthName(holder);
		expected = "Jul 20 1969";
		Assert.assertEquals(expected, actual); 
	}
	
	@Test
	public void inverseDateCheckTest() {
		String arr = "2020/08/02";
		String dep = "2020/08/03";
		boolean actual = cc.inverseDateCheck(dep, arr);
		boolean expected = true;
		Assert.assertEquals(expected, actual); 
	}
}
