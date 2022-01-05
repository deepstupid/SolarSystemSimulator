/*
 * Copyright (c) 2017 Nico Kuijpers
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is furnished 
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR I
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ephemeris;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import util.Vector3D;

/**
 * Unit test for class EphemerisUtil.
 * @author Nico Kuijpers
 */
public class EphemerisUtilTest {
    
    public EphemerisUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Compute number of centuries past J2000.0
     * @param date date
     * @return number of centuries pas J2000.0
     */
    @Test
    public void testComputeNrCenturiesPastJ2000Zero() {
        GregorianCalendar date = new GregorianCalendar(2000,0,1,12,0);
        double expResult = 0.0;
        double result = EphemerisUtil.computeNrCenturiesPastJ2000(date);
        assertEquals(expResult, result, 1.0E-14);
    }

    @Test
    public void testComputeNrCenturiesPastJ2000One() {
        GregorianCalendar date = new GregorianCalendar(2100,0,1,12,0);
        double expResult = 1.0;
        double result = EphemerisUtil.computeNrCenturiesPastJ2000(date);
        assertEquals(expResult, result, 1.0E-14);
    }
    
    /**
     * Solve Kepler's equation M = E - e*sin(E), where
     * M is mean anomaly, E is eccentric anomaly, and e is eccentricity.
     * Uses fixed point iteration to solve Kepler's equation.
     * 
     * @param Mrad          mean anomaly in radians
     * @param eccentricity  eccentricity
     * @param maxError      maximum error allowed    
     * @return eccentric anamaly in radians
     */
    @Test
    public void testSolveKeplerEquationFixedPointIteration() {
        long timeStart = System.currentTimeMillis();
        double maxError = 1.0E-8;
        double eccentricity = 0.0;
        while (eccentricity < 1.0) {
            for (int i = 0; i < 360; i++) {
                double M = (double) i;
                double Mrad = Math.toRadians(M);
                double Erad = EphemerisUtil.solveKeplerEquationFixedPointIteration(Mrad,eccentricity,maxError);
                double error = Mrad - (Erad -  eccentricity*Math.sin(Erad));
                assertEquals(error,0.0,maxError);
            }
            eccentricity += 0.001;
        }
        long timeStop = System.currentTimeMillis();
        long timeElapsed = timeStop - timeStart;
        System.out.println("Time elapsed fixed point iteration: " + timeElapsed + " ms");
    }
    
    /**
     * Solve Kepler's equation M = E - e*sin(E), where
     * M is mean anomaly, E is eccentric anomaly, and e is eccentricity.
     * Uses Newton-Raphson to solve Kepler's equation.
     * 
     * @param Mrad          mean anomaly in radians
     * @param eccentricity  eccentricity
     * @param maxError      maximum error allowed    
     * @return eccentric anamaly in radians
     */
    @Test
    public void testSolveKeplerEquationNewtonRaphson() {
        long timeStart = System.currentTimeMillis();
        double maxError = 1.0E-14;
        double eccentricity = 0.0;
        while (eccentricity < 1.0) {
            for (int i = 0; i < 360; i++) {
                double M = (double) i;
                double Mrad = Math.toRadians(M);
                double Erad = EphemerisUtil.solveKeplerEquationNewtonRaphson(Mrad,eccentricity,maxError);
                double error = Mrad - (Erad -  eccentricity*Math.sin(Erad));
                assertEquals(error,0.0,maxError);
            }
            eccentricity += 0.001;
        }
        long timeStop = System.currentTimeMillis();
        long timeElapsed = timeStop - timeStart;
        System.out.println("Time elapsed Newton-Raphson: " + timeElapsed + " ms");
    }
    
    /**
     * Solve Kepler's equation M = E - e*sin(E), where
     * M is mean anomaly, E is eccentric anomaly, and e is eccentricity.
     * Uses Halley's method to solve Kepler's equation.
     * 
     * @param Mrad          mean anomaly in radians
     * @param eccentricity  eccentricity
     * @param maxError      maximum error allowed    
     * @return eccentric anamaly in radians
     */
    @Test
    public void testSolveKeplerEquationHalley() {
        long timeStart = System.currentTimeMillis();
        double maxError = 1.0E-14;
        double eccentricity = 0.0;
        while (eccentricity < 1.0) {
            for (int i = 0; i < 360; i++) {
                double M = (double) i;
                double Mrad = Math.toRadians(M);
                double Erad = EphemerisUtil.solveKeplerEquationHalley(Mrad,eccentricity,maxError);
                double error = Mrad - (Erad -  eccentricity*Math.sin(Erad));
                assertEquals(error,0.0,maxError);
            }
            eccentricity += 0.001;
        }
        long timeStop = System.currentTimeMillis();
        long timeElapsed = timeStop - timeStart;
        System.out.println("Time elapsed Halley's method : " + timeElapsed + " ms");
    }
    
