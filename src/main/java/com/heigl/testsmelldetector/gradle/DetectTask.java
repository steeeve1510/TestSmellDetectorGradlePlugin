package com.heigl.testsmelldetector.gradle;

import com.heigl.testsmelldetector.gradle.util.TestFileDetector;
import com.heigl.testsmelldetector.gradle.util.TestSmellDetectorProvider;
import com.heigl.testsmelldetector.gradle.util.TestSmellDetectorRunner;
import com.heigl.testsmelldetector.gradle.util.TestSmellWriter;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import testsmell.TestFile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DetectTask extends DefaultTask {

    private final TestSmellDetectorProvider testSmellDetectorProvider = new TestSmellDetectorProvider();
    private final TestFileDetector testFileDetector = new TestFileDetector();
    private final TestSmellDetectorRunner testSmellDetectorRunner = new TestSmellDetectorRunner(testSmellDetectorProvider, getLogger());
    private final TestSmellWriter testSmellWriter = new TestSmellWriter(testSmellDetectorProvider, getLogger());

    @TaskAction
    public void runTask() {
        List<String> testDirectories = getTestDirectories();
        String appName = getProject().getName();
        String outputFile = getOutputFile();

        List<TestFile> testFiles = testFileDetector.getTestFiles(testDirectories, appName);
        List<TestFile> testFilesWithSmells = testSmellDetectorRunner.getTestSmells(testFiles);
        testSmellWriter.write(testFilesWithSmells, outputFile);

        getLogger().warn("Test files: " + testFilesWithSmells);
        getLogger().warn("App: " + appName);
        getLogger().warn("Output: " + outputFile);
    }

    private List<String> getTestDirectories() {
        Project project = getProject();
        JavaPluginConvention convention = project.getConvention().getPlugin(JavaPluginConvention.class);
        return convention.getSourceSets().stream()
                .map(SourceSet::getAllJava)
                .map(SourceDirectorySet::getSrcDirs)
                .flatMap(Collection::stream)
                .map(File::getAbsolutePath)
                .filter(s -> s.matches(".*test.java"))
                .collect(Collectors.toList());
    }

    private String getOutputFile() {
        Project project = getProject();
        project.getPlugins().apply(JavaPlugin.class);
        return project.getBuildDir().getAbsolutePath() + "/test-smells.csv";
    }
}
