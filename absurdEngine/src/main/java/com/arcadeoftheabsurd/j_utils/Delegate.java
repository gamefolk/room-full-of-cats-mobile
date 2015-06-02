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
 * Basic Java delegate interface - implement with anonymous class
 * @author sam
 */

public interface Delegate 
{
    void function(Object... args);
}
