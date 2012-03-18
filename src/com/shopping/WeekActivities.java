package com.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class WeekActivities {
	class UserActivity {
		User user;
		int counter;
	}

	// the hash map connect a day of the week (expressed by the first Integer in the map) and a list of User+counter
	private Map<Integer, List<UserActivity>> mymap = new HashMap<Integer, List<UserActivity>>(); 

	// retrieve the number of activities per type of actor
	public int countByDayAndType(Integer day, String type) {
		int counter = 0;

		if (mymap.get(day) != null) {
			List<UserActivity> list = mymap.get(day);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null) {
					if (list.get(i).user.getType().equals(type)) {
						counter += list.get(i).counter;
					}
				}
			}
		}
		return counter;
	}

	public int countByDayAndUser(Integer day, User user) {
		int counter = 0;
		if (mymap.get(day) != null) {
			List<UserActivity> list = mymap.get(day);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) != null) {
					if (list.get(i).user.equals(user)) {
						counter = list.get(i).counter;
						break;
					}
				}
			}
		}
		return counter;
	}

	public int countByUser(User user) {
		int counter = 0;
		for (Map.Entry<Integer, List<UserActivity>> element : mymap.entrySet()) {
			for (UserActivity activities : element.getValue()) {
				if (activities != null) {
					if (activities.user.equals(user)) {
						counter += activities.counter;
					}
				}
			}
		}
		return counter;
	}

	public int countByType(String type) {
		int counter = 0;
		for (Map.Entry<Integer, List<UserActivity>> list : mymap.entrySet()) {
			for (UserActivity activity : list.getValue()) {
				if (activity != null && activity.user!=null) {
					if (activity.user.getType().equals(type)) {
						counter += activity.counter;
					}
				}
			}
		}
		return counter;
	}

	public void addActivityPerDayByUser(Integer day, User user) {
		
		if (mymap.get(day) != null) {
			List<UserActivity> list = mymap.get(day);
			
			if (isUserInList(list,user)){
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).user.equals("user"))
						list.get(i).counter += 1;
				}
			}else{
				UserActivity activity = new UserActivity();
				activity.counter = 1;
				activity.user = user;
				list.add(activity);
			}
		}else {
			UserActivity activity = new UserActivity();
			activity.counter = 1;
			activity.user = user; 
			List<UserActivity> activities = new ArrayList<UserActivity>();
			activities.add(activity);
			mymap.put(day, activities);
		}
	}

	private boolean isUserInList(List<UserActivity> list, User user) {
		
		for (int i=0; i<list.size();i++){
			if (list.get(i).user==user){
				return true;
			}
		}
		return false;
	}
	
	public void print(){
		for (Integer key : mymap.keySet()) {
			Log.d("WEEKACTIVITY","day: "+key);
			for (UserActivity useractivity : mymap.get(key)) {
				Log.d("WEEKACTIVITY","element: "+useractivity.user.getFirstName() + " with value: "+useractivity.counter);
			}
		}
		
	}
}
