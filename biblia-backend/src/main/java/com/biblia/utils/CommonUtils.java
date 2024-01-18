
package com.biblia.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtils {
	static final String RANDOM_STR = "0123456789abcdefghijklmnopqrstuvwxyz";
	static final String RANDOM_NUMBER = "0123456789";

	public static String generateRandomCode(int len) {
		Random rand = new Random();
		StringBuffer sb = new StringBuffer(len);
		char str;
		boolean checkOneNumber = false;
		for (int i = 0; i < len; i++) {
			str = RANDOM_STR.charAt(rand.nextInt(RANDOM_STR.length()));
			if (StringUtils.isNumeric(str + "")) {
				checkOneNumber = true;
			}
			sb.append(str);
		}

		// set one up case character
		int position1 = rand.nextInt(len);
		char str_check = sb.charAt(position1);
		while (StringUtils.isNumeric(str_check + "")) {
			position1 = rand.nextInt(len);
			str_check = sb.charAt(position1);
		}

		sb.setCharAt(position1, Character.toUpperCase(str_check));

		// check at least one number
		if (!checkOneNumber) {
			int position3 = rand.nextInt(len);
			while (position3 == position1) {
				position3 = rand.nextInt(len);
			}
			sb.setCharAt(position3,
					RANDOM_NUMBER.charAt(rand.nextInt(RANDOM_NUMBER.length())));
		}

		return sb.toString();
	}
	public static String encriptMd5(String str){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
	        md.update(str.getBytes());

	        byte byteData[] = md.digest();

	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	public static String removeSpace(String text){
		if(!StringUtils.isEmpty(text)){
			text = text.trim();
			text = text.replaceAll("\\s{1,}", " ");
		}
		return text;
	}

	public static String convertToUTF8(String input){
		String output = "";
		try {
		    /* From ISO-8859-1 to UTF-8 */
		    output = new String(input.getBytes("ISO-8859-1"), "UTF-8");

		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		}
		return output;
	}

	public static long generateRandom(int length) {
	     Random random = new Random();
	     char[] digits = new char[length];
	     digits[0] = (char) (random.nextInt(9) + '1');
	     for (int i = 1; i < length; i++) {
	         digits[i] = (char) (random.nextInt(10) + '0');
	     }
	     return Long.parseLong(new String(digits));
	 }

	public static String generateBarcode(long numberRandom) {
		String output = "";
		String fistChar = "NBC";
		if(numberRandom > 0) {
			output = fistChar.concat(Long.toString(numberRandom));
		}
		return output;
	}

	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static String unAccent (String s) {
		        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "");
	}

	public static String convertVNString(String str) {
		try {
			if(!StringUtils.isEmpty(str)) {
				String result = str.toLowerCase();
				String str1 = "áàảãạăắằẳẵặâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵ";
			 	String str2 = "aaaaaaaaaaaaaaaaadeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyy";

			 	for(int i = 0; i < str1.length(); i++) {
			 		if(result.contains(str1.substring(i, i + 1)))
			 			result = result.replace(str1.substring(i, i + 1), str2.substring(i, i + 1));
			 	}

			 	result = result.replace(" ", "");
				return result;
			}else return "";
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String updateLicenseCd(String str) {
		try {
			if(!StringUtils.isEmpty(str)) {
				String result = str;
				result = result.replace("-", "");
				result = result.replace("/", "");
				return result;
			} return "";
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String converToMD5(String input) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.reset();
		messageDigest.update(input.getBytes(Charset.forName("UTF8")));
		byte[] resultByte = messageDigest.digest();
		String result = Hex.encodeHexString(resultByte);
		return result;
	}
	public static float distanceBetween(double startLatitude, double startLongitude, double endLatitude,
			double endLongitude) {
		float distance = computeDistanceAndBearing(startLatitude, startLongitude, endLatitude, endLongitude);
		return distance;
	}

	private static float computeDistanceAndBearing(double lat1, double lon1, double lat2, double lon2) {
		// Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
		// using the "Inverse Formula" (section 4)

		int MAXITERS = 20;
		// Convert lat/long to radians
		lat1 *= Math.PI / 180.0;
		lat2 *= Math.PI / 180.0;
		lon1 *= Math.PI / 180.0;
		lon2 *= Math.PI / 180.0;

		double a = 6378137.0; // WGS84 major axis
		double b = 6356752.3142; // WGS84 semi-major axis
		double f = (a - b) / a;
		double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

		double L = lon2 - lon1;
		double A = 0.0;
		double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
		double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

		double cosU1 = Math.cos(U1);
		double cosU2 = Math.cos(U2);
		double sinU1 = Math.sin(U1);
		double sinU2 = Math.sin(U2);
		double cosU1cosU2 = cosU1 * cosU2;
		double sinU1sinU2 = sinU1 * sinU2;

		double sigma = 0.0;
		double deltaSigma = 0.0;
		double cosSqAlpha = 0.0;
		double cos2SM = 0.0;
		double cosSigma = 0.0;
		double sinSigma = 0.0;
		double cosLambda = 0.0;
		double sinLambda = 0.0;

		double lambda = L; // initial guess
		for (int iter = 0; iter < MAXITERS; iter++) {
			double lambdaOrig = lambda;
			cosLambda = Math.cos(lambda);
			sinLambda = Math.sin(lambda);
			double t1 = cosU2 * sinLambda;
			double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
			double sinSqSigma = t1 * t1 + t2 * t2; // (14)
			sinSigma = Math.sqrt(sinSqSigma);
			cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
			sigma = Math.atan2(sinSigma, cosSigma); // (16)
			double sinAlpha = (sinSigma == 0) ? 0.0 : cosU1cosU2 * sinLambda / sinSigma; // (17)
			cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
			cos2SM = (cosSqAlpha == 0) ? 0.0 : cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

			double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
			A = 1 + (uSquared / 16384.0) * // (3)
					(4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared)));
			double B = (uSquared / 1024.0) * // (4)
					(256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));
			double C = (f / 16.0) * cosSqAlpha * (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
			double cos2SMSq = cos2SM * cos2SM;
			deltaSigma = B * sinSigma * // (6)
					(cos2SM + (B / 4.0) * (cosSigma * (-1.0 + 2.0 * cos2SMSq)
							- (B / 6.0) * cos2SM * (-3.0 + 4.0 * sinSigma * sinSigma) * (-3.0 + 4.0 * cos2SMSq)));

			lambda = L + (1.0 - C) * f * sinAlpha
					* (sigma + C * sinSigma * (cos2SM + C * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)

			double delta = (lambda - lambdaOrig) / lambda;
			if (Math.abs(delta) < 1.0e-12) {
				break;
			}
		}

		float distance = (float) (b * A * (sigma - deltaSigma));
		return distance;
	}

	public static String listToString(List<Integer> list) {
		StringBuilder strs = new StringBuilder();
		for (Integer i : list) {
			strs.append(i + ",");
		}
		if (StringUtils.isNotBlank(strs.toString())) {
			return strs.substring(0, strs.toString().length() - 1);
		}
		return "";
	}
	public static BigDecimal strToBigDecimal(String str){
		if(StringUtils.isBlank(str)){
			return null;
		}
		return new BigDecimal(str);
	}

	public static int compareStr(String number1, String number2) throws Exception {
		return Long.compare(Long.parseLong(number1), Long.parseLong(number2));
	}
}
