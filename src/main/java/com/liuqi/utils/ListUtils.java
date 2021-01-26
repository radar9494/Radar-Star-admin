package com.liuqi.utils;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {

    public static List removeDuplicate(List list) {
        if(list==null || list.size()==0){
            return new ArrayList();
        }
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }
}
