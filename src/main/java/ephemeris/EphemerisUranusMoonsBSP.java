/*
 * Copyright (c) 2021 Nico Kuijpers and Marco Brassé
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

import util.Vector3D;

import java.util.*;

/**
 * Ephemeris for the Uranus System.
 * This ephemeris is valid from 1 Jan 1970 through 31 Dec 2025.
 * @author Nico Kuijpers
 */

public class EphemerisUranusMoonsBSP implements IEphemeris {

    // File name of BSP file
    private static final String BSPfilename = "EphemerisFilesBSP/ura111_UranusSystem_1970_2025.bsp";

    // Observer code for BSP file
    private static final int observer = 7;

    // Target codes for BSP file
    private final Map<String,Integer> targets;

    // Bodies for which ephemeris can be computed or approximated
    private final List<String> bodies;

    // First valid date
    private final GregorianCalendar firstValidDate;

    // Last valid date
    private final GregorianCalendar lastValidDate;

    // Singleton instance
    private static IEphemeris instance = null;

    // Read ephemeris from BSP file
    private SPK spk = null;

    /**
     * Constructor. Singleton pattern.
     */
    private EphemerisUranusMoonsBSP() {

        /*
         * BSP file ura111_UranusSystem_1970_2025.bsp was generated from ura111.bsp
         * https://naif.jpl.nasa.gov/pub/naif/generic_kernels/spk/satellites/ura111.bsp
         * using
         * python -m jplephem excerpt --targets 7,701,702,703,704,705,799 1970/1/1 2025/12/31 ura111.bsp ura111_UranusSystem_1970_2025.bsp
         * https://pypi.org/project/jplephem/
         */

        // Target codes for BSP file
        targets = new HashMap<>();
        targets.put("Uranus",799);
        targets.put("Miranda",705);
        targets.put("Ariel",701);
        targets.put("Umbriel",702);
        targets.put("Titania",703);
        targets.put("Oberon",704);

        // Bodies for which ephemeris can be computed or approximated
        bodies = new ArrayList<>();
        bodies.addAll(targets.keySet());

        // First valid date Jan 2, 1970
        firstValidDate = new GregorianCalendar(1970,0,2);
        firstValidDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Last valid date Dec 31, 2025
        lastValidDate = new GregorianCalendar(2025,11,31);
        lastValidDate.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Get instance of EphemerisPlutoMoons.
     * @return instance
     */
    public static IEphemeris getInstance() {
        if (instance == null) {
            instance = new EphemerisUranusMoonsBSP();
        }
        return instance;
    }

    @Override
    public GregorianCalendar getFirstValidDate() {
        return firstValidDate;
    }

    @Override
    public GregorianCalendar getLastValidDate() {
        return lastValidDate;
    }

    @Override
    public List<String> getBodies() {
        return Collections.unmodifiableList(bodies);
    }

    @Override
    public Vector3D getBodyPosition(String name, GregorianCalendar date) {
        return getBodyPositionVelocity(name,date)[0];
    }

    @Override
    public Vector3D getBodyVelocity(String name, GregorianCalendar date) {
        return getBodyPositionVelocity(name,date)[1];
    }

    @Override
    public Vector3D[] getBodyPositionVelocity(String name, GregorianCalendar date) {

        // Check whether body name is valid
        if (!bodies.contains(name)) {
            throw new IllegalArgumentException("Unknown body " + name + " for Ephemeris of Uranus System");
        }

        // Check whether date is valid
        if (date.before(firstValidDate) || date.after(lastValidDate)) {
            throw new IllegalArgumentException("Date not valid for Ephemeris of Uranus System");
        }

        // Initialize SPK and open file to read when needed for the first time
        if (spk == null) {
            spk = new SPK();
            spk.initWithBSPFile(BSPfilename);
        }

        // Number of seconds past J2000
        double et = EphemerisUtil.computeNrSecondsPastJ2000(date);

        // Target
        int target = targets.get(name);

        // Observer is barycenter of Uranus System
        Vector3D[] moonPosVel = spk.getPositionVelocity(et,target,observer);
        Vector3D[] uranusPosVel = spk.getPositionVelocity(et,799,observer);
        moonPosVel[0] = moonPosVel[0].minus(uranusPosVel[0]);
        moonPosVel[1] = moonPosVel[1].minus(uranusPosVel[1]);

        // Position and velocity are computed for J2000 frame
        Vector3D positionInvTrans = EphemerisUtil.inverseTransformJ2000(moonPosVel[0]);
        Vector3D velocityInvTrans = EphemerisUtil.inverseTransformJ2000(moonPosVel[1]);
        return new Vector3D[]{positionInvTrans,velocityInvTrans};
    }

    @Override
    public Vector3D getBodyPositionBarycenter(String name, GregorianCalendar date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Vector3D getBodyVelocityBarycenter(String name, GregorianCalendar date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Vector3D[] getBodyPositionVelocityBarycenter(String name, GregorianCalendar date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

