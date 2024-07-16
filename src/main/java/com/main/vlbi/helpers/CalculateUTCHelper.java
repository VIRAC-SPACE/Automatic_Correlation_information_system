package com.main.vlbi.helpers;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.text.DecimalFormat;


/**
 * 
 * @author Code of coordinate calculation is based on John Kielkopf java code
 * 
 */
/* ****************************************************************************/
/*                                                                            */
/* Celestial Coordinate Algorithms                                            */
/*                                                                            */
/* John Kielkopf                                                              */
/* kielkopf@louisville.edu                                                    */
/*                                                                            */
/* Converted to Java by Karen Collins December 22nd, 2011                     */
/* karen.collins@insightbb.com                                                */
/*                                                                            */
/* Distributed under the terms of the General Public License (see LICENSE)    */
/* ****************************************************************************/

public class CalculateUTCHelper {
	   /**
     * The mathematical constant PI.
     */
    public static final double PI = 3.14159265358979;

	public static double CalcLST(int year, int month, int day, double ut, double glong, double leapSecs)
	    {
	    double TU, TU2, TU3, T0;
	    double gmst,lmst, jdEOD;

	    jdEOD = CalcJD(year, month, day, 0.0);

	    TU = (jdEOD - 2451545.0) / 36525.0;  //TU =number of Julian centuries since 2000 January 1.5
	    TU2 = TU * TU;
	    TU3 = TU2 * TU;
	    T0 = 6.697374558 + 2400.0513369072 * TU + 2.58622E-5 * TU2 - 1.7222078704899681391543959355894E-9* TU3;
	    T0 = Map24(T0);

	    gmst = Map24(T0 + ut * 1.00273790935 + NLongitude(jdEOD, leapSecs) * cos(TrueObliquity(jdEOD, leapSecs)*PI/180.0) / 15);

	    lmst = 24.0 * frac((gmst + glong / 15.0) / 24.0);
	    return (lmst);
	    }
		
		/**
	     *  Compute the Julian Day for the given date.
	     *  Julian Date is the number of days since noon of Jan 1 4713 B.C.
	     * @param ny year
	     * @param nm month
	     * @param nd day
	     * @param ut UT time
	     * @return Julian Date
	     */
	    public static double CalcJD(int ny, int nm, int nd, double ut)
	    {
	    double A, B, C, D, jd, day;

	    day = nd + ut / 24.0;
	    if ((nm == 1) || (nm == 2))
	        {
	        ny = ny - 1;
	        nm = nm + 12;
	        }

	    if (((double)(ny + nm / 12.0 + day / 365.25)) >= (1582.0 + 10.0 / 12.0 + 15.0 / 365.25))
	        {
	        A = ((int) (ny / 100.0));
	        B = 2.0 - A + (int) (A / 4.0);
	        }
	    else
	        {
	        B = 0.0;
	        }

	    if (ny < 0.0)
	        {
	        C = (int) ((365.25 * (double) ny) - 0.75);
	        }
	    else
	        {
	        C = (int) (365.25 * (double) ny);
	        }

	    D = (int) (30.6001 * (double) (nm + 1));
	    jd = B + C + D + day + 1720994.5;
	    return (jd);
	    }
		
		
	/**
	 * Map a time in hours to the range  0  to 24.
	 * @param hour
	 * @return modified hour
	 */
	public static double Map24(double hour)
	    {
	    int n;
	    if (hour < 0.0)
	        {
	        n = (int) (hour / 24.0) - 1;
	        return (hour - n * 24.0);
	        }
	    else if (hour >= 24.0)
	        {
	        n = (int) (hour / 24.0);
	        return (hour - n * 24.0);
	        }
	    else
	        {
	        return (hour);
	        }
	    }


	/**
	 * Nutation of the longitude of the ecliptic for the EOD in degrees.
	 * @param jd Julian Date
	 * @param leapSecs Leap Seconds (TAI - UTC)
	 * @return Nutation of the longitude of the ecliptic in degrees
	 */
	public static double NLongitude(double jd, double leapSecs)
	    {
	      double dpsi, lsun, lmoon, omega;
	      double dt, t;

	      dt = leapSecs;
	      dt += 32.184;

	      /* Change units to centuries */

	      dt /= (36525 * 24. * 60. * 60.);

	      /* Julian centuries for the EOD from a base epoch 2000.0 */

	      t = (jd - 2451545.0) / 36525.0;

	      /* Correct for dt = tt - ut1  */

	      t += dt ;

	      /* Longitude of the ascending node of the Moon's mean orbit */

	      omega = Map360(125.04452 - 1934.136261*t + 0.0020708*t*t + t*t*t/450000.);

	      /* Mean longitude of the Moon */

	      lmoon = Map360(218.31654591 + 481267.88134236*t
	        - 0.00163*t*t + t*t*t/538841. - t*t*t*t/65194000.);

	      /* Mean longitude of the Sun */

	      lsun = LongitudeSun(jd, leapSecs);

	      /* Convert to radians */

	      omega = omega*PI/180.;
	      lsun = lsun*PI/180.;
	      lmoon = lmoon*PI/180.;

	      /* Nutation in longitude in seconds of arc for the EOD */

	      dpsi = -17.20*sin(omega) - 1.32*sin(2.*lsun) -
	        0.23*sin(2.*lmoon) + 0.21*sin(2.*omega);

	      /* Convert to degrees */

	      dpsi /= 3600.;

	      return (dpsi);
	    }
		
