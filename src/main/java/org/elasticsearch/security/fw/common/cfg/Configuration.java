package org.elasticsearch.security.fw.common.cfg;

import java.io.File;
import java.nio.file.Path;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.security.fw.plugin.SecurityFwPlugin;

public class Configuration {
	private Environment environment;
	private Settings settings;
	
	@Inject
	public Configuration(Environment env,Settings settings){
		this.environment = env;
		this.settings=settings;
	}
	
	public Environment getEnvironment() {
		return environment;
	}

	public Settings getSettings() {
		return settings;
	}
	
	public String getApiContext(){
		return "/_auth";
	}
	
	public String getSecurityIndicename(){
		return ".security-fw";
	}
	public String[] getReserveindicesnames(){
		return null;
	}
	public static Path getConfigInPluginDir(){
		return PathUtils
				.get(new File(SecurityFwPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())
						.getParent(), "config")
				.toAbsolutePath();
	}
	
	public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
	
	public String getUserFwApiUrl(){
		return null;//"http://127.0.0.1:9200"+getApiContext()+"/acl/authlist";
	}
	
	public int getUserFwCacheTime(){
		return 20;
	}
}
