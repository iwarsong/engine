package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.mod.vfs.model.impl.VirtualFile;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class UnpackAnalysis extends AbstractAndroidAnalysis {

    private transient ArrayList<VirtualFile> files;

    public UnpackAnalysis() {
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.INFO, "Unpacking .apk...");
        //unpack file
        files = apkFile.unpackWithTechnique(currentProject, apkFile);
        positiveMatch = !files.isEmpty();
        if (positiveMatch) {
            //save files count
            //SET APP FILES & CALCULATE THEIR HASHES, FUZZING HASH, EXTENSION, SIGNATURES
            apkFile.decodeWithTechnique(currentProject, files);
            //set aproject files
            currentProject.setAppFiles(files);
            timeStamp.stop();
            return true;
        }
        //currentProject.setAppFiles(files);
        timeStamp.stop();
        return false;
    }

    @Override
    public String getName() {
        return "In-memory .apk unpacker";
    }
}