/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ephemeris;

import org.junit.*;
import util.Vector3D;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test consistency of AccurateEphemeris by predicting the position of each
 * body for the next hour, based on previous position and velocity and the
 * current velocity.
 * @author Nico Kuijpers
 */
public class EphemerisAccurateTest {
    
    // Major planets
    private static List<String> majorBodies;
    
    // Accurate ephemeris
    private IEphemeris ephemerisAccurate = null;
    
    public EphemerisAccurateTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        majorBodies = new ArrayList<>();
        majorBodies.add("Sun");
        majorBodies.add("Mercury");
        majorBodies.add("Venus");
        majorBodies.add("Earth");
        majorBodies.add("Moon");
        majorBodies.add("Mars");
        majorBodies.add("Jupiter");
        majorBodies.add("Saturn");
        majorBodies.add("Uranus");
        majorBodies.add("Neptune");
        majorBodies.add("Pluto System");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        ephemerisAccurate = EphemerisAccurate.the;
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testPositionVelocityMajorPlanets() {
        // Start test at January 1, 1620
        // Note that January is month 0
        GregorianCalendar date = new GregorianCalendar(1620,0,1);
                
        // https://www.timeanddate.com/time/aboututc.html
        // Use Coordinated Universal Time (UTC) to avoid 
        // sudden changes in ephemeris due to changes from 
        // winter time to summer time and vice versa
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        GregorianCalendar endDate = new GregorianCalendar(2200,0,1);
        assertTrue(endDate.before(JulianDateConverter.convertJulianDateToCalendar(2524623.5)));

        // Previous position and velocity
        Map<String,Vector3D> positionPrevious = new HashMap<>();
        Map<String,Vector3D> velocityPrevious = new HashMap<>();

        // Start test
        while (date.before(endDate)) {
            
            for (String bodyName : majorBodies) {

                // Compute (x,y,z) position [m] for body from ephemeris
                Vector3D position = ephemerisAccurate.getBodyPosition(bodyName, date);
         
                // Compute (x,y,z) velocity [m/s] for body from ephemeris
                Vector3D velocity = ephemerisAccurate.getBodyVelocity(bodyName, date);
                
                // Compare actual position to predicted position
                if (positionPrevious.get(bodyName) != null &&
                    velocityPrevious.get(bodyName) != null) {
                    /*
                     * Compute predicted position from position and velocity
                     * of the previous hour and the current velocity:
                     * Predicted position = 
                     *     previous position + 
                     *     0.5 * deltaTime * previous velocity +
                     *     0.5 * deltaTime * current velocity
                     * with deltaTime = 3600 s
                     */
                    Vector3D positionPredicted = 
                        positionPrevious.get(bodyName).
                        plus(velocityPrevious.get(bodyName).scalarProduct(1800.0).
                        plus(velocity.scalarProduct(1800.0)));
                
                        double difference = positionPredicted.euclideanDistance(position);
                        //System.out.println(difference);
                        assertEquals("Difference between position and predicted position for " + 
                            bodyName, 0.0, difference, 500.0); // 500 m
                }
                
                // Store position and velocity of body for the next hour
                positionPrevious.put(bodyName,position);
                velocityPrevious.put(bodyName,velocity);
            }
            date.add(Calendar.MINUTE, 60);
        }
    }
}
