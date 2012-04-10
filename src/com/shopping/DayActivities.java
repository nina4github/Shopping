package com.shopping;

import java.util.Map;

public class DayActivities {

	private int day;
	private Map<User, Integer> dayActivities;

	public DayActivities(int d) {
		setDay(d);
		setDayActivities(null);
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	public void setDayActivities(Map<User, Integer> userActivities) {
		this.dayActivities = userActivities;
	}

	public Map<User, Integer> getDayActivities() {
		return dayActivities;
	}

	public int getDayActivitiesByUser(User user) {
		return dayActivities.get(user);
	}

	public boolean setDayActivitiesByUser(User user, Integer value) {
		return dayActivities.put(user, value) != null;
	}

	public void addDayActivitiesToUser(User user) {

		dayActivities.put(user, dayActivities.get(user) != null ? 1
				: dayActivities.get(user) + 1);

	}

}
