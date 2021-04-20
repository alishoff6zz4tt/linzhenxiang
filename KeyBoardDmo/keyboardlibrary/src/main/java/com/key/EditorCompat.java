package com.key;

import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/4/29.
 * Editor 帮助类 （java 反射机制)
 */

 class EditorCompat {


    /**
     * 显示EditText文本选中菜单
     */
    public static void startSelectActionMode(EditText editText) {
        loadEditorInnerClass(editText, "startSelectionActionMode");
    }


    /**
     * 关闭 EditText 文本选中快捷菜单
     */
    public static void stopTextActionMode(EditText editText) {
        loadEditorInnerClass(editText, "stopTextActionMode");
    }


    /**
     * 显示 textSelectHandle
     */
    public static void showInsertionController(EditText editText) {
        //获取InsertionPointCursorController
        Object object = loadEditorInnerClass(editText, "getInsertionController");
        //调用InsertionPointCursorController类的show()方法
        show(object);

    }


    /**
     * 显示 textSelectHandle
     */
    public static void hideInsertionController(EditText editText) {

        //获取InsertionPointCursorController
        Object object = loadEditorInnerClass(editText, "getInsertionController");
        //调用InsertionPointCursorController类的hide()方法
        hide(object);
    }


    /**
     * 显示 textSelectHandleLeft 和textSelectHandleRight
     */
    public static void showSelectionController(EditText editText) {


        //获取SelectionModifierCursorController
        Object object = loadEditorInnerClass(editText, "getSelectionController");
        //调用SelectionModifierCursorController 类的show()方法
        show(object);

    }

    /**
     * 显示 textSelectHandleLeft 和textSelectHandleRight
     */
    public static void hideSelectionController(EditText editText) {

        //获取SelectionModifierCursorController
        Object object = loadEditorInnerClass(editText, "getSelectionController");
        //调用SelectionModifierCursorController 类的hide()方法
        hide(object);

    }


    /**
     * 仅初始化光标位置，使光标可见，EditText 可编辑
     */
    public static void positionAtCursorOffset(EditText editText) {
        Object object = loadEditorInnerClass(editText, "getInsertionController");
        //查找 InsertionPointCursorController类中的getHandle方法
        loadClassDeclaredMethod(object, "getHandle");

    }



    //执行Editor类的种的无参方法，并反会方法的返回参数（内部类对象）
    protected static Object loadEditorInnerClass(EditText editText, String me) {
        //获取Editor变量
        try {Field mEditor = TextView.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            //拿到隐藏类Editor；
            Class mClass = Class.forName("android.widget.Editor");
            //获取mEditor对应的对象实例
            Object object = mEditor.get(editText);
            //取得方法  Editor 类的方法
            Method method = mClass.getDeclaredMethod(me);
            //取消访问私有方法的合法性检查
            method.setAccessible(true);
            //调用方法，获取内部类对象
            Object mObject = method.invoke(object);
            return mObject;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected static void loadClassDeclaredMethod(Object object, String me, Object... args) {
        try {
            Method method = object.getClass().getDeclaredMethod(me);
            //取消访问私有方法的合法性检查
            method.setAccessible(true);
            //调用方法，获取内部类对象
            method.invoke(object, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }




    //调用隐藏方法
    protected static void hide(Object object) {
        try {
            Method method = object.getClass().getDeclaredMethod("hide");
            method.setAccessible(true);
            //执行show()方法
            method.invoke(object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    //调用显示方法
    protected static void show(Object object) {
        try {
            Method method =object.getClass().getDeclaredMethod("show");
            method.setAccessible(true);
            //执行show()方法
            method.invoke(object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
