package com.demonwav.mcdev.buildsystem.maven;

import com.demonwav.mcdev.buildsystem.BuildDependency;
import com.demonwav.mcdev.buildsystem.BuildRepository;
import com.demonwav.mcdev.buildsystem.BuildSystem;
import com.demonwav.mcdev.buildsystem.maven.pom.Dependency;
import com.demonwav.mcdev.buildsystem.maven.pom.MavenProjectXml;
import com.demonwav.mcdev.buildsystem.maven.pom.Repository;
import com.demonwav.mcdev.platform.PlatformType;
import com.demonwav.mcdev.platform.ProjectConfiguration;
import com.demonwav.mcdev.platform.bukkit.BukkitTemplate;
import com.demonwav.mcdev.platform.bungeecord.BungeeCordTemplate;
import com.demonwav.mcdev.platform.sponge.SpongeTemplate;
import com.google.common.base.Strings;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenResource;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MavenBuildSystem extends BuildSystem {

    private VirtualFile pomFile;

    @Override
    public void create(@NotNull Module module, @NotNull PlatformType type, @NotNull ProjectConfiguration configuration) {
        rootDirectory.refresh(false, true);
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceDirectories = Collections.singletonList(VfsUtil.createDirectories(rootDirectory.getPath() + "/src/main/java"));
                resourceDirectories = Collections.singletonList(VfsUtil.createDirectories(rootDirectory.getPath() + "/src/main/resources"));
                testSourcesDirectories = Collections.singletonList(VfsUtil.createDirectories(rootDirectory.getPath() + "/src/test/java"));
                testResourceDirectories = Collections.singletonList(VfsUtil.createDirectories(rootDirectory.getPath() + "/src/test/resources"));

                PsiFile pomPsi = null;

                String text = null;
                if (type == PlatformType.BUKKIT || type == PlatformType.SPIGOT || type == PlatformType.PAPER) {
                    text = BukkitTemplate.applyPomTemplate(module, buildVersion);
                } else if (type == PlatformType.BUNGEECORD) {
                    text = BungeeCordTemplate.applyPomTemplate(module, buildVersion);
                } else if (type == PlatformType.SPONGE) {
                    text = SpongeTemplate.applyPomTemplate(module, buildVersion);
                }

                if (text != null) {
                    pomPsi = PsiFileFactory.getInstance(module.getProject()).createFileFromText(XMLLanguage.INSTANCE, text);
                }

                if (pomPsi != null) {
                    pomPsi.setName("pom.xml");

                    final XmlFile pomXmlPsi = (XmlFile) pomPsi;
                    final PsiFile finalPomPsi = pomPsi;
                    new WriteCommandAction.Simple(module.getProject(), pomPsi) {
                        @Override
                        protected void run() throws Throwable {
                            XmlTag root = pomXmlPsi.getRootTag();

                            DomManager manager = DomManager.getDomManager(module.getProject());
                            MavenProjectXml mavenProjectXml = manager.getFileElement(pomXmlPsi, MavenProjectXml.class, "project").getRootElement();

                            mavenProjectXml.getGroupId().setValue(groupId);
                            mavenProjectXml.getArtifactId().setValue(artifactId);
                            mavenProjectXml.getVersion().setValue(version);
                            mavenProjectXml.getName().setValue(pluginName);

                            if (root == null) {
                                return;
                            }
                            XmlTag properties = root.findFirstSubTag("properties");
                            if (properties == null) {
                                return;
                            }

                            if (!Strings.isNullOrEmpty(configuration.website)) {
                                XmlTag url = root.createChildTag("url", null, configuration.website, false);
                                root.addAfter(url, properties);
                            }

                            if (!Strings.isNullOrEmpty(configuration.description)) {
                                XmlTag description = root.createChildTag("description", null, configuration.description, false);
                                root.addBefore(description, properties);
                            }

                            for (BuildRepository buildRepository : repositories) {
                                Repository repository = mavenProjectXml.getRepositories().addRepository();
                                repository.getId().setValue(buildRepository.getId());
                                repository.getUrl().setValue(buildRepository.getUrl());
                            }

                            for (BuildDependency buildDependency : dependencies) {
                                Dependency dependency = mavenProjectXml.getDependencies().addDependency();
                                dependency.getGroupId().setValue(buildDependency.getGroupId());
                                dependency.getArtifactId().setValue(buildDependency.getArtifactId());
                                dependency.getVersion().setValue(buildDependency.getVersion());
                                dependency.getScope().setValue(buildDependency.getScope());
                            }

                            PsiDirectory rootDirectoryPsi = PsiManager.getInstance(module.getProject()).findDirectory(rootDirectory);
                            if (rootDirectoryPsi != null) {
                                rootDirectoryPsi.add(finalPomPsi);
                            }

                            pomFile = rootDirectory.findChild("pom.xml");
                            // Reformat the code to match their code style
                            PsiFile pomFilePsi = PsiManager.getInstance(module.getProject()).findFile(pomFile);
                            if (pomFilePsi != null) {
                                new ReformatCodeProcessor(pomFilePsi, false).run();
                            }
                        }
                    }.execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void finishSetup(@NotNull Module module, @NotNull PlatformType type, @NotNull ProjectConfiguration configuration) {
        Project project = module.getProject();

        // Force Maven to setup the project
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        manager.addManagedFilesOrUnignore(Collections.singletonList(pomFile));
        manager.getImportingSettings().setDownloadDocsAutomatically(true);
        manager.getImportingSettings().setDownloadSourcesAutomatically(true);

        // Setup the default Maven run config
        if (getRootDirectory().getCanonicalPath() != null) {
            MavenRunnerParameters params = new MavenRunnerParameters();
            params.setWorkingDirPath(getRootDirectory().getCanonicalPath());
            params.setGoals(Arrays.asList("clean", "package"));
            RunnerAndConfigurationSettings runnerSettings = MavenRunConfigurationType.createRunnerAndConfigurationSettings(null, null, params, module.getProject());
            runnerSettings.setName(module.getName() + " build");
            RunManager.getInstance(project).addConfiguration(runnerSettings, false);
            RunManager.getInstance(project).setSelectedConfiguration(runnerSettings);
        }
    }

    @Override
    public void reImport(@NotNull Module module, @NotNull PlatformType type) {
        rootDirectory = ModuleRootManager.getInstance(module).getContentRoots()[0];
        List<MavenProject> mavenProjects = MavenProjectsManager.getInstance(module.getProject()).getProjects();

        mavenProjects.stream()
                .filter(p -> p.getFile().getParent().equals(rootDirectory))
                .findFirst()
                .ifPresent(p -> {

                    artifactId = p.getMavenId().getArtifactId();
                    groupId = p.getMavenId().getGroupId();
                    version = p.getMavenId().getVersion();

                    dependencies = p.getDependencies().stream()
                            .map(d -> new BuildDependency(d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getScope()))
                            .collect(Collectors.toList());
                    repositories = p.getRemoteRepositories().stream()
                            .map(r -> new BuildRepository(r.getId(), r.getUrl()))
                            .collect(Collectors.toList());

                    pomFile = p.getFile();
                    sourceDirectories = p.getSources().stream()
                            .map(LocalFileSystem.getInstance()::findFileByPath)
                            .collect(Collectors.toList());

                    resourceDirectories = p.getResources().stream()
                            .map(MavenResource::getDirectory)
                            .map(LocalFileSystem.getInstance()::findFileByPath)
                            .collect(Collectors.toList());

                    testSourcesDirectories = p.getTestSources().stream()
                            .map(LocalFileSystem.getInstance()::findFileByPath)
                            .collect(Collectors.toList());

                    testResourceDirectories = p.getTestResources().stream()
                            .map(MavenResource::getDirectory)
                            .map(LocalFileSystem.getInstance()::findFileByPath)
                            .collect(Collectors.toList());

                    // Set author and plugin name, if set
                    ApplicationManager.getApplication().runReadAction(() -> {
                        PsiFile psiPomFile = PsiManager.getInstance(module.getProject()).findFile(pomFile);
                        if (psiPomFile instanceof XmlFile) {
                            XmlTag rootTag = ((XmlFile) psiPomFile).getRootTag();

                            if (rootTag != null) {
                                XmlTag nameTag = rootTag.findFirstSubTag("name");

                                if (nameTag != null) {
                                    pluginName = nameTag.getValue().getText();
                                }

                                // this is just awful...
                                // anyways, find the build version somewhere in there
                                XmlTag buildTag = rootTag.findFirstSubTag("build");
                                if (buildTag != null) {
                                    XmlTag pluginsTag = buildTag.findFirstSubTag("plugins");
                                    if (pluginsTag != null) {
                                        XmlTag[] pluginTags = pluginsTag.findSubTags("plugin");
                                        for (XmlTag pluginTag : pluginTags) {
                                            XmlTag artifactIdTag = pluginTag.findFirstSubTag("artifactId");
                                            if (artifactIdTag != null) {
                                                if (artifactIdTag.getValue().getText().equals("maven-compiler-plugin")) {
                                                    XmlTag configuration = pluginTag.findFirstSubTag("configuration");
                                                    if (configuration != null) {
                                                        XmlTag sourceTag = configuration.findFirstSubTag("source");
                                                        if (sourceTag != null) {
                                                            buildVersion = sourceTag.getValue().getText();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
    }
}
