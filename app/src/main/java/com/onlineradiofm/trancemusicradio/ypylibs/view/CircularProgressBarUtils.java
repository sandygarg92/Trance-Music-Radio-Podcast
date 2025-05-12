/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.ypylibs.view;

/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */
public class CircularProgressBarUtils {
	private CircularProgressBarUtils() {
		
	}

	public static void checkSpeed(float speed) {
		if (speed <= 0f)
			throw new IllegalArgumentException("Speed must be >= 0");
	}

	public static void checkColors(int[] colors) {
		if (colors == null || colors.length == 0)
			throw new IllegalArgumentException("You must provide at least 1 color");
	}

	public static void checkAngle(int angle) {
		if (angle < 0 || angle > 360)
			throw new IllegalArgumentException(String.format("Illegal angle %d: must be >=0 and <= 360", angle));
	}

	public static void checkPositiveOrZero(float number, String name) {
		if (number < 0)
			throw new IllegalArgumentException(String.format("%s %d must be positive", name, number));
	}

	public static void checkPositive(int number, String name) {
		if (number <= 0)
			throw new IllegalArgumentException(String.format("%s must not be null", name));
	}

	public static void checkNotNull(Object o, String name) {
		if (o == null)
			throw new IllegalArgumentException(String.format("%s must be not null", name));
	}
}
