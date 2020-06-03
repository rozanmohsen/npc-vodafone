
package com.asset.vodafone.npc.core.utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class BusinessWorkTime {

	public BusinessWorkTime(int startingHour, int endingHour, int startingMinute, int endingMinute) {
		calendar = Calendar.getInstance();
		days = new ArrayList<>();
		officialDays = new ArrayList<>();
		this.startingHour = startingHour;
		this.endingHour = endingHour;
		this.startingMinute = startingMinute;
		this.endingMinute = endingMinute;
		init();
	}
/**
 * MEthod to read Business time parameters from properties file 
 * @param resourceBundle
 */
	public BusinessWorkTime(ResourceBundle resourceBundle) {
		calendar = Calendar.getInstance();
		days = new ArrayList<>();
		officialDays = new ArrayList<>();
		startingHour = Integer.parseInt(resourceBundle.getString("BUSINESS_HOUR_BEGIN"));
		endingHour = Integer.parseInt(resourceBundle.getString("BUSINESS_HOUR_END"));
		startingMinute = Integer.parseInt(resourceBundle.getString("BUSINESS_MINUTE_BEGIN"));
		endingMinute = Integer.parseInt(resourceBundle.getString("BUSINESS_MINUTE_END"));
		this.resourceBundle = resourceBundle;
		init();
		String daysToken = resourceBundle.getString("BUSINESS_OFF_DAYS") + ",";
		String[] tokens = daysToken.split("\\s*,\\s*");
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(new Locale("en"));
		String[] dayNames = dateFormatSymbols.getShortWeekdays();
		for (int i = 0; i < dayNames.length; i++) {
			for (int j = 0; j < tokens.length; j++) {
				String day = tokens[j];
				if (dayNames[i].equalsIgnoreCase(day))
					days.add(i);
			}

		}
		String businessOfficialVacation=System.getenv("BUSINESS_OFFICIAL_VACATION");
		if (businessOfficialVacation==null)
			businessOfficialVacation=resourceBundle.getString("BUSINESS_OFFICIAL_VACATION");
		String officialDaysToken = businessOfficialVacation + ",";
		tokens = officialDaysToken.split("\\s*,\\s*");
		for (int i = 0; i < tokens.length; i++)
			officialDays.add(tokens[i].trim());

	}

	private void init() {
		if (endingHour < startingHour)
			throw new RuntimeException("Ending Hour cannot less than Starting Hour");
		if (endingHour == startingHour && endingMinute <= startingMinute) {
			throw new RuntimeException("Ending Minute cannot less than or equal Starting Minute");
		} else {
			duration =(long) (60 * (endingHour - startingHour));
			noOfMinutes = duration + (long) (endingMinute - startingMinute);
			setCurrentTime(calendar.get(11), calendar.get(12));
			
		}
	}

	private void setCurrentTime(int hours, int minutes) {
		if (hours < startingHour || hours == startingHour && minutes < startingMinute) {
			currentTime = 1L;
			
		}
		if (hours > endingHour || hours == endingHour && minutes > endingMinute) {
			currentTime = noOfMinutes;
		
		} else {
			currentTime = (hours - startingHour) * 60L + minutes;
			
		}
	}

	public void setCurrentTime(Calendar calendar) {
		this.calendar = calendar;
		setCurrentTime(calendar.get(11), calendar.get(12));
	}

	public void setCurrentTime(Date date) {
		calendar.setTime(date);
		setCurrentTime(calendar);
	}

	public void addBusinessDays(long daySpan) {
		currentTime += daySpan * duration;
	}

	public void addBusinessHours(long hourSpan) {
		currentTime += hourSpan * 60L;
	}

	public void addBusinessMinutes(long minuteSpan) {
		currentTime += minuteSpan;
	}

	public void addBusinessTime(short timeUnit, int timeSpan) {
		switch (timeUnit) {
		case 0: 
			addBusinessDays(timeSpan);
			return;

		case 1: 
			addBusinessHours(timeSpan);
			return;

		case 2: 
			addBusinessMinutes(timeSpan);
			return;
			
		default: 
			return ;
		}
	}

	public void addCalendarTime(short timeUnit, int timeSpan) {
		switch (timeUnit) {
		case 0: 
			calendar.add(5, timeSpan);
			return;

		case 1: 
			calendar.add(11, timeSpan);
			return;

		case 2: 
			calendar.add(12, timeSpan);
			return;
		default: 
			return ;
		}
	}

	public Date getBusinessTime() {
		int noOfDays = (int) (currentTime / duration);
		int noOfHours = (int) ((currentTime % duration) / 60L);
		int minutesNo = (int) (currentTime % duration % 60L);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
		int totalNoOfDays = noOfDays;
		for (int i = 1; i <= totalNoOfDays; i++) {
			if ((officialDays.indexOf(sdf.format(calendar.getTime())) != -1
					|| days.indexOf(calendar.get(7)) != -1) && i == 1) {
				int currentHour = calendar.get(11);
				if (currentHour >= startingHour && currentHour < endingHour) {
					noOfHours = 0;
					minutesNo = 0;
					totalNoOfDays++;
				}
			}
			calendar.add(5, 1);
			if (officialDays.indexOf(sdf.format(calendar.getTime())) != -1) {
				calendar.set(11, startingHour);
				totalNoOfDays++;
			} else if (days.indexOf(calendar.get(7)) != -1)
						totalNoOfDays++;
		}

		calendar.set(11, startingHour + noOfHours);
		calendar.set(12, startingMinute + minutesNo);
		return calendar.getTime();
	}

	public Date getCalendarTime() {
		return calendar.getTime();
	}

	

	int startingHour;
	int endingHour;
	int startingMinute;
	int endingMinute;
	long noOfMinutes;
	long currentTime;
	long duration;
	Calendar calendar;
	ResourceBundle resourceBundle;
	public static final short DAYS = 0;
	public static final short HOURS = 1;
	public static final short MINUTES = 2;
	public static final short NO_OF_DAYS_OF_WEAK = 7;
	ArrayList<Integer> days;
	ArrayList<String> officialDays;
}
