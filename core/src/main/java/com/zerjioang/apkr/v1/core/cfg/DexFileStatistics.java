package com.zerjioang.apkr.v1.core.cfg;

import com.zerjioang.apkr.temp.ApkrIntelligence;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DalvikVM;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.reader.DexHeaderReader;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by sergio on 3/5/16.
 */
public class DexFileStatistics implements Serializable {
    private transient final ApkrProject currentProject;
    private transient final ArrayList<ResourceFile> list;
    private transient final DalvikVM vm;

    //interesting variables

    //packages info
    private HashSet<String> developerPackages, realDeveloperPackages;
    //packages count
    private int totalPackageCount, realDeveloperPackagesCount;

    //class info
    private HashSet<String> developerClassList, realDeveloperClassList, realDeveloperInnerClassList;

    //class count
    private int totalClassCount;
    private int developerClassCount, realDeveloperClassCount;
    private int realDeveloperInnerClassCount;

    public DexFileStatistics(ApkrProject currentProject, ArrayList<ResourceFile> list) {
        //create VM
        vm = new DalvikVM(currentProject);
        this.currentProject = currentProject;
        this.list = list;
        this.list.forEach(this::process);
    }

    private void process(ResourceFile dexFile) {
        byte[] data = currentProject.getDexData(dexFile);
        //first use my class loader and check for file integrity,...
        DexHeaderReader loader = new DexHeaderReader(data, currentProject);
        loader.loadClasses(dexFile);
        ///then use default class loader
        vm.load(dexFile, data, DalvikVM.MULTIDEX);
        //once file is loaded, we can read the info

        developerPackages = new HashSet<>();
        realDeveloperPackages = new HashSet<>();
        developerClassList = new HashSet<>();
        realDeveloperClassList = new HashSet<>();
        realDeveloperInnerClassList = new HashSet<>();

        ApkrIntelligence intel = ApkrIntelligence.getInstance();

        //1 generate package names

        //2 get the classnames of developer handmade
        for (IAtomClass cls : currentProject.getInternalInfo().getListClasses()) {
            String full = cls.getName();
            int idx = full.lastIndexOf("/");
            if (idx != -1)
                full = full.substring(0, idx).replace("/", ".");
            developerPackages.add(full);
            if (!intel.isAndroidv4v7Class(full)) {
                realDeveloperPackages.add(full);
            }
            if (intel.isDeveloperClass(cls.getName())) {
                developerClassList.add(cls.getName().replace("/", "."));
            }
        }
        //1.1 count packages
        totalPackageCount = developerPackages.size();
        realDeveloperPackagesCount = realDeveloperPackages.size();

        //2.1 count total class and dev class
        this.developerClassCount = developerClassList.size();
        this.totalClassCount = currentProject.getInternalInfo().getListClasses().length;

        //2.2 remove autogeneratedClasses
        String[] junk = {
                ".R$anim",
                ".R$attr",
                ".R$integer",
                ".R$styleable",
                ".R$layout",
                ".R$mipmap",
                ".R$style",
                ".R",
                ".R$string",
                ".R$drawable",
                ".R$color",
                ".R$dimen",
                ".R$id",
                ".BuildConfig",
                ".R$bool"
        };
        //2.1 count total class and dev class
        Iterator iter = developerClassList.iterator();
        while (iter.hasNext()) {
            String clsName = (String) iter.next();
            boolean notFound = false;
            for (String s : junk) {
                notFound = clsName.endsWith(s);
                if (notFound) {
                    break;
                }
            }
            if (!notFound) {
                if (clsName.contains("$")) {
                    //inner class
                    realDeveloperInnerClassList.add(clsName);
                } else {
                    //normal class
                    realDeveloperClassList.add(clsName);
                }
            }
        }
        //count classes
        this.realDeveloperClassCount = realDeveloperClassList.size();
        //count inner classes
        this.realDeveloperInnerClassCount = realDeveloperInnerClassList.size();
        Log.write(LoggerType.TRACE, ".dex statistics done");
    }
}
