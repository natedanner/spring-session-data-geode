package io.spring.gradle.convention

import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.bundling.Zip

/**
 * Aggregates asciidoc, javadoc, and deploying of the docs into a single plugin
 */
class DocsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager();

		pluginManager.apply(BasePlugin);
		pluginManager.apply("org.asciidoctor.jvm.convert");
		pluginManager.apply("org.asciidoctor.jvm.pdf");
		pluginManager.apply(AsciidoctorConventionPlugin);
		pluginManager.apply(DeployDocsPlugin);
		pluginManager.apply(JavadocApiPlugin);

		project.tasks.withType(AbstractAsciidoctorTask) { t ->
			project.configure(t) {
				sources {
					include "**/*.adoc"
					exclude '_*/**'
				}
			}
		}

		Task docsZip = project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'asciidoctor'
			group = 'Distribution'
			archiveBaseName = project.rootProject.name
			archiveClassifier = 'docs'
			description = "Builds -${classifier} archive containing all " +
				"Docs for deployment at docs.spring.io"

			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
			duplicatesStrategy 'exclude'
		}

		Task docs = project.tasks.create("docs") {
			group = 'Documentation'
			description 'An aggregator task to generate all the documentation'
			dependsOn docsZip
		}
		project.tasks.assemble.dependsOn docs
	}
}
