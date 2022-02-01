package com.heigl.testsmelldetector.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class TestSmellDetectorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);
        project.getTasks().create("testSmellDetector", DetectTask.class);
    }
}