/*
 * AbsurdEngine (https://bitbucket.org/smpsnr/absurdengine/) 
 * (c) by Sam Posner (http://www.arcadeoftheabsurd.com/)
 *
 * AbsurdEngine is licensed under a
 * Creative Commons Attribution 4.0 International License
 *
 * You should have received a copy of the license along with this
 * work. If not, see http://creativecommons.org/licenses/by/4.0/ 
 */

package com.arcadeoftheabsurd.j_utils;

/**
 * Holds two integer coordinates
 * @author sam
 */

public class Vector2d 
{    
	public int x;
    public int y;
    
    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void offset(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public void set (int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof Vector2d) ) {
            return false;
        }
        Vector2d test = (Vector2d) o;
        
        return (x == test.x) && (y == test.y);
    }
    
    @Override
    public int hashCode() { 
    	return x ^ y; 
    }
}
