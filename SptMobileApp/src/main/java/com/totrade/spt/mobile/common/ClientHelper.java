package com.totrade.spt.mobile.common;

import com.autrade.spt.activity.stub.service.impl.ActivityStubBase;
import com.autrade.spt.bank.stub.service.impl.BankStubBase;
import com.autrade.spt.datacentre.stub.service.impl.SptDatacentreBase;
import com.autrade.spt.deal.stub.service.impl.SptDealStubBase;
import com.autrade.spt.master.stub.service.impl.SptMasterStubBase;
import com.autrade.spt.nego.stub.service.impl.SptNegoStubBase;
import com.autrade.spt.report.stub.service.impl.SptReportStubBase;
import com.autrade.spt.zone.stub.service.impl.SptZoneStubBase;
import com.totrade.spt.mobile.app.SptApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

public class ClientHelper {
    private static final Map<String, Class<? extends Object>> serviceMapStub = new HashMap<>();

    static {
        Class<?>[] baseClasses = new Class<?>[]{
                BankStubBase.class,
                SptDealStubBase.class,
                SptMasterStubBase.class,
                SptNegoStubBase.class,
                SptReportStubBase.class,
                SptZoneStubBase.class,
                SptDatacentreBase.class,
                ActivityStubBase.class,
        };

        for (Class<?> class1 : baseClasses) {
            loadClass(class1);
        }

    }

    /**
     * 把查询到的interface和它的实现类以键值对形式存到map中
     *
     * @param class1 不同服务领域的基类
     */
    private static void loadClass(Class<?> class1) {
        List<String> classNames = getClassName(class1.getPackage().getName());
        if (classNames != null) {
            for (String className : classNames) {
                if (!className.equals(class1.getName())) {      //不包含baseStub类
                    try {
                        Class<?> forName = Class.forName(className);
                        if (forName.getInterfaces().length > 0) {
                            serviceMapStub.put(forName.getInterfaces()[0].getName(), forName);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }

    /**
     * 根据包名查找包下的所有类
     *
     * @param packageName
     * @return
     */
    public static List<String> getClassName(String packageName) {
        List<String> classNameList = new ArrayList<>();
        DexFile df = null;
        try {
            df = new DexFile(SptApplication.context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                String className = enumeration.nextElement();

                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                df.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classNameList;
    }

    public static Map<String, Class<? extends Object>> getServicemapstub() {
        return serviceMapStub;
    }

}
