package com.cn.leedane.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.cn.leedane.mybatis.table.annotation.Column;

public class BeanUtil {
	 /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param bean
     * @param map
     * @return
     */
    public static void convertBeanToMap(Object bean, Map<String, Object> map) {
        Field[] fields = getFields(bean.getClass());
        try {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                //特殊处理serialVersionUID
                if(field.getName().equalsIgnoreCase("serialVersionUID")){
                    continue;
                }
                
                Column serializedName = field.getAnnotation(Column.class);
                String columnName = "";
                if (serializedName != null) {
                    columnName = serializedName.value();
                }
                field.setAccessible(true);
                Object object = field.get(bean);

                if (StringUtil.isNull(columnName)) {
                    map.put(camelToUnderline(field.getName()), object);
                }else{
                    map.put(columnName, object);
                }
            }
        } catch (Exception e) {
            new RuntimeException("get insert sql is exceptoin:" + e);
        }
    }
    
    
    
    /**
     * 添加驼峰式下划线
     * @param param
     * @return
     */
    public static String camelToUnderline(String param){  
        if (param==null||"".equals(param.trim())){  
            return "";  
        }  
        int len=param.length();  
        StringBuilder sb=new StringBuilder(len);  
        for (int i = 0; i < len; i++) {  
            char c=param.charAt(i);  
            if (Character.isUpperCase(c)){  
                sb.append("_");  
                sb.append(Character.toLowerCase(c));  
            }else{  
                sb.append(c);  
            }  
        }  
        return sb.toString();  
    }  

    /**
     * 获取类clazz的所有Field，包括其父类的Field，如果重名，以子类Field为准。
     * @param beanClass
     * @return Field数组
     */
    private static Field[] getFields(Class<?> beanClass) {
        ArrayList<Field> fieldList = new ArrayList<Field>();
        Field[] dFields = beanClass.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }

        Class<?> superClass = beanClass.getSuperclass();
        if (superClass != Object.class) {
            Field[] superFields = getFields(superClass);
            if (null != superFields && superFields.length > 0) {
                for(Field field:superFields){
                    if(!isContain(fieldList, field)){
                        fieldList.add(field);
                    }
                }
            }
        }
        Field[] result=new Field[fieldList.size()];
        fieldList.toArray(result);
        return result;
    }

    /**检测Field List中是否已经包含了目标field
     * @param fieldList
     * @param field 带检测field
     * @return
     */
    public static boolean isContain(ArrayList<Field> fieldList,Field field){
        for(Field temp:fieldList){
            if(temp.getName().equals(field.getName())){
                return true;
            }
        }
        return false;
    }
}