    /**
     * Compute position in orbit plane from position in ecliptic plane.
     *
     * @param position position of planet in m
     * @param longNode longitude of ascending node [degrees]
     * @param inclination inclination [degrees]
     * @param argPerihelion argument of perihelion [degrees
     * @return position in orbit plane (typically z will be near zero)
     * Author: Marco
     */
    @Test
    public void testComputeOrbitPositionFromEclipticPosition() {
        Vector3D a = new Vector3D(-246000,-5000,0); // reflects hypothetical orbit position
        Vector3D b;
        Vector3D c;
        b = EphemerisUtil.computeOrbitPositionFromEclipticPosition(a, -50,7,135);
        c = EphemerisUtil.computeEclipticPositionFromOrbitPosition(b, -50,7,135);
        // c must be equal to a (within, say, 1e-6)
        double distance = c.euclideanDistance(a);
        Assert.assertEquals("c not equal to a",0.0,distance,1.0E-10);
    }
    
    /**
     * Compute position in ecliptic plane from position in orbit plane.
     *
     * @param position position of planet in m
     * @param longNode longitude of ascending node [degrees]
     * @param inclination inclination [degrees]
     * @param argPerihelion argument of perihelion [degrees]
     * @return position in ecliptic plane
     * Author: Marco
     */
    @Test
    public void testComputeEclipticPositionFromOrbitPosition() {
        Vector3D a = new Vector3D(-246000,-5000,0); // reflects hypothetical orbit position
        Vector3D b;
        Vector3D c;
        b = EphemerisUtil.computeEclipticPositionFromOrbitPosition(a, -50,7,135);
        c = EphemerisUtil.computeOrbitPositionFromEclipticPosition(b, -50,7,135);
        // c must be equal to a (within, say, 1e-6)
        double distance = c.euclideanDistance(a);
        Assert.assertEquals("c not equal to a",0.0,distance,1.0E-10);
    }
    
    /**
     * Compute orbital elements from position and velocity of planet.
     * @param position Position of planet in m
     * @param velocity Velocity of planet in m/s
     * @return orbitElements orbit elements (Keplerian elements)
     */
    @Test
    public void testComputeOrbitalElementsFromPositionVelocityJupiter() {
        System.out.println("computeOrbitalElementsFromPositionVelocity (Jupiter)");

        // Date is Jan 1, 2017
        GregorianCalendar date = new GregorianCalendar(1, 0, 2017);

        // Orbital parameters for Jupiter
        double[] orbitPars = SolarSystemParameters.getInstance().getOrbitParameters("jupiter");

        // Jupiter orbits around the sun in 12 years
        int nrDays = (int) (12 * 365.25);
        int day = 0;
        while (day < nrDays) {

            // Compute orbital elements for given date
            double orbitElementsExpected[] = EphemerisUtil.computeOrbitalElements(orbitPars, date);

            // Compute (x,y,z) position of Jupiter [m] from orbital elements
            Vector3D position = EphemerisUtil.computePosition(orbitElementsExpected);

            // Compute (x,y,z) velocity of Jupiter [m/s] from orbital elements
            Vector3D velocity = EphemerisUtil.computeVelocity(orbitElementsExpected);

            // Compute orbital elements from position and velocity
            double orbitElementsActual[]
                    = EphemerisUtil.computeOrbitalElementsFromPositionVelocity("sun",position, velocity);

            // Expected orbital elements
            double axisExpected = orbitElementsExpected[0]; // semi-major axis [au]
            double eccentricityExpected = orbitElementsExpected[1]; // eccentricity [-]
            double inclinationExpected = orbitElementsExpected[2]; // inclination [degrees]
            double meanAnomalyExpected = orbitElementsExpected[3]; // mean anomaly [degrees]
            double argPerihelionExpected = orbitElementsExpected[4]; // argument of perihelion [degrees]
            double longNodeExpected = orbitElementsExpected[5]; // longitude of ascending node [degrees]

            // Actual orbital elements
            double axisActual = orbitElementsActual[0]; // semi-major axis [au]
            double eccentricityActual = orbitElementsActual[1]; // eccentricity [-]
            double inclinationActual = orbitElementsActual[2]; // inclination [degrees]
            double meanAnomalyActual = orbitElementsActual[3]; // mean anomaly [degrees]
            double argPerihelionActual = orbitElementsActual[4]; // argument of perihelion [degrees]
            double longNodeActual = orbitElementsActual[5]; // longitude of ascending node [degrees]
            
            // Ensure that 0 <= expected mean anomaly < 360 for comparison to actual mean anomaly
            while (meanAnomalyExpected < 0.0) {
                meanAnomalyExpected += 360.0;
            }
            while (meanAnomalyExpected >= 360.0) {
                meanAnomalyExpected -= 360.0;
            }
            
            // Ensure that 0 <= actual mean anomaly < 360 for comparison to expected mean anomaly
            while (meanAnomalyActual < 0.0) {
                meanAnomalyActual += 360.0;
            }
            while (meanAnomalyActual >= 360.0) {
                meanAnomalyActual -= 360.0;
            }

            // Compare actual orbital elements to expected orbital elements
            Assert.assertEquals("Wrong semi-major axis(day " + day + ")", axisExpected, axisActual, 1.0E-14);
            Assert.assertEquals("Wrong eccentricity (day " + day + ")", eccentricityExpected, eccentricityActual, 1.0E-13);
            Assert.assertEquals("Wrong inclination (day " + day + ")", inclinationExpected, inclinationActual, 1.0E-12);
            Assert.assertEquals("Wrong mean anomaly (day " + day + ")", meanAnomalyExpected, meanAnomalyActual, 1.0E-08);
            Assert.assertEquals("Wrong arg perihelion (day " + day + ")", argPerihelionExpected, argPerihelionActual, 1.0E-07);
            Assert.assertEquals("Wrong long asc node (day " + day + ")", longNodeExpected, longNodeActual, 1.0E-13);
            
            // Next day
            date.add(Calendar.DAY_OF_MONTH, 1);
            day++;
        }
    }
    
