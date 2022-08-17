package io.github.lukebemish.brainfrick.plugin.gradle;

import io.github.lukebemish.brainfrick.compile.MultipleCompiler;
import org.gradle.api.internal.tasks.compile.CompilerForkUtils;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.work.InputChanges;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class BrainfrickCompile extends AbstractCompile {
    private final CompileOptions compileOptions;
    private final ObjectFactory objectFactory = this.getProject().getObjects();

    public BrainfrickCompile() {
        this.compileOptions = this.objectFactory.newInstance(CompileOptions.class);
        CompilerForkUtils.doNotCacheIfForkingViaExecutable(this.compileOptions, this.getOutputs());
    }

    @TaskAction
    protected void compile(InputChanges inputs) {
        // recompile whole thing, due to potential brainmap changes
        List<Path> inputBrainfrick = getSource().getFiles().stream().map(File::toPath).filter(p->p.getFileName().toString().endsWith(".frick")).toList();
        Path outputPath = getDestinationDirectory().getAsFile().get().toPath();
        MultipleCompiler.compile(inputBrainfrick, outputPath);
    }
}