	/**
	 * True geometric solar longitude for the JD in degrees.
	 * @param jd Julian Date
	 * @param leapSecs Leap Seconds (TAI - UTC)
	 * @return True geometric solar longitude in degrees
	 */
	public static double LongitudeSun(double jd, double leapSecs)
	    {
	      double lsun, glsun, msun, csun;
	      double dt, t;

	      dt = leapSecs;
	      dt += 32.184;

	      /* Change units to centuries */

	      dt /= (36525 * 24. * 60. * 60.);

	      /* Julian centuries for the EOD from a base epoch 2000.0 */

	      t = (jd - 2451545.0) / 36525.0;

	      /* Correct for dt = tt - ut1  */

	      t += dt ;

	      lsun = Map360(280.46645 + 36000.76983*t + 0.0003032*t*t);

	      /* Mean anomaly */

	      msun = Map360(357.52910 + 35999.05030*t - 0.0001559*t*t -
	        0.00000048*t*t*t);

	      msun = msun*PI/180.;

	      /* Sun's center */

	      csun = (1.9146000 - 0.004817*t - 0.000014*t*t)*sin(msun)
	        + (0.019993 - 0.000101*t)*sin(2.*msun)
	        + 0.000290*sin(3.*msun);

	      /* True geometric longitude */

	      glsun = Map360(lsun  + csun);

	      return (glsun);
	    }
	
	/**
	 * Mean obliquity of the ecliptic for the Julian Date in degrees.
	 * @param jd Julian Date
	 * @param leapSecs Leap Seconds (TAI - UTC)
	 * @return Mean Obliquity of the ecliptic in degrees
	 */
	public static double MeanObliquity(double jd, double leapSecs)
	    {
	      double eps0;
	      double dt, t;

	      dt = leapSecs;
	      dt += 32.184;

	      /* Change units to centuries */

	      dt /= (36525 * 24. * 60. * 60.);

	      /* Julian centuries for the JD from a base epoch 2000.0 */

	      t = (jd - 2451545.0) / 36525.0;

	      /* Correct for dt = tdt - ut1 (not significant) */

	      t += dt ;

	      /* Mean obliquity in degrees */

	      eps0 = 23.0+26./60+21.448/3600.;
	      eps0 += (- 46.8150*t - 0.00059*t*t + 0.001813*t*t*t)/3600.;

	      return (eps0);
	    }

	/**
	 * Nutation of the obliquity of the ecliptic for the EOD in degrees.
	 * @param jd Julian Date
	 * @param leapSecs Leap Seconds (TAI - UTC)
	 * @return Nutation of the obliquity of the ecliptic in degrees
	 */
	public static double NObliquity(double jd, double leapSecs)
	    {
	      double deps, lsun, lmoon, omega;
	      double dt, t;

	      dt = leapSecs;
	      dt += 32.184;

	      /* Change units to centuries */

	      dt /= (36525 * 24. * 60. * 60.);

	      /* Julian centuries for the JD from a base epoch 2000.0 */

	      t = (jd - 2451545.0) / 36525.0;

	      /* Correct for dt = tt - ut1  */

	      t += dt ;

	      /* Longitude of the ascending node of the Moon's mean orbit in degrees */

	      omega = 125.04452 - 1934.136261*t + 0.0020708*t*t + t*t*t/450000.;

	      /* Mean longitudes of the Sun and the Moon in degrees */

	      lsun = LongitudeSun(jd, leapSecs);
	      lmoon = Map360(218.31654591 + 481267.88134236*t - 0.00163*t*t + t*t*t/538841. - t*t*t*t/65194000.);

	      /* Convert to radians */

	      omega = omega*PI/180.;
	      lsun = lsun*PI/180.;
	      lmoon = lmoon*PI/180.;

	      /* Nutation of the obliquity in seconds of arc for the JD */

	      deps = 9.20*cos(omega) + 0.57*cos(2.*lsun) +
	        0.1*cos(2.*lmoon) - 0.09*cos(2.*omega);

	      /* Convert to degrees */

	      deps /= 3600.;

	      return (deps);
	    }


	
	
