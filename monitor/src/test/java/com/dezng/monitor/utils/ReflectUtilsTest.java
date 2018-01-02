package com.dezng.monitor.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dezng on 17-12-30.
 */
public class ReflectUtilsTest {



    @Test
    public void before() throws Exception {

    }
    @Test
    public void getClazz() throws Exception {

    }

    @Test
    public void getField() throws Exception {

        // public field
        BaseImpl baseImpl = new BaseImpl();
        Object obj = ReflectUtils.getFieldValue(baseImpl, "pub_int");
        assertEquals(123, obj);
        assertEquals(Integer.class, obj.getClass());

        // private field
        obj = ReflectUtils.getFieldValue(baseImpl, "pri_ch");
        assertEquals('a', obj);
        assertEquals(Character.class, obj.getClass());


//        // static field
//        obj = ReflectUtils.getFieldValue(baseImpl, "sta_dou");
//        assertEquals(12D, obj);
//        assertEquals(Double.class, obj.getClass());
//
//
//        // private static field from parent
//        obj = ReflectUtils.getFieldValue(baseImpl, "pa_pri_int");
//        assertEquals(111, obj);
//        assertEquals(Integer.class, obj.getClass());


    }

    @Test
    public void setField() throws Exception {


        // public field
        BaseImpl baseImpl = new BaseImpl();
        ReflectUtils.setFieldValue(baseImpl, "pub_int", 234);
        Object obj = ReflectUtils.getFieldValue(baseImpl, "pub_int");
        assertEquals(234, obj);
        assertEquals(Integer.class, obj.getClass());

        // private field
        ReflectUtils.setFieldValue(baseImpl, "pri_ch", 'c');
        obj = ReflectUtils.getFieldValue(baseImpl, "pri_ch");
        assertEquals('c', obj);
        assertEquals(Character.class, obj.getClass());
    }

    @Test
    public void staticField() throws Exception {


        BaseImpl baseImpl = new BaseImpl();
        Object obj;

        // static field
        ReflectUtils.setFieldValue(baseImpl, "sta_dou", 23D);
        obj = ReflectUtils.getFieldValue(baseImpl, "sta_dou");
        assertEquals(23D, obj);
        assertEquals(Double.class, obj.getClass());


        // private static field from parent
        ReflectUtils.setFieldValue(baseImpl, "pa_pri_int", 121);
        obj = ReflectUtils.getFieldValue(baseImpl, "pa_pri_int");
        assertEquals(121, obj);
        assertEquals(Integer.class, obj.getClass());

    }

    @Test
    public void invoke() throws Exception {

    }

    public static class Base {
        private static int pa_pri_int = 111;
        public String str;
        private String p;
    }

    public static class BaseImpl extends Base {
        private char pri_ch = 'a';
        public static double sta_dou = 12;
        public int pub_int = 123;

        public static double getSta_dou() {
            return sta_dou;
        }

        public static void setSta_dou(double sta_dou) {
            BaseImpl.sta_dou = sta_dou;
        }
    }

}