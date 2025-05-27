package com.example.vipcrackmodule;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VipCrackHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.hcn.mm"))
            return;

        XposedBridge.log("[*] Hooking com.hcn.mm...");

        XposedHelpers.findAndHookMethod(
            "android.app.Application", lpparam.classLoader, "attach", Context.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = (Context) param.args[0];
                    ClassLoader cl = context.getClassLoader();

                    try {
                        Class<?> userInfoClass = cl.loadClass("com.hcn.mm.beans.UserInfo");

                        for (Constructor<?> ctor : userInfoClass.getDeclaredConstructors()) {
                            XposedBridge.log("[+] Found Constructor: " + ctor.toString());
                        }

                        // Hook 构造函数（建议简化测试版本，确认 hook 是否成功）
                        XposedHelpers.findAndHookConstructor(
                            "com.hcn.mm.beans.UserInfo", cl,
                            String.class, String.class, String.class, String.class,
                            String.class, String.class, int.class, int.class, int.class,
                            String.class, String.class, int.class, String.class, int.class,
                            int.class, long.class, Integer.class, Double.class, Double.class,
                            Double.class, Double.class, String.class, String.class,
                            Integer.class, String.class, Integer.class,
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("[*] UserInfo Constructor Hooked");

                                    // 示例：修改 VIP 状态参数
                                    param.args[8] = 2; // vipStatus
                                }
                            }
                        );

                        // Hook 成员方法（如 isVip）
                        XposedHelpers.findAndHookMethod("com.hcn.mm.beans.UserInfo", cl, "isVip",
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("[*] isVip() hooked, forcing true");
                                    param.setResult(true);
                                }
                            });

                    } catch (Exception e) {
                        XposedBridge.log("[!] Hook failed in Application.attach: " + Log.getStackTraceString(e));
                    }
                }
            }
        );
    }
}
