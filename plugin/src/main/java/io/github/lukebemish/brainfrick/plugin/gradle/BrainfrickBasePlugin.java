package io.github.lukebemish.brainfrick.plugin.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.internal.tasks.DefaultSourceSetOutput;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.internal.Cast;

import javax.inject.Inject;

public class BrainfrickBasePlugin implements Plugin<Project> {
    private final ObjectFactory objectFactory;
    private Project project;

    @Inject
    public BrainfrickBasePlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public void apply(Project project) {
        this.project = project;
        project.getPluginManager().apply(JavaBasePlugin.class);
        this.configureSourceSetDefaults();
    }

    private void configureSourceSetDefaults() {
        this.javaPluginExtension()
                .getSourceSets()
                .all(
                        sourceSet -> {
                            SourceDirectorySet brainfrickSources = objectFactory.sourceDirectorySet("brainfrick", ((DefaultSourceSet)sourceSet).getDisplayName()+" Brainfrick source");
                            brainfrickSources.getFilter().include("**/*.frick", "**/*.map");
                            brainfrickSources.srcDir("src/" + sourceSet.getName() + "/brainfrick");
                            sourceSet.getAllJava().source(brainfrickSources);
                            sourceSet.getAllSource().source(brainfrickSources);
                            TaskProvider<BrainfrickCompile> compileTask = project
                                    .getTasks()
                                    .register(
                                            sourceSet.getCompileTaskName("brainfrick"),
                                            BrainfrickCompile.class,
                                            compile -> {
                                                ConfigurableFileCollection classpath = compile.getProject().getObjects().fileCollection();
                                                compile.getConventionMapping().map("classpath", () -> classpath);
                                                compile.setDescription("Compiles the " + sourceSet.getName() + " Brainfrick source.");
                                                compile.setSource(brainfrickSources);
                                            }
                                    );
                            String compileClasspathConfigurationName = sourceSet.getCompileClasspathConfigurationName();

                            String sourceSetChildPath = "classes/" + brainfrickSources.getName() + "/" + sourceSet.getName();
                            brainfrickSources.getDestinationDirectory().convention(project.getLayout().getBuildDirectory().dir(sourceSetChildPath));
                            DefaultSourceSetOutput sourceSetOutput = Cast.cast(DefaultSourceSetOutput.class, sourceSet.getOutput());
                            sourceSetOutput.addClassesDir(brainfrickSources.getDestinationDirectory());
                            sourceSetOutput.registerClassesContributor(compileTask);
                            brainfrickSources.compiledBy(compileTask, AbstractCompile::getDestinationDirectory);
                            this.project.getTasks().named(sourceSet.getClassesTaskName(), task -> task.dependsOn(compileTask));
                            this.project.getTasks().named(sourceSet.getCompileJavaTaskName(), task -> task.dependsOn(compileTask));
                            this.project
                                    .getConfigurations()
                                    .getByName(compileClasspathConfigurationName)
                                    .attributes(
                                            attrs -> attrs.attribute(
                                                    LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                                                    this.project.getObjects().named(LibraryElements.class, "classes+resources")
                                            )
                                    );

                            sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(objectFactory.fileCollection().from(brainfrickSources.getDestinationDirectory())));
                            sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(objectFactory.fileCollection().from(brainfrickSources.getDestinationDirectory())));
                        }
                );
    }

    private JavaPluginExtension javaPluginExtension() {
        return this.project.getExtensions().getByType(JavaPluginExtension.class);
    }
}
