/*
 * Copyright (c) 2019 Nico Kuijpers
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
package spacecraft;

import solarsystem.SolarSystem;
import solarsystem.SolarSystemBody;
import util.Vector3D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class Spacecraft extends SolarSystemBody implements Serializable {

    // Default serialVersion id
    private static final long serialVersionUID = 1L;

    // Trajectories
    private final List<SpacecraftTrajectory> trajectories;

    /**
     * Constructor.
     * @param name           name of spacecraft
     * @param centerBodyName name of the center body
     * @param solarSystem    the Solar System
     */
    Spacecraft(String name, String centerBodyName, SolarSystem solarSystem) {
        this.setName(name);
        this.setCenterBody(solarSystem.getBody(centerBodyName));
        this.trajectories = defineTrajectories();
        defineEvents(solarSystem);
        this.setOrbit(computeTrajectory());
        updateStatus(solarSystem.getSimulationDateTime());
    }

    /**
     * Compute position, velocity, and trajectory of
     * spacecraft at given date/time
     * @param dateTime
     */
    public void updateStatus(GregorianCalendar dateTime) {
        SpacecraftTrajectory trajectory = findTrajectory(dateTime);
        if (trajectory == null) {
            trajectory = trajectories.get(0);
            dateTime = trajectory.getStartDateTime();
        }
        Vector3D position = trajectory.computePosition(dateTime);
        Vector3D velocity = trajectory.computeVelocity(dateTime);
        setPosition(position);
        setVelocity(velocity);
    }

    /**
     * Find trajectory corresponding to given date/time.
     * Return trajectory with start date/time before given date/time and
     * stop date/time after given date/time.
     * Return null when no trajectory is found.
     * @param dateTime date/time for which trajectory should be found
     * @return trajectory
     */
    private SpacecraftTrajectory findTrajectory(Calendar dateTime) {
        for (SpacecraftTrajectory t : trajectories) {
            if (!t.getStartDateTime().after(dateTime) && t.getStopDateTime().after(dateTime)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Compute entire trajectory of spacecraft
     * @return trajectory [m]
     */
    private Vector3D[] computeTrajectory() {
        List<Vector3D> trajectoryAsList = new ArrayList<>();
        for (SpacecraftTrajectory trajectory : trajectories) {
            trajectoryAsList.addAll(trajectory.computeTrajectory());
        }
        int nrPositions = trajectoryAsList.size();
        Vector3D[] trajectoryAsArray = new Vector3D[nrPositions];
        for (int i = 0; i < nrPositions; i++) {
            trajectoryAsArray[i] = trajectoryAsList.get(i);
        }
        return trajectoryAsArray;
    }

    /**
     * Define trajectories for this spacecraft.
     * @return list of trajectories
     */
    protected abstract List<SpacecraftTrajectory> defineTrajectories();

    /**
     * Define events for this spacecraft and add them to the Solar System.
     * @param solarSystem reference to the Solar System
     */
    protected abstract void defineEvents(SolarSystem solarSystem);
}