	/**
	 * True obliquity of the ecliptic for the JD in degrees.
	 * @param jd Julian Date
	 * @param leapSecs Leap Seconds (TAI - UTC)
	 * @return True Obliquity of the ecliptic in degrees
	 */
	public static double TrueObliquity(double jd, double leapSecs)
	    {
	      double eps, eps0, deps;

	      eps0 = MeanObliquity(jd, leapSecs);
	      deps = NObliquity(jd, leapSecs);
	      eps = eps0 + deps;

	      return (eps);
	    }
		
		
	/**
	 * Calculate the fractional part of double number.
	 * @param x number to find fractional part of
	 * @return fraction of x
	 */
	public static double frac(double x)
	    {
	    x -= (int) x;
	    return ((x < 0) ? x + 1.0 : x);
	    }

	/**
	 * Map an angle in degrees to  0 <= angle < 360.
	 * @param angle
	 * @return modified angle in degrees
	 */
	public static double Map360(double angle)
	    {
	    int n;
	    if (angle < 0.0)
	        {
	        n = (int) (angle / 360.0) - 1;
	        return (angle - n * 360.0);
	        }
	    else if (angle >= 360.0)
	        {
	        n = (int) (angle / 360.0);
	        return (angle - n * 360.0);
	        }
	    else
	        {
	        return (angle);
	        }
	    }
	

	
	public static String decToString (double d, int fractionPlaces, int base, Boolean showPlus)
	{
    DecimalFormat nf = new DecimalFormat();
    DecimalFormat nf2x =  new DecimalFormat();
	nf.setMinimumIntegerDigits(2);
    nf2x.setMinimumIntegerDigits(2);
    nf2x.setMinimumFractionDigits(0);
    nf2x.setMaximumFractionDigits(fractionPlaces);

    boolean ampm = false;
    boolean pm = false;
    if (base == 1224)
        {
//        base = 12;
        ampm = true;
        if (d >= 12.0)
            {
            d -= 12.0;
            pm = true;
            }
        }

	double dd = Math.abs(d);
//    dd += 0.0000001;

	int h = (int)dd;
	int m = (int)(60.0*(dd-(double)h));
	double s = 3600.0*(dd-(double)h-(double)m/60.0);
//    String secString="";
//    secString = nf2x.format(s);
//    if (Tools.parseDouble(secString) >= 60.0)  //correct rounding issues when converting to dms format
//        {
//        if      (fractionPlaces == 0) secString = "59";
//        else if (fractionPlaces == 1) secString = "59.9";
//        else if (fractionPlaces == 2) secString = "59.99";
//        else if (fractionPlaces == 3) secString = "59.999";
//        else if (fractionPlaces == 4) secString = "59.9999";
//        else if (fractionPlaces == 5) secString = "59.99999";
//        else if (fractionPlaces == 6) secString = "59.999999";
//        else if (fractionPlaces == 7) secString = "59.9999999";
//        else if (fractionPlaces == 8) secString = "59.99999999";
//        else if (fractionPlaces == 9) secString = "59.999999999";
//        else if (fractionPlaces == 10)secString = "59.9999999999";
//        }



    if (parseDouble(nf2x.format(s)) >= 60.0)
        {
        s = 0.0;
        m += 1;
        }
    if (m > 59)
        {
        m -= 60;
        h += 1;
        }
    if (d > 0 && h >= base)
        {
        if (base == 180 || (base == 12 && !ampm))
            {
            d = -d;
            if (s != 0)
                {
                s = 60 - s;
                m = 59 - m;
                h--;
                }
             else if (m != 0)
                {
                m = 59 - m;
                h--;
                }
            }
        else if (base == 12 && ampm)
            {
            h -= base;
            pm = !pm;
            }
        else if(base == 90)
            {
            h = 90;
            m = 0;
            s = 0;
            }
        else
            h -= base;
        }
    else if (base == 90 && d < -90)
        {
        h = 90;
        m = 0;
        s = 0;
        }

    if (ampm && h == 0) h = 12;
	String str = "";
	if (d < 0.0) str = "-";
    else if (showPlus) str = "+";
    str += "" + nf.format(h) + ":" + nf.format(m) + ":" + nf2x.format(s);
    if (ampm) str += pm ? " PM" : " AM";
	return str;
	}

	public static double parseDouble(String s) {
		return parseDouble(s, Double.NaN);
	}
	
	public static double parseDouble(String s, double defaultValue) {
		if (s==null) return defaultValue;
		try {
			defaultValue = Double.parseDouble(s);
		} catch (NumberFormatException e) {}
		return defaultValue;			
	}
	
	
}
