package com.tnd.multifuction1.util;

import com.tnd.multifuction1.model.Location;

public class GPSUtil {


    public static Location getLocationInfo(String[] locationInfo) {

        Location location = null;
        if (locationInfo == null || locationInfo.length != 13) return null;

        if(locationInfo[2] != "A" && locationInfo[0].equals("GPRMC")){
            location = new Location();
            location.locationSuccess = false;
            return location;
        }

        if(!locationInfo[0].equals("GPRMC")) return null;

        location = new Location();
        location.locationSuccess = true;
//        String temp = "";
//        if ((Integer.parseInt(locationInfo[1].substring(0, 2))+8)>24)
//        {
//            temp = ((Integer.parseInt(locationInfo[1].substring(0, 2)) + 8) - 24) + "";
//        }
//        else
//            temp = (Integer.parseInt(locationInfo[1].substring(0, 2)) + 8) + "";
//        shiJian = temp + ":" + locationInfo[1].substring(2, 2) + ":" + locationInfo[1].substring(4, 2);
//        if (locationInfo[9].length() == 6)
//            riQi = "20" + locationInfo[9].substring(4, 2) + "年" + locationInfo[9].substring(2, 2) + "月" + locationInfo[9].substring(0, 2)+"日";
        if (locationInfo[4] == "N")
        {
            if (locationInfo[3].length() == 10)
                location.weidu = "北纬" + locationInfo[3].substring(0, 2) + "度" + locationInfo[3].substring(2, 7) + "分";
            else if (locationInfo[3].length() == 9)
                location.weidu = "北纬" + locationInfo[3].substring(0, 2) + "度" + locationInfo[3].substring(2, 7) + "分";
        }
        if (locationInfo[4] == "S")
        {
            if (locationInfo[3].length() == 10)
                location.weidu = "南纬" + locationInfo[3].substring(0, 2) + "度" + locationInfo[3].substring(2, 7) + "分";
            else if (locationInfo[3].length() == 9)
                location.weidu = "南纬" + locationInfo[3].substring(0, 2) + "度" + locationInfo[3].substring(2, 7) + "分";
        }
        if (locationInfo[6] == "E")
        {
            if (locationInfo[5].length() == 10)
                location.jingdu = "东经" + locationInfo[5].substring(0, 2) + "度" + locationInfo[5].substring(2, 7) + "分";
            else if (locationInfo[5].length() == 11)
                location.jingdu = "东经" + locationInfo[5].substring(0, 3) + "度" + locationInfo[5].substring(3, 7) + "分";
        }
        if (locationInfo[6] == "W")
        {
            if (locationInfo[5].length() == 10)
                location.jingdu = "西经" + locationInfo[5].substring(0, 2) + "度" + locationInfo[5].substring(2, 8) + "分";
            else if (locationInfo[5].length() == 11)
                location.jingdu = "西经" + locationInfo[5].substring(0, 3) + "度" + locationInfo[5].substring(3, 8) + "分";
        }

        return location;
    }
}
