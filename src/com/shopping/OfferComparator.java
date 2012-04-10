package com.shopping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class OfferComparator implements Comparator<ShoppingOffer> {

	public int compare(ShoppingOffer lhs, ShoppingOffer rhs) {
		try {

			Date datelhs = new SimpleDateFormat("DD/MM-HH:mm",//"yyyy-MM-dd'T'HH:mm:ss",
					Locale.ENGLISH).parse(lhs.getPublished());
			Date daterhs = new SimpleDateFormat("DD/MM-HH:mm",//"yyyy-MM-dd'T'HH:mm:ss",
					Locale.ENGLISH).parse(rhs.getPublished());
			return daterhs.compareTo(datelhs); // more recent date first
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}