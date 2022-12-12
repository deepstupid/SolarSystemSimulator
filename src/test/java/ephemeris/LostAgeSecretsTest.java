package ephemeris;

import org.junit.Test;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class LostAgeSecretsTest {

    /** 2003/11/4 17:53p (GMT)? https://www.youtube.com/watch?v=JX1ciG8beOU*/
    @Test
    public void x28_solar_flare() {
        var e = EphemerisAccurate.the;
        var e2 = EphemerisSolarSystem.the;
        var w = new GregorianCalendar(2003,
                GregorianCalendar.NOVEMBER,4,
                19,53);
        w.setTimeZone(TimeZone.getTimeZone("UTC"));

        var earth = e.getBodyPosition("Earth", w);
        var moon  = e.getBodyPosition("Moon", w);
        var sun   = e.getBodyPosition("Sun", w);
        var ceres = e2.getBodyPosition("Ceres", w);

        var distEarthMoon = earth.euclideanDistance(moon) / SolarSystemParameters.ASTRONOMICALUNIT;
        var distCeresSun = sun.euclideanDistance(ceres) / SolarSystemParameters.ASTRONOMICALUNIT;
        double ratio = distCeresSun / distEarthMoon;
        System.out.println("earth<->moon = " + distEarthMoon);
        System.out.println("ceres<->sun = " + distCeresSun);
        System.out.println("ratio = " + ratio);
        assertEquals(1000.08f, ratio, 0.01f);
    }
}
