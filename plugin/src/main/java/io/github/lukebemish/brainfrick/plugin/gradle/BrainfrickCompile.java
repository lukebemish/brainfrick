package io.github.lukebemish.brainfrick.plugin.gradle;

import io.github.lukebemish.brainfrick.compile.MultipleCompiler;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.work.InputChanges;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Allows for compilation of brainfrick source files. Though brainmaps may be included in the source set, only the
 * brainfrick source files themselves will be compiled; brainmaps are specified by relative file path.
 */
public class BrainfrickCompile extends AbstractCompile {

    public BrainfrickCompile() {
    }

    @TaskAction
    protected void compile(InputChanges inputs) {
        // recompile whole thing, due to potential brainmap changes
        List<Path> inputBrainfrick = getSource().getFiles().stream().map(File::toPath).filter(p->p.getFileName().toString().endsWith(".frick")).toList();
        Path outputPath = getDestinationDirectory().getAsFile().get().toPath();
        MultipleCompiler.compile(inputBrainfrick, outputPath);
    }
}
