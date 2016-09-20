package com.zerjioang.apkr.v2.plugins.sdk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;

/**
 * Created by sergio on 18/2/16.
 */
@JsonIgnoreProperties(value = {"currentProject"})
public abstract class AbstractApkrStaticPlugin extends PluginAnalysis {

    protected transient ApkrProject currentProject;
    protected boolean positiveMatch;
    protected String html;
    private ProcessStatus status;
    private AtomTimeStamp timeStamp;

    public AbstractApkrStaticPlugin() {
        html = "";
    }

    public void analyze() {
        status = ProcessStatus.WAITING;
        this.timeStamp = new AtomTimeStamp();
        this.positiveMatch = false;
        status = ProcessStatus.STARTED;
        //pre execute
        onPreExecute();
        status = ProcessStatus.EXECUTING;
        //execute
        onExecute();
        //post execute
        postExecute();
        status = ProcessStatus.FINISHED;
        //stop time counter
        this.timeStamp.stop();
    }

    protected abstract void onPreExecute();

    protected abstract void onExecute();

    protected abstract void postExecute();

    protected abstract String getPluginName();

    protected String getResultAsJson() {
        //todo convert the object to json pojo
        return "";
    }

    protected String getResultAsHTML() {
        return html;
    }

    public ApkrProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ApkrProject currentProject) {
        this.currentProject = currentProject;
    }
}
