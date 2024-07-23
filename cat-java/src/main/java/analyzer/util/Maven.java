package analyzer.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import analyzer.Config;

public final class Maven {
    public static List<String> getDependencyJarPaths(String path) throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(path + "/pom.xml"));

        return model.getDependencies().stream().map(dependency -> getDependencyJarPath(dependency)).toList();
    }

    private static String getDependencyJarPath(Dependency dependency) {
        String groupPath = String.join("/", dependency.getGroupId().split("\\."));
        return Config.MAVEN_REPOSITORY_PATH + "/" + groupPath + "/" + dependency.getArtifactId() + "/"
                + dependency.getVersion() + "/" + dependency.getArtifactId() + "-" + dependency.getVersion() + ".jar";
    }
}
