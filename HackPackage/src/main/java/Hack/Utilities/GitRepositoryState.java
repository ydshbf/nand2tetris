package Hack.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

class GitRepositoryState {
    private static GitRepositoryState gitRepositoryState;
    private final String describeShort;
    private final String buildVersion;

    static GitRepositoryState getGitRepositoryState() throws IOException {
        if (gitRepositoryState == null) {
            final URL gitProperties = GitRepositoryState.class.getClassLoader().getResource("git.properties");
            final Properties properties = new Properties();
            if (gitProperties != null) {
                try (InputStream is = gitProperties.openStream()) {
                    properties.load(new InputStreamReader(is, Charset.forName("UTF-8")));
                }
            } else {
                properties.setProperty("git.commit.id.describe-short", "未知");
                properties.setProperty("git.build.version", "未知");
            }

            gitRepositoryState = new GitRepositoryState(properties);
        }
        return gitRepositoryState;
    }

    private GitRepositoryState(Properties properties) {
        this.describeShort = String.valueOf(properties.get("git.commit.id.describe-short"));
        this.buildVersion = String.valueOf(properties.get("git.build.version"));
    }

    String getDescribeShort() {
        return describeShort;
    }

    String getBuildVersion() {
        return buildVersion;
    }
}
