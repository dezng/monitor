package com.dezng.monitor.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by dezng on 17-12-30.
 */
public class ReflectUtils {

    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = Helper.getField(obj.getClass(), fieldName);
        if (field != null) {
            return field.get(obj);
        } else {
            return null;
        }
    }

    public static boolean setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException {
        Field field = Helper.getField(obj.getClass(), fieldName);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static Object getStaticFieldValue(String clazzName, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getStaticFieldValue(Helper.getClazz(clazzName), fieldName);
    }

    public static Object getStaticFieldValue(Class clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = Helper.getField(clazz, fieldName);
        Object result = null;
        result = field.get(clazz);
        return result;
    }

    public static boolean setStaticFieldValue(String string, String fieldName, Object value) throws NoSuchFieldException {
        return setStaticFieldValue(Helper.getClazz(string), fieldName, value);
    }

    public static boolean setStaticFieldValue(Class clazz, String fieldName, Object value) throws NoSuchFieldException {
        Field field = Helper.getField(clazz, fieldName);
        try {
            field.set(clazz, value);
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }

    public static Object invoke(Object obj, String methodName, Class[] clazzes, Object... params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = Helper.getMethod(obj.getClass(), methodName, clazzes);
        if (method != null) {
            return method.invoke(obj, params);
        }
        return null;
    }

    public static Object invoke(Object obj, String methodName, Object... params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = Helper.getMethod(obj.getClass(), methodName, Helper.getParamsClass(params));
        if (method != null) {
            return method.invoke(obj, params);
        }
        return null;
    }

    public static Object invokeStatic(String clazzName, String methodName, Class[] clazzes, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invokeStatic(Helper.getClazz(clazzName), methodName, clazzes, params);
    }

    public static Object invokeStatic(Class clazz, String methodName, Class[] clazzes, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = Helper.getMethod(clazz, methodName, clazzes);
        if (method != null) {
            return method.invoke(clazz, params);
        }
        return null;
    }

    public static Object invokeStatic(String clazzName, String methodName, Object... params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invokeStatic(Helper.getClazz(clazzName), methodName, params);
    }

    public static Object invokeStatic(Class clazz, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = Helper.getMethod(clazz, methodName, Helper.getParamsClass(params));
        if (method != null) {
            return method.invoke(clazz, params);
        }
        return null;
    }


    /**
     * cache Class, Method and Field.
     */
    private static class Helper {


        private static HashMap<String, Class> mClazzCache = new HashMap<>();
        private static HashMap<Class, HashMap<String, Method>> mMethodCache = new HashMap<>();
        private static HashMap<Class, HashMap<String, Field>> mFieldCache = new HashMap<>();

        private static Class<?> getClazz(String clazzName) {
            if (mClazzCache.containsKey(clazzName)) {
                return mClazzCache.get(clazzName);
            }
            Class<?> clazz = null;
            try {
                clazz = Class.forName(clazzName);
                mClazzCache.put(clazzName, clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return clazz;
        }

        public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {

            if (clazz == null) {
                return null;
            }
            Field field = getFieldFromCache(clazz, fieldName);
            if (field != null) {
                System.out.println("class " + clazz + " get field " + fieldName + " from cache!");
                return field;
            }
            try {
                field = clazz.getField(fieldName);
            } catch (NoSuchFieldException e) {
                Class tmp = clazz;
                while (tmp != Object.class && field == null) {
                    try {
                        field = tmp.getDeclaredField(fieldName);
                    } catch (NoSuchFieldException e1) {
                        tmp = tmp.getSuperclass();
                    }
                }
                if (field == null) {
                    throw e;
                }
            }
            if (field != null) {
                field.setAccessible(true);
                saveFieldToCache(clazz, fieldName, field);
            }
            return field;
        }


        public static Method getMethod(String clazzName, String methodName, Class<?>... params) throws NoSuchMethodException {
            return getMethod(getClazz(clazzName), methodName, params);
        }

        public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) throws NoSuchMethodException {
            if (clazz == null) {
                return null;
            }
            Method method = getMethodFromCache(clazz, methodName, params);
            try {
                method = clazz.getMethod(methodName, params);
            } catch (NoSuchMethodException e) {
                Class tmp = clazz;
                while (tmp != Object.class && method == null) {
                    try {
                        method = tmp.getDeclaredMethod(methodName, params);
                    } catch (NoSuchMethodException e1) {
                        tmp = tmp.getSuperclass();
                    }
                }
                if (method == null) {
                    throw e;
                }
            }

            if (method != null) {
                method.setAccessible(true);
                saveMethodToCache(clazz, methodName, method, params);
            }
            return method;
        }

        private static void saveFieldToCache(Class clazz, String fieldName, Field field) {
            HashMap<String, Field> cache = mFieldCache.get(clazz);
            if (cache == null) {
                cache = new HashMap<>();
            }
            cache.put(fieldName, field);
            return;
        }

        private static Field getFieldFromCache(Class clazz, String fieldName) {
            HashMap<String, Field> cache = mFieldCache.get(clazz);
            if (cache == null) {
                return null;
            }
            return cache.get(fieldName);
        }

        private static void saveMethodToCache(Class clazz, String methodName, Method method, Class<?>[] objs) {
            HashMap<String, Method> cache = mMethodCache.get(clazz);
            if (cache == null) {
                cache = new HashMap<>();
            }
            String methodKey = getMethodKey(methodName, objs);
            cache.put(methodKey, method);
            return;
        }

        private static Method getMethodFromCache(Class clazz, String methodName, Class<?>[] objs) {
            HashMap<String, Method> cache = mMethodCache.get(clazz);
            if (cache == null) {
                return null;
            }
            return cache.get(getMethodKey(methodName, objs));
        }

        private static String getMethodKey(String methodName, Class<?>[] objs) {
            if (objs == null) {
                return methodName;
            }
            StringBuilder key = new StringBuilder(methodName);
            for (Class clazz : objs) {
                key.append("#");
                key.append(clazz.getCanonicalName());
            }
            return key.toString();
        }

        private static Class[] getParamsClass(Object[] params) {
            Class[] paramsClazz = null;
            if (params != null) {
                int len = params.length;
                paramsClazz = new Class[len];
                for (int i = 0; i < len; i++) {
                    paramsClazz[i] = params[i] != null ? params[i].getClass() : null;
                }
            }
            return paramsClazz;
        }
    }
}