    @Test
    public void testComputeOrbitalElementsFromPositionVelocityMercury() {
        System.out.println("computeOrbitalElementsFromPositionVelocity (Mercury)");
        
        // Start date is Jan 1, 2017
        GregorianCalendar date = new GregorianCalendar(1,0,2017);
        
        // Orbital parameters for Mercury
        double[] orbitPars = SolarSystemParameters.getInstance().getOrbitParameters("mercury");
       
        // Mercury orbits around the sun in 88 days
        int nrDays = 88;
        int day = 0;
        while (day < nrDays) {

            // Compute orbital elements for given date
            double orbitElementsExpected[] = EphemerisUtil.computeOrbitalElements(orbitPars, date);

            // Compute (x,y,z) position of Jupiter [m] from orbital elements
            Vector3D position = EphemerisUtil.computePosition(orbitElementsExpected);

            // Compute (x,y,z) velocity of Jupiter [m/s] from orbital elements
            Vector3D velocity = EphemerisUtil.computeVelocity(orbitElementsExpected);

            // Compute orbital elements from position and velocity
            double orbitElementsActual[]
                    = EphemerisUtil.computeOrbitalElementsFromPositionVelocity("sun",position, velocity);

            // Expected orbital elements
            double axisExpected = orbitElementsExpected[0]; // semi-major axis [au]
            double eccentricityExpected = orbitElementsExpected[1]; // eccentricity [-]
            double inclinationExpected = orbitElementsExpected[2]; // inclination [degrees]
            double meanAnomalyExpected = orbitElementsExpected[3]; // mean anomaly [degrees]
            double argPerihelionExpected = orbitElementsExpected[4]; // argument of perihelion [degrees]
            double longNodeExpected = orbitElementsExpected[5]; // longitude of ascending node [degrees]

            // Actual orbital elements
            double axisActual = orbitElementsActual[0]; // semi-major axis [au]
            double eccentricityActual = orbitElementsActual[1]; // eccentricity [-]
            double inclinationActual = orbitElementsActual[2]; // inclination [degrees]
            double meanAnomalyActual = orbitElementsActual[3]; // mean anomaly [degrees]
            double argPerihelionActual = orbitElementsActual[4]; // argument of perihelion [degrees]
            double longNodeActual = orbitElementsActual[5]; // longitude of ascending node [degrees]
            
            // Ensure that 0 <= expected mean anomaly < 360 for comparison to actual mean anomaly
            while (meanAnomalyExpected < 0.0) {
                meanAnomalyExpected += 360.0;
            }
            while (meanAnomalyExpected >= 360.0) {
                meanAnomalyExpected -= 360.0;
            }
            
            // Ensure that 0 <= actual mean anomaly < 360 for comparison to expected mean anomaly
            while (meanAnomalyActual < 0.0) {
                meanAnomalyActual += 360.0;
            }
            while (meanAnomalyActual >= 360.0) {
                meanAnomalyActual -= 360.0;
            }
            
            // Compare actual orbital elements to expected orbital elements
            Assert.assertEquals("Wrong semi-major axis(day " + day + ")", axisExpected, axisActual, 1.0E-14);
            Assert.assertEquals("Wrong eccentricity (day " + day + ")", eccentricityExpected, eccentricityActual, 1.0E-14);
            Assert.assertEquals("Wrong inclination (day " + day + ")", inclinationExpected, inclinationActual, 1.0E-13);
            Assert.assertEquals("Wrong mean anomaly (day " + day + ")", meanAnomalyExpected, meanAnomalyActual, 1.0E-10);
            Assert.assertEquals("Wrong arg perihelion (day " + day + ")", argPerihelionExpected, argPerihelionActual, 1.0E-10);
            Assert.assertEquals("Wrong long asc node (day " + day + ")", longNodeExpected, longNodeActual, 1.0E-13);

            // Next day
            date.add(Calendar.DAY_OF_MONTH, 1);
            day++;
        }
    }
}
