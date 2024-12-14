package de.tum.cit.fop.maze.arbitrarymap;

import java.io.*;
import java.util.*;

public class MapParser {

    //working with hashmaps because Jay said so
    //System.getProperty("user.dir")
    public static HashMap<String, Integer> parseMap(String filePath) {
        HashMap<String, Integer> mapData = new HashMap<>();


        try  {
            //https://www.baeldung.com/java-buffered-reader where im reading from
            // maps/level-1.properties
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null)   {
                String[] joe = line.split("="); // splits based on the = as divisor and now stores it into an array
                String xy = joe[0]; // coordinate pair
                int key = Integer.parseInt(joe[1]); //special number
//                String[] mama = xy.split(",");
//                int x = Integer.parseInt(mama[0]); //x value
//                int y = Integer.parseInt(mama[1]); //y value

                mapData.put(xy, key);
            }
        } catch (Exception e)  {
            System.out.println(e);
        }
        return mapData;
    }
}

