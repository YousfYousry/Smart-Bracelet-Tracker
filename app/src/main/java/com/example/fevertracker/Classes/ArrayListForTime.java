package com.example.fevertracker.Classes;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayListForTime {
    ArrayList<Integer> Index = new ArrayList<>(), Seconds = new ArrayList<>();
    ArrayList<String> timeTiltle = new ArrayList<>();

    public void add(int index, int seconds) {
        Index.add(index);
        Seconds.add(seconds);
    }

    public void clear() {
        Index.clear();
        Seconds.clear();
    }

    public void formTimeTitle() {
        timeTiltle.clear();
        ArrayList<Boolean> firstDateInit = new ArrayList<>();
        int secTem, indexTem, tempIndex = 0;
        int maxIndex = Collections.max(Index);
        int n = Seconds.size();
        ArrayList<String> firstDate = new ArrayList<>();
        ArrayList<String> secondDate = new ArrayList<>();
        for (int i = 0; i < maxIndex + 1; i++) {
            timeTiltle.add("");
            firstDateInit.add(false);
            firstDate.add("");
            secondDate.add("");
        }
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (Seconds.get(i) > Seconds.get(j)) {
                        secTem = Seconds.get(i);
                        Seconds.set(i, Seconds.get(j));
                        Seconds.set(j, secTem);

                        indexTem = Index.get(i);
                        Index.set(i, Index.get(j));
                        Index.set(j, indexTem);
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            tempIndex = Index.get(i);
            if (!firstDateInit.get(Index.get(i))) {
                firstDateInit.set(Index.get(i), true);
                firstDate.set(Index.get(i), SecondsToTime(Seconds.get(i)));
            }
            if (i < n - 1) {
                if (Index.get(i + 1) != tempIndex) {
                    if(firstDate.get(Index.get(i)).compareTo(SecondsToTime(Seconds.get(i)))!=0) {
                        secondDate.set(Index.get(i), SecondsToTime(Seconds.get(i)));
                        timeTiltle.set(Index.get(i), timeTiltle.get(Index.get(i)) + firstDate.get(Index.get(i)) + "  to  " + secondDate.get(Index.get(i)) + "\n");
                    }
                    firstDateInit.set(Index.get(i), false);
                }
            } else {
                secondDate.set(Index.get(i), SecondsToTime(Seconds.get(i)));
            }
        }
        for (int i = 0; i < timeTiltle.size(); i++) {
            if (firstDate.get(i).isEmpty()) {
                if (secondDate.get(i).isEmpty()) {
                    timeTiltle.set(i, firstDate.get(i));
                } else {
                    timeTiltle.set(i, firstDate.get(i) + "  to  " + secondDate.get(i));
                }
            }
        }
    }

    public String get(int index) {
        return timeTiltle.get(index);
    }

    public String SecondsToTime(int seconds) {
        String secondsString = "", minutesString = "", hoursString = "";
        int minutes = 0;
        int hours = 0;
        while (seconds > 59) {
            minutes += 1;
            seconds -= 60;
        }
        while (minutes > 59) {
            hours += 1;
            minutes -= 60;
        }
        if (Integer.toString(seconds).length() < 2) {
            secondsString = "0" + seconds;
        } else {
            secondsString = Integer.toString(seconds);
        }

        if (Integer.toString(minutes).length() < 2) {
            minutesString = "0" + minutes;
        } else {
            minutesString = Integer.toString(minutes);
        }

        if (Integer.toString(hours).length() < 2) {
            hoursString = "0" + hours;
        } else {
            hoursString = Integer.toString(hours);
        }
        return (hoursString + ":" + minutesString + ":" + secondsString);
    }

}
