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
package solarsystem;

import java.io.Serializable;
import util.Vector3D;

/**
 * Represents a body of the Solar System.
 * @author Nico Kuijpers
 */
public class SolarSystemBody implements Serializable {
    
    // Default serialVersion id
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Vector3D position;
    private Vector3D[] orbit;
    private double diameter;
    private SolarSystemBody centerBody;

    /**
     * Constructor.
     * @param name       name of body
     * @param position   position in m
     * @param orbit      orbit
     * @param diameter   diameter in m
     * @param centerBody center body
     */
    public SolarSystemBody(String name, Vector3D position, Vector3D[] orbit, 
            double diameter, SolarSystemBody centerBody) {
        this.name = name;
        this.position = position;
        this.orbit = orbit;
        this.diameter = diameter;
        this.centerBody = centerBody;
    }
    
    /**
     * Get name of body.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of body.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get position of body in m.
     * @return position
     */
    public Vector3D getPosition() {
        return position;
    }
    
    /**
     * Set position of body.
     * @param position new position in m
     */
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    /**
     * Get orbit of body.
     * @return orbit
     */
    public Vector3D[] getOrbit() {
        if (centerBody == null) {
            // This body is a planet with center body sun
            return orbit;
        }
        else {
            // This body is a moon with a planet as center body
            Vector3D planetPosition = centerBody.getPosition();
            Vector3D[] orbitAroundCenterBody = new Vector3D[orbit.length];
            for (int i = 0; i < orbit.length; i++) {
                orbitAroundCenterBody[i] = planetPosition.plus(orbit[i]);
            }
            return orbitAroundCenterBody;
        }
    }
    
    /**
     * Set orbit of body.
     * @param orbit new orbit in m
     */
    public void setOrbit(Vector3D[] orbit) {
        this.orbit = orbit;
    }
    
    /**
     * Get diameter of body in m.
     * @return diameter
     */
    public double getDiameter() {
        return diameter;
    }
    
    /**
     * Set diameter of body.
     * @param diameter new diameter in m
     */
    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }
    
     /**
     * Get reference to center body.
     * @return name of center body
     */
    public SolarSystemBody getCenterBody() {
        return centerBody;
    }

    /**
     * Set reference to center body.
     * @param centerBody reference to center body
     */
    public void setCenterBody(SolarSystemBody centerBody) {
        this.centerBody = centerBody;
    }
}
