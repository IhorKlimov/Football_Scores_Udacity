/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package barqsoft.footballscores;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.DECEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Igor Klimov on 1/31/2016.
 */
public class MainTest {
    Calendar c;

    @Before
    public void init() {
        c = Calendar.getInstance();
    }

    @Test
    public void compareDate_today() {
        assertEquals(compareDate(c.getTimeInMillis()), 0);
    }

    @Test
    public void compareDate_yesterday() {
        c.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR) - 1);
        assertEquals(compareDate(c.getTimeInMillis()), -1);
    }

    @Test
    public void compareDate_dayBeforeYesterday() {
        c.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR) - 2);
        assertEquals(compareDate(c.getTimeInMillis()), -2);
    }

    @Test
    public void compareDate_tomorrow() {
        c.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR) + 1);
        assertEquals(compareDate(c.getTimeInMillis()), 1);
    }

    @Test
    public void compareDate_dayAfterTomorrow() {
        c.set(DAY_OF_YEAR, c.get(DAY_OF_YEAR) + 2);
        assertEquals(compareDate(c.getTimeInMillis()), 2);
    }

    private int compareDate(long timeInMillis) {
        int result = -66;

        Calendar c = Calendar.getInstance();
        int today = c.get(DAY_OF_YEAR);
        int daysInCurrentYear = c.getActualMaximum(DAY_OF_YEAR);

        c.setTimeInMillis(timeInMillis);
        int givenDay = c.get(DAY_OF_YEAR);
        int daysInGivenYear = c.getActualMaximum(DAY_OF_YEAR);

        switch (compareYear(c)) {
            case 0:
                if (givenDay == today) return 0;
                else if (givenDay == today - 1) return -1;
                else if (givenDay == today - 2) return -2;
                else if (givenDay == today + 1) return 1;
                else if (givenDay == today + 2) return 2;
                break;
            case 1:
                if (today == daysInCurrentYear) {
                    if (givenDay == 1) return 1;
                    else if (givenDay == 2) return 2;
                } else if (today == daysInCurrentYear - 1) {
                    if (givenDay == 1) return 2;
                }
                break;
            case -1:
                if (today == 1) {
                    if (givenDay == daysInGivenYear) return -1;
                    else if (givenDay == daysInGivenYear - 1) return -2;
                } else if (today == 2) {
                    if (givenDay == daysInGivenYear) return -2;
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return result;
    }

    private int compareYear(Calendar givenDate) {
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int givenYear = givenDate.get(Calendar.YEAR);

        if (currentYear == givenYear) return 0;
        else if (currentYear > givenYear) return -1;
        else if (currentYear < givenYear) return 1;
        return -66;
    }


}
